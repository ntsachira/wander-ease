package com.ironcodesoftware.wanderease.ui.partner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ironcodesoftware.wanderease.R;


public class PartnerServiceFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_partner_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadFragment(new PartnerVehicleRentalFragment());
        BottomNavigationView navigationView = view.findViewById(R.id.partner_services_bottomNavigationView);
        navigationView.setOnItemSelectedListener(item -> {
            setSelectedFragment(item);
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.partner_services_main_fragmentContaine, fragment).setReorderingAllowed(true)
                .commit();
    }

    private void setSelectedFragment(MenuItem item){
        if(item.getItemId() == R.id.partner_services_tourGuide_menuitem){
            loadFragment(new PartnerTourGuideFragment());
        }else {
            loadFragment(new PartnerVehicleRentalFragment());
        }
    }


}