package com.example.ict361studentregistration;

import android.graphics.Color;
import android.graphics.Typeface;
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

import java.util.ArrayList;
import java.util.List;

public class AssignmentsActivity extends AppCompatActivity {

    private LinearLayout assignmentsContainer;

    private TextView allButton;
    private TextView pendingButton;
    private TextView submittedButton;

    private final List<Assignment> assignments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_assignments);

        // Handle different screen sizes, status bars,
        // notches and navigation bars.
        View rootView = findViewById(R.id.assignments_root);

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

        // Find views
        assignmentsContainer =
                findViewById(R.id.assignments_container);

        allButton =
                findViewById(R.id.filter_all);

        pendingButton =
                findViewById(R.id.filter_pending);

        submittedButton =
                findViewById(R.id.filter_submitted);

        // Create demo assignments
        createAssignments();

        // Filter buttons
        allButton.setOnClickListener(v ->
                showAssignments("ALL")
        );

        pendingButton.setOnClickListener(v ->
                showAssignments("PENDING")
        );

        submittedButton.setOnClickListener(v ->
                showAssignments("SUBMITTED")
        );

        // Show all assignments when screen opens
        showAssignments("ALL");
    }

    private void createAssignments() {

        assignments.clear();

        assignments.add(
                new Assignment(
                        "ICT361",
                        "Mobile Application Development Lab",
                        "Complete the Android registration application and submit your group work.",
                        "08 October 2026 • 23:59",
                        "PENDING",
                        "#1687D9"
                )
        );

        assignments.add(
                new Assignment(
                        "ICT381",
                        "Cybersecurity Principles — Security Report",
                        "Analyse common security threats and propose controls for a student information system.",
                        "12 October 2026 • 17:00",
                        "PENDING",
                        "#E53935"
                )
        );

        assignments.add(
                new Assignment(
                        "ICT351",
                        "Advanced Databases — SQL Practical",
                        "Design database queries and demonstrate relational database operations.",
                        "30 September 2026",
                        "SUBMITTED",
                        "#1687D9"
                )
        );

        assignments.add(
                new Assignment(
                        "ICT341",
                        "System Modelling — UML Assignment",
                        "Create UML models for the proposed Campus Companion system.",
                        "15 October 2026 • 23:59",
                        "PENDING",
                        "#E53935"
                )
        );
    }

    private void showAssignments(String filter) {

        assignmentsContainer.removeAllViews();

        updateFilterButtons(filter);

        boolean found = false;

        for (Assignment assignment : assignments) {

            if (filter.equals("ALL")
                    || assignment.status.equals(filter)) {

                addAssignmentCard(assignment);

                found = true;
            }
        }

        if (!found) {
            showEmptyMessage(filter);
        }
    }

    private void updateFilterButtons(String selectedFilter) {

        setFilterStyle(
                allButton,
                selectedFilter.equals("ALL")
        );

        setFilterStyle(
                pendingButton,
                selectedFilter.equals("PENDING")
        );

        setFilterStyle(
                submittedButton,
                selectedFilter.equals("SUBMITTED")
        );
    }

    private void setFilterStyle(
            TextView button,
            boolean selected
    ) {

        if (selected) {

            button.setBackgroundResource(
                    R.drawable.student_button
            );

            button.setTextColor(Color.WHITE);

        } else {

            button.setBackgroundResource(
                    R.drawable.student_login_panel
            );

            button.setTextColor(
                    Color.rgb(16, 45, 99)
            );
        }
    }

    private void addAssignmentCard(
            Assignment assignment
    ) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                dp(16),
                dp(16),
                dp(16),
                dp(16)
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(Color.WHITE);

        background.setCornerRadius(
                dp(16)
        );

        card.setBackground(background);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                dp(12)
        );

        card.setLayoutParams(cardParams);

        // Top row
        LinearLayout topRow =
                new LinearLayout(this);

        topRow.setGravity(
                Gravity.CENTER_VERTICAL
        );

        // Course code
        TextView course =
                new TextView(this);

        course.setText(
                assignment.courseCode
        );

        course.setTextColor(
                Color.parseColor(
                        assignment.accentColor
                )
        );

        course.setTextSize(12);

        course.setTypeface(
                null,
                Typeface.BOLD
        );

        LinearLayout.LayoutParams courseParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        course.setLayoutParams(courseParams);

        // Status
        TextView status =
                new TextView(this);

        status.setText(
                assignment.status
        );

        status.setTextSize(11);

        status.setTypeface(
                null,
                Typeface.BOLD
        );

        if (assignment.status.equals("SUBMITTED")) {

            status.setTextColor(
                    Color.rgb(46, 160, 67)
            );

        } else {

            status.setTextColor(
                    Color.rgb(229, 138, 0)
            );
        }

        topRow.addView(course);
        topRow.addView(status);

        // Assignment title
        TextView title =
                new TextView(this);

        title.setText(
                assignment.title
        );

        title.setTextColor(
                Color.rgb(16, 45, 99)
        );

        title.setTextSize(17);

        title.setTypeface(
                null,
                Typeface.BOLD
        );

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        titleParams.setMargins(
                0,
                dp(6),
                0,
                0
        );

        title.setLayoutParams(titleParams);

        // Description
        TextView description =
                new TextView(this);

        description.setText(
                assignment.description
        );

        description.setTextColor(
                Color.rgb(104, 122, 155)
        );

        description.setTextSize(12);

        LinearLayout.LayoutParams descriptionParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        descriptionParams.setMargins(
                0,
                dp(5),
                0,
                0
        );

        description.setLayoutParams(
                descriptionParams
        );

        // Due date
        TextView dueDate =
                new TextView(this);

        if (assignment.status.equals("SUBMITTED")) {

            dueDate.setText(
                    "Submitted: " + assignment.date
            );

            dueDate.setTextColor(
                    Color.rgb(46, 160, 67)
            );

        } else {

            dueDate.setText(
                    "Due: " + assignment.date
            );

            dueDate.setTextColor(
                    Color.rgb(16, 45, 99)
            );
        }

        dueDate.setTextSize(12);

        dueDate.setTypeface(
                null,
                Typeface.BOLD
        );

        LinearLayout.LayoutParams dateParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        dateParams.setMargins(
                0,
                dp(12),
                0,
                0
        );

        dueDate.setLayoutParams(dateParams);

        // Add everything
        card.addView(topRow);
        card.addView(title);
        card.addView(description);
        card.addView(dueDate);

        // Pending assignments can be marked as done
        if (assignment.status.equals("PENDING")) {

            TextView markDone =
                    new TextView(this);

            markDone.setText(
                    "MARK AS DONE  ✓"
            );

            markDone.setGravity(
                    Gravity.CENTER
            );

            markDone.setTextColor(Color.WHITE);

            markDone.setTextSize(12);

            markDone.setTypeface(
                    null,
                    Typeface.BOLD
            );

            GradientDrawable doneBackground =
                    new GradientDrawable();

            doneBackground.setColor(
                    Color.rgb(46, 160, 67)
            );

            doneBackground.setCornerRadius(
                    dp(10)
            );

            markDone.setBackground(
                    doneBackground
            );

            LinearLayout.LayoutParams doneParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dp(44)
                    );

            doneParams.setMargins(
                    0,
                    dp(14),
                    0,
                    0
            );

            markDone.setLayoutParams(doneParams);

            markDone.setClickable(true);
            markDone.setFocusable(true);

            markDone.setOnClickListener(v -> {

                assignment.status = "SUBMITTED";

                showAssignments("ALL");
            });

            card.addView(markDone);
        }

        assignmentsContainer.addView(card);
    }

    private void showEmptyMessage(String filter) {

        TextView message =
                new TextView(this);

        message.setText(
                "No " +
                        filter.toLowerCase() +
                        " assignments."
        );

        message.setTextColor(
                Color.rgb(104, 122, 155)
        );

        message.setTextSize(14);

        message.setGravity(
                Gravity.CENTER
        );

        message.setPadding(
                dp(20),
                dp(40),
                dp(20),
                dp(40)
        );

        assignmentsContainer.addView(message);
    }

    private int dp(int value) {

        return (int) (
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }

    private static class Assignment {

        String courseCode;
        String title;
        String description;
        String date;
        String status;
        String accentColor;

        Assignment(
                String courseCode,
                String title,
                String description,
                String date,
                String status,
                String accentColor
        ) {

            this.courseCode = courseCode;
            this.title = title;
            this.description = description;
            this.date = date;
            this.status = status;
            this.accentColor = accentColor;
        }
    }
}