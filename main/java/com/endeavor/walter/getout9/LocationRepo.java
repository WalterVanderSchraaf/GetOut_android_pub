package com.endeavor.walter.getout9;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.endeavor.walter.getout9.Utils.SumTotalTimes;
import static com.endeavor.walter.getout9.Utils.appLog;
import static com.endeavor.walter.getout9.WVS_Utils.getDateTimeString_standard;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class LocationRepo {

    DBHelper dbHelper;
    public final String TAG = LocationRepo.class.getSimpleName();//"LocationRepo";

    public LocationRepo(){
        dbHelper = new DBHelper();
        DatabaseManager.initializeInstance(dbHelper);
    }

    public LocationRepo(Context context){
        dbHelper = new DBHelper( context );
        DatabaseManager.initializeInstance(dbHelper);
    }

    public long insert (Location location){

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //LocationId should be a trigger
        values.put(DBInfo.TABLE_LOCATION_KEY_Latitude, location.getLatitude());
        values.put(DBInfo.TABLE_LOCATION_KEY_Longitude, location.getLongitude());
        values.put(DBInfo.TABLE_LOCATION_KEY_Altitude, location.getAltitude());

        Log.i(TAG, "has altitude " + location.hasAltitude());
        Log.i(TAG, "    altitude " + location.getAltitude());
        Log.i(TAG, "has speed " + location.hasSpeed() );
        Log.i(TAG, "    speed " + location.getSpeed() );

        values.put(DBInfo.TABLE_LOCATION_KEY_ColorPath, Utils.getLocation_Color(location) );
        values.put(DBInfo.TABLE_LOCATION_KEY_LocationsId, Utils.getLocation_LocationsId(location));
        values.put(DBInfo.TABLE_LOCATION_KEY_DateTime, getDate() );

        // Inserting Row
        long notes_id = db.insert(DBInfo.TABLE_LOCATION,null,values);
        db.close(); // Closing database connection
        return notes_id;
    }

    public int deleteRoutebyLocationdSId(long locations_id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        int result = db.delete(DBInfo.TABLE_LOCATION, DBInfo.TABLE_LOCATION_KEY_LocationsId + "= ?", new String[] { String.valueOf(locations_id) });
        db.close(); // Closing database connection
        return result;
    }

    public int deleteSectionInRoutebyLocationd_Id(long location_id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        int result = db.delete(DBInfo.TABLE_LOCATION, DBInfo.TABLE_LOCATION_KEY_LocationId + "= ?", new String[] { String.valueOf(location_id) });
//        Log.i(TAG, " WVS locationid = " + location_id + ", return = " + result);
        db.close(); // Closing database connection
        return result;
    }

    public ArrayList<Location_foo> getRouteDetailsByLocationsId(Long locationsid){

        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sOrderby=" ORDER BY " + DBInfo.TABLE_LOCATIONS_KEY_DateTime;

        String selectQuery =  "SELECT  " +
                DBInfo.TABLE_LOCATION_KEY_LocationId + "," +
                DBInfo.TABLE_LOCATION_KEY_Latitude + "," +
                DBInfo.TABLE_LOCATION_KEY_Longitude + "," +
                DBInfo.TABLE_LOCATION_KEY_Altitude + "," +
                DBInfo.TABLE_LOCATION_KEY_ColorPath + "," +
                DBInfo.TABLE_LOCATION_KEY_DateTime + "," +
                DBInfo.TABLE_LOCATION_KEY_DateTime + "," +
                " IFNULL(" + DBInfo.TABLE_LOCATION_KEY_Activity + ", -1) AS ActivityId," +
                " IFNULL(" + DBInfo.TABLE_LOCATION_KEY_Transition + ", -1) AS TransitionId," +
                DBInfo.TABLE_LOCATION_KEY_LocationsId +
                " FROM " + DBInfo.TABLE_LOCATION +
                " WHERE " +
                DBInfo.TABLE_LOCATION_KEY_LocationsId + "=?" + sOrderby;
//wvs: hint: todo: rawquery orderby
//                + // It's a good practice to use parameter ?, instead of concatenate string
//                " ORDER BY " + sOrderby;

        Log.i(TAG, " WVS getRouteDetailsByLocationsId " + locationsid);
//        Log.i(TAG, " WVS getRouteDetailsByLocationsId " + selectQuery);
        ArrayList<Location_foo> locationList = new ArrayList<Location_foo>();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { locationsid.toString() } );
        // looping through all rows and adding to list
        Log.i(TAG, " WVS getRouteDetailsByLocationsId " + locationsid + " back from query");

        if (cursor.moveToFirst()) {
            do {
                Location_foo tmpLoc = new Location_foo("fused");
                tmpLoc.setLocationId(cursor.getLong(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_LocationId)));
                tmpLoc.setLongitude(cursor.getDouble(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_Longitude)));
                tmpLoc.setLatitude(cursor.getDouble(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_Latitude)));
                tmpLoc.setAltitude(cursor.getDouble(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_Altitude)));
                tmpLoc.setColorPath(cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_ColorPath)));
                tmpLoc.setDateTime(cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_DateTime)));
                tmpLoc.setActivityId(cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_Activity)));
                tmpLoc.setTransitionId(cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_Transition)));
