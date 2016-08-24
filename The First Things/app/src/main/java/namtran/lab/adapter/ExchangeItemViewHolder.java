package namtran.lab.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import namtran.lab.entity.CurrencyParser;
import namtran.lab.revexpmanager.R;

/**
 * Created by namtr on 22/08/2016.
 */
public class ExchangeItemViewHolder extends RecyclerView.ViewHolder {
    TextView type, cost, date;
    CurrencyParser mParser;

    public ExchangeItemViewHolder(View parent) {
        super(parent);
        type = (TextView) parent.findViewById(R.id.tv_type);
        cost = (TextView) parent.findViewById(R.id.tv_cost);
        date = (TextView) parent.findViewById(R.id.tv_note);
        mParser = new CurrencyParser(3);
    }

}
