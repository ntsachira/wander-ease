package com.ironcodesoftware.wanderease.ui.delivery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.ui.admin.AdminSettingsActivity;
import com.ironcodesoftware.wanderease.ui.home.MessageFragment;

public class DeliveryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delivery);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.delivery_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setStatusBarColor(getColor(R.color.white));
        loadFragment(new DeliveryNewTaskFragment());

        BottomNavigationView bottomNavigationView = findViewById(R.id.delivery_bottomNavigationMenu);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            changeFragment(item.getItemId());
            return true;
        });

        ImageButton buttonSettings = findViewById(R.id.delivery_settings_imageButton);
        buttonSettings.setOnClickListener(v->{
            startActivity(new Intent(DeliveryActivity.this, AdminSettingsActivity.class));
        });
    }

    private void changeFragment(int itemId) {
        if(itemId == R.id.delivery_completed_tasks_menu_item){
            loadFragment(new DeliveryCompletedTaskFragment());
        } else if (itemId == R.id.delivery_messages_menuitem) {
            loadFragment(new MessageFragment());
        }else{
            loadFragment(new DeliveryNewTaskFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.delivery_fragmentContainerView,fragment).setReorderingAllowed(true)
                .commit();
    }

}