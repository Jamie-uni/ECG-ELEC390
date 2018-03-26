package com.elec390.teamb.ecg;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 *
 */

public class DataStorage {
    private final SessionDatabase sessionDatabase;
    public DataStorage(Context c) {
        sessionDatabase = DatabaseInitializer.getDatabase(c);
    }
    public void saveWaveform(ECGSession ecgs, List<Short> data) {
        File ecgdataroot = new File(Environment.getExternalStorageDirectory(), "ECGData");
        // Create storage folder if it doesn't exist
        if (!ecgdataroot.exists()) ecgdataroot.mkdirs();
        // Create file
        File ecgdatafile = new File(ecgdataroot,
                DateTypeConverter.dateToString(ecgs.getStartTime())+".txt");
        try {
            FileWriter ecgfilewriter = new FileWriter(ecgdatafile,true);
            for(int i=0 ; i<data.size() ; i++) {ecgfilewriter.append(Short.toString(data.get(i)) + "\n");}
            ecgfilewriter.flush();
            ecgfilewriter.close();
        } catch (Exception e) {e.printStackTrace();}
        SessionEntity sessent;
        sessent = new SessionEntity(ecgs.getStartTime(), ecgs.getStopTime(),
                ecgs.getTimestampedComments(),
                DateTypeConverter.dateToString(ecgs.getStartTime())+".txt");
        DatabaseInitializer.addSession(sessionDatabase, sessent);
    }
    public List<SessionEntity> getSessionList() {
        return DatabaseInitializer.getSessions(sessionDatabase);
    }
    public void deleteSession(SessionEntity se) {
        DatabaseInitializer.deleteSession(sessionDatabase, se);
    }
}