package com.example.ict361studentregistration;

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

        // Load the Student Portal screen
        setContentView(R.layout.activity_student);

        // Connect Java to the screen elements
        signInButton = findViewById(R.id.sign_in_button);
        createAccountButton = findViewById(R.id.create_account_button);

        forgotPassword = findViewById(R.id.forgot_password);

        backButton = findViewById(R.id.student_back_button);
        helpButton = findViewById(R.id.student_help_button);
        passwordEyeButton = findViewById(R.id.password_eye_button);

        studentNumberInput = findViewById(R.id.student_number_input);
        passwordInput = findViewById(R.id.password_input);


        // SIGN IN BUTTON
        signInButton.setOnClickListener(v -> {

            String identifier = studentNumberInput
                    .getText()
                    .toString()
                    .trim();

            String password = passwordInput
                    .getText()
                    .toString();

            // Check whether the student number/email field is empty
            if (identifier.isEmpty()) {

                studentNumberInput.setError(
                        "Enter your student number or email"
                );

                studentNumberInput.requestFocus();

                return;
            }

            // Check whether the user entered an email
            boolean isEmail = Patterns.EMAIL_ADDRESS
                    .matcher(identifier)
                    .matches();

            // Check whether the user entered a 9-digit student number
            boolean isStudentNumber = identifier.matches("\\d{9}");

            // Make sure the identifier is valid
            if (!isEmail && !isStudentNumber) {

                studentNumberInput.setError(
                        "Enter a valid 9-digit student number or email"
                );

                studentNumberInput.requestFocus();

                return;
            }

            // Check whether the password field is empty
            if (password.isEmpty()) {

                passwordInput.setError(
                        "Enter your password"
                );

                passwordInput.requestFocus();

                return;
            }

            // The user has supplied both required details
            new AlertDialog.Builder(StudentActivity.this)
                    .setTitle("Sign In")
                    .setMessage(
                            "Your login details have been entered correctly.\n\n" +
                                    "Account verification will be connected " +
                                    "to the Campus Companion server in the next " +
                                    "development stage."
                    )
                    .setPositiveButton("OK", null)
                    .show();

        });


        // CREATE ACCOUNT BUTTON
        createAccountButton.setOnClickListener(v -> {

            new AlertDialog.Builder(StudentActivity.this)
                    .setTitle("Create Account")
                    .setMessage(
                            "New students can create their Campus Companion " +
                                    "account here.\n\n" +
                                    "You will need your student number and registration " +
                                    "details to create your account."
                    )
                    .setPositiveButton("OK", null)
                    .show();

        });


        // FORGOT PASSWORD
        forgotPassword.setOnClickListener(v -> {

            new AlertDialog.Builder(StudentActivity.this)
                    .setTitle("Forgot Password?")
                    .setMessage(
                            "Enter the student number or email associated " +
                                    "with your account to begin the password recovery process."
                    )
                    .setPositiveButton("OK", null)
                    .show();

        });


        // PASSWORD SHOW / HIDE BUTTON
        passwordEyeButton.setOnClickListener(v -> {

            if (passwordVisible) {

                // Hide the password
                passwordInput.setTransformationMethod(
                        PasswordTransformationMethod.getInstance()
                );

                passwordVisible = false;

            } else {

                // Show the password
                passwordInput.setTransformationMethod(
                        HideReturnsTransformationMethod.getInstance()
                );

                passwordVisible = true;
            }

            // Keep the cursor at the end of the password
            passwordInput.setSelection(
                    passwordInput.getText().length()
            );

        });


        // BACK BUTTON
        backButton.setOnClickListener(v -> {

            finish();

        });


        // STUDENT PORTAL HELP
        helpButton.setOnClickListener(v -> {

            new AlertDialog.Builder(StudentActivity.this)
                    .setTitle("Student Portal Guide")
                    .setMessage(
                            "Welcome to the Campus Companion Student Portal!\n\n" +

                                    "1. STUDENT NUMBER OR EMAIL\n" +
                                    "Enter either your 9-digit student number " +
                                    "or the email associated with your account.\n\n" +

                                    "2. PASSWORD\n" +
                                    "Enter your account password. " +
                                    "Tap the eye button to show or hide your password.\n\n" +

                                    "3. SIGN IN\n" +
                                    "After entering your details, press SIGN IN " +
                                    "to access your Student Portal.\n\n" +

                                    "4. CREATE ACCOUNT\n" +
                                    "If you do not have an account, press CREATE ACCOUNT " +
                                    "to begin registration.\n\n" +

                                    "5. FORGOT PASSWORD\n" +
                                    "If you cannot remember your password, select " +
                                    "Forgot Password? to begin the recovery process."
                    )
                    .setPositiveButton("GOT IT", null)
                    .show();

        });
    }
}