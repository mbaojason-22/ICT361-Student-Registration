package com.example.ict361studentregistration;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.security.SecureRandom;

public class CreateAccountStep2Activity extends AppCompatActivity {

    private EditText contactInput;
    private EditText createPasswordInput;
    private EditText confirmPasswordInput;

    private Button createAccountButton;
    private Button suggestPasswordButton;

    private ImageButton backButton;
    private ImageButton helpButton;
    private ImageButton createPasswordEyeButton;
    private ImageButton confirmPasswordEyeButton;

    private TextView backToStep1;

    private TextView passwordStrengthText;
    private ProgressBar passwordStrengthBar;

    private TextView passwordRequirementLength;
    private TextView passwordRequirementUppercase;
    private TextView passwordRequirementLowercase;
    private TextView passwordRequirementNumber;
    private TextView passwordRequirementSpecial;

    private boolean createPasswordVisible = false;
    private boolean confirmPasswordVisible = false;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load Step 2
        setContentView(R.layout.activity_create_account_step2);

        // Connect input fields
        contactInput = findViewById(R.id.contact_input);
        createPasswordInput = findViewById(R.id.create_password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);

        // Connect buttons
        createAccountButton = findViewById(
                R.id.create_account_final_button
        );

        suggestPasswordButton = findViewById(
                R.id.suggest_password_button
        );

        // Connect navigation
        backButton = findViewById(
                R.id.create_account_step2_back_button
        );

        helpButton = findViewById(
                R.id.create_account_step2_help_button
        );

        createPasswordEyeButton = findViewById(
                R.id.create_password_eye_button
        );

        confirmPasswordEyeButton = findViewById(
                R.id.confirm_password_eye_button
        );

        backToStep1 = findViewById(
                R.id.back_to_step1
        );

        // Connect password strength section
        passwordStrengthText = findViewById(
                R.id.password_strength_text
        );

        passwordStrengthBar = findViewById(
                R.id.password_strength_bar
        );

        passwordRequirementLength = findViewById(
                R.id.password_requirement_length
        );

        passwordRequirementUppercase = findViewById(
                R.id.password_requirement_uppercase
        );

        passwordRequirementLowercase = findViewById(
                R.id.password_requirement_lowercase
        );

        passwordRequirementNumber = findViewById(
                R.id.password_requirement_number
        );

        passwordRequirementSpecial = findViewById(
                R.id.password_requirement_special
        );

