package com.ironcodesoftware.wanderease.ui.home.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.HttpClient;
import com.ironcodesoftware.wanderease.model.Product;
import com.ironcodesoftware.wanderease.model.adaper.ProductAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;


public class ProductFragment extends Fragment {

    final HashMap<String,String> categoryMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadCategories(view);
        loadProducts(view);
        Button buttonSearch = view.findViewById(R.id.product_list_search_button);
        buttonSearch.setOnClickListener(v->{
            loadProducts(view);
        });
    }

    private void loadProducts(View view) {
        AutoCompleteTextView textViewCategory = view.findViewById(R.id.product_list_search_category_spinner);
        EditText editTextSearch = view.findViewById(R.id.product_list_search_editText);
        String searchKey = editTextSearch.getText().toString().trim();
        String category = categoryMap.get(textViewCategory.getText().toString());
        if (category == null){
            category = "";
        }

        setLoading(view);
        RecyclerView recyclerView = view.findViewById(R.id.product_list_recyclerView);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.removeAllViews();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        HttpClient.getInstance().newCall(
                new Request.Builder().url(BuildConfig.HOST_URL+
                                String.format("LoadAllActiveProducts?key=%s&category=%s",searchKey,category)
                        ).build()
        ).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                view.post(()->{
                    resetLoading(view);
                    showEmptyCard(view);
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
                Log.e(MainActivity.TAG, "Product load failed",e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    Gson gson = new Gson();
                    JsonObject responseJsonObject = gson.fromJson(response.body().string(), JsonObject.class);

                    JsonArray productList = responseJsonObject.get("productList").getAsJsonArray();
                    view.post(()->{

                        resetLoading(view);
                        if(!productList.isEmpty()){
                            ProductAdapter productAdapter = new ProductAdapter(productList,getActivity());
                            recyclerView.setAdapter(productAdapter);
                        }else{
                            showEmptyCard(view);
                        }
                    });


                }else{
                    view.post(()->{
                        Snackbar snackbar = Snackbar.make(
                                view,
                                "Request failed, Please check your connection",
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

    private void loadCategories(View view) {
        AutoCompleteTextView spinner = view.findViewById(R.id.product_list_search_category_spinner);
        spinner.setOnClickListener(v->{
            spinner.setText("");
            spinner.showDropDown();
        });
        ArrayList<String> categoryList = new ArrayList<>();
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
                        });
                    }
                }else{
                    view.post(()->{
                        Snackbar snackbar = Snackbar.make(
                                view,
                                "Unable to Load Categories",
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

        spinner.setAdapter(arrayAdapter);
    }

    private void showEmptyCard(View view) {
        view.post(()->{
            ConstraintLayout spinnerContainer = view.findViewById(R.id.product_spinner_container);
            RecyclerView recyclerView = view.findViewById(R.id.product_list_recyclerView);
            recyclerView.setVisibility(View.INVISIBLE);
            spinnerContainer.setVisibility(View.INVISIBLE);
            ImageView imageViewEmptyProduct = view.findViewById(R.id.product_list_empty_imageView);
            imageViewEmptyProduct.setVisibility(View.VISIBLE);
        });
    }

    private void resetLoading(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.product_list_recyclerView);
        recyclerView.bringToFront();
        ImageView spinner = view.findViewById(R.id.product_spinner_imageView);
        spinner.clearAnimation();
        ConstraintLayout spinnerContainer = view.findViewById(R.id.product_spinner_container);
        spinnerContainer.setVisibility(View.GONE);
    }

    private void setLoading(View view) {
        ConstraintLayout spinnerContainer = view.findViewById(R.id.product_spinner_container);
        spinnerContainer.setVisibility(View.VISIBLE);
        spinnerContainer.bringToFront();
        ImageView spinner = view.findViewById(R.id.product_spinner_imageView);
        spinner.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.load_rotate);
        spinner.setAnimation(animation);
        animation.start();
        ImageView imageViewEmptyProduct = view.findViewById(R.id.product_list_empty_imageView);
        imageViewEmptyProduct.setVisibility(View.INVISIBLE);
    }
}