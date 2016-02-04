package cron;

import data.*;
import entities.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import util.Constants;
import util.Date;
import java.util.ArrayList;

/**
 * Created by LogiX on 2016-01-08.
 */
public class RemoveExpiredTokenTask {
    UserDAO userDAO;
    AttendanceDAO attendanceDAO;
    ClassDAO classDAO;

    public RemoveExpiredTokenTask() {
        ApplicationContext context = new ClassPathXmlApplicationContext("server-config.xml");
        ServerConfiguration conf = (ServerConfiguration) context.getBean("serverConfiguration");
        this.userDAO = new DBconnector(conf.getDbURL(), conf.getDbUser(), conf.getDbPassword());
        this.attendanceDAO = new DBconnector(conf.getDbURL(), conf.getDbUser(), conf.getDbPassword());
        classDAO = new DBconnector(conf.getDbURL(), conf.getDbUser(), conf.getDbPassword());
    }

    @Scheduled(cron="0 0 23 * * MON-FRI", zone="Europe/Stockholm")
    public void cronJob() {
        ArrayList<Invite> inviteList = userDAO.fetchAllInvites();
        //check every 24 hours if there is any invites that has expired, if so remove them
        for (Invite inv : inviteList) {
            if (inv.getExpireDate().before(Date.getToday())) {
                userDAO.deleteInviteByToken(inv.getToken());
            }
        }
        //Rensa lösenord-resets.
        ArrayList<PasswordReset> resetList = userDAO.fetchAllPasswordResets();
        //check every 24 hours if there is any invites that has expired, if so remove them
        for (PasswordReset pwdReset : resetList) {
            if (pwdReset.getExpireDate().before(Date.getToday())) {
                userDAO.deletePasswordResetById(pwdReset.getId());
            }
        }
    }

    @Scheduled(cron="0 59 23 * * MON-FRI", zone="Europe/Stockholm")
    public void cronJob2() {
        ArrayList<Classes> classes = classDAO.fetchAllClasses();
        ArrayList<Attendance> yesterdayAttendanceList = attendanceDAO.fetchAttendanceByDate(Date.getToday().toString());
        for (Classes klass : classes) {
            ArrayList<User> users = userDAO.fetchUsersByClassId(klass.getId());
            //Build a string with a list of user ids.
            String userIds = "(";
            for (User user : users) {
                if (user.getActive()) {
                    userIds += user.getId() + ", ";
                }
            }
            if (!userIds.equals("(")) {
                userIds = userIds.substring(0, userIds.length() - 2) + ")";
            } else {
                userIds += ")";
            }
            //Check if anyone in the class has attended today. If not, we assume there wasn't any classes today.
            if (attendanceDAO.checkForAttendancesByDateAndUserIds(Date.getToday().toString(), userIds)) {
                //All the students that were not registered the day before as sick or attending will be registered as not valid
                ArrayList<Integer> notRegistered = new ArrayList<Integer>();
                for (User user : users) {
                    boolean found = false;
                    for (Attendance attendance : yesterdayAttendanceList) {
                        if (user.getId() == attendance.getUserId()) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        notRegistered.add(user.getId());
                    }
                }
                for (Integer i : notRegistered) {
                    Attendance temp = new Attendance();
                    temp.setTypeId(5);
                    temp.setUserId(i);
                    temp.setAttendanceTimestamp(Date.getToday());
                    attendanceDAO.createAttendanceWithUserIdAndDate(temp);
                }
            }
        }
    }
}
