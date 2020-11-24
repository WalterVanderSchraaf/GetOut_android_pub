package com.endeavor.walter.getout9;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
//import android.support.annotation.NonNull;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class Recognition {
    public static String TAG = Recognition.class.getSimpleName();
    private ActivityRecognitionClient mactivityRecognitionClient;
    private PendingIntent mtransRecognitionPendingIntent;
    private Context mContext;

    public void StartTracking (Context context){
        setupTransitions(context);
    }

    public void StopTracking (Context context){
        if (context != null && mtransRecognitionPendingIntent != null){
            Task task = mactivityRecognitionClient
                    .removeActivityTransitionUpdates(mtransRecognitionPendingIntent);
                    task.addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            mtransRecognitionPendingIntent.cancel();
                            Log.i(TAG, " WVS REMOVE ACTIVITY TRANSITION SUCCESS");
                        }
                    });

                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, " WVS REMOVE ACTIVITY TRANSITION FAILURE");
                        }
                    });

        }
    }
    public void setupTransitions(Context context){

        ArrayList<ActivityTransition> transitionsArray = new ArrayList<ActivityTransition>();
//https://developer.android.com/guide/topics/location/transitions#java
//https://developers.google.com/android/reference/com/google/android/gms/location/ActivityRecognitionClient

        transitionsArray.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());

        transitionsArray.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());

        transitionsArray.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_FOOT)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());

        transitionsArray.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_FOOT)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());

        transitionsArray.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());

        transitionsArray.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());

        transitionsArray.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());

        transitionsArray.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
//
        transitionsArray.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());

        transitionsArray.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        transitionsArray.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());

        transitionsArray.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());

        ActivityTransitionRequest request = new ActivityTransitionRequest(transitionsArray);

        mactivityRecognitionClient = ActivityRecognition.getClient(context);

//      Major use cases for a pending intent include the following:
//      Declaring an intent to be executed when the user performs an action with your Notification (the Android system's NotificationManager executes the Intent).
//      Declaring an intent to be executed when the user performs an action with your App Widget (the Home screen app executes the Intent).
//      Declaring an intent to be executed at a specified future time (the Android system's AlarmManager executes the Intent).
        Intent intent = new Intent(context, RecognitionReceiver.class);
        intent.setAction(RecognitionReceiver.ACTION_PROCESS_TRANSITION_UPDATES);
//      A PendingIntent object is a wrapper around an Intent object. The primary purpose of a PendingIntent is to grant permission to a
//      foreign application to use the contained Intent as if it were executed from your app's own process.

        mtransRecognitionPendingIntent =  PendingIntent.getBroadcast(context,0,intent,0);

        Task task = mactivityRecognitionClient
                .requestActivityTransitionUpdates(request, mtransRecognitionPendingIntent);
        task.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Log.i(TAG, " WVS requestActivityTransitionUpdates addOnSuccessListener onSuccess");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, " WVS requestActivityTransitionUpdates addOnSuccessListener onFailure");
            }
        });
    }


}
