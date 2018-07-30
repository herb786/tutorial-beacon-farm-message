package com.hacaller.farmbeacon;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class BeaconActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    BluetoothLeScanner bluetoothLeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE NOT SUPPORTED", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 998);
        } else {
            initBleManager();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999 && resultCode == RESULT_OK) {
            bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            bluetoothLeScanner.startScan(myScanCallback);

        }
    }

    private void initBleManager(){
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 999);
        } else {
            bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            bluetoothLeScanner.startScan(myScanCallback);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 998 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initBleManager();
        }
    }

    ScanCallback myScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            StringBuilder builder = new StringBuilder();
            StringBuilder proxUUID = new StringBuilder();
            int i=0;
            for (byte b : result.getScanRecord().getBytes()) {
                if (i>8 && i<25){
                    proxUUID.append(String.format("%02X", b));
                }
                builder.append(String.format("%02X ", b));
                i++;
            }
            proxUUID.insert(20,"-");
            proxUUID.insert(16,"-");
            proxUUID.insert(12,"-");
            proxUUID.insert(8,"-");
            Log.d("LEBDATA", builder.toString());
            Log.d("LEBPROXUUID", proxUUID.toString());

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };


}
