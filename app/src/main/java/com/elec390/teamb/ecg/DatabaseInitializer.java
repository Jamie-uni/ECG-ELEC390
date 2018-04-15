package com.elec390.teamb.ecg;

import android.arch.persistence.room.Room;
import android.content.Context;

import java.util.List;

/**
 * Room database worker class. Uses the DAO to perform SQL functions
 */

public class DatabaseInitializer {
    public static SessionDatabase getDatabase(Context c) {
        return getAppDatabase(c);
    }
    public static void addSession(final SessionDatabase db, SessionEntity se) {
        db.sessionDao().insert(se);
    }
    public static void deleteSession(final SessionDatabase db, SessionEntity se) {
        db.sessionDao().delete(se);
    }
    public static List<SessionEntity> getSessions(SessionDatabase db) {
        return db.sessionDao().getAll();
    }
    private static SessionDatabase getAppDatabase (Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), SessionDatabase.class,
                "session-database").allowMainThreadQueries().build();
    }
}