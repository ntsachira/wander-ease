package com.ironcodesoftware.wanderease.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.location.LocationManagerCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.WanderDialog;

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
    }

    private void loadMap() {
        SupportMapFragment supportMapFragment = new SupportMapFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.delivery_location_map_fragmentLayout,supportMapFragment).commit();

        supportMapFragment.getMapAsync(googleMap -> {
            map = googleMap;
            setCurrentLocation(googleMap);
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
            googleMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()))
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