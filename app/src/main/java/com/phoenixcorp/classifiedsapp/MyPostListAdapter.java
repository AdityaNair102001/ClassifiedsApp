package com.phoenixcorp.classifiedsapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MyPostListAdapter extends RecyclerView.Adapter<MyPostListAdapter.MyPostListViewHolder> {

    ArrayList<String> productNames;
    ArrayList<String> prices;
    ArrayList<String> productDescriptions;
    ArrayList<String> locations;
    ArrayList<String> documentID;

    HashMap<String, String> imageUrls;
    HashMap<String,Boolean> likedPosts;

    MyPostsFragment myPostsFragment;

    public MyPostListAdapter(ArrayList<String> productNames,ArrayList<String>productDescriptions,ArrayList<String>prices,ArrayList<String>locations,HashMap<String,String>imageUrls,ArrayList<String>documentID,HashMap<String,Boolean> likedPosts, MyPostsFragment myPostsFragment){
        this.productNames=productNames;
        this.prices=prices;
        this.productDescriptions=productDescriptions;
        this.locations=locations;
        this.imageUrls=imageUrls;
        this.documentID=documentID;
        this.likedPosts=likedPosts;
        this.myPostsFragment = myPostsFragment;
    }

    public MyPostListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.myposts_post_layout,parent,false);
        return new MyPostListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostListViewHolder holder, int position) {
        holder.productName.setText(productNames.get(position));
        holder.price.setText("₹"+prices.get(position));
        holder.location.setText(locations.get(position));
        Picasso.get().load(imageUrls.get(documentID.get(position))).placeholder(R.drawable.loader).into(holder.postImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(myPostsFragment.getContext(), MyAdsProductDescriptionActivity.class);
                intent.putExtra("Product Name", productNames.get(position));
                intent.putExtra("Product Price", "₹"+prices.get(position));
                intent.putExtra("Document ID", documentID.get(position));
                intent.putExtra("Product Location", locations.get(position));
                myPostsFragment.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productNames.size();
    }

    public class MyPostListViewHolder extends RecyclerView.ViewHolder {

        TextView productName;
        TextView price;
        TextView location;

        ImageView postImage;
        CheckBox likeBtn;

        public MyPostListViewHolder(@NonNull View itemView) {
            super(itemView);
            productName=itemView.findViewById(R.id.myPostProductName);
            price=itemView.findViewById(R.id.myPostPrice);
            location=itemView.findViewById(R.id.myPostLocation);
            postImage=itemView.findViewById(R.id.myPostImage);

        }
    }
}
