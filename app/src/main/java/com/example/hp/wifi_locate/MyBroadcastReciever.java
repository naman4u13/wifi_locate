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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
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
            String location = "";
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
                StringBuilder URL = new StringBuilder();

                WifiManager myWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                ArrayList<ScanResult> wireless = (ArrayList<ScanResult>) myWifiManager.getScanResults();
                for (ScanResult res : wireless) {
                    sb.append("\n\n SSID : " + res.SSID + "\n BSSID - " + res.BSSID);
                    String BSSID = (res.BSSID.replace(":",""));
                    URL.append(BSSID+",");
                }
                if (URL.length() > 0) {
                    URL.setLength(URL.length() - 1);
                }
                view = result;
                try {
                   location = extractFeatureFromJson(makeHTTpRequest( new URL("http://openwifi.su/api/v1/bssids/"+URL.toString()))) + "\n\n";
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            }

            return (location + String.valueOf(sb));
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            view.setText(s);

        }


    }

    private String makeHTTpRequest(URL url) {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(15000);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);

        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonResponse;
    }

    private String readFromStream(InputStream in) {
        StringBuilder output = new StringBuilder();
        if (in != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(in, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            try {
                String line = bufferedReader.readLine();
                while (line != null) {
                    output.append(line);
                    line = bufferedReader.readLine();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return output.toString();
    }

    private String extractFeatureFromJson(String jsonresp) {
        String loc = null;
        try {
            JSONObject baseJSONresp = new JSONObject(jsonresp);
            loc ="Longitude :" +baseJSONresp.getString("lon")+"   Latitude :" + baseJSONresp.getString("lat");

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

    return loc;
    }

}
