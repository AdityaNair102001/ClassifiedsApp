package com.phoenixcorp.classifiedsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

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
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    String receiverUID;

    int ITEM_SEND = 1, ITEM_RECEIVE = 2;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public MessageAdapter(Context context, ArrayList<Messages> messagesArrayList, String receiverUID) {
//        Collections.sort(messagesArrayList, Comparator.comparing(Messages::getTimeStamp));

        this.context = context;
        this.messagesArrayList = messagesArrayList;
        this.receiverUID = receiverUID;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
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
//        Log.d("Date time : ", TodaysDate);
//        Log.d("onBindViewHolder: ", MsgDate);
        if(TodaysDate.matches(MsgDate)){
            displayDate = "Today" + dateTime.substring(8,dateTime.length());
        }

        if(holder.getClass() == senderViewHolder.class)
        {
            senderViewHolder viewHolder = (senderViewHolder) holder;
            viewHolder.messageTxt.setText(messages.getChat());
            viewHolder.senderTime.setText(displayDate);

            Picasso.get().load(senderImg).into(viewHolder.circleImageView);

            viewHolder.sentChat.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    firestore.collection("chats").document(auth.getUid()).collection("messages sent to").document(receiverUID).collection("messages").whereEqualTo("timeStamp", messages.getTimeStamp()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            String sentmsgID = "";
                                            for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                                                if(snapshot.exists()) {
                                                    sentmsgID = snapshot.getId();
                                                    firestore.collection("chats").document(auth.getUid()).collection("messages sent to").document(receiverUID).collection("messages").document(sentmsgID).delete();
                                                    messagesArrayList.remove(position);
                                                    notifyItemRemoved(position);
                                                }
                                            }
                                            Log.d( "sent msgID : ", sentmsgID);
                                        }
                                    });
                                    firestore.collection("chats").document(receiverUID).collection("messages received from").document(auth.getUid()).collection("messages").whereEqualTo("timeStamp", messages.getTimeStamp()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            String receivedmsgID="";
                                            for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                                                if(snapshot.exists()) {
                                                    receivedmsgID = snapshot.getId();
                                                    firestore.collection("chats").document(receiverUID).collection("messages received from").document(auth.getUid()).collection("messages").document(receivedmsgID).delete();
                                                }
                                            }
                                            Log.d( "received msgID : ", receivedmsgID);
                                        }
                                    });
                                    firestore.collection("chats").document(auth.getUid()).collection("messages sent to").document(receiverUID).collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                                            if(value.isEmpty()){
                                                firestore.collection("chats").document(auth.getUid()).collection("messages sent to").document(receiverUID).delete();
                                                firestore.collection("chats").document(receiverUID).collection("messages received from").document(auth.getUid()).delete();
                                            }
                                        }
                                    });
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Unsend this message!");
                    builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                    return false;
                }
            });
        }
        else{
            receiverViewHolder viewHolder = (receiverViewHolder) holder;
            viewHolder.messageTxt.setText(messages.getChat());
            viewHolder.receiverTime.setText(displayDate);

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
        LinearLayout sentChat;

        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.senderProfile);
            messageTxt = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
            sentChat = itemView.findViewById(R.id.sentChat);

        }
    }

    class receiverViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView messageTxt;
        TextView receiverTime;
        LinearLayout receivedChat;

        public receiverViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.receiverProfile);
            messageTxt = itemView.findViewById(R.id.receiverText);
            receiverTime = itemView.findViewById(R.id.receiverTime);
            receivedChat = itemView.findViewById(R.id.receivedChat);

        }
    }
}
