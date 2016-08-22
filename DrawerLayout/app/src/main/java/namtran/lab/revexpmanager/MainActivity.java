package namtran.lab.revexpmanager;

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

import namtran.lab.database.InDb;
import namtran.lab.database.InterestDb;
import namtran.lab.database.OutDb;
import namtran.lab.entity.UserInfo;
import namtran.lab.info.InfoActivity;
import namtran.lab.statistics.StatisticsFragment;
import namtran.lab.transaction.AddItemActivity;
import namtran.lab.transaction.TransactionsFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private View view;
    private ActionBar actionBar;
    private SQLiteDatabase sqlInterest;
    private SQLiteDatabase sqlIn;
    private SQLiteDatabase sqlOut;
    private UserInfo userInfo;
    private FragmentTransaction transaction;
    private FragmentManager manager;
    private FloatingActionButton fab;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        if (savedInstanceState == null) {
            navigationView.getMenu().performIdentifierAction(R.id.nav_home, Menu.NONE);
        }
        InDb inDb = new InDb(this);
        OutDb outDb = new OutDb(this);
        InterestDb interestDb = new InterestDb(this);
        sqlIn = inDb.getWritableDatabase();
        sqlOut = outDb.getWritableDatabase();
        sqlInterest = interestDb.getWritableDatabase();
    }

    private void init() {
        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        // Home screen
//        manager = getSupportFragmentManager();
//        transaction = manager.beginTransaction();
//        TransactionsFragment home = new TransactionsFragment();
//        transaction.add(R.id.contentPanel, home);
//        transaction.commit();
//        actionBar.setTitle("Sổ giao dịch");
        // Layout
        view = findViewById(R.id.clayout_main);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        // Lấy header view của Navigation
        View header = navigationView.getHeaderView(0);
        getUserInfo();
        TextView description = (TextView) header.findViewById(R.id.description_header);
        description.setText(userInfo.getEmail());
        // FloatingActionButton
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        // Toggle cho Navigation
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // Lấy thông tin người dùng
    private void getUserInfo() {
        SharedPreferences pref = getSharedPreferences(UserInfo.PREF_NAME, MODE_PRIVATE);
        String email = pref.getString(UserInfo.KEY_EMAIL, null);
        String uid = pref.getString(UserInfo.KEY_UID, null);
        userInfo = new UserInfo(email, uid);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        toggle.onConfigurationChanged(newConfig);
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
        fab.hide();
        drawer.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.nav_home:
                actionBar.setTitle(item.getTitle());
                item.setChecked(true);
                setFragment(0);
                fab.show();
                break;
            case R.id.nav_diagram:
                actionBar.setTitle(item.getTitle());
                item.setChecked(true);
                setFragment(1);
                break;
            case R.id.nav_exchange:
                actionBar.setTitle(item.getTitle());
                item.setChecked(true);
                setFragment(2);
                break;
            case R.id.nav_deposit:
                actionBar.setTitle(item.getTitle());
                item.setChecked(true);
                setFragment(3);
                break;
            case R.id.nav_sync:
                if (isInternetAvailable()) {
                    Log.d("Sync", "begin");
                    SyncDataToServer sync = new SyncDataToServer(this, view);
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

    // Xoá tất cả dữ liệu đã lưu khi đăng xuất
    private void removeAllInfoOfUser() {
        sqlIn.execSQL("delete from " + InDb.TABLE_NAME);
        sqlOut.execSQL("delete from " + OutDb.TABLE_NAME);
        sqlInterest.execSQL("delete from " + InterestDb.TABLE_NAME);
        SharedPreferences pref = getSharedPreferences(UserInfo.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear().apply();
    }

    // Mở fragment tương ứng với menu được chọn
    private void setFragment(int position) {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        switch (position) {
            case 0:
                TransactionsFragment home = new TransactionsFragment();
                transaction.replace(R.id.contentPanel, home);
                transaction.commit();
                break;
            case 1:
                StatisticsFragment stats = new StatisticsFragment();
                transaction.replace(R.id.contentPanel, stats);
                transaction.commit();
        }
    }

    // Kiểm tra mạng
    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    // Hiển thị thông báo dạng snackbar
    private void notification(String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    }
}
