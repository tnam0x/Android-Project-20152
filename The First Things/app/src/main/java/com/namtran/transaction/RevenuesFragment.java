package com.namtran.transaction;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.namtran.adapter.TransactionsRecyclerAdapter;
import com.namtran.database.BankAccountDb;
import com.namtran.database.InDb;
import com.namtran.database.OutDb;
import com.namtran.entity.BankItem;
import com.namtran.entity.CurrencyParser;
import com.namtran.entity.TransactionsItem;
import com.namtran.entity.UserInfo;
import com.namtran.main.R;

import java.util.ArrayList;

/**
 * Created by namtr on 15/08/2016.
 * Danh sách thu.
 */
public class RevenuesFragment extends Fragment {
    private DatabaseReference mFirebase;
    private SQLiteDatabase mSQLiteIn, mSQLiteOut, mSQLiteRate;
    private ArrayList<TransactionsItem> mListItem;
    private TransactionsRecyclerAdapter mAdapter;
    private ProgressDialog mProDialog;
    private UserInfo mUserInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_list_item, container, false);
        getUserInfo();
        // Khởi tạo firebase
        mFirebase = FirebaseDatabase.getInstance().getReference();
        // Khởi tạo databse
        InDb inDb = new InDb(getContext());
        OutDb outDb = new OutDb(getContext());
        BankAccountDb interestDb = new BankAccountDb(getContext());
        mSQLiteIn = inDb.getWritableDatabase();
        mSQLiteOut = outDb.getWritableDatabase();
        mSQLiteRate = interestDb.getWritableDatabase();
        // Lưu danh sách thu
        mListItem = new ArrayList<>();
        setupRecyclerView(recyclerView);
        // Lấy dữ liệu đổ lên listview
        initDialog();
        if (isDbEmpty()) {
            new GetDataFromServer().execute();
        } else {
            new GetDataFromDb().execute();
        }
        return recyclerView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new TransactionsRecyclerAdapter(mListItem, true, new RecyclerListener(recyclerView));
        recyclerView.setAdapter(mAdapter);
    }

    // Lấy thông tin người dùng
    private void getUserInfo() {
        SharedPreferences pref = getContext().getSharedPreferences(UserInfo.PREF_NAME, Context.MODE_PRIVATE);
        String name = pref.getString(UserInfo.KEY_NAME, "User name");
        String email = pref.getString(UserInfo.KEY_EMAIL, null);
        String uid = pref.getString(UserInfo.KEY_UID, null);
        mUserInfo = new UserInfo(name, email, uid);
    }

    // Hiển thị dialog khi lấy dữ liệu
    private void initDialog() {
        mProDialog = new ProgressDialog(getContext());
        mProDialog.setMessage("Đang tải dữ liệu...");
        mProDialog.setCancelable(false);
    }

    // Kiểm tra xem databse có dữ liệu hay chưa
    private boolean isDbEmpty() {
        Cursor cursorIn = mSQLiteIn.rawQuery("select * from " + InDb.TABLE_NAME, null);
        Cursor cursorOut = mSQLiteOut.rawQuery("select * from " + OutDb.TABLE_NAME, null);
        boolean isEmpty = cursorIn.moveToFirst() && cursorOut.moveToFirst();
        cursorIn.close();
        cursorOut.close();
        return !isEmpty;
    }

    private class RecyclerListener implements View.OnClickListener {
        private RecyclerView mRecyclerView;
        private CurrencyParser mParser;

        RecyclerListener(RecyclerView recyclerView) {
            this.mRecyclerView = recyclerView;
            mParser = new CurrencyParser();
        }

        @Override
        public void onClick(View view) {
            int position = mRecyclerView.getChildLayoutPosition(view);
            TransactionsItem item = mListItem.get(position);
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("Thông Tin Khoản Thu").setCancelable(true);
            StringBuilder msg = new StringBuilder();
            msg.append("Tiền: ").append(mParser.format(item.getCost()));
            msg.append("\nNhóm: ").append(item.getType());
            msg.append("\nNgày: ").append(item.getDate());
            msg.append("\nGhi Chú: ").append(item.getNote().isEmpty() ? "Trống" : item.getNote());
            dialog.setMessage(msg);
            dialog.setNegativeButton("OK", null);
            dialog.show();
        }
    }

    // Lấy dữ liệu từ Database rồi hiển thị lên listview
    private class GetDataFromDb extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
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
                    mListItem.add(item);
                } while (cursorIn.moveToNext());
            }
            cursorIn.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Nếu UI is dead thì không làm gì cả
            if (getActivity() == null) {
                return;
            }
            mAdapter.notifyDataSetChanged();
            mProDialog.dismiss();
        }
    }

    // Lấy dữ liệu từ server, lưu vào database và hiển thị lên listview
    private class GetDataFromServer extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Tiền thu
            DatabaseReference refIncome = mFirebase.child(mUserInfo.getUid()).child("Income");
            final Query inQuery = refIncome.orderByValue();
            inQuery.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    TransactionsItem item;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        item = snapshot.getValue(TransactionsItem.class);
                        ContentValues values = new ContentValues();
                        values.put(InDb.COL_COST, item.getCost());
                        values.put(InDb.COL_TYPE, item.getType());
                        values.put(InDb.COL_NOTE, item.getNote());
                        values.put(InDb.COL_DATE, item.getDate());
                        mSQLiteIn.insert(InDb.TABLE_NAME, null, values);
                        mListItem.add(item);
                    }
                    // Nếu UI is dead thì không làm gì cả
                    if (getActivity() == null) {
                        return;
                    }
                    mSQLiteIn.close();
                    inQuery.removeEventListener(this);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            DatabaseReference interestRef = mFirebase.child(mUserInfo.getUid()).child("Interest Rate");
            final Query intsQuery = interestRef.orderByValue();
            intsQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    BankItem item;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        item = snapshot.getValue(BankItem.class);
                        ContentValues values = new ContentValues();
                        values.put(BankAccountDb.COL_NAME, item.getName());
                        values.put(BankAccountDb.COL_MONEY, item.getMoney());
                        values.put(BankAccountDb.COL_RATE, item.getRate());
                        values.put(BankAccountDb.COL_DATE, item.getDate());
                        mSQLiteRate.insert(BankAccountDb.TABLE_NAME, null, values);
                    }
                    // Nếu UI is dead thì không làm gì cả
                    if (getActivity() == null) {
                        return;
                    }
                    mSQLiteRate.close();
                    intsQuery.removeEventListener(this);
                    mProDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }
    }
}
