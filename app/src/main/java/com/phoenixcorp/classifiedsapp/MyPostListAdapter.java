package com.phoenixcorp.classifiedsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

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

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        final String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

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


        holder.deleteBtn.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(myPostsFragment.requireContext());
            builder.setTitle("Delete Post");
            builder.setMessage("Are you sure");
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    db.collection("users/"+currentUser+"/my posts").document(documentID.get(position)).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(myPostsFragment.getContext(),"Deleted from my posts",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    db.collection("posts").document(documentID.get(position)).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(myPostsFragment.getContext(),"Deleted from my posts",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    productNames.remove(position);
                    productDescriptions.remove(position);
                    prices.remove(position);
                    imageUrls.remove(documentID.get(position));
                    documentID.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,productNames.size());

//                    if(productNames.isEmpty()){
//
//                        noLikesLayout = likedPostsFragment.getActivity().findViewById(R.id.noLikes);
//                        exploreAds = likedPostsFragment.getView().findViewById(R.id.noLikes_explore_ads_button);
////                                    Toast.makeText(likedPostsFragment.getContext(),"No favorites yet", Toast.LENGTH_SHORT).show();
//                        noLikesLayout.setVisibility(View.VISIBLE);
//                        exploreAds.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                likedPostsFragment.startActivity(new Intent(likedPostsFragment.getContext(), DefaultPageActivity.class));
//                            }
//                        });
//                    }
                }
            });
            builder.show();
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
        Button deleteBtn;

        public MyPostListViewHolder(@NonNull View itemView) {
            super(itemView);
            productName=itemView.findViewById(R.id.myPostProductName);
            price=itemView.findViewById(R.id.myPostPrice);
            location=itemView.findViewById(R.id.myPostLocation);
            postImage=itemView.findViewById(R.id.myPostImage);
            deleteBtn=itemView.findViewById(R.id.myPostDelete);

        }
    }
}
