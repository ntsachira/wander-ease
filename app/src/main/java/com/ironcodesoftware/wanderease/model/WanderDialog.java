package com.ironcodesoftware.wanderease.model;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ironcodesoftware.wanderease.R;

public class WanderDialog{

    public static AlertDialog.Builder build(Context context, String message, String title){
        return new android.app.AlertDialog.Builder(context)
                .setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton("Ok", (dialog, which) -> {
            dialog.cancel();
        });
    }
    public static AlertDialog.Builder build(Context context, String message){
        return build(context, message, "Warning");
    }

    public static AlertDialog loading(Context context){
        return loading(context, "Loading...");
    }
    public static AlertDialog loading(Context context,String message){
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setCancelable(false);
        View view = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null);
        ImageView imageView = view.findViewById(R.id.loading_dialog_imageView);
        TextView textView = view.findViewById(R.id.loading_dialog_textView);
        textView.setText(message);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.load_rotate);
        imageView.setAnimation(animation);
        dialog.setView(view);
        animation.start();
        return dialog;
    }

    public static AlertDialog success(Context context,String message){
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null);
        ImageView imageView = view.findViewById(R.id.loading_dialog_imageView);
        imageView.setImageResource(lk.payhere.androidsdk.R.drawable.enabled);
        TextView textView = view.findViewById(R.id.loading_dialog_textView);
        textView.setText(message);
        dialog.setView(view);
        return dialog;
    }

    public static AlertDialog cancel(Context context,String message){
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null);
        ImageView imageView = view.findViewById(R.id.loading_dialog_imageView);
        imageView.setImageResource(lk.payhere.androidsdk.R.drawable.payment_declined_image);
        TextView textView = view.findViewById(R.id.loading_dialog_textView);
        textView.setText(message);
        dialog.setView(view);
        return dialog;
    }

}
