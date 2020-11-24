package com.endeavor.walter.getout9;

import com.google.android.gms.maps.model.LatLng;

public class WVSInfo {
    public static int EDIT_LOCATIONS_ITEM_REQUEST = 1;
    public static int DELETE_LOCATIONS_ITEM_REQUEST = 2;
    public static int ADD_GEOLOCATION_ITEM_REQUEST = 3;
    public static int RESULT_DELETED = 10;
    public static int RESULT_ERROR = 11;

    public static int ADD_NOTE_REQUEST = 20;
    public static int VIEW_NOTE_REQUEST = 21;
    public static int EDIT_NOTE_REQUEST = 22;


//    static final int VIEW_NOTE_REQUEST = 1;
    static final int VIEW_NOTE_ITEMS_REQUEST = 23;
//    static final int ADD_NOTE_REQUEST = 3;
    static final int ADD_NOTE_ITEM_REQUEST = 24;
    static final int START_CAMERA = 25;
    static final int EDIT_NOTE_ITEM_REQUEST = 26;
//    static final int VIEW_IMAGES = 27;
    static final int IMPORT_NOTE = 28;
    static final int IMPORT_IMAGE = 29;
    static final int IMPORT_IMAGE_ABOVEKITKAT = 35;

    static final int VIEW_CALENDAR = 30;
    static final int START_PICKER_TIME = 31;
    static final int ADD_EVENT_TONOTE_REQUEST = 32;
    static final int VIEW_EVENTS = 33;
    static final int EDIT_EVENT_INNOTE_REQUEST = 34;

//    static final int VIEW_CALENDAR_EVENTS = 36;
//    static final int EDIT_EVENT_FROMLIST_REQUEST = 37;

    static final int EDIT_CALENDAR_EVENT = 40;

    static final int START_PICKER_CONTACT = 50;
    static final int START_PICKER_EMAIL = 51;
    static final int IMPORT_DB = 52;

    static final int SORTBYNAME=40;
    static final int SORTBYDATE =41;
    static final int SORTBYCHECK=42;
    static final int SORTBYTAG=43;

    static final int RESULT_DELETE=50;

    static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;

    static public float ZOOM_1 = 18.0f;
    static public float ZOOM_2 = 16.0f;
    static public float ZOOM_3 = 10.0f;

    static final String EVENTTYPE_NOTIFICATION = "Notification";
    static final String EVENTTYPE_TEXTMSG = "Text Msg";
    static final String EVENTTYPE_EMAIL = "Email";
    static final String EVENTTYPE_SCHEDULE_NOTIFICATION = "Sched Notice";

    static final String EVENTOCCURRANCE_ONETIME = "OneTime"; //"OneTime", "Weekly", "Monthly", "Yearly"
    static final String EVENTOCCURRANCE_WEEKLY = "Weekly";
    static final String EVENTOCCURRANCE_MONTHLY = "Monthly";
    static final String EVENTOCCURRANCE_YEARLY = "Yearly";

    static final public String CALENDAR_LONGCLICKTOADD = "long click to add date/time";
    static final public String EVENT_DELETED = "event deleted";

    public static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_4, R.string.tab_text_5}; //R.string.tab_text_3, @StringRes... used to be in SectionsPagerAdapter
    public static LatLng llChateau = new LatLng(37.760504, -122.408504);

    static final public String MARKER_ENABLED = "(enabled)";
    static final public String MARKER_DISABLED = "(disabled)";

    static final public String type_STEP_COUNT_CUMULATIVE = "TYPE_STEP_COUNT_CUMULATIVE";
    static final public String type_DISTANCE_DELTA = "TYPE_DISTANCE_DELTA";
    static final public String type_HEART_POINTS = "TYPE_HEART_POINTS";
    static final public String type_MOVE_MINUTES = "TYPE_MOVE_MINUTES";
    static final public String type_LOCATION_SAMPLE = "TYPE_LOCATION_SAMPLE";
}
