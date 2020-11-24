package com.endeavor.walter.getout9;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_PICTURES;
import static com.endeavor.walter.getout9.WVS_Utils.RotateImage_toBitmap;

public class LocationsArrayAdapter extends ArrayAdapter<Locations> {

    private static String TAG = LocationsArrayAdapter.class.getSimpleName();
    private static Context context;
    private ArrayList<Locations> objALocations;


    public LocationsArrayAdapter(@androidx.annotation.NonNull Context context, int resource, @androidx.annotation.NonNull ArrayList<Locations> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objALocations = objects;
    }

    @Override
    public int getCount() {
//        return super.getCount();
        return this.objALocations.size();
    }

    @Nullable
    @Override
    public Locations getItem(int position) {
//        return super.getItem(position);
        return this.objALocations.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);

        TextView txtPathname, txtDateTime, txtTotalTime, txtUser, txtSteps, txtDistance, txtMoveMinutes, txtHeartPts, txtHeartMinutes;
        ImageView imgNoteItem;

        View row = convertView;
        if (row == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.listitem_locations, parent, false);
        } else {
            row = convertView;
        }

        // get item
        final Locations tmpLocations = getItem(position);

        TextView txtLocationsId = row.findViewById(R.id.txtLocationdId);
        txtPathname = row.findViewById(R.id.txtPathname);
        txtTotalTime = row.findViewById(R.id.txtTotalTime);
        txtDateTime = row.findViewById(R.id.txtDatetime);
        txtUser = row.findViewById(R.id.txtUser);
        txtSteps = row.findViewById(R.id.txtSteps_route);
        txtDistance = row.findViewById(R.id.txtDistance);
        imgNoteItem = row.findViewById(R.id.imgLocationsNoteItem);
        txtMoveMinutes = row.findViewById(R.id.txtMoveMinutes);
        txtHeartPts = row.findViewById(R.id.txtHeartPts);
        txtHeartMinutes = row.findViewById(R.id.txtHeartMinutes);


//        txtLocationsId.setText(String.format(objALocations.get(position).getLocationsId().toString(), "%1$02d"));
//        txtLocationsId.setText(((objALocations.get(position).getLocationsId() < 10) ? " " + objALocations.get(position).getLocationsId().toString() : objALocations.get(position).getLocationsId().toString())) ;
//        txtPathname.setText(objALocations.get(position).getPathname());
        txtLocationsId.setText(((tmpLocations.getLocationsId() < 10) ? " " + tmpLocations.getLocationsId().toString() : tmpLocations.getLocationsId().toString())) ;
        txtPathname.setText(tmpLocations.getPathname());


        if (tmpLocations.getChecked()>0){
            txtPathname.setPaintFlags(txtPathname.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            txtPathname.setBackgroundColor(Color.YELLOW);
        } else {
            txtPathname.setPaintFlags(txtPathname.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            txtPathname.setBackgroundColor(Color.WHITE);
        }

        txtPathname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationsRepo lsr = new LocationsRepo(context);

                if ((txtPathname.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) == Paint.STRIKE_THRU_TEXT_FLAG){
                    txtPathname.setPaintFlags(txtPathname.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    txtPathname.setBackgroundColor(Color.WHITE);
//                    objALocations.get(position).setChecked(0);
                    tmpLocations.setChecked(0);
//                    lsr.updateCheckedLocationsId(objALocations.get(position).getLocationsId(), 0);
                    lsr.updateCheckedLocationsId(tmpLocations.getLocationsId(), 0);
                } else {
                    //              add strikethru
                    txtPathname.setPaintFlags(txtPathname.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    txtPathname.setBackgroundColor(Color.YELLOW);
//                    objALocations.get(position).setChecked(1);
                    tmpLocations.setChecked(1);
                    lsr.updateCheckedLocationsId(tmpLocations.getLocationsId(), 1);
                }
            }
        });

//        String totaltime = objALocations.get(position).getTotalTime();
        String totaltime = tmpLocations.getTotalTime();
        if (totaltime != null) {
//            Log.i(TAG, " WVS totaltime = " + totaltime + " len=" + totaltime.length());
            String formattedtotaltime = WVS_Utils.ReformatTimeStringForTextView(totaltime);
            txtTotalTime.setText(Html.fromHtml(formattedtotaltime));
        } else {
            txtTotalTime.setText("             ");
        }
//        String datetime = new SimpleDateFormat("MM-dd-yy hh:mm a").format(WVS_Utils.convertDBdateStringToDate_datetime(objALocations.get(position).getDatetime()) );
        String datetime = new SimpleDateFormat("MM-dd-yy hh:mm a").format(WVS_Utils.convertDBdateStringToDate_datetime(tmpLocations.getDatetime()) );
// WVS todo: convertDBdateStringToDate_dateAMPM()
        String formatteddatetime = ((datetime.substring(0,1).equals("0")) ? " " : datetime.substring(0,1)) + datetime.substring(1,9) +
                                   ((datetime.substring(9,10).equals("0")) ? "  " : datetime.substring(9,10))  + datetime.substring(10);
        txtDateTime.setText(formatteddatetime);
//        int idx = objALocations.get(position).getUserId();
        int idx = tmpLocations.getUserId();
//        String sUser = MainActivity.applogins[objALocations.get(position).getUserId()];
        String sUser = MainActivity.applogins[tmpLocations.getUserId()];
        txtUser.setText(sUser);

        txtDistance.setText(String.format("%3.2f", tmpLocations.getTotalDistance()) );  //String.format("%.2f", this.
        txtSteps.setText(String.format("%5d", tmpLocations.getTotalSteps()));
        txtMoveMinutes.setText(String.format("%3d", tmpLocations.getMoveDuration()));
        txtHeartPts.setText(String.format("%3.0f", tmpLocations.getHeartIntensity()));
        txtHeartMinutes.setText(String.format("%3d", tmpLocations.getHeartDuration()));

        String filenames = tmpLocations.getmNoteImage();
        if (filenames == null){
            filenames="";
        }
        final String tmpFileArray[] = filenames.split(",");
        String primaryimage = tmpFileArray[0];

        if (primaryimage.length() != 0) {
            File tmpfilepath = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)) + "/" + primaryimage);
            if (tmpfilepath.exists()) {
                int rotateImage = WVS_Utils.getCameraPhotoOrientation(tmpfilepath.getAbsolutePath());
                Bitmap bmpRotate = RotateImage_toBitmap(tmpfilepath, rotateImage);
//              https://stackoverflow.com/questions/2577221/android-how-to-create-runtime-thumbnail
                int THUMBNAIL_SIZE = 100;
                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(bmpRotate, THUMBNAIL_SIZE, THUMBNAIL_SIZE);

                imgNoteItem.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imgNoteItem.setImageBitmap(ThumbImage);
                imgNoteItem.setVisibility(View.VISIBLE);
            } else {
                Log.i(TAG, " WVS IMAGE DOES NOT EXIST " + primaryimage);
            }
        } else {
//            hide image
            imgNoteItem.setVisibility(View.GONE);
        }

        imgNoteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG," WVS imgNoteItem click from Adapter ");
                Log.i(TAG, " WVS imgImage was clicked - " + tmpLocations.getmNoteImage());
                String tmpImageFiles = tmpLocations.getmNoteImage();

                if (tmpImageFiles.length() != 0) {
                    Intent intent1 = new Intent(context , ViewImages.class);
                    intent1.putExtra("imagefiles", tmpImageFiles);
                    context.startActivity(intent1);
                }
            }
        });

        return row;
    }
}
