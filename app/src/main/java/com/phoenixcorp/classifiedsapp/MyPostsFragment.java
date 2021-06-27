package com.phoenixcorp.classifiedsapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyPostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPostsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyPostsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyPostsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyPostsFragment newInstance(String param1, String param2) {
        MyPostsFragment fragment = new MyPostsFragment();
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

    RecyclerView myPostList;
    CircularProgressIndicator pd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_my_posts, container, false);

        myPostList=view.findViewById(R.id.myPosts);
        pd=view.findViewById(R.id.progressBarMyPosts);

        pd.setVisibility(View.INVISIBLE);

        ArrayList<String> productNamesFromDB= new ArrayList<>();
        ArrayList<String> pricesFromDB=new ArrayList<>();
        ArrayList<String> productDescriptionFromDB=new ArrayList<>();
        ArrayList<String> locationFromDB=new ArrayList<>();
        ArrayList<String> documentID = new ArrayList<>();

        HashMap<String, String> imageUrlsFromDB=new HashMap<>();
        HashMap<String,Boolean> likedPostsFromDB = new HashMap<>();

        String[] names={"Aaloo","Bhujia","Vada","Dosa","Chamanti","Porota"};

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        db.collection("users").document(currentUser).collection("my posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot: Objects.requireNonNull(task.getResult())){
                        productNamesFromDB.add(documentSnapshot.getString("productName"));
                        pricesFromDB.add(documentSnapshot.getString("price"));
                        productDescriptionFromDB.add(documentSnapshot.getString("productDescription"));
                        locationFromDB.add(documentSnapshot.getString("location"));
                        documentID.add(documentSnapshot.getId());

                        db.collection("users").document(currentUser).collection("my posts").document(documentSnapshot.getId()).collection("urls").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                        List<DocumentSnapshot> documentList=task.getResult().getDocuments();
                                        imageUrlsFromDB.put(documentSnapshot.getId(),documentList.get(documentList.size()-1).getString("url"));

                                        adapterHandler(productNamesFromDB,productDescriptionFromDB,pricesFromDB,locationFromDB,imageUrlsFromDB,documentID,likedPostsFromDB);
                                }
                            }
                        });

                    }
                }
            }
        });

        return view;
    }

    void adapterHandler(ArrayList<String> productNames,ArrayList<String>productDescription,ArrayList<String>prices,ArrayList<String>locations,HashMap<String,String>imageUrls,ArrayList<String>documentID,HashMap<String,Boolean> likedPosts){

        MyPostListAdapter adapter=new MyPostListAdapter(productNames,productDescription,prices,locations,imageUrls,documentID,likedPosts, this);
        myPostList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
        myPostList.setAdapter(adapter);
    }

}