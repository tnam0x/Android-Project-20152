package namtran.lab.entity;

import android.util.Log;

import java.text.DecimalFormat;

/**
 * Created by namtr on 22/08/2016.
 */
public class ForeignCurrency {
    public String symbol;
    public String name;
    public float rate;

    public ForeignCurrency(String symbol, String name, float rate) {
        this.name = name;
        this.symbol = symbol;
        this.rate = rate;
    }

    public String VNDToAntoher(String moneyOnVND) {
        Float f = Float.parseFloat(moneyOnVND);
        return String.valueOf(f / this.rate);
    }

    /***
     * Trả về tỉ giá của loại tiền tệ hiện tại
     */
    @Override
    public String toString() {
        return rate + " ₫";
    }
}
