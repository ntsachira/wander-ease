package com.ironcodesoftware.wanderease.ui.partner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ironcodesoftware.wanderease.R;

public class PartnerDashboardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_partner_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setTotalRevenue();
        setTotalProducts();
        setTotalOrders();
        setDailySales();
        setSalesByCategory();
    }

    private void setSalesByCategory() {
    }

    private void setDailySales() {
    }

    private void setTotalOrders() {
    }

    private void setTotalProducts() {
    }

    private void setTotalRevenue() {
    }
}