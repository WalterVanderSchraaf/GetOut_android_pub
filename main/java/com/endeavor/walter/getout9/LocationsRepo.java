package com.endeavor.walter.getout9;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLGEOLOCATION;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLGEOLOCATION_KEY_Enabled;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLGEOLOCATION_KEY_Latitude;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLGEOLOCATION_KEY_Longitude;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLGEOLOCATION_KEY_Name;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLGEOLOCATION_KEY_Within;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLLOGIN;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLPET;
import static com.endeavor.walter.getout9.Utils.appLog;
import static com.endeavor.walter.getout9.WVS_Utils.getDateTimeString_standard;

public class LocationsRepo {

    DBHelper dbHelper;
    public final String TAG = LocationsRepo.class.getSimpleName();//"LocationsRepo";

    public LocationsRepo(){
        dbHelper = new DBHelper();
        DatabaseManager.initializeInstance(dbHelper);
    }

    public LocationsRepo(Context context){
        dbHelper = new DBHelper( context );
        DatabaseManager.initializeInstance(dbHelper);
    }

    public long insert (String pathname, int loginid) {
        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //LocationsId should be a trigger
        values.put(DBInfo.TABLE_LOCATIONS_KEY_PathName, pathname);
        values.put(DBInfo.TABLE_LOCATIONS_KEY_LoginId, loginid);
        values.put(DBInfo.TABLE_LOCATIONS_KEY_DateTime, getDate());

        // Inserting Row
        long locations_id = db.insert(DBInfo.TABLE_LOCATIONS,null,values);
        db.close(); // Closing database connection
        return locations_id;
    }

    public int delete(long locations_id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        int result = db.delete(DBInfo.TABLE_LOCATIONS, DBInfo.TABLE_LOCATIONS_KEY_LocationsId + "= ?", new String[] { String.valueOf(locations_id) });
        db.close(); // Closing database connection
        return result;
    }


    public int updateMoveZoomTo(Locations tmpLoc) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBInfo.TABLE_LOCATIONS_KEY_PathName, tmpLoc.getPathname());
