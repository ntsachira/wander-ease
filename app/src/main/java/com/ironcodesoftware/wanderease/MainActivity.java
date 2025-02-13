package com.ironcodesoftware.wanderease;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.ironcodesoftware.wanderease.model.User;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.adaper.IntroSliderAdapter;
import com.ironcodesoftware.wanderease.ui.admin.AdminActivity;
import com.ironcodesoftware.wanderease.ui.delivery.DeliveryActivity;
import com.ironcodesoftware.wanderease.ui.home.HomeActivity;
import com.ironcodesoftware.wanderease.ui.login.LogInActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static String TAG = "WanderEaseLog";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if(UserLogIn.hasLogin(MainActivity.this)){
                UserLogIn logIn = UserLogIn.getLogin(MainActivity.this);
                if (logIn.getUser_role().equals(User.DELIVERY)) {
                    gotoActivity(DeliveryActivity.class);
                } else if (logIn.getUser_role().equals(User.ADMIN)) {
                    gotoActivity(AdminActivity.class);
                }else{
                    gotoActivity(HomeActivity.class);
                }
            }else if(isReturningUser()){
                gotoActivity(LogInActivity.class);
            }else{
                EdgeToEdge.enable(this);
                setContentView(R.layout.activity_main);
                ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.partner_main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

                ViewPager2 viewPager = findViewById(R.id.main_viewPager);
                if(viewPager!=null){
                    IntroSliderAdapter.registerViewPager(viewPager);

                    viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                            Button buttonSkip = findViewById(R.id.main_button_getStarted);
                            Button buttonNext = findViewById(R.id.main_button_next);
                            if(position == viewPager.getAdapter().getItemCount()-1){
                                buttonSkip.setVisibility(View.INVISIBLE);
                                buttonNext.setText(R.string.main_getStarted);
                            }else {
                                buttonSkip.setVisibility(View.VISIBLE);
                                buttonNext.setText(R.string.main_next);
                            }
                        }
                    });

                    Button buttonSkip = findViewById(R.id.main_button_getStarted);
                    buttonSkip.setOnClickListener(view->{
                        updateAsReturningUser();
                        gotoActivity(LogInActivity.class);
                    });

                    Button buttonNext = findViewById(R.id.main_button_next);
                    buttonNext.setOnClickListener(view->{
                        if(viewPager.getCurrentItem() == viewPager.getAdapter().getItemCount()-1){
                            updateAsReturningUser();
                            gotoActivity(LogInActivity.class);
                        }else{
                            viewPager.setCurrentItem(viewPager.getCurrentItem()+1,true);
                        }
                    });
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }



    }
    private void gotoActivity(Class<? extends AppCompatActivity> activityClass) {
        startActivity(new Intent(MainActivity.this, activityClass));
        finish();
    }

    private boolean isReturningUser() {
        return getSharedPreferences(getString(R.string.app_package), Context.MODE_PRIVATE)
                .contains(getString(R.string.main_returningUser));
    }

    private void updateAsReturningUser(){
        getSharedPreferences(getString(R.string.app_package), Context.MODE_PRIVATE)
                .edit()
                .putString(getString(R.string.main_returningUser), getString(R.string.main_returningUser))
                .apply();
    }
}