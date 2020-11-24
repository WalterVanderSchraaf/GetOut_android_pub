package com.endeavor.walter.getout9;

import android.location.Location;

public class Location_foo extends Location {

private Long mLocationId;
private Long mLocationSId;
private Integer mColorPath;
private String mDateTime;
private Integer mActivityId;
private Integer mTransitionId;

    public Location_foo(String provider) {
        super(provider);
    }

    public Long getLocationSId() {
        return mLocationSId;
    }

    public void setLocationSId(long locationsid){
        this.mLocationSId = locationsid;
    }

    public Long getLocationId() {
        return mLocationId;
    }

    public void setLocationId(long locationid){
        this.mLocationId = locationid;
    }

    public Integer getColorPath(){
        return this.mColorPath;
    }

    public void setColorPath(Integer colorpath){
        this.mColorPath = colorpath;
    }

    public String getDateTime(){
        return mDateTime;
    }

    public void setDateTime(String datetime){
        this.mDateTime = datetime;
    }
    public Integer getActivityId(){return mActivityId;}
    public void setActivityId(Integer activityid){this.mActivityId = activityid;}

    public Integer getTransitionId(){return mTransitionId;}
    public void setTransitionId(Integer transitionid){this.mTransitionId = transitionid;}

}
