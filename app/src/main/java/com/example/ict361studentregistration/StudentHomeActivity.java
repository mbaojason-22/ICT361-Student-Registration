package com.example.ict361studentregistration;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StudentHomeActivity extends AppCompatActivity {

    private static final int ACTIVE_BLUE = Color.rgb(22, 135, 217);
    private static final int ACTIVE_BLUE_LIGHT = Color.rgb(230, 243, 255);
    private static final int INACTIVE_GREY = Color.rgb(104, 122, 155);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_home);

        // ---------------------------------------------------------
        // SYSTEM BARS
        // ---------------------------------------------------------

        View scrollView =
                findViewById(R.id.student_home_scroll);

        ViewCompat.setOnApplyWindowInsetsListener(
                scrollView,
                (view, windowInsets) -> {

                    Insets insets =
                            windowInsets.getInsets(
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


        // ---------------------------------------------------------
        // NOTIFICATIONS
        // ---------------------------------------------------------

        View notificationButton =
                findViewById(R.id.notification_button);

        notificationButton.setOnClickListener(
                v -> showNotifications()
        );


        // ---------------------------------------------------------
        // PROFILE AVATAR
        // ---------------------------------------------------------

        View studentAvatar =
                findViewById(R.id.student_avatar);

        studentAvatar.setOnClickListener(
                v -> openProfile()
        );


        // ---------------------------------------------------------
        // SEARCH BAR
        // ---------------------------------------------------------

        View searchBar =
                findViewById(R.id.home_search_bar);

        searchBar.setClickable(true);
        searchBar.setFocusable(true);

        searchBar.setOnClickListener(
                v -> showSearchDialog()
        );


        // ---------------------------------------------------------
        // IMPORTANT NOTICE
        // ---------------------------------------------------------

        View exploreButton =
                findViewById(R.id.explore_button);

        exploreButton.setOnClickListener(
                v -> showImportantNotice()
        );


        // ---------------------------------------------------------
        // SEE ALL
        // ---------------------------------------------------------

        View seeAllText =
                findViewById(R.id.see_all_text);

        seeAllText.setOnClickListener(
                v -> showAllFeatures()
        );


        // ---------------------------------------------------------
        // MY CLASSES
        // ---------------------------------------------------------

        View myClassesCard =
                findViewById(R.id.my_classes_card);

        myClassesCard.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    StudentHomeActivity.this,
                                    MyClassesActivity.class
                            );

                    startActivity(intent);
                }
        );


        // ---------------------------------------------------------
        // TIMETABLE
        // ---------------------------------------------------------

        View timetableCard =
                findViewById(R.id.timetable_card);

        timetableCard.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    StudentHomeActivity.this,
                                    TimetableActivity.class
                            );

                    startActivity(intent);
                }
        );


        // ---------------------------------------------------------
        // ASSIGNMENTS
        // ---------------------------------------------------------

        View assignmentsCard =
                findViewById(R.id.assignments_card);

        assignmentsCard.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    StudentHomeActivity.this,
                                    AssignmentsActivity.class
                            );

                    startActivity(intent);
                }
        );


        // ---------------------------------------------------------
        // LAB GROUPS
        // ---------------------------------------------------------

        View labGroupsCard =
                findViewById(R.id.study_groups_card);

        labGroupsCard.setOnClickListener(
                v -> openLabGroups()
        );


        // ---------------------------------------------------------
        // TODAY - FIRST CLASS
        // ---------------------------------------------------------

        View classOne =
                findViewById(R.id.class_one);

        classOne.setOnClickListener(
                v -> showClassDetails(
                        "ICT361",
                        "Mobile Application Programming",
                        "08:00 - 10:00",
                        "Mr Nyirenda",
                        "Old DH"
                )
        );


        // ---------------------------------------------------------
        // TODAY - SECOND CLASS
        // ---------------------------------------------------------

        View classTwo =
                findViewById(R.id.class_two);

        classTwo.setOnClickListener(
                v -> showClassDetails(
                        "ICT261",
                        "Database Systems",
                        "14:00 - 16:00",
                        "Mr Nyirenda",
                        "NLT"
                )
        );


        // ---------------------------------------------------------
        // BOTTOM NAVIGATION - HOME
        // ---------------------------------------------------------

        View navHome =
                findViewById(R.id.nav_home);

        navHome.setOnClickListener(
                v -> setHomeActive()
        );


        // ---------------------------------------------------------
        // BOTTOM NAVIGATION - LAB GROUPS
        // ---------------------------------------------------------

        View navLabGroups =
                findViewById(R.id.nav_chat);

        navLabGroups.setOnClickListener(
                v -> openLabGroups()
        );


        // Change Chat icon and label to Lab Groups
        if (navLabGroups instanceof LinearLayout) {

            LinearLayout navLayout =
                    (LinearLayout) navLabGroups;

            if (navLayout.getChildCount() > 0
                    && navLayout.getChildAt(0)
                    instanceof TextView) {

                TextView icon =
                        (TextView) navLayout.getChildAt(0);

                icon.setText("👥");
                icon.setTextSize(21);
                icon.setContentDescription(
                        "Lab Groups"
                );

                icon.setTextColor(
                        INACTIVE_GREY
                );

                icon.setBackground(
                        new ColorDrawable(
                                Color.TRANSPARENT
                        )
                );
            }

            if (navLayout.getChildCount() > 1
                    && navLayout.getChildAt(1)
                    instanceof TextView) {

                TextView label =
                        (TextView) navLayout.getChildAt(1);

                label.setText("Lab Groups");
                label.setTextColor(
                        INACTIVE_GREY
                );
            }
        }


        // ---------------------------------------------------------
        // BOTTOM NAVIGATION - RESOURCES
        // ---------------------------------------------------------

        View navResources =
                findViewById(R.id.nav_resources);

        navResources.setOnClickListener(
                v -> openResources()
        );


        // ---------------------------------------------------------
        // BOTTOM NAVIGATION - PROFILE
        // ---------------------------------------------------------

        View navProfile =
                findViewById(R.id.nav_profile);

        navProfile.setOnClickListener(
                v -> openProfile()
        );


        // Home starts as the active section
        setHomeActive();
    }


    // -------------------------------------------------------------
    // SEARCH
    // -------------------------------------------------------------

    private void showSearchDialog() {

        final EditText searchInput =
                new EditText(this);

        searchInput.setHint(
                "Search courses, notes, events..."
        );

        searchInput.setSingleLine(true);

        searchInput.setTextColor(
                Color.rgb(16, 45, 99)
        );

        searchInput.setHintTextColor(
                Color.rgb(137, 150, 170)
        );

        searchInput.setPadding(
                dp(14),
                dp(10),
                dp(14),
                dp(10)
        );


        LinearLayout searchContainer =
                new LinearLayout(this);

        searchContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        searchContainer.setPadding(
                dp(20),
                dp(5),
                dp(20),
                dp(5)
        );

        searchContainer.addView(
                searchInput,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(55)
                )
        );


        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Search Campus Companion")
                        .setView(searchContainer)
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Search",
                                null
                        )
                        .create();


        dialog.setOnShowListener(
                ignored -> {

                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    ).setOnClickListener(v -> {

                        String query =
                                searchInput.getText()
                                        .toString()
                                        .trim();

                        if (query.isEmpty()) {

                            searchInput.setError(
                                    "Enter something to search."
                            );

                            return;
                        }

                        hideKeyboard(
                                searchInput
                        );

                        dialog.dismiss();

                        showSearchResults(
                                query
                        );
                    });


                    searchInput.requestFocus();

                    dialog.getWindow()
                            .setSoftInputMode(
                                    android.view.WindowManager
                                            .LayoutParams
                                            .SOFT_INPUT_STATE_ALWAYS_VISIBLE
                            );
                }
        );

        dialog.show();
    }


    private void showSearchResults(
            String query
    ) {

        String searchQuery =
                query.toLowerCase(
                        Locale.ROOT
                );


        List<String> results =
                new ArrayList<>();

        addSearchResult(
                results,
                searchQuery,
                "ICT361 — Mobile Application Programming",
                "Course • Today 08:00 - 10:00 • Old DH"
        );

        addSearchResult(
                results,
                searchQuery,
                "ICT261 — Database Systems",
                "Course • Today 14:00 - 16:00 • NLT"
        );

        addSearchResult(
                results,
                searchQuery,
                "ICT402 — Statistics & Empirical Methods",
                "Course • My Classes"
        );

        addSearchResult(
                results,
                searchQuery,
                "My Classes",
                "Academic section"
        );

        addSearchResult(
                results,
                searchQuery,
                "Timetable",
                "Academic section"
        );

        addSearchResult(
                results,
                searchQuery,
                "Assignments",
                "Academic section"
        );

        addSearchResult(
                results,
                searchQuery,
                "Lab Groups",
                "Group section • G02"
        );

        addSearchResult(
                results,
                searchQuery,
                "Resources",
                "Learning resources"
        );

        addSearchResult(
                results,
                searchQuery,
                "Campus Maps",
                "Resources • Campus venues"
        );

        addSearchResult(
                results,
                searchQuery,
                "Lab Guides",
                "Resources • Lab instructions"
        );

        addSearchResult(
                results,
                searchQuery,
                "Academic Links",
                "Resources • University links"
        );

        addSearchResult(
                results,
                searchQuery,
                "My Profile",
                "Student profile"
        );


        if (results.isEmpty()) {

            new AlertDialog.Builder(this)
                    .setTitle("No Results")
                    .setMessage(
                            "Nothing matched \"" +
                                    query +
                                    "\".\n\n" +
                                    "Try searching for a course, "
                                    + "timetable, assignment, "
                                    + "Lab Groups, Resources or Profile."
                    )
                    .setPositiveButton(
                            "OK",
                            null
                    )
                    .show();

            return;
        }


        StringBuilder message =
                new StringBuilder();

        for (int i = 0;
             i < results.size();
             i++) {

            message.append(
                    results.get(i)
            );

            if (i < results.size() - 1) {
                message.append(
                        "\n\n"
                );
            }
        }


        new AlertDialog.Builder(this)
                .setTitle(
                        "Search Results"
                )
                .setMessage(
                        message.toString()
                )
                .setPositiveButton(
                        "Close",
                        null
                )
                .show();
    }


    private void addSearchResult(
            List<String> results,
            String query,
            String title,
            String details
    ) {

        String combined =
                (title + " " + details)
                        .toLowerCase(
                                Locale.ROOT
                        );

        if (combined.contains(query)) {

            results.add(
                    title
                            + "\n"
                            + details
            );
        }
    }


    private void hideKeyboard(
            View view
    ) {

        InputMethodManager manager =
                (InputMethodManager)
                        getSystemService(
                                Context.INPUT_METHOD_SERVICE
                        );

        if (manager != null) {

            manager.hideSoftInputFromWindow(
                    view.getWindowToken(),
                    0
            );
        }
    }


    // -------------------------------------------------------------
    // HOME ACTIVE STATE
    // -------------------------------------------------------------

    private void setHomeActive() {

        View navHome =
                findViewById(R.id.nav_home);

        if (navHome instanceof LinearLayout) {

            LinearLayout navLayout =
                    (LinearLayout) navHome;

            if (navLayout.getChildCount() > 0
                    && navLayout.getChildAt(0)
                    instanceof TextView) {

                TextView icon =
                        (TextView) navLayout.getChildAt(0);

                icon.setTextColor(
                        ACTIVE_BLUE
                );

                icon.setBackground(
                        new ColorDrawable(
                                ACTIVE_BLUE_LIGHT
                        )
                );
            }

            if (navLayout.getChildCount() > 1
                    && navLayout.getChildAt(1)
                    instanceof TextView) {

                TextView label =
                        (TextView) navLayout.getChildAt(1);

                label.setTextColor(
                        ACTIVE_BLUE
                );

                label.setTypeface(
                        null,
                        Typeface.BOLD
                );
            }
        }
    }


    // -------------------------------------------------------------
    // OPEN LAB GROUPS
    // -------------------------------------------------------------

    private void openLabGroups() {

        Intent intent =
                new Intent(
                        StudentHomeActivity.this,
                        LabGroupsActivity.class
                );

        startActivity(intent);
    }


    // -------------------------------------------------------------
    // OPEN RESOURCES
    // -------------------------------------------------------------

    private void openResources() {

        Intent intent =
                new Intent(
                        StudentHomeActivity.this,
                        ResourcesActivity.class
                );

        startActivity(intent);
    }


    // -------------------------------------------------------------
    // OPEN PROFILE
    // -------------------------------------------------------------

    private void openProfile() {

        Intent intent =
                new Intent(
                        StudentHomeActivity.this,
                        ProfileActivity.class
                );

        startActivity(intent);
    }


    // -------------------------------------------------------------
    // NOTIFICATIONS
    // -------------------------------------------------------------

    private void showNotifications() {

        String[] notifications = {
                "ICT361 Group Lab — Check your submission requirements.",
                "Your student registration is currently active.",
                "Welcome to Campus Companion!"
        };

        new AlertDialog.Builder(this)
                .setTitle("Notifications")
                .setItems(
                        notifications,
                        null
                )
                .setPositiveButton(
                        "Close",
                        null
                )
                .show();
    }


    // -------------------------------------------------------------
    // IMPORTANT NOTICE
    // -------------------------------------------------------------

    private void showImportantNotice() {

        new AlertDialog.Builder(this)
                .setTitle(
                        "ICT361 Group Lab Submission"
                )
                .setMessage(
                        "Check your group requirements and "
                                + "submission details.\n\n"
                                + "Make sure your lab group is "
                                + "active and that you coordinate "
                                + "your submission with your group "
                                + "members."
                )
                .setPositiveButton(
                        "Got it",
                        null
                )
                .show();
    }


    // -------------------------------------------------------------
    // SEE ALL
    // -------------------------------------------------------------

    private void showAllFeatures() {

        String[] features = {
                "My Classes",
                "Timetable",
                "Assignments",
                "Lab Groups",
                "Resources",
                "My Profile"
        };

        new AlertDialog.Builder(this)
                .setTitle(
                        "Campus Companion Features"
                )
                .setItems(
                        features,
                        (dialog, which) -> openFeature(which)
                )
                .setPositiveButton(
                        "Close",
                        null
                )
                .show();
    }


    // -------------------------------------------------------------
    // OPEN FEATURE SELECTED FROM SEE ALL
    // -------------------------------------------------------------

    private void openFeature(int featureIndex) {

        Intent intent = null;

        switch (featureIndex) {

            case 0:
                intent = new Intent(
                        StudentHomeActivity.this,
                        MyClassesActivity.class
                );
                break;

            case 1:
                intent = new Intent(
                        StudentHomeActivity.this,
                        TimetableActivity.class
                );
                break;

            case 2:
                intent = new Intent(
                        StudentHomeActivity.this,
                        AssignmentsActivity.class
                );
                break;

            case 3:
                openLabGroups();
                return;

            case 4:
                openResources();
                return;

            case 5:
                openProfile();
                return;

            default:
                return;
        }

        if (intent != null) {
            startActivity(intent);
        }
    }


    // -------------------------------------------------------------
    // CLASS DETAILS
    // -------------------------------------------------------------

    private void showClassDetails(
            String courseCode,
            String courseName,
            String time,
            String lecturer,
            String venue
    ) {

        new AlertDialog.Builder(this)
                .setTitle(
                        courseCode
                                + " — Class Details"
                )
                .setMessage(
                        "Course\n"
                                + courseCode
                                + " - "
                                + courseName
                                + "\n\n"
                                + "Time\n"
                                + time
                                + "\n\n"
                                + "Lecturer\n"
                                + lecturer
                                + "\n\n"
                                + "Venue\n"
                                + venue
                )
                .setPositiveButton(
                        "Close",
                        null
                )
                .show();
    }


    // -------------------------------------------------------------
    // DP HELPER
    // -------------------------------------------------------------

    private int dp(
            int value
    ) {

        return (int) (
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}