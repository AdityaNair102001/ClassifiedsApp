package com.phoenixcorp.classifiedsapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    RecyclerView ChatList;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    TextView exploreBtn;
    LinearLayout emptyChatLayout;
    CircularProgressIndicator progressBar;
    Users tempUser;

    ArrayList<Users> usersArrayList;

    ChatListAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        usersArrayList = new ArrayList<>();

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        emptyChatLayout = view.findViewById(R.id.empty_chat);
        exploreBtn = view.findViewById(R.id.explore_ads_button);

        progressBar = view.findViewById(R.id.chatProgress);
        progressBar.setVisibility(View.VISIBLE);

        ChatList = view.findViewById(R.id.ChatList);
        adapter = new ChatListAdapter(usersArrayList, this);

        exploreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), DefaultPageActivity.class));
            }
        });


        DocumentReference reference = firestore.collection("chats").document(auth.getUid());


        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public synchronized void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        CollectionReference ref = firestore.collection("chats").document(auth.getUid()).collection("messages sent to");

                        ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(!queryDocumentSnapshots.isEmpty()) {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        Users users = new Users(documentSnapshot.getId(), documentSnapshot.getString("receiverName"), "generic@gmail.com", documentSnapshot.getString("imageURI"), "7359102080");
                                        tempUser = users;
                                        Log.d("onSuccess : ", users.userName);
                                        if(!usersArrayList.contains(users))
                                            usersArrayList.add(users);
                                    }
                                    firestore.collection("chats").document(auth.getUid()).collection("messages received from").
                                            get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                                    Users users = new Users(queryDocumentSnapshot.getId(), queryDocumentSnapshot.getString("senderName"), "generic@gmail.com", queryDocumentSnapshot.getString("imageURI"), "7359102080");
//                                                    if(usersArrayList.contains(tempUser)) usersArrayList.remove(tempUser);
                                                    if (!usersArrayList.contains(tempUser)) {
                                                        usersArrayList.add(users);
                                                    }
                                                }
                                                progressBar.setVisibility(View.GONE);
                                                adapter.notifyDataSetChanged();
                                            }
                                            else{
                                                adapter.notifyDataSetChanged();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }
                                else{
                                    firestore.collection("chats").
                                            document(auth.getUid()).
                                            collection("messages received from").
                                            get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public synchronized void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                                                Users uusers = new Users(queryDocumentSnapshot.getId(), queryDocumentSnapshot.getString("senderName"), "generic@gmail.com", queryDocumentSnapshot.getString("imageURI"), "7359102080");
                                                usersArrayList.add(uusers);
                                                adapter.notifyDataSetChanged();
                                            }
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "No Chats Yet!", Toast.LENGTH_SHORT).show();
                        emptyChatLayout.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

        ChatList.setHasFixedSize(true);
        ChatList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        ChatList.setAdapter(adapter);

        return view;

    }

}