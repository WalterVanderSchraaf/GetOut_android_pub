package com.endeavor.walter.getout9;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
//import android.support.annotation.NonNull;  replaced by androidx....
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.Fragment;
//import android.support.v4.content.LocalBroadcastManager;
//import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;  //replaces android.support.v4.app.Fragment; etc

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import static com.endeavor.walter.getout9.MainActivity.COLOR_IDS;
import static com.endeavor.walter.getout9.MainActivity.PATTERN_POLYGON_ALPHA;
import static com.endeavor.walter.getout9.MainActivity.PATTERN_POLYGON_RUN;
import static com.endeavor.walter.getout9.MainActivity.PERMISSION_GETMYLOCATION;
import static com.endeavor.walter.getout9.MainActivity.REQUEST_GETMYLOCATION;

import static com.endeavor.walter.getout9.Utils.appLog;
import static com.endeavor.walter.getout9.Utils.getLocation_Color;
import static com.endeavor.walter.getout9.Utils.setLocation_Color;

public class MapFragment extends Fragment implements GoogleMap.OnMapClickListener {
    private String TAG = MapFragment.class.getSimpleName();
//    public String ACTION_SHOW_TRANS = "showTransitions";
    private String sTitle;

    public GoogleMap mMap;
    //  enable disable menu...
    private Menu mMenu;
    private int mMaptype;
    private boolean bNewPathColor;

    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;
    // Tracks the bound state of the service.
    private boolean mBound = false;


    private float fZoom;

    ArrayList<Location_foo> mLocationArray = new ArrayList<>();
    private LatLng llWillo = new LatLng(37.760518, -122.408997);
    private Marker mrkWillo;

    Recognition mRecognition = null;

    public static final CharSequence[] MAP_TYPE_ITEMS =
            {"Normal", "Traffic", "Satellite", "Terrain", "Hybrid"};

    public MapFragment() {
        // Required empty public constructor
    }

    public MapFragment(ArrayList<Location_foo> locationArray){
        mLocationArray = locationArray;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
//        if (savedInstanceState != null){
//            appLog(TAG, " WVS onCreate() savedInstanceState != null");
//        } else {
//            appLog(TAG, " WVS onCreate() savedInstanceState == NULL");
//        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
//        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
//        appLog(TAG, " WVS onCreateView() ");
        View rootView = inflater.inflate(R.layout.fragment_map, container,false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
//                appLog(TAG, " WVS onMapReady() ");
                mMap = googleMap;
                InitializeMap(mMap);
//        if (mrkoptList != null) {
//            for (int i = 0; i < mrkoptList.size(); i++){
//                mMap.addMarker(mrkoptList.get(i));
//            }
//        }
//      first time go to Will spot
                if (mLocationArray != null && mLocationArray.size() == 0) {
                    if (fZoom == 0f) {
                        fZoom = Utils.getZoomLevel(getActivity());
//                        appLog(TAG, " WVS onMapReady() from Preferences fZoom ZERO to " + fZoom);
                    } else {
//                        appLog(TAG, " WVS onMapReady() previous fZoom = " + fZoom);
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(llWillo, fZoom));
                }
//                else d
                if (mMaptype == 0) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else {
                    mMap.setMapType(mMaptype);
                }
                mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
//                        appLog(TAG, " WVS ZOOM LEVEL updated Utils, CHANGED TO " + mMap.getCameraPosition().zoom);
                        fZoom = mMap.getCameraPosition().zoom;
                        Utils.setZoomLevel(getActivity(),fZoom);
                    }
                });
            }
        });


//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync((OnMapReadyCallback) this);

        //wvs#1 add "google_maps_key" to string resource
        //wvs#2
        mapFragment.setRetainInstance(true);

//        setHasOptionsMenu(true);
//        return super.onCreateView(inflater, container, savedInstanceState);
        return rootView;

    }

    @Override
    public void onStart() {
        super.onStart();
//        appLog(TAG, " WVS onStart() fZoom = " +fZoom);
        fZoom = Utils.getZoomLevel(getActivity());
//        appLog(TAG, " WVS onStart() from Utils fZoom = " +fZoom);
    }

    @Override
    public void onResume() {
        super.onResume();
//        appLog(TAG, " WVS onResume() fZoom = " + fZoom);
        if (Utils.getRequestingLocationUpdates(getContext())) {
            CheckGPSEnabled();
        }
//        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myReceiver, new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
//        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myReceiver, new IntentFilter(ACTION_SHOW_TRANS));
    }

    @Override
    public void onPause() {
//        appLog(TAG, " WVS onPause()");
        super.onPause();
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myReceiver);
    }

    @Override
    public void onStop() {
//        appLog(TAG, " WVS onStop()");
        super.onStop();
    }

    public void CheckGPSEnabled() {
        //https://stackoverflow.com/questions/24160472/show-popup-when-location-access-is-disable-by-user-andorid-google-maps
        // Make sure that GPS is enabled on the device
        LocationManager mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            showDialogGPS();
        }
    }
    //https://stackoverflow.com/questions/24160472/show-popup-when-location-access-is-disable-by-user-andorid-google-maps

    /**
     * Show a dialog to the user requesting that GPS be enabled
     */
    private void showDialogGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("Enable GPS");
        builder.setMessage("Please enable GPS");
