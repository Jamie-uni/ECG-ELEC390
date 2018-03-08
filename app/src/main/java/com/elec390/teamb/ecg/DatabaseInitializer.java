package com.elec390.teamb.ecg;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.Date;
import java.util.List;

/**
 * Room database worker class
 */

public class DatabaseInitializer {
    public static void populateAsync(@NonNull final SessionDatabase db) {
        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }
    public static SessionDatabase getDatabase(Context c) {
//        GetDbAsync task = new GetDbAsync(c);
//        task.execute();
        return getAppDatabase(c);
    }
    private static void addSession(final SessionDatabase db, SessionEntity se) {
        db.sessionDao().insert(se);
    }
    public static List<SessionEntity> getSessions(SessionDatabase db) {
        return db.sessionDao().getAll();
    }
    private static void populateWithTestData(SessionDatabase db) {
        Date start = new Date();
        SessionEntity se = new SessionEntity(start, start, "Comments:"
                , "data.csv");
        addSession(db, se);
    }
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final SessionDatabase mDb;
        PopulateDbAsync(SessionDatabase db) {mDb = db;}
        @Override
        protected Void doInBackground(final Void... params) {
            populateWithTestData(mDb);
            return null;
        }
    }
    private static SessionDatabase getAppDatabase (Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), SessionDatabase.class,
                "session-database").allowMainThreadQueries().build();
    }
    private static class GetDbAsync extends AsyncTask<Void, Void, Void> {
        private final Context mContext;
        public SessionDatabase appdatabase;
        GetDbAsync(Context c) {mContext = c;}
        @Override
        protected Void doInBackground(final Void... params) {
            appdatabase = getAppDatabase(mContext);
            return null;
        }
    }
}
