package com.example.ict361studentregistration;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResourcesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_resources);

        View rootView = findViewById(R.id.resources_root);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            view.setPadding(
                    view.getPaddingLeft(),
                    insets.top + 16,
                    view.getPaddingRight(),
                    view.getPaddingBottom() + insets.bottom
            );

            return windowInsets;
        });

        // Back button
        View backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());

        // Course Materials
        View courseMaterialsCard = findViewById(R.id.course_materials_card);

        courseMaterialsCard.setOnClickListener(v -> showCourseMaterials());

        // Study Resources
        View studyResourcesCard = findViewById(R.id.study_resources_card);

        studyResourcesCard.setOnClickListener(v -> showStudyResources());

        // Course Registration
        View courseRegistrationCard =
                findViewById(R.id.course_registration_card);

        courseRegistrationCard.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ResourcesActivity.this,
                    CourseRegistrationActivity.class
            );

            startActivity(intent);
        });

        // Campus Maps
        View campusMapsCard = findViewById(R.id.campus_maps_card);

        campusMapsCard.setOnClickListener(v -> showCampusMaps());

        // Lab Guides
        View labGuidesCard = findViewById(R.id.lab_guides_card);

        labGuidesCard.setOnClickListener(v -> showLabGuides());

        // Academic Links
        View academicLinksCard = findViewById(R.id.academic_links_card);

        academicLinksCard.setOnClickListener(v -> showAcademicLinks());

        // Bottom navigation - Home
        View navHome = findViewById(R.id.nav_home);

        navHome.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ResourcesActivity.this,
                    StudentHomeActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
        });

        // Bottom navigation - Lab Groups
        View navLabGroups = findViewById(R.id.nav_lab_groups);

        navLabGroups.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ResourcesActivity.this,
                    LabGroupsActivity.class
            );

            startActivity(intent);
        });

        // Bottom navigation - Resources
        View navResources = findViewById(R.id.nav_resources);

        navResources.setOnClickListener(v -> {
            // Already on Resources
        });

        // Bottom navigation - Profile
        View navProfile = findViewById(R.id.nav_profile);

        navProfile.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ResourcesActivity.this,
                    ProfileActivity.class
            );

            startActivity(intent);
        });
    }

    private void showCourseMaterials() {

        new AlertDialog.Builder(this)
                .setTitle("Course Materials")
                .setMessage(
                        "Access your lecture notes, slides and other course materials here."
                )
                .setPositiveButton("OK", null)
                .show();
    }

    private void showStudyResources() {

        new AlertDialog.Builder(this)
                .setTitle("Study & Revision")
                .setMessage(
                        "Find revision materials, practice questions and useful study resources."
                )
                .setPositiveButton("OK", null)
                .show();
    }

    private void showCampusMaps() {

        new AlertDialog.Builder(this)
                .setTitle("Campus Maps")
                .setMessage(
                        "Campus locations include Old DH, NLT and New DH.\n\n"
                                + "Google Maps integration can be connected here later."
                )
                .setPositiveButton("OK", null)
                .show();
    }

    private void showLabGuides() {

        new AlertDialog.Builder(this)
                .setTitle("Lab Guides")
                .setMessage(
                        "View laboratory instructions, practical guides and other lab-related resources."
                )
                .setPositiveButton("OK", null)
                .show();
    }

    private void showAcademicLinks() {

        new AlertDialog.Builder(this)
                .setTitle("Academic Links")
                .setMessage(
                        "Useful academic websites and university links will appear here."
                )
                .setPositiveButton("OK", null)
                .show();
    }
}