package com.example.ict361studentregistration.data.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AssignmentDao {

    // Insert a new assignment.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAssignment(Assignment assignment);

    // Retrieve one assignment using its ID.
    @Query("SELECT * FROM assignments WHERE assignmentId = :assignmentId LIMIT 1")
    LiveData<Assignment> getAssignmentById(String assignmentId);

    // Retrieve all assignments.
    @Query("SELECT * FROM assignments ORDER BY date ASC")
    LiveData<List<Assignment>> getAllAssignments();

    // Retrieve assignments for a particular course.
    @Query("SELECT * FROM assignments WHERE courseCode = :courseCode ORDER BY date ASC")
    LiveData<List<Assignment>> getAssignmentsByCourse(String courseCode);

    // Update an existing assignment.
    @Update
    void updateAssignment(Assignment assignment);

    // Delete an assignment using its ID.
    @Query("DELETE FROM assignments WHERE assignmentId = :assignmentId")
    void deleteAssignment(String assignmentId);

    // Count assignments.
    @Query("SELECT COUNT(*) FROM assignments")
    int countAssignments();
}