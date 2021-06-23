package com.phoenixcorp.classifiedsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneNumber extends AppCompatActivity {

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    PhoneAuthProvider.ForceResendingToken forceResendingToken;

    static final String TAG = "MAIN_TAG";
    PinView VerificationCode;
    Button verifyBtn, resendBtn;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    DocumentReference documentReference;

    String UserID, userID;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number);

        fAuth = FirebaseAuth.getInstance();

        //progress dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);

        resendBtn = findViewById(R.id.ResendBtn);
        verifyBtn = findViewById(R.id.VerifyBtn);

        resendBtn.setVisibility(View.INVISIBLE);

        String phone = getIntent().getStringExtra("phoneNo");

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NotNull PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NotNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                resendBtn.setVisibility(View.VISIBLE);

                pd.dismiss();
                Toast.makeText(VerifyPhoneNumber.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                UserID = verificationId;
                forceResendingToken = token;
                pd.dismiss();

                resendBtn.setVisibility(View.INVISIBLE);

                Toast.makeText(VerifyPhoneNumber.this, "Verification Code Sent...", Toast.LENGTH_SHORT).show();
            }
        };

        startPhoneNumberVerification(phone);

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VerificationCode = findViewById(R.id.Verification_Code);
                String code = VerificationCode.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(VerifyPhoneNumber.this, "Please Enter the OTP.", Toast.LENGTH_SHORT).show();
                } else {
                    verifyPhoneNumberWithCode(UserID, code);
                }

            }
        });

        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = getIntent().getStringExtra("phoneNo");
                resendVerificationCode(phone, forceResendingToken);

            }
        });
    }

    private void startPhoneNumberVerification(String phone) {
        pd.setMessage("Verifying Phone Number.");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(fAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(30L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendVerificationCode(String phone, PhoneAuthProvider.ForceResendingToken token) {
        pd.setMessage("Sending You a New Code.");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(fAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(30L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyPhoneNumberWithCode(String userID, String code) {
        pd.setMessage("Verifying Your Code.");
        pd.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(userID, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        pd.setMessage("Logging In");
        pd.show();

        String phone = getIntent().getStringExtra("phoneNo");
        String email = getIntent().getStringExtra("Email");
        String username = getIntent().getStringExtra("Name");
        String default_URI = getIntent().getStringExtra("ImgURI");

        fStore = FirebaseFirestore.getInstance();

        fAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "linkWithCredential:success" + fAuth.getCurrentUser().getUid());
                    Log.d(TAG, "User Has been Verified 1." + fAuth.getCurrentUser().getUid());

                    //Toast.makeText(VerifyPhoneNumber.this, "User Has been Verified."+fAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();

                    userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                    Log.d(TAG, "User Has been Verified 2." + fAuth.getCurrentUser().getUid());
                    documentReference = fStore.collection("users").document(userID);
                    Map<String, Object> user = new HashMap<>();
                    user.put("username", username);
                    user.put("email", email);
                    user.put("phone", phone);
                    user.put("imageURI", default_URI);
                    Log.d(TAG, "User Has been Verified 3." + fAuth.getCurrentUser().getUid());
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(VerifyPhoneNumber.this, "User Details have been stored in the Database", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(VerifyPhoneNumber.this, "User Details could NOT be stored in the Database", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onFailure: " + e.toString());
                        }
                    });

                    // Move User to the Application Dashboard
                    Intent intent = new Intent(VerifyPhoneNumber.this, DefaultPageActivity.class);
                    startActivity(intent);
                } else {
                    Log.w(TAG, "linkWithCredential:failure", task.getException());
                    Toast.makeText(VerifyPhoneNumber.this, "Login Failed." + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