//                tmpLoc.setLocationSId(cursor.getLong(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_LocationsId)));
                tmpLoc.setLocationSId(locationsid);
//                Log.i(TAG," WVS locationid=" + cursor.getLong(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_LocationId)) +
//                        ", lat=" + cursor.getDouble(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_Longitude)) +
//                        ", long=" + cursor.getDouble(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_Latitude)) +
//                        ", locationsid=" + cursor.getLong(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_LocationsId)));
                locationList.add(tmpLoc);
            } while (cursor.moveToNext());
        }
        Log.i(TAG, " WVS getRouteDetailsByLocationsId " + locationsid + " created ArrayList<Location_foo>");
        Log.i(TAG, " WVS getRouteDetailsByLocationsId count = " + cursor.getCount());
        cursor.close();
        db.close();
        return locationList;
    }

    public ArrayList<Location_foo> getRouteActivityByLocationsId(Long locationsid, Context context){

        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sOrderby=" ORDER BY l." + DBInfo.TABLE_LOCATION_KEY_LocationId;

        String sActivityOffset = Utils.getRouteAcivityOffset(context);
        String selectQueryorig =  "SELECT  " +
                "l." + DBInfo.TABLE_LOCATION_KEY_LocationId + "," +
                "l." + DBInfo.TABLE_LOCATION_KEY_Latitude + "," +
                "l." + DBInfo.TABLE_LOCATION_KEY_Longitude + "," +
                "l." + DBInfo.TABLE_LOCATION_KEY_Altitude + "," +
                "l." + DBInfo.TABLE_LOCATION_KEY_ColorPath + "," +
                "l." + DBInfo.TABLE_LOCATION_KEY_DateTime + "," +
                "l." + DBInfo.TABLE_LOCATION_KEY_LocationsId + "," +
                "e." + DBInfo.TABLE_ACTIVITIES_ID  + "," +
                "IFNULL(e." + DBInfo.TABLE_ACTIVITIES_ActivityId  + ",-1) " + DBInfo.TABLE_ACTIVITIES_ActivityId + " ," +
                "IFNULL(e." + DBInfo.TABLE_ACTIVITIES_TransitionId  + ",-1) " + DBInfo.TABLE_ACTIVITIES_TransitionId + " ," +
                "e." + DBInfo.TABLE_ACTIVITIES_DateTime + " ActivityDatetime" +
                " FROM " + DBInfo.TABLE_LOCATION + " l" +
                " LEFT OUTER JOIN (" +
                    "SELECT " +
                    "d." + DBInfo.TABLE_LOCATION_KEY_LocationsId + "," +
                    "a." + DBInfo.TABLE_ACTIVITIES_ID + "," +
                    "a." + DBInfo.TABLE_ACTIVITIES_ActivityId + "," +
                    "a." + DBInfo.TABLE_ACTIVITIES_TransitionId + "," +
                    "a." + DBInfo.TABLE_ACTIVITIES_DateTime + "," +
                    "l." + DBInfo.TABLE_LOCATION_KEY_LocationId  + "," +
                    "l." + DBInfo.TABLE_LOCATION_KEY_DateTime  +
                    " FROM " + DBInfo.TABLE_ACTIVITIES + " a" +
                    " INNER JOIN (" +
                        " SELECT " +
                        "c." + DBInfo.TABLE_LOCATION_KEY_LocationsId + "," +
                        "c." + DBInfo.TABLE_ACTIVITIES_ID + "," +
                        " MIN(" + "c." + DBInfo.TABLE_LOCATION_KEY_LocationId + ") LocationId" +
                        " FROM (" + "SELECT " +
                            " a." + DBInfo.TABLE_LOCATION_KEY_LocationsId + ", " +
                            " a." + DBInfo.TABLE_ACTIVITIES_ID  + ", " +
                            "l." + DBInfo.TABLE_LOCATION_KEY_LocationId  + "," +
                            "l." + DBInfo.TABLE_LOCATION_KEY_DateTime +
                            " FROM " + DBInfo.TABLE_LOCATION + " l" +
                            " LEFT JOIN " + DBInfo.TABLE_ACTIVITIES + " a" +
                            " ON a." + DBInfo.TABLE_LOCATIONS_KEY_LocationsId + " = l." + DBInfo.TABLE_LOCATIONS_KEY_LocationsId +
                            " WHERE STRFTIME('%Y-%m-%d %H:%M:%S', a." + DBInfo.TABLE_LOCATION_KEY_DateTime + " , '-" + sActivityOffset + " seconds') <= l." + DBInfo.TABLE_LOCATION_KEY_DateTime +
                        " ORDER BY " + "a." + DBInfo.TABLE_ACTIVITIES_DateTime + ", " + DBInfo.TABLE_ACTIVITIES_ID + ", "+ DBInfo.TABLE_LOCATION_KEY_LocationId + ") c " +
                        " GROUP BY " + DBInfo.TABLE_LOCATIONS_KEY_LocationsId + ", " + DBInfo.TABLE_ACTIVITIES_ID + ") d" +
                        " ON a." + DBInfo.TABLE_ACTIVITIES_ID + " = d." + DBInfo.TABLE_ACTIVITIES_ID  +
                " INNER JOIN " + DBInfo.TABLE_LOCATION + " l" +
                " ON d." + DBInfo.TABLE_LOCATION_KEY_LocationId + " = l." + DBInfo.TABLE_LOCATION_KEY_LocationId +
                ") e ON l." + DBInfo.TABLE_LOCATION_KEY_LocationId + " = e." + DBInfo.TABLE_LOCATION_KEY_LocationId +
                " WHERE " +
                "l." + DBInfo.TABLE_LOCATION_KEY_LocationsId + "=?" + sOrderby;
//        " WHERE a." + DBInfo.TABLE_LOCATION_KEY_DateTime + " <= " + "l." + DBInfo.TABLE_LOCATION_KEY_DateTime +
//        " WHERE STRFTIME('%Y-%m-%d %H:%M:%S', a." + DBInfo.TABLE_LOCATION_KEY_DateTime + " , '-60.000 seconds') <= " + "l." + DBInfo.TABLE_LOCATION_KEY_DateTime +
//        STRFTIME('%Y-%m-%d %H:%M:%S', a.DateTime, '-60.000 seconds')
//wvs: hint: todo: rawquery orderby
//                + // It's a good practice to use parameter ?, instead of concatenate string
//                " ORDER BY " + sOrderby;
        String selectQuery =  "SELECT  " +
                "l." + DBInfo.TABLE_LOCATION_KEY_LocationId + "," +
                "l." + DBInfo.TABLE_LOCATION_KEY_Latitude + "," +
                "l." + DBInfo.TABLE_LOCATION_KEY_Longitude + "," +
                "l." + DBInfo.TABLE_LOCATION_KEY_Altitude + "," +
                "l." + DBInfo.TABLE_LOCATION_KEY_ColorPath + "," +
                "l." + DBInfo.TABLE_LOCATION_KEY_DateTime + "," +
                "l." + DBInfo.TABLE_LOCATION_KEY_LocationsId + "," +
                "e." + DBInfo.TABLE_ACTIVITIES_ID  + "," +
                "IFNULL(e." + DBInfo.TABLE_ACTIVITIES_ActivityId  + ",-1) " + DBInfo.TABLE_ACTIVITIES_ActivityId + " ," +
                "IFNULL(e." + DBInfo.TABLE_ACTIVITIES_TransitionId  + ",-1) " + DBInfo.TABLE_ACTIVITIES_TransitionId + " ," +
                "e." + DBInfo.TABLE_ACTIVITIES_DateTime + " ActivityDatetime" +
                " FROM " + DBInfo.TABLE_LOCATION + " l" +
                " LEFT OUTER JOIN " + DBInfo.TABLE_ACTIVITIES + " e ON e." + DBInfo.TABLE_ACTIVITIES_DateTime + " BETWEEN " +
                " l." + DBInfo.TABLE_ACTIVITIES_DateTime + " AND STRFTIME ('%Y-%m-%d %H:%M:%S', l." + DBInfo.TABLE_ACTIVITIES_DateTime +
                ", '+" + sActivityOffset + " seconds') AND e.activityid != 3" +
                " WHERE " +
                "l." + DBInfo.TABLE_LOCATION_KEY_LocationsId + "=?" + sOrderby;;

        Log.i(TAG, " WVS getRouteActivityByLocationsId " + locationsid);
//        Log.i(TAG, " WVS getRouteActivityByLocationsId " + selectQuery);
        ArrayList<Location_foo> locationList = new ArrayList<Location_foo>();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { locationsid.toString() } );
        // looping through all rows and adding to list
        Log.i(TAG, " WVS getRouteActivityByLocationsId " + locationsid + " back from query");

        if (cursor.moveToFirst()) {
            do {
                Location_foo tmpLoc = new Location_foo("fused");
                tmpLoc.setLocationId(cursor.getLong(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_LocationId)));
                tmpLoc.setLongitude(cursor.getDouble(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_Longitude)));
                tmpLoc.setLatitude(cursor.getDouble(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_Latitude)));
                tmpLoc.setAltitude(cursor.getDouble(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_Altitude)));
                tmpLoc.setColorPath(cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_ColorPath)));
                tmpLoc.setDateTime(cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_DateTime)));
                tmpLoc.setLocationSId(cursor.getLong(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_LocationsId)));
                tmpLoc.setActivityId(cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_ACTIVITIES_ActivityId)));
                tmpLoc.setTransitionId(cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_ACTIVITIES_TransitionId)));
