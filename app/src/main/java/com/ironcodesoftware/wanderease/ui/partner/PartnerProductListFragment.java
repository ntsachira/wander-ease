package com.ironcodesoftware.wanderease.ui.partner;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
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
import com.ironcodesoftware.wanderease.model.Product;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.WanderDialog;
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

        Button buttonSearch = view.findViewById(R.id.partner_product_search_button);
        buttonSearch.setOnClickListener(v->{
            try {
                loadProducts(view);
            } catch (IOException | ClassNotFoundException e) {
                Log.d(MainActivity.TAG,e.getLocalizedMessage(),e);
            }
        });

    }

    private void loadProducts(View view) throws IOException, ClassNotFoundException {
        setLoading(view);
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
                        TabLayout tabLayout = getActivity().findViewById(R.id.partner_product_tabLayout);
                        tabLayout.getTabAt(0).setText(String.format("Product List (%s)", productList.size()));
                        if(!productList.isEmpty()){

                            tabLayout.getTabAt(0).setText(String.format("My Products (%s)", productList.size()));
                            PartnerProductAdapter partnerProductAdapter = new PartnerProductAdapter(productList, getActivity()) {
                                @Override
                                public void setStatusIndicator(TextView textViewStatus, Switch statusSwitch, boolean activeStatus) {
                                    textViewStatus.setVisibility(View.VISIBLE);
                                    textViewStatus.setText(activeStatus?"Active":"Inactive");
                                    if(!activeStatus){
                                        textViewStatus.setTextColor(getActivity().getColor(R.color.inactive));
                                        textViewStatus.setBackgroundColor(getActivity().getColor(R.color.red_badge));
                                    }

                                }

                                @Override
                                public void setEditButton(ImageButton editButton, JsonObject product) {
                                    editButton.setOnClickListener(v -> {
                                        View card = LayoutInflater.from(getContext()).inflate(
                                                R.layout.product_quantity_update_dialog_layout,
                                                null);
                                        TextView textView = card.findViewById(R.id.product_quantity_textView);
                                        textView.setText(String.format("(ID: %s)",
                                                product.get("id").getAsString()));
                                        new AlertDialog.Builder(getContext())
                                                .setCancelable(false)
                                                .setPositiveButton("Save", (dialog, which) -> {

                                                    EditText editTextQuantity = card.findViewById(R.id.partner_product_quantity_update_editText);
                                                    String qty = editTextQuantity.getText().toString().trim();
                                                    if(!qty.isEmpty() && Integer.parseInt(qty)>0){
                                                        updateProductQuantity(view,product.get("id").getAsString(),qty);
                                                    }
                                                })
                                                .setNegativeButton("Cancel", (dialog, which) -> {

                                                }).setView(card)
                                                .show();

                                    });
                                }
                            };
                            recyclerView.setAdapter(partnerProductAdapter);
                        }else{
                            resetLoading(view);
                            showEmptyCard(view);
                            tabLayout.getTabAt(0).setText(String.format("My Products (%s)",0));
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

    private void updateProductQuantity(View view,String id, String qty) {
        android.app.AlertDialog loading = WanderDialog.loading(getContext());
        loading.show();
        Request request = new Request.Builder()
                .url(BuildConfig.HOST_URL + String.format("UpdateProductQuantity?id=%s&qty=%s", id, qty))
                .build();
        HttpClient.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                view.post(()->{
                    loading.cancel();
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
                Log.e(MainActivity.TAG, "Product update failed",e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    view.post(()->{
                        loading.cancel();
                        android.app.AlertDialog success = WanderDialog.success(getContext(), "Product updated successfully");
                        success.setButton(DialogInterface.BUTTON_POSITIVE,"Ok",(dialog, which) -> {
                            getActivity().recreate();
                        });
                        success.show();

                    });

                }else {

                    view.post(()->{
                        loading.cancel();
                        Snackbar snackbar = Snackbar.make(
                                view,
                                "Something went Wrong, Server error",
                                Snackbar.LENGTH_INDEFINITE

                        );
                        snackbar.setAction("Ok", v -> {
                            snackbar.dismiss();
                        });
                        snackbar.show();
                    });
                    Log.e(MainActivity.TAG, "Product update failed");
                }
            }
        });
    }

    private void showEmptyCard(View view) {
        view.post(()->{
            RecyclerView recyclerView = view.findViewById(R.id.partner_product_recyclerView);
            recyclerView.setVisibility(View.INVISIBLE);
            ConstraintLayout emptyconstraintLayout = view.findViewById(R.id.partner_no_products_imageView_container);
            emptyconstraintLayout.setVisibility(View.VISIBLE);
            ImageView spinner = view.findViewById(R.id.partner_no_products_imageView);
            spinner.setImageResource(R.drawable.box_empty_icon);
        });
    }

    private void resetLoading(View view) {
        ImageView spinner = view.findViewById(R.id.partner_no_products_imageView);
        spinner.clearAnimation();
        ConstraintLayout emptyconstraintLayout = view.findViewById(R.id.partner_no_products_imageView_container);
        emptyconstraintLayout.setVisibility(View.GONE);
    }

    private void setLoading(View view) {
        ImageView spinner = view.findViewById(R.id.partner_no_products_imageView);
        spinner.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.load_rotate);
        spinner.setAnimation(animation);
        animation.start();
    }
}