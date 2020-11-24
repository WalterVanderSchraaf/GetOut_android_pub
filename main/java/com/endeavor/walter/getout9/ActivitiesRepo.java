package com.endeavor.walter.getout9;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class ActivitiesRepo {

    DBHelper dbHelper;
    public final String TAG =  ActivitiesRepo.class.getSimpleName();// "LocationRepo";

    public ActivitiesRepo(){
        dbHelper = new DBHelper();
        DatabaseManager.initializeInstance(dbHelper);
    }

    public ActivitiesRepo(Context context){
        dbHelper = new DBHelper( context );
        DatabaseManager.initializeInstance(dbHelper);
    }

    public long insert (Activities activities) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBInfo.TABLE_ACTIVITIES_LocationsId, activities.mLocationSId);
        //ActivitiesId should be a trigger
        values.put(DBInfo.TABLE_ACTIVITIES_ActivityId, activities.getActivityType());
        values.put(DBInfo.TABLE_ACTIVITIES_TransitionId, activities.getTransitionType());
//        values.put(DBInfo.TABLE_ACTIVITIES_ElapsedRealTimeNanos, activities.getElapsedRealTimeNanos());
        values.put(DBInfo.TABLE_ACTIVITIES_DateTime, activities.getDateTime());
        values.put(DBInfo.TABLE_ACTIVITIES_Description, activities.getDescription());
        // Inserting Row
        long activities_id = db.insert(DBInfo.TABLE_ACTIVITIES,null,values);
        db.close(); // Closing database connection
        return activities_id;
    }

    public ArrayList<Activities> getAllActivitiesbyLocationsId(Long locationsid){

        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sOrderby=" ORDER BY " + DBInfo.TABLE_ACTIVITIES_DateTime;

        String selectQuery =  "SELECT  " +
                DBInfo.TABLE_ACTIVITIES_ID + "," +
                DBInfo.TABLE_ACTIVITIES_LocationsId + "," +
                DBInfo.TABLE_ACTIVITIES_ActivityId + "," +
                DBInfo.TABLE_ACTIVITIES_TransitionId+ "," +
//                DBInfo.TABLE_ACTIVITIES_ElapsedRealTimeNanos + "," +
                DBInfo.TABLE_ACTIVITIES_DateTime + "," +
                DBInfo.TABLE_ACTIVITIES_Description +
                " FROM " + DBInfo.TABLE_ACTIVITIES +
                " WHERE " +
                DBInfo.TABLE_LOCATION_KEY_LocationsId + "=?" + sOrderby;
//wvs: hint: todo: rawquery orderby
//                + // It's a good practice to use parameter ?, instead of concatenate string
//                " ORDER BY " + sOrderby;

//        Log.i(TAG, " WVS getAllActivitiesbyLocationsId " + locationsid);
//        Log.i(TAG, " WVS getAllActivitiesbyLocationsId " + selectQuery);
        ArrayList<Activities> activitiesArrayList = new ArrayList<Activities>();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { locationsid.toString() } );
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {

        //      getActivityType, getTransitionType, getElapsedRealTimeNanos
                int activityType = cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_ACTIVITIES_ActivityId));
                int transitionType = cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_ACTIVITIES_TransitionId));
//                long elapsedtimerealtimenano = cursor.getLong(cursor.getColumnIndex(DBInfo.TABLE_ACTIVITIES_ElapsedRealTimeNanos));
                long elapsedtimerealtimenano = 0;
                Activities tmpActivity = new Activities(activityType,transitionType,elapsedtimerealtimenano);
                tmpActivity.setDateTime(cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_ACTIVITIES_DateTime)));
                tmpActivity.setDescription(cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_ACTIVITIES_Description)));

//                Log.i(TAG," WVS locationid=" + cursor.getLong(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_LocationId)) +
//                        ", lat=" + cursor.getDouble(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_Longitude)) +
//                        ", long=" + cursor.getDouble(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_Latitude)) +
//                        ", locationsid=" + cursor.getLong(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_LocationsId)));
                activitiesArrayList.add(tmpActivity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return activitiesArrayList;
    }


}
