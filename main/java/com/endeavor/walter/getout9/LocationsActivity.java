package com.endeavor.walter.getout9;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

//https://github.com/googlemaps/android-maps-utils/blob/master/demo/src/com/google/maps/android/utils/demo/DistanceDemoActivity.java


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import static android.os.Environment.DIRECTORY_PICTURES;
import static com.endeavor.walter.getout9.MainActivity.COLOR_IDS;
import static com.endeavor.walter.getout9.MainActivity.PATTERN_POLYGON_ALPHA;
import static com.endeavor.walter.getout9.MainActivity.PATTERN_POLYGON_RUN;
import static com.endeavor.walter.getout9.MainActivity.PERMISSION_GETMYLOCATION;
import static com.endeavor.walter.getout9.MainActivity.REQUEST_GETMYLOCATION;
import static com.endeavor.walter.getout9.MainActivity.appRouteChoices;

import static com.endeavor.walter.getout9.MapFragment.showMapTypeSelectorDialog;
import static com.endeavor.walter.getout9.Utils.getLocation_Color;
import static com.endeavor.walter.getout9.WVSInfo.ADD_GEOLOCATION_ITEM_REQUEST;
import static com.endeavor.walter.getout9.WVSInfo.DELETE_LOCATIONS_ITEM_REQUEST;
import static com.endeavor.walter.getout9.WVSInfo.EDIT_LOCATIONS_ITEM_REQUEST;
import static com.endeavor.walter.getout9.WVSInfo.MARKER_DISABLED;
import static com.endeavor.walter.getout9.WVSInfo.MARKER_ENABLED;
import static com.endeavor.walter.getout9.WVSInfo.RESULT_DELETED;
import static com.endeavor.walter.getout9.WVSInfo.RESULT_ERROR;
import static com.endeavor.walter.getout9.WVSInfo.START_CAMERA;
import static com.endeavor.walter.getout9.WVSInfo.ZOOM_1;

import static com.endeavor.walter.getout9.WVSInfo.type_DISTANCE_DELTA;
import static com.endeavor.walter.getout9.WVSInfo.type_HEART_POINTS;
import static com.endeavor.walter.getout9.WVSInfo.type_MOVE_MINUTES;
import static com.endeavor.walter.getout9.WVSInfo.type_STEP_COUNT_CUMULATIVE;
import static com.endeavor.walter.getout9.WVS_Utils.convertDBdateStringToDate_dateAMPM;
import static com.endeavor.walter.getout9.WVS_Utils.convertDBdateStringToDate_timeAMPM;
import static com.endeavor.walter.getout9.WVS_Utils.convertDBdateStringToString_dateAMPM;
import static com.endeavor.walter.getout9.WVS_Utils.convertDBdateStringToString_endofDay;
import static com.endeavor.walter.getout9.WVS_Utils.convertDBdateStringToString_startofDay;
import static java.text.DateFormat.SHORT;
import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;

public class LocationsActivity extends AppCompatActivity {
    private static final String TAG = LocationsActivity.class.getSimpleName();
    private GoogleMap mMap;
    ArrayList<Location_foo> mLocationArray = new ArrayList<>();
    ArrayList<Health> mHealthArray = new ArrayList<>();
    Long mLocationsId;

    private float fZoom;
    private LatLng llZoomTo;
    Context mContext;
    ArrayList<MarkerOptions> mMarkerArray = new ArrayList<MarkerOptions>();

    private Long mltotalSteps = 0l;
    private Float mftotalDistance = 0f;
    private Float mfHeartIntensity = 0f;
    private Integer miHeartDuration = 0;
    private Long mlMoveMinutes = 0l;
    AlertDialog mdialog;

    ArrayList<Polyline> polylineArray = new ArrayList<Polyline>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_locations);
        Log.i(TAG, " WVS onCreate()");
        Log.i(TAG, " WVS Context.getFilesDir() files-path=" + mContext.getFilesDir().toString());
        Log.i(TAG, " WVS Context.getCacheDir() cache-path=" + mContext.getCacheDir().toString());
        Log.i(TAG, " WVS Environment.getExternalStorageDirectory() external-path=" + Environment.getExternalStorageDirectory().toString());
        Log.i(TAG, " WVS Context.getExternalFilesDir external-files-path=" + mContext.getExternalFilesDir(null).toString());
        Log.i(TAG, " WVS Context.getExternalCacheDir() external-cache-path=" + mContext.getExternalCacheDir().toString());
        Log.i(TAG, " WVS Context.getExternalMediaDirs() external-media-path=" + mContext.getExternalMediaDirs().toString());

        Log.i(TAG, " WVS Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)=" + Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES).toString());


//        setupFitness();  //needs to be moved to the MainActivity

        if (savedInstanceState !=null){

        } else {

        }

        Intent intent2 = getIntent();

        if (intent2.hasExtra("locationsid")) {
            mLocationsId = intent2.getExtras().getLong("locationsid");
        } else {
            mLocationsId = 0l;
        }
        if (intent2.hasExtra("locationstitle")) {
            setTitle( intent2.getExtras().getString("locationstitle"));
        }
