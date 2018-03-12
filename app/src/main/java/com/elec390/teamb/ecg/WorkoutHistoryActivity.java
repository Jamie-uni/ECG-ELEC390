package com.elec390.teamb.ecg;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;



public class WorkoutHistoryActivity extends Activity
{
    private DataStorage dataStorage;
    private ListView mListView;
    private MenuItem editMenuItem = null;
    private boolean deleteMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteMode = false;
        setContentView(R.layout.activity_workout_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Workout History");
        dataStorage = new DataStorage(this);
        mListView = (ListView) findViewById(R.id.sessionListView);
        printSessions();
    }
    private void printSessions() {
        // Set ListView adapter to display the toString() of each session in a separate TextView
        List<SessionEntity> sessions = dataStorage.getSessionList();
        ArrayAdapter<SessionEntity> adapter = new ArrayAdapter<SessionEntity>(this,
                R.layout.activity_listview, sessions);
                mListView.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_profile_menu, menu);
        editMenuItem = menu.findItem(R.id.editProfileButton);
        return true;
    }
    /* Switch the activity to edit-mode, enable save button and text input */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.editProfileButton) {
            deleteMode = deleteMode^deleteMode;
            if (deleteMode == true)
                Toast.makeText(getApplicationContext(), "Delete Mode", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "Regular Mode", Toast.LENGTH_LONG).show();
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

}

