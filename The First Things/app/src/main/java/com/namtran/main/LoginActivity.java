package com.namtran.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.namtran.entity.UserInfo;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by namtr on 15/08/2016.
 */
public class LoginActivity extends Activity {
    private TextInputEditText mEmailField, mPasswordField;
    private ProgressBar mProBar;
    private LinearLayout mLoginForm;
    private FirebaseAuth mAuth;
    private String mUid;
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern mPattern = Pattern.compile(EMAIL_PATTERN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mAuth = FirebaseAuth.getInstance();
        init();
    }

    private void init() {
        mEmailField = (TextInputEditText) findViewById(R.id.et_email_login);
        mPasswordField = (TextInputEditText) findViewById(R.id.et_pass_login);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnSignUp = (Button) findViewById(R.id.btnSignUp_signup);
        mProBar = (ProgressBar) findViewById(R.id.login_progress);
        mLoginForm = (LinearLayout) findViewById(R.id.login_form);
        // login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();
                validateAndSignIn(email, password);
            }
        });
        // sign up
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        mPasswordField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String email = mEmailField.getText().toString();
                    String password = mPasswordField.getText().toString();
                    validateAndSignIn(email, password);
                }
                return false;
            }
        });
    }

    private void validateAndSignIn(String email, String password) {
        hideKeyboard();
        if (validateEmail(email) && validatePassword(password)) {
            hideControl();
            signIn(email, password);
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

    private boolean validatePassword(String password) {
        if (password.length() > 5) {
            mPasswordField.setError(null);
            return true;
        } else {
            mPasswordField.requestFocus();
            mPasswordField.setError("Mật khẩu tối thiểu phải chứa 6 kí tự");
            return false;
        }
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager methodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            methodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void hideControl() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProBar.setVisibility(View.VISIBLE);
                mLoginForm.setVisibility(View.GONE);
            }
        });
    }

    private void showControl() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProBar.setVisibility(View.GONE);
                mLoginForm.setVisibility(View.VISIBLE);
            }
        });
    }

    private void signIn(final String email, String password) {
        Task<AuthResult> task = mAuth.signInWithEmailAndPassword(email, password);
        task.addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user = authResult.getUser();
                StringTokenizer st = new StringTokenizer(user.getUid(), "-");
                mUid = "";
                while (st.hasMoreTokens()) {
                    mUid += st.nextToken();
                }
                SharedPreferences pref = getSharedPreferences(UserInfo.PREF_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(UserInfo.KEY_NAME, user.getDisplayName());
                editor.putString(UserInfo.KEY_EMAIL, user.getEmail());
                editor.putString(UserInfo.KEY_UID, mUid);
                editor.putString(UserInfo.KEY_AVATAR, null);
                editor.apply();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                LoginActivity.this.finish();
            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showControl();
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