//        mdialog = new ProgressDialog(mContext);  //https://stackoverflow.com/questions/11254523/android-runonuithread-explanation
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView2);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.i(TAG, " WVS onMapReady() ");
                mMap = googleMap;
                InitializeMap(mMap);

                if (mLocationsId > 0){
                    try {
                        DrawPath_LocationsId(mLocationsId);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
//              https://ahsensaeed.com/android-custom-info-window-view-on-marker-click-map-utils/
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Toast.makeText(mContext,"onInfoWindowClick " + marker.getTitle(),Toast.LENGTH_LONG).show();
                    }
                });

                mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
                    @Override
                    public void onInfoWindowLongClick(Marker marker) {
                        LocationsRepo lsr = new LocationsRepo(mContext);
                        String tmpTitle = marker.getTitle();
                        String dbTitle = tmpTitle.replace("Geo:","");
                        if (marker.getTitle().contains(MARKER_ENABLED)){
//                            DISABLE
                            dbTitle = dbTitle.replace(MARKER_ENABLED,"");
                            tmpTitle = tmpTitle.replace(MARKER_ENABLED,MARKER_DISABLED);
                            marker.setTitle(tmpTitle);
                            lsr.updateGEOLocation(dbTitle,0, 0);
                            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                        } else {
//                            ENABLE
                            dbTitle = dbTitle.replace(MARKER_DISABLED,MARKER_ENABLED);
                            tmpTitle = tmpTitle.replace(MARKER_DISABLED,MARKER_ENABLED);
                            marker.setTitle(tmpTitle);
                            lsr.updateGEOLocation(dbTitle,1, 0);
                            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        }
                        marker.hideInfoWindow();
                        marker.showInfoWindow();
                    }
                });
            }
//            https://ahsensaeed.com/android-custom-info-window-view-on-marker-click-map-utils/

        });
                //wvs#1 add "google_maps_key" to string resource
                //wvs#2
                mapFragment.setRetainInstance(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_route, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Log.i(TAG, " WVS onOptionsItemSelected " + id);
        LocationsRepo lsr = new LocationsRepo(this);
        Locations tmpLoc = lsr.getRouteInfobyLocationsId(mLocationsId);
        String[] startendtimes = lsr.getStartEndTimesbyLocationsId(mLocationsId);

        if (id == R.id.miEdit_route){
            Intent intent1 = new Intent(this, LocationsAddEditDelete.class);
            intent1.putExtra("locations", tmpLoc);
            intent1.putExtra("requestCode",EDIT_LOCATIONS_ITEM_REQUEST);
            Log.i(TAG, " WVS onOptionsItemSelected before startActivityForResult");
            startActivityForResult(intent1, EDIT_LOCATIONS_ITEM_REQUEST);
            Log.i(TAG, " WVS onOptionsItemSelected after  startActivityForResult");
        } else if (id == R.id.mi_Screenshot_route) {
//            Bitmap b = Screenshot.takescreenshotOfRootView(imageView);
//            takeScreenshot();
            String sFileName = tmpLoc.getDatetime() + "_" + tmpLoc.getPathname() + ".jpg";
            String sInfo = tmpLoc.get_Title();
            take_map_snapshot(sFileName,sInfo);
        } else if (id == R.id.miGEOlocations) {
            String namelatlngenabled = lsr.getGeoLocations();
            DisplayGeoMarkers(namelatlngenabled);
        } else if (id == R.id.miDelete_route){
            Intent intent1 = new Intent(this, LocationsAddEditDelete.class);
            intent1.putExtra("locations", tmpLoc);
            intent1.putExtra("requestCode",DELETE_LOCATIONS_ITEM_REQUEST);
            startActivityForResult(intent1,DELETE_LOCATIONS_ITEM_REQUEST);
        }
        else if (id == R.id.miUpdateTotalTime){
//            lsr.updateTotalTimeLocationsId(mLocationsId);
            LocationRepo lr = new LocationRepo(this);
            Toast.makeText(mContext,lr.getTimeDifference_wPauses_LocationsId(mLocationsId),Toast.LENGTH_LONG).show();
//            read_update_DataForPeriod(startendtimes, "TYPE_MOVE_MINUTES"); //AGGREGATE_MOVE_MINUTES
        } else if (id == R.id.miUpdateFit_route_only) {
            DataForPeriod_update_tblLocations(startendtimes, mLocationsId, false, true  );
        } else if (id == R.id.miUpdate_ELEVATION_route) {
            Double dblElevationDiff = 0d;
            dblElevationDiff = lsr.getElevationDiffLocationsId(mLocationsId);
            lsr.updateElevationDiffLocationsId(mLocationsId, dblElevationDiff);
            try {
                DrawPath_LocationsId(mLocationsId);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, " WVS onActivityResult()");
        if (requestCode == EDIT_LOCATIONS_ITEM_REQUEST){
            if (resultCode == RESULT_CANCELED){
                Log.i(TAG, " WVS onActivityResult() CANCELLED");
            } else if (resultCode == RESULT_OK){
                if (data.getExtras().containsKey("routename")){
                    String tmptitle = "updated to " + data.getExtras().getString("routename");
                    setTitle(tmptitle);
                }
            }
        } else if (requestCode == DELETE_LOCATIONS_ITEM_REQUEST){
            Log.i(TAG, " WVS onActivityResult() DELETE ROUTE");
            if (resultCode == RESULT_CANCELED){
                Log.i(TAG, " WVS onActivityResult() CANCELLED");
            } else if (resultCode == RESULT_DELETED){
                Log.i(TAG, " WVS  deleted route, should navigate back to LocationsFragment...");
                finish();
            } else if (resultCode == RESULT_ERROR){
                Log.i(TAG, " WVS  COULD NOT deleted route, ERROR?");
            }
        } else if (requestCode == START_CAMERA & resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            if (extras.containsKey("imagefile")){
                File tmpfile = (File) extras.get("imagefile");
//              cameraIntent.putExtra("latlng", latlng);
                String latlng = extras.getString("latlng");

                if (tmpfile.exists()){
//                  check to see if note exists based on locationsid
                    NoteRepo nr = new NoteRepo(mContext);
                    Note tmpnote = nr.getNoteByLocationsId(mLocationsId);
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
                        tmpnote = new Note(0,"Pictures",null, latlng,null,0,newimagefilename,null, mLocationsId.intValue());
                        nr.insert(tmpnote);
                    }
                }
            }
        }

//        else if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                subscribe_toSteps();
//            }
//        }
    }

    public void InitializeMap(final GoogleMap gmMap) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//          PERMISSION_DENIED...
            Toast.makeText(this , "onMapReady NO permission... will ask now?", Toast.LENGTH_LONG).show();
