package com.elec390.teamb.ecg;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.UUID;

import static android.content.Context.BLUETOOTH_SERVICE;

public class ECGDataCollectionService {

    String TAG = "ECGDataCollectionService";
    private Context context;
    //private List<short> waveformDataBuffer;
    BluetoothManager mBluetoothManager;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    BluetoothGattServer mGattServer;
    GattServerCallback gattServerCallback;

    //Gatt Values
    public static String UUID_HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String UUID_CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String UUID_HEART_RATE_SERVICE = "0000180d-0000-1000-8000-00805f9b34fb";


    public boolean ECGDataCollectionService(Context context) {
        this.context = context;
        mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (!mBluetoothAdapter.isMultipleAdvertisementSupported()) {
            Log.d(TAG, "Advertising Fail");
            return false;
        }
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        mGattServer = mBluetoothManager.openGattServer(context, gattServerCallback);
        setupServer();
        startAdvertising();
        return true;
    }
}
    private void startAdvertising() {
        if (mBluetoothLeAdvertiser == null) {
            return;
        }
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                .build();
    }


    private void setupServer() {
        BluetoothGattService service = new BluetoothGattService(UUID.fromString(UUID_HEART_RATE_SERVICE),
                BluetoothGattService.SERVICE_TYPE_PRIMARY);
        mGattServer.addService(service);
        }

    private class GattServerCallback extends BluetoothGattServerCallback {};
}


