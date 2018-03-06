package com.elec390.teamb.ecg;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

/**
 * Room DAO
 */

@Dao
public interface SessionDao {

    @Insert
    void insert(SessionEntity session);

    @Delete
    void delete(SessionEntity session);
}
