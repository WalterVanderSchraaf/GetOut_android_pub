package com.endeavor.walter.getout9;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


public class EventsActivity extends AppCompatActivity {

    private EventsActivityFragment frag;
    private String sDatePart;
    private Note tmpNote;
    TextView edtEventSource;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//      wvs todo: change below
//        setContentView(R.layout.content_event_item);
        setContentView(R.layout.activity_events);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
//          wvs disable UP Button
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        Bundle arguments = new Bundle();
//      get passed in arguments/extras datetime from calendar or Noteid from Note
        if (savedInstanceState == null){
            if (getIntent()!= null && getIntent().getExtras().containsKey("datepart_calendarselected")) {
                sDatePart = getIntent().getExtras().getString("datepart_calendarselected");
                arguments.putString("datepart_calendarselected", sDatePart);
            } else if (getIntent()!= null && getIntent().getExtras().containsKey("Note")){
                tmpNote = (Note) getIntent().getExtras().getSerializable("Note");
                arguments.putSerializable("Note", tmpNote);
            }
        }

        //                setTitle("Events from " + sDatePart);
        edtEventSource = findViewById(R.id.edtEventSource);
        edtEventSource.setText(sDatePart);

        frag = new EventsActivityFragment();
        frag.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.event_detail_container, frag)
                .commit();
// wvs hint: EventActivityFragment.class when using import android.app.Fragment instead of import android.support.v4.app.Fragment
//           will produce the following error:
//  error: no suitable method found for add(int,EventActivityFragment)
//  method FragmentTransaction.add(Fragment,String) is not applicable
//  (argument mismatch; int cannot be converted to Fragment)
//  method FragmentTransaction.add(int,Fragment) is not applicable
//  (argument mismatch; EventActivityFragment cannot be converted to Fragment)



    }
}
