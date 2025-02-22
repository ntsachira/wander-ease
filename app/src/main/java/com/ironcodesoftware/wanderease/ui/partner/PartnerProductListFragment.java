package com.ironcodesoftware.wanderease.ui.partner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.HttpClient;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.adaper.PartnerProductAdapter;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;


public class PartnerProductListFragment extends Fragment {

    boolean productsLoaded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_partner_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            loadProducts(view);
        } catch (IOException | ClassNotFoundException e) {
            Log.d(MainActivity.TAG,e.getLocalizedMessage(),e);
        }

    }

    private void loadProducts(View view) throws IOException, ClassNotFoundException {
        RecyclerView recyclerView = view.findViewById(R.id.partner_product_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        EditText editTextSearch = view.findViewById(R.id.partner_product_editText);
        String query = editTextSearch.getText().toString().trim();
        String email = UserLogIn.getLogin(getContext()).getEmail();
        HttpClient.getInstance()
                .newCall(new Request.Builder()
                        .url(BuildConfig.HOST_URL + String.format("LoadProducts?email=%s&query=%s",email,query))
                        .build())
                .enqueue(new Callback() {
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
                        ImageView imageViewEmptyProduct = view.findViewById(R.id.partner_no_products_imageView);
                        TextView textViewCount = view.findViewById(R.id.partner_product_cout_textView);
                        if(!productList.isEmpty()){
                            imageViewEmptyProduct.setVisibility(View.INVISIBLE);
                            textViewCount.setText(String.format("Product List (%s)", productList.size()));
                            PartnerProductAdapter partnerProductAdapter = new PartnerProductAdapter(productList,getActivity());
                            recyclerView.setAdapter(partnerProductAdapter);
                        }else{
                            imageViewEmptyProduct.setVisibility(View.VISIBLE);
                            textViewCount.setText(R.string.product_list_empty);
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
}