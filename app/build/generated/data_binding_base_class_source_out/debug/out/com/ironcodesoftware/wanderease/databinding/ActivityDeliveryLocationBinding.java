// Generated by view binder compiler. Do not edit!
package com.ironcodesoftware.wanderease.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.ironcodesoftware.wanderease.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityDeliveryLocationBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final CardView cardView4;

  @NonNull
  public final Toolbar deleiverLocationToolbar;

  @NonNull
  public final TextView deleveryLocationCurrentLocationTextView;

  @NonNull
  public final Button deleveryLocationSaveCurrentLocationButton;

  @NonNull
  public final FrameLayout deliveryLocationMapFragmentLayout;

  @NonNull
  public final ConstraintLayout main;

  @NonNull
  public final TextView textView15;

  private ActivityDeliveryLocationBinding(@NonNull ConstraintLayout rootView,
      @NonNull CardView cardView4, @NonNull Toolbar deleiverLocationToolbar,
      @NonNull TextView deleveryLocationCurrentLocationTextView,
      @NonNull Button deleveryLocationSaveCurrentLocationButton,
      @NonNull FrameLayout deliveryLocationMapFragmentLayout, @NonNull ConstraintLayout main,
      @NonNull TextView textView15) {
    this.rootView = rootView;
    this.cardView4 = cardView4;
    this.deleiverLocationToolbar = deleiverLocationToolbar;
    this.deleveryLocationCurrentLocationTextView = deleveryLocationCurrentLocationTextView;
    this.deleveryLocationSaveCurrentLocationButton = deleveryLocationSaveCurrentLocationButton;
    this.deliveryLocationMapFragmentLayout = deliveryLocationMapFragmentLayout;
    this.main = main;
    this.textView15 = textView15;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityDeliveryLocationBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityDeliveryLocationBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_delivery_location, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityDeliveryLocationBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.cardView4;
      CardView cardView4 = ViewBindings.findChildViewById(rootView, id);
      if (cardView4 == null) {
        break missingId;
      }

      id = R.id.deleiver_location_toolbar;
      Toolbar deleiverLocationToolbar = ViewBindings.findChildViewById(rootView, id);
      if (deleiverLocationToolbar == null) {
        break missingId;
      }

      id = R.id.delevery_location_current_location_textView;
      TextView deleveryLocationCurrentLocationTextView = ViewBindings.findChildViewById(rootView, id);
      if (deleveryLocationCurrentLocationTextView == null) {
        break missingId;
      }

      id = R.id.delevery_location_save_current_location_button;
      Button deleveryLocationSaveCurrentLocationButton = ViewBindings.findChildViewById(rootView, id);
      if (deleveryLocationSaveCurrentLocationButton == null) {
        break missingId;
      }

      id = R.id.delivery_location_map_fragmentLayout;
      FrameLayout deliveryLocationMapFragmentLayout = ViewBindings.findChildViewById(rootView, id);
      if (deliveryLocationMapFragmentLayout == null) {
        break missingId;
      }

      ConstraintLayout main = (ConstraintLayout) rootView;

      id = R.id.textView15;
      TextView textView15 = ViewBindings.findChildViewById(rootView, id);
      if (textView15 == null) {
        break missingId;
      }

      return new ActivityDeliveryLocationBinding((ConstraintLayout) rootView, cardView4,
          deleiverLocationToolbar, deleveryLocationCurrentLocationTextView,
          deleveryLocationSaveCurrentLocationButton, deliveryLocationMapFragmentLayout, main,
          textView15);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
