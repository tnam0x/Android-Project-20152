package com.namtran.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.namtran.entity.CurrencyParser;
import com.namtran.main.R;

public class TransactionsViewHolder extends RecyclerView.ViewHolder {
    TextView type, cost, note;
    CurrencyParser mParser;

    public TransactionsViewHolder(View parent) {
        super(parent);
        type = (TextView) parent.findViewById(R.id.tv_type);
        cost = (TextView) parent.findViewById(R.id.tv_cost);
        note = (TextView) parent.findViewById(R.id.tv_note);
        mParser = new CurrencyParser();
    }
}
