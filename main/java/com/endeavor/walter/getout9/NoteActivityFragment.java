package com.endeavor.walter.getout9;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

//import android.support.v4.app.Fragment;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.os.Environment.DIRECTORY_PICTURES;
import static com.endeavor.walter.getout9.WVSInfo.EDIT_NOTE_ITEM_REQUEST;
import static com.endeavor.walter.getout9.WVSInfo.SORTBYCHECK;
import static com.endeavor.walter.getout9.WVSInfo.SORTBYDATE;
import static com.endeavor.walter.getout9.WVSInfo.SORTBYNAME;
import static com.endeavor.walter.getout9.WVSInfo.SORTBYTAG;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link NoteActivityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NoteActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteActivityFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

//WVS: TODO: update title from "NoteActivity" to mNotes.getName()
//WVS: TODO: add navigate up logic back to MainActivity (i.e. Notes list)

    private static final String ARG_PARAM1 = "notesid";
    private static final String ARG_PARAM2 = "param2";
    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;
    // The URL to +1.  Must be a valid URL.
    private final String PLUS_ONE_URL = "http://developer.android.com";
    // TODO: Rename and change types of parameters
    private Notes mNotes;
    private String mParam2;
//    private PlusOneButton mPlusOneButton;

    private OnFragmentInteractionListener mListener;

    final public static String TAG = NoteActivityFragment.class.getSimpleName();
    //wvs hint: https://stackoverflow.com/questions/35286670/how-to-refresh-a-fragment-from-an-activity-in-android#35305687
