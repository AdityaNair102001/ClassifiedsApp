package com.phoenixcorp.classifiedsapp;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class LikedPostListAdapter extends RecyclerView.Adapter<LikedPostListAdapter.LikedPostListViewHolder> {

    ArrayList<String> productNames;
    ArrayList<String> prices;
    ArrayList<String> productDescriptions;
    ArrayList<String> locations;
    ArrayList<String> documentID;

    HashMap<String, String> imageUrls;
    HashMap<String,Boolean> likedPosts;

    LikedPostsFragment likedPostsFragment;
    LinearLayout noLikesLayout;
    TextView exploreAds;

    public LikedPostListAdapter(ArrayList<String> productNames,ArrayList<String>productDescriptions,ArrayList<String>prices,ArrayList<String>locations,HashMap<String,String>imageUrls,ArrayList<String>documentID,HashMap<String,Boolean> likedPosts,LikedPostsFragment likedPostsFragment) {
        this.productNames=productNames;
        this.prices=prices;
        this.productDescriptions=productDescriptions;
        this.locations=locations;
        this.imageUrls=imageUrls;
        this.documentID=documentID;
        this.likedPosts=likedPosts;
        this.likedPostsFragment=likedPostsFragment;
    }

    @NonNull
    @Override
    public LikedPostListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.likedposts_post_layout,parent,false);

        return new LikedPostListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikedPostListViewHolder holder, int position) {

        holder.productName.setText(productNames.get(position));
        holder.price.setText(prices.get(position));
        holder.location.setText(locations.get(position));
        Picasso.get().load(imageUrls.get(documentID.get(position))).placeholder(R.drawable.loader).into(holder.postImage);

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        final String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        String docID=documentID.get(position);

        if(likedPosts.get(docID)!=null && likedPosts.get(docID)){
            holder.likeBtn.setChecked(true);
        }

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.likeBtn.isChecked()) {
                    db.collection("users/" + currentUser + "/liked posts").document(docID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(likedPostsFragment.getActivity(), "Unliked!", Toast.LENGTH_SHORT).show();
                                productNames.remove(position);
                                productDescriptions.remove(position);
                                prices.remove(position);
                                imageUrls.remove(docID);
                                documentID.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position,productNames.size());

                                if(productNames.isEmpty()){

                                    noLikesLayout = likedPostsFragment.getActivity().findViewById(R.id.noLikes);
                                    exploreAds = likedPostsFragment.getView().findViewById(R.id.noLikes_explore_ads_button);
//                                    Toast.makeText(likedPostsFragment.getContext(),"No favorites yet", Toast.LENGTH_SHORT).show();
                                    noLikesLayout.setVisibility(View.VISIBLE);
                                    exploreAds.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            likedPostsFragment.startActivity(new Intent(likedPostsFragment.getContext(), DefaultPageActivity.class));
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(likedPostsFragment.getContext(), ProductDescription.class);
                intent.putExtra("Product Name", productNames.get(position));
                intent.putExtra("Product Price", prices.get(position));
                intent.putExtra("Seller UID", docID.substring(13, docID.length()));
                intent.putExtra("Document ID", docID);
                intent.putExtra("Product Location", locations.get(position));
                likedPostsFragment.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return productNames.size();
    }

    public static class LikedPostListViewHolder extends RecyclerView.ViewHolder {

        TextView productName;
        TextView price;
        TextView location;

        ImageView postImage;
        CheckBox likeBtn;


        public LikedPostListViewHolder(@NonNull View itemView) {
            super(itemView);

            productName=itemView.findViewById(R.id.likedPostProductName);
            price=itemView.findViewById(R.id.likedPostPrice);
            location=itemView.findViewById(R.id.likedPostLocation);
            postImage=itemView.findViewById(R.id.likedPostImage);
            likeBtn=itemView.findViewById(R.id.likedPostLikeBtn);

        }
    }
}
