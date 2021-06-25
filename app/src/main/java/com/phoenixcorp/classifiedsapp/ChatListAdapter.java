package com.phoenixcorp.classifiedsapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    private ArrayList<Users> names;
    Fragment chatFragment;
    public ChatListAdapter(ArrayList<Users> names, ChatFragment chatfrag)
    {
        this.names = names;
        this.chatFragment = chatfrag;
    }


    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.chatlist_chat_layout,parent,false);
        return new ChatListViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ChatListViewHolder holder, int position) {

        Users users = names.get(position);
        String receiverUid = users.getUid();
        FirebaseFirestore.getInstance().collection("users").document(receiverUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String receiverImg = documentSnapshot.getString("imageURI");
                    String receiverNem = documentSnapshot.getString("username");
                    Picasso.get().load(receiverImg).into(holder.circleImageView);
                    holder.name.setText(receiverNem);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(chatFragment.getActivity(), ChatActivity.class);
                            intent.putExtra("name", receiverNem);
                            intent.putExtra("ReceiverImage", receiverImg);
                            intent.putExtra("UID", receiverUid);
                            chatFragment.startActivity(intent);
                        }
                    });

                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder{

        ImageView circleImageView;
        TextView name;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView=itemView.findViewById(R.id.ProfilePicture);
            name=itemView.findViewById(R.id.name);

        }
    }


}
