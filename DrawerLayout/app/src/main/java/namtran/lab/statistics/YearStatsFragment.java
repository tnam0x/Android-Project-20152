package namtran.lab.statistics;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import namtran.lab.database.InDb;
import namtran.lab.database.OutDb;
import namtran.lab.entity.Item;
import namtran.lab.entity.ParseDate;
import namtran.lab.revexpmanager.R;

/**
 * Created by namtr on 20/08/2016.
 */
public class YearStatsFragment extends Fragment {
    private static TextView tvYear;
    private static TextView tvRev;
    private static TextView tvExp;
    private ProgressDialog dialog;
    private SQLiteDatabase sqlOut, sqlIn;
    private static ArrayList<Item> in;
    private static ArrayList<Item> out;
    private static NumberFormat format;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_year, container, false);
        ImageButton btnSelectDate = (ImageButton) rootView.findViewById(R.id.btn_select_date_year);
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(0);
        tvYear = (TextView) rootView.findViewById(R.id.tv_year);
        tvRev = (TextView) rootView.findViewById(R.id.tv_in_year);
        tvExp = (TextView) rootView.findViewById(R.id.tv_out_year);
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

    private static void getItemYear(String date) {
        String year = date;
        if (date.length() != 4) {
            year = ParseDate.parseYear(date);
        }
        Log.d("Year", year);
        int costIn = 0, costOut = 0;
        for (Item item : in) {
            if (year.equals(ParseDate.parseYear(item.getDate()))) {
                costIn += Integer.parseInt(item.getCost());
            }
        }
        for (Item item : out) {
            if (year.equals(ParseDate.parseYear(item.getDate()))) {
                costOut += Integer.parseInt(item.getCost());
            }
        }
        tvYear.setText(year);
        tvRev.setText(format.format(costIn));
        tvExp.setText(format.format(costOut));
    }

    private String today() {
        Calendar calendar = Calendar.getInstance();
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());
        return format.format(calendar.getTime());
    }

    private void showDatePicker() {
        DialogFragment dialog = new DatePickerFragment();
        dialog.show(getFragmentManager(), "YearPicker");
    }

    private class RetrieveData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
            Log.d("YearStats", "getting data");
            in = new ArrayList<>();
            out = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
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
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            pickerDialog.setTitle("Chọn Năm Muốn Xem");
            try {
                java.lang.reflect.Field[] datePickerDialogFields = pickerDialog.getClass().getDeclaredFields();
                for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                    if (datePickerDialogField.getName().equals("mDatePicker")) {
                        datePickerDialogField.setAccessible(true);
                        DatePicker datePicker = (DatePicker) datePickerDialogField.get(pickerDialog);
                        java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                        for (java.lang.reflect.Field datePickerField : datePickerFields) {
                            Log.i("datePickerField", datePickerField.getName());
                            if ("mDaySpinner".equals(datePickerField.getName())) {
                                datePickerField.setAccessible(true);
                                Object dayPicker = datePickerField.get(datePicker);
                                ((View) dayPicker).setVisibility(View.GONE);
                            }
                            if ("mMonthSpinner".equals(datePickerField.getName())) {
                                datePickerField.setAccessible(true);
                                Object monthPicker = datePickerField.get(datePicker);
                                ((View) monthPicker).setVisibility(View.GONE);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                Log.d("Exception", ex.getMessage());
            }
            return pickerDialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            getItemYear(String.valueOf(year));
        }
    }
}
