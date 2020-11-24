package com.endeavor.walter.getout9;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;

import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.work.Data;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StorageAccessLevel;
import com.amplifyframework.storage.StorageItem;
import com.amplifyframework.storage.options.StorageUploadFileOptions;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.android.volley.BuildConfig;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.endeavor.walter.getout9.ui.main.SectionsPagerAdapter;
import com.endeavor.walter.getout9.ui.main.ViewPagerMod;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static android.os.Environment.DIRECTORY_PICTURES;
import static com.endeavor.walter.getout9.Utils.appLog;
import static com.endeavor.walter.getout9.WVSInfo.ADD_NOTE_REQUEST;
import static com.endeavor.walter.getout9.WVSInfo.EDIT_NOTE_ITEM_REQUEST;
import static com.endeavor.walter.getout9.WVSInfo.EVENTOCCURRANCE_MONTHLY;
import static com.endeavor.walter.getout9.WVSInfo.EVENTOCCURRANCE_ONETIME;
import static com.endeavor.walter.getout9.WVSInfo.EVENTOCCURRANCE_WEEKLY;
import static com.endeavor.walter.getout9.WVSInfo.EVENTOCCURRANCE_YEARLY;
import static com.endeavor.walter.getout9.WVSInfo.EVENTTYPE_EMAIL;
import static com.endeavor.walter.getout9.WVSInfo.EVENTTYPE_NOTIFICATION;
import static com.endeavor.walter.getout9.WVSInfo.EVENTTYPE_SCHEDULE_NOTIFICATION;
import static com.endeavor.walter.getout9.WVSInfo.EVENTTYPE_TEXTMSG;
import static com.endeavor.walter.getout9.WVSInfo.IMPORT_DB;
import static com.endeavor.walter.getout9.WVSInfo.IMPORT_NOTE;
import static com.endeavor.walter.getout9.WVSInfo.REQUEST_OAUTH_REQUEST_CODE;
import static com.endeavor.walter.getout9.WVSInfo.START_CAMERA;
import static com.endeavor.walter.getout9.WVSInfo.type_DISTANCE_DELTA;
import static com.endeavor.walter.getout9.WVSInfo.type_HEART_POINTS;
import static com.endeavor.walter.getout9.WVSInfo.type_MOVE_MINUTES;
import static com.endeavor.walter.getout9.WVSInfo.type_STEP_COUNT_CUMULATIVE;
import static com.endeavor.walter.getout9.WVS_Utils.convertDBdateStringToString_endofDay;
import static com.endeavor.walter.getout9.WVS_Utils.convertDBdateStringToString_startofDay;
import static com.endeavor.walter.getout9.WVS_Utils.getDateTimeString_standard;
import static com.endeavor.walter.getout9.WVS_Utils.getEventId_NotificationTag;
import static com.endeavor.walter.getout9.WVS_Utils.getNoteId_NotificationTag;
import static com.endeavor.walter.getout9.WVS_Utils.getStringFromFile;
import static com.endeavor.walter.getout9.WVS_Utils.getstrCurrentYear;
import static java.text.DateFormat.SHORT;
import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;

//using Volley for simple http request

//testing sha256 hashing - https://stackoverflow.com/questions/51444782/sha256-encryption-in-java-and-python-produce-different-results
//java counterpart? import hashlib;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener {
//  wvs 3/19/20 Sign your app - Build > Generate Signed Bundle/APK - signed APK that you can deploy to a device
    private static String TAG = MainActivity.class.getSimpleName();
    private int mMaptype;
    public static final int REQUEST_GETMYLOCATION = 0;
    public static final String[] PERMISSION_GETMYLOCATION = new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};
    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE_LOCATION = 34;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE_EXTFILES = 36;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE_CONTACTS = 51;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE_SMSTEXT = 61;
    static final int PREVIOUS_ROUTE_REQUEST = 10;

    private Menu mMenu;
    ViewPagerMod viewPager;
//    ViewPager viewPager;
    private GoogleMap mMap;
    private boolean bNewPathColor;

    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;
    // Tracks the bound state of the service.
    private boolean mBound = false;

    Recognition mRecognition = null;
    private String sTitle;
    public int iTabNumber;
    TextView txtStatus;

    ArrayList<Location_foo> mLocationArray = new ArrayList<>();
    private LatLng llWillo = new LatLng(37.760518, -122.408997);
    private Marker mrkWillo;

    //https://stackoverflow.com/questions/13721008/how-to-draw-dashed-polyline-with-android-google-map-sdk-v2
    public static final int PATTERN_DASH_LENGTH_PX = 20;
    public static final int PATTERN_GAP_LENGTH_PX = 20;
    public static final PatternItem DOT = new Dot();
    public static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    public static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    public static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);
    public static final List<PatternItem> PATTERN_POLYGON_RUN = Arrays.asList(GAP, DOT);

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;
    public String ACTION_SHOW_TRANS = "showTransitions";
    private MapFragment frag;
    private LocationsFragment locationsfrag;

    Context mContext;

    boolean bPaused;
    boolean bBackFromNotification;

    ArrayList <Login> mArrayListLogins = new ArrayList<>();
    String [] arrayLogins;
    Login tmpLogin;
    boolean blnRDSLogin;
    boolean blnappLoginEQtblLogin;
    public static String GetOutURL = "http://XXXXXXXXX.us-west-2.elasticbeanstalk.com";

    private Long mltotalSteps = 0l;
    private Float mftotalDistance = 0f;
    private Float mfHeartIntensity = 0f;
    private Integer miHeartDuration = 0;
    private Long mlMoveMinutes = 0l;
    ArrayList<Health> mHealthArray = new ArrayList<>();

    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            appLog(TAG, " WVS onReceive()");
            String sMsg = "";
//            txtStatus = (TextView) findViewById(R.id.txtStatus);

            if (context != null && intent != null) {
                Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
                if (location != null) {
//                    if (location.getExtras() != null && location.getExtras().containsKey("pathcolor")) {
//                        appLog(TAG, " WVS onReceive color = " + location.getExtras().getInt("pathcolor"));
//                    }

//                  https://stackoverflow.com/questions/7379165/update-data-in-listfragment-as-part-of-viewpager
//                  index 0 is the map fragment...
                    frag = (MapFragment) viewPager.getAdapter().instantiateItem(viewPager,0);
                    frag.onLocationChanged_redrawMap(location);

//                    Toast.makeText(context, Utils.getLocation_Text(location),Toast.LENGTH_SHORT).show();
                }
//                DisplayExtras(intent);
                if (intent.getExtras() != null && intent.getExtras().containsKey("activitytransitionresult")) {
                    if (Utils.getRequestingLocationUpdates(context)) {
//                      yes, requesting location updates so display transitions
                    } else {
//                      not requesting location updates so kill transition requests...
                        if (mRecognition != null) {
                            appLog(TAG, " WVS onReceive LOCATION has been killed, so stopping TRANSACTIONS...");
                            mRecognition.StopTracking(context);
                        } else {
//                            mRecognition = new Recognition();
//                            mRecognition.StopTracking(mContext);
                            appLog(TAG, " WVS OptionsItemSelected mRecognition=NULL, unable to stop TRANSACTIONS");
                        }
                    }
                    ActivityTransitionResult tmpTransEvents = intent.getParcelableExtra("activitytransitionresult");
                    if (tmpTransEvents != null) {
                        appLog(TAG, " WVS MapsActivity.mReceiver.onReceive latest TRANSITION result: " + tmpTransEvents.getTransitionEvents().get(tmpTransEvents.getTransitionEvents().size() - 1).toString());
                        for (ActivityTransitionEvent event : tmpTransEvents.getTransitionEvents()) {
                            appLog(TAG, " WVS EACH TRANSITION:" + TransitionRecognitionUtils.createTranstionString(event));
                            ActivitiesRepo ar = new ActivitiesRepo(context);

                            Activities tmpActivity = new Activities(event.getActivityType(), event.getTransitionType(), event.getElapsedRealTimeNanos());
                            //      getActivityType, getTransitionType, getElapsedRealTimeNanos

                            //      get current locationsid
                            long locationsid = Utils.getCurrentPathNameID(context);
                            tmpActivity.setLocationSId(locationsid);
//                            String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                            String datetime = getDateTimeString_standard();
                            tmpActivity.setDateTime(datetime);
                            tmpActivity.setDescription("");
//
                            long activitiesid = ar.insert(tmpActivity);
                        }

                    } else {
                        appLog(TAG, " WVS MapsActivity.mReceiver.onReceive latest transition result NULL");
                    }
                }

            }
        }

    }
    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            appLog(TAG, " WVS onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
            mRecognition = null;
            appLog(TAG, " WVS onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);
        myReceiver = new MyReceiver();

//      one time db step, might require calling in LocationsFragment.java
//        tmpTableMod();
//        NotificationHandler.cancelALLReminders();

//        appLog(TAG, " WVS onCreate() sTitle = " + ((sTitle == null) ? "" : sTitle));
        if (savedInstanceState !=null){

            if (savedInstanceState.containsKey("caption")){
                sTitle = savedInstanceState.getString("caption");
                setTitle(sTitle);
//                appLog(TAG, " WVS onCreate() savedInstanceState sTitle = " + ((sTitle == null) ? "" : sTitle));
            }
            if (savedInstanceState.containsKey("paused")){
                bPaused = savedInstanceState.getBoolean("paused");
            }
            if ( savedInstanceState.containsKey("locations")){
                Locations tmpLoc = savedInstanceState.getParcelable("locations");
                Long locationsid = tmpLoc.getLocationsId();
                LocationRepo lr = new LocationRepo(this );
                //            //                  index 0 is the map fragment...
                mLocationArray = lr.getRouteActivityByLocationsId(locationsid, mContext);

                frag = (MapFragment) viewPager.getAdapter().instantiateItem(viewPager,0);
                frag.DrawPath(mLocationArray);
            }

            if (savedInstanceState.containsKey("tabnumber")){
                iTabNumber = savedInstanceState.getInt("tabnumber");
            } else {
                savedInstanceState.putInt("tabnumber", iTabNumber);
            }
            bBackFromNotification = false;
        } else {
//            test if coming from notification pending intent.... "id"
            Intent intent1 = getIntent();
            appLog(TAG, " WVS onCreate() calling BackFromNotificaition_delEvent");
            bBackFromNotification = BackFromNotification_delEvent_gotoNote(intent1);

//          load maps by default, index 0
//            iTabNumber = Utils.getTabNumber(mContext);
            iTabNumber = 0;
            Utils.setTabNumber(mContext,0);
//          could be returning from background services...
            if (Utils.getRequestingLocationUpdates(mContext)){
                sTitle = Utils.getRouteName(mContext) + ((Utils.getPausedState(mContext)? ": paused" : ": recording")  ) ;
                setTitle(sTitle);
                appLog(TAG, " WVS onCreate() getRequestingLocationUpdates sTitle = " + ((sTitle == null) ? "" : sTitle));
            } else {
                setTitle(Utils.getCurrentTitleUser(mContext));
            }
//          check tblLogin table, check aws login, default to local stub
            LoginRepo lr = new LoginRepo(mContext);
//          first time in mArrayListLogins will be empty
            if (mArrayListLogins != null & mArrayListLogins.size() == 0){
                mArrayListLogins = lr.getAllLogins();
                if (mArrayListLogins.size() == 0){
                    checkRDSLogins();
                }
            }
        }

        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        //        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager = findViewById(R.id.view_pager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
//                appLog(TAG, " WVS onPageScrolled, Position index of the first page currently being displayed = " + i);
                if (v == 0.0 && i1 == 0 && i == 0) {
//                    first page 0 disable maps
                    viewPager.setPagingEnabled(false);
                } else if (v == 0.0 && i1 == 0 && i != 0) {
//                    first page 0 disable maps
                    viewPager.setPagingEnabled(true);
                }
            }

            @Override
            public void onPageSelected(int i) {
//                appLog(TAG, " WVS onPageSelected = " + i);
//                boolean bReqLocationUpdates = Utils.getRequestingLocationUpdates(mContext);
                Utils.setTabNumber(mContext, i);
                if (i==0){
//                  TODAY
                    setMenuState(true, appMenuChoices[2]);
                } else if (i == 1){
//                  ROUTES
                    locationsfrag = (LocationsFragment) viewPager.getAdapter().instantiateItem(viewPager,i);
//                    appLog(TAG, " WVS onPageSelected) BEFORE refresh objA_Locations.size="+ locationsfrag.objA_Locations.size());
                    locationsfrag.RefreshList();
//                  appMenuChoices[] "NoChanges","AddNotes","AddNotesNot"
                    setMenuState(true, appMenuChoices[2]);
                } else if (i==2) {
//                  NOTES
                    NotesActivityFragment notesfrag = (NotesActivityFragment) viewPager.getAdapter().instantiateItem(viewPager, i);
                    notesfrag.RefreshList();
//                  Notes page add plus icon...
                    setMenuState(true, appMenuChoices[1]);
                } else if (i==3) {
//                  NOTES todo changed to Calendar 8/26/20
                    CalendarFragment calendarfrag = (CalendarFragment) viewPager.getAdapter().instantiateItem(viewPager, i);
                    calendarfrag.RefreshList();
//
                    setMenuState(true, appMenuChoices[2]);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
//                appLog(TAG, " WVS onPageScrollStateChanged = " + i);
            }
        });

        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // Check that the user hasn't revoked permissions by going to Settings.
        if (Utils.getRequestingLocationUpdates(this)) {
            if (!checkPermissions_Location()) {
                requestPermissions_Location();  // if granted will lead to mService.requestLocationUpdates() and mRecognition.StartTracking(this);
            } else {

            }
        }
        requestPermission_WriteExternalStorage();
        setupFitness();
