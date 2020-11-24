package com.endeavor.walter.getout9;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.endeavor.walter.getout9.MainActivity.appRouteChoices;
import static com.endeavor.walter.getout9.WVSInfo.ADD_GEOLOCATION_ITEM_REQUEST;
import static com.endeavor.walter.getout9.WVSInfo.DELETE_LOCATIONS_ITEM_REQUEST;
import static com.endeavor.walter.getout9.WVSInfo.EDIT_LOCATIONS_ITEM_REQUEST;
import static com.endeavor.walter.getout9.WVSInfo.RESULT_DELETED;
import static com.endeavor.walter.getout9.WVSInfo.RESULT_ERROR;
//import androidx.core.app.ActivityCompat;

public class LocationsAddEditDelete extends AppCompatActivity {
    // AppCompatActivity Base class for activities that use the support library action bar features. You can add an ActionBar to your activity when running on API level 7 or higher by extending this class for your activity and setting the activity theme to Theme.AppCompat or a similar theme
    public static String TAG = LocationsAddEditDelete.class.getSimpleName();
    int reqCode;
    Context mContext;
    Locations tmpLoc;
    TextView txtLocationsId;
    TextView txtRouteName;
    EditText edtRouteName;
    Button btnSave, btnCancel, btnDelete;
    LatLng mobjlatlng;
    Long mLocationsId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, " WVS onCreate()");
        setContentView(R.layout.activity_locations_addedit);
        mContext = this;

        txtLocationsId = findViewById(R.id.txtLocationsId);
        txtRouteName = findViewById(R.id.txtRouteName);
        edtRouteName = findViewById(R.id.edtRouteName);
        edtRouteName.setSelectAllOnFocus(true);
        btnCancel = findViewById(R.id.btnLocationsCancel);
        btnSave = findViewById(R.id.btnLocationsSave);
        btnDelete = findViewById(R.id.btnLocationsDelete);

//        get intent, locations
        final Intent intent2 = getIntent();
        reqCode = intent2.getExtras().getInt("requestCode");
//      wvs: hint: currently only time tmpNotes!=null is when adding a new note
        tmpLoc = (Locations) getIntent().getSerializableExtra("locations");

        if (reqCode == EDIT_LOCATIONS_ITEM_REQUEST){
            if (tmpLoc != null){
                txtLocationsId.setText(tmpLoc.getLocationsId().toString());
                txtRouteName.setText("Rename Route from '" + tmpLoc.getPathname() + "'");
                edtRouteName.setText(tmpLoc.getPathname());
                btnSave.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.INVISIBLE);
            }
        } else if (reqCode == DELETE_LOCATIONS_ITEM_REQUEST){
            if (tmpLoc != null){
                txtLocationsId.setText(tmpLoc.getLocationsId().toString());
                txtRouteName.setText("DELETE Route name" );
                edtRouteName.setText(tmpLoc.getPathname());
                btnSave.setVisibility(View.INVISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
            }
        } else if (reqCode == ADD_GEOLOCATION_ITEM_REQUEST){

            if (intent2.getExtras().containsKey("objlatlng")){
                mobjlatlng = (LatLng) intent2.getExtras().get("objlatlng");
                mLocationsId = intent2.getExtras().getLong("locationsid");
                if (mobjlatlng != null){
                    txtLocationsId.setText(mLocationsId.toString());
                    txtRouteName.setText("ADD GEO location" );
                    edtRouteName.setText("Delores Park");
                    btnSave.setVisibility(View.VISIBLE);
                    btnDelete.setVisibility(View.INVISIBLE);
                }
            }

        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationsRepo lsr = new LocationsRepo(mContext);
                if (reqCode == EDIT_LOCATIONS_ITEM_REQUEST){
                    String routename = edtRouteName.getText().toString();
                    if( !tmpLoc.getPathname().equals(routename)){
                        tmpLoc.setPathname(routename);
                        lsr.updateMoveZoomTo(tmpLoc);
                        intent2.putExtra("routename", routename);
                        setResult(RESULT_OK, intent2);
                        finish();
                    }
                } else if (reqCode == ADD_GEOLOCATION_ITEM_REQUEST){
                    String geoname = edtRouteName.getText().toString();
                    lsr.insertGeoLocation(geoname, mobjlatlng);
                    setResult(RESULT_OK, intent2);
                    finish();
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED, intent2);
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationsRepo lsr = new LocationsRepo(mContext);
                LocationRepo lr = new LocationRepo(mContext);
                int iRes = lr.deleteRoutebyLocationdSId(tmpLoc.getLocationsId());
                if (iRes >= 0) {
                    lsr.delete(tmpLoc.getLocationsId());
                    NoteRepo nr = new NoteRepo(mContext);
                    nr = new NoteRepo(mContext);
                    Note tmpnote = nr.getNoteByLocationsId(tmpLoc.getLocationsId());
                    if (tmpnote != null) {
                        NoteActivityFragment.Delete_Ind_Note_Event_Image(tmpnote, mContext);
                    }
                    setResult(RESULT_DELETED, intent2);
                } else {
                    setResult(RESULT_ERROR, intent2);
                }
                finish();
            }
        });

        txtRouteName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reqCode == EDIT_LOCATIONS_ITEM_REQUEST){
                    String routename = edtRouteName.getText().toString();
                    showDialogNewRoute(routename);
                }
            }
        });
    }

    public void showDialogNewRoute(String currentName){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("current name '" + currentName + "'");
//       final String[] choices = {"Walk", "Dog Walk", "Run", "Going Out", "To Gym", "Pickup"};
        final String[] choices_stock = appRouteChoices;
        LocationsRepo lrs = new LocationsRepo(mContext);
        ArrayList<String> dbChoices = new ArrayList<>();
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
            if ( choices[i].equals(currentName)){
                indx = i;
                break;
            }
        }

        builder.setSingleChoiceItems(choices, indx, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pathname = choices[which];
                edtRouteName.setText(pathname);
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
