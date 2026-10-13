package com.example.ict361studentregistration;

import android.content.Context;

public final class AuthApiClient {

    private AuthApiClient() {
        // Prevent creating instances.
    }

    public static AuthApiService getService(Context context) {
        return RetrofitClient.createService(
                context,
                AuthApiService.class
        );
    }
}
