
package com.example.ict361studentregistration;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ict361studentregistration.data.room.AppDatabase;
import com.example.ict361studentregistration.data.room.Timetable;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddTimetableActivity extends AppCompatActivity {

    private EditText courseCodeInput, courseNameInput;
    private EditText startTimeInput, endTimeInput, venueInput;
    private EditText programmeInput;

    private Spinner daySpinner, classTypeSpinner;
    private Button saveButton;

    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_timetable);

        courseCodeInput = findViewById(R.id.course_code_input);
        courseNameInput = findViewById(R.id.course_name_input);
        startTimeInput = findViewById(R.id.start_time_input);
        endTimeInput = findViewById(R.id.end_time_input);
        venueInput = findViewById(R.id.venue_input);
        programmeInput = findViewById(R.id.programme_input);

        daySpinner = findViewById(R.id.day_spinner);
        classTypeSpinner = findViewById(R.id.class_type_spinner);
        saveButton = findViewById(R.id.save_timetable_button);

        findViewById(R.id.add_timetable_back_button)
                .setOnClickListener(v -> finish());

        setSpinner(daySpinner, new String[]{
                "Monday", "Tuesday", "Wednesday",
                "Thursday", "Friday"
        });

        setSpinner(classTypeSpinner, new String[]{
                "Lecture", "Tutorial", "Practical",
                "Lab", "Seminar"
        });

        startTimeInput.setFocusable(false);
        startTimeInput.setClickable(true);

        endTimeInput.setFocusable(false);
        endTimeInput.setClickable(true);

        startTimeInput.setOnClickListener(
                v -> chooseTime(startTimeInput)
        );

        endTimeInput.setOnClickListener(
                v -> chooseTime(endTimeInput)
        );

        saveButton.setOnClickListener(
                v -> saveTimetableEntry()
        );
    }

    private void setSpinner(Spinner spinner, String[] values) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                values
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
    }

    private void chooseTime(EditText target) {

        new TimePickerDialog(
                this,
                (view, hour, minute) -> target.setText(
                        String.format(
                                Locale.getDefault(),
                                "%02d:%02d",
                                hour,
                                minute
                        )
                ),
                8,
                0,
                true
        ).show();
    }

    private void saveTimetableEntry() {

        String code = courseCodeInput.getText()
                .toString().trim();

        String name = courseNameInput.getText()
                .toString().trim();

        String start = startTimeInput.getText()
                .toString().trim();

        String end = endTimeInput.getText()
                .toString().trim();

        String venue = venueInput.getText()
                .toString().trim();

        String programme = programmeInput.getText()
                .toString().trim();

        String day = daySpinner.getSelectedItem().toString();
        String type = classTypeSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(code)
                || TextUtils.isEmpty(name)
                || TextUtils.isEmpty(start)
                || TextUtils.isEmpty(end)
                || TextUtils.isEmpty(venue)
                || TextUtils.isEmpty(programme)) {

            Toast.makeText(
                    this,
                    "Please complete all fields.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (start.compareTo(end) >= 0) {

            Toast.makeText(
                    this,
                    "End time must be later than start time.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        saveButton.setEnabled(false);

        Timetable entry = new Timetable();

        entry.classId = java.util.UUID.randomUUID().toString();
        entry.courseCode = code;
        entry.courseName = name;
        entry.dayOfWeek = day;
        entry.startTime = start;
        entry.endTime = end;
        entry.venue = venue;
        entry.programme = programme;
        entry.classType = type;

        entry.accentColor = type.equals("Lecture")
                ? android.graphics.Color.rgb(22, 135, 217)
                : android.graphics.Color.rgb(229, 57, 53);

        entry.synced = false;

        databaseExecutor.execute(() -> {

            try {

                // Save the timetable entry to Room.
                AppDatabase.getDatabase(getApplicationContext())
                        .timetableDao()
                        .insertTimetable(entry);

                // Save a local notification for this timetable update.
                TimetableNotifications.addNotification(
                        getApplicationContext(),
                        entry.courseCode,
                        entry.courseName,
                        entry.dayOfWeek,
                        entry.startTime,
                        entry.endTime,
                        entry.venue
                );

                runOnUiThread(() -> {

                    Toast.makeText(
                            AddTimetableActivity.this,
                            "Timetable class saved!",
                            Toast.LENGTH_LONG
                    ).show();

                    finish();
                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    saveButton.setEnabled(true);

                    Toast.makeText(
                            AddTimetableActivity.this,
                            "Could not save class. Please try again.",
                            Toast.LENGTH_LONG
                    ).show();

                    android.util.Log.e(
                            "AddTimetable",
                            "Failed to save timetable entry",
                            e
                    );
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseExecutor.shutdown();
    }
}
