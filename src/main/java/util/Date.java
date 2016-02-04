package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by LogiX on 2016-01-08.
 */
public class Date {

    public static java.sql.Date getToday() {
        java.util.Date utilDate = new java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        return sqlDate;
    }
    public static java.sql.Date getYeterday() {
        java.util.Date utilDate = new java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime() - 24*60*60*1000);

        return sqlDate;
    }
}
