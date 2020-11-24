package com.endeavor.walter.getout9;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import static android.os.Environment.DIRECTORY_PICTURES;
import static com.endeavor.walter.getout9.WVS_Utils.RotateImage_toBitmap;
//import static com.endeavor.walter.notepad.WVS_Utils.RotateImage_toBitmap;


public class NoteArrayAdapter extends ArrayAdapter<Note> {
    private static final String TAG = NoteArrayAdapter.class.getSimpleName();
    private static Context context;

    private TextView txtNoteId, txtNote, txtAlarmDate, txtNotesId, txtTag, txtOrder, txtDate, txtCost;
    private ImageView imgNoteItem, imgAlarm;
    private ArrayList<Note> objA_Note;
    private ArrayList<Note> objA_Note_original;

    Boolean bHideTags;

    public NoteArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Note> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objA_Note = objects;
        this.objA_Note_original = new ArrayList<Note>();
        this.objA_Note_original.addAll(objects);
    }

    @Override
    public int getCount() {
        return objA_Note.size();
    }

    @Override
    public Note getItem(int position) {
        return this.objA_Note.get(position);
    }

    @Override
    public View getView(final int postion, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.note_listitem_copy, parent, false);
        } else {
            row = convertView;
        }
//        https://stackoverflow.com/questions/51652532/how-to-programmatically-add-constraint-layouts-to-a-scrollview
//        ConstraintLayout constraintLayout = row.findViewById(R.id.linearLayout2);

        // get item
        final Note tmpnote = getItem(postion);

        txtOrder = row.findViewById(R.id.txtOrder);
        txtOrder.setText(Integer.toString( postion + 1));
        imgNoteItem = (ImageView) row.findViewById(R.id.imgNoteItem);
        imgAlarm = (ImageView) row.findViewById(R.id.imgAlarm);
        txtDate = row.findViewById(R.id.txtDate2);
        txtCost = row.findViewById(R.id.note_listitem_txtCost);

        //        txtNoteId = (TextView) row.findViewById(R.id.txtNote2Id);
        txtNote = (TextView) row.findViewById(R.id.note_listitem_txtNote);
//        txtNote.setBackgroundColor(Color.CYAN);
        txtAlarmDate = (TextView) row.findViewById(R.id.txtDate2);
        txtTag = (TextView) row.findViewById(R.id.note_listitem_txtTag);

//        txtNotesId = (TextView) row.findViewById(R.id.txtNotes2Id);

//        txtNoteId.setText(String.valueOf(tmpnote.getNoteId()));
        txtNote.setText(tmpnote.getNoteItem());
        txtNote.setTag(tmpnote.getNoteId());

        txtDate.setText(WVS_Utils.convertDBdateStringToString_dateAMPM( tmpnote.getDate()));
        if (tmpnote.getCost() != null){
            txtCost.setText(tmpnote.getCost().toString());
        }

        //        txtAlarmDate.setText(tmpnote.getmDate());
        txtTag.setText(tmpnote.getTag());
        if (bHideTags != null && bHideTags){
            txtTag.setVisibility(View.INVISIBLE);
        } else {
            txtTag.setVisibility(View.VISIBLE);
        }

        if (tmpnote.getChecked()>0){
            txtNote.setPaintFlags(txtNote.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            txtNote.setBackgroundColor(Color.YELLOW);
        } else {
            txtNote.setPaintFlags(txtNote.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
//            txtNote.setBackgroundColor(Color.GRAY);
//            txtNote.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
            txtNote.setBackgroundColor(context.getResources().getColor(android.R.color.white));
        }

        String filenames = tmpnote.getImage();
        final String tmpFileArray[] = filenames.split(",");
        String primaryimage = tmpFileArray[0];

        if (primaryimage.length() != 0) {
            File tmpfilepath = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)) + "/" + primaryimage);
        //        File tmpfilepath = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES))+"/" + tmpProduct.getImage().toString() );
            if (tmpfilepath.exists()) {
                int rotateImage = WVS_Utils.getCameraPhotoOrientation(tmpfilepath.getAbsolutePath());
                Bitmap bmpRotate = RotateImage_toBitmap(tmpfilepath, rotateImage);
        //        https://stackoverflow.com/questions/2577221/android-how-to-create-runtime-thumbnail
                int THUMBNAIL_SIZE = 100;
                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(bmpRotate, THUMBNAIL_SIZE, THUMBNAIL_SIZE);

                imgNoteItem.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imgNoteItem.setImageBitmap(ThumbImage);
                imgNoteItem.setVisibility(View.VISIBLE);
//              adjust location of tag under imgNoteItem
//              https://stackoverflow.com/questions/51652532/how-to-programmatically-add-constraint-layouts-to-a-scrollview
//              Create ConstraintSet set Tag below Image
/*
                ConstraintSet constraintSet = new ConstraintSet();

                ConstraintLayout constraintLayout;
                constraintLayout = row.findViewById(R.id.linearLayout2);

                constraintSet.clone(constraintLayout);

                constraintSet.connect(R.id.imgNoteItem,ConstraintSet.END, R.id.txtDate2, ConstraintSet.START,0);
                constraintSet.connect(R.id.txtDate2, ConstraintSet.END,ConstraintSet.PARENT_ID, ConstraintSet.END, 4);
                constraintSet.connect(R.id.note_listitem_txtNote,ConstraintSet.END, R.id.imgNoteItem, ConstraintSet.START,0);
                constraintSet.connect(R.id.note_listitem_txtNote,ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START,70);
                constraintSet.connect(R.id.note_listitem_txtTag,ConstraintSet.TOP,R.id.imgNoteItem,ConstraintSet.BOTTOM,0) ;
                constraintSet.applyTo(constraintLayout);
*/
            } else {
                Log.i(TAG, " WVS IMAGE DOES NOT EXIST " + primaryimage);
            }
        } else {
//            hide image
            imgNoteItem.setVisibility(View.GONE);
//            adjust location of tag under note_listitem_txtNote
//        https://stackoverflow.com/questions/51652532/how-to-programmatically-add-constraint-layouts-to-a-scrollview
// Create ConstraintSet set Tag below Note item
/*
            ConstraintSet constraintSet = new ConstraintSet();

            ConstraintLayout constraintLayout;
            constraintLayout = (ConstraintLayout) row.findViewById(R.id.linearLayout2);

            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.note_listitem_txtTag,ConstraintSet.TOP,R.id.note_listitem_txtNote,ConstraintSet.BOTTOM,0);
            constraintSet.applyTo(constraintLayout);
*/
        }
