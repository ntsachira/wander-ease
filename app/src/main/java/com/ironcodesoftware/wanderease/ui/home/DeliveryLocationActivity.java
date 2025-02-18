package com.ironcodesoftware.wanderease.ui.home;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.location.LocationManagerCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.GsonBuildConfig;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.HttpClient;
import com.ironcodesoftware.wanderease.model.WanderDialog;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class DeliveryLocationActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied;
    private GoogleMap map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delivery_location);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.deleiver_location_toolbar);
        toolbar.setNavigationOnClickListener(v->{
            finish();
        });

        loadMap();

        Button buttonConfirmLocation = findViewById(R.id.delevery_location_save_current_location_button);
        buttonConfirmLocation.setOnClickListener(v->{
            requestAddress();
        });
    }

    private void saveLocation(String address) {
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.app_package),
                MODE_PRIVATE);
        JsonObject location = new JsonObject();
        location.addProperty("lat", map.getCameraPosition().target.latitude);
        location.addProperty("lon", map.getCameraPosition().target.longitude);
        location.addProperty("address", address);
        sharedPreferences.edit()
                .putString(getString((R.string.location_field)),location.toString())
                .apply();
        finish();
    }

    private void requestAddress() {
        Request request = new Request.Builder().url(
                String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s",
                        map.getCameraPosition().target.latitude,
                        map.getCameraPosition().target.longitude,
                        BuildConfig.MAPS_API_KEY
                ))
                .build();
        HttpClient.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(MainActivity.TAG, "Geo location address request failed",e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    JsonObject goeDataJson = new Gson().fromJson(response.body().string(), JsonObject.class);
                    JsonObject results = goeDataJson.get("results").getAsJsonArray().get(0).getAsJsonObject();
                    if(isSouthernProvince(results)){
                        String address = results.get("formatted_address").getAsString();
                        runOnUiThread(()->{
                            saveLocation(address);
                        });
                    }else{
                        runOnUiThread(()->{
                            WanderDialog.build(
                                    DeliveryLocationActivity.this,
                                    "Please select a location within South of Sri lanka")
                                    .show();
                        });
                    }

                }
            }
        });
    }

    private boolean isSouthernProvince(JsonObject results) {
        for (JsonElement element : results.get("address_components").getAsJsonArray()) {
            JsonObject jsonObject = element.getAsJsonObject();
            if(jsonObject.get("types").getAsJsonArray().get(0).getAsString().equals("administrative_area_level_1")
            && jsonObject.get("long_name").getAsString().equals("Southern Province")){
                return true;
            }
        }
        return false;
    }

    private void loadMap() {
        SupportMapFragment supportMapFragment = new SupportMapFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.delivery_location_map_fragmentLayout,supportMapFragment).commit();

        supportMapFragment.getMapAsync(googleMap -> {
            map = googleMap;
            setCurrentLocation(googleMap);
            googleMap.setOnCameraMoveListener(() -> {
                googleMap.clear();
                CameraPosition cameraPosition = googleMap.getCameraPosition();

                googleMap.addMarker(
                        new MarkerOptions().position(cameraPosition.target).title("Selected Location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_navigation_marker_icon))
                );
                LatLng target = googleMap.getCameraPosition().target;
                TextView textView = findViewById(R.id.delevery_location_current_location_textView);
                textView.setText(String.format("Lat: %s, Lon: %s", target.latitude, target.longitude));

            });
        });


    }

    private void setCurrentLocation(GoogleMap googleMap) {
        if(permissionDenied){
            showMissingPermissionError();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showMissingPermissionError();
            requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        LocationManager locationManager = getSystemService(LocationManager.class);

        Location myLocation = locationManager.getLastKnownLocation(LocationManager.FUSED_PROVIDER);

        if(myLocation != null){
            TextView textView = findViewById(R.id.delevery_location_current_location_textView);
            textView.setText(String.format("Lat: %s, Lon: %s", myLocation.getLatitude(), myLocation.getLongitude()));
            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            googleMap.addMarker(
                    new MarkerOptions().position(latLng).title("Selected Location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_navigation_marker_icon))
            );
            googleMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(latLng)
                                    .zoom(15).build()
                    )

            );

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        if(requestCode != LOCATION_PERMISSION_REQUEST_CODE){
            super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);
            return;
        }
        if(grantResults[0] != PackageManager.PERMISSION_GRANTED
                || grantResults[1] != PackageManager.PERMISSION_GRANTED){
            permissionDenied = true;
            showMissingPermissionError();
        }else{
            loadMap();
        }
    }
    private void showMissingPermissionError() {
        WanderDialog.build(this, "Cant access your device location").show();
    }
}