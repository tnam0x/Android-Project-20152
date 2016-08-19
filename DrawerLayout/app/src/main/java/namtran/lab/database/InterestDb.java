package namtran.lab.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Legendary on 04/05/2016.
 */
public class InterestDb extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "tblaixuat";
    public static final String COL_NAME = "tennganhang";
    public static final String COL_TIEN = "tienlaixuat";
    public static final String COL_LAIXUAT = "laixuat";
    public static final String COL_DATE = "ngaythanglaixuat";
    private static final String DB_NAME = "tienlaixuat";
    private static final int DB_VERSION = 1;
    private static final String STRING_CREATE = "CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COL_NAME + " TEXT," + COL_TIEN + " TEXT," + COL_LAIXUAT + " TEXT," + COL_DATE + " TEXT);";

    public InterestDb(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(STRING_CREATE);
//        ContentValues cv = new ContentValues();
//        db.insert(TABLE_NAME, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
