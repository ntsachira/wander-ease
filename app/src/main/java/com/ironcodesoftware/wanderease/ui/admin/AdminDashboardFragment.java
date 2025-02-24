package com.ironcodesoftware.wanderease.ui.admin;

import android.animation.TypeConverter;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.HttpClient;
import com.ironcodesoftware.wanderease.model.Order;
import com.ironcodesoftware.wanderease.model.Product;
import com.ironcodesoftware.wanderease.model.SellerOrder;
import com.ironcodesoftware.wanderease.model.UserLogIn;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;


public class AdminDashboardFragment extends Fragment {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadData(view);

    }

    private void setOrderStatusChart(View view, HashMap<String, Integer> statusMap) {
        setPieChart(
                statusMap,
                view.findViewById(R.id.adimin_dashboard_order_status_chart),
                "Status"
        );
    }

    private void loadUsersCounts(View view){
        TextView textViewUsers = view.findViewById(R.id.adimin_dashboard_total_users_textView);
        TextView textViewDelivery = view.findViewById(R.id.adimin_dashboard_total_delivery_person_textView);
        textViewUsers.setText(R.string.loading);
        textViewDelivery.setText(R.string.loading);
        Request request = new Request.Builder().url(BuildConfig.HOST_URL + "LoadUserCounts").build();
        HttpClient.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    Gson gson = new Gson();
                    String responseText = response.body().string();

                    JsonObject json = gson.fromJson(responseText, JsonObject.class);


                    view.post(()->{
                        textViewUsers.setText(json.get("User").getAsString());
                        textViewDelivery.setText(json.get("Delivery").getAsString());
                    });
                }
            }
        });
    }

    private void loadData(View view) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        HashMap<String,Integer> statusMap = new HashMap<>();
        loadUsersCounts(view);
        TextView textViewOrders = view.findViewById(R.id.adimin_dashboard_total_orders_textView);
        TextView textViewOrdersTime = view.findViewById(R.id.adimin_dashboard_total_orders_time_textView);
        TextView textViewDeliveryPersonTime = view.findViewById(R.id.adimin_dashboard_total_delivery_person_time_textView);
        TextView textViewUsersTime = view.findViewById(R.id.adimin_dashboard_total_users_time_textView);
        textViewOrders.setText(R.string.loading);

        Gson gson = new Gson();
        firestore.collection(Order.F_COLLECTION)
                .addSnapshotListener((value, error) -> {
                    view.post(()->{
                        String updatedDate = "Updated: "+dateFormat.format(Calendar.getInstance().getTime());
                        textViewOrders.setText(String.valueOf(value.size()));
                        textViewOrdersTime.setText(updatedDate);
                        textViewDeliveryPersonTime.setText(updatedDate);
                        textViewUsersTime.setText(updatedDate);
                    });
                    if(error==null && !value.isEmpty()){
                        for (DocumentSnapshot document : value.getDocuments()) {
                            String status = document.getString(Order.F_STATE);
                            if(statusMap.containsKey(status)){
                                statusMap.put(status, 1+statusMap.get(status));
                            }else{
                                statusMap.put(status, 1);
                            }
                        }
                    }else{

                    }
                    view.post(()->{
                        setOrderStatusChart(view,statusMap);
                    });
                });
    }



    private void setPieChart(HashMap<String, Integer> map, PieChart pieChart, String centerLabel){
        if(!map.isEmpty()){
            ArrayList<PieEntry> pieEntries = new ArrayList<>();
            ArrayList<LegendEntry> legendEntries = new ArrayList<>();
            ArrayList<Integer> colorList = new ArrayList<>();
            map.forEach((category, value) -> {
                Random random = new Random();
                int color = Color.rgb(
                        random.nextInt(256),
                        random.nextInt(256),
                        random.nextInt(256)
                );
                pieEntries.add(new PieEntry(value));
                legendEntries.add(new LegendEntry(
                        category, Legend.LegendForm.CIRCLE,13,Float.NaN,null,color
                ));
                colorList.add(color);
            });

            pieChart.setDescription(null);
            pieChart.animateY(1000);
            pieChart.setCenterText(centerLabel);
            pieChart.setCenterTextSize(16);
            pieChart.setCenterTextColor(getContext().getColor(R.color.primary));

            Legend legend = pieChart.getLegend();
            legend.setWordWrapEnabled(true);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            legend.setTextSize(10);
            legend.setCustom(legendEntries);

            PieDataSet pieDataSet = new PieDataSet(pieEntries,"");
            pieDataSet.setColors(colorList);
            pieDataSet.setValueTextSize(15);
            pieDataSet.setValueTextColor(getContext().getColor(R.color.white));

            PieData pieData = new PieData(pieDataSet);
            pieChart.setData(pieData);
            pieChart.invalidate();
        }
    }
}