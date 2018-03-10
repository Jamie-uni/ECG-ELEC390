package com.elec390.teamb.ecg;
import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WorkoutHistoryActivity extends AppCompatActivity
{
    private DataStorage dataStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Workout History");

        // Check if access to external storage is granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission to access external storage
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }

        dataStorage = new DataStorage(this);

        printSessions();

    }

    private void printSessions()
    {
        String ListView = "";
        List<SessionEntity> sessions = dataStorage.getSessionList();

        for(int i=0 ; i<sessions.size() ; i++) {
            System.out.println(sessions.get(i).sId + "      " + sessions.get(i).mSessionStart + "     " + sessions.get(i).mSessionEnd
                    + "     " + sessions.get(i).mSessionComments + "      " + sessions.get(i).mSessionDataFileName);
        }
    }

    

}