//          No explanation needed; request the permission
            ActivityCompat.requestPermissions(this  , PERMISSION_GETMYLOCATION, REQUEST_GETMYLOCATION);
            return;
        }

//        **********************
        //              get last zoom level
        fZoom = Utils.getZoomLevel(mContext);
        if (fZoom == 0) {
            fZoom = ZOOM_1;
            Utils.setZoomLevel(mContext, fZoom);
        }
//                if (mMaptype == 0) {
//                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                } else {
//                    mMap.setMapType(mMaptype);
//                }
        gmMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                llZoomTo = gmMap.getCameraPosition().target;
                fZoom = gmMap.getCameraPosition().zoom;
//                Log.i(TAG, " WVS onMapReady() setOnCameraIdleListener Utils.setZoomLevel, setting fZoom to " + fZoom);
//                Log.i(TAG, " WVS onMapReady() setOnCameraIdleListener setting llZoomTo to " + llZoomTo.toString());

                Utils.setZoomLevel(mContext, fZoom);
            }
        });

        gmMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                showDialogMapOptions(latLng);
            }
        });

        gmMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                boolean isPolylineClicked = false;
                Log.i(TAG, " WVS onMapClick = " +latLng.toString()  );
                for (Polyline polyline : polylineArray) {
                    if (PolyUtil.isLocationOnPath(latLng, polyline.getPoints(), false, 50)) {
                        isPolylineClicked = true;
                        Log.i(TAG, " WVS PolyUtil.isLocationOnPath TRUE"  );
                        Log.i(TAG, " WVS polyline clicked " + polyline.getColor() ); //COLOR_IDS[]
                        // Polyline clicked!
                    }
                }

                if (!isPolylineClicked) {
                    // Other OnMapClickListener behavior
                    Log.i(TAG, " WVS PolyUtil.isLocationOnPath FALSE"  );
                }
            }
        });

        gmMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            //              https://stackoverflow.com/questions/14497734/dont-snap-to-marker-after-click-in-android-map-v2
            Marker lastOpened = null;
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Check if there is an open info window
                if (lastOpened != null) {
                    // Close the info window
                    lastOpened.hideInfoWindow();

                    // Is the marker the same marker that was already open
                    if (lastOpened.equals(marker)) {
                        // Nullify the lastOpened object
                        lastOpened = null;
                        // Return so that the info window isn't opened again
                        return true;
                    }
                }


                String stitle = marker.getTitle();
                Log.i(TAG, " WVS stitle = " + stitle);
                Log.i(TAG, " WVS stitle.indexOf(\"Pics:\") = " + String.valueOf(stitle.indexOf("Pics:")) );
                if (stitle.contains("Pics:")){
//                  open pictures...
                    String tmpImageFiles = stitle.substring("Pics:".length());
                    Log.i(TAG, " WVS tmpImageFiles = " + tmpImageFiles);
                    if (tmpImageFiles.length() > 0) {
                        Intent intent1 = new Intent(mContext , ViewImages.class);
                        intent1.putExtra("imagefiles", tmpImageFiles);
                        startActivity(intent1);
                    }
                } else {
                    // Open the info window for the marker
                    marker.showInfoWindow();
                }

                // Re-assign the last opened such that we can close it later
                lastOpened = marker;
//                        return false;
                // Event was handled by our code do not launch default behaviour.
                return true;
            }
        });

//        **********************


//      on first install, and user grants permission, it will never get below... double check?
        gmMap.setMyLocationEnabled(true);
        gmMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                return false;
            }
        }); //wvs#3 requires implements com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListenter...

        gmMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(@NonNull Location location) {

            }
        });

        gmMap.getUiSettings().setZoomControlsEnabled(true);
        gmMap.getUiSettings().setCompassEnabled(true);
        gmMap.setPadding(0, 40, 0, 20);

