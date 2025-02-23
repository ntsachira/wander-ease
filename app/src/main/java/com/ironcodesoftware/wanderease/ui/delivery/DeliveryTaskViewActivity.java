package com.ironcodesoftware.wanderease.ui.delivery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.Delivery;
import com.ironcodesoftware.wanderease.model.HttpClient;
import com.ironcodesoftware.wanderease.model.Notification;
import com.ironcodesoftware.wanderease.model.Order;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.WanderDialog;
import com.ironcodesoftware.wanderease.model.adaper.OrderItemAdapter;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DeliveryTaskViewActivity extends AppCompatActivity {

    final int CALL_PERMISSION_REQUEST_CODE = 119;
    String mobile;
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
        Button buttonMark = findViewById(R.id.delivery_taskView_done_button);

        buttonMark.setOnClickListener(v->{
            AlertDialog dialogConfirmation = WanderDialog.confirm(this, String.format("Confirm Order delivery for Order: %s",
                    getIntent().getStringExtra(Order.F_ID)));
            dialogConfirmation.setButton(DialogInterface.BUTTON_POSITIVE,"Confirm",(dialog, which) -> {
                updateOrder();
                dialog.cancel();
            });
            dialogConfirmation.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancel",(dialog, which) -> {
                dialog.cancel();
            });
            dialogConfirmation.show();
        });
    }

    private void updateOrder() {
        AlertDialog loading = WanderDialog.loading(this, "Loading...");
        loading.show();
        HashMap<String,Object> orderUpdates = new HashMap<>();
        orderUpdates.put(Order.F_STATE, Order.State.Delivered.name());
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(Order.F_COLLECTION)
                .document(getIntent().getStringExtra("document_id"))
                .update(orderUpdates)
                .addOnSuccessListener( unused -> {
                    firestore.collection(Delivery.F_COLLECTION)
                            .whereEqualTo(Delivery.F_ORDER_ID, getIntent().getStringExtra(Order.F_ID))
                            .get().addOnCompleteListener(task->{
                                if(task.isSuccessful()){
                                    HashMap<String,Object> deliveryUpdates = new HashMap<>();
                                    deliveryUpdates.put(Delivery.F_STATUS, Delivery.State.Delivered.name());
                                    firestore.collection(Delivery.F_COLLECTION)
                                            .document(task.getResult().getDocuments().get(0).getId())
                                            .update(deliveryUpdates)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    sendNotification(getIntent().getStringExtra(Order.F_BUYER),
                                                            String.format("Order: %s has delivered",
                                                                    getIntent().getStringExtra(Order.F_ID)));
                                                    runOnUiThread(()->{
                                                        loading.cancel();
                                                        Toast.makeText(
                                                                        DeliveryTaskViewActivity.this,
                                                                        "Task updated success",Toast.LENGTH_LONG )
                                                                .show();
                                                        finish();
                                                    });

                                                }
                                            }).addOnFailureListener(e->{
                                                Log.e(MainActivity.TAG,"3:Order update error",e);
                                                runOnUiThread(()->{
                                                    loading.cancel();
                                                    Snackbar snackbar = Snackbar.make(findViewById(R.id.main), "3:Failed to update order", Snackbar.LENGTH_INDEFINITE);
                                                    snackbar.setAction("Ok", v -> {
                                                        snackbar.dismiss();
                                                    }).show();
                                                });
                                            });
                                }
                            })
                            .addOnFailureListener(e->{
                                Log.e(MainActivity.TAG,"2:Order update error",e);
                                runOnUiThread(()->{
                                    loading.cancel();
                                    Snackbar snackbar = Snackbar.make(findViewById(R.id.main), "2:Failed to update order", Snackbar.LENGTH_INDEFINITE);
                                    snackbar.setAction("Ok", v -> {
                                        snackbar.dismiss();
                                    }).show();
                                });
                            });
                })
                .addOnFailureListener(e->{
                    Log.e(MainActivity.TAG,"1:Order update error",e);
                    runOnUiThread(()->{
                        loading.cancel();
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.main), "1:Failed to update order", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("Ok", v -> {
                            snackbar.dismiss();
                        }).show();
                    });
                });
    }

    private void sendNotification(String email,String message) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        HashMap<String,Object> notificationMap = new HashMap<>();
        notificationMap.put(Notification.F_MESSAGE, message);
        notificationMap.put(Notification.F_TIME, Calendar.getInstance().getTime());
        notificationMap.put(Notification.F_USER,email);
        notificationMap.put(Notification.F_STATUS, Notification.State.Not_Seen.toString());
        firestore.collection(Notification.COLLECTION)
                .add(notificationMap)
                .addOnCompleteListener(task -> {
                    runOnUiThread(()->{
                        Toast.makeText(
                                        DeliveryTaskViewActivity.this,
                                        "Notification sent",Toast.LENGTH_LONG )
                                .show();
                        finish();
                    });
                })
                .addOnFailureListener(e->{
                    Log.e(MainActivity.TAG,"1:Order update error",e);
                    runOnUiThread(()->{
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.main),
                                "1:Failed to send notification",
                                Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("Ok", v -> {
                            snackbar.dismiss();
                        }).show();
                    });
                });
    }

    @SuppressLint("CheckResult")
    private void setupCallButton() {
        AlertDialog loading = WanderDialog.loading(this, "Loading...");
        loading.show();

        Button buttonCall = findViewById(R.id.delivery_taskView_call_button);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(
                    UserLogIn.EMAIL_FIELD,getIntent().getStringExtra(Order.F_BUYER));
            Request request = new Request.Builder().url(BuildConfig.HOST_URL + String.format("GetProfile"))
                    .post(RequestBody.create(jsonObject.toString(), HttpClient.JSON)).build();
            HttpClient.getInstance().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(MainActivity.TAG,"Profile load error",e);
                    runOnUiThread(()->{
                        loading.cancel();
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.main), "1:Failed to load data", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("Ok", v -> {
                            snackbar.dismiss();
                        }).show();
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    runOnUiThread(loading::cancel);
                    if(response.isSuccessful()){
                        String responseText = response.body().string();
                        JsonObject responseJson = new Gson().fromJson(responseText, JsonObject.class);
                        if(responseJson.has("ok") && responseJson.get("ok").getAsBoolean()){
                            String mobile = responseJson.get("profile").getAsJsonObject().get("mobile").getAsString();
                            DeliveryTaskViewActivity.this.mobile = mobile;
                            runOnUiThread(()->{
                                buttonCall.setOnClickListener(v -> {
                                    if(hasCallPermission()){
                                        callBuyer();
                                    }
                                });
                            });
                        }else{
                            runOnUiThread(()->{
                                Snackbar snackbar = Snackbar.make(
                                        findViewById(R.id.main),
                                        "3:Failed to load data",
                                        Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction("Ok", v -> {
                                    snackbar.dismiss();
                                }).show();
                            });
                        }
                    }else{
                        runOnUiThread(()->{
                            Snackbar snackbar = Snackbar.make(
                                    findViewById(R.id.main),
                                    "2:Failed to load data",
                                    Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("Ok", v -> {
                                snackbar.dismiss();
                            }).show();
                        });
                    }
                }
            });


    }

    private boolean hasCallPermission() {
        if(checkSelfPermission(Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED){
            return true;
        }
        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST_CODE);
        return false;
    }

    private void callBuyer() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(String.format("tel:%s",mobile)));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);
        if(requestCode == CALL_PERMISSION_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                callBuyer();
            }else{
                WanderDialog.cancel(DeliveryTaskViewActivity.this, "Call Permission denied").show();
            }
        }
    }

    private void loadOrderSummary(JsonArray items) {
        RecyclerView recyclerView = findViewById(R.id.delivery_taskView_items_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new OrderItemAdapter(items,this));

        TextView textViewOrderId = findViewById(R.id.delivery_taskView_orderId_textView);
        textViewOrderId.setText(getIntent().getStringExtra(Order.F_ID));
        TextView textViewItemTotal = findViewById(R.id.delivery_taskView_item_totak_textView);
        int count = 0;
        for (JsonElement item : items) {
            count+=item.getAsJsonObject().get("qty").getAsInt();
        }
        textViewItemTotal.setText(String.valueOf(count));

        TextView textViewTotalAmount = findViewById(R.id.delivery_taskView_total_amount_textView);
        textViewTotalAmount.setText(String.format("Rs. %s",
                new DecimalFormat().format(getIntent().getDoubleExtra(Order.F_PRICE,0))));
    }

    private void loadMap(JsonObject location) {
        TextView textViewAddress = findViewById(R.id.delivery_taskView_delivery_address_textView);
        textViewAddress.setText(location.get("address").getAsString());
        LatLng latLng = new LatLng(location.get("lat").getAsDouble(), location.get("lon").getAsDouble());
        SupportMapFragment mapFragment = new SupportMapFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.delivery_taskView_map_framelayout, mapFragment).commit();

        mapFragment.getMapAsync(googleMap -> {
            googleMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder().target(latLng)
                                    .zoom(15).build()
                    )
            );
            googleMap.addMarker(
                    new MarkerOptions().position(latLng).title("Delivery Location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_navigation_marker_icon))
            );
        });


    }
}