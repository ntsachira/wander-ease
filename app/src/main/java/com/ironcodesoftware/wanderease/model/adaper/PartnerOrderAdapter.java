package com.ironcodesoftware.wanderease.model.adaper;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.SellerOrder;
import com.ironcodesoftware.wanderease.model.WanderDialog;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public abstract class PartnerOrderAdapter extends RecyclerView.Adapter<PartnerOrderAdapter.PartnerOrderItemViewHolder> {

    List<DocumentSnapshot> documentList;
    FragmentActivity activity;

    public PartnerOrderAdapter(List<DocumentSnapshot> documentList,FragmentActivity activity) {
        this.documentList = documentList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public PartnerOrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PartnerOrderItemViewHolder(LayoutInflater.from(parent.getContext()
                ).inflate(R.layout.completed_order_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PartnerOrderItemViewHolder holder, int position) {
        DocumentSnapshot document = documentList.get(position);

        JsonArray jsonArray = new Gson().fromJson(document.getString("items"), JsonArray.class);

        int itemCount = 0;
        double total = 0;
        for (JsonElement element : jsonArray) {
            itemCount += element.getAsJsonObject().get("qty").getAsInt();

            total += element.getAsJsonObject().get("item").getAsJsonObject().get("price").getAsDouble();
        }
        String orderId = document.getString(SellerOrder.F_ID);

        holder.textViewOrderId.setText(String.format("ORDER ID: %s", orderId));
        holder.textViewStatus.setText(document.getString(SellerOrder.F_STATE));
        holder.textViewItemCount.setText(String.format("Total(%s items): ", itemCount));
        holder.textViewTotal.setText(
                String.format("Rs. %s", new DecimalFormat().format(total))
        );

        addAction(document,holder.buttonAction);

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        holder.recyclerView.setAdapter(new MyOrderItemAdapter(jsonArray,activity));
    }

    public abstract void addAction(@NonNull DocumentSnapshot document, Button buttonAction);

    public void updateOrderStatus(String id) {
        AlertDialog loading = WanderDialog.loading(activity, "Updating...");
        loading.show();
        HashMap<String,Object> updateMap = new HashMap<>();
        updateMap.put(SellerOrder.F_STATE, SellerOrder.State.Completed.name());
        FirebaseFirestore.getInstance().collection(SellerOrder.F_COLLECTION)
                .document(id).update(updateMap)
                .addOnSuccessListener(unused -> {
                    activity.runOnUiThread(()->{
                        loading.cancel();
                        Toast.makeText(activity, "Order updated successfully", Toast.LENGTH_LONG)
                                .show();
                    });
                })
                .addOnFailureListener(e->{
                    activity.runOnUiThread(()->{
                        loading.cancel();
                        WanderDialog.cancel(activity, "Failed to update order").show();
                    });
                });
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    static class PartnerOrderItemViewHolder extends RecyclerView.ViewHolder{

        RecyclerView recyclerView;
        TextView textViewOrderId;
        TextView textViewStatus;
        TextView textViewItemCount;
        TextView textViewTotal;
        Button buttonAction;
        public PartnerOrderItemViewHolder(@NonNull View itemView) {
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
