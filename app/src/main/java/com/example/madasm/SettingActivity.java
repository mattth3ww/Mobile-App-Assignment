package com.example.madasm;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingActivity extends AppCompatActivity {

    private SwitchCompat switchTheme;
    private TextView tvSwitchLabel;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        // Get the current theme mode from SharedPreferences
        sharedPreferences = getSharedPreferences("theme_pref", Context.MODE_PRIVATE);
        int currentThemeMode = sharedPreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_NO);

        // Set the theme based on the current theme mode
        setTheme(currentThemeMode == AppCompatDelegate.MODE_NIGHT_YES ? R.style.AppTheme_Dark : R.style.AppTheme);

        setContentView(R.layout.setting_page);

        // Find the switch and text label by their IDs
        switchTheme = findViewById(R.id.switchTheme);
        tvSwitchLabel = findViewById(R.id.tvSwitchLabel);

        // Set the initial state of the switch based on the current theme mode
        switchTheme.setChecked(currentThemeMode == AppCompatDelegate.MODE_NIGHT_YES);

        // Set an OnCheckedChangeListener to the switch
        switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Call the toggleTheme() method when the switch is checked or unchecked
                toggleTheme(isChecked);
            }
        });

        // Set the text color of the label based on the current theme mode
        updateLabelTextColor(currentThemeMode == AppCompatDelegate.MODE_NIGHT_YES);

        ImageButton btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Find the logout button by its ID
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the logout() method when the logout button is clicked
                logout();
            }
        });

        // Find the logout button by its ID
        Button btnResetPassword = findViewById(R.id.btnResetPassword);
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset Password
                onResetPasswordClicked();
            }
        });

    }

    // Method to toggle between light and dark mode
    private void toggleTheme(boolean isDarkMode) {
        // Update the theme mode based on the switch state
        int newThemeMode = isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(newThemeMode);

        // Save the theme mode to SharedPreferences
        sharedPreferences.edit().putInt("theme_mode", newThemeMode).apply();

        // Recreate the activity to apply the theme changes
        recreate();
    }

    // Method to update the text color of the label based on the theme mode
    private void updateLabelTextColor(boolean isDarkMode) {
        if (isDarkMode) {
            tvSwitchLabel.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            tvSwitchLabel.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    // Method to logout from the Firebase authenticated account
    private void logout() {
        // Sign out the current user
        firebaseAuth.signOut();
        // Navigate to the login page
        Intent intent = new Intent(SettingActivity.this, Login.class);
        startActivity(intent);
        finish(); // Optional: finish the current activity to prevent going back to it after logout
    }

    // Helper method to validate password format
    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false; // Password must have at least 8 characters
        }
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasNumber = false;

        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);
            if (Character.isUpperCase(ch)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(ch)) {
                hasLowercase = true;
            } else if (Character.isDigit(ch)) {
                hasNumber = true;
            }
        }
        return hasUppercase && hasLowercase && hasNumber;
    }

    // Updates the user's password in Firebase Authentication.
    private void changePassword(String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SettingActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingActivity.this, "Failed to update password. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // initiates the password reset process by showing the current password dialog
    private void onResetPasswordClicked() {
        showCurrentPasswordDialog();
    }

    // Displays an alert dialog for entering the current password.
    private void showCurrentPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");
        builder.setMessage("Enter your current password:");

        final EditText currentPasswordEditText = new EditText(this);
        currentPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(currentPasswordEditText);

        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String currentPassword = currentPasswordEditText.getText().toString();
                if (TextUtils.isEmpty(currentPassword)) {
                    Toast.makeText(SettingActivity.this, "Please enter your current password", Toast.LENGTH_SHORT).show();
                } else {
                    verifyCurrentPassword(currentPassword);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Verifies the current password entered by the user with Firebase Authentication.
    private void verifyCurrentPassword(String currentPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                showNewPasswordDialog();
                            } else {
                                Toast.makeText(SettingActivity.this, "Invalid password. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // Displays an alert dialog for entering the new password and confirming it.
    private void showNewPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");

        final EditText newPasswordEditText = new EditText(this);
        newPasswordEditText.setHint("New Password");
        newPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final EditText confirmNewPasswordEditText = new EditText(this);
        confirmNewPasswordEditText.setHint("Confirm New Password");
        confirmNewPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(newPasswordEditText);
        layout.addView(confirmNewPasswordEditText);

        builder.setView(layout);

        builder.setPositiveButton("Change Password", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPassword = newPasswordEditText.getText().toString();
                String repeatedPassword = confirmNewPasswordEditText.getText().toString();

                // Validate new password format
                if (isValidPassword(newPassword)) {
                    // Validate repeated password
                    if (newPassword.equals(repeatedPassword)) {
                        // Update password in Firebase Authentication
                        changePassword(newPassword);
                    } else {
                        // Display error message for repeated password mismatch
                        Toast.makeText(getApplicationContext(), "Repeated password is not same", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Display error message for invalid new password format
                    Toast.makeText(getApplicationContext(), "Password must be at least 8 characters, start with a capital letter, and include a mix of alphabets and numbers", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}