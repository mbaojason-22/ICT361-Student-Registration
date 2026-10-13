
package com.example.ict361studentregistration.data.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lecturers")
public class Lecturer {

    @PrimaryKey
    @NonNull
    public String lecturerId = "";

    public String firstName;
    public String lastName;
    public String email;
    public String phone;

    // Used for secure offline password verification
    public String passwordHash;
    public String passwordSalt;

    public Lecturer() {
        // Room uses this constructor to create Lecturer objects.
    }
}
