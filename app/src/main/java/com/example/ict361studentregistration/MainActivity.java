package com.example.ict361studentregistration;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button studentButton;
    private Button lecturerButton;
    private ImageButton helpIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the Welcome screen
        setContentView(R.layout.activity_main);

        // Connect Java to the buttons in activity_main.xml
        studentButton = findViewById(R.id.studentButton);
        lecturerButton = findViewById(R.id.lecturerButton);
        helpIcon = findViewById(R.id.helpIcon);


        // STUDENT BUTTON
        studentButton.setOnClickListener(v -> {

            // Open the Student Portal
            Intent intent = new Intent(
                    MainActivity.this,
                    StudentActivity.class
            );

            startActivity(intent);

        });


        // LECTURER BUTTON
        lecturerButton.setOnClickListener(v -> {

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Lecturer Portal")
                    .setMessage(
                            "The Lecturer Portal is used by authorised " +
                                    "lecturers to manage student registration records.\n\n" +

                                    "Lecturers will be able to:\n\n" +

                                    "• Add student records\n" +
                                    "• View student records\n" +
                                    "• Edit student information\n" +
                                    "• Delete student records\n" +
                                    "• Search and filter students\n" +
                                    "• Assign and transfer laboratory groups\n" +
                                    "• Correct student numbers\n" +
                                    "• View laboratory group totals"
                    )
                    .setPositiveButton("GOT IT", null)
                    .show();

        });


        // WELCOME SCREEN HELP
        helpIcon.setOnClickListener(v -> {

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Campus Companion Guide")
                    .setMessage(
                            "Welcome to Campus Companion!\n\n" +

                                    "Campus Companion helps students and lecturers " +
                                    "manage student registration and laboratory groups.\n\n" +

                                    "STUDENTS\n" +
                                    "Select STUDENT to open the Student Portal. " +
                                    "You can sign in, create an account or recover " +
                                    "your password.\n\n" +

                                    "LECTURERS\n" +
                                    "Select LECTURER to access lecturer features " +
                                    "for managing student records and laboratory groups.\n\n" +

                                    "HELP\n" +
                                    "Use the ? button whenever you need guidance " +
                                    "about the screen you are currently using.\n\n" +

                                    "Campus Companion\n" +
                                    "Same Campus. Brighter Tomorrow."
                    )
                    .setPositiveButton("GOT IT", null)
                    .show();

        });
    }
}