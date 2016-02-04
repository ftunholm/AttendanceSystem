package entities;

import util.Constants;
import util.TokenGenerator;

import java.sql.Date;

/**
 * Created by LanfeaR on 2015-12-28.
 */
public class Invite {

    private int id;
    private String email;
    private String token;
    private Date expireDate;
    private int userLevel;
    private int studentClass;

    public Invite(String email, String token, int userLevel, int studentClass) {
        this.email = email;
        this.token = token;
        this.expireDate = expireDate;
        this.userLevel = userLevel;
        this.studentClass = studentClass;
    }

    public Invite(String email, int studentClass) {
        this.email = email;
        this.token = TokenGenerator.generateToken();
        this.userLevel = Constants.STUDENT;
        this.studentClass = studentClass;
    }

    public Invite() {

    }

    public int getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(int studentClass) {
        this.studentClass = studentClass;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }
}
