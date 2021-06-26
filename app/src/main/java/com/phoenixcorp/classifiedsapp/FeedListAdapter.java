package com.phoenixcorp.classifiedsapp;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import io.grpc.internal.KeepAliveManager;

public class FeedListAdapter extends RecyclerView.Adapter<FeedListAdapter.FeedListViewHolder> {
    ArrayList<String> products;
    ArrayList<String> prices;
    ArrayList<String> UIDs;
    ArrayList<String> DocumentID;
    ArrayList<String> location;
    ArrayList<String> productDescriptions;

    HashMap<String,Boolean> likedPosts;
    HashMap<String,String> names;
    HashMap<String,String> imageURLs;

    Fragment homeFragment;

    public FeedListAdapter(ArrayList<String>products, ArrayList<String> prices, HashMap<String,String> imageURLs, ArrayList<String> UIDs, ArrayList<String> location , HashMap<String,String> names, HomeFragment homeFragment, ArrayList<String> DocumentID,HashMap<String,Boolean> likedPosts,ArrayList<String>productDescriptions) {

        this.products=products;
        this.prices=prices;
        this.imageURLs=imageURLs;
        this.names=names;
        this.UIDs=UIDs;
        this.homeFragment=homeFragment;
        this.DocumentID = DocumentID;
        this.location=location;
        this.likedPosts= likedPosts;
        this.productDescriptions=productDescriptions;

    }

    @NonNull
    @Override
    public FeedListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.feedlist_feed_layout,parent,false);
        return new FeedListViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull FeedListViewHolder holder, int position) {
        String productName=products.get(position);
        String price= "\u20B9"+ prices.get(position);
        String productLocation = location.get(position);
        String docID = DocumentID.get(position);
        String url=imageURLs.get(docID);
        String sellerUID = UIDs.get(position);
        holder.productName.setText(productName);
        holder.location.setText(location.get(position));
        holder.price.setText(price);
        Picasso.get().load(url).placeholder(R.drawable.loader).into(holder.feedImage);
        String productDescription=productDescriptions.get(position);

        holder.productCard.setOnClickListener(v -> {
            Intent intent = new Intent(homeFragment.getActivity(), ProductDescription.class);
            intent.putExtra("Product Name", productName);
            intent.putExtra("Product Price", price);
            intent.putExtra("Seller UID", sellerUID);
            intent.putExtra("Document ID", docID);
            intent.putExtra("Product Location", productLocation);
            homeFragment.startActivity(intent);
        });

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        final String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.likeBtn.isChecked()){
                    HashMap<String,String> likedPostSet=new HashMap();
                    likedPostSet.put("productName",productName);
                    likedPostSet.put("price",price);
                    likedPostSet.put("location",productLocation);
                    likedPostSet.put("UID",sellerUID);
                    likedPostSet.put("docID",docID);
                    likedPostSet.put("productDescription",productDescription);
                    db.collection("users").document(currentUser).collection("liked posts").document(docID).set(likedPostSet).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                               Toast.makeText(homeFragment.getActivity(),"Added to Liked Post",Toast.LENGTH_SHORT).show();

                               HashMap<String,String> likedPostUrlSet=new HashMap();
                               likedPostUrlSet.put("url",url);

                               db.collection("users").document(currentUser).collection("liked posts").document(docID).collection("urls").add(likedPostUrlSet).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                   @Override
                                   public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(homeFragment.getActivity(),"Images added to Liked Post",Toast.LENGTH_SHORT).show();
                                        }
                                   }
                               });


                           }else{
                               Toast.makeText(homeFragment.getActivity(),"Couldn't add to like post",Toast.LENGTH_LONG).show();
                           }
                        }
                    });
                }else{
                    db.collection("users/"+currentUser+"/liked posts").document(docID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(homeFragment.getActivity(),"Unliked!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


        if(likedPosts.get(docID)!=null && likedPosts.get(docID)){
            holder.likeBtn.setChecked(true);
        }


    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class FeedListViewHolder extends RecyclerView.ViewHolder{

        TextView productName;
        TextView price;
        TextView location;
        ImageView feedImage;
        CardView productCard;
        CheckBox likeBtn;


        public FeedListViewHolder(@NonNull View itemView) {
            super(itemView);
            productName=itemView.findViewById(R.id.productNameTextView);
            price=itemView.findViewById(R.id.priceTextView);
            feedImage=itemView.findViewById(R.id.feedImage);
            productCard= itemView.findViewById(R.id.productCard);
            location=itemView.findViewById(R.id.locationTextView);
            likeBtn=itemView.findViewById(R.id.likeBtn);

        }
    }
}
