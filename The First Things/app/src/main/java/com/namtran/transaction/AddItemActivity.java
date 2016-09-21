package com.namtran.transaction;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.namtran.database.InDb;
import com.namtran.database.OutDb;
import com.namtran.main.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by namtr on 18/08/2016.
 * Thêm khoản thu hoặc chi.
 */
public class AddItemActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String[] ARRAY_TYPE_IN = {"Tiền Lương", "Đòi Nợ", "Bán Đồ", "Đi Vay", "Được Tặng", "Tiền Thưởng", "Khác"};
    private static final String[] ARRAY_TYPE_OUT = {"Ăn Uống", "Mua Sắm", "Sinh Hoạt", "Di Chuyển", "Sức Khoẻ", "Giáo Dục", "Giải Trí", "Du Lịch", "Cho vay", "Trả Nợ", "Khác"};
    private EditText costField, noteField;
    private TextView mDateTextView, mTitleTextView;
    private Spinner mTypeSpinner;
    private SQLiteDatabase mSQLiteIn;
    private SQLiteDatabase mSQLiteOut;
    private View mView;
    private Calendar mCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(year, monthOfYear, dayOfMonth);
            monthOfYear = monthOfYear + 1;
            String date, day, month;
            if (dayOfMonth < 10) {
                day = "0" + dayOfMonth;
            } else {
                day = dayOfMonth + "";
            }
            if (monthOfYear < 10) {
                month = "0" + monthOfYear;
            } else {
                month = monthOfYear + "";
            }
            date = day + "/" + month + "/" + year;
            mDateTextView.setText(date);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        initControl();
        InDb inDb = new InDb(this);
        mSQLiteIn = inDb.getWritableDatabase();
        OutDb outDb = new OutDb(this);
        mSQLiteOut = outDb.getWritableDatabase();
    }

    private void initControl() {
        mView = findViewById(R.id.add_exp_layout);
        mTitleTextView = (TextView) findViewById(R.id.title_add_layout);
        mDateTextView = (TextView) findViewById(R.id.tv_date_out);
        costField = (EditText) findViewById(R.id.et_cost_out);
        noteField = (EditText) findViewById(R.id.et_note_out);
        mTypeSpinner = (Spinner) findViewById(R.id.spinner_type_out);
        ImageButton btnSelectDate = (ImageButton) findViewById(R.id.btn_select_date_banking);
        ImageButton btnSave = (ImageButton) findViewById(R.id.btn_save_out);
        btnSelectDate.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        showDefaultDate();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner.setAdapter(adapter);
        initForEachType(adapter);
    }

    private void initForEachType(ArrayAdapter<String> spinnerAdapter) {
        switch (TransactionsFragment.mCurrentPage) {
            case 0:
                mTitleTextView.setText(getResources().getString(R.string.title_in));
                costField.setHint(getResources().getString(R.string.et_hint_in));
                spinnerAdapter.addAll(ARRAY_TYPE_IN);
                spinnerAdapter.notifyDataSetChanged();
                break;
            case 1:
                mTitleTextView.setText(getResources().getString(R.string.title_out));
                costField.setHint(getResources().getString(R.string.et_hint_out));
                spinnerAdapter.addAll(ARRAY_TYPE_OUT);
                spinnerAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_select_date_banking:
                showDatePicker();
                break;
            case R.id.btn_save_out:
                saveData();
                break;
            default:
                break;
        }
    }

    private void saveData() {
        String cost = costField.getText().toString();
        if (cost.isEmpty() || checkZero(cost)) {
            costField.requestFocus();
            Snackbar.make(mView, "Bạn chưa nhập số tiền", Snackbar.LENGTH_SHORT).show();
        } else {
            ContentValues cv = new ContentValues();
            cv.put(OutDb.COL_COST, cost);
            cv.put(OutDb.COL_TYPE, mTypeSpinner.getSelectedItem().toString());
            cv.put(OutDb.COL_NOTE, noteField.getText().toString().trim());
            cv.put(OutDb.COL_DATE, mDateTextView.getText().toString());
            if (TransactionsFragment.mCurrentPage == 0) {
                mSQLiteIn.insert(InDb.TABLE_NAME, null, cv);
            } else if (TransactionsFragment.mCurrentPage == 1) {
                mSQLiteOut.insert(OutDb.TABLE_NAME, null, cv);
            }
            costField.setText("");
            noteField.setText("");
            Snackbar.make(mView, "Thêm giao dịch thành công", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void showDefaultDate() {
        Calendar calendar = Calendar.getInstance();
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());
        mDateTextView.setText(format.format(calendar.getTime()));
    }

    private boolean checkZero(String s) {
        try {
            Float f = Float.parseFloat(s);
            if (f == 0) {
                return true;
            }
        } catch (NumberFormatException e) {
            Snackbar.make(mView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }

    private void showDatePicker() {
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        DatePickerFragment pickerFragment = new DatePickerFragment(this, callback, year, month, day);
        pickerFragment.show();
    }

    private class DatePickerFragment extends DatePickerDialog {

        DatePickerFragment(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
            super(context, callBack, year, monthOfYear, dayOfMonth);
        }

        @Override
        public void setTitle(CharSequence title) {
            super.setTitle("Chọn ngày diễn ra");
        }
    }
}
