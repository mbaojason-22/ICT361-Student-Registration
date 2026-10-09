package com.example.ict361studentregistration;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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

public class VerificationActivity extends AppCompatActivity {

    private EditText code1;
    private EditText code2;
    private EditText code3;
    private EditText code4;
    private EditText code5;
    private EditText code6;

    private Button verifyButton;

    private TextView contactText;
    private TextView resendText;
    private TextView changeContactText;

    private ImageButton backButton;
    private ImageButton helpButton;

    private CountDownTimer countDownTimer;

    private static final String DEMO_CODE = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_verification);

        // =========================================================
        // FIND VIEWS
        // =========================================================

        code1 = findViewById(R.id.code_digit_1);
        code2 = findViewById(R.id.code_digit_2);
        code3 = findViewById(R.id.code_digit_3);
        code4 = findViewById(R.id.code_digit_4);
        code5 = findViewById(R.id.code_digit_5);
        code6 = findViewById(R.id.code_digit_6);

        verifyButton =
                findViewById(
                        R.id.verify_account_button
                );

        contactText =
                findViewById(
                        R.id.verification_contact
                );

        resendText =
                findViewById(
                        R.id.resend_code_text
                );

        changeContactText =
                findViewById(
                        R.id.change_contact_text
                );

        backButton =
                findViewById(
                        R.id.verification_back_button
                );

        helpButton =
                findViewById(
                        R.id.verification_help_button
                );

        // =========================================================
        // GET CONTACT
        // =========================================================

        String contact =
                getIntent().getStringExtra("contact");

        if (contact != null && !contact.isEmpty()) {

            contactText.setText(
                    maskContact(contact)
            );
        }

        // =========================================================
        // OTP BOXES
        // =========================================================

        setupCodeBoxes();

        // =========================================================
        // COUNTDOWN
        // =========================================================

        startCountdown();

        // =========================================================
        // VERIFY
        // =========================================================

        verifyButton.setOnClickListener(
                v -> verifyCode()
        );

        // =========================================================
        // RESEND
        // =========================================================

        resendText.setOnClickListener(v -> {

            if (resendText.isEnabled()) {

                new AlertDialog.Builder(
                        VerificationActivity.this
                )
                        .setTitle("Code Sent")
                        .setMessage(
                                "A new verification code has been requested.\n\n"
                                        + "For this local demonstration, use:\n\n"
                                        + "123456"
                        )
                        .setPositiveButton(
                                "OK",
                                null
                        )
                        .show();

                startCountdown();
            }
        });

        // =========================================================
        // WRONG CONTACT
        // =========================================================

        changeContactText.setOnClickListener(v -> {

            new AlertDialog.Builder(
                    VerificationActivity.this
            )
                    .setTitle("Wrong Contact?")
                    .setMessage(
                            "If the email address or phone number is incorrect, "
                                    + "go back and update your registration details."
                    )
                    .setPositiveButton(
                            "OK",
                            null
                    )
                    .show();
        });

        // =========================================================
        // BACK
        // =========================================================

        backButton.setOnClickListener(
                v -> finish()
        );

        // =========================================================
        // HELP
        // =========================================================

        helpButton.setOnClickListener(v -> {

            new AlertDialog.Builder(
                    VerificationActivity.this
            )
                    .setTitle("Verification Help")
                    .setMessage(
                            "A 6-digit verification code has been sent "
                                    + "to the email address or phone number "
                                    + "you provided.\n\n"
                                    + "Enter the six digits into the boxes "
                                    + "and press VERIFY ACCOUNT.\n\n"
                                    + "For this local demonstration, the "
                                    + "verification code is:\n\n"
                                    + "123456\n\n"
                                    + "Never share your verification code "
                                    + "with anyone."
                    )
                    .setPositiveButton(
                            "GOT IT",
                            null
                    )
                    .show();
        });
    }

    // =============================================================
    // VERIFY CODE
    // =============================================================

    private void verifyCode() {

        String enteredCode =
                code1.getText().toString()
                        + code2.getText().toString()
                        + code3.getText().toString()
                        + code4.getText().toString()
                        + code5.getText().toString()
                        + code6.getText().toString();

        // =========================================================
        // INCOMPLETE
        // =========================================================

        if (enteredCode.length() != 6) {

            new AlertDialog.Builder(
                    VerificationActivity.this
            )
                    .setTitle("Incomplete Code")
                    .setMessage(
                            "Please enter all 6 digits."
                    )
                    .setPositiveButton(
                            "OK",
                            null
                    )
                    .show();

            return;
        }

        // =========================================================
        // WRONG CODE
        // =========================================================

        if (!enteredCode.equals(DEMO_CODE)) {

            // Error sound
            playErrorSound();

            // Error vibration
            vibrateError();

            // Red X animation
            showErrorAnimation();

            return;
        }

        // =========================================================
        // CORRECT CODE
        // =========================================================

        showVerificationAnimation();
    }

    // =============================================================
    // SUCCESS ANIMATION
    // =============================================================

    private void showVerificationAnimation() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Success sound
        playSuccessSound();

        // Success vibration
        vibrateSuccess();

        final Dialog animationDialog =
                new Dialog(this);

        animationDialog.requestWindowFeature(
                Window.FEATURE_NO_TITLE
        );

        VerificationAnimationView animationView =
                new VerificationAnimationView(this);

        animationDialog.setContentView(
                animationView
        );

        animationDialog.setCancelable(false);
        animationDialog.setCanceledOnTouchOutside(false);

        Window window =
                animationDialog.getWindow();

        if (window != null) {

            window.setBackgroundDrawableResource(
                    android.R.color.transparent
            );

            WindowManager.LayoutParams params =
                    window.getAttributes();

            params.width = dpToPx(280);
            params.height = dpToPx(280);
            params.gravity = Gravity.CENTER;
            params.dimAmount = 0.35f;

            window.setAttributes(params);

            window.addFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND
            );
        }

        animationDialog.setOnShowListener(
                dialog -> {

                    animationView.startAnimation();

                    new Handler().postDelayed(
                            () -> {

                                if (animationDialog.isShowing()) {
                                    animationDialog.dismiss();
                                }

                                openStudentPortal();

                            },
                            1850
                    );
                }
        );

        animationDialog.show();

        Window shownWindow =
                animationDialog.getWindow();

        if (shownWindow != null) {

            WindowManager.LayoutParams params =
                    shownWindow.getAttributes();

            params.width = dpToPx(280);
            params.height = dpToPx(280);
            params.gravity = Gravity.CENTER;
            params.dimAmount = 0.35f;

            shownWindow.setAttributes(params);

            shownWindow.setBackgroundDrawableResource(
                    android.R.color.transparent
            );

            shownWindow.addFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND
            );
        }
    }

    // =============================================================
    // ERROR ANIMATION
    // =============================================================

    private void showErrorAnimation() {

        final Dialog animationDialog =
                new Dialog(this);

        animationDialog.requestWindowFeature(
                Window.FEATURE_NO_TITLE
        );

        VerificationAnimationView animationView =
                new VerificationAnimationView(this);

        animationDialog.setContentView(
                animationView
        );

        animationDialog.setCancelable(false);
        animationDialog.setCanceledOnTouchOutside(false);

        Window window =
                animationDialog.getWindow();

        if (window != null) {

            window.setBackgroundDrawableResource(
                    android.R.color.transparent
            );

            WindowManager.LayoutParams params =
                    window.getAttributes();

            params.width = dpToPx(280);
            params.height = dpToPx(280);
            params.gravity = Gravity.CENTER;
            params.dimAmount = 0.35f;

            window.setAttributes(params);

            window.addFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND
            );
        }

        animationDialog.setOnShowListener(
                dialog -> {

                    animationView.startErrorAnimation();

                    // Let the X animation finish,
                    // then return to the verification screen.
                    new Handler().postDelayed(
                            () -> {

                                if (animationDialog.isShowing()) {
                                    animationDialog.dismiss();
                                }

                            },
                            1500
                    );
                }
        );

        animationDialog.show();

        Window shownWindow =
                animationDialog.getWindow();

        if (shownWindow != null) {

            WindowManager.LayoutParams params =
                    shownWindow.getAttributes();

            params.width = dpToPx(280);
            params.height = dpToPx(280);
            params.gravity = Gravity.CENTER;
            params.dimAmount = 0.35f;

            shownWindow.setAttributes(params);

            shownWindow.setBackgroundDrawableResource(
                    android.R.color.transparent
            );

            shownWindow.addFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND
            );
        }
    }

    // =============================================================
    // OPEN STUDENT PORTAL
    // =============================================================

    private void openStudentPortal() {

        Intent intent =
                new Intent(
                        VerificationActivity.this,
                        StudentHomeActivity.class
                );

        // Open Student Home directly after successful verification.
        // Clear registration/login screens from the back stack.
        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }

    // =============================================================
    // SUCCESS SOUND
    // =============================================================

    private void playSuccessSound() {

        MediaPlayer mediaPlayer =
                MediaPlayer.create(
                        this,
                        R.raw.success
                );

        if (mediaPlayer != null) {

            mediaPlayer.setOnCompletionListener(
                    MediaPlayer::release
            );

            mediaPlayer.start();
        }
    }

    // =============================================================
    // ERROR SOUND
    // =============================================================

    private void playErrorSound() {

        ToneGenerator toneGenerator =
                new ToneGenerator(
                        AudioManager.STREAM_NOTIFICATION,
                        80
                );

        toneGenerator.startTone(
                ToneGenerator.TONE_PROP_NACK,
                180
        );

        new Handler().postDelayed(
                toneGenerator::release,
                250
        );
    }

    // =============================================================
    // SUCCESS VIBRATION
    // =============================================================

    private void vibrateSuccess() {

        Vibrator vibrator;

        if (android.os.Build.VERSION.SDK_INT >= 31) {

            VibratorManager vibratorManager =
                    (VibratorManager)
                            getSystemService(
                                    Context.VIBRATOR_MANAGER_SERVICE
                            );

            vibrator =
                    vibratorManager.getDefaultVibrator();

        } else {

            vibrator =
                    (Vibrator)
                            getSystemService(
                                    Context.VIBRATOR_SERVICE
                            );
        }

        if (vibrator == null) {
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= 26) {

            vibrator.vibrate(
                    VibrationEffect.createOneShot(
                            70,
                            VibrationEffect.DEFAULT_AMPLITUDE
                    )
            );

        } else {

            vibrator.vibrate(70);
        }
    }

    // =============================================================
    // ERROR VIBRATION
    // =============================================================

    private void vibrateError() {

        Vibrator vibrator;

        if (android.os.Build.VERSION.SDK_INT >= 31) {

            VibratorManager vibratorManager =
                    (VibratorManager)
                            getSystemService(
                                    Context.VIBRATOR_MANAGER_SERVICE
                            );

            vibrator =
                    vibratorManager.getDefaultVibrator();

        } else {

            vibrator =
                    (Vibrator)
                            getSystemService(
                                    Context.VIBRATOR_SERVICE
                            );
        }

        if (vibrator == null) {
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= 26) {

            long[] pattern = {
                    0,
                    80,
                    60,
                    120
            };

            vibrator.vibrate(
                    VibrationEffect.createWaveform(
                            pattern,
                            -1
                    )
            );

        } else {

            long[] pattern = {
                    0,
                    80,
                    60,
                    120
            };

            vibrator.vibrate(
                    pattern,
                    -1
            );
        }
    }

    // =============================================================
    // DP TO PIXELS
    // =============================================================

    private int dpToPx(int dp) {

        return Math.round(
                dp
                        * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    // =============================================================
    // OTP BOX SETUP
    // =============================================================

    private void setupCodeBoxes() {

        setupNextDigit(
                code1,
                code2
        );

        setupNextDigit(
                code2,
                code3
        );

        setupNextDigit(
                code3,
                code4
        );

        setupNextDigit(
                code4,
                code5
        );

        setupNextDigit(
                code5,
                code6
        );

        setupBackspace(
                code2,
                code1
        );

        setupBackspace(
                code3,
                code2
        );

        setupBackspace(
                code4,
                code3
        );

        setupBackspace(
                code5,
                code4
        );

        setupBackspace(
                code6,
                code5
        );

        code1.requestFocus();
    }

    // =============================================================
    // NEXT OTP BOX
    // =============================================================

    private void setupNextDigit(
            EditText current,
            EditText next
    ) {

        current.addTextChangedListener(
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

                        if (s.length() == 1) {
                            next.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );
    }

    // =============================================================
    // BACKSPACE
    // =============================================================

    private void setupBackspace(
            EditText current,
            EditText previous
    ) {

        current.setOnKeyListener(
                (v, keyCode, event) -> {

                    if (keyCode
                            == KeyEvent.KEYCODE_DEL
                            && event.getAction()
                            == KeyEvent.ACTION_DOWN
                            && current.getText().length() == 0) {

                        previous.requestFocus();

                        return true;
                    }

                    return false;
                }
        );
    }

    // =============================================================
    // RESEND COUNTDOWN
    // =============================================================

    private void startCountdown() {

        resendText.setEnabled(false);

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer =
                new CountDownTimer(
                        30000,
                        1000
                ) {

                    @Override
                    public void onTick(
                            long millisUntilFinished
                    ) {

                        long seconds =
                                millisUntilFinished / 1000;

                        resendText.setText(
                                "Resend code in 00:"
                                        + String.format(
                                        "%02d",
                                        seconds
                                )
                        );
                    }

                    @Override
                    public void onFinish() {

                        resendText.setEnabled(true);

                        resendText.setText(
                                "RESEND CODE"
                        );
                    }
                };

        countDownTimer.start();
    }

    // =============================================================
    // MASK EMAIL / PHONE
    // =============================================================

    private String maskContact(
            String contact
    ) {

        if (contact.contains("@")) {

            String[] parts =
                    contact.split("@");

            String name = parts[0];
            String domain = parts[1];

            if (name.length() <= 2) {

                return "***@" + domain;
            }

            return name.charAt(0)
                    + "***"
                    + name.charAt(
                    name.length() - 1
            )
                    + "@"
                    + domain;
        }

        if (contact.length() >= 4) {

            return "**** **** "
                    + contact.substring(
                    contact.length() - 4
            );
        }

        return "****";
    }

    // =============================================================
    // CLEAN UP
    // =============================================================

    @Override
    protected void onDestroy() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        super.onDestroy();
    }
}