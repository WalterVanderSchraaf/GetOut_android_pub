package com.endeavor.walter.getout9;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import static com.endeavor.walter.getout9.WVSInfo.EDIT_CALENDAR_EVENT;
import static com.endeavor.walter.getout9.WVSInfo.EDIT_EVENT_INNOTE_REQUEST;

public class EventsActivityFragment extends Fragment {

    // wvs hint: EventActivityFragment.class when using import android.app.Fragment instead of import android.support.v4.app.Fragment
//           will produce the following error:
//  error: no suitable method found for add(int,EventActivityFragment)
//  method FragmentTransaction.add(Fragment,String) is not applicable
//  (argument mismatch; int cannot be converted to Fragment)
//  method FragmentTransaction.add(int,Fragment) is not applicable
//  (argument mismatch; EventActivityFragment cannot be converted to Fragment)

    final public static String TAG = EventsActivityFragment.class.getSimpleName();
    private NoteActivityFragment.OnFragmentInteractionListener mListener;
    private boolean shouldRefreshOnResume = false;
    static ArrayList<Event_Object> objA_Event;

    public EventArrayAdapter genericAdapter;
    public ListView lstVGeneric;
    public Note tmpNote;
    public String sDatePart_selectCalendar;

    public EventsActivityFragment(){
        // Required empty public constructor
    }

    public static EventsActivityFragment newInstance() {
        EventsActivityFragment fragment = new EventsActivityFragment();
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState );

        if (savedInstanceState == null) {
            if (getArguments()!= null && getArguments().containsKey("Note")){
                tmpNote = (Note) getArguments().getSerializable("Note");
            } else if (getArguments()!= null && getArguments().containsKey("datepart_calendarselected")){
                sDatePart_selectCalendar = getArguments().getString("datepart_calendarselected");
            }
        }

    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_event_activity, container, false);

        //            List<Event_Object>
        objA_Event = new ArrayList<Event_Object>();
        EventObjectRepo eor = new EventObjectRepo(getActivity());
        if (tmpNote!=null){
            objA_Event = eor.getEventsByNoteId(tmpNote.getNoteId(), false);
        } else if (sDatePart_selectCalendar !=null){
//            Date tmpDate = convertDBdateStringToDate_datetime(sDatePart);  //normalize  check if this should be used convertDBdateStringToDate_datetime
            objA_Event = eor.getEventsByDateString(sDatePart_selectCalendar);
        }
        final ListView lstVGeneric = (ListView) rootView.findViewById(R.id.lstVfragment_event);
        //wvs hint:
        //            https://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view
        //            use this code to use any type of child view inside a ScrollView & perform Touch operations.
        lstVGeneric.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

//        ArrayAdapter<Notes> genericAdapter = new ArrayAdapter<Notes>(getActivity(),android.R.layout.simple_list_item_1, objA_Notes);
        EventArrayAdapter genericAdapter = new EventArrayAdapter(getActivity(),R.layout.listitem_event,objA_Event);
        lstVGeneric.setAdapter(genericAdapter);
        genericAdapter.notifyDataSetChanged();

        lstVGeneric.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent1 = new Intent(getActivity(), NoteAddActivity.class);
//                intent1.putExtra("Note", objA_Note.get(position));
//                intent1.putExtra("requestCode",EDIT_NOTE_ITEM_REQUEST);
//                startActivityForResult(intent1,EDIT_NOTE_ITEM_REQUEST);
                Intent intent1 = new Intent(getActivity(), EventAddActivity.class);
                if (tmpNote != null){ //coming from NoteAddActivity
                    intent1.putExtra("requestCode", EDIT_EVENT_INNOTE_REQUEST);
                    intent1.putExtra("Note", tmpNote);
                } else if (sDatePart_selectCalendar !=null){  //coming from Calendar
                    intent1.putExtra("requestCode", EDIT_CALENDAR_EVENT);
                    Event_Object eo = (Event_Object) objA_Event.get(position);
                    intent1.putExtra("event_object", eo);
                }
                startActivityForResult(intent1, EDIT_CALENDAR_EVENT);
            }
        });

        shouldRefreshOnResume = false;
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    https://stackoverflow.com/questions/37276576/android-studio-getting-must-implement-onfragmentinteractionlistener#37279212
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onStart() {
        super.onStart();
//        Log.i(TAG, " WVS ##3 FRAG onStart");
    }
    //wvs hint:    https://stackoverflow.com/questions/35286670/how-to-refresh-a-fragment-from-an-activity-in-android#35305687
    @Override
    public void onResume() {
        super.onResume();
//        Log.i(TAG," WVS onResume ##4 , shouldRefreshOnResume="+shouldRefreshOnResume);
        // Check should we need to refresh the fragment
        if(shouldRefreshOnResume){
            // refresh fragment wvs hint: https://stackoverflow.com/questions/20702333/refresh-fragment-at-reload#20702418
//            Log.i(TAG," WVS onResume ##4 refreshing fragment");
//            android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            androidx.fragment.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
        shouldRefreshOnResume = false;
//        Log.i(TAG," WVS onResume ##4 , reset shouldRefreshOnResume="+shouldRefreshOnResume);
    }
    @Override
    public void onPause() {
        super.onPause();
//        Log.i(TAG, " WVS ##5 onPause");
    }
    //wvs hint:    https://stackoverflow.com/questions/35286670/how-to-refresh-a-fragment-from-an-activity-in-android#35305687
    @Override
    public void onStop() {
        super.onStop();
//        Log.i(TAG," WVS onStop ##6 , shouldRefreshOnResume="+shouldRefreshOnResume);
        shouldRefreshOnResume = true;
//        Log.i(TAG," WVS onStop ##6 , reset shouldRefreshOnResume="+shouldRefreshOnResume);
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
//        Log.i(TAG, " WVS onDestroyView ##7");
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
//        Log.i(TAG," WVS onDetach ##9 ,");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, " WVS onActivityResult()");
        RefreshList();
    }

    public void RefreshList(){
//        LocationsRepo lsr = new LocationsRepo(getActivity());
//        objA_Locations = lsr.getPathNamesForList();
        EventObjectRepo eor = new EventObjectRepo(getActivity());
        if (tmpNote!=null){
            objA_Event = eor.getEventsByNoteId(tmpNote.getNoteId(), false);
        } else if (sDatePart_selectCalendar !=null){
//            Date tmpDate = convertDBdateStringToDate_datetime(sDatePart);  //normalize  check if this should be used convertDBdateStringToDate_datetime
            objA_Event = eor.getEventsByDateString(sDatePart_selectCalendar);
        }
//        genericAdapter.notifyDataSetChanged();
//        Log.i(TAG, " WVS onPageSelected) AFTER refresh objA_Locations.size="+ objA_Locations.size());
//        shouldRefreshOnResume = true;
        // refresh fragment wvs hint: https://stackoverflow.com/questions/20702333/refresh-fragment-at-reload#20702418
        Log.i(TAG," WVS RefreshList() refreshing fragment");
//            android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        androidx.fragment.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}
