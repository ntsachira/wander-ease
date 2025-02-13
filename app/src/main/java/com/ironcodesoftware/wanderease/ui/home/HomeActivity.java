package com.ironcodesoftware.wanderease.ui.home;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ironcodesoftware.wanderease.R;

public class HomeActivity extends AppCompatActivity {

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setStatusBarColor(getColor(R.color.primary));
        BottomNavigationView bottomNavigationView = findViewById(R.id.home_bottomNavigationView);
        bottomNavigationView.setItemActiveIndicatorColor(
                ColorStateList.valueOf(getColor(R.color.navigation_active_indicator))
        );
        loadFragment(searchFragment);

        bottomNavigationView.setOnItemSelectedListener(item -> {
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
            return true;
        });

    }
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_fragmentContainer, fragment).setReorderingAllowed(true)
                .commit();
    }
}