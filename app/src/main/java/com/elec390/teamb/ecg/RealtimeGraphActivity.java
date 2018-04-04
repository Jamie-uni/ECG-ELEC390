package com.elec390.teamb.ecg;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class RealtimeGraphActivity extends AppCompatActivity
{
    private final Handler mHandler = new Handler();
    private LineGraphSeries<DataPoint> mSeries;
    private double lastXvalue = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_graph);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        GraphView graph = findViewById(R.id.realtimeGraph);
        // set manual X bounds
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(5);
        // set manual Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(250);
        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        mSeries = new LineGraphSeries<>(generateData());
        graph.addSeries(mSeries);
        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Time (s)");
        gridLabel.setVerticalAxisTitle("Voltage (mV)");
        mHandler.postDelayed(plotPoint, 0);
    }
    private DataPoint[] tempData;
    private int index=0;
    Runnable plotPoint = new Runnable() {
        @Override
        public void run() {
            if(index==0)tempData = generateData();
            mSeries.appendData(tempData[index],true,10000);
            index++;
            if (index < tempData.length) mHandler.postDelayed(this, 1);
            else {
                index = 0;
                mHandler.postDelayed(this, 100);
            }
        }
    };
    private DataPoint[] generateData() {
        Short[] ecgData = new Short[] {90,90,90,90,90,90,90,90,90,90,90,90,90,91,99,106,110,112,113,110,105,97,90,90,90,90,90,90,95,124,153,182,211,241,230,202,173,143,114,89,83,75,67,70,78,85,90,90,90,90,90,92,100,107,113,118,122,124,125,124,121,117,111,104,97,90,90,90,90,90,90,90,90,90,90,92,93,93,92,90,90,90,90,90,90,90,90,90,90,90,90};
        DataPoint[] values = new DataPoint[ecgData.length];
        for(int i=0;i<ecgData.length;i++){
            lastXvalue += (double) 1/200; // 200Hz Sample Rate
            DataPoint v = new DataPoint(lastXvalue, ecgData[i]);
            values[i] = v;
        }
        return values;
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