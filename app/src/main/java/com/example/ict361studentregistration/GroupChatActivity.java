package com.example.ict361studentregistration;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GroupChatActivity extends AppCompatActivity {

    private LinearLayout messagesContainer;
    private EditText messageInput;
    private ScrollView messagesScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        // Main views
        View rootView = findViewById(R.id.group_chat_root);

        messagesContainer =
                findViewById(R.id.messages_container);

        messageInput =
                findViewById(R.id.message_input);

        messagesScroll =
                findViewById(R.id.messages_scroll);


        // Handle system bars
        ViewCompat.setOnApplyWindowInsetsListener(
                rootView,
                (view, windowInsets) -> {

                    Insets insets = windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            view.getPaddingLeft(),
                            insets.top,
                            view.getPaddingRight(),
                            insets.bottom
                    );

                    return windowInsets;
                }
        );


        // Back button
        View backButton =
                findViewById(R.id.back_button);

        backButton.setOnClickListener(
                v -> finish()
        );


        // Group information
        View groupInfoButton =
                findViewById(R.id.group_info_button);

        groupInfoButton.setOnClickListener(
                v -> showGroupInformation()
        );


        // Send message
        View sendButton =
                findViewById(R.id.send_button);

        sendButton.setOnClickListener(
                v -> sendMessage()
        );


        // Attachment button
        View attachButton =
                findViewById(R.id.attach_button);

        attachButton.setOnClickListener(
                v -> showAttachmentMessage()
        );
    }


    private void sendMessage() {

        String message =
                messageInput.getText()
                        .toString()
                        .trim();

        // Don't send an empty message
        if (message.isEmpty()) {
            return;
        }


        // Create message container
        LinearLayout messageLayout =
                new LinearLayout(this);

        messageLayout.setOrientation(
                LinearLayout.VERTICAL
        );

        messageLayout.setGravity(
                android.view.Gravity.END
        );


        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        layoutParams.setMargins(
                0,
                0,
                0,
                dp(12)
        );

        messageLayout.setLayoutParams(
                layoutParams
        );


        // Message bubble
        TextView messageText =
                new TextView(this);

        messageText.setText(message);

        messageText.setTextColor(
                android.graphics.Color.WHITE
        );

        messageText.setTextSize(14);

        messageText.setPadding(
                dp(13),
                dp(10),
                dp(13),
                dp(10)
        );

        messageText.setBackgroundResource(
                R.drawable.student_button
        );


        LinearLayout.LayoutParams bubbleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        bubbleParams.setMargins(
                0,
                0,
                0,
                dp(3)
        );

        messageText.setLayoutParams(
                bubbleParams
        );


        // Time
        TextView timeText =
                new TextView(this);

        timeText.setText(
                "Now ✓"
        );

        timeText.setTextColor(
                android.graphics.Color.rgb(
                        137,
                        150,
                        170
                )
        );

        timeText.setTextSize(10);

        LinearLayout.LayoutParams timeParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        timeParams.setMargins(
                0,
                0,
                dp(8),
                0
        );

        timeText.setLayoutParams(
                timeParams
        );


        // Add message to screen
        messageLayout.addView(
                messageText
        );

        messageLayout.addView(
                timeText
        );

        messagesContainer.addView(
                messageLayout
        );


        // Clear input
        messageInput.setText("");


        // Scroll to newest message
        messagesScroll.post(
                () -> messagesScroll.fullScroll(
                        View.FOCUS_DOWN
                )
        );
    }


    private void showGroupInformation() {

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("G02 Lab Group")
                .setMessage(
                        "Lab Group: G02\n\n"
                                + "Members: 8 / 15\n"
                                + "Status: Active\n\n"
                                + "Only students assigned to this "
                                + "lab group can participate in "
                                + "this group chat."
                )
                .setPositiveButton(
                        "OK",
                        null
                )
                .show();
    }


    private void showAttachmentMessage() {

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Attachments")
                .setMessage(
                        "File and document sharing will be "
                                + "available when the server "
                                + "and group storage features "
                                + "are connected."
                )
                .setPositiveButton(
                        "OK",
                        null
                )
                .show();
    }


    private int dp(int value) {

        return (int) (
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}