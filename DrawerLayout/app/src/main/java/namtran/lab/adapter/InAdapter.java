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
public class InAdapter extends ArrayAdapter<Item> {
    private LayoutInflater inflater;
    private ArrayList<Item> data;
    private int resId;
    private NumberFormat format = NumberFormat.getCurrencyInstance();

    public InAdapter(Context context, int resource, ArrayList<Item> data) {
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
            holder.cost_in = (TextView) convertView.findViewById(R.id.tv_cost_in);
            holder.type_in = (TextView) convertView.findViewById(R.id.tv_type_in);
            holder.note_in = (TextView) convertView.findViewById(R.id.tv_note_in);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Item item = data.get(position);
        String cost = "+" + format.format(Integer.parseInt(item.getCost()));
        holder.cost_in.setText(cost);
        holder.type_in.setText(item.getType());
        holder.note_in.setVisibility(View.GONE); // Đảm báo item nào có note thì ms cho hiển thị
        String note = item.getNote();
        if (!note.isEmpty()) {
            holder.note_in.setVisibility(View.VISIBLE);
            holder.note_in.setText(note);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView cost_in, type_in, note_in;
    }

}
