package com.phoenixcorp.classifiedsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class MyAdsProductDescriptionActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    ArrayList<String> images;
    ArrayList<String> reverseImages;

    TextView productNameView, productPriceView, productDescriptionView, productLocationView;
    String productname, productprice, productdescription, productlocation, documentID;

    SliderView postImageSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads_product_description);


        getSupportActionBar().setTitle("Product Description");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        images = new ArrayList<>();
        reverseImages = new ArrayList<>();

        productNameView = findViewById(R.id.MyAds_productDesc_name);
        productPriceView = findViewById(R.id.MyAds_productDesc_price);
        productDescriptionView = findViewById(R.id.MyAds_productDesc_description);
        productLocationView = findViewById(R.id.MyAds_productDesc_location);

        postImageSlider = findViewById(R.id.MyAds_productDesc_imageSlider);


        productname = getIntent().getStringExtra("Product Name");
        productprice = getIntent().getStringExtra("Product Price");
        documentID = getIntent().getStringExtra("Document ID");
        productlocation = getIntent().getStringExtra("Product Location");

        productNameView.setText(productname);
        productPriceView.setText(productprice);
        productLocationView.setText(productlocation);



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
                    images.add(imageStr);
//                    Log.d( "onSuccess Image URL : ", snapshot.getString("url"));
                }
                for(int i=images.size()-1; i>=0; i--)
                    reverseImages.add(images.get(i));

                adapterHandler(reverseImages);
            }
        });

    }
    private void adapterHandler(ArrayList<String> images) {
        ProductDescriptionSliderAdapter adapter=new ProductDescriptionSliderAdapter(images);
//        Log.d( "adapterHandler: ", ""+ images);
        postImageSlider.setSliderAdapter(adapter);
    }
}