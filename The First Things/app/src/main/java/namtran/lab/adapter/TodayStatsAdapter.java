package namtran.lab.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;

import namtran.lab.entity.StatsItem;
import namtran.lab.revexpmanager.R;

/**
 * Created by namtr on 20/08/2016.
 */
public class TodayStatsAdapter extends ArrayAdapter<StatsItem> {
    private LayoutInflater inflater;
    private int resId;
    private ArrayList<StatsItem> data;

    public TodayStatsAdapter(Context context, int resource, ArrayList<StatsItem> data) {
        super(context, resource, data);
        this.inflater = LayoutInflater.from(context);
        this.resId = resource;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(resId, parent, false);
            holder.date = (TextView) convertView.findViewById(R.id.tv_date_stats);
            holder.cost_in = (TextView) convertView.findViewById(R.id.tv_cost_in);
            holder.cost_out = (TextView) convertView.findViewById(R.id.tv_cost_out);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        StatsItem item = data.get(position);
        Log.d("Position", position + "");
        String date;
        String cost_in = "Thu: " + holder.format(item.getCostIn());
        String cost_out = "Chi: " + holder.format(item.getCostOut());
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
        Log.d("Date", date);
        Log.d("In", cost_in);
        Log.d("Out", cost_out);
        holder.date.setText(date);
        holder.cost_in.setText(cost_in);
        holder.cost_out.setText(cost_out);
        return convertView;
    }

    static class ViewHolder {
        TextView date, cost_in, cost_out;
        private DecimalFormat format;

        public ViewHolder() {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator(',');
            symbols.setDecimalSeparator('.');
            format = new DecimalFormat(",### ₫", symbols);
            format.setMaximumFractionDigits(0);
        }

        String format(String value) {
            int money = Integer.parseInt(value);
            return format.format(money);
        }
    }

}
