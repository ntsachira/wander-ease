package com.ironcodesoftware.wanderease.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.HttpClient;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.adaper.WatchlistItemAdapter;
import com.ironcodesoftware.wanderease.ui.home.search.SingleProductActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;


public class SavedFragment extends Fragment {

    private UserLogIn logIn;
    JsonArray itemArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            logIn = UserLogIn.getLogin(getContext());
        } catch (IOException | ClassNotFoundException e) {
            getActivity().finish();
        }

        loadSavedItems(view);
        setupItemTouchHelper(view);
    }

    private void loadSavedItems(View view) {
        setLoading(view);
        RecyclerView recyclerView = view.findViewById(R.id.watchlist_recyclerView);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Request request = new Request.Builder().url(BuildConfig.HOST_URL + "LoadWatchlist?email="+logIn.getEmail()).build();
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
                Log.e(MainActivity.TAG, "Product load failed",e);
            }

            @SuppressLint("CheckResult")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                view.post(()->resetLoading(view));
                if(response.isSuccessful()){
                    Gson gson = new Gson();
                    String responseText = response.body().string();
                    try{
                        itemArray = gson.fromJson(responseText, JsonArray.class);

                        if(!itemArray.isEmpty()){
                            view.post(()->{
                                recyclerView.setAdapter(new WatchlistItemAdapter(itemArray, getActivity()) {
                                    @Override
                                    public void onCardClick(JsonObject item, ConstraintLayout card) {
                                        Intent intent = new Intent(getContext(), SingleProductActivity.class);
                                        intent.putExtra("singleProduct", item.toString());
                                        startActivity(intent);
                                    }
                                });
                            });
                        }else {
                            view.post(()->{
                                resetLoading(view);
                                showEmptyCard(view);
                            });
                        }
                    }catch (JsonParseException e){
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
                        Log.e(MainActivity.TAG, response.message(),e);
                    }

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

    private void setupItemTouchHelper(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.watchlist_recyclerView);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.Callback() {
                    @Override
                    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                        return makeMovementFlags(0, ItemTouchHelper.RIGHT);
                    }

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int adapterPosition = viewHolder.getAdapterPosition();
                        String productId = itemArray.get(adapterPosition).getAsJsonObject().get("id").getAsString();
                        Log.d(MainActivity.TAG,productId);

                        removeFromWatchlist(view,productId);
                        itemArray.remove(adapterPosition);
                        view.post(recyclerView.getAdapter()::notifyDataSetChanged);
                        if(itemArray.isEmpty()){
                            showEmptyCard(view);
                        }

                    }
                }
        );
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void removeFromWatchlist(View view, String productId) {
        Request request = new Request.Builder().url(BuildConfig.HOST_URL + String.format(
                "RemoveFromWatchlist?email=%s&id=%s", logIn.getEmail(), productId
        )).build();
        HttpClient.getInstance().newCall(request).enqueue(new Callback() {
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
                Log.e(MainActivity.TAG, "Watchlist item remove failed",e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    view.post(()->{
                        Toast.makeText(getContext(), "Removed from watchlist successfully",
                                Toast.LENGTH_LONG).show();

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
                    Log.e(MainActivity.TAG, response.body().string());
                }
            }
        });
    }

    private void showEmptyCard(View view) {
        view.post(()->{
            ConstraintLayout spinnerContainer = view.findViewById(R.id.watchlist_spinner_container);
            RecyclerView recyclerView = view.findViewById(R.id.watchlist_recyclerView);
            recyclerView.setVisibility(View.INVISIBLE);
            spinnerContainer.setVisibility(View.VISIBLE);
            ImageView spinner = view.findViewById(R.id.watchlist_spinner_imageView);
            spinner.setImageResource(R.drawable.box_empty_icon);
        });
    }

    private void resetLoading(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.watchlist_recyclerView);
        recyclerView.bringToFront();
        ImageView spinner = view.findViewById(R.id.watchlist_spinner_imageView);
        spinner.clearAnimation();
        ConstraintLayout spinnerContainer = view.findViewById(R.id.watchlist_spinner_container);
        spinnerContainer.setVisibility(View.GONE);
    }

    private void setLoading(View view) {
        ConstraintLayout spinnerContainer = view.findViewById(R.id.watchlist_spinner_container);
        spinnerContainer.setVisibility(View.VISIBLE);
        spinnerContainer.bringToFront();
        ImageView spinner = view.findViewById(R.id.watchlist_spinner_imageView);
        spinner.setImageResource(lk.payhere.androidsdk.R.drawable.spinner_circle);
        spinner.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.load_rotate);
        spinner.setAnimation(animation);
        animation.start();
        spinner.setImageResource(lk.payhere.androidsdk.R.drawable.spinner_circle);
    }
}