package com.ironcodesoftware.wanderease.ui.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.UserLogIn;


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
        EditText editTextEmail = view.findViewById(R.id.signup_edittext_email);
        EditText editTextDisplayName  = view.findViewById(R.id.signup_edittext_displayname);
        EditText editTextMobile = view.findViewById(R.id.signup_edittext_mobile);
        EditText editTextPassword = view.findViewById(R.id.signup_edittext_password);

        String email = String.valueOf(editTextEmail.getText()).trim();
        String displayName = String.valueOf(editTextDisplayName.getText()).trim();
        String mobile = String.valueOf(editTextMobile.getText()).trim();
        String password = String.valueOf(editTextPassword.getText()).trim();

        if(email.isEmpty()){
            editTextEmail.setError(UserLogIn.ERROR_EMAIL_EMPTY);
        } else if (!email.matches(getString(R.string.email_regex))) {
            editTextEmail.setError(UserLogIn.ERROR_EMAIL_INVALID);
        }
        if(displayName.isEmpty()){
            editTextDisplayName.setError(UserLogIn.ERROR_DISPLAY_NAME_EMPTY);
        }
        if(mobile.isEmpty()){
            editTextMobile.setError(UserLogIn.ERROR_MOBILE_EMPTY);
        } else if (!mobile.matches(getString(R.string.mobile_regex))) {
            
        }
        if(password.isEmpty()){
            editTextPassword.setError(UserLogIn.ERROR_PASSWORD_EMPTY);
        }


    }
}