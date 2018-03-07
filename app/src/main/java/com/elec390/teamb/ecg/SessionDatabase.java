package com.elec390.teamb.ecg;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

/**
 * Room database
 */

@Database(entities = {SessionEntity.class}, version = 1, exportSchema = false)
@TypeConverters({DateTypeConverter.class})
public abstract class SessionDatabase extends RoomDatabase {
    public abstract SessionDao sessionDao();
}
