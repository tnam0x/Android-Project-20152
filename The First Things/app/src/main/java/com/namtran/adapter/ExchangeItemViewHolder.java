package com.namtran.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.namtran.entity.CurrencyParser;
import com.namtran.main.R;

/**
 * Created by namtr on 22/08/2016.
 * ViewHolder cho Adapter {@link ExchangeRecyclerAdapter}
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
