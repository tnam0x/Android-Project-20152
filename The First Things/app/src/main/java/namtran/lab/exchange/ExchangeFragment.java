package namtran.lab.exchange;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import namtran.lab.adapter.ExchangeViewPagerAdapter;
import namtran.lab.database.InDb;
import namtran.lab.database.OutDb;
import namtran.lab.entity.ExchangeItem;
import namtran.lab.entity.ForeignCurrency;
import namtran.lab.entity.HTMLParser;
import namtran.lab.entity.Item;
import namtran.lab.revexpmanager.R;

/**
 * Created by namtr on 22/08/2016.
 */
public class ExchangeFragment extends Fragment {
    private Spinner exchageRateSpinner;
    private TextView tvRate;
    private SQLiteDatabase sqlIn, sqlOut;
    public static ArrayList<Item> in, out;
    private ProgressDialog dialog;
    private ArrayList<ForeignCurrency> mFCurrency;
    private final static String[] NAME_MONEY = {"US Dollar (USD)", "Euro (EUR)",
            "British Pound (GBD)", "HongKong Dollar (HKD)", "Japan Yen (JPY)",
            "South Korean Won (KRW)", "Indian RUPEE (INR)",
            "Kuwaiti Dinar (KWD)", "Swiss France (CHF)",
            "Austraylia Dollar (AUD)", "Malaysian Ringgit (MYR)",
            "Russian Ruble (RUB)", "Singapore Dollar (SGD)", "Thai Baht (THB)",
            "Canadian Dollar (CAD)", "Saudi Rial (SAR)"};
    private final static String[] KH_MONEY = {"USD", "EUR", "GBP", "HKD", "JPY",
            "KRW", "INR", "KWD", "CHF", "AUD", "MYR", "RUB", "SGD", "THB",
            "CAD", "SAR"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transactions_statistics, container, false);
        TableLayout tableLayout = (TableLayout) rootView.findViewById(R.id.layout_exchange);
        tableLayout.setVisibility(View.VISIBLE);
        exchageRateSpinner = (Spinner) rootView.findViewById(R.id.spinner_exchange);
        tvRate = (TextView) rootView.findViewById(R.id.tv_rate_exchange);
        final TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        final ViewPager pager = (ViewPager) rootView.findViewById(R.id.pager);
        pager.setAdapter(new ExchangeViewPagerAdapter(getChildFragmentManager()));
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(pager, true);
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, NAME_MONEY);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exchageRateSpinner.setAdapter(adapter);
        init();
        return rootView;
    }

    private void init() {
        InDb inDb = new InDb(getContext());
        OutDb outDb = new OutDb(getContext());
        sqlIn = inDb.getWritableDatabase();
        sqlOut = outDb.getWritableDatabase();
        out = new ArrayList<>();
        in = new ArrayList<>();
        mFCurrency = new ArrayList<>();
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Đang tải...");
        dialog.setCancelable(false);
        if (isInternetAvailable() && out.isEmpty()) {
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
                        tvRate.setText(currency.toString());
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
        for (Item item : in) {
            cost = currency.VNDToAntoher(item.getCost());
            type = item.getType();
            date = item.getDate();
            mItem = new ExchangeItem(cost, type, date);
            revData.add(mItem);
        }
        for (Item item : out) {
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
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            mFCurrency = HTMLParser.parseHTML();
            Item item;
            int id;
            String queryIn = "select * from " + InDb.TABLE_NAME;
            Cursor cursorIn = sqlIn.rawQuery(queryIn, null);
            if (cursorIn.moveToFirst()) {
                do {
                    id = Integer.parseInt(cursorIn.getString(0));
                    String cost = cursorIn.getString(1);
                    String type = cursorIn.getString(2);
                    String note = cursorIn.getString(3);
                    String date = cursorIn.getString(4);
                    item = new Item(cost, type, note, date, id);
                    in.add(item);
                    Log.d("IN DATA - Exchange", item.toString());
                } while (cursorIn.moveToNext());
            }
            cursorIn.close();

            String queryOut = "select * from " + OutDb.TABLE_NAME;
            Cursor cursorOut = sqlOut.rawQuery(queryOut, null);
            if (cursorOut.moveToFirst()) {
                do {
                    id = Integer.parseInt(cursorOut.getString(0));
                    String cost = cursorOut.getString(1);
                    String type = cursorOut.getString(2);
                    String note = cursorOut.getString(3);
                    String date = cursorOut.getString(4);
                    item = new Item(cost, type, note, date, id);
                    out.add(item);
                    Log.d("OUT DATA - Exchange", item.toString());
                } while (cursorOut.moveToNext());
            }
            cursorOut.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            exchageRateSpinner.setOnItemSelectedListener(spinnerListener);
            loadChange(0);
            dialog.dismiss();
        }
    }

}
