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
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.Vehicle;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public abstract class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    JsonArray vehicleArray;

    FragmentActivity activity;

    public VehicleAdapter(JsonArray vehicleArray, FragmentActivity activity) {
        this.vehicleArray = vehicleArray;
        this.activity = activity;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VehicleViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vehicle_item_layout, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        JsonObject jsonObject = vehicleArray.get(position).getAsJsonObject();
        String id = jsonObject.get("id").getAsString();

        holder.textViewTitle.setText(jsonObject.get("title").getAsString());
        holder.textViewGearMode.setText(String.format("Gear Mode: %s",
                Vehicle.GEAR_MODES.get(jsonObject.get("gearMode").getAsInt())));
        holder.textViewAvailability.setText(String.format("Status: %s",
                Vehicle.STATUS_LIST.get(jsonObject.get("availability").getAsInt())));
        holder.textViewPrice.setText(String.format("Rs. %s",
                new DecimalFormat().format(jsonObject.get("pricePerDay").getAsDouble())));
        holder.textViewHighlights.setText(jsonObject.get("highlights").getAsString());

        onCardClick(jsonObject, holder.card);

        new Thread(()->{
            try {
                URL url = new URL(BuildConfig.HOST_URL + String.format("product/%s.png", id));
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
        return vehicleArray.size();
    }

    public abstract void onCardClick(JsonObject jsonObject,ConstraintLayout card);

    static class VehicleViewHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle;
        TextView textViewGearMode;
        TextView textViewAvailability;
        TextView textViewPrice;
        TextView textViewHighlights;
        ImageView imageView;

        ConstraintLayout card;
        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.vehicle_card_title_textView);
            textViewGearMode = itemView.findViewById(R.id.vehicle_card_gearMode_textView);
            textViewAvailability = itemView.findViewById(R.id.vehicle_card_availability_textView);
            textViewPrice = itemView.findViewById(R.id.vehicle_card_price_textView);
            textViewHighlights = itemView.findViewById(R.id.vehicle_card_highlights_textView);
            imageView = itemView.findViewById(R.id.vehicle_card_image_imageView);
            card = itemView.findViewById(R.id.vehicle_card_constraintLayout);
        }
    }
}
