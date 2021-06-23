package com.phoenixcorp.classifiedsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductDescription extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    ArrayList<Uri> images;
    ArrayList<String> subDocs;
    SliderView sliderView;
    TextView productNameView, productPriceView, productDescriptionView, productLocationView, sellerNameView;
    CircleImageView sellerImageView;

    SliderView postImageSlider;

    String productname, productprice, productdescription, productlocation, sellername, sellerUID, documentID;
    String sellerURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        getSupportActionBar().setTitle("Product Description");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        subDocs = new ArrayList<>();
        images = new ArrayList<>();

        productNameView = findViewById(R.id.desc_product_name);
        productPriceView = findViewById(R.id.desc_product_price);
        productDescriptionView = findViewById(R.id.desc_product_description);
        productLocationView = findViewById(R.id.desc_product_location);
        sellerNameView = findViewById(R.id.desc_product_sellerName);
        sellerImageView = findViewById(R.id.desc_product_sellerImg);
        postImageSlider = findViewById(R.id.desc_product_imageSlider);

        productname = getIntent().getStringExtra("Product Name");
        productprice = getIntent().getStringExtra("Product Price");
        sellerUID = getIntent().getStringExtra("Seller UID");
        documentID = getIntent().getStringExtra("Document ID");
        productlocation = getIntent().getStringExtra("Product Location");

        productNameView.setText(productname);
        productPriceView.setText(productprice);
        productLocationView.setText(productlocation);
        sellerNameView.setText(sellername);


        firestore.collection("posts").document(documentID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                productdescription = documentSnapshot.getString("productDescription");
                productDescriptionView.setText(productdescription);
            }
        });

        firestore.collection("posts").document(documentID).collection("urls").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    String imageStr = snapshot.getString("url");
                    Uri ImageUri = Uri.parse(imageStr);
                    images.add(ImageUri);
                    Log.d( "onSuccess Image URL : ", snapshot.getString("url"));
                }adapterHandler(images);
            }
        });



        firestore.collection("users").document(sellerUID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        sellername = snapshot.getString("username");
                        sellerURI = snapshot.getString("imageURI");
                        sellerNameView.setText(sellername);
                        Picasso.get().load(sellerURI).into(sellerImageView);
                    }
                }
            }
        });


    }
    private void adapterHandler(ArrayList<Uri> images) {
        ProductDescriptionSliderAdapter adapter=new ProductDescriptionSliderAdapter(images);
        Log.d( "adapterHandler: ", ""+ images);
        postImageSlider.setSliderAdapter(adapter);
    }
}