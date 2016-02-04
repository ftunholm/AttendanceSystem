package data;

/**
 * Created by LogiX on 2015-11-19.
 */

import java.lang.reflect.Array;
import java.sql.Connection;
import entities.*;
import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.mindrot.jbcrypt.BCrypt;
import util.Constants;
import util.Date;
import util.Event;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DBconnector implements UserDAO, AttendanceDAO, ClassDAO {


    private DataSource dataSource;
    private String driver = "com.mysql.jdbc.Driver";

    //OBS! Dessa variabler konfigureras nu i server-config.xml.
    private String url;
    private String sqlPassword;
    private String username;

    /**
     * Konstruktor.
     *
     * @param url         URL till databasen. Inkludera databasnamn.
     * @param username    Användarnamn till databasen.
     * @param sqlPassword Lösenord till databasen.
     */
    public DBconnector(String url, String username, String sqlPassword) {

        System.out.println("Loading underlying JDBC driver.");
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Done.");

        this.url = url;
        this.sqlPassword = sqlPassword;
        this.username = username;
        dataSource = setupDataSource(url, username, sqlPassword);
    }


    /**
     * Sätter upp connection pooling för databasanslutningen.
     * @param connectURI
     * @param uName
     * @param pwd
     * @return
     */
    public static DataSource setupDataSource(String connectURI, String uName, String pwd) {

        ConnectionFactory connectionFactory =
                new DriverManagerConnectionFactory(connectURI,uName, pwd);

        PoolableConnectionFactory poolableConnectionFactory =
                new PoolableConnectionFactory(connectionFactory, null);

        ObjectPool<PoolableConnection> connectionPool =
                new GenericObjectPool<PoolableConnection>(poolableConnectionFactory);

        poolableConnectionFactory.setPool(connectionPool);

        PoolingDataSource<PoolableConnection> dataSource =
                new PoolingDataSource<PoolableConnection>(connectionPool);

        return dataSource;
    }

    /**
     * Skapar alla tabeller och initiala poster i databasen. Kommer att skriva över eventuellt befintlig databas.
     * Skapar ett adminkonto med givet login och lösenord.
     * Lägg till förändringar i databasen här.
     *
     * @param adminEmail    Login för admin.
     * @param adminPassword Lösenord för admin.
     * @return
     */
    public boolean createTables(String adminEmail, String adminPassword) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        /*Table structure for table attendance_types */
        String[] query = {
                "DROP TABLE IF EXISTS attendance_types;",
                "CREATE TABLE attendance_types (" +
                        "id INT(11) NOT NULL AUTO_INCREMENT," +
                        "attendance_type_name VARCHAR(32) DEFAULT NULL," +
                        "PRIMARY KEY (id)" +
                        ");",
                "INSERT INTO attendance_types (id, attendance_type_name) VALUES (1, 'Närvaro');",
                "INSERT INTO attendance_types (id, attendance_type_name) VALUES (2, 'Sjuk');",
                "INSERT INTO attendance_types (id, attendance_type_name) VALUES (3, 'VAB');",
                "INSERT INTO attendance_types (id, attendance_type_name) VALUES (4, 'Övrigt');",
                "INSERT INTO attendance_types (id, attendance_type_name) VALUES (5, 'Ogiltig');",

                "DROP TABLE IF EXISTS attendances;",

                "CREATE TABLE attendances (" +
                        "id INT(11) NOT NULL AUTO_INCREMENT," +
                        "type_id INT(11) DEFAULT NULL," +
                        "reason VARCHAR(256) DEFAULT NULL," +
                        "attendance_timestamp DATETIME DEFAULT NULL," +
                        "user_id INT(11) DEFAULT NULL," +
                        "PRIMARY KEY (id)" +
                        ");",

                "DROP TABLE IF EXISTS token;",

                "CREATE TABLE token (" +
                        "id INT(11) NOT NULL AUTO_INCREMENT," +
                        "email VARCHAR(256) DEFAULT NULL," +
                        "token VARCHAR(256) DEFAULT NULL," +
                        "user_level INT(11) DEFAULT NULL," +
                        "user_class VARCHAR(64) DEFAULT NULL," +
                        "expire_date DATETIME DEFAULT NULL," +
                        "PRIMARY KEY (id)" +
                        ");",

                "DROP TABLE IF EXISTS password_reset;",

                "CREATE TABLE password_reset (" +
                        "id INT(11) NOT NULL AUTO_INCREMENT," +
                        "user_id INT(11) DEFAULT NULL," +
                        "token VARCHAR(256) DEFAULT NULL," +
                        "expire_date DATETIME DEFAULT NULL," +
                        "PRIMARY KEY (id)" +
                        ");",

                "DROP TABLE IF EXISTS user_level;",

                "CREATE TABLE user_level (" +
                        "id INT(11) NOT NULL AUTO_INCREMENT," +
                        "user_level_name VARCHAR(256) DEFAULT NULL," +
                        "PRIMARY KEY (id)" +
                        ");",

                "INSERT INTO user_level (id, user_level_name) VALUES (1, 'Administratör');",
                "INSERT INTO user_level (id, user_level_name) VALUES (2, 'Lärare');",
                "INSERT INTO user_level (id, user_level_name) VALUES (3, 'Studerande');",

                "DROP TABLE IF EXISTS classes;",

                "CREATE TABLE classes (" +
                        "id INT(11) NOT NULL AUTO_INCREMENT," +
                        "classes_name VARCHAR(64) DEFAULT NULL," +
                        "start_date DATE DEFAULT NULL," +
                        "end_date DATE DEFAULT NULL," +
                        "PRIMARY KEY (id)" +
                        ");",

                "DROP TABLE IF EXISTS classes_courses;",

                "CREATE TABLE classes_courses (" +
                        "id INT(11) NOT NULL AUTO_INCREMENT," +
                        "class_id INT(11) DEFAULT NULL," +
                        "course_id INT(11) DEFAULT NULL," +
                        "PRIMARY KEY (id)" +
                        ");",

                "DROP TABLE IF EXISTS courses;",

                "CREATE TABLE courses (" +
                        "id INT(11) NOT NULL AUTO_INCREMENT," +
                        "courses_name VARCHAR(64) DEFAULT NULL," +
                        "start_date DATE DEFAULT NULL," +
                        "end_date DATE DEFAULT NULL," +
                        "PRIMARY KEY (id)" +
                        ");",

                "DROP TABLE IF EXISTS teachers;",

                "CREATE TABLE teachers (" +
                        "id INT(11) NOT NULL AUTO_INCREMENT," +
                        "user_id INT(11) DEFAULT NULL," +
                        "course_id INT(11) DEFAULT NULL," +
                        "PRIMARY KEY (id)" +
                        ");",

                "DROP TABLE IF EXISTS users;",

                "CREATE TABLE users (" +
                        "id INT(11) NOT NULL AUTO_INCREMENT," +
                        "first_name VARCHAR(64) DEFAULT NULL," +
                        "last_name VARCHAR(64) DEFAULT NULL," +
                        "password_hash VARCHAR(64) DEFAULT NULL," +
                        "email VARCHAR(254) DEFAULT NULL," +
                        "user_level INT(11) DEFAULT NULL," +
                        "person_number VARCHAR(16) DEFAULT NULL," +
                        "gender VARCHAR(8) DEFAULT NULL," +
                        "image MEDIUMTEXT," +
                        "address VARCHAR(128) DEFAULT NULL," +
                        "phone VARCHAR(32) DEFAULT NULL," +
                        "class_id INT(11) DEFAULT NULL," +
                        "register_date DATE DEFAULT NULL," +
                        "active BOOLEAN DEFAULT 1," +
                        "PRIMARY KEY (id)" +
                        ");"};

        /*Data for the table users */

        try {


            connection = dataSource.getConnection();
            for (String q : query) {
                preparedStatement = connection.prepareStatement(q);
                preparedStatement.executeUpdate();
            }
            preparedStatement = connection.prepareStatement("INSERT INTO users (email, password_hash, user_level) values (?, ?, 1);");
            preparedStatement.setString(1, adminEmail);
            preparedStatement.setString(2, BCrypt.hashpw(adminPassword, BCrypt.gensalt()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            //log
            return false;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    @Override
    public User fetchUserById(int id) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;


        String query = "SELECT * FROM users WHERE id = (?)";
        User user = null;
        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                //user will be null
                return user;
            } else {
                user = new User();
                user.setId(resultSet.getInt(Constants.ID));
                user.setEmail(resultSet.getString(Constants.EMAIL));
                user.setFirstName(resultSet.getString(Constants.FIRST_NAME));
                user.setLastName(resultSet.getString(Constants.LAST_NAME));
                user.setPassword(resultSet.getString(Constants.PASSWORD));
                user.setLevel(resultSet.getInt(Constants.LEVEL));
                user.setPersonNumber(resultSet.getString(Constants.PERSON_NUMBER));
                user.setGender(resultSet.getInt(Constants.GENDER));
                user.setImage(resultSet.getString(Constants.IMAGE));
                user.setAddress(resultSet.getString(Constants.ADDRESS));
                user.setPhoneNumber(resultSet.getString(Constants.PHONE_NUMBER));
                user.setClassId(resultSet.getInt(Constants.CLASS_ID));
                user.setRegisterDate(resultSet.getDate(Constants.REGISTER_DATE));
                user.setActive(resultSet.getBoolean(Constants.ACTIVE));
            }

        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return user;
    }

    @Override
    public ArrayList<User> fetchAllUsersByClassId(int classId) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "SELECT * FROM users WHERE class_id = (?)";
        ArrayList<User> users = new ArrayList<User>();
        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, classId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                users.add(new User(
                        resultSet.getInt(Constants.ID),
                        resultSet.getString(Constants.FIRST_NAME),
                        resultSet.getString(Constants.LAST_NAME),
                        resultSet.getString(Constants.EMAIL),
                        resultSet.getString(Constants.PASSWORD),
                        resultSet.getInt(Constants.LEVEL),
                        resultSet.getString(Constants.PERSON_NUMBER),
                        resultSet.getInt(Constants.GENDER),
                        resultSet.getString(Constants.IMAGE),
                        resultSet.getString(Constants.ADDRESS),
                        resultSet.getString(Constants.PHONE_NUMBER),
                        resultSet.getInt(Constants.CLASS_ID),
                        resultSet.getDate(Constants.REGISTER_DATE),
                        resultSet.getBoolean(Constants.ACTIVE)));
            }

        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return users;
    }

    @Override
    public User fetchUserByEmail(String email) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "SELECT * FROM users WHERE email = (?)";
        User user = null;

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);

            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                //user will be null
                return user;
            } else {
                user = new User();
                user.setId(resultSet.getInt(Constants.ID));
                user.setEmail(resultSet.getString(Constants.EMAIL));
                user.setFirstName(resultSet.getString(Constants.FIRST_NAME));
                user.setLastName(resultSet.getString(Constants.LAST_NAME));
                user.setPassword(resultSet.getString(Constants.PASSWORD));
                user.setLevel(resultSet.getInt(Constants.LEVEL));
                user.setPersonNumber(resultSet.getString(Constants.PERSON_NUMBER));
                user.setGender(resultSet.getInt(Constants.GENDER));
                user.setImage(resultSet.getString(Constants.IMAGE));
                user.setAddress(resultSet.getString(Constants.ADDRESS));
                user.setPhoneNumber(resultSet.getString(Constants.PHONE_NUMBER));
                user.setClassId(resultSet.getInt(Constants.CLASS_ID));
                user.setRegisterDate(resultSet.getDate(Constants.REGISTER_DATE));
                user.setActive(resultSet.getBoolean(Constants.ACTIVE));
            }
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return user;
    }

    @Override
    public Event createUser(User user) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        if (fetchUserBySocialId(user.getPersonNumber()).getPersonNumber() != null) {
            return Event.USER_SOCIALID_ALREADY_EXISTS;
        } else {
            //Check if user already exists in database
            if (fetchUserByEmail(user.getEmail()) == null) {
                String pw_hash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

                String query = "INSERT INTO users (email, password_hash, first_name, last_name, user_level, person_number," +
                        " gender, image, address, phone, class_id, register_date) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

                try {


                    connection = dataSource.getConnection();

                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, user.getEmail());
                    preparedStatement.setString(2, pw_hash);
                    preparedStatement.setString(3, user.getFirstName());
                    preparedStatement.setString(4, user.getLastName());
                    preparedStatement.setInt(5, user.getLevel());
                    preparedStatement.setString(6, user.getPersonNumber());
                    preparedStatement.setInt(7, user.getGender());
                    preparedStatement.setString(8, user.getImage());
                    preparedStatement.setString(9, user.getAddress());
                    preparedStatement.setString(10, user.getPhoneNumber());
                    preparedStatement.setInt(11, user.getClassId());


                    int result = preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    //log
                } finally {
                    try {
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                return Event.USER_CREATE_SUCCESS;
            } else {
                return Event.USERNAME_ALREADY_IN_USE;
            }
        }
    }

    @Override
    public User fetchUserBySocialId(String social_id) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "SELECT * FROM users WHERE person_number = ?";
        ArrayList<User> userList = new ArrayList<User>();
        User user = new User();

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, social_id);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                user.setId(resultSet.getInt(Constants.ID));
                user.setEmail(resultSet.getString(Constants.EMAIL));
                user.setFirstName(resultSet.getString(Constants.FIRST_NAME));
                user.setLastName(resultSet.getString(Constants.LAST_NAME));
                user.setPassword(resultSet.getString(Constants.PASSWORD));
                user.setLevel(resultSet.getInt(Constants.LEVEL));
                user.setPersonNumber(resultSet.getString(Constants.PERSON_NUMBER));
                user.setGender(resultSet.getInt(Constants.GENDER));
                user.setImage(resultSet.getString(Constants.IMAGE));
                user.setAddress(resultSet.getString(Constants.ADDRESS));
                user.setPhoneNumber(resultSet.getString(Constants.PHONE_NUMBER));
                user.setClassId(resultSet.getInt(Constants.CLASS_ID));
                user.setRegisterDate(resultSet.getDate(Constants.REGISTER_DATE));
                userList.add(user);
            }
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }


    @Override
    public ArrayList<User> fetchUsersByClassId(int classId) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "SELECT * FROM users WHERE class_id = ? AND  active = 1 ORDER BY last_name ASC";
        ArrayList<User> userList = new ArrayList<User>();

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, classId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt(Constants.ID));
                user.setEmail(resultSet.getString(Constants.EMAIL));
                user.setFirstName(resultSet.getString(Constants.FIRST_NAME));
                user.setLastName(resultSet.getString(Constants.LAST_NAME));
                user.setPassword(resultSet.getString(Constants.PASSWORD));
                user.setLevel(resultSet.getInt(Constants.LEVEL));
                user.setPersonNumber(resultSet.getString(Constants.PERSON_NUMBER));
                user.setGender(resultSet.getInt(Constants.GENDER));
                user.setImage(resultSet.getString(Constants.IMAGE));
                user.setAddress(resultSet.getString(Constants.ADDRESS));
                user.setPhoneNumber(resultSet.getString(Constants.PHONE_NUMBER));
                user.setClassId(resultSet.getInt(Constants.CLASS_ID));
                user.setRegisterDate(resultSet.getDate(Constants.REGISTER_DATE));
                user.setActive(resultSet.getBoolean(Constants.ACTIVE));
                userList.add(user);
            }
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return userList;
    }

    @Override
    public ArrayList<User> fetchInactiveUsers(int classId) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "SELECT * FROM users WHERE class_id = ? AND  active = 0";
        ArrayList<User> userList = new ArrayList<User>();

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, classId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt(Constants.ID));
                user.setEmail(resultSet.getString(Constants.EMAIL));
                user.setFirstName(resultSet.getString(Constants.FIRST_NAME));
                user.setLastName(resultSet.getString(Constants.LAST_NAME));
                user.setPassword(resultSet.getString(Constants.PASSWORD));
                user.setLevel(resultSet.getInt(Constants.LEVEL));
                user.setPersonNumber(resultSet.getString(Constants.PERSON_NUMBER));
                user.setGender(resultSet.getInt(Constants.GENDER));
                user.setImage(resultSet.getString(Constants.IMAGE));
                user.setAddress(resultSet.getString(Constants.ADDRESS));
                user.setPhoneNumber(resultSet.getString(Constants.PHONE_NUMBER));
                user.setClassId(resultSet.getInt(Constants.CLASS_ID));
                user.setRegisterDate(resultSet.getDate(Constants.REGISTER_DATE));
                userList.add(user);
            }
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return userList;
    }

    @Override
    public ArrayList<User> fetchUsersByUserLevel(int userLevel) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "SELECT * FROM users WHERE user_level = (?)";
        ArrayList<User> userList = new ArrayList<User>();

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userLevel);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt(Constants.ID));
                user.setEmail(resultSet.getString(Constants.EMAIL));
                user.setFirstName(resultSet.getString(Constants.FIRST_NAME));
                user.setLastName(resultSet.getString(Constants.LAST_NAME));
                user.setPassword(resultSet.getString(Constants.PASSWORD));
                user.setLevel(resultSet.getInt(Constants.LEVEL));
                user.setPersonNumber(resultSet.getString(Constants.PERSON_NUMBER));
                user.setGender(resultSet.getInt(Constants.GENDER));
                user.setImage(resultSet.getString(Constants.IMAGE));
                user.setAddress(resultSet.getString(Constants.ADDRESS));
                user.setPhoneNumber(resultSet.getString(Constants.PHONE_NUMBER));
                user.setClassId(resultSet.getInt(Constants.CLASS_ID));
                user.setRegisterDate(resultSet.getDate(Constants.REGISTER_DATE));
                user.setActive(resultSet.getBoolean(Constants.ACTIVE));
                userList.add(user);
            }
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return userList;
    }

    @Override
    public ArrayList<User> fetchActiveUsersByUserLevel(int userLevel) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "SELECT * FROM users WHERE user_level = (?) AND active = 1";
        ArrayList<User> userList = new ArrayList<User>();

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userLevel);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt(Constants.ID));
                user.setEmail(resultSet.getString(Constants.EMAIL));
                user.setFirstName(resultSet.getString(Constants.FIRST_NAME));
                user.setLastName(resultSet.getString(Constants.LAST_NAME));
                user.setPassword(resultSet.getString(Constants.PASSWORD));
                user.setLevel(resultSet.getInt(Constants.LEVEL));
                user.setPersonNumber(resultSet.getString(Constants.PERSON_NUMBER));
                user.setGender(resultSet.getInt(Constants.GENDER));
                user.setImage(resultSet.getString(Constants.IMAGE));
                user.setAddress(resultSet.getString(Constants.ADDRESS));
                user.setPhoneNumber(resultSet.getString(Constants.PHONE_NUMBER));
                user.setClassId(resultSet.getInt(Constants.CLASS_ID));
                user.setRegisterDate(resultSet.getDate(Constants.REGISTER_DATE));
                userList.add(user);
            }
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return userList;
    }

    @Override
    public Event deleteUser(User user) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "DELETE FROM users WHERE id = ?;";

        //Försök radera attendances först
        if (deleteAllAttendanceByUserId(user.getId()) == Event.DELETED_ATTENDANCE_SUCCESS) {
            try {

                connection = dataSource.getConnection();
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, user.getId());
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                //log
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            return Event.USER_DELETE_FAILED;
        }
        return Event.USER_DELETE_SUCCESS;
    }

    @Override
    public Event updateUser(User user) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "UPDATE users SET first_name = ?, last_name = ?, email = ?, person_number = ?, address = ?, phone = ?, class_id = ?, active = ? WHERE id = ?";
        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPersonNumber());
            preparedStatement.setString(5, user.getAddress());
            preparedStatement.setString(6, user.getPhoneNumber());
            preparedStatement.setInt(7, user.getClassId());
            preparedStatement.setBoolean(8, user.getActive());
            preparedStatement.setInt(9, user.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return Event.USER_UPDATE_SUCCESS;
    }

    @Override
    public Event updateUserPassword(User user) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "UPDATE users SET password_hash = ? WHERE id = ?";
        Event result = Event.USER_UPDATE_FAILED;
        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user.getPassword());
            preparedStatement.setInt(2, user.getId());
            preparedStatement.executeUpdate();
            result = Event.USER_UPDATE_SUCCESS;
        } catch (SQLException e) {
            //log
        }  finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private boolean userLevelExists(int id) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        boolean result = false;
        String query = "SELECT * FROM user_level WHERE id = ?";

        try {

            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                result = true;
            }
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private boolean attendanceTypeExists(int typeId) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        boolean result = false;
        String query = "SELECT * FROM attendance_types WHERE id = ?";

        try {

            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, typeId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                result = true;
            }
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    @Override
    public ArrayList<AttendanceType> getAttendanceTypes() {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        ArrayList<AttendanceType> attendanceTypeList = new ArrayList<AttendanceType>();

        String query = "SELECT * FROM attendance_types";

        try {

            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(query);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                AttendanceType attendanceType = new AttendanceType();
                attendanceType.setId(resultSet.getInt(Constants.ID));
                attendanceType.setAttendanceTypeName(resultSet.getString(Constants.ATTENDANCE_TYPE_NAME));
                attendanceTypeList.add(attendanceType);
            }
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return attendanceTypeList;
    }

    private Attendance checkIfAttendanceExists(Attendance attendance, int userId) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        Attendance attendanceReturn = null;
        String query = "SELECT * FROM attendances WHERE user_id = " + userId + " AND DATE(attendance_timestamp) = DATE('" + attendance.getAttendanceTimestamp() + "')";
        java.sql.Statement statement = null;
        try {

            connection = dataSource.getConnection();

            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                attendanceReturn = new Attendance();
                attendanceReturn.setId(resultSet.getInt(Constants.ID));
                attendanceReturn.setAttendanceTimestamp(attendance.getAttendanceTimestamp());
                attendanceReturn.setUserId(attendance.getUserId());
                attendanceReturn.setTypeId(attendance.getTypeId());
                attendanceReturn.setReason(attendance.getReason());
                attendanceReturn.setTime(attendance.getTime());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return attendanceReturn;
    }

    @Override
    public Event createAttendance(User user, Attendance attendance) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        int result = 0;
        if (!attendanceTypeExists(attendance.getTypeId())) {
            return Event.NO_SUCH_ATTENDANCE_TYPE_FOUND;
        }
        attendance.setAttendanceTimestamp(Date.getToday());
        Attendance attendanceReturn = checkIfAttendanceExists(attendance, user.getId());

        if (attendanceReturn != null) {
            Event event = updateAttendanceWithTimeNow(attendanceReturn);
            if (event == Event.UPDATE_ATTENDANCE_SUCCESS) {
                return Event.REGISTER_ATTENDANCE_SUCCESS;
            } else {
                return Event.REGISTER_ATTENDANCE_FAILED;
            }
        }

        String query = "INSERT INTO attendances (attendance_timestamp, reason, type_id, user_id) VALUES(NOW(), ?, ?, ?)";

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, attendance.getReason());
            preparedStatement.setInt(2, attendance.getTypeId());
            preparedStatement.setInt(3, user.getId());
            result = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (result > 0) {
            return Event.REGISTER_ATTENDANCE_SUCCESS;
        } else {
            return Event.REGISTER_ATTENDANCE_FAILED;
        }
    }

    @Override
    public Event createAttendanceWithUserId(Attendance attendance) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        int result = 0;
        if (!attendanceTypeExists(attendance.getTypeId())) {
            return Event.NO_SUCH_ATTENDANCE_TYPE_FOUND;
        }
        Attendance attendanceReturn = checkIfAttendanceExists(attendance, attendance.getUserId());
        if (attendanceReturn != null) {
            Event event = updateAttendanceWithTimeNow(attendanceReturn);

            if (event == Event.UPDATE_ATTENDANCE_SUCCESS) {
                return Event.REGISTER_ATTENDANCE_SUCCESS;
            } else {
                return Event.REGISTER_ATTENDANCE_FAILED;
            }
        }

        String query = "INSERT INTO attendances (reason, type_id, user_id, attendance_timestamp) VALUES(?, ?, ?, NOW())";

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, attendance.getReason());
            preparedStatement.setInt(2, attendance.getTypeId());
            preparedStatement.setInt(3, attendance.getUserId());
            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (result > 0) {
            return Event.REGISTER_ATTENDANCE_SUCCESS;
        } else {
            return Event.REGISTER_ATTENDANCE_FAILED;
        }
    }

    @Override
    public Event createAttendanceWithUserIdAndDate(Attendance attendance) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        int result = 0;
        if (!attendanceTypeExists(attendance.getTypeId())) {
            return Event.NO_SUCH_ATTENDANCE_TYPE_FOUND;
        }
        Attendance attendanceReturn = checkIfAttendanceExists(attendance, attendance.getUserId());
        if (attendanceReturn != null) {
            Event event = updateAttendance(attendanceReturn);
            if (event == Event.UPDATE_ATTENDANCE_SUCCESS) {
                return Event.REGISTER_ATTENDANCE_SUCCESS;
            } else {
                return Event.REGISTER_ATTENDANCE_FAILED;
            }
        }

        String query = "INSERT INTO attendances (attendance_timestamp, reason, type_id, user_id) VALUES(?, ?, ?, ?)";

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDate(1, attendance.getAttendanceTimestamp());
            preparedStatement.setString(2, attendance.getReason());
            preparedStatement.setInt(3, attendance.getTypeId());
            preparedStatement.setInt(4, attendance.getUserId());
            result = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (result > 0) {
            return Event.REGISTER_ATTENDANCE_SUCCESS;
        } else {
            return Event.REGISTER_ATTENDANCE_FAILED;
        }
    }

    //Returns null if there is no attendance with input id
    @Override
    public Attendance fetchAttendanceById(int id) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "SELECT * FROM attendances WHERE id = (?)";
        Attendance attendance = null;

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                return attendance;
            } else {
                attendance = new Attendance();
                attendance.setId(resultSet.getInt(Constants.ID));
                attendance.setAttendanceTimestamp(resultSet.getDate(Constants.ATTENDANCE_TIMESTAMP));
                attendance.setReason(resultSet.getString(Constants.ATTENDANCE_REASON));
                attendance.setTypeId(resultSet.getInt(Constants.ATTENDANCE_TYPE_ID));
                attendance.setUserId(resultSet.getInt(Constants.ATTENDANCE_USER_ID));
            }
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return attendance;
    }
    @Override
    public ArrayList<Attendance> fetchAttendanceByWarning() {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "SELECT\n" +
                "\tuser_id,\n" +
                "    count(user_id) as total\n" +
                "FROM attendances \n" +
                "WHERE \n" +
                "\tattendance_timestamp > (DATE(NOW()) - INTERVAL 9 DAY) and \n" +
                "    type_id IN (2,3,4,5)\n" +
                "GROUP BY user_id;";

        ArrayList<Attendance> attendanceList = new ArrayList<Attendance>();

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Attendance attendance = new Attendance();
                attendance.setUserId(resultSet.getInt(Constants.ATTENDANCE_USER_ID));
                attendance.setTotal(resultSet.getInt(Constants.ATTENDANCE_WARN_TOTAL));


                attendanceList.add(attendance);
            }

        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return attendanceList;
    }

    @Override
    public ArrayList<Attendance> fetchAttendanceByUserId(int userId) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;

        String query = "SELECT * FROM attendances WHERE user_id = (?)";
        ArrayList<Attendance> attendanceList = new ArrayList<Attendance>();

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Attendance attendance = new Attendance();
                attendance.setId(resultSet.getInt(Constants.ID));
                attendance.setAttendanceTimestamp(resultSet.getDate(Constants.ATTENDANCE_TIMESTAMP));
                attendance.setReason(resultSet.getString(Constants.ATTENDANCE_REASON));
                attendance.setTypeId(resultSet.getInt(Constants.ATTENDANCE_TYPE_ID));
                attendance.setUserId(resultSet.getInt(Constants.ATTENDANCE_USER_ID));

                attendanceList.add(attendance);
            }
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return attendanceList;
    }

    @Override
    public Event updateAttendance(Attendance attendance) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "UPDATE attendances SET attendance_timestamp = ?, reason = ?, type_id = ? WHERE id = ?";
        int result = 0;
        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDate(1, attendance.getAttendanceTimestamp());
            preparedStatement.setString(2, attendance.getReason());
            preparedStatement.setInt(3, attendance.getTypeId());
            preparedStatement.setInt(4, attendance.getId());

            result = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (result > 0) {
            return Event.UPDATE_ATTENDANCE_SUCCESS;
        } else {
            return Event.UPDATE_ATTENDANCE_FAILED;
        }
    }

    private Event updateAttendanceWithTimeNow(Attendance attendance) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "UPDATE attendances SET attendance_timestamp = NOW(), reason = ?, type_id = ? WHERE id = ?";
        int result = 0;
        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, attendance.getReason());
            preparedStatement.setInt(2, attendance.getTypeId());
            preparedStatement.setInt(3, attendance.getId());

            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (result > 0) {
            return Event.UPDATE_ATTENDANCE_SUCCESS;
        } else {
            return Event.UPDATE_ATTENDANCE_FAILED;
        }

    }


    @Override
    public Event deleteAllAttendanceByUserId(int userId) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        Event result = Event.DELETED_ATTENDANCE_FAILED;
        String query = "DELETE FROM attendances WHERE user_id = ?;";

        try {

            connection = dataSource.getConnection();

            //Kolla att det inte finns en klass med samma namn.
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
            result = Event.DELETED_ATTENDANCE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public ArrayList<Attendance> fetchAttendanceByToday() {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "SELECT * FROM attendances WHERE DATE(attendance_timestamp) = CURDATE()";

        ArrayList<Attendance> attendanceList = new ArrayList<Attendance>();

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Attendance attendance = new Attendance();
                attendance.setId(resultSet.getInt(Constants.ID));
                attendance.setAttendanceTimestamp(resultSet.getDate(Constants.ATTENDANCE_TIMESTAMP));
                attendance.setTime(resultSet.getTimestamp(Constants.ATTENDANCE_TIMESTAMP));
                attendance.setReason(resultSet.getString(Constants.ATTENDANCE_REASON));
                attendance.setTypeId(resultSet.getInt(Constants.ATTENDANCE_TYPE_ID));
                attendance.setUserId(resultSet.getInt(Constants.ATTENDANCE_USER_ID));

                attendanceList.add(attendance);
            }
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return attendanceList;
    }

    @Override
    public ArrayList<Attendance> fetchAttendanceByDate(String date) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "SELECT * FROM attendances WHERE DATE(attendance_timestamp) = (?)";

        ArrayList<Attendance> attendanceList = new ArrayList<Attendance>();

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, date);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Attendance attendance = new Attendance();
                attendance.setId(resultSet.getInt(Constants.ID));
                attendance.setAttendanceTimestamp(resultSet.getDate(Constants.ATTENDANCE_TIMESTAMP));
                attendance.setTime(resultSet.getTimestamp(Constants.ATTENDANCE_TIMESTAMP));
                attendance.setReason(resultSet.getString(Constants.ATTENDANCE_REASON));
                attendance.setTypeId(resultSet.getInt(Constants.ATTENDANCE_TYPE_ID));
                attendance.setUserId(resultSet.getInt(Constants.ATTENDANCE_USER_ID));

                attendanceList.add(attendance);
            }
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return attendanceList;
    }

    @Override
    public boolean checkForAttendancesByDateAndUserIds(String date, String userIds) {
        //Fulhack! Måste finnas ett sätt att sätta en lista i preparedStatement.
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "SELECT id FROM attendances WHERE DATE(attendance_timestamp) = (?) AND user_id IN " + userIds + "  AND type_id = 1;";

        boolean result = false;

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, date);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    public ArrayList<Attendance> fetchAttendanceByCurrentYear() {
        String query = "SELECT * FROM attendances WHERE YEAR(attendance_timestamp) = YEAR(?)";
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;

        ArrayList<Attendance> attendanceList = new ArrayList<Attendance>();

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDate(1, Date.getToday());

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Attendance attendance = new Attendance();
                attendance.setId(resultSet.getInt(Constants.ID));
                attendance.setAttendanceTimestamp(resultSet.getDate(Constants.ATTENDANCE_TIMESTAMP));
                attendance.setTime(resultSet.getTimestamp(Constants.ATTENDANCE_TIMESTAMP));
                attendance.setReason(resultSet.getString(Constants.ATTENDANCE_REASON));
                attendance.setTypeId(resultSet.getInt(Constants.ATTENDANCE_TYPE_ID));
                attendance.setUserId(resultSet.getInt(Constants.ATTENDANCE_USER_ID));

                attendanceList.add(attendance);
            }
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return attendanceList;
    }

    @Override
    public ArrayList<UserLevel> getUserLevels() {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        ArrayList<UserLevel> userLevelsList = new ArrayList<UserLevel>();

        String query = "SELECT * FROM user_level ORDER BY user_level_name DESC";

        try {

            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(query);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                UserLevel userLevel = new UserLevel();
                userLevel.setId(resultSet.getInt(Constants.ID));
                userLevel.setName(resultSet.getString(Constants.USER_LEVEL_NAME));
                userLevelsList.add(userLevel);
            }
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return userLevelsList;
    }

    @Override
    public Event createUserInvite(Invite invite) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        int result = 0;
        if (!userLevelExists(invite.getUserLevel())) {
            return Event.NO_SUCH_USER_LEVEL_FOUND;
        }
        String query = "DELETE FROM token WHERE email = ?;";

        try {


            connection = dataSource.getConnection();

            //Radera eventuell gammal invite till samma mail.
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, invite.getEmail());
            preparedStatement.executeUpdate();

            //Kolla efter befintlig user med samma mail.
            if (fetchUserByEmail(invite.getEmail()) == null) {

                //Skapa ny invite.
                query = "INSERT INTO token (email, token, user_level, user_class, expire_date) VALUES(?, ?, ?, ?, NOW() + INTERVAL 30 DAY)";

                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, invite.getEmail());
                preparedStatement.setString(2, invite.getToken());
                preparedStatement.setInt(3, invite.getUserLevel());
                preparedStatement.setInt(4, invite.getStudentClass());
                result = preparedStatement.executeUpdate();
            } else {
                result = 2;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (result == 1) {
            return Event.USER_INVITE_SUCCESS;
        } else if (result == 2) {
            return Event.USERNAME_ALREADY_IN_USE;
        } else {
            return Event.USER_INVITE_FAILED;
        }
    }

    @Override
    public Invite fetchUserInviteByToken(String token) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "SELECT * FROM token WHERE token = (?)";
        Invite invite = null;

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, token);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                invite = new Invite();
                invite.setId(resultSet.getInt(Constants.ID));
                invite.setEmail(resultSet.getString(Constants.INVITE_EMAIL));
                invite.setToken(resultSet.getString(Constants.INVITE_TOKEN));
                invite.setUserLevel(resultSet.getInt(Constants.INVITE_USER_LEVEL));
                invite.setStudentClass(resultSet.getInt(Constants.INVITE_CLASS));
                invite.setExpireDate(resultSet.getDate(Constants.INVITE_EXPIRE_DATE));
            }
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return invite;
    }

    @Override
    public ArrayList<Invite> fetchAllInvites() {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        ArrayList<Invite> inviteList = new ArrayList<Invite>();

        String query = "SELECT * FROM token";

        try {

            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(query);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Invite invite = new Invite();
                invite.setId(resultSet.getInt(Constants.ID));
                invite.setEmail(resultSet.getString(Constants.INVITE_EMAIL));
                invite.setToken(resultSet.getString(Constants.INVITE_TOKEN));
                invite.setUserLevel(resultSet.getInt(Constants.INVITE_USER_LEVEL));
                invite.setStudentClass(resultSet.getInt(Constants.INVITE_CLASS));
                invite.setExpireDate(resultSet.getDate(Constants.INVITE_EXPIRE_DATE));
                inviteList.add(invite);
            }
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return inviteList;
    }

    @Override
    public void deleteInviteByToken(String token) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;

        String query = "DELETE FROM token WHERE token = (?)";

        try {

            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, token);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public ArrayList<Classes> fetchAllClasses() {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        ArrayList<Classes> classesList = new ArrayList<Classes>();

        String query = "SELECT * FROM classes";

        try {

            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(query);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Classes classes = new Classes();
                classes.setId(resultSet.getInt(Constants.ID));
                classes.setName(resultSet.getString(Constants.CLASSES_NAME));
                classes.setStartDate(resultSet.getDate(Constants.CLASSES_START_DATE));
                classes.setEndDate(resultSet.getDate(Constants.CLASSES_END_DATE));
                classesList.add(classes);
            }
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return classesList;
    }

    @Override
    public Classes fetchClassById(int id) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "SELECT * FROM classes WHERE id = (?)";
        Classes classes = null;

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                return classes;
            } else {
                classes = new Classes();
                classes.setId(resultSet.getInt(Constants.ID));
                classes.setName(resultSet.getString(Constants.CLASSES_NAME));
                classes.setStartDate(resultSet.getDate(Constants.CLASSES_START_DATE));
                classes.setEndDate(resultSet.getDate(Constants.CLASSES_END_DATE));
            }

        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return classes;
    }

    @Override
    public Event createClass(Classes klass) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "SELECT id FROM classes WHERE classes_name = ?;";

        try {


            connection = dataSource.getConnection();

            //Kolla att det inte finns en klass med samma namn.
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, klass.getName());
            resultSet = preparedStatement.executeQuery();

            //Skapa ny klass.
            if (!resultSet.next()) {
                query = "INSERT INTO classes (classes_name, start_date, end_date) VALUES (?, ?, ?);";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, klass.getName());
                preparedStatement.setDate(2, klass.getStartDate());
                preparedStatement.setDate(3, klass.getEndDate());
                preparedStatement.executeUpdate();
                }
            else {
                System.out.println("The class you're trying to create already exists!");
                return Event.CLASS_CREATE_FAILED;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return Event.CLASS_CREATE_SUCCESS;
    }

    @Override
    public Event deleteUsersByClassId(int class_id) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        Event result = Event.USER_DELETE_FAILED;
        String query = "SELECT id FROM users WHERE class_id = ?;";

        try {

            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, class_id);
            //Leta upp alla users som hör till klassen.
            resultSet = preparedStatement.executeQuery();
            boolean attendance_del_fail = false;
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                //Radera alla attendances tillhörande en user.
                if (deleteAllAttendanceByUserId(id) == Event.DELETED_ATTENDANCE_SUCCESS) {
                    query = "DELETE FROM users WHERE id = ?;";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, id);
                    preparedStatement.executeUpdate();
                } else {
                    attendance_del_fail = true;
                }
            }
            //Kolla om alla attendances och users har blivit raderade.
            if (!attendance_del_fail) {
                result = Event.USER_DELETE_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public Event deleteClass(Classes klass) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        Event result = Event.CLASS_DELETE_FAILED;

        String query = "DELETE FROM classes WHERE id = ?;";

        try {
            if (deleteUsersByClassId(klass.getId()) == Event.USER_DELETE_SUCCESS) {



                connection = dataSource.getConnection();

                //Kolla att det inte finns en klass med samma namn.
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, klass.getId());
                preparedStatement.executeUpdate();

                result = Event.CLASS_DELETE_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public Event createPasswordReset(PasswordReset pwdReset) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "INSERT INTO password_reset (user_id, token, expire_date) VALUES(?, ?, ?);";
        Event result = Event.PASSWORD_RESET_CREATE_FAILED;

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, pwdReset.getUserId());
            preparedStatement.setString(2, pwdReset.getToken());
            preparedStatement.setDate(3, pwdReset.getExpireDate());
            preparedStatement.executeUpdate();
            result = Event.PASSWORD_RESET_CREATE_SUCCESS;

        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public PasswordReset fetchPasswordResetByToken(String token) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        String query = "SELECT * FROM password_reset WHERE token = (?)";
        PasswordReset pwdReset = null;

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, token);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                pwdReset = new PasswordReset(
                        resultSet.getInt(Constants.ID),
                        resultSet.getInt(Constants.PASSWORD_RESET_USER_ID),
                        resultSet.getString(Constants.PASSWORD_RESET_TOKEN),
                        resultSet.getDate(Constants.PASSWORD_RESET_EXPIRE_DATE)
                );
            }
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return pwdReset;
    }

    @Override
    public Event deletePasswordResetById(int id) {
        PreparedStatement preparedStatement;
        Connection connection = null;
        String query = "DELETE FROM password_reset WHERE id = (?)";
        Event result = Event.PASSWORD_RESET_DELETE_FAILED;

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            result = Event.PASSWORD_RESET_DELETE_SUCCESS;
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    public Event deletePasswordResetsByUserId(int id) {
        PreparedStatement preparedStatement;
        Connection connection = null;
        String query = "DELETE FROM password_reset WHERE user_id = (?)";
        Event result = Event.PASSWORD_RESET_DELETE_FAILED;

        try {


            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            result = Event.PASSWORD_RESET_DELETE_SUCCESS;
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
    @Override
    public ArrayList<PasswordReset> fetchAllPasswordResets() {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;
        ArrayList<PasswordReset> resetList = new ArrayList<PasswordReset>();

        String query = "SELECT * FROM password_reset";

        try {

            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(query);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                resetList.add(new PasswordReset(
                        resultSet.getInt(Constants.ID),
                        resultSet.getInt(Constants.PASSWORD_RESET_USER_ID),
                        resultSet.getString(Constants.PASSWORD_RESET_TOKEN),
                        resultSet.getDate(Constants.PASSWORD_RESET_EXPIRE_DATE)
                ));
            }
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return resetList;
    }

    @Override
    public int[] fetchAttendanceStatisticsByUserIDAndDate(int id, java.sql.Date start, java.sql.Date end) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Connection connection = null;

        String query = "select type_id, COUNT(*) from attendances where user_id=? AND attendance_timestamp BETWEEN ? AND ? GROUP BY type_id;";

        int[] result = {0, 0, 0, 0, 0};
        try {

            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.setDate(2, start);
            preparedStatement.setDate(3, end);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                result[resultSet.getInt(Constants.ATTENDANCE_TYPE_ID) - 1] = resultSet.getInt("COUNT(*)");
            }
        } catch (SQLException e) {
            //log
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}