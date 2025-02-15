package com.ironcodesoftware.wanderease.model;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClient {
    public static final MediaType JSON = MediaType.get("application/json");
    public static final MediaType TEXT = MediaType.parse("text/plain");
    public static final MediaType IMAGE = MediaType.parse("text/plain");
    public static final String END_POINT_OFFLINE = "ERR_NGROK_3200";

    private final OkHttpClient client = new OkHttpClient();

    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static OkHttpClient getInstance(){

        return new OkHttpClient();
    }
}
