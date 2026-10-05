package com.example.ict361studentregistration;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TimetableActivity extends AppCompatActivity {

    private LinearLayout scheduleContainer;

    private View monday;
    private View tuesday;
    private View wednesday;
    private View thursday;
    private View friday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timetable);

        // Handle different screen sizes, status bars,
        // notches and navigation bars.
        View rootView = findViewById(R.id.timetable_root);

        ViewCompat.setOnApplyWindowInsetsListener(
                rootView,
                (view, windowInsets) -> {

                    Insets insets = windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            view.getPaddingLeft(),
                            insets.top + 16,
                            view.getPaddingRight(),
                            insets.bottom + 16
                    );

                    return windowInsets;
                }
        );

        // Back button
        View backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());

        // Schedule container
        scheduleContainer = findViewById(R.id.schedule_container);

        // Day buttons
        monday = findViewById(R.id.day_monday);
        tuesday = findViewById(R.id.day_tuesday);
        wednesday = findViewById(R.id.day_wednesday);
        thursday = findViewById(R.id.day_thursday);
        friday = findViewById(R.id.day_friday);

        // Monday starts selected
        showMonday();

        // Monday
        monday.setOnClickListener(v -> showMonday());

        // Tuesday
        tuesday.setOnClickListener(v -> showTuesday());

        // Wednesday
        wednesday.setOnClickListener(v -> showWednesday());

        // Thursday
        thursday.setOnClickListener(v -> showThursday());

        // Friday
        friday.setOnClickListener(v -> showFriday());
    }

    private void showMonday() {

        selectDay(monday);

        showClass(
                "08:00",
                "10:00",
                "ICT351",
                "Advanced Databases",
                "Lecture",
                "#1687D9"
        );
    }

    private void showTuesday() {

        selectDay(tuesday);

        showClass(
                "10:00",
                "12:00",
                "ICT381",
                "Cybersecurity Principles",
                "Lecture",
                "#E53935"
        );
    }

    private void showWednesday() {

        selectDay(wednesday);

        showClass(
                "14:00",
                "16:00",
                "ICT361",
                "Mobile Application Programming",
                "Lecture",
                "#1687D9"
        );
    }

    private void showThursday() {

        selectDay(thursday);

        showClass(
                "09:00",
                "11:00",
                "ICT341",
                "System Modelling",
                "Lecture",
                "#E53935"
        );
    }

    private void showFriday() {

        selectDay(friday);

        showClass(
                "11:00",
                "13:00",
                "ICT371",
                "Theory of Computation",
                "Lecture",
                "#1687D9"
        );
    }

    private void selectDay(View selectedDay) {

        // Reset all days
        monday.setBackgroundResource(
                R.drawable.student_login_panel
        );

        tuesday.setBackgroundResource(
                R.drawable.student_login_panel
        );

        wednesday.setBackgroundResource(
                R.drawable.student_login_panel
        );

        thursday.setBackgroundResource(
                R.drawable.student_login_panel
        );

        friday.setBackgroundResource(
                R.drawable.student_login_panel
        );

        // Highlight selected day
        selectedDay.setBackgroundResource(
                R.drawable.student_button
        );
    }

    private void showClass(
            String startTime,
            String endTime,
            String courseCode,
            String courseName,
            String classType,
            String accentColor
    ) {

        // Remove the previous day's classes
        scheduleContainer.removeAllViews();

        // Main class card
        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(
                dp(14),
                dp(14),
                dp(14),
                dp(14)
        );

        GradientDrawable cardBackground =
                new GradientDrawable();

        cardBackground.setColor(
                Color.WHITE
        );

        cardBackground.setCornerRadius(
                dp(16)
        );

        card.setBackground(cardBackground);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                dp(10)
        );

        card.setLayoutParams(cardParams);

        // Time section
        TextView time = new TextView(this);

        time.setLayoutParams(
                new LinearLayout.LayoutParams(
                        dp(62),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        time.setText(
                startTime + "\n" + endTime
        );

        time.setTextColor(
                Color.rgb(16, 45, 99)
        );

        time.setTextSize(12);

        time.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        // Accent line
        View accentLine = new View(this);

        LinearLayout.LayoutParams accentParams =
                new LinearLayout.LayoutParams(
                        dp(3),
                        dp(60)
                );

        accentLine.setLayoutParams(accentParams);

        accentLine.setBackgroundColor(
                Color.parseColor(accentColor)
        );

        // Course information container
        LinearLayout information =
                new LinearLayout(this);

        information.setOrientation(
                LinearLayout.VERTICAL
        );

        LinearLayout.LayoutParams informationParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        informationParams.setMargins(
                dp(12),
                0,
                0,
                0
        );

        information.setLayoutParams(
                informationParams
        );

        // Course code
        TextView code = new TextView(this);

        code.setText(courseCode);

        code.setTextColor(
                Color.parseColor(accentColor)
        );

        code.setTextSize(11);

        code.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        // Course name
        TextView name = new TextView(this);

        name.setText(courseName);

        name.setTextColor(
                Color.rgb(16, 45, 99)
        );

        name.setTextSize(15);

        name.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        LinearLayout.LayoutParams nameParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        nameParams.setMargins(
                0,
                dp(3),
                0,
                0
        );

        name.setLayoutParams(nameParams);

        // Class type
        TextView type = new TextView(this);

        type.setText(classType);

        type.setTextColor(
                Color.rgb(104, 122, 155)
        );

        type.setTextSize(11);

        LinearLayout.LayoutParams typeParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        typeParams.setMargins(
                0,
                dp(3),
                0,
                0
        );

        type.setLayoutParams(typeParams);

        // Add information
        information.addView(code);
        information.addView(name);
        information.addView(type);

        // Add everything to card
        card.addView(time);
        card.addView(accentLine);
        card.addView(information);

        // Add card to timetable
        scheduleContainer.addView(card);
    }

    private int dp(int value) {

        return (int) (
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}