package com.phoenixcorp.classifiedsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent registration = new Intent(MainActivity.this, LoginActivity.class);
                Intent home = new Intent(MainActivity.this, DefaultPageActivity.class);
                if(auth.getCurrentUser() != null)
                    startActivity(home);
                else
                    startActivity(registration);
                finish();
            }
        }, 1500);

//        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
//        startActivity(intent);
    }
}