
package com.example.ict361studentregistration.data.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LecturerDao {

    // Insert a lecturer or replace an existing record
    // with the same lecturer ID.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLecturer(Lecturer lecturer);

    // Retrieve a lecturer using their lecturer ID.
    @Query("SELECT * FROM lecturers WHERE lecturerId = :lecturerId LIMIT 1")
    LiveData<Lecturer> getLecturerById(String lecturerId);

    // Retrieve a lecturer synchronously for authentication.
    @Query("SELECT * FROM lecturers WHERE lecturerId = :lecturerId LIMIT 1")
    Lecturer findLecturerById(String lecturerId);

    // Retrieve a lecturer by email, ignoring letter case.
    @Query("SELECT * FROM lecturers WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    Lecturer findLecturerByEmail(String email);

    // Check whether a lecturer ID already exists.
    @Query("SELECT COUNT(*) FROM lecturers WHERE lecturerId = :lecturerId")
    int countLecturerById(String lecturerId);

    // Check whether an email is already registered.
    @Query("SELECT COUNT(*) FROM lecturers WHERE LOWER(email) = LOWER(:email)")
    int countLecturerByEmail(String email);

    // Retrieve all lecturers.
    @Query("SELECT * FROM lecturers ORDER BY lecturerId ASC")
    LiveData<List<Lecturer>> getAllLecturers();

    // Update an existing lecturer.
    @Update
    void updateLecturer(Lecturer lecturer);

    // Delete a lecturer using their lecturer ID.
    @Query("DELETE FROM lecturers WHERE lecturerId = :lecturerId")
    void deleteLecturer(String lecturerId);
}
