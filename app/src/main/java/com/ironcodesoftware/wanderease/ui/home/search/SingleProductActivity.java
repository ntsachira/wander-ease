package com.ironcodesoftware.wanderease.ui.home.search;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.HttpClient;
import com.ironcodesoftware.wanderease.model.Product;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.WanderDialog;
import com.ironcodesoftware.wanderease.ui.home.CheckoutActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class SingleProductActivity extends AppCompatActivity {

    JsonObject productJsonObject;
    UserLogIn login;
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

        try {
            login = UserLogIn.getLogin(this);
        } catch (IOException | ClassNotFoundException e) {
            finish();
        }
        Toolbar toolbar = findViewById(R.id.single_product_view_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        String singleProduct = getIntent().getStringExtra("singleProduct");
        if(singleProduct != null){
            Gson gson = new Gson();
            productJsonObject = gson.fromJson(singleProduct, JsonObject.class);
            loadProduct(productJsonObject);
        }else{
            Log.e(MainActivity.TAG,"No intent for single product");
            finish();
        }


        setupQuantitySelector();

        Button buttonBuyNow = findViewById(R.id.single_product_view_butNow_button);
        buttonBuyNow.setOnClickListener(v->{
            openCheckout();
        });

        Button buttonAddCart = findViewById(R.id.single_product_view_addto_cart_button);
        buttonAddCart.setOnClickListener(v -> {
            addToCart();
        });

        ImageButton buttonAddFavourites = findViewById(R.id.single_product_view_favourites_imageButton);
        buttonAddFavourites.setOnClickListener(v->{
            addToFavourites();
        });

    }

    private void addToFavourites() {
        AlertDialog loading = WanderDialog.loading(this, "Adding to favourites...");
        loading.show();
        String id = productJsonObject.get("id").getAsString();
        Request request = new Request.Builder().url(
                BuildConfig.HOST_URL + String.format("AddToFavourites?id=%s&email=%s",
                        id,login.getEmail())
        ).build();
        HttpClient.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(()->{
                    loading.cancel();
                    Snackbar snackbar = Snackbar.make(
                            findViewById(R.id.main),
                            "Something went Wrong, Please try again later",
                            Snackbar.LENGTH_INDEFINITE

                    );
                    snackbar.setAction("Ok", v -> {
                        snackbar.dismiss();
                    });
                    snackbar.show();
                });
                Log.e(MainActivity.TAG, "add to favourites failed");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(loading::cancel);
                if(response.isSuccessful()){
                    runOnUiThread(()->{
                        Toast.makeText(
                                SingleProductActivity.this,
                                "Product added to favourites successfully",
                                Toast.LENGTH_LONG
                        ).show();
                    });
                }else{
                    runOnUiThread(()->{
                        Snackbar snackbar = Snackbar.make(
                                findViewById(R.id.main),
                                "Request failed, Server error",
                                Snackbar.LENGTH_INDEFINITE

                        );
                        snackbar.setAction("Ok", v -> {
                            snackbar.dismiss();
                        });
                        snackbar.show();
                    });
                    Log.e(MainActivity.TAG, response.message());
                }
            }
        });
    }

    private void setupQuantitySelector() {
        ImageButton buttonDecreaseQuantity = findViewById(R.id.single_product_decrease_quantity_imageButton);
        buttonDecreaseQuantity.setOnClickListener(v->{
            TextView editTextQuantity = findViewById(R.id.single_product_quantity_editTextNumber);
            int qty = Integer.parseInt(editTextQuantity.getText().toString());
            if(qty > 1){
                editTextQuantity.setText(String.valueOf(qty-1));
            }
        });
    }

    private void addToCart() {
        AlertDialog loading = WanderDialog.loading(this, "Adding to cart...");
        loading.show();
        TextView editTextQuantity = findViewById(R.id.single_product_quantity_editTextNumber);
        String id = productJsonObject.get("id").getAsString();
        Request request = new Request.Builder().url(
                BuildConfig.HOST_URL + String.format("AddToCart?id=%s&qty=%s&email=%s",
                        id,editTextQuantity.getText().toString(),login.getEmail())
        ).build();
        HttpClient.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(()->{
                    loading.cancel();
                    Snackbar snackbar = Snackbar.make(
                            findViewById(R.id.main),
                            "Something went Wrong, Please try again later",
                            Snackbar.LENGTH_INDEFINITE

                    );
                    snackbar.setAction("Ok", v -> {
                        snackbar.dismiss();
                    });
                    snackbar.show();
                });
                Log.e(MainActivity.TAG, "add to cart failed");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(loading::cancel);
                if(response.isSuccessful()){
                    runOnUiThread(()->{
                        Toast.makeText(
                                SingleProductActivity.this,
                                "Product added to cart successfully",
                                Toast.LENGTH_LONG
                        ).show();
                    });
                }else{
                    runOnUiThread(()->{
                        Snackbar snackbar = Snackbar.make(
                                findViewById(R.id.main),
                                "Request failed, Server error",
                                Snackbar.LENGTH_INDEFINITE

                        );
                        snackbar.setAction("Ok", v -> {
                            snackbar.dismiss();
                        });
                        snackbar.show();
                    });
                    Log.e(MainActivity.TAG, response.message());
                }
            }
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