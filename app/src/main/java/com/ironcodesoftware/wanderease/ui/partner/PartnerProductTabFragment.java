package com.ironcodesoftware.wanderease.ui.partner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.ui.home.search.ProductFragment;

public class PartnerProductTabFragment extends Fragment {

    PartnerProductListFragment productListFragment = new PartnerProductListFragment();
    PartnerProductFragment newProductFragment = new PartnerProductFragment();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_partner_product_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadFragment(new PartnerProductListFragment());
        TabLayout tabLayout = view.findViewById(R.id.partner_product_tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()== 1){
                    loadFragment(newProductFragment);
                }else{
                    loadFragment(productListFragment);
                    getActivity().getIntent().removeExtra("product");
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
            getActivity().getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                    .replace(R.id.partner_product_tab_fragmentContainerView, fragment)
                    .commit();
    }

    @Override
    public void onResume() {
        super.onResume();


    }
}