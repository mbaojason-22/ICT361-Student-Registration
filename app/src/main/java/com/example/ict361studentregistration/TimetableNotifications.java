
package com.example.ict361studentregistration;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public final class TimetableNotifications {

    private static final String PREFS_NAME =
            "timetable_notifications";

    private static final String KEY_NOTIFICATIONS =
            "notifications";

    private TimetableNotifications() {
        // Prevent creating instances of this helper.
    }

    public static synchronized void addNotification(
            Context context,
            String courseCode,
            String courseName,
            String day,
            String startTime,
            String endTime,
            String venue
    ) {
        SharedPreferences preferences =
                context.getApplicationContext()
                        .getSharedPreferences(
                                PREFS_NAME,
                                Context.MODE_PRIVATE
                        );

        JSONArray notifications;

        try {
            String saved = preferences.getString(
                    KEY_NOTIFICATIONS,
                    "[]"
            );

            notifications = new JSONArray(saved);

        } catch (JSONException e) {
            notifications = new JSONArray();
        }

        String message =
                courseCode + " — " + courseName
                        + "\nTimetable updated"
                        + "\nDay: " + day
                        + "\nTime: " + startTime + " - " + endTime
                        + "\nVenue: " + venue;

        notifications.put(message);

        preferences.edit()
                .putString(
                        KEY_NOTIFICATIONS,
                        notifications.toString()
                )
                .apply();
    }

    public static synchronized List<String> getNotifications(
            Context context
    ) {
        SharedPreferences preferences =
                context.getApplicationContext()
                        .getSharedPreferences(
                                PREFS_NAME,
                                Context.MODE_PRIVATE
                        );

        List<String> result = new ArrayList<>();

        try {
            JSONArray notifications = new JSONArray(
                    preferences.getString(
                            KEY_NOTIFICATIONS,
                            "[]"
                    )
            );

            for (int i = notifications.length() - 1;
                 i >= 0; i--) {

                result.add(notifications.getString(i));
            }

        } catch (JSONException e) {
            result.add(
                    "Unable to load timetable notifications."
            );
        }

        return result;
    }
}
