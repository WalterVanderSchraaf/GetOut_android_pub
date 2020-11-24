package com.endeavor.walter.getout9;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ScratchPad extends AppCompatActivity {
    TextView txtScratchPad;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.scrachpad);
        txtScratchPad = findViewById(R.id.txtScratchPad);
        String sScratch;

        Intent intent1 = getIntent();
        if (intent1.hasExtra("scratchpad")) {
//            sScratch = intent1.getStringExtra("scratchpad");
            sScratch = intent1.getExtras().getString("scratchpad");
            txtScratchPad.setText(sScratch);
        }

//        if (savedInstanceState == null){
//             if (savedInstanceState.containsKey("scratchpad")){
//                 sScratch = savedInstanceState.getString("scratchpad");
//                 txtScratchPad.setText(sScratch);
//             }
//        }

    }
}
