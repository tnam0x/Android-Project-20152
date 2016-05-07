package com.phanmemquanlychitieu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * Created by tuand on 02/05/2016.
 */
public class SignUpActivity extends Activity {
    EditText name, email, password, retype_password;
    Button btnSignUp;
    ScrollView signUp_form;
    Firebase root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Firebase.setAndroidContext(this);
        root = new Firebase("https://expenseproject.firebaseio.com");

        loadControl();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().equals(retype_password.getText().toString())) {
                    root.createUser(email.getText().toString(), password.getText().toString(), new Firebase.ResultHandler() {
                        @Override
                        public void onSuccess() {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(SignUpActivity.this);
                            dialog.setTitle("Đăng kí tài khoản");
                            dialog.setMessage("Đăng kí thành công");
                            dialog.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    email.setText("");
                                    password.setText("");
                                    finish();
                                }
                            });
                            dialog.create().show();
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            Toast.makeText(SignUpActivity.this, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else
                    Toast.makeText(SignUpActivity.this, "Error, please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void loadControl() {
        email = (EditText) findViewById(R.id.user_email);
        password = (EditText) findViewById(R.id.user_password);
        retype_password = (EditText) findViewById(R.id.user_retype_password);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        signUp_form = (ScrollView) findViewById(R.id.signUp_form);
    }
}
