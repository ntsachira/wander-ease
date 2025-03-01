package com.ironcodesoftware.wanderease.ui.home.search;



import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.AddressRequest;
import com.ironcodesoftware.wanderease.model.Order;
import com.ironcodesoftware.wanderease.model.SQLiteHelper;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.Vehicle;
import com.ironcodesoftware.wanderease.model.WanderDialog;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SingleVehicleActivity extends AppCompatActivity {

    private static final int CALL_PERMISSION_REQUEST_CODE = 119;
    private UserLogIn login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_vehicle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        try {
            login = UserLogIn.getLogin(this);
        } catch (IOException | ClassNotFoundException e) {
            finish();
        }

        getWindow().setStatusBarColor(getColor(R.color.white));
        if(!getIntent().hasExtra("vehicle")){
            finish();
        }

        Gson gson = new Gson();
        JsonObject vehicle = gson.fromJson(getIntent().getStringExtra("vehicle"), JsonObject.class);
        loadVehicleDetails(vehicle);


        Toolbar toolbar = findViewById(R.id.single_vehicle_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        toolbar.setTitle(String.format("%s: %s",
                getString(R.string.home_search_vehicleRental),
                vehicle.get("title").getAsString()));

        loadOwnerDetails(vehicle.get("owner").getAsJsonObject());

        loadPickUpLocation(
                gson.fromJson(vehicle.get("pickupLocation").getAsString().replace("\"",""), JsonObject.class)
        );

        Button buttonBookNow  = findViewById(R.id.single_vehicle_book_now_button);
        buttonBookNow.setOnClickListener(v -> {
            bookNow(vehicle);
        });
    }

    private void bookNow(JsonObject vehicle) {
        View view = LayoutInflater.from(this).inflate(R.layout.vehicle_booking_dialog_layout, null);
        CalendarView calendarView = view.findViewById(R.id.vehicle_booking_calendarView);
        EditText editTextDays = view.findViewById(R.id.vehicle_booking_days_editText);
        ImageView addButton = view.findViewById(R.id.vehicle_booking_days_increment_imageView);
        ImageView removeButton = view.findViewById(R.id.booking_days_decrement_imageView);
        Button buttonBook = view.findViewById(R.id.vehicle_booking_positive_button);
        Button buttonCancel = view.findViewById(R.id.vehicle_booking_cancel_button);

        calendarView.setMinDate(System.currentTimeMillis()+24*60*60*1000);
        addButton.setOnClickListener(v -> {
            int days = Integer.parseInt(editTextDays.getText().toString());
            if(days < 7){
                editTextDays.setText(String.valueOf(++days));
            }
        });
        removeButton.setOnClickListener(v -> {
            int days = Integer.parseInt(editTextDays.getText().toString());
            if(days > 1){
                editTextDays.setText(String.valueOf(--days));
            }
        });
        AlertDialog dialog = new AlertDialog.Builder(this).setView(view)
                .setCancelable(false).create();
        buttonCancel.setOnClickListener(v -> {
            dialog.cancel();
        });
        buttonBook.setOnClickListener(v -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(calendarView.getDate());
            int days = Integer.parseInt(editTextDays.getText().toString());
            String date = dateFormat.format(calendar.getTime());
            HashMap<String,Object> bookingMap = new HashMap<>();
            JsonObject profile = SQLiteHelper.getProfile(SingleVehicleActivity.this);
            bookingMap.put(Vehicle.F_START_DATE, date);
            bookingMap.put(Vehicle.F_DAYS, days);
            bookingMap.put(Vehicle.F_VEHICLE, vehicle.toString());
            bookingMap.put(Vehicle.F_USER, profile.get("email").getAsString());
            bookingMap.put(Vehicle.F_RENTAL_STATUS, Order.State.Pending.name());
            bookingMap.put(Vehicle.F_CREATED_DATE, dateFormat.format(new Date()));
            bookingMap.put(Vehicle.F_REVIEW_STATUS, Order.State.Pending.name());


            android.app.AlertDialog loading = WanderDialog.loading(SingleVehicleActivity.this);
            loading.show();
            FirebaseFirestore.getInstance().collection("booking")
                    .add(bookingMap)
                    .addOnSuccessListener(documentReference -> {
                        v.post(()->{
                            loading.cancel();
                            WanderDialog.success(SingleVehicleActivity.this,
                                            "Booking saved successfully")
                                    .show();
                            dialog.cancel();
                        });
                    })
                    .addOnFailureListener(e->{
                        v.post(()->{
                            loading.cancel();
                            WanderDialog.cancel(SingleVehicleActivity.this,
                                            "Failed to save booking")
                                    .show();
                            dialog.cancel();
                        });
                    });

        });
        dialog.show();



    }

    private void loadPickUpLocation(JsonObject location) {
        LatLng latLng = new LatLng(location.get("lat").getAsDouble(), location.get("lon").getAsDouble());
        TextView textViewAddress = findViewById(R.id.single_vehicle_pickup_address_textView);
        ImageButton buttonCopy = findViewById(R.id.single_vehicle_copy_address_imageButton);
        buttonCopy.setVisibility(View.INVISIBLE);
        textViewAddress.setText(R.string.loading);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.load_blink);
        textViewAddress.setAnimation(animation);
        animation.start();

        new AddressRequest(location.get("lat").getAsString(),location.get("lon").getAsString()){
            @Override
            protected void onResponseSuccess(String address) {
                runOnUiThread(()->{
                    textViewAddress.setText(address);
                    textViewAddress.clearAnimation();
                    buttonCopy.setVisibility(View.VISIBLE);

                    buttonCopy.setOnClickListener(v -> {
                        copyToClipboard(String.format("%s,%s", latLng.latitude,latLng.longitude));
                        new Thread(()->{

                            runOnUiThread(()->{
                                buttonCopy.setBackground(getDrawable(R.drawable.dashboard_card_background));
                            });
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {

                            }
                            runOnUiThread(()->{
                                buttonCopy.setBackground(getDrawable(lk.payhere.androidsdk.R.drawable.payment_option_back));
                            });
                        }).start();
                    });

                });
            }
        };

        loadMap(latLng);
    }

    private void copyToClipboard(String location) {
        ClipboardManager clipboard  = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("location", location));
        Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void loadMap(LatLng latLng) {
        SupportMapFragment mapFragment = new SupportMapFragment();

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                .add(R.id.single_vehicle_map_frame,mapFragment).commit();

        mapFragment.getMapAsync(googleMap -> {
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder().target(latLng).zoom(12).build()
            ));
            googleMap.addMarker(
                    new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_navigation_marker_icon))
                            .title(getString(R.string.pickup_location))
            );
            googleMap.getUiSettings().setScrollGesturesEnabled(false);

        });
    }

    private void loadOwnerDetails(JsonObject owner) {
        TextView textViewName = findViewById(R.id.single_vehicle_owner_name_textView49);
        textViewName.setText(String.format("Name: %s", owner.get(UserLogIn.DISPLAY_NAME_FIELD).getAsString()));
        ImageButton buttonCall = findViewById(R.id.singel_vehicle_call_imageButton);
        ImageButton buttonMessage = findViewById(R.id.single_vehicle_message_imageButton);
        buttonCall.setOnClickListener(v -> {
            callCourier(owner.get(UserLogIn.MOBILE_FIELD).getAsString());
        });
        buttonMessage.setOnClickListener(v -> {
            openWhatsApp(owner.get(UserLogIn.MOBILE_FIELD).getAsString().replace("0", "+94"));
        });

    }

    private void openWhatsApp(String phoneNumber) {
        try {
            String url = "https://wa.me/" + phoneNumber.replace("+", "").replace(" ", "");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hasCallPermission() {
        if(checkSelfPermission(Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
            return true;
        }
        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST_CODE);
        return false;
    }

    private void callCourier(String mobile) {
        if(hasCallPermission()){
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(String.format("tel:%s",mobile)));
            startActivity(intent);
        }
    }

    private void loadVehicleDetails(JsonObject vehicle) {
        FrameLayout frameLayout = findViewById(R.id.single_vehicle_card_frame);
        View viewVehicle = LayoutInflater.from(this).inflate(R.layout.vehicle_item_layout, null);
        frameLayout.addView(viewVehicle);

        TextView textViewTitle = findViewById(R.id.vehicle_card_title_textView);
        TextView textViewGearMode = findViewById(R.id.vehicle_card_gearMode_textView);
        TextView textViewAvailability = findViewById(R.id.vehicle_card_availability_textView);
        TextView textViewPrice = findViewById(R.id.vehicle_card_price_textView);
        TextView textViewHighlights = findViewById(R.id.vehicle_card_highlights_textView);
        ImageView imageView = findViewById(R.id.vehicle_card_image_imageView);

        String id = vehicle.get("id").getAsString();

        textViewTitle.setText(vehicle.get("title").getAsString());
        textViewGearMode.setText(String.format("Gear Mode: %s",
                Vehicle.GEAR_MODES.get(vehicle.get("gearMode").getAsInt())));
        textViewAvailability.setText(String.format("Status: %s",
                Vehicle.STATUS_LIST.get(vehicle.get("availability").getAsInt())));
        textViewPrice.setText(String.format("Rs. %s",
                new DecimalFormat().format(vehicle.get("pricePerDay").getAsDouble())));
        textViewHighlights.setText(vehicle.get("highlights").getAsString());

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.load_blink);
        imageView.setAnimation(animation);
        animation.start();
        new Thread(()->{
            try {
                URL url = new URL(BuildConfig.HOST_URL + String.format("product/%s.png", id));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input =  connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                runOnUiThread(()->{
                    imageView.clearAnimation();
                    imageView.setImageBitmap(bitmap);
                });
                connection.disconnect();
            } catch (Exception e) {
                Log.e(MainActivity.TAG,e.getLocalizedMessage(),e);
            }
        }).start();
    }
}