package com.ironcodesoftware.wanderease.model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.ui.admin.AdminActivity;
import com.ironcodesoftware.wanderease.ui.delivery.DeliveryActivity;
import com.ironcodesoftware.wanderease.ui.home.HomeActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class UserLogIn implements Serializable {

    private String email;
    private String password;
    private String user_role;

    public UserLogIn() {
    }

    public UserLogIn(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_role() {
        return user_role;
    }

    public void setUser_role(String user_role) {
        this.user_role = user_role;
    }

    private static UserLogIn userLogIn;

    //field names
    public static final String EMAIL_FIELD = "email";
    public static final String DISPLAY_NAME_FIELD = "display_name";
    public static final String MOBILE_FIELD = "mobile";
    public static final String PASSWORD_FIELD = "password";
    public static final String ACTIVE_STATE_FIELD = "active_state";
    public static final String USER_ROLE_FIELD = "user_role";
    public static final String USER_COLLECTION = "user";


    //warning messages
    public static final String ERROR_EMAIL_EMPTY = "Email Cannot be empty";
    public static final String ERROR_EMAIL_INVALID = "Invalid email address";
    public static final String ERROR_DISPLAY_NAME_EMPTY = "Name Cannot be empty";
    public static final String ERROR_MOBILE_EMPTY = "Mobile cannot be empty";
    public static final String ERROR_MOBILE_INVALID = "Invalid mobile number";
    public static final String ERROR_PASSWORD_EMPTY = "Password Cannot be empty";
    public static final String ERROR_INVALID_CREDENTIALS = "Invalid Email or Password";

    public static UserLogIn getLogin(Context context) throws IOException, ClassNotFoundException {
        File userLog = new File(context.getFilesDir(), context.getString(R.string.user_log));
        if(userLog.exists()){
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(userLog));
                UserLogIn userLogIn = (UserLogIn) objectInputStream.readObject();
                if(userLogIn.getEmail() != null && userLogIn.getPassword() != null && userLogIn.getUser_role()!= null){
                    return userLogIn;
                }
        }
        return null;
    }

    public static boolean hasLogin(Context context) throws IOException, ClassNotFoundException {
        return getLogin(context) != null;
    }


    public boolean isDataValid(EditText editTextEmail,EditText editTextPassword){
        boolean isValid = true;
        if(String.valueOf(editTextEmail.getText()).isBlank()){
            editTextEmail.setError(ERROR_EMAIL_EMPTY);
            isValid =false;
        }
        if(String.valueOf(editTextPassword.getText()).isBlank()){
            editTextPassword.setError(ERROR_PASSWORD_EMPTY);
            isValid =false;
        }

        if(isValid){
            email = String.valueOf(editTextEmail.getText()).trim();
            password = String.valueOf(editTextPassword.getText()).trim();
            return true;
        }
        return false;
    }




    public static UserLogIn getInstance(){
        if(userLogIn == null){
            userLogIn = new UserLogIn();
        }
        return userLogIn;
    }

    public boolean serialize(Context context){
        boolean isSuccess = false;
        File userLog = new File(context.getFilesDir(), context.getString(R.string.user_log));
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    new FileOutputStream(userLog)
            );
            objectOutputStream.writeObject(this);
            objectOutputStream.flush();
            isSuccess = true;
        } catch (IOException e) {
            Log.e(MainActivity.TAG, e.getMessage());
        }

        if(!isSuccess){
            Toast.makeText(
                    context,
                    "Unable to save login",
                    Toast.LENGTH_LONG
            ).show();
        }
        return isSuccess;
    }



}
