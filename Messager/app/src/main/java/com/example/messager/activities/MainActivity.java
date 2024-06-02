package com.example.messager.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.messager.R;
import com.example.messager.adapters.RecentConversationsAdapter;
import com.example.messager.databinding.ActivityMainBinding;
import com.example.messager.listeners.ConversionListener;
import com.example.messager.models.ChatMessage;
import com.example.messager.models.User;
import com.example.messager.utilities.Constants;
import com.example.messager.utilities.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    private List<ChatMessage> conversations;
    private RecentConversationsAdapter recentConversationsAdapter;
    private FirebaseFirestore database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        loadUserDetails();
        setListeners();

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               int id = item.getItemId();
               if (id == R.id.home) {
                   startActivity(new Intent(getApplicationContext(),MainActivity.class));
                   overridePendingTransition(0,0);
               }
               else if (id == R.id.search) {
                   startActivity(new Intent(getApplicationContext(),UsersActivity.class));
                   overridePendingTransition(0,0);
               }
               else if (id == R.id.messages) {
                   startActivity(new Intent(getApplicationContext(),RecentMessages.class));
                   overridePendingTransition(0,0);
               }
               else if (id == R.id.profile) {
                   startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                   overridePendingTransition(0,0);
               }
               return true;
            }
        });
    }


    private void setListeners() {
        Intent intent = new Intent(this,ProfileActivity.class);
        binding.signout.setOnClickListener(v -> signOut());
        binding.imageProfile.setOnClickListener(v -> startActivity(intent));
    }


    private void loadUserDetails() {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }


    private void showToast(String messeage) {
        Toast.makeText(getApplicationContext(), messeage, Toast.LENGTH_SHORT).show();
    }


    private void signOut() {
        showToast("Signing out...");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    showToast("Unable to sign out.");
                });
    }
}