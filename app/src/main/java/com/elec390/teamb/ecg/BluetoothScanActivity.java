package com.elec390.teamb.ecg;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.*;

public class BluetoothScanActivity extends AppCompatActivity {

    String TAG = "BluetoothScanActivity";

    // Defines
    private final static int REQUEST_ENABLE_BT = 1;
    private final static int REQUEST_FINE_LOCATION = 2;
    private final static int SCAN_PERIOD = 20000;       //Scan Period in ms

    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeScanner mBluetoothLeScanner;
    public HashMap<String, BluetoothDevice> mScanResults;
    BtleScanCallback mScanCallback;
    boolean mScanning;
    Handler mHandler;
    List<String> mScanNames;
    List<String> mScanUUIDs;
    // List View
    ListView mListView;
    public ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_scan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Device Scan");
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final Context context = this;
        mScanNames = new ArrayList<>();
        mScanUUIDs = new ArrayList<>();
        // Set ListView adapter to display BLE Devices
        mListView = (ListView) findViewById(R.id.bleListView);
        adapter = new ArrayAdapter<>(context,
                R.layout.activity_listview2, mScanNames);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //final SessionEntity selectedSession = sessions.get(position);

                Log.d(TAG, "Clicked: " + mScanNames.get(position));
                final BluetoothDevice device = mScanResults.get(mScanUUIDs.get(position));
                if (device == null) return;
                final Intent intent = new Intent(view.getContext(), WorkoutActivity.class);
                intent.putExtra(WorkoutActivity.EXTRAS_DEVICE_NAME, device.getName());
                intent.putExtra(WorkoutActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                        Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityIfNeeded(intent, 0);
                stopScan();
                finish();
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan(view);
            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        startScan(findViewById(R.id.fab));

    }

    private void startScan(View view) {
        Log.d(TAG, "Start Scan");

        if (!hasPermissions() || mScanning) {
            Snackbar.make(view, "Already Scanning", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        Snackbar.make(view, "Now Scanning", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        List<ScanFilter> filters = new ArrayList<>();
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();
        mScanResults = new HashMap<>();
        mScanCallback = new BtleScanCallback();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
        mScanning = true;
        mScanning = true;
        mHandler = new Handler();
        mHandler.postDelayed(this::stopScan, SCAN_PERIOD);
    }

    private void stopScan() {
        Log.d(TAG, "Stop Scan");
        if (mScanning && mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && mBluetoothLeScanner != null) {
            mBluetoothLeScanner.stopScan(mScanCallback);
            scanComplete();
        }

        mScanCallback = null;
        mScanning = false;
        mHandler = null;
    }

    private void scanComplete() {
        if (mScanResults.isEmpty()) {
            return;
        }
        for (String deviceAddress : mScanResults.keySet()) {
            Log.d(TAG, "Found device: " + deviceAddress);
        }
        Snackbar.make(findViewById(R.id.fab), "Scan Complete", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private boolean hasPermissions() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            requestBluetoothEnable();
            return false;
        } else if (!hasLocationPermissions()) {
            requestLocationPermission();
            return false;
        }
        return true;
    }

    private void requestBluetoothEnable() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        Log.d(TAG, "Requested user enables Bluetooth. Try starting the scan again.");
    }

    private boolean hasLocationPermissions() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
    }

    private class BtleScanCallback extends ScanCallback {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            addScanResult(result);
        }
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                addScanResult(result);
            }
        }
        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "BLE Scan Failed with code " + errorCode);
        }
        private void addScanResult(ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String deviceAddress = device.getAddress();
            if (mScanResults.containsKey(deviceAddress)) {
                Log.e(TAG, "BLE Scan result already added: " + deviceAddress);
                return;
            }
            mScanResults.put(deviceAddress, device);
            Log.e(TAG, "BLE Scan result added: " + deviceAddress);

            if  (device.getName() != null){
            adapter.insert(device.getName() + "\n" + deviceAddress, adapter.getCount());
            adapter.notifyDataSetChanged();
            mScanUUIDs.add(deviceAddress);
            Log.e(TAG, "ECG Device added: " + deviceAddress);

            }

        }
    };


}