//        requestPermission_Contacts(mContext,this);
        requestPermission_SendSMS(mContext,this);
//        viewPager.setCurrentItem(iTabNumber);

//      wvs https://docs.amplify.aws/lib/storage/getting-started/q/platform/android#configure-your-application
//      wvs make sure app/raw/awsconfiguration.json
        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails userStateDetails) {
                try {
                    Amplify.addPlugin(new AWSS3StoragePlugin());
                    Amplify.configure(getApplicationContext());
                    appLog("StorageQuickstart", "All set and ready to go!");
                } catch (Exception exception) {
                    appLog("StorageQuickstart", exception.getMessage());
                }
            }

            @Override
            public void onError(Exception exception) {
                appLog("StorageQuickstart", "Initialization error." + exception.getMessage());
            }
        });

    }

    public static void uploadFileToAWSS3(String sfilename, String keyname, Context context) {
//        File sampleFile = new File(getApplicationContext().getFilesDir(), sfilename);
        File sampleFile;
        if (sfilename.contains(".jpg")){
            sampleFile = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)) + "/" + sfilename );
        } else {
//          String pathToExtStorage = Environment.getExternalStorageDirectory().toString();
//          File sd = new File(pathToExtStorage);
//          ScriptFile = new File(sd, sfilename);
//            sampleFile = new File(context.getFilesDir(), sfilename);
            sampleFile = new File(Environment.getExternalStorageDirectory(), sfilename);
        }
        appLog(" WVS uploadFileToAWSS3()", "Attempting to uploaded: " + sfilename + ", keyname " + keyname);
        String sBucket = "getout-android-bucket95810-getoutenv";
        String downloaddate = getDateTimeString_standard();
        ScriptsRepo sr = new ScriptsRepo(context);
        Boolean bFileWasUploaded = false;
//      check to see if previously uploaded... MySQL_route_inserts_UsernameLoginid_LocationsId_filepart.txt MySQL_health_inserts_walter1_TYPE_DISTANCE_DELTA _20200611110936.txt
        String stubroute, stubhealth, stubfilename;
//        stubroute = "MySQL_route_inserts_";  // Sqlite3_route_inserts_
//        stubroute = sfilename.substring(0, sfilename.indexOf("route_inserts_"));
        stubroute = stubhealth = "";
        if (sfilename.indexOf("route_inserts_") > 0) {
            stubroute = sfilename.substring(0, sfilename.indexOf("route_inserts_") + "route_inserts_".length());
        }
        if (sfilename.indexOf("health_inserts_") > 0) {
            stubhealth = sfilename.substring(0, sfilename.indexOf("health_inserts_") + "health_inserts_".length());;
        }

        if (sfilename.substring(0, stubroute.length()).equals(stubroute)) {
//          e.g. MySQL_route_inserts_walter1_305%
            stubfilename = sfilename.substring(0, sfilename.length() - "_20200611110936.txt".length() + 3) + "%";
            bFileWasUploaded = sr.ScriptExistByName(stubfilename);

        } else if (sfilename.substring(0, stubhealth.length()).equals(stubhealth)) {
            stubfilename = sfilename.substring(0, sfilename.length() - "_20200611110936.txt".length()) + "%";
            bFileWasUploaded = sr.ScriptExistByName(stubfilename);
        } else {
//          Contents.txt?
            bFileWasUploaded = true;
        }


