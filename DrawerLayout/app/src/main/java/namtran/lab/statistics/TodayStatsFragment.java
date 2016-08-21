package namtran.lab.statistics;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import namtran.lab.adapter.TodayStatsAdapter;
import namtran.lab.database.InDb;
import namtran.lab.database.OutDb;
import namtran.lab.entity.Item;
import namtran.lab.entity.ParseDate;
import namtran.lab.entity.StatsItem;
import namtran.lab.revexpmanager.R;

/**
 * Created by namtr on 20/08/2016.
 */
public class TodayStatsFragment extends Fragment {
    private ProgressDialog dialog;
    private SQLiteDatabase sqlOut, sqlIn;
    private ArrayList<Item> in, out;
    private ArrayList<StatsItem> listItem;
    private TodayStatsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_today, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.list_item_today);
        listItem = new ArrayList<>();
        adapter = new TodayStatsAdapter(getContext(), R.layout.custom_list_today, listItem);
        listView.setAdapter(adapter);
        InDb inDb = new InDb(getContext());
        OutDb outDb = new OutDb(getContext());
        sqlIn = inDb.getWritableDatabase();
        sqlOut = outDb.getWritableDatabase();
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Đang tải");
        dialog.setCancelable(false);
        new RetrieveData().execute();
        return rootView;
    }

    private void getItemAndSetupToListView() {
        Log.d("TodayStats", "setup");
        getItemNow();
        getItemMonth();
        getItemYear();
        adapter.notifyDataSetChanged();
        dialog.dismiss();
    }

    private void getItemNow() {
        String date = today();
        int costIn = 0, costOut = 0;
        for (Item item : in) {
            if (item.getDate().equals(date)) {
                costIn += Integer.parseInt(item.getCost());
                Log.d("CostIn", costIn + "");
            }
        }
        for (Item item : out) {
            if (item.getDate().equals(date)) {
                costOut += Integer.parseInt(item.getCost());
                Log.d("CostOut", costOut + "");
            }
        }
        StatsItem statsItem = new StatsItem(date, String.valueOf(costIn), String.valueOf(costOut));
        listItem.add(statsItem);
    }

    private void getItemMonth() {
        String date = ParseDate.parseMonth(today());
        Log.d("Month", date);
        int costIn = 0, costOut = 0;
        for (Item item : in) {
            if (date.equals(ParseDate.parseMonth(item.getDate()))) {
                costIn += Integer.parseInt(item.getCost());
            }
        }
        for (Item item : out) {
            if (date.equals(ParseDate.parseMonth(item.getDate()))) {
                costOut += Integer.parseInt(item.getCost());
            }
        }
        StatsItem statsItem = new StatsItem(date, String.valueOf(costIn), String.valueOf(costOut));
        listItem.add(statsItem);
    }

    private void getItemYear() {
        String date = ParseDate.parseYear(today());
        Log.d("Year", date);
        int costIn = 0, costOut = 0;
        for (Item item : in) {
            if (date.equals(ParseDate.parseYear(item.getDate()))) {
                costIn += Integer.parseInt(item.getCost());
            }
        }
        for (Item item : out) {
            if (date.equals(ParseDate.parseYear(item.getDate()))) {
                costOut += Integer.parseInt(item.getCost());
            }
        }
        StatsItem statsItem = new StatsItem(date, String.valueOf(costIn), String.valueOf(costOut));
        listItem.add(statsItem);
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
            dialog.show();
            Log.d("TodayStats", "getting data");
            in = new ArrayList<>();
            out = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("TodayStats", "doing");
            Item item;
            int id;
            String queryIn = "select * from " + InDb.TABLE_NAME;
            Cursor cursorIn = sqlIn.rawQuery(queryIn, null);
            if (cursorIn.moveToFirst()) { // Thu
                do {
                    id = Integer.parseInt(cursorIn.getString(0));
                    String cost = cursorIn.getString(1);
                    String type = cursorIn.getString(2);
                    String note = cursorIn.getString(3);
                    String date = cursorIn.getString(4);
                    item = new Item(cost, type, note, date, id);
                    in.add(item);
                    Log.d("IN TodayStats", item.toString());
                } while (cursorIn.moveToNext());
            }
            cursorIn.close();
            String queryOut = "select * from " + OutDb.TABLE_NAME;
            Cursor cursorOut = sqlOut.rawQuery(queryOut, null);
            if (cursorOut.moveToFirst()) { // Thu
                do {
                    id = Integer.parseInt(cursorOut.getString(0));
                    String cost = cursorOut.getString(1);
                    String type = cursorOut.getString(2);
                    String note = cursorOut.getString(3);
                    String date = cursorOut.getString(4);
                    item = new Item(cost, type, note, date, id);
                    out.add(item);
                    Log.d("OUT TodayStats", item.toString());
                } while (cursorOut.moveToNext());
            }
            cursorOut.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("TodayStats", "done");
            getItemAndSetupToListView();
        }
    }
}
