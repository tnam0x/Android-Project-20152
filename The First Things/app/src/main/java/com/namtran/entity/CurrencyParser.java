package com.namtran.entity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by namtr on 23/08/2016.
 * Định dạng số bất kì thành định dạng tiền tệ của VN
 */
public class CurrencyParser {
    private DecimalFormat format;

    public CurrencyParser() {
        init();
        format.setMaximumFractionDigits(0);
    }

    public CurrencyParser(int fractionDigits) {
        init();
        format.setMaximumFractionDigits(fractionDigits);
    }

    private void init() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        format = new DecimalFormat("0 ₫", symbols);
        format.setGroupingUsed(true);
        format.setGroupingSize(3);
    }

    public String format(String value) {
        double money = Double.parseDouble(value);
        return format.format(money);
    }

    public String format(float value) {
        return format.format(value);
    }
}