//      wvs todo: context.getResources().openRawResource(R.raw.amplifyconfiguration);
        if (! bFileWasUploaded) {
            StorageUploadFileOptions options =
                    StorageUploadFileOptions.builder()
                            .accessLevel(StorageAccessLevel.PROTECTED)
                            .build();
//      Amplify.Storage.uploadFile(uploadFileName, localFilePath, options, resultListener) { ... }
            if (sampleFile.exists()) {
                Amplify.Storage.uploadFile(
                        keyname,
                        sampleFile.getAbsolutePath(),
//                options,
                        result -> {
                            appLog(" WVS uploadFileToAWSS3()", "Successfully uploaded: " + result.getKey());
                            Toast.makeText(context, "Successfully uploaded: " + result.getKey(), Toast.LENGTH_LONG).show();
                            //wvs todo: update local tblScripts...
                            Scripts tmpscripts = new Scripts(0, sfilename, sBucket, "public/", downloaddate, null, -1);
                            sr.insert(tmpscripts);
                        },
                        storageFailure -> {
                            appLog(" WVS uploadFileToAWSS3()", "Upload error." + storageFailure);
                            Toast.makeText(context, "Updload error: " + storageFailure, Toast.LENGTH_LONG).show();
                        }
                );
            }
        }
    }

    public static void downloadFileFromAWSS3(String sfilename, Context context) {
//      sampleFile = new File(Environment.getExternalStorageDirectory(), sfilename);
        Amplify.Storage.downloadFile(
                sfilename,
//                getApplicationContext().getFilesDir() + "/download.txt",
                Environment.getExternalStorageDirectory() + "/" + sfilename,
                result -> appLog("StorageQuickStart", "Successfully downloaded: " + result.getFile().getName()),
                storageFailure -> appLog("StorageQuickStart", storageFailure.getMessage()  + storageFailure)
        );
    }

    public static void listFilesFromAWSS3(String sfilename_s3_contents) throws IOException {
//      wvs todo how to reference bucket name in amplifyconfiguration.json
//      path getout-android-bucket95810-getoutenv, /

        try {
            String state = Environment.getExternalStorageState();
            String pathToExtStorage = Environment.getExternalStorageDirectory().toString();
            File sd = new File(pathToExtStorage);
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                if (Environment.MEDIA_MOUNTED.equals(state) ||
                        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                    if (sd.canWrite()) {

                        File S3ContentsFile = null;
//                        String sfilename_s3_contents = "s3_contents.txt"; //""uploaded_contents.txt";
                        //      MainActivity.downloadFileFromAWSS3(sfilename_s3_contents,context);
                        S3ContentsFile = new File(sd, sfilename_s3_contents);
                        //      FileOutputStream fOut2 = new FileOutputStream(S3ContentsFile, true);  //wvs this will append w/o it will create new
                        FileOutputStream fOut2 = new FileOutputStream(S3ContentsFile);
                        OutputStreamWriter myOutWriter2_s3_contents = new OutputStreamWriter(fOut2);
                        myOutWriter2_s3_contents.append("Files on S3:\n");

                        Amplify.Storage.list(
                                "",
                                storageListResult -> {
                                    for (StorageItem item : storageListResult.getItems()) {
                                        appLog(" WVS StorageQuickStart", "Item: " + item.getKey());
                                        try {
                                            myOutWriter2_s3_contents.append("S3:" + item.getKey() + "\n");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
//                                    appLog(TAG, " WVS myOutWriter2.notify(), FINISHED listing all S3 items");
                                    try {
                                        myOutWriter2_s3_contents.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        fOut2.close();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } finally {
//                                        MainActivity.ExportRoutestoAWSS3();
                                    }

                                },
                                storageFailure -> {
                                    Log.e("StorageQuickStart", storageFailure.getMessage(), storageFailure);
                                    try {
                                        myOutWriter2_s3_contents.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        fOut2.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                        );
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {

        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean bBackFromNotification;
        if (intent.hasExtra("id")){
//                wvs todo should delete event
            appLog(TAG, " WVS onNewIntent() calling BackFromNotificaition_delEvent");
            bBackFromNotification = BackFromNotification_delEvent_gotoNote(intent);
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
//        appLog(TAG, " WVS onStart() sTitle = " + ((sTitle == null) ? "" : sTitle));
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
//        appLog(TAG, " WVS onStart() pageViewer.getCurrentItem() = " + viewPager.getCurrentItem());
        // Restore the state of the buttons when the activity (re)launches.
        boolean bReqLocationUpdates = Utils.getRequestingLocationUpdates(this);
//        setButtonsState(bReqLocationUpdates);
//      could be returning from edit note and on Notes tab
        if ( Utils.getTabNumber(mContext) == 2){
            setMenuState(bReqLocationUpdates, appMenuChoices[1]);
        } else {
            setMenuState(bReqLocationUpdates, appMenuChoices[0]);
        }


        if (bReqLocationUpdates) {
            if (mRecognition == null) {
                mRecognition = new Recognition();
                mRecognition.StartTracking(this);
            }
        }

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
//      It can be operated programmatically using an interface that it defines and exports. Clients establish a connection to the Service object and use that connection to call into the service.
//      The connection is established by calling Context.bindService(), and is closed by calling Context.unbindService().
//      Multiple clients can bind to the same service. If the service has not already been launched, bindService() can optionally launch it.
        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        appLog(TAG, " WVS onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, new IntentFilter(ACTION_SHOW_TRANS));

    }

    @Override
    protected void onPause() {
        super.onPause();
//        appLog(TAG, " WVS onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
    }

    @Override
    protected void onStop() {
//        appLog(TAG, " WVS onStop - unBindService, promote to ForegroundService");
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        save zoom level
//        appLog(TAG, " WVS onSaveInstanceState() sTitle = " +sTitle);
        String appname = getResources().getString(R.string.app_name);
        if (sTitle!=null && !sTitle.equals("") && sTitle.indexOf(appname) < 0 && sTitle.indexOf(Utils.getCurrentLogin(this)) < 0 ) {
//          sTitle defined when initial recording started... could be stopped
            if (Utils.getRequestingLocationUpdates(mContext)) {
//                outState.putString("caption", Utils.getRouteName(mContext));
                outState.putString("caption", sTitle);
            } else {

                if (sTitle.indexOf(": finished") < 0) {
                    outState.putString("caption", Utils.getRouteName(mContext) + ": finished!");
                } else {
                    sTitle = appname + ": " + Utils.getCurrentLogin(this);
                    outState.putString("caption", sTitle);
                }
            }
        } else {
            sTitle = appname + ": " + Utils.getCurrentLogin(this);
            outState.putString("caption", sTitle);
        }
        outState.putBoolean("paused", bPaused);
        setTitle(sTitle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        appLog(TAG, " WVS onActivityResult() ");
        appLog(TAG, Integer.toString(requestCode));
        File tmpfile;
        String htmlDocument="";
        int iFileStart=0;
        String tmpCost;
        String tmpfilename;
        String tmpfilepath;
        String tmpNoteItem, tmpTag, tmpImage;
        tmpCost=tmpNoteItem=tmpTag=tmpImage="";

        int iPathStart=0;
        String [] notelines;
        NotesRepo nsr;
        Notes tmpnotes;
        Long lnotesid;
        NoteRepo nr;
        Note tmpnote;

        if (requestCode == IMPORT_NOTE){
            //        data.getData().getLastPathSegment();  data.getData().getPath()
            if (data.getData().getLastPathSegment().lastIndexOf("/") == -1 ) {
                iFileStart = data.getData().getLastPathSegment().lastIndexOf(":")+1;
                tmpfilepath = "/";
            } else {
                iFileStart = data.getData().getLastPathSegment().lastIndexOf("/")+1;
                iPathStart = data.getData().getLastPathSegment().lastIndexOf(":")+1;
                tmpfilepath = "/" + data.getData().getLastPathSegment().substring(iPathStart,iFileStart);
            }
            tmpfilename = data.getData().getLastPathSegment().substring(iFileStart);
            //https://stackoverflow.com/questions/5568874/how-to-extract-the-file-name-from-uri-returned-from-intent-action-get-content#25005243
//            appLog(TAG, " WVS IMPORT NOTE: " + tmpfilename);
//            new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES))+"/" + tmpName );
//          wvs: todo: savefile_NotebyNotesId creates file in root, not all potential import reside in /bluetooth...
//            tmpfile = new File(Environment.getExternalStorageDirectory().toString() + "/bluetooth/" + tmpfilename);
            tmpfile = new File(Environment.getExternalStorageDirectory().toString() + tmpfilepath + tmpfilename);


            try {
                if (tmpfile.exists()){
                    nsr = new NotesRepo(this);
                    tmpnotes = new Notes(0,tmpfilename,"", DBInfo.TABLE_TBLNOTE_KEY_Date , 0);
                    lnotesid = nsr.insert(tmpnotes);
                    nr = new NoteRepo(this);

                    htmlDocument = getStringFromFile(tmpfile.getAbsolutePath());
                    notelines = htmlDocument.split("\\r?\\n");
                    for (String line: notelines){
//                        appLog(TAG, line);
//                        line.substring(1,line.lastIndexOf(";")-1);  //item
//                        line.substring(line.lastIndexOf(";")+1);  //tag
//                        line.contains(";")
                        int cnt1 = line.split(";").length;
//cnt1=1 no ";", =2 one ";", =3 two ";"
//                      if (line.lastIndexOf(";")== -1) {
                        if (cnt1 == 1) {
                            tmpNoteItem=line;
                            tmpTag="";
                            tmpImage="";
                        } else if (cnt1 == 2) {
                            tmpNoteItem = line.substring(0,line.lastIndexOf(";")).trim();
                            tmpTag =line.substring(line.lastIndexOf(";")+1).trim();
                            tmpImage="";
                        } else if (cnt1 >= 3) {
                            tmpNoteItem = line.substring(0,line.indexOf(";")).trim();
                            tmpTag =line.substring(line.indexOf(";")+1, line.lastIndexOf(";")).trim();
                            tmpImage=line.substring(line.lastIndexOf(";")+1).trim();
                        }
//                      DON'T import lines: added name: all/checked items, filename OR BLANK LINES
                        if (tmpNoteItem.contains("Name:") || tmpNoteItem.length() == 0) {
//                            DON'T INSERT
                        } else {
                            tmpnote = new Note(0, tmpNoteItem, "", tmpTag, lnotesid.intValue(), 0, tmpImage, tmpCost,null);
                            nr.insert(tmpnote);
                        }
                    }
                }
            } catch (Exception e){
                appLog(TAG, e.getMessage() +", " + e.getLocalizedMessage());
            } finally {
//                appLog(TAG,htmlDocument.toString());
            }
        } else if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                subscribe_toSteps();
                subscribe_toDistance();
                subscribe_toHeart();
                subscribe_toMoveMinutes();
                subscribe_toLocations();
            }
        } else if (requestCode == IMPORT_DB) {

//            String DBPath="";
//            DBPath = getPathNameForSQLiteDB(mContext,data);
//            if (!DBPath.equals("")) {
//                // import current db IMPORT_DB
//                DBHelper dbHelper = new DBHelper(mContext);
//                try {
////                    dbHelper.resetDataBase_fromAssets("2020-02-06 09-50-18_Maps05.db"); //this works
//                    dbHelper.resetDataBase_fromAssets(DBPath);  // this now works sdcard root where db exports are located
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
        } else if (requestCode == START_CAMERA & resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            if (extras.containsKey("imagefile")){
                tmpfile = (File) extras.get("imagefile");
                long locationsid = Utils.getCurrentPathNameID(mContext);
                String latlng = extras.getString("latlng");

                if (tmpfile.exists()){
//                  check to see if note exists based on locationsid
                    nr = new NoteRepo(mContext);
                    tmpnote = nr.getNoteByLocationsId(locationsid);
                    String sImages ="";
                    String newimagefilename = tmpfile.getAbsolutePath().substring(tmpfile.getAbsolutePath().lastIndexOf("/") + 1);
                    if (tmpnote !=null){
//                      add image to note
                        sImages = tmpnote.getImage();
                        if (sImages.length() > 0){
                            sImages += "," + newimagefilename.toString();
                        } else {
                            sImages = newimagefilename.toString();
                        }
                        tmpnote.setImage(sImages);
//                        lat/lng: (37.76095613513063,-122.40868058055638)
                        if (tmpnote.getTag() != null && tmpnote.getTag().contains("lat/lng:") && latlng.contains("lat/lng:")){
                            tmpnote.setTag(tmpnote.getTag()+ "," +latlng);
                        } else if (latlng.contains("lat/lng:")) {
                            tmpnote.setTag(latlng);
                        }
                        nr.update(tmpnote,true);
                    } else {
//                      create note with image
                        tmpnote = new Note(0,"Pictures",null,latlng,null,0,newimagefilename,null, (int) locationsid);
                        nr.insert(tmpnote);

                    }
//                  create or updated note.imagefile
//                    PopulateLinearLayoutWithImages(this,myGallery,txtImage.getText().toString() , viewonclicklistener);
//                    myHorScrollView.setVisibility(View.VISIBLE);
//                    PopulateLinearLayoutWithImages(this, myGallery, txtImage.getText().toString(), new MyViewOnClickListener());

                }


            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

//        appLog(TAG, " WVS current page is " + viewPager.getCurrentItem());
        this.mMenu = menu;
//        appLog(TAG, " WVS onCreateOptionsMenu mMenu = " + ((mMenu == null) ? "NULL" : "not NULL"));
        //        return true;
        setMenuState(true, appMenuChoices[2]);
        return super.onCreateOptionsMenu(menu);

    }

    private void setMenuState(boolean bCheckLocationUpdates, String options) {
        boolean breqLocationUpdates = false;
        if(bCheckLocationUpdates){
            breqLocationUpdates = Utils.getRequestingLocationUpdates(mContext);
        }
        if (mMenu != null) {
            MenuItem itemRun = mMenu.findItem(R.id.mi_main_Run);
            MenuItem itemPause = mMenu.findItem(R.id.mi_main_Pause);
            MenuItem itemCamera = mMenu.findItem(R.id.mi_main_Camera);
            if (breqLocationUpdates){
//                @android:drawable/star_big_on
//                item3.setIcon(android.R.drawable.btn_star_big_off);
                itemRun.setIcon(android.R.drawable.ic_delete);
                itemRun.setTitle("Stop");
                itemPause.setVisible(true);
                itemCamera.setVisible(true);
                if (Utils.getPausedState(mContext)){
                    itemPause.setIcon(android.R.drawable.ic_media_play);
                    itemPause.setTitle("Play");
                } else {
                    itemPause.setIcon(android.R.drawable.ic_media_pause);
                    itemPause.setTitle("Pause");
                }
            } else {
                itemRun.setIcon(android.R.drawable.btn_star_big_on);
                itemRun.setTitle("Run");
                itemPause.setVisible(false);
                itemCamera.setVisible(false);
            }
//            MenuItem item3 = mMenu.findItem(R.id.getPreviousRoutes);
//            item3.setEnabled(breqLocationUpdates);
            MenuItem itemRouteActivityOffset = mMenu.findItem(R.id.mi_main_RouteActivityOffset);
            String sfinalOffset = Utils.getRouteActivityOffset_msg(mContext);
            itemRouteActivityOffset.setTitle(sfinalOffset);

            MenuItem itemRouteGeoFenceTimeOut = mMenu.findItem(R.id.mi_main_RouteActivityGeoFenceTimeOut);
            String sfinalGeoFenceTimeOut = Utils.getRouteActivityGeoFenceTimeOut_msg(mContext);
            itemRouteGeoFenceTimeOut.setTitle(sfinalGeoFenceTimeOut);

//          wvs hint pass value to resource string in menu "share health scripts for 2020"
            MenuItem itemShareHealthCurrentYear = mMenu.findItem(R.id.mi_main_sharehealth);
            String ShareHealthcurrentYear = Utils.getShareHealthCurrentYear_msg(mContext);
            itemShareHealthCurrentYear.setTitle(ShareHealthcurrentYear);

//          wvs hint pass value to resource string in menu "update tblHealth for 2020"
            MenuItem itemUpdate_tblHealth = mMenu.findItem(R.id.mi_main_updatetblhealth);
            String UpdatetblHealthcurrentYear = Utils.getUpdatetblHealthCurrentYear_msg(mContext);
            itemUpdate_tblHealth.setTitle(UpdatetblHealthcurrentYear);


//          Manage tab menu: TODAY, ROUTES, NOTES
            MenuItem itemAddNote = mMenu.findItem(R.id.mi_main_AddNote);
            MenuItem itemNOTES = mMenu.findItem(R.id.mi_main_notes);

            MenuItem itemROUTES = mMenu.findItem(R.id.mi_main_routes);
            if (options.equals(appMenuChoices[0])){
//              no changes
            } else if (options.equals(appMenuChoices[1])){
//              display AddNote
                itemAddNote.setVisible(true);
                itemNOTES.setVisible(true);
                itemRun.setVisible(false);
                itemPause.setVisible(false);
                itemCamera.setVisible(false);
//              hide TODAY, ROUTES

                itemROUTES.setVisible(false);
            } else if (options.equals(appMenuChoices[2])){
//              hide AddNote
                itemAddNote.setVisible(false);
                itemNOTES.setVisible(false);
                itemRun.setVisible(true );
//              display TODAY, ROUTES

                itemROUTES.setVisible(true);
                if (breqLocationUpdates){
                    itemPause.setVisible(true);
                    itemCamera.setVisible(true);
                    if (Utils.getPausedState(mContext)){
                        itemPause.setIcon(android.R.drawable.ic_media_play);
                        itemPause.setTitle("Play");
                    } else {
                        itemPause.setIcon(android.R.drawable.ic_media_pause);
                        itemPause.setTitle("Pause");
                    }
                } else {
                    itemPause.setVisible(false);
                }
            }
        }
    }

//    private void setButtonsState(boolean requestingLocationUpdates) {
//        appLog(TAG, " WVS setButtonsState mMenu = " + ((mMenu == null) ? "NULL" : "not NULL"));
////        if (getRequestingLocationUpdates) {
////            mRequestLocationUpdatesButton.setEnabled(false);
////            mRemoveLocationUpdatesButton.setEnabled(true);
////        } else {
////            mRequestLocationUpdatesButton.setEnabled(true);
////            mRemoveLocationUpdatesButton.setEnabled(false);
////        }
//        setMenuState(requestingLocationUpdates);
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
//        appLog(TAG, " WVS onOptionsItemSelected " + id);
        String pasteData="";
        if (id == R.id.mi_main_Camera){
            Intent cameraIntent = new Intent(this, AndroidCameraApi.class);
//          wvs todo: add latlng, cameraIntent.putExtra("latlng", latlng)
            Long locationsid = Utils.getCurrentPathNameID(this);
            LocationRepo lr = new LocationRepo(mContext);
            LatLng tmpLatLng = lr.getLastLatLng_LocationsId(locationsid);
            cameraIntent.putExtra("latlng", tmpLatLng.toString());
            startActivityForResult(cameraIntent, START_CAMERA);

        } else if (id == R.id.mi_main_Run) {
            if (item.getTitle().equals("Run") ){
//              disabled, so start requests...
//                LocationRepo lr = new LocationRepo(this);
//                lr.OnPauseSetEnterExit(1l);
//                lr.getTimeDifference_wPauses_LocationsId(94l);
//                LocationsRepo lrs = new LocationsRepo(this);
//                lrs.updateTotalTimeLocationsId(94l);
                showDialogNewRoute(true);
                return true;
            } else if (item.getTitle().equals("Stop")){
//              enabled, so stop requests
                sTitle = Utils.getRouteName(mContext) + ": finished";
                setTitle(sTitle);
                mService.removeLocationUpdates();
                if (mRecognition != null) {
                    mRecognition.StopTracking(this);
                    appLog(TAG, " WVS OptionsItemSelected stopping TRANSACTIONS");
                } else {
                    appLog(TAG, " WVS OptionsItemSelected mRecognition=NULL, unable to stop TRANSACTIONS");
                }
//              last step update total time
                Long locationsid = Utils.getCurrentPathNameID(this);
                LocationsRepo lsr = new LocationsRepo(this);

                lsr.updateTotalTimeLocationsId(locationsid);
                Double dblElevationDiff = 0d;
                dblElevationDiff = lsr.getElevationDiffLocationsId(locationsid);
                lsr.updateElevationDiffLocationsId(locationsid, dblElevationDiff);

//              last step update total distance based on gaps/transitions due to connecting different maps
//                LocationRepo lr = new LocationRepo(this);
//              wvs 3/27/20  mLocationArray = lr.getRouteActivityByLocationsId(locationsid, this);
//                mLocationArray = lr.getRouteDetailsByLocationsId(locationsid);
//                WVS_MapHelper mh = new WVS_MapHelper();
//                Float totaldistance = mh.Determine_Update_Distance(mLocationArray,this, true);

                String[] startendtimes = lsr.getStartEndTimesbyLocationsId(locationsid);
                boolean btblHealth = false;
                boolean btblLocations = true;
                DataForPeriod_update_tblLocations(startendtimes, locationsid, btblHealth, btblLocations);

//                no need to call below, taken care of when calling DataForPeriod_update_tblLocations w false, true
//                last call to getHistoryClient_Move_final will call DataForPeriod_update_tblHealth(startendtimes) with previous week start

                return true;
            }

        } else if (id == R.id.mi_main_Pause){
            if (bPaused == true){
                bPaused = false;
                Utils.setPausedState(mContext,false);
                item.setIcon(android.R.drawable.ic_media_pause);
                sTitle = Utils.getRouteName(mContext) + ": recording";
                setTitle(sTitle);
            } else {
//              default... will be paused
                bPaused = true;
                Utils.setPausedState(mContext,true);
                item.setIcon(android.R.drawable.ic_media_play);
                sTitle = Utils.getRouteName(mContext) + ": paused";
                setTitle(sTitle);
                Long locationsid = Utils.getCurrentPathNameID(this);
                LocationRepo lr = new LocationRepo(this);
                lr.OnPauseSetEnterExit(locationsid);
            }
            setMenuState(true, "no change");
        }
        else if (id == R.id.mi_main_Login) {
            showDialogNewLogin();
        }
        else if (id == R.id.mi_main_cleartblLogin){
             showDialogResetUsers();
        }
        else if (id == R.id.mi_main_RandomTask) {
            if (false) {
                //Get time before alarm
                int minutesBeforeAlert = 1;
                long alertTime = convertMinutesToMilli(minutesBeforeAlert) - System.currentTimeMillis();
//            long current =  System.currentTimeMillis();
                int random = (int) (Math.random() * 50 + 1);
                String tag = UUID.randomUUID().toString(); // NoteId EventId eventdate

                Data data = new Data.Builder()
                        .putString("title", "this is the title")
                        .putString("text", "this is the text")
                        .putInt("id", random)
                        .build();

                NotificationHandler.scheduleReminder(alertTime, data, tag);
            }
            if (false) {
                // update/replace db...
                DBHelper dbHelper = new DBHelper(mContext);
                try {
                    //dbHelper.resetDataBase_fromAssets("Maps05_20191009.db");
                    dbHelper.resetDataBase_fromAssets("Maps05_20200112.db");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (true) {
                // export current db
                DBHelper dbHelper = new DBHelper(mContext);
                try {
                    dbHelper.exportDB();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return true;
        } else if (id == R.id.mi_main_ImportDB){
//            Intent mediaIntent = new Intent(Intent.ACTION_GET_CONTENT);
//            Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
//            mediaIntent.setDataAndType(uri, "*/*");
//            mediaIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//            startActivityForResult(mediaIntent, IMPORT_DB);
            AssetManager assetMgr = getAssets();
            String[] assetsIWant = new String[0];
            try {
                assetsIWant = assetMgr.list("");
            } catch (IOException e) {
                e.printStackTrace();
            }
//            for(String asset: assetsIWant) {
//                appLog(TAG,"Asset folder item = " + asset);
//            }
            if (assetsIWant.length >= 1){
                showDialogResetDB(assetsIWant);
            }
            return true;

        } else if (id == R.id.miListSubscriptions){
            Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
//                    .listSubscriptions(DataType.TYPE_ACTIVITY_SAMPLES)
                    .listSubscriptions()
                    .addOnSuccessListener(new OnSuccessListener<List<Subscription>>() {
                        @Override
                        public void onSuccess(List<Subscription> subscriptions) {
                            Log.i(TAG, " WVS subscription count = " + subscriptions.size());
                            String sScratchPad="ACTIVE SUBSCRIPTIONS...\n";
                            for (Subscription sub: subscriptions){
//                                Log.i(TAG, " WVS subscription: " + sub.toString());
                                Log.i(TAG, " WVS subscription: " + sub.getDataType().getName());
                                sScratchPad = sScratchPad + "\n" + sub.getDataType().getName();
                            }
                            Intent intent1 = new Intent(mContext, ScratchPad.class);
                            intent1.putExtra("scratchpad",sScratchPad);
                            startActivity(intent1);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, " WVS failed to list subscriptions");
                        }
                    });
        }

        else if (id == R.id.mi_main_Add_VocabList){
//      https://www.dictionaryapi.com/api/v1/references/spanish/xml/car?key=75da9658-d5ee-4b0d-8c00-1dfe4eaca8a4
//wvs   https://www.dictionaryapi.com/api/v1/references/sd3/xml/test?key=e236effc-24a4-4eff-8047-62488429f559
//            https://www.dictionaryapi.com/api/v1/references/sd3/xml/test?key=75da9658-d5ee-4b0d-8c00-1dfe4eaca8a4
//https://www.youtube.com/watch?v=Q7e52RGimJY

            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if ((clipboard.hasPrimaryClip())) {
                ClipData.Item item1 = clipboard.getPrimaryClip().getItemAt(0);
                // Gets the clipboard as text.
                pasteData = item1.getText().toString();
            }
            if (pasteData == null | pasteData.length()==0){
//                pasteData = "necessary occassion aggression apparratus errand channel";
                android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Vocabulary List");
                alertDialog.setMessage("Please copy a vocabulary list");
                alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            } else {
                final String vocab = pasteData;
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Confirm");
                builder.setMessage("Create list:("+pasteData +")?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        dialog.dismiss();
                        CreateVocabList(vocab);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
                android.app.AlertDialog alert = builder.create();
                alert.show();
            }

//            ParseMWDefinition(pasteData);
//          refresh list/frag
//            getSupportFragmentManager().beginTransaction().detach(frag).attach(frag).commit();
            return true;
        }
        else if (id == R.id.mi_main_exportroutes) {
            if (locationsfrag != null) {
//                locationsfrag.ExportRoutes();
                try {
                    String sfile= "s3_contents.txt";
                    listFilesFromAWSS3(sfile);
//                  wait for s3_contents.txt then call MainActivity.ExportRoutestoAWSS3();
                    File sd = WVS_Utils.getWritableFile(sfile);
                    File s3File = new File(sd,sfile);
                    int ncnt = 0;
                    while (!s3File.exists()){
                        wait(1000);
                        ncnt += 1;
                        if (ncnt > 5){
                            break;
                        }
                    }
                    if (s3File.exists()){
                        ExportRoutestoAWSS3();
                    }

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(mContext, "No routes selected", Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.mi_main_connectroutes) {
            if (locationsfrag != null) {
                locationsfrag.ConnectRoutes();
                locationsfrag.RefreshList();
            } else {
                Toast.makeText(mContext, "No routes selected", Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.mi_main_RouteActivityOffset) {
            showDialogUpdateValue(getResources().getString(R.string.menu_main_RouteActivityOffset));
        } else if (id == R.id.mi_main_RouteActivityGeoFenceTimeOut) {
            showDialogUpdateValue(getResources().getString(R.string.menu_main_RouteActivityGeoFenceTimeOut));
        } else if (id == R.id.mi_main_updatetblhealth) {
            String sYear = getstrCurrentYear();
            String[] startendtimes = {"",""};
            startendtimes[0] = sYear + "-01-01 00:00:00";
//          END TIME is yesterday
            startendtimes[1] = new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(new Date(System.currentTimeMillis()-24*60*60*1000));
//          test
//            startendtimes[0] = "2020-10-15 12:41:57";
//            startendtimes[1] = "2020-10-15 12:51:58";

            DataForPeriod_update_tblHealth(startendtimes);
        } else if (id == R.id.mi_main_sharehealth) {
            String sYear = getstrCurrentYear();
            String[] startendtimes = {"",""};
            startendtimes[0] = sYear + "-01-01 00:00:00";
//          END TIME is yesterday
            startendtimes[1] = new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(new Date(System.currentTimeMillis()-24*60*60*1000));
            HealthRepo hr = new HealthRepo(mContext);
            hr.createScript_HealthtoAWSS3_toSQLite3(startendtimes, mContext);
//            hr.createScript_HealthtoAWSS3_toMysSQL(startendtimes, "TYPE_DISTANCE_DELTA", mContext);
//            hr.createScript_HealthtoAWSS3_toMysSQL(startendtimes, "TYPE_STEP_COUNT_CUMULATIVE", mContext);
//            hr.createScript_HealthtoAWSS3_toMysSQL(startendtimes, "TYPE_HEART_POINTS", mContext);
//            hr.createScript_HealthtoAWSS3_toMysSQL(startendtimes, "TYPE_MOVE_MINUTES", mContext);

        } else if (id == R.id.mi_main_AddNote){
            Intent intent1 = new Intent(mContext, NoteActivity.class);
//            intent1.putExtra("notesid", objA_Notes.get(position));
            intent1.putExtra("requestCode", ADD_NOTE_REQUEST);
            startActivity(intent1);
        } else if (id == R.id.mi_main_import_note) {
            Intent mediaIntent = new Intent(Intent.ACTION_GET_CONTENT);
//getExternalStoragePublicDirectory??
            Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                    + "/bluetooth/");
            mediaIntent.setDataAndType(uri, "text/plain");
//            Type 	Description 	Example of typical subtypes
//            text 	Represents any document that contains text and is theoretically human readable 	text/plain, text/html, text/css, text/javascript
//            image 	Represents any kind of images. Videos are not included, though animated images (like animated gif) are described with an image type.
//                      image/gif, image/png, image/jpeg, image/bmp, image/webp, image/x-icon, image/vnd.microsoft.icon
//            audio 	Represents any kind of audio files 	audio/midi, audio/mpeg, audio/webm, audio/ogg, audio/wav
//            video 	Represents any kind of video files 	video/webm, video/ogg
//            application 	Represents any kind of binary data.
//                      application/javascript, application/octet-stream, application/pkcs12, application/vnd.mspowerpoint, application/xhtml+xml, application/xml, application/pdf

            mediaIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//            mediaIntent.setType("text/plain"); //set mime type as per requirement
            startActivityForResult(mediaIntent, IMPORT_NOTE);
            return true;
        } else if (id == R.id.mi_main_about) {
            Intent intent1 = new Intent(mContext, About.class);
            startActivity(intent1);

        }

        return super.onOptionsItemSelected(item);
    }


    private long convertMinutesToMilli(int userInput){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, userInput);
        return cal.getTimeInMillis();
    }

    public void showDialogNewRoute(Boolean bnewroute){
       AlertDialog.Builder builder = new AlertDialog.Builder(this);
       builder.setTitle("choose activity");
//       final String[] choices = {"Walk", "Dog Walk", "Run", "Going Out", "To Gym", "Pickup"};
        final String[] choices_stock = appRouteChoices;
        LocationsRepo lrs = new LocationsRepo(mContext);
        ArrayList <String> dbChoices = new ArrayList<>();
        dbChoices = lrs.GetPopularRouteNames();
        for (int i=0; i < choices_stock.length; i++) {
            if (! dbChoices.contains(choices_stock[i])){
                dbChoices.add(choices_stock[i]);
            }
        }

        final String[] choices = dbChoices.toArray(new String[dbChoices.size()]);


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
           }
       });
       int indx = 0;
       for (int i = 0; i < choices.length; i++ ){
           if ( choices[i].equals(Utils.getRouteName(mContext))){
               indx = i;
               break;
           }
       }

       builder.setSingleChoiceItems(choices, indx, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (bnewroute) {
//                    appLog(TAG, String.valueOf(which));
                    String pathname = choices[which];
                    Utils.setRouteName(mContext, pathname);
                    startRequests(pathname);
                    sTitle = pathname + ": recording";
                    setTitle(sTitle);
//              wvs todo: determine global WithinGeoTarget, default to home
                    Utils.setPausedState(mContext, false);
                    dialog.dismiss();
                }
            }
       });
       AlertDialog alert = builder.create();
       alert.show();
   }

   public void showDialogUpdateValue(String sMsg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.edit_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle(sMsg);
        dialogBuilder.setMessage("e.g. choose between 2.00 and 8.00 seconds");
        final String sRouteActivityOffset = Utils.getRouteAcivityOffset(mContext);
        final String sRouteActivityGeoFenceTimeout = Utils.getRouteAcivityGeoFenceTimeOut(mContext);

        if (sMsg.equals(getResources().getString(R.string.menu_main_RouteActivityOffset))) {
            DecimalFormat format = new DecimalFormat("##.##");
            String formatted = format.format(Float.valueOf(sRouteActivityOffset));
            edt.setText(formatted);
       } else if (sMsg.equals(getResources().getString(R.string.menu_main_RouteActivityGeoFenceTimeOut))) {
           edt.setText(sRouteActivityGeoFenceTimeout);
       }
//      format so only number representing secconds

        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String sOffset = edt.getText().toString();

//                "@string/menu_main_RouteActivityOffset"
                if (sMsg.equals(getResources().getString(R.string.menu_main_RouteActivityOffset))){
                    if (!sRouteActivityOffset.equals(sOffset)){
                        Utils.setRouteAcivityOffset(mContext,sOffset);
                        setMenuState(true, appMenuChoices[0]);
                    }
                } else if(sMsg.equals(getResources().getString(R.string.menu_main_RouteActivityGeoFenceTimeOut))){
                    if (!sRouteActivityGeoFenceTimeout.equals(sOffset)) {
                        Utils.setRouteAcivityGeoFenceTimeOut(mContext, sOffset);
                        setMenuState(true, appMenuChoices[0]);
                    }
                }

                return;
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void showDialogResetDB(String[] tmpDBs){

//      wvs string array to array list, filter
        ArrayList <String> tmpDBArray = new ArrayList( Arrays.asList(tmpDBs));
//        ArrayList <String> tmpDBArray = (ArrayList<String>) Arrays.asList(tmpDBs);
        Iterator<String> iterator = tmpDBArray.iterator();
        while (iterator.hasNext()) {
            String string = iterator.next();
            if (!string.contains(".db")) {
                iterator.remove();
            }
        }
        String [] DBs =  tmpDBArray.toArray(new String[tmpDBArray.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset db based on");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        int indx = 0;

        builder.setSingleChoiceItems(DBs, indx, new DialogInterface.OnClickListener() {
            //      builder.setSingleChoiceItems(applogins, indx, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                appLog(TAG, String.valueOf(which) +", DB = " + tmpDBs[which]);
                // import current db IMPORT_DB
                DBHelper dbHelper = new DBHelper(mContext);
                try {
//                    dbHelper.resetDataBase_fromAssets("2020-02-06 09-50-18_Maps05.db"); //this works
                    dbHelper.resetDataBase_fromAssets(tmpDBs[which]);  // this now works sdcard root where db exports are located

                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }
   public void showDialogNewLogin(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("choose user");

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        int indx = 0;
        String[] tmpLogins = new String[mArrayListLogins.size()];
        for (int i = 0; i < mArrayListLogins.size(); i++) {
            tmpLogins[i] = mArrayListLogins.get(i).getUsername();
            if (i == Utils.getCurrentLoginID(this)) {
               indx = i;
//               break;
            }
        }

        builder.setSingleChoiceItems(tmpLogins, indx, new DialogInterface.OnClickListener() {
//      builder.setSingleChoiceItems(applogins, indx, new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
//               appLog(TAG, String.valueOf(which));
//             wvs todo must test which value compared to non sequential loginid... mArrayListLogins.get(which).getLoginId()
//               Utils.setCurrentLoginId(mContext, which);
               Utils.setCurrentLoginId(mContext, mArrayListLogins.get(which).getLoginId().intValue() );
        //                   Utils.setCurrentLogin(mContext, applogins[which]);
               Utils.setCurrentLogin(mContext, mArrayListLogins.get(which).getUsername());
               setTitle(Utils.getCurrentTitleUser(mContext));
               dialog.dismiss();
           }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void showDialogResetUsers() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = this.getLayoutInflater();
//        final View dialogView = inflater.inflate(R.layout.yes_no_dialog, null);
//        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Reset all Users?");
        dialogBuilder.setMessage("Yes - click Done" + "\n" + "No - click Cancel");

        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//              delete all rows from tblLogin
                LoginRepo lr = new LoginRepo(mContext);
                int iRes = lr.deleteAll();
                mArrayListLogins.clear();
                Utils.setCurrentLoginId(mContext,0);
                checkRDSLogins();
                return;
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void checkRDSLogins() {

        blnRDSLogin = false;
        // https://developer.android.com/training/volley/simple
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = Utils.getGetOutURL(mContext) + "/hello";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // [[1,"walter","walter@email.com"],[2,"lucas","lucas@email.com"]]\n
//                        appLog(TAG, response.trim());
                        String sets = response.replace("\n","");  // remove newline
                        sets = sets.substring(1,sets.length()-1);  // assumes first [, last ]
                        //int cnt = sets.split("\\]" ).length;
                        int cnt = response.split("," ).length/3;
                        String firstset;
                        // add dummy index 0
                        mArrayListLogins.clear();
                        tmpLogin = new Login(0,"none","none");
                        mArrayListLogins.add(tmpLogin);
                        arrayLogins = new String[cnt+1];
                        arrayLogins[0]="none";
                        int fpos = 1;
                        int lpos =1;
                        for (int i = 0; i < cnt; i++){
                            lpos = sets.indexOf("]",fpos+1);
                            firstset = sets.substring(fpos, lpos);
                            fpos = lpos+3; //],[
                            tmpLogin = new Login(Integer.parseInt( firstset.split(",")[0]), firstset.split(",")[1], firstset.split(",")[2]);
                            mArrayListLogins.add(tmpLogin);
                            arrayLogins[i+1] = firstset.split(",")[1];
                        }
                        blnRDSLogin = true;
//                      truncate and update tblLogin
                        LoginRepo lr = new LoginRepo(mContext);
                        int iRes = lr.deleteAll();
                        for (int i = 0; i <= cnt; i++){
                            lr.insert(mArrayListLogins.get(i));
                        }
                        if (Utils.getCurrentLoginID(mContext)==0){
                            showDialogNewLogin();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //textView.setText("That didn't work!");  coming from tbllogin empty
                appLog(TAG,"aws rds login not accessible");
                blnRDSLogin = false;
                if (! blnappLoginEQtblLogin) {
                    if (mArrayListLogins != null && mArrayListLogins.size() == 0) {
                        LoginRepo lr = new LoginRepo(mContext);

                        int iRes = lr.deleteAll();
                        for (int i = 0; i < applogins.length; i++) {
                            tmpLogin = new Login(i, applogins[i], applogins[i] + "@email.com");
                            lr.insert(tmpLogin);
                            mArrayListLogins.add(tmpLogin);
                        }
                        blnappLoginEQtblLogin = true;
                        if (Utils.getCurrentLoginID(mContext)==0){
                            showDialogNewLogin();
                        }
                    }
                }
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    //    public class Logins{
//        int id;
//        String user;
//        String email;
//    }

   private void startRequests(String pathname){
       if (!checkPermissions_Location()) {
           requestPermissions_Location();
       } else {
           Utils.setCurrentPathName(this, pathname);
           Utils.setCurrentPath_HitGeoFence(this,false);
           LocationsRepo lsr = new LocationsRepo(this);
           long locationsid = lsr.insert(pathname, Utils.getCurrentLoginID(this));
           lsr.resetGEOLocationWithin();
           Utils.setCurrentPathNameId(this, locationsid);
           mService.requestLocationUpdates();
           mRecognition = new Recognition();  // activity recognition e.g. walking running
           mRecognition.StartTracking(this);
       }
   }



    /**
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermissions_Location() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions_Location() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
//            appLog(TAG, " WVS Displaying permission rationale to provide additional mContext.");
            Snackbar.make(
                    findViewById(R.id.activity_main),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE_LOCATION);
                        }
                    })
                    .show();
        } else {
//            appLog(TAG, " WVS Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE_LOCATION);
        }
    }

    private void requestPermission_WriteExternalStorage() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Inform and request")
                    .setMessage("External FILE permision is needed for core functionality")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_PERMISSIONS_REQUEST_CODE_EXTFILES);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_REQUEST_CODE_EXTFILES);
        }
    }

    public static void requestPermission_Contacts(Context pContext, Activity pActivity) {
        // Should we show an explanation?
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
//            new AlertDialog.Builder(this)
//                    .setTitle("Inform and request")
//                    .setMessage("Contacts permision is needed for core functionality")
//                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            ActivityCompat.requestPermissions_Location(MainActivity.this,
//                                    new String[]{Manifest.permission.READ_CONTACTS},
//                                    REQUEST_PERMISSIONS_REQUEST_CODE_CONTACTS);
//                        }
//                    })
//                    .show();
//        } else
//            {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_PERMISSIONS_REQUEST_CODE_CONTACTS);
//        }

        if (ActivityCompat.checkSelfPermission(pContext, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(pActivity, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_PERMISSIONS_REQUEST_CODE_CONTACTS);
        }
    }

    public static void requestPermission_SendSMS(Context pContext, Activity pActivity) {

        if (ActivityCompat.checkSelfPermission(pContext, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(pActivity, new String[]{Manifest.permission.SEND_SMS}, REQUEST_PERMISSIONS_REQUEST_CODE_SMSTEXT);
        }
    }
    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
//        appLog(TAG, " WVS onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE_LOCATION) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
//                appLog(TAG, " WVS User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                mService.requestLocationUpdates();
                mRecognition.StartTracking(this);
            } else {
                // Permission denied.
//                setButtonsState(false);
                setMenuState(false, appMenuChoices[0]);

                Snackbar.make(
                        findViewById(R.id.activity_main),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Update the buttons state depending on whether location updates are being requested.
        if (s.equals(Utils.KEY_REQUESTING_LOCATION_UPDATES)) {
//            setButtonsState(sharedPreferences.getBoolean(Utils.KEY_REQUESTING_LOCATION_UPDATES,false));
            setMenuState(true, appMenuChoices[0]);
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    public boolean BackFromNotification_delEvent_gotoNote(Intent intent1){
        boolean bBackFromNotification;

//      test if coming from notification pending intent.... "id"
        if (intent1 != null && intent1.hasExtra("notificationtag")){
            bBackFromNotification = true;
            appLog(TAG, " WVS MainActivity BackFromNotification_delEvent_gotoNote()... intent contains NOTIFICATION tag = " + intent1.getExtras().getString("notificationtag"));
            String sNotificationTag = intent1.getExtras().getString("notificationtag");
//              wvs todo should delete event (possibly put this in DoWork?) and navigate to noteitem
//              notification tag NoteId + " " + EventId + " " + EventDate
//            int posFirstSpace = sNotificationTag.indexOf(" ");
//            int posSecondSpace = sNotificationTag.indexOf(" ", sNotificationTag.indexOf(" ") + 1);
//            int iNoteId = Integer.parseInt(sNotificationTag.substring(0,posFirstSpace));
//            int iEventId = Integer.parseInt(sNotificationTag.substring(posFirstSpace, posSecondSpace).trim());
            int iNoteId = getNoteId_NotificationTag(sNotificationTag);
            int iEventId = getEventId_NotificationTag(sNotificationTag);

            EventObjectRepo eor = new EventObjectRepo(mContext);
            int iRet = eor.deleteORupdateEventbyNoteId(iNoteId);
            appLog(TAG, " WVS onCreate() back from notification, deleted eventid (" + iEventId + "), row cnt=" + iRet);
            NoteRepo nr = new NoteRepo(mContext);
            Note tmpNote = nr.getNoteById(iNoteId);
            if (tmpNote != null) {
                Intent intent2 = new Intent(mContext, NoteAddActivity.class);
                intent2.putExtra("Note", tmpNote);
                intent2.putExtra("requestCode", EDIT_NOTE_ITEM_REQUEST);
                startActivityForResult(intent2, EDIT_NOTE_ITEM_REQUEST);
            } else {
//                    tell user note does not exist...
            }
        } else {
            appLog(TAG, " WVS MainActivity BackFromNotification_delEvent_gotoNote()... NOT FROM Notification" );
            bBackFromNotification = false;
        }
        return bBackFromNotification;
    }

    public void ImportNote(){
        Intent mediaIntent = new Intent(Intent.ACTION_GET_CONTENT);
//getExternalStoragePublicDirectory??
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "/bluetooth/");
        mediaIntent.setDataAndType(uri, "text/plain");
//            Type 	Description 	Example of typical subtypes
//            text 	Represents any document that contains text and is theoretically human readable 	text/plain, text/html, text/css, text/javascript
//            image 	Represents any kind of images. Videos are not included, though animated images (like animated gif) are described with an image type.
//                      image/gif, image/png, image/jpeg, image/bmp, image/webp, image/x-icon, image/vnd.microsoft.icon
//            audio 	Represents any kind of audio files 	audio/midi, audio/mpeg, audio/webm, audio/ogg, audio/wav
//            video 	Represents any kind of video files 	video/webm, video/ogg
//            application 	Represents any kind of binary data.
//                      application/javascript, application/octet-stream, application/pkcs12, application/vnd.mspowerpoint, application/xhtml+xml, application/xml, application/pdf

        mediaIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//            mediaIntent.setType("text/plain"); //set mime type as per requirement
        startActivityForResult(mediaIntent, IMPORT_NOTE);

    }

    public void CreateVocabList(String SSV){

        String[] words, lines;
        words = SSV.split(" ");
        NotesRepo nsr;
        NoteRepo nr;
        Notes tmpNotes;
        Note tmpNote;
        Long lNotesId;
        Long lNoteId;
        int linecount=0;
        String strNoteItem="";
        String strTag="";
//        https://stackoverflow.com/questions/4436923/xml-string-parsing-in-android
//        https://stackoverflow.com/questions/8408504/how-to-parse-a-string-containing-xml-in-java-and-retrieve-the-value-of-the-root
        if (words.length > 0) {

            String listname = new SimpleDateFormat("yyyy-MM-dd hh-mm'_vocab'").format(new Date());
            tmpNotes = new Notes(0,listname,"", DBInfo.TABLE_TBLNOTES_KEY_Date,0);  //Notes.KEY_Date
            nsr = new NotesRepo(this);
            lNotesId = nsr.insert(tmpNotes);
//           tmpNotes.setNotesId((int) lNotesId);
            nr = new NoteRepo(this);
            if (SSV.contains(System.getProperty("line.separator"))){
                linecount = SSV.split(System.getProperty("line.separator")).length;
            }
            if (linecount == 0){
                for (String word : words) {
                    tmpNote = new Note(0, word, "", "", lNotesId.intValue(), 0, "", "", null);
                    lNoteId = nr.insert(tmpNote);
                }
            } else {
                lines = SSV.split(System.getProperty("line.separator"));
                for (String line : lines){
                    if (line.split(";").length > 1){
                        strNoteItem = line.split(";")[0].toString();
                        strTag = line.split(";")[1].toString().trim();
                    } else {
                        strNoteItem = line;
                        strTag = "";
                    }
                    if (! line.trim().equals("")) {
                        tmpNote = new Note(0, strNoteItem, "", strTag, lNotesId.intValue(), 0, "", "", null);
                        lNoteId = nr.insert(tmpNote);
                    }
                }
            }
            if (frag != null) {
                getSupportFragmentManager().beginTransaction().detach(frag).attach(frag).commit();
            }
        }
    }

    public void ExportRoutestoAWSS3(){

        ArrayList <String> checkedRoutes = new ArrayList<>();
        LocationsRepo lsr = new LocationsRepo(mContext);
        checkedRoutes = lsr.getCheckedRoutes();
        if (checkedRoutes != null & checkedRoutes.size() > 0) {
//            lsr.createScript_RoutestoAWSS3_toMySQL(checkedRoutes, mContext);
            lsr.createScript_RoutestoAWSS3_toSQLite3(checkedRoutes,mContext);
        }
    }

    public void tmpTableMod(){

        //Open connection to write data
        DBHelper dbHelper;
        dbHelper = new DBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();


//        String selectQuery =  "ALTER TABLE " + DBInfo.TABLE_LOCATIONS + " ADD COLUMN " + DBInfo.TABLE_LOCATIONS_KEY_LoginId + " INTEGER";
//        String selectQuery =  "ALTER TABLE " + DBInfo.TABLE_LOCATIONS + " ADD COLUMN " + DBInfo.TABLE_LOCATIONS_KEY_TotalDistance + " INTEGER";
//        String selectQuery =  "ALTER TABLE " + DBInfo.TABLE_LOCATIONS + " ADD COLUMN " + DBInfo.TABLE_LOCATIONS_KEY_TotalSteps + " INTEGER";
//        String selectQuery =  "ALTER TABLE " + DBInfo.TABLE_TBLNOTE + " ADD COLUMN " + DBInfo.TABLE_TBLNOTE_KEY_Image + " TEXT";
//        String selectQuery =  "ALTER TABLE " + DBInfo.TABLE_LOCATION + " ADD COLUMN " + DBInfo.TABLE_LOCATION_KEY_Altitude + " INTEGER";
//        String selectQuery =  "ALTER TABLE " + DBInfo.TABLE_TBLNOTE + " ADD COLUMN " + DBInfo.TABLE_TBLNOTE_KEY_Cost + " INTEGER";
//        String selectQuery =  "ALTER TABLE " + TABLE_LOCATIONS + " ADD COLUMN " + TABLE_LOCATIONS_KEY_Checked + " INTEGER";
//        String selectQuery =  "ALTER TABLE " + TABLE_TBLNOTE + " ADD COLUMN " + TABLE_LOCATIONS_KEY_LocationsId + " INTEGER";
//        String selectQuery =  "ALTER TABLE " + TABLE_TBLGEOLOCATION + " ADD COLUMN " + TABLE_TBLGEOLOCATION_KEY_Enabled + " INTEGER";
//        String selectQuery =  "ALTER TABLE " + TABLE_TBLGEOLOCATION + " ADD COLUMN " + TABLE_TBLGEOLOCATION_KEY_Within + " INTEGER";
//        String selectQuery =  "ALTER TABLE " + DBInfo.TABLE_LOCATIONS + " ADD COLUMN " + DBInfo.TABLE_LOCATIONS_KEY_Move_Time + " INTEGER";
//        db.execSQL(selectQuery);
//        selectQuery =  "ALTER TABLE " + DBInfo.TABLE_LOCATIONS + " ADD COLUMN " + DBInfo.TABLE_LOCATIONS_KEY_Heart_Intensity + " INTEGER";
//        db.execSQL(selectQuery);
//        selectQuery =  "ALTER TABLE " + DBInfo.TABLE_LOCATIONS + " ADD COLUMN " + DBInfo.TABLE_LOCATIONS_KEY_Heart_Time + " INTEGER";
//        db.execSQL(selectQuery);

//        String selectQuery =  "ALTER TABLE " + DBInfo.TABLE_TBLEVENTS + " ADD COLUMN " + DBInfo.TABLE_TBLEVENTS_KEY_EventRepeat + " TEXT";
//        db.execSQL(selectQuery);

//        String selectQuery =  "ALTER TABLE " + DBInfo.TABLE_TBLEVENTS + " ADD COLUMN " + DBInfo.TABLE_TBLEVENTS_KEY_EventMessageTag + " TEXT";
//        db.execSQL(selectQuery);

//        String selectQuery =  "ALTER TABLE " + DBInfo.TABLE_LOCATIONS + " ADD COLUMN " + DBInfo.TABLE_LOCATIONS_KEY_ElevationDifference + " INTEGER";
//        db.execSQL(selectQuery);

//        String selectQuery = "DELETE FROM " + TABLE_TBLGEOLOCATION;
//        db.execSQL(selectQuery);

//        -- select * from tblLocation where locationsid = 92; -- count 209
//        insert into tblLocation (Latitude, Longitude, Altitude, ColorPath, DateTime, LocationsId)
//                --values (37.739003253, -122.44358651,95.0498657226563,-16777216,'2020-02-22 15:31:36',92);
//        values (37.739003253, -122.44358651,95.0498657226563,-16777216,'2020-02-22 15:31:44',92);

//        SQLite does not allow rename column, must drop/recreate
//        String CREATE_TABLE = "";
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TBLLOGIN);
//        CREATE_TABLE = "CREATE TABLE " + TABLE_TBLLOGIN + "("
//                + DBInfo.TABLE_TBLLOGIN_KEY_ID  + " INTEGER,"
//                + DBInfo.TABLE_TBLLOGIN_KEY_Name  + " TEXT,"
//                + DBInfo.TABLE_TBLLOGIN_KEY_Email + " TEXT )";
//        db.execSQL(CREATE_TABLE);

//        String CREATE_TABLE = "CREATE TABLE " + DBInfo.TABLE_TBLHEALTH  + "("
//                + DBInfo.TABLE_TBLHEALTH_KEY_HealthId  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
//                + DBInfo.TABLE_TBLHEALTH_KEY_Type + " TEXT, "
//                + DBInfo.TABLE_TBLHEALTH_KEY_SourceName + " TEXT, "
//                + DBInfo.TABLE_TBLHEALTH_KEY_SourceVersion + " TEXT, "
//                + DBInfo.TABLE_TBLHEALTH_KEY_Device + " TEXT, "
//                + DBInfo.TABLE_TBLHEALTH_KEY_Unit + " TEXT, "
//                + DBInfo.TABLE_TBLHEALTH_KEY_CreationDate + " TEXT, "
//                + DBInfo.TABLE_TBLHEALTH_KEY_StartDate + " TEXT, "
//                + DBInfo.TABLE_TBLHEALTH_KEY_EndDate + " TEXT, "
//                + DBInfo.TABLE_TBLHEALTH_KEY_Value + " TEXT, "
//                + DBInfo.TABLE_TBLHEALTH_KEY_LoginId + " INTEGER)";
//        db.execSQL(CREATE_TABLE);

//      wvs 7/20/20
//        String  CREATE_TABLE = "CREATE TABLE " + DBInfo.TABLE_TBLSCRIPTS + "("
//                + DBInfo.TABLE_TBLSCRIPTS_KEY_ScriptsId  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
//                + DBInfo.TABLE_TBLSCRIPTS_KEY_ScriptName + " TEXT, "
//                + DBInfo.TABLE_TBLSCRIPTS_KEY_Bucket + " TEXT, "
//                + DBInfo.TABLE_TBLSCRIPTS_KEY_BucketPrefix + " TEXT, "
//                + DBInfo.TABLE_TBLSCRIPTS_KEY_DownloadDate + " TEXT, "
//                + DBInfo.TABLE_TBLSCRIPTS_KEY_ExecutionDate + " TEXT, "
//                + DBInfo.TABLE_TBLSCRIPTS_KEY_RowsAffected + " TEXT )";
//        db.execSQL(CREATE_TABLE);

//      wvs 8/4/20
//        String  CREATE_TABLE = "CREATE TABLE " + DBInfo.TABLE_TBLGEOLOCATION + "("
//                + DBInfo.TABLE_TBLGEOLOCATION_KEY_Id  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
//                + DBInfo.TABLE_TBLGEOLOCATION_KEY_Name + " TEXT, "
//                + DBInfo.TABLE_TBLGEOLOCATION_KEY_Latitude + " INTEGER, "
//                + DBInfo.TABLE_TBLGEOLOCATION_KEY_Longitude + " INTEGER, "
//                + DBInfo.TABLE_TBLGEOLOCATION_KEY_Altitude + " INTEGER, "
//                + DBInfo.TABLE_TBLGEOLOCATION_KEY_Enabled  + " INTEGER, "
//                + DBInfo.TABLE_TBLGEOLOCATION_KEY_Within  + " INTEGER )";
//        db.execSQL(CREATE_TABLE);

//      wvs 8/4/20
//        String  CREATE_TABLE = "CREATE TABLE " + DBInfo.TABLE_ZTBLGEOLOCATION + "("
//                + DBInfo.TABLE_ZTBLGEOLOCATION_KEY_Id  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
//                + DBInfo.TABLE_ZTBLGEOLOCATION_KEY_GeoLocationId + " INTEGER, "
//                + DBInfo.TABLE_ZTBLGEOLOCATION_KEY_LocationId + " INTEGER )";
//        db.execSQL(CREATE_TABLE);

//        String selectQuery =  "DELETE FROM " + TABLE_TBLSCRIPTS;
//        db.execSQL(selectQuery);

//        String CREATE_TABLE = "CREATE TABLE " + TABLE_TBLLOGIN + "("
//                + DBInfo.TABLE_TBLLOGIN_KEY_ID  + " INTEGER,"
//                + DBInfo.TABLE_TBLLOGIN_KEY_Name + " TEXT )";
//        db.execSQL(CREATE_TABLE);
//
//        String INSERT_TABLE;
//        for (int i = 0; i < MainActivity.applogins.length; i++) {
//            INSERT_TABLE = "INSERT INTO " + TABLE_TBLLOGIN + "("
//                    + DBInfo.TABLE_TBLLOGIN_KEY_Name + ") values ("
//                    +  "'" + MainActivity.applogins[i] + "')";
//            db.execSQL(INSERT_TABLE);
//        }
//
//        CREATE_TABLE = "CREATE TABLE " + TABLE_TBLPET + "("
//                + DBInfo.TABLE_TBLPET_KEY_ID  + " INTEGER,"
//                + DBInfo.TABLE_TBLPET_KEY_Name + " TEXT )";
//        db.execSQL(CREATE_TABLE);
//
//        for (int i = 0; i < MainActivity.appPets.length; i++) {
//            INSERT_TABLE = "INSERT INTO " + TABLE_TBLPET + "("
//                    + DBInfo.TABLE_TBLPET_KEY_Name + ") values ("
//                    +  "'" + MainActivity.appPets[i] + "')";
//            db.execSQL(INSERT_TABLE);
//        }

        db.close();

    }

    private void setupFitness() {
        FitnessOptions fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
//                        .addDataType(DataType.TYPE_LOCATION_SAMPLE)  // latLng data
                        .addDataType(DataType.TYPE_DISTANCE_CUMULATIVE)
                        .addDataType(DataType.TYPE_DISTANCE_DELTA)
                        .addDataType(DataType.AGGREGATE_HEART_POINTS,  FitnessOptions.ACCESS_READ)
                        .addDataType(DataType.TYPE_HEART_POINTS,  FitnessOptions.ACCESS_READ)
                        .addDataType(DataType.AGGREGATE_MOVE_MINUTES)
                        .addDataType(DataType.TYPE_MOVE_MINUTES)
                        .build();
////        FORCE THIS...
//        GoogleSignIn.requestPermissions_Location(
//                this,
//                REQUEST_OAUTH_REQUEST_CODE,
//                GoogleSignIn.getLastSignedInAccount(this),
//                fitnessOptions);


        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            subscribe_toSteps();
            subscribe_toDistance();
            subscribe_toHeart();
            subscribe_toMoveMinutes();
            subscribe_toLocations();
        }
    }

    /** Records step data by requesting a subscription to background step data. */
    public void subscribe_toSteps() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
//      to unsubscribe...   Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(mContext)).unsubscribe
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(mContext))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    appLog(TAG, " WVS Successfully subscribed to STEPS!");
                                } else {
                                    Log.w(TAG, " WVS There was a problem subscribing to STEPS.", task.getException());
                                }
                            }
                        });
    }

    public void subscribe_toDistance() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
//      to unsubscribe...   Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(mContext)).unsubscribe
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(mContext))
//                .subscribe(DataType.TYPE_DISTANCE_CUMULATIVE)  // TYPE_DISTANCE_DELTA, TYPE_DISTANCE_CUMULATIVE
                .subscribe(DataType.TYPE_DISTANCE_DELTA)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    appLog(TAG, " WVS Successfully subscribed to TYPE_DISTANCE_DELTA!");
                                } else {
                                    Log.w(TAG, " WVS There was a problem subscribing to TYPE_DISTANCE_DELTA.", task.getException());
                                }
                            }
                        });
    }

    public void subscribe_toLocations() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
//      to unsubscribe...   Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(mContext)).unsubscribe
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(mContext))
                .subscribe(DataType.TYPE_LOCATION_SAMPLE)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    appLog(TAG, " WVS Successfully subscribed to LOCATION SAMPLES!");
                                } else {
                                    Log.w(TAG, " WVS There was a problem subscribing to LOCATION SAMPLES.", task.getException());
                                }
                            }
                        });
    }
    public void subscribe_toHeart() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
//      to unsubscribe...   Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(mContext)).unsubscribe

//        2019-06-28 10:13:46.565 4259-4259/com.endeavor.walter.getout9 W/MainActivity: WVS There was a problem subscribing to HEART POINTS.
//    com.google.android.gms.common.api.ResolvableApiException: 5000: Application needs OAuth consent from the user

        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(mContext))
                .subscribe(DataType.TYPE_HEART_POINTS) // TYPE_HEART_POINTS, AGGREGATE_HEART_POINTS failed
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    appLog(TAG, " WVS Successfully subscribed to HEART POINTS!");
                                } else {
                                    Log.w(TAG, " WVS There was a problem subscribing to HEART POINTS.", task.getException());
                                }
                            }
                        });
    }

    public void subscribe_toMoveMinutes() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
