package com.phoenixcorp.classifiedsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductDescription extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    ArrayList<String> images;
    ArrayList<String> subDocs;
    SliderView sliderView;
    TextView productNameView, productPriceView, productDescriptionView, productLocationView, sellerNameView;
    CircleImageView sellerImageView;

    String productname, productprice, productdescription, productlocatoin, sellername, sellerUID, documentID;
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

        productNameView = findViewById(R.id.desc_product_name);
        productPriceView = findViewById(R.id.desc_product_price);
        productDescriptionView = findViewById(R.id.desc_product_description);
        productLocationView = findViewById(R.id.desc_product_location);
        sellerNameView = findViewById(R.id.desc_product_sellerName);
        sellerImageView = findViewById(R.id.desc_product_sellerImg);

        productname = getIntent().getStringExtra("Product Name");
        productprice = getIntent().getStringExtra("Product Price");
        sellerUID = getIntent().getStringExtra("Seller UID");
        documentID = getIntent().getStringExtra("Document ID");

        productNameView.setText(productname);
        productPriceView.setText(productprice);
//        productLocationView.setText(productlocation);
        sellerNameView.setText(sellername);


//        firestore.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        subDocs.add(document.getId());
//                    }
//                    Log.d("nuber of documents : ", Integer.toString(subDocs.size()));
//                } else {
//                    Log.d("abc", "abc");
//                }
//            }
//        });


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
}