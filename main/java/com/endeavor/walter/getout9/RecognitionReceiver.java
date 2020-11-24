package com.endeavor.walter.getout9;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

//import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.endeavor.walter.getout9.WVS_Utils.getDateTimeString_standard;

public class RecognitionReceiver extends BroadcastReceiver {
    private Context mContext;
    private static final String TAG = RecognitionReceiver.class.getSimpleName();
    public static final String ACTION_PROCESS_TRANSITION_UPDATES ="com.google.android.gms.location.sample.locationupdatesforegroundservice.action.PROCESS_TRANSITION_UPDATES";


    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        Log.i(TAG," WVS onReceive()");
        if (!Utils.getPausedState(context)) {
            if (serviceIsRunningInForeground(context)) {
                Log.i(TAG, " WVS onReceive() LOCATION SRV IS RUNNING, should insert into database");
// ***************** ADD current activity to DB *******************************************
                final String action = intent.getAction();
                if (ACTION_PROCESS_TRANSITION_UPDATES.equals(action)) {
                    if (ActivityTransitionResult.hasResult(intent)) {
                        ActivityTransitionResult tmpTransEvents = ActivityTransitionResult.extractResult(intent);
                        if (tmpTransEvents != null) {
                            Log.i(TAG, " WVS MapsActivity.mReceiver.onReceive latest TRANSITION result: " + tmpTransEvents.getTransitionEvents().get(tmpTransEvents.getTransitionEvents().size() - 1).toString());
                            for (ActivityTransitionEvent event : tmpTransEvents.getTransitionEvents()) {
                                Log.i(TAG, " WVS EACH TRANSITION:" + TransitionRecognitionUtils.createTranstionString(event));
                                ActivitiesRepo ar = new ActivitiesRepo(context);

                                Activities tmpActivity = new Activities(event.getActivityType(), event.getTransitionType(), event.getElapsedRealTimeNanos());
                                //      getActivityType, getTransitionType, getElapsedRealTimeNanos

                                //      get current locationsid
                                long locationsid = Utils.getCurrentPathNameID(context);
                                tmpActivity.setLocationSId(locationsid);
//                                String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                                String datetime = getDateTimeString_standard();
                                tmpActivity.setDateTime(datetime);
                                tmpActivity.setDescription("");
//
                                long activitiesid = ar.insert(tmpActivity);
                            }
//                processTransitionResult(result);

                        } else {
                            Log.i(TAG, " WVS RecognitionReceiver ActivityRecognitionResult = NULL");
                        }
                    }
                }
            } else {
                Log.i(TAG, " WVS onReceive() LOCATION SRV IS NOT RUNNING, will broadcast to MainActivity.onReceive.");
                final String action = intent.getAction();
                if (ACTION_PROCESS_TRANSITION_UPDATES.equals(action)) {
                    if (ActivityTransitionResult.hasResult(intent)) {
                        ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
                        if (result != null) {
                            Intent i = new Intent(new MainActivity().ACTION_SHOW_TRANS);
                            i.putExtra("success", true);
                            i.putExtra("activitytransitionresult", result);
                            LocalBroadcastManager
                                    .getInstance(context)
                                    .sendBroadcast(i);
                            Log.i(TAG, " WVS onReceive() broadcast TRANSITIONS back to MainActivity..." + result.toString());
//                processTransitionResult(result);

                        } else {
                            Log.i(TAG, " WVS RecognitionReceiver ActivityRecognitionResult = NULL");
                        }
                    }
                }
            }
        }
    }

    protected boolean isAppRunning(Context context){
        String longactivity = MainActivity.class.getName();
        String simpleactivity = MainActivity.class.getSimpleName();
        String activity = longactivity.substring(0,longactivity.length() - simpleactivity.length() - 1);
        String tmpActivity;

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> tasks = activityManager.getRunningAppProcesses();
        Log.i(TAG, " WVS isAppRunning() MainActivity task name = " + activity);
        for (int i = 0; i < tasks.size() ; i++ ){
            tmpActivity = tasks.get(i).processName;
            Log.i(TAG, " WVS isAppRunning() " + tasks.get(i).pid + "  " + tasks.get(i).uid);

            Log.i(TAG, " WVS isAppRunning() Running app task name = " + tmpActivity);
            if (activity.equals(tmpActivity)) {
                return true;
            }
        }
        return false;
    }

    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            Log.i(TAG, " WVS TRANSACTION getRunningServices = " +service.service.getClassName());
            if (LocationUpdatesService.class.getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }
}
