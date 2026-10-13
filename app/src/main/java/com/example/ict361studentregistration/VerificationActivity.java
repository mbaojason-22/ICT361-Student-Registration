
package com.example.ict361studentregistration;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ict361studentregistration.data.room.AppDatabase;
import com.example.ict361studentregistration.data.room.Student;
import com.google.gson.JsonObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationActivity extends AppCompatActivity {

    private EditText code1, code2, code3, code4, code5, code6;
    private Button verifyButton;
    private TextView contactText, resendText, changeContactText;
    private ImageButton backButton, helpButton;

    private CountDownTimer countDownTimer;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    private boolean requestInProgress = false;
    private boolean emailVerified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        code1 = findViewById(R.id.code_digit_1);
        code2 = findViewById(R.id.code_digit_2);
        code3 = findViewById(R.id.code_digit_3);
        code4 = findViewById(R.id.code_digit_4);
        code5 = findViewById(R.id.code_digit_5);
        code6 = findViewById(R.id.code_digit_6);

        verifyButton = findViewById(R.id.verify_account_button);
        contactText = findViewById(R.id.verification_contact);
        resendText = findViewById(R.id.resend_code_text);
        changeContactText = findViewById(R.id.change_contact_text);
        backButton = findViewById(R.id.verification_back_button);
        helpButton = findViewById(R.id.verification_help_button);

        String email = getIntent().getStringExtra("contact");
        if (email != null && !email.isEmpty()) {
            contactText.setText(maskContact(email));
        }

        setupCodeBoxes();
        startCountdown();

        verifyButton.setOnClickListener(v -> verifyCode());
        resendText.setOnClickListener(v -> resendCode());

        changeContactText.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Wrong Email?")
                        .setMessage("Go back and update your registration details.")
                        .setPositiveButton("OK", null)
                        .show()
        );

        backButton.setOnClickListener(v -> finish());

        helpButton.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Email Verification")
                        .setMessage(
                                "Enter the six-digit verification code sent to your email. " +
                                        "The code expires after 10 minutes. You can request a new code " +
                                        "using RESEND CODE."
                        )
                        .setPositiveButton("GOT IT", null)
                        .show()
        );
    }

    private void verifyCode() {
        if (requestInProgress) return;

        String enteredCode =
                code1.getText().toString()
                        + code2.getText().toString()
                        + code3.getText().toString()
                        + code4.getText().toString()
                        + code5.getText().toString()
                        + code6.getText().toString();

        if (!enteredCode.matches("\\d{6}")) {
            showMessage("Incomplete Code", "Please enter all six digits.");
            return;
        }

        String email = getIntent().getStringExtra("contact");
        if (email == null || email.trim().isEmpty()) {
            showMessage("Missing Email", "Return to registration and enter your email again.");
            return;
        }

        requestInProgress = true;
        verifyButton.setEnabled(false);
        resendText.setEnabled(false);

        JsonObject request = new JsonObject();
        request.addProperty("email", email);
        request.addProperty("verification_code", enteredCode);

        AuthApiClient.getService(this).verifyEmail(request)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(
                            Call<JsonObject> call,
                            Response<JsonObject> response
                    ) {
                        requestInProgress = false;
                        verifyButton.setEnabled(true);

                        JsonObject body = response.body();
                        boolean success = response.isSuccessful()
                                && body != null
                                && body.has("success")
                                && body.get("success").getAsBoolean();

                        if (success) {
                            emailVerified = true;
                            if (countDownTimer != null) countDownTimer.cancel();
                            showVerificationAnimation();
                        } else {
                            resendText.setEnabled(true);
                            String message = "Verification failed. Check the code and try again.";
                            if (body != null && body.has("message")) {
                                message = body.get("message").getAsString();
                            }
                            playErrorSound();
                            vibrateError();
                            showErrorAnimation();
                            showMessage("Verification Error", message);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        requestInProgress = false;
                        verifyButton.setEnabled(true);
                        resendText.setEnabled(true);
                        showMessage("Connection Error",
                                "Could not contact the verification server. Check your network and try again.\n\n"
                                        + t.getMessage());
                    }
                });
    }

    private void resendCode() {
        if (requestInProgress || !resendText.isEnabled()) return;

        String email = getIntent().getStringExtra("contact");
        if (email == null || email.trim().isEmpty()) {
            showMessage("Missing Email", "Return to registration and enter your email again.");
            return;
        }

        requestInProgress = true;
        resendText.setEnabled(false);

        JsonObject request = new JsonObject();
        request.addProperty("email", email);

        AuthApiClient.getService(this).resendVerificationCode(request)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(
                            Call<JsonObject> call,
                            Response<JsonObject> response
                    ) {
                        requestInProgress = false;

                        JsonObject body = response.body();
                        boolean success = response.isSuccessful()
                                && body != null
                                && body.has("success")
                                && body.get("success").getAsBoolean();

                        if (success) {
                            clearCodeBoxes();
                            startCountdown();
                            showMessage("Code Sent",
                                    body.has("message")
                                            ? body.get("message").getAsString()
                                            : "A new verification code has been sent to your email.");
                        } else {
                            resendText.setEnabled(true);
                            String message = "Could not resend the verification code.";
                            if (body != null && body.has("message")) {
                                message = body.get("message").getAsString();
                            }
                            showMessage("Resend Failed", message);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        requestInProgress = false;
                        resendText.setEnabled(true);
                        showMessage("Connection Error",
                                "Could not contact the server.\n\n" + t.getMessage());
                    }
                });
    }

    private void showVerificationAnimation() {
        playSuccessSound();
        vibrateSuccess();

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        VerificationAnimationView animationView =
                new VerificationAnimationView(this);

        dialog.setContentView(animationView);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        configureAnimationDialog(dialog);

        dialog.setOnShowListener(d -> {
            animationView.startAnimation();

            handler.postDelayed(() -> {
                if (isFinishing() || isDestroyed()) {
                    if (dialog.isShowing()) dialog.dismiss();
                    return;
                }

                if (dialog.isShowing()) dialog.dismiss();
                saveStudentAccount();
            }, 1850);
        });

        dialog.show();
        configureAnimationDialog(dialog);
    }

    private void configureAnimationDialog(Dialog dialog) {
        Window window = dialog.getWindow();

        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams params = window.getAttributes();
            params.width = dpToPx(280);
            params.height = dpToPx(280);
            params.gravity = Gravity.CENTER;
            params.dimAmount = 0.35f;

            window.setAttributes(params);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
    }

    private void showErrorAnimation() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        VerificationAnimationView animationView =
                new VerificationAnimationView(this);

        dialog.setContentView(animationView);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        configureAnimationDialog(dialog);

        dialog.setOnShowListener(d -> {
            animationView.startErrorAnimation();
            handler.postDelayed(() -> {
                if (dialog.isShowing()) dialog.dismiss();
            }, 1500);
        });

        dialog.show();
        configureAnimationDialog(dialog);
    }

    private void saveStudentAccount() {
        if (!emailVerified || isFinishing() || isDestroyed()) return;

        String email = getIntent().getStringExtra("contact");
        String password = getIntent().getStringExtra("password");
        String studentNumber = getIntent().getStringExtra("student_number");
        String firstName = getIntent().getStringExtra("first_name");
        String lastName = getIntent().getStringExtra("last_name");
        String programme = getIntent().getStringExtra("programme");

        if (email == null || password == null || studentNumber == null
                || firstName == null || lastName == null || programme == null) {
            showMessage("Account Verified",
                    "Your email is verified, but some details are missing from this screen. You can now try logging in with your email and password.");
            return;
        }

        databaseExecutor.execute(() -> {
            try {
                AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

                boolean studentExists =
                        db.studentDao().countStudentById(studentNumber) > 0;
                boolean emailExists =
                        db.studentDao().countByEmail(email) > 0;

                if (!studentExists && !emailExists) {
                    Student student = new Student();
                    student.studentId = studentNumber;
                    student.firstName = firstName;
                    student.lastName = lastName;
                    student.programme = programme;
                    student.groupName = "";
                    student.email = email;
                    student.phone = "";
                    student.passwordSalt = PasswordUtils.generateSalt();
                    student.passwordHash =
                            PasswordUtils.hashPassword(password, student.passwordSalt);

                    db.studentDao().insertStudent(student);
                }

                handler.post(() -> {
                    if (isFinishing() || isDestroyed()) return;

                    new AlertDialog.Builder(this)
                            .setTitle("Email Verified")
                            .setMessage(
                                    "Your email has been verified successfully. " +
                                            "Your account is registered on the server. You can now log in."
                            )
                            .setCancelable(false)
                            .setPositiveButton("CONTINUE", (d, which) -> {
                                Intent intent = new Intent(this, StudentActivity.class);
                                intent.addFlags(
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                | Intent.FLAG_ACTIVITY_SINGLE_TOP
                                );
                                startActivity(intent);
                                finish();
                            })
                            .show();
                });

            } catch (Exception e) {
                handler.post(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    showMessage("Email Verified",
                            "Your server account is verified, but the local copy could not be saved. You can still try logging in with your email and password.");
                });
            }
        });
    }

    private void clearCodeBoxes() {
        code1.setText("");
        code2.setText("");
        code3.setText("");
        code4.setText("");
        code5.setText("");
        code6.setText("");
        code1.requestFocus();
    }

    private void setupCodeBoxes() {
        EditText[] boxes = {code1, code2, code3, code4, code5, code6};

        for (int i = 0; i < boxes.length; i++) {
            final int index = i;

            boxes[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < boxes.length - 1) {
                        boxes[index + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            if (i > 0) {
                boxes[i].setOnKeyListener((v, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_DEL
                            && event.getAction() == KeyEvent.ACTION_DOWN
                            && boxes[index].getText().length() == 0) {
                        boxes[index - 1].requestFocus();
                        return true;
                    }
                    return false;
                });
            }
        }

        code1.requestFocus();
    }

    private void startCountdown() {
        if (countDownTimer != null) countDownTimer.cancel();

        resendText.setEnabled(false);
        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                resendText.setText("Resend code in 00:" + String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                resendText.setEnabled(true);
                resendText.setText("RESEND CODE");
            }
        };
        countDownTimer.start();
    }

    private String maskContact(String email) {
        if (email.contains("@")) {
            String[] parts = email.split("@", 2);
            String name = parts[0];
            String domain = parts[1];

            if (name.length() <= 2) return "***@" + domain;
            return name.charAt(0) + "***"
                    + name.charAt(name.length() - 1) + "@" + domain;
        }
        return "****";
    }

    private void showMessage(String title, String message) {
        if (isFinishing() || isDestroyed()) return;

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void playSuccessSound() {
        MediaPlayer player = MediaPlayer.create(this, R.raw.success);
        if (player != null) {
            player.setOnCompletionListener(MediaPlayer::release);
            player.start();
        }
    }

    private void playErrorSound() {
        ToneGenerator tone = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80);
        tone.startTone(ToneGenerator.TONE_PROP_NACK, 180);
        handler.postDelayed(tone::release, 250);
    }

    private void vibrateSuccess() {
        vibrate(new long[]{0, 70});
    }

    private void vibrateError() {
        vibrate(new long[]{0, 80, 60, 120});
    }

    private void vibrate(long[] pattern) {
        try {
            Vibrator vibrator;

            if (Build.VERSION.SDK_INT >= 31) {
                VibratorManager manager = (VibratorManager)
                        getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
                vibrator = manager == null ? null : manager.getDefaultVibrator();
            } else {
                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            }

            if (vibrator == null || !vibrator.hasVibrator()) return;

            if (Build.VERSION.SDK_INT >= 26) {
                if (pattern.length == 2) {
                    vibrator.vibrate(VibrationEffect.createOneShot(
                            pattern[1], VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
                }
            } else {
                vibrator.vibrate(pattern, -1);
            }
        } catch (Exception ignored) {
            // Verification can continue if vibration is unavailable.
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) countDownTimer.cancel();
        handler.removeCallbacksAndMessages(null);
        databaseExecutor.shutdown();
        super.onDestroy();
    }
}
