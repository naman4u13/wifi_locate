package com.example.hp.wifi_locate;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyBroadcastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //List<ScanResult> wireless = myWifiManager.getScanResults();
        new Task(context).execute(intent);

    }

    private class Task extends AsyncTask<Intent, Void, String> {

        TextView view;
        TextView mac;
        TextView result;
        Context context;
        Activity activity;

        Task(Context context) {
            this.context = context;
            activity = (Activity) context;
            result = activity.findViewById(R.id.RESULT);
            mac = activity.findViewById(R.id.MAC);
        }

        @Override
        protected String doInBackground(Intent... params) {
            StringBuilder sb = new StringBuilder();
            String action = params[0].getAction();
            if (action.equals("android.net.wifi.p2p.THIS_DEVICE_CHANGED")) {
                view = mac;
                Bundle extras = params[0].getExtras();
                try {
                    if (extras != null) {
                        Set<String> keySet = extras.keySet();
                        for (String key : keySet) {
                            try {

                                String str = params[0].getExtras().get(key).toString();
                                int i = str.indexOf("deviceAddress");
                                int j = str.indexOf("interface");
                                sb.append(str.substring(i, j));

                            } catch (Exception e) {

                            }
                        }
                    }
                } catch (Exception e) {

                }
            } else {

                WifiManager myWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                ArrayList<ScanResult> wireless = (ArrayList<ScanResult>) myWifiManager.getScanResults();
                for (ScanResult res : wireless) {
                    sb.append("\n\n SSID : " + res.SSID + "\n BSSID - " + res.BSSID);
                }
                view = result;
            }
            return String.valueOf(sb);
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            view.setText(s);

        }


    }
}
