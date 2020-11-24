/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.endeavor.walter.getout9;


import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.endeavor.walter.getout9.MainActivity.GetOutURL;
import static com.endeavor.walter.getout9.WVSInfo.ZOOM_1;
import static com.endeavor.walter.getout9.WVSInfo.llChateau;
import static com.endeavor.walter.getout9.WVS_Utils.getstrCurrentYear;

class Utils {

    static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates";
    static final String KEY_CURRENT_PATHNAME = "current_pathname";
    static final String KEY_CURRENT_PATHNAME_ID = "current_pathnameid";
    static final String KEY_ZOOM_LEVEL = "zoomlevel";
    static final String KEY_ROUTE_CAPTION = "routecaption";
    static final String KEY_ROUTE_ACTIVITY_OFFSET = "routeactivityoffset";
    static final String KEY_CURRENT_LOGIN = "current_login";
    static final String KEY_CURRENT_LOGINID = "current_loginid";
    static final String KEY_CURRENT_PAUSEDSTATE = "paused_state";
    static final String KEY_CURRENT_TABNUMBER = "current_tab";
    static final String KEY_GETOUT_URL = "GetOut_url";
    static final String KEY_CURRENT_PATH_HASTIMEELAPSED = "current_path_hastimeelapsed";
    private String TAG = Utils.class.getSimpleName();
    static final String KEY_ROUTE_GEOFENCE_TIMEOUT = "GeoFenceTimeOut";
    static final String KEY_DOWNLOAD_COMPLETE = "download_complete";
    static final Boolean bLOGCAT = false;
    static final Boolean bLOGLOCALFILE = true;

    static  float getZoomLevel(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getFloat(KEY_ZOOM_LEVEL, ZOOM_1);
    }
    static void setZoomLevel(Context context, float zoom){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putFloat(KEY_ZOOM_LEVEL,zoom).apply();
    }

    static  String getRouteName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_ROUTE_CAPTION,"UNKNOWN");
    }
    static void setRouteName(Context context, String routename){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_ROUTE_CAPTION,routename).apply();
    }

    static  String getCurrentPathName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_CURRENT_PATHNAME,"UNKNOWN");
    }
    static void setCurrentPathName(Context context, String currentpathname){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_CURRENT_PATHNAME,currentpathname).apply();
    }
    static  long getCurrentPathNameID(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(KEY_CURRENT_PATHNAME_ID,0);
    }
    static void setCurrentPathNameId(Context context, long currentpathnameid){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(KEY_CURRENT_PATHNAME_ID,currentpathnameid).apply();
    }

    static int getCurrentLoginID(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_CURRENT_LOGINID,0);

    }
    static void setCurrentLoginId(Context context, int currentloginid){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(KEY_CURRENT_LOGINID,currentloginid).apply();
    }

    static  String getCurrentLogin(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_CURRENT_LOGIN,MainActivity.applogins[0]);
    }
    static void setCurrentLogin(Context context, String currentLogin){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_CURRENT_LOGIN,currentLogin).apply();
    }

    static String getCurrentTitleUser(Context context){
        String sTitle = context.getResources().getString(R.string.app_name) + ": " + Utils.getCurrentLogin(context);
        return sTitle;
    }
    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The {@link Context}.
     */
    static boolean getRequestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

    /**
     * Stores the location updates state in SharedPreferences.
     * @param requestingLocationUpdates The location updates state.
     */
    static void setRequestingLocationUpdates(Context context, boolean requestingLocationUpdates) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
                .apply();
    }


    static boolean getPausedState(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_CURRENT_PAUSEDSTATE, false);
    }

    static void setPausedState(Context context, boolean bpaused){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean( KEY_CURRENT_PAUSEDSTATE, bpaused)
                .apply();
    }

    static int getTabNumber(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(KEY_CURRENT_TABNUMBER , 0);
    }

    static void setTabNumber(Context context, int tabnumber){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt( KEY_CURRENT_TABNUMBER, tabnumber)
                .apply();
    }

