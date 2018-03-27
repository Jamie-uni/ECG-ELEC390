package com.elec390.teamb.ecg;

import android.Manifest;
import android.arch.persistence.room.Room;
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
import java.util.Date;
import java.util.List;

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
    public void startSession(View v) {
        ecgSession = new ECGSession();
        b1.setVisibility(View.INVISIBLE);
        b2.setVisibility(View.VISIBLE);
        b3.setVisibility(View.VISIBLE);
    }
    public void stopSession(View v) {
        ecgSession.stopSession();
        List<Short> shortList = new ArrayList<>();
        for(int i=0;i<5;i++)shortList.addAll(generateData());
        dataStorage.saveWaveform(ecgSession, shortList);
        String text = printSessions();
        TextView tv1 = findViewById(R.id.tv1);
        tv1.setMovementMethod(new ScrollingMovementMethod());
        tv1.setText(text);
        b1.setVisibility(View.VISIBLE);
        b2.setVisibility(View.INVISIBLE);
        b3.setVisibility(View.INVISIBLE);
    }
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
    private List<Short> generateData() {
        Short[] ecgData = new Short[] {90,90,90,90,90,90,90,90,90,90,90,90,90,91,99,106,110,112,113,110,105,97,90,90,90,90,90,90,95,124,153,182,211,241,230,202,173,143,114,89,83,75,67,70,78,85,90,90,90,90,90,92,100,107,113,118,122,124,125,124,121,117,111,104,97,90,90,90,90,90,90,90,90,90,90,92,93,93,92,90,90,90,90,90,90,90,90,90,90,90,90};
        return Arrays.asList(ecgData);
    }
}