//    wvs: todo: add onResume, OnStop shouldRefreshOnResume logic
    private boolean shouldRefreshOnResume = false;

    static ArrayList<Note> objA_Note;
    static ArrayList<Note> objA_Note_filtered;
    static ArrayList<Note> objA_Note_orig;
    ArrayList<Note> objA_tmpNote;
    public NoteArrayAdapter genericAdapter;
    public ListView lstVGeneric;


    public NoteActivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoteActivityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NoteActivityFragment newInstance(String param1, String param2) {
        NoteActivityFragment fragment = new NoteActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//WVS: HINT: https://stackoverflow.com/questions/34942492/implementing-searchview-in-toolbar-with-fragments#34942824
//     Also ensure you are calling SetHasOptionsMenu in onCreate to notify the fragment that it should participate in options menu handling.
//        setHasOptionsMenu(true);
        if (savedInstanceState == null) {
//            Log.i(TAG," WVS onCreate ##1 getArgument() != null");
//          1st time in after clicking add Notes, mNotes is null
//            Return the arguments supplied when the fragment was instantiated, if any.
            if (getArguments().containsKey("Notes")) {
                mNotes = (Notes) getArguments().getSerializable("Notes");
            }
            Activity activity = this.getActivity();
//            activity.setTitle(mNotes.getName());
            activity.setTitle("");
//            if (mNotes != null){
//                activity.setTitle(mNotes.getName());
//            }
// TODO: add name to EditText instead of title...
        } else {
//            Log.i(TAG," WVS onCreate ##1 savedInstanceState == NULL");
            if (savedInstanceState.containsKey("Notes")) {
                mNotes = (Notes) getArguments().getSerializable("Notes");
            }
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//wvs hint: on pixel infinite loop from onResume to onStop: adding or deteting user did not refresh list, but below statement resolves issue.  list is refreshed properly
//      while adding contact (ContactListActivity) SAMSUNG navigates to SessionUsersActivityFragment onCreate, onCreateView PIXEL does not
//      must add  FRAG onResume, onStop reset shouldRefreshOnResume logic

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_note_activity, container, false);

        //Find the +1 button
//        mPlusOneButton = (PlusOneButton) view.findViewById(R.id.plus_one_button);

        objA_Note = new ArrayList<Note>();
        objA_tmpNote = new ArrayList<Note>();
        NoteRepo nr = new NoteRepo(getActivity());
        if (mNotes != null){
//            Log.i(TAG," WVS onCreateView ##2 mNotes != null");
            objA_tmpNote = nr.getEachNoteByNotesId(mNotes);
            if (objA_tmpNote.size() != 0){
                objA_Note = objA_tmpNote;
            } else {
//                Log.i(TAG," WVS onCreateView ##2 note does not exist");
            }
        } else {
//            returning from NoteAddActivity? or from clicking add new note (mNotes will be null)...
//            Log.i(TAG," WVS onCreateView ##2 mNotes == NULL");
            if (getArguments() != null){
                if (getArguments().containsKey("Notes")){
                mNotes = (Notes) getArguments().getSerializable("Notes");
//                  extra data Notes is not present???
                    if (mNotes!=null) {
                        objA_tmpNote = nr.getEachNoteByNotesId(mNotes);
                        if (objA_tmpNote.size() != 0) {
                            objA_Note = objA_tmpNote;
                        } else {
                            Log.i(TAG, " WVS onCreateView note does not exist");
                        }
                    }
                }
            }

        }

        final ListView lstVGeneric = (ListView) rootView.findViewById(R.id.lstVfragment_note);
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
//      wvs may be returning back from edit noteitem based on search filter
        NoteArrayAdapter genericAdapter;
        if (objA_Note_filtered != null && objA_Note_filtered.size() != objA_Note.size()) {
            if (objA_Note_filtered.get(0).getNotesId() == objA_tmpNote.get(0).getNotesId()){
                genericAdapter = new NoteArrayAdapter(getActivity(), R.layout.listitem_note, objA_Note_filtered);
            } else {
                genericAdapter = new NoteArrayAdapter(getActivity(), R.layout.listitem_note, objA_Note);
            }
        } else {
            genericAdapter = new NoteArrayAdapter(getActivity(), R.layout.listitem_note, objA_Note);
        }

        lstVGeneric.setAdapter(genericAdapter);
        genericAdapter.notifyDataSetChanged();

        lstVGeneric.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = new Intent(getActivity(), NoteAddActivity.class);
                if (objA_Note_filtered != null && objA_Note_filtered.size() != objA_Note.size()){
                    if (objA_Note_filtered.get(0).getNotesId() == objA_tmpNote.get(0).getNotesId()) {
                        intent1.putExtra("Note", objA_Note_filtered.get(position));
                    } else {
                        intent1.putExtra("Note", objA_Note.get(position));
                    }
                } else {
                    intent1.putExtra("Note", objA_Note.get(position));
                }
                intent1.putExtra("requestCode",EDIT_NOTE_ITEM_REQUEST);
                startActivityForResult(intent1,EDIT_NOTE_ITEM_REQUEST);
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
//    public void onAttach(Context mContext) {
//        super.onAttach(mContext);
//        if (mContext instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) mContext;
//        } else {
//            throw new RuntimeException(mContext.toString()
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

    public void sortList(int SortType, int order){
        //            http://www.worldbestlearningcenter.com/tips/Android-sort-ListView.htm
//        Collections.sort(objA_Note, new SorterByName(order));

        if (SortType == SORTBYNAME) {
            Collections.sort(objA_Note, new SorterByName(order));
        } else if (SortType == SORTBYDATE) {
            Collections.sort(objA_Note, new SorterByDate(order));
        } else if (SortType == SORTBYCHECK) {
            Collections.sort(objA_Note, new SorterByCheck(order));
        } else if (SortType == SORTBYTAG) {
            Collections.sort(objA_Note, new SorterByTag(order));
        }

        NoteArrayAdapter genericAdapter = new NoteArrayAdapter(getActivity(),R.layout.listitem_note,objA_Note);
        ListView lstVGeneric = (ListView) getActivity().findViewById(R.id.lstVfragment_note);
        lstVGeneric.setAdapter(genericAdapter);
        genericAdapter.notifyDataSetChanged();

    }
    static class SorterByName implements Comparator<Note> {
        int order=-1;
        SorterByName(int order){
            this.order=order;
        }

        public int compare(Note ob1,Note ob2){
            int RetVal;
            Log.i(TAG, " WVS " +String.valueOf(ob1.getNoteId()) + " " + ob1.getNoteItem() + " " + ob1.getDate() + ", "  + String.valueOf(ob1.getNotesId()) + ", " + String.valueOf(ob2.getNoteId()) + " " + ob2.getNoteItem()+" "+ ob2.getDate()+ String.valueOf(ob2.getNotesId()) );
            if(ob1.getNoteItem().compareToIgnoreCase(ob2.getNoteItem())==0){
                if (ob1.getDate().compareToIgnoreCase(ob2.getDate())==0){
                    Log.i(TAG," WVS A 0");
                    return 0;
                }
                else if (ob1.getDate().compareToIgnoreCase(ob2.getDate())<0){
                    Log.i(TAG," WVS B order");
                    return order;
                }
                else {
                    Log.i(TAG," WVS C -order");
                    return (-1 * order);
                }
            }
            else if(ob1.getNoteItem().compareToIgnoreCase(ob2.getNoteItem())<0) {
                Log.i(TAG," WVS D order");
                return order;
            }
            else{
                Log.i(TAG," WVS G -order");
                return (-1 * order);
            }
        }
    }

    static class SorterByDate implements Comparator<Note> {
        int order=-1;
        SorterByDate(int order){
            this.order=order;
        }

        public int compare(Note ob1,Note ob2){
            int RetVal;
            Log.i(TAG, " WVS " + String.valueOf(ob1.getNoteId()) + " " + ob1.getNoteItem() + " " + ob1.getDate() + ", "  + String.valueOf(ob1.getNotesId()) + ", " + String.valueOf(ob2.getNoteId()) + " " + ob2.getNoteItem()+" "+ ob2.getDate()+ String.valueOf(ob2.getNotesId()) );
            if(ob1.getDate().compareTo(ob2.getDate())==0){
//                if (ob1.getNoteItem().compareTo(ob2.getNoteItem())==0){
                if (ob1.getNoteItem().compareToIgnoreCase(ob2.getNoteItem())==0){
                    Log.i(TAG," WVS A 0");
                    return 0;
                }
                else if (ob1.getNoteItem().compareToIgnoreCase(ob2.getNoteItem())<0){
                    Log.i(TAG," WVS B order");
                    return order;
                }
                else {
                    Log.i(TAG," WVS C -order");
                    return (-1 * order);
                }
            }
            else if(ob1.getDate().compareToIgnoreCase(ob2.getDate())<0) {
                Log.i(TAG," WVS D order");
                return order;
            }
            else{
                Log.i(TAG," WVS G -order");
                return (-1 * order);
            }
        }
    }

    static class SorterByCheck implements Comparator<Note> {
        int order = -1;

        SorterByCheck(int order) {
            this.order = order;
        }

        public int compare(Note ob1, Note ob2) {
            int RetVal;
            Log.i(TAG, " WVS " + String.valueOf(ob1.getChecked()) + " " + ob1.getTag() + " " + ob1.getNoteItem() + ", " + ", " + String.valueOf(ob2.getChecked()) + " " + ob2.getTag() + " " + ob2.getNoteItem());
            if (ob1.getChecked().compareTo(ob2.getChecked()) == 0) {
                if (ob1.getTag().compareToIgnoreCase(ob2.getTag()) == 0 && ob1.getNoteItem().compareToIgnoreCase(ob2.getNoteItem()) == 0) {
                    Log.i(TAG, " WVS A return 0");
                    return 0;
                } else if (ob1.getTag().compareToIgnoreCase(ob2.getTag()) == 0 && ob1.getNoteItem().compareToIgnoreCase(ob2.getNoteItem()) < 0) {
                    Log.i(TAG, " WVS B return order " + order);
                    return order;
                } else if (ob1.getTag().compareToIgnoreCase(ob2.getTag()) == 0 && ob1.getNoteItem().compareToIgnoreCase(ob2.getNoteItem()) > 0) {
                    Log.i(TAG, " WVS BB return - order " + order);
                    return (-1 * order);
                } else if (ob1.getTag().compareToIgnoreCase(ob2.getTag()) < 0 ) {
                    Log.i(TAG, " WVS C return order " + order);
                    return order;
                } else {
                    Log.i(TAG, " WVS CC return - order " + order);
                    return (-1 * order);
                }
            } else if (ob1.getChecked().compareTo(ob2.getChecked()) > 0) {
                Log.i(TAG, " WVS D return order " + order);
                return order;
            } else
                Log.i(TAG, " WVS E return - order " + order);
                return (-1 * order);
        }
    }
    static class SorterByTag implements Comparator<Note> {
        int order=-1;
        SorterByTag(int order){
            this.order=order;
        }

        public int compare(Note ob1,Note ob2){
            int RetVal;
            if(ob1.getTag().compareTo(ob2.getTag())==0){
                if (ob1.getNoteItem().compareToIgnoreCase(ob2.getNoteItem())==0){
                    return 0;
                }
                else if (ob1.getNoteItem().compareToIgnoreCase(ob2.getNoteItem())>0){
                    return order;
                }
                else {
                    return (-1 * order);
                }
            }
            else if(ob1.getTag().compareTo(ob2.getTag())>0) {
                return order;
            }
            else
                return (-1 * order);
            }

    }
    public File WriteNotebyNotedId (boolean checked, boolean linenumbers) {
        File sFileName = null;
        NoteRepo nr = new NoteRepo(getActivity());
        sFileName = nr.savefile_NotebyNotesId(mNotes, checked, linenumbers);
        return sFileName;
    }


    public void UpdateListOfItems() {
//      get list of item from db
//      get list from adapter and compare. update any deltas
        Note tmp_note;
        Note tmp_dbnote;
        NoteRepo nr;
        nr = new NoteRepo(getActivity());

        ListView lstVGeneric = (ListView) getActivity().findViewById(R.id.lstVfragment_note);
        int cnt = lstVGeneric.getCount();
        for (int i = 0; i < cnt; i++){
//get Note from ArrayList<Note> in ListView
            tmp_note= (Note) lstVGeneric.getItemAtPosition(i);
            tmp_dbnote = nr.getNoteById(tmp_note.getNoteId());

            if (tmp_note.getNoteItem().compareTo(tmp_dbnote.getNoteItem()) != 0){
//                update row todo: update note_list_item.xls edtNote2
                System.out.println(TAG + ": updating  " + tmp_dbnote.getNoteItem() + " to " + tmp_note.getNoteItem());
                nr.update(tmp_note, true);
            } else if (tmp_note.getChecked().compareTo(tmp_dbnote.getChecked()) !=0 ) {
                System.out.println(TAG + ": updating  checked " + tmp_dbnote.getNoteItem() + " to " + tmp_note.getNoteItem());
                nr.update(tmp_note, false);
            }
        }
    }

    public void DeleteCheckedItems() {
//      get list from adapter and compare. delete checked
        Note tmp_note;
        NoteRepo nr;
        EventObjectRepo eor;
        eor = new EventObjectRepo(getActivity());
        nr = new NoteRepo(getActivity());

        ListView lstVGeneric = (ListView) getActivity().findViewById(R.id.lstVfragment_note);
        int cnt = lstVGeneric.getCount();
//        int cntDelete = 0;
        for (int i = 0; i < cnt; i++){
            //get Note from ArrayList<Note> in ListView
            tmp_note= (Note) lstVGeneric.getItemAtPosition(i);
//            tmp_dbnote = nr.getNoteById(tmp_note.getNoteId());

            if (tmp_note.getChecked() == 1){
                System.out.println(TAG + ": deleting Note checked " +  tmp_note.getNoteItem());
                Delete_Ind_Note_Event_Image(tmp_note, getContext());
//                nr.delete(tmp_note.getNoteId());
//                eor.deletebyNoteId(tmp_note.getNoteId());
//                cntDelete ++;
////              delete images associated with note item
//                String images = tmp_note.getImage();
//                String [] imageArray = images.split(",");
//                if (images.length() > 0) {
//                    File imagepath;
//                    for (String img : imageArray) {
//                        imagepath = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES))+"/" + img );
//                        if (imagepath.exists()){
//                            if (nr.DoesAnotherImageExist(tmp_note.getNotesId(),img)==0) {
//                                imagepath.delete();
//                            }
//                        }
//                    }
//                }
            }
        }
//        System.out.println(TAG + ": deleting " + Integer.toString(cntDelete) +" out of " + Integer.toString(cnt));
// does not refresh list?
//        NoteArrayAdapter tmpAdapter;
//        tmpAdapter = (NoteArrayAdapter) lstVGeneric.getAdapter();
//        tmpAdapter.notifyDataSetChanged();
    }

    public void DeleteNoteOrItems(boolean blnwholeNotes, boolean blncheckedItems) {
//      get list from adapter and compare. delete whole Notes or individual Note items or delete checked items
        Note tmp_note;
        NoteRepo nr;
        EventObjectRepo eor;
        nr = new NoteRepo(getActivity());
        eor = new EventObjectRepo(getActivity());
        NotesRepo nsr=null;

        ListView lstVGeneric = (ListView) getActivity().findViewById(R.id.lstVfragment_note);
        int cnt = lstVGeneric.getCount();
//        int cntDelete = 0;
        Integer notesid=mNotes.getNotesId();
        for (int i = 0; i < cnt; i++){
            //get Note from ArrayList<Note> in ListView
            tmp_note= (Note) lstVGeneric.getItemAtPosition(i);
            if (i==0){
                notesid = tmp_note.getNotesId();
            }

            if (!blnwholeNotes & blncheckedItems){
                if (tmp_note.getChecked() == 1){
                    System.out.println(TAG + ": deleting checked item: " +  tmp_note.getNoteItem());
                    Delete_Ind_Note_Event_Image(tmp_note, getContext());
//                    nr.delete(tmp_note.getNoteId());
//                    eor.deletebyNoteId(tmp_note.getNoteId());
//                    cntDelete ++;
////              delete images associated with note item
//                    String images = tmp_note.getImage();
//                    String [] imageArray = images.split(",");
//                    if (images.length() > 0) {
//                        File imagepath;
//                        for (String img : imageArray) {
//                            imagepath = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES))+"/" + img );
//                            if (imagepath.exists()){
//                                if (nr.DoesAnotherImageExist(tmp_note.getNotesId(),img) == 0) {
//                                    imagepath.delete();
//                                }
//                            }
//                        }
//                    }
                }
            } else if (!blncheckedItems) {
                System.out.println(TAG + ": deleting item: " +  tmp_note.getNoteItem());
                Delete_Ind_Note_Event_Image(tmp_note, getContext());
//                nr.delete(tmp_note.getNoteId());
//                eor.deletebyNoteId(tmp_note.getNoteId());
//                cntDelete ++;
////              delete images associated with note item
//                String images = tmp_note.getImage();
//                String [] imageArray = images.split(",");
//                if (images.length() > 0) {
//                    File imagepath;
//                    for (String img : imageArray) {
//                        imagepath = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES))+"/" + img );
//                        if (imagepath.exists()){
//                            if (nr.DoesAnotherImageExist(tmp_note.getNotesId(),img) == 0) {
//                                Log.i(TAG, " WVS deleting image " + imagepath.toString());
//                                imagepath.delete();
//                            }
//                        }
//                    }
//                }
            }
        }
//        System.out.println(TAG + ": deleting " + Integer.toString(cntDelete) +" out of " + Integer.toString(cnt));
        if (blnwholeNotes){
            nsr = new NotesRepo(getActivity());
            nsr.delete(notesid);
            System.out.println(TAG + ": deleting NotesId " + notesid.toString() );
        }
    }
    public static void Delete_Ind_Note_Event_Image(Note tmp_note, Context context){

        NoteRepo nr;
        EventObjectRepo eor;
        nr = new NoteRepo(context);
        eor = new EventObjectRepo(context);
        NotesRepo nsr=null;

        nr.delete(tmp_note.getNoteId());
        eor.deletebyNoteId(tmp_note.getNoteId());
//      delete images associated with note item
        String images = tmp_note.getImage();
        String [] imageArray = images.split(",");
        if (images.length() > 0) {
            File imagepath;
            for (String img : imageArray) {
                imagepath = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES))+"/" + img );
                if (imagepath.exists()){
                    if (nr.DoesAnotherImageExist(tmp_note.getNotesId(),img) == 0) {
                        Log.i(TAG, " WVS deleting image " + imagepath.toString());
                        imagepath.delete();
                    }
                }
            }
        }

    }
    public void Filter_the_Adapter_lstV(String charText){

        Log.i(TAG," WVS Filter_the_Adapter_lstV, charText = " + charText);
        Integer iEmptySpc = charText.indexOf(" ");
        if (iEmptySpc > 0){
            charText = charText.substring(0,iEmptySpc-1);
        }

        if (genericAdapter == null){
//wvs: todo: objA_Notes needs to be the original list everytime...
            NoteRepo nr = new NoteRepo(getActivity());
            if ((mNotes!=null && charText.equals("") ) || objA_Note_filtered == null ) {
//                Log.i(TAG, " WVS Filter_the_Adapter_lstV genericAdapter == NULL, #1 filtering with mNotes");
//            if (objA_Note_filtered != null && objA_Note_filtered.size() != objA_Note.size())
                objA_Note_filtered = nr.getEachNoteByNotesId(mNotes);
            } else {
                if (objA_Note_filtered != null && objA_Note_filtered.size() > 0 && objA_Note_filtered.get(0).getNotesId() == mNotes.getNotesId()) {
                    Log.i(TAG, " WVS Filter_the_Adapter_lstV genericAdapter == NULL, filtering with objA_Note_filtered");
                } else {
//                    Log.i(TAG, " WVS Filter_the_Adapter_lstV genericAdapter == NULL, #2 filtering with mNotes");
                    objA_Note_filtered = nr.getEachNoteByNotesId(mNotes);
                }
            }

            NoteArrayAdapter genericAdapter = new NoteArrayAdapter(getActivity(),R.layout.listitem_note,objA_Note_filtered);
            ListView lstVGeneric = (ListView) getActivity().findViewById(R.id.lstVfragment_note);
            lstVGeneric.setAdapter(genericAdapter);
//      wvs .filter_note will eventually update objA_Note_filtered to subset based on text (via notifyDataSetChanged())
            genericAdapter.filter_note(charText);
        } else {
            Log.i(TAG, " WVS Filter_the_Adapter_lstV genericAdapter != null, filtering with text = " + charText);
            genericAdapter.filter_note(charText);
        }
    }


    public void ToggleTags(boolean bhide_tags){

        NoteArrayAdapter genericAdapter = new NoteArrayAdapter(getActivity(),R.layout.listitem_note,objA_Note);
        ListView lstVGeneric = (ListView) getActivity().findViewById(R.id.lstVfragment_note);
        lstVGeneric.setAdapter(genericAdapter);

        genericAdapter.ToggleTags(bhide_tags);
    }
    public void ToggleChecks(boolean bHideChecks){
        if (mNotes != null) {
            NoteRepo nr = new NoteRepo(getActivity());
            objA_Note_orig = nr.getEachNoteByNotesId(mNotes);
            NoteArrayAdapter genericAdapter = new NoteArrayAdapter(getActivity(),R.layout.listitem_note,objA_Note_orig);
            ListView lstVGeneric = (ListView) getActivity().findViewById(R.id.lstVfragment_note);
            lstVGeneric.setAdapter(genericAdapter);
            genericAdapter.Toggle_Checks(bHideChecks);
        }
    }

    public void ToggleChecksTags(boolean bHideChecks, boolean bHideTags){
        if (mNotes != null) {
            NoteRepo nr = new NoteRepo(getActivity());
            objA_Note_orig = nr.getEachNoteByNotesId(mNotes);
            NoteArrayAdapter genericAdapter = new NoteArrayAdapter(getActivity(),R.layout.listitem_note,objA_Note_orig);
            ListView lstVGeneric = (ListView) getActivity().findViewById(R.id.lstVfragment_note);
            lstVGeneric.setAdapter(genericAdapter);
            genericAdapter.ToggleChecksTags(bHideChecks,bHideTags);
        }
    }
    public void RefreshList(){

        NoteRepo nr = new NoteRepo(getActivity());
        objA_Note = nr.getEachNoteByNotesId(mNotes);

        // refresh fragment wvs hint: https://stackoverflow.com/questions/20702333/refresh-fragment-at-reload#20702418
        Log.i(TAG," WVS RefreshList() refreshing fragment");
//            android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        androidx.fragment.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}
