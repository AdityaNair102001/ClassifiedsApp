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
import android.view.View;
import android.widget.Button;

import com.smarteist.autoimageslider.SliderView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class NewPostActivity extends AppCompatActivity {

//    Bitmap[] images;
    ArrayList<Uri> images;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);


        SliderView sliderView = findViewById(R.id.imageSlider);
        sliderView.setSliderAdapter(new SliderAdapter(images));

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK ){
            List<Bitmap> bitmaps = new ArrayList<>();
            ClipData clipData=data.getClipData();

            if(clipData!=null){
                for(int i=0;i<clipData.getItemCount();i++){
                    Uri imageURI=clipData.getItemAt(i).getUri();
                    images.add(imageURI);
//                    try{
//
//                        InputStream is = getContentResolver().openInputStream(imageURI);
//
//                        Bitmap bitmap= BitmapFactory.decodeStream(is);
//
//                        bitmaps.add(bitmap);
//
//
//                    }catch (FileNotFoundException e){
//                        e.printStackTrace();
//                    }
                }
            }else{
                Uri imageURI=data.getData();
                images.add(imageURI);

//                try {
//                    InputStream is = getContentResolver().openInputStream(imageURI);
//                    Bitmap bitmap= BitmapFactory.decodeStream(is);
//                    bitmaps.add(bitmap);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }

            }

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    for(Bitmap b:bitmaps){
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                images[]
//                            }
//                        });
//                    }
//                }
//            }).start();
//
//            for(int i=0;i<bitmaps.size();i++){
//                images[i]=bitmaps.get(i);
//            }

        }
    }
}