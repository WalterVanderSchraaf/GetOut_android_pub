package com.endeavor.walter.getout9;

import android.content.Context;
import android.media.Image;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import static com.endeavor.walter.getout9.WVS_Utils.convertDBdateStringToString_dateAMPM;

public class EventArrayAdapter extends ArrayAdapter <Event_Object> {
    private static final String TAG = "EventArrayAdapter";
    private static Context context;

    private TextView txtEventMsg, txtEventDateTime;
    private Image imgEventType;
    private ArrayList<Event_Object> objA_Event;

    public EventArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList <Event_Object> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objA_Event = objects;
    }

    @Override
    public int getCount(){
        return objA_Event.size();
    }

    @Override
    public Event_Object getItem(int position) {
        return this.objA_Event.get(position);
    }

    @Override public View getView(final int postion, View convertView, ViewGroup parent){
        View row = convertView;
        if (row == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.listitem_event, parent, false);
        } else {
            row = convertView;
        }
        final Event_Object tmpEO = getItem(postion);
        txtEventMsg = row.findViewById(R.id.txtEventMsg);
        txtEventDateTime = row.findViewById(R.id.txtEventDateTime);

        txtEventMsg.setText(tmpEO.getMessage());
        txtEventDateTime.setText(convertDBdateStringToString_dateAMPM(tmpEO.getDate()));


        return row;
    }

}
