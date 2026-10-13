package com.example.ict361studentregistration;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BackupEmailActivity extends AppCompatActivity {

    private TextView currentBackupEmail;
    private TextView backupEmailStatus;

    private EditText backupEmailInput;
    private EditText confirmBackupEmailInput;

    private Button saveBackupEmailButton;
    private Button removeBackupEmailButton;
    private Button cancelBackupEmailButton;

    private SharedPreferences preferences;

    private static final String PREFS_NAME =
            "campus_companion_profile";

    private static final String KEY_BACKUP_EMAIL =
            "backup_email";

    private static final String KEY_BACKUP_EMAIL_VERIFIED =
            "backup_email_verified";

    // Demonstration only. The real backend will send verification codes.
    private static final String DEMO_VERIFICATION_CODE =
            "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_backup_email);

        preferences = getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
        );

        setupSystemBars();
        setupViews();
        setupButtons();

        displayCurrentBackupEmail();
    }

    // =========================================================
    // SYSTEM BARS
    // =========================================================

    private void setupSystemBars() {

        View rootView =
                findViewById(R.id.backup_email_root);

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

        currentBackupEmail =
                findViewById(R.id.current_backup_email);

        backupEmailStatus =
                findViewById(R.id.backup_email_status);

        backupEmailInput =
                findViewById(R.id.backup_email_input);

        confirmBackupEmailInput =
                findViewById(
                        R.id.confirm_backup_email_input
                );

        saveBackupEmailButton =
                findViewById(
                        R.id.save_backup_email_button
                );

        removeBackupEmailButton =
                findViewById(
                        R.id.remove_backup_email_button
                );

        cancelBackupEmailButton =
                findViewById(
                        R.id.cancel_backup_email_button
                );
    }

    // =========================================================
    // DISPLAY CURRENT BACKUP EMAIL
    // =========================================================

    private void displayCurrentBackupEmail() {

        String savedEmail =
                preferences.getString(
                        KEY_BACKUP_EMAIL,
                        "Not added"
                );

        boolean verified =
                preferences.getBoolean(
                        KEY_BACKUP_EMAIL_VERIFIED,
                        false
                );

        currentBackupEmail.setText(savedEmail);

        if (savedEmail.equals("Not added")
                || savedEmail.trim().isEmpty()) {

            backupEmailStatus.setText("Not added");

            backupEmailStatus.setTextColor(
                    0xFF777777
            );

        } else if (verified) {

            backupEmailStatus.setText("✓ Verified");

            backupEmailStatus.setTextColor(
                    0xFF2E7D32
            );

        } else {

            backupEmailStatus.setText("Not verified");

            backupEmailStatus.setTextColor(
                    0xFFE57C00
            );
        }

        // Hide Remove if there is no backup email.
        removeBackupEmailButton.setVisibility(
                savedEmail.equals("Not added")
                        || savedEmail.trim().isEmpty()
                        ? View.GONE
                        : View.VISIBLE
        );
    }

    // =========================================================
    // BUTTONS
    // =========================================================

    private void setupButtons() {

        // Back
        View backButton =
                findViewById(
                        R.id.backup_email_back_button
                );

        backButton.setOnClickListener(
                v -> finish()
        );

        // Save
        saveBackupEmailButton.setOnClickListener(
                v -> validateBackupEmail()
        );

        // Remove
        removeBackupEmailButton.setOnClickListener(
                v -> confirmRemoveBackupEmail()
        );

        // Cancel
        cancelBackupEmailButton.setOnClickListener(
                v -> finish()
        );
    }

    // =========================================================
    // VALIDATE EMAIL
    // =========================================================

    private void validateBackupEmail() {

        String email =
                backupEmailInput.getText()
                        .toString()
                        .trim();

        String confirmEmail =
                confirmBackupEmailInput.getText()
                        .toString()
                        .trim();

        // Email is required.
        if (email.isEmpty()) {

            backupEmailInput.setError(
                    "Please enter your backup email."
            );

            backupEmailInput.requestFocus();

            return;
        }

        // Check email format.
        if (!Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()) {

            backupEmailInput.setError(
                    "Enter a valid email address."
            );

            backupEmailInput.requestFocus();

            return;
        }

        // Confirmation is required.
        if (confirmEmail.isEmpty()) {

            confirmBackupEmailInput.setError(
                    "Please confirm your email."
            );

            confirmBackupEmailInput.requestFocus();

            return;
        }

        // Both email entries must match.
        if (!email.equalsIgnoreCase(confirmEmail)) {

            confirmBackupEmailInput.setError(
                    "Email addresses do not match."
            );

            confirmBackupEmailInput.requestFocus();

            return;
        }

        // Backup email should differ from the primary email.
        String primaryEmail =
                preferences.getString(
                        "email",
                        ""
                ).trim();

        if (!primaryEmail.isEmpty()
                && email.equalsIgnoreCase(primaryEmail)) {

            backupEmailInput.setError(
                    "Use an email different from your primary email."
            );

            backupEmailInput.requestFocus();

            return;
        }

        // All checks passed.
        showVerificationDialog(email);
    }

    // =========================================================
    // EMAIL VERIFICATION
    // =========================================================

    private void showVerificationDialog(String email) {

        EditText verificationCodeInput =
                new EditText(this);

        verificationCodeInput.setHint(
                "Enter 6-digit verification code"
        );

        verificationCodeInput.setSingleLine(true);

        verificationCodeInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );

        verificationCodeInput.setFilters(
                new InputFilter[]{
                        new InputFilter.LengthFilter(6)
                }
        );

        verificationCodeInput.setPadding(
                32, 24, 32, 24
        );

        AlertDialog verificationDialog =
                new AlertDialog.Builder(this)
                        .setTitle("Verify Backup Email")
                        .setMessage(
                                "Verification is required before this email becomes active.\n\n"
                                        + "For this local demonstration, enter the code 123456.\n\n"
                                        + "No real email has been sent."
                        )
                        .setView(verificationCodeInput)
                        .setNegativeButton(
                                "CANCEL",
                                null
                        )
                        .setPositiveButton(
                                "VERIFY",
                                null
                        )
                        .create();

        verificationDialog.setOnShowListener(dialog -> {

            verificationDialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener(v -> {

                String enteredCode =
                        verificationCodeInput.getText()
                                .toString()
                                .trim();

                if (enteredCode.isEmpty()) {

                    verificationCodeInput.setError(
                            "Enter the verification code."
                    );

                    return;
                }

                if (enteredCode.length() != 6) {

                    verificationCodeInput.setError(
                            "The code must contain 6 digits."
                    );

                    return;
                }

                if (!enteredCode.equals(
                        DEMO_VERIFICATION_CODE
                )) {

                    verificationCodeInput.setError(
                            "Incorrect code. Try again."
                    );

                    return;
                }

                // Verification succeeded.
                saveVerifiedBackupEmail(email);

                verificationDialog.dismiss();
            });
        });

        verificationDialog.show();

        verificationDialog.getWindow()
                .setSoftInputMode(
                        WindowManager.LayoutParams
                                .SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
    }

    // =========================================================
    // SAVE VERIFIED EMAIL
    // =========================================================

    private void saveVerifiedBackupEmail(String email) {

        boolean saved = preferences.edit()
                .putString(
                        KEY_BACKUP_EMAIL,
                        email
                )
                .putBoolean(
                        KEY_BACKUP_EMAIL_VERIFIED,
                        true
                )
                .commit();

        if (!saved) {

            Toast.makeText(
                    this,
                    "Could not save the backup email. Please try again.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        displayCurrentBackupEmail();

        // Clear the form after successful verification.
        backupEmailInput.setText("");
        confirmBackupEmailInput.setText("");

        new AlertDialog.Builder(this)
                .setTitle("Backup Email Verified ✓")
                .setMessage(
                        "Your backup email has been saved and verified successfully.\n\n"
                                + email
                                + "\n\n"
                                + "You can now see it on your Profile."
                )
                .setPositiveButton(
                        "DONE",
                        (dialog, which) -> finish()
                )
                .setCancelable(false)
                .show();
    }

    // =========================================================
    // REMOVE BACKUP EMAIL
    // =========================================================

    private void confirmRemoveBackupEmail() {

        String savedEmail =
                preferences.getString(
                        KEY_BACKUP_EMAIL,
                        "Not added"
                );

        if (savedEmail.equals("Not added")
                || savedEmail.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "There is no backup email to remove.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Remove Backup Email")
                .setMessage(
                        "Are you sure you want to remove this backup email?\n\n"
                                + savedEmail
                                + "\n\n"
                                + "You will lose this additional account recovery option."
                )
                .setNegativeButton(
                        "CANCEL",
                        null
                )
                .setPositiveButton(
                        "REMOVE",
                        (dialog, which) -> removeBackupEmail()
                )
                .show();
    }

    private void removeBackupEmail() {

        boolean saved = preferences.edit()
                .putString(
                        KEY_BACKUP_EMAIL,
                        "Not added"
                )
                .putBoolean(
                        KEY_BACKUP_EMAIL_VERIFIED,
                        false
                )
                .commit();

        if (!saved) {

            Toast.makeText(
                    this,
                    "Could not remove the backup email. Please try again.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        backupEmailInput.setText("");
        confirmBackupEmailInput.setText("");

        displayCurrentBackupEmail();

        new AlertDialog.Builder(this)
                .setTitle("Backup Email Removed")
                .setMessage(
                        "Your backup email has been removed."
                )
                .setPositiveButton(
                        "OK",
                        null
                )
                .show();
    }
}