//        txtNotesId.setText(String.valueOf(tmpnote.getNotesId()));

        EventObjectRepo eor;
        eor = new EventObjectRepo(context);
        int cnt = eor.getCountByNoteId(tmpnote.getNoteId(), false);
        if (cnt == 0){
            imgAlarm.setVisibility(View.GONE);
        }else{
            imgAlarm.setVisibility(View.VISIBLE);
        }

//WVS: hint: disable EditText so in NoteActivityFragment lstVGeneric.setOnItemClickListener will fire
//        EditText_disable(txtNote,true);

        imgNoteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG," WVS imgNoteItem click from Adapter ");
                        Log.i(TAG, " WVS imgImage was clicked - " + tmpnote.getImage());
                        String tmpImageFiles = tmpnote.getImage();
                        String tmpNoteItem = tmpnote.getNoteItem();

                        if (tmpImageFiles.length() != 0) {
                            Intent intent1 = new Intent(context , ViewImages.class);
                            intent1.putExtra("imagefiles", tmpImageFiles);
                            intent1.putExtra("noteitem", tmpNoteItem);
                            intent1.putExtra("Note", tmpnote);
                            context.startActivity(intent1);
                        }
            }
        });

        txtNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteRepo nr;
                nr = new NoteRepo(context);
//v.findViewById()
                TextView tmpTxt = (TextView) v.findViewById(R.id.note_listitem_txtNote);

                if ((tmpTxt.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) == Paint.STRIKE_THRU_TEXT_FLAG) {
                    tmpTxt.setPaintFlags(tmpTxt.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
//                    <color name="darker_gray">#aaa</color>
//                    tmpTxt.setBackgroundColor(Color.LTGRAY);
//                    tmpTxt.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                    tmpTxt.setBackgroundColor(context.getResources().getColor(android.R.color.white));
//                    tmpTxt.setBackgroundColor(android.R.color.darker_gray);
//                  Color.GRAY, LTGRAY vs. android:color/darker_gray
                    tmpnote.setChecked(0);
                    nr.update(tmpnote, false);
                } else {
//              add strikethru
                    tmpTxt.setPaintFlags(tmpTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tmpTxt.setBackgroundColor(Color.YELLOW);
                    tmpnote.setChecked(1);
                    nr.update(tmpnote, false);
                }
            }
        });

        return row;
    }


    // Filter Class  https://abhiandroid.com/ui/searchview
    public void filter_note(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());

        objA_Note.clear();
        if (charText.length() == 0) {
            objA_Note.addAll(objA_Note_original);
        } else {
            for (Note wp : objA_Note_original) {
                if (wp.getNoteItem().toLowerCase(Locale.getDefault()).contains(charText)) {
                    objA_Note.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void Toggle_Checks (boolean bHideChecks){

        objA_Note.clear();
        if (bHideChecks){
            for (Note noteitem : objA_Note_original){
                if (noteitem.getChecked() == 0) {
                    objA_Note.add(noteitem);
                }
            }
        } else {
            objA_Note.addAll(objA_Note_original);
        }
//        this.bHideTags
//          bHideTags
        notifyDataSetChanged();
    }
    public void ToggleTags(boolean bhide_tags){

        this.bHideTags = bhide_tags;
        notifyDataSetChanged();

    }

    public void ToggleChecksTags(boolean bHideChecks, boolean bHideTags){
        objA_Note.clear();
        if (bHideChecks){
            for (Note noteitem : objA_Note_original){
                if (noteitem.getChecked() == 0) {
                    objA_Note.add(noteitem);
                }
            }
        } else {
            objA_Note.addAll(objA_Note_original);
        }
        this.bHideTags = bHideTags;
        notifyDataSetChanged();

    }
}
