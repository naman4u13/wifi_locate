package com.example.hp.wifi_locate;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView result;
    Button scan;
    boolean wasEnabled;
    WifiManager myWifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result = findViewById(R.id.RESULT);
        scan = findViewById(R.id.SCAN);
        IntentFilter i = new IntentFilter();
        i.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        i.addAction("android.net.wifi.p2p.THIS_DEVICE_CHANGED");
        registerReceiver(new MyBroadcastReciever(), i);
        result.setMovementMethod(new ScrollingMovementMethod());
        myWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wasEnabled = myWifiManager.isWifiEnabled();
        myWifiManager.setWifiEnabled(true);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myWifiManager.isWifiEnabled()) {
                    myWifiManager.startScan();
                }
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            myWifiManager.setWifiEnabled(wasEnabled);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}
