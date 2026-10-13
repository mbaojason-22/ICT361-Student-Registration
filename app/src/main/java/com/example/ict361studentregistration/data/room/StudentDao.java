package com.example.ict361studentregistration.data.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StudentDao {

    // Do not overwrite an existing student if the ID conflicts.
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertStudent(Student student);

    @Query("SELECT * FROM students WHERE studentId = :studentId LIMIT 1")
    LiveData<Student> getStudentById(String studentId);

    @Query("SELECT * FROM students WHERE studentId = :identifier OR LOWER(email) = LOWER(:identifier) LIMIT 1")
    Student findStudentByIdentifier(String identifier);

    @Query("SELECT * FROM students WHERE studentId = :identifier OR LOWER(email) = LOWER(:identifier) LIMIT 1")
    LiveData<Student> getStudentByIdentifier(String identifier);

    @Query("SELECT * FROM students ORDER BY studentId ASC")
    LiveData<List<Student>> getAllStudents();

    @Update
    void updateStudent(Student student);

    @Query("DELETE FROM students WHERE studentId = :studentId")
    void deleteStudent(String studentId);

    @Query("SELECT COUNT(*) FROM students WHERE studentId = :studentId")
    int countStudentById(String studentId);

    @Query("SELECT COUNT(*) FROM students WHERE LOWER(email) = LOWER(:email) AND email IS NOT NULL AND email != ''")
    int countByEmail(String email);

    @Query("SELECT COUNT(*) FROM students WHERE phone = :phone AND phone IS NOT NULL AND phone != ''")
    int countByPhone(String phone);
}
