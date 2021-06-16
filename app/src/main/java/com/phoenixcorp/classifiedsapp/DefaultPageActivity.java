package com.phoenixcorp.classifiedsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DefaultPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_page);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);

        getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContainer,new HomeFragment()).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);


    }

    BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener(){


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch(item.getItemId())
            {
                case R.id.home:
                    selectedFragment=new HomeFragment();
                    break;
                case R.id.search:
                    selectedFragment=new SearchFragment();
                    break;
                case R.id.blog:
                    selectedFragment=new ChatFragment();
                    break;
                case R.id.profile:
                    selectedFragment=new ProfileFragment();
                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContainer,selectedFragment).commit();
            return true;
        }
    };

}