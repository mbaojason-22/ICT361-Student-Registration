package com.example.ict361studentregistration;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("token")
    private String token;

    @SerializedName("student")
    private StudentInfo student;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public StudentInfo getStudent() {
        return student;
    }

    public static class StudentInfo {

        @SerializedName("student_id")
        private String studentId;

        @SerializedName("student_number")
        private String studentNumber;

        @SerializedName("first_name")
        private String firstName;

        @SerializedName("last_name")
        private String lastName;

        @SerializedName("email")
        private String email;

        @SerializedName("programme_id")
        private String programmeId;

        @SerializedName("status")
        private String status;

        public String getStudentId() {
            return studentId;
        }

        public String getStudentNumber() {
            return studentNumber;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public String getProgrammeId() {
            return programmeId;
        }

        public String getStatus() {
            return status;
        }
    }
}
