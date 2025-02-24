package com.ironcodesoftware.wanderease.ui.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.ui.home.search.ProductFragment;


public class AdminDeliveryManagementFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_delivery_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadFragment(new AdminToAssignFragment());



        TabLayout tabLayout  = view.findViewById(R.id.admin_delivery_tablayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                toggleTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void toggleTab(int position){
        if(position== 1){
            loadFragment(new AdminDeliveryActiveFragment());
        } else if (position== 2) {
            loadFragment(new AdminDeliveryCompletedFragment());
        }else{
            loadFragment(new AdminToAssignFragment());
        }
    }



    private void loadFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                .replace(R.id.admin_delivery_fragmentContainerView,fragment).commit();
    }
}