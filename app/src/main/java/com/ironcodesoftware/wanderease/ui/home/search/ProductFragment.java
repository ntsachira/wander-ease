package com.ironcodesoftware.wanderease.ui.home.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.ironcodesoftware.wanderease.model.adaper.PartnerProductAdapter;
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
    }

    private void loadProducts(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.product_list_recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        HttpClient.getInstance().newCall(
                new Request.Builder().url(BuildConfig.HOST_URL+"LoadAllActiveProducts").build()
        ).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                view.post(()->{
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
                        ImageView imageViewEmptyProduct = view.findViewById(R.id.product_list_empty_imageView);

                        if(!productList.isEmpty()){
                            imageViewEmptyProduct.setVisibility(View.INVISIBLE);
                            ProductAdapter productAdapter = new ProductAdapter(productList,getActivity());
                            recyclerView.setAdapter(productAdapter);
                        }else{
                            imageViewEmptyProduct.setVisibility(View.VISIBLE);
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
        Spinner spinner = view.findViewById(R.id.product_list_search_category_spinner);
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
}