package com.endeavor.walter.getout9;

import java.io.Serializable;

public class Scripts implements Serializable {
    private long mScriptsId;
    private String mScriptName;
    private String mBucket;
    private String mBucketPrefix;
    private String mDownloadDate;
    private String mExecutionDate;
    private long mRowsAffected;

    public static final String TAG = Scripts.class.getSimpleName();

    public Scripts(long id, String scriptname, String bucket, String bucketprefix, String downloaddate, String executiondate, long rowsaffected){
        mScriptsId = id;
        mScriptName = scriptname;
        mBucket = bucket;
        mBucketPrefix = bucketprefix;
        mDownloadDate = downloaddate;
        mExecutionDate = executiondate;
        mRowsAffected = rowsaffected;
    }

    public Long getScriptsId() {
        return mScriptsId;
    }

    public void setScriptsId(long scriptsid){
        this.mScriptsId= scriptsid;
    }

    public String getScriptName(){
        return mScriptName;
    }

    public void setScriptName(String scriptname){
        this.mScriptName = scriptname;
    }

    public String getBucket(){
        return mBucket;
    }

    public void setBucket(String bucket){
        this.mBucket = bucket;
    }

    public String getBucketPrefix(){
        return mBucketPrefix;
    }

    public void setBucketPrefix(String bucketprefix){
        this.mBucketPrefix = bucketprefix;
    }

    public String getDownloadDate(){
        return mDownloadDate;
    }

    public void setDownloadDate(String downloaddate){
        this.mDownloadDate = downloaddate;
    }

    public String getExecutionDate(){
        return mExecutionDate;
    }

    public void setExecutionDate(String executiondate){
        this.mExecutionDate = executiondate;
    }

    public Long getRowsAffected() {
        return mRowsAffected;
    }

    public void setRowsAffected(long rowsaffected){
        this.mRowsAffected= rowsaffected;
    }
}
