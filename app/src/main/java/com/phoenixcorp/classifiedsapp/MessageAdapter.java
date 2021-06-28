package com.phoenixcorp.classifiedsapp;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.phoenixcorp.classifiedsapp.ChatActivity.receiverImg;
import static com.phoenixcorp.classifiedsapp.ChatActivity.senderImg;

public class MessageAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Messages> messagesArrayList;

    int ITEM_SEND = 1, ITEM_RECEIVE = 2;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public MessageAdapter(Context context, ArrayList<Messages> messagesArrayList) {
        Collections.sort(messagesArrayList, Comparator.comparing(Messages::getTimeStamp));

        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_chat_layout, parent, false);
            return new senderViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_chat_layout, parent, false);
            return new receiverViewHolder(view);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Messages messages = messagesArrayList.get(position);
        long timestmp = messages.timeStamp;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestmp);
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy hh:mm aa");
        SimpleDateFormat todaysFormat = new SimpleDateFormat("dd/MM/yy");
        String dateTime = sdf.format(timestmp);
        String TodaysDate = todaysFormat.format(new Date());
        String MsgDate = dateTime.substring(0,8);
        String displayDate = dateTime;
        Log.d("Date time : ", TodaysDate);
        Log.d("onBindViewHolder: ", MsgDate);
        if(TodaysDate.matches(MsgDate)){
            displayDate = "Today" + dateTime.substring(8,dateTime.length());
        }

        if(holder.getClass() == senderViewHolder.class)
        {
            senderViewHolder viewHolder = (senderViewHolder) holder;
            viewHolder.messageTxt.setText(messages.getChat());
            viewHolder.senderTime.setText(displayDate);

            Picasso.get().load(senderImg).into(viewHolder.circleImageView);
        }
        else{
            receiverViewHolder viewHolder = (receiverViewHolder) holder;
            viewHolder.messageTxt.setText(messages.getChat());
            viewHolder.receiverTime.setText(displayDate

            );

            Picasso.get().load(receiverImg).into(viewHolder.circleImageView);
        }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages = messagesArrayList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderID()))
            return ITEM_SEND;
        else
            return ITEM_RECEIVE;

    }

    class senderViewHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView messageTxt;
        TextView senderTime;
        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.senderProfile);
            messageTxt = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);

        }
    }

    class receiverViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView messageTxt;
        TextView receiverTime;

        public receiverViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.receiverProfile);
            messageTxt = itemView.findViewById(R.id.receiverText);
            receiverTime = itemView.findViewById(R.id.receiverTime);

        }
    }
}
