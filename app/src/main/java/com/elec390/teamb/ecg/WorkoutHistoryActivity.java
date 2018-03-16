package com.elec390.teamb.ecg;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.util.List;



public class WorkoutHistoryActivity extends Activity {
    private DataStorage dataStorage;
    private List<SessionEntity> sessions;
    private ListView mListView;
    private MenuItem editMenuItem = null;
    private boolean deleteMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_history);
        final Context context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Workout History");
        dataStorage = new DataStorage(context);
        sessions = dataStorage.getSessionList();
        mListView = (ListView) findViewById(R.id.sessionListView);
        // Set ListView adapter to display the toString() of each session in a separate TextView
        final ArrayAdapter<SessionEntity> adapter = new ArrayAdapter<SessionEntity>(context,
                R.layout.activity_listview, sessions);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final SessionEntity selectedSession = sessions.get(position);
                final int positionToRemove = position;
                AlertDialog.Builder adb1=new AlertDialog.Builder(context);
                adb1.setTitle("View or Delete?");
                adb1.setMessage("Would you like to view or delete session?");
                adb1.setNegativeButton("View", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent detailIntent = new Intent(context, WorkoutSessionDetailsActivity.class);
                        detailIntent.putExtra("SESSION_DETAILS", selectedSession.detailsString());
                        startActivity(detailIntent);
                    }});
                adb1.setPositiveButton("Delete", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder adb2=new AlertDialog.Builder(context);
                        adb2.setTitle("Delete?");
                        adb2.setMessage("Are you sure you want to delete Session?");
                        adb2.setNegativeButton("Cancel", null);
                        adb2.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Remove from database
                                dataStorage.deleteSession(selectedSession);
                                // Delete file
                                File ecgdataroot = new File(Environment.getExternalStorageDirectory(), "ECGData");
                                File ecgdatafile = new File(ecgdataroot,
                                        DateTypeConverter.dateToString(selectedSession.mSessionStart)+".txt");
                                try {
                                    ecgdatafile.delete();
                                } catch (Exception e) {e.printStackTrace();}
                                sessions.remove(positionToRemove);
                                adapter.notifyDataSetChanged();
                            }});
                        adb2.show();
                    }});
                adb1.show();
                /*
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete Session?");
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Remove from database
                        dataStorage.deleteSession(selectedSession);
                        // Delete file
                        File ecgdataroot = new File(Environment.getExternalStorageDirectory(), "ECGData");
                        File ecgdatafile = new File(ecgdataroot,
                                DateTypeConverter.dateToString(selectedSession.mSessionStart)+".txt");
                        try {
                            ecgdatafile.delete();
                        } catch (Exception e) {e.printStackTrace();}
                        sessions.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                    }});
                adb.show();*/
                //Intent detailIntent = new Intent(context, WorkoutSessionDetailsActivity.class);
                //detailIntent.putExtra("SESSION_DETAILS", selectedSession.detailsString());
                //startActivity(detailIntent);
            }
        });
        //printSessions();
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