//                Log.i(TAG," WVS locationid=" + cursor.getLong(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_LocationId)) +
//                        ", lat=" + cursor.getDouble(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_Longitude)) +
//                        ", long=" + cursor.getDouble(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_Latitude)) +
//                        ", locationsid=" + cursor.getLong(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_LocationsId)));
                locationList.add(tmpLoc);
            } while (cursor.moveToNext());
        }
        Log.i(TAG, " WVS getRouteActivityByLocationsId " + locationsid + " created ArrayList<Location_foo>");
        Log.i(TAG, " WVS getRouteActivityByLocationsId count = " + cursor.getCount());
        cursor.close();
        db.close();
        appLog(TAG, " WVS getRouteActivityByLocationsId() AFTER loading tmpLoc");
        return locationList;
    }

    public ArrayList<Location_foo> getBadSections_RouteInfobyLocationsId(Long locationsid, LatLng latlng){
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sOrderby=" ORDER BY l." + DBInfo.TABLE_LOCATION_KEY_LocationId;

//        String selectQuery =  "SELECT" +
//                " ." + DBInfo.TABLE_LOCATION_KEY_LocationsId + "," +
//                " L." + DBInfo.TABLE_LOCATION_KEY_LocationId + "," +
//                " L." + DBInfo.TABLE_LOCATION_KEY_Latitude + "," +
//                " L." + DBInfo.TABLE_LOCATION_KEY_Longitude + "," +
//                " L." + DBInfo.TABLE_LOCATION_KEY_DateTime + "," +
//                " PREV." + DBInfo.TABLE_LOCATION_KEY_LocationId + " AS PREVLOCATIONID," +
//                " ABS(L." + DBInfo.TABLE_LOCATION_KEY_Latitude + " - PREV." + DBInfo.TABLE_LOCATION_KEY_Latitude + ") AS LATDIFF," +
//                " ABS(L." + DBInfo.TABLE_LOCATION_KEY_Longitude + " - PREV." + DBInfo.TABLE_LOCATION_KEY_Longitude + ") AS LNGDIFF" +
//                " FROM " + DBInfo.TABLE_LOCATION + " L " +
//                " LEFT OUTER JOIN " + DBInfo.TABLE_LOCATION + " PREV " + "ON L." + DBInfo.TABLE_LOCATION_KEY_LocationId + " - 1 = PREVLOCATIONID" +
//                " WHERE ROUND(L." + DBInfo.TABLE_LOCATION_KEY_Latitude + ", 3) = ROUND(" + latlng.latitude + ", 3)" +
//                " AND ROUND(L." + DBInfo.TABLE_LOCATION_KEY_Longitude + ", 3) = ROUND(" + latlng.longitude + ", 3)" +
//                " AND L." + DBInfo.TABLE_LOCATIONS_KEY_LocationsId + "=?";

        String selectQuery =  "SELECT" +
                " loc." + DBInfo.TABLE_LOCATION_KEY_LocationsId + "," +
                " loc." + DBInfo.TABLE_LOCATION_KEY_LocationId + "," +
                " loc." + DBInfo.TABLE_LOCATION_KEY_Latitude + "," +
                " loc." + DBInfo.TABLE_LOCATION_KEY_Longitude + "," +
                " loc." + DBInfo.TABLE_LOCATION_KEY_DateTime + "," +
                " src.LATDIFF," +
                " src.LNGDIFF" +
                " FROM " + DBInfo.TABLE_LOCATION + " loc " +
                " INNER JOIN " +
                " (SELECT " +
                DBInfo.TABLE_LOCATION_KEY_LocationsId + "," +
                DBInfo.TABLE_LOCATION_KEY_LocationId + "," +
                DBInfo.TABLE_LOCATION_KEY_Latitude + "," +
                DBInfo.TABLE_LOCATION_KEY_Longitude + "," +
                DBInfo.TABLE_LOCATION_KEY_DateTime + "," +
                " LATDIFF," +
                " LNGDIFF," +
                " MAX(PREVLOCATIONID) prevlocid " +
                " FROM " +
                " (SELECT " +
                " L." + DBInfo.TABLE_LOCATION_KEY_LocationsId + "," +
                " L." + DBInfo.TABLE_LOCATION_KEY_LocationId + "," +
                " L." + DBInfo.TABLE_LOCATION_KEY_Latitude + "," +
                " L." + DBInfo.TABLE_LOCATION_KEY_Longitude + "," +
                " L." + DBInfo.TABLE_LOCATION_KEY_DateTime + "," +
                " PREV.LocationId AS PREVLOCATIONID," +
                " ABS(L.Latitude - PREV.Latitude) AS LATDIFF, ABS(L.Longitude - PREV.Longitude) AS LNGDIFF " +
                " FROM " + DBInfo.TABLE_LOCATION + " L " +
                " LEFT OUTER JOIN " + DBInfo.TABLE_LOCATION + " PREV " + "ON PREVLOCATIONID < " + "L." + DBInfo.TABLE_LOCATION_KEY_LocationId  +
                " WHERE ROUND(L." + DBInfo.TABLE_LOCATION_KEY_Latitude + ", 3) = ROUND(" + latlng.latitude + ", 3)" +
                " AND ROUND(L." + DBInfo.TABLE_LOCATION_KEY_Longitude + ", 3) = ROUND(" + latlng.longitude + ", 3)" +
                " AND L." + DBInfo.TABLE_LOCATIONS_KEY_LocationsId + "=?)" +
                " GROUP BY LocationsId, LocationId, Latitude, Longitude, DateTime) src on loc.locationid = src.prevlocid or loc.locationid = src.locationid" +
                " GROUP BY loc.LocationsId, loc.LocationId, loc.Latitude, loc.Longitude, loc.DateTime";

//        Log.i(TAG, " WVS getBadRouteInfobyLocationsId " + selectQuery);
        ArrayList<Location_foo> locationList = new ArrayList<Location_foo>();
        Bundle extraBundle = new Bundle();
        extraBundle.putBoolean("Bad Location", FALSE);
        Cursor cursor = db.rawQuery(selectQuery, new String[]{locationsid.toString()});

        if (cursor.moveToFirst()) {
            do {
                Location_foo tmpLoc = new Location_foo("fused");
                tmpLoc.setLocationSId(cursor.getLong(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_LocationsId)));
                tmpLoc.setLocationId(cursor.getLong(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_LocationId)));
                tmpLoc.setLatitude(cursor.getDouble(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_Latitude)));
                tmpLoc.setLongitude(cursor.getDouble(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_Longitude)));
                tmpLoc.setDateTime(cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_DateTime)));
                Log.i(TAG, "LOCATION ID " + Long.toString(cursor.getLong(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_LocationId))));
                Log.i(TAG, "LAT DIFF " + Double.toString(cursor.getDouble(cursor.getColumnIndex("LATDIFF"))));
                Log.i(TAG, "LNG DIFF " + Double.toString(cursor.getDouble(cursor.getColumnIndex("LNGDIFF"))));
