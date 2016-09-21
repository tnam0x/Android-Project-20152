package com.namtran.exchange;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.namtran.adapter.ExchangeRecyclerAdapter;
import com.namtran.entity.CurrencyParser;
import com.namtran.entity.ExchangeItem;
import com.namtran.entity.TransactionsItem;
import com.namtran.main.R;

import java.util.ArrayList;

/**
 * Created by namtr on 22/08/2016.
 * Danh sách thu khi đổi tiền tệ.
 */
public class RevExchangeFragment extends Fragment {
    private static ArrayList<ExchangeItem> mListExchange;
    private static ExchangeRecyclerAdapter mAdapter;
    private ArrayList<TransactionsItem> mListTrans;

    public static void update(ArrayList<ExchangeItem> data) {
        mListExchange.clear();
        mListExchange.addAll(data);
        mAdapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_list_item, container, false);
        mListExchange = new ArrayList<>();
        mListTrans = ExchangeFragment.mListTransIn;
        setupRecyclerView(recyclerView);
        return recyclerView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ExchangeRecyclerAdapter(new RecyclerListener(recyclerView), mListExchange, true);
        recyclerView.setAdapter(mAdapter);
    }

    private class RecyclerListener implements View.OnClickListener {
        private RecyclerView mRecyclerView;
        private CurrencyParser mParser;

        RecyclerListener(RecyclerView recyclerView) {
            this.mRecyclerView = recyclerView;
            mParser = new CurrencyParser();
        }

        @Override
        public void onClick(View view) {
            int position = mRecyclerView.getChildLayoutPosition(view);
            TransactionsItem item = mListTrans.get(position);
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("Thông Tin Khoản Thu").setCancelable(true);
            StringBuilder msg = new StringBuilder();
            msg.append("Tiền: ").append(mParser.format(item.getCost()));
            msg.append("\nNhóm: ").append(item.getType());
            msg.append("\nNgày: ").append(item.getDate());
            msg.append("\nGhi Chú: ").append(item.getNote().isEmpty() ? "Trống" : item.getNote());
            dialog.setMessage(msg);
            dialog.setNegativeButton("OK", null);
            dialog.show();
        }
    }
}
