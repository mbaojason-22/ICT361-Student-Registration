
package com.example.ict361studentregistration;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ict361studentregistration.data.room.AppDatabase;
import com.example.ict361studentregistration.data.room.Lecturer;
import com.example.ict361studentregistration.data.room.LecturerDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateLecturerPasswordActivity extends AppCompatActivity {

    private ImageButton backButton, helpButton;
    private EditText passwordInput, confirmPasswordInput;
    private ImageButton passwordEyeButton, confirmPasswordEyeButton;
    private Button completeRegistrationButton;

    private boolean passwordVisible = false;
    private boolean confirmPasswordVisible = false;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lecturer_password);

        backButton = findViewById(R.id.set_pwd_back_button);
        helpButton = findViewById(R.id.set_pwd_help_button);
        passwordInput = findViewById(R.id.lecturer_set_pwd_input);
        confirmPasswordInput = findViewById(R.id.lecturer_set_confirm_input);
        passwordEyeButton = findViewById(R.id.lecturer_set_pwd_eye);
        confirmPasswordEyeButton = findViewById(R.id.lecturer_set_confirm_eye);
        completeRegistrationButton =
                findViewById(R.id.complete_registration_button);

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
            passwordInput.setSelection(passwordInput.length());
        });

        confirmPasswordEyeButton.setOnClickListener(v -> {
            confirmPasswordVisible = !confirmPasswordVisible;
            confirmPasswordInput.setTransformationMethod(
                    confirmPasswordVisible
                            ? HideReturnsTransformationMethod.getInstance()
                            : PasswordTransformationMethod.getInstance()
            );
            confirmPasswordEyeButton.setImageResource(
                    confirmPasswordVisible
                            ? R.drawable.password_eye_off_red
                            : R.drawable.password_eye_red
            );
            confirmPasswordInput.setSelection(confirmPasswordInput.length());
        });

        completeRegistrationButton.setOnClickListener(v ->
                completeRegistration()
        );

        helpButton.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Set Password Help")
                        .setMessage(
                                "Use at least 8 characters, including a letter, " +
                                        "a number and a special character. Both password " +
                                        "fields must match."
                        )
                        .setPositiveButton("GOT IT", null)
                        .show()
        );
    }

    private void completeRegistration() {
        String password = passwordInput.getText().toString();
        String confirmation = confirmPasswordInput.getText().toString();

        if (password.length() < 8 ||
                !password.matches(".*[a-zA-Z].*") ||
                !password.matches(".*[0-9].*") ||
                !password.matches(".*[^a-zA-Z0-9].*")) {
            passwordInput.setError(
                    "Use at least 8 characters, including a letter, " +
                            "a number and a special character"
            );
            passwordInput.requestFocus();
            return;
        }

        if (!password.equals(confirmation)) {
            confirmPasswordInput.setError("Passwords do not match");
            confirmPasswordInput.requestFocus();
            return;
        }

        Intent source = getIntent();

        String lecturerId = source.getStringExtra("STAFF_ID");
        String firstName = source.getStringExtra("FIRST_NAME");
        String lastName = source.getStringExtra("LAST_NAME");
        String email = source.getStringExtra("EMAIL");
        String phone = source.getStringExtra("CONTACT");

        if (lecturerId == null || lecturerId.trim().isEmpty() ||
                firstName == null || firstName.trim().isEmpty() ||
                lastName == null || lastName.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                phone == null || phone.trim().isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Registration details missing")
                    .setMessage(
                            "Some lecturer details were not received. " +
                                    "Please return to lecturer registration and try again."
                    )
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        lecturerId = lecturerId.trim();
        firstName = firstName.trim();
        lastName = lastName.trim();
        email = email.trim();
        phone = phone.trim();

        final String id = lecturerId;
        final String first = firstName;
        final String last = lastName;
        final String mail = email;
        final String contact = phone;
        final String rawPassword = password;

        completeRegistrationButton.setEnabled(false);

        executor.execute(() -> {
            try {
                LecturerDao dao =
                        AppDatabase.getDatabase(getApplicationContext())
                                .lecturerDao();

                if (dao.findLecturerById(id) != null ||
                        dao.findLecturerByEmail(mail) != null) {
                    runOnUiThread(() -> {
                        completeRegistrationButton.setEnabled(true);
                        new AlertDialog.Builder(this)
                                .setTitle("Account already exists")
                                .setMessage(
                                        "This lecturer ID or email is already " +
                                                "registered. Please sign in instead."
                                )
                                .setPositiveButton("GO TO SIGN IN",
                                        (dialog, which) -> {
                                            startActivity(new Intent(
                                                    this, LecturerActivity.class));
                                            finish();
                                        })
                                .setNegativeButton("CANCEL", null)
                                .show();
                    });
                    return;
                }

                String salt = PasswordUtils.generateSalt();
                String hash = PasswordUtils.hashPassword(rawPassword, salt);

                Lecturer lecturer = new Lecturer();
                lecturer.lecturerId = id;
                lecturer.firstName = first;
                lecturer.lastName = last;
                lecturer.email = mail;
                lecturer.phone = contact;
                lecturer.passwordSalt = salt;
                lecturer.passwordHash = hash;

                dao.insertLecturer(lecturer);

                runOnUiThread(() -> {
                    completeRegistrationButton.setEnabled(true);
                    new AlertDialog.Builder(this)
                            .setTitle("Account Created Successfully")
                            .setMessage(
                                    "Your lecturer account has been saved. " +
                                            "You can now sign in using your registered " +
                                            "email or lecturer ID and password."
                            )
                            .setCancelable(false)
                            .setPositiveButton("GO TO SIGN IN", (dialog, which) -> {
                                Intent intent = new Intent(
                                        this, LecturerActivity.class);
                                intent.addFlags(
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                                );
                                startActivity(intent);
                                finish();
                            })
                            .show();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    completeRegistrationButton.setEnabled(true);
                    new AlertDialog.Builder(this)
                            .setTitle("Registration Failed")
                            .setMessage(
                                    "The account could not be saved. " +
                                            "Please try again. Details: " +
                                            e.getClass().getSimpleName()
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
