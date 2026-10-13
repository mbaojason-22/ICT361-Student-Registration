package com.example.ict361studentregistration;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CourseRegistrationActivity extends AppCompatActivity {

    // Step panels
    private View yearSelectionPanel;
    private View semesterSelectionPanel;
    private View registrationPanel;

    // Year selection
    private RadioButton yearOne;
    private RadioButton yearTwo;
    private RadioButton yearThree;
    private RadioButton yearFour;

    // Semester selection
    private RadioButton semesterOne;
    private RadioButton semesterTwo;

    // Course selections
    private CheckBox courseIct361;
    private CheckBox courseIct261;
    private CheckBox courseIct402;
    private CheckBox courseIct371;
    private CheckBox courseIct381;

    private CheckBox extraIct221;
    private CheckBox extraIct232;
    private CheckBox extraIct201;

    // Text fields
    private TextView selectedYearText;
    private TextView registrationSelectionSummary;
    private TextView selectedCourseCount;
    private TextView registrationStatus;

    private EditText lecturerQuery;

    private String selectedYear = "";
    private String selectedSemester = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_course_registration);

        setupSystemBars();
        setupViews();
        setupYearSelection();
        setupSemesterSelection();
        setupCourseSelection();
        setupButtons();
        setupBottomNavigation();
    }

    private void setupSystemBars() {

        View rootView = findViewById(R.id.course_registration_root);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, windowInsets) -> {

            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            view.setPadding(
                    view.getPaddingLeft(),
                    insets.top,
                    view.getPaddingRight(),
                    view.getPaddingBottom() + insets.bottom
            );

            return windowInsets;
        });
    }

    private void setupViews() {

        yearSelectionPanel = findViewById(R.id.year_selection_panel);
        semesterSelectionPanel = findViewById(R.id.semester_selection_panel);
        registrationPanel = findViewById(R.id.registration_panel);

        yearOne = findViewById(R.id.year_one);
        yearTwo = findViewById(R.id.year_two);
        yearThree = findViewById(R.id.year_three);
        yearFour = findViewById(R.id.year_four);

        semesterOne = findViewById(R.id.semester_one);
        semesterTwo = findViewById(R.id.semester_two);

        selectedYearText = findViewById(R.id.selected_year_text);

        registrationSelectionSummary =
                findViewById(R.id.registration_selection_summary);

        selectedCourseCount =
                findViewById(R.id.selected_course_count);

        registrationStatus =
                findViewById(R.id.registration_status);

        lecturerQuery =
                findViewById(R.id.lecturer_query);

        courseIct361 = findViewById(R.id.course_ict361);
        courseIct261 = findViewById(R.id.course_ict261);
        courseIct402 = findViewById(R.id.course_ict402);
        courseIct371 = findViewById(R.id.course_ict371);
        courseIct381 = findViewById(R.id.course_ict381);

        extraIct221 = findViewById(R.id.extra_ict221);
        extraIct232 = findViewById(R.id.extra_ict232);
        extraIct201 = findViewById(R.id.extra_ict201);
    }

    // =========================================================
    // YEAR SELECTION
    // =========================================================

    private void setupYearSelection() {

        View.OnClickListener yearListener = v -> {

            RadioButton clicked = (RadioButton) v;

            yearOne.setChecked(false);
            yearTwo.setChecked(false);
            yearThree.setChecked(false);
            yearFour.setChecked(false);

            clicked.setChecked(true);

            selectedYear = clicked.getText().toString();

            selectedYearText.setText(
                    "Selected year: " + selectedYear
            );
        };

        yearOne.setOnClickListener(yearListener);
        yearTwo.setOnClickListener(yearListener);
        yearThree.setOnClickListener(yearListener);
        yearFour.setOnClickListener(yearListener);
    }

    // =========================================================
    // SEMESTER SELECTION
    // =========================================================

    private void setupSemesterSelection() {

        View.OnClickListener semesterListener = v -> {

            RadioButton clicked = (RadioButton) v;

            semesterOne.setChecked(false);
            semesterTwo.setChecked(false);

            clicked.setChecked(true);

            selectedSemester = clicked.getText().toString();
        };

        semesterOne.setOnClickListener(semesterListener);
        semesterTwo.setOnClickListener(semesterListener);
    }

    // =========================================================
    // COURSE SELECTION
    // =========================================================

    private void setupCourseSelection() {

        View.OnClickListener courseListener = v ->
                updateCourseCount();

        courseIct361.setOnClickListener(courseListener);
        courseIct261.setOnClickListener(courseListener);
        courseIct402.setOnClickListener(courseListener);
        courseIct371.setOnClickListener(courseListener);
        courseIct381.setOnClickListener(courseListener);

        extraIct221.setOnClickListener(courseListener);
        extraIct232.setOnClickListener(courseListener);
        extraIct201.setOnClickListener(courseListener);
    }

    private void updateCourseCount() {

        int count = getSelectedCourseCount();

        String courseText;

        if (count == 1) {
            courseText = "1 course selected";
        } else {
            courseText = count + " courses selected";
        }

        selectedCourseCount.setText(courseText);
    }

    // =========================================================
    // BUTTONS
    // =========================================================

    private void setupButtons() {

        // Top back button
        View backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());

        // YEAR -> SEMESTER
        Button yearContinueButton =
                findViewById(R.id.year_continue_button);

        yearContinueButton.setOnClickListener(v -> {

            if (selectedYear.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please select your year of study.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            yearSelectionPanel.setVisibility(View.GONE);
            semesterSelectionPanel.setVisibility(View.VISIBLE);
            registrationPanel.setVisibility(View.GONE);
        });

        // SEMESTER -> YEAR
        Button semesterBackButton =
                findViewById(R.id.semester_back_button);

        semesterBackButton.setOnClickListener(v -> {

            semesterSelectionPanel.setVisibility(View.GONE);
            registrationPanel.setVisibility(View.GONE);
            yearSelectionPanel.setVisibility(View.VISIBLE);
        });

        // SEMESTER -> REGISTRATION
        Button semesterContinueButton =
                findViewById(R.id.semester_continue_button);

        semesterContinueButton.setOnClickListener(v -> {

            if (selectedSemester.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please select a semester.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            registrationSelectionSummary.setText(
                    selectedYear + "   •   " + selectedSemester
            );

            semesterSelectionPanel.setVisibility(View.GONE);
            registrationPanel.setVisibility(View.VISIBLE);
        });

        // REGISTRATION -> SEMESTER
        Button registrationBackButton =
                findViewById(R.id.registration_back_button);

        registrationBackButton.setOnClickListener(v -> {

            registrationPanel.setVisibility(View.GONE);
            yearSelectionPanel.setVisibility(View.GONE);
            semesterSelectionPanel.setVisibility(View.VISIBLE);
        });

        // SUBMIT REGISTRATION REQUEST
        Button requestRegistrationButton =
                findViewById(R.id.request_registration_button);

        requestRegistrationButton.setOnClickListener(v ->
                submitRegistrationRequest()
        );
    }

    // =========================================================
    // SUBMIT REGISTRATION
    // =========================================================

    private void submitRegistrationRequest() {

        int selectedCourses = getSelectedCourseCount();

        // At least one course is required
        if (selectedCourses == 0) {

            Toast.makeText(
                    this,
                    "Please select at least one course.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        /*
         * Lecturer message is OPTIONAL.
         *
         * We still read it so that, later, the backend can send it
         * together with the registration request when provided.
         */
        String message = lecturerQuery.getText()
                .toString()
                .trim();

        String messageStatus;

        if (message.isEmpty()) {
            messageStatus = "No message added";
        } else {
            messageStatus = "Lecturer message included";
        }

        new AlertDialog.Builder(this)
                .setTitle("Submit Registration Request")
                .setMessage(
                        "Year: " + selectedYear +
                                "\nSemester: " + selectedSemester +
                                "\nCourses selected: " + selectedCourses +
                                "\n" + messageStatus +
                                "\n\nYour registration request will be sent to the lecturer for review."
                )
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("SUBMIT", (dialog, which) -> {

                    registrationStatus.setText(
                            "Status: Pending Lecturer Approval"
                    );

                    registrationStatus.setTextColor(
                            0xFFE57C00
                    );

                    Toast.makeText(
                            this,
                            "Registration request submitted successfully.",
                            Toast.LENGTH_LONG
                    ).show();

                    showPendingDialog();
                })
                .show();
    }

    private int getSelectedCourseCount() {

        int count = 0;

        if (courseIct361.isChecked()) count++;
        if (courseIct261.isChecked()) count++;
        if (courseIct402.isChecked()) count++;
        if (courseIct371.isChecked()) count++;
        if (courseIct381.isChecked()) count++;

        if (extraIct221.isChecked()) count++;
        if (extraIct232.isChecked()) count++;
        if (extraIct201.isChecked()) count++;

        return count;
    }

    private void showPendingDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Request Submitted ✓")
                .setMessage(
                        "Your course registration request has been submitted.\n\n"
                                + "Status: Pending Lecturer Approval\n\n"
                                + "The lecturer will review your selected courses and message."
                )
                .setPositiveButton("OK", null)
                .show();
    }

    // =========================================================
    // BOTTOM NAVIGATION
    // =========================================================

    private void setupBottomNavigation() {

        // Home
        View navHome = findViewById(R.id.nav_home);

        navHome.setOnClickListener(v -> {

            Intent intent = new Intent(
                    CourseRegistrationActivity.this,
                    StudentHomeActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
        });

        // Lab Groups
        View navLabGroups =
                findViewById(R.id.nav_lab_groups);

        navLabGroups.setOnClickListener(v -> {

            Intent intent = new Intent(
                    CourseRegistrationActivity.this,
                    LabGroupsActivity.class
            );

            startActivity(intent);
        });

        // Resources
        View navResources =
                findViewById(R.id.nav_resources);

        navResources.setOnClickListener(v -> {

            Intent intent = new Intent(
                    CourseRegistrationActivity.this,
                    ResourcesActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
        });

        // Profile
        View navProfile =
                findViewById(R.id.nav_profile);

        navProfile.setOnClickListener(v -> {

            Intent intent = new Intent(
                    CourseRegistrationActivity.this,
                    ProfileActivity.class
            );

            startActivity(intent);
        });
    }
}