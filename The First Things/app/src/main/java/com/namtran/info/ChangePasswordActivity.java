package com.namtran.info;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.namtran.database.InDb;
import com.namtran.database.BankAccountDb;
import com.namtran.database.OutDb;
import com.namtran.entity.UserInfo;
import com.namtran.main.LoginActivity;
import com.namtran.main.R;

/**
 * Created by namtr on 28/08/2016.
 */
public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mOldPasswordField, mNewPasswordField, mRePasswordField;
    private LinearLayout mLayout;
    private UserInfo mUserInfo;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ProgressDialog dialog;
    private SQLiteDatabase mSQLiteIn, mSQLiteOut, mSQLiteRate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getUserInfo();
        init();
        InDb inDb = new InDb(this);
        OutDb outDb = new OutDb(this);
        BankAccountDb interestDb = new BankAccountDb(this);
        mSQLiteIn = inDb.getWritableDatabase();
        mSQLiteOut = outDb.getWritableDatabase();
        mSQLiteRate = interestDb.getWritableDatabase();
    }

    private void init() {
        mLayout = (LinearLayout) findViewById(R.id.layout_change_password);
        mOldPasswordField = (EditText) findViewById(R.id.et_old_password_cp);
        mNewPasswordField = (EditText) findViewById(R.id.et_new_password_cp);
        mRePasswordField = (EditText) findViewById(R.id.et_re_password_cp);
        Button mChangePasswordButton = (Button) findViewById(R.id.btn_change_password_cp);
        Button mCancelButton = (Button) findViewById(R.id.btn_cancel_cp);
        mChangePasswordButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
    }

    private void getUserInfo() {
        SharedPreferences pref = getSharedPreferences(UserInfo.PREF_NAME, MODE_PRIVATE);
        String name = pref.getString(UserInfo.KEY_NAME, "User name");
        String email = pref.getString(UserInfo.KEY_EMAIL, null);
        String uid = pref.getString(UserInfo.KEY_UID, null);
        mUserInfo = new UserInfo(name, email, uid);
    }

    private void validate() {
        Log.d("validate", "begin");
        String oldPassword = mOldPasswordField.getText().toString();
        String newPassword = mNewPasswordField.getText().toString();
        String reNewPassword = mRePasswordField.getText().toString();
        if (validatePassword(oldPassword)) {
            if (validatePassword(newPassword)) {
                if (newPassword.equals(reNewPassword)) {
                    String[] passwords = {oldPassword, newPassword};
                    doChange(passwords);
                } else {
                    mRePasswordField.requestFocus();
                    Toast.makeText(this, "Nhập lại mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                }
            } else {
                mNewPasswordField.requestFocus();
            }
        } else {
            mOldPasswordField.requestFocus();
        }

    }

    private void doChange(final String[] passwords) {
        hideControl();
        final FirebaseUser user = mAuth.getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(mUserInfo.getEmail(), passwords[0]);
        if (user != null) {
            Log.d("reAuth", "user not null");
            Task<Void> reAuthTask = user.reauthenticate(credential);
            reAuthTask.addOnSuccessListener(this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Task<Void> updateTask = user.updatePassword(passwords[1]);
                    updateTask.addOnSuccessListener(ChangePasswordActivity.this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("validate", "signout");
                            mSQLiteIn.execSQL("delete from " + InDb.TABLE_NAME);
                            mSQLiteOut.execSQL("delete from " + OutDb.TABLE_NAME);
                            mSQLiteRate.execSQL("delete from " + BankAccountDb.TABLE_NAME);
                            SharedPreferences pref = getSharedPreferences(UserInfo.PREF_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.clear().apply();
                            mAuth.signOut();
                            AlertDialog.Builder dialog = new AlertDialog.Builder(ChangePasswordActivity.this);
                            dialog.setTitle("Đổi mật khẩu thành công").setCancelable(false);
                            dialog.setMessage("Hãy đăng nhập lại bằng mật khẩu mới của bạn");
                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    ActivityCompat.finishAffinity(ChangePasswordActivity.this);
                                }
                            });
                            dialog.show();
                        }
                    });
                    updateTask.addOnFailureListener(ChangePasswordActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showControl();
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            reAuthTask.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showControl();
                    Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            showControl();
            Toast.makeText(ChangePasswordActivity.this, "Đã xảy ra lỗi khi đổi mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validatePassword(String password) {
        Log.d("validatePassword", "begin");
        if (password.isEmpty()) {
            Toast.makeText(this, "Bạn chưa nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu tối thiểu phải chứa 6 kí tự", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setMessage("Vui lòng chờ...");
        dialog.setCancelable(false);
        dialog.show();
    }

    private void hideProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void hideControl() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLayout.setVisibility(View.GONE);
                showProgressDialog();
            }
        });
    }

    private void showControl() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgressDialog();
                mLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager methodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            methodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    // Kiểm tra mạng
    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_change_password_cp:
                hideKeyboard();
                if (isInternetAvailable()) {
                    validate();
                } else {
                    Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_cancel_cp:
                finish();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideProgressDialog();
    }

}
