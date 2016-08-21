package namtran.lab.transaction;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

import namtran.lab.database.InDb;
import namtran.lab.database.OutDb;
import namtran.lab.revexpmanager.R;

/**
 * Created by namtr on 18/08/2016.
 */
public class AddItemActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etCost, etNote;
    private TextView tvDate, title;
    private Spinner typeSpinner;
    private static final String[] ARRAY_TYPE_IN = {"Tiền Lương", "Đòi Nợ", "Bán Đồ", "Đi Vay", "Được Tặng", "Tiền Thưởng", "Khác"};
    private static final String[] ARRAY_TYPE_OUT = {"Ăn Uống", "Mua Sắm", "Sinh Hoạt", "Di Chuyển", "Sức Khoẻ", "Giải Trí", "Du Lịch", "Cho vay", "Trả Nợ", "Khác"};
    private SQLiteDatabase sqlIn;
    private SQLiteDatabase sqlOut;
    private View rootView;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        initControl();
        InDb inDb = new InDb(this);
        sqlIn = inDb.getWritableDatabase();
        OutDb outDb = new OutDb(this);
        sqlOut = outDb.getWritableDatabase();
    }

    private void initControl() {
        rootView = findViewById(R.id.add_exp_layout);
        title = (TextView) findViewById(R.id.title_add_layout);
        etCost = (EditText) findViewById(R.id.et_cost_out);
        etNote = (EditText) findViewById(R.id.et_note_out);
        tvDate = (TextView) findViewById(R.id.tv_date_out);
        ImageButton btnSelectDate = (ImageButton) findViewById(R.id.btn_select_date_out);
        btnSelectDate.setOnClickListener(this);
        typeSpinner = (Spinner) findViewById(R.id.spinner_type_out);
        ImageButton btnSave = (ImageButton) findViewById(R.id.btn_save_out);
        btnSave.setOnClickListener(this);
        showDefaultDate();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
        initForEachType(adapter);
    }

    private void initForEachType(ArrayAdapter<String> adapter) {
        switch (TransactionsFragment.currentPage) {
            case 0:
                title.setText(getResources().getString(R.string.title_in));
                etCost.setHint(getResources().getString(R.string.hint_in));
                adapter.addAll(ARRAY_TYPE_IN);
                adapter.notifyDataSetChanged();
                break;
            case 1:
                title.setText(getResources().getString(R.string.title_out));
                etCost.setHint(getResources().getString(R.string.hint_out));
                adapter.addAll(ARRAY_TYPE_OUT);
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
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
            if (TransactionsFragment.currentPage == 0) {
                sqlIn.insert(InDb.TABLE_NAME, null, cv);
                Log.d("Insert In", cv.toString());
            } else if (TransactionsFragment.currentPage == 1) {
                sqlOut.insert(OutDb.TABLE_NAME, null, cv);
                Log.d("Insert Out", cv.toString());
            }
            etCost.setText("");
            etNote.setText("");
            Snackbar.make(rootView, "Thêm giao dịch thành công", Snackbar.LENGTH_SHORT).show();
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
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerFragment(this, callback, year, month, day).show();
    }

    DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(year, monthOfYear, dayOfMonth);
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
    };

    public class DatePickerFragment extends DatePickerDialog {

        public DatePickerFragment(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
            super(context, callBack, year, monthOfYear, dayOfMonth);
        }

        @Override
        public void setTitle(CharSequence title) {
            super.setTitle("Chọn ngày diễn ra");
        }
    }
}
