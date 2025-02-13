package com.ironcodesoftware.wanderease.ui.partner;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;

public class PartnerProductFragment extends Fragment {

    Uri selectedImageUri;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
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

    private void resetImage() {
        ImageView imageView = getActivity().findViewById(R.id.new_product_imageView);
        imageView.setImageResource(R.drawable.empty_image);
        Button buttonUploadImage = getActivity().findViewById(R.id.new_product_button_upload_Image);
        buttonUploadImage.setVisibility(View.VISIBLE);
        ImageButton buttonClose = getActivity().findViewById(R.id.new_product_cancel_imageButton);
        buttonClose.setVisibility(View.INVISIBLE);
        selectedImageUri = null;
    }

    private void setSelectedImage(Uri uri) {
        Log.d(MainActivity.TAG, "Selected URI: " + uri);
        ImageView imageView = getActivity().findViewById(R.id.new_product_imageView);
        imageView.setImageURI(uri);
        Button buttonUploadImage = getActivity().findViewById(R.id.new_product_button_upload_Image);
        buttonUploadImage.setVisibility(View.INVISIBLE);
        ImageButton buttonClose = getActivity().findViewById(R.id.new_product_cancel_imageButton);
        buttonClose.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_partner_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonUploadImage = view.findViewById(R.id.new_product_button_upload_Image);
        buttonUploadImage.setOnClickListener(v->{
            launceImagePicker();
        });
        ImageButton buttonClose = getActivity().findViewById(R.id.new_product_cancel_imageButton);
        buttonClose.setOnClickListener(v -> {
            resetImage();
        });
    }

    private void launceImagePicker() {
        pickMedia.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()
        );
    }
}