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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import com.namtran.adapter.TransactionsRecyclerAdapter;
import com.namtran.database.InDb;
import com.namtran.database.OutDb;
import com.namtran.entity.CurrencyParser;
import com.namtran.entity.TransactionsItem;
import com.namtran.entity.UserInfo;
import com.namtran.main.R;

/**
 * Created by namtr on 19/08/2016.
 */
public class ExpenditureFragment extends Fragment {
    private Firebase mFirebase;
    private SQLiteDatabase mSQLiteIn, mSQLiteOut;
    private ArrayList<TransactionsItem> mListItem;
    private ProgressDialog mProDialog;
    private UserInfo mUserInfo;
    private TransactionsRecyclerAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_list_item, container, false);
        getUserInfo();
        // Khởi tạo firebase
        Firebase.setAndroidContext(getContext());
        mFirebase = new Firebase("https://expenseproject.firebaseio.com");
        // Khởi tạo database
        InDb inDb = new InDb(getContext());
        OutDb outDb = new OutDb(getContext());
        mSQLiteIn = inDb.getWritableDatabase();
        mSQLiteOut = outDb.getWritableDatabase();
        // Lưu danh sách thu
        mListItem = new ArrayList<>();
        setupRecyclerView(recyclerView);
        // Lấy dữ liệu hiển thị lên listview
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
        mAdapter = new TransactionsRecyclerAdapter(mListItem, false, new RecyclerListener(recyclerView));
        recyclerView.setAdapter(mAdapter);
    }

    // Lấy thông tin người dùng
    private void getUserInfo() {
        SharedPreferences pref = getContext().getSharedPreferences(UserInfo.PREF_NAME, Context.MODE_PRIVATE);
        String email = pref.getString(UserInfo.KEY_EMAIL, null);
        String uid = pref.getString(UserInfo.KEY_UID, null);
        mUserInfo = new UserInfo(email, uid);
    }

    // Hiển thị dialog khi lấy dữ liệu
    private void initDialog() {
        mProDialog = new ProgressDialog(getContext());
        mProDialog.setMessage("Đang tải dữ liệu...");
        mProDialog.setCancelable(false);
    }

    // Kiểm tra database có dữ liệu hay chưa
    private boolean isDbEmpty() {
        Cursor cursorIn = mSQLiteIn.rawQuery("select * from " + InDb.TABLE_NAME, null);
        Cursor cursorOut = mSQLiteOut.rawQuery("select * from " + OutDb.TABLE_NAME, null);
        boolean isEmpty = cursorIn.moveToFirst() && cursorOut.moveToFirst();
        cursorIn.close();
        cursorOut.close();
        Log.d("Is database empty", !isEmpty + "");
        return !isEmpty;
    }

    private class RecyclerListener implements View.OnClickListener {
        private RecyclerView mRecyclerView;
        private CurrencyParser mParser;

        public RecyclerListener(RecyclerView recyclerView) {
            this.mRecyclerView = recyclerView;
            mParser = new CurrencyParser();
        }

        @Override
        public void onClick(View view) {
            int position = mRecyclerView.getChildLayoutPosition(view);
            TransactionsItem item = mListItem.get(position);
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("Thông Tin Khoản Chi").setCancelable(true);
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

    // Lấy dữ liệu từ Database
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
            String queryOut = "select * from " + OutDb.TABLE_NAME;
            Cursor cursorOut = mSQLiteOut.rawQuery(queryOut, null);
            if (cursorOut.moveToFirst()) {
                do {
                    id = cursorOut.getString(0);
                    String cost = cursorOut.getString(1);
                    String type = cursorOut.getString(2);
                    String note = cursorOut.getString(3);
                    String date = cursorOut.getString(4);
                    item = new TransactionsItem(cost, type, note, date, id);
                    mListItem.add(item);
                    Log.d("OUT DATA - Local", item.toString());
                } while (cursorOut.moveToNext());
            }
            cursorOut.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.notifyDataSetChanged();
            mProDialog.dismiss();
        }
    }

    // Lấy dữ liệu từ server
    private class GetDataFromServer extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("Get From Server - Exp", "doing");
            // Tiền chi
            Firebase refExpense = mFirebase.child(mUserInfo.getUid()).child("Expense");
            final Query outQuery = refExpense.orderByValue();
            outQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    TransactionsItem item;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        item = snapshot.getValue(TransactionsItem.class);
                        ContentValues cv = new ContentValues();
                        cv.put(OutDb.COL_COST, item.getCost());
                        cv.put(OutDb.COL_TYPE, item.getType());
                        cv.put(OutDb.COL_NOTE, item.getNote());
                        cv.put(OutDb.COL_DATE, item.getDate());
                        mSQLiteOut.insert(OutDb.TABLE_NAME, null, cv);
                        mListItem.add(item);
                        Log.d("OUT DATA - Server", item.toString());
                    }
                    mSQLiteOut.close();
                    outQuery.removeEventListener(this);
                    mAdapter.notifyDataSetChanged();
                    mProDialog.dismiss();
                    Log.d("Get out data - Exp", "Done");
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Toast.makeText(getContext(), firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }
    }
}
