package com.phanmemquanlychitieu;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import Adapter.DanhSachChi;
import Adapter.DanhSachThu;
import Adapter.DoiNgay;
import Database.UserDatabase;
import Database.dbChi;
import Database.dbLaiXuat;
import Database.dbThu;
import Objects.BaoCao;
import Objects.Item;
import Objects.TienThuChi;

/**
 * Created by Legendary on 10/05/2016.
 */
public class NavigationDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // danh sach thu
    private UserDatabase userDb;
    private SQLiteDatabase mSQLite;

    private dbLaiXuat laiXuatDb;
    private SQLiteDatabase mDbLaiXuat;

    private dbThu dbthu;
    private SQLiteDatabase mDbthu;
    private Cursor mCursorthu;
    // danh sach chi
    private dbChi dbchi;
    private SQLiteDatabase mDbchi;
    private Cursor mCursorchi;

    private BaoCao objectchi2;
    private Firebase usersRef;
    private ArrayList<BaoCao> arrthu;
    private ArrayList<BaoCao> arrchi;
    // test
    private ArrayList<TienThuChi> sapxepthu = null;
    private ArrayList<TienThuChi> sapxepchi = null;
    private ArrayAdapter<String> adapterthu = null;
    private ArrayAdapter<String> adapterchi = null;
    private Context context = this;
    // income
    private ListView listthu;
    private ListView listchi;

    //sua thu
    private EditText suatienthu;
    private Spinner suanhomthu;
    private TextView suangaythu;
    private EditText suaghichuthu;
    private ImageButton btnsavethu, btnsuangaythu;
    private String[] arrspinnerthu = {"Tiền Lương", "Đòi Nợ", "Bán Đồ", "Đi Vay", "Khác"};
    //sua chi
    private EditText suatienchi;
    private Spinner suanhomchi;
    private TextView suangaychi;
    private EditText suaghichuchi;
    private ImageButton btnsavechi, btnsuangaychi;
    private String[] arrspinnerchi = {"Ăn Uống", "Quần Áo", "Cho vay", "Sinh Hoạt", "Đi Lại", "Trả Nợ", "Khác"};
    ProgressDialog dialog;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //loadTab();
        Firebase.setAndroidContext(this);
        Firebase root = new Firebase("https://expenseproject.firebaseio.com/");
        userDb = new UserDatabase(this);
        dbthu = new dbThu(this);
        dbchi = new dbChi(this);
        laiXuatDb = new dbLaiXuat(this);
        usersRef = root.child(getUid());
//        listthu = (ListView) findViewById(R.id.listView_danhsachkhoanthu);
//        listchi = (ListView) findViewById(R.id.listView_danhsachkhoanchi);
//        danhSachThu();
//        danhSachChi();
       // new GetData().execute();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, new DanhSachThuChi());
        fragmentTransaction.commit();

        //su kien khi click vào listview thu
