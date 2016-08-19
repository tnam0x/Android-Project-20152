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
import namtran.lab.database.OutDb;
import namtran.lab.entity.Item;
import namtran.lab.entity.UserInfo;

/**
 * Created by namtr on 19/08/2016.
 */
public class SyncDataToServer extends AsyncTask<Void, Void, Void> {
    private Context context;
    private ProgressDialog dialog;
    private SQLiteDatabase sqlIn;
    private SQLiteDatabase sqlOut;
    private Firebase userRef;
    private UserInfo userInfo;
    private View view;

    public SyncDataToServer(Context context, View view) {
        this.context = context;
        this.view = view;
        getUserInfo();
        Firebase.setAndroidContext(context);
        Firebase root = new Firebase("https://expenseproject.firebaseio.com/");
        InDb inDb = new InDb(context);
        OutDb outDb = new OutDb(context);
        sqlIn = inDb.getWritableDatabase();
        sqlOut = outDb.getWritableDatabase();
        userRef = root.child(userInfo.getUid());
    }

    // Lấy thông tin người dùng
    private void getUserInfo() {
        SharedPreferences pref = context.getSharedPreferences(UserInfo.PREF_NAME, Context.MODE_PRIVATE);
        String email = pref.getString(UserInfo.KEY_EMAIL, null);
        String uid = pref.getString(UserInfo.KEY_UID, null);
        userInfo = new UserInfo(email, uid);
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setTitle("Đồng Bộ");
        dialog.setMessage("Đang đồng bộ dữ liệu...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d("Sync", "doing");
        Item item;
        int id;
        Firebase expenseRef = userRef.child("Expense");
        Firebase incomeRef = userRef.child("Income");
        expenseRef.setValue(null);
        incomeRef.setValue(null);

        // Tiền thu
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
                incomeRef.child("" + item.getId()).setValue(item);
            } while (cursorIn.moveToNext());
        }
        cursorIn.close();

        // Tiền chi
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
                expenseRef.child("" + item.getId()).setValue(item);
            } while (cursorOut.moveToNext());
        }
        cursorOut.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Snackbar.make(view, "Đồng bộ thành công", Snackbar.LENGTH_SHORT).show();
            }
        }, 1000);
    }
}
