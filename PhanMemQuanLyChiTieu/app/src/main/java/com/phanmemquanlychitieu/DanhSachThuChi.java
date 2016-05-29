package com.phanmemquanlychitieu;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import Adapter.DanhSachChi;
import Adapter.DanhSachThu;
import Adapter.DoiNgay;
import Database.dbChi;
import Database.dbThu;
import Objects.BaoCao;
import Objects.TienThuChi;

public class DanhSachThuChi extends Fragment {
    private List<TienThuChi> sapxepthu;
    private List<TienThuChi> sapxepchi;
    private ArrayAdapter<String> adapterthu;
    private ArrayAdapter<String> adapterchi;
    // income
    private dbThu dbthu;
    private SQLiteDatabase mDbthu;
    // expense
    private dbChi dbchi;
    private SQLiteDatabase mDbchi;
    ListView listthu;
    ListView listchi;

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
    DanhSachThu myadapterthu;
    DanhSachChi myadapterchi;
    ArrayList<BaoCao> arrthu;
    ArrayList<BaoCao> arrchi;
    BaoCao objectchi2;
    Cursor mCursorchi;
    Cursor mCursorthu;

    public DanhSachThuChi() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_danhsachthuchi, container, false);
        listthu = (ListView) view.findViewById(R.id.listView_danhsachkhoanthu);
        listchi = (ListView) view.findViewById(R.id.listView_danhsachkhoanchi);
        dbthu = new dbThu(DanhSachThuChi.this.getActivity());
        dbchi = new dbChi(DanhSachThuChi.this.getActivity());
        loadTab(view);
        sapxepthu = new ArrayList<>();
        sapxepchi = new ArrayList<>();
        arrayThu();
        arrayChi();
        GetData getData = new GetData();
        getData.execute();
        listthu.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                AlertDialog.Builder b = new AlertDialog.Builder(DanhSachThuChi.this.getActivity());
                b.setTitle("Lựa Chọn Của Bạn");
                b.setMessage("Bạn Muốn Gì?");
                b.setPositiveButton("Sửa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Dialog dialogthu = new Dialog(DanhSachThuChi.this.getActivity());
                        LayoutInflater li = (LayoutInflater) DanhSachThuChi.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View v = li.inflate(R.layout.suakhoanthu, parent, false);
                        dialogthu.setContentView(v);
                        dialogthu.setTitle("Sửa Khoản Thu");
                        dialogthu.show();
                        // link to layout
                        suatienthu = (EditText) dialogthu.findViewById(R.id.editTextsuathu_tienkhoanthu);
                        suanhomthu = (Spinner) dialogthu.findViewById(R.id.spinnersuathu_nhomkhoanthu);
                        suangaythu = (TextView) dialogthu.findViewById(R.id.textViewsuathu_ngaykhoanthu);
                        suaghichuthu = (EditText) dialogthu.findViewById(R.id.editTextsuathu_ghichukhoanthu);
                        dexuat();
                        // pick time
                        btnsuangaythu = (ImageButton) dialogthu.findViewById(R.id.imageButtonsuathu_chonngaykhoanthu);
                        btnsuangaythu.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                showDatePickerDialog();
                            }
                        });
                        // set value to dialog
                        suatienthu.setText(sapxepthu.get(position).getTien());
                        suangaythu.setText(sapxepthu.get(position).getNgaythang());
                        suaghichuthu.setText(sapxepthu.get(position).getGhichu());
                        adapterthu = new ArrayAdapter<>(DanhSachThuChi.this.getActivity(), android.R.layout.simple_spinner_dropdown_item, arrspinnerthu);
                        suanhomthu.setAdapter(adapterthu);
                        suanhomthu.setSelection(checkPosition(sapxepthu.get(position).getNhom(), arrspinnerthu));
                        // save button
                        btnsavethu = (ImageButton) dialogthu.findViewById(R.id.imageButtonsuathu_luukhoanthu);
                        btnsavethu.setOnClickListener(new OnClickListener() {

                            @SuppressWarnings("static-access")
                            @Override
                            public void onClick(View v) {
                                mDbthu = dbthu.getWritableDatabase();
                                ContentValues cv = new ContentValues();
                                String cost = suatienthu.getText().toString();
                                String date = suangaythu.getText().toString();
                                String type = suanhomthu.getSelectedItem().toString();
                                String note = suaghichuthu.getText().toString();
                                cv.put(dbThu.COL_TIEN, cost);
                                cv.put(dbThu.COL_DATE, date);
                                cv.put(dbThu.COL_NHOM, type);
                                cv.put(dbThu.COL_GHICHU, note);
                                mDbthu.update(dbThu.TABLE_NAME, cv, "_id " + "=" + sapxepthu.get(position).getId(), null);
                                danhSachThu();
                                dialogthu.cancel();
                            }
                        });

                    }
                });
                b.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDbthu = dbthu.getWritableDatabase();
                        String id = sapxepthu.get(position).getId();
                        mDbthu.delete(dbThu.TABLE_NAME, "_id=?", new String[]{id});
                        danhSachThu();
                    }
                });
                b.create().show();
            }
        });
        //su kien khi click vào listview chi
        listchi.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view,
                                    final int position, long id) {
                AlertDialog.Builder b = new AlertDialog.Builder(DanhSachThuChi.this.getActivity());
                b.setTitle("Lựa Chọn Của Bạn");
                b.setMessage("Bạn Muốn Gì?");
                b.setPositiveButton("Sửa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Dialog dialogchi = new Dialog(DanhSachThuChi.this.getActivity());
                        LayoutInflater li = (LayoutInflater) DanhSachThuChi.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View v = li.inflate(R.layout.suakhoanchi, parent, false);
                        dialogchi.setContentView(v);
                        dialogchi.setTitle("Sửa Khoản Chi");
                        dialogchi.show();
                        // link to layout
                        suatienchi = (EditText) dialogchi.findViewById(R.id.editTextsuachi_tienkhoanthu);
                        suanhomchi = (Spinner) dialogchi.findViewById(R.id.spinnersuachi_nhomkhoanthu);
                        suangaychi = (TextView) dialogchi.findViewById(R.id.textViewsuachi_ngaykhoanthu);
                        suaghichuchi = (EditText) dialogchi.findViewById(R.id.editTextsuachi_ghichukhoanthu);
                        dexuatchi();
                        // pick time
                        btnsuangaychi = (ImageButton) dialogchi.findViewById(R.id.imageButtonsuachi_chonngaykhoanthu);
                        btnsuangaychi.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                showDatePickerDialogchi();
                            }
                        });
                        // set value to dialog
                        suatienchi.setText(sapxepchi.get(position).getTien());
                        suangaychi.setText(sapxepchi.get(position).getNgaythang());
                        suaghichuchi.setText(sapxepchi.get(position).getGhichu());
                        adapterchi = new ArrayAdapter<>(DanhSachThuChi.this.getActivity(), android.R.layout.simple_spinner_dropdown_item, arrspinnerchi);
                        suanhomchi.setAdapter(adapterchi);
                        suanhomchi.setSelection(checkPosition(sapxepchi.get(position).getNhom(), arrspinnerchi));
                        // save button
                        btnsavechi = (ImageButton) dialogchi.findViewById(R.id.imageButtonsuachi_luukhoanthu);
                        btnsavechi.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                mDbchi = dbchi.getWritableDatabase();
                                ContentValues cv = new ContentValues();
                                cv.put(dbChi.COL_TIEN, suatienchi.getText().toString());
                                cv.put(dbChi.COL_DATE, suangaychi.getText().toString());
                                cv.put(dbChi.COL_NHOM, suanhomchi.getSelectedItem().toString());
                                cv.put(dbChi.COL_GHICHU, suaghichuchi.getText().toString());
                                mDbchi.update(dbChi.TABLE_NAME, cv, "_id " + "=" + sapxepchi.get(position).getId(), null);
                                danhSachThu();
                                dialogchi.cancel();
                                danhSachChi();
                            }
                        });
                    }
                });
                b.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDbchi = dbchi.getWritableDatabase();
                        String id = sapxepchi.get(position).getId();
                        mDbchi.delete(dbChi.TABLE_NAME, "_id=?", new String[]{id});
                        danhSachChi();
                    }
                });
                b.create().show();
            }
        });
        return view;
    }

    public class GetData extends AsyncTask<Void, Void, List<TienThuChi>> {

        @Override
        protected List<TienThuChi> doInBackground(Void... params) {
            mDbthu = dbthu.getReadableDatabase();
            String query = "select * from " + dbThu.TABLE_NAME;
            Cursor mCursorthu = mDbthu.rawQuery(query, null);
            ArrayList<TienThuChi> arrthu = new ArrayList<>();
            DoiNgay doingaythu = new DoiNgay();
            TienThuChi objectthu;
            if (mCursorthu.moveToFirst()) {
                do {
                    Log.i("I'm in", "true");
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
            sapxepthu = doingaythu.sapXep(arrthu);
            for (int i = 0; i < sapxepthu.size(); i++) {
                String strsapxep = doingaythu.ngay(sapxepthu.get(i).getNgaythang());
                sapxepthu.get(i).setNgaythang(strsapxep);
            }
            Log.i("Size1", arrthu.size() + "");
            Log.i("Size2", sapxepthu.size() + "");
            return sapxepthu;
        }

        @Override
        protected void onPostExecute(List<TienThuChi> result) {
            super.onPostExecute(result);
            myadapterthu = new DanhSachThu(DanhSachThuChi.this.getActivity(), R.layout.t_customlayout_thu, sapxepthu);
            listthu.setAdapter(myadapterthu);
            myadapterthu.notifyDataSetChanged();
        }
    }

    public void arrayChi() {
        SQLiteDatabase mChi = dbchi.getReadableDatabase();
        String queryChi = "select * from " + dbChi.TABLE_NAME;
        mCursorchi = mChi.rawQuery(queryChi, null);
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
        SQLiteDatabase mThu = dbthu.getReadableDatabase();
        String query = "select * from " + dbThu.TABLE_NAME;
        mCursorthu = mThu.rawQuery(query, null);
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

    public void dexuat() {
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
    public void showDatePickerDialogchi() {
        DialogFragment newFragment = new DatePickerFragmentchi();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    //datepicker thu
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void loadTab(View view) {
        TabHost tab = (TabHost) view.findViewById(android.R.id.tabhost);
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

    public void danhSachThu() {
        mDbthu = dbthu.getReadableDatabase();
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

        sapxepthu = doingaythu.sapXep(arrthu);
        for (int i = 0; i < sapxepthu.size(); i++) {
            String strsapxep = doingaythu.ngay(sapxepthu.get(i).getNgaythang());
            sapxepthu.get(i).setNgaythang(strsapxep);
        }
        myadapterthu = new DanhSachThu(DanhSachThuChi.this.getActivity(), R.layout.t_customlayout_thu, sapxepthu);
        listthu.setAdapter(myadapterthu);
        myadapterthu.notifyDataSetChanged();
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

        sapxepchi = doingaychi.sapXep(arrchi);
        for (int i = 0; i < sapxepchi.size(); i++) {
            String strsapxep = doingaychi.ngay(sapxepchi.get(i).getNgaythang());
            sapxepchi.get(i).setNgaythang(strsapxep);
        }
        myadapterchi = new DanhSachChi(DanhSachThuChi.this.getActivity(), R.layout.t_customlayout_chi, sapxepchi);
        listchi.setAdapter(myadapterchi);
        myadapterchi.notifyDataSetChanged();
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
        @SuppressWarnings("deprecation")
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
}
