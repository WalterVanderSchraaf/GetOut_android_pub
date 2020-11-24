package com.endeavor.walter.getout9;
//https://inducesmile.com/android/how-to-create-android-custom-calendar-view-with-events/

import android.content.Context;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.endeavor.walter.getout9.MainActivity.appEventOccurrance;
import static com.endeavor.walter.getout9.WVSInfo.CALENDAR_LONGCLICKTOADD;

import static com.endeavor.walter.getout9.WVSInfo.ADD_EVENT_TONOTE_REQUEST;
import static com.endeavor.walter.getout9.WVSInfo.EDIT_EVENT_INNOTE_REQUEST;
import static com.endeavor.walter.getout9.WVSInfo.EDIT_CALENDAR_EVENT;

import static com.endeavor.walter.getout9.WVSInfo.START_PICKER_TIME;

import static com.endeavor.walter.getout9.WVS_Utils.convertDateToString_datepart;
import static com.endeavor.walter.getout9.WVS_Utils.convertTimePicker_formatAMPM_To_DBdateString;
import static com.endeavor.walter.getout9.WVS_Utils.getCountOfChar;


public class CustomCalendarActivity2 extends AppCompatActivity {
    private static final String TAG = CustomCalendarActivity2.class.getSimpleName();
    final Context mContext = this;
    TextView Calendar_lbl;  // label -> Event type: Time info: Event info

    private ImageView previousButton, nextButton;
    private TextView displayMonthYear,txtSun;

    private GridView calendarGridView;
    private Button btnSave, btnDelete, btnCancel;
    private static final int MAX_CALENDAR_COLUMN = 42;
    private int mEventId;
    private SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    private CalendarGridAdapter mAdapter;

    private EventObjectRepo eor;

    List<Date> dayValueInCells;
    ArrayList<Event_Object> mEvents;
    Integer reqCode;
    Note tmpNote;
    int EventGridPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_custom_calendar2);
//        CalendarCustomView mView = (CalendarCustomView)findViewById(R.id.custom_calendar);
        previousButton = findViewById(R.id.previous_month);
        nextButton = findViewById(R.id.next_month);
        displayMonthYear = findViewById(R.id.display_current_date);
        btnSave = findViewById(R.id.btnSave_Calendar);
        btnCancel = findViewById(R.id.btnCancel_Calendar);
        btnDelete = findViewById(R.id.btnDelete_Calendar);
        calendarGridView = findViewById(R.id.calendar_grid);

        final GridView calendarGridView = findViewById(R.id.calendar_grid);
        Calendar_lbl = findViewById(R.id.txtStatus_Calendar);

        String sCALENDAREVENTS = getResources().getString(R.string.event_lbl);
        Intent intent1 = getIntent();
        if (intent1 != null) {
            Bundle extras = intent1.getExtras();
            if (extras != null && extras.containsKey("Note")) {
//              only coming from NoteAddActivity, obscelenceEventAddActivity...
                tmpNote = (Note) getIntent().getSerializableExtra("Note");
                if (tmpNote != null) {
                    TextView txtSunNoteId = findViewById(R.id.sun);
                    txtSunNoteId.setTag(tmpNote);
                    if (getIntent().getExtras().containsKey("calendar_lbl")) {
                        String sMsg = getIntent().getExtras().get("calendar_lbl").toString();
                        Calendar_lbl.setText(sMsg);
                    }
                }
            }
            if (extras != null && extras.containsKey("eventid")){
//              coming from AddEditEvent...
                mEventId = extras.getInt("eventid");
                if (getIntent().getExtras().containsKey("calendar_lbl")) {
                    String sMsg = getIntent().getExtras().get("calendar_lbl").toString();
                    Calendar_lbl.setText(sMsg);
                }
            }

            if (extras != null && extras.containsKey("requestCode")) {
                reqCode = extras.getInt("requestCode");
                if (reqCode == ADD_EVENT_TONOTE_REQUEST) {
                    btnSave.setVisibility(View.VISIBLE);
                    btnSave.setEnabled(false);
                    btnCancel.setVisibility(View.VISIBLE);
                    btnDelete.setVisibility(View.GONE);
                    setTitle("Add Event to note");
                }  else if (reqCode == EDIT_EVENT_INNOTE_REQUEST) {
                    btnSave.setVisibility(View.VISIBLE);
                    btnSave.setEnabled(false);
                    btnDelete.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.VISIBLE);
                    setTitle("Edit Event from note");
                } else if (reqCode == EDIT_CALENDAR_EVENT) {
                    btnDelete.setVisibility(View.GONE);
                    btnSave.setEnabled(false);
                    setTitle("Long click to edit event time");
                }
            }
        }

        setUpCalendarAdapter();
        setPreviousButtonClickEvent();
        setNextButtonClickEvent();

        calendarGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Date tmpDate = (Date) calendarGridView.getItemAtPosition(position);
                String sDateTime = convertDateToString_datepart(tmpDate);
