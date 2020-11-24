package com.endeavor.walter.getout9;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.endeavor.walter.getout9.WVSInfo.ADD_EVENT_TONOTE_REQUEST;
import static com.endeavor.walter.getout9.WVSInfo.ADD_NOTE_ITEM_REQUEST;
import static com.endeavor.walter.getout9.WVSInfo.EDIT_EVENT_INNOTE_REQUEST;
import static com.endeavor.walter.getout9.WVSInfo.EDIT_NOTE_ITEM_REQUEST;
import static com.endeavor.walter.getout9.WVS_Utils.convertDateToString_datepart;

public class CalendarFragment<onActivityResult> extends Fragment {

//    private CalendarViewModel mViewModel;
//    private CustomCalendarActivity2 mViewModel;
    public static String TAG = CalendarFragment.class.getSimpleName();
    private boolean shouldRefreshOnResume = false;

    TextView Calendar_lbl;  // label -> Event type: Time info: Event info

    private ImageView previousButton, nextButton;
    private TextView displayMonthYear,txtSun;
//    private EditText edtContact;
    private GridView calendarGridView;
    private Button btnSave, btnDelete, btnCancel;
    private LinearLayout layoutCalendar;  //layoutDays,
    private CheckBox chkSun, chkMon, chkTue, chkWed, chkThu, chkFri, chkSat;
    private static final int MAX_CALENDAR_COLUMN = 42;
    private int month, year;
    private SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    private CalendarGridAdapter mAdapter;
    //    private DatabaseQuery mQuery;
    private EventObjectRepo eor;
    private String sEventType, sEventOccurance;

    List<Date> dayValueInCells;
    ArrayList<Event_Object> mEvents;
    Integer reqCode;
    Note tmpNote;
    int EventGridPosition;
//    Spinner spnEventOccurance, spnEventType;

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        shouldRefreshOnResume = false;
//      CALENDAR TAB: Displays all events
        return inflater.inflate(R.layout.calendar_fragment, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mViewModel = ViewModelProviders.of(this).get(CalendarViewModel.class);
//        mViewModel = ViewModelProviders.of(this).get(CustomCalendarActivity2.class);
        // TODO: Use the ViewModel
        Log.i(TAG, " WVS onActivityCreated");
        previousButton = getView().findViewById(R.id.previous_month);
        nextButton = getView().findViewById(R.id.next_month);
        displayMonthYear = getView().findViewById(R.id.display_current_date);
        btnSave = getView().findViewById(R.id.btnSave_Calendar);
        btnCancel = getView().findViewById(R.id.btnCancel_Calendar);
        btnDelete = getView().findViewById(R.id.btnDelete_Calendar);
        calendarGridView = getView().findViewById(R.id.calendar_grid);
//        spnEventType = getView().findViewById(R.id.spnEventType_Calendar);
//        spnEventOccurance = getView().findViewById(R.id.spnEventOccurance_Calendar);
//        edtContact = getView().findViewById(R.id.edtContacts_Calendar);
//        layoutDays = getView().findViewById(R.id.layout_Days);
        layoutCalendar = getView().findViewById(R.id.activity_custom_calendar2);
        layoutCalendar.setPadding(0,120,0,0);
        final GridView calendarGridView = getView().findViewById(R.id.calendar_grid);

        btnSave.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
//        layoutDays.setVisibility(View.GONE);
//        spnEventType.setVisibility(View.GONE);
//        spnEventOccurance.setVisibility(View.GONE);
//        edtContact.setVisibility(View.GONE);
        setUpCalendarAdapter();
        setPreviousButtonClickEvent();
        setNextButtonClickEvent();

        calendarGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Date clickedDate = (Date) calendarGridView.getItemAtPosition(position);
                String sDatePart = convertDateToString_datepart(clickedDate);
                Boolean bEventExists = false;
                if (mEvents != null) {
                    for (int i = 0; i < mEvents.size(); i++) {
                        if (WVS_Utils.IsDBStringDateEqualtoDate(mEvents.get(i).getDate(), clickedDate)) {
                            bEventExists = true;
                            break;
                        }
                    }
                }
                if (bEventExists) {
                    Intent intent1 = new Intent(getContext(), EventsActivity.class);
                    intent1.putExtra("datepart_calendarselected", sDatePart);
                    startActivity(intent1);
                }
            }
        }
        );
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
        eor = new EventObjectRepo(getContext());
//        WVS TODO remove ADDD_NOTE_ITEM_REQUEST, EDIT_NOTE_ITEM_REQUEST
        if (reqCode != null && (reqCode == EDIT_NOTE_ITEM_REQUEST || reqCode == ADD_NOTE_ITEM_REQUEST || reqCode == ADD_EVENT_TONOTE_REQUEST || reqCode == EDIT_EVENT_INNOTE_REQUEST)) {
            mEvents = eor.getEventsByNoteId(tmpNote.getNoteId(), false);
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
        mAdapter = new CalendarGridAdapter(getContext(), dayValueInCells, cal, mEvents);
        calendarGridView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.i(TAG," WVS onResume() ##4, shouldRefreshOnResume="+shouldRefreshOnResume);
        // Check should we need to refresh the fragment
        if(shouldRefreshOnResume){
            // refresh fragment wvs hint: https://stackoverflow.com/questions/20702333/refresh-fragment-at-reload#20702418
//            Log.i(TAG," WVS onResume ##4 refreshing fragment");
//            android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
//            ft.detach(this).attach(this).commit();
            androidx.fragment.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
        shouldRefreshOnResume = false;
//        Log.i(TAG," WVS onResume() ##4, reset shouldRefreshOnResume="+shouldRefreshOnResume);
    }

    @Override
    public void onStop() {
        super.onStop();
        shouldRefreshOnResume = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void RefreshList() {

//        NotesRepo nr = new NotesRepo(getActivity());
//        objA_Notes = nr.getAllNotes();

        // refresh fragment wvs hint: https://stackoverflow.com/questions/20702333/refresh-fragment-at-reload#20702418
        Log.i(TAG, " WVS RefreshList() refreshing fragment");
//            android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        androidx.fragment.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}
