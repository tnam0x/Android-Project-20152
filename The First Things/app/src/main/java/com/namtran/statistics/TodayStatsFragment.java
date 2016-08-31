package com.namtran.statistics;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.namtran.adapter.StatisticsListAdapter;
import com.namtran.database.InDb;
import com.namtran.database.OutDb;
import com.namtran.entity.DateParser;
import com.namtran.entity.StatsItem;
import com.namtran.entity.TransactionsItem;
import com.namtran.main.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by namtr on 20/08/2016.
 * Danh sách thu chi thống kê theo ngày, tháng, năm.
 */
public class TodayStatsFragment extends Fragment {
    private ProgressDialog mProDialog;
    private SQLiteDatabase mSQLiteIn, mSQLiteOut;
    private ArrayList<TransactionsItem> mListTransIn, mListTransOut;
    private ArrayList<StatsItem> mListStats;
    private StatisticsListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        ListView listView = (ListView) view.findViewById(R.id.list_item_today);
        mListStats = new ArrayList<>();
        mAdapter = new StatisticsListAdapter(getContext(), R.layout.custom_list_today, mListStats);
        listView.setAdapter(mAdapter);
        InDb inDb = new InDb(getContext());
        OutDb outDb = new OutDb(getContext());
        mSQLiteIn = inDb.getWritableDatabase();
        mSQLiteOut = outDb.getWritableDatabase();
        mProDialog = new ProgressDialog(getContext());
        mProDialog.setMessage("Đang tải");
        mProDialog.setCancelable(false);
        new RetrieveData().execute();
        return view;
    }

    private void getItemAndSetupToListView() {
        getItemNow();
        getItemMonth();
        getItemYear();
        mAdapter.notifyDataSetChanged();
        if (mProDialog != null && mProDialog.isShowing()) {
            mProDialog.dismiss();
        }
    }

    private void getItemNow() {
        String date = today();
        int costIn = 0, costOut = 0;
        for (TransactionsItem item : mListTransIn) {
            if (item.getDate().equals(date)) {
                costIn += Integer.parseInt(item.getCost());
            }
        }
        for (TransactionsItem item : mListTransOut) {
            if (item.getDate().equals(date)) {
                costOut += Integer.parseInt(item.getCost());
            }
        }
        StatsItem statsItem = new StatsItem(date, String.valueOf(costIn), String.valueOf(costOut));
        mListStats.add(statsItem);
    }

    private void getItemMonth() {
        String date = DateParser.parseMonth(today());
        int costIn = 0, costOut = 0;
        for (TransactionsItem item : mListTransIn) {
            if (date.equals(DateParser.parseMonth(item.getDate()))) {
                costIn += Integer.parseInt(item.getCost());
            }
        }
        for (TransactionsItem item : mListTransOut) {
            if (date.equals(DateParser.parseMonth(item.getDate()))) {
                costOut += Integer.parseInt(item.getCost());
            }
        }
        StatsItem statsItem = new StatsItem(date, String.valueOf(costIn), String.valueOf(costOut));
        mListStats.add(statsItem);
    }

    private void getItemYear() {
        String date = DateParser.parseYear(today());
        int costIn = 0, costOut = 0;
        for (TransactionsItem item : mListTransIn) {
            if (date.equals(DateParser.parseYear(item.getDate()))) {
                costIn += Integer.parseInt(item.getCost());
            }
        }
        for (TransactionsItem item : mListTransOut) {
            if (date.equals(DateParser.parseYear(item.getDate()))) {
                costOut += Integer.parseInt(item.getCost());
            }
        }
        StatsItem statsItem = new StatsItem(date, String.valueOf(costIn), String.valueOf(costOut));
        mListStats.add(statsItem);
    }

    private String today() {
        Calendar calendar = Calendar.getInstance();
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());
        return format.format(calendar.getTime());
    }

    private class RetrieveData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProDialog.show();
            mListTransIn = new ArrayList<>();
            mListTransOut = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            TransactionsItem item;
            String id;
            String queryIn = "select * from " + InDb.TABLE_NAME;
            Cursor cursorIn = mSQLiteIn.rawQuery(queryIn, null);
            if (cursorIn.moveToFirst()) { // Thu
                do {
                    id = cursorIn.getString(0);
                    String cost = cursorIn.getString(1);
                    String type = cursorIn.getString(2);
                    String note = cursorIn.getString(3);
                    String date = cursorIn.getString(4);
                    item = new TransactionsItem(cost, type, note, date, id);
                    mListTransIn.add(item);
                } while (cursorIn.moveToNext());
            }
            cursorIn.close();
            String queryOut = "select * from " + OutDb.TABLE_NAME;
            Cursor cursorOut = mSQLiteOut.rawQuery(queryOut, null);
            if (cursorOut.moveToFirst()) { // Thu
                do {
                    id = cursorOut.getString(0);
                    String cost = cursorOut.getString(1);
                    String type = cursorOut.getString(2);
                    String note = cursorOut.getString(3);
                    String date = cursorOut.getString(4);
                    item = new TransactionsItem(cost, type, note, date, id);
                    mListTransOut.add(item);
                } while (cursorOut.moveToNext());
            }
            cursorOut.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Nếu UI is dead thì không làm gì cả
            if (getActivity() == null) {
                return;
            }
            getItemAndSetupToListView();
        }
    }
}
