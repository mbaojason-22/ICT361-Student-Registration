package com.example.ict361studentregistration;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText newPasswordInput;
    private EditText confirmPasswordInput;

    private ImageButton newPasswordEyeButton;
    private ImageButton confirmPasswordEyeButton;

    private TextView strengthText;

    private Button updatePasswordButton;

    private boolean newPasswordVisible = false;
    private boolean confirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load Reset Password screen
        setContentView(R.layout.activity_reset_password);

        // Connect XML views
        newPasswordInput =
                findViewById(R.id.new_password_input);

        confirmPasswordInput =
                findViewById(R.id.confirm_new_password_input);

        newPasswordEyeButton =
                findViewById(R.id.new_password_eye_button);

        confirmPasswordEyeButton =
                findViewById(
                        R.id.confirm_new_password_eye_button
                );

        strengthText =
                findViewById(R.id.new_password_strength);

        updatePasswordButton =
                findViewById(R.id.update_password_button);

        // LIVE PASSWORD STRENGTH
        newPasswordInput.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        updateStrength(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );

        // SHOW / HIDE NEW PASSWORD
        newPasswordEyeButton.setOnClickListener(v -> {

            if (newPasswordVisible) {

                newPasswordInput.setTransformationMethod(
                        PasswordTransformationMethod.getInstance()
                );

                newPasswordVisible = false;

            } else {

                newPasswordInput.setTransformationMethod(
                        HideReturnsTransformationMethod.getInstance()
                );

                newPasswordVisible = true;
            }

            newPasswordInput.setSelection(
                    newPasswordInput.getText().length()
            );
        });

        // SHOW / HIDE CONFIRM PASSWORD
        confirmPasswordEyeButton.setOnClickListener(v -> {

            if (confirmPasswordVisible) {

                confirmPasswordInput.setTransformationMethod(
                        PasswordTransformationMethod.getInstance()
                );

                confirmPasswordVisible = false;

            } else {

                confirmPasswordInput.setTransformationMethod(
                        HideReturnsTransformationMethod.getInstance()
                );

                confirmPasswordVisible = true;
            }

            confirmPasswordInput.setSelection(
                    confirmPasswordInput.getText().length()
            );
        });

        // UPDATE PASSWORD
        updatePasswordButton.setOnClickListener(v -> {

            String password =
                    newPasswordInput
                            .getText()
                            .toString();

            String confirmPassword =
                    confirmPasswordInput
                            .getText()
                            .toString();

            // Check empty password
            if (password.isEmpty()) {

                newPasswordInput.setError(
                        "Create a new password"
                );

                newPasswordInput.requestFocus();

                return;
            }

            // Check minimum length
            if (password.length() < 8) {

                newPasswordInput.setError(
                        "Password must be at least 8 characters"
                );

                newPasswordInput.requestFocus();

                return;
            }

            // Check password strength
            if (calculateScore(password) < 4) {

                newPasswordInput.setError(
                        "Please choose a stronger password"
                );

                newPasswordInput.requestFocus();

                return;
            }

            // Check confirmation
            if (confirmPassword.isEmpty()) {

                confirmPasswordInput.setError(
                        "Re-enter your password"
                );

                confirmPasswordInput.requestFocus();

                return;
            }

            // Compare passwords
            if (!password.equals(confirmPassword)) {

                confirmPasswordInput.setError(
                        "Passwords do not match"
                );

                confirmPasswordInput.requestFocus();

                return;
            }

            /*
             * PASSWORD UPDATE SUCCESS
             *
             * In the final backend version, this is where the
             * server will actually update the password hash.
             */
            new AlertDialog.Builder(
                    ResetPasswordActivity.this
            )
                    .setTitle("Password Updated ✓")
                    .setMessage(
                            "Your password has been updated successfully.\n\n" +
                                    "You can now sign in using your new password."
                    )
                    .setCancelable(false)
                    .setPositiveButton(
                            "BACK TO SIGN IN",
                            (dialog, which) -> {

                                /*
                                 * Return to the existing Student Portal
                                 * instead of going back to the recovery
                                 * screens.
                                 */
                                Intent intent =
                                        new Intent(
                                                ResetPasswordActivity.this,
                                                StudentActivity.class
                                        );

                                /*
                                 * CLEAR_TOP:
                                 * Remove PasswordRecovery,
                                 * RecoveryVerification and ResetPassword
                                 * from above StudentActivity.
                                 *
                                 * SINGLE_TOP:
                                 * Reuse the existing StudentActivity
                                 * instead of creating another one.
                                 */
                                intent.setFlags(
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                                );

                                startActivity(intent);

                                finish();
                            }
                    )
                    .show();
        });
    }

    // Calculate password strength
    private int calculateScore(
            String password
    ) {

        int score = 0;

        if (password.length() >= 8) {
            score++;
        }

        if (password.matches(
                ".*[A-Z].*"
        )) {
            score++;
        }

        if (password.matches(
                ".*[a-z].*"
        )) {
            score++;
        }

        if (password.matches(
                ".*\\d.*"
        )) {
            score++;
        }

        if (password.matches(
                ".*[^a-zA-Z0-9].*"
        )) {
            score++;
        }

        return score;
    }

    // Update password strength text
    private void updateStrength(
            String password
    ) {

        int score =
                calculateScore(password);

        if (password.isEmpty()) {

            strengthText.setText(
                    "Password strength: Not set"
            );

            strengthText.setTextColor(
                    Color.GRAY
            );

        } else if (score <= 1) {

            strengthText.setText(
                    "Password strength: Very weak"
            );

            strengthText.setTextColor(
                    Color.RED
            );

        } else if (score == 2) {

            strengthText.setText(
                    "Password strength: Weak"
            );

            strengthText.setTextColor(
                    Color.rgb(
                            220,
                            120,
                            0
                    )
            );

        } else if (score == 3) {

            strengthText.setText(
                    "Password strength: Medium"
            );

            strengthText.setTextColor(
                    Color.rgb(
                            220,
                            170,
                            0
                    )
            );

        } else if (score == 4) {

            strengthText.setText(
                    "Password strength: Strong"
            );

            strengthText.setTextColor(
                    Color.rgb(
                            30,
                            150,
                            80
                    )
            );

        } else {

            strengthText.setText(
                    "Password strength: Very strong"
            );

            strengthText.setTextColor(
                    Color.rgb(
                            20,
                            130,
                            70
                    )
            );
        }
    }
}