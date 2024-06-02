package com.example.messager.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.messager.R;
import com.example.messager.databinding.ActivityMainBinding;
import com.example.messager.databinding.ActivityProfileBinding;
import com.example.messager.utilities.Constants;
import com.example.messager.utilities.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private PreferenceManager preferenceManager;
    Boolean isKeyboardShowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
        setListeners();
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.profile);
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

    private void showToast(String messeage) {
        Toast.makeText(getApplicationContext(), messeage, Toast.LENGTH_SHORT).show();
    }

    private void setListeners(){
        binding.profileBtn.setOnClickListener(v -> {
            if (isValidProfileSettings()){
                //saveProfileSettings();
            }
        });
    }

    private void loadUserDetails() {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    /*private void saveProfileSettings(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, binding.profilenameinput.getText().toString());
        user.put(Constants.KEY_EMAIL, binding.profileemailinput.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.profilepasswordinput.getText().toString());
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    preferenceManager.putString(Constants.KEY_NAME, binding.profilenameinput.getText().toString());
                        })
                .addOnFailureListener(exception -> {
                    showToast(exception.getMessage());
                });


    }*/

    private Boolean isValidProfileSettings() {
        if (binding.profilenameinput.getText().toString().trim().isEmpty()) {
            showToast("Enter namne");
        } else if (binding.profileemailinput.getText().toString().trim().isEmpty()) {
            showToast("Enter email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.profileemailinput.getText().toString()).matches()) {
            showToast("enter valid image");
            return false;
        } else if (binding.profilepasswordinput.getText().toString().trim().isEmpty()) {
            showToast("Enter password.");
            return false;
        } else {
            return true;
        }
        return null;
    }


}