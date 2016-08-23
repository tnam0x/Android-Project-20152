package com.phanmemquanlychitieu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;

import Adapter.DanhSachTienTeChi;
import Adapter.DanhSachTienTeThu;
import Adapter.DoiNgay;
import Database.dbChi;
import Database.dbThu;
import Objects.TienThuChi;

public class NgoaiTe extends Activity {

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
    private Context context = this;
    private dbThu dbthu;
    private dbChi dbchi;
    private TextView txttygia;
    private ListView listthu;
    private ListView listchi;
    private DanhSachTienTeThu myadapterthu = null;
    private DanhSachTienTeChi myadapterchi = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doitien);

        listthu = (ListView) findViewById(R.id.listView_danhsachkhoanthu);
        listchi = (ListView) findViewById(R.id.listView_danhsachkhoanchi);
        Spinner spinnertien = (Spinner) findViewById(R.id.spinner_danhsachloaitien);
        txttygia = (TextView) findViewById(R.id.textView_tygia);
        ArrayAdapter<String> adaptertien = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, NAME_MONEY);
        spinnertien.setAdapter(adaptertien);
        if (checkConn()) {
            spinnertien.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    new LoadDataTask(NgoaiTe.this, position).execute();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } else {
            AlertDialog.Builder a = new AlertDialog.Builder(NgoaiTe.this);
            a.setTitle("Kiểm tra kết nối");
            a.setMessage("Không có kết nối Internet");
            a.setNegativeButton("OK", null);
            a.show();
        }

        dbthu = new dbThu(this);
        dbchi = new dbChi(this);
        loadTab();

        danhSachThu();
        danhSachChi();

    }

    public void loadSpin(int position) {
        if (checkConn()) {
            TienThuChi khoantien = new TienThuChi();
            txttygia.setText(khoantien.getTyGia(KH_MONEY[position]));
            myadapterthu.vietBua(KH_MONEY[position]);
            myadapterthu.notifyDataSetChanged();
            myadapterchi.vietBua(KH_MONEY[position]);
            myadapterchi.notifyDataSetChanged();
        } else {
            txttygia.setText("");
            myadapterthu.vietBua(" (VND)");
            myadapterthu.notifyDataSetChanged();
            myadapterchi.vietBua(" (VND)");
            myadapterchi.notifyDataSetChanged();
        }
    }

    public boolean checkConn() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ConnectivityManager managerConn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = managerConn.getActiveNetworkInfo();
        if (info != null) { // connected to the internet
            if (info.getType() == ConnectivityManager.TYPE_WIFI || info.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }

    public void loadTab() {
        final TabHost tab = (TabHost) findViewById(android.R.id.tabhost);
        // goi cau lenh setup
        tab.setup();
        TabHost.TabSpec spec;
        // tao tab 1
        spec = tab.newTabSpec("t1");
        spec.setContent(R.id.tab11);
        spec.setIndicator("DANH SÁCH THU");
        tab.addTab(spec);
        // tao tab2
        spec = tab.newTabSpec("t2");
        spec.setContent(R.id.tab21);
        spec.setIndicator("DANH SÁCH CHI");
        tab.addTab(spec);
        tab.setCurrentTab(0);
    }

    public void danhSachThu() {
        SQLiteDatabase mDbthu = dbthu.getWritableDatabase();
        String query = "select * from " + dbThu.TABLE_NAME;
        Cursor mCursorthu = mDbthu.rawQuery(query, null);
        ArrayList<TienThuChi> arrthu = new ArrayList<>();
        DoiNgay doingaythu = new DoiNgay();
        if (mCursorthu.moveToFirst()) {
            do {
                TienThuChi objectthu = new TienThuChi();
                objectthu.setId(mCursorthu.getString(0));
                objectthu.setTien(mCursorthu.getString(1));
                objectthu.setNhom(mCursorthu.getString(2));
                objectthu.setNgaythang(doingaythu.doiDate(mCursorthu.getString(3)));
                objectthu.setGhichu(mCursorthu.getString(4));
                arrthu.add(objectthu);
            } while (mCursorthu.moveToNext());
        }
        mCursorthu.close();

        ArrayList<TienThuChi> sapxepthu;
        sapxepthu = doingaythu.sapXep(arrthu);
        for (int i = 0; i < sapxepthu.size(); i++) {
            String strsapxep = doingaythu.ngay(sapxepthu.get(i).getNgaythang());
            sapxepthu.get(i).setNgaythang(strsapxep);
        }
        myadapterthu = new DanhSachTienTeThu(this, R.layout.t_customlayout_thu, sapxepthu);
        listthu.setAdapter(myadapterthu);
    }

    public void danhSachChi() {
        SQLiteDatabase mDbchi = dbchi.getWritableDatabase();
        String querychi = "select * from " + dbChi.TABLE_NAME;
        Cursor mCursorchi = mDbchi.rawQuery(querychi, null);
        ArrayList<TienThuChi> arrchi = new ArrayList<>();
        DoiNgay doingaychi = new DoiNgay();
        if (mCursorchi.moveToFirst()) {
            do {
                TienThuChi objectchi = new TienThuChi();
                objectchi.setId(mCursorchi.getString(0));
                objectchi.setTien(mCursorchi.getString(1));
                objectchi.setNhom(mCursorchi.getString(2));
                objectchi.setNgaythang(doingaychi.doiDate(mCursorchi.getString(3)));
                objectchi.setGhichu(mCursorchi.getString(4));
                arrchi.add(objectchi);
            } while (mCursorchi.moveToNext());
        }
        mCursorchi.close();

        ArrayList<TienThuChi> sapxepchi;
        sapxepchi = doingaychi.sapXep(arrchi);
        for (int i = 0; i < sapxepchi.size(); i++) {
            String strsapxep = doingaychi.ngay(sapxepchi.get(i).getNgaythang());
            sapxepchi.get(i).setNgaythang(strsapxep);
        }
        myadapterchi = new DanhSachTienTeChi(this, R.layout.t_customlayout_chi, sapxepchi);
        listchi.setAdapter(myadapterchi);
    }

    public class LoadDataTask extends AsyncTask<Void, Integer, Void> {
        ProgressDialog progressDialog;
        Activity daddy;
        int postion;

        public LoadDataTask(Activity context, int postion) {
            daddy = context;
            this.postion = postion;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                synchronized (this) {
                    int counter = 0;
                    while (counter <= 5) {
                        this.wait(100);
                        counter++;
                        publishProgress(counter * 20);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            loadSpin(postion);
        }
    }
}