//        listthu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
//                AlertDialog.Builder b = new AlertDialog.Builder(NavigationDrawer.this);
//                b.setTitle("Lựa Chọn Của Bạn");
//                b.setMessage("Bạn Muốn Gì?");
//                b.setPositiveButton("Sửa", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        final Dialog dialogthu = new Dialog(context);
//                        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                        View v = li.inflate(R.layout.suakhoanthu, parent, false);
//                        dialogthu.setContentView(v);
//                        dialogthu.setTitle("Sửa Khoản Thu");
//                        dialogthu.show();
//                        // link to layout
//                        suatienthu = (EditText) dialogthu.findViewById(R.id.editTextsuathu_tienkhoanthu);
//                        suanhomthu = (Spinner) dialogthu.findViewById(R.id.spinnersuathu_nhomkhoanthu);
//                        suangaythu = (TextView) dialogthu.findViewById(R.id.textViewsuathu_ngaykhoanthu);
//                        suaghichuthu = (EditText) dialogthu.findViewById(R.id.editTextsuathu_ghichukhoanthu);
//                        dexuatthu();
//                        // pick time
//                        btnsuangaythu = (ImageButton) dialogthu.findViewById(R.id.imageButtonsuathu_chonngaykhoanthu);
//                        btnsuangaythu.setOnClickListener(new View.OnClickListener() {
//
//                            @Override
//                            public void onClick(View v) {
//                                showDatePickerDialog(null);
//                            }
//                        });
//                        // set value to dialog
//                        suatienthu.setText(sapxepthu.get(position).getTien());
//                        suangaythu.setText(sapxepthu.get(position).getNgaythang());
//                        suaghichuthu.setText(sapxepthu.get(position).getGhichu());
//                        adapterthu = new ArrayAdapter<>(NavigationDrawer.this, android.R.layout.simple_spinner_dropdown_item, arrspinnerthu);
//                        suanhomthu.setAdapter(adapterthu);
//                        suanhomthu.setSelection(checkPosition(sapxepthu.get(position).getNhom(), arrspinnerthu));
//                        // save button
//                        btnsavethu = (ImageButton) dialogthu.findViewById(R.id.imageButtonsuathu_luukhoanthu);
//                        btnsavethu.setOnClickListener(new View.OnClickListener() {
//
//                            @SuppressWarnings("static-access")
//                            @Override
//                            public void onClick(View v) {
//                                mDbthu = dbthu.getWritableDatabase();
//                                ContentValues cv = new ContentValues();
//                                String cost = suatienthu.getText().toString();
//                                String date = suangaythu.getText().toString();
//                                String type = suanhomthu.getSelectedItem().toString();
//                                String note = suaghichuthu.getText().toString();
//                                cv.put(dbThu.COL_TIEN, cost);
//                                cv.put(dbThu.COL_DATE, date);
//                                cv.put(dbThu.COL_NHOM, type);
//                                cv.put(dbThu.COL_GHICHU, note);
//                                mDbthu.update(dbThu.TABLE_NAME, cv, "_id " + "=" + sapxepthu.get(position).getId(), null);
//                                danhSachThu();
//                                dialogthu.cancel();
//                            }
//                        });
//
//                    }
//                });
//                b.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        mDbthu = dbthu.getWritableDatabase();
//                        String id = sapxepthu.get(position).getId();
//                        mDbthu.delete(dbThu.TABLE_NAME, "_id=?", new String[]{id});
//                        danhSachThu();
//                    }
//                });
//                b.create().show();
//            }
//        });
//        //su kien khi click vào listview chi
//        listchi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(final AdapterView<?> parent, View view,
//                                    final int position, long id) {
//                AlertDialog.Builder b = new AlertDialog.Builder(NavigationDrawer.this);
//                b.setTitle("Lựa Chọn Của Bạn");
//                b.setMessage("Bạn Muốn Gì?");
//                b.setPositiveButton("Sửa", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        final Dialog dialogchi = new Dialog(context);
//                        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                        View v = li.inflate(R.layout.suakhoanchi, parent, false);
//                        dialogchi.setContentView(v);
//                        dialogchi.setTitle("Sửa Khoản Chi");
//                        dialogchi.show();
//                        // link to layout
//                        suatienchi = (EditText) dialogchi.findViewById(R.id.editTextsuachi_tienkhoanthu);
//                        suanhomchi = (Spinner) dialogchi.findViewById(R.id.spinnersuachi_nhomkhoanthu);
//                        suangaychi = (TextView) dialogchi.findViewById(R.id.textViewsuachi_ngaykhoanthu);
//                        suaghichuchi = (EditText) dialogchi.findViewById(R.id.editTextsuachi_ghichukhoanthu);
//                        dexuatchi();
//                        // pick time
//                        btnsuangaychi = (ImageButton) dialogchi.findViewById(R.id.imageButtonsuachi_chonngaykhoanthu);
//                        btnsuangaychi.setOnClickListener(new View.OnClickListener() {
//
//                            @Override
//                            public void onClick(View v) {
//                                showDatePickerDialogchi(null);
//                            }
//                        });
//                        // set value to dialog
//                        suatienchi.setText(sapxepchi.get(position).getTien());
//                        suangaychi.setText(sapxepchi.get(position).getNgaythang());
//                        suaghichuchi.setText(sapxepchi.get(position).getGhichu());
//                        adapterchi = new ArrayAdapter<>(NavigationDrawer.this, android.R.layout.simple_spinner_dropdown_item, arrspinnerchi);
//                        suanhomchi.setAdapter(adapterchi);
//                        suanhomchi.setSelection(checkPosition(sapxepchi.get(position).getNhom(), arrspinnerchi));
//                        // save button
//                        btnsavechi = (ImageButton) dialogchi.findViewById(R.id.imageButtonsuachi_luukhoanthu);
//                        btnsavechi.setOnClickListener(new View.OnClickListener() {
//
//                            @Override
//                            public void onClick(View v) {
//                                mDbchi = dbchi.getWritableDatabase();
//                                ContentValues cv = new ContentValues();
//                                cv.put(dbChi.COL_TIEN, suatienchi.getText().toString());
//                                cv.put(dbChi.COL_DATE, suangaychi.getText().toString());
//                                cv.put(dbChi.COL_NHOM, suanhomchi.getSelectedItem().toString());
//                                cv.put(dbChi.COL_GHICHU, suaghichuchi.getText().toString());
//                                mDbchi.update(dbChi.TABLE_NAME, cv, "_id " + "=" + sapxepchi.get(position).getId(), null);
//                                dialogchi.cancel();
//                                danhSachChi();
//                            }
//                        });
//                    }
//                });
//                b.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        mDbchi = dbchi.getWritableDatabase();
//                        String id = sapxepchi.get(position).getId();
//                        mDbchi.delete(dbChi.TABLE_NAME, "_id=?", new String[]{id});
//                        danhSachChi();
//                    }
//                });
//                b.create().show();
//            }
//        });
    }

    public class GetData extends AsyncTask<Void, Void, ArrayList<TienThuChi>> {
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(NavigationDrawer.this);
            dialog.setMessage("Đang tải...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected ArrayList<TienThuChi> doInBackground(Void... params) {
            mDbthu = dbthu.getWritableDatabase();
            String query = "select * from " + dbThu.TABLE_NAME;
            Cursor mCursorthu = mDbthu.rawQuery(query, null);
            ArrayList<TienThuChi> arrthu = new ArrayList<>();
            DoiNgay doingaythu = new DoiNgay();
            TienThuChi objectthu;
            if (mCursorthu.moveToFirst()) {
                do {
                    objectthu = new TienThuChi();
                    objectthu.setId(mCursorthu.getString(0));
                    objectthu.setTien(mCursorthu.getString(1));
                    objectthu.setNhom(mCursorthu.getString(2));
                    objectthu.setNgaythang(doingaythu.doiDate(mCursorthu.getString(3)));
                    objectthu.setGhichu(mCursorthu.getString(4));
                    arrthu.add(objectthu);
                } while (mCursorthu.moveToNext());
            }
            mCursorthu.close();

            sapxepthu = new ArrayList<>();
            sapxepthu = doingaythu.sapXep(arrthu);
            for (int i = 0; i < sapxepthu.size(); i++) {
                String strsapxep = doingaythu.ngay(sapxepthu.get(i).getNgaythang());
                sapxepthu.get(i).setNgaythang(strsapxep);
            }
            return sapxepthu;
        }

        @Override
        protected void onPostExecute(ArrayList<TienThuChi> result) {
            if (dialog.isShowing())
                dialog.dismiss();
            DanhSachThu myadapterthu = new DanhSachThu(NavigationDrawer.this, R.layout.t_customlayout_thu, sapxepthu);
            listthu.setAdapter(myadapterthu);
            myadapterthu.notifyDataSetChanged();
            super.onPostExecute(result);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_diagram) {
            arrayThu();
            arrayChi();
            if (arrthu.size() == 0 || arrchi.size() == 0) {
                Toast toast = Toast.makeText(NavigationDrawer.this, "Danh sách rỗng", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Intent chuyen = new Intent(NavigationDrawer.this, BaoCaoThuChi.class);
                startActivity(chuyen);
            }
        } else if (id == R.id.nav_exchange) {
            Intent chuyen = new Intent(NavigationDrawer.this, NgoaiTe.class);
            startActivity(chuyen);
        } else if (id == R.id.nav_deposit) {
            Intent chuyen = new Intent(NavigationDrawer.this, LaiXuat.class);
            startActivity(chuyen);
        } else if (id == R.id.nav_sync) {
            if (checkConn()) {
                syncData();
                Toast.makeText(NavigationDrawer.this, "Sync success", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder a = new AlertDialog.Builder(NavigationDrawer.this);
                a.setTitle("Kiểm tra kết nối");
                a.setMessage("Không có kết nối Internet");
                a.setNegativeButton("OK", null);
                a.show();
            }

        } else if (id == R.id.nav_user_info) {

        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder exitDialog = new AlertDialog.Builder(NavigationDrawer.this);
            exitDialog.setTitle("Quản lý chi tiêu");
            exitDialog.setMessage("Bạn có thực sự muốn đăng xuất chương trình không?");
            exitDialog.setNegativeButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mSQLite = userDb.getWritableDatabase();
                    mDbthu = dbthu.getWritableDatabase();
                    mDbchi = dbchi.getWritableDatabase();
                    mDbLaiXuat = laiXuatDb.getWritableDatabase();

                    mSQLite.execSQL("delete from " + UserDatabase.TABLE_NAME);
                    mDbthu.execSQL("delete from " + dbThu.TABLE_NAME);
                    mDbchi.execSQL("delete from " + dbChi.TABLE_NAME);
                    mDbLaiXuat.execSQL("delete from " + dbLaiXuat.TABLE_NAME);
                    Intent intent = new Intent(NavigationDrawer.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            exitDialog.setPositiveButton("Để sau", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            exitDialog.setCancelable(false);
            exitDialog.show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    public String getUid() {
        String uid = "";
        mSQLite = userDb.getReadableDatabase();
        String query = "select * from " + UserDatabase.TABLE_NAME;
        Cursor userCursor = mSQLite.rawQuery(query, null);
        if (userCursor.moveToFirst()) {
            uid = userCursor.getString(1);
        }
        userCursor.close();
        return uid;
    }

    private void syncData() {
        int id;
        Firebase expenseRef = usersRef.child("Expense");
        Firebase incomeRef = usersRef.child("Income");
        expenseRef.setValue(null);
        incomeRef.setValue(null);
        mDbchi = dbchi.getReadableDatabase();
        String queryChi = "select * from " + dbChi.TABLE_NAME;
        mCursorchi = mDbchi.rawQuery(queryChi, null);
        Item item;
        if (mCursorchi.moveToFirst()) {
            do {
                id = Integer.parseInt(mCursorchi.getString(0));
                String cost = mCursorchi.getString(1);
                String type = mCursorchi.getString(2);
                String date = mCursorchi.getString(3);
                String note = mCursorchi.getString(4);
                item = new Item(cost, type, note, date, id);
                expenseRef.child("" + id).setValue(item);
            } while (mCursorchi.moveToNext());
        }

        mDbthu = dbthu.getReadableDatabase();
        String queryThu = "select * from " + dbThu.TABLE_NAME;
        mCursorthu = mDbthu.rawQuery(queryThu, null);
        if (mCursorthu.moveToFirst()) {
            do {
                id = Integer.parseInt(mCursorthu.getString(0));
                String cost = mCursorthu.getString(1);
                String type = mCursorthu.getString(2);
                String date = mCursorthu.getString(3);
                String note = mCursorthu.getString(4);
                item = new Item(cost, type, note, date, id);
                incomeRef.child("" + id).setValue(item);
            } while (mCursorthu.moveToNext());
        }
    }

    public void danhSachThu() {
        mDbthu = dbthu.getWritableDatabase();
        String query = "select * from " + dbThu.TABLE_NAME;
        Cursor mCursorthu = mDbthu.rawQuery(query, null);
        ArrayList<TienThuChi> arrthu = new ArrayList<>();
        DoiNgay doingaythu = new DoiNgay();
        TienThuChi objectthu;
        if (mCursorthu.moveToFirst()) {
            do {
                objectthu = new TienThuChi();
                objectthu.setId(mCursorthu.getString(0));
                objectthu.setTien(mCursorthu.getString(1));
                objectthu.setNhom(mCursorthu.getString(2));
                objectthu.setNgaythang(doingaythu.doiDate(mCursorthu.getString(3)));
                objectthu.setGhichu(mCursorthu.getString(4));
                arrthu.add(objectthu);
            } while (mCursorthu.moveToNext());
        }
        mCursorthu.close();

        sapxepthu = new ArrayList<>();
        sapxepthu = doingaythu.sapXep(arrthu);
        for (int i = 0; i < sapxepthu.size(); i++) {
            String strsapxep = doingaythu.ngay(sapxepthu.get(i).getNgaythang());
            sapxepthu.get(i).setNgaythang(strsapxep);
        }
        DanhSachThu myadapterthu = new DanhSachThu(NavigationDrawer.this, R.layout.t_customlayout_thu, sapxepthu);
        listthu.setAdapter(myadapterthu);
    }

    public void danhSachChi() {
        mDbchi = dbchi.getReadableDatabase();
        String querychi = "select * from " + dbChi.TABLE_NAME;
        Cursor mCursorchi = mDbchi.rawQuery(querychi, null);
        ArrayList<TienThuChi> arrchi = new ArrayList<>();
        DoiNgay doingaychi = new DoiNgay();
        TienThuChi objectchi;
        if (mCursorchi.moveToFirst()) {
            do {
                objectchi = new TienThuChi();
                objectchi.setId(mCursorchi.getString(0));
                objectchi.setTien(mCursorchi.getString(1));
                objectchi.setNhom(mCursorchi.getString(2));
                objectchi.setNgaythang(doingaychi.doiDate(mCursorchi.getString(3)));
                objectchi.setGhichu(mCursorchi.getString(4));
                arrchi.add(objectchi);
            } while (mCursorchi.moveToNext());
        }
        mCursorchi.close();

        sapxepchi = new ArrayList<>();
        sapxepchi = doingaychi.sapXep(arrchi);
        for (int i = 0; i < sapxepchi.size(); i++) {
            String strsapxep = doingaychi.ngay(sapxepchi.get(i).getNgaythang());
            sapxepchi.get(i).setNgaythang(strsapxep);
        }
        DanhSachChi myadapterchi = new DanhSachChi(NavigationDrawer.this, R.layout.t_customlayout_chi, sapxepchi);
        listchi.setAdapter(myadapterchi);
    }

    public void arrayChi() {
        mDbchi = dbchi.getReadableDatabase();
        String queryChi = "select * from " + dbChi.TABLE_NAME;
        mCursorchi = mDbchi.rawQuery(queryChi, null);
        arrchi = new ArrayList<>();
        if (mCursorchi.moveToFirst()) {
            do {
                objectchi2 = new BaoCao();
                objectchi2.setTienchi(mCursorchi.getString(1));
                objectchi2.setNgay(mCursorchi.getString(3));
                objectchi2.setNhom(mCursorchi.getString(2));
                arrchi.add(objectchi2);
            } while (mCursorchi.moveToNext());
        }
    }

    public void arrayThu() {
        mDbthu = dbthu.getReadableDatabase();
        String query = "select * from " + dbThu.TABLE_NAME;
        mCursorthu = mDbthu.rawQuery(query, null);
        arrthu = new ArrayList<>();
        if (mCursorthu.moveToFirst()) {
            do {
                objectchi2 = new BaoCao();
                objectchi2.setTienthu(mCursorthu.getString(1));
                objectchi2.setNgay(mCursorthu.getString(3));
                objectchi2.setNhom(mCursorthu.getString(2));
                arrthu.add(objectchi2);
            } while (mCursorthu.moveToNext());
        }
    }

    // find the position
    public int checkPosition(String name, String[] array) {
        int i = 0;
        for (; i < array.length; i++)
            if (array[i].equals(name))
                break;
        return i;
    }

    public void dexuatthu() {
        Calendar ngaythang = Calendar.getInstance();
        //dinh dang 12h
        String dinhdang24 = "dd/MM/yyyy";
        SimpleDateFormat dinhdang;
        dinhdang = new SimpleDateFormat(dinhdang24, Locale.getDefault());
        suangaythu.setText(dinhdang.format(ngaythang.getTime()));
    }

    public void dexuatchi() {
        Calendar ngaythang = Calendar.getInstance();
        //dinh dang 12h
        String dinhdang24 = "dd/MM/yyyy";
        SimpleDateFormat dinhdang;
        dinhdang = new SimpleDateFormat(dinhdang24, Locale.getDefault());
        suangaychi.setText(dinhdang.format(ngaythang.getTime()));
    }

    //datepicker ngay chi
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showDatePickerDialogchi(View v) {
        DialogFragment newFragment = new DatePickerFragmentchi();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    //datepicker thu
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

//    @Override
//    protected void onResume() {
//        danhSachThu();
//        danhSachChi();
//        super.onResume();
//    }

    public void loadTab() {
        final TabHost tab = (TabHost) findViewById(android.R.id.tabhost);
        //goi cau lenh setup
        tab.setup();
        TabHost.TabSpec spec;
        //tao tab 1
        spec = tab.newTabSpec("t1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("DANH SÁCH THU");
        tab.addTab(spec);
        //tao tab2
        spec = tab.newTabSpec("t2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("DANH SÁCH CHI");
        tab.addTab(spec);
        tab.setCurrentTab(0);
    }


    @SuppressLint({"ValidFragment", "NewApi"})
    public class DatePickerFragmentchi extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DATE);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            month = month + 1;
            String datetimesuachi;
            if (day < 10) {
                if (month < 10) {
                    datetimesuachi = "0" + String.valueOf(day) + "/" + "0" + String.valueOf(month) + "/" + String.valueOf(year);
                } else {
                    datetimesuachi = "0" + String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
                }
            } else {
                if (month < 10) {
                    datetimesuachi = String.valueOf(day) + "/" + "0" + String.valueOf(month) + "/" + String.valueOf(year);
                } else {
                    datetimesuachi = String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
                }
            }
            suangaychi.setText(datetimesuachi);
        }
    }

    @SuppressLint({"ValidFragment", "NewApi"})
    public class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DATE);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            month = month + 1;
            String datetimesuathu;
            if (day < 10) {
                if (month < 10) {
                    datetimesuathu = "0" + String.valueOf(day) + "/" + "0" + String.valueOf(month) + "/" + String.valueOf(year);
                } else {
                    datetimesuathu = "0" + String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
                }
            } else {
                if (month < 10) {
                    datetimesuathu = String.valueOf(day) + "/" + "0" + String.valueOf(month) + "/" + String.valueOf(year);
                } else {
                    datetimesuathu = String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
                }
            }
            suangaythu.setText(datetimesuathu);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
