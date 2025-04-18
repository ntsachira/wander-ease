// Generated by view binder compiler. Do not edit!
package com.ironcodesoftware.wanderease.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

public final class ProductQuantityUpdateDialogLayoutBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final EditText partnerProductQuantityUpdateEditText;

  @NonNull
  public final TextView productQuantityTextView;

  @NonNull
  public final TextView textView24;

  private ProductQuantityUpdateDialogLayoutBinding(@NonNull ConstraintLayout rootView,
      @NonNull EditText partnerProductQuantityUpdateEditText,
      @NonNull TextView productQuantityTextView, @NonNull TextView textView24) {
    this.rootView = rootView;
    this.partnerProductQuantityUpdateEditText = partnerProductQuantityUpdateEditText;
    this.productQuantityTextView = productQuantityTextView;
    this.textView24 = textView24;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ProductQuantityUpdateDialogLayoutBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ProductQuantityUpdateDialogLayoutBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.product_quantity_update_dialog_layout, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ProductQuantityUpdateDialogLayoutBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.partner_product_quantity_update_editText;
      EditText partnerProductQuantityUpdateEditText = ViewBindings.findChildViewById(rootView, id);
      if (partnerProductQuantityUpdateEditText == null) {
        break missingId;
      }

      id = R.id.product_quantity_textView;
      TextView productQuantityTextView = ViewBindings.findChildViewById(rootView, id);
      if (productQuantityTextView == null) {
        break missingId;
      }

      id = R.id.textView24;
      TextView textView24 = ViewBindings.findChildViewById(rootView, id);
      if (textView24 == null) {
        break missingId;
      }

      return new ProductQuantityUpdateDialogLayoutBinding((ConstraintLayout) rootView,
          partnerProductQuantityUpdateEditText, productQuantityTextView, textView24);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
