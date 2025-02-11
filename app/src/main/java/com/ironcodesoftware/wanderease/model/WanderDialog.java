package com.ironcodesoftware.wanderease.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class WanderDialog extends AlertDialog {
    protected WanderDialog(@NonNull Context context,String title,String message) {
        super(context);
        setMessage(message);
        setTitle(title);
    }

}
