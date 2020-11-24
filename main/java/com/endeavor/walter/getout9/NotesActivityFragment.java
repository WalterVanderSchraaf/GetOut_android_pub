package com.endeavor.walter.getout9;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import static com.endeavor.walter.getout9.WVSInfo.VIEW_NOTE_REQUEST;

public class NotesActivityFragment extends Fragment {
    public static String TAG = NotesActivityFragment.class.getSimpleName();

    private boolean shouldRefreshOnResume = false;
    static ArrayList<Notes> objA_Notes;
    public NotesArrayAdapter genericAdapter;
    public ListView lstVGeneric;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_notes, container,false);

        objA_Notes = new ArrayList<Notes>();
        Long NotesId;
        NotesRepo nr = new NotesRepo(getActivity());
        objA_Notes = nr.getAllNotes();

        ListView lstVGeneric = rootView.findViewById(R.id.lstVfragment_notes);

        NotesArrayAdapter genericAdapter = new NotesArrayAdapter(getActivity(), R.layout.listitem_notes, objA_Notes);
        lstVGeneric.setAdapter(genericAdapter);
        genericAdapter.notifyDataSetChanged();

        //wvs hint:
        //            https://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view
        //            use this code to use any type of child view inside a ScrollView & perform Touch operations.
//        lstVGeneric.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                v.getParent().requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });
        lstVGeneric.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = new Intent(getActivity(), NoteActivity.class);
                intent1.putExtra("Notes", objA_Notes.get(position));
                intent1.putExtra("requestCode", VIEW_NOTE_REQUEST);
                startActivity(intent1);
            }
        });


        shouldRefreshOnResume = false;
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
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

    public void RefreshList(){

        NotesRepo nr = new NotesRepo(getActivity());
        objA_Notes = nr.getAllNotes();

        // refresh fragment wvs hint: https://stackoverflow.com/questions/20702333/refresh-fragment-at-reload#20702418
        Log.i(TAG," WVS RefreshList() refreshing fragment");
//            android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        androidx.fragment.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}
