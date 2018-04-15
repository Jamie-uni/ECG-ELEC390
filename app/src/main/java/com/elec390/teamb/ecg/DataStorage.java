package com.elec390.teamb.ecg;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * Used for storing ECG sessions
 */

public class DataStorage {
    private final SessionDatabase sessionDatabase;
    public DataStorage(Context c) {
        sessionDatabase = DatabaseInitializer.getDatabase(c);
    }

    /**
     * Saves the ECG waveform by creating an .ecg file.
     * Also inserts and ECGEntity into the SQL database
     */
    public void saveWaveform(ECGSession ecgs, List<Short> data) {
        File ecgdataroot = new File(Environment.getExternalStorageDirectory(), "ECGData");
        // Create storage folder if it doesn't exist
        if (!ecgdataroot.exists()) ecgdataroot.mkdirs();
        // Create file
        File ecgdatafile = new File(ecgdataroot,
                DateTypeConverter.dateToString(ecgs.getStartTime())+".ecg");
        try {
            FileWriter ecgfilewriter = new FileWriter(ecgdatafile,true);
            ecgfilewriter.append("Time,Value\n");
            for(int i=0 ; i<data.size() ; i++) {
                // Data is sampled at 200 Hz
                double d = (double) i/200;
                ecgfilewriter.append(d+",");
                ecgfilewriter.append(Short.toString(data.get(i)) + "\n");
            }
            ecgfilewriter.flush();
            ecgfilewriter.close();
        } catch (Exception e) {e.printStackTrace();}
        SessionEntity sessent;
        sessent = new SessionEntity(ecgs.getStartTime(), ecgs.getStopTime(),
                ecgs.getTimestampedComments(),
                DateTypeConverter.dateToString(ecgs.getStartTime())+".ecg");
        DatabaseInitializer.addSession(sessionDatabase, sessent);
    }

    /**
     * Returns a List of SessionEntity objects
     */
    public List<SessionEntity> getSessionList() {
        return DatabaseInitializer.getSessions(sessionDatabase);
    }

    /**
     * Deletes a SessionEntity from the SQL database and removes the datafile
     */
    public void deleteSession(SessionEntity se) {
        DatabaseInitializer.deleteSession(sessionDatabase, se);
    }
}