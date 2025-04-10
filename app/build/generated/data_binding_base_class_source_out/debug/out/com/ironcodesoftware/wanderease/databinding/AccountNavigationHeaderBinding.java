// Generated by view binder compiler. Do not edit!
package com.ironcodesoftware.wanderease.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.ironcodesoftware.wanderease.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class AccountNavigationHeaderBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ImageButton accountNaviagtionHeaderSettingsButton;

  @NonNull
  public final TextView accountNavigationHeaderLetterTextView;

  @NonNull
  public final TextView accountNavigationHeaderTextViewUsername;

  @NonNull
  public final Button accountNavigationMenuHeaderButtonEditProfile;

  @NonNull
  public final ImageView accountNavigationProfileImageView;

  private AccountNavigationHeaderBinding(@NonNull ConstraintLayout rootView,
      @NonNull ImageButton accountNaviagtionHeaderSettingsButton,
      @NonNull TextView accountNavigationHeaderLetterTextView,
      @NonNull TextView accountNavigationHeaderTextViewUsername,
      @NonNull Button accountNavigationMenuHeaderButtonEditProfile,
      @NonNull ImageView accountNavigationProfileImageView) {
    this.rootView = rootView;
    this.accountNaviagtionHeaderSettingsButton = accountNaviagtionHeaderSettingsButton;
    this.accountNavigationHeaderLetterTextView = accountNavigationHeaderLetterTextView;
    this.accountNavigationHeaderTextViewUsername = accountNavigationHeaderTextViewUsername;
    this.accountNavigationMenuHeaderButtonEditProfile = accountNavigationMenuHeaderButtonEditProfile;
    this.accountNavigationProfileImageView = accountNavigationProfileImageView;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static AccountNavigationHeaderBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static AccountNavigationHeaderBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.account_navigation_header, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static AccountNavigationHeaderBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.account_naviagtion_header_settings_button;
      ImageButton accountNaviagtionHeaderSettingsButton = ViewBindings.findChildViewById(rootView, id);
      if (accountNaviagtionHeaderSettingsButton == null) {
        break missingId;
      }

      id = R.id.account_navigation_header_letter_textView;
      TextView accountNavigationHeaderLetterTextView = ViewBindings.findChildViewById(rootView, id);
      if (accountNavigationHeaderLetterTextView == null) {
        break missingId;
      }

      id = R.id.account_navigation_header_textView_username;
      TextView accountNavigationHeaderTextViewUsername = ViewBindings.findChildViewById(rootView, id);
      if (accountNavigationHeaderTextViewUsername == null) {
        break missingId;
      }

      id = R.id.account_navigation_menu_header_button_edit_profile;
      Button accountNavigationMenuHeaderButtonEditProfile = ViewBindings.findChildViewById(rootView, id);
      if (accountNavigationMenuHeaderButtonEditProfile == null) {
        break missingId;
      }

      id = R.id.account_navigation_profile_imageView;
      ImageView accountNavigationProfileImageView = ViewBindings.findChildViewById(rootView, id);
      if (accountNavigationProfileImageView == null) {
        break missingId;
      }

      return new AccountNavigationHeaderBinding((ConstraintLayout) rootView,
          accountNaviagtionHeaderSettingsButton, accountNavigationHeaderLetterTextView,
          accountNavigationHeaderTextViewUsername, accountNavigationMenuHeaderButtonEditProfile,
          accountNavigationProfileImageView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
