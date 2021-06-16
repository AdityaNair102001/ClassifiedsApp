package com.phoenixcorp.classifiedsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    private String[] names;
    public ChatListAdapter(String[] names)
    {
        this.names=names;

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
