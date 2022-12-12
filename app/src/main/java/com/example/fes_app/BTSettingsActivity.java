package com.example.fes_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class BTSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btsettings);
        setTitle("Bluetooth Settings");
    }

    public void toggleBTEnable(View v){
        //TODO: Figure out how to enable BT if disabled
        //For now, make a message if BT is disabled to enable it from system menu

        Toast.makeText(this, "Please exit the app and turn on Bluetooth from your systems settings.", Toast.LENGTH_SHORT).show();
    }

    public void scanBTDevices(){
        //TODO: SCAN FOR AVAILABLE DEVICES
    }
}