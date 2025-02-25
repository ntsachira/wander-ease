package com.ironcodesoftware.wanderease.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.BuildConfig;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.ui.home.DeliveryLocationActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class AddressRequest {

    private String lat;
    private String lon;

    public AddressRequest(String lat, String lon) {
        this.lat = lat;
        this.lon = lon;
        start();
    }

    private void start(){
        Request request = new Request.Builder().url(
                        String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s",
                                lat,
                                lon,
                                BuildConfig.MAPS_API_KEY
                        ))
                .build();
        HttpClient.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                onResponseFailed(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    JsonObject goeDataJson = new Gson().fromJson(response.body().string(), JsonObject.class);
                    JsonObject results = goeDataJson.get("results").getAsJsonArray().get(0).getAsJsonObject();
                    if(isSouthernProvince(results)){
                        String address = results.get("formatted_address").getAsString();
                        onResponseSuccess(address);
                    }else{
                        onResponseFailed("Outside southern province");
                    }

                }else{
                   onResponseFailed("2:Geo location address request failed");
                }
            }
        });

    }

    protected void onResponseSuccess(String address){

    }
    protected void onResponseFailed(String message){
        Log.e(MainActivity.TAG, message);
    }
    private boolean isSouthernProvince(JsonObject results) {
        for (JsonElement element : results.get("address_components").getAsJsonArray()) {
            JsonObject jsonObject = element.getAsJsonObject();
            if(jsonObject.get("types").getAsJsonArray().get(0).getAsString().equals("administrative_area_level_1")
                    && jsonObject.get("long_name").getAsString().equals("Southern Province")){
                return true;
            }
        }
        return false;
    }
}
