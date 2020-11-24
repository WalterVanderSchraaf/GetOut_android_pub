package com.endeavor.walter.getout9;

import java.io.Serializable;

public class Event_Object implements Serializable {
    private int id;
    private String message, date, occurance, eventtype, tocontact, repeat, messagetag;
    private Integer mNoteId;

    public Event_Object(String message, String date) {
        this.message = message;
        this.date = date;
    }
    public Event_Object(int id, String message, String date, Integer noteid) {
        this.date = date;
        this.message = message;
        this.id = id;
        this.mNoteId = noteid;
    }
    public Event_Object(int id, String message, String date, String occurance, String eventtype, String tocontact, Integer noteid, String repeat, String messagetag) {
        this.message = message;
        this.messagetag = messagetag;
        this.date = date;
        this.occurance = occurance;
        this.eventtype = eventtype;
        this.tocontact = tocontact;
        this.id = id;
        this.mNoteId = noteid;
        this.repeat = repeat;
    }
    public int getId() {
        return id;
    }
    public void setId(int _id){
        this.id = _id;
    }
    public String getMessage() {
        return message;
    }
    public void setMessageTag(String tag){
        this.messagetag = tag;
    }
    public String getMessageTag() {
        return messagetag;
    }
    public void setMessage(String msg){
        this.message = msg;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String dte) {
        this.date = dte;
    }
    public String getOccurance(){return occurance;}
    public void setOccurance (String occurance) {this.occurance = occurance;}
    public String getEventType(){return eventtype;}
    public void setEventType (String eventtype) {this.eventtype = eventtype;}
    public String getToContact(){return tocontact;}
    public void setToContact (String tocontact) {this.tocontact = tocontact;}
    public Integer getmNoteId(){return mNoteId;};
    public void setmNoteId(Integer noteid) { this.mNoteId = noteid; }

public String getRepeat(){return repeat;}
    public void setRepeat (String repeat) {this.repeat = repeat;}

}
