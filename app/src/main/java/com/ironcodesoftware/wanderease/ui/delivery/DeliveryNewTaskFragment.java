package com.ironcodesoftware.wanderease.ui.delivery;

import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.Delivery;
import com.ironcodesoftware.wanderease.model.Order;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.adaper.AdminActiveDeliveryAdapter;
import com.ironcodesoftware.wanderease.ui.admin.AdminSettingsActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DeliveryNewTaskFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delivery_new_task, container, false);
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
        RecyclerView recyclerView = view.findViewById(R.id.delivery_active_task_recyclerView);

        recyclerView.bringToFront();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(Delivery.F_COLLECTION).where(Filter.and(
                Filter.equalTo(Delivery.F_STATUS,Delivery.State.Active.name()),
                Filter.equalTo(Delivery.F_COURIER, UserLogIn.getLogin(getContext()).getEmail())
                ))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(!value.isEmpty()){
                            ArrayList<String> idList = new ArrayList<>();
                            for (DocumentSnapshot document : value.getDocuments()) {
                                idList.add(document.getString(Delivery.F_ORDER_ID));
                            }

                            firestore.collection(Order.F_COLLECTION).where(
                                    Filter.inArray(Order.F_ID, idList)).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    List<DocumentSnapshot> documents = value.getDocuments();
                                    view.post(()->{
                                        recyclerView.setVisibility(View.VISIBLE);
                                        resetLoading(view);
                                        recyclerView.setAdapter(new AdminActiveDeliveryAdapter(
                                                documents, getActivity()) {
                                            @Override
                                            public void addAction(@NonNull DocumentSnapshot document, Button buttonAction) {
                                                buttonAction.setVisibility(View.VISIBLE);
                                                buttonAction.setText(R.string.view);
                                                buttonAction.setOnClickListener(v -> {

                                                    startActivity(
                                                            new Intent(getContext(),DeliveryTaskViewActivity.class)
                                                                    .putExtra(Order.F_ID
                                                                            ,document.getString(Order.F_ID))
                                                                    .putExtra(Order.F_ITEMS, document.getString(Order.F_ITEMS))
                                                                    .putExtra(Order.F_LOCATION, document.getString(Order.F_LOCATION))
                                                                    .putExtra(Order.F_BUYER, document.getString(Order.F_BUYER))
                                                                    .putExtra(Order.F_DATE, document.getDate(Order.F_DATE))
                                                                    .putExtra(Order.F_PRICE, document.getDouble(Order.F_PRICE))
                                                                    .putExtra("document_id", document.getId())
                                                    );
                                                });
                                            }
                                        });
                                    });
                                }
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
            ConstraintLayout emptyconstraintLayout = view.findViewById(R.id.delivery_new_task_spinner_container);
            RecyclerView recyclerView = view.findViewById(R.id.delivery_active_task_recyclerView);
            recyclerView.setVisibility(View.INVISIBLE);
            emptyconstraintLayout.setVisibility(View.VISIBLE);
            ImageView spinner = view.findViewById(R.id.delivery_new_task_spinner);
            spinner.setImageResource(R.drawable.box_empty_icon);
        });
    }

    private void resetLoading(View view) {
        ImageView spinner = view.findViewById(R.id.delivery_new_task_spinner);
        spinner.clearAnimation();
        ConstraintLayout emptyconstraintLayout = view.findViewById(R.id.delivery_new_task_spinner_container);
        emptyconstraintLayout.setVisibility(View.GONE);
    }

    private void setLoading(View view) {
        ImageView spinner = view.findViewById(R.id.delivery_new_task_spinner);
        spinner.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.load_rotate);
        spinner.setAnimation(animation);
        animation.start();
    }
}