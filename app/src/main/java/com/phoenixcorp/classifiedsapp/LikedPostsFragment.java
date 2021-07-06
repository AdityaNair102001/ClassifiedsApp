package com.phoenixcorp.classifiedsapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LikedPostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LikedPostsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LikedPostsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LikedPostsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LikedPostsFragment newInstance(String param1, String param2) {
        LikedPostsFragment fragment = new LikedPostsFragment();
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

    RecyclerView likedPostList;
    CircularProgressIndicator pd;

    LinearLayout noLikesLayout;
    TextView exploreAds;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_liked_posts, container, false);

        ArrayList<String> productNamesFromDB=new ArrayList<>();;
        ArrayList<String> pricesFromDB=new ArrayList<>();;
        ArrayList<String> productDescriptionsFromDB=new ArrayList<>();;
        ArrayList<String> locationsFromDB=new ArrayList<>();;
        ArrayList<String> documentIDFromDB=new ArrayList<>();;

        HashMap<String, String> imageUrlsFromDB = new HashMap<>();
        HashMap<String,Boolean> likedPostsFromDB = new HashMap<>();

        likedPostList=view.findViewById(R.id.likedPosts);
        pd=view.findViewById(R.id.progressBarLikedPosts);

        noLikesLayout = view.findViewById(R.id.noLikes);
        exploreAds = view.findViewById(R.id.noLikes_explore_ads_button);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        db.collection("users").document(currentUser).collection("liked posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void  onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                   for(DocumentSnapshot documentSnapshot: Objects.requireNonNull(task.getResult())){

                       productNamesFromDB.add(documentSnapshot.getString("productName"));
                       pricesFromDB.add(documentSnapshot.getString("price"));
                       productDescriptionsFromDB.add(documentSnapshot.getString("productDescription"));
                       locationsFromDB.add(documentSnapshot.getString("location"));
                       documentIDFromDB.add(documentSnapshot.getId());

                       db.collection("users").document(currentUser).collection("liked posts").document(documentSnapshot.getId()).collection("urls").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                               if(task.isSuccessful()){
                                   List<DocumentSnapshot> documentList= Objects.requireNonNull(task.getResult()).getDocuments();

                                   imageUrlsFromDB.put(documentSnapshot.getId(),documentList.get(documentList.size()-1).getString("url"));

                                   adapterHandler(productNamesFromDB,productDescriptionsFromDB,pricesFromDB,locationsFromDB,imageUrlsFromDB,documentIDFromDB,likedPostsFromDB);
                               }
                           }
                       });

                   }

                    if(documentIDFromDB.isEmpty()){
                        noLikesLayout.setVisibility(View.VISIBLE);
                        exploreAds.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(getContext(), DefaultPageActivity.class));
                            }
                        });
                        pd.setVisibility(View.GONE);
                    }

                }

            }
        });

        db.collection("users").document(currentUser).collection("liked posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot: Objects.requireNonNull(task.getResult())){
                        likedPostsFromDB.put(documentSnapshot.getId(),true);
                    }
                }
            }
        });

        return view;
    }

    void adapterHandler(ArrayList<String> productNames,ArrayList<String>productDescription,ArrayList<String>prices,ArrayList<String>locations,HashMap<String,String>imageUrls,ArrayList<String>documentID,HashMap<String,Boolean> likedPosts){

        pd.setVisibility(View.INVISIBLE);

        Log.e("url", "adapterHandler: "+imageUrls );

        LikedPostListAdapter adapter=new LikedPostListAdapter(productNames,productDescription,prices,locations,imageUrls,documentID,likedPosts,this);
        likedPostList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
        likedPostList.setAdapter(adapter);
    }
}