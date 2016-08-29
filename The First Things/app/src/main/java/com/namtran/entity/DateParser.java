package com.namtran.entity;

/**
 * Created by namtr on 21/08/2016.
 * Tách thời gian thành ngày, tháng năm hoặc ngày/tháng
 * Dùng cho chức năng Thống kê
 */
public class DateParser {
    public static String parseMonth(String date) {
        return date == null ? "" : date.substring(3);
    }

    public static String parseYear(String date) {
        return date == null ? "" : date.substring(6);
    }

    public static String getDay(String date) {
        return date == null ? "" : date.substring(0, 2);
    }

    public static String getMonth(String date) {
        return date == null ? "" : date.substring(3, 5);
    }

    public static String getYear(String date) {
        return date == null ? "" : date.substring(6);
    }
}
