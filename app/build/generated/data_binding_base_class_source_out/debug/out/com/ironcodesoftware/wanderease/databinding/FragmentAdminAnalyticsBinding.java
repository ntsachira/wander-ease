// Generated by view binder compiler. Do not edit!
package com.ironcodesoftware.wanderease.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.github.mikephil.charting.charts.PieChart;
import com.ironcodesoftware.wanderease.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentAdminAnalyticsBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final PieChart adiminDashboardOrderStatusAnalytics;

  @NonNull
  public final TextView textView22;

  private FragmentAdminAnalyticsBinding(@NonNull FrameLayout rootView,
      @NonNull PieChart adiminDashboardOrderStatusAnalytics, @NonNull TextView textView22) {
    this.rootView = rootView;
    this.adiminDashboardOrderStatusAnalytics = adiminDashboardOrderStatusAnalytics;
    this.textView22 = textView22;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentAdminAnalyticsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentAdminAnalyticsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_admin_analytics, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentAdminAnalyticsBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.adimin_dashboard_order_status_analytics;
      PieChart adiminDashboardOrderStatusAnalytics = ViewBindings.findChildViewById(rootView, id);
      if (adiminDashboardOrderStatusAnalytics == null) {
        break missingId;
      }

      id = R.id.textView22;
      TextView textView22 = ViewBindings.findChildViewById(rootView, id);
      if (textView22 == null) {
        break missingId;
      }

      return new FragmentAdminAnalyticsBinding((FrameLayout) rootView,
          adiminDashboardOrderStatusAnalytics, textView22);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
