package com.ironcodesoftware.wanderease.ui.partner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.ShakeDetector;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.ui.home.HelpFragment;
import com.ironcodesoftware.wanderease.ui.home.MessageFragment;
import com.ironcodesoftware.wanderease.ui.login.LogInActivity;

import java.io.IOException;

public class PartnerActivity extends AppCompatActivity {

    PartnerDashboardFragment dashboardFragment = new PartnerDashboardFragment();
    PartnerProductTabFragment productFragment = new PartnerProductTabFragment();
    PartnerServiceFragment serviceFragment = new PartnerServiceFragment();
    PartnerOrderFragment orderFragment = new PartnerOrderFragment();
    PartnerBookingFragment bookingFragment = new PartnerBookingFragment();
    MessageFragment messageFragment = new MessageFragment();
    HelpFragment helpFragment = new HelpFragment();
    private ShakeDetector shakeDetector;

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_partner);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.partner_drawer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initShakeDetector();

        getWindow().setStatusBarColor(getColor(R.color.background));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().getDecorView().bringToFront();

        setUserDetails();
        MaterialToolbar toolbar = findViewById(R.id.partner_materialToolbar);
        toolbar.setTitleCentered(true);

        toolbar.setTitle(R.string.partner_dashboard);
        toolbar.setNavigationOnClickListener(v -> {
            DrawerLayout drawer = findViewById(R.id.partner_drawer);
            drawer.open();
        });

        MenuItem menuItemAddShortcut = toolbar.getMenu().getItem(0);
        menuItemAddShortcut.setOnMenuItemClickListener(item -> {
            createShortcut();
            return false;
        });

        NavigationView navigationView = findViewById(R.id.partner_navigationView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                setSelectedFragment(item);
                DrawerLayout drawer = findViewById(R.id.partner_drawer);
                drawer.close();
                return true;
            }
        });

    }

    private void initShakeDetector() {
        shakeDetector = new ShakeDetector(this) {
            @Override
            public void onShake() {
                recreate();
            }
        };
    }

    private void setUserDetails() {
        try {
            NavigationView navigationView = findViewById(R.id.partner_navigationView);
            TextView textViewUsername = navigationView.getHeaderView(0)
                    .findViewById(R.id.partner_navigation_header_textView_username);
            if(UserLogIn.hasLogin(this)){
                UserLogIn login = UserLogIn.getLogin(this);
                if(login.getDisplay_name()!=null){
                    textViewUsername.setText(login.getDisplay_name());
                }else{
                    textViewUsername.setText(login.getEmail());
                }
            }else{
                gotoLogin();
            }
        } catch (IOException | ClassNotFoundException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
                    .show();
            Log.e(MainActivity.TAG,e.getLocalizedMessage());
            gotoLogin();
        }
    }
    private void gotoLogin(){
        startActivity(new Intent(this, LogInActivity.class));
        finish();
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.partner_main_fragmentContainerView, fragment).setReorderingAllowed(true)
                .commit();
    }

    private void setSelectedFragment(MenuItem item){
        MaterialToolbar toolbar = findViewById(R.id.partner_materialToolbar);
        int titleId;
        if(item.getItemId() == R.id.partner_menu_products){
            titleId = R.string.partner_products;
            loadFragment(productFragment);
        } else if (item.getItemId() == R.id.partner_menu_services) {
            titleId = R.string.partner_services;
            loadFragment(serviceFragment);
        } else if (item.getItemId() == R.id.partner_menu_orders) {
            titleId = R.string.partner_orders;
            loadFragment(orderFragment);
        } else if (item.getItemId() == R.id.partner_menu_booking) {
            titleId = R.string.partner_bookings;
            loadFragment(bookingFragment);
        } else if (item.getItemId() == R.id.partner_menu_messages) {
            titleId = R.string.partner_Messages;
            loadFragment(messageFragment);
        } else if (item.getItemId() == R.id.partner_menu_help) {
            titleId = R.string.partner_Help;
            loadFragment(helpFragment);
        } else {
            titleId = R.string.partner_dashboard;
            loadFragment(dashboardFragment);
        }
        toolbar.setTitle(titleId);
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private void createShortcut() {
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (shortcutManager != null && shortcutManager.isRequestPinShortcutSupported()) {
                ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(this, "wander_ease_partner")
                        .setShortLabel("WanderEase Partner")
                        .setIcon(Icon.createWithResource(this, R.drawable.be_partner))
                        .setIntent(new Intent(this, PartnerActivity.class)
                                .setAction(Intent.ACTION_VIEW))
                        .build();

                shortcutManager.requestPinShortcut(shortcutInfo, null);
            }
        }
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        NavigationView navigationView = findViewById(R.id.partner_navigationView);
        outState.putInt("selectedMenuItem", navigationView.getCheckedItem().getItemId());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        NavigationView navigationView = findViewById(R.id.partner_navigationView);
        Menu menu = navigationView.getMenu();
        setSelectedFragment(menu.findItem(savedInstanceState.getInt("selectedMenuItem")));
    }

    @Override
    protected void onResume() {
        super.onResume();
        shakeDetector.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        shakeDetector.stop();
    }
}