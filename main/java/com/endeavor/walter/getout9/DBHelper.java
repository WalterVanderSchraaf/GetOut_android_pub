package com.endeavor.walter.getout9;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.DetectedActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.endeavor.walter.getout9.DBInfo.KEY_Checked;
import static com.endeavor.walter.getout9.DBInfo.KEY_Orderby;
import static com.endeavor.walter.getout9.DBInfo.TABLE_ACTIVITIES;
import static com.endeavor.walter.getout9.DBInfo.TABLE_ACTIVITIES_ActivityId;
import static com.endeavor.walter.getout9.DBInfo.TABLE_ACTIVITIES_DateTime;
import static com.endeavor.walter.getout9.DBInfo.TABLE_ACTIVITIES_Description;
import static com.endeavor.walter.getout9.DBInfo.TABLE_ACTIVITIES_ID;
import static com.endeavor.walter.getout9.DBInfo.TABLE_ACTIVITIES_LocationsId;
import static com.endeavor.walter.getout9.DBInfo.TABLE_ACTIVITIES_TransitionId;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLLOGIN;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TBLPET;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TLKPACTIVITY;
import static com.endeavor.walter.getout9.DBInfo.TABLE_TLKPTRANSITION;
import static com.endeavor.walter.getout9.TransitionRecognitionUtils.toActivityString;
import static com.endeavor.walter.getout9.TransitionRecognitionUtils.toTransitiontypeString;

public class DBHelper extends SQLiteOpenHelper {
    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.

    private static final int DATABASE_VERSION = 4; //3

    // Database Name
    private static final String DATABASE_NAME = "Maps05.db";
    private static final String TAG = DBHelper.class.getSimpleName();//" WVS:DBHelper";
    private Object ctx;

    private String mPackageName = "com.endeavor.walter.maps5";
    private String currentDBPath = "//data//" + mPackageName+ "//databases//" + DATABASE_NAME;
    private final Context mContext;
    private final File DB_FILE;


    public DBHelper(Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
        DB_FILE = this.mContext.getDatabasePath(DATABASE_NAME);
    }


