package com.example.ict361studentregistration;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LabGroupsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lab_groups);

        // Handle system bars
        View rootView =
                findViewById(R.id.study_groups_root);

        ViewCompat.setOnApplyWindowInsetsListener(
                rootView,
                (view, windowInsets) -> {

                    Insets insets =
                            windowInsets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    view.setPadding(
                            view.getPaddingLeft(),
                            insets.top + 16,
                            view.getPaddingRight(),
                            view.getPaddingBottom() + insets.bottom
                    );

                    return windowInsets;
                }
        );


        // Back button
        View backButton =
                findViewById(R.id.back_button);

        backButton.setOnClickListener(
                v -> finish()
        );


        // Group Chat
        View groupChat =
                findViewById(R.id.group_chat);

        groupChat.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            LabGroupsActivity.this,
                            GroupChatActivity.class
                    );

            startActivity(intent);
        });


        // Lecturer Notices
        View groupAnnouncements =
                findViewById(R.id.group_announcements);

        groupAnnouncements.setOnClickListener(
                v -> showAnnouncements()
        );


        // Request Group Change
        View requestGroupChange =
                findViewById(R.id.request_group_change);

        requestGroupChange.setOnClickListener(
                v -> showGroupChangeDialog()
        );


        // My Group Requests
        View myGroupRequests =
                findViewById(R.id.my_group_requests);

        myGroupRequests.setOnClickListener(
                v -> showMyRequests()
        );


        // Bottom Navigation - Home
        View navHome =
                findViewById(R.id.nav_home);

        navHome.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            LabGroupsActivity.this,
                            StudentHomeActivity.class
                    );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
        });


        // Bottom Navigation - Lab Groups
        View navLabGroups =
                findViewById(R.id.nav_lab_groups);

        navLabGroups.setOnClickListener(
                v -> {
                    // Already on Lab Groups.
                }
        );


        // Bottom Navigation - Resources
        View navResources =
                findViewById(R.id.nav_resources);

        navResources.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            LabGroupsActivity.this,
                            ResourcesActivity.class
                    );

            startActivity(intent);
        });


        // Bottom Navigation - Profile
        View navProfile =
                findViewById(R.id.nav_profile);

        navProfile.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            LabGroupsActivity.this,
                            ProfileActivity.class
                    );

            startActivity(intent);
        });
    }


    private void showGroupChangeDialog() {

        String[] groups = {
                "G01",
                "G02",
                "G03",
                "G04",
                "Unassigned"
        };

        new AlertDialog.Builder(this)
                .setTitle("Request Group Change")
                .setMessage(
                        "Select the group you would like to request."
                )
                .setItems(
                        groups,
                        (dialog, which) -> {

                            String selectedGroup =
                                    groups[which];

                            showRequestConfirmation(
                                    selectedGroup
                            );
                        }
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }


    private void showRequestConfirmation(
            String selectedGroup
    ) {

        new AlertDialog.Builder(this)
                .setTitle("Request Submitted")
                .setMessage(
                        "Your request to move to "
                                + selectedGroup
                                + " has been recorded.\n\n"
                                + "Status: Pending lecturer approval."
                )
                .setPositiveButton(
                        "OK",
                        null
                )
                .show();
    }


    private void showAnnouncements() {

        String[] announcements = {
                "ICT361 Group Lab meeting — Friday at 14:00.",
                "Remember to coordinate your lab assignment with your group members.",
                "Your current lab group has 8 of 15 available places."
        };

        new AlertDialog.Builder(this)
                .setTitle("Lab Announcements")
                .setItems(
                        announcements,
                        null
                )
                .setPositiveButton(
                        "Close",
                        null
                )
                .show();
    }


    private void showMyRequests() {

        new AlertDialog.Builder(this)
                .setTitle("My Group Requests")
                .setMessage(
                        "No active group-change requests.\n\n"
                                + "When you submit a request, "
                                + "its status will appear here."
                )
                .setPositiveButton(
                        "OK",
                        null
                )
                .show();
    }
}