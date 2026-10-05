package com.example.ict361studentregistration;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MyClassesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_classes);

        View rootView = findViewById(R.id.my_classes_root);

        // Handle different screen sizes, status bars,
        // notches and navigation bars.
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

        // Back button
        View backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());
    }
}