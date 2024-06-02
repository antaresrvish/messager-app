package com.example.messager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.messager.R;
import com.example.messager.databinding.ActivitySignInBinding;
import com.example.messager.utilities.Constants;
import com.example.messager.utilities.PreferenceManager;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(getApplicationContext());

        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_sign_in);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();
    }

    //Setting up listeners for button clicks etc.
    private void setListeners() {
        binding.registertextviewbtn.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
        binding.loginBtn.setOnClickListener(v -> {
            if (isValidSignInDetails()) {
                signIn();
            }
        });
    }

    //Main sign in method
    private void signIn() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS) //get data from users collection
                .whereEqualTo(Constants.KEY_EMAIL, binding.loginemailinput.getText().toString()) //check data if its equal to the data from the input
                .whereEqualTo(Constants.KEY_PASSWORD , binding.loginpasswordinput.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null
                    && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        loading(false);
                        showToast("Unable to sign in");
                    }
                });
    }


    private void loading(Boolean isLoading) {
        if(isLoading) {
            binding.loginBtn.setVisibility(View.INVISIBLE);
            binding.loginProgressbar.setVisibility(View.VISIBLE);
        } else {
            binding.loginBtn.setVisibility(View.VISIBLE);
            binding.loginProgressbar.setVisibility(View.INVISIBLE);
        }
    }


    private void showToast(String messeage) {
        Toast.makeText(getApplicationContext(), messeage, Toast.LENGTH_SHORT).show();
    }


    private Boolean isValidSignInDetails() {
        if (binding.loginemailinput.getText().toString().trim().isEmpty()) {
            showToast("Enter email.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.loginemailinput.getText().toString()).matches()) {
            showToast("Enter valid email.");
            return false;
        } else if (binding.loginpasswordinput.getText().toString().trim().isEmpty()) {
            showToast("Enter password.");
            return false;
        } else {
            return true;
        }
    }
}