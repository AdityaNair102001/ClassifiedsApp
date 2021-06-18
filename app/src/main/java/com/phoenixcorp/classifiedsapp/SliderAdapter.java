package com.phoenixcorp.classifiedsapp;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smarteist.autoimageslider.SliderViewAdapter;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH>{

     ArrayList<Uri> images;

    public SliderAdapter(ArrayList<Uri> images) {
        this.images=images;
    }


    public void renewItems(ArrayList<Uri> sliderItems) {
        this.images = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.images.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(Uri sliderItem) {
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
        viewHolder.imageView.setImageURI(images.get(position));
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
