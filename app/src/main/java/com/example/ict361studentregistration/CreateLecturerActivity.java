package com.example.ict361studentregistration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CreateLecturerActivity extends AppCompatActivity {

    private ImageButton backButton;
    private ImageButton helpButton;

    private Spinner titleSpinner;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText staffIdInput;
    private EditText emailInput;
    private EditText contactInput;

    private Button verifyButton;
    private TextView alreadyHaveAccountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the Create Lecturer Account layout
        setContentView(R.layout.activity_create_lecturer);

        // Connect UI elements
        backButton = findViewById(R.id.create_lecturer_back_button);
        helpButton = findViewById(R.id.create_lecturer_help_button);

        titleSpinner = findViewById(R.id.lecturer_title_spinner);
        firstNameInput = findViewById(R.id.lecturer_first_name_input);
        lastNameInput = findViewById(R.id.lecturer_last_name_input);
        staffIdInput = findViewById(R.id.lecturer_staff_id_input);
        emailInput = findViewById(R.id.lecturer_email_input);
        contactInput = findViewById(R.id.lecturer_contact_input);

        verifyButton = findViewById(R.id.lecturer_verify_button);
        alreadyHaveAccountText = findViewById(R.id.lecturer_already_have_account);

        // Populate Title Spinner
        ArrayAdapter<String> titleAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Mr.", "Dr.", "Ms.", "Prof.", "Mrs."}
        );
        titleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        titleSpinner.setAdapter(titleAdapter);


        // BACK BUTTON
        backButton.setOnClickListener(v -> finish());


        // ALREADY HAVE ACCOUNT LINK
        alreadyHaveAccountText.setOnClickListener(v -> finish());


        // VERIFY BUTTON
        verifyButton.setOnClickListener(v -> {

            String selectedTitle = titleSpinner.getSelectedItem() != null ? titleSpinner.getSelectedItem().toString() : "Mr.";
            String firstName = firstNameInput.getText().toString().trim();
            String lastName = lastNameInput.getText().toString().trim();
            String staffId = staffIdInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String contact = contactInput.getText().toString().trim();

            if (firstName.isEmpty()) {
                firstNameInput.setError("Enter your first name");
                firstNameInput.requestFocus();
                return;
            }

            if (lastName.isEmpty()) {
                lastNameInput.setError("Enter your last name");
                lastNameInput.requestFocus();
                return;
            }

            if (staffId.isEmpty()) {
                staffIdInput.setError("Enter your staff or lecturer ID");
                staffIdInput.requestFocus();
                return;
            }

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Enter a valid email address");
                emailInput.requestFocus();
                return;
            }

            if (contact.isEmpty() || contact.length() < 7) {
                contactInput.setError("Enter a valid contact number");
                contactInput.requestFocus();
                return;
            }

            String fullName = selectedTitle + " " + firstName + " " + lastName;

            // Show alert for verification code (default test code 1234)
            new AlertDialog.Builder(CreateLecturerActivity.this)
                    .setTitle("Verification Code Sent")
                    .setMessage(
                            "A verification code has been sent to " + selectedTitle + " " + lastName + " (" + email + ").\n\n" +
                                    "Default test verification code: 1234"
                    )
                    .setPositiveButton("PROCEED TO VERIFY", (dialog, which) -> {
                        Intent intent = new Intent(
                                CreateLecturerActivity.this,
                                VerifyCodeActivity.class
                        );
                        intent.putExtra("USER_IDENTIFIER", email);
                        intent.putExtra("USER_NAME", fullName);
                        intent.putExtra("TITLE", selectedTitle);
                        intent.putExtra("FIRST_NAME", firstName);
                        intent.putExtra("LAST_NAME", lastName);
                        intent.putExtra("STAFF_ID", staffId);
                        intent.putExtra("EMAIL", email);
                        intent.putExtra("CONTACT", contact);
                        intent.putExtra("EXPECTED_CODE", "1234");
                        intent.putExtra("NEXT_ACTIVITY", "CREATE_LECTURER_PASSWORD");
                        startActivity(intent);
                    })
                    .show();

        });


        // HELP BUTTON (Red help guide)
        helpButton.setOnClickListener(v -> {

            new AlertDialog.Builder(CreateLecturerActivity.this)
                    .setTitle("Lecturer Registration Guide")
                    .setMessage(
                            "Register your Campus Companion lecturer account:\n\n" +
                                    "1. STAFF DETAILS\n" +
                                    "Select your Title (Mr., Dr., Ms., Prof., Mrs.) and enter your First Name, Last Name, and official Staff/Lecturer ID.\n\n" +
                                    "2. CONTACT\n" +
                                    "Provide your email address and contact phone number.\n\n" +
                                    "3. VERIFY\n" +
                                    "Tap VERIFY to receive a code (Default code: 1234) before setting your password."
                    )
                    .setPositiveButton("GOT IT", null)
                    .show();

        });
    }


}
