package com.example.ict361studentregistration;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText recoveryIdentifierInput;

    private Button sendRecoveryCodeButton;

    private ImageButton backButton;
    private ImageButton helpButton;

    private TextView backToSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load Forgot Password screen
        setContentView(R.layout.forgot_password_screen);

        // Connect XML views
        recoveryIdentifierInput =
                findViewById(R.id.recovery_identifier_input);

        sendRecoveryCodeButton =
                findViewById(R.id.send_recovery_code_button);

        backButton =
                findViewById(R.id.forgot_password_back_button);

        helpButton =
                findViewById(R.id.forgot_password_help_button);

        backToSignIn =
                findViewById(R.id.back_to_sign_in);

        // SEND VERIFICATION CODE
        sendRecoveryCodeButton.setOnClickListener(v -> {

            String identifier =
                    recoveryIdentifierInput
                            .getText()
                            .toString()
                            .trim();

            // Empty field
            if (identifier.isEmpty()) {

                recoveryIdentifierInput.setError(
                        "Enter your student number or email/phone"
                );

                recoveryIdentifierInput.requestFocus();

                return;
            }

            // Check student number
            boolean isStudentNumber =
                    identifier.matches("\\d{9}");

            // Check email
            boolean isEmail =
                    Patterns.EMAIL_ADDRESS
                            .matcher(identifier)
                            .matches();

            // Check phone number
            boolean isPhone =
                    identifier.matches(
                            "^\\+?[0-9]{7,15}$"
                    );

            // Invalid identifier
            if (!isStudentNumber &&
                    !isEmail &&
                    !isPhone) {

                recoveryIdentifierInput.setError(
                        "Enter a valid student number, email or phone number"
                );

                recoveryIdentifierInput.requestFocus();

                return;
            }

            // Local demonstration
            new AlertDialog.Builder(
                    ForgotPasswordActivity.this
            )
                    .setTitle("Verification Code Sent")
                    .setMessage(
                            "A verification code has been sent " +
                                    "to the contact information linked to your account.\n\n" +
                                    "For this local demonstration, use:\n\n" +
                                    "123456"
                    )
                    .setPositiveButton(
                            "CONTINUE",
                            null
                    )
                    .show();
        });

        // BACK BUTTON
        backButton.setOnClickListener(v -> {
            finish();
        });

        // BACK TO SIGN IN
        backToSignIn.setOnClickListener(v -> {
            finish();
        });

        // HELP
        helpButton.setOnClickListener(v -> {

            new AlertDialog.Builder(
                    ForgotPasswordActivity.this
            )
                    .setTitle("Password Recovery Help")
                    .setMessage(
                            "Forgot your Campus Companion password?\n\n" +

                                    "1. Enter your 9-digit student number, " +
                                    "registered email or phone number.\n\n" +

                                    "2. Press SEND VERIFICATION CODE.\n\n" +

                                    "3. Your account will be verified using " +
                                    "a verification code.\n\n" +

                                    "4. After verification, you will be able " +
                                    "to create a new password.\n\n" +

                                    "For this local demonstration, use:\n\n" +
                                    "123456"
                    )
                    .setPositiveButton(
                            "GOT IT",
                            null
                    )
                    .show();
        });
    }
}