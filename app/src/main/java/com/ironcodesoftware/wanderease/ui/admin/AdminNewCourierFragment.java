package com.ironcodesoftware.wanderease.ui.admin;

import android.app.AlertDialog;
import android.content.Intent;
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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.HttpClient;
import com.ironcodesoftware.wanderease.model.User;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.WanderDialog;
import com.ironcodesoftware.wanderease.ui.home.HomeActivity;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdminNewCourierFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_new_courier, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonRegister = view.findViewById(R.id.admin_courier_register_button);
        buttonRegister.setOnClickListener(v -> {
            signup(view);
        });
    }

    private void signup(View view) {
        HashMap<String,String> user = validateUserInput(view);
        Gson gson = new Gson();
        if(user != null){
            AlertDialog loading = WanderDialog.loading(getContext());
            loading.show();
            Request request = new Request.Builder().url(BuildConfig.HOST_URL + "SignUp")
                    .post(RequestBody.create(gson.toJson(user), HttpClient.JSON)).build();
            HttpClient.getInstance().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    view.post(()->{
                        loading.cancel();
                        new AlertDialog.Builder(getContext()).setTitle("SignUp Failed")
                                .setMessage("1: Something went Wrong Please check your connection and try again")
                                .setPositiveButton(R.string.responseOk, (dialog, which) -> {
                                    dialog.cancel();
                                }).show();
                    });
                    Log.e(MainActivity.TAG,e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if(response.isSuccessful()){
                        saveUserToFirestore(view,user,loading);
                    }else{
                        view.post(()->{
                            loading.cancel();
                            new AlertDialog.Builder(getContext()).setTitle("SignUp Failed")
                                    .setMessage("Connection failed. no Server response")
                                    .setPositiveButton(R.string.responseOk, (dialog, which) -> {
                                        dialog.cancel();
                                    }).show();
                        });
                    }
                }
            });
        }
    }

    private void saveUserToFirestore(View view, HashMap<String, String> user, AlertDialog  loading) {
        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put(UserLogIn.EMAIL_FIELD, user.get(UserLogIn.EMAIL_FIELD));
        userMap.put(UserLogIn.PASSWORD_FIELD, user.get(UserLogIn.PASSWORD_FIELD));
        userMap.put(UserLogIn.ACTIVE_STATE_FIELD, User.ACTIVE);
        userMap.put(UserLogIn.USER_ROLE_FIELD, User.USER);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(UserLogIn.USER_COLLECTION)
                .add(userMap)
                .addOnSuccessListener(document->{
                    view.post(()->{
                        loading.cancel();
                        Toast.makeText(getContext(), "Signup Success", Toast.LENGTH_LONG).show();
                        resetUserInput(view);
                    });

                })
                .addOnFailureListener(e->{
                    view.post(()->{
                        loading.cancel();
                        new AlertDialog.Builder(getContext()).setTitle("SignUp Failed")
                                .setMessage("1: Something went Wrong Please check your connection and try again")
                                .setPositiveButton(R.string.responseOk, (dialog, which) -> {
                                    dialog.cancel();
                                }).show();
                    });
                    Log.e(MainActivity.TAG,e.getLocalizedMessage());
                });
    }

    private HashMap<String, String> validateUserInput(View view) {
        boolean isValid = true;
        EditText editTextEmail = view.findViewById(R.id.admin_courier_email_edittext);
        EditText editTextDisplayName  = view.findViewById(R.id.admin_courier_name_editTextText);
        EditText editTextMobile = view.findViewById(R.id.admin_courier_Mobile_editTextPhone);
        EditText editTextPassword = view.findViewById(R.id.admin_courier_password_editTextTextPassword);

        String email = String.valueOf(editTextEmail.getText()).trim();
        String displayName = String.valueOf(editTextDisplayName.getText()).trim();
        String mobile = String.valueOf(editTextMobile.getText()).trim();
        String password = String.valueOf(editTextPassword.getText()).trim();

        if(email.isEmpty()){
            isValid = false;
            editTextEmail.setError(UserLogIn.ERROR_EMAIL_EMPTY);
        } else if (!email.matches(getString(R.string.email_regex))) {
            isValid = false;
            editTextEmail.setError(UserLogIn.ERROR_EMAIL_INVALID);
        }
        if(displayName.isEmpty()){
            isValid = false;
            editTextDisplayName.setError(UserLogIn.ERROR_DISPLAY_NAME_EMPTY);
        }
        if(mobile.isEmpty()){
            isValid = false;
            editTextMobile.setError(UserLogIn.ERROR_MOBILE_EMPTY);
        } else if (!mobile.matches(getString(R.string.mobile_regex))) {
            isValid = false;
            editTextMobile.setError(UserLogIn.ERROR_MOBILE_INVALID);
        }
        if(password.isEmpty()){
            isValid = false;
            editTextPassword.setError(UserLogIn.ERROR_PASSWORD_EMPTY);
        }

        if(isValid){
            HashMap<String,String> user = new HashMap<>();
            user.put(UserLogIn.EMAIL_FIELD, email);
            user.put(UserLogIn.DISPLAY_NAME_FIELD, displayName);
            user.put(UserLogIn.MOBILE_FIELD, mobile);
            user.put(UserLogIn.PASSWORD_FIELD, password);
            return user;
        }
        return null;
    }

    public void resetUserInput(View view){
        EditText editTextEmail = view.findViewById(R.id.admin_courier_email_edittext);
        EditText editTextDisplayName  = view.findViewById(R.id.admin_courier_name_editTextText);
        EditText editTextMobile = view.findViewById(R.id.admin_courier_Mobile_editTextPhone);
        EditText editTextPassword = view.findViewById(R.id.admin_courier_password_editTextTextPassword);
        editTextPassword.setText("");
        editTextEmail.setText("");
        editTextDisplayName.setText("");
        editTextMobile.setText("");
    }
}