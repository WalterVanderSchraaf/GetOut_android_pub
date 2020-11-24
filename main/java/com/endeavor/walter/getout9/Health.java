package com.endeavor.walter.getout9;

import android.content.Intent;

import java.io.Serializable;

import static com.endeavor.walter.getout9.WVS_Utils.getDateTimeString_standard;

public class Health implements Serializable {
    public static final String TAG = Health.class.getSimpleName();

    private String mType,mSourceName, mSourceVersion, mDevice, mUnit, mCreationDate, mStartDate, mEndDate, mValue;
    private Integer mHealthId, mLoginId;


    public Health(Integer healthid, String type, String sourcename, String sourceversion, String device, String unit, String creationdate, String startdate, String enddate, String value, Integer loginid){
        this.mHealthId = healthid;
        this.mType = type;
        this.mSourceName = sourcename;
        this.mSourceVersion = sourceversion;
        this.mDevice = device;
        this.mUnit = unit;
        this.mCreationDate = creationdate;
        this.mStartDate = startdate;
        this.mEndDate = enddate;
        this.mValue = value;
        this.mLoginId = loginid;
    }

    public Integer getHealthId() {return mHealthId;}
    public void setHealthId(Integer healthid) {this.mHealthId = healthid;}

    public String getType() {return mType;}
    public void setType(String type) {this.mType = type;}

    public String getSourceName() {return mSourceName;}
    public void setSourceName(String sourcename) {this.mSourceName = sourcename;}

    public String getSourceVersion() {return mSourceVersion;}
    public void setSourceVersion(String sourceversion) {this.mSourceVersion = sourceversion;}

    public String getDevice() {return mDevice;}
    public void setDevice(String device) {this.mDevice = device;}

    public String getUnit() {return mUnit;}
    public void setUnit(String unit) {this.mUnit = unit;}

    public String getCreationDate() {return mCreationDate;}
    public String getStartDate() {return mStartDate;}
    public String getEndDate() {return mEndDate;}

    public String getValue() {return mValue;}

    public Integer getLoginId() {return mLoginId;}
}
