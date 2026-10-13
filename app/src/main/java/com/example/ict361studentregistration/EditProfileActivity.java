package com.example.ict361studentregistration;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private EditText phoneInput;

    private TextView studentNumberText;

    private Spinner genderSpinner;
    private Spinner yearSpinner;
    private Spinner programmeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);

        setupViews();
        setupSpinners();
        loadExistingInformation();
        setupButtons();
    }

    private void setupViews() {

        firstNameInput = findViewById(R.id.edit_first_name);
        lastNameInput = findViewById(R.id.edit_last_name);

        studentNumberText =
                findViewById(R.id.edit_student_number);

        genderSpinner =
                findViewById(R.id.edit_gender);

        yearSpinner =
                findViewById(R.id.edit_year);

        programmeSpinner =
                findViewById(R.id.edit_programme);

        emailInput =
                findViewById(R.id.edit_email);

        phoneInput =
                findViewById(R.id.edit_phone);
    }

    private void setupSpinners() {

        // Gender
        String[] genders = {
                "Select gender",
                "Male",
                "Female",
                "Prefer not to say"
        };

        ArrayAdapter<String> genderAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        genders
                );

        genderAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        genderSpinner.setAdapter(genderAdapter);


        // Year of study
        String[] years = {
                "Select year",
                "First Year",
                "Second Year",
                "Third Year",
                "Fourth Year"
        };

        ArrayAdapter<String> yearAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        years
                );

        yearAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        yearSpinner.setAdapter(yearAdapter);


        // Programme
        String[] programmes = {
                "Choose programme",
                "Computer Science",
                "Information Technology",
                "Data Science",
                "Cybersecurity"
        };

        ArrayAdapter<String> programmeAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        programmes
                );

        programmeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        programmeSpinner.setAdapter(programmeAdapter);
    }

    private void loadExistingInformation() {

        // Existing values sent from ProfileActivity.
        String currentName =
                getIntent().getStringExtra("current_name");

        String currentProgramme =
                getIntent().getStringExtra("current_programme");

        String currentStudentNumber =
                getIntent().getStringExtra("current_student_number");

        String currentGender =
                getIntent().getStringExtra("current_gender");

        String currentYear =
                getIntent().getStringExtra("current_year");

        String currentEmail =
                getIntent().getStringExtra("current_email");

        String currentPhone =
                getIntent().getStringExtra("current_phone");


        // -----------------------------------------------------
        // Full name -> First + Last name
        // -----------------------------------------------------

        if (currentName != null
                && !currentName.trim().isEmpty()) {

            String trimmedName = currentName.trim();

            String[] nameParts =
                    trimmedName.split("\\s+", 2);

            firstNameInput.setText(nameParts[0]);

            if (nameParts.length > 1) {
                lastNameInput.setText(nameParts[1]);
            }
        }


        // -----------------------------------------------------
        // Protected student number
        // -----------------------------------------------------

        if (currentStudentNumber != null
                && !currentStudentNumber.trim().isEmpty()) {

            studentNumberText.setText(
                    currentStudentNumber.trim()
            );
        }


        // -----------------------------------------------------
        // Email
        // -----------------------------------------------------

        if (currentEmail != null) {
            emailInput.setText(currentEmail);
        }


        // -----------------------------------------------------
        // Phone
        // -----------------------------------------------------

        if (currentPhone != null) {
            phoneInput.setText(currentPhone);
        }


        // -----------------------------------------------------
        // Programme
        // -----------------------------------------------------

        if (currentProgramme != null) {

            setSpinnerValue(
                    programmeSpinner,
                    currentProgramme
            );
        }


        // -----------------------------------------------------
        // Gender
        // -----------------------------------------------------

        if (currentGender != null) {

            setSpinnerValue(
                    genderSpinner,
                    currentGender
            );
        }


        // -----------------------------------------------------
        // Year
        // -----------------------------------------------------

        if (currentYear != null) {

            setSpinnerValue(
                    yearSpinner,
                    currentYear
            );
        }
    }

    private void setSpinnerValue(
            Spinner spinner,
            String value
    ) {

        if (value == null) {
            return;
        }

        for (int i = 0;
             i < spinner.getCount();
             i++) {

            Object item =
                    spinner.getItemAtPosition(i);

            if (item != null
                    && item.toString()
                    .equalsIgnoreCase(value)) {

                spinner.setSelection(i);
                break;
            }
        }
    }

    private void setupButtons() {

        // Back
        View backButton =
                findViewById(
                        R.id.edit_profile_back_button
                );

        backButton.setOnClickListener(v -> finish());


        // Save
        Button saveButton =
                findViewById(
                        R.id.save_profile_button
                );

        saveButton.setOnClickListener(
                v -> saveProfile()
        );


        // Cancel
        Button cancelButton =
                findViewById(
                        R.id.cancel_edit_profile_button
                );

        cancelButton.setOnClickListener(
                v -> finish()
        );
    }

    private void saveProfile() {

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

        String email =
                emailInput
                        .getText()
                        .toString()
                        .trim();

        String phone =
                phoneInput
                        .getText()
                        .toString()
                        .trim();

        String gender =
                genderSpinner
                        .getSelectedItem()
                        .toString();

        String year =
                yearSpinner
                        .getSelectedItem()
                        .toString();

        String programme =
                programmeSpinner
                        .getSelectedItem()
                        .toString();


        // -----------------------------------------------------
        // Validate name
        // -----------------------------------------------------

        if (TextUtils.isEmpty(firstName)) {

            firstNameInput.setError(
                    "Enter your first name"
            );

            firstNameInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lastName)) {

            lastNameInput.setError(
                    "Enter your last name"
            );

            lastNameInput.requestFocus();
            return;
        }


        String fullName =
                firstName + " " + lastName;


        if (fullName.trim().length() < 2) {

            Toast.makeText(
                    this,
                    "Please enter a valid name.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // -----------------------------------------------------
        // Validate programme
        // -----------------------------------------------------

        if (programme.equals("Choose programme")) {

            Toast.makeText(
                    this,
                    "Please select your programme.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // -----------------------------------------------------
        // Validate year
        // -----------------------------------------------------

        if (year.equals("Select year")) {

            Toast.makeText(
                    this,
                    "Please select your year of study.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // -----------------------------------------------------
        // Validate gender
        // -----------------------------------------------------

        if (gender.equals("Select gender")) {

            Toast.makeText(
                    this,
                    "Please select your gender.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // -----------------------------------------------------
        // Prepare result
        // -----------------------------------------------------

        Intent result =
                new Intent();

        result.putExtra(
                "updated_name",
                fullName
        );

        result.putExtra(
                "updated_programme",
                programme
        );

        result.putExtra(
                "updated_gender",
                gender
        );

        result.putExtra(
                "updated_year",
                year
        );

        result.putExtra(
                "updated_email",
                email
        );

        result.putExtra(
                "updated_phone",
                phone
        );

        /*
         * Student number is deliberately NOT edited.
         *
         * We return the existing value unchanged.
         */
        result.putExtra(
                "updated_student_number",
                studentNumberText
                        .getText()
                        .toString()
        );


        setResult(
                RESULT_OK,
                result
        );


        Toast.makeText(
                this,
                "Profile changes saved successfully.",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }
}