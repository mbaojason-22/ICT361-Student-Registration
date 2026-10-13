package com.example.ict361studentregistration;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ict361studentregistration.data.room.AppDatabase;
import com.example.ict361studentregistration.data.room.Assignment;
import com.example.ict361studentregistration.data.room.AssignmentDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AssignmentsActivity extends AppCompatActivity {

    private LinearLayout assignmentsContainer;
    private TextView allButton;
    private TextView pendingButton;
    private TextView submittedButton;

    private AssignmentDao assignmentDao;
    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    private final List<Assignment> assignments = new ArrayList<>();
    private String currentFilter = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments);

        View rootView = findViewById(R.id.assignments_root);

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

        View backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        assignmentsContainer = findViewById(R.id.assignments_container);
        allButton = findViewById(R.id.filter_all);
        pendingButton = findViewById(R.id.filter_pending);
        submittedButton = findViewById(R.id.filter_submitted);

        allButton.setOnClickListener(v -> showAssignments("ALL"));
        pendingButton.setOnClickListener(v -> showAssignments("PENDING"));
        submittedButton.setOnClickListener(v -> showAssignments("SUBMITTED"));

        AppDatabase database = AppDatabase.getDatabase(getApplicationContext());
        assignmentDao = database.assignmentDao();

        // Observe Room. The screen refreshes when its assignments change.
        assignmentDao.getAllAssignments().observe(this, roomAssignments -> {
            assignments.clear();

            if (roomAssignments != null) {
                assignments.addAll(roomAssignments);
            }

            showAssignments(currentFilter);
        });

        // Add example records only when the table has no assignments.
        databaseExecutor.execute(() -> {
            if (assignmentDao.countAssignments() == 0) {
                seedDemoAssignments();
            }
        });
    }

    private void seedDemoAssignments() {
        insertDemo(
                "ICT361-2026-01",
                "ICT361",
                "Mobile Application Development Lab",
                "Complete the Android registration application and submit your group work.",
                "08 October 2026 • 23:59",
                "PENDING",
                "#1687D9"
        );

        insertDemo(
                "ICT381-2026-01",
                "ICT381",
                "Cybersecurity Principles — Security Report",
                "Analyse common security threats and propose controls for a student information system.",
                "12 October 2026 • 17:00",
                "PENDING",
                "#E53935"
        );

        insertDemo(
                "ICT351-2026-01",
                "ICT351",
                "Advanced Databases — SQL Practical",
                "Design database queries and demonstrate relational database operations.",
                "30 September 2026",
                "SUBMITTED",
                "#1687D9"
        );

        insertDemo(
                "ICT341-2026-01",
                "ICT341",
                "System Modelling — UML Assignment",
                "Create UML models for the proposed Campus Companion system.",
                "15 October 2026 • 23:59",
                "PENDING",
                "#E53935"
        );
    }

    private void insertDemo(
            String id,
            String courseCode,
            String title,
            String description,
            String date,
            String status,
            String accentColor
    ) {
        Assignment assignment = new Assignment();

        assignment.assignmentId = id;
        assignment.courseCode = courseCode;
        assignment.title = title;
        assignment.description = description;
        assignment.date = date;
        assignment.status = status;
        assignment.accentColor = Color.parseColor(accentColor);
        assignment.synced = false;

        assignmentDao.insertAssignment(assignment);
    }

    private void showAssignments(String filter) {
        currentFilter = filter;
        assignmentsContainer.removeAllViews();
        updateFilterButtons(filter);

        boolean found = false;

        for (Assignment assignment : assignments) {
            if ("ALL".equals(filter)
                    || filter.equalsIgnoreCase(assignment.status)) {
                addAssignmentCard(assignment);
                found = true;
            }
        }

        if (!found) {
            showEmptyMessage(filter);
        }
    }

    private void updateFilterButtons(String selectedFilter) {
        setFilterStyle(allButton, selectedFilter.equals("ALL"));
        setFilterStyle(pendingButton, selectedFilter.equals("PENDING"));
        setFilterStyle(submittedButton, selectedFilter.equals("SUBMITTED"));
    }

    private void setFilterStyle(TextView button, boolean selected) {
        if (selected) {
            button.setBackgroundResource(R.drawable.student_button);
            button.setTextColor(Color.WHITE);
        } else {
            button.setBackgroundResource(R.drawable.student_login_panel);
            button.setTextColor(Color.rgb(16, 45, 99));
        }
    }

    private void addAssignmentCard(Assignment assignment) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(dp(16));
        card.setBackground(background);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dp(12));
        card.setLayoutParams(cardParams);

        LinearLayout topRow = new LinearLayout(this);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView course = new TextView(this);
        course.setText(assignment.courseCode);
        course.setTextColor(assignment.accentColor);
        course.setTextSize(12);
        course.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams courseParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );
        course.setLayoutParams(courseParams);

        TextView status = new TextView(this);
        status.setText(assignment.status);
        status.setTextSize(11);
        status.setTypeface(null, Typeface.BOLD);
        status.setTextColor(
                "SUBMITTED".equalsIgnoreCase(assignment.status)
                        ? Color.rgb(46, 160, 67)
                        : Color.rgb(229, 138, 0)
        );

        topRow.addView(course);
        topRow.addView(status);

        TextView title = new TextView(this);
        title.setText(assignment.title);
        title.setTextColor(Color.rgb(16, 45, 99));
        title.setTextSize(17);
        title.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.setMargins(0, dp(6), 0, 0);
        title.setLayoutParams(titleParams);

        TextView description = new TextView(this);
        description.setText(assignment.description);
        description.setTextColor(Color.rgb(104, 122, 155));
        description.setTextSize(12);

        LinearLayout.LayoutParams descriptionParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        descriptionParams.setMargins(0, dp(5), 0, 0);
        description.setLayoutParams(descriptionParams);

        TextView dueDate = new TextView(this);
        boolean submitted = "SUBMITTED".equalsIgnoreCase(assignment.status);

        dueDate.setText(
                (submitted ? "Submitted: " : "Due: ") + assignment.date
        );
        dueDate.setTextColor(
                submitted
                        ? Color.rgb(46, 160, 67)
                        : Color.rgb(16, 45, 99)
        );
        dueDate.setTextSize(12);
        dueDate.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        dateParams.setMargins(0, dp(12), 0, 0);
        dueDate.setLayoutParams(dateParams);

        card.addView(topRow);
        card.addView(title);
        card.addView(description);
        card.addView(dueDate);

        if (!submitted) {
            TextView markDone = new TextView(this);
            markDone.setText("MARK AS DONE  ✓");
            markDone.setGravity(Gravity.CENTER);
            markDone.setTextColor(Color.WHITE);
            markDone.setTextSize(12);
            markDone.setTypeface(null, Typeface.BOLD);

            GradientDrawable doneBackground = new GradientDrawable();
            doneBackground.setColor(Color.rgb(46, 160, 67));
            doneBackground.setCornerRadius(dp(10));
            markDone.setBackground(doneBackground);

            LinearLayout.LayoutParams doneParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dp(44)
                    );
            doneParams.setMargins(0, dp(14), 0, 0);
            markDone.setLayoutParams(doneParams);

            markDone.setOnClickListener(v -> markAsSubmitted(assignment));
            card.addView(markDone);
        }

        assignmentsContainer.addView(card);
    }

    private void markAsSubmitted(Assignment assignment) {
        // Update the database on a background thread, not the UI thread.
        databaseExecutor.execute(() -> {
            assignment.status = "SUBMITTED";
            assignment.synced = false;
            assignmentDao.updateAssignment(assignment);

            runOnUiThread(() ->
                    Toast.makeText(
                            AssignmentsActivity.this,
                            "Assignment marked as submitted.",
                            Toast.LENGTH_SHORT
                    ).show()
            );
        });
    }

    private void showEmptyMessage(String filter) {
        TextView message = new TextView(this);
        message.setText(
                "No " + filter.toLowerCase() + " assignments."
        );
        message.setTextColor(Color.rgb(104, 122, 155));
        message.setTextSize(14);
        message.setGravity(Gravity.CENTER);
        message.setPadding(dp(20), dp(40), dp(20), dp(40));

        assignmentsContainer.addView(message);
    }

    private int dp(int value) {
        return (int) (
                value * getResources().getDisplayMetrics().density
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseExecutor.shutdown();
    }
}
