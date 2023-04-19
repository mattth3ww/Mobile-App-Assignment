package com.example.madasm;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegistrationActivity extends AppCompatActivity {
    private EditText etEmail;
    private EditText etPassword;
    private EditText etRepeatPassword;
    private EditText etName;
    private static final String TAG = "RegistrationActivity";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etRepeatPassword = findViewById(R.id.etRepeatPassword);
        etName = findViewById(R.id.etName);

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String repeatPassword = etRepeatPassword.getText().toString();
        String name = etName.getText().toString();

        setLoginLinkTextAndTouchListener();

        // Set an OnClickListener for the Register button
        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input values
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String repeatPassword = etRepeatPassword.getText().toString();

                // Call the handleRegistration() method with input values
                handleRegistration(name, email, password, repeatPassword);
            }
        });

    }

    // Helper method to validate email using a regular expression
    private boolean isEmailValid(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    private void isCredentialTaken(final String email, final String name, final String password) {
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SignInMethodQueryResult result = task.getResult();
                        if (result != null && result.getSignInMethods() != null
                                && !result.getSignInMethods().isEmpty()) {
                            // Email is already registered
                            // You can handle this case as needed, e.g. show an error message
                            // or prevent registration with this email address
                            Log.d(TAG, "Email is already registered");
                            Toast.makeText(getApplicationContext(), "Email is already taken", Toast.LENGTH_SHORT).show();
                        } else {
                            // Email is not registered, no further action needed
                            Log.d(TAG, "Email is not registered");
                            registerUser(name, email, password);
                        }
                    } else {
                        // Error occurred while checking email registration status
                        // You can handle this case as needed, e.g. show an error message
                        Log.e(TAG, "Error checking email registration status", task.getException());
                    }
                });
    }

    // Method to register user in Firebase authentication
    private void registerUser(String name, String email, String password) {
        // Register user in Firebase Authentication
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Get the current user
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            // Add custom claim for name
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Custom claim added successfully (User name is stored)

                                                // Send email verification to user
                                                user.sendEmailVerification()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    // Email verification sent successfully
                                                                    Toast.makeText(RegistrationActivity.this, "Verify account through the email sent to your email address and try logging in again.", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    // Failed to send email verification
                                                                    Toast.makeText(RegistrationActivity.this, "Failed to send email verification", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                // Failed to add custom claim
                                                Toast.makeText(RegistrationActivity.this, "Failed to add custom claim", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            // TODO: Perform additional registration logic here


                        } else {
                            // Registration failed
                            Toast.makeText(RegistrationActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Custom Method
    private void setLoginLinkTextAndTouchListener() {
        // Get a reference to the TextView
        TextView tvLoginLink = findViewById(R.id.tvLoginLink);

        // Create a SpannableString to apply different styles and colors
        SpannableString spannableString = new SpannableString("Already have an account? Login here.");

        // Set the text color for "Already have an account?" to black
        ForegroundColorSpan blackSpan = new ForegroundColorSpan(Color.BLACK);
        spannableString.setSpan(blackSpan, 0, 24, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the text color for "Login here" to blue
        ForegroundColorSpan blueSpan = new ForegroundColorSpan(Color.BLUE);
        spannableString.setSpan(blueSpan, 25, 35, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the SpannableString as the text for the TextView
        tvLoginLink.setText(spannableString);

        // Set a TouchListener to change text color on hover
        tvLoginLink.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Change text color to a brighter color when touched
                        ForegroundColorSpan brightBlueSpan = new ForegroundColorSpan(Color.parseColor("#6495ED")); // You can adjust the color to your liking
                        spannableString.setSpan(brightBlueSpan, 25, 35, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tvLoginLink.setText(spannableString);
                        break;
                    case MotionEvent.ACTION_UP:
                        // Change text color back to the original color when released
                        spannableString.setSpan(blueSpan, 25, 35, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tvLoginLink.setText(spannableString);
                        break;
                }
                return false;
            }
        });

        // Set an OnClickListener to handle click event
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Method to handle validation and registration logic
    private void handleRegistration(String name, String email, String password, String repeatPassword) {
        // Check if name is empty
        if (TextUtils.isEmpty(name)) {
            etName.setError("Please enter your name");
            etName.requestFocus();
            return;
        }

        // Check if email is empty
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Please enter an email");
            etEmail.requestFocus();
            return;
        }

        // Check if email is valid
        if (!isEmailValid(email)) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return;
        }

        // Check if password is empty
        if (TextUtils.isEmpty(password)){
            etPassword.setError("Please enter a password");
            etPassword.requestFocus();
            return;
        }

        // Check if password meets requirements
        if (!isPasswordValid(password)) {
            etPassword.setError("Password must be at least 8 characters, start with a capital letter, and include a mix of alphabets and numbers");
            etPassword.requestFocus();
            return;
        }

        // Check if repeat password is empty
        if (TextUtils.isEmpty(repeatPassword)) {
            etRepeatPassword.setError("Please repeat your password");
            etRepeatPassword.requestFocus();
            return;
        }

        // Check if password and repeat password match
        if (!password.equals(repeatPassword)) {
            etRepeatPassword.setError("Passwords do not match");
            etRepeatPassword.requestFocus();
            return;
        }

        // TODO: Perform registration logic here
        // Call isCredentialTaken() to check if email is taken
        isCredentialTaken(email, name, password);
    }

    private boolean isPasswordValid(String password) {
        // Password must be at least 8 characters
        if (password.length() < 8) {
            return false;
        }

        // Password must start with a capital letter
        if (!Character.isUpperCase(password.charAt(0))) {
            return false;
        }

        boolean hasAlphabet = false;
        boolean hasNumber = false;

        // Password must include a mix of alphabets and numbers
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isLetter(c)) {
                hasAlphabet = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            }
        }

        return hasAlphabet && hasNumber;
    }
}