//              wvs todo: add temp event to mEvents as place holder?
//              assumption only 1 at most mEvents.size <= 1
                TextView txtSunNoteId = findViewById(R.id.sun);
//                if (reqCode != null && (reqCode == EDIT_NOTE_ITEM_REQUEST || reqCode == ADD_NOTE_ITEM_REQUEST)) {}
                if(txtSunNoteId.getTag()!=null) { // represents coming from note add/edit...
                    if (mEvents!=null && mEvents.size()>0){
                        mEvents.get(0).setDate(sDateTime);  //WVS HINT: SET (0) VALUE AS TEMPORARY
                        mAdapter.notifyDataSetChanged();
    //                    view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
                    Note tmpNote;
                    tmpNote = (Note) txtSunNoteId.getTag();
//                    tmpMsg = ", NoteId =" + tmpNote.getNoteId() + ", " + tmpNote.getNoteItem(); // txtSunNoteId.getTag().toString();
//                  get specific event/alarm time and update tmpDate with time part, add Event ifno as extra (pass back to NoteAddActivity then upon Save create Event related to NoteId
//                  set time to current time as default
                    Intent intent1 = new Intent(mContext, DateTimePicker.class);
                    intent1.putExtra("Note",tmpNote);
                    intent1.putExtra("date", tmpDate);
                    startActivityForResult(intent1, START_PICKER_TIME);
                } else if (reqCode == EDIT_CALENDAR_EVENT) {
                    Intent intent1 = new Intent(mContext, DateTimePicker.class);
                    intent1.putExtra("date", tmpDate);
                    startActivityForResult(intent1, START_PICKER_TIME);
                }
//                Toast.makeText(context,"Long click " + tmpDate.toString() + tmpMsg , Toast.LENGTH_LONG).show();
                return true;
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = getIntent();
                setResult(RESULT_CANCELED,intent1 );
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              returned from Picker... returnIntent.putExtra("datetime", sdatetime);
                Intent intent1 = getIntent();
                String sContact = "";
                String sEventType = "";
                String sEventOccurance = "";
                String sEventRepeat = "";
                int iEventId=0;
                String sEventMsg = "";

                String sDBFormattedDateTime = "";
                String sEventTime = getTimeInfoFromCalendarLabel();
                if (intent1.getExtras().containsKey("datetime") ) {
//                  2019-07-09 03:19 PM
                    String sDatetime = intent1.getExtras().getString("datetime");
                    sDBFormattedDateTime = convertTimePicker_formatAMPM_To_DBdateString(sDatetime);
                } else {
//                  possible time did not change only appEventType...
//                  get datetime from Calendar_lbl  evettype: datetime: msg  must be converted from eventtype: datetime PM: msg to 2019-07-09 03:19:00
//                    sDBFormattedDateTime = WVS_Utils.convertTimePicker_formatAMPM_To_DBdateString(Calendar_lbl.getText().toString().substring(Calendar_lbl.getText().toString().indexOf(":")+1, Calendar_lbl.getText().toString().lastIndexOf(":")).trim());
                    if (isEventTime_datetime(sEventTime)){
                        sDBFormattedDateTime = convertTimePicker_formatAMPM_To_DBdateString(sEventTime);
                    } else {
                        sDBFormattedDateTime = getTimeFromCalendarLabel();
                    }
                }

                setResult(RESULT_OK,intent1 );
                finish();
            }
        });

    }


    private void setPreviousButtonClickEvent(){
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MONTH, -1);
                setUpCalendarAdapter();
            }
        });
    }
    private void setNextButtonClickEvent(){
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MONTH, 1);
                setUpCalendarAdapter();
            }
        });
    }
    private void setUpCalendarAdapter(){
//        List<Date> dayValueInCells = new ArrayList<Date>();
        dayValueInCells = new ArrayList<Date>();
        eor = new EventObjectRepo(mContext);
//        WVS TODO remove ADDD_NOTE_ITEM_REQUEST, EDIT_NOTE_ITEM_REQUEST
//        if (reqCode != null && (reqCode == EDIT_NOTE_ITEM_REQUEST || reqCode == ADD_NOTE_ITEM_REQUEST || reqCode == ADD_EVENT_TONOTE_REQUEST || reqCode == EDIT_EVENT_INNOTE_REQUEST)) {
        if (reqCode != null && reqCode == ADD_EVENT_TONOTE_REQUEST) {
//            should be no event
            mEvents = null;
        } else if (reqCode != null && reqCode == EDIT_EVENT_INNOTE_REQUEST) {
            mEvents = eor.getEventsByNoteId(tmpNote.getNoteId(), false);
//            mEvents =  eor.getEventsByNoteId(mEventId, false);
        } else if (reqCode == EDIT_CALENDAR_EVENT) {
//          getting singular event
            mEvents = eor.getEventsByEventId(mEventId);
        } else {
            mEvents = eor.getAllEvents();
        }
        Calendar mCal = (Calendar)cal.clone();
        mCal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfTheMonth = mCal.get(Calendar.DAY_OF_WEEK) - 1;
        mCal.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonth);
        while(dayValueInCells.size() < MAX_CALENDAR_COLUMN){
            dayValueInCells.add(mCal.getTime());
            mCal.add(Calendar.DAY_OF_MONTH, 1);
        }
        Log.d(TAG, "Number of date " + dayValueInCells.size());
        String sDate = formatter.format(cal.getTime());
