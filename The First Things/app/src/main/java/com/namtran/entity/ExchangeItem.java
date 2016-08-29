package com.namtran.entity;

/**
 * Created by namtr on 22/08/2016.
 * Class lưu dữ liệu của một khoản thu hoặc chi
 * Gồm có tiền, nhóm, ngày của giao dịch
 * Dùng cho chức năng Đổi tiền tệ
 */
public class ExchangeItem {
    private String cost;
    private String type;
    private String date;

    public ExchangeItem(String cost, String type, String date) {
        this.cost = cost;
        this.type = type;
        this.date = date;
    }

    public String getCost() {
        return cost;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }
}
