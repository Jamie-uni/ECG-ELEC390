package com.elec390.teamb.ecg;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;

public class WorkoutSessionDetailsActivity extends AppCompatActivity
{
    private ShareActionProvider mShareActionProvider;
    private String session_filename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_session_details);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        String session_details = this.getIntent().getExtras().getString("SESSION_DETAILS");
        session_filename = this.getIntent().getExtras().getString("SESSION_FILENAME");
        TextView tv1 = findViewById(R.id.tv1);
        tv1.setText(session_details);
        GraphView graph = findViewById(R.id.ecgGraph);
        // set manual X bounds
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(10);
        // set manual Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(10);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(getData());
        graph.addSeries(series);
    }
    private DataPoint[] getData() {
        DataPoint[] values = new DataPoint[10];
        File ecg_data_root = new File(Environment.getExternalStorageDirectory(), "ECGData");
        File ecg_datafile = new File(ecg_data_root, session_filename);
        try {
            FileReader ecgFile = new FileReader(ecg_datafile);
            BufferedReader bufferedFile = new BufferedReader(ecgFile);
            for(int i=0 ; i<10 ; i++) {
                short y = Short.parseShort(bufferedFile.readLine());
                DataPoint v = new DataPoint(i, y);
                values[i] = v;
            }
        } catch (Exception e) {e.printStackTrace();}
        return values;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.menu_workout_details, menu);
        // Get the share menu item.
        MenuItem shareItem = menu.findItem(R.id.action_share);
        // Now get the ShareActionProvider from the item
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        Intent myShareIntent = new Intent(Intent.ACTION_SEND);
        myShareIntent.setType("text/plain");
        File ecg_data_root = new File(Environment.getExternalStorageDirectory(), "ECGData");
        File ecg_datafile = new File(ecg_data_root, session_filename);
        myShareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(ecg_datafile));
        mShareActionProvider.setShareIntent(myShareIntent);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}