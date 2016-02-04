package util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LogiX on 2015-11-19.
 */

public class Translator {

    public static String translateToJson(Object obj) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        String result = gson.toJson(obj);

        return result;
    }

    public static Object translateFromJson(String json, Class<?> clazz) {
        Gson gson = new Gson();
        Object obj = gson.fromJson(json, clazz);
        return obj;
    }

    public static String[] translateFromJsonToArray(String json) {
        Gson gson = new Gson();
        String[] array = gson.fromJson(json, String[].class);
        return array;
    }
    public static java.sql.Date stringToTimestamp(String date) {
        java.sql.Date timestamp = null;
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date parsed = format.parse(date);
            timestamp = new java.sql.Date(parsed.getTime());
        }catch(Exception e) {
            //ignore this
        }
        return timestamp;
    }

}
