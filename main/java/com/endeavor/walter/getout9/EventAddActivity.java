package com.endeavor.walter.getout9;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.endeavor.walter.getout9.MainActivity.appEventOccurrance;
import static com.endeavor.walter.getout9.MainActivity.appEventType;
import static com.endeavor.walter.getout9.WVSInfo.ADD_EVENT_TONOTE_REQUEST;

import static com.endeavor.walter.getout9.WVSInfo.CALENDAR_LONGCLICKTOADD;
import static com.endeavor.walter.getout9.WVSInfo.EDIT_CALENDAR_EVENT;
import static com.endeavor.walter.getout9.WVSInfo.EDIT_EVENT_INNOTE_REQUEST;

import static com.endeavor.walter.getout9.WVSInfo.EVENTOCCURRANCE_ONETIME;
import static com.endeavor.walter.getout9.WVSInfo.EVENTOCCURRANCE_WEEKLY;
import static com.endeavor.walter.getout9.WVSInfo.EVENTTYPE_EMAIL;
import static com.endeavor.walter.getout9.WVSInfo.EVENTTYPE_NOTIFICATION;
import static com.endeavor.walter.getout9.WVSInfo.EVENTTYPE_SCHEDULE_NOTIFICATION;
import static com.endeavor.walter.getout9.WVSInfo.EVENTTYPE_TEXTMSG;
import static com.endeavor.walter.getout9.WVSInfo.RESULT_DELETE;
import static com.endeavor.walter.getout9.WVSInfo.START_PICKER_CONTACT;
import static com.endeavor.walter.getout9.WVSInfo.START_PICKER_EMAIL;
import static com.endeavor.walter.getout9.WVSInfo.START_PICKER_TIME;
import static com.endeavor.walter.getout9.WVSInfo.VIEW_CALENDAR;
import static com.endeavor.walter.getout9.WVSInfo.VIEW_EVENTS;
import static com.endeavor.walter.getout9.WVS_Utils.convertDBdateStringToString_dateAMPM;
import static com.endeavor.walter.getout9.WVS_Utils.convertTimePicker_formatAMPM_To_DBdateString;
import static com.endeavor.walter.getout9.WVS_Utils.isDateValid;
import static com.endeavor.walter.getout9.WVS_Utils.translateWeekdayNumbers;


