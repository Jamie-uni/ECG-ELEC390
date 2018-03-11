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
import java.util.Date;
import java.util.List;

public class DataBaseTestActivity extends AppCompatActivity {
    private DataStorage dataStorage;
    private ECGSession ecgSession;
    private Button b1,b2;
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
    }
    public void startSession(View v) {
        ecgSession = new ECGSession();
        b1.setVisibility(View.INVISIBLE);
        b2.setVisibility(View.VISIBLE);
    }
    public void stopSession(View v) {
        ecgSession.stopSession();
        List<Short> shortList = new ArrayList<>();
        for(short i=0 ; i<10 ; i++){shortList.add(i);}
        dataStorage.saveWaveform(ecgSession, shortList);
        String text = printSessions();
        TextView tv1 = findViewById(R.id.tv1);
        tv1.setMovementMethod(new ScrollingMovementMethod());
        tv1.setText(text);
        b1.setVisibility(View.VISIBLE);
        b2.setVisibility(View.INVISIBLE);
    }
    public void addComment(View v, String s) {ecgSession.addComment(s);}
/*    public void generateSession(View v) {
//        DatabaseInitializer.populateAsync(sd);
        ECGSession ecgs = new ECGSession();
        ecgs.addComment("Hi");
        ecgs.stopSession();
        ecgs.addComment("Bye");
        List<Short> shortList = new ArrayList<>();
        for(short i=0 ; i<10 ; i++){shortList.add(i);}
        dataStorage.saveWaveform(ecgs, shortList);
        String text = printSessions();
        TextView tv1 = findViewById(R.id.tv1);
        tv1.setMovementMethod(new ScrollingMovementMethod());
        tv1.setText(text);
    }*/
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
}
