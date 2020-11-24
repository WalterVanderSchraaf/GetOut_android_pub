package com.endeavor.walter.getout9;

public class DBInfo {

    public static final String ALIAS = " c ";
    public static final String ALIASdot = " c.";
    public static final String KEY_Orderby="Orderby";
    public static final String KEY_Checked="Checked";

    public static final String TABLE_LOCATIONS ="tblLocations";
    public static final String TABLE_LOCATIONS_KEY_LocationsId ="LocationsId";
    public static final String TABLE_LOCATIONS_KEY_LoginId = "LoginId";
    public static final String TABLE_LOCATIONS_KEY_PathName = "PathName";
    public static final String TABLE_LOCATIONS_KEY_DateTime ="DateTime";
    public static final String TABLE_LOCATIONS_KEY_ZoomLevel ="ZoomLevel";
    public static final String TABLE_LOCATIONS_KEY_ZoomTo_Latitude ="ZoomToLatitude";
    public static final String TABLE_LOCATIONS_KEY_ZoomTo_Longitude ="ZoomToLongitude";
    public static final String TABLE_LOCATIONS_KEY_TotalTime ="Totaltime";
    public static final String TABLE_LOCATIONS_KEY_TotalDistance ="TotalDistance";
    public static final String TABLE_LOCATIONS_KEY_TotalSteps ="TotalSteps";
    public static final String TABLE_LOCATIONS_KEY_ElevationDifference ="ElevationDifference";
    public static final String TABLE_LOCATIONS_KEY_Move_Time = "MoveMinutes";
    public static final String TABLE_LOCATIONS_KEY_Heart_Intensity = "HeartIntensity";
    public static final String TABLE_LOCATIONS_KEY_Heart_Time = "HeartDuration";
    public static final String TABLE_LOCATIONS_KEY_Checked = KEY_Checked;


    public static final String TABLE_LOCATION = "tblLocation";
    public static final String TABLE_LOCATION_KEY_LocationId = "LocationId";
    public static final String TABLE_LOCATION_KEY_LocationItem ="LocationItem";
    public static final String TABLE_LOCATION_KEY_Latitude = "Latitude";
    public static final String TABLE_LOCATION_KEY_Longitude = "Longitude";
    public static final String TABLE_LOCATION_KEY_Altitude = "Altitude";
    public static final String TABLE_LOCATION_KEY_Activity ="ActivityId";
    public static final String TABLE_LOCATION_KEY_Transition ="TransitionId";
    public static final String TABLE_LOCATION_KEY_ColorPath ="ColorPath";
    public static final String TABLE_LOCATION_KEY_Accuracy = "Accuracy";
    public static final String TABLE_LOCATION_KEY_DateTime ="DateTime";
    public static final String TABLE_LOCATION_KEY_LocationsId =TABLE_LOCATIONS_KEY_LocationsId;

    public static final String TABLE_TLKPACTIVITY = "tlkpActivity";
    public static final String TABLE_TLKPACTIVITY_KEY_ID = "ActivityId";
    public static final String TABLE_TLKPACTIVITY_KEY_DESCRIPTION = "Description";

    public static final String TABLE_TLKPTRANSITION = "tlkpTransition";
    public static final String TABLE_TLKPTRANSITION_KEY_ID = "TransitionId";
    public static final String TABLE_TLKPTRANSITION_KEY_DESCRIPTION = "Description";

    public static final String TABLE_ACTIVITIES = "tblActivities";
    public static final String TABLE_ACTIVITIES_ID = "ActivitiesId";
    public static final String TABLE_ACTIVITIES_LocationsId = TABLE_LOCATIONS_KEY_LocationsId;
    public static final String TABLE_ACTIVITIES_ActivityId = TABLE_TLKPACTIVITY_KEY_ID;
    public static final String TABLE_ACTIVITIES_ElapsedRealTimeNanos = "ElapsedRealTimeNanos";
    public static final String TABLE_ACTIVITIES_TransitionId = TABLE_TLKPTRANSITION_KEY_ID;
    public static final String TABLE_ACTIVITIES_DateTime = "DateTime";
    public static final String TABLE_ACTIVITIES_Description = "Description";

    public static final String TABLE_TBLLOGIN = "tblLogin";
    public static final String TABLE_TBLLOGIN_KEY_ID = "LoginId";
    public static final String TABLE_TBLLOGIN_KEY_Name = "UserName";
    public static final String TABLE_TBLLOGIN_KEY_Email = "Email";

    public static final String TABLE_TBLPET = "tblPet";
    public static final String TABLE_TBLPET_KEY_ID = "LoginId";
    public static final String TABLE_TBLPET_KEY_Name = "Name";

    public static final String TABLE_TBLNOTES = "tblNotes";
    public static final String TABLE_TBLNOTES_KEY_NotesId = "NotesId";
    public static final String TABLE_TBLNOTES_KEY_Name = "NotesName";
    public static final String TABLE_TBLNOTES_KEY_Date = "NotesDate";

