package com.example.ict361studentregistration;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
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

public class VerifyCodeActivity extends AppCompatActivity {

    private EditText code1, code2, code3, code4, code5, code6;
    private Button verifyButton;
    private TextView contactText;
    private ImageButton backButton, helpButton;

    private String expectedCode = "123456";

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
        backButton = findViewById(R.id.verification_back_button);
        helpButton = findViewById(R.id.verification_help_button);

        String email = getIntent().getStringExtra("EMAIL");
        if (email != null && !email.isEmpty()) {
            contactText.setText(email);
        }

        String passedCode = getIntent().getStringExtra("EXPECTED_CODE");
        if (passedCode != null && !passedCode.isEmpty()) {
            expectedCode = passedCode;
        }

        setupCodeBoxes();

        verifyButton.setOnClickListener(v -> verifyCode());

        backButton.setOnClickListener(v -> finish());

        helpButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Lecturer Verification Help")
                    .setMessage("Enter the verification code sent to your email (Default demo code: 1234 or 123456).")
                    .setPositiveButton("GOT IT", null)
                    .show();
        });
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

            boxes[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && boxes[index].getText().length() == 0 && index > 0) {
                    boxes[index - 1].requestFocus();
                    return true;
                }
                return false;
            });
        }
        code1.requestFocus();
    }

    private void verifyCode() {
        String enteredCode = code1.getText().toString()
                + code2.getText().toString()
                + code3.getText().toString()
                + code4.getText().toString()
                + code5.getText().toString()
                + code6.getText().toString();

        if (enteredCode.length() < 6) {
            new AlertDialog.Builder(this)
                    .setTitle("Incomplete Code")
                    .setMessage("Please enter all 6 digits.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        boolean isCorrect = enteredCode.equals(expectedCode) || enteredCode.equals("1234") || enteredCode.equals("123456");

        if (!isCorrect) {
            // Vibrate and show red X animation (error sound removed as requested)
            vibrateError();
            showErrorAnimation();
            return;
        }

        // Correct code entered -> play success sound, vibrate, show green check animation
        showVerificationAnimation();
    }

    private void showVerificationAnimation() {
        playSuccessSound();
        vibrateSuccess();

        final Dialog animationDialog = new Dialog(this);
        animationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        VerificationAnimationView animationView = new VerificationAnimationView(this);
        animationDialog.setContentView(animationView);
        animationDialog.setCancelable(false);
        animationDialog.setCanceledOnTouchOutside(false);

        Window window = animationDialog.getWindow();
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

        animationDialog.setOnShowListener(dialog -> {
            animationView.startAnimation();

            new Handler().postDelayed(() -> {
                if (animationDialog.isShowing()) {
                    animationDialog.dismiss();
                }

                String nextActivity = getIntent().getStringExtra("NEXT_ACTIVITY");
                Class<?> targetClass;
                if ("RESET_PASSWORD".equals(nextActivity)) {
                    targetClass = ResetPasswordActivity.class;
                } else {
                    targetClass = CreateLecturerPasswordActivity.class;
                }

                Intent intent = new Intent(VerifyCodeActivity.this, targetClass);
                if (getIntent().getExtras() != null) {
                    intent.putExtras(getIntent().getExtras());
                }
                startActivity(intent);
                finish();
            }, 1850);
        });

        animationDialog.show();
    }

    private void showErrorAnimation() {
        final Dialog animationDialog = new Dialog(this);
        animationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        VerificationAnimationView animationView = new VerificationAnimationView(this);
        animationDialog.setContentView(animationView);
        animationDialog.setCancelable(false);
        animationDialog.setCanceledOnTouchOutside(false);

        Window window = animationDialog.getWindow();
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

        animationDialog.setOnShowListener(dialog -> {
            animationView.startErrorAnimation();

            new Handler().postDelayed(() -> {
                if (animationDialog.isShowing()) {
                    animationDialog.dismiss();
                }
            }, 1500);
        });

        animationDialog.show();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void playSuccessSound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.success);
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
            mediaPlayer.start();
        }
    }



    private void vibrateSuccess() {
        Vibrator vibrator;
        if (Build.VERSION.SDK_INT >= 31) {
            VibratorManager vibratorManager = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            vibrator = vibratorManager.getDefaultVibrator();
        } else {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        }
        if (vibrator == null) return;
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(70, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(70);
        }
    }

    private void vibrateError() {
        Vibrator vibrator;
        if (Build.VERSION.SDK_INT >= 31) {
            VibratorManager vibratorManager = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            vibrator = vibratorManager.getDefaultVibrator();
        } else {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        }
        if (vibrator == null) return;
        if (Build.VERSION.SDK_INT >= 26) {
            long[] pattern = {0, 80, 60, 120};
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
        } else {
            long[] pattern = {0, 80, 60, 120};
            vibrator.vibrate(pattern, -1);
        }
    }
}
