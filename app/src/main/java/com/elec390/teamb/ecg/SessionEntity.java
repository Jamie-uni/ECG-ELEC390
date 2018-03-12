package com.elec390.teamb.ecg;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Room entity
 */

@Entity(tableName = "session")
public class SessionEntity {
    @PrimaryKey (autoGenerate = true)
    @ColumnInfo(name = "sessionid")
    public int sId;

    @ColumnInfo(name = "sessionstart")
    @TypeConverters({DateTypeConverter.class})
    @NonNull
    public final Date mSessionStart;

    @ColumnInfo(name = "sessionend")
    @TypeConverters({DateTypeConverter.class})
    @NonNull
    public final Date mSessionEnd;

    @ColumnInfo(name = "sessioncomments")
    public final String mSessionComments;

    @ColumnInfo(name = "sessiondata")
    public final String mSessionDataFileName;

    public SessionEntity(Date mSessionStart, Date mSessionEnd,
                         String mSessionComments, String mSessionDataFileName) {
        this.mSessionStart = mSessionStart;
        this.mSessionEnd = mSessionEnd;
        this.mSessionComments = mSessionComments;
        this.mSessionDataFileName = mSessionDataFileName;
    }
    public String toString() {
        return "Session #" + sId + "\nStart Time:\n"+ mSessionStart + "\nEnd Time:\n"
                       + mSessionEnd + "\nFile Name in ECGData Folder:\n"
                       + mSessionDataFileName + "\nSession Comments:\n" + mSessionComments + "\n";
        }
}