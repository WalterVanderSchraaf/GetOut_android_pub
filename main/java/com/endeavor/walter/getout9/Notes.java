package com.endeavor.walter.getout9;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.endeavor.walter.getout9.WVS_Utils.getDateTimeString_standard;

public class Notes implements Serializable {
    public static final String TAG = Notes.class.getSimpleName();//"Notes";


    private String mName,mDate,mOrderby;
    private Integer mNotesId;
    private Integer mChecked;

    public Notes(Integer notesid, String name, String date, String orderby, Integer checked){
        this.mNotesId = notesid;
        this.mName = name;
        this.mDate = date;  //DEFAULT ( DATETIME('NOW') )
        this.mOrderby = orderby;
        this.mChecked = checked; //todo: add parameter , Integer checked
    }
    public Integer getNotesId() {return mNotesId;}
    public void setNotesId(Integer notesid) {this.mNotesId = notesid;}

    public String getName() {return mName;}
    public void setName(String name) {this.mName = name;}

    public String getDate() {return mDate;}
    public void setDate_Now() {
//        String filedatepart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String datetime = getDateTimeString_standard();
        this.mDate = datetime;
    }
    public String getOrderby() {return mOrderby;}
    public void setOrderby(String orderby) {this.mOrderby = orderby;}

    public Integer getChecked() {return mChecked;}
    public void setChecked(Integer checked) {this.mChecked = checked;}



}
