package com.example.madasm;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import java.util.List;

public class Login extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;
    private TextView tvForgotPassword; // Added TextView for forgot password
    private FirebaseAuth auth; // Firebase Authentication instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance();

        // Initialize UI components
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword); // Initialize forgot password TextView

        // TextWatcher to listen for changes in the EditText fields
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Check if both username and password fields are filled
                if (etUsername.getText().toString().trim().length() > 0 &&
                        etPassword.getText().toString().trim().length() > 0) {
                    // Enable login button
                    btnLogin.setEnabled(true);
                } else {
                    // Disable login button
                    btnLogin.setEnabled(false);
                }
            }
        };

        // Add TextChangedListener to both EditText fields
        etUsername.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);

        // Set click listener for login button
        // Set click listener for login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                // Validate username and password
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(Login.this, "Please enter username", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Please enter password", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform login with Firebase Authentication
                    auth.signInWithEmailAndPassword(username, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Check if the user's email is verified
                                        FirebaseUser user = auth.getCurrentUser();
                                        if (user != null && user.isEmailVerified()) {
                                            // Email is verified, login successful
                                            Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                                            // Navigate to next page
                                            Intent intent = new Intent(Login.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            // Email is not verified, show error message
                                            Toast.makeText(Login.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                                            // Sign out the user
                                            auth.signOut();
                                        }
                                    } else {
                                        // Login failed
                                        Toast.makeText(Login.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        // Set click listener for register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to registration page
                Intent intent = new Intent(Login.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Set click listener for forgot password TextView
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to forgot password page
                Intent intent = new Intent(Login.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onStart(){
        super.onStart();
        // Check if user is already logged in, if yes, navigate to next page
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Check if the app is coming from the backstack
            boolean isFromBackstack = false;
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            if (taskInfo != null && !taskInfo.isEmpty()) {
                ComponentName topActivity = taskInfo.get(0).topActivity;
                if (topActivity != null && topActivity.getClassName().equals(getClass().getName())) {
                    isFromBackstack = true;
                }
            }

            // If coming from backstack, allow user to stay logged in, else log out
            if (isFromBackstack) {
                // User is allowed to stay logged in
            } else {
                // User is automatically logged out
                auth.signOut(); // Added this line to sign out user
                Intent intent = new Intent(Login.this, Login.class);
                startActivity(intent);
                finish();
            }
        }
    }
}