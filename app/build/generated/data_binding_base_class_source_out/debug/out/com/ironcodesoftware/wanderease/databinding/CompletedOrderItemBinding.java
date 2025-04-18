// Generated by view binder compiler. Do not edit!
package com.ironcodesoftware.wanderease.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.ironcodesoftware.wanderease.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class CompletedOrderItemBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final TextView completedOrderItemCountTextView;

  @NonNull
  public final RecyclerView completedOrderItemRecyclerView;

  @NonNull
  public final TextView completedOrderOrderIDTextView;

  @NonNull
  public final TextView completedOrderStatusTextView;

  @NonNull
  public final TextView completedOrderTotalAmountTextView;

  @NonNull
  public final Button myOrderItemActionButton;

  private CompletedOrderItemBinding(@NonNull ConstraintLayout rootView,
      @NonNull TextView completedOrderItemCountTextView,
      @NonNull RecyclerView completedOrderItemRecyclerView,
      @NonNull TextView completedOrderOrderIDTextView,
      @NonNull TextView completedOrderStatusTextView,
      @NonNull TextView completedOrderTotalAmountTextView,
      @NonNull Button myOrderItemActionButton) {
    this.rootView = rootView;
    this.completedOrderItemCountTextView = completedOrderItemCountTextView;
    this.completedOrderItemRecyclerView = completedOrderItemRecyclerView;
    this.completedOrderOrderIDTextView = completedOrderOrderIDTextView;
    this.completedOrderStatusTextView = completedOrderStatusTextView;
    this.completedOrderTotalAmountTextView = completedOrderTotalAmountTextView;
    this.myOrderItemActionButton = myOrderItemActionButton;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static CompletedOrderItemBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static CompletedOrderItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.completed_order_item, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static CompletedOrderItemBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.completed_order_Item_count_textView;
      TextView completedOrderItemCountTextView = ViewBindings.findChildViewById(rootView, id);
      if (completedOrderItemCountTextView == null) {
        break missingId;
      }

      id = R.id.completed_order_item_recyclerView;
      RecyclerView completedOrderItemRecyclerView = ViewBindings.findChildViewById(rootView, id);
      if (completedOrderItemRecyclerView == null) {
        break missingId;
      }

      id = R.id.completed_order_orderID_textView;
      TextView completedOrderOrderIDTextView = ViewBindings.findChildViewById(rootView, id);
      if (completedOrderOrderIDTextView == null) {
        break missingId;
      }

      id = R.id.completed_order_status_textView;
      TextView completedOrderStatusTextView = ViewBindings.findChildViewById(rootView, id);
      if (completedOrderStatusTextView == null) {
        break missingId;
      }

      id = R.id.completed_order_total_amount_textView;
      TextView completedOrderTotalAmountTextView = ViewBindings.findChildViewById(rootView, id);
      if (completedOrderTotalAmountTextView == null) {
        break missingId;
      }

      id = R.id.my_order_item_action_button;
      Button myOrderItemActionButton = ViewBindings.findChildViewById(rootView, id);
      if (myOrderItemActionButton == null) {
        break missingId;
      }

      return new CompletedOrderItemBinding((ConstraintLayout) rootView,
          completedOrderItemCountTextView, completedOrderItemRecyclerView,
          completedOrderOrderIDTextView, completedOrderStatusTextView,
          completedOrderTotalAmountTextView, myOrderItemActionButton);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
