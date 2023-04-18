package com.example.madasm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserActivity extends AppCompatActivity {
    private TextView tvUserName;
    private EditText etUserName;
    private Button btnEdit;
    private Button btnSave;
    private Button btnCancel;
    private Button btnExit;
    private String originalUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTheme();
        setContentView(R.layout.user_page);


        // Retrieve user's name from Firebase Custom Claim
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        // Get references to views in your code
        tvUserName = findViewById(R.id.tvUserName);
        etUserName = findViewById(R.id.etUserName);
        btnEdit = findViewById(R.id.btnEdit);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnExit = findViewById(R.id.btnExit);

        // Set initial user name to TextView
        tvUserName.setText(userName);
        originalUserName = userName; // Store original user name

        // Set onClick listener for the exit button
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity to exit and return to previous page
                finish();
            }
        });

        // Set click listener for Edit button
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide TextView and show EditText
                tvUserName.setVisibility(View.GONE);
                etUserName.setVisibility(View.VISIBLE);
                etUserName.setText(originalUserName); // Set original user name to EditText
                etUserName.setEnabled(true); // Enable editing
                // Show Save and Cancel buttons, hide Edit button
                btnEdit.setVisibility(View.GONE);
                btnSave.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
            }
        });

        // Set click listener for Save button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get edited user name from EditText
                String editedUserName = etUserName.getText().toString().trim();

                // Update user name in Firebase and local TextView
                updateUserNameInFirebase(editedUserName);
            }
        });

        // Set click listener for Cancel button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide EditText and show TextView
                etUserName.setVisibility(View.GONE);
                tvUserName.setVisibility(View.VISIBLE);
                etUserName.setEnabled(false); // Disable editing
                // Show Edit button and hide Save and Cancel buttons
                btnEdit.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
            }
        });
    }

    private void updateUserNameInFirebase(String userName) {
        // Update user name in Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Update TextView with edited user name
                            tvUserName.setText(userName);
                            // Update originalUserName with edited user name
                            originalUserName = userName;
                            // Show Edit button and hide Save and Cancel buttons
                            btnEdit.setVisibility(View.VISIBLE);
                            btnSave.setVisibility(View.GONE);
                            btnCancel.setVisibility(View.GONE);
                            // Disable editing of user name
                            etUserName.setEnabled(false);
                            Toast.makeText(UserActivity.this, "User name updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserActivity.this, "Failed to update user name", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set up theme
        setupTheme();
    }

    private void setupTheme() {
        // Get the current theme mode from SharedPreferences, or default to light mode
        SharedPreferences sharedPreferences = getSharedPreferences("theme_pref", Context.MODE_PRIVATE);
        int currentThemeMode = sharedPreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_NO);

        // Set the theme based on the current theme mode
        setTheme(currentThemeMode == AppCompatDelegate.MODE_NIGHT_YES ? R.style.AppTheme_Dark : R.style.AppTheme);
    }
}
