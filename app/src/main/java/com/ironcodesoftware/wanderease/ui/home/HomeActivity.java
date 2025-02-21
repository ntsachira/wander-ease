package com.ironcodesoftware.wanderease.ui.home;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.Notification;
import com.ironcodesoftware.wanderease.model.ShakeDetector;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.WanderDialog;
import com.ironcodesoftware.wanderease.model.WanderNotification;
import com.ironcodesoftware.wanderease.ui.login.LogInActivity;

import java.io.IOException;

public class HomeActivity extends AppCompatActivity {

    private ShakeDetector shakeDetector;
    AccountFragment accountFragment =  new AccountFragment();
    BookingFragment bookingFragment = new BookingFragment();
    CartFragment cartFragment = new CartFragment();
    SavedFragment savedFragment = new SavedFragment();
    SearchFragment searchFragment = new SearchFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.delivery_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        showHint();

        initShakeDetector();

        getWindow().setStatusBarColor(getColor(R.color.white));
        BottomNavigationView bottomNavigationView = findViewById(R.id.home_bottomNavigationView);
        bottomNavigationView.setItemActiveIndicatorColor(
                ColorStateList.valueOf(getColor(R.color.navigation_active_indicator))
        );

        loadFragment(searchFragment);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            setSelectedFragment(item);
            return true;
        });

        ImageButton buttonNotification = findViewById(R.id.home_header_notofication_imageButton);
        buttonNotification.setOnClickListener(v->{
            startActivity(new Intent(HomeActivity.this,MessagesActivity.class));
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

    private void showHint() {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        if(!sharedPreferences.contains("hint")){
            AlertDialog infoDialog = WanderDialog.info(this,
                    "You can shake you phone to reload the content");
            infoDialog.setButton(DialogInterface.BUTTON_NEUTRAL,"Do not show this again",(dialog, which) -> {
                sharedPreferences.edit().putBoolean("hint", false).apply();
            });
            infoDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Ok",(dialog, which) -> {
                dialog.cancel();
            });
            infoDialog.show();
        }

    }


    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_fragmentContainer, fragment).setReorderingAllowed(true)
                .commit();
    }

    private void setSelectedFragment(MenuItem item){
        if(item.getItemId() == R.id.bottom_menu_account){
            loadFragment(accountFragment);
        } else if (item.getItemId() == R.id.bottom_menu_booking) {
            loadFragment(bookingFragment);
        } else if (item.getItemId() == R.id.bottom_menu_cart) {
            loadFragment(cartFragment);
        } else if (item.getItemId() == R.id.bottom_menu_saved) {
            loadFragment(savedFragment);
        }else {
            loadFragment(searchFragment);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        BottomNavigationView bottomNavigationView = findViewById(R.id.home_bottomNavigationView);
        outState.putInt("selectedMenuItemId",bottomNavigationView.getSelectedItemId());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        BottomNavigationView bottomNavigationView = findViewById(R.id.home_bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        setSelectedFragment(menu.findItem(savedInstanceState.getInt("selectedMenuItemId")));
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if(!UserLogIn.hasLogin(this)){
                startActivity(new Intent(this, LogInActivity.class));
                finish();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        shakeDetector.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        shakeDetector.stop();
    }
}