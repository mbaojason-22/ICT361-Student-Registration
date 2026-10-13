package com.example.ict361studentregistration;

import com.example.ict361studentregistration.data.room.Student;
import com.example.ict361studentregistration.data.room.Assignment;
import com.example.ict361studentregistration.data.room.Timetable;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // STUDENTS

    @GET("students")
    Call<ApiResponse<List<Student>>> getStudents();

    @GET("students/{id}")
    Call<ApiResponse<Student>> getStudentById(
            @Path("id") String studentId
    );

    @POST("students")
    Call<ApiResponse<Student>> createStudent(
            @Body Student student
    );

    @PUT("students/{id}")
    Call<ApiResponse<Student>> updateStudent(
            @Path("id") String studentId,
            @Body Student student
    );

    @DELETE("students/{id}")
    Call<ApiResponse<Object>> deleteStudent(
            @Path("id") String studentId
    );


    // ASSIGNMENTS

    @GET("assignments")
    Call<ApiResponse<List<Assignment>>> getAssignments();

    @GET("assignments/{id}")
    Call<ApiResponse<Assignment>> getAssignmentById(
            @Path("id") String assignmentId
    );

    @POST("assignments")
    Call<ApiResponse<Assignment>> createAssignment(
            @Body Assignment assignment
    );

    @PUT("assignments/{id}")
    Call<ApiResponse<Assignment>> updateAssignment(
            @Path("id") String assignmentId,
            @Body Assignment assignment
    );

    @DELETE("assignments/{id}")
    Call<ApiResponse<Object>> deleteAssignment(
            @Path("id") String assignmentId
    );


    // TIMETABLE

    @GET("timetable")
    Call<ApiResponse<List<Timetable>>> getTimetable();

    @GET("timetable/{id}")
    Call<ApiResponse<Timetable>> getTimetableById(
            @Path("id") String timetableId
    );

    @POST("timetable")
    Call<ApiResponse<Timetable>> createTimetable(
            @Body Timetable timetable
    );

    @PUT("timetable/{id}")
    Call<ApiResponse<Timetable>> updateTimetable(
            @Path("id") String timetableId,
            @Body Timetable timetable
    );

    @DELETE("timetable/{id}")
    Call<ApiResponse<Object>> deleteTimetable(
            @Path("id") String timetableId
    );
}
