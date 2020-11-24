package com.endeavor.walter.getout9;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLLOGIN;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLLOGIN_KEY_Email;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLLOGIN_KEY_ID;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLLOGIN_KEY_Name;


public class LoginRepo {

    DBHelper dbHelper;
    public final String TAG =  LoginRepo.class.getSimpleName();// "LocationRepo";

    public LoginRepo(){
        dbHelper = new DBHelper();
        DatabaseManager.initializeInstance(dbHelper);
    }

    public LoginRepo(Context context){
        dbHelper = new DBHelper( context );
        DatabaseManager.initializeInstance(dbHelper);
    }

    public long insert (Login login) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TABLE_TBLLOGIN_KEY_ID, login.getLoginId());
        values.put(TABLE_TBLLOGIN_KEY_Name, login.getUsername());
        values.put(DBInfo.TABLE_TBLLOGIN_KEY_Email, login.getEmail());
        // Inserting Row
        long activities_id = db.insert(DBInfo.TABLE_TBLLOGIN,null,values);
        db.close(); // Closing database connection
        return activities_id;
    }

    public int delete(int user_id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        int result = db.delete(TABLE_TBLLOGIN,   TABLE_TBLLOGIN_KEY_ID + "= ?", new String[] { String.valueOf(user_id) });
        Log.i(TAG, " WVS delete " + user_id + ", result = " + result);
        db.close(); // Closing database connection
        return result;
    }

    public int deleteAll() {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int result = db.delete(DBInfo.TABLE_TBLLOGIN, null, null);
        db.close();

        Log.i(TAG, " WVS deleted all rows " +  ", result = " + result);
        return result;
    }
    public Login getLoginbyId(int id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                TABLE_TBLLOGIN_KEY_ID + "," +
                TABLE_TBLLOGIN_KEY_Name + "," +
                TABLE_TBLLOGIN_KEY_Email +
                " FROM " + TABLE_TBLLOGIN +
                " WHERE " + TABLE_TBLLOGIN_KEY_ID + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        int iCount =0;
        Integer mId=null;
        String mName="";
        String mEmail = "";

//        Log.i(TAG, " WVS userid "+ id + ", getLoginbyId() " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(id) } );

        if (cursor.moveToFirst()) {
            do {
                mId = cursor.getInt(cursor.getColumnIndex(TABLE_TBLLOGIN_KEY_ID));
                mName = cursor.getString(cursor.getColumnIndex(TABLE_TBLLOGIN_KEY_Name));
                mEmail = cursor.getString(cursor.getColumnIndex(TABLE_TBLLOGIN_KEY_Email));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        Login tmpLogin = new Login(mId, mName, mEmail);
        return tmpLogin;
    }
    public ArrayList<Login> getAllLogins(){

        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sOrderby=" ORDER BY " + TABLE_TBLLOGIN_KEY_ID;

        String selectQuery =  "SELECT  " +
                TABLE_TBLLOGIN_KEY_ID + "," +
                TABLE_TBLLOGIN_KEY_Name + "," +
                DBInfo.TABLE_TBLLOGIN_KEY_Email +
                " FROM " + DBInfo.TABLE_TBLLOGIN +
                sOrderby;
//        Log.i(TAG, " WVS getAllLogins " + selectQuery);
        ArrayList<Login> loginArrayList = new ArrayList<Login>();
        Cursor cursor = db.rawQuery(selectQuery, null );
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                Login tmpLogin = new Login(
                        cursor.getInt(cursor.getColumnIndex(TABLE_TBLLOGIN_KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(TABLE_TBLLOGIN_KEY_Name)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLLOGIN_KEY_Email)));

                loginArrayList.add(tmpLogin);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return loginArrayList;
    }


}
