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
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;

public class WorkoutSessionDetailsActivity extends AppCompatActivity
{
    private int data_size = 0;
    private String session_details, session_filename;
    private File ecg_data_root, ecg_datafile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_session_details);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        session_details = this.getIntent().getExtras().getString("SESSION_DETAILS");
        String session_date = this.getIntent().getExtras().getString("SESSION_DATE");
        getSupportActionBar().setTitle(session_date);
        session_filename = this.getIntent().getExtras().getString("SESSION_FILENAME");
        ecg_data_root = new File(Environment.getExternalStorageDirectory(), "ECGData");
        ecg_datafile = new File(ecg_data_root, session_filename);
        try {
            FileReader ecgFile = new FileReader(ecg_datafile);
            BufferedReader bufferedFile = new BufferedReader(ecgFile);
            while (bufferedFile.readLine() != null)data_size++;
        } catch (Exception e) {e.printStackTrace();}
        TextView tv1 = findViewById(R.id.tv1);
        TextView tv2 = findViewById(R.id.tv2);
        String[] lines = session_details.split(System.getProperty("line.separator"));
        tv1.setText(lines[0]);
        lines[0] = "";
        for (int i = 1; i < lines.length; i++){
            lines[0] = lines[0] + "\n" + lines[i];
        }
        tv2.setText(lines[0]);
        tv2.setMovementMethod(new ScrollingMovementMethod());
        GraphView graph = findViewById(R.id.ecgGraph);
        // set manual X bounds
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(5);
        // set manual Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(200);
        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(getData());
        graph.addSeries(series);
        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Time (s)");
        gridLabel.setHumanRounding(true);
        gridLabel.setLabelsSpace(-2);
        gridLabel.setVerticalLabelsVisible(false);
    }
    private DataPoint[] getData() {
        DataPoint[] values = new DataPoint[data_size-1];
        try {
            FileReader ecgFile = new FileReader(ecg_datafile);
            BufferedReader bufferedFile = new BufferedReader(ecgFile);
            bufferedFile.readLine();
            for(int i=0 ; i<data_size-1 ; i++) {
                String sTemp = bufferedFile.readLine();
                String[] splitLine = sTemp.split(",");
                double x = Double.parseDouble(splitLine[0]);
                short y = Short.parseShort(splitLine[1]);
                DataPoint v = new DataPoint(x, y/20);
                values[i] = v;
            }
            bufferedFile.close();
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
        // Get Email data from Shared Preferences
        SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(this);
        Profile profile = new Profile(sharedPreferenceHelper.getProfile());
        myShareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{profile.getDrEmail()});
        myShareIntent.putExtra(Intent.EXTRA_SUBJECT, profile.getName()+"'s ECG Data");
        myShareIntent.putExtra(Intent.EXTRA_TEXT, "Hello "+profile.getDrName()+
                ". This is my ECG data.\n" + session_details);
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