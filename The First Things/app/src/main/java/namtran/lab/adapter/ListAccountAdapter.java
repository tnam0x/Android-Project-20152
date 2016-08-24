package namtran.lab.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import namtran.lab.entity.BankingItem;
import namtran.lab.entity.CurrencyParser;
import namtran.lab.revexpmanager.R;

/**
 * Created by namtr on 23/08/2016.
 */
public class ListAccountAdapter extends ArrayAdapter<BankingItem> {
    private LayoutInflater mInflater;
    private int mResId;
    private ArrayList<BankingItem> mListAccount;

    public ListAccountAdapter(Context context, int resource, ArrayList<BankingItem> data) {
        super(context, resource, data);
        this.mInflater = LayoutInflater.from(context);
        this.mResId = resource;
        this.mListAccount = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
        BankingItem item = mListAccount.get(position);
        holder.money.setText(holder.mParser.format(item.getMoney()));
        holder.name.setText(item.getName());
        holder.rate.setText(String.valueOf(item.getRate() + "%"));
        holder.date.setText(item.getDate());
        return convertView;
    }

    static class ViewHolder {
        CurrencyParser mParser;
        TextView name, money, rate, date;

        public ViewHolder() {
            mParser = new CurrencyParser();
        }
    }
}