    public static final String TABLE_TBLNOTE = "tblNote";
    public static final String TABLE_TBLNOTE_KEY_Noteid = "NoteId";
    public static final String TABLE_TBLNOTE_KEY_NoteItem = "NoteItem";
    public static final String TABLE_TBLNOTE_KEY_Tag = "NoteTag";
    public static final String TABLE_TBLNOTE_KEY_Cost = "Cost";
    public static final String TABLE_TBLNOTE_KEY_Date = "NoteDate";
    public static final String TABLE_TBLNOTE_KEY_Checked = KEY_Checked;
    public static final String TABLE_TBLNOTE_KEY_Image = "Image";
    public static final String TABLE_TBLNOTE_KEY_NotesId = TABLE_TBLNOTES_KEY_NotesId;
    public static final String TABLE_TBLNOTE_KEY_LocationsId = TABLE_LOCATIONS_KEY_LocationsId;

    public static final String TABLE_TBLEVENTS = "tblEvents";
    public static final String TABLE_TBLEVENTS_KEY_EventId = "EventId";
    public static final String TABLE_TBLEVENTS_KEY_EventMessage = "EventMessage";
    public static final String TABLE_TBLEVENTS_KEY_EventMessageTag = "EventMessageTag";
    public static final String TABLE_TBLEVENTS_KEY_EventOccurance = "EventOccurance";
    public static final String TABLE_TBLEVENTS_KEY_EventRepeat = "EventRepeat";
    public static final String TABLE_TBLEVENTS_KEY_EventDate = "EventDate";
    public static final String TABLE_TBLEVENTS_KEY_EventType = "EventType";
    public static final String TABLE_TBLEVENTS_KEY_ToContact = "ToContact";
    public static final String TABLE_TBLEVENTS_KEY_NoteId = TABLE_TBLNOTE_KEY_Noteid;

    public static final String TABLE_TBLHEALTH = "tblHealth";
    public static final String TABLE_TBLHEALTH_KEY_HealthId = "Health_Id";
    public static final String TABLE_TBLHEALTH_KEY_Type = "Type";
    public static final String TABLE_TBLHEALTH_KEY_SourceName = "SourceName";
    public static final String TABLE_TBLHEALTH_KEY_SourceVersion = "SourceVersion";
    public static final String TABLE_TBLHEALTH_KEY_Device = "Device";
    public static final String TABLE_TBLHEALTH_KEY_Unit = "Unit";
    public static final String TABLE_TBLHEALTH_KEY_CreationDate = "CreationDate";
    public static final String TABLE_TBLHEALTH_KEY_StartDate = "StartDate";
    public static final String TABLE_TBLHEALTH_KEY_EndDate = "EndDate";
    public static final String TABLE_TBLHEALTH_KEY_Value = "Value";
    public static final String TABLE_TBLHEALTH_KEY_LoginId = "LoginId";

    public static final String TABLE_TBLSCRIPTS = "tblScripts";
    public static final String TABLE_TBLSCRIPTS_KEY_ScriptsId = "ScriptsId";
    public static final String TABLE_TBLSCRIPTS_KEY_ScriptName = "ScriptName";
    public static final String TABLE_TBLSCRIPTS_KEY_Bucket = "Bucket";
    public static final String TABLE_TBLSCRIPTS_KEY_BucketPrefix = "BucketPrefix";
    public static final String TABLE_TBLSCRIPTS_KEY_DownloadDate = "DownloadDate";
    public static final String TABLE_TBLSCRIPTS_KEY_ExecutionDate = "ExecutionDate";
    public static final String TABLE_TBLSCRIPTS_KEY_RowsAffected = "RowsAffected";

    public static final String TABLE_TBLGEOLOCATION = "tblGeoLocation";
    public static final String TABLE_TBLGEOLOCATION_KEY_Id = "GeoLocationId";
    public static final String TABLE_TBLGEOLOCATION_KEY_Name = "Name";
    public static final String TABLE_TBLGEOLOCATION_KEY_Latitude = "Latitude";
    public static final String TABLE_TBLGEOLOCATION_KEY_Longitude = "Longitude";
    public static final String TABLE_TBLGEOLOCATION_KEY_Altitude = "Altitude";
    public static final String TABLE_TBLGEOLOCATION_KEY_Enabled = "Enabled";
    public static final String TABLE_TBLGEOLOCATION_KEY_Within = "Within";

    public static final String TABLE_ZTBLGEOLOCATION = "ztblGeoLocation";
    public static final String TABLE_ZTBLGEOLOCATION_KEY_Id = "Id";
    public static final String TABLE_ZTBLGEOLOCATION_KEY_GeoLocationId = TABLE_TBLGEOLOCATION_KEY_Id;
    public static final String TABLE_ZTBLGEOLOCATION_KEY_LocationId = TABLE_LOCATION_KEY_LocationId;


//            CREATE_TABLE = "CREATE TABLE " + Note.TABLE  + "("
//                + Note.KEY_NoteId  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
//                + Note.KEY_NoteItem + " TEXT, "
//                + Note.KEY_Tag + " TEXT, "
//                + Note.KEY_Date + " TEXT, "
//                + Note.KEY_Checked + " INTEGER, "
//                + Note.KEY_NotesId + " INTEGER )";


}
