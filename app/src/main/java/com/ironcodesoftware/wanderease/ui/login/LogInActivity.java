package com.ironcodesoftware.wanderease.ui.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.WanderDialog;

public class LogInActivity extends AppCompatActivity {

    LoginFragment loginFragment = new LoginFragment();
    SignUpFragment signUpFragment = new SignUpFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.delivery_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setStatusBarColor(getColor(R.color.primary));

        initOnBackPressDispatcher(this);

        loadFragment(loginFragment);

        Button buttonLoginSwitch = findViewById(R.id.login_switch_button_login);
        buttonLoginSwitch.setOnClickListener(view->{
            loadFragment(loginFragment);
        });

        Button buttonSignUpSwitch = findViewById(R.id.login_switch_button_sighup);
        buttonSignUpSwitch.setOnClickListener(view->{
            loadFragment(signUpFragment);
        });


    }

    private void initOnBackPressDispatcher(Context context) {
        getOnBackPressedDispatcher().addCallback(this,new OnBackPressedCallback(true){
            @Override
            public void handleOnBackPressed() {
                showExitConfirmation();
            }
            private void showExitConfirmation() {
                AlertDialog confirm = WanderDialog.confirm(
                        context,
                        "Are you sure you want to exit?");
                confirm.setButton(
                        DialogInterface.BUTTON_POSITIVE,
                        "Yes",
                        (dialog, which) -> finishAffinity());
                confirm.setButton(
                        DialogInterface.BUTTON_NEGATIVE,
                        "No",
                        (dialog, which) -> dialog.dismiss());
                confirm.show();
            }
        });
    }



    public void loadSignUpFragment(){
        loadFragment(signUpFragment);
    }
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.login_fragmentContainerView, fragment)
                .setReorderingAllowed(true)
                .commit();
        setSwitchStyle(fragment);
    }
    private void setSwitchStyle(Fragment fragment){
        Button buttonLogin = findViewById(R.id.login_switch_button_login);
        Button buttonSignup = findViewById(R.id.login_switch_button_sighup);
        if(fragment.getClass()== LoginFragment.class){
            buttonLogin.setEnabled(false);
            buttonLogin.setTextColor(getColor(R.color.white));
            buttonLogin.setBackgroundColor(getColor(R.color.primary));
            buttonSignup.setEnabled(true);
            buttonSignup.setTextColor(getColor(R.color.primary));
            buttonSignup.setBackgroundColor(Color.TRANSPARENT);
        }else{
            buttonLogin.setEnabled(true);
            buttonLogin.setTextColor(getColor(R.color.primary));
            buttonLogin.setBackgroundColor(Color.TRANSPARENT);
            buttonSignup.setEnabled(false);
            buttonSignup.setTextColor(getColor(R.color.white));
            buttonSignup.setBackgroundColor(getColor(R.color.primary));
        }
    }


}