    public DBHelper( ) {
        super(App.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = App.getContext();
        DB_FILE = this.mContext.getDatabasePath(DATABASE_NAME);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here
        // wvs hint db.execSQL(CourseRepo.createTable());

        String CREATE_TABLE = "CREATE TABLE " + DBInfo.TABLE_LOCATIONS + "("
                + DBInfo.TABLE_LOCATIONS_KEY_LocationsId  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + DBInfo.TABLE_LOCATIONS_KEY_LoginId + " INTEGER, "
                + DBInfo.TABLE_LOCATIONS_KEY_PathName + " TEXT, "
                + DBInfo.TABLE_LOCATIONS_KEY_DateTime + " TEXT, "
                + DBInfo.TABLE_LOCATIONS_KEY_ZoomLevel + " INTEGER, "
                + DBInfo.TABLE_LOCATIONS_KEY_ZoomTo_Latitude + " INTEGER, "
                + DBInfo.TABLE_LOCATIONS_KEY_ZoomTo_Longitude + " INTEGER, "
                + DBInfo.TABLE_LOCATIONS_KEY_TotalTime + " TEXT, "
                + DBInfo.TABLE_LOCATIONS_KEY_TotalDistance + " INTEGER, "
                + DBInfo.TABLE_LOCATIONS_KEY_TotalSteps + " INTEGER, "
                + DBInfo.TABLE_LOCATIONS_KEY_ElevationDifference + " INTEGER, "
                + DBInfo.TABLE_LOCATIONS_KEY_Move_Time + " INTEGER, "
                + DBInfo.TABLE_LOCATIONS_KEY_Heart_Intensity + " INTEGER, "
                + DBInfo.TABLE_LOCATIONS_KEY_Heart_Time + " INTEGER, "
                + DBInfo.TABLE_LOCATIONS_KEY_Checked + " INTEGER, "
                + DBInfo.KEY_Orderby + " TEXT )";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE " + DBInfo.TABLE_LOCATION  + "("
                + DBInfo.TABLE_LOCATION_KEY_LocationId  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + DBInfo.TABLE_LOCATION_KEY_Latitude + " INTEGER, "
                + DBInfo.TABLE_LOCATION_KEY_Longitude + " INTEGER, "
                + DBInfo.TABLE_LOCATION_KEY_Altitude + " INTEGER, "
                + DBInfo.TABLE_LOCATION_KEY_Activity + " INTEGER, "
                + DBInfo.TABLE_LOCATION_KEY_Transition + " INTEGER, "
                + DBInfo.TABLE_LOCATION_KEY_ColorPath + " INTEGER, "
                + DBInfo.TABLE_LOCATION_KEY_Accuracy + " INTEGER, "
                + DBInfo.TABLE_LOCATION_KEY_DateTime + " TEXT, "
                + DBInfo.TABLE_LOCATION_KEY_LocationsId + " INTEGER)";
//                + Notes.KEY_Orderby + " TEXT )";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE " + DBInfo.TABLE_TBLGEOLOCATION + "("
                + DBInfo.TABLE_TBLGEOLOCATION_KEY_Id + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + DBInfo.TABLE_TBLGEOLOCATION_KEY_Name + " TEXT, "
                + DBInfo.TABLE_TBLGEOLOCATION_KEY_Latitude + " INTEGER, "
                + DBInfo.TABLE_TBLGEOLOCATION_KEY_Longitude + " INTEGER, "
                + DBInfo.TABLE_TBLGEOLOCATION_KEY_Altitude + " INTEGER, "
                + DBInfo.TABLE_TBLGEOLOCATION_KEY_Enabled + " INTEGER, "
                + DBInfo.TABLE_TBLGEOLOCATION_KEY_Within + " INTEGER) ";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE " + TABLE_ACTIVITIES  + "("
                + TABLE_ACTIVITIES_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + TABLE_ACTIVITIES_LocationsId + " INTEGER, "
                + TABLE_ACTIVITIES_ActivityId + " INTEGER, "
                + TABLE_ACTIVITIES_TransitionId  + " INTEGER, "
//                + TABLE_ACTIVITIES_ElapsedRealTimeNanos + "TEXT, "
                + TABLE_ACTIVITIES_Description   + " TEXT, "
                + TABLE_ACTIVITIES_DateTime + " TEXT )";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE " + TABLE_TLKPACTIVITY + "("
                + DBInfo.TABLE_TLKPACTIVITY_KEY_ID  + " INTEGER,"
                + DBInfo.TABLE_TLKPACTIVITY_KEY_DESCRIPTION + " TEXT )";
        db.execSQL(CREATE_TABLE);

        String INSERT_TABLE = "INSERT INTO " + TABLE_TLKPACTIVITY + "("
                + DBInfo.TABLE_TLKPACTIVITY_KEY_ID + ", "
                + DBInfo.TABLE_TLKPACTIVITY_KEY_DESCRIPTION + ") values ("
                + DetectedActivity.STILL + ",'" + toActivityString(DetectedActivity.STILL) + "')";
        db.execSQL(INSERT_TABLE);

        INSERT_TABLE = "INSERT INTO " + TABLE_TLKPACTIVITY + "("
                + DBInfo.TABLE_TLKPACTIVITY_KEY_ID + ", "
                + DBInfo.TABLE_TLKPACTIVITY_KEY_DESCRIPTION + ") values ("
                + DetectedActivity.ON_FOOT + ",'" + toActivityString(DetectedActivity.ON_FOOT) + "')";
        db.execSQL(INSERT_TABLE);

        INSERT_TABLE = "INSERT INTO " + TABLE_TLKPACTIVITY + "("
                + DBInfo.TABLE_TLKPACTIVITY_KEY_ID + ", "
                + DBInfo.TABLE_TLKPACTIVITY_KEY_DESCRIPTION + ") values ("
                + DetectedActivity.WALKING + ",'" + toActivityString(DetectedActivity.WALKING) + "')";
        db.execSQL(INSERT_TABLE);

        INSERT_TABLE = "INSERT INTO " + TABLE_TLKPACTIVITY + "("
                + DBInfo.TABLE_TLKPACTIVITY_KEY_ID + ", "
                + DBInfo.TABLE_TLKPACTIVITY_KEY_DESCRIPTION + ") values ("
                + DetectedActivity.RUNNING + ",'" + toActivityString(DetectedActivity.RUNNING) + "')";
        db.execSQL(INSERT_TABLE);

        INSERT_TABLE = "INSERT INTO " + TABLE_TLKPACTIVITY + "("
                + DBInfo.TABLE_TLKPACTIVITY_KEY_ID + ", "
                + DBInfo.TABLE_TLKPACTIVITY_KEY_DESCRIPTION + ") values ("
                + DetectedActivity.ON_BICYCLE + ",'" + toActivityString(DetectedActivity.ON_BICYCLE) + "')";
        db.execSQL(INSERT_TABLE);

        INSERT_TABLE = "INSERT INTO " + TABLE_TLKPACTIVITY + "("
                + DBInfo.TABLE_TLKPACTIVITY_KEY_ID + ", "
                + DBInfo.TABLE_TLKPACTIVITY_KEY_DESCRIPTION + ") values ("
                + DetectedActivity.IN_VEHICLE + ",'" + toActivityString(DetectedActivity.IN_VEHICLE) + "')";
        db.execSQL(INSERT_TABLE);

        CREATE_TABLE = "CREATE TABLE " + TABLE_TLKPTRANSITION + "("
                + DBInfo.TABLE_TLKPTRANSITION_KEY_ID  + " INTEGER,"
                + DBInfo.TABLE_TLKPTRANSITION_KEY_DESCRIPTION + " TEXT )";
        db.execSQL(CREATE_TABLE);

        INSERT_TABLE = "INSERT INTO " + TABLE_TLKPTRANSITION + "("
                + DBInfo.TABLE_TLKPTRANSITION_KEY_ID + ", "
                + DBInfo.TABLE_TLKPTRANSITION_KEY_DESCRIPTION + ") values ("
                + ActivityTransition.ACTIVITY_TRANSITION_ENTER + ",'" + toTransitiontypeString(ActivityTransition.ACTIVITY_TRANSITION_ENTER) + "')";
        db.execSQL(INSERT_TABLE);


        INSERT_TABLE = "INSERT INTO " + TABLE_TLKPTRANSITION + "("
                + DBInfo.TABLE_TLKPTRANSITION_KEY_ID + ", "
                + DBInfo.TABLE_TLKPTRANSITION_KEY_DESCRIPTION + ") values ("
                + ActivityTransition.ACTIVITY_TRANSITION_EXIT + ",'" + toTransitiontypeString(ActivityTransition.ACTIVITY_TRANSITION_EXIT) + "')";
        db.execSQL(INSERT_TABLE);

        CREATE_TABLE = "CREATE TABLE " + TABLE_TBLLOGIN + "("
                + DBInfo.TABLE_TBLLOGIN_KEY_ID  + " INTEGER,"
                + DBInfo.TABLE_TBLLOGIN_KEY_Name  + " TEXT,"
                + DBInfo.TABLE_TBLLOGIN_KEY_Email + " TEXT )";
        db.execSQL(CREATE_TABLE);

        for (int i = 0; i < MainActivity.applogins.length; i++) {
            INSERT_TABLE = "INSERT INTO " + TABLE_TBLLOGIN + "("
                    + DBInfo.TABLE_TBLLOGIN_KEY_Name + ") values ("
                    +  "'" + MainActivity.applogins[i] + "')";
            db.execSQL(INSERT_TABLE);
        }

        CREATE_TABLE = "CREATE TABLE " + TABLE_TBLPET + "("
                + DBInfo.TABLE_TBLPET_KEY_ID  + " INTEGER,"
                + DBInfo.TABLE_TBLPET_KEY_Name + " TEXT )";
        db.execSQL(CREATE_TABLE);

        for (int i = 0; i < MainActivity.appPets.length; i++) {
            INSERT_TABLE = "INSERT INTO " + TABLE_TBLPET + "("
                    + DBInfo.TABLE_TBLPET_KEY_Name + ") values ("
                    +  "'" + MainActivity.appPets[i] + "')";
            db.execSQL(INSERT_TABLE);
        }
//******************************    NOTES app *****************************************
        CREATE_TABLE = "CREATE TABLE " + DBInfo.TABLE_TBLNOTES + "("
                + DBInfo.TABLE_TBLNOTES_KEY_NotesId  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + DBInfo.TABLE_TBLNOTES_KEY_Name + " TEXT, "
                + DBInfo.TABLE_TBLNOTES_KEY_Date + " TEXT, "
                + KEY_Checked + " INTEGER, "
                + KEY_Orderby + " TEXT ) ";

        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE " + DBInfo.TABLE_TBLNOTE  + "("
                + DBInfo.TABLE_TBLNOTE_KEY_Noteid  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + DBInfo.TABLE_TBLNOTE_KEY_NoteItem + " TEXT, "
                + DBInfo.TABLE_TBLNOTE_KEY_Tag + " TEXT, "
                + DBInfo.TABLE_TBLNOTE_KEY_Cost + " INTEGER, "
                + DBInfo.TABLE_TBLNOTE_KEY_Date + " TEXT, "
                + DBInfo.TABLE_TBLNOTE_KEY_Image + " TEXT, "
                + KEY_Checked + " INTEGER, "
                + KEY_Orderby + " TEXT, "
                + DBInfo.TABLE_TBLNOTE_KEY_NotesId + " INTEGER, "
                + DBInfo.TABLE_TBLNOTE_KEY_LocationsId + " INTEGER )";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE " + DBInfo.TABLE_TBLEVENTS  + "("
                + DBInfo.TABLE_TBLEVENTS_KEY_EventId  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + DBInfo.TABLE_TBLEVENTS_KEY_EventMessage + " TEXT, "
                + DBInfo.TABLE_TBLEVENTS_KEY_EventOccurance + " TEXT, "
                + DBInfo.TABLE_TBLEVENTS_KEY_EventDate + " TEXT, "
                + DBInfo.TABLE_TBLEVENTS_KEY_EventType + " TEXT, "
                + DBInfo.TABLE_TBLEVENTS_KEY_ToContact + " TEXT, "
                + KEY_Checked + " INTEGER, "
                + KEY_Orderby + " TEXT, "
                + DBInfo.TABLE_TBLNOTE_KEY_Noteid + " INTEGER )";
        db.execSQL(CREATE_TABLE);


        CREATE_TABLE = "CREATE TABLE " + DBInfo.TABLE_TBLHEALTH  + "("
                + DBInfo.TABLE_TBLHEALTH_KEY_HealthId  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + DBInfo.TABLE_TBLHEALTH_KEY_Type + " TEXT, "
                + DBInfo.TABLE_TBLHEALTH_KEY_SourceName + " TEXT, "
                + DBInfo.TABLE_TBLHEALTH_KEY_SourceVersion + " TEXT, "
                + DBInfo.TABLE_TBLHEALTH_KEY_Device + " TEXT, "
                + DBInfo.TABLE_TBLHEALTH_KEY_Unit + " TEXT, "
                + DBInfo.TABLE_TBLHEALTH_KEY_CreationDate + " TEXT, "
                + DBInfo.TABLE_TBLHEALTH_KEY_StartDate + " TEXT, "
                + DBInfo.TABLE_TBLHEALTH_KEY_EndDate + " TEXT, "
                + DBInfo.TABLE_TBLHEALTH_KEY_Value + " TEXT, "
                + DBInfo.TABLE_TBLHEALTH_KEY_LoginId + " INTEGER)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE " + DBInfo.TABLE_TBLSCRIPTS + "("
                + DBInfo.TABLE_TBLSCRIPTS_KEY_ScriptsId  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + DBInfo.TABLE_TBLSCRIPTS_KEY_ScriptName + " TEXT, "
                + DBInfo.TABLE_TBLSCRIPTS_KEY_Bucket + " TEXT, "
                + DBInfo.TABLE_TBLSCRIPTS_KEY_BucketPrefix + " TEXT, "
                + DBInfo.TABLE_TBLSCRIPTS_KEY_DownloadDate + " TEXT, "
                + DBInfo.TABLE_TBLSCRIPTS_KEY_ExecutionDate + " TEXT, "
                + DBInfo.TABLE_TBLSCRIPTS_KEY_RowsAffected + " TEXT )";
        db.execSQL(CREATE_TABLE);

//******************************    NOTES   *****************************************
        //
////https://inducesmile.com/android/how-to-create-android-custom-calendar-view-with-events/
//        //        CREATE TABLE "events" ( `_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `message` TEXT NOT NULL, `reminder` TEXT NOT NULL, `end` TEXT );
//        CREATE_TABLE = "CREATE TABLE " + Event_Object.TABLE + "("
//                + Event_Object.KEY_EventId + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
//                + Event_Object.KEY_EventMessage + " TEXT, "
//                + Event_Object.KEY_EventOccurance + " TEXT, "
//                + Event_Object.KEY_EventDate + " TEXT, "
//                + Event_Object.KEY_EventType + " TEXT, "
//                + Event_Object.KEY_EventToContact + " TEXT, "
//                + Event_Object.KEY_NoteId + " INTEGER )";
//        db.execSQL(CREATE_TABLE);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + DBInfo.TABLE_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + DBInfo.TABLE_LOCATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TLKPACTIVITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TLKPTRANSITION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TBLLOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TBLPET);
        db.execSQL("DROP TABLE IF EXISTS " + DBInfo.TABLE_TBLNOTES);
        db.execSQL("DROP TABLE IF EXISTS " + DBInfo.TABLE_TBLNOTE);
        db.execSQL("DROP TABLE IF EXISTS " + DBInfo.TABLE_TBLEVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + DBInfo.TABLE_TBLHEALTH);

        //        db.execSQL("DROP TABLE IF EXISTS " + Event_Object.TABLE );
        //lookups
        // Create tables again
        onCreate(db);


    }


    //exporting database does not work sd.mkdirs is FALSE
    public String exportDB() {
        String retFile="";
        try {
            String state = Environment.getExternalStorageState();
            //File sd = Environment.getExternalStorageDirectory();
//            String pathToExtStorage = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).toString();  //  /storage/emulated/0/Download
//            String pathToExtStorage = Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM).toString();  //  /storage/emulated/0/DCIM
//            String pathToExtStorage = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES).toString();  //  /storage/emulated/0/Pictures
//            String pathToExtStorage = Environment.getExternalStorageDirectory().toString();
            String pathToExtStorage = Environment.getExternalStorageDirectory().toString();

            File sd = new File(pathToExtStorage);
//            sd.setWritable(true);
            //            File sd = new File(pathToExtStorage + "/SparkCeramics_back.db" );
            //File sd = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
            ///storage/emulated/0/Download
//            if (sd.mkdirs()){
                File data = Environment.getDataDirectory();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    if (Environment.MEDIA_MOUNTED.equals(state) ||
                            Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                        if (sd.canWrite()) {
//                            String currentDBPath = "//data//" + mPackageName+ "//databases//" + DATABASE_NAME;
//                            String backupDBPath = "/BackupFolder/backup" + DATABASE_NAME;
                            String filedatepart = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss'_'").format(new Date());
                            String backupDBPath = filedatepart + DATABASE_NAME;
                            //File currentDB = new File(data, currentDBPath);
                            File backupDB = new File(sd, backupDBPath);
                            //FileChannel src = new FileInputStream(currentDB).getChannel();
                            FileChannel src = new FileInputStream(DB_FILE).getChannel();
                            FileChannel dst = new FileOutputStream(backupDB).getChannel();
                            dst.transferFrom(src, 0, src.size());
                            //Log.i(TAG,"creating backup db from "+ currentDB.toString() + " to " + backupDB.toString() );
                            retFile = backupDBPath;
                            Log.i(TAG,"creating backup db from "+ DB_FILE.toString() + " to " + backupDB.toString() );
                            src.close();
                            dst.close();
                        }
                    }
                }
//            }
        } catch (Exception e) {
            throw new Error("ErrorExportingingDatabase");
        }
        finally {

        }
        return retFile;
    }

    public boolean importDatabase(String dbPath) throws IOException {
        //wvs hint:        https://stackoverflow.com/questions/6540906/simple-export-and-import-of-a-sqlite-database-on-android#6542214
        // Close the SQLiteOpenHelper so it will commit the created empty
        // database to internal storage.
        close();
        File newDb = new File(dbPath);
//        File oldDb = new File(currentDBPath);
//        currentDBPath = //data//com.endeavor.walter.maps5//databases//Maps05.db - this fails oldDb.exists()
//        DB_FILE = /data/user/0/com.endeavor.walter.mapstest5/databases/Maps05.db
        if (newDb.exists()) {
//          if(oldDb.exists()){
            if(DB_FILE.exists()){
                exportDB();
            }
//            WVS_Utils.copyFile(new FileInputStream(newDb), new FileOutputStream(oldDb));
            WVS_Utils.copyFile(new FileInputStream(newDb), new FileOutputStream(DB_FILE));
            // Access the copied database so SQLiteHelper will cache it and mark
            // it as created.
            getWritableDatabase().close();
            return true;
        }
        return false;
    }

    // Copy the database from assets
    // https://stackoverflow.com/questions/9109438/how-to-use-an-existing-database-with-an-android-application
    public void resetDataBase_fromAssets(String newDBcopy) throws IOException {
//  exportDB() places copy in Internal shared storage as 2019-12-13 12-07-41_Maps05.db,
//  just copy to andriod /assets directory as specify in MainActivity.
        File newDB_File=null;
        if (DB_FILE.exists()){
            this.getReadableDatabase();
            this.close();
            Log.i(TAG, " WVS resetting database based on "+ newDBcopy);
            try{
                InputStream mInput = null;
                if (newDBcopy.contains("/storage/")) {
                    //        newDBcopy="/storage/emulated/0/2020-01-31 12-12-41_Maps05.db"
                    newDB_File = new File(newDBcopy);
                    if (newDB_File.exists()) {
                        mInput = new FileInputStream(newDBcopy);  // mInput.path =
                    } else {
                        return;
                    }
                } else {
                    mInput = mContext.getAssets().open(newDBcopy);
//                    mContext.getAssets().list();
                }
                OutputStream mOutput = new FileOutputStream(DB_FILE);
                byte[] mBuffer = new byte[1024];
                int mLength;
                while ((mLength = mInput.read(mBuffer)) > 0) {
                    mOutput.write(mBuffer, 0, mLength);
                }
                mOutput.flush();
                mOutput.close();
                mInput.close();
            } catch (IOException e){
                throw new Error("ErrorCopyingDatabase");
            }
        }

    }
}
