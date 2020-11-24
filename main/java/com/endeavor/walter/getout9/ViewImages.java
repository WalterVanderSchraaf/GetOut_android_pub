package com.endeavor.walter.getout9;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


public class ViewImages extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_slider);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        String sNoteItem,sImages;
        sNoteItem=sImages="";

        Intent intent1 = getIntent();
        Bundle extras = intent1.getExtras();
        if (extras.containsKey("imagefiles")){
            sImages = extras.getString("imagefiles");
            if (extras.containsKey("noteitem")) {
                sNoteItem = extras.getString("noteitem");
            }
        }
        if( extras.containsKey("Note")){
            Note tmpNote = (Note) extras.getSerializable("Note");
            setTitle(tmpNote.getNoteItem());
        }

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, sImages, sNoteItem);
        viewPager.setAdapter(viewPagerAdapter);

    }
}
