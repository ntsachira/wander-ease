package com.ironcodesoftware.wanderease.model;

import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.NonNull;

public class WanderDialog{

    public static AlertDialog.Builder build(Context context, String message, String title){
        return new android.app.AlertDialog.Builder(context)
                .setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton("Ok", (dialog, which) -> {
            dialog.cancel();
        });
    }
    public static AlertDialog.Builder build(Context context, String message){
        return new android.app.AlertDialog.Builder(context)
                .setTitle("Warning").setMessage(message).setCancelable(false)
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.cancel();
                });
    }

}