//**************************************************************************************************
    /**
     * Returns the {@code location} object as a human readable string.
     * @param location  The {@link Location}.
     */
    static String getLocation_Text(Location location) {
        return location == null ? "Unknown location" :
                "(" + location.getLatitude() + ", " + location.getLongitude() + ")";
    }

    static String getLocationUpdated_msg(Context context) {
        return context.getString(R.string.location_updated,
                DateFormat.getDateTimeInstance().format(new Date()));
    }

    static  String getRouteAcivityOffset(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_ROUTE_ACTIVITY_OFFSET,"4.00");
    }
    static void setRouteAcivityOffset(Context context, String routeactivityoffset){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_ROUTE_ACTIVITY_OFFSET, routeactivityoffset).apply();
    }

    static String getRouteActivityOffset_msg(Context context) {
        return context.getString(R.string.RouteActivityOffset,
                getRouteAcivityOffset(context) );
    }

    static String getRouteActivityGeoFenceTimeOut_msg(Context context) {
        return context.getString(R.string.RouteActivityGeoFenceTimeOut ,
                getRouteAcivityGeoFenceTimeOut(context) );
    }

    static String getShareHealthCurrentYear_msg(Context context) {
        return context.getString(R.string.menu_sharehealth, getstrCurrentYear() );
    }

    static String getUpdatetblHealthCurrentYear_msg(Context context) {
        return context.getString(R.string.menu_updatetblhealth, getstrCurrentYear() );
    }

    static int getLocation_Color(Location location){
//        default color is BLACK
        int tmpColor = Color.BLACK;
        if (location.getExtras() != null && location.getExtras().containsKey("pathcolor")){
            tmpColor = location.getExtras().getInt("pathcolor");
        }
        return tmpColor;
    }

    static void setLocation_Color(Location location, int tmpColor){
//        if (location.getExtras() != null && location.getExtras().containsKey("pathcolor")){
            Bundle bundle = new Bundle();
            bundle.putInt("pathcolor", tmpColor);
            location.setExtras(bundle);
//        }
    }

    static long getLocation_LocationsId(Location location){
//        default color is BLACK
        long tmpLocationsId = 0;
        if (location.getExtras() != null && location.getExtras().containsKey("locationsid")){
            tmpLocationsId= location.getExtras().getLong("locationsid");
        }
        return tmpLocationsId;
    }

    static void setLocation_Extra_LocationsId(Location location, long locationsid){
//        if (location.getExtras() != null && location.getExtras().containsKey("locationsid")){
            Bundle bundle = new Bundle();
            bundle.putLong("locationsid", locationsid);
            location.setExtras(bundle);
//        }
    }

    static  String getGetOutURL(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_GETOUT_URL, GetOutURL);
    }
    static void setGetOutURL(Context context, String url){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_GETOUT_URL,url).apply();
    }

    static void setCurrentPath_HitGeoFence(Context context, boolean bTimeElapsed){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(KEY_CURRENT_PATH_HASTIMEELAPSED,bTimeElapsed).apply();
    }
    static  boolean getCurrentPath_HitGeoFence(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_CURRENT_PATH_HASTIMEELAPSED,false);
    }

    static void WithinGeoTarget(Context context, Long locationsid, Location location, LocationRepo lr){
//      wvs todo: pass in geo target, menu change timeOutMin
        float[] fDistance = new float[1];
        if (!Utils.getCurrentPath_HitGeoFence(context)) {
//          wvs todo check, if time has elapse then check if close to "geofence" default hom
            int timeOutMin = Integer.parseInt( Utils.getRouteAcivityGeoFenceTimeOut(context));
            if (lr.getTimeDifferenceLocationsId(locationsid) >= timeOutMin){
                Log.i("Utils.ReturnedHome", " WVS time elapsed BEGIN...");
//              if Location.distanceBetween(location and geofence) < 50 meters then sound alarm/notification;
                LocationsRepo lsr = new LocationsRepo(context);
                String sNameLatLngEnabled = lsr.getGeoLocations();
                if (!sNameLatLngEnabled.equals("")) {
                    String [] arrNameLatLngs;
                    Double lat, lng;
                    Integer enabled, within;
                    String name;
                    arrNameLatLngs = sNameLatLngEnabled.split(",");
                    for (int i = 0; i < arrNameLatLngs.length; i++) {
                        name = arrNameLatLngs[i];
                        lat = Double.valueOf(arrNameLatLngs[i + 1]);
                        lng = Double.valueOf(arrNameLatLngs[i + 2]);
                        enabled = Integer.valueOf(arrNameLatLngs[i + 3]);
                        within = Integer.valueOf(arrNameLatLngs[i + 4]);
                        if (enabled.equals(1) && within.equals(0)) {
                            Location.distanceBetween(location.getLatitude(), location.getLongitude(), lat, lng, fDistance);
                            if (fDistance[0] < 50f) {
                                lsr.updateGEOLocation(name, enabled, 1);
                                RingtoneSound(context);
                            }
                        }
                        i = i + 3;
                    }
                }
                Location.distanceBetween(location.getLatitude(),location.getLongitude(), llChateau.latitude, llChateau.longitude, fDistance);
                if (fDistance[0] < 50f){
                    Log.i("Utils.WithinGeoTarget", " WVS distance < 50 meters...");
                    Utils.setCurrentPath_HitGeoFence(context,true);
                    RingtoneSound(context);
                }
            }
        }
    }

    public static void RingtoneSound(Context context){
        Uri alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(alertUri == null){
            // alert is null, using backup
            alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            // I can't see this ever being null (as always have a default notification)
            // but just incase
            if(alertUri == null) {
                // alert backup is null, using 2nd backup
                alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        Ringtone rngtone = RingtoneManager.getRingtone(context, alertUri);
//                  https://www.programcreek.com/java-api-examples/index.php?api=android.media.Ringtone
        while (rngtone != null){
            for (int i = 0; i <= 2; i++){
                rngtone.play();
                Log.i("Utils.ReturnedHome", " WVS ringtone playing..." + String.valueOf(i));
                long timeout = 1000;
                while(rngtone.isPlaying() && timeout> 0 ) {
                    timeout = timeout - 100;  //timeout/100=10 sec
                    try {
                        Thread.sleep(1000);  //1 sec
                    } catch (InterruptedException e){
                        Thread.currentThread().interrupt();
                    }
                }
                Log.i("Utils.ReturnedHome", " WVS ringtone stopping...");
                rngtone.stop();
            }
            rngtone = null;
        }
//                    rngtone.stop();
    }
    static  String getRouteAcivityGeoFenceTimeOut(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_ROUTE_GEOFENCE_TIMEOUT,"10");
    }
    static void setRouteAcivityGeoFenceTimeOut(Context context, String routeactivityoffset){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_ROUTE_GEOFENCE_TIMEOUT, routeactivityoffset).apply();
    }

    public static String SumTotalTimes(String csvTotalTimes){
        String totalTime = "";  //00:02:12,00:06:54,00:03:30,00:23:49,00:03:00
        String [] tmptotalTime = csvTotalTimes.split(",");
        Integer iHr,iMin,iSec,tmpHr,tmpMin,tmpSec;
        iHr=iMin=iSec=tmpHr=tmpMin=tmpSec=0;

        for (int i = 0; i < tmptotalTime.length; i++){
            iHr = iHr + Integer.parseInt(tmptotalTime[i].split(":")[0]);
            iMin = iMin + Integer.parseInt(tmptotalTime[i].split(":")[1]);
            iSec = iSec + Integer.parseInt(tmptotalTime[i].split(":")[2]);
        }
        tmpSec = iSec % 60;
        tmpMin = iMin + iSec / 60;
        tmpHr = iHr + tmpMin / 60;
        totalTime = String.format("%02d",tmpHr) + ":" + String.format("%02d",tmpMin) + ":" + String.format("%02d",tmpSec);
        Log.i("Utils.SumTotalTimes ", " WVS " + csvTotalTimes + " => " + totalTime);
        return totalTime;
    }
    public boolean getDownloadComplete(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_DOWNLOAD_COMPLETE,false);
    }
    public void setDownloadComplete(Context context, boolean bFlag){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(KEY_DOWNLOAD_COMPLETE,bFlag).apply();
    }

    public static void appLog(String tag, String msg) {
        if (bLOGCAT) {
            Log.d(tag, msg);
        }
        if (bLOGLOCALFILE) {
            try {
                writeappLog(tag, msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeappLog(String tag, String msg) throws IOException {

        String filedatepart = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String stime = new SimpleDateFormat("yyyyMMdd HH:mm:ss ").format(new Date());
        String slogfile = "GetOut_" + filedatepart + ".log";
        File logFile = null;
        try {
            String state = Environment.getExternalStorageState();
            String pathToExtStorage = Environment.getExternalStorageDirectory().toString();
            File sd = new File(pathToExtStorage);
            FileOutputStream fOut;
            if (Environment.MEDIA_MOUNTED.equals(state)) {
//                Environment.MEDIA_MOUNTED.equals(state) || removed
//                if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                    if (sd.canWrite()) {
                        logFile = new File(sd, slogfile);
                        if (logFile.exists()) {
                            fOut = new FileOutputStream(logFile, true);
                        } else {
                            fOut = new FileOutputStream(logFile);
                        }
                        OutputStreamWriter myOutWriter_logfile = new OutputStreamWriter(fOut);
                        myOutWriter_logfile.append(stime + tag + msg +"\n");
                        myOutWriter_logfile.close();
                        fOut.close();
                    }
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{

        }

    }

}
