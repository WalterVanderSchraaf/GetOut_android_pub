package com.endeavor.walter.getout9;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class NotesArrayAdapter extends ArrayAdapter<Notes> {
    private static final String TAG = "NotesArrayAdapter";
    private static Context mContext;

    private TextView txtNotesId, txtNotes, txtDate, txtRowId;
    private ArrayList<Notes> objA_Notes;

//Notes[]
    public NotesArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Notes> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.objA_Notes = objects;
    }

    @Override
    public int getCount() {
//        return super.getCount();
        return objA_Notes.size();
    }

    @Nullable
    @Override
    public Notes getItem(int position) {
//        return super.getItem(position);
        return this.objA_Notes.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View row = convertView;
        if (row == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            row = inflater.inflate(R.layout.listitem_notes, parent, false);
        } else {
            row = convertView;
        }

        // get item
        final Notes tmpnotes = getItem(position);

        txtNotes = row.findViewById(R.id.txtNotesItem);
        txtDate =  row.findViewById(R.id.txtDatetime);
        txtRowId = row.findViewById(R.id.txtRowId);

        txtRowId.setText(Integer.toString(position+1));  //possible format for 3 digits...

        txtNotes.setText(tmpnotes.getName());
        txtNotes.setTag(String.valueOf(tmpnotes.getNotesId()));

        txtDate.setText(tmpnotes.getDate());

        if (tmpnotes.getChecked()>0){
            txtNotes.setPaintFlags(txtNotes.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            txtNotes.setBackgroundColor(Color.YELLOW);
        } else {
            txtNotes.setPaintFlags(txtNotes.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            txtNotes.setBackgroundColor(Color.WHITE);
        }

        txtNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotesRepo nsr;
                nsr = new NotesRepo(mContext);

                TextView tmpTxt = (TextView) v.findViewById(R.id.txtNotesItem);
                if ((tmpTxt.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) == Paint.STRIKE_THRU_TEXT_FLAG) {
//              remove strikethru (txt.setPaintFlags(txt) & (~ Paint.STRIKE_THRU_TEXT_FLAG))
//              alternatively use  exclusive OR operator ^ instead of | with &(~) combination
//                    txtNote.setPaintFlags(txtNote.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG );
                    tmpTxt.setPaintFlags(tmpTxt.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    tmpTxt.setBackgroundColor(Color.WHITE);
                    tmpnotes.setChecked(0);
                    nsr.update(tmpnotes);

                } else {
//              add strikethru
                    tmpTxt.setPaintFlags(tmpTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tmpTxt.setBackgroundColor(Color.YELLOW);
                    tmpnotes.setChecked(1);
                    nsr.update(tmpnotes);
                }
                notifyDataSetChanged();
            }
        });
//        return super.getView(position, convertView, parent);
        return row;
    }
}
