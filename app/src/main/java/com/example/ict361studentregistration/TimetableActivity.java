
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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.ict361studentregistration.data.room.AppDatabase;
import com.example.ict361studentregistration.data.room.Timetable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TimetableActivity extends AppCompatActivity {

    private LinearLayout scheduleContainer;
    private View[] dayButtons;
    private final String[] days = {
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
    };

    private String selectedDay = "Monday";
    private List<Timetable> timetableEntries = new ArrayList<>();
    private Observer<List<Timetable>> timetableObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        View rootView = findViewById(R.id.timetable_root);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            view.setPadding(
                    view.getPaddingLeft(),
                    insets.top + dp(16),
                    view.getPaddingRight(),
                    insets.bottom + dp(16)
            );

            return windowInsets;
        });

        findViewById(R.id.back_button).setOnClickListener(v -> finish());

        scheduleContainer = findViewById(R.id.schedule_container);

        dayButtons = new View[]{
                findViewById(R.id.day_monday),
                findViewById(R.id.day_tuesday),
                findViewById(R.id.day_wednesday),
                findViewById(R.id.day_thursday),
                findViewById(R.id.day_friday)
        };

        for (int i = 0; i < dayButtons.length; i++) {
            final String day = days[i];
            dayButtons[i].setOnClickListener(v -> selectDay(day));
        }

        selectDay("Monday");
        observeTimetable();
    }

    private void observeTimetable() {
        LiveData<List<Timetable>> liveData =
                AppDatabase.getDatabase(getApplicationContext())
                        .timetableDao()
                        .getAllTimetable();

        timetableObserver = entries -> {
            timetableEntries = entries == null
                    ? new ArrayList<>()
                    : new ArrayList<>(entries);

            renderSelectedDay();
        };

        liveData.observe(this, timetableObserver);
    }

    private void selectDay(String day) {
        selectedDay = day;

        for (int i = 0; i < dayButtons.length; i++) {
            dayButtons[i].setBackgroundResource(
                    days[i].equals(day)
                            ? R.drawable.student_button
                            : R.drawable.student_login_panel
            );
        }

        renderSelectedDay();
    }

    private void renderSelectedDay() {
        if (scheduleContainer == null) return;

        scheduleContainer.removeAllViews();
        boolean found = false;

        for (Timetable entry : timetableEntries) {
            if (entry.dayOfWeek == null
                    || !entry.dayOfWeek.trim().equalsIgnoreCase(selectedDay)) {
                continue;
            }

            found = true;

            String color = String.format(
                    Locale.US, "#%06X",
                    (0xFFFFFF & entry.accentColor)
            );

            addClassCard(
                    safe(entry.startTime),
                    safe(entry.endTime),
                    safe(entry.courseCode),
                    safe(entry.courseName),
                    safe(entry.classType),
                    safe(entry.venue),
                    color
            );
        }

        if (!found) {
            showEmptyState("No classes scheduled for " + selectedDay);
        }
    }

    private void showEmptyState(String message) {
        TextView empty = new TextView(this);
        empty.setText(message);
        empty.setTextColor(Color.rgb(104, 122, 155));
        empty.setTextSize(14);
        empty.setGravity(Gravity.CENTER);
        empty.setPadding(dp(20), dp(32), dp(20), dp(32));
        scheduleContainer.addView(empty);
    }

    private void addClassCard(
            String startTime,
            String endTime,
            String courseCode,
            String courseName,
            String classType,
            String venue,
            String accentColor
    ) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(dp(16));
        card.setBackground(background);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dp(10));
        card.setLayoutParams(cardParams);

        TextView time = new TextView(this);
        time.setLayoutParams(new LinearLayout.LayoutParams(
                dp(62), LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        time.setText(startTime + "\n" + endTime);
        time.setTextColor(Color.rgb(16, 45, 99));
        time.setTextSize(12);
        time.setTypeface(null, android.graphics.Typeface.BOLD);

        View accentLine = new View(this);
        LinearLayout.LayoutParams accentParams = new LinearLayout.LayoutParams(
                dp(3), dp(70)
        );
        accentLine.setLayoutParams(accentParams);

        try {
            accentLine.setBackgroundColor(Color.parseColor(accentColor));
        } catch (IllegalArgumentException e) {
            accentLine.setBackgroundColor(Color.rgb(22, 135, 217));
        }

        LinearLayout information = new LinearLayout(this);
        information.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        );
        infoParams.setMargins(dp(12), 0, 0, 0);
        information.setLayoutParams(infoParams);

        TextView code = new TextView(this);
        code.setText(courseCode);
        code.setTextColor(Color.rgb(22, 135, 217));
        code.setTextSize(11);
        code.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView name = new TextView(this);
        name.setText(courseName);
        name.setTextColor(Color.rgb(16, 45, 99));
        name.setTextSize(15);
        name.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView type = new TextView(this);
        type.setText(classType);
        type.setTextColor(Color.rgb(104, 122, 155));
        type.setTextSize(11);

        TextView venueView = new TextView(this);
        venueView.setText(venue.isEmpty() ? "" : "Venue: " + venue);
        venueView.setTextColor(Color.rgb(104, 122, 155));
        venueView.setTextSize(11);

        information.addView(code);
        information.addView(name);
        information.addView(type);
        if (!venue.isEmpty()) {
            information.addView(venueView);
        }

        card.addView(time);
        card.addView(accentLine);
        card.addView(information);

        scheduleContainer.addView(card);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private int dp(int value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }
}
