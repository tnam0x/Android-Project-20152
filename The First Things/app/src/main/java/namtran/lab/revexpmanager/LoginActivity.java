package namtran.lab.revexpmanager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import namtran.lab.entity.UserInfo;

/**
 * Created by namtr on 15/08/2016.
 */
public class LoginActivity extends Activity {
    private TextInputEditText mEmailField, mPasswordField;
    private ProgressBar mProBar;
    private LinearLayout mLoginForm;
    private Firebase mFirebase;
    private String mUid;
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern mPattern = Pattern.compile(EMAIL_PATTERN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Firebase.setAndroidContext(this);
        mFirebase = new Firebase("https://expenseproject.firebaseio.com");
        loadControl();
    }

    private void loadControl() {
        mEmailField = (TextInputEditText) findViewById(R.id.et_email_login);
        mPasswordField = (TextInputEditText) findViewById(R.id.et_pass_login);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnSignUp = (Button) findViewById(R.id.btnSignUp);
        mProBar = (ProgressBar) findViewById(R.id.login_progress);
        mLoginForm = (LinearLayout) findViewById(R.id.login_form);
        // login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = mEmailField.getText().toString();
                String pass = mPasswordField.getText().toString();
                Log.d("Login", "Button");
                validate(mail, pass);
            }
        });
        // sign up
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
//                startActivity(intent);
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        mPasswordField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String mail = mEmailField.getText().toString();
                    String pass = mPasswordField.getText().toString();
                    Log.d("Login", "Enter");
                    validate(mail, pass);
                }
                return false;
            }
        });
    }

    private void validate(String mail, String pass) {
        hideKeyboard();
        if (!validateEmail(mail)) {
            mEmailField.requestFocus();
            mEmailField.setError("Email không hợp lệ");
        } else if (!validatePassword(pass)) {
            mPasswordField.requestFocus();
            mPasswordField.setError("Mật khẩu tối thiểu phải chứa 6 kí tự");
        } else {
            hideControl();
            signIn(mail, pass);
        }
    }

    private boolean validateEmail(String email) {
        Matcher matcher = mPattern.matcher(email);
        return matcher.matches();
    }

    private boolean validatePassword(String password) {
        return password.length() > 5;
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

    private void signIn(final String email, String pass) {
        mFirebase.authWithPassword(email, pass, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                Log.d("Firebase", authData.getProvider());
                StringTokenizer st = new StringTokenizer(authData.getUid(), "-");
                mUid = "";
                while (st.hasMoreTokens()) {
                    mUid += st.nextToken();
                }
                SharedPreferences pref = getSharedPreferences(UserInfo.PREF_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(UserInfo.KEY_EMAIL, email);
                editor.putString(UserInfo.KEY_UID, mUid);
                editor.putString(UserInfo.KEY_AVATAR, null);
                editor.apply();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                LoginActivity.this.finish();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                showControl();
                Toast.makeText(LoginActivity.this, firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
