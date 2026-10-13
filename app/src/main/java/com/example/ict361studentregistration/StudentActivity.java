
package com.example.ict361studentregistration;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ict361studentregistration.data.room.AppDatabase;
import com.example.ict361studentregistration.data.room.Student;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentActivity extends AppCompatActivity {

    private EditText studentNumberInput;
    private EditText passwordInput;

    private Button signInButton;
    private Button createAccountButton;

    private ImageButton backButton;
    private ImageButton helpButton;
    private ImageButton passwordEyeButton;

    private boolean passwordVisible = false;
    private boolean loginInProgress = false;

    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        signInButton = findViewById(R.id.sign_in_button);
        createAccountButton =
                findViewById(R.id.create_account_button);

        studentNumberInput =
                findViewById(R.id.student_number_input);
        passwordInput =
                findViewById(R.id.password_input);

        backButton =
                findViewById(R.id.student_back_button);
        helpButton =
                findViewById(R.id.student_help_button);
        passwordEyeButton =
                findViewById(R.id.password_eye_button);

        signInButton.setOnClickListener(v -> loginStudent());

        createAccountButton.setOnClickListener(v ->
                startActivity(new Intent(
                        StudentActivity.this,
                        CreateAccountActivity.class
                ))
        );

        findViewById(R.id.forgot_password).setOnClickListener(v ->
                startActivity(new Intent(
                        StudentActivity.this,
                        PasswordRecoveryActivity.class
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

            passwordInput.setSelection(
                    passwordInput.getText().length()
            );
        });

        helpButton.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Student Login Help")
                        .setMessage(
                                "Enter your registered 9-digit student " +
                                        "number or email address, followed by " +
                                        "your password. If you don't have an " +
                                        "account, select CREATE ACCOUNT."
                        )
                        .setPositiveButton("GOT IT", null)
                        .show()
        );
    }

    private void loginStudent() {

        if (loginInProgress) {
            return;
        }

        String identifier =
                studentNumberInput.getText().toString().trim();

        String password =
                passwordInput.getText().toString();

        if (identifier.isEmpty()) {
            studentNumberInput.setError(
                    "Enter your student number or email"
            );
            studentNumberInput.requestFocus();
            return;
        }

        boolean isEmail =
                Patterns.EMAIL_ADDRESS.matcher(identifier).matches();

        boolean isStudentNumber =
                identifier.matches("\\d{9}");

        if (!isEmail && !isStudentNumber) {
            studentNumberInput.setError(
                    "Enter a valid email or 9-digit student number"
            );
            studentNumberInput.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Enter your password");
            passwordInput.requestFocus();
            return;
        }

        loginInProgress = true;
        signInButton.setEnabled(false);

        if (isEmail) {
            sendBackendLogin(identifier, password);
        } else {
            findStudentEmailAndLogin(identifier, password);
        }
    }

    private void findStudentEmailAndLogin(
            String studentNumber,
            String password
    ) {
        databaseExecutor.execute(() -> {
            try {
                AppDatabase db =
                        AppDatabase.getDatabase(
                                getApplicationContext()
                        );

                Student student = db.studentDao()
                        .findStudentByIdentifier(studentNumber);

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    if (student == null) {
                        resetLoginState();

                        new AlertDialog.Builder(StudentActivity.this)
                                .setTitle("Student Not Found")
                                .setMessage(
                                        "This student number was not found " +
                                                "in the local database. Try your " +
                                                "registered email address instead."
                                )
                                .setPositiveButton("OK", null)
                                .show();
                        return;
                    }

                    String email = student.email;

                    if (email == null ||
                            !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        resetLoginState();

                        new AlertDialog.Builder(StudentActivity.this)
                                .setTitle("Email Required")
                                .setMessage(
                                        "A valid email address is not " +
                                                "available for this local record. " +
                                                "Please sign in using your " +
                                                "registered email address."
                                )
                                .setPositiveButton("OK", null)
                                .show();
                        return;
                    }

                    sendBackendLogin(email, password);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    resetLoginState();

                    showMessage(
                            "Login Error",
                            "Unable to read the local student record. " +
                                    "Please try again."
                    );
                });
            }
        });
    }

    private void sendBackendLogin(
            String email,
            String password
    ) {
        LoginRequest request =
                new LoginRequest(email, password);

        AuthApiService api =
                AuthApiClient.getService(getApplicationContext());

        Call<LoginResponse> call = api.login(request);

        call.enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(
                    Call<LoginResponse> call,
                    Response<LoginResponse> response
            ) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                if (response.isSuccessful() &&
                        response.body() != null) {

                    LoginResponse result = response.body();

                    if (result.isSuccess()
                            && result.getToken() != null
                            && !result.getToken().trim().isEmpty()
                            && result.getStudent() != null) {

                        AuthInterceptor.saveToken(
                                getApplicationContext(),
                                result.getToken()
                        );

                        LoginResponse.StudentInfo student =
                                result.getStudent();

                        Intent intent = new Intent(
                                StudentActivity.this,
                                StudentHomeActivity.class
                        );

                        intent.putExtra(
                                "STUDENT_ID",
                                student.getStudentId()
                        );

                        intent.putExtra(
                                "USER_NAME",
                                (student.getFirstName() == null
                                        ? ""
                                        : student.getFirstName())
                                        + " "
                                        + (student.getLastName() == null
                                        ? ""
                                        : student.getLastName())
                        );

                        intent.putExtra(
                                "STUDENT_NUMBER",
                                student.getStudentNumber()
                        );

                        intent.putExtra(
                                "STUDENT_EMAIL",
                                student.getEmail()
                        );

                        resetLoginState();
                        startActivity(intent);

                    } else {
                        resetLoginState();

                        showMessage(
                                "Login Failed",
                                result.getMessage() != null
                                        ? result.getMessage()
                                        : "The server returned an " +
                                          "incomplete login response."
                        );
                    }

                } else {
                    resetLoginState();

                    if (response.code() == 401) {
                        passwordInput.setText("");
                        passwordInput.setError(
                                "Incorrect email or password"
                        );
                        passwordInput.requestFocus();

                    } else if (response.code() == 403) {
                        showMessage(
                                "Account Not Verified",
                                "Your account may not be verified yet. " +
                                        "Please complete email verification."
                        );

                    } else {
                        showMessage(
                                "Login Failed",
                                "The server returned error " +
                                        response.code() +
                                        ". Please try again."
                        );
                    }
                }
            }

            @Override
            public void onFailure(
                    Call<LoginResponse> call,
                    Throwable throwable
            ) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                resetLoginState();

                String message =
                        "Unable to connect to the backend. " +
                                "Check the network and try again.";

                if (throwable instanceof IOException) {
                    message =
                            "Network connection failed. Make sure " +
                                    "the backend is running and reachable " +
                                    "from this device.";
                }

                showMessage("Connection Error", message);
            }
        });
    }

    private void resetLoginState() {
        loginInProgress = false;
        signInButton.setEnabled(true);
    }

    private void showMessage(String title, String message) {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        databaseExecutor.shutdown();
        super.onDestroy();
    }
}
