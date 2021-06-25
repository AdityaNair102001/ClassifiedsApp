package com.phoenixcorp.classifiedsapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.collection.CircularArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "";
    String receiverImage, receiverUID, receiverName;
    String buyerUID, buyerName, buyerImage;
    String senderUID, senderName;
    CircleImageView profileImg;
    TextView receivername;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    public static String senderImg;
    public static String receiverImg;

    String senderRoom="", receiverRoom="";

    RecyclerView messageAdapter;

    CardView sendBtn;
    EditText chatMsg;

    ArrayList<Messages> messagesArrayList;

    MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        receiverImage = getIntent().getStringExtra("ReceiverImage");
        receiverName = getIntent().getStringExtra("name");
        receiverUID = getIntent().getStringExtra("UID");
        buyerImage = getIntent().getStringExtra("BuyerUri");
        buyerName = getIntent().getStringExtra("BuyerName");
        buyerUID = getIntent().getStringExtra("BuyerUID");


        Log.d("onCreate: ", receiverImage);
        Log.d("onCreate: ", receiverName);
//        Log.d("onCreate: ", receiverUID);
//        Log.d("onCreate: ", buyerImage);
//        Log.d("onCreate: ", buyerName);
//        Log.d("onCreate: ", buyerUID);


        profileImg = findViewById(R.id.profile_image);

        Picasso.get().load(receiverImage).into(profileImg);
        receivername = findViewById(R.id.receiverName);
        receivername.setText("" + receiverName);

        sendBtn = findViewById(R.id.sendBtn);
        chatMsg = findViewById(R.id.chatMessage);

        senderUID = firebaseAuth.getCurrentUser().getUid();

        senderRoom = senderUID + receiverUID;
        receiverRoom = receiverUID + senderUID;

        messageAdapter = findViewById(R.id.messageAdapter);
        messagesArrayList = new ArrayList<Messages>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        adapter = new MessageAdapter(ChatActivity.this, messagesArrayList);
        messageAdapter.setLayoutManager(linearLayoutManager);

        messageAdapter.setAdapter(adapter);

        DocumentReference userReference = firestore.collection("users").document(firebaseAuth.getUid());
        CollectionReference chatSentReference = firestore.collection("chats").document(firebaseAuth.getUid()).collection("messages sent to").document(receiverUID).collection("messages");
        CollectionReference chatReceivedReference = firestore.collection("chats").document(firebaseAuth.getUid()).collection("messages received from").document(receiverUID).collection("messages");

        chatSentReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()) {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Messages message = documentSnapshot.toObject(Messages.class);
//                        if (!messagesArrayList.contains(message))
                        messagesArrayList.add(message);
                        adapter.notifyDataSetChanged();

                    }
                }
            }
        });
        chatReceivedReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        Messages message = documentSnapshot.toObject(Messages.class);
                        if (!messagesArrayList.contains(message))
                            messagesArrayList.add(message);
                    }
//                    messagesArrayList.sort(Comparator.comparing(Messages::getTimeStamp));
                    Collections.sort(messagesArrayList, Comparator.comparing(Messages::getTimeStamp));
                    adapter.notifyDataSetChanged();
                }
            }
        });

        userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                senderImg = documentSnapshot.getString("imageURI");
                senderName = documentSnapshot.getString("username");
                receiverImg = receiverImage;

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chat = chatMsg.getText().toString();
                chat.trim();
                if(chat.isEmpty()){
                    Toast.makeText(ChatActivity.this, "Please Enter some Message", Toast.LENGTH_SHORT).show();
                    return;
                }
                chatMsg.setText("");
                Date date = new Date();
                Messages m = new Messages(receiverName, receiverImage, chat, senderUID, date.getTime());
                firestore.collection("chats").document(senderUID).set(m);
                firestore.collection("chats").document(senderUID).collection("messages sent to").document(receiverUID).set(m);


                firestore.
                        collection("chats").
                        document(senderUID).
                        collection("messages sent to").document(receiverUID).collection("messages").
                        add(m).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(ChatActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                            messagesArrayList.add(m);
//                            messagesArrayList.sort(Comparator.comparing(Messages::getTimeStamp));
                            Collections.sort(messagesArrayList, Comparator.comparing(Messages::getTimeStamp));
                            adapter.notifyDataSetChanged();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("chat", chat);
                            if(buyerName!=null) {
                                map.put("imageURI", buyerImage);
                                map.put("senderName", buyerName);
                            }
                            else {
                                map.put("imageURI", senderImg);
                                map.put("senderName", senderName);
                            }
                            map.put("timeStamp", date.getTime());
                            map.put("senderID", firebaseAuth.getUid());
                            firestore.collection("chats").document(receiverUID).set(map);
                            firestore.collection("chats").document(receiverUID).collection("messages received from").document(senderUID).set(map);

                            firestore.collection("chats").
                                    document(receiverUID).
                                    collection("messages received from").document(senderUID).collection("messages").
                                    add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ChatActivity.this, "Message received", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        }
                        else
                            Toast.makeText(ChatActivity.this, "ERROR", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

    }
}