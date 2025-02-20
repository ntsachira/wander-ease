package com.ironcodesoftware.wanderease.ui.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.Order;
import com.ironcodesoftware.wanderease.model.User;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.WanderDialog;
import com.ironcodesoftware.wanderease.model.adaper.AdminToAssignDeliveryAdapter;
import com.ironcodesoftware.wanderease.ui.ErrorActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminToAssignFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_to_assign, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AutoCompleteTextView textView = view.findViewById(R.id.admin_deliver_assign_person_select_autoCompleteTextView);

        textView.setOnClickListener(v -> {
            textView.showDropDown();
        });
        try {
            loadOrders(view);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        Button buttonCancel = view.findViewById(R.id.admin_deliver_assign_cancel_button);
        buttonCancel.setOnClickListener(v->{
            new FlingAnimation(view.findViewById(R.id.slide_down_card), DynamicAnimation.TRANSLATION_Y)
                    .setStartVelocity(-9000f).setFriction(1f).start();

        });

        loadCouriers(view);
    }

    private void loadCouriers(View view) {
        AutoCompleteTextView textViewCourier = view.findViewById(R.id.admin_deliver_assign_person_select_autoCompleteTextView);
        ArrayList<String> courierList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.single_spinner_item, courierList);
        textViewCourier.setAdapter(arrayAdapter);
        FirebaseFirestore.getInstance().collection("user")
                .whereEqualTo(UserLogIn.USER_ROLE_FIELD, User.DELIVERY).get()
                .addOnCompleteListener(task->{
                    if(task.isSuccessful()){
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            courierList.add(document.getString(UserLogIn.EMAIL_FIELD));
                        }
                        view.post(arrayAdapter::notifyDataSetChanged);
                    }else{
                        view.post(()->{
                            WanderDialog.cancel(getContext(), "2:Failed to load Couriers");
                        });
                    }
                })
                .addOnFailureListener(new ErrorActivity(),e->{
                    Log.e(MainActivity.TAG,"Loading couriers failed",e);
                    view.post(()->{
                        WanderDialog.cancel(getContext(), "1:Failed to load Couriers");
                    });
                } );
        textViewCourier.setOnClickListener(v->{
            textViewCourier.showDropDown();
        });
        textViewCourier.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                textViewCourier.showDropDown();
            }
        });
    }

    private void loadOrders(View view) throws IOException, ClassNotFoundException {
        setLoading(view);
        RecyclerView recyclerView = view.findViewById(R.id.admin_to_assign_recyclerview);
        recyclerView.bringToFront();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Order").where(Filter.or(
                        Filter.equalTo(Order.F_STATE,Order.State.Pending.name()),
                        Filter.equalTo(Order.F_STATE,Order.State.Processing.name())
                ))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.isEmpty()){
                    List<DocumentSnapshot> documents = value.getDocuments();

                    view.post(()->{
                        resetLoading(view);
                        recyclerView.setAdapter(new AdminToAssignDeliveryAdapter(
                                documents, getActivity()));
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