//        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(
                        new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

//        // Inflate the menu; this adds items to the action bar if it is present.
//        inflater.inflate(R.menu.menu_main,menu);
//        this.mMenu = menu;
////        appLog(TAG, " WVS onCreateOptionsMenu mMenu = " + ((mMenu == null) ? "NULL" : "not NULL"));
//        setMenuState(Utils.getRequestingLocationUpdates(getActivity()));
        super.onCreateOptionsMenu(menu, inflater);
    }

//    private void setMenuState(boolean breqLocationUpdates) {
//
////        Boolean breqLocationUpdates = Utils.getRequestingLocationUpdates(this);
//        if (mMenu != null) {
//            MenuItem item = mMenu.findItem(R.id.activatelocationupdates);
//            item.setEnabled(!breqLocationUpdates);
//
//            MenuItem item2 = mMenu.findItem(R.id.deactivatelocationupdates);
//            item2.setEnabled(breqLocationUpdates);
//
////            MenuItem item3 = mMenu.findItem(R.id.getPreviousRoutes);
////            item3.setEnabled(breqLocationUpdates);
//        }
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }

    public void InitializeMap(final GoogleMap tmpMap) {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//          PERMISSION_DENIED...
            Toast.makeText(getActivity(), "onMapReady NO permission... will ask now?", Toast.LENGTH_LONG).show();
//          No explanation needed; request the permission
            ActivityCompat.requestPermissions(getActivity(), PERMISSION_GETMYLOCATION, REQUEST_GETMYLOCATION);
            return;
        }
//      on first install, and user grants permission, it will never get below... double check?
        tmpMap.setMyLocationEnabled(true);
        tmpMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                return false;
            }
        }); //wvs#3 requires implements com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListenter...

        tmpMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
           @Override
           public void onMyLocationClick(@NonNull Location location) {

           }
        });

//        mMap.getUiSettings().setZoomControlsEnabled(true);
//        mMap.getUiSettings().setCompassEnabled(true);
//        mMap.setPadding(0, 40, 0, 20);
        tmpMap.getUiSettings().setZoomControlsEnabled(true);
        tmpMap.getUiSettings().setCompassEnabled(true);
        tmpMap.setPadding(0,40,0,20);

        tmpMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                showMapTypeSelectorDialog(tmpMap, getContext(), mLocationArray);
            }
        });
    }
    public void onLocationChanged_redrawMap(Location location) {

        // New location has now been determined
        String msg = " **** onLocationChanged_redrawMap ***** " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude()) + "," +
                Double.toString(location.getAccuracy());
//      Accuracy reported by getAccuracy() is in meters, ex. 10 meters. Google defines accuracy as the radius of 68% confidence(on sigma),
//      which means if you get 10 meters of accuracy from getAccuracy(), then you are somewhere within 10 meters from the actual location with probability of 68/100 times.
//      sIf getAccuracy() reported is 20 meteres, then you are somewhere within 20 meters from actual location. So the smaller number reported by getAccuracy() means you are closer to the actual location.
        appLog(TAG, " WVS " + msg);
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

// ***************** ADD current location to DB *******************************************
        LocationRepo lr = new LocationRepo(getActivity());
//      get current locationsid
        long locationsid = Utils.getCurrentPathNameID(getActivity());
//      add locationsid to location
        Utils.setLocation_Extra_LocationsId(location, locationsid);
//      insert into tblLocation
        long locationid = lr.insert(location);
