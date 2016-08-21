package namtran.lab.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import namtran.lab.entity.Item;
import namtran.lab.revexpmanager.R;

/**
 * Created by namtr on 20/08/2016.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerItemViewHolder> {
    private ArrayList<Item> data;
    private boolean isReveneus;
    private final View.OnClickListener mOnClickListener;

    public RecyclerAdapter(ArrayList<Item> data, boolean isReveneus, View.OnClickListener mOnClickListener) {
        this.data = data;
        this.isReveneus = isReveneus;
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public RecyclerItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_transactions, parent, false);
        view.setOnClickListener(mOnClickListener);
        return new RecyclerItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerItemViewHolder holder, int position) {
        Item item = data.get(position);
        setItemText(item, holder);
    }

    public void setItemText(Item item, RecyclerItemViewHolder holder) {
        Log.d("Reveneus", isReveneus + "");
        if (isReveneus) {
            String money = "+" + holder.format(item.getCost());
            holder.cost.setText(money);
            holder.cost.setTextColor(Color.BLUE);
        } else {
            String money = "-" + holder.format(item.getCost());
            holder.cost.setText(money);
            holder.cost.setTextColor(Color.RED);
        }
        holder.type.setText(item.getType());
        holder.note.setVisibility(View.INVISIBLE); // Đảm báo item nào có note thì ms cho hiển thị
        String notes = item.getNote();
        if (!notes.isEmpty()) {
            holder.note.setVisibility(View.VISIBLE);
            holder.note.setText(notes);
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

}
