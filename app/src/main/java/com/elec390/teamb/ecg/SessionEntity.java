package com.elec390.teamb.ecg;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.File;
import java.util.Date;
import java.util.Map;

/**
 * Room entity
 */

@Entity(tableName = "sessions")
public class SessionEntity {

    @PrimaryKey
    @ColumnInfo(name = "sessionstart")
    private Date mSessionStart;

    @ColumnInfo(name = "sessionend")
    private Date mSessionEnd;

    @ColumnInfo(name = "sessioncomments")
    private Map<Date,String> mSessionComments;

    @ColumnInfo(name = "sessiondata")
    private File mSessionData;

    public SessionEntity(Date ss, Date se, Map<Date,String> sc, File sessiondata) {
        this.mSessionStart = ss;
        this.mSessionEnd = se;
        this.mSessionComments = sc;
        this.mSessionData = sessiondata;
    }
}