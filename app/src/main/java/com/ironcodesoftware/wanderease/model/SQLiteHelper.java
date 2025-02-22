package com.ironcodesoftware.wanderease.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.MainActivity;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE = "wander.sqlite";
    private static final int DB_VERSION = 1;

    public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE profile (\n" +
                        "    email           TEXT    PRIMARY KEY,\n" +
                        "    display_name    TEXT,\n" +
                        "    user_role       NUMERIC,\n" +
                        "    mobile          TEXT,\n" +
                        "    registered_date TEXT\n" +
                        ");\n"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public static void saveProfile(Context context,JsonObject userProfile){
        SQLiteHelper sqLiteHelper = new SQLiteHelper(context, DATABASE, null, DB_VERSION);
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", userProfile.get("email").getAsString());
        contentValues.put("display_name", userProfile.get("display_name").getAsString());
        contentValues.put("user_role", userProfile.get("user_role").getAsInt());
        contentValues.put("mobile", userProfile.get("mobile").getAsString());
        contentValues.put("registered_date", userProfile.get("registered_date").getAsString());
        long inserted = sqLiteHelper.getWritableDatabase().insert("profile", null, contentValues);
        Log.d(MainActivity.TAG, String.valueOf(inserted));
    }
}
