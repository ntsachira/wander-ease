package com.ironcodesoftware.wanderease.ui.admin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.ironcodesoftware.wanderease.model.adaper.UserAdapter;
import com.ironcodesoftware.wanderease.model.adaper.VehicleAdapter;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;


public class AdminUserFragment extends Fragment {


    private static final int CALL_PERMISSION_REQUEST_CODE = 119;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUsers(view);

        Button buttonSearch = view.findViewById(R.id.admin_user_search_button);
        buttonSearch.setOnClickListener(v->{
            EditText editTextSearch = view.findViewById(R.id.admin_user_search_editTextText);
            if(!editTextSearch.getText().toString().trim().isEmpty()){
                loadUsers(view);
            }

        });

        EditText editTextSearch = view.findViewById(R.id.admin_user_search_editTextText);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().isEmpty()){
                    loadUsers(view);
                }
            }
        });
    }

    private void loadUsers(View view) {
        setLoading(view);
        EditText editTextSearch = view.findViewById(R.id.admin_user_search_editTextText);
        Request request = new Request.Builder().url(BuildConfig.HOST_URL + "LoadUsers?query="
                +editTextSearch.getText().toString().trim()).build();
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
                Log.e(MainActivity.TAG, "Users loading failed",e);
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
        RecyclerView recyclerView = view.findViewById(R.id.admin_user_recyclerView);
        recyclerView.bringToFront();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.setAdapter(new UserAdapter(jsonArray, getActivity()) {
            @Override
            public void onCallButtonClick(ImageButton callButton, String mobile) {
                if(hasCallPermission()){
                    callBuyer(mobile);
                }
            }
        });
    }

    private boolean hasCallPermission() {
        if(getActivity().checkSelfPermission(Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
            return true;
        }
        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST_CODE);
        return false;
    }

    private void callBuyer(String mobile) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(String.format("tel:%s",mobile)));
        startActivity(intent);
    }
    private void showEmptyCard(View view) {
        view.post(()->{
            RecyclerView recyclerView = view.findViewById(R.id.admin_user_recyclerView);
            recyclerView.setVisibility(View.INVISIBLE);
            ConstraintLayout emptyconstraintLayout = view.findViewById(R.id.admin_user_spinner_container);
            emptyconstraintLayout.setVisibility(View.VISIBLE);
            ImageView spinner = view.findViewById(R.id.admin_user_spinner_imageView);
            spinner.setImageResource(R.drawable.box_empty_icon);
        });
    }

    private void resetLoading(View view) {
        ImageView spinner = view.findViewById(R.id.admin_user_spinner_imageView);
        spinner.clearAnimation();
        ConstraintLayout emptyconstraintLayout = view.findViewById(R.id.admin_user_spinner_container);
        emptyconstraintLayout.setVisibility(View.GONE);
    }

    private void setLoading(View view) {
        ImageView spinner = view.findViewById(R.id.admin_user_spinner_imageView);
        spinner.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.load_rotate);
        spinner.setAnimation(animation);
        animation.start();
    }
}