public class EventAddActivity extends AppCompatActivity {
    public final static String TAG = EventAddActivity.class.getSimpleName();
    final Context context = this;
    Integer reqCode;
    Spinner spnEventOccurance, spnEventType;
    TextView lblAddres, txtEventDate, lblEventDate_tagNote, lblDate_tagEventId, txtEventId, txtNoteId, Calendar_lbl;  // label -> Event type: Time info: Event info;
    EditText edtEventMessage, edtEventMessageTag, edtContact;
    Button btnSave, btnCancel, btnDelete;
    CheckBox chkSun, chkMon, chkTue, chkWed, chkThu, chkFri, chkSat;
    LinearLayout layoutDays;
    ArrayList<Event_Object> mEvents;
    Event_Object eo;
    String sEventType, sEventOccurance;
    EventObjectRepo eor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_item_add);

        //  wvs todo determine if new androidx replace old code below
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_event);
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//      lbl is used to setTag() to EventId
        lblDate_tagEventId = findViewById(R.id.EventId_lbl);
        edtEventMessage = findViewById(R.id.edtEventMessage);
        edtEventMessageTag = findViewById(R.id.edtEventMessageTag);
        spnEventOccurance = findViewById(R.id.spnEventOccurance);
        lblEventDate_tagNote = findViewById(R.id.txtEventDatelbl);
        txtEventDate = findViewById(R.id.txtEventDate);
        spnEventType = findViewById(R.id.spnEventType);
        lblAddres = findViewById(R.id.edtEventToContactlbl);
        edtContact = findViewById(R.id.edtEventToContact);
        btnSave = findViewById(R.id.btnEventSave);
        btnCancel =findViewById(R.id.btnEventCancel);
        btnDelete = findViewById(R.id.btnEventDelete);

        txtEventId = findViewById(R.id.txtEventEventId);
        txtNoteId = findViewById(R.id.txtEventNoteId);

        layoutDays = findViewById(R.id.layout_Days);
        chkSun = findViewById(R.id.chkSun);
        chkMon = findViewById(R.id.chkMon);
        chkTue = findViewById(R.id.chkTue);
        chkWed = findViewById(R.id.chkWed);
        chkThu = findViewById(R.id.chkThu);
        chkFri = findViewById(R.id.chkFri);
        chkSat = findViewById(R.id.chkSat);

        Calendar_lbl = findViewById(R.id.txtStatus_Calendar);

        Intent intent1 = getIntent();
        Bundle extras = intent1.getExtras();



        if (extras.containsKey("requestCode")) {
            reqCode = extras.getInt("requestCode");
        }

        //      https://stackoverflow.com/questions/13377361/how-to-create-a-drop-down-list
        ArrayAdapter<String> adapterEventOccurance = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, appEventOccurrance);
        spnEventOccurance.setAdapter(adapterEventOccurance);

        ArrayAdapter<String> adapterEventType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, appEventType);
        spnEventType.setAdapter(adapterEventType);
        eor = new EventObjectRepo(context);
        Note tmpNote = null;
        if (reqCode == ADD_EVENT_TONOTE_REQUEST) {
            setTitle("Add Event to Note");
            if (extras.containsKey("Note")) {
                // coming from NoteAddActivity, todo POSSIBLY provide navigation to Note??
                tmpNote = (Note) extras.getSerializable("Note");
//              coming from note without event
                edtEventMessage.setText(tmpNote.getNoteItem());
//                edtEventMessageTag.setText(tmpNote.getTag());
                txtNoteId.setText(String.valueOf(tmpNote.getNoteId()));
                setEventRepeatCheckBoxes("");
            }

        } else if (reqCode == EDIT_EVENT_INNOTE_REQUEST || reqCode == EDIT_CALENDAR_EVENT) {
            if (reqCode == EDIT_EVENT_INNOTE_REQUEST) {
                setTitle("Edit Event in Note");
                if (extras.containsKey("Note")) {
                    // coming from NoteAddActivity, todo POSSIBLY provide navigation to Note??
                    tmpNote = (Note) extras.getSerializable("Note");
                }
            }
            if (extras.containsKey("event_object")) {
                // coming from NoteAddActivity w existing event OR Calendar tab, EventsActivityFragment list
                eo = (Event_Object) extras.getSerializable("event_object");
                if (eo != null) {
                    edtEventMessage.setText(eo.getMessage());
                    edtEventMessageTag.setText(eo.getMessageTag());

                    if (eo.getDate() != null) {
                        txtEventDate.setText(convertDBdateStringToString_dateAMPM(eo.getDate()));
                    }
                    if (eo.getOccurance() != null) {
//                              https://stackoverflow.com/questions/2390102/how-to-set-selected-item-of-spinner-by-value-not-by-position#4228121
                        sEventOccurance = eo.getOccurance().toString();
                        int spinnerPosition = adapterEventOccurance.getPosition(eo.getOccurance());
                        spnEventOccurance.setSelection(spinnerPosition);
                    }
                    if (eo.getEventType() != null) {
//                              https://stackoverflow.com/questions/2390102/how-to-set-selected-item-of-spinner-by-value-not-by-position#4228121
                        sEventType = eo.getEventType().toString();
                        int spinnerPosition = adapterEventType.getPosition(eo.getEventType());
                        spnEventType.setSelection(spinnerPosition);
                    }
                    if (eo.getToContact() != null) {
                        edtContact.setText(eo.getToContact());
                    }
                    if (eo.getId() != 0) {
//                        lblDate_tagEventId.setTag(eo.getId());
                        txtEventId.setText(String.valueOf( eo.getId()));
                    }
                    if (eo.getmNoteId() != 0){
                        txtNoteId.setText(String.valueOf(eo.getmNoteId()));
                    }
                    if (sEventType.equals(EVENTTYPE_SCHEDULE_NOTIFICATION) && sEventOccurance != null && sEventOccurance.equals(EVENTOCCURRANCE_WEEKLY)) {
                        layoutDays.setVisibility(View.VISIBLE);
                        if (!eo.getRepeat().toString().equals("")) {
                            setEventRepeatCheckBoxes(eo.getRepeat().toString());
                        }
                    } else {
                        layoutDays.setVisibility(View.GONE);
                    }
                    if (reqCode == EDIT_CALENDAR_EVENT) {
                        setTitle("Edit Event in Calendar");
                        tmpNote = eor.getNoteById(eo.getmNoteId());
                    }
                }
            }
        }

