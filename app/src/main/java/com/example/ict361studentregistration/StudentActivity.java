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

public class StudentActivity extends AppCompatActivity {

    private Button signInButton;
    private Button createAccountButton;
    private TextView forgotPassword;
    private ImageButton backButton;
    private ImageButton helpButton;
    private ImageButton passwordEyeButton;
    private EditText studentNumberInput;
    private EditText passwordInput;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        signInButton = findViewById(R.id.sign_in_button);
        createAccountButton = findViewById(R.id.create_account_button);
        forgotPassword = findViewById(R.id.forgot_password);
        backButton = findViewById(R.id.student_back_button);
        helpButton = findViewById(R.id.student_help_button);
        passwordEyeButton = findViewById(R.id.password_eye_button);
        studentNumberInput = findViewById(R.id.student_number_input);
        passwordInput = findViewById(R.id.password_input);

        signInButton.setOnClickListener(v -> {

            String identifier =
                    studentNumberInput.getText().toString().trim();

            String password =
                    passwordInput.getText().toString();

            if (identifier.isEmpty()) {
                studentNumberInput.setError(
                        "Enter your student number or email"
                );
                studentNumberInput.requestFocus();
                return;
            }

            boolean isEmail =
                    Patterns.EMAIL_ADDRESS
                            .matcher(identifier)
                            .matches();

            boolean isStudentNumber =
                    identifier.matches("\\d{9}");

            if (!isEmail && !isStudentNumber) {
                studentNumberInput.setError(
                        "Enter a valid 9-digit student number or email"
                );
                studentNumberInput.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                passwordInput.setError(
                        "Enter your password"
                );
                passwordInput.requestFocus();
                return;
            }

            // Open Student Home after successful local validation
            Intent intent =
                    new Intent(
                            StudentActivity.this,
                            StudentHomeActivity.class
                    );

            startActivity(intent);
        });

        createAccountButton.setOnClickListener(v -> {
            Intent intent =
                    new Intent(
                            StudentActivity.this,
                            CreateAccountActivity.class
                    );

            startActivity(intent);
        });

        forgotPassword.setOnClickListener(v -> {
            Intent intent =
                    new Intent(
                            StudentActivity.this,
                            PasswordRecoveryActivity.class
                    );

            startActivity(intent);
        });

        passwordEyeButton.setOnClickListener(v -> {

            if (passwordVisible) {

                passwordInput.setTransformationMethod(
                        PasswordTransformationMethod.getInstance()
                );

                passwordVisible = false;

            } else {

                passwordInput.setTransformationMethod(
                        HideReturnsTransformationMethod.getInstance()
                );

                passwordVisible = true;
            }

            passwordInput.setSelection(
                    passwordInput.getText().length()
            );
        });

        backButton.setOnClickListener(v -> finish());

        helpButton.setOnClickListener(v -> {

            new AlertDialog.Builder(StudentActivity.this)
                    .setTitle("Student Portal Guide")
                    .setMessage(
                            "Welcome to the Campus Companion Student Portal!\n\n" +

                                    "1. STUDENT NUMBER OR EMAIL\n" +
                                    "Enter either your 9-digit student number " +
                                    "or the email associated with your account.\n\n" +

                                    "2. PASSWORD\n" +
                                    "Enter your account password.\n\n" +

                                    "3. SIGN IN\n" +
                                    "Press SIGN IN after entering your details.\n\n" +

                                    "4. CREATE ACCOUNT\n" +
                                    "Create a new Campus Companion account.\n\n" +

                                    "5. FORGOT PASSWORD\n" +
                                    "Use Forgot Password? to recover your account."
                    )
                    .setPositiveButton("GOT IT", null)
                    .show();
        });
    }
}