//        values.put(DBInfo.TABLE_LOCATIONS_KEY_DateTime, getDate()); //keep original date of route
        values.put(DBInfo.TABLE_LOCATIONS_KEY_ZoomLevel, tmpLoc.getZoomLevel());
        values.put(DBInfo.TABLE_LOCATIONS_KEY_ZoomTo_Latitude, tmpLoc.getLatitude());
        values.put(DBInfo.TABLE_LOCATIONS_KEY_ZoomTo_Longitude, tmpLoc.getLongitude());

        // It's a good practice to use parameter ?, instead of concatenate string
        int result = db.update(DBInfo.TABLE_LOCATIONS , values, DBInfo.TABLE_LOCATIONS_KEY_LocationsId+ "= ?", new String[] { String.valueOf(tmpLoc.getLocationsId()) });
        db.close(); // Closing database connection
        return result;
    }

    public int updateTotalDistanceLocationsId(Long locationsid, Float totaldistance ) {
        // tmpLocation.distanceBetween is float
        // TYPE_DISTANCE_DELTA is double
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBInfo.TABLE_LOCATIONS_KEY_TotalDistance, totaldistance);
        // It's a good practice to use parameter ?, instead of concatenate string
        int result = db.update(DBInfo.TABLE_LOCATIONS , values, DBInfo.TABLE_LOCATIONS_KEY_LocationsId+ "= ?", new String[] { String.valueOf(locationsid) });
        db.close(); // Closing database connection
        return result;
    }

    public int updateTotalStepsLocationsId(Long locationsid, Long totalsteps){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBInfo.TABLE_LOCATIONS_KEY_TotalSteps, totalsteps);
        // It's a good practice to use parameter ?, instead of concatenate string
        int result = db.update(DBInfo.TABLE_LOCATIONS , values, DBInfo.TABLE_LOCATIONS_KEY_LocationsId+ "= ?", new String[] { String.valueOf(locationsid) });
        db.close(); // Closing database connection
        return result;
    }

    public int updateElevationDiffLocationsId(Long locationsid, Double elevdiff){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBInfo.TABLE_LOCATIONS_KEY_ElevationDifference, elevdiff);
        // It's a good practice to use parameter ?, instead of concatenate string
        int result = db.update(DBInfo.TABLE_LOCATIONS , values, DBInfo.TABLE_LOCATIONS_KEY_LocationsId+ "= ?", new String[] { String.valueOf(locationsid) });
        db.close(); // Closing database connection
        return result;
    }

    public int updateHeartInfoLocationsId(Long locationsid, Float intensity, Integer duration){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBInfo.TABLE_LOCATIONS_KEY_Heart_Intensity, intensity);
        values.put(DBInfo.TABLE_LOCATIONS_KEY_Heart_Time , duration);
        // It's a good practice to use parameter ?, instead of concatenate string
        int result = db.update(DBInfo.TABLE_LOCATIONS , values, DBInfo.TABLE_LOCATIONS_KEY_LocationsId+ "= ?", new String[] { String.valueOf(locationsid) });
        db.close(); // Closing database connection
        return result;
    }

    public int updateMoveInfoLocationsId(Long locationsid, Long duration){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBInfo.TABLE_LOCATIONS_KEY_Move_Time , duration);
        // It's a good practice to use parameter ?, instead of concatenate string
        int result = db.update(DBInfo.TABLE_LOCATIONS , values, DBInfo.TABLE_LOCATIONS_KEY_LocationsId+ "= ?", new String[] { String.valueOf(locationsid) });
        db.close(); // Closing database connection
        return result;
    }

    public int updateCheckedLocationsId(Long locationsid, Integer checked){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBInfo.TABLE_LOCATIONS_KEY_Checked, checked);
        // It's a good practice to use parameter ?, instead of concatenate string
        int result = db.update(DBInfo.TABLE_LOCATIONS , values, DBInfo.TABLE_LOCATIONS_KEY_LocationsId+ "= ?", new String[] { String.valueOf(locationsid) });
        db.close(); // Closing database connection
        return result;
    }

    public Double getElevationDiffLocationsId(Long locationsid){
        Double retDiff = 0d;
        Double dbllow = 0d;
        Double dblhi = 0d;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT MIN(altitude) as low, MAX(altitude) as hi FROM tblLocation WHERE locationsid =?";

        Cursor cursor = db.rawQuery(selectQuery, new String[] { locationsid.toString() } );

        if (cursor.moveToFirst()) {
//            cursor.getString(cursor.getColumnIndex("StartTime"));
            dbllow = cursor.getDouble(cursor.getColumnIndex("low"));
            dblhi = cursor.getDouble(cursor.getColumnIndex("hi"));
            retDiff = dblhi - dbllow;
        }

        return retDiff;

    }

    public int updateTotalTimeLocationsId(Long locationsid){

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        String sTotalTime="";
        int result = -1;
        //select lmax.locationsid, lmax.endtime, lmin.starttime,
//printf("%02d",((STRFTIME('%s', lmax.endtime) - STRFTIME('%s', lmin.starttime))/60)/60,1,2) || ':' ||
//printf("%02d",(((STRFTIME('%s', lmax.endtime) - STRFTIME('%s', lmin.starttime))/60)%60),1,2) || ':' ||
//printf("%02d", ((STRFTIME('%s', lmax.endtime) - STRFTIME('%s', lmin.starttime))%60) ,1,2)
//--substr( ((STRFTIME('%s', lmax.endtime) - STRFTIME('%s', lmin.starttime))%60) ,1,2)
//from
//(select locationsid, max(DateTime) EndTime from tblLocation group by locationsid) lmax
//inner join (select locationsid, min(DateTime) StartTime from tblLocation group by locationsid) lmin on lmax.locationsid = lmin.locationsid
//--where  lmax.locationsid = 14;
//   https://www.sqlite.org/lang_datefunc.html
//-- select datetime, strftime('%s',datetime), strftime('%s',datetime, '-1 hour'), datetime(strftime('%s',datetime, '-1 hour'), 'unixepoch', 'localtime'), datetime(strftime('%s',datetime, '-1 hour'), 'unixepoch') from tblLocations where locationsid = 1;
//-- select datetime('now'), datetime('now','localtime')
//-- SELECT (strftime('%s','now') - strftime('%s','2020-03-31 01:00:00'))/60/60
//-- SELECT (strftime('%s','now')) - (strftime('%s','now', '-1 hour'))
//-- select datetime('now') , datetime('now','-1 hour')

        String selectQuery1 = "SELECT (printf(\"%02d\",((STRFTIME('%s', lmax.endtime) - STRFTIME('%s', lmin.starttime))/60)/60,1,2) || ':' || " +
                "printf(\"%02d\",(((STRFTIME('%s', lmax.endtime) - STRFTIME('%s', lmin.starttime))/60)%60),1,2) || ':' ||" +
                "printf(\"%02d\", ((STRFTIME('%s', lmax.endtime) - STRFTIME('%s', lmin.starttime))%60) ,1,2) ) totaltime" +
                " FROM " +
                " (select locationsid, max(DateTime) EndTime from tblLocation group by locationsid) lmax " +
                " inner join (select locationsid, min(DateTime) StartTime from tblLocation group by locationsid) lmin on lmax.locationsid = lmin.locationsid " +
                " where  lmax.locationsid =? ";
        Cursor cursor = db.rawQuery(selectQuery1, new String[] { locationsid.toString() } );
        // looping through all rows and adding to list
//        appLog(TAG, " WVS " + selectQuery1);

        if (cursor.moveToFirst()) {
                sTotalTime = cursor.getString(cursor.getColumnIndex("totaltime"));
        }
        cursor.close();

        if (!sTotalTime.equals("")) {
            values.put(DBInfo.TABLE_LOCATIONS_KEY_TotalTime, sTotalTime);
            // It's a good practice to use parameter ?, instead of concatenate string
            result = db.update(DBInfo.TABLE_LOCATIONS, values, DBInfo.TABLE_LOCATIONS_KEY_LocationsId + "= ?", new String[]{String.valueOf(locationsid)});
        }
        db.close(); // Closing database connection

        return result;

    }

    public String[] getStartEndTimesbyLocationsId(Long locationsid){

        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
        String[] sStartEndtime={"",""};

        String selectQuery1 = "SELECT lmin.StartTime, lmax.EndTime" +
                " FROM " +
                " (select locationsid, max(DateTime) EndTime from tblLocation group by locationsid) lmax " +
                " inner join (select locationsid, min(DateTime) StartTime from tblLocation group by locationsid) lmin on lmax.locationsid = lmin.locationsid " +
                " where  lmax.locationsid =? ";
        Cursor cursor = db.rawQuery(selectQuery1, new String[] { locationsid.toString() } );
        // looping through all rows and adding to list
//        appLog(TAG, " WVS " + selectQuery1);

        if (cursor.moveToFirst()) {
            sStartEndtime[0] = cursor.getString(cursor.getColumnIndex("StartTime"));
            sStartEndtime[1] = cursor.getString(cursor.getColumnIndex("EndTime"));
        }
        cursor.close();
        db.close();
        return sStartEndtime;
    }
    public Locations getRouteInfobyLocationsId(Long locationsid){
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery =  "SELECT  " +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_LocationsId + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_PathName + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_DateTime + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_ZoomLevel + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_ZoomTo_Latitude + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_ZoomTo_Longitude + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_TotalTime + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_TotalDistance + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_TotalSteps + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_Heart_Intensity + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_Heart_Time + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_Move_Time + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_LoginId + "," +
                "n." + DBInfo.TABLE_TBLNOTE_KEY_Image + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_ElevationDifference + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_Checked +
                " FROM " + DBInfo.TABLE_LOCATIONS + " l" +
                " LEFT OUTER JOIN " + DBInfo.TABLE_TBLNOTE + " n on l." +
                DBInfo.TABLE_LOCATIONS_KEY_LocationsId + " = n." + DBInfo.TABLE_TBLNOTE_KEY_LocationsId +
                " WHERE l." +
                DBInfo.TABLE_LOCATIONS_KEY_LocationsId + "=?";

//        appLog(TAG, " WVS getRouteInfobyLocationsId " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, new String[]{locationsid.toString()});
        Locations tmpLocs;
        if (cursor.moveToFirst()) {
            tmpLocs = new Locations(
                    cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_PathName)),
                    cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_DateTime)),
                    cursor.getLong(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_LocationsId)),
                    cursor.getFloat(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_ZoomLevel)),
                    cursor.getDouble(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_ZoomTo_Latitude)),
                    cursor.getDouble(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_ZoomTo_Longitude)),
                    cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_TotalTime)),
                    cursor.getFloat(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_TotalDistance)),
                    cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_LoginId)),
                    cursor.getLong(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_TotalSteps)),
                    cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_Checked)),
                    cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_Image))
            );
//          wvs hint: instead of changing signature, just add via method
            tmpLocs.setHeartIntensity(cursor.getFloat(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_Heart_Intensity)));
            tmpLocs.setHeartDuration(cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_Heart_Time)));
            tmpLocs.setMoveDuration(cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_Move_Time)));
            tmpLocs.setElevationDifference(cursor.getDouble(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_ElevationDifference)));
        } else {
            tmpLocs=null;
        }
        cursor.close();
        db.close();
        return  tmpLocs;
    }
    public ArrayList<String> getPreviousRoutes(){
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sOrderby=" ORDER BY " + DBInfo.TABLE_LOCATIONS_KEY_LocationsId;

        String selectQuery =  "SELECT  " +
                DBInfo.TABLE_LOCATIONS_KEY_LocationsId + "," +
                DBInfo.TABLE_LOCATIONS_KEY_PathName + "," +
                DBInfo.TABLE_LOCATIONS_KEY_DateTime +
                " FROM " + DBInfo.TABLE_LOCATIONS +
                sOrderby;
//wvs: hint: todo: rawquery orderby
//                + // It's a good practice to use parameter ?, instead of concatenate string
//                " ORDER BY " + sOrderby;

//        appLog(TAG, " WVS getPreviousRoutes " + selectQuery);
        ArrayList<String> routesArrayList = new ArrayList<String>();
        Cursor cursor = db.rawQuery(selectQuery ,null );
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {

                //      getActivityType, getTransitionType, getElapsedRealTimeNanos
                String sId = String.valueOf( cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_LocationsId))) + ".";
                String sPath = cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_PathName));
