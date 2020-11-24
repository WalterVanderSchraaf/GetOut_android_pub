package com.endeavor.walter.getout9;
//https://inducesmile.com/android/how-to-create-android-custom-calendar-view-with-events/
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.endeavor.walter.getout9.MainActivity.appEventOccurrance;
import static com.endeavor.walter.getout9.Utils.appLog;
import static com.endeavor.walter.getout9.WVSInfo.EVENTOCCURRANCE_ONETIME;
import static com.endeavor.walter.getout9.WVSInfo.EVENTOCCURRANCE_WEEKLY;
import static com.endeavor.walter.getout9.WVS_Utils.convertDBdateStringToDate_date;
import static com.endeavor.walter.getout9.WVS_Utils.convertDBdateStringToDate_datetime;


public class EventObjectRepo {
    DBHelper dbHelper;
    public final static String TAG = "EventObjectRepo";
    private ArrayList<Event_Object> mEvents;
    private Note mNote;
    Context mContext;

    public EventObjectRepo(Context context  ) {
        this.mContext = context;
        dbHelper = new DBHelper(context);
        DatabaseManager.initializeInstance(dbHelper);
    }

//    public EventObjectRepo() {
//        dbHelper = new DBHelper();
//        DatabaseManager.initializeInstance(dbHelper);
//    }

    public long insert(Event_Object event) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //EventId should be a trigger
        values.put(DBInfo.TABLE_TBLEVENTS_KEY_EventMessage, event.getMessage());
        values.put(DBInfo.TABLE_TBLEVENTS_KEY_EventMessageTag, event.getMessageTag());
        values.put(DBInfo.TABLE_TBLEVENTS_KEY_EventDate, event.getDate());
        values.put(DBInfo.TABLE_TBLEVENTS_KEY_EventOccurance, event.getOccurance());
        values.put(DBInfo.TABLE_TBLEVENTS_KEY_EventType, event.getEventType());
        values.put(DBInfo.TABLE_TBLEVENTS_KEY_EventRepeat, event.getRepeat());
        values.put(DBInfo.TABLE_TBLEVENTS_KEY_ToContact, event.getToContact());
        values.put(DBInfo.TABLE_TBLEVENTS_KEY_NoteId, event.getmNoteId());

        // Inserting Row
        long Event_id = db.insert(DBInfo.TABLE_TBLEVENTS,null,values);
        db.close(); // Closing database connection
        return Event_id;
    }

    public int deletebyNoteId(int note_id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        int result = db.delete(DBInfo.TABLE_TBLEVENTS, DBInfo.TABLE_TBLEVENTS_KEY_NoteId + "= ?", new String[] { String.valueOf(note_id) });
        db.close(); // Closing database connection
        return result;
    }
    public int deletebyEventId(int event_id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        int result = db.delete(DBInfo.TABLE_TBLEVENTS, DBInfo.TABLE_TBLEVENTS_KEY_EventId + "= ?", new String[] { String.valueOf(event_id) });
        db.close(); // Closing database connection
        return result;
    }

    public int deleteORupdateEventbyNoteId(Integer note_id) {

        mEvents = getEventsByNoteId(note_id,false);

        int result = 0;
        //          wvs always assume one event per note
        //          {"OneTime", "Weekly", "Monthly", "Yearly"}

        if (mEvents != null && mEvents.get(0).getOccurance().equals(EVENTOCCURRANCE_ONETIME)) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            result = db.delete(DBInfo.TABLE_TBLEVENTS, DBInfo.TABLE_TBLEVENTS_KEY_NoteId + "= ?", new String[]{String.valueOf(note_id)});
            appLog(TAG, " Delete One time event " + mEvents.get(0).getMessage() + ", " + mEvents.get(0).getDate());
            db.close(); // Closing database connection
        } else if (mEvents != null && mEvents.get(0).getOccurance().equals(EVENTOCCURRANCE_WEEKLY)) {
//          get Event, note objects, call
            mNote = getNoteById(note_id);
            WVS_Utils.CreateNotification(mContext, mEvents.get(0), "", mNote );
        }

        return result;
    }

    public int update(Event_Object event) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBInfo.TABLE_TBLEVENTS_KEY_EventMessage, event.getMessage());
        values.put(DBInfo.TABLE_TBLEVENTS_KEY_EventMessageTag, event.getMessageTag());
        values.put(DBInfo.TABLE_TBLEVENTS_KEY_EventDate, event.getDate());
        values.put(DBInfo.TABLE_TBLEVENTS_KEY_EventOccurance, event.getOccurance());
        values.put(DBInfo.TABLE_TBLEVENTS_KEY_EventType, event.getEventType());
        values.put(DBInfo.TABLE_TBLEVENTS_KEY_EventRepeat, event.getRepeat());
        values.put(DBInfo.TABLE_TBLEVENTS_KEY_ToContact, event.getToContact());
        values.put(DBInfo.TABLE_TBLEVENTS_KEY_NoteId, event.getmNoteId());
        values.put(DBInfo.TABLE_TBLEVENTS_KEY_EventId, event.getId());
