package com.springapp.mvc;

import cron.RemoveExpiredTokenTask;
import data.*;
import entities.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import util.*;

import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@Controller
public class ServletController {
    private UserDAO userDAO;
    private ClassDAO classDAO;
	private AttendanceDAO attendanceDAO;
    private MailHandler mailHandler;
    private String rootUser;
    private String rootPassword;

    public ServletController() {

        //Läs in konfigurationen från server-config.xml.
        ApplicationContext context =
                new ClassPathXmlApplicationContext("server-config.xml");

        ServerConfiguration conf = (ServerConfiguration) context.getBean("serverConfiguration");

        rootUser = conf.getRootUserName();
        rootPassword = conf.getRootUserPassword();

        this.userDAO = new DBconnector(conf.getDbURL(), conf.getDbUser(), conf.getDbPassword());
		this.attendanceDAO = new DBconnector(conf.getDbURL(), conf.getDbUser(), conf.getDbPassword());
        this.classDAO = new DBconnector(conf.getDbURL(), conf.getDbUser(), conf.getDbPassword());
        mailHandler = (MailHandler) context.getBean("mailHandler");
        new RemoveExpiredTokenTask();
    }

    @RequestMapping(value = "/reset_password/{token}", method = RequestMethod.GET)
    public String resetPasswordView(ModelMap model, HttpSession session, @PathVariable String token) {
        model.addAttribute("token", token);
        model.addAttribute("message", "");
        return "password_reset";
    }

    @RequestMapping(value = "/reset_password/{token}", method = RequestMethod.POST)
    public String resetPasswordPost(ModelMap model, HttpSession session,
                                    @PathVariable String token,
                                    @RequestParam("password1") String password1,
                                    @RequestParam("password1") String password2) {
        PasswordReset pwdReset = userDAO.fetchPasswordResetByToken(token);
        if (pwdReset != null) {
            User user = userDAO.fetchUserById(pwdReset.getUserId());
            if (user != null) {
                if (password1.equals(password2)) {
                    user.setPassword(BCrypt.hashpw(password1, BCrypt.gensalt()));
                    userDAO.updateUserPassword(user);
                    userDAO.deletePasswordResetById(pwdReset.getId());
                    return "redirect:/index";
                } else {
                    model.addAttribute("message", "Lösenordet överensstämmer ej.");
                }
            } else {
                model.addAttribute("message", "Användaren hittades ej. Kontakta en administratör.");
            }
        } else {
            model.addAttribute("message", "Token hittades ej.");
        }
        model.addAttribute("token", token);
        return "password_reset";
    }

    @RequestMapping(value = "/request_password", method = RequestMethod.GET)
    public String requestPasswordView(ModelMap model, HttpSession session) {
        model.addAttribute("message", "");
        return "password_request";
    }

    @RequestMapping(value = "/request_password", method = RequestMethod.POST)
    public String requestPasswordPost(HttpServletRequest request, ModelMap model, HttpSession session, @RequestParam("email") String email) {
        User user = userDAO.fetchUserByEmail(email);
        if (user == null) {
            model.addAttribute("message", "Du finns inte!");
        } else {
            PasswordReset pwdReset = new PasswordReset(user.getId());
            userDAO.deletePasswordResetsByUserId(user.getId());
            if(userDAO.createPasswordReset(pwdReset) == Event.PASSWORD_RESET_CREATE_SUCCESS) {
                //Skicka mail
                String resetLink = "http://" + request.getServerName() + ":" + request.getServerPort() + "/reset_password/" + pwdReset.getToken();
                HashMap<String, String> values = new HashMap<String, String>();
                values.put("pwdResetLink", resetLink);
                if (user.getFirstName() != null) {
                    values.put("firstName", user.getFirstName());
                } else {
                    values.put("firstName", "");
                }
                if (user.getLastName() != null) {
                    values.put("lastName", user.getLastName());
                } else {
                    values.put("lastName", "");
                }
                if (mailHandler.sendMail(user.getEmail(), "pwdReset", values)) {
                    model.addAttribute("message", "Ett mail har skickats till " + email + ".");
                } else {
                    model.addAttribute("message", "Kunde ej skicka mail. Kontakta en administratör.");
                }
            } else {
                model.addAttribute("message", "Kunde ej återställa lösenordet.");
            }
        }
        return "password_request";
    }

