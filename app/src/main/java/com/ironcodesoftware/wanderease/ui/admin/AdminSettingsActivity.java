package com.ironcodesoftware.wanderease.ui.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.WanderDialog;
import com.ironcodesoftware.wanderease.ui.login.LogInActivity;

public class AdminSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button buttonLogout = findViewById(R.id.admin_settings_logout_button);
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

        Toolbar toolbar = findViewById(R.id.admin_settings_toolbar_toolbar);
        toolbar.setNavigationOnClickListener(v->{
            finish();
        });
    }
    private void logout() {
        if(UserLogIn.clear(this)){
            Toast.makeText(this,"Logout success", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LogInActivity.class));
            this.finish();
        }
    }
}