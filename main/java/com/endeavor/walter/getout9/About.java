package com.endeavor.walter.getout9;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class About extends AppCompatActivity {
    ImageView imgQR;
    TextView txtLinkedinURL, txtGetOutURL;
    Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.about);
        imgQR = findViewById(R.id.imgQR);

        txtLinkedinURL = findViewById(R.id.txtLinkedInURL);
        txtGetOutURL = findViewById(R.id.txtGetOutURL);

//        imgQR.getDrawable();
//        imgQR.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        txtLinkedinURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = txtLinkedinURL.getText().toString();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, url);
                intent.putExtra(Intent.EXTRA_SUBJECT,  "Walter VanderSchraaf");
                startActivity(intent);
            }
        });

        txtGetOutURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = Utils.getGetOutURL(mContext);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, url);
                intent.putExtra(Intent.EXTRA_SUBJECT,  "Walter VanderSchraaf");
                startActivity(intent);
            }
        });

        txtGetOutURL.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String url = Utils.getGetOutURL(mContext);
                showDialogGenericUpdateValue("Update GetOut URL", "enter new value", url, new OnClickRetrunURLListenter() {
                    @Override
                    public void returnValue(String sValue) {
                        Utils.setGetOutURL(mContext, sValue);
                    }
                });

                return false;
            }
        });
    }

    public void showDialogGenericUpdateValue(String sMsg1, String sMsg2, String OrigValue, final OnClickRetrunURLListenter callbackOK) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.edit_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle(sMsg1);
        dialogBuilder.setMessage(sMsg2);
        edt.setText(OrigValue);
//      format so only number representing secconds

        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String NewValue = edt.getText().toString();
                callbackOK.returnValue(NewValue);
                return;
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    interface OnClickRetrunURLListenter {
//        https://stackoverflow.com/questions/36651655/returning-a-value-from-alertdialog
//         A "callback" is any function that is called by another function which takes the first function as a parameter.
        void returnValue(String sValue);
    }
}
