package namtran.lab.exchange;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import namtran.lab.adapter.ExchangeRecyclerAdapter;
import namtran.lab.entity.ExchangeItem;
import namtran.lab.entity.Item;
import namtran.lab.revexpmanager.R;

/**
 * Created by namtr on 22/08/2016.
 */
public class ExpExchangeFragment extends Fragment {
    private static ArrayList<ExchangeItem> mData;
    private static ExchangeRecyclerAdapter adapter;
    private ArrayList<Item> out;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_list_item, container, false);
        mData = new ArrayList<>();
        out = ExchangeFragment.out;
        setupRecyclerView(recyclerView);
        return recyclerView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ExchangeRecyclerAdapter(new RecyclerListener(recyclerView), mData, false);
        recyclerView.setAdapter(adapter);
    }

    public static void update(ArrayList<ExchangeItem> data) {
        Log.d("Update Exp", "doing");
        mData.clear();
        mData.addAll(data);
        adapter.notifyDataSetChanged();
    }

    private class RecyclerListener implements View.OnClickListener {
        private RecyclerView recyclerView;
        private DecimalFormat format;

        public RecyclerListener(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator(',');
            symbols.setDecimalSeparator('.');
            format = new DecimalFormat(",### ₫", symbols);
            format.setMaximumFractionDigits(0);
        }

        @Override
        public void onClick(View view) {
            int position = recyclerView.getChildLayoutPosition(view);
            Item item = out.get(position);
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("Thông Tin Khoản Chi").setCancelable(true);
            StringBuilder msg = new StringBuilder();
            msg.append("Tiền: ").append(format.format(Integer.parseInt(item.getCost())));
            msg.append("\nNhóm: ").append(item.getType());
            msg.append("\nNgày: ").append(item.getDate());
            msg.append("\nGhi Chú: ").append(item.getNote().isEmpty() ? "Trống" : item.getNote());
            dialog.setMessage(msg);
            dialog.setNegativeButton("OK", null);
            dialog.show();
        }
    }

}
