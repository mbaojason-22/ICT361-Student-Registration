package com.example.ict361studentregistration;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class RecoveryVerificationActivity extends AppCompatActivity {

    private EditText code1, code2, code3, code4, code5, code6;
    private Button verifyButton;
    private TextView contactText, resendText, cancelText;
    private CountDownTimer countDownTimer;

    private static final String DEMO_CODE = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_verification);

        code1 = findViewById(R.id.recovery_code_1);
        code2 = findViewById(R.id.recovery_code_2);
        code3 = findViewById(R.id.recovery_code_3);
        code4 = findViewById(R.id.recovery_code_4);
        code5 = findViewById(R.id.recovery_code_5);
        code6 = findViewById(R.id.recovery_code_6);

        verifyButton = findViewById(R.id.verify_recovery_button);
        contactText = findViewById(R.id.recovery_verification_contact);
        resendText = findViewById(R.id.recovery_resend_code);
        cancelText = findViewById(R.id.recovery_cancel);

        String identifier =
                getIntent().getStringExtra("recovery_identifier");

        if (identifier != null && !identifier.isEmpty()) {
            contactText.setText(
                    "Enter the 6-digit code sent to "
                            + maskIdentifier(identifier)
            );
        }

        setupCodeBoxes();
        startCountdown();

        verifyButton.setOnClickListener(v -> verifyCode());

        resendText.setOnClickListener(v -> {

            if (resendText.isEnabled()) {

                new AlertDialog.Builder(this)
                        .setTitle("Code Sent")
                        .setMessage(
                                "A new verification code has been requested.\n\n"
                                        + "For this local demonstration, use:\n\n"
                                        + "123456"
                        )
                        .setPositiveButton("OK", null)
                        .show();

                startCountdown();
            }
        });

        cancelText.setOnClickListener(v -> finish());
    }

    private void verifyCode() {

        String enteredCode =
                code1.getText().toString()
                        + code2.getText().toString()
                        + code3.getText().toString()
                        + code4.getText().toString()
                        + code5.getText().toString()
                        + code6.getText().toString();

        if (enteredCode.length() != 6) {

            new AlertDialog.Builder(this)
                    .setTitle("Incomplete Code")
                    .setMessage("Please enter all 6 digits.")
                    .setPositiveButton("OK", null)
                    .show();

            return;
        }

        if (!enteredCode.equals(DEMO_CODE)) {

            playErrorSound();

            new AlertDialog.Builder(this)
                    .setTitle("Verification Failed")
                    .setMessage(
                            "The verification code you entered is incorrect.\n\n"
                                    + "Please check the code and try again."
                    )
                    .setPositiveButton("TRY AGAIN", null)
                    .show();

            return;
        }

        // Correct code
        showVerificationAnimation();
    }

    /**
     * Shows the Face-ID-style verification animation.
     *
     * No words are displayed.
     *
     * Flow:
     * 1. Success sound
     * 2. Green circular ring draws
     * 3. White tick draws
     * 4. Tick/circle gently pops
     * 5. Reset Password opens automatically
     */
    private void showVerificationAnimation() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        playSuccessSound();

        final Dialog animationDialog =
                new Dialog(this);

        animationDialog.requestWindowFeature(
                Window.FEATURE_NO_TITLE
        );

        VerificationAnimationView animationView =
                new VerificationAnimationView(this);

        animationDialog.setContentView(animationView);

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

            params.width =
                    dpToPx(280);

            params.height =
                    dpToPx(280);

            params.gravity =
                    Gravity.CENTER;

            params.dimAmount =
                    0.35f;

            window.setAttributes(params);

            window.addFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND
            );
        }

        animationDialog.setOnShowListener(dialog -> {

            animationView.startAnimation();

            /*
             * The animation takes approximately:
             *
             * Ring  = 900ms
             * Tick  = 450ms
             * Pop   = 350ms
             *
             * Total ≈ 1700ms
             *
             * We wait slightly longer before moving
             * to the Reset Password screen.
             */
            new Handler().postDelayed(() -> {

                if (animationDialog.isShowing()) {
                    animationDialog.dismiss();
                }

                openResetPassword();

            }, 1850);
        });

        animationDialog.show();

        /*
         * Dialog dimensions are set again after show()
         * because Android may reset them during creation.
         */
        Window shownWindow =
                animationDialog.getWindow();

        if (shownWindow != null) {

            WindowManager.LayoutParams params =
                    shownWindow.getAttributes();

            params.width =
                    dpToPx(280);

            params.height =
                    dpToPx(280);

            params.gravity =
                    Gravity.CENTER;

            params.dimAmount =
                    0.35f;

            shownWindow.setAttributes(params);

            shownWindow.setBackgroundDrawableResource(
                    android.R.color.transparent
            );

            shownWindow.addFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND
            );
        }
    }

    private void openResetPassword() {

        String identifier =
                getIntent().getStringExtra(
                        "recovery_identifier"
                );

        Intent intent =
                new Intent(
                        RecoveryVerificationActivity.this,
                        ResetPasswordActivity.class
                );

        intent.putExtra(
                "recovery_identifier",
                identifier
        );

        startActivity(intent);

        finish();
    }

    private int dpToPx(int dp) {

        return Math.round(
                dp * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

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

    private void playErrorSound() {

        android.media.ToneGenerator toneGenerator =
                new android.media.ToneGenerator(
                        android.media.AudioManager.STREAM_NOTIFICATION,
                        80
                );

        toneGenerator.startTone(
                android.media.ToneGenerator.TONE_PROP_NACK,
                180
        );

        new Handler().postDelayed(
                toneGenerator::release,
                250
        );
    }

    private void setupCodeBoxes() {

        setupNextDigit(code1, code2);
        setupNextDigit(code2, code3);
        setupNextDigit(code3, code4);
        setupNextDigit(code4, code5);
        setupNextDigit(code5, code6);

        setupBackspace(code2, code1);
        setupBackspace(code3, code2);
        setupBackspace(code4, code3);
        setupBackspace(code5, code4);
        setupBackspace(code6, code5);

        code1.requestFocus();
    }

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

    private void setupBackspace(
            EditText current,
            EditText previous
    ) {

        current.setOnKeyListener(
                (v, keyCode, event) -> {

                    if (keyCode == KeyEvent.KEYCODE_DEL
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

    private String maskIdentifier(
            String identifier
    ) {

        if (identifier.contains("@")) {

            String[] parts =
                    identifier.split("@");

            String name = parts[0];
            String domain = parts[1];

            if (name.length() <= 2) {

                return "***@" + domain;
            }

            return name.charAt(0)
                    + "***"
                    + name.charAt(name.length() - 1)
                    + "@"
                    + domain;
        }

        if (identifier.length() >= 4) {

            return "**** **** "
                    + identifier.substring(
                    identifier.length() - 4
            );
        }

        return "****";
    }

    @Override
    protected void onDestroy() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        super.onDestroy();
    }
}