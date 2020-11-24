package com.endeavor.walter.getout9;

import java.io.Serializable;

public class Locations implements Serializable {
    private String mPathname, mDatetime, mTitle, mTotalTime, mNoteImage;
    private Long mLocationsid, mTotalSteps;
    private int mUserId;
    private Float mfZoom, mfTotalDistance, mHeartIntensity;
    private Double mdLatitude, mdLongitude, mdElevationDifference;
    private Integer mChecked, mHeartDuration, mMoveMinutes;

    public Locations(String path, String datetime, Long locationsid, Float zoomlevel, Double latitude, Double longitude, String totaltime, Float totaldistance ,int userid, Long totalsteps, Integer checked, String noteimage){
        this.mPathname = path;
        this.mDatetime = datetime;
        this.mLocationsid = locationsid;
        this.mfZoom = zoomlevel;
        this.mdLatitude = latitude;
        this.mdLongitude = longitude;
        this.mTotalTime = totaltime;
        this.mfTotalDistance = totaldistance;
        this.mUserId = userid;
        this.mTotalSteps = totalsteps;
        this.mChecked = checked;
        this.mNoteImage = noteimage;
    }

    public String getPathname(){return this.mPathname;}
    public void setPathname(String name){this.mPathname = name;}

    public String getDatetime() {return this.mDatetime;}
    public void setDatetime(String datetime){this.mDatetime = datetime;}

    public Long getLocationsId(){return this.mLocationsid;}
    public void setLocationsId(Long locationsid) {this.mLocationsid = locationsid;}

    public String get_Title() {
        if (mTitle != null && mTitle.length() > 0) {
            return mTitle;
        } else {
            set_Title("");
            return mTitle;
        }
    }
    public void set_Title(String title){
        if (title.length()>0){
            this.mTitle = title;
        } else {
//            wvs hint precision two digits
            this.mTitle = this.mLocationsid + ". " + this.mPathname + " " + WVS_Utils.ReformatTimeString(this.mTotalTime) +
                    ", " + String.format("%.2f", this.mfTotalDistance) + " mi, " + this.mTotalSteps + " steps, " +
                    Integer.toString(this.getMoveDuration()) + " move min, " + Float.toString(this.getHeartIntensity()) + " Heart Pts, " +
                    Integer.toString(this.getHeartDuration()) + " min";
//          getFitSInfo() = sMoveMinutes + "move min, " + sHeartTime + "/" + sHeartIntensity + " Heart Pts/min";
        }

    }

    public Float getZoomLevel() {return this.mfZoom;}
    public void setZoomLevel(Float zoomlevel){this.mfZoom = zoomlevel;}

    public Double getLatitude() {return this.mdLatitude;}
    public void setLatitude( Double lat) {this.mdLatitude = lat;}

    public Double getLongitude() {return this.mdLongitude;}
    public void setLongitude( Double lng) {this.mdLongitude = lng;}

    public String getTotalTime(){return this.mTotalTime;}
    public void setTotalTime(String totaltime){this.mTotalTime = totaltime;}

    public Float getTotalDistance() {return this.mfTotalDistance;}
    public void setTotalDistance(Float totaldistance){this.mfTotalDistance = totaldistance;}

    public int getUserId(){return this.mUserId;}
    public void setUserId(int userid) {this.mUserId= userid;}

    public Long getTotalSteps(){return this.mTotalSteps;}
    public void setTotalSteps(Long totalsteps) {this.mTotalSteps= totalsteps;}

    public Integer getChecked() {return mChecked;}
    public void setChecked(Integer checked) {this.mChecked = checked;}

    public String getmNoteImage() {return this.mNoteImage;}
    public void setmNoteImage(String noteimage){this.mNoteImage = noteimage;}

    public Integer getMoveDuration() {
        if (this.mMoveMinutes == null) {
            return 0;
        } else {
            return this.mMoveMinutes;
        }
    }
    public void setMoveDuration(Integer duration) {this.mMoveMinutes = duration;}

    public Integer getHeartDuration() {
        if (this.mHeartDuration == null) {
            return 0;
        } else {
            return this.mHeartDuration;
        }
    }
    public void setHeartDuration(Integer duration) {this.mHeartDuration = duration;}

    public Float getHeartIntensity() {
        if (this.mHeartIntensity == null) {
            return 0f;
        } else {
            return this.mHeartIntensity;
        }
    }

    public void setHeartIntensity(Float intensity) {this.mHeartIntensity = intensity;}

    public Double getElevationDifference() {return this.mdElevationDifference;}
    public void setElevationDifference( Double elevationdifference) {this.mdElevationDifference = elevationdifference;}

}
