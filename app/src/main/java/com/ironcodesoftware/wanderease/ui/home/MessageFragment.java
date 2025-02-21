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
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.Notification;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.WanderNotification;
import com.ironcodesoftware.wanderease.model.adaper.MessageAdapter;

import java.io.IOException;
import java.util.List;

public class MessageFragment extends Fragment {

    UserLogIn logIn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            logIn = UserLogIn.getLogin(getContext());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        loadMessages(view);
    }

    private void loadMessages(View view) {
        setLoading(view);
        RecyclerView recyclerView = view.findViewById(R.id.messages_recyclerView);
        recyclerView.bringToFront();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(Notification.COLLECTION).where(Filter.and(
                Filter.equalTo(Notification.F_USER, logIn.getEmail()),
                Filter.equalTo(Notification.F_STATUS, Notification.State.Not_Seen.toString())
        )).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null && !value.isEmpty()){
                    List<DocumentSnapshot> documents = value.getDocuments();
                    view.post(()->{
                        resetLoading(view);
                       recyclerView.setVisibility(View.VISIBLE);
                       TextView textViewEmpty = view.findViewById(R.id.messages_empty_textView);
                       textViewEmpty.setVisibility(View.INVISIBLE);
                       recyclerView.setAdapter(new MessageAdapter(documents));
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
            ConstraintLayout emptyconstraintLayout = view.findViewById(R.id.messages_spinner_container);
            RecyclerView recyclerView = view.findViewById(R.id.messages_recyclerView);
            recyclerView.setVisibility(View.INVISIBLE);
            emptyconstraintLayout.setVisibility(View.VISIBLE);
            ImageView spinner = view.findViewById(R.id.messages_spinner_imageView);
            spinner.setVisibility(View.INVISIBLE);
            TextView textViewEmpty = view.findViewById(R.id.messages_empty_textView);
            textViewEmpty.setVisibility(View.VISIBLE);
        });
    }

    private void resetLoading(View view) {
        ImageView spinner = view.findViewById(R.id.messages_spinner_imageView);
        spinner.clearAnimation();
        spinner.setVisibility(View.GONE);
    }

    private void setLoading(View view) {
        ImageView spinner = view.findViewById(R.id.messages_spinner_imageView);
        spinner.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.load_rotate);
        spinner.setAnimation(animation);
        animation.start();
    }
}