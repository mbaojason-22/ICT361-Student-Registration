package com.example.ict361studentregistration;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CreateLecturerPasswordActivity extends AppCompatActivity {

    private ImageButton backButton;
    private ImageButton helpButton;

    private EditText passwordInput;
    private EditText confirmPasswordInput;

    private ImageButton passwordEyeButton;
    private ImageButton confirmPasswordEyeButton;

    private Button completeRegistrationButton;

    private boolean passwordVisible = false;
    private boolean confirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_lecturer_password);

        backButton = findViewById(R.id.set_pwd_back_button);
        helpButton = findViewById(R.id.set_pwd_help_button);

        passwordInput = findViewById(R.id.lecturer_set_pwd_input);
        confirmPasswordInput = findViewById(R.id.lecturer_set_confirm_input);

        passwordEyeButton = findViewById(R.id.lecturer_set_pwd_eye);
        confirmPasswordEyeButton = findViewById(R.id.lecturer_set_confirm_eye);

        completeRegistrationButton = findViewById(R.id.complete_registration_button);


        // BACK BUTTON
        backButton.setOnClickListener(v -> finish());


        // PASSWORD EYE
        passwordEyeButton.setOnClickListener(v -> {
            if (passwordVisible) {
                passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                passwordEyeButton.setImageResource(R.drawable.password_eye_red);
                passwordEyeButton.setAlpha(1.0f);
                passwordVisible = false;
            } else {
                passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                passwordEyeButton.setImageResource(R.drawable.password_eye_off_red);
                passwordEyeButton.setAlpha(0.6f);
                passwordVisible = true;
            }
            passwordInput.setSelection(passwordInput.getText().length());
        });


        // CONFIRM PASSWORD EYE
        confirmPasswordEyeButton.setOnClickListener(v -> {
            if (confirmPasswordVisible) {
                confirmPasswordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                confirmPasswordEyeButton.setImageResource(R.drawable.password_eye_red);
                confirmPasswordEyeButton.setAlpha(1.0f);
                confirmPasswordVisible = false;
            } else {
                confirmPasswordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                confirmPasswordEyeButton.setImageResource(R.drawable.password_eye_off_red);
                confirmPasswordEyeButton.setAlpha(0.6f);
                confirmPasswordVisible = true;
            }
            confirmPasswordInput.setSelection(confirmPasswordInput.getText().length());
        });


        // COMPLETE REGISTRATION BUTTON
        completeRegistrationButton.setOnClickListener(v -> {
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            if (password.isEmpty()) {
                passwordInput.setError("Enter your password");
                passwordInput.requestFocus();
                return;
            }

            if (password.length() < 8) {
                passwordInput.setError("Password must be at least 8 characters long");
                passwordInput.requestFocus();
                return;
            }

            boolean hasLetter = password.matches(".*[a-zA-Z].*");
            boolean hasDigit = password.matches(".*[0-9].*");
            boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?~`\\p{Punct}].*") || password.matches(".*[^a-zA-Z0-9].*");

            if (!hasLetter || !hasDigit || !hasSpecialChar) {
                passwordInput.setError("Password must include letters, numbers, and at least one special character (!@#$)");
                passwordInput.requestFocus();
                return;
            }

            if (confirmPassword.isEmpty()) {
                confirmPasswordInput.setError("Confirm your password");
                confirmPasswordInput.requestFocus();
                return;
            }

            if (!password.equals(confirmPassword)) {
                confirmPasswordInput.setError("Passwords do not match");
                confirmPasswordInput.requestFocus();
                return;
            }

            // Success dialog
            new AlertDialog.Builder(CreateLecturerPasswordActivity.this)
                    .setTitle("Lecturer Account Created Successfully!")
                    .setMessage(
                            "Your lecturer account and password have been registered successfully.\n\n" +
                                    "You can now sign in to the Lecturer Portal."
                    )
                    .setPositiveButton("PROCEED TO HOME", (dialog, which) -> {
                        Intent intent = new Intent(
                                CreateLecturerPasswordActivity.this,
                                HomeActivity.class
                        );
                        if (getIntent().getExtras() != null) {
                            intent.putExtras(getIntent().getExtras());
                        }
                        if (intent.getStringExtra("USER_NAME") == null) {
                            String firstName = getIntent().getStringExtra("FIRST_NAME");
                            intent.putExtra("USER_NAME", (firstName != null && !firstName.trim().isEmpty()) ? firstName : "Lecturer");
                        }
                        startActivity(intent);
                        finish();
                    })
                    .setCancelable(false)
                    .show();
        });


        // HELP BUTTON
        helpButton.setOnClickListener(v -> {
            new AlertDialog.Builder(CreateLecturerPasswordActivity.this)
                    .setTitle("Set Password Help")
                    .setMessage(
                            "Create a secure password with at least 8 characters, including letters, numbers, and special characters.\n\n" +
                                    "Ensure both password fields match before tapping COMPLETE REGISTRATION."
                    )
                    .setPositiveButton("GOT IT", null)
                    .show();
        });
    }
}
