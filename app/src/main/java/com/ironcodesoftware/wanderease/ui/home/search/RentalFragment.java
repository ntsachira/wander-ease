package com.ironcodesoftware.wanderease.ui.home.search;

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
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.HttpClient;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.adaper.VehicleAdapter;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;


public class RentalFragment extends Fragment {
    private UserLogIn login;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rental, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            login = UserLogIn.getLogin(getContext());
        } catch (IOException | ClassNotFoundException e) {
            getActivity().finish();
        }
        loadOrders(view);
    }

    private void loadOrders(View view) {
        setLoading(view);

        Request request = new Request.Builder().url(BuildConfig.HOST_URL + "LoadVehicles").build();
        HttpClient.getInstance().newCall(request).enqueue(new Callback() {
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
                Log.e(MainActivity.TAG, "Vehicles loading failed",e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    Gson gson = new Gson();
                    String responseText = response.body().string();
                    try{
                        JsonArray jsonArray = gson.fromJson(responseText, JsonArray.class);
                        if(jsonArray!=null&&!jsonArray.isEmpty()){
                            view.post(()->{
                                resetLoading(view);
                                loadRecycleView(view,jsonArray);
                            });
                        }else{
                            view.post(()->{
                                resetLoading(view);
                                showEmptyCard(view);
                            });
                        }
                    }catch (JsonSyntaxException e){
                        view.post(()->{
                            resetLoading(view);
                            showEmptyCard(view);
                            Snackbar snackbar = Snackbar.make(
                                    view,
                                    "Server isn't responding, Please try again later",
                                    Snackbar.LENGTH_INDEFINITE

                            );
                            snackbar.setAction("Ok", v -> {
                                snackbar.dismiss();
                            });
                            snackbar.show();
                        });
                    }

                }else{
                    view.post(()->{
                        resetLoading(view);
                        showEmptyCard(view);
                        Snackbar snackbar = Snackbar.make(
                                view,
                                "Something went Wrong, Please check your connection",
                                Snackbar.LENGTH_INDEFINITE

                        );
                        snackbar.setAction("Ok", v -> {
                            snackbar.dismiss();
                        });
                        snackbar.show();
                    });
                }
            }
        });
    }

    private void loadRecycleView(View view, JsonArray jsonArray) {
        RecyclerView recyclerView = view.findViewById(R.id.rental_recyclerView);
        recyclerView.bringToFront();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.setAdapter(new VehicleAdapter(jsonArray,getActivity()) {
            @Override
            public void onCardClick(JsonObject jsonObject, ConstraintLayout card) {

            }
        });
    }

    private void showEmptyCard(View view) {
        view.post(()->{
            RecyclerView recyclerView = view.findViewById(R.id.rental_recyclerView);
            recyclerView.setVisibility(View.INVISIBLE);
            ConstraintLayout emptyconstraintLayout = view.findViewById(R.id.rental_spinner_container);
            emptyconstraintLayout.setVisibility(View.VISIBLE);
            ImageView spinner = view.findViewById(R.id.rental_spinner_imageView);
            spinner.setImageResource(R.drawable.box_empty_icon);
        });
    }

    private void resetLoading(View view) {
        ImageView spinner = view.findViewById(R.id.rental_spinner_imageView);
        spinner.clearAnimation();
        ConstraintLayout emptyconstraintLayout = view.findViewById(R.id.rental_spinner_container);
        emptyconstraintLayout.setVisibility(View.GONE);
    }

    private void setLoading(View view) {
        ImageView spinner = view.findViewById(R.id.rental_spinner_imageView);
        spinner.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.load_rotate);
        spinner.setAnimation(animation);
        animation.start();
    }
}