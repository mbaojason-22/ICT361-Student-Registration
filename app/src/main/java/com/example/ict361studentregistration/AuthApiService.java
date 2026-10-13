package com.example.ict361studentregistration;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("auth/register")
    Call<JsonObject> register(@Body JsonObject request);

    @POST("auth/verify-email")
    Call<JsonObject> verifyEmail(@Body JsonObject request);

    @POST("auth/resend-verification-code")
    Call<JsonObject> resendVerificationCode(@Body JsonObject request);

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}