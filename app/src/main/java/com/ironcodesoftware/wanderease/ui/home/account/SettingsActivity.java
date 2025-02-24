package com.ironcodesoftware.wanderease.ui.home.account;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.os.LocaleListCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.WanderDialog;
import com.ironcodesoftware.wanderease.ui.login.LogInActivity;

import java.io.PipedOutputStream;
import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setStatusBarColor(getColor(R.color.white));

        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        toolbar.setNavigationOnClickListener(v->{
            finish();
        });

        Button buttonLanguage = findViewById(R.id.settings_language_button);
        buttonLanguage.setOnClickListener(v->{
            openLanguageDialog();
        });
        Button buttonPolicies = findViewById(R.id.settings_policies_button);
        buttonPolicies.setOnClickListener(v->{
            startActivity(new Intent(SettingsActivity.this, PoliciesActivity.class));
        });
        Button buttonLogout = findViewById(R.id.settings_logout_button);
        buttonLogout.setOnClickListener(v->{
            android.app.AlertDialog dialog = WanderDialog.confirm(this, "Are you sure you want to log out?");
            dialog.setCancelable(false);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancel",(dialog1, which) -> {
                dialog.cancel();
            });
            dialog.setButton(DialogInterface.BUTTON_POSITIVE,"Logout",(dialog1, which) -> {
                logout();
            });
            dialog.show();

        });


    }

    private void logout() {
        if(UserLogIn.clear(this)){
            Toast.makeText(this,"Logout success", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LogInActivity.class));
            finish();
        }
    }

    private void openLanguageDialog() {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String lang = sharedPreferences.getString("lang", "e");
        AlertDialog confirm = WanderDialog.confirm(SettingsActivity.this, getString(R.string.language_change_confirmation_message));
        if(lang.equals("e")){
            confirm.setButton(DialogInterface.BUTTON_POSITIVE,"YES",(dialog, which) -> {
                LocaleListCompat appLocale = LocaleListCompat.forLanguageTags("si");
                AppCompatDelegate.setApplicationLocales(appLocale);
                sharedPreferences.edit().putString("lang", "s").apply();
                recreate();
            });
        }else{
            confirm.setButton(DialogInterface.BUTTON_POSITIVE,"YES",(dialog, which) -> {
                LocaleListCompat appLocale = LocaleListCompat.forLanguageTags("en");
                AppCompatDelegate.setApplicationLocales(appLocale);
                sharedPreferences.edit().putString("lang", "e").apply();
                recreate();
            });
        }
        confirm.setButton(DialogInterface.BUTTON_NEGATIVE,"No",(dialog, which) -> {

        });
        confirm.show();
    }
}