//todo: why is EventId null, where is assigned?

        // It's a good practice to use parameter ?, instead of concatenate string
        int result = db.update(DBInfo.TABLE_TBLEVENTS , values, DBInfo.TABLE_TBLEVENTS_KEY_EventId + "= ?", new String[] { String.valueOf(event.getId()) });
        db.close(); // Closing database connection
        return result;
    }

    public ArrayList<Event_Object> getAllEvents(){
//  public List<Event_Object> getAllEvents(){
        //        Date dateToday = new Date();
//        dateToday = convertDBdateStringToDate(dateToday.toString());
        Calendar tmpcal = Calendar.getInstance(Locale.ENGLISH);
        tmpcal.add(Calendar.DAY_OF_MONTH, -1);;
        Date dateToday = tmpcal.getTime();

        ArrayList<Event_Object> events = new ArrayList<>();
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "select * from " + DBInfo.TABLE_TBLEVENTS;

        Cursor cursor = db.rawQuery(selectQuery, null);

            if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                String message = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventMessage));
                String messagetag = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventMessageTag));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventDate));
                String sOccurance = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventOccurance));
                String sRepeat =  cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventRepeat));
                String sEventType = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventType));
                String sToContact = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_ToContact));
                Integer NoteId = cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_TBLEVENTS_KEY_NoteId));

                if (!startDate.trim().equals("")) {
                    //convert start date to date object
                    Date reminderDate = convertDBdateStringToDate_datetime(startDate);  //normalize  check if this should be used convertDBdateStringToDate_datetime
//                    if (reminderDate.after(dateToday) || reminderDate.equals(dateToday)) {
//                        events.add(new Event_Object(id, message, startDate, NoteId));
                        events.add(new Event_Object(id,message,startDate,sOccurance,sEventType,sToContact,NoteId,sRepeat, messagetag));
//                    }
                }
            }while (cursor.moveToNext());
        }
            cursor.close();
            return events;
    }

    public int getCountByNoteId(Integer noteid, boolean bFuterDatesOnly) {

        Calendar tmpcal = Calendar.getInstance(Locale.ENGLISH);
        tmpcal.add(Calendar.DAY_OF_MONTH, -1);;
        Date dateToday = tmpcal.getTime();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int iRet=0;

        String selectQuery="";
        Cursor cursor;

        if (bFuterDatesOnly){
            selectQuery = "select count(*) cnt from " + DBInfo.TABLE_TBLEVENTS +
                    " WHERE " + DBInfo.TABLE_TBLEVENTS_KEY_NoteId + "=?" +
                    " AND " + DBInfo.TABLE_TBLEVENTS_KEY_EventDate + ">=?";
            cursor = db.rawQuery(selectQuery,  new String[] { noteid.toString(), dateToday.toString() } );
        } else {
            selectQuery = "select count(*) cnt from " + DBInfo.TABLE_TBLEVENTS +
                    " WHERE " + DBInfo.TABLE_TBLEVENTS_KEY_NoteId + "=?";
            cursor = db.rawQuery(selectQuery,  new String[] { noteid.toString() } );
        }

        if (cursor.moveToFirst()){
            do {
//                iRet = cursor.getCount();
                iRet = cursor.getInt(cursor.getColumnIndex("cnt"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return iRet;
    }
    public ArrayList<Event_Object> getEventsByNoteId(Integer noteid, boolean bFutureDatesOnly){

        Calendar tmpcal = Calendar.getInstance(Locale.ENGLISH);
        tmpcal.add(Calendar.DAY_OF_MONTH, -1);;
        Date dateToday = tmpcal.getTime();

        ArrayList<Event_Object> events = new ArrayList<>();
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "select * from " + DBInfo.TABLE_TBLEVENTS +
                " WHERE " + DBInfo.TABLE_TBLEVENTS_KEY_NoteId + "=?";

        Cursor cursor = db.rawQuery(selectQuery, new String[] { noteid.toString() } );

        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                String message = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventMessage));
                String messagetag = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventMessageTag));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventDate));
                String sOccurance = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventOccurance));
                String sRepeat = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventRepeat));
                String sEventType = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventType));
                String sToContact = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_ToContact));

                Integer NoteId = cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_TBLEVENTS_KEY_NoteId));
                if (!startDate.trim().equals("")){
                    //convert start date to date object
                    Date reminderDate = convertDBdateStringToDate_date(startDate);  //normalize  check if this should be used convertDBdateStringToDate_datetime
                    if (bFutureDatesOnly){
                        if (reminderDate.after(dateToday) || reminderDate.equals(dateToday)) {
    //                        events.add(new Event_Object(id, message, startDate, NoteId));
                            events.add(new Event_Object(id,message,startDate,sOccurance,sEventType,sToContact,NoteId,sRepeat, messagetag));
                        }
                    } else {
                        events.add(new Event_Object(id,message,startDate,sOccurance,sEventType,sToContact,NoteId,sRepeat, messagetag));
                    }
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return events;
    }

    public ArrayList<Event_Object> getEventsByEventId(Integer eventid){

        ArrayList<Event_Object> events = new ArrayList<>();
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "select * from " + DBInfo.TABLE_TBLEVENTS +
                " WHERE " + DBInfo.TABLE_TBLEVENTS_KEY_EventId + "=?";

        Cursor cursor = db.rawQuery(selectQuery, new String[] { eventid.toString() } );

        if(cursor.moveToFirst()){
            do {
                int id = cursor.getInt(0);
                String message = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventMessage));
                String messagetag = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventMessageTag));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventDate));
                String sOccurance = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventOccurance));
                String sRepeat = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventRepeat));
                String sEventType = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventType));
                String sToContact = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_ToContact));

                Integer NoteId = cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_TBLEVENTS_KEY_NoteId));
                if (!startDate.trim().equals("")){
                    //convert start date to date object
                    Date reminderDate = convertDBdateStringToDate_date(startDate);  //normalize  check if this should be used convertDBdateStringToDate_datetime
                    events.add(new Event_Object(id,message,startDate,sOccurance,sEventType,sToContact,NoteId,sRepeat, messagetag));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return events;
    }

    public ArrayList<Event_Object> getEventsByDateString(String tmpDate){

        Calendar tmpcal = Calendar.getInstance(Locale.ENGLISH);
        tmpcal.add(Calendar.DAY_OF_MONTH, -1);;
        Date dateToday = tmpcal.getTime();

        ArrayList<Event_Object> events = new ArrayList<>();
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
//      String datepart = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
//        String sDate = convertDateToString_datepart(tmpDate);

        String selectQuery = "select * from " + DBInfo.TABLE_TBLEVENTS +
                " WHERE " + DBInfo.TABLE_TBLEVENTS_KEY_EventDate + " LIKE ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[] { tmpDate + "%"} );

        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                String message = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventMessage));
                String messagetag = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventMessageTag));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventDate));
                String sOccurance = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventOccurance));
                String sRepeat = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventRepeat));
                String sEventType = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_EventType));
                String sToContact = cursor.getString(cursor.getColumnIndexOrThrow(DBInfo.TABLE_TBLEVENTS_KEY_ToContact));
                Integer NoteId = cursor.getInt(cursor.getColumnIndex(DBInfo.TABLE_TBLEVENTS_KEY_NoteId));
                if (!startDate.trim().equals("")) {
//                    events.add(new Event_Object(id, message, startDate, NoteId));
                    events.add(new Event_Object(id,message,startDate,sOccurance,sEventType,sToContact,NoteId,sRepeat, messagetag));
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return events;
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
}
