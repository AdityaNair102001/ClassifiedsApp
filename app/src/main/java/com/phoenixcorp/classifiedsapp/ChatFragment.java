package com.phoenixcorp.classifiedsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.ContentView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();




        int flag = 0;
        if(flag == 1)
        {
            View v = inflater.inflate(R.layout.empty_chat_layout, container, false);
            exploreBtn = (TextView) v.findViewById(R.id.explore_ads_button);
            exploreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), DefaultPageActivity.class));

                }
            });
            return v;
        }
        else {

            View view = inflater.inflate(R.layout.fragment_chat, container, false);
            ChatList = view.findViewById(R.id.ChatList);
            ChatList.setHasFixedSize(true);
            ChatList.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));

            String[] names = {"Labrador", "Pomerian", "Pug", "Pitbull", "Stray", "Indian", "Golden Retriever", "Husky", "Labrador", "Husky", "Indian", "Pug", "Stray"};
//        String[] loacation={"Kalyan","Ulhasnagar","Kharghar","CST","Dadar","Thane","Kurla","Andheri","Dombivili","Kalyan","Khadakpada","Radha Nagar","Scion"};
            ChatList.setAdapter(new ChatListAdapter(names, ChatFragment.this));


            return view;

        }

    }
}