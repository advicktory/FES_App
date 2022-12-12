package com.example.fes_app;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class BoardControlsActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 0;
    public int i = 0;
    public TextView t;
    public TextView t2;
    public TextView t3;
    public Button b_GO;
    public Button b_BREAK;
    public BluetoothManager bluetoothManager;
    public BluetoothAdapter bluetoothAdapter = null;
    public BluetoothSocket bluetoothSocket = null;
    public OutputStream outStream;

    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_controls);
        setTitle("Board Controls");

        /* Settin things up:*/
        //---------------------------------------
        t = findViewById(R.id.textView2);
        t2 = findViewById(R.id.textView456);
        t3 = findViewById(R.id.textView3);
        b_GO = findViewById(R.id.button_GO);
        b_BREAK = findViewById(R.id.button_BREAK);
        //---------------------------------------

        /*AT SOME POINT THE LINES BELOW MAY NEED TO GO INTO EITHER MAIN OR BT SETTINGS*/
        //ENABLING BLUETOOTH HOPEFULLY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i("first if", "here");
            bluetoothManager = getSystemService(BluetoothManager.class);
            bluetoothAdapter = bluetoothManager.getAdapter();
        } else {
            // This should be the case if the code was written in such a way as to not support the correct Android API
            Toast.makeText(this, "This app will not work on your phone b/c it's to old.", Toast.LENGTH_SHORT).show();
            // TODO: IF THIS OCCURS THEN THERE NEEDS TO BE A WAY TO NOT CONTINUE IN THE CODE
        } // if bluetooth isnt available.


        if (!bluetoothAdapter.isEnabled()) {
            //Toast.makeText(this, "BT not enabled, enabling.", Toast.LENGTH_SHORT).show();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                startBTResult.launch(enableBtIntent);
            } else {
                //Toast.makeText(this, "B/c Android dumb, I need to ask you to allow this permission.", Toast.LENGTH_SHORT).show();
                requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
            }
        } //  enable system BT if not enabled & request permission to connect if not allowed.

//        if(!bluetoothAdapter.isDiscovering() && bluetoothAdapter.isEnabled()){
//            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){
//
//                Intent startDiscBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//                discBTResult.launch(startDiscBTIntent);
//
//                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//                registerReceiver(receiver, filter);
//
//            } else {
//                //Toast.makeText(this, "B/c Android dumb, I need to ask you to allow this permission.", Toast.LENGTH_SHORT).show();
//                requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
//            }
//        } // Discovering stuff

        t.setText("Bluetooth is: " + bluetoothAdapter.isEnabled());
        t2.setText("Bluetooth discoverability is: " + bluetoothAdapter.isDiscovering());

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        BluetoothDevice HC_05_MODULE = null;
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                HC_05_MODULE = device;
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if (Objects.equals(deviceHardwareAddress, "98:D3:41:F6:78:25")) {
                    t3.setText("Device Name: " + deviceName + "\nDevice Addy: " + deviceHardwareAddress);
                    Log.i("DeviceName", "Device name: " + deviceName + " Device MAC: " + deviceHardwareAddress);
                }
            }
        } else {
            t3.setText("No paired Devices");
        }

        try {
            bluetoothSocket = HC_05_MODULE.createRfcommSocketToServiceRecord(mUUID);
            bluetoothSocket.connect();
            Log.i("Connected?", "Bluetooth is Connected: " + bluetoothSocket.isConnected());
        } catch (IOException e) {
            e.printStackTrace();
        }
//
        try {
            outStream = bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        b_GO.setOnTouchListener(new RepeatListener(40, 20, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i < 180) i++;
                try {
                    outStream.write(i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("b_GO Action Down", "i: " + i);
            }
        }));

        b_BREAK.setOnTouchListener(new RepeatListener(40, 20, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i > 0) i--;
                try {
                    outStream.write(i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("b_Break Action Down", "i: " + i);
            }
        }));


    } // End of onCreate

    @Override
    protected void onStop() {
        super.onStop();
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Toast.makeText(BoardControlsActivity.this, "You need BT Lugnut", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_OK){
            Log.i("onActivityResult", "It worked!");
        }
        else{
            Log.i("onActivityResult", "shit");
        }
    }

//    private final BroadcastReceiver receiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                // Discovery has found a device. Get the BluetoothDevice
//                // object and its info from the Intent.
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                String deviceName = device.getName();
//                String deviceHardwareAddress = device.getAddress(); // MAC address
//            }
//        }
//    };

    ActivityResultLauncher<Intent> startBTResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //Log.i("onActivityResult", "The Result: " + result.toString());
                    if(result.getResultCode() == RESULT_OK){
                        Log.i("ActivityResult startBT", "The Result: " + result.toString());
                        Toast.makeText(BoardControlsActivity.this, "Bluetooth should be enabled", Toast.LENGTH_SHORT).show();
                    }
                    else if (result.getResultCode() == RESULT_CANCELED){
                        Log.i("ActivityResult_startBT", "The Result: " + result.toString());
                        Toast.makeText(BoardControlsActivity.this, "Something happened couldn't start BT", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    ); // Used to call to enable bluetooth

//    ActivityResultLauncher<Intent> discBTResult = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    Log.i("ActivityResult_discBT", "The Result: " + result.toString());
//                }
//            }
//    ); // Used to call to enable bluetooth



}