//      to unsubscribe...   Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(mContext)).unsubscribe
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(mContext))
                .subscribe(DataType.TYPE_MOVE_MINUTES) //AGGREGATE_MOVE_MINUTES
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    appLog(TAG, " WVS Successfully subscribed to AGGREGATE_MOVE_MINUTES!");
                                } else {
                                    Log.w(TAG, " WVS There was a problem subscribing to AGGREGATE_MOVE_MINUTES.", task.getException());
                                }
                            }
                        });
    }
    public void DataForPeriod_update_tblLocations(final String[] start_end_time, Long pLocationsId, boolean btblHealth, boolean btblLocations) {
        //e.g. 2019-05-23 18:30:03
        String sFitnessDataType1, sFitnessDataType2, sFitnessDataType3, sFitnessDataType4, sFitnessDataType5;
        if (start_end_time[0].equals(start_end_time[1])){
            Log.i(TAG, " WVS read_update_DataForPeriod() EQUAL start and end times... exit");
            return;
        }
        Calendar cal = Calendar.getInstance();
        long startTime, endTime;

        Date now = new Date();
//        cal.setTime(now);
        mltotalSteps = 0l;
        mftotalDistance = 0f;
        mfHeartIntensity = 0f;
        miHeartDuration = 0;
        mlMoveMinutes = 0l;
        mHealthArray.clear();

        endTime = WVS_Utils.convertDBdateStringToMilliSeconds(start_end_time[1]);
        Log.i(TAG, " WVS read_update_DataForPeriod() read_update_DataForPeriod() endTime  ="+ endTime + " time=" + start_end_time[1]);
        startTime = WVS_Utils.convertDBdateStringToMilliSeconds(start_end_time[0]);
        Log.i(TAG, " WVS read_update_DataForPeriod() read_update_DataForPeriod() startTime=" + startTime  + " time=" + start_end_time[0]);

        LocationsRepo lsr = new LocationsRepo(mContext);
//      1. getHistoryClient_Steps_Distancd 2. getHistoryClient_Distance_Heart 3. getHistoryClient_Heart_Move 4. getHistoryClient_Move_Final
        getHistoryClient_Steps_Distance(start_end_time,startTime,endTime,btblHealth,btblLocations,pLocationsId);


        if (false) {
            sFitnessDataType5 = "TYPE_LOCATION_SAMPLE";
            DataReadRequest readRequest5 = new DataReadRequest.Builder()
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .read(DataType.TYPE_LOCATION_SAMPLE)
                    .build();
            Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .readData(readRequest5)
                    .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                        @Override
                        public void onSuccess(DataReadResponse dataReadResponse) {

                            Log.i(TAG, " WVS dataReadResponse.getBuckets().size()" + dataReadResponse.getBuckets().size());
                            Log.i(TAG, " WVS dataReadResponse.getDataSets().size()" + dataReadResponse.getDataSets().size());
                            parseintoDataSets_mHealthArray(dataReadResponse, sFitnessDataType5);
//                          todo map data...latLng
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, " WVS There was a problem getting the Window LOCATION_SAMPLE data.", e);
                        }
                    });
        }
    }

    public void DataForPeriod_update_tblHealth(final String[] start_end_time) {
//  public void DataForPeriod_update_tblHealth(final String[] start_end_time, final String sDataType) {
        String sFitnessDataType1, sFitnessDataType2, sFitnessDataType3, sFitnessDataType4, sFitnessDataType5;
        //e.g. 2019-05-23 18:30:03
        if (start_end_time[0].equals(start_end_time[1])){
            Log.i(TAG, " WVS DataForPeriod_update_tblHealth EQUAL start and end times... exit");
            return;
        }
        Calendar cal = Calendar.getInstance();
        long startTime, endTime;
        Date now = new Date();
        mltotalSteps = 0l;
        mftotalDistance = 0f;
        mfHeartIntensity = 0f;
        miHeartDuration = 0;
        mlMoveMinutes = 0l;
        mHealthArray.clear();

//      CHANGE dates to begin and end of day
        String startDay = convertDBdateStringToString_startofDay(start_end_time[0]);
        String endDay = convertDBdateStringToString_endofDay(start_end_time[1]);

        endTime = WVS_Utils.convertDBdateStringToMilliSeconds(endDay);
        startTime = WVS_Utils.convertDBdateStringToMilliSeconds(startDay);

//      1. getHistoryClient_Steps_Distancd 2. getHistoryClient_Distance_Heart 3. getHistoryClient_Heart_Move 4. getHistoryClient_Move_Final
        boolean btblHealth = true;
        boolean btblLocations = false;
        getHistoryClient_Steps_Distance(start_end_time,startTime,endTime,btblHealth,btblLocations, 0l);


        if (false) {
            sFitnessDataType5 = "TYPE_LOCATION_SAMPLE";
            Log.i(TAG, " WVS dailyHealth_DataForPeriod for " + sFitnessDataType5);
            DataReadRequest readRequest5 = new DataReadRequest.Builder()
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .read(DataType.TYPE_LOCATION_SAMPLE)
                    .build();
            Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .readData(readRequest5)
                    .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                        @Override
                        public void onSuccess(DataReadResponse dataReadResponse) {

                            Log.i(TAG, " WVS dataReadResponse.getBuckets().size()" + dataReadResponse.getBuckets().size());
                            Log.i(TAG, " WVS dataReadResponse.getDataSets().size()" + dataReadResponse.getDataSets().size());
                            parseintoDataSets_mHealthArray(dataReadResponse, sFitnessDataType5);
//                          todo map data...
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, " WVS There was a problem getting the Window LOCATION_SAMPLE data.", e);
                        }
                    });
        }
    }

    public void getHistoryClient_Steps_Distance(String[] start_end_time, long startTime, long endTime,boolean btblHealth, boolean btblLocations, long pLocationsId){

        String sFitnessDataType1 = type_STEP_COUNT_CUMULATIVE;
        Log.i(TAG, " WVS getHistoryClient_next for " + sFitnessDataType1);
        DataReadRequest readRequest1 = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA,
                        DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)  //.bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest1)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        parseintoDataSets_mHealthArray(dataReadResponse, sFitnessDataType1);
                        Toast.makeText(mContext,"TYPE_STEP_COUNT_CUMULATIVE " + mltotalSteps,Toast.LENGTH_LONG ).show();
                        if (mltotalSteps > 0l ) {
                            if (btblHealth) Add_mHealthArrayTotblHealth();
                            if (btblLocations){
                                LocationsRepo lsr = new LocationsRepo(mContext);
                                lsr.updateTotalStepsLocationsId(pLocationsId, mltotalSteps);
                            }
                        }
                        getHistoryClient_Distance_Heart(start_end_time,startTime,endTime,btblHealth,btblLocations,pLocationsId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, " WVS There was a problem getting TYPE_STEP_COUNT_CUMULATIVE step count.", e);
                    }
                })
        ;

    }

    public void getHistoryClient_Distance_Heart(String[] start_end_time, long startTime, long endTime,boolean btblHealth, boolean btblLocations, long pLocationsId){

        String sFitnessDataType2 = type_DISTANCE_DELTA;
        Log.i(TAG, " WVS getHistoryClient_Distance_Move for " + sFitnessDataType2);
        DataReadRequest readRequest2 = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_DISTANCE_DELTA,
                        DataType.AGGREGATE_DISTANCE_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)  //.bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest2)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        parseintoDataSets_mHealthArray(dataReadResponse, sFitnessDataType2);
                        Toast.makeText(mContext,"TYPE_DISTANCE_DELTA " + mftotalDistance,Toast.LENGTH_LONG ).show();
                        if (mftotalDistance > 0l ) {
                            if (btblHealth) Add_mHealthArrayTotblHealth();
                            if (btblLocations){
                                LocationsRepo lsr = new LocationsRepo(mContext);
                                lsr.updateTotalDistanceLocationsId(pLocationsId, mftotalDistance);
                            }
                        }
                        getHistoryClient_Heart_Move(start_end_time,startTime,endTime,btblHealth,btblLocations,pLocationsId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, " WVS There was a problem getting the TYPE_DISTANCE_DELTA.", e);
                    }
                })
        ;

    }

    public void getHistoryClient_Heart_Move(String[] start_end_time, long startTime, long endTime,boolean btblHealth, boolean btblLocations, long pLocationsId){

        String sFitnessDataType3 = type_HEART_POINTS;
        Log.i(TAG, " WVS dailyHealth_DataForPeriod for " + sFitnessDataType3);
        DataReadRequest readRequest3 = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_HEART_POINTS,
                        DataType.AGGREGATE_HEART_POINTS)
                .bucketByTime(1, TimeUnit.DAYS)  //.bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest3)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        parseintoDataSets_mHealthArray(dataReadResponse, sFitnessDataType3);
                        Toast.makeText(mContext,"TYPE_HEART_POINTS duration = " + miHeartDuration +", intensity = " + mfHeartIntensity,Toast.LENGTH_LONG ).show();
                        if (miHeartDuration > 0 || mfHeartIntensity > 0f){
                            if (btblHealth) Add_mHealthArrayTotblHealth();
                            if (btblLocations){
                                LocationsRepo lsr = new LocationsRepo(mContext);
                                lsr.updateHeartInfoLocationsId(pLocationsId, mfHeartIntensity, miHeartDuration );
                            }
                        }
                        getHistoryClient_Move_Final(start_end_time,startTime,endTime,btblHealth,btblLocations,pLocationsId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, " WVS There was a problem getting the TYPE_HEART_POINTS.", e);
                    }
                })
        ;

    }

    public void getHistoryClient_Move_Final(String[] start_end_time, long startTime, long endTime,boolean btblHealth, boolean btblLocations, long pLocationsId){

        String sFitnessDataType4 = type_MOVE_MINUTES;
        Log.i(TAG, " WVS dailyHealth_DataForPeriod for " + sFitnessDataType4);
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_MOVE_MINUTES,
                        DataType.AGGREGATE_MOVE_MINUTES)
                .bucketByTime(1, TimeUnit.DAYS)  //.bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        parseintoDataSets_mHealthArray(dataReadResponse, sFitnessDataType4);
                        Toast.makeText(mContext,"TYPE_MOVE_MINUTES " + mlMoveMinutes,Toast.LENGTH_LONG ).show();
                        if (mlMoveMinutes > 0l){
                            if (btblHealth) Add_mHealthArrayTotblHealth();
                            if (btblLocations){
                                LocationsRepo lsr = new LocationsRepo(mContext);
                                lsr.updateMoveInfoLocationsId(pLocationsId, mlMoveMinutes);
                            }
                        }
                        if (btblLocations){
//                          assumption is just finished recording route so now get health from the previous week
                            String[] startendtimes = {"",""};
                            // BEGIN TIME is one week ago
                            startendtimes[0] = new SimpleDateFormat("yyyy-MM-dd 23:59:59").format(new Date(System.currentTimeMillis()-24*7*60*60*1000));
                            //  END TIME is yesterday
                            startendtimes[1] = new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(new Date(System.currentTimeMillis()-24*60*60*1000));
                            DataForPeriod_update_tblHealth(startendtimes);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, " WVS There was a problem getting the TYPE_MOVE_MINUTES.", e);
                    }
                })
        ;
    }
    public void parseintoDataSets_mHealthArray(DataReadResponse dataReadResult, String sDataType) {
        //  previously printData()
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, " WVS parseintoDataSets_mHealthArray() Number of returned BUCKETS is: " + dataReadResult.getBuckets().size());
            int b = 0;
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                Log.i(TAG, " WVS parseintoDataSets_mHealthArray() BUCKET #: " + b);
                b=b+1;
                int i = 0;
                for (DataSet dataSet : dataSets) {
                    Log.i(TAG, " WVS parseintoDataSets_mHealthArray() Number DataSets #: " + i);
                    dumpDataSet_mHealthArray(dataSet, sDataType);
                    i=i+1;
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, " WVS parseintoDataSets_mHealthArray() Number of returned DATASETS is: " + dataReadResult.getDataSets().size());
            int i = 0;
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                Log.i(TAG, " WVS parseintoDataSets_mHealthArray() Number DataSets #: " + i);
                dumpDataSet_mHealthArray(dataSet, sDataType);
                i=i+1;
            }
        }
        // [END parse_read_data_result]
    }

    // [START parse_dataset]
    private void dumpDataSet_mHealthArray(DataSet dataSet, String datatype) {
        Log.i(TAG, " WVS dumpDataSet_mHealthArray() Data returned for Data type: " + dataSet.getDataType().getName() + ", size = " + dataSet.getDataPoints().size());
        DateFormat timeFormat = getTimeInstance();
        DateFormat dateFormat1 = getDateInstance(SHORT);  //"yyyy-MM-dd HH-mm-ss"
        Float totalLength;
//        LocationsRepo lsr = new LocationsRepo(mContext);

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i(TAG, " WVS getDataPoints() Data point:");
            Log.i(TAG, " WVS \tType: " + dp.getDataType().getName());
            Log.i(TAG, " WVS \tStart: " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) );
            Log.i(TAG, " WVS \tStart: " + dateFormat1.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, " WVS \tEnd: " + timeFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));

            Date startDate = new Date(dp.getStartTime(TimeUnit.MILLISECONDS));
            String startDateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startDate);
            String creationDateString =  new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(startDate);;
            Date endDate = new Date(dp.getEndTime(TimeUnit.MILLISECONDS));
            String endDateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endDate);

            Health health = null;
            int iLoginId = 0;
            String sValue = "";
            for (Field field : dp.getDataType().getFields()) {
                Log.i(TAG, " WVS \tField: " + field.getName() + " RAW Value: " + dp.getValue(field));
                sValue = "";
//            read_update_DataForPeriod(startendtimes, "TYPE_STEP_COUNT_CUMULATIVE");
//            read_update_DataForPeriod(startendtimes, "TYPE_LOCATION_SAMPLE");
//            read_update_DataForPeriod(startendtimes, "TYPE_HEART_POINTS");  //AGGREGATE_HEART_POINTS
//            read_update_DataForPeriod(startendtimes, "TYPE_MOVE_MINUTES"); //AGGREGATE_MOVE_MINUTES

                switch (datatype){
                    case "TYPE_STEP_COUNT_CUMULATIVE":
                        mltotalSteps = mltotalSteps + dp.getValue(field).asInt();  //used for tblLocations insert
                        sValue = String.valueOf(dp.getValue(field).asInt());
                        break;
                    case "TYPE_LOCATION_SAMPLE":
                        //                  LatLng location samples
//                        totalLength = dp.getValue(field).asFloat()/1609.344f;
//                        mdtotalDistance = totalLength.doubleValue();
                        break;
                    case "TYPE_DISTANCE_DELTA":
                        // unit="distance"     1 Mile = 1609.344 Meters
                        totalLength = dp.getValue(field).asFloat()/1609.344f;
                        mftotalDistance = mftotalDistance + totalLength;
                        sValue = totalLength.toString();
                        Log.i(TAG, " WVS \tField: " + field.getName() + " CONVERTED miles Value: " + totalLength.toString());
                        break;
                    case "TYPE_HEART_POINTS":
                        if (field.getName().equals("intensity")){
                            mfHeartIntensity = mfHeartIntensity + dp.getValue(field).asFloat();
                            sValue = String.valueOf(dp.getValue(field).asFloat());
                        }
                        if (field.getName().equals("duration")) {
                            miHeartDuration = miHeartDuration + dp.getValue(field).asInt();
                            sValue = String.valueOf(dp.getValue(field).asInt());
                        }
                        break;
                    case "TYPE_MOVE_MINUTES":
                        mlMoveMinutes = mlMoveMinutes + dp.getValue(field).asInt();
                        sValue = String.valueOf(dp.getValue(field).asInt());
                        break;
                }

                if (mHealthArray != null ){
                    iLoginId = Utils.getCurrentLoginID(mContext);
                    health = new Health(null, datatype, "Pixel3a", null,"Pixel3a",field.getName().toString() ,creationDateString , startDateString, endDateString, sValue, iLoginId);
                    mHealthArray.add(health);
                } else {
                    Log.i(TAG, " WVS ERROR dumpDataSet " + datatype + ", mHealthArray IS NULL");
                }
            }
        }
    }
