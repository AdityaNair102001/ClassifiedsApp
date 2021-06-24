package com.phoenixcorp.classifiedsapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class FeedListAdapter extends RecyclerView.Adapter<FeedListAdapter.FeedListViewHolder> {
    ArrayList<String> products;
    ArrayList<String> prices;
    ArrayList<String> UIDs;
    ArrayList<String> location;

    HashMap<String,String> names;
    HashMap<String,String> imageURLs;

    Fragment homeFragment;

    public FeedListAdapter( ArrayList<String>products,ArrayList<String> prices, HashMap<String,String> imageURLs, ArrayList<String> UIDs, ArrayList<String> location ,HashMap<String,String> names,HomeFragment homeFragment) {
        this.products=products;
        this.prices=prices;
        this.imageURLs=imageURLs;
        this.names=names;
        this.UIDs=UIDs;
        this.homeFragment=homeFragment;
        this.location=location;
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
        String url=imageURLs.get(productName);
        holder.productName.setText(productName);
        holder.location.setText(location.get(position));
        holder.price.setText(price);
        Picasso.get().load(url).placeholder(R.drawable.loader).into(holder.feedImage);

        holder.productCard.setOnClickListener(v -> {
            Intent intent = new Intent(homeFragment.getActivity(), ProductDescription.class);
            homeFragment.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class FeedListViewHolder extends RecyclerView.ViewHolder{

        TextView productName;
        TextView price;
        TextView location;
        ImageView feedImage;
        CardView productCard;


        public FeedListViewHolder(@NonNull View itemView) {
            super(itemView);
            productName=itemView.findViewById(R.id.productNameTextView);
            price=itemView.findViewById(R.id.priceTextView);
            feedImage=itemView.findViewById(R.id.feedImage);
            productCard= itemView.findViewById(R.id.productCard);
            location=itemView.findViewById(R.id.locationTextView);

        }
    }
}
