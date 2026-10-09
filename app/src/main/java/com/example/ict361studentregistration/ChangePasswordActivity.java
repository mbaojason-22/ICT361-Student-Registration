package com.example.ict361studentregistration;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText currentPassword;
    private EditText newPassword;
    private EditText confirmPassword;

    private ImageButton currentPasswordEye;
    private ImageButton newPasswordEye;
    private ImageButton confirmPasswordEye;

    private TextView passwordStrengthLabel;
    private ProgressBar passwordStrengthBar;

    private TextView requirementLength;
    private TextView requirementUpper;
    private TextView requirementLower;
    private TextView requirementNumber;
    private TextView requirementSpecial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_password);

        setupSystemBars();
        setupViews();
        setupPasswordEyes();
        setupPasswordStrength();
        setupButtons();
    }

    // =========================================================
    // SYSTEM BARS
    // =========================================================

    private void setupSystemBars() {

        View rootView =
                findViewById(R.id.change_password_root);

        ViewCompat.setOnApplyWindowInsetsListener(
                rootView,
                (view, windowInsets) -> {

                    Insets insets =
                            windowInsets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    view.setPadding(
                            view.getPaddingLeft(),
                            insets.top,
                            view.getPaddingRight(),
                            view.getPaddingBottom()
                                    + insets.bottom
                    );

                    return windowInsets;
                }
        );
    }

    // =========================================================
    // FIND VIEWS
    // =========================================================

    private void setupViews() {

        currentPassword =
                findViewById(R.id.current_password);

        newPassword =
                findViewById(R.id.new_password);

        confirmPassword =
                findViewById(R.id.confirm_password);

        currentPasswordEye =
                findViewById(R.id.current_password_eye);

        newPasswordEye =
                findViewById(R.id.new_password_eye);

        confirmPasswordEye =
                findViewById(R.id.confirm_password_eye);

        passwordStrengthLabel =
                findViewById(R.id.password_strength_label);

        passwordStrengthBar =
                findViewById(R.id.password_strength_bar);

        requirementLength =
                findViewById(
                        R.id.password_requirement_length
                );

        requirementUpper =
                findViewById(
                        R.id.password_requirement_upper
                );

        requirementLower =
                findViewById(
                        R.id.password_requirement_lower
                );

        requirementNumber =
                findViewById(
                        R.id.password_requirement_number
                );

        requirementSpecial =
                findViewById(
                        R.id.password_requirement_special
                );
    }

    // =========================================================
    // PASSWORD EYES
    // =========================================================

    private void setupPasswordEyes() {

        currentPasswordEye.setOnClickListener(v ->
                togglePasswordVisibility(currentPassword)
        );

        newPasswordEye.setOnClickListener(v ->
                togglePasswordVisibility(newPassword)
        );

        confirmPasswordEye.setOnClickListener(v ->
                togglePasswordVisibility(confirmPassword)
        );
    }

    private void togglePasswordVisibility(EditText field) {

        int cursorPosition = field.getSelectionStart();

        TransformationMethod transformation =
                field.getTransformationMethod();

        if (transformation instanceof PasswordTransformationMethod) {

            // Show password
            field.setTransformationMethod(null);

        } else {

            // Hide password
            field.setTransformationMethod(
                    PasswordTransformationMethod.getInstance()
            );
        }

        // Keep cursor in the same position.
        if (cursorPosition >= 0) {

            field.setSelection(
                    Math.min(
                            cursorPosition,
                            field.getText().length()
                    )
            );
        }
    }

    // =========================================================
    // PASSWORD STRENGTH
    // =========================================================

    private void setupPasswordStrength() {

        newPassword.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                        // Not needed.
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        updatePasswordStrength(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                        // Not needed.
                    }
                }
        );
    }

    private void updatePasswordStrength(String password) {

        if (password.isEmpty()) {

            passwordStrengthLabel.setText(
                    "Password strength: Not entered"
            );

            passwordStrengthLabel.setTextColor(
                    0xFF777777
            );

            passwordStrengthBar.setProgress(0);

            updateRequirement(
                    requirementLength,
                    false,
                    "At least 8 characters"
            );

            updateRequirement(
                    requirementUpper,
                    false,
                    "Include an uppercase letter"
            );

            updateRequirement(
                    requirementLower,
                    false,
                    "Include a lowercase letter"
            );

            updateRequirement(
                    requirementNumber,
                    false,
                    "Include a number"
            );

            updateRequirement(
                    requirementSpecial,
                    false,
                    "Include a special character"
            );

            return;
        }

        boolean hasLength =
                password.length() >= 8;

        boolean hasUpper =
                password.matches(".*[A-Z].*");

        boolean hasLower =
                password.matches(".*[a-z].*");

        boolean hasNumber =
                password.matches(".*[0-9].*");

        boolean hasSpecial =
                password.matches(".*[^A-Za-z0-9].*");

        int score = 0;

        if (hasLength) score++;
        if (hasUpper) score++;
        if (hasLower) score++;
        if (hasNumber) score++;
        if (hasSpecial) score++;

        passwordStrengthBar.setMax(5);
        passwordStrengthBar.setProgress(score);

        if (score <= 1) {

            passwordStrengthLabel.setText(
                    "Password strength: Very Weak"
            );

            passwordStrengthLabel.setTextColor(
                    0xFFE53935
            );

        } else if (score == 2) {

            passwordStrengthLabel.setText(
                    "Password strength: Weak"
            );

            passwordStrengthLabel.setTextColor(
                    0xFFE57C00
            );

        } else if (score == 3) {

            passwordStrengthLabel.setText(
                    "Password strength: Fair"
            );

            passwordStrengthLabel.setTextColor(
                    0xFFE57C00
            );

        } else if (score == 4) {

            passwordStrengthLabel.setText(
                    "Password strength: Strong"
            );

            passwordStrengthLabel.setTextColor(
                    0xFF2E7D32
            );

        } else {

            passwordStrengthLabel.setText(
                    "Password strength: Very Strong"
            );

            passwordStrengthLabel.setTextColor(
                    0xFF2E7D32
            );
        }

        updateRequirement(
                requirementLength,
                hasLength,
                "At least 8 characters"
        );

        updateRequirement(
                requirementUpper,
                hasUpper,
                "Include an uppercase letter"
        );

        updateRequirement(
                requirementLower,
                hasLower,
                "Include a lowercase letter"
        );

        updateRequirement(
                requirementNumber,
                hasNumber,
                "Include a number"
        );

        updateRequirement(
                requirementSpecial,
                hasSpecial,
                "Include a special character"
        );
    }

    private void updateRequirement(
            TextView view,
            boolean satisfied,
            String text
    ) {

        if (satisfied) {

            view.setText(
                    "✓ " + text
            );

            view.setTextColor(
                    0xFF2E7D32
            );

        } else {

            view.setText(
                    "○ " + text
            );

            view.setTextColor(
                    0xFF777777
            );
        }
    }

    // =========================================================
    // BUTTONS
    // =========================================================

    private void setupButtons() {

        // Back
        View backButton =
                findViewById(
                        R.id.change_password_back_button
                );

        backButton.setOnClickListener(
                v -> finish()
        );

        // Cancel
        Button cancelButton =
                findViewById(
                        R.id.cancel_change_password_button
                );

        cancelButton.setOnClickListener(
                v -> finish()
        );

        // Update password
        Button changePasswordButton =
                findViewById(
                        R.id.change_password_button
                );

        changePasswordButton.setOnClickListener(
                v -> updatePassword()
        );
    }

    // =========================================================
    // UPDATE PASSWORD
    // =========================================================

    private void updatePassword() {

        String current =
                currentPassword
                        .getText()
                        .toString();

        String newPass =
                newPassword
                        .getText()
                        .toString();

        String confirm =
                confirmPassword
                        .getText()
                        .toString();

        // Current password required
        if (current.isEmpty()) {

            currentPassword.setError(
                    "Enter your current password"
            );

            currentPassword.requestFocus();

            return;
        }

        // New password required
        if (newPass.isEmpty()) {

            newPassword.setError(
                    "Create a new password"
            );

            newPassword.requestFocus();

            return;
        }

        // Password requirements
        boolean validPassword =
                newPass.length() >= 8
                        && newPass.matches(
                        ".*[A-Z].*"
                )
                        && newPass.matches(
                        ".*[a-z].*"
                )
                        && newPass.matches(
                        ".*[0-9].*"
                )
                        && newPass.matches(
                        ".*[^A-Za-z0-9].*"
                );

        if (!validPassword) {

            Toast.makeText(
                    this,
                    "Please meet all password requirements.",
                    Toast.LENGTH_LONG
            ).show();

            newPassword.requestFocus();

            return;
        }

        // Confirm password required
        if (confirm.isEmpty()) {

            confirmPassword.setError(
                    "Confirm your new password"
            );

            confirmPassword.requestFocus();

            return;
        }

        // Passwords must match
        if (!newPass.equals(confirm)) {

            confirmPassword.setError(
                    "Passwords do not match"
            );

            confirmPassword.requestFocus();

            return;
        }

        // New password must differ from current
        if (current.equals(newPass)) {

            newPassword.setError(
                    "Choose a different password"
            );

            newPassword.requestFocus();

            return;
        }

        // Confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Update Password")
                .setMessage(
                        "Are you sure you want to change your password?"
                )
                .setNegativeButton(
                        "CANCEL",
                        null
                )
                .setPositiveButton(
                        "UPDATE",
                        (dialog, which) -> {

                            showSuccessMessage();

                        }
                )
                .show();
    }

    private void showSuccessMessage() {

        new AlertDialog.Builder(this)
                .setTitle("Password Updated ✓")
                .setMessage(
                        "Your password has been updated successfully."
                                + "\n\n"
                                + "You can now use your new password "
                                + "the next time you sign in."
                )
                .setPositiveButton(
                        "DONE",
                        (dialog, which) -> finish()
                )
                .setCancelable(false)
                .show();
    }
}