//        gmMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
//            @Override
//            public void onMapLongClick(LatLng latLng) {
//                showMapTypeSelectorDialog(gmMap, mContext);
//            }
//        });

    }

    public boolean DrawPath_LocationsId(Long locationsid) throws ParseException {
        Boolean bRet;

//        frag = (MapFragment) viewPager.getAdapter().instantiateItem(viewPager,0);

        if (mMap!=null) {
            mMap.clear();
            LocationRepo lr = new LocationRepo(this);
            mLocationArray = lr.getRouteActivityByLocationsId(locationsid, mContext);
            LocationsRepo lrs = new LocationsRepo(this);
            Locations tmpLoc = lrs.getRouteInfobyLocationsId(locationsid);

//            bRet = DrawPath(mLocationArray);
//            AlertDialog mdialog = new ProgressDialog(mContext);
//            mdialog.setMessage("Populating Map.....");
//            mdialog.show();
            DrawPathWrapper(mLocationArray, tmpLoc);  // wvs 8/6/20
            bRet = true;
//            https://stackoverflow.com/questions/6048975/google-maps-v3-how-to-calculate-the-zoom-level-for-a-given-bounds
//            if (bRet) {
//                if (tmpLoc.getLatitude() == null || tmpLoc.getLatitude() == 0) {
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocationArray.get(0).getLatitude(), mLocationArray.get(0).getLongitude()), fZoom));
//                    Log.i(TAG, " WVS DrawPath_LocationsId() ZOOMING based on fZoom");
//                } else {
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(tmpLoc.getLatitude(), tmpLoc.getLongitude()), tmpLoc.getZoomLevel()));
//                    Log.i(TAG, " WVS DrawPath_LocationsId() ZOOMING based on saved locations/zoom level");
//                }
//            }
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(llWillo, fZoom));
        } else {
            bRet = false;
        }
        return bRet;
    }



    public boolean DrawPath(ArrayList<Location_foo> tmpLocationArray, Locations tmpLocs) throws ParseException {
        boolean bretValue = false;
        PolylineOptions tmpPolyLineOpts;
        boolean bMapColorChange;
        boolean bIsPath;
        Location tmpLocation;
        int tmpColor;
        String sMarkerTitle;
        Polyline tmpPolyLine;


        polylineArray = new ArrayList<Polyline>();
        int arrowColor = Color.WHITE;  //BACKGROUND of surrounding circle
        BitmapDescriptor endCapIcon = WVS_Utils.getEndCapIcon(mContext, arrowColor, R.mipmap.ic_end_arrow1);

//        float totalLength=0f;
//        float[] fDistance = new float[1];

        if (tmpLocationArray != null && tmpLocationArray.size() > 0) {

            tmpPolyLineOpts = new PolylineOptions();

            for (int i = 0; i < tmpLocationArray.size(); i++) {
                tmpLocation = tmpLocationArray.get(i);
//                tmpLocation.distanceBetween();
                if (i == 0) {
                    bMapColorChange = false;
                    bIsPath = false;
                    tmpColor = getLocation_Color(tmpLocation);
                    tmpPolyLineOpts.color(tmpColor);
                    tmpPolyLineOpts.add(new LatLng(tmpLocation.getLatitude(), tmpLocation.getLongitude()));
                    sMarkerTitle = "Start: "+ convertDBdateStringToDate_timeAMPM(((Location_foo) tmpLocation).getDateTime());
                    AddMarker((Location_foo) tmpLocation,sMarkerTitle, tmpLocs);
                } else {
//                    tmpLocation.distanceBetween(tmpLocationArray.get(i-1).getLatitude(),tmpLocationArray.get(i-1).getLongitude(), tmpLocation.getLatitude(), tmpLocation.getLongitude(), fDistance );
//                    totalLength = totalLength + fDistance[0];
//                    Log.i(TAG, " WVS " + i + " total=" + totalLength + ", segment=" + fDistance[0]);
                    tmpColor = getLocation_Color(tmpLocation);
                    if (((Location_foo) tmpLocation).getActivityId()!=null && ((Location_foo) tmpLocation).getActivityId() > 0){
                        if (((Location_foo) tmpLocation).getTransitionId() == ActivityTransition.ACTIVITY_TRANSITION_ENTER){
//                            ENTERING NEW TRANSISTION
//                            Log.i(TAG, " WVS activity/transition = "+ ((Location_foo) tmpLocation).getActivityId() + "/" + ((Location_foo) tmpLocation).getTransitionId()+ ": " + ((Location_foo) tmpLocation).getDateTime() );

                            Integer id =  ((Location_foo) tmpLocation).getActivityId();
                            tmpColor = COLOR_IDS[ id ];
                        }
//                        mrkoptList.add(tmpMrkOpt);
                    }

                    if (tmpColor == getLocation_Color(tmpLocationArray.get(i - 1))) {
//                      no color change from last one.... add next location
                        tmpPolyLineOpts.add(new LatLng(tmpLocation.getLatitude(), tmpLocation.getLongitude()));
                        bIsPath = true;
                    } else {
//                      draw path

//                        https://stackoverflow.com/questions/14416457/clickable-polylines-google-maps-api-android
                        tmpPolyLine = mMap.addPolyline(tmpPolyLineOpts);
                        //                        https://stackoverflow.com/questions/38202109/how-to-create-direction-arrows-for-my-polylines-in-android
//                        tmpPolyLine.setEndCap(new SquareCap());
                        tmpPolyLine.setEndCap(new CustomCap(endCapIcon, 64));  //larger # smaller cirlcle?
                        tmpPolyLine.setClickable(true);
                        tmpPolyLine.setTag(((Location_foo) tmpLocation).getLocationId());
                        polylineArray.add(tmpPolyLine);
//                        polylineArray.get(polylineArray.size()-1).setClickable(true);
//                        polylineArray.get(polylineArray.size()-1).setTag(((Location_foo) tmpLocation).getLocationId());

                        bretValue = true;
                        ((Location_foo) tmpLocation).setColorPath(tmpColor);
//                      then begin new path
                        tmpPolyLineOpts = new PolylineOptions();
                        tmpPolyLineOpts.color(tmpColor);
                        if(((Location_foo) tmpLocation).getActivityId()!=null &&  ((Location_foo) tmpLocation).getActivityId() == DetectedActivity.IN_VEHICLE){
//                          IN_VEHICLE... dashed lines
                            tmpPolyLineOpts.pattern(PATTERN_POLYGON_ALPHA);
                        } else if (((Location_foo) tmpLocation).getActivityId()!=null && ((Location_foo) tmpLocation).getActivityId() == DetectedActivity.RUNNING){
                            tmpPolyLineOpts.pattern(PATTERN_POLYGON_RUN);
                        }
                        tmpPolyLineOpts.add(new LatLng(tmpLocation.getLatitude(), tmpLocation.getLongitude()));
                        bIsPath = false;
                        bMapColorChange = true;
                    }
                }
                if (i == tmpLocationArray.size() - 1 && bIsPath) {
                    tmpPolyLine = mMap.addPolyline(tmpPolyLineOpts);
                    tmpPolyLine.setEndCap(new CustomCap(endCapIcon, 64));  //larger # smaller cirlcle?
                    tmpPolyLine.setClickable(true);
                    tmpPolyLine.setTag(((Location_foo) tmpLocation).getLocationId());
                    polylineArray.add(tmpPolyLine);

                    bretValue = true;
                }
                if (i == tmpLocationArray.size() - 1 ) {
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    Date d1 = null;
                    Date d2 = null;
                    sMarkerTitle = "End: "+ convertDBdateStringToDate_timeAMPM(((Location_foo) tmpLocation).getDateTime());
//                    totalLength = totalLength/1609.344f;
//                    Log.i(TAG, " WVS " + i + " total miles = " + totalLength );
//                    LocationsRepo lsr = new LocationsRepo(this);
//                    lsr.updateTotalDistanceLocationsId(((Location_foo) tmpLocation).getLocationSId(), totalLength);

//                    try {
//                        d1 = convertDBdateStringToDate_datetime(tmpLocationArray.get(0).getDateTime());
//                        d2 = convertDBdateStringToDate_datetime(((Location_foo) tmpLocation).getDateTime());
//
//                        //in milliseconds
//                        long diff = d2.getTime() - d1.getTime();
//                        long diffSeconds = diff / 1000 % 60;
//                        long diffMinutes = diff / (60 * 1000) % 60;
//                        sMarkerTitle = "End: total "+ diffMinutes + ":" + String.format("%02d", diffSeconds) ;
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

//                    sMarkerTitle = "End: "+ ((Location_foo) tmpLocation).getDateTime();
                    AddMarker((Location_foo) tmpLocation,sMarkerTitle, tmpLocs);
                }
            }
//            check to see if images are available
            NoteRepo nr = new NoteRepo(mContext);
            Long lLocationsId = tmpLocationArray.get(0).getLocationSId().longValue();
            Note tmpnote = nr.getNoteByLocationsId(lLocationsId);
            Double dLat=0d;
            Double dLng=0d;
            LatLng latlng=null;
            String [] arrLatLngs;
            String tmpLatLng;
            int iStart, iEnd, imgcnt;
            if (tmpnote!=null){
                String images = tmpnote.getImage();
                if (images.length() > 0){
//                        lat/lng: (37.76095613513063,-122.40868058055638)
                    if (tmpnote.getTag() != null && tmpnote.getTag().contains("lat/lng:")){
//                      wvs todo: loop thru and add each individual marker
                        arrLatLngs = tmpnote.getTag().split("lat/lng:");
                        imgcnt = 0;
                        for (int i = 0; i < arrLatLngs.length; i++){
                            tmpLatLng = arrLatLngs[i];
                            if (tmpLatLng.length() >0) {
                                if (tmpLatLng.contains("(") && tmpLatLng.contains(")")) {
                                    iStart = tmpLatLng.indexOf("(") + 1;
                                    iEnd = tmpLatLng.indexOf(")");
                                    dLat = Double.valueOf(tmpLatLng.substring(iStart, iEnd).split(",")[0]);
                                    dLng = Double.valueOf(tmpLatLng.substring(iStart, iEnd).split(",")[1]);
//                            iStart = tmpnote.getTag().indexOf("(")+1;
//                            iEnd = tmpnote.getTag().indexOf(")");
//                            dLat = Double.valueOf( tmpnote.getTag().substring(iStart,iEnd).split(",")[0]);
//                            dLng = Double.valueOf( tmpnote.getTag().substring(iStart,iEnd).split(",")[1]);
                                    latlng = new LatLng(dLat, dLng);
                                    if (images.split(",").length + 1 == arrLatLngs.length) {
                                        AddMarkerLatLng(latlng, "Pics:" + images.split(",")[imgcnt], 1);
                                    } else {
                                        AddMarkerLatLng(latlng, "Pics:" + images, 1);
                                    }
                                    imgcnt += 1;
                                } else {

                                }
                            }
                        }

                    } else {
                        int halfway = tmpLocationArray.size() / 2;
                        AddMarker(((Location_foo) tmpLocationArray.get(halfway)), "Pics:" + images, tmpLocs);
                    }
                }
//                String [] imageArray = images.split(",");
//                if (images.length() > 0) {
//                    File imageFile;
//                    for (String img : imageArray) {
//                        imageFile = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES))+"/" + img );
//                        if (imageFile.exists()){
//                            sLatLng = getCameraPhotoLatLng(imageFile);
//                        }
//                    }
//                }
            }
        }

        return bretValue;
    }

    public void AddMarker(Location_foo tmpLocation, String pTitle, Locations tmpLocs){
        if (mMap!=null) {
            MarkerOptions tmpMrkOpt = new MarkerOptions();
            tmpMrkOpt.position(new LatLng(tmpLocation.getLatitude(), tmpLocation.getLongitude()));
            tmpMrkOpt.title(pTitle);
//          wvs hint: https://www.zoftino.com/google-maps-android-custom-info-window-example#:~:text=To%20create%20custom%20info%20window,in%20your%20custom%20info%20window.&text=After%20custom%20info%20window%20layout,InfoWindowAdapter%20class%20by%20implementing%20GoogleMap.
            CustomInfoWindowGoogleMap.InfoWindowData info = new CustomInfoWindowGoogleMap.InfoWindowData();

            if (pTitle.indexOf("Start:") >= 0) {
                tmpMrkOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else if (pTitle.indexOf("Pics:") >= 0){
                tmpMrkOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE ));
            } else if (pTitle.indexOf("End:") >= 0) {
//          else default color is red
            }
            info.setTitle(pTitle);
            info.setImage("RouteDetails");
            info.setDateTime(convertDBdateStringToDate_dateAMPM(tmpLocs.getDatetime()));
            info.setTotalTime(tmpLocs.getTotalTime());
            info.setTotalDistance(String.format("%3.2f", tmpLocs.getTotalDistance()));
            info.setSteps(String.format("%5d", tmpLocs.getTotalSteps()));
            info.setElevation_meters(String.format("%5.2f", tmpLocs.getElevationDifference()));
            info.setMoveMinutes(String.format("%3d", tmpLocs.getMoveDuration()));
            info.setHeartPoints(String.format("%3.0f", tmpLocs.getHeartIntensity()));
            info.setHeartMinutes(String.format("%3d", tmpLocs.getHeartDuration()));

            CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(this);
            mMap.setInfoWindowAdapter(customInfoWindow);
            tmpMrkOpt.draggable(true);
            Marker m = mMap.addMarker(tmpMrkOpt);
            if (pTitle.indexOf("End:") >= 0 || pTitle.indexOf("Start:") >= 0) {
                m.setTag(info);
            }
        }

    }

    public void AddMarkerLatLng(LatLng latlng, String pTitle, Integer enabled){
        if (mMap!=null) {
            MarkerOptions tmpMrkOpt = new MarkerOptions();
            tmpMrkOpt.position(latlng);
//            tmpMrkOpt.title(pTitle);

            if (pTitle.indexOf("Start:") >= 0) {
                tmpMrkOpt.title(pTitle);
                tmpMrkOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else if (pTitle.indexOf("Pics:") >= 0){
//                tmpMrkOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE ));
//              https://stackoverflow.com/questions/8248077/google-maps-v3-standard-icon-shadow-names-equiv-of-g-default-icon-in-v2
//              https://maps.gstatic.com/mapfiles/ms2/micons/camera.png
//                tmpMrkOpt.icon("https://maps.gstatic.com/mapfiles/ms2/micons/camera.png");
                tmpMrkOpt.title(pTitle);
                tmpMrkOpt.icon(BitmapDescriptorFactory.fromAsset("ic_camera.png"));
            } else if (pTitle.indexOf("Geo:") >= 0){
//                tmpMrkOpt.icon(BitmapDescriptorFactory.fromAsset("camera.png"));
                if (enabled.equals(1)) {
                    tmpMrkOpt.title(pTitle + MARKER_ENABLED);
                    tmpMrkOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                } else {
                    tmpMrkOpt.title(pTitle + MARKER_DISABLED);
                    tmpMrkOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                }
            }
//          else default color is red
            tmpMrkOpt.draggable(true);
//          review added markers
            Boolean bMarkerExist = false;
            if (mMarkerArray != null){
                for(int i=0; i < mMarkerArray.size(); i++){
                    if (mMarkerArray.get(i).getTitle().equals(tmpMrkOpt.getTitle())){
                        bMarkerExist = true;
                        break;
                    }
                }
                if(!bMarkerExist){
                    mMap.addMarker(tmpMrkOpt);
                    mMarkerArray.add(tmpMrkOpt);
                } else {
//                    Toast.makeText(mContext,tmpMrkOpt.getTitle() + " MARKER already exists.", Toast.LENGTH_LONG).show();
                }
            }
//            mMap.addMarker(tmpMrkOpt);
        }

    }

    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */
    private void readData() {
        //wvs #4 options menu throws exception
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                long total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                                Log.i(TAG, " WVS readData() Total steps: " + total);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, " WVS readData()There was a problem getting the step count.", e);
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
            Log.i(TAG, " WVS dailyHealth_DataForPeriod EQUAL start and end times... exit");
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
//                        try {
//                            DrawPath_LocationsId(mLocationsId);
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
                        //                        update title with steps
                        LocationsRepo lsr = new LocationsRepo(mContext);
                        String curTitle = (String) getTitle();
                        String updateTitle = curTitle.substring(0, curTitle.indexOf("mi, ") + "mi, ".length()) + lsr.getFitSInfo(mLocationsId) ;
                        setTitle(updateTitle);
                        if (btblLocations){
//                          assumption is just finished updating route fit tblLocations so now get health from the previous week
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
    public void parseintoDataSets_mHealthArray(DataReadResponse dataReadResult, String sDataType) {
        //  previously printData()
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, " WVS parseintoDataSets() Number of returned BUCKETS is: " + dataReadResult.getBuckets().size());
            int b = 0;
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                Log.i(TAG, " WVS parseintoDataSets() BUCKET #: " + b);
                b=b+1;
                int i = 0;
                for (DataSet dataSet : dataSets) {
                    Log.i(TAG, " WVS parseintoDataSets() Number DataSets #: " + i);
                    dumpDataSet_mHealthArray(dataSet, sDataType);
                    i=i+1;
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, " WVS parseintoDataSets() Number of returned DATASETS is: " + dataReadResult.getDataSets().size());
            int i = 0;
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                Log.i(TAG, " WVS parseintoDataSets() Number DataSets #: " + i);
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

    private void showDialogMapOptions(final LatLng latlng){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        String TitleInfo = getTitle().toString();
        TitleInfo = TitleInfo.substring(TitleInfo.indexOf(",") + 1);
        builder.setTitle(TitleInfo);
//        builder.setMessage("Rename or Delete");
//        builder.setCancelable(true);
        final String[] choices = {"Save Position/Zoom", "Map Types", "Add GEO location to Path?", "Snap Picture", "Get Picture", "Delete bad points?"};

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG," WVS Cancel clicked");
                dialog.dismiss();
            }
        });
        builder.setSingleChoiceItems(choices, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG," WVS  onMapLongClick " + choices[which]);
                if (which == 0){
//                    "Save Position/Zoom",
                    LocationsRepo lsr = new LocationsRepo(mContext);
                    Locations tmpLocs = lsr.getRouteInfobyLocationsId(mLocationsId);
                    tmpLocs.setZoomLevel(fZoom);
                    tmpLocs.setLatitude(llZoomTo.latitude);
                    tmpLocs.setLongitude(llZoomTo.longitude);
                    lsr.updateMoveZoomTo(tmpLocs);  //wvs 5/28/19
                    Log.i(TAG, " WVS updating tblLocations with zoom,location");
                    Toast.makeText(mContext,"Saved position and zoom level",Toast.LENGTH_LONG).show();

                } else if (which == 1) {
//                    "Map Types",
                    showMapTypeSelectorDialog(mMap, mContext, mLocationArray);

                } else if (which == 2) {
//                    "Add GEO location to Path?",
                    boolean isPolylineClicked = false;
                    Log.i(TAG, " WVS onMapLongClick = " +latlng.toString()  );
                    for (Polyline polyline : polylineArray) {
                        if (PolyUtil.isLocationOnPath(latlng, polyline.getPoints(), false, 50)) {
                            isPolylineClicked = true;
// wvs 8/4/20                 polyline.getPoints(); latlng
//                            polyline.getTag();  locationid
//                            add Geo Name
//                            launch LocationsAddEditDelete.class
                            Intent intent1 = new Intent(mContext, LocationsAddEditDelete.class);
                            intent1.putExtra("locationsid", (long) polyline.getTag());  // exception Long cannot be cast to Bundle
                            LatLng tmplatlng = new LatLng(polyline.getPoints().get(0).latitude, polyline.getPoints().get(0).longitude);
                            intent1.putExtra("objlatlng", (Parcelable) tmplatlng);  // wvs todo later altitude?
                            intent1.putExtra("requestCode",ADD_GEOLOCATION_ITEM_REQUEST);
                            startActivityForResult(intent1,ADD_GEOLOCATION_ITEM_REQUEST);
                            
                            Toast.makeText(mContext,"clicked locationid = " + polyline.getTag().toString(),Toast.LENGTH_LONG).show();
//                            Log.i(TAG, " WVS polyline clicked locationid = " + polyline.getTag().toString() ); //COLOR_IDS[]
                            // Polyline clicked!
                        }
                    }

                    if (!isPolylineClicked) {
                        // Other OnMapClickListener behavior
                        Log.i(TAG, " WVS PolyUtil.isLocationOnPath FALSE"  );
                    }
                } else if (which == 3) {
//                    "Snap Picture",
                    Intent cameraIntent = new Intent(mContext, AndroidCameraApi.class);
                    cameraIntent.putExtra("latlng",  latlng.toString());  //lat/lng: (37.76095613513063,-122.40868058055638)
                    startActivityForResult(cameraIntent, START_CAMERA);
                } else if (which == 4) {
//"Get Picture",
                } else if (which == 5) {
//                    "Delete bad points?"
                    deleteSegmentInRoute(latlng);
                }
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void showDialogDeleteSegsRoute(ArrayList <Long> arrayLocationIds) {

        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        LayoutInflater inflater = this.getLayoutInflater();
//        final View dialogView = inflater.inflate(R.layout.yes_no_dialog, null);
//        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Delete bad sections in Route?");
        dialogBuilder.setMessage("Yes - click Delete" + "\n" + "No - click Cancel");

        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//              delete specified segments in route
                LocationRepo lr = new LocationRepo(mContext);
                for (int i = 0; i < arrayLocationIds.size(); i++) {
                    lr.deleteSectionInRoutebyLocationd_Id(arrayLocationIds.get(i));
                }
                // update distance
                mLocationArray = lr.getRouteActivityByLocationsId(mLocationsId, mContext);
                WVS_MapHelper mh = new WVS_MapHelper();
                Float totaldistance = mh.Determine_Update_Distance(mLocationArray, mContext, true);
//                          update time ...
                // refresh map or fragment
                try {
                    DrawPath_LocationsId(mLocationsId);
                } catch (ParseException e) {
                    e.printStackTrace();
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
        androidx.appcompat.app.AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void ShareImage(File imageFile, String info) {
//     https://stackoverflow.com/questions/2661536/how-to-programmatically-take-a-screenshot-on-android
        if (imageFile.exists()) {
            String route = imageFile.getName();
            String sIdid = "";
            String sWhen = "";
            int routechoice_pos = route.indexOf("_") + 1;

            if (routechoice_pos >= 0){
                sWhen = convertDBdateStringToString_dateAMPM(route.substring(0,routechoice_pos - 1));
//                 "Dog Walk", "Walk", "Run", "Going Out", "To Gym", "Pickup"
                if (route.contains(appRouteChoices[0])){
                    sIdid = "I walked the dog! ";
                } else if (route.contains(appRouteChoices[1])){
                    sIdid = "I went for a walk! ";
                } else if (route.contains(appRouteChoices[1])){
                    sIdid = "I went for a walk! ";
                } else if (route.contains(appRouteChoices[2])){
                    sIdid = "I went for a run! ";
                } else if (route.contains(appRouteChoices[3])){
                    sIdid = "I went out! ";
                } else if (route.contains(appRouteChoices[4])){
                    sIdid = "I went to the gym! ";
                }  else if (route.contains(appRouteChoices[5])){
                    sIdid = "I'm a yellow cab! ";
                }
            }
            Intent intent = new Intent();
//          to view
//            intent.setAction(Intent.ACTION_VIEW);

//https://www.androidcode.ninja/android-share-intent-example/  THIS WORKS!!
//          to share image
            intent.setAction(Intent.ACTION_SEND);
//            /data/user/0/com.endeavor.walter.getout9/files

            Uri uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".fileprovider", imageFile);
//https://stackoverflow.com/questions/41737138/why-using-a-fileprovider-i-cant-open-file-from-internal-storage-with-external-a
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);

            intent.setDataAndType(uri, "image/*");  //intent.setDataAndType(uri, "image/jpeg");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
//            intent.putExtra(Intent.EXTRA_SUBJECT, "I walked the dog");
            intent.putExtra(Intent.EXTRA_TEXT, sWhen + System.getProperty("line.separator") + sIdid + System.getProperty("line.separator") + info);
            intent.putExtra(Intent.EXTRA_SUBJECT, sWhen + ", " + sIdid + ", " + info);

//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        }
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void take_map_snapshot(final String filename, final String info){
//        https://github.com/googlemaps/android-samples/blob/master/ApiDemos/java/app/src/main/res/layout/snapshot_demo.xml
        if (mMap == null) {
            return;
        }
        final GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                // Callback is called from the main thread, so we can modify the ImageView safely.
                Log.i(TAG, " WVS bitmap.toString() " + bitmap.toString());
                Date now = new Date();
                android.text.format.DateFormat.format("yyyy-MM-dd_HH:mm:ss", now);
                String mPath = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES).toString() + "/" + filename;;

//              files-path=/data/user/0/com.endeavor.walter.getout9/files
//                String mPath = mContext.getFilesDir().toString() + "/" + filename;

                File imageFile = new File(mPath);

                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(imageFile);
                } catch (FileNotFoundException e) {
                    Log.i(TAG, " WVS outputStream error " + e.toString());
                    e.printStackTrace();
                }
                int quality = 100;
                Log.i(TAG, " WVS  bitmap.compress BEFORE" );
                boolean bret = bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                Log.i(TAG, " WVS  bitmap.compress AFTER " + bret);

                try {
                    outputStream.flush();
                } catch (IOException e) {
                    Log.i(TAG, " WVS outputStream.flush error " + e.toString());
                    e.printStackTrace();
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.i(TAG, " WVS outputStream.close error " + e.toString());
                    e.printStackTrace();
                }
                ShareImage(imageFile, info);
            }
        };

        mMap.snapshot(callback);

    }

    public void deleteSegmentInRoute(LatLng latlng){
        LocationRepo lr = new LocationRepo(mContext);
        ArrayList<Location_foo> tmparrayLoc = lr.getBadSections_RouteInfobyLocationsId(mLocationsId, latlng);
        Log.i(TAG, "delete bad latlng");
        boolean bAttemptDelete = false;
        boolean bDelete = false;
        if (tmparrayLoc != null && tmparrayLoc.size() > 0){
            for (int i = 0; i < tmparrayLoc.size(); i++){
                Bundle extras = tmparrayLoc.get(i).getExtras();
                if (extras != null && extras.containsKey("Bad Location")){
                    if (extras.getBoolean("Bad Location"))
                        bAttemptDelete = true;
                    break;
                }
            }
        }
        if (bAttemptDelete) {
            long locationid;
//                        Location tmpLocation;
            float[] fDistance = new float[1];
            Location_foo seedLatLng = null;
            Location_foo nextLatLng = null;
            ArrayList <Long> arrayLocationIds = new ArrayList<>();
            for (int i = 0; i < tmparrayLoc.size(); i++) {
//                          SKIP FIRST ROW RETURNED, assume it's the SEED?  DELETE ALL OTHERS
                if (i == 0) {
                    seedLatLng = tmparrayLoc.get(0);
                } else {
//                              approximate distance in meters between two latlngs
                    nextLatLng = tmparrayLoc.get(i);
                    Location.distanceBetween(seedLatLng.getLatitude(), seedLatLng.getLongitude(), nextLatLng.getLatitude(), nextLatLng.getLongitude(), fDistance);
//                              1 Mile = 1609.344 Meters
//                              milestofeet = 5280;
//                              meters to feet = 3.28084
                    float delta = 0f;
                    delta = fDistance[0] * 3.29084f;
                    Log.i(TAG, " WVS locationid " + seedLatLng.getLocationId() + " and " + nextLatLng.getLocationId() + " = " + delta);
                    Log.i(TAG, " WVS locationid " + seedLatLng.getLatitude() + ", " + seedLatLng.getLongitude() + " next " + nextLatLng.getLatitude() + ", " + nextLatLng.getLongitude());

                    if (delta > 800f) {
                        locationid = nextLatLng.getLocationId();
                        arrayLocationIds.add(locationid);
//                                    lr.deleteSectionInRoutebyLocationd_Id(locationid);
                        bDelete = true;
//                                    Log.i(TAG, " WVS DELETING " + nextLatLng.getLocationId());
                    } else {
                        seedLatLng = tmparrayLoc.get(i);
                    }
                }
            }
            if (bDelete) {
                showDialogDeleteSegsRoute(arrayLocationIds);
            }
        }

    }
    public void DrawPathWrapper(ArrayList<Location_foo> tmpLocationArray, Locations tmpLoc) {
        runOnUiThread(new Runnable(){
            public void run() {
                //call DrawPath wvs 8/4/20
                Boolean bRet;
                try {
                    bRet = DrawPath(tmpLocationArray, tmpLoc);
                    try {
//                        mdialog.dismiss();
                        if (bRet) {
                            if (tmpLoc.getLatitude() == null || tmpLoc.getLatitude() == 0) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocationArray.get(0).getLatitude(), mLocationArray.get(0).getLongitude()), fZoom));
                                Log.i(TAG, " WVS DrawPath_LocationsId() ZOOMING based on fZoom");
                            } else {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(tmpLoc.getLatitude(), tmpLoc.getLongitude()), tmpLoc.getZoomLevel()));
                                Log.i(TAG, " WVS DrawPath_LocationsId() ZOOMING based on saved locations/zoom level");
                            }
                        }

                    } catch (final Exception ex) {
                        Log.i("---","DrawPathWrapper() Exception in thread");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void DisplayGeoMarkers(String sNameLatLngEnabled){

        String [] arrNameLatLngs;
        String name;
        Double lat, lng;
        Integer enabled;
        LatLng latlng;
        if (!sNameLatLngEnabled.equals("")) {
            arrNameLatLngs = sNameLatLngEnabled.split(",");
            for (int i = 0; i < arrNameLatLngs.length; i++) {
                name = "Geo:" + arrNameLatLngs[i];
                lat = Double.valueOf(arrNameLatLngs[i + 1]);
                lng = Double.valueOf(arrNameLatLngs[i + 2]);
                enabled = Integer.valueOf(arrNameLatLngs[i + 3]);
//                within = Integer.valueOf(arrNameLatLngs[i + 4]);
                latlng = new LatLng(lat, lng);
                AddMarkerLatLng(latlng, name, enabled);
                i = i + 4;
            }
        } else {
            Toast.makeText(mContext, "No GEO locations exist. Long press map to create!", Toast.LENGTH_LONG).show();
        }
    }
}
