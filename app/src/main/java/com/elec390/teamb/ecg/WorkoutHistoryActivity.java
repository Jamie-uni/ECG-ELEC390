package com.elec390.teamb.ecg;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class WorkoutHistoryActivity extends AppCompatActivity
{
    private DataStorage dataStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_history);
        dataStorage = new DataStorage(this);
        printSessions();
    }
    private void printSessions()
    {
        List<SessionEntity> sessions = dataStorage.getSessionList();
        // Set ListView adapter to display the toString() of each session in a separate TextView
        ArrayAdapter<SessionEntity> adapter = new ArrayAdapter<SessionEntity>(this,
                R.layout.activity_listview, sessions);
        ListView listView = findViewById(R.id.historyList);
        listView.setAdapter(adapter);
    }
}