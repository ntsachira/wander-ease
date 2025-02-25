package com.ironcodesoftware.wanderease.ui.home.account;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.ShakeDetector;

public class MyOrdersActivity extends AppCompatActivity {
    private ShakeDetector shakeDetector;
    CompletedOrdersFragment completedOrdersFragment = new CompletedOrdersFragment();
    ToReceiveFragment  toReceiveFragment = new ToReceiveFragment();
    ToReviewFragment toReviewFragment = new ToReviewFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_orders);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initShakeDetector();
        getWindow().setStatusBarColor(getColor(R.color.white));
        loadFragment(new CompletedOrdersFragment());

        Toolbar toolbar = findViewById(R.id.my_orders_toolbar);
        toolbar.setNavigationOnClickListener(v->{
            finish();
        });

        TabLayout tabLayout = findViewById(R.id.my_orders_tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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

    private void loadFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.my_orders_fragment_container_view, fragment).setReorderingAllowed(true)
                .commit();
    }

    private void changeTab(int position){
        if(position == 1){
            loadFragment(toReceiveFragment);
        } else if (position == 2) {
            loadFragment(toReviewFragment);
        }else{
            loadFragment(completedOrdersFragment);
        }
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