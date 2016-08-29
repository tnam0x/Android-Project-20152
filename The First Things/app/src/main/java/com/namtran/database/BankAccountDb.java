package com.namtran.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Legendary on 04/05/2016.
 * Cơ sở dữ liệu cho Tài khoản ngân hàng.
 */
public class BankAccountDb extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "account";
    public static final String COL_NAME = "name";
    public static final String COL_MONEY = "money";
    public static final String COL_RATE = "interest_rate";
    public static final String COL_DATE = "date";
    private static final String DB_NAME = "banking_account";
    private static final int DB_VERSION = 1;
    private static final String STRING_CREATE = "CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COL_NAME + " TEXT," + COL_MONEY + " TEXT," + COL_RATE + " TEXT," + COL_DATE + " TEXT);";

    public BankAccountDb(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(STRING_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
