package com.ironcodesoftware.wanderease.ui.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.HttpClient;
import com.ironcodesoftware.wanderease.model.Notification;
import com.ironcodesoftware.wanderease.model.SQLiteHelper;
import com.ironcodesoftware.wanderease.model.User;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.WanderNotification;
import com.ironcodesoftware.wanderease.ui.admin.AdminActivity;
import com.ironcodesoftware.wanderease.ui.delivery.DeliveryActivity;
import com.ironcodesoftware.wanderease.ui.home.HomeActivity;
import com.ironcodesoftware.wanderease.ui.home.MessagesActivity;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button buttonGotoRegister = view.findViewById(R.id.login_button_gotoRegister);
        buttonGotoRegister.setOnClickListener(v -> {
            gotoSignUp();
        });

        Button buttonLogin = view.findViewById(R.id.login_button_login);
        buttonLogin.setOnClickListener(v -> {
            login(view);
        });
    }

    private void gotoSignUp() {
        LogInActivity logInActivity = (LogInActivity) getActivity();
        if(logInActivity!=null){
            logInActivity.loadSignUpFragment();
        }
    }

    private void login(View view) {
        EditText editTextEmail = view.findViewById(R.id.login_editText_email);
        EditText editTextPassword = view.findViewById(R.id.login_editText_Password);


        UserLogIn logIn = UserLogIn.getInstance();
        if(logIn.isDataValid(editTextEmail,editTextPassword)){
            setLoadingButton(view);

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection(UserLogIn.USER_COLLECTION).where(
                    Filter.and(
                            Filter.equalTo(UserLogIn.EMAIL_FIELD, logIn.getEmail()),
                            Filter.equalTo(UserLogIn.PASSWORD_FIELD, logIn.getPassword())
                    )
            ).get(Source.SERVER)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            if(authenticate(task.getResult().getDocuments(),logIn,view)){
                                if(logIn.serialize(view.getContext())){
                                    logIn.requestUserDetails(getContext(),logIn.getEmail());
                                    checkNotifications(logIn.getEmail());
                                    if(logIn.getUser_role().equals(User.USER)){
                                        gotoActivity(HomeActivity.class);
                                    } else if (logIn.getUser_role().equals(User.DELIVERY)) {
                                        gotoActivity(DeliveryActivity.class);
                                    } else if (logIn.getUser_role().equals(User.ADMIN)) {
                                        gotoActivity(AdminActivity.class);
                                    }
                                }else{
                                    resetLoadingButton(view);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(error -> {
                        resetLoadingButton(view);
                        new AlertDialog.Builder(getContext()).setTitle("Login Failed")
                                        .setMessage("Please check your internet connection and try again")
                                                .setPositiveButton(R.string.responseOk, (dialog, which) -> {
                                                    dialog.cancel();
                                                }).show();
                        Log.e(MainActivity.TAG,error.getLocalizedMessage(),error);

                    });
        }else{
            resetLoadingButton(view);
        }
    }



    private void checkNotifications(String email) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(Notification.COLLECTION).where(Filter.and(
                Filter.equalTo(Notification.F_USER, email),
                Filter.equalTo(Notification.F_STATUS, Notification.State.Not_Seen.toString())
        )).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null && !value.isEmpty() && getContext() != null){
                    WanderNotification.notify(getContext(), MessagesActivity.class );
                }else{
                    Log.e(MainActivity.TAG, error!=null?error.getMessage():"No new notifications");
                }
            }
        });
    }

    private void gotoActivity(Class<? extends AppCompatActivity> destinationActivityClass) {
        startActivity(new Intent(getContext(),destinationActivityClass));
        getActivity().finish();
    }

    private boolean authenticate(List<DocumentSnapshot> documents,UserLogIn logIn,View view) {
        if(documents.size()==1){
            String activeState = documents
                    .get(0).getString(UserLogIn.ACTIVE_STATE_FIELD);
            String userRole = documents.get(0).getString(UserLogIn.USER_ROLE_FIELD);
            if(activeState != null){
                if(activeState.equals(User.ACTIVE)){
                    logIn.setUser_role(userRole);
                    return true;
                }else{
                    resetLoadingButton(view);
                    new AlertDialog.Builder(getContext()).setTitle("Login Failed")
                            .setMessage("Your account is currently INACTIVE. Please contact support")
                            .setPositiveButton(R.string.responseOk, (dialog, which) -> {
                                dialog.cancel();
                            }).show();
                }
            }else{
                resetLoadingButton(view);
                new AlertDialog.Builder(getContext()).setTitle("Login Failed")
                        .setMessage("Something went wrong. Please contact support")
                        .setPositiveButton(R.string.responseOk, (dialog, which) -> {
                            dialog.cancel();
                        }).show();
            }
        }else{
            resetLoadingButton(view);
            new AlertDialog.Builder(getContext(),R.style.Theme_WanderEase_AlertDialog).setTitle("Login Failed")
                    .setMessage(UserLogIn.ERROR_INVALID_CREDENTIALS)
                    .setPositiveButton(R.string.responseOk, (dialog, which) -> {
                        dialog.cancel();
                    }).show();
        }
        return false;
    }


    private void setLoadingButton(View view){
        Button buttonLogin = view.findViewById(R.id.login_button_login);
        Button buttonSwitchSignUp = getActivity().findViewById(R.id.login_switch_button_sighup);
        buttonLogin.setText(R.string.loading);
        buttonLogin.setEnabled(false);
        buttonSwitchSignUp.setEnabled(false);
    }
    private void resetLoadingButton(View view){
        Button buttonLogin = view.findViewById(R.id.login_button_login);
        Button buttonSwitchSignUp = getActivity().findViewById(R.id.login_switch_button_sighup);
        buttonLogin.setText(R.string.login_switch_login);
        buttonLogin.setEnabled(true);
        buttonSwitchSignUp.setEnabled(true);
    }
}