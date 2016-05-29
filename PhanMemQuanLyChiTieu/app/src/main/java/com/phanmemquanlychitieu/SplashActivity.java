package com.phanmemquanlychitieu;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import Database.UserDatabase;

/**
 * Created by Legendary on 27/04/2016.
 */
public class SplashActivity extends Activity {
    private UserDatabase userDb;
    ImageView view2;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        view2 = (ImageView) findViewById(R.id.splashImg);
        userDb = new UserDatabase(this);
        new CheckLogin().execute();
    }

    public class CheckLogin extends AsyncTask<Void, Void, Boolean> {
        private Thread splashThread;

        @Override
        protected void onPreExecute() {
            splashThread = new Thread() {
                @Override
                public void run() {
                    try {
                        int waited = 0;
                        // Splash screen pause time
                        while (waited < 3500) {
                            sleep(100);
                            waited += 100;
                        }
                    } catch (InterruptedException e) {
                        // do something
                    }
                }
            };
            splashThread.start();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = false;
            SQLiteDatabase mSQLite = userDb.getReadableDatabase();
            String query = "select * from " + UserDatabase.TABLE_NAME;
            Cursor cursor = mSQLite.rawQuery(query, null);
            if (cursor.moveToFirst())
                result = true;
            cursor.close();
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                intent = new Intent(SplashActivity.this, NavigationDrawer.class);
                startActivity(intent);
                finish();
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            super.onPostExecute(result);
        }
    }
}
