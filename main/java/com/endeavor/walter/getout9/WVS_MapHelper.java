package com.endeavor.walter.getout9;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class WVS_MapHelper {
    Context mContext;
    final String TAG = WVS_MapHelper.class.getSimpleName();


    public Float Determine_Update_Distance(ArrayList<Location_foo> tmpLocationArray, Context context, Boolean bUpdateDB){

        Location tmpLocation;
        float totalLength=0f;
//        ArrayList<Float> fDistance;
        float[] fDistance = new float[1];

        if (tmpLocationArray != null && tmpLocationArray.size() > 0) {

            for (int i = 0; i < tmpLocationArray.size(); i++) {
                tmpLocation = tmpLocationArray.get(i);
                if (i > 0){
                    if (tmpLocationArray.get(i).getTransitionId() == 0) {
                       Log.i(TAG, "transition entry, skip this distance to previous exit" );
                    } else {
                        tmpLocation.distanceBetween(tmpLocationArray.get(i - 1).getLatitude(), tmpLocationArray.get(i - 1).getLongitude(), tmpLocation.getLatitude(), tmpLocation.getLongitude(), fDistance);
                        totalLength = totalLength + fDistance[0];
//                        Log.i(TAG, " WVS " + i + " total=" + totalLength + ", segment=" + fDistance[0]);
                    }
                }
                if (i == tmpLocationArray.size() - 1 ) {
//                  1 Mile = 1609.344 Meters
                    totalLength = totalLength/1609.344f;
                    Log.i(TAG, " WVS " + i + " total miles = " + totalLength );
                    if (bUpdateDB) {
                        LocationsRepo lsr = new LocationsRepo(context);
                        lsr.updateTotalDistanceLocationsId(((Location_foo) tmpLocation).getLocationSId(), totalLength);
                    }
                }
            }
        }
        return totalLength;
    }
}
