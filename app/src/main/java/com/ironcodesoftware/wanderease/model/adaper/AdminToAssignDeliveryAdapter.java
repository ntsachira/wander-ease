package com.ironcodesoftware.wanderease.model.adaper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.Delivery;
import com.ironcodesoftware.wanderease.model.Order;
import com.ironcodesoftware.wanderease.model.WanderDialog;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AdminToAssignDeliveryAdapter extends RecyclerView.Adapter<AdminToAssignDeliveryAdapter.AdminToAssignDeliveryItemViewHolder> {
    List<DocumentSnapshot> documentList;
    FragmentActivity activity;

    public AdminToAssignDeliveryAdapter(List<DocumentSnapshot> documentList, FragmentActivity activity) {
        this.documentList = documentList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AdminToAssignDeliveryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdminToAssignDeliveryItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.completed_order_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdminToAssignDeliveryItemViewHolder holder, int position) {
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
        holder.buttonAction.setVisibility(View.VISIBLE);
        holder.buttonAction.setText(R.string.assign_delivery);
        holder.buttonAction.setOnClickListener(v->{
            // add delivery assignment

            CardView cardView = activity.findViewById(R.id.slide_down_card);
            TextView textViewSelectedId = activity.findViewById(R.id.admin_selected_delivery_card_id_textView);
            textViewSelectedId.setText(String.format("ORDER ID: %s", orderId));
            Log.d(MainActivity.TAG, String.valueOf(cardView.getTranslationY()));
            if(cardView.getTranslationY() < -1000f){
                FlingAnimation animation = new FlingAnimation(cardView, DynamicAnimation.TRANSLATION_Y);
                animation.setStartVelocity(9000f).setFriction(1f);
                animation.start();
            }

            Button buttonAssign = activity.findViewById(R.id.admin_delivery_assign_button);
            buttonAssign.setOnClickListener(v1->{
                AutoCompleteTextView textViewCourier = activity.findViewById(R.id.admin_deliver_assign_person_select_autoCompleteTextView);
                String courierEmail = textViewCourier.getText().toString();
                if(!courierEmail.isEmpty() && !courierEmail.equals(activity.getString(R.string.select_courier))){
                    updateOrderAndAssignDelivery(orderId,courierEmail,document);
                }else{
                    v.post(()->{
                        AlertDialog alertDialog = WanderDialog.cancel(activity, "Please select courier to assign");
                        alertDialog.setCancelable(false);
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"ok",(dialog, which) -> {
                            dialog.cancel();
                        });
                        alertDialog.show();
                    });
                }

            });


        });

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        holder.recyclerView.setAdapter(new MyOrderItemAdapter(jsonArray,activity));

    }

    private void updateOrderAndAssignDelivery(String orderId, String courierEmail, DocumentSnapshot document) {
        AlertDialog loading = WanderDialog.loading(activity, "Processing...");
        activity.runOnUiThread(loading::show);
        HashMap<String, Object> updateMap = new HashMap<>();
        updateMap.put(Order.F_STATE, Order.State.Delivery_Assigned.name().replace("_", " "));
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(Order.F_COLLECTION).document(document.getId())
                .update(updateMap).addOnFailureListener(e->{
                    activity.runOnUiThread(()->{
                        loading.cancel();
                        WanderDialog.cancel(activity, "Order update failed").show();

                    });
                    Log.e(MainActivity.TAG,"Order update failed",e);
                }).addOnSuccessListener(task->{

                    HashMap<String,Object> deliveryMap = new HashMap<>();
                    deliveryMap.put(Delivery.F_STATUS, Delivery.State.Active.name());
                    deliveryMap.put(Delivery.F_ASSIGNED_ON, Calendar.getInstance().getTime());
                    deliveryMap.put(Delivery.F_ORDER_ID, orderId);
                    firestore.collection(Delivery.F_COLLECTION).add(deliveryMap)
                            .addOnSuccessListener(documentReference->{
                                activity.runOnUiThread(()->{
                                    loading.cancel();
                                    Toast.makeText(
                                            activity,
                                                    "Delivery assignment success",
                                                    Toast.LENGTH_LONG)
                                            .show();
                                    new FlingAnimation(activity.findViewById(R.id.slide_down_card), DynamicAnimation.TRANSLATION_Y)
                                            .setStartVelocity(-9000f).setFriction(1f).start();
                                });
                            })
                            .addOnFailureListener(e->{
                                activity.runOnUiThread(()->{
                                        loading.cancel();
                                        WanderDialog.cancel(activity, "Delivery assignment failed").show();

                            });
                                Log.e(MainActivity.TAG,"Delivery assign failed",e);
                            });
                });
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    static class AdminToAssignDeliveryItemViewHolder extends RecyclerView.ViewHolder{

        RecyclerView recyclerView;
        TextView textViewOrderId;
        TextView textViewStatus;
        TextView textViewItemCount;
        TextView textViewTotal;
        Button buttonAction;

        public AdminToAssignDeliveryItemViewHolder(@NonNull View itemView) {
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