//        WVS_Utils.convertDBdateStringToString_dateAMPM(sDate);
        displayMonthYear.setText(sDate);
        mAdapter = new CalendarGridAdapter(mContext, dayValueInCells, cal, mEvents);
        calendarGridView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == START_PICKER_TIME){
            if (resultCode == RESULT_OK) {

                Date tmpdate = (Date) data.getSerializableExtra("date");
                Intent returnIntent = getIntent();
                String datepart = new SimpleDateFormat("yyyy-MM-dd").format(tmpdate);  //remove time HH-mm
//            todo: test data.getextras().containsKey("time")
                String sdatetime = datepart + " " + data.getStringExtra("time");
                returnIntent.putExtra("datetime", sdatetime);

                updateCalendarlblEventTime(sdatetime);

                setResult(RESULT_OK, returnIntent);
                mAdapter.notifyDataSetChanged();
                btnSave.setEnabled(true);
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }


    public String getEventFromCalendarLabel(){
        //          label -> Event type: Time info [<Weekly>,time]: Event info OR "long click to add date/time"
        String label = Calendar_lbl.getText().toString();
        String sEventType = "";
        if (label.contains(":")) {
            sEventType = label.substring(0, label.indexOf(":")).trim();
        }
        return  sEventType;
    }

    public String getTimeInfoFromCalendarLabel(){
        //          label -> Event type: Time info [<Weekly>,time]: Event info OR "long click to add date/time"  Notification: OneTime, 2020-08-28 01:30 PM
        String label = Calendar_lbl.getText().toString();
        String sTimeInfo = "";
        if (label.contains(":")) {
            int pos1 = label.indexOf(":") + 1;
            if (label.contains(CALENDAR_LONGCLICKTOADD)) {
                // long click to add date/time: advisory
            } else {
                //long click to add date/time: <Weekly>,2020-08-19 09:20 AM [from addeditnote ":noteitem details"]
                int cnt = getCountOfChar(label, ':');
                if (cnt == 3) {
                    int pos2 = label.lastIndexOf(":");
                    sTimeInfo = label.substring(pos1, pos2);
                } else {
                    sTimeInfo = label.substring(pos1).trim();
                }
            }

        }
        return  sTimeInfo;
    }

    public String getTimeFromCalendarLabel(){
        //          label -> Event type: Time info [<Weekly>,time]: Event info OR "long click to add date/time"
        String sTimeInfo = getTimeInfoFromCalendarLabel();
        String sTime = "";
        if (sTimeInfo.contains(",") && !sTimeInfo.contains(CALENDAR_LONGCLICKTOADD)){
//            Weekly, 2020-08-20 04:10 PM
            sTime = sTimeInfo.substring(sTimeInfo.indexOf(",") + 1).trim();
        } else {
            sTime = sTimeInfo.trim();
        }
        sTime = convertTimePicker_formatAMPM_To_DBdateString(sTime);

        return  sTime;
    }

    public String getEventInfoFromCalendarLabel(){
        //          label -> Event type: Time info [<Weekly>,time]: Event info OR "long click to add date/time"
        String label = Calendar_lbl.getText().toString();
        int cnt = getCountOfChar(label,':');
        String sEventInfo = "";
        if (cnt == 3) {
            sEventInfo = label.substring(label.lastIndexOf(":") + 1).trim();
        }
        return  sEventInfo;
    }

    public Boolean isEventTime_datetime(String sEventTime){
        Boolean bRet = true;
        for (String stype: appEventOccurrance){
            if (sEventTime.contains(stype)){
                bRet = false;
                break;
            }
        }
        return bRet;
    }

    public void updateCalendarlblEventType(String snewEventType){
        //          label -> Event type: Time info [<Weekly>,time]: Event info
        String sEventTime =  getTimeInfoFromCalendarLabel();
        String sEventInfo = getEventInfoFromCalendarLabel();
        Calendar_lbl.setText(snewEventType + ": " + sEventTime + ": " + sEventInfo);
    }

    public void updateCalendarlblEventTime(String snewEventTime){
        //          label -> Event type: Time info [<Weekly>,time]: Event info
        String sEventType = getEventFromCalendarLabel();
        String sEventTime =  getTimeInfoFromCalendarLabel();
        String sEventInfo = getEventInfoFromCalendarLabel();

        if (sEventTime.contains(",")){
            String sOccurance = "";
            sOccurance = sEventTime.substring(0, sEventTime.indexOf(",")).trim();
            sEventTime = sOccurance + ", " + snewEventTime;
        } else {
            sEventTime = snewEventTime;
        }
        Calendar_lbl.setText(sEventType + ": " + sEventTime + ": " + sEventInfo);
    }

    public void updateCalendarlblEventOccurance(String snewEventOccuranc){
        //          label -> Event type: Time info [<Weekly>,time]: Event info
        String sEventType = getEventFromCalendarLabel();  //long click to add date/time: 2020-08-19 09:20 AM: advisory
        String sEventTime =  getTimeInfoFromCalendarLabel();
        String sEventInfo = getEventInfoFromCalendarLabel();
        String sTime = "";

        if (sEventTime.contains(",")){
            sTime = sEventTime.substring(sEventTime.indexOf(",") + 1).trim();
            sEventTime = snewEventOccuranc + ", " + sTime;
        } else {
            sEventTime = snewEventOccuranc + ", " + sEventTime;
        }
        Calendar_lbl.setText(sEventType + ": " + sEventTime + ": " + sEventInfo);
    }
}
