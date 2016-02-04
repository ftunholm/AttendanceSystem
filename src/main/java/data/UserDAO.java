package data;

import entities.Invite;
import entities.PasswordReset;
import entities.User;
import entities.UserLevel;
import util.Event;

import java.util.ArrayList;

/**
 * Created by LogiX on 2015-11-19.
 */
public interface UserDAO {

    Event deleteUsersByClassId(int class_id);

    User fetchUserBySocialId(String social_id);

    User fetchUserById(int id);

    User fetchUserByEmail(String email);

    Event createUser(User user);

    ArrayList<User> fetchUsersByUserLevel(int userLevel);

    ArrayList<User> fetchActiveUsersByUserLevel(int userLevel);

    ArrayList<User> fetchUsersByClassId(int classId);

    ArrayList<User> fetchAllUsersByClassId(int classId);
    ArrayList<User> fetchInactiveUsers(int classId);

    Event deleteUser(User user);

    Event updateUser(User user);

    Event updateUserPassword(User user);

    ArrayList<UserLevel> getUserLevels();

    Event createUserInvite(Invite invite);

    Invite fetchUserInviteByToken(String token);

    ArrayList<Invite> fetchAllInvites();

    void deleteInviteByToken(String token);

    boolean createTables(String email, String password);

    Event createPasswordReset(PasswordReset pwdReset);

    Event deletePasswordResetById(int id);

    PasswordReset fetchPasswordResetByToken(String token);

    ArrayList<PasswordReset> fetchAllPasswordResets();

    Event deletePasswordResetsByUserId(int id);

}
