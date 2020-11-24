package com.endeavor.walter.getout9;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class HealthRepo {
    DBHelper dbHelper;
    public final String TAG = HealthRepo.class.getSimpleName();

    public HealthRepo(){
        dbHelper = new DBHelper();
        DatabaseManager.initializeInstance(dbHelper);
    }

    public HealthRepo(Context context){
        dbHelper = new DBHelper(context);
        DatabaseManager.initializeInstance(dbHelper);
    }
    public int DeleteAll(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int result = db.delete(DBInfo.TABLE_TBLHEALTH, null, null);
        db.close();
        return result;
    }
    public long insert(Health health){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBInfo.TABLE_TBLHEALTH_KEY_Type, health.getType());
        values.put(DBInfo.TABLE_TBLHEALTH_KEY_SourceName, health.getSourceName());
        values.put(DBInfo.TABLE_TBLHEALTH_KEY_SourceVersion, health.getSourceVersion());
        values.put(DBInfo.TABLE_TBLHEALTH_KEY_Unit, health.getUnit());
        values.put(DBInfo.TABLE_TBLHEALTH_KEY_CreationDate, health.getCreationDate());
        values.put(DBInfo.TABLE_TBLHEALTH_KEY_StartDate, health.getStartDate());
        values.put(DBInfo.TABLE_TBLHEALTH_KEY_EndDate, health.getEndDate());
        values.put(DBInfo.TABLE_TBLHEALTH_KEY_Value, health.getValue());
        values.put(DBInfo.TABLE_TBLHEALTH_KEY_LoginId, health.getLoginId());

        long healthid = db.insert(DBInfo.TABLE_TBLHEALTH,null, values);
        db.close();
        return healthid;
    }

    public int update(Health health){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBInfo.TABLE_TBLHEALTH_KEY_Type, health.getType());
        values.put(DBInfo.TABLE_TBLHEALTH_KEY_SourceName, health.getSourceName());
        values.put(DBInfo.TABLE_TBLHEALTH_KEY_SourceVersion, health.getSourceVersion());
        values.put(DBInfo.TABLE_TBLHEALTH_KEY_Unit, health.getUnit());
        values.put(DBInfo.TABLE_TBLHEALTH_KEY_CreationDate, health.getCreationDate());
        values.put(DBInfo.TABLE_TBLHEALTH_KEY_StartDate, health.getStartDate());
        values.put(DBInfo.TABLE_TBLHEALTH_KEY_EndDate, health.getEndDate());
        values.put(DBInfo.TABLE_TBLHEALTH_KEY_Value, health.getValue());
        values.put(DBInfo.TABLE_TBLHEALTH_KEY_LoginId, health.getLoginId());

        int result = db.update(DBInfo.TABLE_TBLHEALTH, values, DBInfo.TABLE_TBLHEALTH_KEY_HealthId + " =?", new String[] {String.valueOf(health.getHealthId())});

        db.close();
        return result;
    }


    public ArrayList<Health> getHealthRecords_basedonDateRange(String[] start_end_dates, String sLoginId, boolean bAllRecords){
//      no longer passing health.type [, String sHealthDataType] , get all types
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sOrderby="";
        String selectQuery = "SELECT  " +
                DBInfo.TABLE_TBLHEALTH_KEY_HealthId + "," +
                DBInfo.TABLE_TBLHEALTH_KEY_Type + "," +
                DBInfo.TABLE_TBLHEALTH_KEY_SourceName + "," +
                DBInfo.TABLE_TBLHEALTH_KEY_SourceVersion + "," +
                DBInfo.TABLE_TBLHEALTH_KEY_Device + "," +
                DBInfo.TABLE_TBLHEALTH_KEY_Unit + "," +
                DBInfo.TABLE_TBLHEALTH_KEY_CreationDate + "," +
                DBInfo.TABLE_TBLHEALTH_KEY_StartDate + "," +
                DBInfo.TABLE_TBLHEALTH_KEY_EndDate + "," +
                DBInfo.TABLE_TBLHEALTH_KEY_Value + "," +
                DBInfo.TABLE_TBLHEALTH_KEY_LoginId +
                " FROM " + DBInfo.TABLE_TBLHEALTH;

        ArrayList<Health> arrayHealth = new ArrayList<Health>();
        Cursor cursor = null;
        if (bAllRecords) {
            cursor = db.rawQuery(selectQuery, null);  //new String[] { notes.getNotesId().toString()
        } else {
            selectQuery = selectQuery +
                    " WHERE " + DBInfo.TABLE_TBLHEALTH_KEY_LoginId + " =? " +
//                    " AND " + DBInfo.TABLE_TBLHEALTH_KEY_Type  + " =? " +
                    " AND " + DBInfo.TABLE_TBLHEALTH_KEY_CreationDate  + " between ? AND ?";
            cursor = db.rawQuery(selectQuery, new String[] {sLoginId, start_end_dates[0], start_end_dates[1]});  //new String[] { notes.getNotesId().toString()  removed , sHealthDataType
        }
//        Log.i(TAG, " WVS getHealth() selectQuery " + selectQuery);
        if (cursor.moveToFirst()) {
            do {
                Health health = new Health(
                        cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_HealthId)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_Type)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_SourceName)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_SourceVersion)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_Device)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_Unit)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_CreationDate)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_StartDate)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_EndDate)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_Value)),
                        cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_LoginId))
                );
                arrayHealth.add(health);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return arrayHealth;
    }

    public Health getSingle_HealthRecord(Health health){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sOrderby="";
        String selectQuery = "SELECT  " +
                DBInfo.TABLE_TBLHEALTH_KEY_HealthId + "," +
                DBInfo.TABLE_TBLHEALTH_KEY_Type + "," +
                DBInfo.TABLE_TBLHEALTH_KEY_SourceName + "," +
                DBInfo.TABLE_TBLHEALTH_KEY_SourceVersion + "," +
                DBInfo.TABLE_TBLHEALTH_KEY_Device + "," +
                DBInfo.TABLE_TBLHEALTH_KEY_Unit + "," +
                DBInfo.TABLE_TBLHEALTH_KEY_CreationDate + "," +
                DBInfo.TABLE_TBLHEALTH_KEY_StartDate + "," +
                DBInfo.TABLE_TBLHEALTH_KEY_EndDate + "," +
                DBInfo.TABLE_TBLHEALTH_KEY_Value + "," +
                DBInfo.TABLE_TBLHEALTH_KEY_LoginId +
                " FROM " + DBInfo.TABLE_TBLHEALTH;

        ArrayList<Health> arrayHealth = new ArrayList<Health>();
        selectQuery = selectQuery +
                " WHERE " + DBInfo.TABLE_TBLHEALTH_KEY_LoginId + " =? " +
                " AND " + DBInfo.TABLE_TBLHEALTH_KEY_Type  + " =? " +
                " AND " + DBInfo.TABLE_TBLHEALTH_KEY_Unit  + " =? " +
                " AND " + DBInfo.TABLE_TBLHEALTH_KEY_CreationDate  + " =? " +
                " AND " + DBInfo.TABLE_TBLHEALTH_KEY_StartDate  + " =? " +
                " AND " + DBInfo.TABLE_TBLHEALTH_KEY_EndDate  + " =? ";
        Cursor cursor = db.rawQuery(selectQuery, new String[] {health.getLoginId().toString(), health.getType(), health.getUnit(), health.getCreationDate(), health.getStartDate(), health.getEndDate()} ); // ,health.getStartDate(),health.getEndDate()

//        Log.i(TAG, " WVS getHealth() selectQuery " + selectQuery);
//        assuming only 1 match
        Health health_out = null;
        if (cursor.moveToFirst()) {
            do {
                health_out = new Health(
                        cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_HealthId)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_Type)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_SourceName)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_SourceVersion)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_Device)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_Unit)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_CreationDate)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_StartDate)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_EndDate)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_Value)),
                        cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_TBLHEALTH_KEY_LoginId))
                );
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return health_out;
    }
    public boolean HealtExist(Health health){
//      wvs todo: select * and return health or null
        boolean bExist = false;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT Health_Id FROM " + DBInfo.TABLE_TBLHEALTH +
                " WHERE " + DBInfo.TABLE_TBLHEALTH_KEY_LoginId + " =? " +
                " AND " + DBInfo.TABLE_TBLHEALTH_KEY_Type  + " =? " +
                " AND " + DBInfo.TABLE_TBLHEALTH_KEY_CreationDate  + " =? " +
                " AND " + DBInfo.TABLE_TBLHEALTH_KEY_StartDate  + " =? " +
                " AND " + DBInfo.TABLE_TBLHEALTH_KEY_EndDate  + " =? ";
        int count = 0;
        Cursor cursor = db.rawQuery(selectQuery, new String[] {health.getLoginId().toString(), health.getType(),health.getCreationDate(), health.getStartDate(), health.getEndDate()} ); // ,health.getStartDate(),health.getEndDate()
        if (cursor.moveToFirst()){
            do {
                count = cursor.getCount();
                if (count>0){
                    bExist = true;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return bExist;
    }

    public void createScript_HealthtoAWSS3_toMysSQL(String[] start_end_dates, String sHealthDataType, Context context){

        String locationsid;
        Locations tmpLocs;
        String filedatepart = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
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
                        //table Health: type, unit, sourcename, unit, screationdate, startdate, enddate, value, loginid
                        // filename: MySQL_health_inserts_walter1_TYPE_DISTANCE_DELTA _20200611110936.txt wvs updated format suffix 7/20/20
                        String sLoginId = Integer.toString(Utils.getCurrentLoginID(context));
                        String sfilename_script = "MySQL_health_inserts_" + Utils.getCurrentLogin(context) + sLoginId + "_" + sHealthDataType + "_" +  filedatepart + ".txt";
                        ScriptFile = new File(sd, sfilename_script);

                        FileOutputStream fOut = new FileOutputStream(ScriptFile);
                        OutputStreamWriter myOutWriter_currentscript = new OutputStreamWriter(fOut);

                        String sfilename_s3_contents = "s3_contents.txt"; //""uploaded_contents.txt";
                        S3ContentsFile = new File(sd, sfilename_s3_contents);
                        FileOutputStream fOut2 = new FileOutputStream(S3ContentsFile, true);  //wvs this will append w/o it will create new
                        OutputStreamWriter myOutWriter2_s3_contents = new OutputStreamWriter(fOut2);
                        myOutWriter2_s3_contents.append("new:" + sfilename_script + "\n");

                        ArrayList<Health> arryHealth = getHealthRecords_basedonDateRange(start_end_dates, sLoginId,false);  // removed , sHealthDataType
//                        String sLoginId;
                        for (int i=0; i < arryHealth.size(); i++) {
                            myOutWriter_currentscript.append("INSERT INTO health (type, sourcename, unit, creationdate, startdate, enddate, value, loginid) SELECT ");
                            myOutWriter_currentscript.append("'" + arryHealth.get(i).getType() + "',");
                            myOutWriter_currentscript.append("'" + arryHealth.get(i).getSourceName() + "',");
                            myOutWriter_currentscript.append("'" + arryHealth.get(i).getUnit() + "',");
                            myOutWriter_currentscript.append("'" + arryHealth.get(i).getCreationDate() + "',");
                            myOutWriter_currentscript.append("'" + arryHealth.get(i).getStartDate() + "',");
                            myOutWriter_currentscript.append("'" + arryHealth.get(i).getEndDate() + "',");
                            myOutWriter_currentscript.append( arryHealth.get(i).getValue() + ",");
                            if (arryHealth.get(i).getLoginId() == 0){
                                sLoginId = "1"; // default to first user
                            } else {
                                sLoginId = arryHealth.get(i).getLoginId().toString();
                            }
                            myOutWriter_currentscript.append( sLoginId);
                            myOutWriter_currentscript.append( " FROM DUAL WHERE NOT EXISTS (SELECT * FROM health WHERE");
                            myOutWriter_currentscript.append( " loginid = " + sLoginId);
                            myOutWriter_currentscript.append( " AND unit = " + "'" + arryHealth.get(i).getUnit() + "'");
                            myOutWriter_currentscript.append( " AND creationdate = " + "'" + arryHealth.get(i).getCreationDate() + "' LIMIT 1);\n");
                        }

                        TimeUnit.SECONDS.sleep(1);
                        try {
                            myOutWriter_currentscript.close();
                        } catch (IOException e){
                            Log.i(TAG, "myOutWriter_currentscript.close() failed: " + e.toString());
                        }
                        fOut.close();
                        MainActivity.uploadFileToAWSS3(sfilename_script, sfilename_script, context);

                        TimeUnit.SECONDS.sleep(1);
                        try {
                            myOutWriter2_s3_contents.close();
                        } catch (IOException e){
                            Log.i(TAG, "myOutWriter2_s3_contents.close() failed: " + e.toString());
                        } finally {
                            fOut2.close();
                            TimeUnit.SECONDS.sleep(1);
                            MainActivity.uploadFileToAWSS3(sfilename_s3_contents,"uploaded_contents.txt", context);
//                            S3ContentsFile.delete();
                        }
                    }
                }
            }
        } catch (Exception e){

        } finally {

        }
    }

    public void createScript_HealthtoAWSS3_toSQLite3(String[] start_end_dates, Context context){
        // removed startendtimes,
        String locationsid;
        Locations tmpLocs;
        String filedatepart = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        File ScriptFile=null;
        File S3ContentsFile=null;
        String Django_health = "getoutapp_health";
        String Django_loginid = "login_id";
        String Android_health = "tblhealth";
        String Android_loginid ="loginid";
        String health_table = Django_health;
        String loginid_column = Django_loginid;

        try{
            String state = Environment.getExternalStorageState();
            String pathToExtStorage = Environment.getExternalStorageDirectory().toString();
            File sd = new File(pathToExtStorage);
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                if (Environment.MEDIA_MOUNTED.equals(state) ||
                        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                    if (sd.canWrite()) {
                        //table Health: type, unit, sourcename, unit, screationdate, startdate, enddate, value, loginid
                        // filename: MySQL_health_inserts_walter1_TYPE_DISTANCE_DELTA _20200611110936.txt wvs updated format suffix 7/20/20
                        String sLoginId = Integer.toString(Utils.getCurrentLoginID(context));
                        String sfilename_script = "SQLite3_health_inserts_" + Utils.getCurrentLogin(context) + sLoginId + "_" +  filedatepart + ".txt";  //removed + sHealthDataType
                        ScriptFile = new File(sd, sfilename_script);

                        FileOutputStream fOut = new FileOutputStream(ScriptFile);
                        OutputStreamWriter myOutWriter_currentscript = new OutputStreamWriter(fOut);

                        String sfilename_s3_contents = "s3_contents.txt"; //""uploaded_contents.txt";
                        S3ContentsFile = new File(sd, sfilename_s3_contents);
                        FileOutputStream fOut2 = new FileOutputStream(S3ContentsFile, true);  //wvs this will append w/o it will create new
                        OutputStreamWriter myOutWriter2_s3_contents = new OutputStreamWriter(fOut2);
                        myOutWriter2_s3_contents.append("new:" + sfilename_script + "\n");

                        ArrayList<Health> arryHealth = getHealthRecords_basedonDateRange(start_end_dates, sLoginId,false);  // removed , sHealthDataType
//                        String sLoginId;
                        for (int i=0; i < arryHealth.size(); i++) {
                            myOutWriter_currentscript.append("INSERT INTO "+ health_table + " (type, sourcename, unit, creationdate, startdate, enddate, value, " + loginid_column + ") SELECT ");
                            myOutWriter_currentscript.append("'" + arryHealth.get(i).getType() + "',");
                            myOutWriter_currentscript.append("'" + arryHealth.get(i).getSourceName() + "',");
                            myOutWriter_currentscript.append("'" + arryHealth.get(i).getUnit() + "',");
                            myOutWriter_currentscript.append("'" + arryHealth.get(i).getCreationDate() + "',");
                            myOutWriter_currentscript.append("'" + arryHealth.get(i).getStartDate() + "',");
                            myOutWriter_currentscript.append("'" + arryHealth.get(i).getEndDate() + "',");
                            myOutWriter_currentscript.append( arryHealth.get(i).getValue() + ",");
                            if (arryHealth.get(i).getLoginId() == 0){
                                sLoginId = "1"; // default to first user
                            } else {
                                sLoginId = arryHealth.get(i).getLoginId().toString();
                            }
                            myOutWriter_currentscript.append( sLoginId);
                            myOutWriter_currentscript.append( " WHERE NOT EXISTS (SELECT * FROM "+ health_table + " WHERE ");
                            myOutWriter_currentscript.append( loginid_column + " = " + sLoginId);
                            myOutWriter_currentscript.append( " AND unit = " + "'" + arryHealth.get(i).getUnit() + "'");
                            myOutWriter_currentscript.append( " AND creationdate = " + "'" + arryHealth.get(i).getCreationDate() + "'");
                            myOutWriter_currentscript.append( " AND startdate = " + "'" + arryHealth.get(i).getStartDate() + "'");
                            myOutWriter_currentscript.append( " AND enddate = " + "'" + arryHealth.get(i).getEndDate() + "' LIMIT 1);\n");
                        }

                        TimeUnit.SECONDS.sleep(1);
                        try {
                            myOutWriter_currentscript.close();
                        } catch (IOException e){
                            Log.i(TAG, "myOutWriter_currentscript.close() failed: " + e.toString());
                        }
                        fOut.close();
                        MainActivity.uploadFileToAWSS3(sfilename_script, sfilename_script, context);

                        TimeUnit.SECONDS.sleep(1);
                        try {
                            myOutWriter2_s3_contents.close();
                        } catch (IOException e){
                            Log.i(TAG, "myOutWriter2_s3_contents.close() failed: " + e.toString());
                        } finally {
                            fOut2.close();
                            TimeUnit.SECONDS.sleep(1);
                            MainActivity.uploadFileToAWSS3(sfilename_s3_contents,"uploaded_contents.txt", context);
//                            S3ContentsFile.delete();
                        }
                    }
                }
            }
        } catch (Exception e){

        } finally {

        }
    }
}