//        if (reqCode == EDIT_CALENDAR_EVENT || reqCode == ADD_EVENT_TONOTE_REQUEST || reqCode == EDIT_EVENT_INNOTE_REQUEST) {
//            if (extras.containsKey("Note")) {
//                // coming from NoteAddActivity, todo POSSIBLY provide navigation to Note??
//                tmpNote = (Note) extras.getSerializable("Note");
//            }
//
//            if (extras.containsKey("event_object")) {
//                // coming from NoteAddActivity w existing event OR Calendar tab, EventsActivityFragment list
//                eo = (Event_Object) extras.getSerializable("event_object");
//                if (eo != null) {
//                    edtEventMessage.setText(eo.getMessage());
//                    edtEventMessageTag.setText(eo.getMessageTag());
//
//                    if (eo.getDate() != null) {
//                        txtEventDate.setText(convertDBdateStringToString_dateAMPM(eo.getDate()));
//                    }
//                    if (eo.getOccurance() != null) {
////                              https://stackoverflow.com/questions/2390102/how-to-set-selected-item-of-spinner-by-value-not-by-position#4228121
//                        sEventOccurance = eo.getOccurance().toString();
//                        int spinnerPosition = adapterEventOccurance.getPosition(eo.getOccurance());
//                        spnEventOccurance.setSelection(spinnerPosition);
//                    }
//                    if (eo.getEventType() != null) {
////                              https://stackoverflow.com/questions/2390102/how-to-set-selected-item-of-spinner-by-value-not-by-position#4228121
//                        sEventType = eo.getEventType().toString();
//                        int spinnerPosition = adapterEventType.getPosition(eo.getEventType());
//                        spnEventType.setSelection(spinnerPosition);
//                    }
//                    if (eo.getToContact() != null) {
//                        edtContact.setText(eo.getToContact());
//                    }
//                    if (eo.getId() != 0) {
////                        lblDate_tagEventId.setTag(eo.getId());
//                        txtEventId.setText(String.valueOf( eo.getId()));
//                    }
//                    if (eo.getmNoteId() != 0){
//                        txtNoteId.setText(String.valueOf(eo.getmNoteId()));
//                    }
//                    if (sEventType.equals(EVENTTYPE_SCHEDULE_NOTIFICATION) && sEventOccurance != null && sEventOccurance.equals(EVENTOCCURRANCE_WEEKLY)) {
//                        layoutDays.setVisibility(View.VISIBLE);
//                        if (!eo.getRepeat().toString().equals("")) {
//                            setEventRepeatCheckBoxes(eo.getRepeat().toString());
//                        }
//                    } else {
//                        layoutDays.setVisibility(View.GONE);
//                    }
//                }
//            } else {
////              coming from note without event
//                edtEventMessage.setText(tmpNote.getNoteItem());
////                edtEventMessageTag.setText(tmpNote.getTag());
//                txtNoteId.setText(String.valueOf(tmpNote.getNoteId()));
//                setEventRepeatCheckBoxes("");
//            }
//        }
        final Note mNote = tmpNote;
