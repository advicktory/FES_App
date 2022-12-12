package com.example.fes_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.bluetooth.*;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("FES App");

        int permissionCheck = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Y tf isnt the permission granted", Toast.LENGTH_SHORT).show();
        }
    }

    public void launchBTSettingsActivity(View v) {
        Intent i = new Intent(this, BTSettingsActivity.class);
        startActivity(i);
    }

    public void launchBoardControlActivity(View v) {
        Intent i = new Intent(this, BoardControlsActivity.class);
        startActivity(i);
    }
}
