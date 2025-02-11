package com.ironcodesoftware.wanderease.model.adaper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.ironcodesoftware.wanderease.R;

import java.util.Arrays;
import java.util.List;

public class IntroSliderAdapter extends RecyclerView.Adapter<IntroSliderAdapter.SliderViewHolder> {

    List<Slide> slideList;

    public IntroSliderAdapter(List<Slide> slideList) {
        this.slideList = slideList;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.intro_slider_item, parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        Slide slide = slideList.get(position);
        holder.background.setImageResource(slide.getImageID());
        holder.content.setText(slide.getContentID());
    }

    @Override
    public int getItemCount() {
        return slideList.size();
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder{

        TextView content;
        ImageView background;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.intro_slider_textView);
            background = itemView.findViewById(R.id.intro_slide_imageView);
        }
    }

    public static class Slide{
        private int contentID;
        private int imageID;

        public Slide() {
        }

        public Slide(int contentID, int imageID) {
            this.contentID = contentID;
            this.imageID = imageID;
        }

        public int getContentID() {
            return contentID;
        }

        public void setContentID(int contentID) {
            this.contentID = contentID;
        }

        public int getImageID() {
            return imageID;
        }

        public void setImageID(int imageID) {
            this.imageID = imageID;
        }
    }

    public static void registerViewPager(ViewPager2 viewPager){
        viewPager.setAdapter(new IntroSliderAdapter(Arrays.asList(
                new Slide(R.string.main_explore_products,R.drawable.product_explore),
                new Slide(R.string.main_rent_vehicles,R.drawable.vehicle_rental),
                new Slide(R.string.main_book_guides,R.drawable.tour_guide_booking),
                new Slide(R.string.main_be_partner,R.drawable.be_partner)
        )));
        viewPager.setPageTransformer((page, position) -> {
            float scaleFactor = Math.max(0.85f, 1 - Math.abs(position));
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setAlpha(0.7f + (scaleFactor - 0.85f) / (1 - 0.85f) * 0.3f);
        });
    }
}
