package com.ironcodesoftware.wanderease.ui.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.UserLogIn;


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
        Button buttonLogin = view.findViewById(R.id.login_button_login);
        Button buttonSwitchSignUp = getActivity().findViewById(R.id.login_switch_button_sighup);

        EditText editTextEmail = view.findViewById(R.id.login_editText_email);
        EditText editTextPassword = view.findViewById(R.id.login_editText_Password);

        WifiManager wifiManager = getActivity().getSystemService(WifiManager.class);
        ConnectivityManager connectivityManager = getActivity().getSystemService(ConnectivityManager.class);


        UserLogIn logIn = UserLogIn.getInstance();
        if(logIn.isDataValid(editTextEmail,editTextPassword)){
            buttonLogin.setText(R.string.loading);
            buttonLogin.setEnabled(false);
            buttonSwitchSignUp.setEnabled(false);

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection(UserLogIn.USER_COLLECTION).where(
                    Filter.and(
                            Filter.equalTo(UserLogIn.EMAIL_FIELD, logIn.getEmail()),
                            Filter.equalTo(UserLogIn.PASSWORD_FIELD, logIn.getPassword())
                    )
            ).get(Source.SERVER)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            if(task.getResult().getDocuments().size()==1){
                                String activeState = task.getResult().getDocuments()
                                        .get(0).getString(UserLogIn.ACTIVE_STATE_FIELD);
                                if(activeState != null){
                                    if(activeState.equals(UserLogIn))

                                }else{
                                    new AlertDialog.Builder(getContext()).setTitle("Login Failed")
                                            .setMessage("Something went wrong. Please contact support")
                                            .setPositiveButton(R.string.responseOk, (dialog, which) -> {
                                                dialog.cancel();
                                            }).show();
                                }
                            }else{
                                new AlertDialog.Builder(getContext(),R.style.Theme_WanderEase_AlertDialog).setTitle("Login Failed")
                                        .setMessage(UserLogIn.ERROR_INVALID_CREDENTIALS)
                                        .setPositiveButton(R.string.responseOk, (dialog, which) -> {
                                            dialog.cancel();
                                        }).show();
                            }
                        }
                        buttonLogin.setText(R.string.login_switch_login);
                        buttonLogin.setEnabled(true);
                        buttonSwitchSignUp.setEnabled(true);
                    })
                    .addOnFailureListener(error -> {
                        new AlertDialog.Builder(getContext()).setTitle("Login Failed")
                                        .setMessage("Please check your internet connection and try again")
                                                .setPositiveButton(R.string.responseOk, (dialog, which) -> {
                                                    dialog.cancel();
                                                }).show();
                        Log.e(MainActivity.TAG,error.getLocalizedMessage());
                    });
        }else{
            buttonLogin.setText(R.string.login_switch_login);
            buttonLogin.setEnabled(true);
            buttonSwitchSignUp.setEnabled(true);
        }
    }
}