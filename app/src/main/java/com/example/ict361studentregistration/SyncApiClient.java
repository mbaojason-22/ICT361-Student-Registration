package com.example.ict361studentregistration;

import android.content.Context;

public final class SyncApiClient {

    private SyncApiClient() {
        // Prevent creating instances of this class.
    }

    public static SyncApiService getService(Context context) {
        return RetrofitClient.createService(
                context,
                SyncApiService.class
        );
    }
}
