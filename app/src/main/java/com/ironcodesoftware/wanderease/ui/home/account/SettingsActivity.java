package com.ironcodesoftware.wanderease.ui.home.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ironcodesoftware.wanderease.R;

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

        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        toolbar.setNavigationOnClickListener(v->{
            finish();
        });

        Button buttonLanguage = findViewById(R.id.settings_language_button);
        buttonLanguage.setOnClickListener(v->{

        });
        Button buttonPolicies = findViewById(R.id.settings_policies_button);
        buttonPolicies.setOnClickListener(v->{

        });
        Button buttonLogout = findViewById(R.id.settings_logout_button);
        buttonLogout.setOnClickListener(v->{

        });

    }

    private void logout() {
    }

    private void openLanguageDialog() {
    }
}