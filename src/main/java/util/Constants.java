package util;

/**
 * Created by LogiX on 2015-11-20.
 */
public class Constants {

    //User constants
    public static final String ID = "id";
    public static final String EMAIL = "email";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String PASSWORD = "password_hash";
    public static final String LEVEL = "user_level";
    public static final String PERSON_NUMBER = "person_number";
    public static final String GENDER = "gender";
    public static final String IMAGE = "image";
    public static final String ADDRESS = "address";
    public static final String PHONE_NUMBER = "phone";
    public static final String CLASS_ID = "class_id";
    public static final String REGISTER_DATE = "register_date";
    public static final String ACTIVE = "active";
    public static final String USER_LEVEL_NAME = "user_level_name";

    //Attendance constants
    public static final String ATTENDANCE_TIMESTAMP = "attendance_timestamp";
    public static final String ATTENDANCE_REASON = "reason";
    public static final String ATTENDANCE_TYPE_ID = "type_id";
    public static final String ATTENDANCE_USER_ID = "user_id";
    public static final String ATTENDANCE_TYPE_NAME = "attendance_type_name";
    public static final String ATTENDANCE_WARN_TOTAL = "total";

    //Invite constants
    public static final String INVITE_EMAIL = "email";
    public static final String INVITE_TOKEN = "token";
    public static final String INVITE_USER_LEVEL = "user_level";
    public static final String INVITE_EXPIRE_DATE = "expire_date";
    public static final String INVITE_CLASS = "user_class";

    //PasswordReset constants
    public static final String PASSWORD_RESET_USER_ID = "user_id";
    public static final String PASSWORD_RESET_TOKEN = "token";
    public static final String PASSWORD_RESET_EXPIRE_DATE = "expire_date";

    //Class constants
    public static final String CLASSES_NAME = "classes_name";
    public static final String CLASSES_START_DATE = "start_date";
    public static final String CLASSES_END_DATE = "end_date";

    //Level constants
    public static final int STUDENT = 3;
    public static final int TEACHER = 2;
    public static final int ADMIN = 1;

    //Gender constants
    public static final int MALE = 1;
    public static final int GIRL = 0;
}
