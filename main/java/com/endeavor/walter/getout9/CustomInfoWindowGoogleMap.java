package com.endeavor.walter.getout9;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindowGoogleMap(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.infowindow_route, null);

        TextView txtTitle = view.findViewById(R.id.txtTitle_route);
        TextView txtDatetime = view.findViewById(R.id.txtDatetime_route);
        TextView txtTotalTime = view.findViewById(R.id.txtTotalTime_route);
        ImageView imgMapLegend = view.findViewById(R.id.imgMapLegend);

        TextView txtTotalDistance = view.findViewById(R.id.txtTotalDistance_route);
        TextView txtSteps = view.findViewById(R.id.txtSteps_route);
        TextView txtElevation = view.findViewById(R.id.txtElevationDelta);
        TextView txtMoveMinutes = view.findViewById(R.id.txtMoveMinutes_route);
        TextView txtHeartPts = view.findViewById(R.id.txtHeartPts_route);
        TextView txtHeartMinutes = view.findViewById(R.id.txtHeartMinutes_route);

//        name_tv.setText(marker.getTitle());
//        details_tv.setText(marker.getSnippet());

        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();

//        int imageId = context.getResources().getIdentifier(infoWindowData.getImage().toLowerCase(),
//                "drawable", context.getPackageName());
//        imgMapLegend.setImageResource(imageId);
        if (infoWindowData != null) {
            txtTitle.setText(infoWindowData.getTitle());
            txtDatetime.setText(infoWindowData.getDateTime());
            txtTotalTime.setText(infoWindowData.getTotalTime());
            txtTotalDistance.setText(infoWindowData.getTotalDistance());
            txtSteps.setText(infoWindowData.getSteps());
            txtElevation.setText(infoWindowData.getElevation_meters());
            txtMoveMinutes.setText(infoWindowData.getMoveMinutes());
            txtHeartPts.setText(infoWindowData.getHeartpoints());
            txtHeartMinutes.setText(infoWindowData.getHeartMinutes());
        }
        return view;
    }

    public static class InfoWindowData {
        private String title;
        private String image;
        private String datetime;
        private String totaltime;
        private String totaldistance;
        private String steps;
        private String moveminutes;
        private String heartpoints;
        private String heartminutes;
        private String elevation_meters;
        private String elevation_feet;

        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getImage() {
            return image;
        }
        public void setImage(String image) {
            this.image = image;
        }
        public String getDateTime() {
            return datetime;
        }
        public void setDateTime(String datetime) {
            this.datetime = datetime.trim();
        }
        public String getTotalTime() {
            return totaltime;
        }
        public void setTotalTime(String totaltime) {
            this.totaltime = totaltime.trim();
        }
        public String getTotalDistance() {
            return totaldistance + " mi";
        }
        public void setTotalDistance(String totaldistance) {
            this.totaldistance = totaldistance.trim();
        }
        public String getSteps() {
            return steps + " steps";
        }
        public void setSteps(String steps) {
            this.steps = steps.trim();
        }
        public String getMoveMinutes() {
            return moveminutes.trim();
        }
        public void setMoveMinutes(String moveminutes) {
            this.moveminutes = moveminutes + " Move minutes";
        }

        public String getHeartpoints() {
            return heartpoints.trim();
        }
        public void setHeartPoints(String heartpoints) {
            this.heartpoints = heartpoints + " Heart points";
        }

        public String getHeartMinutes() {
            return heartminutes.trim();
        }
        public void setHeartMinutes(String heartminutes) {
            this.heartminutes = heartminutes + " Heart minutes";
        }

        public String getElevation_meters() {
            return elevation_meters.trim();
        }
        public void setElevation_meters(String elevation_meters) {
            this.elevation_meters = elevation_meters + " meters in elevation";
        }

        public String getElevation_feet() {
            return elevation_feet.trim();
        }
        public void setElevation_feet(String elevation_feet) {
            this.elevation_feet = elevation_feet + " ft in elevation";
        }
    }
}
