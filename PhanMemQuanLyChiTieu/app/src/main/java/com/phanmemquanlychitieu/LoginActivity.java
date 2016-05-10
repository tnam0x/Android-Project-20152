package com.phanmemquanlychitieu;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.StringTokenizer;

import Database.UserDatabase;
import Database.dbChi;
import Database.dbThu;
import Objects.Item;

/**
 * Created by Legendary on 25/04/2016.
 */
public class LoginActivity extends Activity {
    private EditText email, password;
    private Button btnLogin, btnSignUp;
    private ProgressBar bar;
    private LinearLayout loginForm;
    private Firebase root;

    private UserDatabase userDb;
    private SQLiteDatabase mSQLite;

    private dbThu thuDb;
    private dbChi chiDb;

    private SQLiteDatabase sqLiteExpense, sqLiteIncome;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Firebase.setAndroidContext(this);
        root = new Firebase("https://expenseproject.firebaseio.com");
        userDb = new UserDatabase(this);
        thuDb = new dbThu(this);
        chiDb = new dbChi(this);

        loadControl();

        // login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString();
                String pass = password.getText().toString();
                authLogin(mail, pass);
            }
        });

        // sign up
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String mail = email.getText().toString();
                    String pass = password.getText().toString();
                    authLogin(mail, pass);
                }
                return false;
            }
        });
    }

    public void authLogin(final String email, String pass) {
        root.authWithPassword(email, pass, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                bar.setVisibility(View.VISIBLE);
                loginForm.setVisibility(View.GONE);
                mSQLite = userDb.getWritableDatabase();
                StringTokenizer st = new StringTokenizer(authData.getUid(), "-");
                String uid = "";
                while (st.hasMoreTokens()) {
                    uid += st.nextToken();
                }
                ContentValues cv = new ContentValues();
                cv.put(UserDatabase.COL_UID, uid);
                cv.put(UserDatabase.COL_EMAIL, email);
                cv.put(UserDatabase.COL_KEY, "true");
                mSQLite.insert(UserDatabase.TABLE_NAME, null, cv);
                syncData(uid);
                Intent intent = new Intent(LoginActivity.this, NavigationDrawer.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadControl() {
        email = (EditText) findViewById(R.id.et_login_1);
        password = (EditText) findViewById(R.id.et_login_2);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        bar = (ProgressBar) findViewById(R.id.login_progress);
        loginForm = (LinearLayout) findViewById(R.id.login_form);
    }

    private void syncData(String name) {
        Firebase refExpense = root.child(name).child("Expense");
        final Query expenseQuery = refExpense.orderByValue();
        expenseQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sqLiteExpense = chiDb.getWritableDatabase();
                for (DataSnapshot listItem : dataSnapshot.getChildren()) {
                    item = listItem.getValue(Item.class);
                    ContentValues cv = new ContentValues();
                    cv.put(dbChi.COL_TIEN, item.getCost());
                    cv.put(dbChi.COL_NHOM, item.getType());
                    cv.put(dbChi.COL_DATE, item.getDate());
                    cv.put(dbChi.COL_GHICHU, item.getNote());
                    sqLiteExpense.insert(dbChi.TABLE_NAME, null, cv);
                }
                expenseQuery.removeEventListener(this);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Firebase refIncome = root.child(name).child("Income");
        final Query incomeQuery = refIncome.orderByValue();
        incomeQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sqLiteIncome = thuDb.getWritableDatabase();
                for (DataSnapshot listItem : dataSnapshot.getChildren()) {
                    item = listItem.getValue(Item.class);
                    ContentValues cv = new ContentValues();
                    cv.put(dbThu.COL_TIEN, item.getCost());
                    cv.put(dbThu.COL_NHOM, item.getType());
                    cv.put(dbThu.COL_DATE, item.getDate());
                    cv.put(dbThu.COL_GHICHU, item.getNote());
                    sqLiteIncome.insert(dbThu.TABLE_NAME, null, cv);
                }
                incomeQuery.removeEventListener(this);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