//        wvs hint: returning from CustomCalender... then onActivityResult setEnabled(true) and edtEventDate.setText(extras.get("datetime").toString());
//        but edtEventDate (when rotated) is blank

        if (savedInstanceState == null && txtEventDate.getText().toString().equals("")) {
//          first time in OR adding new event: disable buttons
            btnSave.setEnabled(false);
            btnDelete.setEnabled(false);
        } else {
//          otherwise, keep previous state
            btnSave.setEnabled(false);
            btnDelete.setEnabled(true);
        }

        txtEventDate.setEnabled(false);
        lblAddres.setVisibility(View.GONE);

        spnEventOccurance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    String tmpEventOccurance = appEventOccurrance[position];
                    updateCalendarlblEventOccurance(tmpEventOccurance);
                    if (tmpEventOccurance.equals(EVENTOCCURRANCE_WEEKLY) && spnEventType.getSelectedItem().toString().equals(EVENTTYPE_SCHEDULE_NOTIFICATION)) {
                        layoutDays.setVisibility(View.VISIBLE);
//                        setEventRepeatCheckBoxes("");  // todo foobar needed to chkSun.setTag etc
                    } else {
                        layoutDays.setVisibility(View.GONE);
                    }
                    if (reqCode == EDIT_CALENDAR_EVENT){
                        if (!sEventOccurance.equals(tmpEventOccurance)){
                            btnSave.setEnabled(true);
                        }
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spnEventType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    String tmpEventtype = appEventType[position];
                    String tmpCalendarlbl = Calendar_lbl.getText().toString();
//                    if (!tmpCalendarlbl.contains(CALENDAR_LONGCLICKTOADD)) {
                    if (tmpEventtype.equals(EVENTTYPE_SCHEDULE_NOTIFICATION)) {
                        int spinnerPosition2 = adapterEventOccurance.getPosition(EVENTOCCURRANCE_WEEKLY);  //default to "Weekly"
                        layoutDays.setVisibility(View.VISIBLE);
                        spnEventOccurance.setSelection(spinnerPosition2);
                        if (tmpCalendarlbl.indexOf(",") > 0){
                            tmpCalendarlbl = tmpEventtype + ": " + appEventOccurrance[spnEventOccurance.getSelectedItemPosition()] + tmpCalendarlbl.substring(tmpCalendarlbl.indexOf(","));
                        } else {
                            tmpCalendarlbl = tmpEventtype + ": " + appEventOccurrance[spnEventOccurance.getSelectedItemPosition()] + "," + txtEventDate.getText().toString();
                        }
                    } else if (!tmpCalendarlbl.equals(getResources().getString(R.string.event_lbl))){  //"CALENDAR EVENTS"
                        int spinnerPosition2 = adapterEventOccurance.getPosition(EVENTOCCURRANCE_ONETIME);  //default to "OneTime"
                        layoutDays.setVisibility(View.GONE);
                        spnEventOccurance.setSelection(spinnerPosition2);
                        tmpCalendarlbl = tmpEventtype + tmpCalendarlbl.substring(tmpCalendarlbl.indexOf(":"));
                    } else if (tmpCalendarlbl.equals(getResources().getString(R.string.event_lbl))) {  //"CALENDAR EVENTS"
                        int spinnerPosition2 = adapterEventOccurance.getPosition(EVENTOCCURRANCE_ONETIME);  //default to "OneTime"
                        layoutDays.setVisibility(View.GONE);
                        spnEventOccurance.setSelection(spinnerPosition2);
                        tmpCalendarlbl = tmpEventtype + ": " + appEventOccurrance[spnEventOccurance.getSelectedItemPosition()] + "," + txtEventDate.getText().toString();
                    }
                    Calendar_lbl.setText(tmpCalendarlbl);
//                    }
                    if (tmpEventtype.equals(EVENTTYPE_NOTIFICATION) || tmpEventtype.equals(EVENTTYPE_SCHEDULE_NOTIFICATION)) {
                        edtContact.setVisibility(View.GONE);
                        spnEventOccurance.setVisibility(View.VISIBLE);
                    } else if (!tmpCalendarlbl.equals("")) {  // (tmpEventtype.equals(EVENTTYPE_TEXTMSG) || tmpEventtype.equals(EVENTTYPE_EMAIL)
                        if (tmpEventtype.equals(EVENTTYPE_EMAIL)){
                            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(edtContact.getText()).matches()){
                                edtContact.setText("");
                            }
                        } else if (tmpEventtype.equals(EVENTTYPE_TEXTMSG)){
                            if(!Patterns.PHONE.matcher(edtContact.getText()).matches()){
                                edtContact.setText("");
                            }
                        }
                        edtContact.setVisibility(View.VISIBLE);
                        spnEventOccurance.setVisibility(View.GONE);
                        updateCalendarlblEventOccurance(EVENTOCCURRANCE_ONETIME); // default to One-Time
                    } else {
                        Log.i(TAG, " WVS BREAK HERE");
                    }
                    if (reqCode == EDIT_CALENDAR_EVENT){
                        if (!sEventType.equals(tmpEventtype)){
                            btnSave.setEnabled(true);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        chkSun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSave.setEnabled(enableSave_chkBoxes());
                resetMessageTagbasedonRepeat();
            }
        });
        chkMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSave.setEnabled(enableSave_chkBoxes());
                resetMessageTagbasedonRepeat();
            }
        });
        chkTue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSave.setEnabled(enableSave_chkBoxes());
                resetMessageTagbasedonRepeat();
            }
        });
        chkWed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSave.setEnabled(enableSave_chkBoxes());
                resetMessageTagbasedonRepeat();
            }
        });
        chkThu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSave.setEnabled(enableSave_chkBoxes());
                resetMessageTagbasedonRepeat();
            }
        });
        chkFri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSave.setEnabled(enableSave_chkBoxes());
                resetMessageTagbasedonRepeat();
            }
        });
        chkSat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSave.setEnabled(enableSave_chkBoxes());
                resetMessageTagbasedonRepeat();
            }
        });

        lblEventDate_tagNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtEventMessage = findViewById(R.id.edtEventMessage);
                txtEventId = findViewById(R.id.txtEventEventId);
//                edtEventDate = findViewById(R.id.edtEventDate);

                String sDateTime = txtEventDate.getText().toString();
                if (sDateTime.equals("")){
                    sDateTime = CALENDAR_LONGCLICKTOADD;
                }
//                Intent intent1 = new Intent(mContext, CustomCalendarActivity_Obsolescence.class);
//              TODO DETERMINE IF coming FROM note ADD_EVENT_TONOTE_REQUEST("Note" tmpNote)/EDIT_EVENT_INNOTE_REQUEST("event_object") or calendar tab, event list
                Intent intent1 = new Intent(context, CustomCalendarActivity2.class);
                intent1.putExtra("requestCode", reqCode);
                if (reqCode == ADD_EVENT_TONOTE_REQUEST){
                    Log.i(TAG, "BREAK HERE");
                    intent1.putExtra("calendar_lbl", CALENDAR_LONGCLICKTOADD + " " + Calendar_lbl.getText().toString());
                    intent1.putExtra("Note", mNote);
                } else if (reqCode == EDIT_EVENT_INNOTE_REQUEST){
                    Integer iEventId = Integer.parseInt( txtEventId.getText().toString());
                    intent1.putExtra("Note", mNote);
//                    intent1.putExtra("eventid" , iEventId);
                    intent1.putExtra("calendar_lbl", Calendar_lbl.getText().toString());
                } else if (reqCode == EDIT_CALENDAR_EVENT){
                    Log.i(TAG, "BREAK HERE pass eventid");
                    if (txtEventId.getText()!=null){
                    Integer iEventId = Integer.parseInt( txtEventId.getText().toString());
                    intent1.putExtra("eventid" , iEventId);
                    intent1.putExtra("calendar_lbl", Calendar_lbl.getText().toString());

                    }
                }
