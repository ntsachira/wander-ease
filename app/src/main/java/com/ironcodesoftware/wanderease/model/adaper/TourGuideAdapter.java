package com.ironcodesoftware.wanderease.model.adaper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ironcodesoftware.wanderease.R;

public abstract class TourGuideAdapter extends RecyclerView.Adapter<TourGuideAdapter.TourGuideViewHolder> {

    JsonArray tourGuideArray;
    FragmentActivity activity;

    public TourGuideAdapter(JsonArray tourGuideArray, FragmentActivity activity) {
        this.tourGuideArray = tourGuideArray;
        this.activity = activity;
    }

    @NonNull
    @Override
    public TourGuideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TourGuideViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tour_guide_card_layout, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TourGuideViewHolder holder, int position) {
        JsonObject jsonObject = tourGuideArray.get(position).getAsJsonObject();

        holder.textViewTitle.setText("");
        holder.textViewType.setText("");
        holder.textViewExperience.setText("");
        holder.textViewPrice.setText("");
        holder.textViewBio.setText("");

        onCardClick(jsonObject, holder.card);

    }

    @Override
    public int getItemCount() {
        return tourGuideArray.size();
    }

    public abstract void onCardClick(JsonObject tourGuide, ConstraintLayout card);

    static class TourGuideViewHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle;
        TextView textViewType;
        TextView textViewExperience;
        TextView textViewPrice;
        TextView textViewBio;

        ImageView imageView;

        ConstraintLayout card;
        public TourGuideViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.tour_guide_cart_title_textView);
            textViewType = itemView.findViewById(R.id.tour_guide_cart_tourType_textView);
            textViewExperience = itemView.findViewById(R.id.tour_guide_cart_experience_textView);
            textViewPrice = itemView.findViewById(R.id.tour_guide_cart_price_textView);
            textViewBio = itemView.findViewById(R.id.tour_guide_cart_bio_textView);
            imageView = itemView.findViewById(R.id.tour_guide_cart_imageView);
            card = itemView.findViewById(R.id.tour_guide_card_constraintLayout);
        }
    }
}
