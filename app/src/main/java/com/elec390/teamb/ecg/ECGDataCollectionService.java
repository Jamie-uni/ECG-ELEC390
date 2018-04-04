package com.elec390.teamb.ecg;

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

import static android.content.Context.BLUETOOTH_SERVICE;

public class ECGDataCollectionService {

    //private List<short> waveformDataBuffer;
    BluetoothManager mBluetoothManager;
    BluetoothAdapter mBluetoothAdapter;

    ECGDataCollectionService(Context context) {

       // mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
       // mBluetoothAdapter = mBluetoothManager.getAdapter();

    }



}
