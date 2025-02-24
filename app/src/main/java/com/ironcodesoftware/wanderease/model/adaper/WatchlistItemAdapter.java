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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.Product;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public abstract class WatchlistItemAdapter extends RecyclerView.Adapter<WatchlistItemAdapter.WatchlistItemViewHolder> {

    JsonArray itemsList;
    FragmentActivity activity;

    public WatchlistItemAdapter(JsonArray itemsList, FragmentActivity activity) {
        this.itemsList = itemsList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public WatchlistItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WatchlistItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.watchlist_item_layout, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull WatchlistItemViewHolder holder, int position) {
        JsonObject item = itemsList.get(position).getAsJsonObject();
        String id = item.get("id").getAsString();

        holder.textViewTitle.setText(item.get(Product.F_TITLE).getAsString());
        holder.textViewQuantity.setText(String.format("Quantity: %s",item.get(Product.F_QTY).getAsInt()));
        holder.textViewPrice.setText(String.format("Rs. %s",new DecimalFormat()
                .format(Double.parseDouble(item.get(Product.F_PRICE).getAsString())))
        );
        holder.textViewColor.setText(String.format("Color: %s",item.get(Product.F_COLOR).getAsString()));

        holder.imageView.setOnClickListener(v->{
            onCardClick(item, holder.card);
        });

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
        return itemsList.size();
    }

    public abstract void onCardClick(JsonObject item,ConstraintLayout card);
    static class WatchlistItemViewHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle;
        TextView textViewColor;
        TextView textViewQuantity;
        TextView textViewPrice;

        ImageView imageView;

        ConstraintLayout card;
        public WatchlistItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.watchlist_item_title_textView);
            textViewColor = itemView.findViewById(R.id.watchlist_item_color_textView);
            textViewQuantity = itemView.findViewById(R.id.watchlist_item_items_textView);
            textViewPrice = itemView.findViewById(R.id.watchlist_item_price_textView);
            imageView = itemView.findViewById(R.id.watchlist_item_imageView);
            card = itemView.findViewById(R.id.watchlist_item_constraintLayout);
        }
    }
}
