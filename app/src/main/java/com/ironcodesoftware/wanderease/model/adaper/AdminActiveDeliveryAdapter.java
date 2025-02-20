package com.ironcodesoftware.wanderease.model.adaper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.ironcodesoftware.wanderease.R;

import java.text.DecimalFormat;
import java.util.List;

public abstract class AdminActiveDeliveryAdapter extends RecyclerView.Adapter<AdminToAssignDeliveryAdapter.AdminToAssignDeliveryItemViewHolder> {
    List<DocumentSnapshot> documentList;
    FragmentActivity activity;

    public AdminActiveDeliveryAdapter(List<DocumentSnapshot> documentList, FragmentActivity activity) {
        this.documentList = documentList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AdminToAssignDeliveryAdapter.AdminToAssignDeliveryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdminToAssignDeliveryAdapter.AdminToAssignDeliveryItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.completed_order_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdminToAssignDeliveryAdapter.AdminToAssignDeliveryItemViewHolder holder, int position) {
        DocumentSnapshot document = documentList.get(position);

        JsonArray jsonArray = new Gson().fromJson(document.getString("items"), JsonArray.class);

        int itemCount = 0;
        for (JsonElement element : jsonArray) {
            itemCount += element.getAsJsonObject().get("qty").getAsInt();
        }
        String orderId = document.getString("orderId");

        holder.textViewOrderId.setText(String.format("ORDER ID: %s", orderId));
        holder.textViewStatus.setText(document.getString("order_status"));
        holder.textViewItemCount.setText(String.format("Total(%s items): ", itemCount));
        holder.textViewTotal.setText(
                String.format("Rs. %s", new DecimalFormat().format(document.getDouble("total_price")))
        );

        addAction(document,holder.buttonAction);

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        holder.recyclerView.setAdapter(new MyOrderItemAdapter(jsonArray,activity));    }

    public abstract void addAction(@NonNull DocumentSnapshot document, Button buttonAction);

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    static  class AdminActiveDeliveryItemViewHolder extends RecyclerView.ViewHolder{

        RecyclerView recyclerView;
        TextView textViewOrderId;
        TextView textViewStatus;
        TextView textViewItemCount;
        TextView textViewTotal;
        Button buttonAction;
        public AdminActiveDeliveryItemViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.completed_order_item_recyclerView);
            textViewOrderId = itemView.findViewById(R.id.completed_order_orderID_textView);
            textViewStatus = itemView.findViewById(R.id.completed_order_status_textView);
            textViewItemCount = itemView.findViewById(R.id.completed_order_Item_count_textView);
            textViewTotal = itemView.findViewById(R.id.completed_order_total_amount_textView);
            buttonAction = itemView.findViewById(R.id.my_order_item_action_button);
        }
    }
}
