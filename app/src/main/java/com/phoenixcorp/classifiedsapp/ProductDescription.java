package com.phoenixcorp.classifiedsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductDescription extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    ArrayList<String> images;
    ArrayList<String> reverseImages;
    SliderView sliderView;
    TextView productNameView, productPriceView, productDescriptionView, productLocationView, sellerNameView;
    LinearLayout callButton, chatButton;
    CircleImageView sellerImageView;

    SliderView postImageSlider;

    String productname, productprice, productdescription, productlocation, sellername, sellerUID, documentID;
    String sellerURI, sellerPhoneNo;
    String BuyerUID, BuyerUri, BuyerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        getSupportActionBar().setTitle("Product Description");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        images = new ArrayList<>();
        reverseImages = new ArrayList<>();

        productNameView = findViewById(R.id.desc_product_name);
        productPriceView = findViewById(R.id.desc_product_price);
        productDescriptionView = findViewById(R.id.desc_product_description);
        productLocationView = findViewById(R.id.desc_product_location);
        sellerNameView = findViewById(R.id.desc_product_sellerName);
        sellerImageView = findViewById(R.id.desc_product_sellerImg);
        postImageSlider = findViewById(R.id.desc_product_imageSlider);

        callButton = findViewById(R.id.callButton);
        chatButton = findViewById(R.id.chatButton);

        productname = getIntent().getStringExtra("Product Name");
        productprice = getIntent().getStringExtra("Product Price");
        sellerUID = getIntent().getStringExtra("Seller UID");
        documentID = getIntent().getStringExtra("Document ID");
        productlocation = getIntent().getStringExtra("Product Location");

        productNameView.setText(productname);
        productPriceView.setText(productprice);
        productLocationView.setText(productlocation);
        sellerNameView.setText(sellername);

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDescription.this, ChatActivity.class);
                intent.putExtra("ReceiverImage", sellerURI);
                intent.putExtra("name", sellername);
                intent.putExtra("UID", sellerUID);
                intent.putExtra("BuyerUID", auth.getUid());
                intent.putExtra("BuyerUri", BuyerUri);
                intent.putExtra("BuyerName", BuyerName);
                startActivity(intent);
            }
        });


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


        firestore.collection("users").document(sellerUID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        sellername = snapshot.getString("username");
                        sellerURI = snapshot.getString("imageURI");
                        sellerPhoneNo = snapshot.getString("phone");
                        sellerNameView.setText(sellername);
                        Picasso.get().load(sellerURI).into(sellerImageView);
                        callButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+sellerPhoneNo));
                                startActivity(intent);
                            }
                        });
                    }
                }
            }
        });

        firestore.collection("users").document(auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        BuyerUID = auth.getUid();
                        BuyerUri = snapshot.getString("imageURI");
                        BuyerName = snapshot.getString("username");
                    }
                }
            }
        });


    }
    private void adapterHandler(ArrayList<String> images) {
        ProductDescriptionSliderAdapter adapter=new ProductDescriptionSliderAdapter(images);
//        Log.d( "adapterHandler: ", ""+ images);
        postImageSlider.setSliderAdapter(adapter);
    }

}