package com.ironcodesoftware.wanderease.ui.home.account;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.HttpClient;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.adaper.CompletedOrderAdapter;
import com.ironcodesoftware.wanderease.ui.home.HomeActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;


public class CompletedOrdersFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_completed_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadOrders(view);
    }

    private void loadOrders(View view) {
        setLoading(view);

        RecyclerView recyclerView = view.findViewById(R.id.completed_orders_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        new Thread(()->{
            try {
                Request request = new Request.Builder().url(String.format("%sLoadCompletedOrders?email=%s",
                        BuildConfig.HOST_URL, UserLogIn.getLogin(getContext()).getEmail())).build();
                HttpClient.getInstance().newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                        Log.e(MainActivity.TAG, "Completed orders loading failed",e);
                        view.post(()->{
                            resetLoading(view);
                            Snackbar snackbar = Snackbar.make(view, "Data loading failed",
                                    Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("Ok", v -> {snackbar.dismiss();}).show();
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        view.post(()->{
                            resetLoading(view);
                        });
                            if(response.isSuccessful()){
                                JsonObject responseObject = new Gson().fromJson(response.body().string(), JsonObject.class);
                                JsonArray orders = responseObject.get("orders").getAsJsonArray();

                                view.post(()->{
                                    if(orders.isEmpty()){
                                        showEmptyCard(view);
                                    }else{
                                        recyclerView.bringToFront();
                                    }
                                   recyclerView.setAdapter(
                                           new CompletedOrderAdapter(
                                                   orders,
                                                   getActivity()
                                           )
                                   );
                               });
                            }else{
                                Log.e(MainActivity.TAG, response.body().string());
                                view.post(()->{
                                    resetLoading(view);
                                    Snackbar snackbar = Snackbar.make(view,
                                            "Data loading failed. Please check your connection",
                                            Snackbar.LENGTH_INDEFINITE);
                                    snackbar.setAction("Ok", v -> {snackbar.dismiss();}).show();
                                });
                            }


                    }
                });
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }).start();
    }

    private void showEmptyCard(View view) {
        view.post(()->{
            ConstraintLayout emptyconstraintLayout = view.findViewById(R.id.completed_orders_empty_constraintLayout);
            emptyconstraintLayout.setVisibility(View.VISIBLE);
            Button button = view.findViewById(R.id.completed_orders_backToHome_button);
            emptyconstraintLayout.bringToFront();
            button.setOnClickListener(v->{
                startActivity(new Intent(getContext(), HomeActivity.class));
                getActivity().finish();
            });
        });
    }

    private void resetLoading(View view) {
        ImageView spinner = view.findViewById(R.id.completed_orders_spinner_imageView);
        spinner.clearAnimation();
        spinner.setVisibility(View.INVISIBLE);

    }

    private void setLoading(View view) {
        ImageView spinner = view.findViewById(R.id.completed_orders_spinner_imageView);
        spinner.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.load_rotate);
        spinner.setAnimation(animation);
        animation.start();
    }
}