    @RequestMapping(value = "/dbadmin", method = RequestMethod.GET)
    public String rootView(ModelMap model, HttpSession session) {
        Date rootLogin = (Date) session.getAttribute("root");
        Date now = new Date();
        if (rootLogin != null && now.before(rootLogin)) {
            model.addAttribute("rootLoggedIn", true);
        } else {
            model.addAttribute("rootLoggedIn", false);
        }
        return "root";
    }

    @RequestMapping(value = "/dbadmin", method = RequestMethod.POST)
    public String rootPost(ModelMap model, HttpSession session, @RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("password2") String password2) {
        Date rootLogin = (Date) session.getAttribute("root");
        Date now = new Date();
        if (rootLogin != null && now.before(rootLogin)) {
            model.addAttribute("rootLoggedIn", true);
            if (email.equals("")) {
                model.addAttribute("dbadminMessage", "Ange e-mail för adminkonto..");
            } else if (!password.equals(password2)) {
                model.addAttribute("dbadminMessage", "Lösenorden överensstämmer ej.");
            } else if (userDAO.createTables(email, password)) {
                return "redirect:/index";
            } else {
                model.addAttribute("dbadminMessage", "Kunde ej skapa databasen");
            }
        } else {
            model.addAttribute("dbadminMessage", "Du är inte inloggad.");
        }
        return "root";
    }

