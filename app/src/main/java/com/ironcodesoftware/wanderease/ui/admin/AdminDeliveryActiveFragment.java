package com.ironcodesoftware.wanderease.ui.admin;

import android.Manifest;
import android.app.AlertDialog;
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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.Delivery;
import com.ironcodesoftware.wanderease.model.HttpClient;
import com.ironcodesoftware.wanderease.model.Order;
import com.ironcodesoftware.wanderease.model.SQLiteHelper;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.WanderDialog;
import com.ironcodesoftware.wanderease.model.adaper.AdminActiveDeliveryAdapter;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AdminDeliveryActiveFragment extends Fragment {

    final int CALL_PERMISSION_REQUEST_CODE = 119;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_delivery_active, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            loadOrders(view);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadOrders(View view) throws IOException, ClassNotFoundException {
        setLoading(view);
        RecyclerView recyclerView = view.findViewById(R.id.admin_delivery_active_recyclerView);
        recyclerView.bringToFront();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Order").whereEqualTo(Order.F_STATE,Order.State.Delivery_Assigned.getName())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(!value.isEmpty()){
                            List<DocumentSnapshot> documents = value.getDocuments();

                            view.post(()->{
                                recyclerView.setVisibility(View.VISIBLE);
                                resetLoading(view);
                                recyclerView.setAdapter(new AdminActiveDeliveryAdapter(
                                        documents, getActivity()) {
                                    @Override
                                    public void addAction(@NonNull DocumentSnapshot document, Button buttonAction) {
                                        buttonAction.setVisibility(View.VISIBLE);
                                        buttonAction.setText(R.string.call_courier);
                                        buttonAction.setOnClickListener(v -> {

                                            getCourierMobile(view,document.getString(Order.F_ID));
                                        });
                                    }
                                });
                            });
                        }else{
                            view.post(()->{
                                resetLoading(view);
                                showEmptyCard(view);
                            });
                        }
                    }
                });
    }

    private void getCourierMobile(View view,String orderId) {
        AlertDialog loading = WanderDialog.loading(getContext());
        view.post(loading::show);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(Delivery.F_COLLECTION)
                .whereEqualTo(Delivery.F_ORDER_ID, orderId)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        String email = task.getResult().getDocuments().get(0).getString(Delivery.F_COURIER);
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty(UserLogIn.EMAIL_FIELD, email);
                        Request request = new Request.Builder().url(BuildConfig.HOST_URL + "GetProfile")
                                .post(RequestBody.create(jsonObject.toString(), HttpClient.JSON)).build();
                        HttpClient.getInstance().newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                Log.e(MainActivity.TAG,e.getLocalizedMessage(),e);
                                view.post(loading::cancel);
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                view.post(loading::cancel);
                                if(response.isSuccessful()){
                                    Gson gson = new Gson();
                                    JsonObject responseJson = gson.fromJson(response.body().string(), JsonObject.class);
                                    if(responseJson.has("ok")
                                            && responseJson.get("ok").getAsBoolean()){
                                        JsonObject profile = responseJson.getAsJsonObject("profile");
                                        callCourier(profile.get(UserLogIn.MOBILE_FIELD).getAsString());
                                    }
                                }
                            }
                        });
                    }else{
                        view.post(loading::cancel);
                    }
                }).addOnFailureListener(e->{
                    view.post(loading::cancel);
                } );
    }

    private boolean hasCallPermission() {
        if(getActivity().checkSelfPermission(Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
            return true;
        }
        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST_CODE);
        return false;
    }

    private void callCourier(String mobile) {
        if(hasCallPermission()){
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(String.format("tel:%s",mobile)));
            startActivity(intent);
        }
    }
    private void showEmptyCard(View view) {
        view.post(()->{
            RecyclerView recyclerView = view.findViewById(R.id.admin_delivery_active_recyclerView);
            recyclerView.setVisibility(View.INVISIBLE);
            ConstraintLayout emptyconstraintLayout = view.findViewById(R.id.admin_delivery_active_spinner_container);
            emptyconstraintLayout.setVisibility(View.VISIBLE);
            ImageView spinner = view.findViewById(R.id.admin_delivery_active_spinnser);
            spinner.setImageResource(R.drawable.box_empty_icon);
        });
    }

    private void resetLoading(View view) {
        ImageView spinner = view.findViewById(R.id.admin_delivery_active_spinnser);
        spinner.clearAnimation();
        ConstraintLayout emptyconstraintLayout = view.findViewById(R.id.admin_delivery_active_spinner_container);
        emptyconstraintLayout.setVisibility(View.GONE);
    }

    private void setLoading(View view) {
        ImageView spinner = view.findViewById(R.id.admin_delivery_active_spinnser);
        spinner.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.load_rotate);
        spinner.setAnimation(animation);
        animation.start();
    }
}