// [END parse_dataset]

private boolean Add_mHealthArrayTotblHealth() {
    boolean bret = false;
//      add to tblHealth
    HealthRepo hr = new HealthRepo(mContext);
//      hr.DeleteAll();
    if(mHealthArray != null && mHealthArray.size() > 0) {
        Health tmpHealth = null;
        Health tmpHealthRecordExists = null;
        for (Integer i = 0; i < mHealthArray.size(); i++) {
            tmpHealth = mHealthArray.get(i);
            tmpHealthRecordExists = hr.getSingle_HealthRecord(tmpHealth);
            if (tmpHealthRecordExists != null) {
//                 don't do anything, maybe check if counts are equal?
            } else {
                bret = true;
                hr.insert(tmpHealth);
            }
        }
//                                  create insert scripts
//                                    hr.ExportHealthtoAWSS3(start_end_time, sDataType, mContext);
    }
//                            Intent intent1 = new Intent(mContext, ScratchPad.class);
//                            intent1.putExtra("scratchpad", "TYPE_MOVE_MINUTES " + mlMoveMinutes);
//                            startActivity(intent1);

    return bret;
}

    public static final int[] COLOR_IDS = {
//COLOR_IDS[0] base  default is -16777216
            Color.MAGENTA,//0 IN_VEHICLE -65281
            Color.BLUE ,//1 ON_BICYCLE   -16776961
            Color.GREEN,//2 ON_FOOT      -16711936
            Color.RED,//3 STILL          -65536
            Color.GRAY,//4 N/A           -7829368
            Color.GRAY,//5 N/A
            Color.GRAY,//6 N/A
            Color.YELLOW,//7 WALKING       -256
            Color.BLUE//8 RUNNING         -16776971
    };

public static final String[] applogins = {
        "none", //0 default
        "walter",
        "lucas",
        "devon",
        "deborah"
    };

public static final String[] appPets = {
        "none", //0 default
        "Willo",
        "Loui",
        "Emma",
        "Shadow",
        "Bubbles",
        "Spots"
    };

public static final String [] appRouteChoices =
        {"Dog Walk", "Walk", "Run", "Going Out", "To Gym", "Pickup"};

public static final String [] appMenuChoices =
        {"NoChanges", //0 default
        "AddNotes","AddNotesNot"};

public static String[] appEventType =
        {EVENTTYPE_NOTIFICATION, EVENTTYPE_TEXTMSG, EVENTTYPE_EMAIL, EVENTTYPE_SCHEDULE_NOTIFICATION};

public static String[] appEventOccurrance =
        {EVENTOCCURRANCE_ONETIME, EVENTOCCURRANCE_WEEKLY, EVENTOCCURRANCE_MONTHLY, EVENTOCCURRANCE_YEARLY};

}