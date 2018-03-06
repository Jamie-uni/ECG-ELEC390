package com.elec390.teamb.ecg;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Room entity
 */

@Entity(tableName = "sessions")
public class SessionEntity {
    @PrimaryKey
    @ColumnInfo(name = "sessionstart")
    @TypeConverters({DateTypeConverter.class})
    @NonNull
    public Date mSessionStart;

    @ColumnInfo(name = "sessionend")
    @TypeConverters({DateTypeConverter.class})
    @NonNull
    public Date mSessionEnd;

    @ColumnInfo(name = "sessioncomments")
    public String mSessionCommentsFileName;

    @ColumnInfo(name = "sessiondata")
    public String mSessionDataFileName;
}