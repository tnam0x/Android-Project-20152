package namtran.lab.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import namtran.lab.entity.ExchangeItem;
import namtran.lab.revexpmanager.R;

/**
 * Created by namtr on 22/08/2016.
 */
public class ExchangeRecyclerAdapter extends RecyclerView.Adapter<ExchangeItemViewHolder> {
    private ArrayList<ExchangeItem> data;
    private boolean isReveneus;
    private final View.OnClickListener mOnClickListener;

    public ExchangeRecyclerAdapter(View.OnClickListener mOnClickListener, ArrayList<ExchangeItem> data, boolean isReveneus) {
        this.mOnClickListener = mOnClickListener;
        this.data = data;
        this.isReveneus = isReveneus;
    }

    @Override
    public ExchangeItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_recycler, parent, false);
        view.setOnClickListener(mOnClickListener);
        return new ExchangeItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExchangeItemViewHolder holder, int position) {
        ExchangeItem item = data.get(position);
        setItemText(item, holder);
    }

    private void setItemText(ExchangeItem item, ExchangeItemViewHolder holder) {
        Log.d("Exchange Adapter", "updated");
        if (isReveneus) {
            String money = "+" + item.getCost();
            holder.cost.setText(money);
            holder.cost.setTextColor(Color.BLUE);
        } else {
            String money = "-" + item.getCost();
            holder.cost.setText(money);
            holder.cost.setTextColor(Color.RED);
        }
        holder.type.setText(item.getType());
        holder.date.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }
}