    @RequestMapping(value = "/dbadminlogin", method = RequestMethod.POST)
    public String rootLogin(ModelMap model, HttpSession session, @RequestParam("login") String login, @RequestParam("password") String password) {
        if (!rootUser.equals("") && rootUser.equals(login) && rootPassword.equals(password)) {
            Date utilDate = new Date();
            utilDate.setTime(utilDate.getTime() + 10 * 60 * 1000);
            session.setAttribute("root", utilDate);
            return "redirect:/dbadmin";
        } else {
            model.addAttribute("dbadminMessage", "Felaktigt lösenord/användarnamn.");
        }
        return "root";
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(ModelMap model) {
        return "index";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String admin(ModelMap model, HttpSession session) {
        //TODO: Check user level...
        return "admin";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String root() {
        return "redirect:index";
    }

    @RequestMapping(value = "/terms", method = RequestMethod.GET)
    public String terms() { return "terms"; }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(ModelMap model, HttpSession session,
                        @RequestParam("email") String email,
                        @RequestParam("password") String password) {
        User user = userDAO.fetchUserByEmail(email);
        if (user == null) {
            model.addAttribute("message", "E-postadressen hittades inte");
            return "index";
        } else if (!user.getActive()) {
            model.addAttribute("message", "Du är inaktiv och kan inte logga in. Kontakta en administratör.");
            return "index";
        } else if (BCrypt.checkpw(password, user.getPassword())) {
            session.setAttribute("user", user);
            if (user.getLevel() == Constants.STUDENT) {
                return "redirect:/student";
            }
            else if (user.getLevel() == Constants.TEACHER){
                return "redirect:/teacher";
            }
            else if(user.getLevel() == Constants.ADMIN){
                return "redirect:/admin";
            }
            else {
                return "redirect:/index";
            }
        } else {
            model.addAttribute("message", "Felaktigt lösenord!");
            return "index";
        }
    }

    @RequestMapping(value = "/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:index";
    }

    @RequestMapping(value = "/teacher", method = RequestMethod.GET)
    public String teacher(ModelMap model, HttpSession session) {
        //TODO: Check user level...
        return "teacher";
    }

    @RequestMapping(value = "/student", method = RequestMethod.GET)
    public String student(ModelMap model, HttpSession session) {
        model.addAttribute("option_list", attendanceDAO.getAttendanceTypes());

        return "student";
    }

    @RequestMapping(value = "/register/{token}", method = RequestMethod.GET)
    public String getRegisterUser(ModelMap model, @PathVariable String token) {
        Invite invite = userDAO.fetchUserInviteByToken(token);
        if (invite == null) {
            return "index";
        }
        else {
            model.addAttribute("email", invite.getEmail());
            model.addAttribute("user_class", classDAO.fetchClassById(invite.getStudentClass()));
            model.addAttribute("token", token);
            return "register";
        }
    }

    @RequestMapping(value = "/registrationhandler", method = RequestMethod.POST)
    public String registerUser(ModelMap model,
                               @RequestParam("firstname") String firstname,
                               @RequestParam("lastname") String lastname,
                               @RequestParam("password") String password,
                               @RequestParam("confirmpassword") String confirmpass,
                               @RequestParam("address") String address,
                               @RequestParam("personnumber_first") String pn1,
                               @RequestParam("personnumber_last") String pn2,
                               @RequestParam("phonenumber") String phonenumber,
                               @RequestParam("token") String token){

        Invite invite = userDAO.fetchUserInviteByToken(token);
        model.addAttribute("email", invite.getEmail());
        model.addAttribute("user_class", classDAO.fetchClassById(invite.getStudentClass()));
        model.addAttribute("token", token);
        int error_count = 0;

        if (!password.equals(confirmpass)) {
            model.addAttribute("error_message", "Lösenorden matchar inte");
            model.addAttribute("password_error", "*");
            error_count++;
        }

        if (!Registration.verifyFirstname(firstname)) {
            model.addAttribute("firstname_error_message", "Du har fyllt i ett ogiltigt förnamn");
            model.addAttribute("firstname_error", "*");
            error_count++;
        }
        else {
            model.addAttribute("firstname", firstname);
        }
        if (!Registration.verifyLastname(lastname)) {
            model.addAttribute("lastname_error_message", "Du har fyllt i ett ogiltigt efternamn");
            model.addAttribute("lastname_error", "*");
            error_count++;
        }
        else {
            model.addAttribute("lastname", lastname);
        }
        if (!Registration.verifyBirthday(pn1 + "-" + pn2)) {
            model.addAttribute("pn_error_message", "Du har fyllt i ett ogiltigt personnummer");
            model.addAttribute("pn_error", "*");
            error_count++;
        }
        else {
            model.addAttribute("pn1", pn1);
            model.addAttribute("pn2", pn2);
        }
        if (!Registration.verifyPhoneNr(phonenumber)) {
            model.addAttribute("phonenumber_error_message", "Du har fyllt i ett ogiltigt telefonnummer");
            model.addAttribute("phonenumber_error", "*");
            error_count++;
        }
        else {
            model.addAttribute("phonenumber", phonenumber);
        }
        model.addAttribute("address", address);
        if (error_count > 0) {

            model.addAttribute("error_count", "Du har " + Integer.toString(error_count) + " fel i formuläret:");
            return "register";
        }
        else {
            //fetch the invite to get the correct user level...


            //if this should be null there is something going on, probably trying to register as another user level than assigned.
            if (invite == null) {
                return "index";
            }
            User user = new User();
            user.setFirstName(firstname);
            user.setLastName(lastname);
            user.setEmail(invite.getEmail());
            user.setPassword(password);
            user.setPersonNumber(pn1 + "-" + pn2);
            user.setAddress(address);
            user.setClassId(invite.getStudentClass());
            user.setPhoneNumber(phonenumber);
            user.setLevel(invite.getUserLevel());

            if (Integer.parseInt("" + pn2.charAt(2)) % 2 == 0) {
                user.setGender(Constants.GIRL);
            }
            else {
                user.setGender(Constants.MALE);
            }

            Event result = userDAO.createUser(user);

            if (result == Event.USERNAME_ALREADY_IN_USE) {
                model.addAttribute("error_message", "Användarnamnet används redan");
                return "register";
            }
            if (result == Event.USER_SOCIALID_ALREADY_EXISTS) {
                model.addAttribute("error_message", "Personnummret existerar redan");
                return "register";
            }
            if (result == Event.USER_CREATE_SUCCESS) {
                userDAO.deleteInviteByToken(invite.getToken());
            }
        }
        return "redirect:index";
    }

    @RequestMapping(value = "/attendance", method = RequestMethod.POST)
    @ResponseBody
    public String RegisterAttendance(ModelMap model, HttpSession session,
                                     @RequestParam("type_id") int typeId,
                                     @RequestParam("reason") String reason) {
        User user = (User) session.getAttribute("user");

        Attendance attendance = new Attendance();
        if (typeId == 1) {
            attendance.setTypeId(4);
        }
        else {
            attendance.setTypeId(typeId);
        }
        attendance.setReason(reason);

        Event result = attendanceDAO.createAttendance(user, attendance);

        if (result == Event.NO_SUCH_ATTENDANCE_TYPE_FOUND) {
            return "Do not mess with me!";
        }
        if (result == Event.REGISTER_ATTENDANCE_SUCCESS) {
            return "Din frånvaro är registrerad!";
        }
        if (result == Event.REGISTER_ATTENDANCE_FAILED) {
            return "Någonting gick fel med din registrering, vänligen försök igen senare!";
        }
        return "student";
    }

    @RequestMapping(value = "/admin/attendance", method = RequestMethod.POST)
    @ResponseBody
    public String AdminRegisterAttendance(ModelMap model, HttpSession session, @RequestParam("user_id") int userId,
                                                                               @RequestParam("date") String date,
                                                                                @RequestParam("type_id") int typeId,
                                                                                @RequestParam("reason") String reason) {
        Attendance attendance = new Attendance();
        attendance.setTypeId(typeId);
        attendance.setUserId(userId);
        attendance.setReason(reason);

        //If date is "" it means its the current day..
        if (date.equals("")) {
            attendance.setAttendanceTimestamp(util.Date.getToday());
            attendanceDAO.createAttendanceWithUserId(attendance);
        }
        else {
            attendance.setAttendanceTimestamp(Translator.stringToTimestamp(date));
            attendanceDAO.createAttendanceWithUserIdAndDate(attendance);
        }

        //If date is "" it means its the current day..
        if (date.equals("")) {
            return Translator.translateToJson(attendanceDAO.fetchAttendanceByToday());
        }
        else {
            return Translator.translateToJson(attendanceDAO.fetchAttendanceByDate(date));
        }
    }

    @RequestMapping(value = "/admin/attendance/{date}", method = RequestMethod.GET)
    @ResponseBody
    public String AdminGetAttendance(ModelMap model, HttpSession session, @PathVariable String date) {
        Attendance attendance = new Attendance();

        attendanceDAO.createAttendanceWithUserId(attendance);

        return Translator.translateToJson(attendanceDAO.fetchAttendanceByDate(date));
    }

    @RequestMapping(value = "/admin/invite", method = RequestMethod.GET)
    public String inviteUser(ModelMap model) {
        model.addAttribute("user_levels", userDAO.getUserLevels());
        model.addAttribute("user_classes", classDAO.fetchAllClasses());

        return "admin_invite";
    }

    @RequestMapping(value = "/admin/sendinvite", method = RequestMethod.POST)
    @ResponseBody
    public String sendInvite(HttpServletRequest request, HttpServletResponse response, ModelMap model, @RequestParam("email") String email,
                                                                         @RequestParam("user_level") int userLevel,
                                                                         @RequestParam("user_class") int userClass) {

        String token = TokenGenerator.generateToken();
        Invite invite = new Invite();
        if (userLevel == Constants.ADMIN || userLevel == Constants.TEACHER) {
            invite.setStudentClass(0);
        }
        else {
            invite.setStudentClass(userClass);
        }
        if (userClass == 0 && userLevel == Constants.STUDENT) {
            return "En studerande måste tillhöra en klass!";
        }
        if (!Registration.verifyEmailAddress(email)) {
            return "Fyll i en giltig mail-adress..";
        }

        invite.setEmail(email);
        invite.setUserLevel(userLevel);
        invite.setToken(token);

        Event event = userDAO.createUserInvite(invite);

        if (event == Event.USER_INVITE_SUCCESS) {
            String inviteLink = "http://" + request.getServerName() + ":" + request.getServerPort() + "/register/" + token;
            HashMap<String, String> values = new HashMap<String, String>();
            values.put("inviteLink", inviteLink);
            boolean sent = mailHandler.sendMail(email, "invite", values);
            if (sent) {
                return "Inbjudan är skickad!";
            } else {
                return "Det gick inte att skicka inbjudan via mail, men inbjudan skapades med token: "+token;
            }
        }
        else {
            return "Det gick inte att skapa inbjudan!";
        }
    }

    @RequestMapping(value = "/admin/classes", method = RequestMethod.GET)
    public String adminClasses(ModelMap model, HttpSession session) {
        model.addAttribute("classes", classDAO.fetchAllClasses());

        return "classes_admin";
    }

    @RequestMapping(value = "/admin/deleteclass", method = RequestMethod.POST)
    public String deleteClass(HttpServletRequest request, HttpServletResponse response, ModelMap model,
                              @RequestParam("class_id") int class_id,
                              @RequestParam("confirm_delete") int confirm) {
        Classes klass = classDAO.fetchClassById(class_id);
        if (klass != null) {
            if (confirm == 1) {
                classDAO.deleteClass(klass);
                return "redirect:/admin/classes";
            } else {
                model.addAttribute("klass", klass);
                model.addAttribute("students", userDAO.fetchAllUsersByClassId(class_id));
                return "classes_delete";
            }
        }
        return "redirect:/admin/classes";
    }

    @RequestMapping(value = "/admin/addclass", method = RequestMethod.POST)
    public String addClass(HttpServletRequest request, HttpServletResponse response, ModelMap model,
                              @RequestParam("name") String name,
                              @RequestParam("start_date") String startDate,
                              @RequestParam("end_date") String endDate) {
        Classes klass = new Classes();
        klass.setName(name);
        klass.setStartDate(Translator.stringToTimestamp(startDate));
        klass.setEndDate(Translator.stringToTimestamp(endDate));
        classDAO.createClass(klass);

        return "redirect:/admin/classes";
    }

    @RequestMapping(value = "/admin/classlist", method = RequestMethod.GET)
    public String classList(ModelMap model, HttpSession session) {
        model.addAttribute("user_classes", classDAO.fetchAllClasses());
        model.addAttribute("users", userDAO.fetchActiveUsersByUserLevel(Constants.STUDENT));
        model.addAttribute("attendance", attendanceDAO.fetchAttendanceByToday());
        model.addAttribute("attendance_types", attendanceDAO.getAttendanceTypes());
        model.addAttribute("option_list", attendanceDAO.getAttendanceTypes());

        return "class_list";
    }

    @RequestMapping(value = "/findClass", method = RequestMethod.POST)
    public String classListFinder(ModelMap model, @RequestParam("class_id") int id) {
        model.addAttribute("users", userDAO.fetchUsersByClassId(id));
        Classes current_class = classDAO.fetchClassById(id);
        model.addAttribute("all_classes", classDAO.fetchAllClasses());
        model.addAttribute("current_class", current_class);
        model.addAttribute("class_id", id);
        if (id == 0) {
            return "redirect:/admin/admin_students";
        }

        return "redirect:/admin/admin_students/{class_id}";
    }

    @RequestMapping(value = "/admin/admin_users", method = RequestMethod.GET)
    public String adminAdminUsers(ModelMap model) {
        model.addAttribute("admins", userDAO.fetchUsersByUserLevel(1));
        model.addAttribute("teachers", userDAO.fetchUsersByUserLevel(2));
        return "admin_users";
    }

    @RequestMapping(value = "/admin/admin_students/{class_id}", method = RequestMethod.GET)
    public String adminAdminUsers(ModelMap model, @PathVariable int class_id) {
        model.addAttribute("users", userDAO.fetchUsersByClassId(class_id));
        model.addAttribute("all_classes", classDAO.fetchAllClasses());
        Classes current_class = classDAO.fetchClassById(class_id);
        model.addAttribute("inactive_users", userDAO.fetchInactiveUsers(class_id));
        model.addAttribute("current_class", current_class);
        return "admin_students";
    }

    @RequestMapping(value = "/admin/admin_students/user/{id}", method = RequestMethod.GET)
    public String updateUser(ModelMap model, @PathVariable int id) {
        User user = userDAO.fetchUserById(id);
        model.addAttribute("user_edit", user);
        model.addAttribute("all_classes", classDAO.fetchAllClasses());
        Classes current_class = classDAO.fetchClassById(user.getClassId());
        model.addAttribute("current_class", current_class);
        model.addAttribute("current_active", user.getActive());
        return "update_user";
    }

    @RequestMapping(value = "/admin/update_user", method = RequestMethod.POST)
    public String updateUserAction(ModelMap model,
                                   @RequestParam("id") int id,
                                   @RequestParam("firstName") String firstname,
                                   @RequestParam("lastName") String lastname,
                                   @RequestParam("email") String email,
                                   @RequestParam("personNumber") String pn,
                                   @RequestParam("address") String address,
                                   @RequestParam("phoneNumber") String phonenumber,
                                   @RequestParam("class") int class_id,
                                   @RequestParam("active") boolean active)
    {

        User user = userDAO.fetchUserById(id);
        if (Registration.verifyFirstname(firstname))
            user.setFirstName(firstname);
        if (Registration.verifyLastname(lastname))
            user.setLastName(lastname);
        if (Registration.verifyEmailAddress(email))
            user.setEmail(email);
        if (Registration.verifyBirthday(pn))
            user.setPersonNumber(pn);
        user.setAddress(address);
        if (Registration.verifyPhoneNr(phonenumber))
            user.setPhoneNumber(phonenumber);
        if (class_id != 0)
            user.setClassId(class_id);
        user.setActive(active);
        userDAO.updateUser(user);
        model.addAttribute("class_id", class_id);
        if (user.getLevel() == 3) {
            return "redirect:/admin/admin_students/{class_id}";
        }
        return "redirect:/admin/admin_users";
    }
    @RequestMapping(value = "/admin/admin_students/delete/{id}", method = RequestMethod.GET)
    public String deleteUser(ModelMap model, @PathVariable int id) {
        User user = userDAO.fetchUserById(id);
        model.addAttribute("user_delete", user);
        return "delete_user";
    }

    @RequestMapping(value = "/admin/delete_user", method = RequestMethod.POST)
    public String deleteUserAction(ModelMap model, @RequestParam("id") int id)
    {
        User user = userDAO.fetchUserById(id);
        userDAO.deleteUser(user);
        if (user.getLevel() == 3) {
            model.addAttribute("class_id", user.getClassId());
            return "redirect:/admin/admin_students/{class_id}";
        }
        return "redirect:/admin/admin_users";
    }

    @RequestMapping(value = "/admin/admin_students", method = RequestMethod.GET)
    public String adminUsers(ModelMap model) {
        model.addAttribute("all_classes", classDAO.fetchAllClasses());
        //model.addAttribute("users", userDAO.fetchUsersByUserLevel(Constants.STUDENT));
        return "admin_students";
    }

    @RequestMapping(value = "/admin/statistics", method = RequestMethod.GET)
    public String statistics(ModelMap model) {
        model.addAttribute("users", userDAO.fetchUsersByUserLevel(Constants.STUDENT));
        model.addAttribute("attendance_year", attendanceDAO.fetchAttendanceByCurrentYear());
        model.addAttribute("user_classes", classDAO.fetchAllClasses());
        model.addAttribute("attendance", attendanceDAO.fetchAttendanceByToday());
        model.addAttribute("attendance_warn", attendanceDAO.fetchAttendanceByWarning());

        return "statistics";
    }

    @RequestMapping(value = "/admin/statisticsAlt", method = RequestMethod.GET)
    public String statisticsAlt(ModelMap model) {
        model.addAttribute("users", new ArrayList<User>());
        model.addAttribute("user_classes", classDAO.fetchAllClasses());
        model.addAttribute("class_id", 1);
        model.addAttribute("startDate", "");
        model.addAttribute("endDate", "");
        return "statisticsAlt";
    }

    @RequestMapping(value = "/admin/statisticsAlt", method = RequestMethod.POST)
    public String statisticsAltPost(ModelMap model,
                                   @RequestParam("class_id") int class_id,
                                   @RequestParam("start_date") String startDate,
                                   @RequestParam("end_date") String endDate) {
        Classes klass = classDAO.fetchClassById(class_id);
        ArrayList<User> students = userDAO.fetchAllUsersByClassId(class_id);
        ArrayList<int[]> attArrayList = new ArrayList<int[]>();
        //Fulhack för att få med sista dagens attendances
        java.sql.Date end = Translator.stringToTimestamp(endDate);
        end.setTime(end.getTime() + 24 * 60 * 60 * 1000);
        for (User student : students) {
            attArrayList.add(attendanceDAO.fetchAttendanceStatisticsByUserIDAndDate(student.getId(), Translator.stringToTimestamp(startDate), end));
        }
        model.addAttribute("students", students);
        model.addAttribute("attArrayList", attArrayList);
        model.addAttribute("user_classes", classDAO.fetchAllClasses());
        model.addAttribute("class_id", class_id);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("dlLink", "/admin/export/" + class_id + "/" + startDate + "/" + endDate + "/" + klass.getName() + "_" + startDate + "_-_" + endDate + ".xlsx");
        return "statisticsAlt";
    }

    @RequestMapping(value = "/admin/export/{class_id}/{start_date}/{end_date}/{filename}.{fileEnd}", method = RequestMethod.GET)
    public void statisticsExport(ModelMap model, HttpServletResponse response,
                                   @PathVariable("filename") String fileName,
                                 @PathVariable("fileEnd") String fileEnd,
                                   @PathVariable("class_id") int class_id,
                                   @PathVariable("start_date") String startDate,
                                   @PathVariable("end_date") String endDate) {
        ArrayList<User> students = userDAO.fetchAllUsersByClassId(class_id);
        ArrayList<int[]> attArrayList = new ArrayList<int[]>();
        //Fulhack för att få med sista dagens attendances
        java.sql.Date end = Translator.stringToTimestamp(endDate);
        end.setTime(end.getTime() + 24 * 60 * 60 * 1000);
        for (User student : students) {
            attArrayList.add(attendanceDAO.fetchAttendanceStatisticsByUserIDAndDate(student.getId(), Translator.stringToTimestamp(startDate), end));
        }
        model.addAttribute("students", students);
        model.addAttribute("attArrayList", attArrayList);
        model.addAttribute("user_classes", classDAO.fetchAllClasses());
        Workbook wb = ExcelHandler.exportToExcel(students, attArrayList);
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + "." + fileEnd);
        try {
            wb.write(response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOError writing file to output stream");
        }
    }

}