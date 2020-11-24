package com.endeavor.walter.getout9;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class DateTimePicker extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.time_picker);
        final TimePicker tp = findViewById(R.id.time_picker);
        Button btSet = findViewById(R.id.btnSet);

//        Intent intent1 = getIntent();
//        Note tmpNote = (Note) intent1.getSerializableExtra("Note");
//        if (tmpNote != null){
////          coming from NoteAddActivity, CustomCalenderActivity
//
//        }

        Calendar cal = Calendar.getInstance();
        int hr = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);


//        Calendar.AM_PM;
        tp.setHour(hr);
        tp.setMinute(min);

        btSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              back to calenders
//                SimpleDateFormat
//              used for display only...
//                int itmpHr = tp.getHour();
//                Integer iHr = (itmpHr > 12)? itmpHr - 12: (itmpHr == 0) ? 12: itmpHr;  // 0 is 12 am, 12 is 12 pm
//                String sAMPM = (itmpHr >= 12)? " PM": " AM";
//                String sTime = String.format("%02d",iHr) + ":" + String.format("%02d",tp.getMinute()) + sAMPM;
                String sTime = WVS_Utils.TimePicker_formatAMPM(tp.getHour(), tp.getMinute());

                Intent intent1 = getIntent();
                intent1.putExtra("time",sTime);

                setResult(RESULT_OK,intent1);
                finish();
            }
        });
    }
}
