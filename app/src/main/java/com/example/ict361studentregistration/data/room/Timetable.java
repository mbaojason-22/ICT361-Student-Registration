package com.example.ict361studentregistration.data.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "timetable")
public class Timetable {
    @PrimaryKey @NonNull public String classId = "";
    public String dayOfWeek;
    public String courseCode;
    public String courseName;
    public String startTime;
    public String endTime;
    public String venue;
    public String classType;
    public String programme;
    public int accentColor;
    public boolean synced;

    public Timetable() {}
}
