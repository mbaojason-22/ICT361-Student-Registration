package com.example.ict361studentregistration;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LecturerActivity extends AppCompatActivity {

    private Button signInButton;
    private Button createAccountButton;

    private TextView forgotPassword;

    private ImageButton backButton;
    private ImageButton helpButton;
    private ImageButton passwordEyeButton;

    private EditText lecturerIdInput;
    private EditText passwordInput;

    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the Lecturer Portal screen
        setContentView(R.layout.activity_lecturer);

        // Connect Java to the screen elements
        signInButton = findViewById(R.id.lecturer_sign_in_button);
        createAccountButton = findViewById(R.id.lecturer_create_account_button);

        forgotPassword = findViewById(R.id.lecturer_forgot_password);

        backButton = findViewById(R.id.lecturer_back_button);
        helpButton = findViewById(R.id.lecturer_help_button);
        passwordEyeButton = findViewById(R.id.lecturer_password_eye_button);

        lecturerIdInput = findViewById(R.id.lecturer_id_input);
        passwordInput = findViewById(R.id.lecturer_password_input);


        // SIGN IN BUTTON
        signInButton.setOnClickListener(v -> {

            String identifier = lecturerIdInput
                    .getText()
                    .toString()
                    .trim();

            String password = passwordInput
                    .getText()
                    .toString();

            // Check whether lecturer ID / email field is empty
            if (identifier.isEmpty()) {

                lecturerIdInput.setError(
                        "Enter your lecturer ID or email"
                );

                lecturerIdInput.requestFocus();

                return;
            }

            // Check whether input is an email
            boolean isEmail = Patterns.EMAIL_ADDRESS
                    .matcher(identifier)
                    .matches();

            // Check whether input is a numeric lecturer ID (e.g. 5-10 digits)
            boolean isLecturerId = identifier.matches("\\d{5,10}");

            if (!isEmail && !isLecturerId) {

                lecturerIdInput.setError(
                        "Enter a valid lecturer ID or email"
                );

                lecturerIdInput.requestFocus();

                return;
            }

            // Check password
            if (password.isEmpty()) {

                passwordInput.setError(
                        "Enter your password"
                );

                passwordInput.requestFocus();

                return;
            }

            // Valid login details supplied
            new AlertDialog.Builder(LecturerActivity.this)
                    .setTitle("Lecturer Sign In Successful")
                    .setMessage("Welcome back to Campus Companion!")
                    .setPositiveButton("PROCEED TO HOME", (dialog, which) -> {
                        Intent intent = new Intent(LecturerActivity.this, HomeActivity.class);
                        intent.putExtra("USER_NAME", identifier);
                        startActivity(intent);
                        finish();
                    })
                    .show();

        });


        // CREATE ACCOUNT BUTTON
        createAccountButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LecturerActivity.this,
                    CreateLecturerActivity.class
            );

            startActivity(intent);

        });


        // FORGOT PASSWORD (Same as Student's recovery flow)
        forgotPassword.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LecturerActivity.this,
                    ForgotPasswordActivity.class
            );

            startActivity(intent);

        });


        // PASSWORD SHOW / HIDE BUTTON
        passwordEyeButton.setOnClickListener(v -> {

            if (passwordVisible) {

                // Hide the password
                passwordInput.setTransformationMethod(
                        PasswordTransformationMethod.getInstance()
                );

                passwordEyeButton.setImageResource(R.drawable.password_eye_red);
                passwordEyeButton.setAlpha(1.0f);

                passwordVisible = false;

            } else {

                // Show the password (dimmed red eye-off icon)
                passwordInput.setTransformationMethod(
                        HideReturnsTransformationMethod.getInstance()
                );

                passwordEyeButton.setImageResource(R.drawable.password_eye_off_red);
                passwordEyeButton.setAlpha(0.6f);

                passwordVisible = true;
            }

            // Keep the cursor at the end of the password
            passwordInput.setSelection(
                    passwordInput.getText().length()
            );

        });


        // BACK BUTTON
        backButton.setOnClickListener(v -> finish());


        // LECTURER PORTAL HELP
        helpButton.setOnClickListener(v -> {

            new AlertDialog.Builder(LecturerActivity.this)
                    .setTitle("Lecturer Portal Guide")
                    .setMessage(
                            "Welcome to the Campus Companion Lecturer Portal!\n\n" +

                                    "1. LECTURER ID OR EMAIL\n" +
                                    "Enter your official staff ID or registered staff email address.\n\n" +

                                    "2. PASSWORD\n" +
                                    "Enter your account password. Tap the eye button to show or hide your password.\n\n" +

                                    "3. SIGN IN\n" +
                                    "Press SIGN IN to access lecturer tools, manage student registration records, and assign laboratory groups.\n\n" +

                                    "4. FORGOT PASSWORD\n" +
                                    "Select Forgot Password? to reset your password using the verification process."
                    )
                    .setPositiveButton("GOT IT", null)
                    .show();

        });
    }
}
