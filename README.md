
 

# wifi_locate
## Table Of Contents
1. [Description](#description)
2. [Getting Started](#getting-started)
   * [Prerequisites](#prerequisites)
   * [Installation](#installation-setting-up-project)
3. [Exploiting Sensitive Data Exposure via WiFi Broadcasts in Android OS](#Exploiting Sensitive Data Exposure via WiFi Broadcasts in Android OS)
    * [Search and Fetch Symptom](#search-and-fetch-symptom) 
    * [Fetching Auth Token](#fetching-auth-token)
    * [ Working with Volley Library](#working-with-volley-library)
4. [ API 3](#api-3)
    * [Web Scraping for a given Diagnosis](#web-scraping-for-a-given-diagnosis)
    * [Use of Firebase Relatime Database](#use-of-firebase-relatime-database)
5. [API 5](#api-5)
   

## Description
The application provide user's location without the need of accessing GPS.It works upon the shortcomings of Android OS Leaking Sensitive 
Data and a comprehensive database of WLANs and their exact location which is provided by website project [OpenWifi.su](https://OpenWifi.su).



 ## Getting Started
> Following instructions will get you a copy of the project up and running on your local machine
****
### Prerequisites
  * You need to have Android Studio installed on your system.[Download Link](https://developer.android.com/studio/)
  * You need to have an Android Device with Debugger enabled.
  

 ### Installation (setting up project)
  * Download the zip file and extract it.
  * Start Android Studio and open extracted folder/zip file.
  * Sync the project and then press "run app" on top right screen of Android Studio. 
  
 
## Exploiting Sensitive Data Exposure via WiFi Broadcasts in Android OS 
 System broadcasts by Android OS expose information about the user’s device to all applications running on the device.This includes
 the WiFi network name, BSSID, local IP addresses, DNS server information and the MAC address. Some of this information (MAC address)
 is no longer available via APIs on Android 6 and higher, and extra permissions are normally required to access the rest of this 
 information. However, by listening to these broadcasts, any application on the device can capture this information thus bypassing 
 any permission checks and existing mitigations.

 Because MAC addresses do not change and are tied to hardware, this can be used to uniquely identify and track any Android device 
 even when MAC address randomization is used. The network name and BSSID can be used to geolocate users via a lookup against a 
 database of BSSID such as WiGLE or SkyHook or OpenWifi.su. 
 
  
 ## Targetting Android SDK < 23 to bypass location permission and obtain WifiManager.getScanResults()    
  Starting from Android 6(API 23) apps requires location permission for WifiManager.getScanResults().However, apps targeting 
  pre-Android-6 API aren't checked for this permission at all and can still access this privacy-related information which indirectly 
  reveals user's location. This way they can bypass the check forever - even in the latest Android O beta it is still possible to call
  WifiManager.getScanResults() without any permission check if the app defines target SDK 22 or less in its manifest.
   
   
   ```
      android {
    compileSdkVersion 27
    defaultConfig {
        applicationId "com.example.hp.wifi_locate"
        minSdkVersion 22
        targetSdkVersion 22
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
  ```
  Using the scan result we can extract SSID(the network name) and BSSID(the MAC address of AP) of Access Points to further fetch 
  Location from online databases.
 
```
 
  WifiManager myWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                ArrayList<ScanResult> wireless = (ArrayList<ScanResult>) myWifiManager.getScanResults();
                for (ScanResult res : wireless) {
                    sb.append("\n\n SSID : " + res.SSID + "\n BSSID - " + res.BSSID);
                    String BSSID = (res.BSSID.replace(":",""));
                    URL.append(BSSID+",");
                }
```
   
   

<img src="https://github.com/naman4u13/medic/blob/master/Img/Screenshot%20(6).png" alt="image" height="200px" width="300px" align="left">
 <img src="https://github.com/naman4u13/medic/blob/master/Img/Screenshot_2018-10-17-21-04-42.png" alt="image" height="300px" width="200px" >

## Using OpenWifi.su API to fetch GPS coordinates
 OpenWifi.su is a website for collecting information about the different wireless hotspots around the world. Users can upload 
 hotspot data like GPS coordinates, SSID and MAC address(BSSID).Once entered in database, it can be then later used as a lookup
 table to fetch gps coordinates using just BSSID. This act of searching and collecting Wi-Fi wireless networks data is called War-driving
 or War-cycling or War-walking depending on the mode of transportation.

OpenWifi.su provides API to query for location coordinates using a list of BSSIDs.
```
 location = extractFeatureFromJson(makeHTTpRequest( new URL("http://openwifi.su/api/v1/bssids/"+URL.toString()))) + "\n\n";
```
The response is in JSON format from which Longitude and Lattitude are extracted and parsed
```
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
```
