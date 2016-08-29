package com.namtran.entity;

/**
 * Created by namtr on 22/08/2016.
 * Lưu thông tin của tiền tệ
 * Gồm kí hiệu, tên, tỉ giá của tiền tệ
 * Dùng cho chức năng đổi tiền tệ
 */
public class ForeignCurrencyItem {
    public String symbol;
    public String name;
    public float rate;

    public ForeignCurrencyItem(String symbol, String name, float rate) {
        this.name = name;
        this.symbol = symbol;
        this.rate = rate;
    }

    public String VNDToAntoher(String moneyOnVND) {
        Float f = Float.parseFloat(moneyOnVND);
        return String.valueOf(f / this.rate);
    }
}
