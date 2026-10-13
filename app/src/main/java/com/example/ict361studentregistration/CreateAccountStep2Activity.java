
package com.example.ict361studentregistration;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import java.security.SecureRandom;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccountStep2Activity extends AppCompatActivity {

    private EditText contactInput, createPasswordInput, confirmPasswordInput;
    private Button createAccountButton, suggestPasswordButton;
    private ImageButton backButton, helpButton;
    private ImageButton createPasswordEyeButton, confirmPasswordEyeButton;
    private TextView passwordStrengthText;
    private ProgressBar passwordStrengthBar;
    private TextView passwordRequirementLength, passwordRequirementUppercase;
    private TextView passwordRequirementLowercase, passwordRequirementNumber;
    private TextView passwordRequirementSpecial, backToStep1;

    private boolean createPasswordVisible = false;
    private boolean confirmPasswordVisible = false;
    private boolean requestInProgress = false;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_step2);

        contactInput = findViewById(R.id.contact_input);
        createPasswordInput = findViewById(R.id.create_password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);

        createAccountButton = findViewById(R.id.create_account_final_button);
        suggestPasswordButton = findViewById(R.id.suggest_password_button);

        backButton = findViewById(R.id.create_account_step2_back_button);
        helpButton = findViewById(R.id.create_account_step2_help_button);
        createPasswordEyeButton = findViewById(R.id.create_password_eye_button);
        confirmPasswordEyeButton = findViewById(R.id.confirm_password_eye_button);
        backToStep1 = findViewById(R.id.back_to_step1);

        passwordStrengthText = findViewById(R.id.password_strength_text);
        passwordStrengthBar = findViewById(R.id.password_strength_bar);

        passwordRequirementLength = findViewById(R.id.password_requirement_length);
        passwordRequirementUppercase = findViewById(R.id.password_requirement_uppercase);
        passwordRequirementLowercase = findViewById(R.id.password_requirement_lowercase);
        passwordRequirementNumber = findViewById(R.id.password_requirement_number);
        passwordRequirementSpecial = findViewById(R.id.password_requirement_special);

        passwordStrengthBar.setMax(5);

        createPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePasswordStrength(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        suggestPasswordButton.setOnClickListener(v -> {
            String password = generateStrongPassword();
            createPasswordInput.setText(password);
            confirmPasswordInput.setText(password);
            createPasswordInput.setSelection(password.length());

            new AlertDialog.Builder(this)
                    .setTitle("Strong Password Generated")
                    .setMessage("A strong password has been filled into both fields. Keep it somewhere safe.")
                    .setPositiveButton("OK", null)
                    .show();
        });

        createPasswordEyeButton.setOnClickListener(v -> {
            createPasswordVisible = !createPasswordVisible;
            createPasswordInput.setTransformationMethod(
                    createPasswordVisible
                            ? HideReturnsTransformationMethod.getInstance()
                            : PasswordTransformationMethod.getInstance()
            );
            createPasswordInput.setSelection(createPasswordInput.length());
        });

        confirmPasswordEyeButton.setOnClickListener(v -> {
            confirmPasswordVisible = !confirmPasswordVisible;
            confirmPasswordInput.setTransformationMethod(
                    confirmPasswordVisible
                            ? HideReturnsTransformationMethod.getInstance()
                            : PasswordTransformationMethod.getInstance()
            );
            confirmPasswordInput.setSelection(confirmPasswordInput.length());
        });

        createAccountButton.setOnClickListener(v -> continueRegistration());
        backButton.setOnClickListener(v -> finish());
        backToStep1.setOnClickListener(v -> finish());

        helpButton.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Create Account — Step 2")
                        .setMessage(
                                "Enter an email address you can access. " +
                                        "Create a password with at least 8 characters, " +
                                        "including uppercase and lowercase letters, a number, " +
                                        "and a special character. Confirm the password, then " +
                                        "press CREATE ACCOUNT. A verification code will be emailed to you."
                        )
                        .setPositiveButton("GOT IT", null)
                        .show()
        );
    }

    private void continueRegistration() {
        String email = contactInput.getText().toString().trim();
        String password = createPasswordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        String firstName = getIntent().getStringExtra("first_name");
        String lastName = getIntent().getStringExtra("last_name");
        String studentNumber = getIntent().getStringExtra("student_number");
        String programme = getIntent().getStringExtra("programme");

        if (TextUtils.isEmpty(email)
                || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            contactInput.setError("Enter a valid email address");
            contactInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(firstName)
                || TextUtils.isEmpty(lastName)
                || TextUtils.isEmpty(studentNumber)
                || TextUtils.isEmpty(programme)) {
            showMessage("Registration Details Missing",
                    "Go back and complete Step 1.");
            return;
        }

        if (!studentNumber.matches("\\d{9}")) {
            showMessage("Invalid Student Number",
                    "Your student number must contain 9 digits.");
            return;
        }

        if (password.length() < 8 || calculatePasswordScore(password) < 5) {
            createPasswordInput.setError(
                    "Use 8+ characters with uppercase, lowercase, number and special character"
            );
            createPasswordInput.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            confirmPasswordInput.requestFocus();
            return;
        }

        int programmeId = getProgrammeId(programme);
        if (programmeId == -1) {
            showMessage("Programme Not Supported",
                    "This programme is not available on the server yet.");
            return;
        }

        registerWithBackend(email, password, firstName, lastName,
                studentNumber, programme, programmeId);
    }

    private int getProgrammeId(String programme) {
        if ("Computer Science".equals(programme)) return 1;
        if ("Information Technology".equals(programme)) return 2;
        if ("Data Science".equals(programme)) return 3;
        return -1;
    }

    private void registerWithBackend(
            String email,
            String password,
            String firstName,
            String lastName,
            String studentNumber,
            String programme,
            int programmeId
    ) {
        if (requestInProgress) return;

        requestInProgress = true;
        createAccountButton.setEnabled(false);

        JsonObject request = new JsonObject();
        request.addProperty("student_number", studentNumber);
        request.addProperty("first_name", firstName);
        request.addProperty("last_name", lastName);
        request.addProperty("email", email);
        request.addProperty("password", password);
        request.addProperty("programme_id", programmeId);

        AuthApiClient.getService(this).register(request)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(
                            Call<JsonObject> call,
                            Response<JsonObject> response
                    ) {
                        requestInProgress = false;
                        createAccountButton.setEnabled(true);

                        JsonObject body = response.body();
                        boolean success = response.isSuccessful()
                                && body != null
                                && body.has("success")
                                && body.get("success").getAsBoolean();

                        if (success) {
                            Intent intent = new Intent(
                                    CreateAccountStep2Activity.this,
                                    VerificationActivity.class
                            );
                            intent.putExtra("contact", email);
                            intent.putExtra("password", password);
                            intent.putExtra("first_name", firstName);
                            intent.putExtra("last_name", lastName);
                            intent.putExtra("student_number", studentNumber);
                            intent.putExtra("programme", programme);
                            startActivity(intent);
                        } else {
                            String message = "Registration failed.";

                            if (body != null && body.has("message")) {
                                message = body.get("message").getAsString();
                            } else if (response.code() == 409) {
                                message = "This email or student number is already registered.";
                            } else if (response.code() == 500) {
                                message = "The server could not complete registration. Check the server email configuration and try again.";
                            }

                            showMessage("Registration Error", message);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        requestInProgress = false;
                        createAccountButton.setEnabled(true);
                        showMessage("Connection Error",
                                "Could not reach the registration server. " +
                                        "Check that the server is running and your device " +
                                        "can reach it on the same Wi-Fi network.\n\n" +
                                        t.getMessage());
                    }
                });
    }

    private int calculatePasswordScore(String password) {
        int score = 0;
        if (password.length() >= 8) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[^a-zA-Z0-9].*")) score++;
        return score;
    }

    private void updatePasswordStrength(String password) {
        boolean length = password.length() >= 8;
        boolean upper = password.matches(".*[A-Z].*");
        boolean lower = password.matches(".*[a-z].*");
        boolean number = password.matches(".*\\d.*");
        boolean special = password.matches(".*[^a-zA-Z0-9].*");

        passwordStrengthBar.setProgress(calculatePasswordScore(password));

        updateRequirement(passwordRequirementLength, length, "At least 8 characters");
        updateRequirement(passwordRequirementUppercase, upper, "Uppercase letter");
        updateRequirement(passwordRequirementLowercase, lower, "Lowercase letter");
        updateRequirement(passwordRequirementNumber, number, "Number");
        updateRequirement(passwordRequirementSpecial, special, "Special character");

        int score = calculatePasswordScore(password);
        if (password.isEmpty()) {
            passwordStrengthText.setText("Not set");
            passwordStrengthText.setTextColor(Color.GRAY);
        } else if (score <= 1) {
            passwordStrengthText.setText("Very weak");
            passwordStrengthText.setTextColor(Color.RED);
        } else if (score == 2) {
            passwordStrengthText.setText("Weak");
            passwordStrengthText.setTextColor(Color.rgb(220, 120, 0));
        } else if (score == 3) {
            passwordStrengthText.setText("Medium");
            passwordStrengthText.setTextColor(Color.rgb(220, 170, 0));
        } else if (score == 4) {
            passwordStrengthText.setText("Strong");
            passwordStrengthText.setTextColor(Color.rgb(30, 150, 80));
        } else {
            passwordStrengthText.setText("Very strong");
            passwordStrengthText.setTextColor(Color.rgb(20, 130, 70));
        }
    }

    private void updateRequirement(TextView view, boolean satisfied, String text) {
        view.setText((satisfied ? "✓ " : "○ ") + text);
        view.setTextColor(satisfied ? Color.rgb(30, 150, 80) : Color.GRAY);
    }

    private String generateStrongPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String special = "!@#$%^&*";
        String all = upper + lower + numbers + special;

        StringBuilder result = new StringBuilder();
        result.append(upper.charAt(secureRandom.nextInt(upper.length())));
        result.append(lower.charAt(secureRandom.nextInt(lower.length())));
        result.append(numbers.charAt(secureRandom.nextInt(numbers.length())));
        result.append(special.charAt(secureRandom.nextInt(special.length())));

        for (int i = 0; i < 10; i++) {
            result.append(all.charAt(secureRandom.nextInt(all.length())));
        }

        char[] chars = result.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = secureRandom.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    private void showMessage(String title, String message) {
        if (isFinishing() || isDestroyed()) return;
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
