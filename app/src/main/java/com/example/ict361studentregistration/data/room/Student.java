
package com.example.ict361studentregistration.data.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "students")
public class Student {

    @PrimaryKey
    @NonNull
    public String studentId = "";

    public String firstName;
    public String lastName;
    public String email;
    public String phone;
    public String programme;
    public String groupName;

    // Used for secure offline password verification
    public String passwordHash;
    public String passwordSalt;

    public Student() {
    }
}