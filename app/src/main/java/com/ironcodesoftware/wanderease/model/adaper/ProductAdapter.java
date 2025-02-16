package com.ironcodesoftware.wanderease.model.adaper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.Product;
import com.ironcodesoftware.wanderease.ui.home.search.SingleProductActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    JsonArray productList;

    FragmentActivity activity;

    public ProductAdapter(JsonArray productList, FragmentActivity activity) {
        this.productList = productList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        JsonObject productJsonObject = productList.get(position).getAsJsonObject();
        String id = productJsonObject.get("id").getAsString();
        holder.textViewTitle.setText(productJsonObject.get(Product.F_TITLE).getAsString());
        holder.textViewQuantity.setText(String.format("%s Items",productJsonObject.get(Product.F_QTY).getAsInt()));
        holder.textViewPrice.setText(String.format("Rs. %s",new DecimalFormat()
                .format(Double.parseDouble(productJsonObject.get(Product.F_PRICE).getAsString())))
        );
        holder.textViewColor.setText(String.format("Color: %s",productJsonObject.get(Product.F_COLOR).getAsString()));

        holder.cardView.setOnClickListener(v->{
            Intent intent = new Intent(activity, SingleProductActivity.class);
            intent.putExtra("singleProduct", productJsonObject.toString());
            activity.startActivity(intent);
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
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle;
        TextView textViewColor;
        TextView textViewQuantity;
        TextView textViewPrice;
        ImageView imageView;

        CardView cardView;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.product_card_title_textView);
            textViewColor = itemView.findViewById(R.id.product_card_color_textView);
            textViewQuantity = itemView.findViewById(R.id.product_card_quantity_textView);
            textViewPrice = itemView.findViewById(R.id.product_card_price_textview);
            imageView = itemView.findViewById(R.id.product_card_image_imageView);
            cardView = itemView.findViewById(R.id.product_card);
        }
    }
}
