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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonArray;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.Order;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.adaper.ToReceiveOrderAdapter;
import com.ironcodesoftware.wanderease.ui.home.HomeActivity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class ToReceiveFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_to_receive, container, false);
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
        RecyclerView recyclerView = view.findViewById(R.id.my_orders_to_receive_orders_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Order")
                .where(Filter.and(
                        Filter.equalTo("buyer", UserLogIn.getLogin(getContext()).getEmail()),
                        Filter.inArray(Order.F_STATE, Arrays.asList(
                                Order.State.Pending.name(),
                                Order.State.Processing.name(),
                                Order.State.Delivery_Assigned.getName()
                        ))
                )).addSnapshotListener((value, error) -> {
                    if(error == null && !value.isEmpty()){
                        List<DocumentSnapshot> documents = value.getDocuments();
                        view.post(()->{
                            resetLoading(view);
                            recyclerView.setAdapter(new ToReceiveOrderAdapter(
                                    documents, getActivity()) {
                                @Override
                                public void setupActionButton(DocumentSnapshot document, Button actionButton) {

                                }
                            });
                        });
                    }else{
                        Log.e(MainActivity.TAG,error!=null?error.getMessage():"Empty orders");
                        view.post(()->{
                            resetLoading(view);
                            showEmptyCard(view);
                            if(error!=null){
                                Snackbar snackbar = Snackbar.make(view,
                                        "Something went wrong. Please try again",
                                        Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction("Ok", v -> {snackbar.dismiss();}).show();
                            }
                        });
                    }
                });

    }

    private void showEmptyCard(View view) {
        view.post(()->{
            ConstraintLayout emptyconstraintLayout = view.findViewById(R.id.my_orders_to_receive_orders_emptyCard);
            emptyconstraintLayout.setVisibility(View.VISIBLE);
            Button button = view.findViewById(R.id.my_orders_to_receive_orders_emptyCard_button);
            emptyconstraintLayout.bringToFront();
            button.setOnClickListener(v->{
                startActivity(new Intent(getContext(), HomeActivity.class));
                getActivity().finish();
            });
        });
    }

    private void resetLoading(View view) {
        ImageView spinner = view.findViewById(R.id.my_orders_to_receive_orders_spinner_imageView);
        spinner.clearAnimation();
        spinner.setVisibility(View.INVISIBLE);
    }

    private void setLoading(View view) {
        ImageView spinner = view.findViewById(R.id.my_orders_to_receive_orders_spinner_imageView);
        spinner.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.load_rotate);
        spinner.setAnimation(animation);
        animation.start();
    }
}