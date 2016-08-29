package com.namtran.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.namtran.exchange.ExchangeFragment;
import com.namtran.entity.CurrencyParser;
import com.namtran.entity.StatsItem;
import com.namtran.main.R;

/**
 * Created by namtr on 20/08/2016.
 * Adapter cho danh sách thu chi được thống kê.
 * Dùng trong: {@link ExchangeFragment}
 */
public class StatisticsListAdapter extends ArrayAdapter<StatsItem> {
    private LayoutInflater mInflater;
    private int mResId;
    private ArrayList<StatsItem> mListStats;

    public StatisticsListAdapter(Context context, int resource, ArrayList<StatsItem> data) {
        super(context, resource, data);
        this.mInflater = LayoutInflater.from(context);
        this.mResId = resource;
        this.mListStats = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(mResId, parent, false);
            holder.date = (TextView) convertView.findViewById(R.id.tv_date_stats);
            holder.costIn = (TextView) convertView.findViewById(R.id.tv_cost_in);
            holder.costOut = (TextView) convertView.findViewById(R.id.tv_cost_out);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        StatsItem item = mListStats.get(position);
        String date;
        String costIn = "Thu: " + holder.mParser.format(item.getCostIn());
        String costOut = "Chi: " + holder.mParser.format(item.getCostOut());
        switch (position) {
            case 0:
                date = "Hôm nay: " + item.getDate();
                break;
            case 1:
                date = "Tháng này: " + item.getDate();
                break;
            case 2:
                date = "Năm nay: " + item.getDate();
                break;
            default:
                date = "Thời gian: " + item.getDate();
                break;
        }
        holder.date.setText(date);
        holder.costIn.setText(costIn);
        holder.costOut.setText(costOut);
        return convertView;
    }

    static class ViewHolder {
        TextView date, costIn, costOut;
        CurrencyParser mParser;

        public ViewHolder() {
            mParser = new CurrencyParser();
        }
    }

}
