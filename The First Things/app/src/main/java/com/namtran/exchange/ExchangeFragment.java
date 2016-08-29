package com.namtran.exchange;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.namtran.adapter.ExchangeViewPagerAdapter;
import com.namtran.database.InDb;
import com.namtran.database.OutDb;
import com.namtran.entity.ExchangeItem;
import com.namtran.entity.ForeignCurrency;
import com.namtran.entity.HTMLParser;
import com.namtran.entity.TransactionsItem;
import com.namtran.main.R;

import java.util.ArrayList;

/**
 * Created by namtr on 22/08/2016.
 */
public class ExchangeFragment extends Fragment {
    private AppCompatSpinner mSpinner;
    private TextView mRateTextView;
    private SQLiteDatabase mSQLiteIn, mSQLiteOut;
    public static ArrayList<TransactionsItem> mListTransIn, mListTransOut;
    private ProgressDialog mProDialog;
    private ArrayList<ForeignCurrency> mFCurrency;
    private final static String[] NAME_MONEY = {"US Dollar (USD)", "Euro (EUR)",
            "British Pound (GBD)", "HongKong Dollar (HKD)", "Japan Yen (JPY)",
            "South Korean Won (KRW)", "Indian RUPEE (INR)",
            "Kuwaiti Dinar (KWD)", "Swiss France (CHF)",
            "Austraylia Dollar (AUD)", "Malaysian Ringgit (MYR)",
            "Russian Ruble (RUB)", "Singapore Dollar (SGD)", "Thai Baht (THB)",
            "Canadian Dollar (CAD)", "Saudi Rial (SAR)"};
    private final static String[] KH_MONEY = {"USD", "EUR", "GBP", "HKD", "JPY",
            "KRW", "INR", "KWD", "CHF", "AUD", "MYR", "RUB", "SGD", "THB", "CAD", "SAR"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions_statistics, container, false);
        LinearLayout tableLayout = (LinearLayout) view.findViewById(R.id.layout_exchange);
        tableLayout.setVisibility(View.VISIBLE);
        mSpinner = (AppCompatSpinner) view.findViewById(R.id.spinner_exchange);
        mRateTextView = (TextView) view.findViewById(R.id.tv_rate_exchange);
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        final ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setAdapter(new ExchangeViewPagerAdapter(getChildFragmentManager()));
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(pager, true);
            }
        });
        // Custom the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner, NAME_MONEY);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        init();
        return view;
    }

    private void init() {
        InDb inDb = new InDb(getContext());
        OutDb outDb = new OutDb(getContext());
        mSQLiteIn = inDb.getWritableDatabase();
        mSQLiteOut = outDb.getWritableDatabase();
        mListTransOut = new ArrayList<>();
        mListTransIn = new ArrayList<>();
        mFCurrency = new ArrayList<>();
        mProDialog = new ProgressDialog(getContext());
        mProDialog.setMessage("Đang tải...");
        mProDialog.setCancelable(false);
        if (isInternetAvailable() && mListTransOut.isEmpty()) {
            new GetDataFromDb().execute();
        } else {
            Toast.makeText(getContext(), "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadChange(final int position) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (ForeignCurrency currency : mFCurrency) {
                    if (currency.symbol.equals(KH_MONEY[position])) {
                        mRateTextView.setText(currency.toString());
                        updateListExp(currency);
                    }
                }
            }
        });
    }

    private void updateListExp(ForeignCurrency currency) {
        ArrayList<ExchangeItem> revData = new ArrayList<>();
        ArrayList<ExchangeItem> expData = new ArrayList<>();
        ExchangeItem mItem;
        String cost, type, date;
        for (TransactionsItem item : mListTransIn) {
            cost = currency.VNDToAntoher(item.getCost());
            type = item.getType();
            date = item.getDate();
            mItem = new ExchangeItem(cost, type, date);
            revData.add(mItem);
        }
        for (TransactionsItem item : mListTransOut) {
            cost = currency.VNDToAntoher(item.getCost());
            type = item.getType();
            date = item.getDate();
            mItem = new ExchangeItem(cost, type, date);
            expData.add(mItem);
        }
        RevExchangeFragment.update(revData);
        ExpExchangeFragment.update(expData);
    }

    private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d("Spinner", "clicked");
            loadChange(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    // Kiểm tra mạng
    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    // Lấy dữ liệu từ Database
    private class GetDataFromDb extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            mFCurrency = HTMLParser.parseHTML();
            TransactionsItem item;
            String id;
            String queryIn = "select * from " + InDb.TABLE_NAME;
            Cursor cursorIn = mSQLiteIn.rawQuery(queryIn, null);
            if (cursorIn.moveToFirst()) {
                do {
                    id = cursorIn.getString(0);
                    String cost = cursorIn.getString(1);
                    String type = cursorIn.getString(2);
                    String note = cursorIn.getString(3);
                    String date = cursorIn.getString(4);
                    item = new TransactionsItem(cost, type, note, date, id);
                    mListTransIn.add(item);
                    Log.d("IN DATA - Exchange", item.toString());
                } while (cursorIn.moveToNext());
            }
            cursorIn.close();

            String queryOut = "select * from " + OutDb.TABLE_NAME;
            Cursor cursorOut = mSQLiteOut.rawQuery(queryOut, null);
            if (cursorOut.moveToFirst()) {
                do {
                    id = cursorOut.getString(0);
                    String cost = cursorOut.getString(1);
                    String type = cursorOut.getString(2);
                    String note = cursorOut.getString(3);
                    String date = cursorOut.getString(4);
                    item = new TransactionsItem(cost, type, note, date, id);
                    mListTransOut.add(item);
                    Log.d("OUT DATA - Exchange", item.toString());
                } while (cursorOut.moveToNext());
            }
            cursorOut.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mSpinner.setOnItemSelectedListener(spinnerListener);
            loadChange(0);
            mProDialog.dismiss();
        }
    }

}
