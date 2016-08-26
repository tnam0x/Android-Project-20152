package com.namtran.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InDb extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "income";
    public static final String COL_COST = "money";
    public static final String COL_TYPE = "type";
    public static final String COL_NOTE = "note";
    public static final String COL_DATE = "date";
    private static final String DB_NAME = "revenues";
    private static final int DB_VERSION = 1;
    private static final String STRING_CREATE = "CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COL_COST + " TEXT," + COL_TYPE + " TEXT," + COL_NOTE + " TEXT," + COL_DATE + " TEXT);";

    public InDb(Context context) {
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
