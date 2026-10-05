package com.example.ict361studentregistration;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText studentNumberInput;

    private Spinner programmeSpinner;

    private Button continueButton;

    private ImageButton backButton;
    private ImageButton helpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load Step 1
        setContentView(R.layout.activity_create_account);

        // Connect XML views
        firstNameInput =
                findViewById(R.id.first_name_input);

        lastNameInput =
                findViewById(R.id.last_name_input);

        studentNumberInput =
                findViewById(R.id.create_student_number_input);

        programmeSpinner =
                findViewById(R.id.programme_spinner);

        continueButton =
                findViewById(
                        R.id.create_account_continue_button
                );

        backButton =
                findViewById(
                        R.id.create_account_back_button
                );

        helpButton =
                findViewById(
                        R.id.create_account_help_button
                );

        // Programme list
        String[] programmes = {
                "Choose programme",
                "Computer Science",
                "Information Technology",
                "Data Science",
                "Cybersecurity"
        };

        /*
         * Custom Spinner adapter.
         *
         * "Choose programme" is only a guideline.
         * It is not selectable from the dropdown.
         */
        ArrayAdapter<String> programmeAdapter =
                new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        programmes
                ) {

                    @Override
                    public boolean isEnabled(
                            int position
                    ) {
                        // The hint cannot be selected.
                        return position != 0;
                    }

                    @Override
                    public View getView(
                            int position,
                            View convertView,
                            ViewGroup parent
                    ) {

                        TextView view =
                                (TextView) super.getView(
                                        position,
                                        convertView,
                                        parent
                                );

                        if (position == 0) {

                            view.setText(
                                    "Choose programme"
                            );

                            view.setTextColor(
                                    Color.GRAY
                            );

                        } else {

                            view.setTextColor(
                                    Color.DKGRAY
                            );
                        }

                        return view;
                    }

                    @Override
                    public View getDropDownView(
                            int position,
                            View convertView,
                            ViewGroup parent
                    ) {

                        TextView view =
                                (TextView) super.getDropDownView(
                                        position,
                                        convertView,
                                        parent
                                );

                        /*
                         * Hide the guideline from the
                         * actual programme list.
                         */
                        if (position == 0) {

                            view.setVisibility(
                                    View.GONE
                            );

                        } else {

                            view.setVisibility(
                                    View.VISIBLE
                            );

                            view.setTextColor(
                                    Color.DKGRAY
                            );
                        }

                        return view;
                    }
                };

        programmeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        programmeSpinner.setAdapter(
                programmeAdapter
        );

        // Start with the guideline displayed.
        programmeSpinner.setSelection(0);

        /*
         * The Spinner is a real dropdown.
         *
         * The student can open it as many times as needed
         * and change their programme before continuing.
         */
        programmeSpinner.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        // No extra action needed.
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent
                    ) {
                    }
                }
        );

        // CONTINUE
        continueButton.setOnClickListener(v -> {

            String firstName =
                    firstNameInput
                            .getText()
                            .toString()
                            .trim();

            String lastName =
                    lastNameInput
                            .getText()
                            .toString()
                            .trim();

            String studentNumber =
                    studentNumberInput
                            .getText()
                            .toString()
                            .trim();

            int programmePosition =
                    programmeSpinner
                            .getSelectedItemPosition();

            // First name validation
            if (TextUtils.isEmpty(firstName)) {

                firstNameInput.setError(
                        "Enter your first name"
                );

                firstNameInput.requestFocus();

                return;
            }

            // Last name validation
            if (TextUtils.isEmpty(lastName)) {

                lastNameInput.setError(
                        "Enter your last name"
                );

                lastNameInput.requestFocus();

                return;
            }

            // Student number validation
            if (!studentNumber.matches("\\d{9}")) {

                studentNumberInput.setError(
                        "Student number must contain exactly 9 digits"
                );

                studentNumberInput.requestFocus();

                return;
            }

            /*
             * Programme must be an actual selection.
             * Position 0 is only the guideline.
             */
            if (programmePosition == 0) {

                new AlertDialog.Builder(
                        CreateAccountActivity.this
                )
                        .setTitle("Programme Required")
                        .setMessage(
                                "Please choose your programme before continuing."
                        )
                        .setPositiveButton(
                                "OK",
                                null
                        )
                        .show();

                return;
            }

            String programme =
                    programmeSpinner
                            .getSelectedItem()
                            .toString();

            // Move to Step 2
            Intent intent =
                    new Intent(
                            CreateAccountActivity.this,
                            CreateAccountStep2Activity.class
                    );

            // Carry Step 1 information forward
            intent.putExtra(
                    "first_name",
                    firstName
            );

            intent.putExtra(
                    "last_name",
                    lastName
            );

            intent.putExtra(
                    "student_number",
                    studentNumber
            );

            intent.putExtra(
                    "programme",
                    programme
            );

            startActivity(intent);
        });

        // BACK
        backButton.setOnClickListener(v -> finish());

        // HELP
        helpButton.setOnClickListener(v -> {

            new AlertDialog.Builder(
                    CreateAccountActivity.this
            )
                    .setTitle(
                            "Create Account — Step 1"
                    )
                    .setMessage(
                            "Enter your basic student details.\n\n" +

                                    "• First Name\n" +
                                    "Enter your first name.\n\n" +

                                    "• Last Name\n" +
                                    "Enter your surname.\n\n" +

                                    "• Student Number\n" +
                                    "Enter your 9-digit student number.\n\n" +

                                    "• Programme\n" +
                                    "Tap Choose programme and select " +
                                    "your programme. You can open the " +
                                    "dropdown again and change your " +
                                    "selection before continuing.\n\n" +

                                    "Press CONTINUE when all information is complete."
                    )
                    .setPositiveButton(
                            "GOT IT",
                            null
                    )
                    .show();
        });
    }
}