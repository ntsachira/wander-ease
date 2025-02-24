package com.ironcodesoftware.wanderease.model.adaper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.HttpClient;
import com.ironcodesoftware.wanderease.model.Product;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;


public abstract class PartnerProductAdapter extends RecyclerView.Adapter<PartnerProductAdapter.ProductViewHolder> {

    JsonArray productList;

    FragmentActivity activity;

    public PartnerProductAdapter(JsonArray productList,FragmentActivity activity) {
        this.productList = productList;
        this.activity =  activity;
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.partner_product_item, parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        JsonObject productJsonObject = productList.get(position).getAsJsonObject();
        JsonObject categoryJsonObject = productJsonObject.get(Product.F_CATEGORY).getAsJsonObject();
        boolean activeStatus = productJsonObject.get(Product.F_STATE).getAsInt() == Product.state.Active.ordinal();

        setEditButton(holder.editButton, productJsonObject);

        holder.textViewTitle.setText(productJsonObject.get(Product.F_TITLE).getAsString());
        holder.textViewCategory.setText(categoryJsonObject.get("name").getAsString());
        holder.textViewQuantity.setText(String.format("%s Items",productJsonObject.get(Product.F_QTY).getAsInt()));
        holder.textureViewPrice.setText(String.format("Rs. %s",new DecimalFormat()
                        .format(Double.parseDouble(productJsonObject.get(Product.F_PRICE).getAsString())))
        );
        holder.textViewColor.setText(productJsonObject.get(Product.F_COLOR).getAsString());

        setStatusIndicator(holder.textViewStatus,holder.stateSwitch,activeStatus);

        new Thread(()->{
            try {
                URL url = new URL(BuildConfig.HOST_URL + String.format("product/%s.webp", productJsonObject.get("id").getAsString()));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input =  connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                activity.runOnUiThread(()->{
                    holder.imageViewProduct.setImageBitmap(bitmap);
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

    public abstract void setStatusIndicator(TextView textViewStatus, Switch statusSwitch, boolean activeStatus);
    public abstract void setEditButton(ImageButton button, JsonObject product);
    static class ProductViewHolder extends RecyclerView.ViewHolder{

        Switch stateSwitch;
        ImageButton editButton;
        TextView textViewTitle;
        TextView textViewCategory;
        TextView textViewQuantity;
        TextView textureViewPrice;
        TextView textViewColor;
        ImageView imageViewProduct;
        TextView textViewStatus;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            stateSwitch = itemView.findViewById(R.id.product_state_switch);
            editButton = itemView.findViewById(R.id.product_edit_imageButton);
            textViewTitle = itemView.findViewById(R.id.partner_product_card_title);
            textViewCategory = itemView.findViewById(R.id.partner_product_cart_category_textView);
            textViewQuantity = itemView.findViewById(R.id.partner_prodct_cart_quantity_textView);
            textureViewPrice = itemView.findViewById(R.id.partner_product_card_price_textView);
            textViewColor = itemView.findViewById(R.id.partner_product_card_color_textView);
            imageViewProduct = itemView.findViewById(R.id.partner_product_card_imageView);
            textViewStatus = itemView.findViewById(R.id.partner_product_card_active_status_textView);
        }
    }
}
