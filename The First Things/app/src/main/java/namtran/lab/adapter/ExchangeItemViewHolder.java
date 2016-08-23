package namtran.lab.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import namtran.lab.revexpmanager.R;

/**
 * Created by namtr on 22/08/2016.
 */
public class ExchangeItemViewHolder extends RecyclerView.ViewHolder {
    TextView type, cost, date;
    private DecimalFormat format;


    public ExchangeItemViewHolder(View parent) {
        super(parent);
        type = (TextView) parent.findViewById(R.id.tv_type);
        cost = (TextView) parent.findViewById(R.id.tv_cost);
        date = (TextView) parent.findViewById(R.id.tv_note);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        format = new DecimalFormat();
        format.setDecimalFormatSymbols(symbols);
        format.setMaximumFractionDigits(3);
    }

    String format(String value) {
        float money = Float.parseFloat(value);
        return format.format(money);
    }

}
