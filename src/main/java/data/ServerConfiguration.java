package data;

/**
 * Created by robin on 2016-01-07.
 */
public class ServerConfiguration {
    private String dbURL;
    private String dbUser;
    private String dbPassword;
    private String rootUserName;
    private String rootUserPassword;

    public String getRootUserName() {
        return rootUserName;
    }

    public void setRootUserName(String rootUserName) {
        this.rootUserName = rootUserName;
    }

    public void setDbURL(String dbURL) {
        this.dbURL = dbURL;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public void setRootUserPassword(String rootUserPassword) {
        this.rootUserPassword = rootUserPassword;
    }

    public String getDbURL() {
        return dbURL;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getRootUserPassword() {
        return rootUserPassword;
    }
}
