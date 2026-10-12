package com.example.ict361studentregistration.data.room;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(
        entities = {Student.class, Lecturer.class, Assignment.class, Timetable.class},
        version = 5,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract StudentDao studentDao();
    public abstract LecturerDao lecturerDao();
    public abstract AssignmentDao assignmentDao();
    public abstract TimetableDao timetableDao();

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS assignments (" +
                    "assignmentId TEXT NOT NULL, courseCode TEXT, title TEXT, description TEXT, date TEXT, status TEXT, " +
                    "accentColor INTEGER NOT NULL, synced INTEGER NOT NULL, PRIMARY KEY(assignmentId))");
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS timetable (" +
                    "classId TEXT NOT NULL, dayOfWeek TEXT, courseCode TEXT, courseName TEXT, startTime TEXT, endTime TEXT, " +
                    "venue TEXT, classType TEXT, accentColor INTEGER NOT NULL, synced INTEGER NOT NULL, PRIMARY KEY(classId))");
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE students ADD COLUMN passwordHash TEXT");
            db.execSQL("ALTER TABLE students ADD COLUMN passwordSalt TEXT");
            db.execSQL("ALTER TABLE timetable ADD COLUMN programme TEXT");
        }
    };

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE lecturers ADD COLUMN passwordHash TEXT");
            db.execSQL("ALTER TABLE lecturers ADD COLUMN passwordSalt TEXT");
        }
    };

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "ict361_student_registration_database"
                    ).addMigrations(
                            MIGRATION_1_2, MIGRATION_2_3,
                            MIGRATION_3_4, MIGRATION_4_5
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
