package com.phoenixcorp.classifiedsapp;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smarteist.autoimageslider.SliderViewAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {


//    Bitmap[] images;
     ArrayList<Uri> images;

    public SliderAdapter(ArrayList<Uri> images) {
//        this.context = context;
        this.images=images;
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

     class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        ImageView imageView;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
