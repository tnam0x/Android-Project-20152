package com.namtran.entity;

/**
 * Created by namtr on 20/08/2016.
 * Lưu thông tin thu chi của thời gian hiện tại
 * Gồm ngày, tiền thu, tiền chi
 * Dùng cho chức năng Thống kê
 */
public class StatsItem {
    private String date;
    private String costIn;
    private String costOut;

    public StatsItem(String date, String costIn, String costOut) {
        this.date = date;
        this.costIn = costIn;
        this.costOut = costOut;
    }

    public String getDate() {
        return date;
    }

    public String getCostIn() {
        return costIn;
    }

    public String getCostOut() {
        return costOut;
    }
}
