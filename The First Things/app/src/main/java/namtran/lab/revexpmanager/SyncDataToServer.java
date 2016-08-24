package namtran.lab.revexpmanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.firebase.client.Firebase;

import namtran.lab.database.InDb;
import namtran.lab.database.InterestDb;
import namtran.lab.database.OutDb;
import namtran.lab.entity.BankingItem;
import namtran.lab.entity.TransactionsItem;
import namtran.lab.entity.UserInfo;

/**
 * Created by namtr on 19/08/2016.
 */
public class SyncDataToServer extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private ProgressDialog mProDialog;
    private SQLiteDatabase mSQLiteIn, mSQLiteOut, mSQLiteRate;
    private Firebase mUserRef;
    private UserInfo mUserInfo;
    private View mView;

    public SyncDataToServer(Context context, View view) {
        this.mContext = context;
        this.mView = view;
        getUserInfo();
        Firebase.setAndroidContext(context);
        Firebase root = new Firebase("https://expenseproject.firebaseio.com/");
        InDb inDb = new InDb(context);
        OutDb outDb = new OutDb(context);
        InterestDb interestDb = new InterestDb(context);
        mSQLiteIn = inDb.getWritableDatabase();
        mSQLiteOut = outDb.getWritableDatabase();
        mSQLiteRate = interestDb.getWritableDatabase();
        mUserRef = root.child(mUserInfo.getUid());
    }

    // Lấy thông tin người dùng
    private void getUserInfo() {
        SharedPreferences pref = mContext.getSharedPreferences(UserInfo.PREF_NAME, Context.MODE_PRIVATE);
        String email = pref.getString(UserInfo.KEY_EMAIL, null);
        String uid = pref.getString(UserInfo.KEY_UID, null);
        mUserInfo = new UserInfo(email, uid);
    }

    @Override
    protected void onPreExecute() {
        mProDialog = new ProgressDialog(mContext);
        mProDialog.setTitle("Đồng Bộ");
        mProDialog.setMessage("Đang đồng bộ dữ liệu...");
        mProDialog.setIndeterminate(true);
        mProDialog.setCancelable(false);
        mProDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d("Sync", "doing");
        TransactionsItem transItem;
        String id;
        Firebase expenseRef = mUserRef.child("Expense");
        Firebase incomeRef = mUserRef.child("Income");
        Firebase rateRef = mUserRef.child("Interest Rate");
        expenseRef.setValue(null);
        incomeRef.setValue(null);
        rateRef.setValue(null);

        // Tiền thu
        String queryIn = "select * from " + InDb.TABLE_NAME;
        Cursor cursorIn = mSQLiteIn.rawQuery(queryIn, null);
        if (cursorIn.moveToFirst()) { // Thu
            do {
                id = cursorIn.getString(0);
                String cost = cursorIn.getString(1);
                String type = cursorIn.getString(2);
                String note = cursorIn.getString(3);
                String date = cursorIn.getString(4);
                transItem = new TransactionsItem(cost, type, note, date, id);
                incomeRef.child(id).setValue(transItem);
            } while (cursorIn.moveToNext());
        }
        cursorIn.close();

        // Tiền chi
        String queryOut = "select * from " + OutDb.TABLE_NAME;
        Cursor cursorOut = mSQLiteOut.rawQuery(queryOut, null);
        if (cursorOut.moveToFirst()) { // Thu
            do {
                id = cursorOut.getString(0);
                String cost = cursorOut.getString(1);
                String type = cursorOut.getString(2);
                String note = cursorOut.getString(3);
                String date = cursorOut.getString(4);
                transItem = new TransactionsItem(cost, type, note, date, id);
                expenseRef.child(id).setValue(transItem);
            } while (cursorOut.moveToNext());
        }
        cursorOut.close();

        // Tài khoản ngân hàng
        String queryInterest = "select * from " + InterestDb.TABLE_NAME;
        Cursor cursorInts = mSQLiteRate.rawQuery(queryInterest, null);
        if (cursorInts.moveToFirst()) {
            do {
                id = cursorInts.getString(0);
                String name = cursorInts.getString(1);
                String money = cursorInts.getString(2);
                String rate = cursorInts.getString(3);
                String date = cursorInts.getString(4);
                BankingItem bankItem = new BankingItem(name, money, rate, date, id);
                rateRef.child(id).setValue(bankItem);
            } while (cursorInts.moveToNext());
        }
        cursorInts.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mProDialog.isShowing()) {
                    mProDialog.dismiss();
                }
                Snackbar.make(mView, "Đồng bộ thành công", Snackbar.LENGTH_SHORT).show();
            }
        }, 1000);
    }
}
