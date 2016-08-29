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
    public float interestRate;

    public ForeignCurrencyItem(String symbol, String name, float interestRate) {
        this.name = name;
        this.symbol = symbol;
        this.interestRate = interestRate;
    }

    public String VNDToAntoher(String moneyOnVND) {
        Float f = Float.parseFloat(moneyOnVND);
        return String.valueOf(f / this.interestRate);
    }

    @Override
    public String toString() {
        return interestRate + " ₫";
    }
}
