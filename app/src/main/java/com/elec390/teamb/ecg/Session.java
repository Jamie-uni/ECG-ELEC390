package com.elec390.teamb.ecg;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Room entity
 */

@Entity(tableName = "sessions")
public class Session {

    @PrimaryKey
    @ColumnInfo(name = "ecgsession")
    private ECGSession mSession;

    @ColumnInfo(name = "sessiondata")
    private short[] mSessionData;

    public Session(ECGSession ecgs, short[] sessiondata) {
        this.mSession = ecgs;
        this.mSessionData = sessiondata;
    }
}
