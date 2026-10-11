package com.example.ict361studentregistration;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    private LecturerGroupViewModel viewModel;
    private LinearLayout coursesContainer;
    private LinearLayout noticeBoardContainer;
    private TextView lecturerTitleNameHeader;
    private ImageButton backButton;
    private Button postAnnouncementButton;
    private Button addStudentRecordButton;

    // Lecturer Profile Registered Details
    private String lecturerTitle = "Dr.";
    private String lecturerFirstName = "Jane";
    private String lecturerLastName = "Lecturer";
    private String lecturerStaffId = "2026001";
    private String lecturerEmail = "lecturer@campus.edu";
    private String lecturerContact = "+260 970 000 000";
    private String lecturerDepartment = "ICT Department";

    // Bottom nav tabs
    private LinearLayout navDashboard;
    private LinearLayout navGroups;
    private LinearLayout navTimetable;
    private LinearLayout navMore;

    private LinearLayout sectionDashboard;
    private LinearLayout sectionGroups;
    private LinearLayout sectionTimetable;
    private LinearLayout sectionMore;

    // Quick access views
    private LinearLayout quickActionNotification;
    private LinearLayout quickActionClasses;
    private LinearLayout quickActionMore;
    private LinearLayout quickActionGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewModel = new ViewModelProvider(this).get(LecturerGroupViewModel.class);

        coursesContainer = findViewById(R.id.courses_container);
        noticeBoardContainer = findViewById(R.id.notice_board_container);
        addNoticeCardToBoard("ICT361 Lab Group Allocation Update", "All students must finalize their lab group selections by Friday 17:00. Unassigned students will be auto-allocated.");
        lecturerTitleNameHeader = findViewById(R.id.lecturer_title_name_header);
        backButton = findViewById(R.id.lecturer_back_button);
        postAnnouncementButton = findViewById(R.id.post_announcement_button);
        addStudentRecordButton = findViewById(R.id.add_student_record_button);

        quickActionNotification = findViewById(R.id.quick_action_notification);
        quickActionClasses = findViewById(R.id.quick_action_classes);
        quickActionMore = findViewById(R.id.quick_action_more);
        quickActionGroups = findViewById(R.id.quick_action_groups);

        navDashboard = findViewById(R.id.nav_dashboard);
        navGroups = findViewById(R.id.nav_groups);
        navTimetable = findViewById(R.id.nav_timetable);
        navMore = findViewById(R.id.nav_more);

        sectionDashboard = findViewById(R.id.section_dashboard);
        sectionGroups = findViewById(R.id.section_groups);
        sectionTimetable = findViewById(R.id.section_timetable);
        sectionMore = findViewById(R.id.section_more);

        // Get Lecturer info passed from login or registration
        String userName = getIntent().getStringExtra("USER_NAME");
        String titleExtra = getIntent().getStringExtra("TITLE");
        String firstNameExtra = getIntent().getStringExtra("FIRST_NAME");
        String lastNameExtra = getIntent().getStringExtra("LAST_NAME");
        String staffIdExtra = getIntent().getStringExtra("STAFF_ID");
        String emailExtra = getIntent().getStringExtra("EMAIL");
        String contactExtra = getIntent().getStringExtra("CONTACT");
        String deptExtra = getIntent().getStringExtra("DEPARTMENT");

        if (titleExtra != null && !titleExtra.trim().isEmpty()) lecturerTitle = titleExtra;
        if (firstNameExtra != null && !firstNameExtra.trim().isEmpty()) lecturerFirstName = firstNameExtra;
        if (lastNameExtra != null && !lastNameExtra.trim().isEmpty()) lecturerLastName = lastNameExtra;
        else if (userName != null && !userName.trim().isEmpty()) {
            String[] parts = userName.trim().split("\\s+");
            lecturerLastName = parts[parts.length - 1];
            if (parts.length > 1 && (firstNameExtra == null || firstNameExtra.trim().isEmpty())) {
                lecturerFirstName = parts[0];
            }
        }
        if (staffIdExtra != null && !staffIdExtra.trim().isEmpty()) lecturerStaffId = staffIdExtra;
        if (emailExtra != null && !emailExtra.trim().isEmpty()) lecturerEmail = emailExtra;
        if (contactExtra != null && !contactExtra.trim().isEmpty()) lecturerContact = contactExtra;
        if (deptExtra != null && !deptExtra.trim().isEmpty()) lecturerDepartment = deptExtra;

        // TOP LEFT BACK BUTTON
        backButton.setOnClickListener(v -> finish());

        // BIND "MORE" SECTION (PROFILE, COURSE APPROVAL, NOTIFICATIONS, SETTINGS)
        View courseApprovalOption = findViewById(R.id.courseApprovalOption);
        View notificationsOption = findViewById(R.id.notificationsOption);
        View settingsOption = findViewById(R.id.settingsOption);

        updateProfileViews();

        if (courseApprovalOption != null) {
            courseApprovalOption.setOnClickListener(v -> {
                showCourseApprovalDialog();
            });
        }

        if (notificationsOption != null) {
            notificationsOption.setOnClickListener(v -> {
                Toast.makeText(this, "Notifications are currently Enabled", Toast.LENGTH_SHORT).show();
            });
        }

        if (settingsOption != null) {
            settingsOption.setOnClickListener(v -> {
                showSettingsDialog();
            });
        }

        TextView tvLecturerName = findViewById(R.id.tvLecturerName);
        if (tvLecturerName != null && tvLecturerName.getParent() instanceof View) {
            ((View) tvLecturerName.getParent()).setOnClickListener(v -> {
                showLecturerProfileDialog();
            });
        }

        // Post Notice
        postAnnouncementButton.setOnClickListener(v -> showPostAnnouncementDialog());

        // Add Student Record
        addStudentRecordButton.setOnClickListener(v -> showAddStudentDialog());

        // Quick Action clicks down the notice board
        quickActionNotification.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications: All notices are up to date", Toast.LENGTH_SHORT).show();
        });

        quickActionClasses.setOnClickListener(v -> {
            showCourseRegistrationSelectionDialog();
        });

        quickActionMore.setOnClickListener(v -> {
            showMoreSection();
            highlightTab(navMore);
        });

        quickActionGroups.setOnClickListener(v -> {
            showGroupsSection();
            highlightTab(navGroups);
        });

        // Bottom Navigation Tab clicks
        navDashboard.setOnClickListener(v -> {
            showDashboardSection();
            highlightTab(navDashboard);
        });

        navGroups.setOnClickListener(v -> {
            showGroupsSection();
            highlightTab(navGroups);
        });

        navTimetable.setOnClickListener(v -> {
            showTimetableSection();
            highlightTab(navTimetable);
        });

        navMore.setOnClickListener(v -> {
            showMoreSection();
            highlightTab(navMore);
        });

        // Observe ViewModel
        viewModel.getCourseGroupsLiveData().observe(this, this::renderCourses);
    }

    private void showDashboardSection() {
        sectionDashboard.setVisibility(View.VISIBLE);
        sectionGroups.setVisibility(View.GONE);
        sectionTimetable.setVisibility(View.GONE);
        sectionMore.setVisibility(View.GONE);
    }

    private void showGroupsSection() {
        sectionDashboard.setVisibility(View.GONE);
        sectionGroups.setVisibility(View.VISIBLE);
        sectionTimetable.setVisibility(View.GONE);
        sectionMore.setVisibility(View.GONE);
    }

    private void showTimetableSection() {
        sectionDashboard.setVisibility(View.GONE);
        sectionGroups.setVisibility(View.GONE);
        sectionTimetable.setVisibility(View.VISIBLE);
        sectionMore.setVisibility(View.GONE);
    }

    private void showMoreSection() {
        sectionDashboard.setVisibility(View.GONE);
        sectionGroups.setVisibility(View.GONE);
        sectionTimetable.setVisibility(View.GONE);
        sectionMore.setVisibility(View.VISIBLE);
    }

    private void highlightTab(LinearLayout selectedTab) {
        ((TextView) navDashboard.getChildAt(1)).setTextColor(Color.parseColor("#607D8B"));
        ((TextView) navGroups.getChildAt(1)).setTextColor(Color.parseColor("#607D8B"));
        ((TextView) navTimetable.getChildAt(1)).setTextColor(Color.parseColor("#607D8B"));
        ((TextView) navMore.getChildAt(1)).setTextColor(Color.parseColor("#607D8B"));

        TextView tv = (TextView) selectedTab.getChildAt(1);
        tv.setTextColor(Color.parseColor("#D32F2F"));
    }

    private void showCourseRegistrationSelectionDialog() {
        showGroupsSection();
        CharSequence[] years = {
                "🎓 FIRST YEAR",
                "🎓 SECOND YEAR",
                "🎓 THIRD YEAR"
        };

        new AlertDialog.Builder(this)
                .setTitle("Course Registration — Select Year")
                .setItems(years, (dialog, yearChoice) -> {
                    String selectedYearName = yearChoice == 0 ? "FIRST YEAR" : (yearChoice == 1 ? "SECOND YEAR" : "THIRD YEAR");
                    showCourseRegistrationSemesterDialog(selectedYearName);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showCourseRegistrationSemesterDialog(String yearName) {
        CharSequence[] semesters = {
                "📅 " + yearName + " — Semester 1",
                "📅 " + yearName + " — Semester 2"
        };

        new AlertDialog.Builder(this)
                .setTitle(yearName + " — Select Semester")
                .setItems(semesters, (dialog, semChoice) -> {
                    int semesterNum = semChoice + 1;
                    showCourseRegistrationCoursesDialog(yearName, semesterNum);
                })
                .setNegativeButton("Back", (dialog, which) -> showCourseRegistrationSelectionDialog())
                .show();
    }

    private void showCourseRegistrationCoursesDialog(String yearName, int semesterNum) {
        String[] allCourses;
        String[] courseCodes;

        if (yearName.equals("FIRST YEAR")) {
            if (semesterNum == 1) {
                allCourses = new String[]{
                        "ICT111 - Intro to Programming",
                        "ICT112 - Computer Architecture"
                };
                courseCodes = new String[]{"ICT111", "ICT112"};
            } else {
                allCourses = new String[]{
                        "ICT121 - Data Structures",
                        "ICT122 - Web Fundamentals"
                };
                courseCodes = new String[]{"ICT121", "ICT122"};
            }
        } else if (yearName.equals("SECOND YEAR")) {
            if (semesterNum == 1) {
                allCourses = new String[]{
                        "ICT211 - Object-Oriented Programming",
                        "ICT212 - Algorithms"
                };
                courseCodes = new String[]{"ICT211", "ICT212"};
            } else {
                allCourses = new String[]{
                        "ICT221 - Operating Systems",
                        "ICT222 - Software Engineering"
                };
                courseCodes = new String[]{"ICT221", "ICT222"};
            }
        } else { // THIRD YEAR
            if (semesterNum == 1) {
                allCourses = new String[]{
                        "ICT361 - Student Registration & Mobile Dev",
                        "ICT341 - Cybersecurity Principles"
                };
                courseCodes = new String[]{"ICT361", "ICT341"};
            } else {
                allCourses = new String[]{
                        "ICT381 - System Modelling and Design",
                        "ICT371 - Advanced Database",
                        "ICT351 - Theory of Computation"
                };
                courseCodes = new String[]{"ICT381", "ICT371", "ICT351"};
            }
        }

        boolean[] checkedItems = new boolean[courseCodes.length];
        Set<String> currentSelected = viewModel.getSelectedCourses();
        for (int i = 0; i < courseCodes.length; i++) {
            checkedItems[i] = currentSelected.contains(courseCodes[i]);
        }

        new AlertDialog.Builder(this)
                .setTitle(yearName + " • Semester " + semesterNum + " — Select Courses")
                .setMultiChoiceItems(allCourses, checkedItems, (dialog, which, isChecked) -> checkedItems[which] = isChecked)
                .setPositiveButton("SAVE COURSES", (dialog, which) -> {
                    Set<String> newSelected = new HashSet<>(currentSelected);
                    for (int i = 0; i < courseCodes.length; i++) {
                        if (checkedItems[i]) {
                            newSelected.add(courseCodes[i]);
                        } else {
                            newSelected.remove(courseCodes[i]);
                        }
                    }
                    viewModel.setSelectedCourses(newSelected);
                    Toast.makeText(this, "Course registration updated for " + yearName + " Sem " + semesterNum + "! Total active: " + newSelected.size(), Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Back", (dialog, which) -> showCourseRegistrationSemesterDialog(yearName))
                .show();
    }

    private void showCourseApprovalDialog() {
        CharSequence[] years = {
                "🎓 FIRST YEAR",
                "🎓 SECOND YEAR",
                "🎓 THIRD YEAR"
        };

        new AlertDialog.Builder(this)
                .setTitle("Course Approval — Select Year")
                .setItems(years, (dialog, yearChoice) -> {
                    String selectedYearName = yearChoice == 0 ? "FIRST YEAR" : (yearChoice == 1 ? "SECOND YEAR" : "THIRD YEAR");
                    showSemesterSelectionDialog(selectedYearName);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showSemesterSelectionDialog(String yearName) {
        CharSequence[] semesters = {
                "📅 " + yearName + " — Semester 1",
                "📅 " + yearName + " — Semester 2"
        };

        new AlertDialog.Builder(this)
                .setTitle(yearName + " — Select Semester")
                .setItems(semesters, (dialog, semChoice) -> {
                    int semesterNum = semChoice + 1;
                    showPendingApprovalsDialog(yearName, semesterNum);
                })
                .setNegativeButton("Back", (dialog, which) -> showCourseApprovalDialog())
                .show();
    }

    private void showPendingApprovalsDialog(String yearName, int semesterNum) {
        String[] pendingStudentApprovals;
        if (yearName.equals("FIRST YEAR")) {
            if (semesterNum == 1) {
                pendingStudentApprovals = new String[]{
                        "Student ID: 202400201 • ICT111 (Intro to Programming) — Pending Approval",
                        "Student ID: 202400202 • ICT112 (Computer Architecture) — Pending Approval"
                };
            } else {
                pendingStudentApprovals = new String[]{
                        "Student ID: 202400203 • ICT121 (Data Structures) — Pending Approval",
                        "Student ID: 202400204 • ICT122 (Web Fundamentals) — Pending Approval"
                };
            }
        } else if (yearName.equals("SECOND YEAR")) {
            if (semesterNum == 1) {
                pendingStudentApprovals = new String[]{
                        "Student ID: 202400301 • ICT211 (Object-Oriented Programming) — Pending Approval",
                        "Student ID: 202400302 • ICT212 (Algorithms) — Pending Approval"
                };
            } else {
                pendingStudentApprovals = new String[]{
                        "Student ID: 202400303 • ICT221 (Operating Systems) — Pending Approval",
                        "Student ID: 202400304 • ICT222 (Software Engineering) — Pending Approval"
                };
            }
        } else { // THIRD YEAR
            if (semesterNum == 1) {
                pendingStudentApprovals = new String[]{
                        "Student ID: 202400101 • ICT361 (Mobile Dev) — Pending Approval",
                        "Student ID: 202400102 • ICT341 (Cybersecurity) — Pending Approval"
                };
            } else {
                pendingStudentApprovals = new String[]{
                        "Student ID: 202400103 • ICT381 (System Design) — Pending Approval",
                        "Student ID: 202400104 • ICT371 (Database) — Pending Approval"
                };
            }
        }

        boolean[] checked = new boolean[pendingStudentApprovals.length];

        new AlertDialog.Builder(this)
                .setTitle(yearName + " • Semester " + semesterNum + " — Pending Approvals")
                .setMultiChoiceItems(pendingStudentApprovals, checked, (dialog, which, isChecked) -> checked[which] = isChecked)
                .setPositiveButton("APPROVE SELECTED", (dialog, which) -> {
                    int count = 0;
                    for (boolean b : checked) {
                        if (b) count++;
                    }
                    if (count > 0) {
                        Toast.makeText(this, "Approved " + count + " student registration(s) for " + yearName + " Sem " + semesterNum + "!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "No registrations selected for approval.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Back", (dialog, which) -> showSemesterSelectionDialog(yearName))
                .show();
    }

    private void showSettingsDialog() {
        CharSequence[] options = {
                "✏️ Edit Profile",
                "🔑 Change Password",
                "🛡️ Permissions",
                "ℹ️ About Us",
                "❓ Help & Support",
                "🚪 Logout"
        };

        TextView titleView = new TextView(this);
        titleView.setText("Lecturer");
        titleView.setTextColor(Color.WHITE);
        titleView.setTextSize(18);
        titleView.setTypeface(null, Typeface.BOLD);
        titleView.setGravity(Gravity.CENTER);
        titleView.setBackgroundColor(Color.parseColor("#1976D2"));
        titleView.setPadding(dp(16), dp(16), dp(16), dp(16));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCustomTitle(titleView)
                .setItems(options, (d, which) -> {
                    if (which == 0) {
                        showEditProfileDialog();
                    } else if (which == 1) {
                        showChangePasswordDialog();
                    } else if (which == 2) {
                        CharSequence[] permOptions = {
                                "📷 Camera Permission",
                                "📂 Storage Permission",
                                "📍 Location Permission",
                                "🔔 Notifications Permission"
                        };
                        new AlertDialog.Builder(this)
                                .setTitle("Manage Permissions")
                                .setItems(permOptions, (pDialog, pWhich) -> {
                                    if (pWhich == 0) {
                                        requestCameraPermission(null);
                                    } else if (pWhich == 1) {
                                        requestStoragePermission(null);
                                    } else {
                                        Toast.makeText(this, "Permission already configured.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setPositiveButton("Back", (pDialog, pWhich) -> showSettingsDialog())
                                .show();
                    } else if (which == 3) {
                        new AlertDialog.Builder(this)
                                .setTitle("About Us — Campus Companion")
                                .setMessage("Campus Companion v1.2\n\nDesigned to empower academic institutions with seamless student registration, course management, group assignments, and administrative tools.\n\nSame Campus. Brighter Tomorrow.")
                                .setPositiveButton("OK", null)
                                .show();
                    } else if (which == 4) {
                        new AlertDialog.Builder(this)
                                .setTitle("Help & Support")
                                .setMessage("Need assistance with Campus Companion?\n\n• Course Approval: Select Year & Semester to approve student registrations.\n• My Classes: Register and manage teaching courses across years.\n• Notice Board: Post and view announcements.\n\nContact IT Support at support@campuscompanion.edu")
                                .setPositiveButton("GOT IT", null)
                                .show();
                    } else if (which == 5) {
                        new AlertDialog.Builder(this)
                                .setTitle("Log Out")
                                .setMessage("Are you sure you want to log out of Campus Companion?")
                                .setPositiveButton("LOG OUT", (logoutDialog, logoutWhich) -> {
                                    Intent intent = new Intent(HomeActivity.this, LecturerActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    }
                })
                .setPositiveButton("Close", null)
                .create();

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int dialogWidth = screenWidth - dp(24);
            window.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        }
    }

    private void requestCameraPermission(Runnable onGranted) {
        new AlertDialog.Builder(this)
                .setTitle("Permission Request")
                .setMessage("Campus Companion would like to access your camera to scan course QR codes and upload profile pictures. Would you like to allow this permission?")
                .setPositiveButton("ALLOW", (dialog, which) -> {
                    Toast.makeText(this, "Camera permission allowed", Toast.LENGTH_SHORT).show();
                    if (onGranted != null) {
                        onGranted.run();
                    }
                })
                .setNegativeButton("DENY", (dialog, which) -> {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void requestStoragePermission(Runnable onGranted) {
        new AlertDialog.Builder(this)
                .setTitle("Permission Request")
                .setMessage("Campus Companion would like to access your device storage to save assignment materials and documents. Would you like to allow this permission?")
                .setPositiveButton("ALLOW", (dialog, which) -> {
                    Toast.makeText(this, "Storage permission allowed", Toast.LENGTH_SHORT).show();
                    if (onGranted != null) {
                        onGranted.run();
                    }
                })
                .setNegativeButton("DENY", (dialog, which) -> {
                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void updateProfileViews() {
        String displayName = lecturerTitle + " " + (lecturerFirstName.isEmpty() ? "" : lecturerFirstName + " ") + lecturerLastName;
        displayName = displayName.trim().replaceAll("\\s+", " ");

        if (lecturerTitleNameHeader != null) {
            lecturerTitleNameHeader.setText(displayName);
        }

        TextView welcomeMessageText = findViewById(R.id.welcome_message_text);
        if (welcomeMessageText != null) {
            welcomeMessageText.setText("Welcome to Campus Companion, " + displayName + ". Use the menu or quick access icons below to manage your classes, notice board, and profile.");
        }

        TextView tvLecturerName = findViewById(R.id.tvLecturerName);
        if (tvLecturerName != null) {
            tvLecturerName.setText(displayName);
        }

        TextView tvLecturerNumber = findViewById(R.id.tvLecturerNumber);
        if (tvLecturerNumber != null) {
            tvLecturerNumber.setText(lecturerStaffId + " - " + lecturerDepartment.toUpperCase());
        }
    }

    private void showEditProfileDialog() {
        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(16), dp(20), dp(16));

        // Title
        TextView titleLabel = new TextView(this);
        titleLabel.setText("Title (e.g. Dr., Mr., Prof., Ms.)");
        titleLabel.setTextSize(12);
        titleLabel.setTextColor(Color.GRAY);
        EditText titleInput = new EditText(this);
        titleInput.setHint("Title");
        titleInput.setText(lecturerTitle);

        // First Name
        TextView firstNameLabel = new TextView(this);
        firstNameLabel.setText("First Name");
        firstNameLabel.setTextSize(12);
        firstNameLabel.setTextColor(Color.GRAY);
        firstNameLabel.setPadding(0, dp(8), 0, 0);
        EditText firstNameInput = new EditText(this);
        firstNameInput.setHint("First Name");
        firstNameInput.setText(lecturerFirstName);

        // Last Name
        TextView lastNameLabel = new TextView(this);
        lastNameLabel.setText("Last Name / Surname");
        lastNameLabel.setTextSize(12);
        lastNameLabel.setTextColor(Color.GRAY);
        lastNameLabel.setPadding(0, dp(8), 0, 0);
        EditText lastNameInput = new EditText(this);
        lastNameInput.setHint("Last Name");
        lastNameInput.setText(lecturerLastName);

        // Staff ID
        TextView staffIdLabel = new TextView(this);
        staffIdLabel.setText("Staff / Lecturer ID");
        staffIdLabel.setTextSize(12);
        staffIdLabel.setTextColor(Color.GRAY);
        staffIdLabel.setPadding(0, dp(8), 0, 0);
        EditText staffIdInput = new EditText(this);
        staffIdInput.setHint("Staff ID");
        staffIdInput.setText(lecturerStaffId);

        // Email Address
        TextView emailLabel = new TextView(this);
        emailLabel.setText("Email Address");
        emailLabel.setTextSize(12);
        emailLabel.setTextColor(Color.GRAY);
        emailLabel.setPadding(0, dp(8), 0, 0);
        EditText emailInput = new EditText(this);
        emailInput.setHint("Email Address");
        emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailInput.setText(lecturerEmail);

        // Contact Number
        TextView contactLabel = new TextView(this);
        contactLabel.setText("Contact Number");
        contactLabel.setTextSize(12);
        contactLabel.setTextColor(Color.GRAY);
        contactLabel.setPadding(0, dp(8), 0, 0);
        EditText contactInput = new EditText(this);
        contactInput.setHint("Contact Number");
        contactInput.setInputType(InputType.TYPE_CLASS_PHONE);
        contactInput.setText(lecturerContact);

        // Department
        TextView deptLabel = new TextView(this);
        deptLabel.setText("Department");
        deptLabel.setTextSize(12);
        deptLabel.setTextColor(Color.GRAY);
        deptLabel.setPadding(0, dp(8), 0, 0);
        EditText deptInput = new EditText(this);
        deptInput.setHint("Department");
        deptInput.setText(lecturerDepartment);

        layout.addView(titleLabel);
        layout.addView(titleInput);
        layout.addView(firstNameLabel);
        layout.addView(firstNameInput);
        layout.addView(lastNameLabel);
        layout.addView(lastNameInput);
        layout.addView(staffIdLabel);
        layout.addView(staffIdInput);
        layout.addView(emailLabel);
        layout.addView(emailInput);
        layout.addView(contactLabel);
        layout.addView(contactInput);
        layout.addView(deptLabel);
        layout.addView(deptInput);

        scrollView.addView(layout);

        new AlertDialog.Builder(this)
                .setTitle("Edit Registered Profile Details")
                .setView(scrollView)
                .setPositiveButton("SAVE CHANGES", (dialog, which) -> {
                    String newTitle = titleInput.getText().toString().trim();
                    String newFirstName = firstNameInput.getText().toString().trim();
                    String newLastName = lastNameInput.getText().toString().trim();
                    String newStaffId = staffIdInput.getText().toString().trim();
                    String newEmail = emailInput.getText().toString().trim();
                    String newContact = contactInput.getText().toString().trim();
                    String newDept = deptInput.getText().toString().trim();

                    if (newLastName.isEmpty() && newFirstName.isEmpty()) {
                        Toast.makeText(this, "First or Last name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!newTitle.isEmpty()) lecturerTitle = newTitle;
                    lecturerFirstName = newFirstName;
                    if (!newLastName.isEmpty()) lecturerLastName = newLastName;
                    if (!newStaffId.isEmpty()) lecturerStaffId = newStaffId;
                    if (!newEmail.isEmpty()) lecturerEmail = newEmail;
                    if (!newContact.isEmpty()) lecturerContact = newContact;
                    if (!newDept.isEmpty()) lecturerDepartment = newDept;

                    updateProfileViews();
                    Toast.makeText(this, "Profile details updated successfully!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showChangePasswordDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(16), dp(20), dp(8));

        EditText currentPwdInput = new EditText(this);
        currentPwdInput.setHint("Current Password");
        currentPwdInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        EditText newPwdInput = new EditText(this);
        newPwdInput.setHint("New Password (min 8 chars)");
        newPwdInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        EditText confirmPwdInput = new EditText(this);
        confirmPwdInput.setHint("Confirm New Password");
        confirmPwdInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        layout.addView(currentPwdInput);
        layout.addView(newPwdInput);
        layout.addView(confirmPwdInput);

        new AlertDialog.Builder(this)
                .setTitle("Change Password")
                .setView(layout)
                .setPositiveButton("UPDATE PASSWORD", (dialog, which) -> {
                    String newPwd = newPwdInput.getText().toString();
                    String confirmPwd = confirmPwdInput.getText().toString();

                    if (newPwd.length() < 8) {
                        Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!newPwd.equals(confirmPwd)) {
                        Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void renderCourses(Map<String, LecturerGroupViewModel.CourseGroupData> groupsMap) {
        coursesContainer.removeAllViews();

        if (groupsMap == null || groupsMap.isEmpty()) {
            return;
        }

        Set<String> selectedCourses = viewModel.getSelectedCourses();

        for (Map.Entry<String, LecturerGroupViewModel.CourseGroupData> entry : groupsMap.entrySet()) {
            if (selectedCourses.contains(entry.getKey())) {
                addCourseCard(entry.getValue());
            }
        }
    }

    private void addCourseCard(LecturerGroupViewModel.CourseGroupData data) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(dp(12));
        bg.setStroke(dp(1), Color.parseColor("#E0E0E0"));
        card.setBackground(bg);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dp(14));
        card.setLayoutParams(cardParams);

        // Top Row
        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView courseCode = new TextView(this);
        courseCode.setText(data.getCourseCode() + " • " + data.getAdminName());
        courseCode.setTextColor(Color.parseColor("#D32F2F"));
        courseCode.setTextSize(14);
        courseCode.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams codeParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        courseCode.setLayoutParams(codeParams);

        TextView capacityText = new TextView(this);
        capacityText.setText(data.getStudentCount() + " / " + data.getMaxCapacity() + " Enrolled");
        capacityText.setTextColor(Color.parseColor("#455A64"));
        capacityText.setTextSize(12);
        capacityText.setTypeface(null, Typeface.BOLD);

        topRow.addView(courseCode);
        topRow.addView(capacityText);

        // Title
        TextView courseTitle = new TextView(this);
        courseTitle.setText(data.getCourseTitle());
        courseTitle.setTextColor(Color.parseColor("#263238"));
        courseTitle.setTextSize(16);
        courseTitle.setTypeface(null, Typeface.BOLD);
        courseTitle.setPadding(0, dp(4), 0, dp(8));

        card.addView(topRow);
        card.addView(courseTitle);

        // Students
        List<LecturerGroupViewModel.StudentItem> students = data.getStudents();
        if (students != null && !students.isEmpty()) {
            for (LecturerGroupViewModel.StudentItem student : students) {
                LinearLayout studentRow = new LinearLayout(this);
                studentRow.setOrientation(LinearLayout.HORIZONTAL);
                studentRow.setPadding(dp(8), dp(6), dp(8), dp(6));
                studentRow.setGravity(Gravity.CENTER_VERTICAL);

                GradientDrawable rowBg = new GradientDrawable();
                rowBg.setColor(Color.parseColor("#FAFAFA"));
                rowBg.setCornerRadius(dp(6));
                studentRow.setBackground(rowBg);

                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                rowParams.setMargins(0, dp(4), 0, 0);
                studentRow.setLayoutParams(rowParams);

                TextView studentInfo = new TextView(this);
                studentInfo.setText(student.getName() + " (" + student.getStudentId() + ") • " + student.getGroupName());
                studentInfo.setTextColor(Color.parseColor("#37474F"));
                studentInfo.setTextSize(12);

                LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                studentInfo.setLayoutParams(infoParams);

                TextView removeButton = new TextView(this);
                removeButton.setText("Remove");
                removeButton.setTextColor(Color.parseColor("#D32F2F"));
                removeButton.setTextSize(12);
                removeButton.setTypeface(null, Typeface.BOLD);
                removeButton.setPadding(dp(8), dp(2), dp(8), dp(2));
                removeButton.setClickable(true);
                removeButton.setFocusable(true);

                removeButton.setOnClickListener(v -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Remove Student")
                            .setMessage("Are you sure you want to delete " + student.getName() + " (" + student.getStudentId() + ") from this group?")
                            .setPositiveButton("YES, DELETE", (dialog, which) -> {
                                boolean success = viewModel.removeStudent(data.getCourseCode(), student.getStudentId());
                                if (success) {
                                    Toast.makeText(this, "Removed " + student.getName(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                });

                studentRow.addView(studentInfo);
                studentRow.addView(removeButton);

                card.addView(studentRow);
            }
        } else {
            TextView emptyText = new TextView(this);
            emptyText.setText("No students enrolled");
            emptyText.setTextColor(Color.GRAY);
            emptyText.setTextSize(12);
            emptyText.setPadding(0, dp(6), 0, 0);
            card.addView(emptyText);
        }

        coursesContainer.addView(card);
    }

    private void showPostAnnouncementDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter announcement text for students...");
        input.setPadding(dp(16), dp(16), dp(16), dp(16));

        new AlertDialog.Builder(this)
                .setTitle("Post Lecturer Announcement")
                .setView(input)
                .setPositiveButton("POST", (dialog, which) -> {
                    String notice = input.getText().toString().trim();
                    if (!notice.isEmpty()) {
                        addNoticeCardToBoard("Lecturer Announcement", notice);
                        Toast.makeText(this, "Announcement posted & added to your notice board!", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addNoticeCardToBoard(String title, String content) {
        if (noticeBoardContainer == null) return;

        // Clear previous notices so only the most recent notice is displayed
        noticeBoardContainer.removeAllViews();

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.notice_card_bg);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.bottomMargin = dp(10);
        card.setLayoutParams(params);

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextColor(Color.parseColor("#D32F2F"));
        titleView.setTextSize(14);
        titleView.setTypeface(null, Typeface.BOLD);

        TextView contentView = new TextView(this);
        contentView.setText(content);
        contentView.setTextColor(Color.parseColor("#37474F"));
        contentView.setTextSize(13);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        contentParams.topMargin = dp(4);
        contentView.setLayoutParams(contentParams);

        TextView dateView = new TextView(this);
        dateView.setText("Posted by Lecturer • Just now");
        dateView.setTextColor(Color.parseColor("#78909C"));
        dateView.setTextSize(11);
        dateView.setTypeface(null, Typeface.ITALIC);
        LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        dateParams.topMargin = dp(6);
        dateView.setLayoutParams(dateParams);

        card.addView(titleView);
        card.addView(contentView);
        card.addView(dateView);

        // Add at the top of notice board container so newest notices appear first
        noticeBoardContainer.addView(card, 0);
    }

    private void showAddStudentDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(16), dp(20), dp(8));

        EditText courseCodeInput = new EditText(this);
        courseCodeInput.setHint("Course Code (e.g. ICT361)");

        EditText studentIdInput = new EditText(this);
        studentIdInput.setHint("9-Digit Student Number");

        EditText studentNameInput = new EditText(this);
        studentNameInput.setHint("Student Full Name");

        EditText groupInput = new EditText(this);
        groupInput.setHint("Group (e.g. Group A)");

        layout.addView(courseCodeInput);
        layout.addView(studentIdInput);
        layout.addView(studentNameInput);
        layout.addView(groupInput);

        new AlertDialog.Builder(this)
                .setTitle("Lecturer Course Registration & Group Assignment")
                .setView(layout)
                .setPositiveButton("ASSIGN & REGISTER", (dialog, which) -> {
                    String courseCode = courseCodeInput.getText().toString().trim().toUpperCase();
                    String id = studentIdInput.getText().toString().trim();
                    String name = studentNameInput.getText().toString().trim();
                    String grp = groupInput.getText().toString().trim();

                    if (courseCode.isEmpty() || id.isEmpty() || name.isEmpty()) {
                        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (grp.isEmpty()) grp = "Group A";

                    boolean added = viewModel.addStudent(courseCode, id, name, grp);
                    if (added) {
                        Toast.makeText(this, "Student registered & assigned to " + courseCode, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed. Check course code or group capacity (max 15).", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showLecturerProfileDialog() {
        int activeCoursesCount = viewModel.getActiveCoursesCount();
        Set<String> selectedCourses = viewModel.getSelectedCourses();
        StringBuilder assignedCoursesText = new StringBuilder();
        for (String code : selectedCourses) {
            LecturerGroupViewModel.CourseGroupData groupData = viewModel.getCourseGroup(code);
            if (groupData != null) {
                assignedCoursesText.append("\n• ").append(groupData.getCourseCode()).append(" - ").append(groupData.getCourseTitle());
            }
        }

        String fullDisplayName = lecturerTitle + " " + (lecturerFirstName.isEmpty() ? "" : lecturerFirstName + " ") + lecturerLastName;

        new AlertDialog.Builder(this)
                .setTitle("Lecturer Profile Details")
                .setMessage(
                        "Full Name: " + fullDisplayName.trim() + "\n\n" +
                                "Staff / Lecturer ID: " + lecturerStaffId + "\n" +
                                "Department: " + lecturerDepartment + "\n" +
                                "Email Address: " + lecturerEmail + "\n" +
                                "Contact Number: " + lecturerContact + "\n\n" +
                                "Role: Authorized Course Administrator\n\n" +
                                "Active Courses Count: " + activeCoursesCount + "\n" +
                                (assignedCoursesText.length() > 0 ? assignedCoursesText.toString() : "\nNone selected")
                )
                .setPositiveButton("OK", null)
                .show();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
