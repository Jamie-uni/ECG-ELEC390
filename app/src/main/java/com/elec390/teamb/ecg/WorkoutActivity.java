package com.elec390.teamb.ecg;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class WorkoutActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    String TAG = "WorkoutActivity";
    private Button beginWorkoutButton;
    private Button pauseWorkoutButton;
    private Button stopWorkoutButton;
    private Button makeCommentButton;

    private TextView timer;
    long startTime = 0;
    long pauseTime = 0;
    Handler timerHandler = new Handler();
    String stopTime;

    AlertDialog commentDialog;
    EditText commentText;
    String commentTime;
    String commentAndTime;

    private final Handler mHandler = new Handler();
    private LineGraphSeries<DataPoint> mSeries;
    private double lastXvalue = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Workout");
        // Check if access to external storage is granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission to access external storage
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);}
        //Assign Buttons.
        beginWorkoutButton = (Button) this.findViewById(R.id.beginWorkoutButton);
        pauseWorkoutButton = (Button) this.findViewById(R.id.pauseWorkoutButton);
        stopWorkoutButton = (Button) this.findViewById(R.id.stopWorkoutButton);
        makeCommentButton = (Button) this.findViewById(R.id.commentButton);
        //Create onClick Listeners for buttons.
        beginWorkoutButton.setOnClickListener(BeginPressed);
        pauseWorkoutButton.setOnClickListener(PausePressed);
        stopWorkoutButton.setOnClickListener(StopPressed);
        makeCommentButton.setOnClickListener(CommentPressed);
        //Makes several buttons invisible.
        pauseWorkoutButton.setVisibility(View.GONE);
        stopWorkoutButton.setVisibility(View.GONE);
        makeCommentButton.setVisibility(View.GONE);
        //Timer declaration.
        timer = (TextView) findViewById(R.id.timerTextView);
        //Comment button dialog window.
        commentDialog = new AlertDialog.Builder(this).create();
        commentText = new EditText(this);
        commentDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        commentDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        mSeries = new LineGraphSeries<>();
        graph.addSeries(mSeries);
        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Time (s)");
        gridLabel.setVerticalAxisTitle("Voltage (mV)");
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
    protected void onResume() {
        super.onResume();

            Log.d(TAG, "onResume called");
        //MenuItem item = new
        //int id = item.getItemId();
        //Intent intent;
        //item.setChecked(true);
    }


    //Timer runnable.
    Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            long milliseconds = (System.currentTimeMillis() - startTime);
            int seconds = (int) (milliseconds / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds %= 60;
            timer.setText(String.format("%d:%02d:%02d", hours, minutes, seconds));
            timerHandler.postDelayed(this, 500);
        }
    };
    //ExecutorService updateTimerExecutor = Executors.newSingleThreadExecutor();
    //Future updateTimerFuture = updateTimerExecutor.submit(updateTimer);

    Button.OnClickListener BeginPressed = new Button.OnClickListener(){
        @Override
        public void onClick(View v){
            Log.d("TAG", "Workout Activity: Begin button pressed.");
            if(pauseWorkoutButton.getText().toString().equals("Resume")){
                pauseWorkoutButton.setText("Pause");
            }
            beginWorkoutButton.setVisibility(View.GONE);
            pauseWorkoutButton.setVisibility(View.VISIBLE);
            stopWorkoutButton.setVisibility(View.VISIBLE);
            makeCommentButton.setVisibility(View.VISIBLE);

            //Resets timer and starts counting
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(updateTimer, 0);
            DataPoint[] tempDP = {new DataPoint(0,0)};
            mSeries.resetData(tempDP);
            lastXvalue = 0.0;
            index = 0;
            mHandler.postDelayed(plotPoint, 0);
        }
    };

    Button.OnClickListener PausePressed = new Button.OnClickListener(){
        @Override
        public void onClick(View v){
            //PAUSE button pressed.
            if(pauseWorkoutButton.getText().toString().equals("Pause")){
                Log.d("TAG", "Workout Activity: Pause button pressed.");
                pauseWorkoutButton.setText("Resume");
                timerHandler.removeCallbacks(updateTimer);
                mHandler.removeCallbacks(plotPoint);
                pauseTime = System.currentTimeMillis() - startTime;
            }
            //RESUME button pressed.
            else if(pauseWorkoutButton.getText().toString().equals("Resume")){
                Log.d("TAG", "Workout Activity: Resume button pressed.");
                pauseWorkoutButton.setText("Pause");
                startTime = System.currentTimeMillis() - pauseTime;
                timerHandler.postDelayed(updateTimer,0);
                mHandler.postDelayed(plotPoint, 0);
            }
        }
    };

    Button.OnClickListener StopPressed = new Button.OnClickListener(){
        @Override
        public void onClick(View v){
            Log.d("TAG", "Workout Activity: Stop button pressed.");
            beginWorkoutButton.setVisibility(View.VISIBLE);
            pauseWorkoutButton.setVisibility(View.GONE);
            stopWorkoutButton.setVisibility(View.GONE);
            makeCommentButton.setVisibility(View.GONE);
            timerHandler.removeCallbacks(updateTimer);
            mHandler.removeCallbacks(plotPoint);
            timer.setText(String.format("%d:%02d:%02d", 0, 0, 0));
        }
    };

    Button.OnClickListener CommentPressed = new Button.OnClickListener(){
        @Override
        public void onClick(View v){
            Log.d("TAG", "Workout Activity: Comment button pressed.");
            pauseWorkoutButton.setText("Resume");
            timerHandler.removeCallbacks(updateTimer);
            pauseTime = System.currentTimeMillis() - startTime;
            commentTime = Long.toString(System.currentTimeMillis() - pauseTime);
            commentDialog.setTitle("Describe the problem:");
            commentDialog.setView(commentText);
            commentText.setText(""); //Clears previous comments
            commentDialog.setCanceledOnTouchOutside(false);
            commentDialog.setButton(AlertDialog.BUTTON_POSITIVE,"Save", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface saveButton, int i){
                    commentAndTime = commentText.getText().toString()
                            .concat(" ").concat(commentTime);
                    Log.d("TAG", "Entered comment: "+ commentAndTime);
                }
            });
            commentDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface cancelButton, int i){
                    Log.d("TAG", "Cancelled comment");
                }
            });
            commentDialog.show();
        }
    };
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.workout, menu);
        //nav_menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startBleScan();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (id == R.id.nav_Workout) {
            Log.d("TAG", "Drawer: Workout was selected.");
            intent = new Intent(this,WorkoutActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(intent,0);
        }
        else if (id == R.id.nav_WorkoutHistory) {
            Log.d("TAG", "Drawer: Session History was selected.");
            intent = new Intent(this,WorkoutHistoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(intent,0);
        }
        else if (id == R.id.nav_Settings) {
            Log.d("TAG", "Drawer: Settings was selected.");
            intent = new Intent(this,SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(intent,0);
        }

        return true;
    }
    private void startBleScan(){
        //Intent BleScanIntent = new Intent(this,BluetoothScanActivity.class);
        //startActivityForResult(BleScanIntent, BLE_DEVICE_REQUEST);
        startActivity(new Intent(this,BluetoothScanActivity.class));


    }
}