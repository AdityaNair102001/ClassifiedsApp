package com.phoenixcorp.classifiedsapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText mUsername, mEmail, mPassword, mPhone;
    Button mSignupBtn, mLoginBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    String passwordVer = "^" +
            "(?=.*[a-zA-Z])" +       // any letter
            "(?=.*[@#$%^&+=])" +     // at least 1 special character
            "(?=\\S+$)" +            // no white spaces
            ".{6,}" +                // at least 4 characters
            "$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mUsername = findViewById(R.id.Username1);
        mEmail = findViewById(R.id.Email1);
        mPassword = findViewById(R.id.Password1);
        mPhone = findViewById(R.id.Phone1);
        mSignupBtn = findViewById(R.id.SignUp);
        mLoginBtn = findViewById(R.id.LogIn);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String username = mUsername.getText().toString();
                final String phone    = "+91" + mPhone.getText().toString().trim();
                final String default_URI = "https://firebasestorage.googleapis.com/v0/b/fir-project-2b9f4.appspot.com/o/displaypicture.png?alt=media&token=007775cd-0561-46a9-b812-e3b8d0573346";

                if(username.isEmpty()){
                    mUsername.setError("Field Cannot be Empty.");
                    return;
                }
                if(email.isEmpty()){
                    mEmail.setError("Field Cannot be Empty.");
                    return;
                }
                if(password.isEmpty()){
                    mPassword.setError("Field Cannot be Empty.");
                    return;
                }
                if(phone.isEmpty()){
                    mPhone.setError("Field Cannot be Empty.");
                    return;
                }
                if(phone.length() < 10){
                    mPhone.setError("Enter a 10-Digit Phone Number.");
                    return;
                }
                if(!email.matches(emailPattern)){
                    mEmail.setError("Invalid Email Address.");
                    return;
                }
                if(!password.matches(passwordVer)){
                    mPassword.setError("Password is Too Weak : \nMust Have 6 Characters\n1 Special Character\nNo Blank Spaces");
                    return;
                }

                //Register User in the FireBase Database By their Email & Password
                fAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        Toast.makeText(SignupActivity.this, "User Created By Their Email.", Toast.LENGTH_SHORT).show();

//                      Move to Verify Phone Number And store user details after the Phone Verification
                        Intent intent = new Intent(SignupActivity.this, VerifyPhoneNumber.class);
                        intent.putExtra("Email", email);
                        intent.putExtra("Name", username);
                        intent.putExtra("ImgURI", default_URI);
                        intent.putExtra("phoneNo", phone);
                        startActivity(intent);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(SignupActivity.this, "Error! Check Your Internet Connection.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication(), LoginActivity.class));
            }
        });

    }
}