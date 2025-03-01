package com.ironcodesoftware.wanderease.model.adaper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.Vehicle;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder>{

    List<DocumentSnapshot> documents;
    FragmentActivity activity;

    public BookingAdapter(List<DocumentSnapshot> documents, FragmentActivity activity) {
        this.documents = documents;
        this.activity = activity;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookingViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_item_layout, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        DocumentSnapshot document = documents.get(position);
        Gson gson = new Gson();
        JsonObject vehicle = gson.fromJson(document.getString(Vehicle.F_VEHICLE), JsonObject.class);
        loadVehicleDetails(holder.frameLayout,vehicle);
        holder.button.setOnClickListener(v -> {
            openWhatsApp(
                    vehicle.get("owner").getAsJsonObject().get(UserLogIn.MOBILE_FIELD).getAsString()
                            .replace("0", "+94")
            );
        });

        holder.textViewCreatedDate.setText(String.format("Created Date: %s",document.getString(Vehicle.F_CREATED_DATE)));
        holder.textViewStatus.setText(document.getString(Vehicle.F_RENTAL_STATUS));
        holder.textViewStartDate.setText(document.getString(Vehicle.F_START_DATE));
        holder.textViewDays.setText(String.valueOf(document.getLong(Vehicle.F_DAYS)));
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    private void openWhatsApp(String phoneNumber) {
        try {
            String url = "https://wa.me/" + phoneNumber.replace("+", "").replace(" ", "");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadVehicleDetails(FrameLayout frameLayout,JsonObject vehicle) {

        View view = LayoutInflater.from(activity).inflate(R.layout.vehicle_item_layout, null);
        frameLayout.addView(view);

        TextView textViewTitle = view.findViewById(R.id.vehicle_card_title_textView);
        TextView textViewGearMode = view.findViewById(R.id.vehicle_card_gearMode_textView);
        TextView textViewAvailability = view.findViewById(R.id.vehicle_card_availability_textView);
        TextView textViewPrice = view.findViewById(R.id.vehicle_card_price_textView);
        TextView textViewHighlights = view.findViewById(R.id.vehicle_card_highlights_textView);
        ImageView imageView = view.findViewById(R.id.vehicle_card_image_imageView);

        String id = vehicle.get("id").getAsString();

        textViewTitle.setText(vehicle.get("title").getAsString());
        textViewGearMode.setText(String.format("Gear Mode: %s",
                Vehicle.GEAR_MODES.get(vehicle.get("gearMode").getAsInt())));
        textViewAvailability.setText(String.format("Status: %s",
                Vehicle.STATUS_LIST.get(vehicle.get("availability").getAsInt())));
        textViewPrice.setText(String.format("Rs. %s",
                new DecimalFormat().format(vehicle.get("pricePerDay").getAsDouble())));
        textViewHighlights.setText(vehicle.get("highlights").getAsString());

        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.load_blink);
        imageView.setAnimation(animation);
        animation.start();
        new Thread(()->{
            try {
                URL url = new URL(BuildConfig.HOST_URL + String.format("product/%s.png", id));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input =  connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                activity.runOnUiThread(()->{
                    imageView.clearAnimation();
                    imageView.setImageBitmap(bitmap);
                });
                connection.disconnect();
            } catch (Exception e) {
                Log.e(MainActivity.TAG,e.getLocalizedMessage(),e);
            }
        }).start();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder{

        TextView textViewCreatedDate;
        TextView textViewStatus;
        TextView textViewStartDate;
        TextView textViewDays;

        FrameLayout frameLayout;
        ImageButton button;
        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCreatedDate = itemView.findViewById(R.id.booking_created_date_textView);
            textViewStatus = itemView.findViewById(R.id.booking_status_textView);
            textViewStartDate = itemView.findViewById(R.id.booking_start_date_textView);
            textViewDays = itemView.findViewById(R.id.booking_days_textView);
            frameLayout = itemView.findViewById(R.id.booking_vehicle_card_frameLayout);
            button = itemView.findViewById(R.id.booking_whatsapp_imageButton);
        }
    }
}
