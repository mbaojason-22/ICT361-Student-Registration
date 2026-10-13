package com.example.ict361studentregistration.data.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "assignments")
public class Assignment {

    @PrimaryKey
    @NonNull
    public String assignmentId = "";

    public String courseCode;
    public String title;
    public String description;
    public String date;
    public String status;
    public int accentColor;

    // Tracks whether this assignment has synchronized with the server.
    public boolean synced = false;

    public Assignment() {
    }
}