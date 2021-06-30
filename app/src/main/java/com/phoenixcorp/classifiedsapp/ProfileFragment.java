package com.phoenixcorp.classifiedsapp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

    static final String TAG = "MAIN_TAG";
    private TextView uName, uEmail, uPhone;
    private ImageView uImage, uEditImage, uEditName;

    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    StorageReference storageReference;
    DocumentReference documentReference;


    private Uri userImage;
    private String userID;
    private Button logout_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(getContext(), DefaultPageActivity.class));
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(callback);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        uImage = v.findViewById(R.id.UserImage);
        uEmail = v.findViewById(R.id.UserEmailText);
        uName = v.findViewById(R.id.UserNameText);
        uPhone = v.findViewById(R.id.UserPhoneText);
        logout_btn = v.findViewById(R.id.logout);
        uEditImage = v.findViewById(R.id.ChangeImage);
        uEditName = v.findViewById(R.id.ChangeUserName);

        String UserID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference documentReference = fStore.collection("users").document(UserID);

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if(Objects.requireNonNull(task.getResult()).exists()){
                            //Toast.makeText(getActivity(), "Data Exists!", Toast.LENGTH_SHORT).show();
                            String name_result = task.getResult().getString("username");
                            String phone_result = task.getResult().getString("phone");
                            String email_result = task.getResult().getString("email");
                            String imageURI_result = task.getResult().getString("imageURI");

                            Picasso.get().load(imageURI_result).into(uImage);
                            uName.setText(name_result);
                            uEmail.setText(email_result);
                            uPhone.setText(phone_result);
                        }
                        else{
                            Toast.makeText(getActivity(), "No Profile Exists.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getActivity(), "Check Your Internet Connection.", Toast.LENGTH_SHORT).show();
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout(view);
            }
        });

        uEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeProfile(view);
            }
        });

        uEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeName(view);
            }
        });
        // Inflate the layout for this fragment
        return v;
    }

    private void changeName(View view) {
        final EditText newName = new EditText(view.getContext());
        final AlertDialog.Builder changeNameDialog = new AlertDialog.Builder(view.getContext());
        changeNameDialog.setTitle("Change Name ?");
        changeNameDialog.setMessage("Enter Your New Name.");
        changeNameDialog.setView(newName);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        String UserID = fAuth.getCurrentUser().getUid();



        changeNameDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nName = newName.getText().toString();

                uName.setText(nName);

                documentReference = fStore.collection("users").document(UserID);

                documentReference.update("username", nName).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "Name Updated Successfully.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onSuccess: user Profile is created for " + UserID);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(getActivity(), "Name Was Not Updated.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: user Profile Update Failed " + UserID);
                    }
                });
            }
        });

        changeNameDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // close the dialog
            }
        });

        changeNameDialog.create().show();
    }

    private void changeProfile(View view) {

        if(ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
            return;

        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data!=null && data.getData()!=null){
                userImage = data.getData();
                Picasso.get().load(userImage)
                        .into(uImage);

                UploadImageToStorage(userImage);

        }
    }

    private void UploadImageToStorage(Uri imageURI) {
        ProgressDialog pd= new ProgressDialog(getActivity());
        pd.setMessage("Uploading Images");
        pd.show();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        String UserID = fAuth.getCurrentUser().getUid();

        StorageReference ImagesFolder = FirebaseStorage.getInstance().getReference().child("profile_images");
        StorageReference imageName= ImagesFolder.child("profile_image"+imageURI.getLastPathSegment());

        imageName.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(), "User Profile Image Added.", Toast.LENGTH_SHORT).show();

                imageName.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url= String.valueOf(uri);
                                Log.d("URL", "onSuccess : "+ url);

                                documentReference = fStore.collection("users").document(UserID);

                                documentReference.update("imageURI", url).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Images Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "onSuccess: user Profile is created for " + UserID);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getActivity(), "Sorry! Couldn't Store the Image.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getActivity(),LoginActivity.class));
        getActivity().finish();
    }


}