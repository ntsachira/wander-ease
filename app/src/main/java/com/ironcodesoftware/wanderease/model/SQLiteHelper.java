package com.ironcodesoftware.wanderease.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.MainActivity;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE = "wander.sqlite";
    private static final int DB_VERSION = 1;
    private static SQLiteHelper sqLiteHelper;

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

    public static void saveProfile(Context context, JsonObject userProfile, String email){

        sqLiteHelper = new SQLiteHelper(context, DATABASE, null, DB_VERSION);
        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();
        db.execSQL("DELETE FROM `profile` WHERE `email` != '"+email+"'");
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", userProfile.get("email").getAsString());
        contentValues.put("display_name", userProfile.get("display_name").getAsString());
        contentValues.put("user_role", userProfile.get("user_role").getAsInt());
        contentValues.put("mobile", userProfile.get("mobile").getAsString());
        contentValues.put("registered_date", userProfile.get("registered_date").getAsString());
        long inserted = sqLiteHelper.getWritableDatabase().insert("profile", null, contentValues);
        Log.d(MainActivity.TAG, String.valueOf(inserted));
    }

    public static JsonObject getProfile(Context context){
        sqLiteHelper = new SQLiteHelper(context, DATABASE, null, DB_VERSION);
        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();
        String[] projection = {"email","display_name","mobile"};

        Cursor cursor = db.query(
                "profile",
                projection,
                null,
                null,
                null,
                null,
                null);
        JsonObject jsonObject = new JsonObject();
        if (cursor.moveToNext()){
            jsonObject.addProperty("email", cursor.getString(0));
            jsonObject.addProperty("display_name",cursor.getString(1) );
            jsonObject.addProperty("mobile",cursor.getString(2));
        }
        return jsonObject;
    }
}
