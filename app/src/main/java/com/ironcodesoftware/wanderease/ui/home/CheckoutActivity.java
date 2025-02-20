package com.ironcodesoftware.wanderease.ui.home;

import static com.ironcodesoftware.wanderease.MainActivity.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.HttpClient;
import com.ironcodesoftware.wanderease.model.Order;
import com.ironcodesoftware.wanderease.model.Product;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.WanderDialog;
import com.ironcodesoftware.wanderease.model.adaper.OrderItemAdapter;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;
import okhttp3.Request;
import okhttp3.Response;

public class CheckoutActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int PAYHERE_REQUEST  = 101;
    double orderTotal = 0;
    int totalItems = 0;

    String orderId;
    JsonArray productArray;
    private ArrayList<String> orderDocumentIDs;
    private AlertDialog loadingDialog;
    private String orderDocumentID;

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
            productArray = gson.fromJson(productList, JsonArray.class);
            loadProductList();
            loadDeliveryLocation();
            loadOrderSummary();
        }else{
            Log.e(TAG,"No intent for single product");
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
                try {
                    proceedToPayment();
                } catch (IOException | ClassNotFoundException e) {
                    Log.e(TAG, "Error",e);
                }
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
                    AlertDialog.Builder alert = WanderDialog.build(this, "Please turn on device location to continue", "Alert");
                    alert.setOnCancelListener(dialog -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }).show();

                }
            }

        });
    }

    private void proceedToPayment() throws IOException, ClassNotFoundException {
        loadingDialog = WanderDialog.loading(this,"Processing...");
        loadingDialog.show();
        orderId = String.valueOf(System.currentTimeMillis());
        saveOrder();
    }

    private void saveOrder() throws IOException, ClassNotFoundException {
        Calendar calendar = Calendar.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.app_package),
                MODE_PRIVATE);
        String locationString = sharedPreferences.getString(
                getString(R.string.location_field),
                getString(R.string.not_specified));
        JsonObject locationJson = new Gson().fromJson(locationString, JsonObject.class);

        HashMap<String,Object> orderMap = new HashMap<>();
        orderMap.put("orderId", orderId);
        orderMap.put("location", locationJson.toString());
        orderMap.put(Order.F_PRICE, orderTotal);
        orderMap.put(Order.F_STATE, Order.State.Pending.name());
        orderMap.put(Order.F_DATE, calendar.getTime());
        orderMap.put(Order.F_BUYER, UserLogIn.getLogin(this).getEmail());
       orderMap.put("items",productArray.toString());

        HashMap<String,JsonArray> sellerOrderMap = new HashMap<>();
        for (JsonElement element : productArray) {
            String sellerEmail = element.getAsJsonObject().get("item").getAsJsonObject().get("seller")
                    .getAsJsonObject().get("email").getAsString();
            JsonArray items;
            if(!sellerOrderMap.containsKey(sellerEmail)){
                items = new JsonArray();
                sellerOrderMap.put(sellerEmail,items);
            }else{
                items = sellerOrderMap.get(sellerEmail);
            }
            items.add(element.getAsJsonObject());
        }

        firestore.collection("Order").add(orderMap).addOnSuccessListener(document->{
            orderDocumentIDs = new ArrayList<>();
            orderDocumentID = document.getId();
            saveSellerOrders(sellerOrderMap);
        }).addOnFailureListener(e->{
            runOnUiThread(()->{
                loadingDialog.cancel();
            });
            Log.e(TAG,"Order saving error",e);
        });

    }

    private void saveSellerOrders(HashMap<String, JsonArray> sellerOrders) {

        sellerOrders.forEach((key, itemsArray) ->{
            HashMap<String,Object> sellerOrder = new HashMap<>();
            sellerOrder.put("order_id", orderId);
            sellerOrder.put("seller_email",key);
            sellerOrder.put("status", Order.State.Pending.name());
            sellerOrder.put("items", itemsArray.toString());
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("SellerOrder").add(sellerOrder)
                    .addOnSuccessListener(document->{
                        loadingDialog.cancel();
                        orderDocumentIDs.add(document.getId());
                        runOnUiThread(()->{
                            Toast.makeText(
                                    CheckoutActivity.this,
                                    "Order Saved",
                                    Toast.LENGTH_LONG
                            ).show();
                        });
                        gotoPaymentActivity();
                    })
                    .addOnFailureListener(e->{
                        runOnUiThread(()->{
                            loadingDialog.cancel();
                        });
                        Log.e(TAG,"Seller Order saving error",e);
                    });
            // TODO
            // send notification messages
//            firestore.collection("notification").add()
        } );
    }

    private void gotoPaymentActivity() {
        InitRequest req = new InitRequest();
        req.setMerchantId("1223792");
        req.setCurrency("LKR");
        req.setAmount(1000.00);
        req.setOrderId(orderId);
        req.setItemsDescription("Door bell wireless");
        req.setCustom1("This is the custom message 1");
        req.setCustom2("This is the custom message 2");
        req.getCustomer().setFirstName("Saman");
        req.getCustomer().setLastName("Perera");
        req.getCustomer().setEmail("samanp@gmail.com");
        req.getCustomer().setPhone("+94771234567");
        req.getCustomer().getAddress().setAddress("No.1, Galle Road");
        req.getCustomer().getAddress().setCity("Colombo");
        req.getCustomer().getAddress().setCountry("Sri Lanka");

        Intent intent = new Intent(this, PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
        startActivityForResult(intent, PAYHERE_REQUEST ); //unique request ID e.g. "11001"
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
            if (resultCode == Activity.RESULT_OK) {
                String msg;
                if (response != null)
                    if (response.isSuccess()) {
                        msg = "Activity result:" + response.getData().getMessage();
                        updateStock();
                    }else {
                        msg = "Result:" + response.getData().getMessage();
                    }
                else {
                    msg = "Result: no response";
                }
                Log.d(TAG, msg);

            } else if (resultCode == Activity.RESULT_CANCELED) {
                removeOrder();
            }
        }
    }

    private void removeOrder() {
        runOnUiThread(()->{
            loadingDialog = WanderDialog.loading(this,"Processing...");
            loadingDialog.show();
        });

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Order").document(orderDocumentID).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        for (String documentID : orderDocumentIDs) {
                            firestore.collection("SellerOrder").document(documentID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            runOnUiThread(()->{
                                                loadingDialog.cancel();
                                            });
                                            Log.e(TAG, "Remove order failed",e);
                                        }
                                    });
                        }
                        runOnUiThread(()->{
                            loadingDialog.cancel();
                            WanderDialog.cancel(CheckoutActivity.this, "Payment cancelled")
                                    .show();
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });



    }

    @SuppressLint("CheckResult")
    private void updateStock() {
        runOnUiThread(()->{
            loadingDialog = WanderDialog.loading(this,"Processing...");
            loadingDialog.show();
        });
        // TODO
        new Thread(()->{
            try {
                Response response = new HttpClient().post(
                        BuildConfig.HOST_URL + "UpdateStock"
                        , productArray.toString());
                if(response.isSuccessful()){
                    Gson gson = new Gson();
                    JsonObject responseJson = gson.fromJson(response.body().string(), JsonObject.class);
                    if(responseJson.has("ok") && responseJson.get("ok").getAsBoolean()){
                        runOnUiThread(()->{
                            AlertDialog alertDialog = WanderDialog.success(
                                    CheckoutActivity.this,
                                    "Order Confirmed");
                            alertDialog.setCancelable(false);
                            alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,"Back to Home",
                                    (dialog, which) -> {
                                        dialog.cancel();
                                        startActivity(new Intent(CheckoutActivity.this,
                                                HomeActivity.class));
                                        finish();
                                    });
                            alertDialog.show();
                        });
                    }

                }else{
                    Log.e(TAG, "Update stock unsuccessful");
                    runOnUiThread(()->{
                        loadingDialog.cancel();
                    });
                }
            } catch (IOException e) {
                Log.e(TAG, "Update stock failed",e);
                runOnUiThread(()->{
                    loadingDialog.cancel();
                });
            }
        }).start();


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDeliveryLocation();
    }

    private void loadOrderSummary() {
        for (JsonElement element : productArray) {
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

    }

    @SuppressLint("CheckResult")
    private void loadDeliveryLocation() {
        TextView textViewLocation = findViewById(R.id.place_order_delivery_location_textView);
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.app_package),
                MODE_PRIVATE);
        if(sharedPreferences.contains(getString(R.string.location_field))){
            String locationString = sharedPreferences.getString(
                    getString(R.string.location_field),
                    getString(R.string.not_specified));
            JsonObject locationJson = new Gson().fromJson(locationString, JsonObject.class);
            textViewLocation.setText(locationJson.get("address").getAsString());
        }else{
            textViewLocation.setText(R.string.not_specified);
        }
    }

    private void loadProductList() {
        RecyclerView recyclerView = findViewById(R.id.place_order_item_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new OrderItemAdapter(productArray,this));
    }
}