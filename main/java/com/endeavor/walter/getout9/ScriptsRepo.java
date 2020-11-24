package com.endeavor.walter.getout9;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLSCRIPTS;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLSCRIPTS_KEY_ScriptsId;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLSCRIPTS_KEY_ScriptName;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLSCRIPTS_KEY_Bucket;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLSCRIPTS_KEY_BucketPrefix;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLSCRIPTS_KEY_DownloadDate;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLSCRIPTS_KEY_ExecutionDate;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLSCRIPTS_KEY_RowsAffected;

public class ScriptsRepo {
    DBHelper dbHelper;
    public final String TAG =  ScriptsRepo.class.getSimpleName();// "ScriptsRepo";

    public ScriptsRepo() {
        dbHelper = new DBHelper();
        DatabaseManager.initializeInstance(dbHelper);
    }

    public ScriptsRepo(Context context) {
        dbHelper = new DBHelper(context);
        DatabaseManager.initializeInstance(dbHelper);
    }

    public long insert (Scripts scripts) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(TABLE_TBLSCRIPTS_KEY_ScriptName, scripts.getScriptName());
        values.put(TABLE_TBLSCRIPTS_KEY_Bucket, scripts.getBucket());
        values.put(TABLE_TBLSCRIPTS_KEY_BucketPrefix, scripts.getBucketPrefix());
        values.put(TABLE_TBLSCRIPTS_KEY_DownloadDate, scripts.getDownloadDate());
        values.put(TABLE_TBLSCRIPTS_KEY_ExecutionDate, scripts.getExecutionDate());
        values.put(TABLE_TBLSCRIPTS_KEY_RowsAffected, scripts.getRowsAffected());
        // Inserting Row
        long scripts_id = db.insert(TABLE_TBLSCRIPTS,null,values);
        db.close(); // Closing database connection
        return scripts_id;
    }

    public int delete(int scripts_id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        int result = db.delete(TABLE_TBLSCRIPTS,   TABLE_TBLSCRIPTS_KEY_ScriptsId + "= ?", new String[] { String.valueOf(scripts_id) });
        Log.i(TAG, " WVS delete " + scripts_id + ", result = " + result);
        db.close(); // Closing database connection
        return result;
    }

    public boolean ScriptExistByName(String stubfilename){
        boolean bRet;
        Integer count = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selectQuery1 = "SELECT count(scriptname) as returncount" +
                " FROM " + TABLE_TBLSCRIPTS +
                " where  scriptname LIKE ?";
        Cursor cursor = db.rawQuery(selectQuery1, new String[] { stubfilename } );
        // looping through all rows and adding to list
//        Log.i(TAG, " WVS " + selectQuery1);

        if (cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndex("returncount"));
        }
        if (count.equals(0)){
            bRet = false;
        } else {
            bRet = true;
        }

        cursor.close();
        db.close();

        return bRet;
    }
}
