package com.phanmemquanlychitieu;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import Database.UserDatabase;

/**
 * Created by Legendary on 27/04/2016.
 */
public class SplashActivity extends Activity {
    private UserDatabase userDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView view1 = (ImageView) findViewById(R.id.img1);
        ImageView view2 = (ImageView) findViewById(R.id.img2);
        userDb = new UserDatabase(this);
        Intent intent;
        if (hasLogin()) {
            intent = new Intent(SplashActivity.this, NavigationDrawer.class);
            startActivity(intent);
            finish();
        } else {
            view1.setVisibility(View.VISIBLE);
            view2.setVisibility(View.GONE);
            intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public boolean hasLogin() {
        boolean result = false;
        SQLiteDatabase mSQLite = userDb.getReadableDatabase();
        String query = "select * from " + UserDatabase.TABLE_NAME;
        Cursor cursor = mSQLite.rawQuery(query, null);
        if (cursor.moveToFirst())
            result = true;
        return result;
    }
}
