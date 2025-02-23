package com.ironcodesoftware.wanderease.ui.partner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.WanderDialog;


public class PartnerVehicleNewFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied;
    private GoogleMap map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_partner_vehicle_new, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       try{
           loadMap(view);
       } catch (Exception e) {
           Log.e(MainActivity.TAG, "Hena awl");
       }


    }

    private void loadMap(View view) {
        SupportMapFragment mapFragment = new SupportMapFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.partner_vehicle_new_map_frameLayout, mapFragment)
                .setReorderingAllowed(true).commit();


        mapFragment.getMapAsync(googleMap -> {
            map = googleMap;

            googleMap.setOnCameraMoveListener(() -> {
                ScrollView scrollView = view.findViewById(R.id.partner_vehicle_new_scrollview);
                scrollView.requestDisallowInterceptTouchEvent(true);
            });
            setCurrentLocation(view);

            googleMap.setOnMapClickListener(latLng -> {
                googleMap.clear();
                map.addMarker(
                        new MarkerOptions().position(latLng).title("Selected Location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pick_pin))
                );
            });
        });
    }

    private void setCurrentLocation(View view) {
        if(permissionDenied){
            showMissingPermissionError();
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showMissingPermissionError();
            requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        LocationManager locationManager = getActivity().getSystemService(LocationManager.class);

        Location myLocation = locationManager.getLastKnownLocation(LocationManager.FUSED_PROVIDER);

        if(myLocation != null){

            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

            map.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(latLng)
                                    .zoom(15).build()
                    )

            );

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode != LOCATION_PERMISSION_REQUEST_CODE){
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if(grantResults[0] != PackageManager.PERMISSION_GRANTED
                || grantResults[1] != PackageManager.PERMISSION_GRANTED){
            permissionDenied = true;
            showMissingPermissionError();
        }else{
            loadMap(getView());
        }
    }


    private void showMissingPermissionError() {
        WanderDialog.build(getContext(), "Cant access your device location").show();
    }
}