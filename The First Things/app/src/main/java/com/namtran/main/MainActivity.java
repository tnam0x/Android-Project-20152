package com.namtran.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.namtran.banking.BankingAccountFragment;
import com.namtran.database.InDb;
import com.namtran.database.InterestDb;
import com.namtran.database.OutDb;
import com.namtran.entity.UserInfo;
import com.namtran.exchange.ExchangeFragment;
import com.namtran.info.InfoActivity;
import com.namtran.statistics.StatisticsFragment;
import com.namtran.transaction.AddItemActivity;
import com.namtran.transaction.TransactionsFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private View mView;
    private ActionBar mActionBar;
    private SQLiteDatabase mSQLiteIn, mSQLiteOut, mSQLiteRate;
    private UserInfo mUserInfo;
    private FloatingActionButton mFAB;
    private NavigationView mNavView;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        if (savedInstanceState == null) {
            mNavView.getMenu().performIdentifierAction(R.id.nav_home, Menu.NONE);
        }
        InDb inDb = new InDb(this);
        OutDb outDb = new OutDb(this);
        InterestDb interestDb = new InterestDb(this);
        mSQLiteIn = inDb.getWritableDatabase();
        mSQLiteOut = outDb.getWritableDatabase();
        mSQLiteRate = interestDb.getWritableDatabase();
    }

    private void init() {
        // Toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mView = findViewById(R.id.clayout_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavView = (NavigationView) findViewById(R.id.nav_view);
        // Lấy header view của Navigation
        View header = mNavView.getHeaderView(0);
        getUserInfo();
        TextView title = (TextView) header.findViewById(R.id.title_header);
        TextView description = (TextView) header.findViewById(R.id.description_header);
        title.setText(mUserInfo.getEmail());
        description.setText(mUserInfo.getName());
        // FloatingActionButton
        mFAB = (FloatingActionButton) findViewById(R.id.fab);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        // Toggle cho Navigation
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);
        mNavView.setNavigationItemSelectedListener(this);
    }

    // Lấy thông tin người dùng
    private void getUserInfo() {
        SharedPreferences pref = getSharedPreferences(UserInfo.PREF_NAME, MODE_PRIVATE);
        String name = pref.getString(UserInfo.KEY_NAME, "User name");
        String email = pref.getString(UserInfo.KEY_EMAIL, null);
        String uid = pref.getString(UserInfo.KEY_UID, null);
        mUserInfo = new UserInfo(name, email, uid);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mToggle.onConfigurationChanged(newConfig);
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Nhấn back lần nữa để thoát", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        mFAB.setVisibility(View.INVISIBLE);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.nav_home:
                mActionBar.setTitle(item.getTitle());
                item.setChecked(true);
                setFragment(0);
                mFAB.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_diagram:
                mActionBar.setTitle(item.getTitle());
                item.setChecked(true);
                setFragment(1);
                break;
            case R.id.nav_exchange:
                mActionBar.setTitle(item.getTitle());
                item.setChecked(true);
                setFragment(2);
                break;
            case R.id.nav_deposit:
                params.setScrollFlags(-1);
                mActionBar.setTitle(item.getTitle());
                item.setChecked(true);
                setFragment(3);
                break;
            case R.id.nav_sync:
                if (isInternetAvailable()) {
                    Log.d("Sync", "begin");
                    SyncDataToServer sync = new SyncDataToServer(this, mView);
                    sync.execute();
                } else {
                    notification("Không có kết nối mạng");
                }
                break;
            case R.id.nav_user_info:
                Intent infoIntent = new Intent(MainActivity.this, InfoActivity.class);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(infoIntent);
                break;
            case R.id.nav_logout:
                AlertDialog.Builder exitDialog = new AlertDialog.Builder(MainActivity.this);
                exitDialog.setTitle("Quản lý chi tiêu");
                exitDialog.setMessage("Bạn có thực sự muốn đăng xuất chương trình không?");
                exitDialog.setNegativeButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeAllInfoOfUser();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                });
                exitDialog.setPositiveButton("Để sau", null);
                exitDialog.setCancelable(false);
                exitDialog.show();
                break;
        }
        return false;
    }

    // Mở fragment tương ứng với menu được chọn
    private void setFragment(int position) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch (position) {
            case 0:
                TransactionsFragment home = new TransactionsFragment();
                transaction.replace(R.id.contentPanel, home);
                break;
            case 1:
                StatisticsFragment stats = new StatisticsFragment();
                transaction.replace(R.id.contentPanel, stats);
                break;
            case 2:
                ExchangeFragment exchange = new ExchangeFragment();
                transaction.replace(R.id.contentPanel, exchange);
                break;
            case 3:
                BankingAccountFragment banking = new BankingAccountFragment();
                transaction.replace(R.id.contentPanel, banking);
                break;
        }
        transaction.commit();
    }

    // Xoá tất cả dữ liệu đã lưu khi đăng xuất
    private void removeAllInfoOfUser() {
        mSQLiteIn.execSQL("delete from " + InDb.TABLE_NAME);
        mSQLiteOut.execSQL("delete from " + OutDb.TABLE_NAME);
        mSQLiteRate.execSQL("delete from " + InterestDb.TABLE_NAME);
        SharedPreferences pref = getSharedPreferences(UserInfo.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear().apply();
    }

    // Kiểm tra mạng
    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    // Hiển thị thông báo dạng snackbar
    private void notification(String msg) {
        Snackbar.make(mView, msg, Snackbar.LENGTH_SHORT).show();
    }
}
