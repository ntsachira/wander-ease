package com.ironcodesoftware.wanderease.ui.delivery;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.Order;

public class DeliveryTaskViewActivity extends AppCompatActivity {

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delivery_task_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.delivery_task_view_activity_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        if(getIntent().hasExtra("items")){

            Gson gson = new Gson();
            JsonArray items = gson.fromJson(getIntent().getStringExtra(Order.F_ITEMS), JsonArray.class);
            JsonObject location = gson.fromJson(getIntent().getStringExtra(Order.F_LOCATION), JsonObject.class);

            loadMap(location);
            loadOrderSummary(items);
            setupCallButton();
            setupMarkButton();
            
            toolbar.setTitle(String.format("Order: %s", getIntent().getStringExtra(Order.F_ID)));

        }else{
            finish();
            Log.e(MainActivity.TAG,"No order dta found in intent");
        }
    }

    private void setupMarkButton() {
    }

    private void setupCallButton() {
    }

    private void loadOrderSummary(JsonArray items) {
    }

    private void loadMap(JsonObject location) {

    }
}