package com.ironcodesoftware.wanderease.ui.login;

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
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.HttpClient;
import com.ironcodesoftware.wanderease.model.User;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.ui.home.HomeActivity;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Response;


public class SignUpFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Button buttonSignUp = view.findViewById(R.id.signup_button_signup);
        buttonSignUp.setOnClickListener(v -> {
            signup(view);
        });
    }

    private void signup(View view) {
        HashMap<String,String> user = validateUserInput(view);
        if(user != null){
            setLoadingButton(view);
            new Thread(()->{
                try {
                    Log.i(MainActivity.TAG,user.toString());
                    Gson gson = new Gson();
                    Response response = new HttpClient()
                            .post(BuildConfig.HOST_URL + "SignUp",gson.toJson(user));
                    if(response.isSuccessful()){
                        JsonObject responseJsonObject = gson.fromJson(response.body().string(), JsonObject.class);
                        if(responseJsonObject != null && responseJsonObject.has("ok")
                                && responseJsonObject.get("ok").getAsBoolean()){
                            // TODO:
                            saveUserToFirestore(view,user);

                        }else{
                            view.post(()->{
                                resetLoadingButton(view);
                                new AlertDialog.Builder(getContext()).setTitle("SignUp Failed")
                                        .setMessage(response.message())
                                        .setPositiveButton(R.string.responseOk, (dialog, which) -> {
                                            dialog.cancel();
                                        }).show();
                            });
                        }
                    }
                    Log.i(MainActivity.TAG,response.body().string());

                } catch (Exception e) {
                    view.post(()->{
                        resetLoadingButton(view);
                        new AlertDialog.Builder(getContext()).setTitle("SignUp Failed")
                                .setMessage("1: Something went Wrong Please check your connection and try again")
                                .setPositiveButton(R.string.responseOk, (dialog, which) -> {
                                    dialog.cancel();
                                }).show();
                    });
                    Log.e(MainActivity.TAG,e.getLocalizedMessage());
                }
            }).start();
        }
    }

    private void saveUserToFirestore(View view, HashMap<String, String> user) {
        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put(UserLogIn.EMAIL_FIELD, user.get(UserLogIn.EMAIL_FIELD));
        userMap.put(UserLogIn.PASSWORD_FIELD, user.get(UserLogIn.PASSWORD_FIELD));
        userMap.put(UserLogIn.ACTIVE_STATE_FIELD, User.ACTIVE);
        userMap.put(UserLogIn.USER_ROLE_FIELD, User.USER);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(UserLogIn.USER_COLLECTION)
                .add(userMap)
                .addOnSuccessListener(document->{
                    UserLogIn logIn = UserLogIn.getInstance();
                    logIn.setUser_role(User.USER);
                    logIn.setEmail(user.get(UserLogIn.EMAIL_FIELD));
                    logIn.setPassword(user.get(UserLogIn.PASSWORD_FIELD));
                    logIn.serialize(view.getContext());
                    view.post(()->{
                        resetLoadingButton(view);
                        Toast.makeText(getContext(), "Signup Success", Toast.LENGTH_LONG).show();
                    });
                    startActivity(new Intent(getContext(), HomeActivity.class));
                    getActivity().finish();
                })
                .addOnFailureListener(e->{
                    view.post(()->{
                        resetLoadingButton(view);
                        new AlertDialog.Builder(getContext()).setTitle("SignUp Failed")
                                .setMessage("1: Something went Wrong Please check your connection and try again")
                                .setPositiveButton(R.string.responseOk, (dialog, which) -> {
                                    dialog.cancel();
                                }).show();
                    });
                    Log.e(MainActivity.TAG,e.getLocalizedMessage());
                });
    }

    private void setLoadingButton(View view) {
        Button buttonSignUp = view.findViewById(R.id.signup_button_signup);
        Button buttonSwitchLogin = getActivity().findViewById(R.id.login_switch_button_login);

        buttonSignUp.setEnabled(false);
        buttonSignUp.setText(R.string.loading);
        buttonSwitchLogin.setEnabled(false);
    }

    private void resetLoadingButton(View view){
        Button buttonSignUp = view.findViewById(R.id.signup_button_signup);
        Button buttonSwitchLogin = getActivity().findViewById(R.id.login_switch_button_login);

        buttonSignUp.setEnabled(true);
        buttonSignUp.setText(R.string.login_switch_signup);
        buttonSwitchLogin.setEnabled(true);
    }

    private HashMap<String, String> validateUserInput(View view) {
        boolean isValid = true;
        EditText editTextEmail = view.findViewById(R.id.signup_edittext_email);
        EditText editTextDisplayName  = view.findViewById(R.id.signup_edittext_displayname);
        EditText editTextMobile = view.findViewById(R.id.signup_edittext_mobile);
        EditText editTextPassword = view.findViewById(R.id.signup_edittext_password);

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
        EditText editTextEmail = view.findViewById(R.id.signup_edittext_email);
        EditText editTextDisplayName  = view.findViewById(R.id.signup_edittext_displayname);
        EditText editTextMobile = view.findViewById(R.id.signup_edittext_mobile);
        EditText editTextPassword = view.findViewById(R.id.signup_edittext_password);
        editTextPassword.setText("");
        editTextEmail.setText("");
        editTextDisplayName.setText("");
        editTextMobile.setText("");
    }
}

