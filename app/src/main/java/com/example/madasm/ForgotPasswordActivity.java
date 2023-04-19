package com.example.madasm;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etForgotPasswordEmail;
    private Button btnForgotPasswordReset;
    private ProgressBar progressBarForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword);

        // Get reference to views
        etForgotPasswordEmail = findViewById(R.id.etForgotPasswordEmail);
        btnForgotPasswordReset = findViewById(R.id.btnForgotPasswordReset);
        progressBarForgotPassword = findViewById(R.id.progressBarForgotPassword);

        // Set an OnClickListener for the "Reset Password" button
        btnForgotPasswordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the email entered by the user
                String email = etForgotPasswordEmail.getText().toString().trim();

                // Check if email is empty
                if (TextUtils.isEmpty(email)) {
                    etForgotPasswordEmail.setError("Please enter an email");
                    etForgotPasswordEmail.requestFocus();
                    return;
                }

                // Show progress bar
                progressBarForgotPassword.setVisibility(View.VISIBLE);

                // Send password reset email to the user's email address
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                // Hide progress bar
                                progressBarForgotPassword.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    // Password reset email sent successfully
                                    Toast.makeText(getApplicationContext(), "Password reset email sent", Toast.LENGTH_SHORT).show();

                                    // Exit the current activity and go back to the login activity
                                    Intent intent = new Intent(ForgotPasswordActivity.this, Login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Failed to send password reset email
                                    if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                        // Email does not exist in authentication
                                        Toast.makeText(getApplicationContext(), "Email does not exist, failed to send reset password email", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Other failure reasons
                                        Toast.makeText(getApplicationContext(), "Failed to send reset password email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });

        // Set an OnClickListener for the "Exit" button
        Button btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the activity and return to the previous activity
                finish();
            }
        });
    }
}
