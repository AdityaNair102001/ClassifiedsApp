package com.phoenixcorp.classifiedsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.collection.CircularArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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

    RecyclerView messageAdapter;

    CardView sendBtn;
    EditText chatMsg;

    ArrayList<Messages> messagesArrayList;
    ArrayList <Messages> temp;

    MessageAdapter adapter;

    CircularProgressIndicator progressBar;

    RelativeLayout relativeLayout;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        relativeLayout = findViewById(R.id.ChatActivityLayout);
        relativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                relativeLayout.getWindowVisibleDisplayFrame(r);
                int screenHeight = relativeLayout.getRootView().getHeight();
                int keyPadHeight = screenHeight - r.bottom;
                if(keyPadHeight > screenHeight * 0.15) {
                    messageAdapter.smoothScrollToPosition(adapter.getItemCount());
                }
                else{
                    messageAdapter.smoothScrollToPosition(adapter.getItemCount());
                }
            }
        });

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

        messageAdapter = findViewById(R.id.messageAdapter);
        messagesArrayList = new ArrayList<Messages>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        messageAdapter.setLayoutManager(linearLayoutManager);
        adapter = new MessageAdapter(ChatActivity.this, messagesArrayList, receiverUID);

        messageAdapter.setAdapter(adapter);

        progressBar = findViewById(R.id.loadMsgs);
        progressBar.setVisibility(View.VISIBLE);
        temp = new ArrayList<>();


        DocumentReference userReference = firestore.collection("users").document(firebaseAuth.getUid());
        CollectionReference chatSentReference = firestore.collection("chats").document(firebaseAuth.getUid()).collection("messages sent to").document(receiverUID).collection("messages");
        CollectionReference chatReceivedReference = firestore.collection("chats").document(firebaseAuth.getUid()).collection("messages received from").document(receiverUID).collection("messages");

        chatSentReference.orderBy("timeStamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if(!value.isEmpty()){
                    messagesArrayList.clear();
                    for (QueryDocumentSnapshot documentSnapshot : value) {
                        Messages message = documentSnapshot.toObject(Messages.class);
                            messagesArrayList.add(message);
                    }
                }
                chatReceivedReference.orderBy("timeStamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        if(!value.isEmpty()){
                            messagesArrayList.removeAll(temp);
                            temp.clear();
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                Messages message = documentSnapshot.toObject(Messages.class);
                                temp.add(message);
                            }
                            messagesArrayList.addAll(temp);
                            Collections.sort(messagesArrayList, Comparator.comparing(Messages::getTimeStamp));
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
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

                String chat = chatMsg.getText().toString().trim();
                if(chat.isEmpty()){
                    Toast.makeText(ChatActivity.this, "Please Enter some Message", Toast.LENGTH_SHORT).show();
                    return;
                }
                chatMsg.setText("");
                Date date = new Date();
                long timeStamp = System.currentTimeMillis();

                Messages m = new Messages(receiverName, receiverImg, chat, senderUID, timeStamp);
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
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("chat", chat);
//                            Log.d("Time : ", ""+timeStamp);
                            if(buyerName!=null) {
                                map.put("imageURI", buyerImage);
                                map.put("senderName", buyerName);
                            }
                            else {
                                map.put("imageURI", senderImg);
                                map.put("senderName", senderName);
                            }
                            map.put("timeStamp", timeStamp);
                            map.put("senderID", firebaseAuth.getUid());
//                            Log.d("onComplete: " , "" + map);
                            firestore.collection("chats").document(receiverUID).set(map);
                            firestore.collection("chats").document(receiverUID).collection("messages received from").document(senderUID).set(map);

                            firestore.collection("chats").
                                    document(receiverUID).
                                    collection("messages received from").document(senderUID).collection("messages").
                                    add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ChatActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
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