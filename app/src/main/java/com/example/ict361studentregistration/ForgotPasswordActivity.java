package com.example.ict361studentregistration;

import android.content.Intent;
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
                        "Enter your lecturer ID or email"
                );

                recoveryIdentifierInput.requestFocus();

                return;
            }

            // Check numeric ID (5-10 digits)
            boolean isLecturerId = identifier.matches("\\d{5,10}");

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
            if (!isLecturerId &&
                    !isEmail &&
                    !isPhone) {

                recoveryIdentifierInput.setError(
                        "Enter a valid lecturer ID or email address"
                );

                recoveryIdentifierInput.requestFocus();

                return;
            }

            // Navigate to VerifyCodeActivity for password recovery verification
            Intent intent = new Intent(ForgotPasswordActivity.this, VerifyCodeActivity.class);
            intent.putExtra("EMAIL", identifier);
            intent.putExtra("EXPECTED_CODE", "123456");
            intent.putExtra("NEXT_ACTIVITY", "RESET_PASSWORD");
            intent.putExtra("USER_TYPE", "LECTURER");
            startActivity(intent);
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