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

public class PasswordRecoveryActivity extends AppCompatActivity {

    private EditText recoveryIdentifierInput;

    private Button sendRecoveryCodeButton;

    private ImageButton backButton;
    private ImageButton helpButton;

    private TextView backToSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load Password Recovery screen
        setContentView(R.layout.activity_password_recovery);

        // Connect XML views
        recoveryIdentifierInput =
                findViewById(R.id.recovery_identifier_input);

        sendRecoveryCodeButton =
                findViewById(R.id.send_recovery_code_button);

        backButton =
                findViewById(R.id.recovery_back_button);

        helpButton =
                findViewById(R.id.recovery_help_button);

        backToSignIn =
                findViewById(R.id.recovery_back_to_sign_in);

        // SEND VERIFICATION CODE
        sendRecoveryCodeButton.setOnClickListener(v -> {

            String identifier =
                    recoveryIdentifierInput
                            .getText()
                            .toString()
                            .trim();

            // Empty input
            if (identifier.isEmpty()) {

                recoveryIdentifierInput.setError(
                        "Enter your email or phone number"
                );

                recoveryIdentifierInput.requestFocus();

                return;
            }

            // Email validation
            boolean isEmail =
                    Patterns.EMAIL_ADDRESS
                            .matcher(identifier)
                            .matches();

            // Phone validation
            boolean isPhone =
                    identifier.matches(
                            "^\\+?[0-9]{7,15}$"
                    );

            // Only email OR phone is allowed
            if (!isEmail && !isPhone) {

                recoveryIdentifierInput.setError(
                        "Enter a valid email or phone number"
                );

                recoveryIdentifierInput.requestFocus();

                return;
            }

            /*
             * Open the password recovery verification screen.
             *
             * The contact information is passed to the next screen
             * so it can display a masked destination.
             */
            Intent intent =
                    new Intent(
                            PasswordRecoveryActivity.this,
                            RecoveryVerificationActivity.class
                    );

            intent.putExtra(
                    "recovery_identifier",
                    identifier
            );

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
                    PasswordRecoveryActivity.this
            )
                    .setTitle("Password Recovery")
                    .setMessage(
                            "Forgot your Campus Companion password?\n\n" +

                                    "1. Enter the email address or phone number " +
                                    "linked to your account.\n\n" +

                                    "2. Press SEND VERIFICATION CODE.\n\n" +

                                    "3. Enter the verification code sent to " +
                                    "your registered contact.\n\n" +

                                    "4. Create a new password after verification.\n\n" +

                                    "For this local demonstration, use " +
                                    "verification code 123456."
                    )
                    .setPositiveButton(
                            "GOT IT",
                            null
                    )
                    .show();
        });
    }
}