//                long elapsedtimerealtimenano = cursor.getLong(cursor.getColumnIndex(DBInfo.TABLE_ACTIVITIES_ElapsedRealTimeNanos));
                String sDate = cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_DateTime));
                routesArrayList.add(sId+sPath+sDate);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return routesArrayList;
    }

    public ArrayList<Locations> getPathNamesForList(){
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sOrderby=" ORDER BY l." + DBInfo.TABLE_LOCATIONS_KEY_LocationsId + " desc";

        String selectQuery =  "SELECT  " +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_LocationsId + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_PathName + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_DateTime + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_ZoomLevel + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_ZoomTo_Latitude + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_ZoomTo_Longitude + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_LoginId + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_TotalTime + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_TotalSteps + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_TotalDistance + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_Heart_Intensity + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_Heart_Time + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_Move_Time + "," +
                "n." + DBInfo.TABLE_TBLNOTE_KEY_Image + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_Checked +
                " FROM " + DBInfo.TABLE_LOCATIONS + " l" +
                " LEFT OUTER JOIN " + DBInfo.TABLE_TBLNOTE + " n on l." +
                DBInfo.TABLE_LOCATIONS_KEY_LocationsId + " = n." + DBInfo.TABLE_TBLNOTE_KEY_LocationsId +
                " WHERE EXISTS (SELECT * FROM " + DBInfo.TABLE_LOCATION + " l2 " +
                " WHERE l." + DBInfo.TABLE_LOCATIONS_KEY_LocationsId + " = l2." + DBInfo.TABLE_LOCATIONS_KEY_LocationsId + ") " +
                sOrderby;
//wvs: hint: todo: rawquery orderby
//                + // It's a good practice to use parameter ?, instead of concatenate string
//                " ORDER BY " + sOrderby;

//        appLog(TAG, " WVS getPathNamesForList " + selectQuery);
        ArrayList<Locations> routesArrayList = new ArrayList<Locations>();
        Cursor cursor = db.rawQuery(selectQuery ,null );
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                Locations tmpLocs = new Locations(
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_PathName)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_DateTime)),
                        cursor.getLong(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_LocationsId)),
                        cursor.getFloat(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_ZoomLevel)),
                        cursor.getDouble(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_ZoomTo_Latitude)),
                        cursor.getDouble(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_ZoomTo_Longitude)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_TotalTime)),
                        cursor.getFloat(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_TotalDistance )),
                        cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_LoginId)),
                        cursor.getLong(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_TotalSteps)),
                        cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_Checked)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_Image))
                );
                tmpLocs.setHeartIntensity(cursor.getFloat(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_Heart_Intensity)));
                tmpLocs.setHeartDuration(cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_Heart_Time)));
                tmpLocs.setMoveDuration(cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_Move_Time)));
                routesArrayList.add(tmpLocs);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return routesArrayList;
    }
    public String getDate() {
//        String filedatepart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String datetime = getDateTimeString_standard();
        return datetime;
    }

    public ArrayList <String>  GetPopularRouteNames(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sOrderby=" ORDER BY 2 DESC";

//SELECT
//    pathname, MAX(datetime)
//FROM
//    tblLocations
//UNION ALL SELECT
//    src.*
//FROM
//    (SELECT
//        pathname, COUNT(pathname) cnt
//    FROM
//        tblLocations
//    GROUP BY pathname
//    ORDER BY cnt DESC) src
//WHERE
//    src.pathname <> (SELECT
//            pathname
//        FROM
//            (SELECT
//                pathname, MAX(datetime)
//            FROM
//                tblLocations));
        String selectQuery =  "SELECT  * FROM " +
                "(SELECT  " +
                DBInfo.TABLE_LOCATIONS_KEY_PathName + ", " +
                " MAX(" + DBInfo.TABLE_LOCATION_KEY_DateTime + ")" +
                " FROM " + DBInfo.TABLE_LOCATIONS +
                " UNION ALL " +
                " SELECT SRC.* " +
                " FROM " +
                " (SELECT " +
                DBInfo.TABLE_LOCATIONS_KEY_PathName + ", " +
                " COUNT(" + DBInfo.TABLE_LOCATIONS_KEY_PathName + ") CNT" +
                " FROM " +
                DBInfo.TABLE_LOCATIONS +
                " GROUP BY " + DBInfo.TABLE_LOCATIONS_KEY_PathName +
                " ORDER BY CNT DESC) SRC" +
                " WHERE SRC." + DBInfo.TABLE_LOCATIONS_KEY_PathName + " <> " +
                " (SELECT " +
                DBInfo.TABLE_LOCATIONS_KEY_PathName +
                " FROM "+
                " (SELECT " +
                DBInfo.TABLE_LOCATIONS_KEY_PathName + ", " +
                " MAX(" + DBInfo.TABLE_LOCATION_KEY_DateTime + ")" +
                " FROM " + DBInfo.TABLE_LOCATIONS + ")) AND CNT > 1)" +
                sOrderby; //)

        ArrayList <String> sPathNames = new ArrayList<String>();
        Cursor cursor = db.rawQuery(selectQuery ,null );
        // looping through all rows and adding to list
//        appLog(" WVS selectQuery: ", selectQuery);
        if (cursor.moveToFirst()) {
            do {
//                appLog(TAG, cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_PathName)) + ", " + cursor.getString(1));
                if (cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_PathName)) != null) {
                    sPathNames.add(cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_PathName)));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return sPathNames;
    }

    public int ConnectRoutes_basedonChecked(){
//select l.locationsid, min(l.locationid) as enter, max(l.locationid) as exit from tblLocation l
//where l.locationsid in
//(select distinct ls.locationsid from tblLocations ls
//inner join tblLocation l on ls.locationsid = l.locationsid
//where ls.Checked = 1)
//group by l.locationsid;
        int iRetVal = -1;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT " +
                " l." + DBInfo.TABLE_LOCATION_KEY_LocationsId + "," +
                " MIN( l." + DBInfo.TABLE_LOCATION_KEY_LocationId +") as enter," +
                " MAX(l." + DBInfo.TABLE_LOCATION_KEY_LocationId +") as exit" +
                " FROM " + DBInfo.TABLE_LOCATION + " l" +
                " WHERE l." + DBInfo.TABLE_LOCATION_KEY_LocationsId +
                " IN (SELECT DISTINCT ls." + DBInfo.TABLE_LOCATIONS_KEY_LocationsId +
                " FROM " + DBInfo.TABLE_LOCATIONS + " ls" +
                " INNER JOIN " + DBInfo.TABLE_LOCATION + " l ON ls." +
                DBInfo.TABLE_LOCATIONS_KEY_LocationsId + " = l." + DBInfo.TABLE_LOCATION_KEY_LocationsId +
                " WHERE ls." + DBInfo.TABLE_LOCATIONS_KEY_Checked + " = 1)" +
                " group by l." + DBInfo.TABLE_LOCATION_KEY_LocationsId;

        ArrayList <String> strReturn = new ArrayList<String>();
        String strRow = "";
        Cursor cursor = db.rawQuery(selectQuery ,null );
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                strRow = cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_LOCATION_KEY_LocationsId)) +
                        "," + cursor.getString(cursor.getColumnIndex("enter")) +
                        "," + cursor.getString(cursor.getColumnIndex("exit"));
                strReturn.add(strRow);
                } while (cursor.moveToNext());
            }
        cursor.close();
        if (strReturn.size() == 2){
            iRetVal = 1;
            for (int i = 0; i < strReturn.size(); i++) {
                strRow = strReturn.get(i);
//              TRANSITION ENTRY
                selectQuery = "UPDATE " + DBInfo.TABLE_LOCATION +
                        " SET " + DBInfo.TABLE_LOCATION_KEY_Transition + " = 0" +
                        " WHERE " + DBInfo.TABLE_LOCATION_KEY_LocationsId + " = " + strRow.split(",")[0] +
                        " AND " + DBInfo.TABLE_LOCATION_KEY_LocationId + " = " + strRow.split(",")[1];
                db.execSQL(selectQuery);

//              TRANSITION EXIT
                selectQuery = "UPDATE " + DBInfo.TABLE_LOCATION +
                        " SET " + DBInfo.TABLE_LOCATION_KEY_Transition + " = 1" +
                        " WHERE " + DBInfo.TABLE_LOCATION_KEY_LocationsId + " = " + strRow.split(",")[0] +
                        " AND " + DBInfo.TABLE_LOCATION_KEY_LocationId + " = " + strRow.split(",")[2];
                db.execSQL(selectQuery);
            }
//       UPDATE 2ND ROUTE TO 1ST ROUTE
//       UPDATE LOCATION SET LOCATIONSID = 3 WHERE LOCATIONSID = 4;
            selectQuery = "UPDATE " + DBInfo.TABLE_LOCATION +
                    " SET " + DBInfo.TABLE_LOCATION_KEY_LocationsId + " = " + strReturn.get(0).split(",")[0] +
                    " WHERE " + DBInfo.TABLE_LOCATION_KEY_LocationsId + " = " + strReturn.get(1).split(",")[0];
            db.execSQL(selectQuery);

//       UPDATE 2ND ACTIVITY TO 1ST ACTIVITY
//       UPDATE tblActivities SET LOCATIONSID = 3 WHERE LOCATIONSID = 4;
            selectQuery = "UPDATE " + DBInfo.TABLE_ACTIVITIES +
                    " SET " + DBInfo.TABLE_ACTIVITIES_LocationsId + " = " + strReturn.get(0).split(",")[0] +
                    " WHERE " + DBInfo.TABLE_ACTIVITIES_LocationsId + " = " + strReturn.get(1).split(",")[0];
            db.execSQL(selectQuery);

//       UNCHECK THE 2ND ROUTES
            selectQuery = "UPDATE " + DBInfo.TABLE_LOCATIONS +
                    " SET " + DBInfo.TABLE_LOCATIONS_KEY_Checked + " = 0" +
                    " WHERE " + DBInfo.TABLE_LOCATIONS_KEY_LocationsId + " = " + strReturn.get(1).split(",")[0];
            db.execSQL(selectQuery);

//       COMBINED TOTAL STEPS...
            selectQuery = "UPDATE " + DBInfo.TABLE_LOCATIONS +
                    " SET " + DBInfo.TABLE_LOCATIONS_KEY_TotalSteps + " = (" +
                    " SELECT SUM(" + DBInfo.TABLE_LOCATIONS_KEY_TotalSteps + ") FROM " + DBInfo.TABLE_LOCATIONS +
                    " WHERE " + DBInfo.TABLE_LOCATIONS_KEY_LocationsId + " = " + strReturn.get(0).split(",")[0] +
                    " or " + DBInfo.TABLE_LOCATIONS_KEY_LocationsId +  " = " + strReturn.get(1).split(",")[0] + ") " +
                    " WHERE " + DBInfo.TABLE_LOCATIONS_KEY_LocationsId + " = " + strReturn.get(0).split(",")[0];
            db.execSQL(selectQuery);

//       COMBINED TOTAL DISTANCE...
            selectQuery = "UPDATE " + DBInfo.TABLE_LOCATIONS +
                    " SET " + DBInfo.TABLE_LOCATIONS_KEY_TotalDistance + " = (" +
                    " SELECT SUM(" + DBInfo.TABLE_LOCATIONS_KEY_TotalDistance + ") FROM " + DBInfo.TABLE_LOCATIONS +
                    " WHERE " + DBInfo.TABLE_LOCATIONS_KEY_LocationsId + " = " + strReturn.get(0).split(",")[0] +
                    " or " + DBInfo.TABLE_LOCATIONS_KEY_LocationsId +  " = " + strReturn.get(1).split(",")[0] + ") " +
                    " WHERE " + DBInfo.TABLE_LOCATIONS_KEY_LocationsId + " = " + strReturn.get(0).split(",")[0];
            db.execSQL(selectQuery);

//          COMBINE TOTAL TIME i.e. begin to end includes gaps
            updateTotalTimeLocationsId(Long.parseLong(strReturn.get(0).split(",")[0]));
//          wvs todo lr.getTimeDifference_wPauses_LocationsId(mLocationsId) use value to update db
        }
        db.close();
        return iRetVal;
    }

    public ArrayList<String> getCheckedRoutes(){
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sOrderby=" ORDER BY " + DBInfo.TABLE_LOCATIONS_KEY_LocationsId;

        String selectQuery =  "SELECT  " +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_LocationsId + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_PathName + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_DateTime + "," +
                "l." + DBInfo.TABLE_LOCATIONS_KEY_LoginId + "," +
                "lg." + DBInfo.TABLE_TBLLOGIN_KEY_Name +
                " FROM " + DBInfo.TABLE_LOCATIONS + " l" +
                " LEFT OUTER JOIN " + TABLE_TBLLOGIN + " lg ON l.loginid = lg.loginid " +
                " WHERE l." + DBInfo.TABLE_LOCATIONS_KEY_Checked + " = 1 " +
                sOrderby;
//wvs: hint: todo: rawquery orderby
//                + // It's a good practice to use parameter ?, instead of concatenate string
//                " ORDER BY " + sOrderby;

//        appLog(TAG, " WVS getPreviousRoutes " + selectQuery);
        ArrayList<String> routesArrayList = new ArrayList<String>();
        Cursor cursor = db.rawQuery(selectQuery ,null );
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                //      getActivityType, getTransitionType, getElapsedRealTimeNanos
                String sId = String.valueOf( cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_LocationsId))) + ",";
                String sPath = cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_PathName)) + ",";