//              threshhold was .0025
                if (cursor.getDouble(cursor.getColumnIndex("LATDIFF")) > .0018) {
                    extraBundle.putBoolean("Bad Location", TRUE);
                }
                if (cursor.getDouble(cursor.getColumnIndex("LNGDIFF")) > .0018) {
                    extraBundle.putBoolean("Bad Location", TRUE);
                }
                tmpLoc.setExtras(extraBundle);
                locationList.add(tmpLoc);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return  locationList;
    }

    public String getDate() {
//        String filedatepart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String datetime = getDateTimeString_standard();
        return datetime;
    }
    public int getTimeDifferenceLocationsId(Long locationsid){

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        int iTotalTime=-1;

        String selectQuery1 = "select lstart.StartTime , " +
                " lend.EndTime, " +
                " ((strftime('%s', lend.EndTime) - strftime('%s', lstart.StartTime))/60) AS timedifference " +
                " FROM " +
                " (select locationsid, max(DateTime) EndTime from tblLocation group by locationsid) lend " +
                " inner join (select locationsid, min(DateTime) StartTime from tblLocation group by locationsid) lstart on lstart.locationsid = lend.locationsid " +
                " where  lstart.locationsid =? ";
        Cursor cursor = db.rawQuery(selectQuery1, new String[] { locationsid.toString() } );
        // looping through all rows and adding to list
//        Log.i(TAG, " WVS " + selectQuery1);

        if (cursor.moveToFirst()) {
            iTotalTime = cursor.getInt(cursor.getColumnIndex("timedifference"));
        }
        cursor.close();
        db.close(); // Closing database connection
        return iTotalTime;
    }

    public String  getTimeDifference_wPauses_LocationsId(Long locationsid){
        String strTotalTime = "0:00:00";
        String returnTotalTime = "0:00:00";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
//      test setup 94: 12451,12460  12461,12481  12482,12496  12497,12567 12568,12591
        String selectQuery2="";
        if (locationsid == 94) {
            selectQuery2 = "UPDATE " + DBInfo.TABLE_LOCATION +
                    " SET " + DBInfo.TABLE_LOCATION_KEY_Transition + " = 0" +
                    " WHERE " + DBInfo.TABLE_LOCATION_KEY_LocationsId + " = " + locationsid.toString() +
                    " AND " + DBInfo.TABLE_LOCATION_KEY_LocationId + " in (12451,12461,12482,12497)";
//            db.execSQL(selectQuery2);
            selectQuery2 = "UPDATE " + DBInfo.TABLE_LOCATION +
                    " SET " + DBInfo.TABLE_LOCATION_KEY_Transition + " = 1" +
                    " WHERE " + DBInfo.TABLE_LOCATION_KEY_LocationsId + " = " + locationsid.toString() +
                    " AND " + DBInfo.TABLE_LOCATION_KEY_LocationId + " in (12460,12481,12496,12567)";
//            db.execSQL(selectQuery2);
        }

//select * from --62 -92
//(select locationsid, min(locationid), -2 as category, datetime from tblLocation where (transitionid is null or transitionid <> 0 or transitionid <> 1)
//group by locationsid
//union
//select locationsid, max(locationid), -1 as category, datetime from tblLocation where (transitionid is null or transitionid <> 0 or transitionid <> 1)
//group by locationsid
//union
//select locationsid, locationid, transitionid as category, datetime from tblLocation where transitionid in (0,1)
//union
//select locationsid, min(locationid), 2  as category, datetime from tblLocation where locationid >
//(select max(locationid) from tblLocation where (transitionid <> 0 or transitionid <> 1) )
//union
//select locationsid, max(locationid), 3  as category, datetime from tblLocation where locationid >
//(select max(locationid) from tblLocation where (transitionid <> 0 or transitionid <> 1) ))
//where locationsid = 109
//order by 2;
        String maxTransitionLocId="";
        String selectQueryMaxTransId = "select max(locationid) as maxTransitionLocId from tblLocation where (transitionid is not null or transitionid <> 0 or transitionid <> 1) and locationsid =? " ;
        Cursor cursor = db.rawQuery(selectQueryMaxTransId, new String[] { locationsid.toString() } );
        if (cursor.moveToFirst()) {
            maxTransitionLocId = cursor.getString(cursor.getColumnIndex("maxTransitionLocId"));
        }

        String selectQuery1 = "select * from" +
                " (select locationsid, min(locationid) as locationid, -2 as category, datetime from tblLocation where (transitionid is null or transitionid <> 0 or transitionid <> 1)" +
                " group by locationsid" +
                " union" +
                " select locationsid, max(locationid) as locationid, -1 as category, datetime from tblLocation where (transitionid is null or transitionid <> 0 or transitionid <> 1)" +
                " group by locationsid" +
                        " union" +
                " select locationsid, locationid, transitionid as category, datetime from tblLocation where transitionid in (0,1)" +
                " union" +
                " select locationsid, min(locationid), 2  as category, datetime from tblLocation where locationid > " + maxTransitionLocId +
                " group by locationsid" +
//                "(select max(locationid) from tblLocation where (transitionid is null or transitionid <> 0 or transitionid <> 1) )" +
                " union" +
                " select locationsid, max(locationid), 3  as category, datetime from tblLocation where locationid > " + maxTransitionLocId +
                " group by locationsid )" +
//                "(select max(locationid) from tblLocation where (transitionid is null or transitionid <> 0 or transitionid <> 1) ))" +
                " where locationsid = ?" +
                " order by 2";
        cursor = db.rawQuery(selectQuery1, new String[] { locationsid.toString() } );
        // looping through all rows and adding to list
//        Log.i(TAG, " WVS " + selectQuery1);
        ArrayList <String> strReturn = new ArrayList<String>();
        String strRow = "";
        if (cursor.moveToFirst()) {
            do {
                strRow = cursor.getString(0) +
                        "," + cursor.getString(cursor.getColumnIndex("locationid")) +
                        "," + cursor.getString(cursor.getColumnIndex("category")) +
                        "," + cursor.getString(cursor.getColumnIndex("datetime"));
                strReturn.add(strRow);
            } while (cursor.moveToNext());
        }
        cursor.close();
        String LocsId, beginLocId, endLocId, tmpbeginLociId, tmpendLocId,tmpCategory;
        LocsId=beginLocId=endLocId=tmpbeginLociId=tmpendLocId=tmpCategory="";

        if (strReturn.size() == 2) {
//          no pauses+seg, just min max [-2,-1]
//            iTotalTime = getTimeDifferenceLocationsId(locationsid);
            LocsId = strReturn.get(0).split(",")[0];
            beginLocId= strReturn.get(0).split(",")[1];
            endLocId = strReturn.get(1).split(",")[1];
            strTotalTime = getTimeDifferenc_btw_location(LocsId, beginLocId, endLocId );
        } else if (strReturn.size() > 2){
//          at least 1 pause+seg [-2,0,1, 2,-1,3] 2 pauses+no seg [-2,0,1, -1,1]
//          check for segments w/o pauses
            boolean bfreeSeg, bpauseSeg;
            bpauseSeg=bfreeSeg=false;
            for (int i = 0; i < strReturn.size(); i++){
                tmpCategory=strReturn.get(i).split(",")[2];
              if (tmpCategory.equals("2") || tmpCategory.equals("3")){
                  bfreeSeg=true;
                  break;
              }
            }
//            if (!bfreeSeg){
                for (int i = 0; i < strReturn.size(); i++){  // i+=3
                    tmpCategory=strReturn.get(i).split(",")[2];
                    LocsId = strReturn.get(i).split(",")[0];
                    beginLocId= strReturn.get(i).split(",")[1];

                    if(Integer.parseInt(tmpCategory) != 0) {
                        if (i+2 <= strReturn.size()) {
                            endLocId = strReturn.get(i + 2).split(",")[1];
                            if (i == 0) {
                                strTotalTime = getTimeDifferenc_btw_location(LocsId, beginLocId, endLocId);
                            } else {
                                strTotalTime = strTotalTime + "," + getTimeDifferenc_btw_location(LocsId, beginLocId, endLocId);
                            }
                            i += 2;
                        }
                    } else {
                        if (i+1 <= strReturn.size()) {
                            endLocId = strReturn.get(i + 1).split(",")[1];
                            strTotalTime = strTotalTime + "," + getTimeDifferenc_btw_location(LocsId, beginLocId, endLocId);
                            i++;
                        }
                    }
                }
            }
//        }

        db.close(); // Closing database connection
        Log.i(TAG, " WVS getTimeDifference_wPauses_LocationsId = " + locationsid.toString() + ", " + strTotalTime);
        returnTotalTime = SumTotalTimes(strTotalTime);
        return returnTotalTime;
    }

    public String getTimeDifferenc_btw_location(String locationsid, String startLocId, String endLocId){
        String totalTime="0:00:00";
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery1 = "SELECT (printf(\"%02d\",((STRFTIME('%s', lmax.endtime) - STRFTIME('%s', lmin.starttime))/60)/60,1,2) || ':' || " +
                "printf(\"%02d\",(((STRFTIME('%s', lmax.endtime) - STRFTIME('%s', lmin.starttime))/60)%60),1,2) || ':' ||" +
                "printf(\"%02d\", ((STRFTIME('%s', lmax.endtime) - STRFTIME('%s', lmin.starttime))%60) ,1,2) ) totaltime" +
                " FROM " +
                " (select locationsid, locationid, DateTime EndTime from tblLocation) lmax " +
                " inner join (select locationsid, locationid, DateTime StartTime from tblLocation) lmin on lmax.locationsid = lmin.locationsid " +
                " where  lmax.locationsid =? and lmax.locationid = ? and lmin.locationid = ?";
        Cursor cursor = db.rawQuery(selectQuery1, new String[] { locationsid.toString(), endLocId.toString(), startLocId.toString() } );
        // looping through all rows and adding to list
//        Log.i(TAG, " WVS " + selectQuery1);

        if (cursor.moveToFirst()) {
            totalTime = cursor.getString(cursor.getColumnIndex("totaltime"));
        }
        cursor.close();
        return totalTime;
    }
    public void OnPauseSetEnterExit(Long locationsid){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selectQuery="";
        String selectQuery2="";
        String selectQuery3="";
        ArrayList <String> strReturn = new ArrayList<String>();
        ArrayList <String> strReturn2 = new ArrayList<String>();
//      test setup
//        selectQuery2 = "UPDATE " + DBInfo.TABLE_LOCATION +
//                " SET " + DBInfo.TABLE_LOCATION_KEY_Transition + " = 0" +
//                " WHERE " + DBInfo.TABLE_LOCATION_KEY_LocationsId + " = " + locationsid.toString() +
//                " AND " + DBInfo.TABLE_LOCATION_KEY_LocationId + " = 1" ;
//        db.execSQL(selectQuery2);
//        selectQuery2 = "UPDATE " + DBInfo.TABLE_LOCATION +
//                " SET " + DBInfo.TABLE_LOCATION_KEY_Transition + " = 1" +
//                " WHERE " + DBInfo.TABLE_LOCATION_KEY_LocationsId + " = " + locationsid.toString() +
//                " AND " + DBInfo.TABLE_LOCATION_KEY_LocationId + " = 12" ;
//        db.execSQL(selectQuery2);

//      GET LATEST ENTER, EXIT COMBOS
        selectQuery = "select enter.locationsid, enter.enter, exit.exit from " +
                "(select locationsid, transitionid, max(locationid) enter from tblLocation where transitionid = 0 group by locationsid, transitionid ) enter " +
                " left outer join (select locationsid, transitionid, max(locationid) exit from tblLocation where transitionid = 1 group by locationsid, transitionid) exit on enter.locationsid = exit.locationsid " +
                " where enter.locationsid = ? ";

        String strRow = "";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{locationsid.toString()} );
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                strRow = cursor.getString(0) +
                        "," + cursor.getString(cursor.getColumnIndex("enter")) +
                        "," + cursor.getString(cursor.getColumnIndex("exit"));
                strReturn.add(strRow);
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (strReturn.size() == 1) {
            strRow = "";
            strRow = strReturn.get(0);
//          if exit only [locationid,enter_id=null,exit_id], set max > enter
            if(strRow.split(",")[1].trim().equals("") && !strRow.split(",")[2].trim().equals("")) {
//              TRANSITION ENTRY
                selectQuery2 = "UPDATE " + DBInfo.TABLE_LOCATION +
                        " SET " + DBInfo.TABLE_LOCATION_KEY_Transition + " = 0" +
                        " WHERE " + DBInfo.TABLE_LOCATION_KEY_LocationsId + " = " + strRow.split(",")[0] +
                        " AND " + DBInfo.TABLE_LOCATION_KEY_LocationId + " = " + strRow.split(",")[1];
                db.execSQL(selectQuery2);
            } else {
//              if enter and exit [locationid,enter_id,exit_id], determine if exists > exit_id then update min/max
                String beginLocId = strRow.split(",")[2];
                selectQuery2 = "select enter.locationsid, enter.enter, exit.exit" +
                        " from (select locationsid, transitionid, min(locationid) enter from tblLocation where locationid > " + beginLocId + " group by locationsid, transitionid ) enter" +
                        " left outer join" +
                        " (select locationsid, transitionid, max(locationid) exit  from tblLocation where locationid > " + beginLocId + " group by locationsid, transitionid) exit on enter.locationsid = exit.locationsid" +
                        " where enter.locationsid = ?";

                strRow = "";
                cursor = db.rawQuery(selectQuery2, new String[]{locationsid.toString()} );
                // looping through all rows and adding to list

                if (cursor.moveToFirst()) {
                    do {
                        strRow = cursor.getString(0) +
                                "," + cursor.getString(cursor.getColumnIndex("enter")) +
                                "," + cursor.getString(cursor.getColumnIndex("exit"));
                        strReturn2.add(strRow);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                if (strReturn2.size() == 0){
//                  do nothing, no more rows to updated
                } else {
                    strRow = "";
                    strRow = strReturn2.get(0);
//              TRANSITION ENTRY
                    selectQuery2 = "UPDATE " + DBInfo.TABLE_LOCATION +
                            " SET " + DBInfo.TABLE_LOCATION_KEY_Transition + " = 0" +
                            " WHERE " + DBInfo.TABLE_LOCATION_KEY_LocationsId + " = " + strRow.split(",")[0] +
                            " AND " + DBInfo.TABLE_LOCATION_KEY_LocationId + " = " + strRow.split(",")[1];
                    db.execSQL(selectQuery2);

//              TRANSITION EXIT
                    selectQuery2 = "UPDATE " + DBInfo.TABLE_LOCATION +
                            " SET " + DBInfo.TABLE_LOCATION_KEY_Transition + " = 1" +
                            " WHERE " + DBInfo.TABLE_LOCATION_KEY_LocationsId + " = " + strRow.split(",")[0] +
                            " AND " + DBInfo.TABLE_LOCATION_KEY_LocationId + " = " + strRow.split(",")[2];
                    db.execSQL(selectQuery2);
                }
            }
        } else if (strReturn.size() == 0) {
//          set min to 0, max to 1
            selectQuery2 = "select locationsid, transitionid, min(locationid) enter, max(locationid) exit from tblLocation where locationsid = ?";

            strRow = "";
            cursor = db.rawQuery(selectQuery2 , new String[]{locationsid.toString()} );
            // looping through all rows and adding to list

            if (cursor.moveToFirst()) {
                do {
                    strRow = cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_LocationsId)) +
                            "," + cursor.getString(cursor.getColumnIndex("enter")) +
                            "," + cursor.getString(cursor.getColumnIndex("exit"));
                    strReturn2.add(strRow);
                } while (cursor.moveToNext());
            }
            cursor.close();
            if (strReturn2.size() == 1){
//              TRANSITION ENTRY
                strRow = strReturn2.get(0);
                selectQuery2 = "UPDATE " + DBInfo.TABLE_LOCATION +
                        " SET " + DBInfo.TABLE_LOCATION_KEY_Transition + " = 0" +
                        " WHERE " + DBInfo.TABLE_LOCATION_KEY_LocationsId + " = " + strRow.split(",")[0] +
                        " AND " + DBInfo.TABLE_LOCATION_KEY_LocationId + " = " + strRow.split(",")[1];
                db.execSQL(selectQuery2);

//              TRANSITION EXIT
                selectQuery2 = "UPDATE " + DBInfo.TABLE_LOCATION +
                        " SET " + DBInfo.TABLE_LOCATION_KEY_Transition + " = 1" +
                        " WHERE " + DBInfo.TABLE_LOCATION_KEY_LocationsId + " = " + strRow.split(",")[0] +
                        " AND " + DBInfo.TABLE_LOCATION_KEY_LocationId + " = " + strRow.split(",")[2];
                db.execSQL(selectQuery2);
            }
        }
        db.close();
    }

    public LatLng getLastLatLng_LocationsId(Long locationsid){
        LatLng tmpLatLng= null;
        Double dLat, dLng;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT " + DBInfo.TABLE_LOCATION_KEY_Latitude + "," +
                DBInfo.TABLE_LOCATION_KEY_Longitude + "," +
                " max(" + DBInfo.TABLE_LOCATION_KEY_LocationsId + ") " +
                " FROM " + DBInfo.TABLE_LOCATION +
                " WHERE " + DBInfo.TABLE_LOCATION_KEY_LocationsId + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String [] {locationsid.toString() } );
        if (cursor.moveToFirst()) {
            do {
                dLat = cursor.getDouble( cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_Latitude));
                dLng = cursor.getDouble( cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_Longitude));
                tmpLatLng = new LatLng(dLat,dLng);
            } while (cursor.moveToNext());
        }
        return  tmpLatLng;
    }
}
