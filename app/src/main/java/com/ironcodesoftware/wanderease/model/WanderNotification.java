package com.ironcodesoftware.wanderease.model;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.ironcodesoftware.wanderease.R;

public class WanderNotification {
    private static final String NOTIFICATION_CHANNEL_ID = "WE_NC_01";
    private static final String NOTIFICATION_CHANNEL_NAME = "WE_NC";
    private static final int NOTIFICATION_REQUEST_CODE = 101;
    private static final int NOTIFICATION_ID = 4;

    public static NotificationManager getManager(Context context){
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        return notificationManager;
    }

    public static void notify(Context context, Class<? extends AppCompatActivity> intentActivity){

        Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("You have New Messages")
                .setContentText("See messages from Wander Ease that you may have missed")
                .setSmallIcon(R.drawable.tree)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("See messages from Wander Ease that you may have missed"))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .addAction(new NotificationCompat.Action.Builder(
                        R.mipmap.ic_launcher, "Open Messages",
                        PendingIntent.getActivity(
                                context,
                                NOTIFICATION_REQUEST_CODE,
                                new Intent(context, intentActivity),
                                PendingIntent.FLAG_IMMUTABLE
                        )
                ).build()).build();

        getManager(context).notify(NOTIFICATION_ID, notification);
    }

    public static void dismiss(Context context){
        getManager(context).cancel(NOTIFICATION_ID);
    }
}
