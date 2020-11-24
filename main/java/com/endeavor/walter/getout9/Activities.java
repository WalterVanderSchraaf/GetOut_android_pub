package com.endeavor.walter.getout9;

import com.google.android.gms.location.ActivityTransitionEvent;


public class Activities extends ActivityTransitionEvent {
    public long mLocationSId;
    public String mDateTime;
    public String mDescription;


    public Activities(int i, int i1, long l) {
//      getActivityType, getTransitionType, getElapsedRealTimeNanos
        super(i, i1, l);
    }

    public Long getLocationSId() {
        return mLocationSId;
    }

    public void setLocationSId(long locationsid){
        this.mLocationSId = locationsid;
    }


    public String getDateTime(){
        return mDateTime;
    }

    public void setDateTime(String datetime){
        this.mDateTime = datetime;
    }

    public String getDescription(){
        return mDescription;
    }

    public void setDescription(String description){
        this.mDescription = description;
    }

}
