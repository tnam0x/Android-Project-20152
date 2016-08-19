package namtran.lab.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;

import namtran.lab.entity.Item;
import namtran.lab.revexpmanager.R;

/**
 * Created by namtr on 18/08/2016.
 */
public class OutAdapter extends ArrayAdapter<Item> {
    private LayoutInflater inflater;
    private ArrayList<Item> data;
    private int resId;
    private NumberFormat format = NumberFormat.getCurrencyInstance();

    public OutAdapter(Context context, int resource, ArrayList<Item> data) {
        super(context, resource, data);
        this.inflater = LayoutInflater.from(context);
        this.resId = resource;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(resId, parent, false);
            holder = new ViewHolder();
            holder.cost_out = (TextView) convertView.findViewById(R.id.tv_cost_out);
            holder.type_out = (TextView) convertView.findViewById(R.id.tv_type_out);
            holder.note_out = (TextView) convertView.findViewById(R.id.tv_note_out);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Item item = data.get(position);
        String cost = "-" + format.format(Integer.parseInt(item.getCost()));
        holder.cost_out.setText(cost);
        holder.type_out.setText(item.getType());
        holder.note_out.setVisibility(View.GONE); // Đảm báo item nào có note thì ms cho hiển thị
        String note = item.getNote();
        if (!note.isEmpty()) {
            holder.note_out.setVisibility(View.VISIBLE);
            holder.note_out.setText(note);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView cost_out, type_out, note_out;
    }
}
