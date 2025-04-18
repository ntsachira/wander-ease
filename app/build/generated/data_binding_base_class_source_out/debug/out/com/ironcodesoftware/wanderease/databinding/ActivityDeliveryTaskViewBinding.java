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
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.ironcodesoftware.wanderease.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityDeliveryTaskViewBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final CardView cardView7;

  @NonNull
  public final CardView cardView8;

  @NonNull
  public final Toolbar deliveryTaskViewActivityToolbar;

  @NonNull
  public final Button deliveryTaskViewCallButton;

  @NonNull
  public final TextView deliveryTaskViewDeliveryAddressTextView;

  @NonNull
  public final Button deliveryTaskViewDoneButton;

  @NonNull
  public final TextView deliveryTaskViewItemTotakTextView;

  @NonNull
  public final RecyclerView deliveryTaskViewItemsRecyclerView;

  @NonNull
  public final FrameLayout deliveryTaskViewMapFramelayout;

  @NonNull
  public final TextView deliveryTaskViewOrderIdTextView;

  @NonNull
  public final TextView deliveryTaskViewTotalAmountTextView;

  @NonNull
  public final ConstraintLayout main;

  @NonNull
  public final TextView textView10;

  @NonNull
  public final TextView textView33;

  @NonNull
  public final TextView textView34;

  @NonNull
  public final TextView textView36;

  @NonNull
  public final TextView textView38;

  private ActivityDeliveryTaskViewBinding(@NonNull ConstraintLayout rootView,
      @NonNull CardView cardView7, @NonNull CardView cardView8,
      @NonNull Toolbar deliveryTaskViewActivityToolbar, @NonNull Button deliveryTaskViewCallButton,
      @NonNull TextView deliveryTaskViewDeliveryAddressTextView,
      @NonNull Button deliveryTaskViewDoneButton,
      @NonNull TextView deliveryTaskViewItemTotakTextView,
      @NonNull RecyclerView deliveryTaskViewItemsRecyclerView,
      @NonNull FrameLayout deliveryTaskViewMapFramelayout,
      @NonNull TextView deliveryTaskViewOrderIdTextView,
      @NonNull TextView deliveryTaskViewTotalAmountTextView, @NonNull ConstraintLayout main,
      @NonNull TextView textView10, @NonNull TextView textView33, @NonNull TextView textView34,
      @NonNull TextView textView36, @NonNull TextView textView38) {
    this.rootView = rootView;
    this.cardView7 = cardView7;
    this.cardView8 = cardView8;
    this.deliveryTaskViewActivityToolbar = deliveryTaskViewActivityToolbar;
    this.deliveryTaskViewCallButton = deliveryTaskViewCallButton;
    this.deliveryTaskViewDeliveryAddressTextView = deliveryTaskViewDeliveryAddressTextView;
    this.deliveryTaskViewDoneButton = deliveryTaskViewDoneButton;
    this.deliveryTaskViewItemTotakTextView = deliveryTaskViewItemTotakTextView;
    this.deliveryTaskViewItemsRecyclerView = deliveryTaskViewItemsRecyclerView;
    this.deliveryTaskViewMapFramelayout = deliveryTaskViewMapFramelayout;
    this.deliveryTaskViewOrderIdTextView = deliveryTaskViewOrderIdTextView;
    this.deliveryTaskViewTotalAmountTextView = deliveryTaskViewTotalAmountTextView;
    this.main = main;
    this.textView10 = textView10;
    this.textView33 = textView33;
    this.textView34 = textView34;
    this.textView36 = textView36;
    this.textView38 = textView38;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityDeliveryTaskViewBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityDeliveryTaskViewBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_delivery_task_view, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityDeliveryTaskViewBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.cardView7;
      CardView cardView7 = ViewBindings.findChildViewById(rootView, id);
      if (cardView7 == null) {
        break missingId;
      }

      id = R.id.cardView8;
      CardView cardView8 = ViewBindings.findChildViewById(rootView, id);
      if (cardView8 == null) {
        break missingId;
      }

      id = R.id.delivery_task_view_activity_toolbar;
      Toolbar deliveryTaskViewActivityToolbar = ViewBindings.findChildViewById(rootView, id);
      if (deliveryTaskViewActivityToolbar == null) {
        break missingId;
      }

      id = R.id.delivery_taskView_call_button;
      Button deliveryTaskViewCallButton = ViewBindings.findChildViewById(rootView, id);
      if (deliveryTaskViewCallButton == null) {
        break missingId;
      }

      id = R.id.delivery_taskView_delivery_address_textView;
      TextView deliveryTaskViewDeliveryAddressTextView = ViewBindings.findChildViewById(rootView, id);
      if (deliveryTaskViewDeliveryAddressTextView == null) {
        break missingId;
      }

      id = R.id.delivery_taskView_done_button;
      Button deliveryTaskViewDoneButton = ViewBindings.findChildViewById(rootView, id);
      if (deliveryTaskViewDoneButton == null) {
        break missingId;
      }

      id = R.id.delivery_taskView_item_totak_textView;
      TextView deliveryTaskViewItemTotakTextView = ViewBindings.findChildViewById(rootView, id);
      if (deliveryTaskViewItemTotakTextView == null) {
        break missingId;
      }

      id = R.id.delivery_taskView_items_recyclerView;
      RecyclerView deliveryTaskViewItemsRecyclerView = ViewBindings.findChildViewById(rootView, id);
      if (deliveryTaskViewItemsRecyclerView == null) {
        break missingId;
      }

      id = R.id.delivery_taskView_map_framelayout;
      FrameLayout deliveryTaskViewMapFramelayout = ViewBindings.findChildViewById(rootView, id);
      if (deliveryTaskViewMapFramelayout == null) {
        break missingId;
      }

      id = R.id.delivery_taskView_orderId_textView;
      TextView deliveryTaskViewOrderIdTextView = ViewBindings.findChildViewById(rootView, id);
      if (deliveryTaskViewOrderIdTextView == null) {
        break missingId;
      }

      id = R.id.delivery_taskView_total_amount_textView;
      TextView deliveryTaskViewTotalAmountTextView = ViewBindings.findChildViewById(rootView, id);
      if (deliveryTaskViewTotalAmountTextView == null) {
        break missingId;
      }

      ConstraintLayout main = (ConstraintLayout) rootView;

      id = R.id.textView10;
      TextView textView10 = ViewBindings.findChildViewById(rootView, id);
      if (textView10 == null) {
        break missingId;
      }

      id = R.id.textView33;
      TextView textView33 = ViewBindings.findChildViewById(rootView, id);
      if (textView33 == null) {
        break missingId;
      }

      id = R.id.textView34;
      TextView textView34 = ViewBindings.findChildViewById(rootView, id);
      if (textView34 == null) {
        break missingId;
      }

      id = R.id.textView36;
      TextView textView36 = ViewBindings.findChildViewById(rootView, id);
      if (textView36 == null) {
        break missingId;
      }

      id = R.id.textView38;
      TextView textView38 = ViewBindings.findChildViewById(rootView, id);
      if (textView38 == null) {
        break missingId;
      }

      return new ActivityDeliveryTaskViewBinding((ConstraintLayout) rootView, cardView7, cardView8,
          deliveryTaskViewActivityToolbar, deliveryTaskViewCallButton,
          deliveryTaskViewDeliveryAddressTextView, deliveryTaskViewDoneButton,
          deliveryTaskViewItemTotakTextView, deliveryTaskViewItemsRecyclerView,
          deliveryTaskViewMapFramelayout, deliveryTaskViewOrderIdTextView,
          deliveryTaskViewTotalAmountTextView, main, textView10, textView33, textView34, textView36,
          textView38);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
