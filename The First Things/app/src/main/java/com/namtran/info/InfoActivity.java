package com.namtran.info;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.namtran.entity.UserInfo;
import com.namtran.main.R;

/**
 * Created by namtr on 15/08/2016.
 */
public class InfoActivity extends AppCompatActivity {
    private UserInfo mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        TextView email = (TextView) findViewById(R.id.email_user);
        TextView name = (TextView) findViewById(R.id.name_user);
        Button btnChangePass = (Button) findViewById(R.id.btn_change_password_info);
        CheckedTextView ctvForgotPassword = (CheckedTextView) findViewById(R.id.ctv_forgot_password_info);
        ctvForgotPassword.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        getUserInfo();
        email.setText(mUserInfo.getEmail());
        name.setText(mUserInfo.getName());
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        ctvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(InfoActivity.this, "Đợi tí xíu đi...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserInfo() {
        SharedPreferences pref = getSharedPreferences(UserInfo.PREF_NAME, MODE_PRIVATE);
        String name = pref.getString(UserInfo.KEY_NAME, "User name");
        String email = pref.getString(UserInfo.KEY_EMAIL, null);
        String uid = pref.getString(UserInfo.KEY_UID, null);
        mUserInfo = new UserInfo(name, email, uid);
    }

}