// ***************** ADD current location to array *******************************************
        mLocationArray = lr.getRouteDetailsByLocationsId(locationsid);

        if (mLocationArray.size() > 0) {
//            appLog(TAG, " WVS onLocationChanged_redrawMap LAST " + mLocationArray.size() + " " + mLocationArray.get(mLocationArray.size() - 1).getLatitude() + " " + mLocationArray.get(mLocationArray.size() - 1).getLongitude() + " color=" + getLocation_Color(mLocationArray.get(mLocationArray.size() - 1)));
//            appLog(TAG, " WVS onLocationChanged_redrawMap bNewPathColor = " + bNewPathColor);

            if (bNewPathColor) {
//              in case COLOR_IDS fails
                //ulatLng = new UberLatLng(latLng, mLocationArray.get(mLocationArray.size()-1).color, "new color");
                for (int i = 0; i < COLOR_IDS.length; i++) {
                    if (getLocation_Color(mLocationArray.get(mLocationArray.size() - 1)) == COLOR_IDS[i]) {

                        if (i == COLOR_IDS.length - 1) {
                            //ulatLng = new UberLatLng(latLng, COLOR_IDS[0], "new color");
                            setLocation_Color(location, COLOR_IDS[0]);
                        } else {
                            //ulatLng = new UberLatLng(latLng, COLOR_IDS[i+1], "new color");
                            setLocation_Color(location, COLOR_IDS[i + 1]);
                        }
//                        appLog(TAG, " WVS onLocationChanged_redrawMap SET bNewPathColor = false");
                        bNewPathColor = false;
                        break;
                    }
                }
            } else {
                //ulatLng = new UberLatLng(latLng, mLocationArray.get(mLocationArray.size() - 1).color, "same color path");
            }
        } else {
            //ulatLng = new UberLatLng(latLng, COLOR_IDS[0],"first coordinate" );
            setLocation_Color(location, COLOR_IDS[2]);
        }
        appLog(TAG, " WVS onLocationChanged_redrawMap CURR " + latLng.latitude + " " + latLng.longitude + " color=" + getLocation_Color(location));

        if (mMap != null) {
            Toast.makeText(getActivity(), mLocationArray.size() + " " + msg, Toast.LENGTH_SHORT).show();
            if (fZoom == 0f) {
                fZoom = Utils.getZoomLevel(getActivity());
            }
            mMap.clear();
            if (DrawPath(mLocationArray)) {
                appLog(TAG, " WVS DrawPath is successful");
            } else {
                appLog(TAG, " WVS DrawPath is NOT successful");
            }

//            fZoom = mMap.getCameraPosition().zoom;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, fZoom), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    appLog(TAG, " WVS onLocationChanged_redrawMap animateCamera onFinish");
                }

                @Override
                public void onCancel() {
                    appLog(TAG, " WVS onLocationChanged_redrawMap animateCamera onCancel set mLocationCallback = null");
                }
            });

//            else {
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng) );
//            }

        } else {
//            Toast.makeText(this, mlatLngs.size() + " Updated location mMap == NULL", Toast.LENGTH_SHORT).show();
            appLog(TAG, " WVS onLocationChanged_redrawMap mMap IS NULL");
        }
//        Utils.WithinGeoTarget(getContext(), locationsid, location, lr);

    }

//    public boolean DrawPath_LocationsId(Long locationsid){
//        Boolean bRet;
//
////        frag = (MapFragment) viewPager.getAdapter().instantiateItem(viewPager,0);
//
//        if (mMap!=null) {
//            mMap.clear();
//            LocationRepo lr = new LocationRepo(getActivity());
//            mLocationArray = lr.getRouteActivityByLocationsId(locationsid, getActivity());
//            bRet = DrawPath(mLocationArray);
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng( mLocationArray.get(0).getLatitude(), mLocationArray.get(0).getLongitude()), fZoom));
////                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(llWillo, fZoom));
//        } else {
//            bRet = false;
//        }
//        return bRet;
//    }
    public boolean DrawPath(ArrayList<Location_foo> tmpLocationArray) {
        boolean bretValue = false;
        PolylineOptions tmpPolyLineOpts;
        boolean bMapColorChange;
        boolean bIsPath;
        Location tmpLocation;
        int tmpColor;
        String sMarkerTitle;

        if (tmpLocationArray != null && tmpLocationArray.size() > 0) {

            tmpPolyLineOpts = new PolylineOptions();

            for (int i = 0; i < tmpLocationArray.size(); i++) {
                tmpLocation = tmpLocationArray.get(i);
                if (i == 0) {
                    bMapColorChange = false;
                    bIsPath = false;
                    tmpColor = getLocation_Color(tmpLocation);
                    tmpPolyLineOpts.color(tmpColor);
                    tmpPolyLineOpts.add(new LatLng(tmpLocation.getLatitude(), tmpLocation.getLongitude()));
                    sMarkerTitle = "Start: "+ ((Location_foo) tmpLocation).getDateTime();
                    AddMarker((Location_foo) tmpLocation,sMarkerTitle);
                } else {
                    tmpColor = getLocation_Color(tmpLocation);
                    if (((Location_foo) tmpLocation).getActivityId()!=null && ((Location_foo) tmpLocation).getActivityId() > 0){
                        if (((Location_foo) tmpLocation).getTransitionId() == ActivityTransition.ACTIVITY_TRANSITION_ENTER){
//                            ENTERING NEW TRANSISTION
//                            appLog(TAG, " WVS activity/transition = "+ ((Location_foo) tmpLocation).getActivityId() + "/" + ((Location_foo) tmpLocation).getTransitionId()+ ": " + ((Location_foo) tmpLocation).getDateTime() );

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
                        mMap.addPolyline(tmpPolyLineOpts);
                        bretValue = true;
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
                    mMap.addPolyline(tmpPolyLineOpts);
                    bretValue = true;
                }
                if (i == tmpLocationArray.size() - 1 ) {
                    sMarkerTitle = "End: "+ ((Location_foo) tmpLocation).getDateTime();
                    AddMarker((Location_foo) tmpLocation,sMarkerTitle);
                }
            }
        }

        return bretValue;
    }

    public void showDialogRoutes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Previous Routes");
//        builder.setMessage("Please select a route");  ??this hides list??

        LocationsRepo lsr = new LocationsRepo(getActivity());
        final ArrayList<String> mPrevRoutes = lsr.getPreviousRoutes();
        if (mPrevRoutes.size() == 0){
            builder.setTitle("No Previous Routes to review");
        }
//        final String[] cs = {"1","2","3","4"};
//        builder.setSingleChoiceItems(cs, 1, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                appLog(TAG, String.valueOf(which));
//            }
//        });
        CharSequence[] cs = mPrevRoutes.toArray(new CharSequence[mPrevRoutes.size()]);
        builder.setItems(cs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                appLog(TAG, String.valueOf(which));
                sTitle = mPrevRoutes.get(which).toString();
//              todo assign sTitle properly in MapFragment
//                txtStatus = (TextView) findViewById(R.id.txtStatus);
//                txtStatus.setText(sTitle);
                if (sTitle.indexOf(".") > 0) {
                    Long locationsId = Long.valueOf(sTitle.substring(0, sTitle.indexOf(".") ));
                    LocationRepo lr = new LocationRepo(getActivity());
//                    appLog(TAG, " WVS LocationsId = " + locationsId);
                    if (mMap!=null) {
                        mMap.clear();
//                    mLocationArray = lr.getRouteDetailsByLocationsId(locationsId);
                        mLocationArray = lr.getRouteActivityByLocationsId(locationsId, getActivity());
                        DrawPath(mLocationArray);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng( mLocationArray.get(0).getLatitude(), mLocationArray.get(0).getLongitude()), fZoom));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(llWillo, fZoom));
                    }
                }
            }
        });


