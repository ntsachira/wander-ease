package com.ironcodesoftware.wanderease.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.renderscript.RenderScript;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.HttpClient;
import com.ironcodesoftware.wanderease.model.Product;
import com.ironcodesoftware.wanderease.model.WanderDialog;
import com.ironcodesoftware.wanderease.model.adaper.OrderItemAdapter;
import com.ironcodesoftware.wanderease.model.adaper.ProductAdapter;

import java.io.IOException;
import java.text.DecimalFormat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class CheckoutActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    double orderTotal = 0;
    int totalItems = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.order_checkout_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        String productList = getIntent().getStringExtra("productList");
        Gson gson = new Gson();
        if(productList != null){
            loadProductList(gson.fromJson(productList, JsonArray.class));
            loadDeliveryLocation();
            loadOrderSummary(gson.fromJson(productList, JsonArray.class));
        }else{
            Log.e(MainActivity.TAG,"No intent for single product");
            finish();
        }

        Button buttonPlaceOrder = findViewById(R.id.place_order_button);
        buttonPlaceOrder.setOnClickListener(v->{
            SharedPreferences sharedPreferences = getSharedPreferences(
                    getString(R.string.app_package),
                    MODE_PRIVATE);
            if(!sharedPreferences.contains(getString(R.string.location_field))){
                WanderDialog.build(this, "Please select your delivery location").show();
            }else{
                // TODO
                // save order in firebase
                // proceed to payment
            }
        });

        Button buttonSelectLocation = findViewById(R.id.place_order_select_location_button);
        buttonSelectLocation.setOnClickListener(v->{
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        LOCATION_PERMISSION_REQUEST_CODE);
                return;
            }
            LocationManager locationManager = getSystemService(LocationManager.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if(locationManager.isLocationEnabled()){
                    startActivity(new Intent(CheckoutActivity.this,DeliveryLocationActivity.class));
                }else{
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }

        });
    }



    private void loadOrderSummary(JsonArray itemArray) {
        for (JsonElement element : itemArray) {
            JsonObject jsonObject = element.getAsJsonObject();
            int qty = jsonObject.get("qty").getAsInt();
            totalItems += qty;
            JsonObject item = jsonObject.get("item").getAsJsonObject();
            double price = item.get(Product.F_PRICE).getAsDouble();
            orderTotal += (price*qty);
        }
        TextView textViewTotalItems = findViewById(R.id.place_order_item_count_textView);
        TextView textViewOrderTotal = findViewById(R.id.place_order_total_amount_textView);
        Button buttonPlaceOrder = findViewById(R.id.place_order_button);

        DecimalFormat decimalFormat = new DecimalFormat();
        textViewOrderTotal.setText(String.format("Rs. %s",decimalFormat.format(orderTotal)));
        textViewTotalItems.setText(String.valueOf(totalItems));

        buttonPlaceOrder.setText(String.format("Total Rs. %s : Place Order",decimalFormat.format(orderTotal)));

        TextView textViewLocation = findViewById(R.id.place_order_delivery_location_textView);
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.app_package),
                MODE_PRIVATE);
        if(sharedPreferences.contains(getString(R.string.location_field))){
            // TODO
            // set delivery location
        }else{
            textViewLocation.setText(R.string.not_specified);
        }
    }

    private void loadDeliveryLocation() {
    }

    private void loadProductList(JsonArray productListJsonObject) {
        RecyclerView recyclerView = findViewById(R.id.place_order_item_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new OrderItemAdapter(productListJsonObject,this));
    }
}