//                long elapsedtimerealtimenano = cursor.getLong(cursor.getColumnIndex(DBInfo.TABLE_ACTIVITIES_ElapsedRealTimeNanos));
                String sDate = cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_DateTime)) + ",";
                String sLoginid = cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_LoginId)) + ",";
                String sUser = cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLLOGIN_KEY_Name)) ;

                routesArrayList.add(sId+sPath+sDate+sLoginid+sUser);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return routesArrayList;
    }

    public void _obsolescenceExportRoutes_createScripts(ArrayList <String> checkedRoutes, String DBName){

//        String filedatepart = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
        String filedatepart = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        File ScriptFile=null;
        File ScriptFile1=null;
        File ScriptFile2=null;
        Integer cnt=1;
        try {
            String state = Environment.getExternalStorageState();
            String pathToExtStorage = Environment.getExternalStorageDirectory().toString();
            String locationsid="";
            File sd = new File(pathToExtStorage);
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                if (Environment.MEDIA_MOUNTED.equals(state) ||
                        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                    if (sd.canWrite()) {
                        ScriptFile = new File(sd, "Sqlite_create_mysql_inserts_" + filedatepart + ".sql");

                        FileOutputStream fOut = new FileOutputStream(ScriptFile);
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                        myOutWriter.append(".out mysql_script1.sql\n");

                        ScriptFile1 = new File(sd, "getout_update_mysql_images_" + filedatepart + ".sql");
                        FileOutputStream fOut1 = new FileOutputStream(ScriptFile1);
                        OutputStreamWriter myOutWriter1 = new OutputStreamWriter(fOut1);
                        myOutWriter1.append(".out mysql_copyImages.sql\n");

                        for (int i=0; i < checkedRoutes.size(); i++){  //id, path, datetime
                            locationsid = checkedRoutes.get(i).split(",")[0];
                            myOutWriter.append(".mode insert locations\n");
                            myOutWriter.append("select '(select locationsid from (select max(locationsid)+1 as locationsid from locations) as locs1)', case when loginid = 0 then 1 else ifnull(loginid,1) end, pathname, datetime, zoomlevel, zoomtomlatitude, zoomtolongitude, totaltime, totaldistance, totalsteps, orderby from tblLocations where locationsid = " + locationsid + ";\n");
                            myOutWriter.append(".mode insert location\n");
                            myOutWriter.append("select null, latitude, longitude, altitude, activityid, transitionid, colorpath, accuracy, datetime, '(select locationsid from (select max(locationsid) as locationsid from locations) as locs1)' from tblLocation where locationsid = " + locationsid + " order by locationsid, locationid;\n");
                            myOutWriter.append(".mode insert note\n");
                            myOutWriter.append("select null, noteitem, notetag, CASE WHEN cost = '' then NULL ELSE IFNULL(cost, NULL) end, notedate, image, checked, orderby, null, '(select locationsid from (select max(locationsid) as locationsid from locations) as locs1)' from tblNote where locationsid = " + locationsid + ";\n");
                            myOutWriter1.append("select image from tblNote where locationsid = " + locationsid + ";\n");
                        }
                        myOutWriter.close();
                        fOut.close();
                        myOutWriter1.close();
                        fOut1.close();
//                        echo
//sqlite3.exe "2020-04-14 19-03-17_Maps05.db" < "Sqlite_create_mysql_inserts_2020-04-14 19-03-17.sql"
//Powershell.exe -executionpolicy remotesigned -File  .\PS1_ReplaceQuotes.ps1 "mysql_script1.sql"
                        ScriptFile2 = new File(sd, "getout_update_mysql_" + filedatepart + ".bat.txt");
                        FileOutputStream fOut2 = new FileOutputStream(ScriptFile2);
                        OutputStreamWriter myOutWriter2 = new OutputStreamWriter(fOut2);
                        myOutWriter2.append("sqlite3.exe \"" + DBName + "\" < \"" + ScriptFile.getName() + "\"\n");
                        myOutWriter2.append("Powershell.exe -executionpolicy remotesigned -File  .\\PS1_ReplaceQuotes.ps1 \"mysql_script1_" + filedatepart +".sql\"\n" );
                        myOutWriter2.append("sqlite3.exe \"" + DBName + "\" < \"" + ScriptFile1.getName() + "\"\n");
                        myOutWriter2.append("Powershell.exe -executionpolicy remotesigned -File  .\\PS1_SearchImages.ps1 \"mysql_copyImages.sql\"\n" );
//                        Powershell.exe -executionpolicy remotesigned -File  .\PS1_SearchImages.ps1 "mysql_copyImages.sql"
                        myOutWriter2.close();
                        fOut2.close();
                    }
                }
            }
        } catch (Exception e){

        } finally {
//            return ScriptFile;
        }
    }

    public void createScript_RoutestoAWSS3_toMySQL(ArrayList <String> checkedRoutes, Context context){

        String locationsid;
        Locations tmpLocs;
        String tbl_locations = "locations"; //Django "locations" MySQL
        String tbllocations_id = "locationsid";
        String tbllocations_loginid = "loginid";
        String tbl_location = "location";   //Django "location"
        String tbllocation_id = "locationid";
        String tbllocation_locationsid = "locationsid";
        String sFromDual = " FROM DUAL";

//        String filedatepart = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
        String filedatepart = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        File ScriptFile=null;
        File S3ContentsFile=null;

        try{
            String state = Environment.getExternalStorageState();
            String pathToExtStorage = Environment.getExternalStorageDirectory().toString();
            File sd = new File(pathToExtStorage);
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                if (Environment.MEDIA_MOUNTED.equals(state) ||
                        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                    if (sd.canWrite()) {
                        //checkedRoutes: locationsid, path, datetime, loginid, username  filename: MySQL_route_inserts_UsernameLoginid_LocationsId_filepart.txt 7/20/20 format suffix change
                        String sfilename_script = "MySQL_route_inserts_" + checkedRoutes.get(0).split(",")[4] + checkedRoutes.get(0).split(",")[3] + "_" + checkedRoutes.get(0).split(",")[0] + "_" +filedatepart + ".txt";
//                      wvs 7/20/20 check tblScripts to see if this was uploaded already
                        String stubfilename = sfilename_script.substring(0, sfilename_script.length() - "_20200611110936.txt".length() + 3) + "%";
                        ScriptsRepo sr = new ScriptsRepo(context);
                        boolean bFileWasUploaded = sr.ScriptExistByName(stubfilename);
                        if (!bFileWasUploaded) {
                            ScriptFile = new File(sd, sfilename_script);
                            String sImages ="";

                            FileOutputStream fOut = new FileOutputStream(ScriptFile);
                            OutputStreamWriter myOutWriter_currentscript = new OutputStreamWriter(fOut);
                            LocationRepo lr = new LocationRepo(context);

    //                      get uploaded_contents.txt from s3, then write to it next updates
                            String sfilename_s3_contents = "s3_contents.txt"; //""uploaded_contents.txt";
    //                        MainActivity.downloadFileFromAWSS3(sfilename_s3_contents,context);
                            S3ContentsFile = new File(sd, sfilename_s3_contents);
                            FileOutputStream fOut2 = new FileOutputStream(S3ContentsFile, true);  //wvs this will append w/o it will create new
    //                        FileOutputStream fOut2 = new FileOutputStream(S3ContentsFile);
                            OutputStreamWriter myOutWriter2_s3_contents = new OutputStreamWriter(fOut2);
//                            appLog(TAG, " WVS storage call list files from AWS S#");
    //                        MainActivity.listFilesFromAWSS3(myOutWriter2_s3_contents);
//                            appLog(TAG, " WVS storage returned from and waiting call list files from AWS S#");

//                            appLog(TAG, " WVS storage list AFTER myOutWriter2_s3_contents.wait();");
                            myOutWriter2_s3_contents.append("new:" + sfilename_script + "\n");

                            for (int i=0; i < checkedRoutes.size(); i++) {
                                locationsid = checkedRoutes.get(i).split(",")[0];
                                tmpLocs = getRouteInfobyLocationsId(Long.parseLong(locationsid));
    //                            myOutWriter_currentscript.append("INSERT INTO locations VALUES((select locationsid from (select max(locationsid)+1 as locationsid from locations) as locs1),");
                                myOutWriter_currentscript.append("INSERT INTO locations (locationsid, loginid, pathname, datetime, zoomlevel, zoomtolatitude, zoomtolongitude, totaltime, totaldistance, totalsteps, orderby)");
                                myOutWriter_currentscript.append(" SELECT (SELECT locationsid from (select max(locationsid)+1 as locationsid from locations) as locsid),");
                                if (tmpLocs.getUserId() == 0){
                                    myOutWriter_currentscript.append( "1,"); // default to first user
                                } else {
                                    myOutWriter_currentscript.append(tmpLocs.getUserId() + ",");
                                }
                                myOutWriter_currentscript.append("'" + tmpLocs.getPathname() + "',");
                                myOutWriter_currentscript.append("'" + tmpLocs.getDatetime() + "',");
                                myOutWriter_currentscript.append(tmpLocs.getZoomLevel() + ",");
                                myOutWriter_currentscript.append(tmpLocs.getLatitude() + ",");
                                myOutWriter_currentscript.append(tmpLocs.getLongitude() + ",");
                                myOutWriter_currentscript.append("'" + tmpLocs.getTotalTime() + "',");
                                myOutWriter_currentscript.append(tmpLocs.getTotalDistance() + ",");
                                myOutWriter_currentscript.append(tmpLocs.getTotalSteps() + ",");
                                myOutWriter_currentscript.append( sFromDual + " WHERE NOT EXISTS (SELECT * FROM locations WHERE loginid = ");
                                if (tmpLocs.getUserId() == 0){
                                    myOutWriter_currentscript.append( "1 AND "); // default to first user
                                } else {
                                    myOutWriter_currentscript.append(tmpLocs.getUserId() + " AND ");
                                }
                                myOutWriter_currentscript.append("pathname = ");
                                myOutWriter_currentscript.append("'" + tmpLocs.getPathname() + "' AND ");
                                myOutWriter_currentscript.append("datetime = ");
                                myOutWriter_currentscript.append("'" + tmpLocs.getDatetime() + "' LIMIT 1);\n");

                                ArrayList<Location_foo> arrLoc = lr.getRouteDetailsByLocationsId(Long.parseLong(locationsid));
                                for (int j=0; j < arrLoc.size(); j++ ){
                                    myOutWriter_currentscript.append("INSERT INTO location VALUES(NULL,");
                                    myOutWriter_currentscript.append(arrLoc.get(j).getLatitude() +",");
                                    myOutWriter_currentscript.append(arrLoc.get(j).getLongitude() +",");
                                    myOutWriter_currentscript.append(arrLoc.get(j).getAltitude() +",");
                                    myOutWriter_currentscript.append(arrLoc.get(j).getActivityId() +",");
                                    myOutWriter_currentscript.append(arrLoc.get(j).getTransitionId() +",");
                                    myOutWriter_currentscript.append(arrLoc.get(j).getColorPath() +",");
                                    myOutWriter_currentscript.append(arrLoc.get(j).getAccuracy() +",");
                                    myOutWriter_currentscript.append("'" + arrLoc.get(j).getDateTime() +"',");
                                    myOutWriter_currentscript.append("(select locationsid from (select max(locationsid) as locationsid from locations) as locs1));\n");
                                }
                                NoteRepo nr = new NoteRepo(context);
                                Note tmpnote = nr.getNoteByLocationsId(Long.parseLong(locationsid));
                                if (tmpnote != null) {
                                    myOutWriter_currentscript.append("INSERT INTO note VALUES(NULL,");
                                    myOutWriter_currentscript.append("'" + tmpnote.getNoteItem() + "',");
                                    myOutWriter_currentscript.append("'" + tmpnote.getTag() + "',");
                                    if(tmpnote.getCost().equals("")){
                                        myOutWriter_currentscript.append("NULL,");
                                    } else {
                                        myOutWriter_currentscript.append(tmpnote.getCost() + ",");
                                    }
                                    myOutWriter_currentscript.append("'" + tmpnote.getDate() + "',");
                                    myOutWriter_currentscript.append("'" + tmpnote.getImage() + "',");
                                    myOutWriter_currentscript.append( tmpnote.getChecked() + ",");
                                    myOutWriter_currentscript.append( "NULL,");
                                    myOutWriter_currentscript.append( "NULL,");
                                    myOutWriter_currentscript.append("(select locationsid from (select max(locationsid) as locationsid from locations) as locs1));\n");
    //                              wvs todo: collect all the images to upload
                                    if (tmpnote.getImage().length() > 0){
                                        if (sImages.length() == 0){
                                            sImages = tmpnote.getImage();
                                        } else {
                                            sImages = sImages + "," + tmpnote.getImage();
                                        }
                                    }
                                }
                            }
                            TimeUnit.SECONDS.sleep(1);
                            try {
                                myOutWriter_currentscript.close();
                            } catch (IOException e){
                                appLog(TAG, "myOutWriter_currentscript.close() failed: " + e.toString());
                            }
                            fOut.close();
    //                      wvs todo: upload sfilename and all the images. after success delete only script not images
                            appLog(TAG," WVS AWS Amplify script = " + sfilename_script);
                            MainActivity.uploadFileToAWSS3(sfilename_script, sfilename_script, context);
                            appLog(TAG," WVS AWS Amplify images = " + sImages);
                            String sImage = "";
                            if (sImages.length() > 0){
                                for( int i = 0; i < sImages.split(",").length; i++){
                                    sImage = sImages.split(",")[i];
                                    MainActivity.uploadFileToAWSS3(sImage, sImage, context);
                                    myOutWriter2_s3_contents.append("new:"+sImage + "\n");
                                }
                            }
    //                        MainActivity.listFilesFromAWSS3(myOutWriter2_s3_contents);  //todo: wvs test
                            TimeUnit.SECONDS.sleep(1);
                            try {
                                myOutWriter2_s3_contents.close();
                            } catch (IOException e){
                                appLog(TAG, "myOutWriter2_s3_contents.close() failed: " + e.toString());
                            } finally {
                                fOut2.close();
                                TimeUnit.SECONDS.sleep(1);
                                MainActivity.uploadFileToAWSS3(sfilename_s3_contents,"uploaded_contents.txt", context);
    //                            S3ContentsFile.delete();
                            }
                        } else {
                            Toast.makeText(context,"File was already uploaded to cloud",Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        } catch (Exception e){

        } finally {

        }
    }

    public void createScript_RoutestoAWSS3_toSQLite3(ArrayList <String> checkedRoutes, Context context){

        String locationsid;
        Locations tmpLocs;
        String tbl_locations = "getoutapp_locations"; //Django "locations" sqlite3
        String tbllocations_id = "id";
        String tbllocations_loginid = "login_id";
        String tbl_location = "getoutapp_location";   //Django "location"
        String tbllocation_id = "id";
        String tbllocation_locationsid = "locations_id";
        String sFromDual = ""; //SQLite3 does not use " FROM DUAL"

//        String filedatepart = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
        String filedatepart = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        File ScriptFile=null;
        File S3ContentsFile=null;

        try{
            String state = Environment.getExternalStorageState();
            String pathToExtStorage = Environment.getExternalStorageDirectory().toString();
            File sd = new File(pathToExtStorage);
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                if (Environment.MEDIA_MOUNTED.equals(state) ||
                        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                    if (sd.canWrite()) {
                        //checkedRoutes: locationsid, path, datetime, loginid, username  filename: MySQL_route_inserts_UsernameLoginid_LocationsId_filepart.txt 7/20/20 format suffix change
                        String sfilename_script = "SQLite3_route_inserts_" + checkedRoutes.get(0).split(",")[4] + checkedRoutes.get(0).split(",")[3] + "_" + checkedRoutes.get(0).split(",")[0] + "_" +filedatepart + ".txt";
//                      wvs 7/20/20 check tblScripts to see if this was uploaded already
                        String stubfilename = sfilename_script.substring(0, sfilename_script.length() - "_20200611110936.txt".length() + 3) + "%";
                        ScriptsRepo sr = new ScriptsRepo(context);
                        boolean bFileWasUploaded = sr.ScriptExistByName(stubfilename);
                        if (!bFileWasUploaded) {
                            ScriptFile = new File(sd, sfilename_script);
                            String sImages ="";

                            FileOutputStream fOut = new FileOutputStream(ScriptFile);
                            OutputStreamWriter myOutWriter_currentscript = new OutputStreamWriter(fOut);
                            LocationRepo lr = new LocationRepo(context);

                            //                      get uploaded_contents.txt from s3, then write to it next updates
                            String sfilename_s3_contents = "s3_contents.txt"; //""uploaded_contents.txt";
                            //                        MainActivity.downloadFileFromAWSS3(sfilename_s3_contents,context);
                            S3ContentsFile = new File(sd, sfilename_s3_contents);
                            FileOutputStream fOut2 = new FileOutputStream(S3ContentsFile, true);  //wvs this will append w/o it will create new
                            //                        FileOutputStream fOut2 = new FileOutputStream(S3ContentsFile);
                            OutputStreamWriter myOutWriter2_s3_contents = new OutputStreamWriter(fOut2);
//                            appLog(TAG, " WVS storage call list files from AWS S#");
                            //                        MainActivity.listFilesFromAWSS3(myOutWriter2_s3_contents);
//                            appLog(TAG, " WVS storage returned from and waiting call list files from AWS S#");

//                            appLog(TAG, " WVS storage list AFTER myOutWriter2_s3_contents.wait();");
                            myOutWriter2_s3_contents.append("new:" + sfilename_script + "\n");

                            for (int i=0; i < checkedRoutes.size(); i++) {
                                locationsid = checkedRoutes.get(i).split(",")[0];
                                tmpLocs = getRouteInfobyLocationsId(Long.parseLong(locationsid));
                                //                            myOutWriter_currentscript.append("INSERT INTO locations VALUES((select locationsid from (select max(locationsid)+1 as locationsid from locations) as locs1),");
                                myOutWriter_currentscript.append("INSERT INTO " + tbl_locations + " (" + tbllocations_id + ", " + tbllocations_loginid + ", pathname, datetime, zoomlevel, zoomtolatitude, zoomtolongitude, totaltime, totaldistance, totalsteps, heartminutes, heartpoints, moveminutes)");
                                myOutWriter_currentscript.append(" SELECT (SELECT " + tbllocations_id + " from (select max(" + tbllocations_id + ")+1 as " + tbllocations_id + " from " + tbl_locations + ") as locsid),");
                                if (tmpLocs.getUserId() == 0){
                                    myOutWriter_currentscript.append( "1,"); // default to first user
                                } else {
                                    myOutWriter_currentscript.append(tmpLocs.getUserId() + ",");
                                }
                                myOutWriter_currentscript.append("'" + tmpLocs.getPathname() + "',");
                                myOutWriter_currentscript.append("'" + tmpLocs.getDatetime() + "',");
                                myOutWriter_currentscript.append(tmpLocs.getZoomLevel() + ",");
                                myOutWriter_currentscript.append(tmpLocs.getLatitude() + ",");
                                myOutWriter_currentscript.append(tmpLocs.getLongitude() + ",");
                                myOutWriter_currentscript.append("'" + tmpLocs.getTotalTime() + "',");
                                myOutWriter_currentscript.append(tmpLocs.getTotalDistance() + ",");
                                myOutWriter_currentscript.append(tmpLocs.getTotalSteps() + ",");
                                myOutWriter_currentscript.append(tmpLocs.getHeartDuration() + ",");
                                myOutWriter_currentscript.append(tmpLocs.getHeartIntensity() + ",");
                                myOutWriter_currentscript.append(tmpLocs.getMoveDuration() + "" );
                                myOutWriter_currentscript.append( sFromDual + " WHERE NOT EXISTS (SELECT * FROM " + tbl_locations + " WHERE " + tbllocations_loginid + " = ");
                                if (tmpLocs.getUserId() == 0){
                                    myOutWriter_currentscript.append( "1 AND "); // default to first user
                                } else {
                                    myOutWriter_currentscript.append(tmpLocs.getUserId() + " AND ");
                                }
                                myOutWriter_currentscript.append("pathname = ");
                                myOutWriter_currentscript.append("'" + tmpLocs.getPathname() + "' AND ");
                                myOutWriter_currentscript.append("datetime = ");
                                myOutWriter_currentscript.append("'" + tmpLocs.getDatetime() + "' LIMIT 1);\n");

                                ArrayList<Location_foo> arrLoc = lr.getRouteDetailsByLocationsId(Long.parseLong(locationsid));
                                for (int j=0; j < arrLoc.size(); j++ ){
                                    myOutWriter_currentscript.append("INSERT INTO " + tbl_location + " VALUES(NULL,");
                                    myOutWriter_currentscript.append(arrLoc.get(j).getLatitude() +",");
                                    myOutWriter_currentscript.append(arrLoc.get(j).getLongitude() +",");
                                    myOutWriter_currentscript.append(arrLoc.get(j).getAltitude() +",");
                                    myOutWriter_currentscript.append(arrLoc.get(j).getActivityId() +",");
                                    myOutWriter_currentscript.append(arrLoc.get(j).getTransitionId() +",");
                                    myOutWriter_currentscript.append(arrLoc.get(j).getColorPath() +",");
                                    myOutWriter_currentscript.append(arrLoc.get(j).getAccuracy() +",");
                                    myOutWriter_currentscript.append("'" + arrLoc.get(j).getDateTime() +"',");
                                    myOutWriter_currentscript.append("(select " + tbllocations_id + " from (select max(" + tbllocations_id + ") as " + tbllocations_id + " from " + tbl_locations + ") as locs1));\n");
                                }
                                NoteRepo nr = new NoteRepo(context);
                                Note tmpnote = nr.getNoteByLocationsId(Long.parseLong(locationsid));
                                if (tmpnote != null) {
                                    myOutWriter_currentscript.append("INSERT INTO note VALUES(NULL,");
                                    myOutWriter_currentscript.append("'" + tmpnote.getNoteItem() + "',");
                                    myOutWriter_currentscript.append("'" + tmpnote.getTag() + "',");
                                    if(tmpnote.getCost().equals("")){
                                        myOutWriter_currentscript.append("NULL,");
                                    } else {
                                        myOutWriter_currentscript.append(tmpnote.getCost() + ",");
                                    }
                                    myOutWriter_currentscript.append("'" + tmpnote.getDate() + "',");
                                    myOutWriter_currentscript.append("'" + tmpnote.getImage() + "',");
                                    myOutWriter_currentscript.append( tmpnote.getChecked() + ",");
                                    myOutWriter_currentscript.append( "NULL,");
                                    myOutWriter_currentscript.append( "NULL,");
                                    myOutWriter_currentscript.append("(select " + tbllocations_id + " from (select max(" + tbllocations_id + ") as " + tbllocations_id + " from " + tbl_locations + ") as locs1));\n");
                                    //                              wvs todo: collect all the images to upload
                                    if (tmpnote.getImage().length() > 0){
                                        if (sImages.length() == 0){
                                            sImages = tmpnote.getImage();
                                        } else {
                                            sImages = sImages + "," + tmpnote.getImage();
                                        }
                                    }
                                }
                            }
                            TimeUnit.SECONDS.sleep(1);
                            try {
                                myOutWriter_currentscript.close();
                            } catch (IOException e){
                                appLog(TAG, "myOutWriter_currentscript.close() failed: " + e.toString());
                            }
                            fOut.close();
                            //                      wvs todo: upload sfilename and all the images. after success delete only script not images
                            appLog(TAG," WVS AWS Amplify script = " + sfilename_script);
                            MainActivity.uploadFileToAWSS3(sfilename_script, sfilename_script, context);
                            appLog(TAG," WVS AWS Amplify images = " + sImages);
                            String sImage = "";
                            if (sImages.length() > 0){
                                for( int i = 0; i < sImages.split(",").length; i++){
                                    sImage = sImages.split(",")[i];
                                    MainActivity.uploadFileToAWSS3(sImage, sImage, context);
                                    myOutWriter2_s3_contents.append("new:"+sImage + "\n");
                                }
                            }
                            //                        MainActivity.listFilesFromAWSS3(myOutWriter2_s3_contents);  //todo: wvs test
                            TimeUnit.SECONDS.sleep(1);
                            try {
                                myOutWriter2_s3_contents.close();
                            } catch (IOException e){
                                appLog(TAG, "myOutWriter2_s3_contents.close() failed: " + e.toString());
                            } finally {
                                fOut2.close();
                                TimeUnit.SECONDS.sleep(1);
                                MainActivity.uploadFileToAWSS3(sfilename_s3_contents,"uploaded_contents.txt", context);
                                //                            S3ContentsFile.delete();
                            }
                        } else {
                            Toast.makeText(context,"File was already uploaded to cloud",Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        } catch (Exception e){

        } finally {

        }
    }
    public String[] getLocationbyLocationsIdLatLng(Long locationsid){

        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
        String[] sStartEndtime={"",""};

        String selectQuery1 = "SELECT lmin.StartTime, lmax.EndTime" +
                " FROM " +
                " (select locationsid, max(DateTime) EndTime from tblLocation group by locationsid) lmax " +
                " inner join (select locationsid, min(DateTime) StartTime from tblLocation group by locationsid) lmin on lmax.locationsid = lmin.locationsid " +
                " where  lmax.locationsid =? ";
        Cursor cursor = db.rawQuery(selectQuery1, new String[] { locationsid.toString() } );
        // looping through all rows and adding to list
//        appLog(TAG, " WVS " + selectQuery1);

        if (cursor.moveToFirst()) {
            sStartEndtime[0] = cursor.getString(cursor.getColumnIndex("StartTime"));
            sStartEndtime[1] = cursor.getString(cursor.getColumnIndex("EndTime"));
        }
        cursor.close();
        db.close();
        return sStartEndtime;
    }
    public long insertGeoLocation (String geoname, LatLng latlng) {
        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //LocationsId should be a trigger
        values.put(DBInfo.TABLE_TBLGEOLOCATION_KEY_Name, geoname);
        values.put(DBInfo.TABLE_TBLGEOLOCATION_KEY_Latitude, latlng.latitude);
        values.put(DBInfo.TABLE_TBLGEOLOCATION_KEY_Longitude, latlng.longitude);
        values.put(DBInfo.TABLE_TBLGEOLOCATION_KEY_Enabled, 1);  //default is enabled
//      todo altitude
        // Inserting Row
        long locations_id = db.insert(DBInfo.TABLE_TBLGEOLOCATION,null,values);
        db.close(); // Closing database connection
        return locations_id;
    }

    public String getGeoLocations(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sNameLatLng, name, lat, lng;
        Double dbllat, dbllng;
        Integer enabled, within;
        sNameLatLng = name = lat = lng = "";
        String selectQuery1 = "SELECT " +
                TABLE_TBLGEOLOCATION_KEY_Name + ", " +
                TABLE_TBLGEOLOCATION_KEY_Latitude + ", " +
                TABLE_TBLGEOLOCATION_KEY_Longitude + ", " +
                TABLE_TBLGEOLOCATION_KEY_Enabled + ", " +
                TABLE_TBLGEOLOCATION_KEY_Within +
                " FROM " + TABLE_TBLGEOLOCATION;
        Cursor cursor = db.rawQuery(selectQuery1, null);

        if (cursor.moveToFirst()){
            do {
                name = cursor.getString(cursor.getColumnIndex(TABLE_TBLGEOLOCATION_KEY_Name));
                dbllat = cursor.getDouble(cursor.getColumnIndex(TABLE_TBLGEOLOCATION_KEY_Latitude));
                dbllng = cursor.getDouble(cursor.getColumnIndex(TABLE_TBLGEOLOCATION_KEY_Longitude));
                enabled = cursor.getInt(cursor.getColumnIndex(TABLE_TBLGEOLOCATION_KEY_Enabled));
                within = cursor.getInt(cursor.getColumnIndex(TABLE_TBLGEOLOCATION_KEY_Within));
                if(sNameLatLng.equals("")){
                    sNameLatLng = name + "," + dbllat.toString() + "," + dbllng.toString() + "," + enabled.toString() + "," + within.toString();
                } else {
                    sNameLatLng = sNameLatLng + "," + name + "," + dbllat.toString() + "," + dbllng.toString() + "," + enabled.toString() + "," + within.toString();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return  sNameLatLng;
    }
    public int updateGEOLocation(String title, Integer enabled, Integer within) {
        //tmpLocation.distanceBetween is float
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(TABLE_TBLGEOLOCATION_KEY_Enabled, enabled);
        values.put(TABLE_TBLGEOLOCATION_KEY_Within, within);

        // It's a good practice to use parameter ?, instead of concatenate string
        int result = db.update(TABLE_TBLGEOLOCATION , values, TABLE_TBLGEOLOCATION_KEY_Name + "= ?", new String[] { title });
        db.close(); // Closing database connection
        return result;
    }

    public int resetGEOLocationWithin(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(TABLE_TBLGEOLOCATION_KEY_Within, 0);

        int result = db.update(TABLE_TBLGEOLOCATION , values, null, null);
        db.close(); // Closing database connection
        return result;

    }

    public String getFitSInfo(Long locationsid){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sReturn = "";
        String sTotalSteps, sMoveMinutes, sHeartTime, sHeartIntensity;
        String selectQuery1 = "SELECT " +
                DBInfo.TABLE_LOCATIONS_KEY_TotalSteps + "," +
                DBInfo.TABLE_LOCATIONS_KEY_Move_Time + "," +
                DBInfo.TABLE_LOCATIONS_KEY_Heart_Time + "," +
                DBInfo.TABLE_LOCATIONS_KEY_Heart_Intensity +
                " FROM " +
                DBInfo.TABLE_LOCATIONS +
                " where  locationsid = ? ";
        Cursor cursor = db.rawQuery(selectQuery1, new String[] { locationsid.toString() } );
//        appLog(TAG, " WVS " + selectQuery1);

        if (cursor.moveToFirst()) {
            sTotalSteps = Integer.toString(cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_TotalSteps)));
            sMoveMinutes = Integer.toString(cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_Move_Time)));
            sHeartTime = Integer.toString(cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_Heart_Time)));
            sHeartIntensity = Float.toString(cursor.getFloat(cursor.getColumnIndex(DBInfo.TABLE_LOCATIONS_KEY_Heart_Intensity)));
            sReturn = sTotalSteps + " steps, " + sMoveMinutes + " move min, " + sHeartIntensity + " Heart Pts," + sHeartTime + " min";
        }

        cursor.close();
        db.close();
        return sReturn;
    }
}
