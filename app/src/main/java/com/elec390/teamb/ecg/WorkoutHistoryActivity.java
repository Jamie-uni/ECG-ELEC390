package com.elec390.teamb.ecg;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;



public class WorkoutHistoryActivity extends Activity {
    private DataStorage dataStorage;
    private List<SessionEntity> sessions;
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
        sessions = dataStorage.getSessionList();
        mListView = (ListView) findViewById(R.id.sessionListView);
        final Context context = this;
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast toast=Toast.makeText(getApplicationContext(),"Session Selected",Toast.LENGTH_SHORT);
                toast.setMargin(50,50);
                toast.show();
                SessionEntity selectedSession = sessions.get(position);
                Intent detailIntent = new Intent(context, WorkoutSessionDetailsActivity.class);
                detailIntent.putExtra("SESSION_DETAILS", selectedSession.detailsString());
                startActivity(detailIntent);
            }
        });
        printSessions();
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
    private void printSessions() {
        // Set ListView adapter to display the toString() of each session in a separate TextView
        final ArrayAdapter<SessionEntity> adapter = new ArrayAdapter<SessionEntity>(this,
                R.layout.activity_listview, sessions);
        mListView.setAdapter(adapter);
    }
}