package com.ironcodesoftware.wanderease.ui.partner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.MediaHandler;
import com.ironcodesoftware.wanderease.model.Vehicle;
import com.ironcodesoftware.wanderease.model.WanderDialog;

import java.io.File;


public class PartnerVehicleNewFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied;
    private GoogleMap map;
    private Uri selectedImageUri;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickMedia = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        setSelectedImage(uri);
                    } else {
                        Log.d(MainActivity.TAG, "No media selected");
                    }
                });
    }

    private void setSelectedImage(Uri uri) {
        Log.d(MainActivity.TAG, "Selected URI: " + uri);
        ImageView imageView = getView().findViewById(R.id.partner_vehicle_new_imageView);
        imageView.setImageURI(uri);
        Button buttonUploadImage = getView().findViewById(R.id.partner_vehicle_new_upload_image_button);
        buttonUploadImage.setVisibility(View.INVISIBLE);
        ImageButton buttonClose = getView().findViewById(R.id.partner_vehicle_new_delete_imageButton);
        buttonClose.setVisibility(View.VISIBLE);
    }

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

       setupButtons(view);

       setupSpinners(view);

    }

    private void setupSpinners(View view) {
        AutoCompleteTextView textViewType = view.findViewById(R.id.partner_vehicle_new_vehicle_Type_spinner);
        textViewType.setAdapter(new ArrayAdapter<String>(
                getContext(),
                R.layout.single_spinner_item,
                Vehicle.VEHICLE_TYPES
        ));
        textViewType.setOnClickListener(v -> {
            textViewType.showDropDown();
        });
        AutoCompleteTextView textViewGearMode = view.findViewById(R.id.partner_vehicle_new_gear_mode_spinner);
        textViewGearMode.setAdapter(new ArrayAdapter<String>(
                getContext(),
                R.layout.single_spinner_item,
                Vehicle.GEAR_MODES
        ));
        textViewGearMode.setOnClickListener(v -> {
            textViewGearMode.showDropDown();
        });
        AutoCompleteTextView textViewAvailability = view.findViewById(R.id.partner_vehicle_new_availability_input);
        textViewAvailability.setAdapter(new ArrayAdapter<String>(
                getContext(),
                R.layout.single_spinner_item,
                Vehicle.STATUS_LIST
        ));
        textViewAvailability.setOnClickListener(v -> {
            textViewAvailability.showDropDown();
        });
    }

    private void setupButtons(View view) {
        Button buttonSelectImage = view.findViewById(R.id.partner_vehicle_new_upload_image_button);
        buttonSelectImage.setOnClickListener(v -> {
            launceImagePicker();
        });
        ImageButton buttonResetImage = view.findViewById(R.id.partner_vehicle_new_delete_imageButton);
        buttonResetImage.setOnClickListener(v -> {
            resetImage(view);
        });
        Button buttonSave = view.findViewById(R.id.partner_vehicle_new_save_vehicle_button);
        buttonSave.setOnClickListener(v -> {
            saveVehicle(view);
        });
    }

    private void saveVehicle(View view) {
        JsonObject productDetails = validateProductDetails(view);
        File imageFile = MediaHandler.uriToFile(getContext(), selectedImageUri);
        if(productDetails != null && imageFile!=null){

        }
    }

    private JsonObject validateProductDetails(View view) {
        return null;
    }

    private void resetImage(View view) {
    }

    private void launceImagePicker() {
            pickMedia.launch(
                    new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build()
            );
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