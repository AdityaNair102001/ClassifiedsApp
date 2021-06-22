package com.phoenixcorp.classifiedsapp;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Map;

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


        DocumentReference reference = firestore.collection("Newchats").document(auth.getUid());

//        if(reference.collection("messages sent to").equals(null)){
//            View v = inflater.inflate(R.layout.empty_chat_layout, container, false);
//            exploreBtn = (TextView) v.findViewById(R.id.explore_ads_button);
//            exploreBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    startActivity(new Intent(getActivity(), DefaultPageActivity.class));
//                }
//            });
//
//            return v;
//        }
//        else {



        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        CollectionReference ref = firestore.collection("Newchats").document(auth.getUid()).collection("messages sent to");

                        ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(!queryDocumentSnapshots.isEmpty()) {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        Users users = new Users(documentSnapshot.getId(), documentSnapshot.getString("receiverName"), "generic@gmail.com", documentSnapshot.getString("imageURI"), "7359102080");
                                        Log.d("onSuccess : ", users.userName);
                                        usersArrayList.add(users);
                                    }
                                    firestore.collection("Newchats").document(auth.getUid()).collection("messages received from").
                                            get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                                                Users users = new Users(queryDocumentSnapshot.getId(), queryDocumentSnapshot.getString("receiverName"), "generic@gmail.com", queryDocumentSnapshot.getString("imageURI"), "7359102080");
                                                if(usersArrayList.contains(users) == true) {
                                                    usersArrayList.add(users);
                                                }
                                            }
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                                else{
                                    firestore.collection("Newchats").
                                            document(auth.getUid()).
                                            collection("messages received from").
                                            get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                                                Users users = new Users(queryDocumentSnapshot.getId(), queryDocumentSnapshot.getString("receiverName"), "generic@gmail.com", queryDocumentSnapshot.getString("imageURI"), "7359102080");
                                                    usersArrayList.add(users);
                                            }
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "list is empty", Toast.LENGTH_SHORT).show();
//                            Toast.makeText(getContext(), reference.collection("messages sent to").toString(), Toast.LENGTH_SHORT).show();

                    }
                }

            }
        });

//        firestore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
//                    Users users = new Users(queryDocumentSnapshot.getId(), queryDocumentSnapshot.getString("username"), "generic@gmail.com", queryDocumentSnapshot.getString("imageURI"), "7359102080");
////                    if(!usersArrayList.contains(users))
//                        usersArrayList.add(users);
//                }
//                adapter.notifyDataSetChanged();
//            }
//        });


        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        ChatList = view.findViewById(R.id.ChatList);
        ChatList.setHasFixedSize(true);
        ChatList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        adapter = new ChatListAdapter(usersArrayList, this);
        ChatList.setAdapter(adapter);

        return view;



//        int flag = 0;
//
//        if(flag == 5){
//            View v = inflater.inflate(R.layout.fragment_chat, container, false);
//            ChatList = v.findViewById(R.id.ChatList);
//            ChatList.setHasFixedSize(true);
//            ChatList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//
//            ArrayList<Users> names = new ArrayList<>(); //= {"Labrador", "Pomerian", "Pug", "Pitbull", "Stray", "Indian", "Golden Retriever", "Husky", "Labrador", "Husky", "Indian", "Pug", "Stray"};
////            names.add("1En97XiEz8g1lfwpm1KWKs0SVtj2", "Elon Musk", "anuragpatil134@gmail.com", "https://firebasestorage.googleapis.com/v0/b/mini-project--ii.appspot.com/o/displaypicture.png?alt=media&token=c371684a-8c1b-4988-b2e7-251cac680fc5");
//            ChatList.setAdapter(new ChatListAdapter(names, ChatFragment.this));
//
//            return v;
//        }
//        else{
//            View v = inflater.inflate(R.layout.empty_chat_layout, container, false);
//            exploreBtn = (TextView) v.findViewById(R.id.explore_ads_button);
//            exploreBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    startActivity(new Intent(getActivity(), DefaultPageActivity.class));
//
//                }
//            });
//
//            return v;
//        }

    }
}