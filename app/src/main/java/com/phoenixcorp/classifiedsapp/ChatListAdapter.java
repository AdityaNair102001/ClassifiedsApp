package com.phoenixcorp.classifiedsapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    private String[] names;
    Fragment chatFragment;
    public ChatListAdapter(String[] names, ChatFragment chatfrag)
    {
        this.names=names;
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

        String name=names[position];
//        String loc=location[position];
        holder.name.setText(name);
//        holder.secondaryText.setText(loc);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(chatFragment.getActivity(), ChatActivity.class);
//                intent.putExtra("name", users.getName());
//                intent.putExtra("ReceiverImage", users.getImageURI());
//                intent.putExtra("UID", users.getuID());
                chatFragment.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return names.length;
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
