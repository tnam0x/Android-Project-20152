package namtran.lab.entity;

/**
 * Created by namtr on 21/08/2016.
 */
public class DateParser {
    public static String parseMonth(String date) {
        return date == null ? "" : date.substring(3);
    }

    public static String parseYear(String date) {
        return date == null ? "" : date.substring(6);
    }
}
