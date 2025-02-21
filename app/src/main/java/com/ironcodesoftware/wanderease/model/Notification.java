package com.ironcodesoftware.wanderease.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ironcodesoftware.wanderease.MainActivity;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.ui.delivery.DeliveryTaskViewActivity;

import java.util.Calendar;
import java.util.HashMap;

public interface Notification {
    enum State{
        Not_Seen,
        Seen;

        @NonNull
        @Override
        public String toString() {
            return this.name().replace("_", " ");
        }
    }


    String F_MESSAGE = "message";
    String F_STATUS = "status";
    String F_TIME = "created_datetime";
    String F_USER= "recipient";
    String COLLECTION= "notification";
}
