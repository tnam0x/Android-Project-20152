package com.namtran.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.namtran.entity.BankItem;
import com.namtran.entity.CurrencyParser;
import com.namtran.main.R;

import java.util.ArrayList;

/**
 * Created by namtr on 23/08/2016.
 * Adapter cho danh sách các tài khoản ngân hàng.
 */
public class AccountListAdapter extends ArrayAdapter<BankItem> {
    private LayoutInflater mInflater;
    private int mResId;
    private ArrayList<BankItem> mListAccount;

    public AccountListAdapter(Context context, int resource, ArrayList<BankItem> data) {
        super(context, resource, data);
        this.mInflater = LayoutInflater.from(context);
        this.mResId = resource;
        this.mListAccount = data;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mResId, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.tv_name_bank);
            holder.money = (TextView) convertView.findViewById(R.id.tv_money_bank);
            holder.rate = (TextView) convertView.findViewById(R.id.tv_rate_bank);
            holder.date = (TextView) convertView.findViewById(R.id.tv_date_bank);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BankItem item = mListAccount.get(position);
        holder.money.setText(holder.mParser.format(item.getMoney()));
        holder.name.setText(item.getName());
        holder.rate.setText(String.valueOf(item.getRate() + "%"));
        holder.date.setText(item.getDate());
        return convertView;
    }

    private static class ViewHolder {
        CurrencyParser mParser;
        TextView name, money, rate, date;

        ViewHolder() {
            mParser = new CurrencyParser();
        }
    }
}
