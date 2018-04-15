package com.elec390.teamb.ecg;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Testing class. Used for generating simulated ECG data to be viewed in
 * WorkoutHistoryActivity
 */
public class DataBaseTestActivity extends AppCompatActivity {
    private DataStorage dataStorage;
    private ECGSession ecgSession;
    private Button b1,b2,b3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base_test);
        // Check if access to external storage is granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission to access external storage
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }
        // Access to database and file storage
        dataStorage = new DataStorage(this);
        String text = printSessions();
        TextView tv1 = findViewById(R.id.tv1);
        tv1.setMovementMethod(new ScrollingMovementMethod());
        tv1.setText(text);
        b1 = findViewById(R.id.b1);
        b1.setVisibility(View.VISIBLE);
        b2 = findViewById(R.id.b2);
        b2.setVisibility(View.INVISIBLE);
        b3 = findViewById(R.id.b3);
        b3.setVisibility(View.INVISIBLE);
    }

    /**
     * Creates new ECGSession
     */
    public void startSession(View v) {
        ecgSession = new ECGSession();
        b1.setVisibility(View.INVISIBLE);
        b2.setVisibility(View.VISIBLE);
        b3.setVisibility(View.VISIBLE);
    }

    /**
     * Used to stop session. Generates the .ecg data file and
     * stores the ECGSession in the SQL database.
     */
    public void stopSession(View v) {
        ecgSession.stopSession();
        List<Short> shortList = new ArrayList<>();
        for(int i=0;i<20;i++)shortList.addAll(generateData());
        dataStorage.saveWaveform(ecgSession, shortList);
        String text = printSessions();
        TextView tv1 = findViewById(R.id.tv1);
        tv1.setMovementMethod(new ScrollingMovementMethod());
        tv1.setText(text);
        b1.setVisibility(View.VISIBLE);
        b2.setVisibility(View.INVISIBLE);
        b3.setVisibility(View.INVISIBLE);
    }

    /**
     * Adds comments to the current ECGSession
     */
    public void addComment(View v) {ecgSession.addComment("Hi");}
    private String printSessions() {
        String text = "";
        List<SessionEntity> sessions = dataStorage.getSessionList();
        for(int i=0 ; i<sessions.size() ; i++) {
            text += "Session #" + sessions.get(i).sId + "\nStart Time:\n"+ sessions.get(i).mSessionStart
                    + "\nEnd Time:\n" + sessions.get(i).mSessionEnd + "\nFile Name in ECGData Folder:\n"
                    + sessions.get(i).mSessionDataFileName + "\nSession Comments:\n"
                    + sessions.get(i).mSessionComments + "\n";
        }
        return text;
    }

    /**
     * Generates an array of Short values representing a simulated ECG reading
     */
    private List<Short> generateData() {
        Short[] ecgData = new Short[] {900,900,900,900,900,900,900,900,900,900,900,899,900,914,994,1060,1104,1125,1128,1101,1049,971,899,900,901,900,899,900,946,1239,1531,1820,2110,2409,2302,2015,1725,1433,1142,894,826,752,674,701,775,851,901,900,899,899,900,920,997,1068,1131,1183,1222,1244,1249,1238,1211,1168,1111,1045,971,901,900,900,900,900,900,900,900,900,900,917,932,934,921,901,900,900,900,900,900,900,900,900,900,900,900};
        return Arrays.asList(ecgData);
    }
}