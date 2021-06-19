package com.phoenixcorp.classifiedsapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Slide;
import android.view.View;
import android.widget.Button;

//import com.smarteist.autoimageslider.SliderView;

import com.smarteist.autoimageslider.SliderView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class NewPostActivity extends AppCompatActivity {


    ArrayList<Uri> images;
    SliderView sliderView;
    SliderAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);


         sliderView = findViewById(R.id.imageSlider);

         adapter=new SliderAdapter((images));



        Button selectImages=findViewById(R.id.selectImages);

        selectImages.setOnClickListener(v->{

            if(ActivityCompat.checkSelfPermission(NewPostActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(NewPostActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
                return;

            }

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
            intent.setType("image/*");
            startActivityForResult(intent,1);

           });

    }


    private void adapterHandler(ArrayList<Uri> images) {
        SliderAdapter adapter=new SliderAdapter(images);
        sliderView.setSliderAdapter(adapter);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        images = new ArrayList<>();


        if(requestCode==1 && resultCode==RESULT_OK ){
            List<Bitmap> bitmaps = new ArrayList<>();
            ClipData clipData=data.getClipData();

            if(clipData!=null){
                for(int i=0;i<clipData.getItemCount();i++){
                    Uri imageURI=clipData.getItemAt(i).getUri();
                    images.add(imageURI);
                    adapterHandler(images);
                }
            }else{
                Uri imageURI=data.getData();
                images.add(imageURI);
                adapterHandler(images);
            }


        }
    }

}