package com.namtran.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.namtran.entity.CurrencyParser;
import com.namtran.entity.StatsItem;
import com.namtran.main.R;

/**
 * Created by namtr on 20/08/2016.
 */
public class TodayStatsAdapter extends ArrayAdapter<StatsItem> {
    private LayoutInflater mInflater;
    private int mResId;
    private ArrayList<StatsItem> mListStats;

    public TodayStatsAdapter(Context context, int resource, ArrayList<StatsItem> data) {
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
            holder.cost_in = (TextView) convertView.findViewById(R.id.tv_cost_in);
            holder.cost_out = (TextView) convertView.findViewById(R.id.tv_cost_out);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        StatsItem item = mListStats.get(position);
        Log.d("Position", position + "");
        String date;
        String cost_in = "Thu: " + holder.mParser.format(item.getCostIn());
        String cost_out = "Chi: " + holder.mParser.format(item.getCostOut());
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
        holder.cost_in.setText(cost_in);
        holder.cost_out.setText(cost_out);
        return convertView;
    }

    static class ViewHolder {
        TextView date, cost_in, cost_out;
        CurrencyParser mParser;

        public ViewHolder() {
            mParser = new CurrencyParser();
        }
    }

}
