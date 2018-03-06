package com.elec390.teamb.ecg;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Room database
 */

@Database(entities = {SessionEntity.class}, version = 1)
public abstract class SessionDatabase extends RoomDatabase {
    public abstract SessionDao sessionDao();
}