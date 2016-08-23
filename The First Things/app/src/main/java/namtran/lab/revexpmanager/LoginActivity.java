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
    private TextInputEditText email, password;
    private ProgressBar bar;
    private LinearLayout loginForm;
    private Firebase root;
    private String uid;
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Firebase.setAndroidContext(this);
        root = new Firebase("https://expenseproject.firebaseio.com");
        loadControl();
    }

    private void loadControl() {
        email = (TextInputEditText) findViewById(R.id.et_email_login);
        password = (TextInputEditText) findViewById(R.id.et_pass_login);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnSignUp = (Button) findViewById(R.id.btnSignUp);
        bar = (ProgressBar) findViewById(R.id.login_progress);
        loginForm = (LinearLayout) findViewById(R.id.login_form);
        // login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString();
                String pass = password.getText().toString();
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
        password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String mail = email.getText().toString();
                    String pass = password.getText().toString();
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
            email.requestFocus();
            email.setError("Email không hợp lệ");
        } else if (!validatePassword(pass)) {
            password.requestFocus();
            password.setError("Mật khẩu tối thiểu phải chứa 6 kí tự");
        } else {
            hideControl();
            authLogin(mail, pass);
        }
    }

    private boolean validateEmail(String email) {
        Matcher matcher = pattern.matcher(email);
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
                bar.setVisibility(View.VISIBLE);
                loginForm.setVisibility(View.GONE);
            }
        });
    }

    private void showControl() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bar.setVisibility(View.GONE);
                loginForm.setVisibility(View.VISIBLE);
            }
        });
    }

    private void authLogin(final String email, String pass) {
        root.authWithPassword(email, pass, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                StringTokenizer st = new StringTokenizer(authData.getUid(), "-");
                uid = "";
                while (st.hasMoreTokens()) {
                    uid += st.nextToken();
                }
                SharedPreferences pref = getSharedPreferences(UserInfo.PREF_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(UserInfo.KEY_EMAIL, email);
                editor.putString(UserInfo.KEY_UID, uid);
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
