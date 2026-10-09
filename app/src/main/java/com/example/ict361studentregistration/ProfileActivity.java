package com.example.ict361studentregistration;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileName;
    private TextView profileFullName;
    private TextView profileProgramme;
    private TextView profileProgrammeDetail;
    private TextView profileStudentNumber;
    private TextView profileGender;
    private TextView profileYear;
    private TextView profileLabGroup;
    private TextView profileEmail;
    private TextView profilePhone;
    private TextView profileBackupEmail;

    private SharedPreferences preferences;

    private static final String PREFS_NAME =
            "campus_companion_profile";

    private static final String DEFAULT_NAME =
            "Student Name";

    private static final String DEFAULT_PROGRAMME =
            "Computer Science";

    private static final String DEFAULT_STUDENT_NUMBER =
            "000000000";

    private static final String DEFAULT_GENDER =
            "Not set";

    private static final String DEFAULT_YEAR =
            "Not set";

    private static final String DEFAULT_EMAIL =
            "student@example.com";

    private static final String DEFAULT_PHONE =
            "Not added";

    private static final String DEFAULT_BACKUP_EMAIL =
            "Not added";

    // Receive profile updates
    private final ActivityResultLauncher<Intent> editProfileLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode()
                                != RESULT_OK
                                || result.getData() == null) {

                            return;
                        }

                        Intent data = result.getData();

                        saveUpdatedProfile(data);
                        loadProfileInformation();

                        Toast.makeText(
                                this,
                                "Your profile has been updated.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        preferences = getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
        );

        setupSystemBars();
        setupViews();
        setupButtons();

        loadProfileInformation();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (preferences != null) {
            loadProfileInformation();
        }
    }

    // =========================================================
    // SYSTEM BARS
    // =========================================================

    private void setupSystemBars() {

        View rootView =
                findViewById(R.id.profile_root);

        ViewCompat.setOnApplyWindowInsetsListener(
                rootView,
                (view, windowInsets) -> {

                    Insets insets =
                            windowInsets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    view.setPadding(
                            view.getPaddingLeft(),
                            insets.top,
                            view.getPaddingRight(),
                            view.getPaddingBottom()
                                    + insets.bottom
                    );

                    return windowInsets;
                }
        );
    }

    // =========================================================
    // FIND VIEWS
    // =========================================================

    private void setupViews() {

        profileName =
                findViewById(R.id.profile_name);

        profileFullName =
                findViewById(R.id.profile_full_name);

        profileProgramme =
                findViewById(R.id.profile_programme);

        profileProgrammeDetail =
                findViewById(R.id.profile_programme_detail);

        profileStudentNumber =
                findViewById(R.id.profile_student_number);

        profileGender =
                findViewById(R.id.profile_gender);

        profileYear =
                findViewById(R.id.profile_year);

        profileLabGroup =
                findViewById(R.id.profile_lab_group);

        profileEmail =
                findViewById(R.id.profile_email);

        profilePhone =
                findViewById(R.id.profile_phone);

        profileBackupEmail =
                findViewById(R.id.profile_backup_email);
    }

    // =========================================================
    // LOAD PROFILE
    // =========================================================

    private void loadProfileInformation() {

        String name =
                preferences.getString(
                        "name",
                        DEFAULT_NAME
                );

        String programme =
                preferences.getString(
                        "programme",
                        DEFAULT_PROGRAMME
                );

        String studentNumber =
                preferences.getString(
                        "student_number",
                        DEFAULT_STUDENT_NUMBER
                );

        String gender =
                preferences.getString(
                        "gender",
                        DEFAULT_GENDER
                );

        String year =
                preferences.getString(
                        "year",
                        DEFAULT_YEAR
                );

        String email =
                preferences.getString(
                        "email",
                        DEFAULT_EMAIL
                );

        String phone =
                preferences.getString(
                        "phone",
                        DEFAULT_PHONE
                );

        String backupEmail =
                preferences.getString(
                        "backup_email",
                        DEFAULT_BACKUP_EMAIL
                );

        profileName.setText(name);
        profileFullName.setText(name);

        profileProgramme.setText(programme);
        profileProgrammeDetail.setText(programme);

        profileStudentNumber.setText(studentNumber);

        profileGender.setText(gender);
        profileYear.setText(year);

        profileLabGroup.setText(
                preferences.getString(
                        "lab_group",
                        "G02"
                )
        );

        profileEmail.setText(email);
        profilePhone.setText(phone);
        profileBackupEmail.setText(backupEmail);
    }

    // =========================================================
    // SAVE PROFILE UPDATES
    // =========================================================

    private void saveUpdatedProfile(Intent data) {

        SharedPreferences.Editor editor =
                preferences.edit();

        String updatedName =
                data.getStringExtra("updated_name");

        String updatedProgramme =
                data.getStringExtra("updated_programme");

        String updatedGender =
                data.getStringExtra("updated_gender");

        String updatedYear =
                data.getStringExtra("updated_year");

        String updatedEmail =
                data.getStringExtra("updated_email");

        String updatedPhone =
                data.getStringExtra("updated_phone");

        if (updatedName != null
                && !updatedName.trim().isEmpty()) {

            editor.putString(
                    "name",
                    updatedName.trim()
            );
        }

        if (updatedProgramme != null
                && !updatedProgramme.trim().isEmpty()) {

            editor.putString(
                    "programme",
                    updatedProgramme.trim()
            );
        }

        if (updatedGender != null
                && !updatedGender.trim().isEmpty()) {

            editor.putString(
                    "gender",
                    updatedGender.trim()
            );
        }

        if (updatedYear != null
                && !updatedYear.trim().isEmpty()) {

            editor.putString(
                    "year",
                    updatedYear.trim()
            );
        }

        if (updatedEmail != null) {

            String email =
                    updatedEmail.trim();

            if (email.isEmpty()) {
                email = DEFAULT_EMAIL;
            }

            editor.putString(
                    "email",
                    email
            );
        }

        if (updatedPhone != null) {

            String phone =
                    updatedPhone.trim();

            if (phone.isEmpty()) {
                phone = DEFAULT_PHONE;
            }

            editor.putString(
                    "phone",
                    phone
            );
        }

        // Student number remains protected.
        String studentNumber =
                preferences.getString(
                        "student_number",
                        DEFAULT_STUDENT_NUMBER
                );

        editor.putString(
                "student_number",
                studentNumber
        );

        editor.apply();
    }

    // =========================================================
    // BUTTONS
    // =========================================================

    private void setupButtons() {

        // Back
        View backButton =
                findViewById(R.id.back_button);

        backButton.setOnClickListener(
                v -> finish()
        );

        // Edit Profile
        View editProfileButton =
                findViewById(
                        R.id.edit_profile_button
                );

        editProfileButton.setOnClickListener(
                v -> openEditProfile()
        );

        // My Requests
        View requestsButton =
                findViewById(
                        R.id.profile_requests_button
                );

        requestsButton.setOnClickListener(
                v -> showMyRequests()
        );

        // Sign Out
        View signOutButton =
                findViewById(
                        R.id.sign_out_button
                );

        signOutButton.setOnClickListener(
                v -> confirmSignOut()
        );

        // Change Password
        View changePasswordButton =
                findViewById(
                        R.id.change_password_button
                );

        changePasswordButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            ProfileActivity.this,
                            ChangePasswordActivity.class
                    );

            startActivity(intent);
        });

        // Backup Email
        View backupEmailButton =
                findViewById(
                        R.id.backup_email_button
                );

        backupEmailButton.setOnClickListener(
                v -> openBackupEmail()
        );

        setupBottomNavigation();
    }

    // =========================================================
    // EDIT PROFILE
    // =========================================================

    private void openEditProfile() {

        Intent intent =
                new Intent(
                        ProfileActivity.this,
                        EditProfileActivity.class
                );

        intent.putExtra(
                "current_name",
                preferences.getString(
                        "name",
                        DEFAULT_NAME
                )
        );

        intent.putExtra(
                "current_programme",
                preferences.getString(
                        "programme",
                        DEFAULT_PROGRAMME
                )
        );

        intent.putExtra(
                "current_student_number",
                preferences.getString(
                        "student_number",
                        DEFAULT_STUDENT_NUMBER
                )
        );

        intent.putExtra(
                "current_gender",
                preferences.getString(
                        "gender",
                        DEFAULT_GENDER
                )
        );

        intent.putExtra(
                "current_year",
                preferences.getString(
                        "year",
                        DEFAULT_YEAR
                )
        );

        intent.putExtra(
                "current_email",
                preferences.getString(
                        "email",
                        DEFAULT_EMAIL
                )
        );

        intent.putExtra(
                "current_phone",
                preferences.getString(
                        "phone",
                        DEFAULT_PHONE
                )
        );

        editProfileLauncher.launch(intent);
    }

    // =========================================================
    // MY REQUESTS
    // =========================================================

    private void showMyRequests() {

        new AlertDialog.Builder(this)
                .setTitle("My Requests")
                .setMessage(
                        "Course Registration\n"
                                + "Status: Pending Lecturer Approval\n\n"
                                + "Lab Group Change\n"
                                + "No recent request"
                )
                .setPositiveButton(
                        "OK",
                        null
                )
                .show();
    }

    // =========================================================
    // BACKUP EMAIL
    // =========================================================

    private void openBackupEmail() {

        Intent intent =
                new Intent(
                        ProfileActivity.this,
                        BackupEmailActivity.class
                );

        startActivity(intent);
    }

    // =========================================================
    // SIGN OUT
    // =========================================================

    private void confirmSignOut() {

        new AlertDialog.Builder(this)
                .setTitle("Sign Out")
                .setMessage(
                        "Are you sure you want to sign out of Campus Companion?"
                )
                .setNegativeButton(
                        "CANCEL",
                        null
                )
                .setPositiveButton(
                        "SIGN OUT",
                        (dialog, which) -> signOut()
                )
                .show();
    }

    private void signOut() {

        Intent intent =
                new Intent(
                        ProfileActivity.this,
                        MainActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }

    // =========================================================
    // BOTTOM NAVIGATION
    // =========================================================

    private void setupBottomNavigation() {

        // Home
        View navHome =
                findViewById(R.id.nav_home);

        navHome.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            ProfileActivity.this,
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

            Intent intent =
                    new Intent(
                            ProfileActivity.this,
                            LabGroupsActivity.class
                    );

            startActivity(intent);
        });

        // Resources
        View navResources =
                findViewById(R.id.nav_resources);

        navResources.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            ProfileActivity.this,
                            ResourcesActivity.class
                    );

            startActivity(intent);
        });

        // Profile
        View navProfile =
                findViewById(R.id.nav_profile);

        navProfile.setOnClickListener(v -> {
            // Already on Profile.
        });
    }
}