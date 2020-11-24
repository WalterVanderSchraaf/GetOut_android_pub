package com.endeavor.walter.getout9;

import android.content.Intent;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.endeavor.walter.getout9.WVS_Utils.getDateTimeString_standard;

public class Note implements Serializable {
    public static final String TAG = Note.class.getSimpleName();//"Note";
//    public static final String TABLE = "tblNote";
//    public static final String ALIAS = " c ";
//    public static final String ALIASdot = " c.";
//    public static final String KEY_NoteId = "NoteId";
//    public static final String KEY_NoteItem ="NoteItem";
//    public static final String KEY_Tag = "Tag";
//    public static final String KEY_Date="Date";
//    public static final String KEY_Checked="Checked";
//    public static final String KEY_NotesId = "NotesId";

    private String mNoteItem,mDate, mNoteTag, mImage;
    private Integer mNoteId, mNotesId, mLocationsId;
    private String mCost;
    private Integer mChecked;

    public Note(Integer noteid, String noteitem, String date, String tag, Integer notesid, Integer checked, String image, String cost, Integer locationsid){
        this.mNoteId = noteid;
        this.mNoteItem = noteitem;
        this.mDate = date;
        this.mNoteTag = tag;
        this.mCost = cost;
        this.mNotesId = notesid;
        this.mChecked = checked;
        this.mImage = image;
        this.mLocationsId = locationsid;
    }

    public Integer getNoteId() {return mNoteId;}
    public void setNoteId(Integer noteid) {this.mNoteId = noteid;}

    public String getNoteItem() {return mNoteItem;}
    public void setNoteItem(String note) {this.mNoteItem = note;}

    public String getDate() {return mDate;}
    public void setDate_Now() {
//        String filedatepart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String datetime = getDateTimeString_standard();
        this.mDate = datetime;
    }

    public String getTag() {return mNoteTag;}
    public void setTag(String tag) {this.mNoteTag = tag;}

    public Integer getChecked() {return mChecked;}
    public void setChecked(Integer checked) {this.mChecked = checked;}

    public Integer getNotesId() {return mNotesId;}
    public void setNotesId(Integer notesid) {this.mNotesId = notesid;}

    public String getImage() {return mImage;}
    public void setImage(String image) {this.mImage = image;}

    public Integer getLocationsId() {return mLocationsId;}
    public void setLocationssId(Integer locationsid) {this.mLocationsId = locationsid;}

    public String getCost() {
//      wvs this column was add via alter table and thus values were null by default
        String strCost = "";
        if (this.mCost != null && ! this.mCost.equals("")){
            Float fltCost = Float.parseFloat(this.mCost);
            strCost = String.format("%.2f", fltCost);
        }
        return  strCost;
    };
    public void setCost(String cost){
        this.mCost = cost;
    }

}
