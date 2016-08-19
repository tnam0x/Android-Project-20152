package namtran.lab.revexpmanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import namtran.lab.database.OutDb;

/**
 * Created by namtr on 18/08/2016.
 */
public class AddItemActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etCost, etNote;
    private static TextView tvDate;
    private Spinner typeSpinner;
    private static final String[] ARRAY_TYPE_SPINNER = {"Ăn Uống", "Mua Sắm", "Cho vay", "Sinh Hoạt", "Di Chuyển", "Sức Khoẻ", "Trả Nợ", "Khác"};
    private SQLiteDatabase sqlOut;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_out);
        initControl();
        OutDb outDb = new OutDb(this);
        sqlOut = outDb.getWritableDatabase();
    }

    private void initControl() {
        rootView = findViewById(R.id.add_exp_layout);
        etCost = (EditText) findViewById(R.id.et_cost_out);
        etNote = (EditText) findViewById(R.id.et_note_out);
        tvDate = (TextView) findViewById(R.id.tv_date_out);
        ImageButton btnSelectDate = (ImageButton) findViewById(R.id.btn_select_date_out);
        btnSelectDate.setOnClickListener(this);
        typeSpinner = (Spinner) findViewById(R.id.spinner_type_out);
        ImageButton btnSave = (ImageButton) findViewById(R.id.btn_save_out);
        btnSave.setOnClickListener(this);
        showDefaultDate();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ARRAY_TYPE_SPINNER);
        typeSpinner.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_select_date_out:
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
        String cost = etCost.getText().toString();
        if (cost.isEmpty() || checkZero(cost)) {
            etCost.requestFocus();
            Snackbar.make(rootView, "Bạn chưa nhập số tiền", Snackbar.LENGTH_SHORT).show();
        } else {
            ContentValues cv = new ContentValues();
            cv.put(OutDb.COL_COST, cost);
            cv.put(OutDb.COL_TYPE, typeSpinner.getSelectedItem().toString());
            cv.put(OutDb.COL_NOTE, etNote.getText().toString().trim());
            cv.put(OutDb.COL_DATE, tvDate.getText().toString());
            long row = sqlOut.insert(OutDb.TABLE_NAME, null, cv);
            sqlOut.close();
            etCost.setText("");
            etNote.setText("");
            Log.d("Insert Out", row + ":" + cv.toString());
            Snackbar.make(rootView, "Thêm khoản chi thành công", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void showDefaultDate() {
        Calendar calendar = Calendar.getInstance();
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());
        tvDate.setText(format.format(calendar.getTime()));
    }

    private boolean checkZero(String s) {
        try {
            Float f = Float.parseFloat(s);
            if (f == 0) {
                return true;
            }
        } catch (NumberFormatException e) {
            Snackbar.make(rootView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }

    private void showDatePicker() {
        DialogFragment dialog = new DatePickerFragment();
        dialog.show(getFragmentManager(), "DatePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
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
            tvDate.setText(date);
        }
    }
}
