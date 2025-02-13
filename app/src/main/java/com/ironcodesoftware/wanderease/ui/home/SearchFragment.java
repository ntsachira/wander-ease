package com.ironcodesoftware.wanderease.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.ui.home.search.GuideFragment;
import com.ironcodesoftware.wanderease.ui.home.search.ProductFragment;
import com.ironcodesoftware.wanderease.ui.home.search.RentalFragment;

import java.util.List;


public class SearchFragment extends Fragment {

    ProductFragment productFragment = new ProductFragment();
    RentalFragment rentalFragment = new RentalFragment();
    GuideFragment guideFragment = new GuideFragment();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadFragment(productFragment);
        TabLayout tabLayout = view.findViewById(R.id.home_search_tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()== 1){
                    loadFragment(rentalFragment);
                } else if (tab.getPosition()== 2) {
                    loadFragment(guideFragment);
                }else{
                    loadFragment(productFragment);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void loadFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_search_fragmentContainer, fragment).setReorderingAllowed(true)
                .commit();
    }
}