//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
////                mPrevRoutes.get(which).toString();
//                appLog(TAG, String.valueOf(which));
//            }
//        });
//
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void AddMarker(Location_foo tmpLocation, String pTitle){
        if (mMap!=null) {
            MarkerOptions tmpMrkOpt = new MarkerOptions();
            tmpMrkOpt.position(new LatLng(tmpLocation.getLatitude(), tmpLocation.getLongitude()));
            tmpMrkOpt.title(pTitle);

            tmpMrkOpt.draggable(true);
            mMap.addMarker(tmpMrkOpt);
        }
    }
    @Override
    public void onMapClick(LatLng latLng) {
        Toast.makeText(getActivity(),"location:"+latLng.latitude,Toast.LENGTH_LONG).show();
    }

    public static void showMapTypeSelectorDialog(final GoogleMap tmpMap, Context context, ArrayList<Location_foo> tmpLocationArray) {
        // Prepare the dialog by setting up a Builder. previously in MainActivity.java
        WVS_MapHelper mh = new WVS_MapHelper();
        Float totaldistance = mh.Determine_Update_Distance(tmpLocationArray, context, false);

        final String fDialogTitle = "Select Map Type - distance = " + totaldistance.toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
        int checkItem=0; // = mMap.getMapType() - 1;
        if (tmpMap.isTrafficEnabled()){
            checkItem = 1;
        } else {
            switch (tmpMap.getMapType()){
                case GoogleMap.MAP_TYPE_NORMAL:
                    checkItem = 0;
                    break;
                case GoogleMap.MAP_TYPE_SATELLITE:
                    checkItem = 2;
                    break;
                case GoogleMap.MAP_TYPE_TERRAIN:
                    checkItem = 3;
                    break;
                case GoogleMap.MAP_TYPE_HYBRID:
                    checkItem = 4;
                    break;
            }
        }

//    MAP_TYPE_NONE = 0;
//    MAP_TYPE_NORMAL = 1;
//    MAP_TYPE_SATELLITE = 2;
//    MAP_TYPE_TERRAIN = 3;
//    MAP_TYPE_HYBRID = 4;

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 1:
                                tmpMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                tmpMap.setTrafficEnabled(true);
                                break;
                            case 2:
                                tmpMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                tmpMap.setTrafficEnabled(false);
                                break;
                            case 3:
                                tmpMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                tmpMap.setTrafficEnabled(false);
                                break;
                            case 4:
                                tmpMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                tmpMap.setTrafficEnabled(false);
                                break;
                            default:
                                tmpMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                tmpMap.setTrafficEnabled(false);
                        }
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }

}
