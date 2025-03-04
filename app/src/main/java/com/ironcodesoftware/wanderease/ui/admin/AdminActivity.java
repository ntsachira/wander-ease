package com.ironcodesoftware.wanderease.ui.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.HttpClient;
import com.ironcodesoftware.wanderease.model.ShakeDetector;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.WanderDialog;
import com.ironcodesoftware.wanderease.ui.login.LogInActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdminActivity extends AppCompatActivity {

    private ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.delivery_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setStatusBarColor(getColor(R.color.white));
        initShakeDetector();
        initOnBackPressDispatcher(this);
        loadFragment(new AdminDashboardFragment());

        Toolbar toolbar = findViewById(R.id.admin_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            DrawerLayout drawerLayout = findViewById(R.id.delivery_main);
            drawerLayout.open();
        });

        NavigationView navigationView = findViewById(R.id.admin_navidation_view);
        View headerView = navigationView.getHeaderView(0);
        ImageButton buttonSettings = headerView.findViewById(R.id.admin_navigation_header_settings_imageButton);
        buttonSettings.setOnClickListener(v->{
            startActivity(new Intent(AdminActivity.this,AdminSettingsActivity.class));
        });

        TextView textViewHeaderGreeting = headerView.findViewById(R.id.admin_navigation_menu_username_textViewtextView);
        try {
            setHeaderGeeting(textViewHeaderGreeting);
        } catch (IOException | ClassNotFoundException e) {
            Log.e(MainActivity.TAG,"Unable to set Header",e);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            setSelectedItem(item.getItemId());
            DrawerLayout drawerLayout = findViewById(R.id.delivery_main);
            drawerLayout.close();
            return true;
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

    private void setSelectedItem(int itemId) {
        Toolbar toolbar = findViewById(R.id.admin_toolbar);
        if(itemId == R.id.admin_user_management_menuItem){
            toolbar.setTitle(R.string.admin_user_management);
            loadFragment(new AdminUserManagementFragment());
        }else if(itemId == R.id.admin_deleveryManagement_menuItem){
            toolbar.setTitle(R.string.admin_delivery_management);
            loadFragment(new AdminDeliveryManagementFragment());
        } else if (itemId == R.id.admin_analytics_menuItem) {
            loadFragment(new AdminAnalyticsFragment());
            toolbar.setTitle(R.string.admin_Analytics);
        }else if(itemId == R.id.admin_product_confirmation){
            loadFragment(new ProductConfirmationFragment());
            toolbar.setTitle(R.string.partner_products);
        }else{
            toolbar.setTitle(R.string.partner_dashboard);
            loadFragment(new AdminDashboardFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                .replace(R.id.admin_main_fragementContainer,fragment).commit();
    }

    private void setHeaderGeeting(TextView textViewHeaderGreeting) throws IOException, ClassNotFoundException {
        UserLogIn login = UserLogIn.getLogin(this);
        if(login.getDisplay_name() == null){
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(UserLogIn.EMAIL_FIELD, login.getEmail());
                Request request = new Request.Builder().url(BuildConfig.HOST_URL + "GetProfile")
                        .post(RequestBody.create(jsonObject.toString(), HttpClient.JSON)).build();
                HttpClient.getInstance().newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        promptFailed("1:Profile loading failed");
                        Log.e(MainActivity.TAG,e.getLocalizedMessage());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if(response.isSuccessful()){
                            Gson gson = new Gson();
                            Log.d(MainActivity.TAG,response.message());
                            String responseText = response.body().string();
                            Log.i(MainActivity.TAG,responseText);
                            JsonObject responseJson = gson.fromJson(responseText, JsonObject.class);
                            if(responseJson.has("ok")
                                    && responseJson.get("ok").getAsBoolean()){
                                String name = responseJson.getAsJsonObject("profile")
                                        .get(UserLogIn.DISPLAY_NAME_FIELD).getAsString();
                                runOnUiThread(()->{
                                    textViewHeaderGreeting.setText(String.format("Welcome! %s",name));
                                });
                                try {
                                    UserLogIn newLogin = UserLogIn.getLogin(AdminActivity.this);
                                    newLogin.setDisplay_name(name);
                                    newLogin.serialize(AdminActivity.this);
                                } catch (ClassNotFoundException e) {
                                    Log.e(MainActivity.TAG,e.getLocalizedMessage());
                                }

                            }else{
                                promptFailed("2:Profile loading failed");
                            }
                        }else{
                            promptFailed("3:Profile loading failed");
                        }
                    }
                });
        }else{
            textViewHeaderGreeting.setText(
                    String.format("Welcome! %s", login.getDisplay_name())
            );
        }
    }
    void promptFailed(String message){
        runOnUiThread(()->{
            Snackbar snackbar = Snackbar.make(findViewById(R.id.delivery_main), message, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Dismiss", v->{
                snackbar.dismiss();
            });
            snackbar.show();
        });
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

    private void initOnBackPressDispatcher(Context context) {
        getOnBackPressedDispatcher().addCallback(this,new OnBackPressedCallback(true){
            @Override
            public void handleOnBackPressed() {
                showExitConfirmation();
            }
            private void showExitConfirmation() {
                AlertDialog confirm = WanderDialog.confirm(
                        context,
                        "Are you sure you want to exit?");
                confirm.setButton(
                        DialogInterface.BUTTON_POSITIVE,
                        "Yes",
                        (dialog, which) -> finishAffinity());
                confirm.setButton(
                        DialogInterface.BUTTON_NEGATIVE,
                        "No",
                        (dialog, which) -> dialog.dismiss());
                confirm.show();
            }
        });
    }
}