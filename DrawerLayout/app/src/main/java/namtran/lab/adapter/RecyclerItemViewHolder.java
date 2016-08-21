package namtran.lab.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.NumberFormat;

import namtran.lab.revexpmanager.R;

public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {
    TextView type, cost, note;
    private NumberFormat format;

    public RecyclerItemViewHolder(View parent) {
        super(parent);
        type = (TextView) parent.findViewById(R.id.tv_type);
        cost = (TextView) parent.findViewById(R.id.tv_cost);
        note = (TextView) parent.findViewById(R.id.tv_note);
        format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(0);
    }

    String format(String value) {
        int money = Integer.parseInt(value);
        return format.format(money);
    }

}