//                if (v.getTag() != null) {  //coming from Note Add/Edit
//                    intent1.putExtra("Note", (Note) v.getTag());
////                  if no date add "long click to add date/time" else use edtEventDate.getText() :
//                    intent1.putExtra("calendar_lbl", sDateTime  + ": " + edtEventMessage.getText());
//                } else if (txtEventId.getText()!=null && !txtEventId.getText().equals("eventid")) {
//                    Integer iEventId = Integer.parseInt( txtEventId.getText().toString());
//                    intent1.putExtra("eventid" , iEventId);
//                    intent1.putExtra("calendar_lbl", Calendar_lbl.getText().toString());
//                }
                startActivityForResult(intent1, reqCode);
            }
        });

        edtEventMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btnSave.setEnabled(true);
            }
        });

        edtEventMessageTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btnSave.setEnabled(true);
            }
        });

        edtContact.setOnClickListener(new View.OnClickListener() {
            //            https://stackoverflow.com/questions/7330165/how-to-access-contacts-in-my-android-program?answertab=active#tab-top
            @Override
            public void onClick(View v) {

                String sEventType = spnEventType.getSelectedItem().toString();
                switch (sEventType) {
                    case EVENTTYPE_NOTIFICATION:
                        break;
                    case EVENTTYPE_SCHEDULE_NOTIFICATION:
                        break;
                    case EVENTTYPE_TEXTMSG:
                        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(intent, START_PICKER_CONTACT);
                        break;
                    case EVENTTYPE_EMAIL:
                        Intent intent2 = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(intent2, START_PICKER_EMAIL);
                        break;
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sContact = edtContact.getText().toString();
//              spnEventType.getItemAtPosition(spnEventType.getSelectedItemPosition()).toString()
                sEventType = spnEventType.getSelectedItem().toString();
                sEventOccurance = spnEventOccurance.getSelectedItem().toString();
                String sEventRepeat = getEventRepeatFromCheckBoxes(sEventOccurance);

                switch (sEventType){
                    case EVENTTYPE_NOTIFICATION:
                        sContact="";
                        break;
                    case EVENTTYPE_SCHEDULE_NOTIFICATION:
                        sContact="";
                        if (sEventOccurance.equals(EVENTOCCURRANCE_WEEKLY) && sEventRepeat.equals("")){
                            Toast.makeText(context,"MUST SELECT Repeat Day",Toast.LENGTH_LONG).show();
                            return;
                        }
                        break;
                    case EVENTTYPE_TEXTMSG:
//                        verify phone format/length
                        if(!Patterns.PHONE.matcher(sContact).matches()){
                            Toast.makeText(context,"INVALID PHONE NUMBER",Toast.LENGTH_LONG).show();
                            return;
                        }
                        break;
                    case EVENTTYPE_EMAIL:
//                        verify email format/length
                        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(sContact).matches()){
                            Toast.makeText(context,"INVALID EMAIL ADDRESS",Toast.LENGTH_LONG).show();
                            return;
                        }
                        break;
                }
                if (txtEventDate.getText().toString().equals("")){
                    Toast.makeText(context,"Please SET Event Time",Toast.LENGTH_LONG).show();
                    return;
                }
//              setResult pass back Event_Object
//                Intent returnIntent = getIntent();  //has Note
                Intent returnIntent = new Intent();

                int iNoteId;
//                if (edtEventMessage.getTag()!=null){
                if (txtNoteId.getText()!=null) {
//                  coming from NoteAddActivity
//                    iNoteId = (int) edtEventMessage.getTag();
                    iNoteId = Integer.parseInt(txtNoteId.getText().toString());
                } else {
//                  coming from somewhere else?
                    iNoteId = 0;
                }
                int iEventId=0;
//                if (lblDate_tagEventId.getTag()!=null){
                if (txtEventId.getText()!=null && !txtEventId.getText().equals("eventid")){  //wvs todo: fix this temp logic
//                    iEventId = (int) lblDate_tagEventId.getTag();
                    iEventId = Integer.parseInt( txtEventId.getText().toString());
                }
                String sDBFormattedDateTime = WVS_Utils.convertTimePicker_formatAMPM_To_DBdateString(txtEventDate.getText().toString());
                Event_Object tmpNew_eo = new Event_Object(iEventId,edtEventMessage.getText().toString(),sDBFormattedDateTime,
                        sEventOccurance, sEventType, edtContact.getText().toString(),iNoteId, sEventRepeat, edtEventMessageTag.getText().toString());

                if (reqCode == ADD_EVENT_TONOTE_REQUEST) {
                    eor.insert(tmpNew_eo);
                } else if (reqCode == EDIT_EVENT_INNOTE_REQUEST || reqCode == EDIT_CALENDAR_EVENT){
                    eor.update(tmpNew_eo);
                }

                Note tmpNote = eor.getNoteById(iNoteId);
//              CREATE NOTIFICATIONS
                WVS_Utils.CreateNotification(context, tmpNew_eo, Calendar_lbl.getText().toString(), tmpNote );

                if (reqCode == EDIT_EVENT_INNOTE_REQUEST || reqCode == EDIT_CALENDAR_EVENT){
                    //              wvs todo 9/1/20  xx cancel old notifications
                    if (eo.getEventType().contains(EVENTTYPE_NOTIFICATION) || (eo.getEventType().contains(EVENTTYPE_SCHEDULE_NOTIFICATION))) {
                        String sOldEventDate = eo.getDate();
                        String sTag_Notification = eo.getmNoteId().toString() + " " + eo.getId() + " " + sOldEventDate; //tmpEOfromTimePicker.getMessage();
                        NotificationHandler.cancelReminder(sTag_Notification);
                        Log.i(TAG, " WVS Cancelling old Notification " + sTag_Notification);
                    } else if (tmpNew_eo.getEventType().contains(EVENTTYPE_TEXTMSG)) {
                        String sOldEventDate = eo.getDate();
                        String sTag_Notification = eo.getmNoteId().toString() + " " + eo.getId() + " " + sOldEventDate; //tmpEOfromTimePicker.getMessage();
                        SMSTextHandler.cancelReminder(sTag_Notification);
                        Log.i(TAG, " WVS Cancelling old Text Msg " + sTag_Notification);
                    }
                }

                returnIntent.putExtra("event_object", tmpNew_eo);          //"eventobject", eo);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int iNoteId, iEventId;
                String sEventId;
                Intent returnIntent = new Intent();

                if (reqCode == EDIT_EVENT_INNOTE_REQUEST || reqCode == EDIT_CALENDAR_EVENT) {
//                    delete event, cancel old notifications
                    EventObjectRepo eor = new EventObjectRepo(context);
                    int iRet = eor.deletebyNoteId(eo.getmNoteId());

//                  wvs cancel old notification
                    String sOldEventDate = eo.getDate();
                    String sTag_Notification = eo.getmNoteId().toString() + " " + eo.getId() + " " + sOldEventDate;
                     if (eo.getEventType().contains(EVENTTYPE_NOTIFICATION) || eo.getEventType().contains(EVENTTYPE_SCHEDULE_NOTIFICATION)) {
                        NotificationHandler.cancelReminder(sTag_Notification);
                        Log.i(TAG, " WVS Cancelling old Notification " + sTag_Notification);
                    } else if (eo.getEventType().contains(EVENTTYPE_TEXTMSG)) {
                        SMSTextHandler.cancelReminder(sTag_Notification);
                        Log.i(TAG, " WVS Cancelling old Text Msg " + sTag_Notification);
                    }

                    setResult(RESULT_DELETE);
                    finish();

                }
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == START_PICKER_TIME){
            if (resultCode == RESULT_OK) {
                btnSave.setEnabled(true);
                Date tmpdate = (Date) data.getSerializableExtra("date");
                Intent returnIntent = getIntent();
                String datepart = new SimpleDateFormat("yyyy-MM-dd").format(tmpdate);  //remove time HH-mm
//            todo: test data.getextras().containsKey("time")
                String sdatetime = datepart + " " + data.getStringExtra("time");
                returnIntent.putExtra("datetime", sdatetime);
                updateCalendarlblEventTime(sdatetime);
                setResult(RESULT_OK, returnIntent);
//                mAdapter.notifyDataSetChanged();  //this refers to mEvents changes that affect CustomCalendarActivity2.java
            } else if (resultCode == RESULT_CANCELED) {

            }
        } else if (requestCode == START_PICKER_CONTACT){
//            https://stackoverflow.com/questions/7330165/how-to-access-contacts-in-my-android-program?answertab=active#tab-top
            if (resultCode == Activity.RESULT_OK)
            {
                btnSave.setEnabled(true);
                Uri contactData = data.getData();
//                Cursor c = managedQuery(contactData, null, null, null, null);
                Cursor c = getContentResolver().query(contactData, null, null, null, null);

                if (c.moveToFirst())
                {
                    String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                    String hasPhone =
                            c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    if (hasPhone.equalsIgnoreCase("1")){
                        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);
                        phones.moveToFirst();
                        String sContact="";
                        ArrayList<String> contacts = new ArrayList<String>();
                        do {
                            sContact = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contacts.add(sContact);
                            Log.i(TAG, " WVS phone " + sContact);
                        } while (phones.moveToNext());

                        showDialogContacts(contacts);
                    }
                }
            }
        } else if (requestCode == START_PICKER_EMAIL){
//            https://stackoverflow.com/questions/7330165/how-to-access-contacts-in-my-android-program?answertab=active#tab-top
            if (resultCode == Activity.RESULT_OK)
            {
                btnSave.setEnabled(true);
                Uri contactData = data.getData();
//                Cursor c = managedQuery(contactData, null, null, null, null);
                Cursor c = getContentResolver().query(contactData, null, null, null, null);

                if (c.moveToFirst())
                {
                    String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

//                    String hasEmail =
//                            c.getString(c.getColumnIndex(ContactsContract.Contacts.MA));

//                    if (hasPhone.equalsIgnoreCase("1")){
                    Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID +" = "+ id,null, null);
                    emails.moveToFirst();
                    String sContact="";
                    ArrayList<String> contacts = new ArrayList<String>();
                    do {
                        sContact = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        contacts.add(sContact);
                        Log.i(TAG, " WVS email " + sContact);
                    } while (emails.moveToNext());

                    showDialogContacts(contacts);
//                    }
                }
            }
        } else if (reqCode == EDIT_CALENDAR_EVENT || reqCode == ADD_EVENT_TONOTE_REQUEST || reqCode == EDIT_EVENT_INNOTE_REQUEST){
//          returning from CustomCalendarActivity2
            if (resultCode == Activity.RESULT_OK && data != null){
                Bundle extras = data.getExtras();
                if (extras.containsKey("datetime")) {
                    String sdatetime = data.getStringExtra("datetime");
                    updateCalendarlblEventTime(sdatetime);
                    txtEventDate.setText(sdatetime);
//                    wvs todo add time part and repeated string to message tag
                    resetMessageTagbasedonRepeat();
                    btnSave.setEnabled(true);
                }
            }
        }
    }

    public void showDialogContacts(final ArrayList<String> contacts){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("choose contact");


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final CharSequence[] cs = contacts.toArray(new CharSequence[contacts.size()]);

        builder.setSingleChoiceItems(cs, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sContact = cs[which].toString();
                edtContact.setText(sContact);
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    //    @Override
//    public void onActivityReenter(int resultCode, Intent data) {
//        super.onActivityReenter(resultCode, data);
//    }
    public String getEventTypeFromCalendarLabel(){
        //          label -> Event type: Time info [<Weekly>,time]: Event info OR "long click to add date/time"
        String label = Calendar_lbl.getText().toString();
        String sEventType = "";
        if (label.contains(":")) {
            sEventType = label.substring(0, label.indexOf(":")).trim();
        }
        return  sEventType;
    }

    public String getTimeInfoFromCalendarLabel(){
        //          label -> Event type: Time info [<occurrance>,time] OR "long click to add date/time"
        String label = Calendar_lbl.getText().toString();
        String sTimeInfo = "";
        if (label.contains(":")) {
            int pos1 = label.indexOf(":") + 1;
//            int pos2 = label.indexOf(",") + 1;
//            if (label.contains(",")) {
//                sTimeInfo = label.substring(pos2).trim();
//            } else
            if (label.contains(CALENDAR_LONGCLICKTOADD)) {
                // long click to add date/time: advisory
            } else {
                //long click to add date/time: <Weekly>,2020-08-19 09:20 AM
                sTimeInfo = label.substring(pos1).trim();
            }

        }
        return  sTimeInfo;
    }

    public String getDBDateTimeFromCalendarLabel(){
        //          label -> Event type: Time info [<occurrance>,time]: Event info OR "long click to add date/time"
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

    public String getDateTimeAMPMFromCalendarLabel(){
        //          label -> Event type: Time info [<occurrance>,time]: Event info OR "long click to add date/time"
        String sTimeInfo = getTimeInfoFromCalendarLabel();
        String sTime = "";
        if (sTimeInfo.contains(",") && !sTimeInfo.contains(CALENDAR_LONGCLICKTOADD)){
//            Weekly, 2020-08-20 04:10 PM
            sTime = sTimeInfo.substring(sTimeInfo.indexOf(",") + 1).trim();
        } else {
            sTime = sTimeInfo.trim();
        }

        return  sTime;
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

    public void updateCalendarlblEventTime(String snewEventTime){
        //          label -> Event type: Time info [<occurrance>,time]: Event info
        String sEventType = getEventTypeFromCalendarLabel();
        String sEventTime =  getTimeInfoFromCalendarLabel();

        if (sEventTime.contains(",")){
            String sOccurance = "";
            sOccurance = sEventTime.substring(0, sEventTime.indexOf(",")).trim();
            sEventTime = sOccurance + ", " + snewEventTime;
        } else {
            sEventTime = snewEventTime;
        }
        Calendar_lbl.setText(sEventType + ": " + sEventTime);
    }

    public void updateCalendarlblEventOccurance(String snewEventOccuranc){
        //          label -> Event type: Time info [<occurrance>,time]: Event info
        String sEventType = getEventTypeFromCalendarLabel();  //long click to add date/time: 2020-08-19 09:20 AM: advisory
        String sEventTime =  getTimeInfoFromCalendarLabel();
        String sTime = "";

        if (sEventTime.contains(",")){
            sTime = sEventTime.substring(sEventTime.indexOf(",") + 1).trim();
            sEventTime = snewEventOccuranc + ", " + sTime;
        } else {
            sEventTime = snewEventOccuranc + ", " + sEventTime;
        }
        Calendar_lbl.setText(sEventType + ": " + sEventTime );
    }

    public String getEventRepeatFromCheckBoxes(String sEventOccurance) {
        String sRepeat = "";
        if (sEventOccurance.equals(EVENTOCCURRANCE_WEEKLY)) {
            sRepeat = chkSun.isChecked() ? String.valueOf(Calendar.SUNDAY) + "," : "";
            sRepeat = sRepeat + (chkMon.isChecked() ? String.valueOf(Calendar.MONDAY) + "," : "");
            sRepeat = sRepeat + (chkTue.isChecked() ? String.valueOf(Calendar.TUESDAY) + "," : "");
            sRepeat = sRepeat + (chkWed.isChecked() ? String.valueOf(Calendar.WEDNESDAY) + "," : "");
            sRepeat = sRepeat + (chkThu.isChecked() ? String.valueOf(Calendar.THURSDAY) + "," : "");
            sRepeat = sRepeat + (chkFri.isChecked() ? String.valueOf(Calendar.FRIDAY) + "," : "");
            sRepeat = sRepeat + (chkSat.isChecked() ? String.valueOf(Calendar.SATURDAY) + "," : "");
        }
        return sRepeat;
    }

    public void setEventRepeatCheckBoxes(String sRepeatEvent){
//      called once, initially from onCreate
        chkSun.setChecked(sRepeatEvent.contains(String.valueOf(Calendar.SUNDAY)));
        chkSun.setTag(chkSun.isChecked());
        chkMon.setChecked(sRepeatEvent.contains(String.valueOf(Calendar.MONDAY)));
        chkMon.setTag(chkMon.isChecked());
        chkTue.setChecked(sRepeatEvent.contains(String.valueOf(Calendar.TUESDAY)));
        chkTue.setTag(chkTue.isChecked());
        chkWed.setChecked(sRepeatEvent.contains(String.valueOf(Calendar.WEDNESDAY)));
        chkWed.setTag(chkWed.isChecked());
        chkThu.setChecked(sRepeatEvent.contains(String.valueOf(Calendar.THURSDAY)));
        chkThu.setTag(chkThu.isChecked());
        chkFri.setChecked(sRepeatEvent.contains(String.valueOf(Calendar.FRIDAY)));
        chkFri.setTag(chkFri.isChecked());
        chkSat.setChecked(sRepeatEvent.contains(String.valueOf(Calendar.SATURDAY)));
        chkSat.setTag(chkSat.isChecked());
    }
    public Boolean enableSave_chkBoxes(){
        Boolean bRet = !(chkSun.getTag().equals(chkSun.isChecked()) &&
                chkMon.getTag().equals(chkMon.isChecked()) &&
                chkTue.getTag().equals(chkTue.isChecked()) &&
                chkWed.getTag().equals(chkWed.isChecked()) &&
                chkThu.getTag().equals(chkThu.isChecked()) &&
                chkFri.getTag().equals(chkFri.isChecked()) &&
                chkSat.getTag().equals(chkSat.isChecked()));

        return bRet;
    }

    public void resetMessageTagbasedonRepeat(){
//      10:30 AM Mo,Tu,Fr
        sEventOccurance = spnEventOccurance.getSelectedItem().toString();
        sEventType = spnEventType.getSelectedItem().toString();
        if (sEventType.equals(EVENTTYPE_SCHEDULE_NOTIFICATION) && sEventOccurance != null && sEventOccurance.equals(EVENTOCCURRANCE_WEEKLY)) {
            String sEventRepeat = getEventRepeatFromCheckBoxes(sEventOccurance);
            String sEventRepeatfomatted = translateWeekdayNumbers(sEventRepeat);
            String sDateTimeAmPm = getDateTimeAMPMFromCalendarLabel();
            String sTimepart = "";
            if (!sDateTimeAmPm.toString().equals("")) {
                sTimepart = sDateTimeAmPm.substring("2020-09-01 ".length()).trim();  //2020-09-02 10:30 AM
            }
            String sRet = sTimepart + " " + sEventRepeatfomatted;
            edtEventMessageTag.setText(sRet.trim());
        }
    }
}
