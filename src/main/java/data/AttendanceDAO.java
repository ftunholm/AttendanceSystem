package data;

import entities.Attendance;
import entities.AttendanceType;
import entities.User;
import util.Event;

import java.util.ArrayList;

/**
 * Created by LogiX on 2015-12-14.
 */
public interface AttendanceDAO {

    Event createAttendance(User user, Attendance attendance);

    Event createAttendanceWithUserId(Attendance attendance);

    Event createAttendanceWithUserIdAndDate(Attendance attendance);

    ArrayList<AttendanceType> getAttendanceTypes();

    Attendance fetchAttendanceById(int id);

    ArrayList<Attendance> fetchAttendanceByUserId(int userId);

    Event updateAttendance(Attendance attendance);

    Event deleteAllAttendanceByUserId(int userId);

    ArrayList<Attendance> fetchAttendanceByToday();

    ArrayList<Attendance> fetchAttendanceByWarning();

    ArrayList<Attendance> fetchAttendanceByDate(String date);

    ArrayList<Attendance> fetchAttendanceByCurrentYear();

    int[] fetchAttendanceStatisticsByUserIDAndDate(int id, java.sql.Date start, java.sql.Date end);

    boolean checkForAttendancesByDateAndUserIds(String date, String userIds);
}
