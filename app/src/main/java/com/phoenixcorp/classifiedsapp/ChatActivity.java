package com.phoenixcorp.classifiedsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.collection.CircularArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Message;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        CollectionReference chatReference = firestore.collection("Newchats").document(firebaseAuth.getUid()).collection("messages sent to");

        chatReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    Messages message = documentSnapshot.toObject(Messages.class);
                    messagesArrayList.add(message);
                }
                adapter.notifyDataSetChanged();
            }
        });

        userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                senderImg = documentSnapshot.getString("imageURI");
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
                firestore.collection("Newchats").document(senderUID).set(m);
                firestore.collection("Newchats").document(senderUID).collection("messages sent to").document(receiverUID).set(m);


                firestore.
                        collection("Newchats").
                        document(senderUID).
                        collection("messages sent to").document(receiverUID).collection("messages").
                        add(m).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(ChatActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                            firestore.collection("Newchats").document(receiverUID).set(m);
                            firestore.collection("Newchats").document(receiverUID).collection("messages received from").document(senderUID).set(m);

                            firestore.collection("Newchats").
                                    document(receiverUID).
                                    collection("messages received from").document(senderUID).collection("messages").
                                    add(m).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
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