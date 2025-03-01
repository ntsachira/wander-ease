package com.ironcodesoftware.wanderease.ui.home;

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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.SQLiteHelper;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.Vehicle;
import com.ironcodesoftware.wanderease.model.adaper.BookingAdapter;

import java.io.IOException;
import java.util.List;

public class BookingFragment extends Fragment {

    private UserLogIn login;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            login = UserLogIn.getLogin(getContext());
        } catch (IOException | ClassNotFoundException e) {
            getActivity().finish();
        }
        loadBookings(view);
    }

    private void loadBookings(View view) {
        setLoading(view);
        RecyclerView recyclerView = view.findViewById(R.id.booking_recyclerView);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Log.d(MainActivity.TAG,login.getEmail());
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("booking")
                .whereEqualTo(Vehicle.F_USER, login.getEmail())
                .addSnapshotListener((value, error) -> {
                    if(error == null && !value.isEmpty()){
                        List<DocumentSnapshot> documents = value.getDocuments();
                        view.post(()->{
                            resetLoading(view);
                            recyclerView.setAdapter(new BookingAdapter(documents,getActivity()));
                        });


                    }else{
                        view.post(()->{
                            resetLoading(view);
                            showEmptyCard(view);
                        });
                    }
                });
    }

    private void showEmptyCard(View view) {
        view.post(()->{
            ConstraintLayout spinnerContainer = view.findViewById(R.id.booking_spinner_container);
            RecyclerView recyclerView = view.findViewById(R.id.booking_recyclerView);
            recyclerView.setVisibility(View.INVISIBLE);
            spinnerContainer.setVisibility(View.VISIBLE);
            ImageView spinner = view.findViewById(R.id.booking_spinner_imageView);
            spinner.setImageResource(R.drawable.box_empty_icon);
        });
    }

    private void resetLoading(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.booking_recyclerView);
        recyclerView.bringToFront();
        ImageView spinner = view.findViewById(R.id.booking_spinner_imageView);
        spinner.clearAnimation();
        ConstraintLayout spinnerContainer = view.findViewById(R.id.booking_spinner_container);
        spinnerContainer.setVisibility(View.GONE);
    }

    private void setLoading(View view) {
        ConstraintLayout spinnerContainer = view.findViewById(R.id.booking_spinner_container);
        spinnerContainer.setVisibility(View.VISIBLE);
        spinnerContainer.bringToFront();
        ImageView spinner = view.findViewById(R.id.booking_spinner_imageView);
        spinner.setImageResource(lk.payhere.androidsdk.R.drawable.spinner_circle);
        spinner.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.load_rotate);
        spinner.setAnimation(animation);
        animation.start();
        spinner.setImageResource(lk.payhere.androidsdk.R.drawable.spinner_circle);
    }
}