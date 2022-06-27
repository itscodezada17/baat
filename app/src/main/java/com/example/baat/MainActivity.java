 package com.example.baat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.baat.Adapters.FragmentsAdapter;
import com.example.baat.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

 public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;  // for binding
    FirebaseAuth mAuth;  // for firebase authentication
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());  // making binding useful
        setContentView(binding.getRoot());  // binding getroot will used instead of R.id.hduh
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();  // using mAuth variable for making instance of firebase

        binding.viewPager.setAdapter((new FragmentsAdapter(getSupportFragmentManager())));
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }


    // creating menu option
     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         MenuInflater inflater = getMenuInflater();
         inflater.inflate(R.menu.menu,menu);
         return super.onCreateOptionsMenu(menu);
     }


     // creating switch cse for menu options in onOptionsItemSelected
     @Override
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.settings:
                Toast.makeText(this, "setting clicked", Toast.LENGTH_SHORT).show();
                break;

            case R.id.logOut:
                mAuth.signOut();  // SignOut function is signing out from
                Intent intent = new Intent(MainActivity.this,SignInActivity.class);
                startActivity(intent);
        }
         return true;
     }
 }