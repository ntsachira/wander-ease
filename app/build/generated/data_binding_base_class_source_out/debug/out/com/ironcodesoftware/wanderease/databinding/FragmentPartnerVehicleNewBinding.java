// Generated by view binder compiler. Do not edit!
package com.ironcodesoftware.wanderease.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ironcodesoftware.wanderease.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentPartnerVehicleNewBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ConstraintLayout constraintLayout4;

  @NonNull
  public final AutoCompleteTextView partnerVehicleNewAvailabilityInput;

  @NonNull
  public final ImageButton partnerVehicleNewDeleteImageButton;

  @NonNull
  public final AutoCompleteTextView partnerVehicleNewGearModeSpinner;

  @NonNull
  public final TextInputEditText partnerVehicleNewHighllightsInput;

  @NonNull
  public final ImageView partnerVehicleNewImageView;

  @NonNull
  public final FrameLayout partnerVehicleNewMapFrameLayout;

  @NonNull
  public final TextInputEditText partnerVehicleNewProcePerDayInput;

  @NonNull
  public final Button partnerVehicleNewSaveVehicleButton;

  @NonNull
  public final ScrollView partnerVehicleNewScrollview;

  @NonNull
  public final TextInputEditText partnerVehicleNewTitleEditText;

  @NonNull
  public final Button partnerVehicleNewUploadImageButton;

  @NonNull
  public final AutoCompleteTextView partnerVehicleNewVehicleTypeSpinner;

  @NonNull
  public final TextInputLayout textInputLayout2;

  @NonNull
  public final TextInputLayout textInputLayout3;

  @NonNull
  public final TextInputLayout textInputLayout4;

  @NonNull
  public final TextInputLayout textInputLayout5;

  @NonNull
  public final TextInputLayout textInputLayout6;

  @NonNull
  public final TextInputLayout textInputLayout7;

  @NonNull
  public final TextView textView13;

  private FragmentPartnerVehicleNewBinding(@NonNull ConstraintLayout rootView,
      @NonNull ConstraintLayout constraintLayout4,
      @NonNull AutoCompleteTextView partnerVehicleNewAvailabilityInput,
      @NonNull ImageButton partnerVehicleNewDeleteImageButton,
      @NonNull AutoCompleteTextView partnerVehicleNewGearModeSpinner,
      @NonNull TextInputEditText partnerVehicleNewHighllightsInput,
      @NonNull ImageView partnerVehicleNewImageView,
      @NonNull FrameLayout partnerVehicleNewMapFrameLayout,
      @NonNull TextInputEditText partnerVehicleNewProcePerDayInput,
      @NonNull Button partnerVehicleNewSaveVehicleButton,
      @NonNull ScrollView partnerVehicleNewScrollview,
      @NonNull TextInputEditText partnerVehicleNewTitleEditText,
      @NonNull Button partnerVehicleNewUploadImageButton,
      @NonNull AutoCompleteTextView partnerVehicleNewVehicleTypeSpinner,
      @NonNull TextInputLayout textInputLayout2, @NonNull TextInputLayout textInputLayout3,
      @NonNull TextInputLayout textInputLayout4, @NonNull TextInputLayout textInputLayout5,
      @NonNull TextInputLayout textInputLayout6, @NonNull TextInputLayout textInputLayout7,
      @NonNull TextView textView13) {
    this.rootView = rootView;
    this.constraintLayout4 = constraintLayout4;
    this.partnerVehicleNewAvailabilityInput = partnerVehicleNewAvailabilityInput;
    this.partnerVehicleNewDeleteImageButton = partnerVehicleNewDeleteImageButton;
    this.partnerVehicleNewGearModeSpinner = partnerVehicleNewGearModeSpinner;
    this.partnerVehicleNewHighllightsInput = partnerVehicleNewHighllightsInput;
    this.partnerVehicleNewImageView = partnerVehicleNewImageView;
    this.partnerVehicleNewMapFrameLayout = partnerVehicleNewMapFrameLayout;
    this.partnerVehicleNewProcePerDayInput = partnerVehicleNewProcePerDayInput;
    this.partnerVehicleNewSaveVehicleButton = partnerVehicleNewSaveVehicleButton;
    this.partnerVehicleNewScrollview = partnerVehicleNewScrollview;
    this.partnerVehicleNewTitleEditText = partnerVehicleNewTitleEditText;
    this.partnerVehicleNewUploadImageButton = partnerVehicleNewUploadImageButton;
    this.partnerVehicleNewVehicleTypeSpinner = partnerVehicleNewVehicleTypeSpinner;
    this.textInputLayout2 = textInputLayout2;
    this.textInputLayout3 = textInputLayout3;
    this.textInputLayout4 = textInputLayout4;
    this.textInputLayout5 = textInputLayout5;
    this.textInputLayout6 = textInputLayout6;
    this.textInputLayout7 = textInputLayout7;
    this.textView13 = textView13;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentPartnerVehicleNewBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentPartnerVehicleNewBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_partner_vehicle_new, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentPartnerVehicleNewBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.constraintLayout4;
      ConstraintLayout constraintLayout4 = ViewBindings.findChildViewById(rootView, id);
      if (constraintLayout4 == null) {
        break missingId;
      }

      id = R.id.partner_vehicle_new_availability_input;
      AutoCompleteTextView partnerVehicleNewAvailabilityInput = ViewBindings.findChildViewById(rootView, id);
      if (partnerVehicleNewAvailabilityInput == null) {
        break missingId;
      }

      id = R.id.partner_vehicle_new_delete_imageButton;
      ImageButton partnerVehicleNewDeleteImageButton = ViewBindings.findChildViewById(rootView, id);
      if (partnerVehicleNewDeleteImageButton == null) {
        break missingId;
      }

      id = R.id.partner_vehicle_new_gear_mode_spinner;
      AutoCompleteTextView partnerVehicleNewGearModeSpinner = ViewBindings.findChildViewById(rootView, id);
      if (partnerVehicleNewGearModeSpinner == null) {
        break missingId;
      }

      id = R.id.partner_vehicle_new_highllights_input;
      TextInputEditText partnerVehicleNewHighllightsInput = ViewBindings.findChildViewById(rootView, id);
      if (partnerVehicleNewHighllightsInput == null) {
        break missingId;
      }

      id = R.id.partner_vehicle_new_imageView;
      ImageView partnerVehicleNewImageView = ViewBindings.findChildViewById(rootView, id);
      if (partnerVehicleNewImageView == null) {
        break missingId;
      }

      id = R.id.partner_vehicle_new_map_frameLayout;
      FrameLayout partnerVehicleNewMapFrameLayout = ViewBindings.findChildViewById(rootView, id);
      if (partnerVehicleNewMapFrameLayout == null) {
        break missingId;
      }

      id = R.id.partner_vehicle_new_proce_per_day_input;
      TextInputEditText partnerVehicleNewProcePerDayInput = ViewBindings.findChildViewById(rootView, id);
      if (partnerVehicleNewProcePerDayInput == null) {
        break missingId;
      }

      id = R.id.partner_vehicle_new_save_vehicle_button;
      Button partnerVehicleNewSaveVehicleButton = ViewBindings.findChildViewById(rootView, id);
      if (partnerVehicleNewSaveVehicleButton == null) {
        break missingId;
      }

      id = R.id.partner_vehicle_new_scrollview;
      ScrollView partnerVehicleNewScrollview = ViewBindings.findChildViewById(rootView, id);
      if (partnerVehicleNewScrollview == null) {
        break missingId;
      }

      id = R.id.partner_vehicle_new_title_EditText;
      TextInputEditText partnerVehicleNewTitleEditText = ViewBindings.findChildViewById(rootView, id);
      if (partnerVehicleNewTitleEditText == null) {
        break missingId;
      }

      id = R.id.partner_vehicle_new_upload_image_button;
      Button partnerVehicleNewUploadImageButton = ViewBindings.findChildViewById(rootView, id);
      if (partnerVehicleNewUploadImageButton == null) {
        break missingId;
      }

      id = R.id.partner_vehicle_new_vehicle_Type_spinner;
      AutoCompleteTextView partnerVehicleNewVehicleTypeSpinner = ViewBindings.findChildViewById(rootView, id);
      if (partnerVehicleNewVehicleTypeSpinner == null) {
        break missingId;
      }

      id = R.id.textInputLayout2;
      TextInputLayout textInputLayout2 = ViewBindings.findChildViewById(rootView, id);
      if (textInputLayout2 == null) {
        break missingId;
      }

      id = R.id.textInputLayout3;
      TextInputLayout textInputLayout3 = ViewBindings.findChildViewById(rootView, id);
      if (textInputLayout3 == null) {
        break missingId;
      }

      id = R.id.textInputLayout4;
      TextInputLayout textInputLayout4 = ViewBindings.findChildViewById(rootView, id);
      if (textInputLayout4 == null) {
        break missingId;
      }

      id = R.id.textInputLayout5;
      TextInputLayout textInputLayout5 = ViewBindings.findChildViewById(rootView, id);
      if (textInputLayout5 == null) {
        break missingId;
      }

      id = R.id.textInputLayout6;
      TextInputLayout textInputLayout6 = ViewBindings.findChildViewById(rootView, id);
      if (textInputLayout6 == null) {
        break missingId;
      }

      id = R.id.textInputLayout7;
      TextInputLayout textInputLayout7 = ViewBindings.findChildViewById(rootView, id);
      if (textInputLayout7 == null) {
        break missingId;
      }

      id = R.id.textView13;
      TextView textView13 = ViewBindings.findChildViewById(rootView, id);
      if (textView13 == null) {
        break missingId;
      }

      return new FragmentPartnerVehicleNewBinding((ConstraintLayout) rootView, constraintLayout4,
          partnerVehicleNewAvailabilityInput, partnerVehicleNewDeleteImageButton,
          partnerVehicleNewGearModeSpinner, partnerVehicleNewHighllightsInput,
          partnerVehicleNewImageView, partnerVehicleNewMapFrameLayout,
          partnerVehicleNewProcePerDayInput, partnerVehicleNewSaveVehicleButton,
          partnerVehicleNewScrollview, partnerVehicleNewTitleEditText,
          partnerVehicleNewUploadImageButton, partnerVehicleNewVehicleTypeSpinner, textInputLayout2,
          textInputLayout3, textInputLayout4, textInputLayout5, textInputLayout6, textInputLayout7,
          textView13);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
