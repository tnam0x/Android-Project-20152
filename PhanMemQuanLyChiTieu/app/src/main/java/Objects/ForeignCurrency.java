package Objects;

import android.util.Log;

import java.text.DecimalFormat;

public class ForeignCurrency {
    public String symbol;
    public String name;
    public float rate;

    public ForeignCurrency(String symbol, String name, float rate) {
        this.name = name;
        this.symbol = symbol;
        this.rate = rate;
    }

    public String VNDToAntoher(String str) {
        Log.d("FCurrency", str);
        String cd;
        Float f = Float.parseFloat(str);
        float a = f / this.rate;
        DecimalFormat first = new DecimalFormat("#.###");
        String format_string = first.format(a);
        cd = "" + format_string + " (" + this.symbol + ")";
        Log.d("FCurrency", cd);
        return cd;
    }

    @Override
    public String toString() {
        return symbol + " - " + rate + " - " + name;
    }

}
