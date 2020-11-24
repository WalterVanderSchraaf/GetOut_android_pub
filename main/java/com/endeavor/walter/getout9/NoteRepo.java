package com.endeavor.walter.getout9;
//select n.*, ns.name from tblNote n left join tblNotes ns on n.NotesId = ns.NotesId where n.Image <> ""

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class NoteRepo {
    DBHelper dbHelper;
    public final static String TAG = NoteRepo.class.getSimpleName();


    public NoteRepo(Context context  ) {
        dbHelper = new DBHelper(context);
        DatabaseManager.initializeInstance(dbHelper);
    }

    public NoteRepo() {
        dbHelper = new DBHelper();
        DatabaseManager.initializeInstance(dbHelper);
    }

    public long insert(Note note) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //NoteId should be a trigger
        values.put(DBInfo.TABLE_TBLNOTE_KEY_NoteItem, note.getNoteItem());
        note.setDate_Now();
        values.put(DBInfo.TABLE_TBLNOTE_KEY_Date, note.getDate());
        values.put(DBInfo.TABLE_TBLNOTE_KEY_Tag, note.getTag());
        values.put(DBInfo.TABLE_TBLNOTE_KEY_Cost, note.getCost());
        values.put(DBInfo.TABLE_TBLNOTE_KEY_Checked, note.getChecked());
        values.put(DBInfo.TABLE_TBLNOTE_KEY_NotesId, note.getNotesId());
        values.put(DBInfo.TABLE_TBLNOTE_KEY_Image,note.getImage());
        values.put(DBInfo.TABLE_TBLNOTE_KEY_LocationsId, note.getLocationsId());
        // Inserting Row
        long note_id = db.insert(DBInfo.TABLE_TBLNOTE,null,values);
        db.close(); // Closing database connection
        return note_id;
    }

    public int delete(int note_id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        int result = db.delete(DBInfo.TABLE_TBLNOTE, DBInfo.TABLE_TBLNOTE_KEY_Noteid + "= ?", new String[] { String.valueOf(note_id) });
        db.close(); // Closing database connection
        return result;
    }

    public int update(Note note, boolean bUpdateDate) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBInfo.TABLE_TBLNOTE_KEY_NoteItem, note.getNoteItem());
        if (bUpdateDate) {
            note.setDate_Now();
            values.put(DBInfo.TABLE_TBLNOTE_KEY_Date, note.getDate());
        }
        values.put(DBInfo.TABLE_TBLNOTE_KEY_Tag,note.getTag());
        values.put(DBInfo.TABLE_TBLNOTE_KEY_Cost, note.getCost());
        values.put(DBInfo.TABLE_TBLNOTE_KEY_Checked, note.getChecked());
        values.put(DBInfo.TABLE_TBLNOTE_KEY_Image,note.getImage());
        Log.i(TAG, " WVS Note update noteid = " + note.getNoteId() + ", values=" + values.toString());
        // It's a good practice to use parameter ?, instead of concatenate string
        int result = db.update(DBInfo.TABLE_TBLNOTE , values, DBInfo.TABLE_TBLNOTE_KEY_Noteid + "= ?", new String[] { String.valueOf(note.getNoteId()) });
        db.close(); // Closing database connection
        return result;
    }

    public Integer DoesAnotherImageExist(Integer notesid, String img) {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Integer iRet=0;
        String sImage = "\"%" + img + "%\"";

        String selectQuery = "SELECT count(*) piccount FROM " + DBInfo.TABLE_TBLNOTE +
                " WHERE " + DBInfo.TABLE_TBLNOTE_KEY_NotesId + " <> " + notesid.toString() +
                " AND " + DBInfo.TABLE_TBLNOTE_KEY_Image + " LIKE " + sImage;

        Cursor cursor = db.rawQuery(selectQuery, null );
        if (cursor.moveToFirst()){
            do {
//                iRet = cursor.getCount();
                iRet = cursor.getInt(cursor.getColumnIndex("piccount"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return iRet;
    }
    //    public ArrayList<Note> getEachNoteByNotesId(Integer notesid){
    public ArrayList<Note> getEachNoteByNotesId(Notes notes){
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sOrderby="";

        if (notes.getOrderby() == null){
            sOrderby = " ORDER BY " + DBInfo.TABLE_TBLNOTE_KEY_Date;
        } else if (notes.getOrderby().equals(DBInfo.TABLE_TBLNOTE_KEY_Date)){
            sOrderby = " ORDER BY " + DBInfo.TABLE_TBLNOTE_KEY_Date + " DESC" + ", " + DBInfo.TABLE_TBLNOTE_KEY_NoteItem;
        } else if (notes.getOrderby().equals(DBInfo.TABLE_TBLNOTE_KEY_Checked)){
            sOrderby = " ORDER BY " + DBInfo.TABLE_TBLNOTE_KEY_Checked + " DESC" + ", " + DBInfo.TABLE_TBLNOTE_KEY_Tag + ", " + DBInfo.TABLE_TBLNOTE_KEY_NoteItem;
        } else if (notes.getOrderby().equals(DBInfo.TABLE_TBLNOTE_KEY_NoteItem)){
            sOrderby = " ORDER BY " + DBInfo.TABLE_TBLNOTE_KEY_NoteItem;
        } else if (notes.getOrderby().equals(DBInfo.TABLE_TBLNOTE_KEY_Tag)){
            sOrderby = " ORDER BY CAST(" + DBInfo.TABLE_TBLNOTE_KEY_Tag + " AS INT), " + DBInfo.TABLE_TBLNOTE_KEY_NoteItem;
        }
//        Log.i(TAG, sOrderby);
        String selectQuery =  "SELECT  " +
                DBInfo.TABLE_TBLNOTE_KEY_Noteid  + "," +
                DBInfo.TABLE_TBLNOTE_KEY_NoteItem + "," +
                DBInfo.TABLE_TBLNOTE_KEY_Date + "," +
                DBInfo.TABLE_TBLNOTE_KEY_Tag + "," +
                DBInfo.TABLE_TBLNOTE_KEY_Cost + "," +
                DBInfo.TABLE_TBLNOTE_KEY_NotesId + "," +
                DBInfo.TABLE_TBLNOTE_KEY_Checked + "," +
                DBInfo.TABLE_TBLNOTE_KEY_Image +
                " FROM " + DBInfo.TABLE_TBLNOTE +
                " WHERE " +
                DBInfo.TABLE_TBLNOTE_KEY_NotesId + "=?" + sOrderby;
//wvs: hint: todo: rawquery orderby
//                + // It's a good practice to use parameter ?, instead of concatenate string
//                " ORDER BY " + sOrderby;

        ArrayList<Note> noteList = new ArrayList<Note>();
//        Log.i(TAG, " WVS getEachNoteByNotesId NotesId = " + notes.getNotesId().toString() + ", " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, new String[] { notes.getNotesId().toString() } );
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {

                Note note = new Note(cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_Noteid)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_NoteItem)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_Date)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_Tag)),
                        cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_NotesId)),
                        cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_Checked)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_Image)),
                        cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_Cost)),
                        null);

                noteList.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return noteList;

    }


    public Note getNoteById(int noteid){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                DBInfo.TABLE_TBLNOTE_KEY_Noteid + "," +
                DBInfo.TABLE_TBLNOTE_KEY_NoteItem + "," +
                DBInfo.TABLE_TBLNOTE_KEY_Date + "," +
                DBInfo.TABLE_TBLNOTE_KEY_Tag + "," +
                DBInfo.TABLE_TBLNOTE_KEY_Cost + "," +
                DBInfo.TABLE_TBLNOTE_KEY_Checked + "," +
                DBInfo.TABLE_TBLNOTE_KEY_NotesId  + "," +
                DBInfo.TABLE_TBLNOTE_KEY_Image +
                " FROM " + DBInfo.TABLE_TBLNOTE
                + " WHERE " +
                DBInfo.TABLE_TBLNOTE_KEY_Noteid + "=?";// It's a good practice to use parameter ?, instead of concatenate string


        Integer mNoteId,mNotesId,mChecked;
        String mCost;
        mCost=null;
        String mNoteItem,mDate,mTag,mImage;
        mChecked=mNoteId=mNotesId=null;
        mImage=mNoteItem=mDate=mTag="";

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(noteid) } );

        if (cursor.moveToFirst()) {
            do {
                mNoteId = cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_Noteid));
                mNoteItem = cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_NoteItem));
                mDate = cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_Date));
                mTag = cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_Tag));
                mCost = cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_Cost));
                mChecked = cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_Checked));
                mNotesId = cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_NotesId));
                mImage = cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_Image));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        Note note = new Note(mNoteId,mNoteItem,mDate,mTag,mNotesId,mChecked,mImage,mCost,null );
        return note;
    }

    public Note getNoteByLocationsId(Long locationsid){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                DBInfo.TABLE_TBLNOTE_KEY_Noteid + "," +
                DBInfo.TABLE_TBLNOTE_KEY_NoteItem + "," +
                DBInfo.TABLE_TBLNOTE_KEY_Date + "," +
                DBInfo.TABLE_TBLNOTE_KEY_Tag + "," +
                DBInfo.TABLE_TBLNOTE_KEY_Cost + "," +
                DBInfo.TABLE_TBLNOTE_KEY_Checked + "," +
                DBInfo.TABLE_TBLNOTE_KEY_NotesId  + "," +
                DBInfo.TABLE_TBLNOTE_KEY_Image +
                " FROM " + DBInfo.TABLE_TBLNOTE
                + " WHERE " +
                DBInfo.TABLE_TBLNOTE_KEY_LocationsId + "=?";// It's a good practice to use parameter ?, instead of concatenate string


        Integer mNoteId,mNotesId,mChecked;
        String mCost;
        mCost=null;
        String mNoteItem,mDate,mTag,mImage;
        mChecked=mNoteId=mNotesId=null;
        mImage=mNoteItem=mDate=mTag="";

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(locationsid) } );

        Note note = null;
        if (cursor.getCount() == 0) {
//            pass back null note
        } else {
            if (cursor.moveToFirst()) {
                do {
                    mNoteId = cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_Noteid));
                    mNoteItem = cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_NoteItem));
                    mDate = cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_Date));
                    mTag = cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_Tag));
                    mCost = cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_Cost));
                    mChecked = cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_Checked));
                    mNotesId = cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_NotesId));
                    mImage = cursor.getString(cursor.getColumnIndex(DBInfo.TABLE_TBLNOTE_KEY_Image));
                } while (cursor.moveToNext());
            }
            note = new Note(mNoteId,mNoteItem,mDate,mTag,mNotesId,mChecked,mImage,mCost, locationsid.intValue() );
        }
        cursor.close();
        db.close();
        return note;
    }
    public File savefile_NotebyNotesId(Notes notes, Boolean onlychecked, boolean linenumbers){

        ArrayList<Note> tmpANote;
        tmpANote = getEachNoteByNotesId(notes);
        String filedatepart = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
        File NoteFile=null;
        Integer cnt=1;
        try {
            String state = Environment.getExternalStorageState();
            String pathToExtStorage = Environment.getExternalStorageDirectory().toString();
            File sd = new File(pathToExtStorage);
//            File data = Environment.getDataDirectory();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                if (Environment.MEDIA_MOUNTED.equals(state) ||
                        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                    if (sd.canWrite()) {
//                        sd.createNewFile();
                        NoteFile = new File(sd, notes.getName() + "_" + filedatepart + ".txt");
//                        sFileName = NoteFile.getAbsolutePath();

                        FileOutputStream fOut = new FileOutputStream(NoteFile);
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

                        if (onlychecked) {
                            myOutWriter.append("Name: " + notes.getName() + "(checked items only), " + NoteFile.getName());
                            myOutWriter.append("\n");
                            myOutWriter.append("\n");
                        } else {
                            myOutWriter.append("Name: " + notes.getName() + "(all items), " + NoteFile.getName());
                            myOutWriter.append("\n");
                            myOutWriter.append("\n");
                        }
//                        loop
                        if (tmpANote != null){
                            for (Note nt: tmpANote){
                                if (onlychecked & nt.getChecked() == 1){
                                    myOutWriter.append( (linenumbers ?  cnt.toString() + ". " : "") + nt.getNoteItem() + (nt.getTag().equals("") ? "" : ";   "  + nt.getTag()) + (nt.getImage().equals("") ? "" : ";   "  + nt.getImage()));
                                    myOutWriter.append("\n");
                                } else if (! onlychecked) {
                                    myOutWriter.append((linenumbers ?  cnt.toString() + ". " : "") + nt.getNoteItem() + (nt.getTag().equals("") ? "" : ";   "  + nt.getTag()) + (nt.getImage().equals("") ? "" : ";   "  + nt.getImage()));
                                    myOutWriter.append("\n");
                                }
                                cnt = cnt +1;
                            }
                        }
                        myOutWriter.close();
                        fOut.close();
                    }
                }
            }
        } catch (Exception e){

        } finally {
            return NoteFile;
        }
    }
}