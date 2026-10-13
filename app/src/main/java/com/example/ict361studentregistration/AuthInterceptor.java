
package com.example.ict361studentregistration;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private static final String PREFS_NAME =
            "campus_companion_auth";

    private static final String TOKEN_KEY = "auth_token";

    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        SharedPreferences preferences =
                context.getSharedPreferences(
                        PREFS_NAME,
                        Context.MODE_PRIVATE
                );

        String token = preferences.getString(TOKEN_KEY, null);

        Request originalRequest = chain.request();

        Request.Builder requestBuilder =
                originalRequest.newBuilder();

        if (token != null && !token.trim().isEmpty()) {
            requestBuilder.header(
                    "Authorization",
                    "Bearer " + token
            );
        }

        return chain.proceed(requestBuilder.build());
    }

    public static void saveToken(Context context, String token) {

        context.getApplicationContext()
                .getSharedPreferences(
                        PREFS_NAME,
                        Context.MODE_PRIVATE
                )
                .edit()
                .putString(TOKEN_KEY, token)
                .apply();
    }

    public static void clearToken(Context context) {

        context.getApplicationContext()
                .getSharedPreferences(
                        PREFS_NAME,
                        Context.MODE_PRIVATE
                )
                .edit()
                .remove(TOKEN_KEY)
                .apply();
    }
}
