package com.ironcodesoftware.wanderease.ui.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
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


public class ProductConfirmationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            loadProducts(view);
        } catch (IOException | ClassNotFoundException e) {
            Log.e(MainActivity.TAG, "Product load failed",e);
        }
    }

    private void loadProducts(View view) throws IOException, ClassNotFoundException {
        setLoading(view);
        RecyclerView recyclerView = view.findViewById(R.id.admin_order_confirmation_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        HttpClient.getInstance()
                .newCall(new Request.Builder()
                        .url(BuildConfig.HOST_URL + "LoadProducts")
                        .build())
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        view.post(()->{
                            resetLoading(view);
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

                                    PartnerProductAdapter partnerProductAdapter = new PartnerProductAdapter(productList, getActivity()) {
                                        @Override
                                        public void setStatusIndicator(TextView textViewStatus, Switch statusSwitch, boolean activeStatus) {
                                                textViewStatus.setVisibility(View.INVISIBLE);
                                                statusSwitch.setVisibility(View.VISIBLE);
                                                statusSwitch.setText(activeStatus?"Active":"Inactive");
                                                statusSwitch.setChecked(activeStatus);
                                                statusSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                                    if(isChecked){
                                                        //update product status
                                                    }
                                                });
                                        }

                                        @Override
                                        public void setEditButton(ImageButton editButton, JsonObject product) {
                                            editButton.setVisibility(View.INVISIBLE);
                                        }
                                    };
                                    recyclerView.setAdapter(partnerProductAdapter);
                                }else{
                                    resetLoading(view);
                                    showEmptyCard(view);
                                }
                            });


                        }else{
                            view.post(()->{
                                resetLoading(view);
                                showEmptyCard(view);
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

    private void showEmptyCard(View view) {
        view.post(()->{
            RecyclerView recyclerView = view.findViewById(R.id.admin_order_confirmation_recyclerview);
            recyclerView.setVisibility(View.INVISIBLE);
            ConstraintLayout emptyconstraintLayout = view.findViewById(R.id.admin_order_confirmation_spinner_container);
            emptyconstraintLayout.setVisibility(View.VISIBLE);
            ImageView spinner = view.findViewById(R.id.admin_order_confirmation_spinner_imageView);
            spinner.setImageResource(R.drawable.box_empty_icon);
        });
    }

    private void resetLoading(View view) {
        ImageView spinner = view.findViewById(R.id.admin_order_confirmation_spinner_imageView);
        spinner.clearAnimation();
        ConstraintLayout emptyconstraintLayout = view.findViewById(R.id.admin_order_confirmation_spinner_container);
        emptyconstraintLayout.setVisibility(View.GONE);
    }

    private void setLoading(View view) {
        ImageView spinner = view.findViewById(R.id.admin_order_confirmation_spinner_imageView);
        spinner.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.load_rotate);
        spinner.setAnimation(animation);
        animation.start();
    }
}