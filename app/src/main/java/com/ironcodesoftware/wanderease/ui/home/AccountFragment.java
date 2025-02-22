package com.ironcodesoftware.wanderease.ui.home;

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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.HttpClient;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.ui.home.account.MyOrdersActivity;
import com.ironcodesoftware.wanderease.ui.home.account.SettingsActivity;
import com.ironcodesoftware.wanderease.ui.login.LogInActivity;
import com.ironcodesoftware.wanderease.ui.partner.PartnerActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AccountFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUserDetails(view);
        NavigationView navigationView = view.findViewById(R.id.account_navigationView);
        View headerView = navigationView.getHeaderView(0);
        ImageButton imageButtonSettings = headerView.findViewById(R.id.account_naviagtion_header_settings_button);
        imageButtonSettings.setOnClickListener(v->{
            startActivity(new Intent(getContext(), SettingsActivity.class));
        });

        Button buttonOpenPartnerActivity = view.findViewById(R.id.account_button_open_partner_button);
        buttonOpenPartnerActivity.setOnClickListener(v->{
            startActivity(new Intent(getContext(), PartnerActivity.class));
        });
        navigationView.setNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.account_orders){
                startActivity(new Intent(getContext(), MyOrdersActivity.class));
            }
            return false;
        });
    }

    private void setUserDetails(View view) {
        try {
            NavigationView navigationView = view.findViewById(R.id.account_navigationView);
            TextView textViewUsername = navigationView.getHeaderView(0)
                    .findViewById(R.id.account_navigation_header_textView_username);
            if(UserLogIn.hasLogin(getContext())){
                UserLogIn login = UserLogIn.getLogin(getContext());
                if(login.getDisplay_name()!=null){
                    textViewUsername.setText(login.getDisplay_name());
                }else{
                    Log.d(MainActivity.TAG,"Test1");
                    textViewUsername.setText(login.getEmail());
                    //requestUserDetails(view, login.getEmail());
                }
            }else{
                gotoLogin();
            }
        } catch (IOException | ClassNotFoundException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG)
                    .show();
            Log.e(MainActivity.TAG,e.getLocalizedMessage());
            gotoLogin();
        }
    }

    private void requestUserDetails(View view, String email){

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(UserLogIn.EMAIL_FIELD, email);
            Request request = new Request.Builder().url(BuildConfig.HOST_URL + "GetProfile")
                    .post(RequestBody.create(jsonObject.toString(), HttpClient.JSON)).build();
            HttpClient.getInstance().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    promptFailed("1:Profile loading failed");
                    Log.e(MainActivity.TAG,e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if(response.isSuccessful()){
                        Gson gson = new Gson();
                        JsonObject responseJson = gson.fromJson(response.body().string(), JsonObject.class);
                        if(responseJson.has("ok")
                                && responseJson.get("ok").getAsBoolean()){
                            String name = responseJson.getAsJsonObject("profile")
                                    .get(UserLogIn.DISPLAY_NAME_FIELD).getAsString();
                            NavigationView navigationView = view.findViewById(R.id.account_navigationView);
                            TextView textViewUsername = navigationView.getHeaderView(0)
                                    .findViewById(R.id.account_navigation_header_textView_username);
                            view.post(()->{
                                textViewUsername.setText(name);
                            });
                            try {
                                UserLogIn login = UserLogIn.getLogin(view.getContext());
                                login.setDisplay_name(name);
                                login.serialize(getContext());
                            } catch (ClassNotFoundException e) {
                                Log.e(MainActivity.TAG,e.getLocalizedMessage());
                            }
                        }else{
                            promptFailed("2:Profile loading failed");
                        }
                    }else{
                        promptFailed("3:Profile loading failed");
                    }
                }
            });

    }

    void promptFailed(String message){
        getView().post(()->{
            Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_LONG);
            snackbar.setAction("Dismiss", v->{
                snackbar.dismiss();
            });
            snackbar.show();
        });
    }

    private void gotoLogin(){
        startActivity(new Intent(getContext(), LogInActivity.class));
        getActivity().finish();
    }
}

