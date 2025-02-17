package com.ironcodesoftware.wanderease.ui.home.search;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.Product;
import com.ironcodesoftware.wanderease.ui.home.CheckoutActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class SingleProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.single_product_view_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        String singleProduct = getIntent().getStringExtra("singleProduct");
        if(singleProduct != null){
            Gson gson = new Gson();
            JsonObject productJsonObject = gson.fromJson(singleProduct, JsonObject.class);
            loadProduct(productJsonObject);
        }else{
            Log.e(MainActivity.TAG,"No intent for single product");
            finish();
        }


        ImageButton buttonDecreaseQuantity = findViewById(R.id.single_product_decrease_quantity_imageButton);
        buttonDecreaseQuantity.setOnClickListener(v->{
            TextView editTextQuantity = findViewById(R.id.single_product_quantity_editTextNumber);
            int qty = Integer.parseInt(editTextQuantity.getText().toString());
            if(qty > 1){
                editTextQuantity.setText(String.valueOf(qty-1));
            }
        });

        Button buttonBuyNow = findViewById(R.id.single_product_view_butNow_button);
        buttonBuyNow.setOnClickListener(v->{
            openCheckout();
        });

    }

    private void openCheckout() {
        TextView editTextQuantity = findViewById(R.id.single_product_quantity_editTextNumber);
        Intent intent = new Intent(SingleProductActivity.this, CheckoutActivity.class);
        JsonObject itemJsonObject = new JsonObject();
        Gson gson = new Gson();
        itemJsonObject.addProperty("qty", editTextQuantity.getText().toString());
        itemJsonObject.add("item", gson.toJsonTree(
                gson.fromJson(getIntent().getStringExtra("singleProduct"), JsonObject.class)
        ));
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(gson.toJsonTree(itemJsonObject));
        intent.putExtra("productList",jsonArray.toString());
        startActivity(intent);
    }

    private void setQuantityIncreaseButton(int total){
        ImageButton buttonIncreaseQuantity = findViewById(R.id.single_product_increase_quantity_imageButton);
        buttonIncreaseQuantity.setOnClickListener(v->{
            TextView editTextQuantity = findViewById(R.id.single_product_quantity_editTextNumber);
            int qty = Integer.parseInt(editTextQuantity.getText().toString());
            if(qty < total && qty < 10){
                editTextQuantity.setText(String.valueOf(qty+1));
            }
        });
    }

    private void loadProduct(JsonObject productJsonObject) {
        String id = productJsonObject.get("id").getAsString();
        loadImage(id);
        TextView textViewTitle = findViewById(R.id.single_product_view_title_textView);
        TextView textViewQuantity = findViewById(R.id.single_product_view__quantity_textView);
        TextView textViewPrice = findViewById(R.id.single_product_view_price_textView);
        TextView textViewColor = findViewById(R.id.single_product_view_color_textView);
        TextView textViewDescription = findViewById(R.id.single_product_view_description_textView);
        Toolbar toolbar = findViewById(R.id.single_product_view_toolbar);

        toolbar.setTitle(productJsonObject.get(Product.F_TITLE).getAsString());

        textViewTitle.setText(productJsonObject.get(Product.F_TITLE).getAsString());
        textViewQuantity.setText(String.format("%s Items Remaining",productJsonObject.get(Product.F_QTY).getAsInt()));
        setQuantityIncreaseButton(productJsonObject.get(Product.F_QTY).getAsInt());
        textViewPrice.setText(String.format("Rs. %s",new DecimalFormat()
                .format(Double.parseDouble(productJsonObject.get(Product.F_PRICE).getAsString())))
        );
        textViewColor.setText(String.format("Color: %s",productJsonObject.get(Product.F_COLOR).getAsString()));
        textViewDescription.setText(productJsonObject.get(Product.F_DESC).getAsString());
    }

    private void loadImage(String id) {
        ImageView imageView = findViewById(R.id.single_product_view_imageView);
        new Thread(()->{
            try {
                URL url = new URL(BuildConfig.HOST_URL + String.format("product/%s.webp", id));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input =  connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                runOnUiThread(()->{
                    imageView.setImageBitmap(bitmap);
                });
                connection.disconnect();
            } catch (Exception e) {
                Log.e(MainActivity.TAG,e.getLocalizedMessage(),e);
            }
        }).start();
    }
}