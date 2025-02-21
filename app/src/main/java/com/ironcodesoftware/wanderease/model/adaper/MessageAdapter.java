package com.ironcodesoftware.wanderease.model.adaper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.Notification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    List<DocumentSnapshot> documentsList;

    public MessageAdapter(List<DocumentSnapshot> documentsList) {
        this.documentsList = documentsList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        DocumentSnapshot document = documentsList.get(position);
        Date date = document.getDate(Notification.F_TIME);
        holder.textViewDate.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(date));
        holder.textViewContent.setText(document.getString(Notification.F_MESSAGE));
        holder.buttonMarkRead.setOnClickListener(v->{
            HashMap<String,Object> updatesMap = new HashMap<>();
            updatesMap.put(Notification.F_STATUS, Notification.State.Seen.name());
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection(Notification.COLLECTION).document(document.getId())
                    .update(updatesMap)
                    .addOnSuccessListener(unused -> {

                    })
                    .addOnFailureListener(e->{

                    });
        });
    }

    @Override
    public int getItemCount() {
        return documentsList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder{

        TextView textViewDate;
        TextView textViewContent;
        Button buttonMarkRead;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.message_date_textView);
            textViewContent = itemView.findViewById(R.id.message_content_textView);
            buttonMarkRead = itemView.findViewById(R.id.message_mark_read_button);
        }
    }
}
