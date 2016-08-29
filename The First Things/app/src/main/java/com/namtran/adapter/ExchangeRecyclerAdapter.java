package com.namtran.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.namtran.entity.ExchangeItem;
import com.namtran.exchange.ExchangeFragment;
import com.namtran.main.R;

import java.util.ArrayList;

/**
 * Created by namtr on 22/08/2016.
 * Adapter cho danh sách thu chi khi qui đổi sang tiền tệ khác.
 * Dùng trong: {@link ExchangeFragment}
 */
public class ExchangeRecyclerAdapter extends RecyclerView.Adapter<ExchangeItemViewHolder> {
    private ArrayList<ExchangeItem> mListExchange;
    private boolean isReveneus;
    private final View.OnClickListener mOnClickListener;

    public ExchangeRecyclerAdapter(View.OnClickListener mOnClickListener, ArrayList<ExchangeItem> data, boolean isReveneus) {
        this.mOnClickListener = mOnClickListener;
        this.mListExchange = data;
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
        ExchangeItem item = mListExchange.get(position);
        setItemText(item, holder);
    }

    private void setItemText(ExchangeItem item, ExchangeItemViewHolder holder) {
        String money;
        if (isReveneus) {
            money = "+" + holder.mParser.format(item.getCost());
            holder.cost.setTextColor(Color.BLUE);
        } else {
            money = "-" + holder.mParser.format(item.getCost());
            holder.cost.setTextColor(Color.RED);
        }
        money = money.substring(0, money.length() - 2);
        holder.cost.setText(money);
        holder.type.setText(item.getType());
        holder.date.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return mListExchange == null ? 0 : mListExchange.size();
    }
}
