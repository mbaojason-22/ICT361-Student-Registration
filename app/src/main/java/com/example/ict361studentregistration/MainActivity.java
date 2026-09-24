package com.example.ict361studentregistration;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button studentButton;
    private Button lecturerButton;
    private ImageButton helpIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Connect Java to the buttons in activity_main.xml
        studentButton = findViewById(R.id.studentButton);
        lecturerButton = findViewById(R.id.lecturerButton);
        helpIcon = findViewById(R.id.helpIcon);


        // STUDENT BUTTON
        studentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Small press animation
                view.animate()
                        .scaleX(0.96f)
                        .scaleY(0.96f)
                        .setDuration(80)
                        .withEndAction(() -> {

                            view.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(80)
                                    .start();

                            Toast.makeText(
                                    MainActivity.this,
                                    "Student section coming next",
                                    Toast.LENGTH_SHORT
                            ).show();

                        })
                        .start();
            }
        });


        // LECTURER BUTTON
        lecturerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Small press animation
                view.animate()
                        .scaleX(0.96f)
                        .scaleY(0.96f)
                        .setDuration(80)
                        .withEndAction(() -> {

                            view.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(80)
                                    .start();

                            Toast.makeText(
                                    MainActivity.this,
                                    "Lecturer section coming next",
                                    Toast.LENGTH_SHORT
                            ).show();

                        })
                        .start();
            }
        });


        // HELP BUTTON
        helpIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(
                        MainActivity.this,
                        "Campus Companion Help",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}