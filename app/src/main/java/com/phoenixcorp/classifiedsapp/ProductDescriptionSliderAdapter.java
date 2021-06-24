package com.phoenixcorp.classifiedsapp;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductDescriptionSliderAdapter extends SliderViewAdapter<ProductDescriptionSliderAdapter.SliderAdapterVH> {
    ArrayList<String> images;

    public ProductDescriptionSliderAdapter(ArrayList<String> images) {
        this.images=images;

    }

    public void renewItems(ArrayList<String> sliderItems) {
        this.images = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.images.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(String sliderItem) {
        this.images.add(sliderItem);
        notifyDataSetChanged();
    }


    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item,parent, false);
        return new SliderAdapterVH(inflate);
    }


    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        Picasso.get().load(images.get(position)).into(viewHolder.imageView);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    static class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        ImageView imageView;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
