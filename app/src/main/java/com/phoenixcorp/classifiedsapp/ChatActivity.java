package com.phoenixcorp.classifiedsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.collection.CircularArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String receiverImage, receiverUID, receiverName;
    String senderUID;
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

    ChatListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        receiverImage = getIntent().getStringExtra("ReceiverImage");
        receiverName = getIntent().getStringExtra("name");
        receiverUID = getIntent().getStringExtra("UID");

        profileImg = findViewById(R.id.profile_image);

        Picasso.get().load(receiverImage).into(profileImg);
        receivername = findViewById(R.id.receiverName);
        receivername.setText("" + receiverName);

        sendBtn = findViewById(R.id.sendBtn);
        chatMsg = findViewById(R.id.chatMessage);

        senderUID = firebaseAuth.getUid();
        senderRoom = senderUID + receiverUID;
        receiverRoom = receiverUID + senderUID;

        messageAdapter = findViewById(R.id.messageAdapter);
        messagesArrayList = new ArrayList<Messages>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
//        adapter = new ChatListAdapter(ChatActivity.this, messagesArrayList);
        messageAdapter.setLayoutManager(linearLayoutManager);

        messageAdapter.setAdapter(adapter);


    }
}