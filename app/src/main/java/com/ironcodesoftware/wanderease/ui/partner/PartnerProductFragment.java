package com.ironcodesoftware.wanderease.ui.partner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.HttpClient;
import com.ironcodesoftware.wanderease.model.MediaHandler;
import com.ironcodesoftware.wanderease.model.Product;
import com.ironcodesoftware.wanderease.model.User;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.WanderDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PartnerProductFragment extends Fragment {

    Uri selectedImageUri;
    final HashMap<String,String> categoryMap = new HashMap<>();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadCategories(view);
        if(getActivity().getIntent().getStringExtra("product")!=null){
            loadProductDetails(view);
        }
        Button buttonUploadImage = view.findViewById(R.id.new_product_button_upload_Image);
        buttonUploadImage.setOnClickListener(v->{
            launceImagePicker();
        });
        ImageButton buttonClose = getActivity().findViewById(R.id.new_product_cancel_imageButton);
        buttonClose.setOnClickListener(v -> {
            resetImage();
        });

        Button buttonSaveProduct = view.findViewById(R.id.new_product_button_save_product);
        buttonSaveProduct.setOnClickListener(v->{
            try {
                saveProduct(view);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void loadProductDetails(View view) {
        EditText editTextTitle = view.findViewById(R.id.new_product_title);
        EditText editTextPrice = view.findViewById(R.id.new_product_price);
        EditText editTextQuantity = view.findViewById(R.id.new_product_quantity);
        EditText editTextDescription = view.findViewById(R.id.new_product_description);
        EditText editTextColor = view.findViewById(R.id.new_product_color);
    }

    private void loadCategories(View view) {
        Spinner spinner = view.findViewById(R.id.nre_product_category_spinner);
        ArrayList<String> categoryList = new ArrayList<>();
        categoryList.add("--- Select Category ---");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.single_spinner_item,
                categoryList
        );

        Request request = new Request.Builder()
                .url(BuildConfig.HOST_URL + "GetCategories").build();
        HttpClient.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(MainActivity.TAG,e.getMessage());
                    Snackbar.make(
                            view,
                            "Unable to Load Categories",
                            Snackbar.LENGTH_LONG
                    ).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    Gson gson = new Gson();
                    JsonObject responseJson = gson.fromJson(response.body().string(), JsonObject.class);
                    if(responseJson.get("ok").getAsBoolean()){
                        for (JsonElement categoryJson : responseJson.get("categoryList").getAsJsonArray()) {
                            categoryList.add(categoryJson.getAsJsonObject().get("name").getAsString());
                            categoryMap.put(categoryJson.getAsJsonObject().get("name").getAsString(),
                                    categoryJson.getAsJsonObject().get("id").getAsString());
                        }
                        view.post(()->{
                            arrayAdapter.notifyDataSetChanged();

                            if(getActivity().getIntent().getStringExtra("product")!=null){
                                JsonObject product = gson.fromJson(
                                        getActivity().getIntent().getStringExtra("product"),
                                        JsonObject.class);

                                Spinner spinnerCategory = view.findViewById(R.id.nre_product_category_spinner);
                                spinnerCategory.setSelection(
                                        Integer.parseInt(categoryMap.get(
                                                product.get(Product.F_CATEGORY).getAsJsonObject().get("name").getAsString()
                                        ))
                                );
                            }
                        });
                    }
                }else{
                    Snackbar snackbar = Snackbar.make(
                            view,
                            "Unable to Load Categories",
                            Snackbar.LENGTH_INDEFINITE

                    );
                    snackbar.setAction("Ok", v -> {
                        snackbar.dismiss();
                    });
                    snackbar.show();
                    Log.e(MainActivity.TAG, response.message());
                }
            }
        });

        spinner.setAdapter(arrayAdapter);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveProduct(View view) throws IOException, ClassNotFoundException {
        HashMap<String, String> productDetails = validateProductDetails(view);
        File imageFile = MediaHandler.uriToFile(getContext(), selectedImageUri);
        if(productDetails != null){
            setLoadingButton(view);
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            productDetails.forEach(builder::addFormDataPart);

            MultipartBody multipartBody = builder.addFormDataPart("image", imageFile.getName(),
                    RequestBody.create(imageFile, HttpClient.IMAGE)).build();
            Request request = new Request.Builder().url(BuildConfig.HOST_URL + "SaveProduct")
                    .post(multipartBody).build();
            HttpClient.getInstance().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    view.post(()->{
                        resetLoadingButton(view);
                        Snackbar snackbar = Snackbar.make(
                                view,
                                "Something went Wrong, Please try again later",
                                Snackbar.LENGTH_INDEFINITE

                        );
                        snackbar.setAction("Ok", v -> {
                            snackbar.dismiss();
                        });
                        snackbar.show();
                    });
                    Log.e(MainActivity.TAG, e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    view.post(()->{
                        resetLoadingButton(view);
                    });
                    if(response.isSuccessful()){
                        view.post(()->{
                            Toast.makeText(getContext(), "Product Saved", Toast.LENGTH_LONG).show();
                            resetImage();
                            resetInput();
                        });
                    }else{
                        view.post(()->{
                            Snackbar snackbar = Snackbar.make(
                                    view,
                                    "Process failed, Please check your connection",
                                    Snackbar.LENGTH_INDEFINITE

                            );
                            snackbar.setAction("Ok", v -> {
                                snackbar.dismiss();
                            });
                            snackbar.show();
                        });
                        Log.e(MainActivity.TAG, response.message());
                    }
                }
            });

        }
    }

    private void resetInput(){
        FragmentActivity view = getActivity();
        EditText editTextTitle = view.findViewById(R.id.new_product_title);
        EditText editTextPrice = view.findViewById(R.id.new_product_price);
        EditText editTextQuantity = view.findViewById(R.id.new_product_quantity);
        EditText editTextDescription = view.findViewById(R.id.new_product_description);
        EditText editTextColor = view.findViewById(R.id.new_product_color);
        Spinner spinnerCategory = view.findViewById(R.id.nre_product_category_spinner);

        editTextTitle.setText("");
        editTextPrice.setText("");
        editTextQuantity.setText("");
        editTextDescription.setText("");
        editTextColor.setText("");
        spinnerCategory.setSelection(0, true);
    }

    private void setLoadingButton(View view) {
        Button buttonSaveProduct = view.findViewById(R.id.new_product_button_save_product);
        Animation loadAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.load_blink);
        buttonSaveProduct.startAnimation(loadAnimation);
        buttonSaveProduct.setText(R.string.loading);
        buttonSaveProduct.setEnabled(false);
    }

    private void resetLoadingButton(View view){
        Button buttonSaveProduct = view.findViewById(R.id.new_product_button_save_product);
        buttonSaveProduct.clearAnimation();
        buttonSaveProduct.setText(R.string.partner_product_saveProduct_button);
        buttonSaveProduct.setEnabled(true);
        buttonSaveProduct.requestFocus();
    }

    private HashMap<String, String> validateProductDetails(View view) throws IOException, ClassNotFoundException {
        EditText editTextTitle = view.findViewById(R.id.new_product_title);
        EditText editTextPrice = view.findViewById(R.id.new_product_price);
        EditText editTextQuantity = view.findViewById(R.id.new_product_quantity);
        EditText editTextDescription = view.findViewById(R.id.new_product_description);
        EditText editTextColor = view.findViewById(R.id.new_product_color);
        Spinner spinnerCategory = view.findViewById(R.id.nre_product_category_spinner);

        String title = editTextTitle.getText().toString().trim();
        String price = editTextPrice.getText().toString().trim();
        String quantity = editTextQuantity.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String color = editTextColor.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        boolean isValid = true;
        if(selectedImageUri == null){
            WanderDialog.build(getContext(),"Please select an Image").show();
            isValid =false;
        } else if (spinnerCategory.getSelectedItemPosition() == 0) {
            isValid =false;
            WanderDialog.build(getContext(),"Please select a product category").show();
        }
        if (title.isEmpty()) {
            isValid =false;
            editTextTitle.setError("Title cannot be empty");
        }
        if(price.isEmpty() || Integer.parseInt(price) <= 0){
            isValid =false;
            editTextPrice.setError("Invalid price");
        }
        if(quantity.isEmpty() || Integer.parseInt(quantity) <=0 ){
            isValid =false;
            editTextQuantity.setError("Invalid quantity");
        }
        if(description.isEmpty()){
            isValid =false;
            editTextDescription.setError("Description cannot be empty");
        }
        if(color.isEmpty()){
            isValid =false;
            editTextColor.setError("Color cannot be empty");
        }
        if(isValid){
            HashMap<String,String> productMap = new HashMap<>();
            productMap.put(Product.F_TITLE, title);
            productMap.put(Product.F_PRICE, price);
            productMap.put(Product.F_QTY, quantity);
            productMap.put(Product.F_DESC, description);
            productMap.put(Product.F_COLOR, color);
            productMap.put(Product.F_CATEGORY, categoryMap.get(category));
            productMap.put(UserLogIn.EMAIL_FIELD, UserLogIn.getLogin(getContext()).getEmail());
            return productMap;
        }

        return null;
    }

    private void launceImagePicker() {
        if(checkMediaPermissions()){
            pickMedia.launch(
                    new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build()
            );
        }else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION}, 100);
        }



    }

    private boolean checkMediaPermissions() {
        return (getActivity().checkSelfPermission(Manifest.permission.ACCESS_MEDIA_LOCATION)
        == PackageManager.PERMISSION_GRANTED);
    }


}