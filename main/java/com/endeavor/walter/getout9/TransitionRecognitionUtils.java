package com.endeavor.walter.getout9;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.DetectedActivity;

public class TransitionRecognitionUtils {

    static String createTranstionString(ActivityTransitionEvent activity){
        String theActivity = toActivityString(activity.getActivityType());
        String theTransitionType = toTransitiontypeString(activity.getTransitionType()) ;

        return theActivity + " - " + theTransitionType;
    }

    static String toActivityString(int activity){

        switch (activity){
            case DetectedActivity.STILL:
                return "STILL";
            case DetectedActivity.ON_FOOT:
                return "ON FOOT";
            case DetectedActivity.WALKING:
                return "WALKING";
            case DetectedActivity.RUNNING:
                return "RUNNING";
            case DetectedActivity.ON_BICYCLE:
                return "ON BICYCLE";
            case DetectedActivity.IN_VEHICLE:
                return "IN VEHICLE";
            default:
                return "UNKNOWN";
        }
    }


    static String toTransitiontypeString(int activity){

        switch (activity) {
            case ActivityTransition.ACTIVITY_TRANSITION_ENTER:
                return "ENTER";
            case ActivityTransition.ACTIVITY_TRANSITION_EXIT:
                return "EXIT";
                default:
                    return "UNKNONW";
        }
    }
}
