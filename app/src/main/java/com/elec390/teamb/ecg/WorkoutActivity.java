package com.elec390.teamb.ecg;
import android.Manifest;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Main activity that is used to record ECG sessions
 */
public class WorkoutActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    //BLE
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public String BleDeviceName = "";
    public String BleDeviceAddress = "";
    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private boolean mRegisteredReceiver = false;
    private boolean recordingECG = false;
    double ECG_time = 0;
    //Database
    private DataStorage dataStorage;
    private ECGSession ecgSession;
    List<Short> ecgDataValues = new ArrayList<>();
    String TAG = "WorkoutActivity";
    private Button beginWorkoutButton;
    private Button pauseWorkoutButton;
    private Button stopWorkoutButton;
    private Button makeCommentButton;
    //UI
    private TextView timer;
    long startTime = 0;
    long pauseTime = 0;
    Handler timerHandler = new Handler();
    String stopTime;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private Profile profile;
    private TextView nameTextView, emailTextView;
    AlertDialog commentDialog;
    EditText commentText;
    String commentTime;
    String commentAndTime;
    //Graph
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
        dataStorage = new DataStorage(this);
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
        View headerView = navigationView.getHeaderView(0);
        sharedPreferenceHelper = new SharedPreferenceHelper(this);
        profile = new Profile(sharedPreferenceHelper.getProfile());
        nameTextView = headerView.findViewById(R.id.userNameView);
        emailTextView = headerView.findViewById(R.id.userEmailView);
        nameTextView.setText(profile.getName());
        emailTextView.setText(profile.getEmail());
        GraphView graph = findViewById(R.id.realtimeGraph);
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
        mSeries = new LineGraphSeries<>();
        graph.addSeries(mSeries);
        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("\nTime (s)");
        gridLabel.setHumanRounding(true);
        gridLabel.setLabelsSpace(-2);
        gridLabel.setVerticalLabelsVisible(false);
        //BLE
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        startActivity(new Intent(this,BluetoothScanActivity.class));
    }
    @Override
    protected void onResume() {
        super.onResume();
            Log.d(TAG, "onResume called");
        //BLE
        Intent intent = getIntent();
        if (intent.hasExtra(WorkoutActivity.EXTRAS_DEVICE_NAME) && intent.hasExtra(WorkoutActivity.EXTRAS_DEVICE_NAME) && !mConnected){
            BleDeviceName = intent.getStringExtra(WorkoutActivity.EXTRAS_DEVICE_NAME);
            BleDeviceAddress = intent.getStringExtra(WorkoutActivity.EXTRAS_DEVICE_ADDRESS);
            if (mRegisteredReceiver && (mBluetoothLeService != null))
                    unregisterReceiver(mGattUpdateReceiver);
                    mBluetoothLeService.disconnect();
                    registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
                    mBluetoothLeService.connect(BleDeviceAddress);
                    mConnected = true;
                    return;
        }
        if (BleDeviceName.isEmpty() || BleDeviceAddress.isEmpty()){
            //startActivity(new Intent(this,BluetoothScanActivity.class));
            return;
        }
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        mRegisteredReceiver = true;
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(BleDeviceAddress);
            mConnected = true;
            Log.d(TAG, "Connect request result=" + result);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // getIntent() should always return the most recent
        Log.d(TAG,"New Intent, NAME: " + intent.getStringExtra(WorkoutActivity.EXTRAS_DEVICE_NAME)
                +  ", ADDRESS: " + intent.getStringExtra(WorkoutActivity.EXTRAS_DEVICE_ADDRESS));
        setIntent(intent);
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
            timer.setText(String.format("%02d:%02d", minutes, seconds));
            timerHandler.postDelayed(this, 500);
        }
    };
    Button.OnClickListener BeginPressed = new Button.OnClickListener(){
        @Override
        public void onClick(View v){
            Log.d("TAG", "Workout Activity: Begin button pressed.");
            if (!mConnected || !mBluetoothLeService.adapterConnected()){
                mConnected = false;
                BleDeviceName = "";
                BleDeviceAddress = "";
                Snackbar.make(v, "Not paired with device", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }
            recordingECG = true;
            if(pauseWorkoutButton.getText().toString().equals("Resume")){
                pauseWorkoutButton.setText("Pause");
            }
            beginWorkoutButton.setVisibility(View.GONE);
            pauseWorkoutButton.setVisibility(View.VISIBLE);
            stopWorkoutButton.setVisibility(View.VISIBLE);
            makeCommentButton.setVisibility(View.VISIBLE);
            ecgSession = new ECGSession();
            //Resets timer and starts counting
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(updateTimer, 0);
            DataPoint[] tempDP = {new DataPoint(0,0)};
            mSeries.resetData(tempDP);
            lastXvalue = 0.0;
        }
    };
    Button.OnClickListener PausePressed = new Button.OnClickListener(){
        @Override
        public void onClick(View v){
            //PAUSE button pressed.
            if(pauseWorkoutButton.getText().toString().equals("Pause")){
                Log.d("TAG", "Workout Activity: Pause button pressed.");
                recordingECG = false;
                pauseWorkoutButton.setText("Resume");
                timerHandler.removeCallbacks(updateTimer);
                pauseTime = System.currentTimeMillis() - startTime;
            }
            //RESUME button pressed.
            else if(pauseWorkoutButton.getText().toString().equals("Resume")){
                recordingECG = true;
                Log.d("TAG", "Workout Activity: Resume button pressed.");
                pauseWorkoutButton.setText("Pause");
                startTime = System.currentTimeMillis() - pauseTime;
                timerHandler.postDelayed(updateTimer,0);
            }
        }
    };
    Button.OnClickListener StopPressed = new Button.OnClickListener(){
        @Override
        public void onClick(View v){
            Log.d("TAG", "Workout Activity: Stop button pressed.");
            recordingECG = false;
            beginWorkoutButton.setVisibility(View.VISIBLE);
            pauseWorkoutButton.setVisibility(View.GONE);
            stopWorkoutButton.setVisibility(View.GONE);
            makeCommentButton.setVisibility(View.GONE);
            ecgSession.stopSession();
            dataStorage.saveWaveform(ecgSession,ecgDataValues);
            timerHandler.removeCallbacks(updateTimer);
            //mHandler.removeCallbacks(plotPoint);
            ecgDataValues.clear();
            ECG_time = 0;
            timer.setText(String.format("%d:%02d:%02d", 0, 0, 0));
        }
    };
    Button.OnClickListener CommentPressed = new Button.OnClickListener(){
        @Override
        public void onClick(View v){
            Log.d("TAG", "Workout Activity: Comment button pressed.");
            commentDialog.setTitle("Describe the problem:");
            commentDialog.setView(commentText);
            commentText.setText(""); //Clears previous comments
            commentDialog.setCanceledOnTouchOutside(false);
            commentDialog.setButton(AlertDialog.BUTTON_POSITIVE,"Save", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface saveButton, int i){
                    ecgSession.addComment(commentText.getText().toString());
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
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //BLE
    private void clearUI() {
        //mGattServicesList.;
       // mDataField.setText(R.string.no_data);
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    private void startBleScan(){
        //Intent BleScanIntent = new Intent(this,BluetoothScanActivity.class);
        //startActivityForResult(BleScanIntent, BLE_DEVICE_REQUEST);
        mBluetoothLeService.disconnect();
        startActivity(new Intent(this,BluetoothScanActivity.class));
    }
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                gatherGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                if (recordingECG){
                    String ECGPoint = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                    mSeries.appendData(new DataPoint(ECG_time ,Double.parseDouble(ECGPoint)/20),
                            true, 10000);
                    ecgDataValues.add(Short.parseShort(ECGPoint));
                }
                ECG_time = ECG_time + 0.0042;
            }
        }
    };

    private void gatherGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = "unknownServiceString";
        String unknownCharaString = "unknownCharaString";
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }


        if (mGattCharacteristics != null) {
            final BluetoothGattCharacteristic characteristic =
                    mGattCharacteristics.get(2).get(0);
            final int charaProp = characteristic.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
                /*if (mNotifyCharacteristic != null) {
                    mBluetoothLeService.setCharacteristicNotification(
                            mNotifyCharacteristic, false);
                    mNotifyCharacteristic = null;
                }*/
                mBluetoothLeService.readCharacteristic(characteristic);
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mNotifyCharacteristic = characteristic;
                mBluetoothLeService.setCharacteristicNotification(
                        characteristic, true);
            }
        }
    }

}