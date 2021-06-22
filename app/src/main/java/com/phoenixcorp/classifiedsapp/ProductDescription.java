package com.phoenixcorp.classifiedsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductDescription extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    ArrayList<Uri> images;
    SliderView sliderView;
    TextView productNameView, productPriceView, productDescriptionView, productLocationView, sellerNameView, sellerUID;
    CircleImageView sellerImageView;

    String productname, productprice, productdescription, productlocatoin, sellername;
    Uri sellerURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        getSupportActionBar().setTitle("Product Description");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();



    }
}