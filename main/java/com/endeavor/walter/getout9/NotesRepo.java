package com.endeavor.walter.getout9;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import static com.endeavor.walter.getout9.DBInfo.KEY_Checked;
import static com.endeavor.walter.getout9.DBInfo.KEY_Orderby;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLNOTES;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLNOTES_KEY_Date;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLNOTES_KEY_Name;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLNOTES_KEY_NotesId;

public class NotesRepo {

    DBHelper dbHelper;
    public final static String TAG = NotesRepo.class.getSimpleName();


    public NotesRepo(Context context  ) {
        dbHelper = new DBHelper(context);
        DatabaseManager.initializeInstance(dbHelper);
    }

    public NotesRepo() {
        dbHelper = new DBHelper();
        DatabaseManager.initializeInstance(dbHelper);
    }

    public long insert(Notes notes) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //NotesId should be a trigger
        values.put(TABLE_TBLNOTES_KEY_Name, notes.getName()); //Notes.KEY_Name
        notes.setDate_Now();
        values.put(TABLE_TBLNOTES_KEY_Date, notes.getName());

        // Inserting Row
        long notes_id = db.insert(TABLE_TBLNOTES,null,values);
        Log.i(TAG, " WVS insert, notesid = " + notes_id);
        db.close(); // Closing database connection
        return (long) notes_id;
    }
    public void RemoveLineNumbers(int notes_id){

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
//   Update tblNote set NoteItem = substr(NoteItem, instr(NoteItem, ". ")+1)  where notesid = 6
        String selectQuery2 =  "Update tblNote set NoteItem = substr(NoteItem, instr(NoteItem, \". \")+2) WHERE " + TABLE_TBLNOTES_KEY_NotesId + "= ?" ;

//        https://stackoverflow.com/questions/9667031/difference-between-rawquery-and-execsql-in-android-sqlite-database
        db.execSQL(selectQuery2, new String[] { String.valueOf(notes_id) });
//      Cursor cursor =  db.rawQuery(selectQuery2, new String[] { String.valueOf(notes_id) });
//        if (cursor.moveToFirst()) {
//        }
//        cursor.close();

        db.close();

    }
    public int delete(int notes_id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        int result = db.delete(TABLE_TBLNOTES,   TABLE_TBLNOTES_KEY_NotesId + "= ?", new String[] { String.valueOf(notes_id) });
        Log.i(TAG, " WVS delete " + notes_id + ", result = " + result);
        db.close(); // Closing database connection
        return result;
    }

    public int update(Notes notes) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(TABLE_TBLNOTES_KEY_Name, notes.getName());
        notes.setDate_Now();
        values.put(TABLE_TBLNOTES_KEY_Date, notes.getDate());
        values.put(KEY_Orderby, notes.getOrderby() );
        values.put(KEY_Checked, notes.getChecked());

        // It's a good practice to use parameter ?, instead of concatenate string
        int result = db.update(TABLE_TBLNOTES, values, TABLE_TBLNOTES_KEY_NotesId + "= ?", new String[] { String.valueOf(notes.getNotesId()) });
        Log.i(TAG, " WVS update " + notes.getNotesId() + ", result = " + result);
        db.close(); // Closing database connection
        return result;
    }

    public ArrayList<Notes> getAllNotes(){

        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +

                TABLE_TBLNOTES_KEY_NotesId + "," +
                TABLE_TBLNOTES_KEY_Name + "," +
                TABLE_TBLNOTES_KEY_Date + "," +
                KEY_Checked + "," +
                KEY_Orderby +
                " FROM " + TABLE_TBLNOTES +
                " ORDER BY " + TABLE_TBLNOTES_KEY_Date + " DESC";

        ArrayList<Notes> notesList = new ArrayList<Notes>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
//        Log.i(TAG, " WVS getAllNotes() " + selectQuery);
        if (cursor.moveToFirst()) {
            do {

                Notes notes = new Notes(cursor.getInt(cursor.getColumnIndex(TABLE_TBLNOTES_KEY_NotesId)),
                        cursor.getString(cursor.getColumnIndex(TABLE_TBLNOTES_KEY_Name)),
                        cursor.getString(cursor.getColumnIndex(TABLE_TBLNOTES_KEY_Date)),
                        cursor.getString(cursor.getColumnIndex(KEY_Orderby)),
                        cursor.getInt(cursor.getColumnIndex(KEY_Checked))
                );

                notesList.add(notes);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return notesList;

    }


    public Notes getNotesById(int notesid){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                TABLE_TBLNOTES_KEY_NotesId + "," +
                TABLE_TBLNOTES_KEY_Name + "," +
                TABLE_TBLNOTES_KEY_Date + "," +
                KEY_Checked + "," +
                KEY_Orderby +
                " FROM " + TABLE_TBLNOTES +
                " WHERE " + TABLE_TBLNOTES_KEY_NotesId + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        int iCount =0;
        Integer mNotesId=null;
        String mName="";
        String mDate = "";
        String mOrderBy = "";
        Integer mChecked=0;

//        Log.i(TAG, " WVS noteid "+ notesid + ", getNotesById() " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(notesid) } );

        if (cursor.moveToFirst()) {
            do {
                mNotesId = cursor.getInt(cursor.getColumnIndex(TABLE_TBLNOTES_KEY_NotesId));
                mName = cursor.getString(cursor.getColumnIndex(TABLE_TBLNOTES_KEY_Name));
                mDate = cursor.getString(cursor.getColumnIndex(TABLE_TBLNOTES_KEY_Date));
                mOrderBy = cursor.getString(cursor.getColumnIndex(KEY_Orderby));
                mChecked =  cursor.getInt(cursor.getColumnIndex(KEY_Checked));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        Notes notes = new Notes(mNotesId,mName,mDate,mOrderBy,mChecked);
        return notes;
    }

    public String  SumCheckedCosts (Integer notesid) {
        String retCost = "";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT SUM(" +
                DBInfo.TABLE_TBLNOTE_KEY_Cost + ") as totalcost" +
                " FROM " + DBInfo.TABLE_TBLNOTE +
                " WHERE " +
                DBInfo.TABLE_TBLNOTE_KEY_Checked + " = 1" + " AND " + DBInfo.TABLE_TBLNOTE_KEY_NotesId + "=?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{notesid.toString()});
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                retCost = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        db.close();
        return retCost;
    }

}
