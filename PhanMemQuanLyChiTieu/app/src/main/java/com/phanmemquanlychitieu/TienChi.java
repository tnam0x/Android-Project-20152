package com.phanmemquanlychitieu;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import Database.dbChi;

public class TienChi extends Activity {
    private EditText sotienkhoanchi, ghichukhoanchi;
    private Spinner nhomkhoanchi;
    private TextView ngaykhoanchi;
    private ImageButton luukhoanchi;
    private dbChi dbchi;
    private SQLiteDatabase mDbchi;
    private String[] arrspinner = {"Ăn Uống", "Quần Áo", "Cho vay", "Sinh Hoạt", "Đi Lại", "Trả Nợ", "Khác"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhapkhoanchi);

        sotienkhoanchi = (EditText) findViewById(R.id.editText_tienkhoanchi);
        ghichukhoanchi = (EditText) findViewById(R.id.editText_ghichukhoanchi);
        nhomkhoanchi = (Spinner) findViewById(R.id.spinner_nhomkhoanchi);
        ngaykhoanchi = (TextView) findViewById(R.id.textView_ngaykhoanchi);
        ImageButton chonngaykhoanchi = (ImageButton) findViewById(R.id.imageButton_chonngaykhoanchi);
        luukhoanchi = (ImageButton) findViewById(R.id.imageButton_luukhoanchi);

        ArrayAdapter<String> adapterchi = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrspinner);
        nhomkhoanchi.setAdapter(adapterchi);
        dbchi = new dbChi(this);
        dexuat();
        chonngaykhoanchi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(null);
            }
        });
        luu();
    }

    public void dexuat() {
        Calendar ngaythang = Calendar.getInstance();
        //dinh dang 12h
        String dinhdang24 = "dd/MM/yyyy";
        SimpleDateFormat dinhdang;
        dinhdang = new SimpleDateFormat(dinhdang24, Locale.getDefault());
        ngaykhoanchi.setText(dinhdang.format(ngaythang.getTime()));
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void luu() {
        luukhoanchi.setOnClickListener(new OnClickListener() {
            @SuppressLint("NewApi")
            @SuppressWarnings("static-access")
            @Override
            public void onClick(View v) {
                if (sotienkhoanchi.getText().toString().isEmpty() || checkZero(sotienkhoanchi.getText().toString())) {
                    Toast toast = Toast.makeText(TienChi.this, "Bạn Chưa Nhập Tiền", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    mDbchi = dbchi.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put(dbChi.COL_TIEN, sotienkhoanchi.getText().toString());
                    cv.put(dbChi.COL_NHOM, nhomkhoanchi.getSelectedItem().toString());
                    cv.put(dbChi.COL_GHICHU, ghichukhoanchi.getText().toString());
                    cv.put(dbChi.COL_DATE, ngaykhoanchi.getText().toString());
                    mDbchi.insert(dbChi.TABLE_NAME, null, cv);

                    // reset view
                    sotienkhoanchi.setText(null);
                    ghichukhoanchi.setText(null);
                    Toast toast = Toast.makeText(TienChi.this, "Nhập Thành Công", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    public boolean checkZero(String s) {
        try {
            Float f = Float.parseFloat(s);
            if (f == 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
            String datetimeloc;
            if (day < 10) {
                if (month < 10) {
                    datetimeloc = "0" + String.valueOf(day) + "/" + "0" + String.valueOf(month) + "/" + String.valueOf(year);
                } else {
                    datetimeloc = "0" + String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
                }
            } else {
                if (month < 10) {
                    datetimeloc = String.valueOf(day) + "/" + "0" + String.valueOf(month) + "/" + String.valueOf(year);
                } else {
                    datetimeloc = String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
                }
            }
            ngaykhoanchi.setText(datetimeloc);
        }
    }

}
