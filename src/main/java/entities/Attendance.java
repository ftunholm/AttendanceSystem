package entities;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by Gustaf on 2015-11-20.
 */
public class Attendance {
    private int id;
    private Date attendanceTimestamp;
    private String reason;
    private int typeId;
    private int userId;
    private int periodId;
    private Timestamp time;
    private int total;

    public Attendance() {

    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public int getPeriodId() {
        return periodId;
    }

    public void setPeriodId(int periodId) {
        this.periodId = periodId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getAttendanceTimestamp() {
        return attendanceTimestamp;
    }

    public void setAttendanceTimestamp(Date attendanceTimestamp) {
        this.attendanceTimestamp = attendanceTimestamp;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTotal() { return total; }

    public void setTotal(int total) { this.total = total; }



}
