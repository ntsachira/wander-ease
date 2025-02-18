package com.ironcodesoftware.wanderease.model.adaper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.Product;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class MyOrderItemAdapter extends RecyclerView.Adapter<MyOrderItemAdapter.MyOrderItemViewHolder> {
    JsonArray itemJsonArray;
    FragmentActivity activity;

    public MyOrderItemAdapter(JsonArray itemJsonArray, FragmentActivity activity) {
        this.itemJsonArray = itemJsonArray;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyOrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyOrderItemAdapter.MyOrderItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_product_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderItemViewHolder holder, int position) {
        JsonObject jsonObject = itemJsonArray.get(position).getAsJsonObject();
        int qty = jsonObject.get("qty").getAsInt();
        JsonObject item = jsonObject.get("item").getAsJsonObject();
        String id = item.get("id").getAsString();

        holder.textViewTitle.setText(item.get(Product.F_TITLE).getAsString());
        holder.textViewQuantity.setText(String.format("Quantity: %s",qty));
        holder.textViewPrice.setText(String.format("Rs. %s",new DecimalFormat()
                .format(Double.parseDouble(item.get(Product.F_PRICE).getAsString())))
        );
        holder.textViewColor.setText(String.format("Color: %s",item.get(Product.F_COLOR).getAsString()));


        new Thread(()->{
            try {
                URL url = new URL(BuildConfig.HOST_URL + String.format("product/%s.webp", id));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input =  connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                activity.runOnUiThread(()->{
                    holder.imageView.setImageBitmap(bitmap);
                });
                connection.disconnect();
            } catch (Exception e) {
                Log.e(MainActivity.TAG,e.getLocalizedMessage(),e);
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return itemJsonArray.size();
    }

    static class MyOrderItemViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTitle;
        TextView textViewColor;
        TextView textViewPrice;
        TextView textViewQuantity;
        ImageView imageView;
        public MyOrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.order_item_titletextView);
            textViewColor = itemView.findViewById(R.id.order_item_color_textView);
            textViewPrice = itemView.findViewById(R.id.order_item_price_textView);
            textViewQuantity = itemView.findViewById(R.id.order_item_quantity_textView);
            imageView = itemView.findViewById(R.id.order_item_imageView);
        }
    }
}
