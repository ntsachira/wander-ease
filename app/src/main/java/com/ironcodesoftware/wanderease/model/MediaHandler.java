package com.ironcodesoftware.wanderease.model;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;

import com.ironcodesoftware.wanderease.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MediaHandler {
    public static File uriToFile(Context context, Uri uri){
        try(Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)){
            if(cursor != null && cursor.moveToFirst()){
                int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if(columnIndex != -1){
                    File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File file = new File(storageDir, cursor.getString(columnIndex));

                    InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    FileOutputStream outputStream = new FileOutputStream(file);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                    inputStream.close();
                    outputStream.close();
                    if(file.exists()){
                        return file;
                    }else{
                        Log.d(MainActivity.TAG, "not exist");
                    }
                }
            }
        } catch (Exception e) {
            Log.e(MainActivity.TAG, e.getMessage());
        }
        return null;
    }
}
