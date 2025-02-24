package com.ironcodesoftware.wanderease.model.adaper;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.UserLogIn;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public abstract class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    JsonArray userList;
    FragmentActivity activity;

    public UserAdapter(JsonArray userList, FragmentActivity activity) {
        this.userList = userList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_layout, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        JsonObject user = userList.get(position).getAsJsonObject();
        holder.textViewLetter.setText(user.get(UserLogIn.DISPLAY_NAME_FIELD).getAsString().substring(0, 1));
        holder.textViewName.setText(user.get(UserLogIn.DISPLAY_NAME_FIELD).getAsString());
        holder.textViewEmail.setText(user.get(UserLogIn.EMAIL_FIELD).getAsString());
        holder.textViewMobile.setText(user.get(UserLogIn.MOBILE_FIELD).getAsString());
        holder.textViewDate.setText(user.get("registered_date").getAsString());

        holder.buttonCall.setOnClickListener(v->{
            onCallButtonClick(holder.buttonCall, user.get(UserLogIn.MOBILE_FIELD).getAsString());
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public abstract void onCallButtonClick(ImageButton callButton,String mobile);

    static class UserViewHolder extends RecyclerView.ViewHolder{

        TextView textViewLetter;
        TextView textViewName;
        TextView textViewEmail;
        TextView textViewMobile;
        TextView textViewDate;
        ImageButton buttonCall;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewLetter = itemView.findViewById(R.id.user_letter_textview);
            textViewName = itemView.findViewById(R.id.user_displayName_textview);
            textViewEmail = itemView.findViewById(R.id.user_email_textView);
            textViewMobile = itemView.findViewById(R.id.user_mobile_textView);
            textViewDate = itemView.findViewById(R.id.user_registered_date_textView);
            buttonCall = itemView.findViewById(R.id.user_call_imageButton);
        }
    }
}
