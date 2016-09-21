package com.namtran.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by namtr on 15/08/2016.
 */
public class SignUpActivity extends AppCompatActivity {
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private EditText mUserNameField, mEmailField, mPasswordField, mRePasswordField;
    private Pattern mPattern = Pattern.compile(EMAIL_PATTERN);
    private LinearLayout mSignUpForm;
    private ProgressBar mProBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        init();
    }

    private void init() {
        mSignUpForm = (LinearLayout) findViewById(R.id.signUp_form);
        mProBar = (ProgressBar) findViewById(R.id.signUp_progress);
        mUserNameField = (EditText) findViewById(R.id.et_name_signup);
        mEmailField = (EditText) findViewById(R.id.et_email_signup);
        mPasswordField = (EditText) findViewById(R.id.et_password_signup);
        mRePasswordField = (EditText) findViewById(R.id.et_repassword_signup);
        Button signUpButton = (Button) findViewById(R.id.btnSignUp_signup);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mUserNameField.getText().toString().trim();
                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();
                String rePassword = mRePasswordField.getText().toString();
                validateAndSignUp(name, email, password, rePassword);
            }
        });
    }

    private void validateAndSignUp(String name, String email, String password, String rePassword) {
        hideKeyboard();
        if (validateName(name) && validateEmail(email) && validatePassword(password, rePassword)) {
            hideControl();
            signUp(name, email, password);
        }
    }

    private void signUp(final String name, String email, String password) {
        Task<AuthResult> signUpTask = mAuth.createUserWithEmailAndPassword(email, password);
        signUpTask.addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user = authResult.getUser();
                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();
                Task<Void> updateTask = user.updateProfile(profile);
                updateTask.addOnSuccessListener(SignUpActivity.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(SignUpActivity.this);
                        dialog.setMessage("Đăng kí thành công");
                        dialog.setCancelable(false);
                        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SignUpActivity.this.finish();
                            }
                        });
                        dialog.show();
                    }
                });
                updateTask.addOnFailureListener(SignUpActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showControl();
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        signUpTask.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showControl();
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateName(String name) {
        if (name.isEmpty()) {
            mUserNameField.setError("Tên không được để trống");
            mUserNameField.requestFocus();
            return false;
        } else {
            mUserNameField.setError(null);
            return true;
        }
    }

    private boolean validateEmail(String email) {
        Matcher matcher = mPattern.matcher(email);
        if (matcher.matches()) {
            mEmailField.setError(null);
            return true;
        } else {
            mEmailField.setError("Email không hợp lệ");
            mEmailField.requestFocus();
            return false;
        }
    }

    private boolean validatePassword(String password, String rePassword) {
        if (password.length() < 6) {
            mPasswordField.setError("Mật khẩu tối thiểu phải chứa 6 kí tự");
            mPasswordField.requestFocus();
            return false;
        } else {
            mPasswordField.setError(null);
            if (rePassword.equals(password)) {
                mRePasswordField.setError(null);
                return true;
            } else {
                mRePasswordField.setError("Mật khẩu không khớp");
                mRePasswordField.requestFocus();
                return false;
            }
        }
    }

    private void hideControl() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProBar.setVisibility(View.VISIBLE);
                mSignUpForm.setVisibility(View.GONE);
            }
        });
    }

    private void showControl() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProBar.setVisibility(View.GONE);
                mSignUpForm.setVisibility(View.VISIBLE);
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
}
