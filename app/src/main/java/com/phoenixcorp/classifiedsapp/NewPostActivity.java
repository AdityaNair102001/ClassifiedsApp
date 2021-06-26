package com.phoenixcorp.classifiedsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

//import com.smarteist.autoimageslider.SliderView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smarteist.autoimageslider.SliderView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NewPostActivity extends AppCompatActivity {


    private static final String TAG ="TAG" ;
    ArrayList<Uri> images;
    SliderView sliderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

         sliderView = findViewById(R.id.imageSlider);

        images = new ArrayList<>();

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

        Button post=findViewById(R.id.post);





        EditText productName=findViewById(R.id.productName);
        EditText productDescription=findViewById(R.id.productDescription);
        EditText price=findViewById(R.id.price);
        EditText location=findViewById(R.id.location);


        FirebaseFirestore db=FirebaseFirestore.getInstance();


        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();


        ProgressDialog pd= new ProgressDialog(this);
        pd.setCancelable(false);


        post.setOnClickListener(v->{

            String productNameVal=productName.getText().toString();
            String productDescriptionVal=productDescription.getText().toString();
            String priceVal=price.getText().toString();
            String locationVal=location.getText().toString();

            final String timeStamp=Long.toString(System.currentTimeMillis());
            final String documentID=timeStamp+currentUser;

            if(images.isEmpty()){

                Toast.makeText(NewPostActivity.this,"Please select images.",Toast.LENGTH_LONG).show();

            }else {
                if(TextUtils.isEmpty(productNameVal)){
                    productName.setError("Product Name is Required.");
                    return;
                }

                if(TextUtils.isEmpty(productDescriptionVal)){
                    productDescription.setError("Product Description is Required.");
                    return;
                }

                if(TextUtils.isEmpty(priceVal)){
                    price.setError("Price is Required.");
                    return;
                }

                if(TextUtils.isEmpty(locationVal)){
                    location.setError("Location is Required.");
                    return;
                }
                else{
                    uploadImages(db,pd,documentID,currentUser);
                    uploadData(productNameVal,productDescriptionVal,priceVal,locationVal,db,pd,documentID,currentUser);

                }
            }




        });

    }

    private void uploadImages(FirebaseFirestore db, ProgressDialog pd,String documentID,String currentUser) {

        pd.setMessage("Uploading Images");
        pd.show();

        StorageReference ImageFolder= FirebaseStorage.getInstance().getReference().child("Images");

        for(int i=0;i<images.size();i++){

            Uri individualImage=images.get(i);
            StorageReference imageName= ImageFolder.child("image"+individualImage.getLastPathSegment());

            imageName.putFile(individualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url= String.valueOf(uri);

                            Log.d("URL", "onSuccess: "+url);


                            Map<String,Object> urlSet=new HashMap<>();
                            urlSet.put("url",url);

                            db.collection("posts").document(documentID).collection("urls").add(urlSet).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if(task.isSuccessful()){
                                        pd.dismiss();
                                        Toast.makeText(NewPostActivity.this,"Images uploaded",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(NewPostActivity.this, DefaultPageActivity.class);
                                        startActivity(intent);

                                    }else{
                                        Toast.makeText(NewPostActivity.this,"Couldn't post."+ Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                            db.collection("users").document(currentUser).collection("my posts").document(documentID).collection("urls").add(urlSet).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(NewPostActivity.this,"Images added to my posts",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });
                }
            });

        }


    }

    void uploadData(String productNameVal,String productDescriptionVal, String priceVal,String locationVal,FirebaseFirestore db,ProgressDialog pd,String documentID,String currentUser){


        Map<String,Object> postData=new HashMap<>();
        postData.put("productName",productNameVal);
        postData.put("productDescription",productDescriptionVal);
        postData.put("price",priceVal);
        postData.put("location",locationVal);
        postData.put("UID",currentUser);


        db.collection("posts").document(documentID).set(postData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(NewPostActivity.this,"Data Posted",Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(NewPostActivity.this,"Couldn't post."+ Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });


        db.collection("users").document(currentUser).collection("my posts").document(documentID).set(postData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(NewPostActivity.this,"Added to my posts",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void adapterHandler(ArrayList<Uri> images) {
        SliderAdapter adapter=new SliderAdapter(images);
        sliderView.setSliderAdapter(adapter);
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