        // Watch the password while the student types
        createPasswordInput.addTextChangedListener(
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
                        updatePasswordStrength(
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

        // CREATE ACCOUNT
        createAccountButton.setOnClickListener(v -> {

            String contact = contactInput
                    .getText()
                    .toString()
                    .trim();

            String password = createPasswordInput
                    .getText()
                    .toString();

            String confirmPassword = confirmPasswordInput
                    .getText()
                    .toString();

            // Validate contact
            if (contact.isEmpty()) {

                contactInput.setError(
                        "Enter your email or phone number"
                );

                contactInput.requestFocus();

                return;
            }

            boolean isEmail = Patterns.EMAIL_ADDRESS
                    .matcher(contact)
                    .matches();

            boolean isPhone = contact.matches(
                    "^\\+?[0-9]{7,15}$"
            );

            if (!isEmail && !isPhone) {

                contactInput.setError(
                        "Enter a valid email or phone number"
                );

                contactInput.requestFocus();

                return;
            }

            // Validate password
            if (password.isEmpty()) {

                createPasswordInput.setError(
                        "Create a password"
                );

                createPasswordInput.requestFocus();

                return;
            }

            if (password.length() < 8) {

                createPasswordInput.setError(
                        "Password must be at least 8 characters"
                );

                createPasswordInput.requestFocus();

                return;
            }

            // Require a strong password
            if (calculatePasswordScore(password) < 4) {

                createPasswordInput.setError(
                        "Please create a stronger password"
                );

                createPasswordInput.requestFocus();

                return;
            }

            // Validate confirmation
            if (confirmPassword.isEmpty()) {

                confirmPasswordInput.setError(
                        "Re-enter your password"
                );

                confirmPasswordInput.requestFocus();

                return;
            }

            if (!password.equals(confirmPassword)) {

                confirmPasswordInput.setError(
                        "Passwords do not match"
                );

                confirmPasswordInput.requestFocus();

                return;
            }

            /*
             * STEP 2 COMPLETE
             *
             * Send the contact information to the
             * verification screen.
             */
            Intent intent = new Intent(
                    CreateAccountStep2Activity.this,
                    VerificationActivity.class
            );

            // Pass the contact to VerificationActivity
            intent.putExtra(
                    "contact",
                    contact
            );

            // Also pass Step 1 details forward
            String firstName = getIntent().getStringExtra(
                    "first_name"
            );

            String lastName = getIntent().getStringExtra(
                    "last_name"
            );

            String studentNumber = getIntent().getStringExtra(
                    "student_number"
            );

            String programme = getIntent().getStringExtra(
                    "programme"
            );

            intent.putExtra(
                    "first_name",
                    firstName
            );

            intent.putExtra(
                    "last_name",
                    lastName
            );

            intent.putExtra(
                    "student_number",
                    studentNumber
            );

            intent.putExtra(
                    "programme",
                    programme
            );

            startActivity(intent);

            // Remove Step 2 from the back stack
            finish();
        });

        // SUGGEST STRONG PASSWORD
        suggestPasswordButton.setOnClickListener(v -> {

            String suggestedPassword =
                    generateStrongPassword();

            createPasswordInput.setText(
                    suggestedPassword
            );

            confirmPasswordInput.setText(
                    suggestedPassword
            );

            createPasswordInput.setSelection(
                    createPasswordInput.getText().length()
            );

            new AlertDialog.Builder(
                    CreateAccountStep2Activity.this
            )
                    .setTitle("Strong Password Suggested")
                    .setMessage(
                            "A strong password has been generated for you.\n\n" +
                                    "Tap the eye button if you want to see it."
                    )
                    .setPositiveButton(
                            "GOT IT",
                            null
                    )
                    .show();
        });

        // SHOW / HIDE CREATE PASSWORD
        createPasswordEyeButton.setOnClickListener(v -> {

            if (createPasswordVisible) {

                createPasswordInput.setTransformationMethod(
                        PasswordTransformationMethod.getInstance()
                );

                createPasswordVisible = false;

            } else {

                createPasswordInput.setTransformationMethod(
                        HideReturnsTransformationMethod.getInstance()
                );

                createPasswordVisible = true;
            }

            createPasswordInput.setSelection(
                    createPasswordInput.getText().length()
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

        // BACK BUTTON
        backButton.setOnClickListener(v -> {
            finish();
        });

        // BACK TO STEP 1
        backToStep1.setOnClickListener(v -> {
            finish();
        });

        // HELP
        helpButton.setOnClickListener(v -> {

            new AlertDialog.Builder(
                    CreateAccountStep2Activity.this
            )
                    .setTitle("Create Account — Step 2")
                    .setMessage(
                            "Complete your account security details.\n\n" +

                                    "EMAIL OR PHONE NUMBER\n" +
                                    "Enter an email address or phone number " +
                                    "where you can receive your verification code.\n\n" +

                                    "PASSWORD\n" +
                                    "Create a strong password. " +
                                    "The strength meter will guide you while you type.\n\n" +

                                    "SUGGEST A STRONG PASSWORD\n" +
                                    "Campus Companion can generate a strong password " +
                                    "for you automatically.\n\n" +

                                    "CONFIRM PASSWORD\n" +
                                    "Enter the same password again.\n\n" +

                                    "When everything is correct, press CREATE ACCOUNT."
                    )
                    .setPositiveButton(
                            "GOT IT",
                            null
                    )
                    .show();
        });
    }

    // Calculate password strength
    private int calculatePasswordScore(String password) {

        int score = 0;

        boolean hasLength = password.length() >= 8;
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(
                ".*[^a-zA-Z0-9].*"
        );

        if (hasLength) score++;
        if (hasUppercase) score++;
        if (hasLowercase) score++;
        if (hasNumber) score++;
        if (hasSpecial) score++;

        return score;
    }

    // Update the strength meter and checklist
    private void updatePasswordStrength(String password) {

        boolean hasLength = password.length() >= 8;
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(
                ".*[^a-zA-Z0-9].*"
        );

        int score = 0;

        if (hasLength) score++;
        if (hasUppercase) score++;
        if (hasLowercase) score++;
        if (hasNumber) score++;
        if (hasSpecial) score++;

        passwordStrengthBar.setProgress(score);

        // Update requirements
        updateRequirement(
                passwordRequirementLength,
                hasLength,
                "At least 8 characters"
        );

        updateRequirement(
                passwordRequirementUppercase,
                hasUppercase,
                "Uppercase letter"
        );

        updateRequirement(
                passwordRequirementLowercase,
                hasLowercase,
                "Lowercase letter"
        );

        updateRequirement(
                passwordRequirementNumber,
                hasNumber,
                "Number"
        );

        updateRequirement(
                passwordRequirementSpecial,
                hasSpecial,
                "Special character"
        );

        // Update strength label
        if (password.isEmpty()) {

            passwordStrengthText.setText("Not set");
            passwordStrengthText.setTextColor(Color.GRAY);

        } else if (score <= 1) {

            passwordStrengthText.setText("Very weak");
            passwordStrengthText.setTextColor(Color.RED);

        } else if (score == 2) {

            passwordStrengthText.setText("Weak");
            passwordStrengthText.setTextColor(
                    Color.rgb(220, 120, 0)
            );

        } else if (score == 3) {

            passwordStrengthText.setText("Medium");
            passwordStrengthText.setTextColor(
                    Color.rgb(220, 170, 0)
            );

        } else if (score == 4) {

            passwordStrengthText.setText("Strong");
            passwordStrengthText.setTextColor(
                    Color.rgb(30, 150, 80)
            );

        } else {

            passwordStrengthText.setText("Very strong");
            passwordStrengthText.setTextColor(
                    Color.rgb(20, 130, 70)
            );
        }
    }

    // Update one password requirement
    private void updateRequirement(
            TextView textView,
            boolean satisfied,
            String requirement
    ) {

        if (satisfied) {

            textView.setText(
                    "✓ " + requirement
            );

            textView.setTextColor(
                    Color.rgb(30, 150, 80)
            );

        } else {

            textView.setText(
                    "○ " + requirement
            );

            textView.setTextColor(
                    Color.GRAY
            );
        }
    }

    // Generate a random strong password
    private String generateStrongPassword() {

        String uppercase =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        String lowercase =
                "abcdefghijklmnopqrstuvwxyz";

        String numbers =
                "0123456789";

        String special =
                "!@#$%^&*";

        String allCharacters =
                uppercase +
                        lowercase +
                        numbers +
                        special;

        StringBuilder password =
                new StringBuilder();

        // Guarantee at least one character of every type
        password.append(
                uppercase.charAt(
                        secureRandom.nextInt(
                                uppercase.length()
                        )
                )
        );

        password.append(
                lowercase.charAt(
                        secureRandom.nextInt(
                                lowercase.length()
                        )
                )
        );

        password.append(
                numbers.charAt(
                        secureRandom.nextInt(
                                numbers.length()
                        )
                )
        );

        password.append(
                special.charAt(
                        secureRandom.nextInt(
                                special.length()
                        )
                )
        );

        // Add extra characters
        for (int i = 0; i < 10; i++) {

            password.append(
                    allCharacters.charAt(
                            secureRandom.nextInt(
                                    allCharacters.length()
                            )
                    )
            );
        }

        // Shuffle the password
        char[] characters =
                password.toString().toCharArray();

        for (
                int i = characters.length - 1;
                i > 0;
                i--
        ) {

            int randomIndex =
                    secureRandom.nextInt(i + 1);

            char temporary =
                    characters[i];

            characters[i] =
                    characters[randomIndex];

            characters[randomIndex] =
                    temporary;
        }

        return new String(characters);
    }
}