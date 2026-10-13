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

import com.example.ict361studentregistration.data.room.AppDatabase;
import com.example.ict361studentregistration.data.room.Lecturer;
import com.example.ict361studentregistration.data.room.LecturerDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LecturerActivity extends AppCompatActivity {

    private Button signInButton;
    private Button createAccountButton;
    private TextView forgotPassword;
    private ImageButton backButton;
    private ImageButton helpButton;
    private ImageButton passwordEyeButton;
    private EditText lecturerIdInput;
    private EditText passwordInput;

    private boolean passwordVisible = false;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer);

        signInButton = findViewById(R.id.lecturer_sign_in_button);
        createAccountButton =
                findViewById(R.id.lecturer_create_account_button);
        forgotPassword = findViewById(R.id.lecturer_forgot_password);
        backButton = findViewById(R.id.lecturer_back_button);
        helpButton = findViewById(R.id.lecturer_help_button);
        passwordEyeButton =
                findViewById(R.id.lecturer_password_eye_button);
        lecturerIdInput = findViewById(R.id.lecturer_id_input);
        passwordInput = findViewById(R.id.lecturer_password_input);

        signInButton.setOnClickListener(v -> signIn());

        createAccountButton.setOnClickListener(v ->
                startActivity(new Intent(
                        LecturerActivity.this,
                        CreateLecturerActivity.class
                ))
        );

        forgotPassword.setOnClickListener(v ->
                startActivity(new Intent(
                        LecturerActivity.this,
                        ForgotPasswordActivity.class
                ))
        );

        backButton.setOnClickListener(v -> finish());

        passwordEyeButton.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;

            passwordInput.setTransformationMethod(
                    passwordVisible
                            ? HideReturnsTransformationMethod.getInstance()
                            : PasswordTransformationMethod.getInstance()
            );

            passwordEyeButton.setImageResource(
                    passwordVisible
                            ? R.drawable.password_eye_off_red
                            : R.drawable.password_eye_red
            );

            passwordEyeButton.setAlpha(passwordVisible ? 0.6f : 1.0f);
            passwordInput.setSelection(passwordInput.length());
        });

        helpButton.setOnClickListener(v ->
                new AlertDialog.Builder(LecturerActivity.this)
                        .setTitle("Lecturer Portal Guide")
                        .setMessage(
                                "1. Enter your registered lecturer ID or email.\n\n" +
                                        "2. Enter the password you created during registration.\n\n" +
                                        "3. If you have not registered, select Create Account.\n\n" +
                                        "4. If you have forgotten your password, select Forgot Password."
                        )
                        .setPositiveButton("GOT IT", null)
                        .show()
        );
    }

    private void signIn() {
        String identifier =
                lecturerIdInput.getText().toString().trim();
        String password =
                passwordInput.getText().toString();

        if (identifier.isEmpty()) {
            lecturerIdInput.setError("Enter your lecturer ID or email");
            lecturerIdInput.requestFocus();
            return;
        }

        boolean isEmail =
                Patterns.EMAIL_ADDRESS.matcher(identifier).matches();
        boolean isLecturerId =
                identifier.matches("\\d{5,10}");

        if (!isEmail && !isLecturerId) {
            lecturerIdInput.setError(
                    "Enter a valid lecturer ID or email"
            );
            lecturerIdInput.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Enter your password");
            passwordInput.requestFocus();
            return;
        }

        signInButton.setEnabled(false);

        executor.execute(() -> {
            try {
                LecturerDao dao =
                        AppDatabase.getDatabase(getApplicationContext())
                                .lecturerDao();

                Lecturer lecturer = isEmail
                        ? dao.findLecturerByEmail(identifier)
                        : dao.findLecturerById(identifier);

                runOnUiThread(() -> {
                    signInButton.setEnabled(true);

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    if (lecturer == null) {
                        new AlertDialog.Builder(LecturerActivity.this)
                                .setTitle("Account Not Found")
                                .setMessage(
                                        "No lecturer account was found for these details. " +
                                                "Please create an account first."
                                )
                                .setPositiveButton("CREATE ACCOUNT",
                                        (dialog, which) ->
                                                startActivity(new Intent(
                                                        LecturerActivity.this,
                                                        CreateLecturerActivity.class
                                                )))
                                .setNegativeButton("CANCEL", null)
                                .show();
                        return;
                    }

                    if (!PasswordUtils.verifyPassword(
                            password,
                            lecturer.passwordSalt,
                            lecturer.passwordHash
                    )) {
                        passwordInput.setError("Incorrect password");
                        passwordInput.setText("");
                        passwordInput.requestFocus();
                        return;
                    }

                    String displayName =
                            lecturer.firstName == null
                                    ? identifier
                                    : lecturer.firstName;

                    new AlertDialog.Builder(LecturerActivity.this)
                            .setTitle("Lecturer Sign In Successful")
                            .setMessage("Welcome back, " + displayName + "!")
                            .setPositiveButton("PROCEED TO HOME",
                                    (dialog, which) -> {
                                        Intent intent = new Intent(
                                                LecturerActivity.this,
                                                HomeActivity.class
                                        );
                                        intent.putExtra(
                                                "USER_NAME", displayName
                                        );
                                        intent.putExtra(
                                                "LECTURER_ID",
                                                lecturer.lecturerId
                                        );
                                        startActivity(intent);
                                        finish();
                                    })
                            .show();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    signInButton.setEnabled(true);

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    new AlertDialog.Builder(LecturerActivity.this)
                            .setTitle("Login Error")
                            .setMessage(
                                    "Unable to verify your account. " +
                                            "Please try again."
                            )
                            .setPositiveButton("OK", null)
                            .show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
