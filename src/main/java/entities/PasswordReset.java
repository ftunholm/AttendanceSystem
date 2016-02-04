package entities;

import java.sql.Date;
import java.util.UUID;

/**
 * Created by LanfeaR on 2015-12-28.
 */
public class PasswordReset {

    private int id;
    private int userId;
    private String token;
    private Date expireDate;

    public PasswordReset(int id, int userId, String token, Date expireDate) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.expireDate = expireDate;
    }

    public PasswordReset(int userId) {
        this.userId = userId;
        this.token = UUID.randomUUID().toString();
        //Sätt expire till ett dygn från nu
        this.expireDate = new Date((System.currentTimeMillis() + 48 * 60 * 60 * 1000));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int user) {
        this.userId = user;
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
