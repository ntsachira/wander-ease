package com.ironcodesoftware.wanderease.ui.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.Order;
import com.ironcodesoftware.wanderease.model.adaper.AdminActiveDeliveryAdapter;
import com.ironcodesoftware.wanderease.model.adaper.AdminToAssignDeliveryAdapter;

import java.io.IOException;
import java.util.List;


public class AdminDeliveryActiveFragment extends Fragment {

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