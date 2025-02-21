package com.ironcodesoftware.wanderease.ui.partner;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.HttpClient;
import com.ironcodesoftware.wanderease.model.Product;
import com.ironcodesoftware.wanderease.model.SellerOrder;
import com.ironcodesoftware.wanderease.model.UserLogIn;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class PartnerDashboardFragment extends Fragment {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    UserLogIn login;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_partner_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            login = UserLogIn.getLogin(getContext());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        setTotalRevenue(view);
        setTotalProducts(view);
    }

    private void setPieChart(HashMap<String, Integer> map,PieChart pieChart,String centerLabel){
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

    private void setSalesByCategory(View view, HashMap<String, Integer> map) {

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

            PieChart pieChart = view.findViewById(R.id.partner_dashboard_categoryChart);
            pieChart.setDescription(null);
            pieChart.animateY(1000);
            pieChart.setCenterText("Sales");
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

    private void setOrderStatusChart(View view, HashMap<String, Integer> statusMap) {
        setPieChart(
                statusMap,
                view.findViewById(R.id.partner_dashboard_order_status_chart),
                "Status"
        );
    }

    private void setTotalOrders(View view, int size) {
        TextView textViewOrders = view.findViewById(R.id.partner_dashboard_total_orders_textView);
        textViewOrders.setText(String.valueOf(size));
    }

    private void setTotalProducts(View view) {
        TextView textViewTotalProducts = view.findViewById(R.id.partner_dashboard_total_products_textView);
        textViewTotalProducts.setText(R.string.loading);

        HttpClient.getInstance()
                .newCall(new Request.Builder()
                        .url(BuildConfig.HOST_URL + String.format("LoadProducts?email=%s&query=",login.getEmail()))
                        .build())
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        view.post(()->{
                            Snackbar snackbar = Snackbar.make(
                                    view,
                                    "Something went Wrong, Please try again later",
                                    Snackbar.LENGTH_INDEFINITE

                            );
                            snackbar.setAction("Ok", v -> {
                                snackbar.dismiss();
                            });
                            snackbar.show();
                        });
                        Log.e(MainActivity.TAG, "Product load failed",e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if(response.isSuccessful()){
                            Gson gson = new Gson();
                            JsonObject responseJsonObject = gson.fromJson(response.body().string(), JsonObject.class);
                            JsonArray productList = responseJsonObject.get("productList").getAsJsonArray();
                            view.post(()->{
                                if(!productList.isEmpty()){
                                    textViewTotalProducts.setText(String.valueOf(productList.size()));
                                }else{
                                    textViewTotalProducts.setText("0");
                                }
                            });


                        }
                    }
                });
    }

    private void setTotalRevenue(View view) {
        HashMap<String,Integer> categoryMap = new HashMap<>();
        HashMap<String,Integer> statusMap = new HashMap<>();

        TextView textViewOrders = view.findViewById(R.id.partner_dashboard_total_orders_textView);
        textViewOrders.setText(R.string.loading);
        TextView textViewRevenue = view.findViewById(R.id.partner_dashboard_revenue_textView);
        Gson gson = new Gson();
        firestore.collection(SellerOrder.F_COLLECTION)
                .whereEqualTo(SellerOrder.F_SELLER, login.getEmail())
                .addSnapshotListener((value, error) -> {
                    double total = 0;
                    view.post(()->{
                        setTotalOrders(view,value.size());
                    });
                    if(error==null && !value.isEmpty()){
                        for (DocumentSnapshot document : value.getDocuments()) {
                            JsonArray items = gson.fromJson(document.getString(SellerOrder.F_ITEMS), JsonArray.class);
                            for (JsonElement element : items) {
                                int qty = element.getAsJsonObject().get("qty").getAsInt();
                                JsonObject item = element.getAsJsonObject().get("item").getAsJsonObject();
                                total += qty*item.get(Product.F_PRICE).getAsDouble();
                                String category = item.get(Product.F_CATEGORY).getAsJsonObject().get("name").getAsString();
                                if(categoryMap.containsKey(category)){
                                    categoryMap.put(category, categoryMap.get(category)+qty);
                                }else{
                                    categoryMap.put(category,qty);
                                }
                            }
                            String status = document.getString(SellerOrder.F_STATE);
                            if(statusMap.containsKey(status)){
                                statusMap.put(status, 1+statusMap.get(status));
                            }else{
                                statusMap.put(status, 1);
                            }
                        }
                    }else{

                    }
                    double finalTotal = total;
                    view.post(()->{
                        setSalesByCategory(view, categoryMap);
                        setOrderStatusChart(view,statusMap);
                        textViewRevenue.setText(String.format(
                                "Rs. %s",new DecimalFormat().format(finalTotal)
                        ));
                    });

                });
    }
}