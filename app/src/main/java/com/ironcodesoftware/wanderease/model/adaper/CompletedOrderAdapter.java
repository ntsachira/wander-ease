package com.ironcodesoftware.wanderease.model.adaper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.ui.home.CheckoutActivity;

public class CompletedOrderAdapter extends RecyclerView.Adapter<CompletedOrderAdapter.CompletedOrderViewHolder> {

    JsonArray itemJsonArray;
    FragmentActivity activity;

    public CompletedOrderAdapter(JsonArray itemJsonArray, FragmentActivity activity) {
        this.itemJsonArray = itemJsonArray;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CompletedOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CompletedOrderViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.completed_order_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedOrderViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return itemJsonArray.size();
    }

    static class CompletedOrderViewHolder extends RecyclerView.ViewHolder{

        public CompletedOrderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
