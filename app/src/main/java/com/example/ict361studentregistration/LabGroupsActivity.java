package com.example.ict361studentregistration;

import android.app.AlertDialog;
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

        View rootView = findViewById(R.id.lab_groups_root);

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

        View backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());

        View requestGroupChange =
                findViewById(R.id.request_group_change);

        requestGroupChange.setOnClickListener(
                v -> showGroupChangeDialog()
        );

        View labAnnouncements =
                findViewById(R.id.lab_announcements);

        labAnnouncements.setOnClickListener(
                v -> showAnnouncements()
        );
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
                .setTitle("Request Lab Group Change")
                .setMessage(
                        "Select the lab group you would like to request."
                )
                .setItems(groups, (dialog, which) -> {

                    String selectedGroup = groups[which];

                    showRequestConfirmation(selectedGroup);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showRequestConfirmation(String selectedGroup) {

        new AlertDialog.Builder(this)
                .setTitle("Request Submitted")
                .setMessage(
                        "Your request to move to Lab Group "
                                + selectedGroup
                                + " has been recorded.\n\n"
                                + "Status: Pending lecturer approval."
                )
                .setPositiveButton("OK", null)
                .show();
    }

    private void showAnnouncements() {

        String[] announcements = {
                "ICT361 Lab Group meeting — Friday at 14:00.",
                "Remember to coordinate your lab assignment with your group members.",
                "Your current lab group has 8 of 15 available places."
        };

        new AlertDialog.Builder(this)
                .setTitle("Lab Announcements")
                .setItems(announcements, null)
                .setPositiveButton("Close", null)
                .show();
    }
}