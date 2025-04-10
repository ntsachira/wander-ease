// Generated by view binder compiler. Do not edit!
package com.ironcodesoftware.wanderease.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.ironcodesoftware.wanderease.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentLoginBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button loginButtonGotoRegister;

  @NonNull
  public final Button loginButtonLogin;

  @NonNull
  public final EditText loginEditTextEmail;

  @NonNull
  public final EditText loginEditTextPassword;

  private FragmentLoginBinding(@NonNull ConstraintLayout rootView,
      @NonNull Button loginButtonGotoRegister, @NonNull Button loginButtonLogin,
      @NonNull EditText loginEditTextEmail, @NonNull EditText loginEditTextPassword) {
    this.rootView = rootView;
    this.loginButtonGotoRegister = loginButtonGotoRegister;
    this.loginButtonLogin = loginButtonLogin;
    this.loginEditTextEmail = loginEditTextEmail;
    this.loginEditTextPassword = loginEditTextPassword;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentLoginBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentLoginBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_login, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentLoginBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.login_button_gotoRegister;
      Button loginButtonGotoRegister = ViewBindings.findChildViewById(rootView, id);
      if (loginButtonGotoRegister == null) {
        break missingId;
      }

      id = R.id.login_button_login;
      Button loginButtonLogin = ViewBindings.findChildViewById(rootView, id);
      if (loginButtonLogin == null) {
        break missingId;
      }

      id = R.id.login_editText_email;
      EditText loginEditTextEmail = ViewBindings.findChildViewById(rootView, id);
      if (loginEditTextEmail == null) {
        break missingId;
      }

      id = R.id.login_editText_Password;
      EditText loginEditTextPassword = ViewBindings.findChildViewById(rootView, id);
      if (loginEditTextPassword == null) {
        break missingId;
      }

      return new FragmentLoginBinding((ConstraintLayout) rootView, loginButtonGotoRegister,
          loginButtonLogin, loginEditTextEmail, loginEditTextPassword);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
