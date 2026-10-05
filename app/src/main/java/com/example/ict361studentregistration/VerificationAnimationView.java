package com.example.ict361studentregistration;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class VerificationAnimationView extends View {

    private final Paint ringPaint =
            new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint markPaint =
            new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF circleBounds =
            new RectF();

    private final Path markPath =
            new Path();

    private float ringProgress = 0f;
    private float markProgress = 0f;
    private float scale = 1f;

    private ValueAnimator ringAnimator;
    private ValueAnimator markAnimator;
    private ValueAnimator scaleAnimator;

    private boolean showMark = false;
    private boolean isError = false;

    public VerificationAnimationView(Context context) {
        super(context);
        init();
    }

    public VerificationAnimationView(
            Context context,
            AttributeSet attrs
    ) {
        super(context, attrs);
        init();
    }

    public VerificationAnimationView(
            Context context,
            AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        // =========================================================
        // CIRCULAR RING
        // =========================================================

        ringPaint.setStyle(
                Paint.Style.STROKE
        );

        ringPaint.setStrokeWidth(7f);

        ringPaint.setStrokeCap(
                Paint.Cap.ROUND
        );

        // =========================================================
        // CHECK / X MARK
        // =========================================================

        markPaint.setStyle(
                Paint.Style.STROKE
        );

        markPaint.setStrokeWidth(9f);

        markPaint.setStrokeCap(
                Paint.Cap.ROUND
        );

        markPaint.setStrokeJoin(
                Paint.Join.ROUND
        );

        setLayerType(
                View.LAYER_TYPE_SOFTWARE,
                null
        );
    }

    // =============================================================
    // DRAWING
    // =============================================================

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        float centerX =
                getWidth() / 2f;

        float centerY =
                getHeight() / 2f;

        float radius =
                Math.min(
                        getWidth(),
                        getHeight()
                ) * 0.34f;

        canvas.save();

        canvas.scale(
                scale,
                scale,
                centerX,
                centerY
        );

        circleBounds.set(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
        );

        // =========================================================
        // GREEN SUCCESS / RED ERROR
        // =========================================================

        if (isError) {

            ringPaint.setColor(
                    0xFFE53935
            );

            // RED X
            markPaint.setColor(
                    0xFFE53935
            );

        } else {

            ringPaint.setColor(
                    0xFF2EA043
            );

            // GREEN CHECK
            markPaint.setColor(
                    0xFF2EA043
            );
        }

        // =========================================================
        // CIRCULAR RING
        // =========================================================

        canvas.drawArc(
                circleBounds,
                -90f,
                360f * ringProgress,
                false,
                ringPaint
        );

        // =========================================================
        // CHECK OR X
        // =========================================================

        if (showMark && markProgress > 0f) {

            if (isError) {

                drawErrorMark(
                        canvas,
                        centerX,
                        centerY,
                        radius
                );

            } else {

                drawSuccessMark(
                        canvas,
                        centerX,
                        centerY,
                        radius
                );
            }
        }

        canvas.restore();
    }

    // =============================================================
    // SUCCESS ANIMATION
    // =============================================================

    public void startAnimation() {

        startVerificationAnimation(false);
    }

    // =============================================================
    // ERROR ANIMATION
    // =============================================================

    public void startErrorAnimation() {

        startVerificationAnimation(true);
    }

    // =============================================================
    // COMMON ANIMATION
    // =============================================================

    private void startVerificationAnimation(
            boolean error
    ) {

        stopAnimations();

        isError = error;

        ringProgress = 0f;
        markProgress = 0f;
        scale = 0.85f;
        showMark = false;

        invalidate();

        // =========================================================
        // RING ANIMATION
        // =========================================================

        ringAnimator =
                ValueAnimator.ofFloat(
                        0f,
                        1f
                );

        ringAnimator.setDuration(700);

        ringAnimator.setInterpolator(
                new DecelerateInterpolator()
        );

        ringAnimator.addUpdateListener(
                animation -> {

                    ringProgress =
                            (float)
                                    animation
                                            .getAnimatedValue();

                    invalidate();
                }
        );

        ringAnimator.addListener(
                new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(
                            Animator animation
                    ) {

                        startMarkAnimation();
                    }
                }
        );

        ringAnimator.start();
    }

    // =============================================================
    // CHECK / X ANIMATION
    // =============================================================

    private void startMarkAnimation() {

        showMark = true;

        markAnimator =
                ValueAnimator.ofFloat(
                        0f,
                        1f
                );

        markAnimator.setDuration(400);

        markAnimator.setInterpolator(
                new DecelerateInterpolator()
        );

        markAnimator.addUpdateListener(
                animation -> {

                    markProgress =
                            (float)
                                    animation
                                            .getAnimatedValue();

                    invalidate();
                }
        );

        markAnimator.addListener(
                new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(
                            Animator animation
                    ) {

                        startScaleAnimation();
                    }
                }
        );

        markAnimator.start();
    }

    // =============================================================
    // GREEN CHECK MARK
    // =============================================================

    private void drawSuccessMark(
            Canvas canvas,
            float centerX,
            float centerY,
            float radius
    ) {

        float startX =
                centerX - radius * 0.42f;

        float startY =
                centerY;

        float middleX =
                centerX - radius * 0.08f;

        float middleY =
                centerY + radius * 0.34f;

        float endX =
                centerX + radius * 0.48f;

        float endY =
                centerY - radius * 0.34f;

        markPath.reset();

        if (markProgress <= 0.5f) {

            float progress =
                    markProgress / 0.5f;

            float currentX =
                    startX
                            + (middleX - startX)
                            * progress;

            float currentY =
                    startY
                            + (middleY - startY)
                            * progress;

            markPath.moveTo(
                    startX,
                    startY
            );

            markPath.lineTo(
                    currentX,
                    currentY
            );

        } else {

            float progress =
                    (markProgress - 0.5f)
                            / 0.5f;

            float currentX =
                    middleX
                            + (endX - middleX)
                            * progress;

            float currentY =
                    middleY
                            + (endY - middleY)
                            * progress;

            markPath.moveTo(
                    startX,
                    startY
            );

            markPath.lineTo(
                    middleX,
                    middleY
            );

            markPath.lineTo(
                    currentX,
                    currentY
            );
        }

        canvas.drawPath(
                markPath,
                markPaint
        );
    }

    // =============================================================
    // RED X MARK
    // =============================================================

    private void drawErrorMark(
            Canvas canvas,
            float centerX,
            float centerY,
            float radius
    ) {

        float left =
                centerX - radius * 0.35f;

        float right =
                centerX + radius * 0.35f;

        float top =
                centerY - radius * 0.35f;

        float bottom =
                centerY + radius * 0.35f;

        markPath.reset();

        // ---------------------------------------------------------
        // FIRST DIAGONAL
        // ---------------------------------------------------------

        if (markProgress <= 0.5f) {

            float progress =
                    markProgress / 0.5f;

            float currentX =
                    left
                            + (right - left)
                            * progress;

            float currentY =
                    top
                            + (bottom - top)
                            * progress;

            markPath.moveTo(
                    left,
                    top
            );

            markPath.lineTo(
                    currentX,
                    currentY
            );

        } else {

            // First diagonal complete
            markPath.moveTo(
                    left,
                    top
            );

            markPath.lineTo(
                    right,
                    bottom
            );

            // -----------------------------------------------------
            // SECOND DIAGONAL
            // -----------------------------------------------------

            float progress =
                    (markProgress - 0.5f)
                            / 0.5f;

            float currentX =
                    right
                            + (left - right)
                            * progress;

            float currentY =
                    top
                            + (bottom - top)
                            * progress;

            markPath.moveTo(
                    right,
                    top
            );

            markPath.lineTo(
                    currentX,
                    currentY
            );
        }

        canvas.drawPath(
                markPath,
                markPaint
        );
    }

    // =============================================================
    // POP EFFECT
    // =============================================================

    private void startScaleAnimation() {

        scaleAnimator =
                ValueAnimator.ofFloat(
                        0.85f,
                        1.08f,
                        1f
                );

        scaleAnimator.setDuration(350);

        scaleAnimator.addUpdateListener(
                animation -> {

                    scale =
                            (float)
                                    animation
                                            .getAnimatedValue();

                    invalidate();
                }
        );

        scaleAnimator.start();
    }

    // =============================================================
    // STOP ANIMATIONS
    // =============================================================

    private void stopAnimations() {

        if (ringAnimator != null) {
            ringAnimator.cancel();
        }

        if (markAnimator != null) {
            markAnimator.cancel();
        }

        if (scaleAnimator != null) {
            scaleAnimator.cancel();
        }
    }

    // =============================================================
    // CLEAN UP
    // =============================================================

    @Override
    protected void onDetachedFromWindow() {

        stopAnimations();

        super.onDetachedFromWindow();
    }
}