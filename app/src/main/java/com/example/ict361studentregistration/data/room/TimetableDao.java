package com.example.ict361studentregistration.data.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface TimetableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTimetable(Timetable timetable);

    @Query("SELECT * FROM timetable WHERE classId = :classId LIMIT 1")
    LiveData<Timetable> getTimetableById(String classId);

    @Query("SELECT * FROM timetable ORDER BY dayOfWeek ASC, startTime ASC")
    LiveData<List<Timetable>> getAllTimetable();

    @Query("SELECT * FROM timetable WHERE dayOfWeek = :dayOfWeek ORDER BY startTime ASC")
    LiveData<List<Timetable>> getTimetableByDay(String dayOfWeek);

    @Query("SELECT * FROM timetable WHERE courseCode = :courseCode ORDER BY dayOfWeek ASC, startTime ASC")
    LiveData<List<Timetable>> getTimetableByCourse(String courseCode);

    @Query("SELECT * FROM timetable WHERE dayOfWeek = :dayOfWeek AND programme = :programme ORDER BY startTime ASC")
    LiveData<List<Timetable>> getTimetableByDayAndProgramme(String dayOfWeek, String programme);

    @Update
    void updateTimetable(Timetable timetable);

    @Query("DELETE FROM timetable WHERE classId = :classId")
    void deleteTimetable(String classId);

    @Query("SELECT COUNT(*) FROM timetable")
    int countTimetableEntries();
}
