package com.endeavor.walter.getout9;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.OutputStreamWriter;
import java.util.ArrayList;

import static com.endeavor.walter.getout9.WVSInfo.DELETE_LOCATIONS_ITEM_REQUEST;
import static com.endeavor.walter.getout9.WVSInfo.EDIT_LOCATIONS_ITEM_REQUEST;

public class LocationsFragment extends Fragment {
    public static String TAG = LocationsFragment.class.getSimpleName();
    //wvs hint: https://stackoverflow.com/questions/35286670/how-to-refresh-a-fragment-from-an-activity-in-android#35305687
    private boolean shouldRefreshOnResume = false;

    static ArrayList<Locations> objA_Locations;
//    ArrayList<Locations> objA_Locations;
    ArrayList<Locations> objA_tmpLocations;
//    public LocationsArrayAdapter genericAdapter;
    public ListView lstVGeneric;
    LocationsArrayAdapter genericAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_locations ,container,false);
        Log.i(TAG, " WVS onCreateView()");
        objA_Locations = new ArrayList<Locations>();
        LocationsRepo lsr = new LocationsRepo(getActivity());

        objA_Locations = lsr.getPathNamesForList();

        lstVGeneric = rootView.findViewById(R.id.lstVfragment_locations);

        genericAdapter = new LocationsArrayAdapter(getActivity(), R.layout.listitem_locations, objA_Locations );
        lstVGeneric.setAdapter(genericAdapter);
        genericAdapter.notifyDataSetChanged();

        //wvs hint:
        //            https://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view
        //            use this code to use any type of child view inside a ScrollView & perform Touch operations.
        lstVGeneric.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
//java.lang.RuntimeException: Don't call setOnClickListener for an AdapterView. You probably want setOnItemClickListener instead
//        lstVGeneric.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        lstVGeneric.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Locations tmpLocs = objA_Locations.get(position);
                Long locationsid = tmpLocs.getLocationsId();
                Intent intent1 = new Intent(getContext(), LocationsActivity.class);
                intent1.putExtra("locationsid",locationsid);
                intent1.putExtra("locationstitle", tmpLocs.get_Title());
                startActivity(intent1);
            }
        });

        lstVGeneric.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Locations tmpLoc = objA_Locations.get(position);
                showDialogEditDeleteRoute(tmpLoc);

//                return false;
                return true; // wvs#hint this will fire only Long not regular
            }
        });

        shouldRefreshOnResume = false;
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, " WVS onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.i(TAG, " WVS onResume()");
//        Log.i(TAG," WVS onResume() ##4 , shouldRefreshOnResume="+shouldRefreshOnResume);
        // Check should we need to refresh the fragment
        if(shouldRefreshOnResume){
            // refresh fragment wvs hint: https://stackoverflow.com/questions/20702333/refresh-fragment-at-reload#20702418
            Log.i(TAG,"onResume ##4 refreshing fragment");
//            android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            androidx.fragment.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
        shouldRefreshOnResume = false;
//        Log.i(TAG," WVS onResume ##4 , reset shouldRefreshOnResume="+shouldRefreshOnResume);

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, " WVS onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, " WVS onStop()");
//        Log.i(TAG," WVS onStop ##6 , shouldRefreshOnResume="+shouldRefreshOnResume);
        shouldRefreshOnResume = true;
//        Log.i(TAG," WVS onStop ##6 , reset shouldRefreshOnResume="+shouldRefreshOnResume);
    }

    private void showDialogEditDeleteRoute(final Locations tmpLoc){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(tmpLoc.getPathname());
        final String[] choices = {"Rename", "Delete"};

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
                if (which == 0){
                    Log.i(TAG," WVS Rename route");
//                    showDialogChangeName(tmpLoc);
//                  launch LocationsAddEditDelete.class
                    Intent intent1 = new Intent(getActivity(), LocationsAddEditDelete.class);
                    intent1.putExtra("locations", tmpLoc);
                    intent1.putExtra("requestCode",EDIT_LOCATIONS_ITEM_REQUEST);
                    startActivityForResult(intent1,EDIT_LOCATIONS_ITEM_REQUEST);

                } else if (which== 1) {
                    Log.i(TAG," WVS Delete route");
                    Intent intent1 = new Intent(getActivity(), LocationsAddEditDelete.class);
                    LocationsRepo lsr = new LocationsRepo(getActivity());

                    intent1.putExtra("locations", tmpLoc);
                    intent1.putExtra("requestCode",DELETE_LOCATIONS_ITEM_REQUEST);
                    startActivityForResult(intent1,DELETE_LOCATIONS_ITEM_REQUEST);
                }
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showDialogChangeName(final Locations tmpLoc) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.edit_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle("Rename route");
        dialogBuilder.setMessage(tmpLoc.getPathname());
        edt.setText(tmpLoc.getPathname());
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
//                LocationsRepo lsr = new LocationsRepo(getActivity());
//                Long locationsid = tmpLoc.getLocationsId();
//                lsr.update(locationsid, edt.getText().toString(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//                objA_Locations = lsr.getPathNamesForList();
//                genericAdapter.notifyDataSetChanged();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, " WVS onActivityResult()");
    }

    public void RefreshList(){
        LocationsRepo lsr = new LocationsRepo(getActivity());
        objA_Locations = lsr.getPathNamesForList();
//        genericAdapter.notifyDataSetChanged();
//        Log.i(TAG, " WVS onPageSelected) AFTER refresh objA_Locations.size="+ objA_Locations.size());
//        shouldRefreshOnResume = true;
        // refresh fragment wvs hint: https://stackoverflow.com/questions/20702333/refresh-fragment-at-reload#20702418
        Log.i(TAG," WVS RefreshList() refreshing fragment");
//            android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        androidx.fragment.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    public void ConnectRoutes(){
//        review checked routes locations.getchecked and join
        LocationsRepo lsr = new LocationsRepo(getActivity());
        int iRet = lsr.ConnectRoutes_basedonChecked();
        if (iRet == -1){
            Toast.makeText(getContext(), "Select two routes to connect", Toast.LENGTH_LONG).show();
        }

    }

}
