package com.ironcodesoftware.wanderease.ui.login;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.ironcodesoftware.wanderease.R;

public class LogInActivity extends AppCompatActivity {

    LoginFragment loginFragment = new LoginFragment();
    SignUpFragment signUpFragment = new SignUpFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.partner_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setStatusBarColor(getColor(R.color.primary));

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