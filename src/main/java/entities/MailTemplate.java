package entities;

import java.util.HashMap;

/**
 * Created by robin on 2016-01-15.
 */
public class MailTemplate {
    private String name;
    private String message;
    private String sender;
    private String subject;

    public String renderMessage(HashMap<String, String> values) {
        String tmp = message;
        if (values != null) {
            for (String key : values.keySet()) {
                tmp = tmp.replaceAll("\\$\\{" + key + "\\}", values.get(key));
            }
        }
        return tmp;
    }

    public String renderSubject(HashMap<String, String> values) {
        String tmp = subject;
        if (values != null) {
            for (String key : values.keySet()) {
                tmp = tmp.replaceAll("\\$\\{" + key + "\\}", values.get(key));
            }
        }
        return tmp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
