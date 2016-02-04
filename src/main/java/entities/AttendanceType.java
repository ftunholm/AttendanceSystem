package entities;

/**
 * Created by LogiX on 2015-12-17.
 */
public class AttendanceType {
    private int id;
    private String attendanceTypeName;


    public String getAttendanceTypeName() {
        return attendanceTypeName;
    }

    public void setAttendanceTypeName(String attendanceTypeName) {
        this.attendanceTypeName = attendanceTypeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
