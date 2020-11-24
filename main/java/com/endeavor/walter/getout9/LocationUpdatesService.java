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

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
//import android.support.annotation.NonNull;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

//import androidx.annotation.NonNull;
//import androidx.core.app.NotificationCompat;
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static com.endeavor.walter.getout9.Utils.appLog;
import static com.endeavor.walter.getout9.WVS_Utils.DisplayExtras;


/**
 * A bound and started service that is promoted to a foreground service when location updates have
 * been requested and all clients unbind.
 *
 * For apps running in the background on "O" devices, location is computed only once every 10
 * minutes and delivered batched every 30 minutes. This restriction applies even to apps
 * targeting "N" or lower which are run on "O" devices.
 *
 * This sample show how to use a long-running service for location updates. When an activity is
 * bound to this service, frequent location updates are permitted. When the activity is removed
 * from the foreground, the service promotes itself to a foreground service, and location updates
 * continue. When the activity comes back to the foreground, the foreground service stops, and the
 * notification assocaited with that service is removed.
 */
public class LocationUpdatesService extends Service {

    private static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationupdatesforegroundservice";

    private static final String TAG = LocationUpdatesService.class.getSimpleName();

    /**
     * The name of the channel for notifications.
     */
    private static final String CHANNEL_ID = "channel_01";

    static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";

    static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";
    public static final String EXTRA_STARTED_FROM_NOTIFICATION_MainActivity = PACKAGE_NAME +
            ".started_from_notification_MainActivity";

    private final IBinder mBinder = new LocalBinder();

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private static final int NOTIFICATION_ID = 12345678;

    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private boolean mChangingConfiguration = false;

    private NotificationManager mNotificationManager;

    /**
     * Contains parameters used by {@link com.google.android.gms.location.FusedLocationProviderApi}.
     */
    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;

    private Handler mServiceHandler;

    /**
     * The current location.
     */
    private Location mLocation;
    private boolean bFirstTimeNotify;
    public LocationUpdatesService() {
    }

    @Override
    public void onCreate() {
//      The system invokes this method to perform one-time setup procedures when the service is initially created (before it calls either onStartCommand() or onBind()).
//      If the service is already running, this method is not called.
        appLog(TAG, " WVS onCreate()");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        createLocationRequest();
        setupLocationListener();

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        appLog(TAG, " WVS onStartCommand()... Service started");
        DisplayExtras(intent);
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false);

        if (startedFromNotification) {
            // We got here because the user decided to remove location updates from the notification.
            removeLocationUpdates();
            stopSelf();
        }
        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        appLog(TAG, " WVS onBind() - MainActivity is now running, stopping ForegroundService");
        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        appLog(TAG, " WVS onRebind() - MainActivity is now running, stopping ForegroundService");
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        appLog(TAG, " WVS onUnbind() Last client unbound from service, promote to ForegroundService");

        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration && Utils.getRequestingLocationUpdates(this)) {
            appLog(TAG, " WVS onUnbind() Starting foreground service");
            startForeground(NOTIFICATION_ID, getNotification());
        }
        return true; // Ensures onRebind() is called when a client re-binds.
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null);
        bFirstTimeNotify= false;
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void requestLocationUpdates() {
        appLog(TAG, " WVS REQUESTING LOCATION UPDATES");
        Utils.setRequestingLocationUpdates(this, true);
//      When a service is started in this case from MainActivity.onOptionsItemSelected, it has a lifecycle that's independent of the component that started it.
        startService(new Intent(getApplicationContext(), LocationUpdatesService.class));
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates(this, false);
            appLog(TAG, " WVS Lost location permission. Could not request updates. " + unlikely);
        }
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void removeLocationUpdates() {
        appLog(TAG, " WVS REMOVING LOCATION UPDATES");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Utils.setRequestingLocationUpdates(this, false);
            stopSelf();
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates(this, true);
            appLog(TAG, " WVS Lost location permission. Could not remove updates. " + unlikely);
        }
    }

    /**
     * Returns the {@link NotificationCompat} used as part of the foreground service.
     */
    private Notification getNotification() {

        Intent serviceIntent = new Intent(this, LocationUpdatesService.class);
        CharSequence text = Utils.getLocation_Text(mLocation);
        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        serviceIntent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);
        // The PendingIntent that leads to a call to onStartCommand() in this service.
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, serviceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent mainactivityIntent = new Intent(this, MainActivity.class);
        mainactivityIntent.setAction("fromLocationUpdatesServiceNotification");
//      wvs CANNOT retrieve Extra in MainActivity????
        mainactivityIntent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION_MainActivity,true);
        // The PendingIntent to launch activity.
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                mainactivityIntent, 0);  //wvs previously last flag=0

//        new NotificationCompat.Builder(getApplicationContext(), "default")  vs. new NotificationCompat.Builder(this) old way
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .addAction(R.mipmap.ic_launcher, getString(R.string.launch_activity),
                        activityPendingIntent)
                .addAction(R.mipmap.ic_cancel, getString(R.string.remove_location_updates),
                        servicePendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(Utils.getLocationUpdated_msg(this))
                .setContentText(text)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setTicker(text);

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        return builder.build();
    }

    private void setupLocationListener() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                                appLog(TAG, " WVS ONE TIME setupLocationListener() " + mLocation.getLongitude() + "" + mLocation.getLatitude());
                            } else {
                                appLog(TAG, " WVS Failed ONE TIME setupLocationListener()");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            appLog(TAG, " WVS Lost location permission." + unlikely);
        }
    }

    private void onNewLocation(Location location) {
        appLog(TAG, " WVS onNewLocation: " + location);

        Bundle bundle = new Bundle();
        bundle.putInt("pathcolor", Color.BLUE);
        location.setExtras(bundle);
        mLocation = location;
        appLog(TAG, " WVS ACCURACY = " + location.getAccuracy());
//      Only add location and eventually transition if not paused...
        if (!Utils.getPausedState(this)) {
            //      ONLY DEAL WITH LOCATIONS WITHIN 30 METERS
            if (location.getAccuracy() <= 30) {
                // Notify anyone listening for broadcasts about the new location.
                Intent intent = new Intent(ACTION_BROADCAST);
                intent.putExtra(EXTRA_LOCATION, location);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                // Update notification content if running as a foreground service.
                if (serviceIsRunningInForeground(this)) {

// ***************** ADD current location to DB *******************************************
                    LocationRepo lr = new LocationRepo(this);
//      get current locationsid
                    long locationsid = Utils.getCurrentPathNameID(this);
//      add locationsid to location
                    Utils.setLocation_Extra_LocationsId(location, locationsid);
//      insert into tblLocation
                    long locationid = lr.insert(location);
//                    Utils.WithinGeoTarget(this, locationsid, location, lr);
//            if (mChangingConfiguration){
//                mNotificationManager.notify(NOTIFICATION_ID, getNotification());
//            }
//wvs only do this the first time in onUnbind
                    if (!bFirstTimeNotify) {
                        bFirstTimeNotify = true;
                        mNotificationManager.notify(NOTIFICATION_ID, getNotification());
                    }
                }
            }
        }
    }

    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        LocationUpdatesService getService() {
            return LocationUpdatesService.this;
        }
    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            appLog(TAG, " WVS getRunningServices = " +service.service.getClassName());
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }
}
