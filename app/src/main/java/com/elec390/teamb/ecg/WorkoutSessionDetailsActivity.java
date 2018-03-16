package com.elec390.teamb.ecg;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class WorkoutSessionDetailsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_session_details);
        String session_details = this.getIntent().getExtras().getString("SESSION_DETAILS");
        String session_filename = this.getIntent().getExtras().getString("SESSION_FILENAME");
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
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(getData(session_filename));
        graph.addSeries(series);
    }
    private DataPoint[] getData(String sfn) {
        DataPoint[] values = new DataPoint[10];
        File ecg_data_root = new File(Environment.getExternalStorageDirectory(), "ECGData");
        File ecg_datafile = new File(ecg_data_root, sfn);
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
}