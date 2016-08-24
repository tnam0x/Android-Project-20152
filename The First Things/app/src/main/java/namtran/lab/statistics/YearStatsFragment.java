package namtran.lab.statistics;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import namtran.lab.database.InDb;
import namtran.lab.database.OutDb;
import namtran.lab.entity.CurrencyParser;
import namtran.lab.entity.DateParser;
import namtran.lab.entity.TransactionsItem;
import namtran.lab.revexpmanager.R;

/**
 * Created by namtr on 20/08/2016.
 */
public class YearStatsFragment extends Fragment {
    private static TextView mYearTextView;
    private static TextView mRevTextView;
    private static TextView mExpTextView;
    private ProgressDialog mProDialog;
    private SQLiteDatabase  mSQLiteIn, mSQLiteOut;
    private static ArrayList<TransactionsItem> mListTransIn;
    private static ArrayList<TransactionsItem> mListTransOut;
    private static CurrencyParser mParser;
    private Calendar mCalendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_year, container, false);
        ImageButton btnSelectDate = (ImageButton) view.findViewById(R.id.btn_select_date_year);
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showYearPicker();
            }
        });
        // Định dạng tiền tệ
        mParser = new CurrencyParser();
        // control
        mYearTextView = (TextView) view.findViewById(R.id.tv_year);
        mRevTextView = (TextView) view.findViewById(R.id.tv_in_year);
        mExpTextView = (TextView) view.findViewById(R.id.tv_out_year);
        // database
        InDb inDb = new InDb(getContext());
        OutDb outDb = new OutDb(getContext());
        mSQLiteIn = inDb.getWritableDatabase();
        mSQLiteOut = outDb.getWritableDatabase();
        // Tạo dialog hiển thị khi đang khởi tạo dữ liệu
        mProDialog = new ProgressDialog(getContext());
        mProDialog.setMessage("Đang tải");
        mProDialog.setCancelable(false);
        // Lấy dữ liệu
        new RetrieveData().execute();
        return view;
    }

    // Lấy thu chi theo năm đã chọn, mặc định là năm hiện tại
    private static void getItemYear(String date) {
        String year = date;
        if (date.length() != 4) {
            year = DateParser.parseYear(date);
        }
        Log.d("Year", year);
        int costIn = 0, costOut = 0;
        for (TransactionsItem transactionsItem : mListTransIn) {
            if (year.equals(DateParser.parseYear(transactionsItem.getDate()))) {
                costIn += Integer.parseInt(transactionsItem.getCost());
            }
        }
        for (TransactionsItem transactionsItem : mListTransOut) {
            if (year.equals(DateParser.parseYear(transactionsItem.getDate()))) {
                costOut += Integer.parseInt(transactionsItem.getCost());
            }
        }
        mYearTextView.setText(year);
        mRevTextView.setText(mParser.format(costIn));
        mExpTextView.setText(mParser.format(costOut));
    }

    //
    private String today() {
        Calendar calendar = Calendar.getInstance();
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());
        return format.format(calendar.getTime());
    }

    private void showYearPicker() {
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        YearPickerFragment pickerFragment = new YearPickerFragment(getContext(), callback, year, month, day);
        pickerFragment.setCancelable(true);
        hideDateAndMonthFields(pickerFragment.getDatePicker());
        pickerFragment.show();
    }

    private void hideDateAndMonthFields(DatePicker datePicker) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(">LOLLIPOP", "yes");
            int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
            Log.d(">LOLLIPOP", daySpinnerId + "");
            if (daySpinnerId != 0) {
                View daySpinner = datePicker.findViewById(daySpinnerId);
                if (daySpinner != null) {
                    Log.d(">LOLLIPOP", "day");
                    daySpinner.setVisibility(View.GONE);
                }
            }

            int monthSpinnerId = Resources.getSystem().getIdentifier("month", "id", "android");
            if (monthSpinnerId != 0) {
                View monthSpinner = datePicker.findViewById(monthSpinnerId);
                if (monthSpinner != null) {
                    Log.d(">LOLLIPOP", "month");
                    monthSpinner.setVisibility(View.GONE);
                }
            }
        } else { // Older SDK versions
            Field f[] = datePicker.getClass().getDeclaredFields();
            for (Field field : f) {
                Log.d("Older SDK versions", field.getName());
                if (field.getName().equals("mDayPicker") || field.getName().equals("mDaySpinner")) {
                    field.setAccessible(true);
                    try {
                        Log.d("mDaySpinner", "yeah");
                        Object dayPicker = field.get(datePicker);
                        ((View) dayPicker).setVisibility(View.GONE);
                    } catch (IllegalAccessException e) {
                        Log.e("Error", e.getMessage());
                    }
                }

                if (field.getName().equals("mMonthPicker") || field.getName().equals("mMonthSpinner")) {
                    field.setAccessible(true);
                    try {
                        Log.d("mMonthSpinner", "yeah");
                        Object monthPicker = field.get(datePicker);
                        ((View) monthPicker).setVisibility(View.GONE);
                    } catch (IllegalAccessException e) {
                        Log.e("Error", e.getMessage());
                    }
                }

            }
        }
    }

    private DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(year, monthOfYear, dayOfMonth);
            getItemYear(String.valueOf(year));
        }
    };

    private class YearPickerFragment extends DatePickerDialog {

        public YearPickerFragment(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
            super(context, callBack, year, monthOfYear, dayOfMonth);
        }

        @Override
        public void setTitle(CharSequence title) {
            super.setTitle("Chọn năm muốn xem");
        }
    }

    // Lấy dữ liệu từ database lưu vào Arraylist
    private class RetrieveData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProDialog.show();
            Log.d("YearStats", "getting data");
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
            getItemYear(today());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("YearStats", "done");
            if (mProDialog.isShowing()) {
                mProDialog.dismiss();
            }
        }
    }

}
