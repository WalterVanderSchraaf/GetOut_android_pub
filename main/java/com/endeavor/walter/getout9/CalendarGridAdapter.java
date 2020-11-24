package com.endeavor.walter.getout9;
//https://inducesmile.com/android/how-to-create-android-custom-calendar-view-with-events/

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarGridAdapter extends ArrayAdapter {
    private static final String TAG = CalendarGridAdapter.class.getSimpleName();
    private LayoutInflater mInflater;
    private List<Date> monthlyDates;
    private Calendar currentDate;
    private ArrayList<Event_Object> allEvents;
    public CalendarGridAdapter(Context context, List<Date> monthlyDates, Calendar currentDate, ArrayList<Event_Object> allEvents) {
        super(context, R.layout.calendar_day_layout);
        this.monthlyDates = monthlyDates;
        this.currentDate = currentDate;
        this.allEvents = allEvents;
        mInflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Date mDate = monthlyDates.get(position);
        Calendar dateCal = Calendar.getInstance();
        Calendar dateCalToday = Calendar.getInstance();
        dateCal.setTime(mDate);
        int cellDay = dateCal.get(Calendar.DAY_OF_MONTH);
        int cellMonth = dateCal.get(Calendar.MONTH) + 1; //zero based month
        int cellYear = dateCal.get(Calendar.YEAR);
//        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentYear = currentDate.get(Calendar.YEAR);
        View view = convertView;
        if(view == null){
            view = mInflater.inflate(R.layout.calendar_day_layout, parent, false);
        }
        if(cellMonth == currentMonth && cellYear == currentYear){
            view.setBackgroundColor(Color.parseColor("#FF5733"));  //brown orange - current month
        }else{
            view.setBackgroundColor(Color.parseColor("#cccccc"));  //grey - befor/after current month
        }
        if (cellDay == dateCalToday.get(Calendar.DAY_OF_MONTH) && cellMonth == dateCalToday.get(Calendar.MONTH) + 1 && cellYear == dateCalToday.get(Calendar.YEAR)){
            view.setBackgroundColor(Color.parseColor("#00E4F6"));//            today - turquoise 00E4F6, purple dec6ee
        }
        //Add day to calendar
        TextView cellNumber = (TextView)view.findViewById(R.id.calendar_date_id);
        cellNumber.setText(String.valueOf(cellDay));

        //Add events to the calendar
        LinearLayout wrapper = (LinearLayout) view.findViewById(R.id.event_wrapper);
        TextView eventIndicator = (TextView)view.findViewById(R.id.event_id);
        Calendar eventCalendar = Calendar.getInstance();
        String sMsg="";
        if (allEvents != null) {
//          wvs can be null when adding an event for the first time...
            for (int i = 0; i < allEvents.size(); i++) {
                if (WVS_Utils.IsDBStringDateEqualtoDate(allEvents.get(i).getDate(), mDate)) {
                    sMsg = allEvents.get(i).getMessage();
                    if (eventIndicator.getText().toString().equals("")) {
                        eventIndicator.setBackgroundColor(Color.parseColor("#FFFFFF")); //https://www.color-hex.com/color/ffffff swhite #FFFFFF, red fushia #FF4081, purple #610083
                        eventIndicator.setText(sMsg);
                    } else {
//                  wvs todo: if day event exists, add additional text box event
                        TextView tmpText = new TextView(getContext());
                        tmpText.setText(sMsg);
                        tmpText.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        tmpText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 6);
//                    tmpText.setHeight(10);
                        tmpText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        ((LinearLayout) wrapper).addView(tmpText);
                    }
                }

//            eventCalendar.setTime(convertDBdateStringToDate( allEvents.get(i).getDate()));
//            if(cellDay == eventCalendar.get(Calendar.DAY_OF_MONTH) && cellMonth == eventCalendar.get(Calendar.MONTH) + 1
//                    && cellYear == eventCalendar.get(Calendar.YEAR)){
//                eventIndicator.setBackgroundColor(Color.parseColor("#610083")); //red fushia #FF4081, purple #610083
//                eventIndicator.setText(allEvents.get(i).getMessage());
//                Log.i(TAG, "event found");
//            }
            }
        }
        return view;
    }
    @Override
    public int getCount() {
        return monthlyDates.size();
    }
    @Nullable
    @Override
    public Object getItem(int position) {
        return monthlyDates.get(position);
    }
    @Override
    public int getPosition(Object item) {
        return monthlyDates.indexOf(item);
    }
}

