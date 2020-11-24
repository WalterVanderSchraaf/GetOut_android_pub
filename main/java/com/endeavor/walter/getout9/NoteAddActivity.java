package com.endeavor.walter.getout9;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.work.Data;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import static android.os.Environment.DIRECTORY_PICTURES;
import static com.endeavor.walter.getout9.WVSInfo.ADD_EVENT_TONOTE_REQUEST;
import static com.endeavor.walter.getout9.WVSInfo.ADD_NOTE_ITEM_REQUEST;
import static com.endeavor.walter.getout9.WVSInfo.CALENDAR_LONGCLICKTOADD;
import static com.endeavor.walter.getout9.WVSInfo.EDIT_EVENT_INNOTE_REQUEST;
import static com.endeavor.walter.getout9.WVSInfo.EDIT_NOTE_ITEM_REQUEST;
import static com.endeavor.walter.getout9.WVSInfo.EVENTTYPE_EMAIL;
import static com.endeavor.walter.getout9.WVSInfo.EVENTTYPE_NOTIFICATION;
import static com.endeavor.walter.getout9.WVSInfo.EVENTTYPE_SCHEDULE_NOTIFICATION;
import static com.endeavor.walter.getout9.WVSInfo.EVENTTYPE_TEXTMSG;
import static com.endeavor.walter.getout9.WVSInfo.EVENT_DELETED;
import static com.endeavor.walter.getout9.WVSInfo.IMPORT_IMAGE;
import static com.endeavor.walter.getout9.WVSInfo.IMPORT_IMAGE_ABOVEKITKAT;
import static com.endeavor.walter.getout9.WVSInfo.RESULT_DELETE;
import static com.endeavor.walter.getout9.WVSInfo.START_CAMERA;
import static com.endeavor.walter.getout9.WVSInfo.VIEW_CALENDAR;
import static com.endeavor.walter.getout9.WVS_Utils.PopulateLinearLayoutWithImages;
import static com.endeavor.walter.getout9.WVS_Utils.StripString;
import static com.endeavor.walter.getout9.WVS_Utils.convertDBdateStringToString_dateAMPM;
import static com.endeavor.walter.getout9.WVS_Utils.convertTimePicker_formatAMPM_To_DBdateString;
import static com.endeavor.walter.getout9.WVS_Utils.translateWeekdayNumbers;

public class NoteAddActivity extends AppCompatActivity {
    public final static String TAG = NoteAddActivity.class.getSimpleName();
    final Context mContext = this;
    //    private NoteAddActivityFragment frag;
    Notes tmpNotes;
    Note tmpNote;
    EditText edtNoteItem, edtNoteCost;
    TextView edtNotetag,txtNoteId, txtNotesId, txtEventId, txtImage, txtImageDelete, txtcontentnote_lbl, txtcontentnote_lbl2, txtEventAlarmlbl, txtEventContact, txtcontentcost_lbl;
    Button btnSave, btnCancel, btnNext, btnCamera, btnAddPic;
    LinearLayout myGallery;
    HorizontalScrollView myHorScrollView;
    ScrollView myScrollView;
    //    scrollvNoteItem
    Integer reqCode;
    Integer DefinitionLimit=10;
    ArrayList<Event_Object> mEvents;
    Event_Object tmpEOfromTimePicker = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_item_add);

        Intent intent2 = getIntent();
        reqCode = intent2.getExtras().getInt("requestCode");
//      wvs: hint: currently only time tmpNotes!=null is when adding a new note
        tmpNotes = (Notes) getIntent().getSerializableExtra("Notes");

        if (reqCode == ADD_NOTE_ITEM_REQUEST){
//            Bundle arguments = new Bundle();
//            arguments.putSerializable("Notes",tmpNotes);
            Log.i(TAG, " WVS onCreate - reqCode == ADD_NOTE_ITEM_REQUEST");
            setTitle("Add Note");
        } else if (reqCode == EDIT_NOTE_ITEM_REQUEST){
            tmpNote = (Note) getIntent().getSerializableExtra("Note");
            Log.i(TAG, " WVS onCreate - reqCode == EDIT_NOTE_ITEM_REQUEST");
            setTitle("Edit Note");
        }

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
//            Log.i(TAG, " WVS #1b onCreate savedInstanceState == NULL");
        } else {
//            Log.i(TAG, " WVS #1b onCreate savedInstanceState != null");
//            wvs: hint: test if tmpNotes is null
            if (tmpNotes==null){
                tmpNotes = (Notes) savedInstanceState.getSerializable("Notes");
                Log.i(TAG,"tmpNotes == NULL, resetting from bundle");
            }
//            Intent intent3 = getIntent();
//            Contact tmpContact = (Contact) intent3.getSerializableExtra("addcontact");
        }

        txtcontentnote_lbl = (TextView) findViewById(R.id.txtcontentnote_lbl);
        txtcontentnote_lbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText tmpNotetag = (EditText) findViewById(R.id.edtcontentnote_Tag);
                final EditText tmpNoteItem = (EditText) findViewById(R.id.edtcontentnote_NoteName);
                final String lookup=tmpNoteItem.getText().toString().trim();
                if (lookup.length()!=0){
                    tmpNotetag.setText("");
                    if(tmpNoteItem.getText().toString().split(" ").length <= 2){
                        Ion.with(getApplicationContext()).load("https://www.dictionaryapi.com/api/v1/references/sd3/xml/" + lookup + "?key=e236effc-24a4-4eff-8047-62488429f559")
                                .asString().setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {

                                ArrayList<String> def=null;
                                def = ParseMWDefinition(lookup, result);
                                if (def != null){
                                    int cnt = def.size();
                                    if (cnt == 0){
                                        tmpNotetag.setText("no definition found");
                                    }else {
                                        String definition = "";
                                        for (int i = 0; i < cnt; i++) {
//                                  for (int i = 0; i < cnt; i += 2) {
                                            definition = definition + def.get(i).toString() + " ";
                                        }
                                        tmpNotetag.setText(definition);
                                    }
                                } else {
                                    tmpNotetag.setText("no definition found");
                                }
//                            getSupportFragmentManager().beginTransaction().detach(frag).attach(frag).commit();
                            }
                        });
                    }
                }

            }
        });

        txtcontentnote_lbl2 = (TextView) findViewById(R.id.txtcontentnote_lblTag);
        txtcontentnote_lbl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                change DefinitionLimit from 25 to ?
//                http://www.androidsnippets.com/prompt-user-input-with-an-alertdialog.html

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder.setTitle("Set Definition limit");
                builder.setMessage("Change number of definition " + DefinitionLimit);


// Set an EditText view to get user input
                final EditText input = new EditText(v.getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        // Do something with value!
                        DefinitionLimit = Integer.valueOf(value);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                builder.show();

            }
        });

        txtEventId = findViewById(R.id.txtcontentnote_EventId);
        txtEventAlarmlbl = findViewById(R.id.txtEventAlarmlbl);
        txtEventContact = findViewById(R.id.txtEventContact);

        if (tmpNote != null) {
            EventObjectRepo eor = new EventObjectRepo(mContext);
            mEvents = eor.getEventsByNoteId(tmpNote.getNoteId(),false);
            if (mEvents != null) {
                for (int i = 0; i < mEvents.size(); i++) {
//              should be one event: OneTime, Weekly, Monthly, Yearly, Intermittent
                    String stmpEventAlarm = "";
                    if (mEvents.get(i).getEventType().contains(EVENTTYPE_SCHEDULE_NOTIFICATION)){
                        String sRepeat =  translateWeekdayNumbers(mEvents.get(i).getRepeat());
                        if (!sRepeat.equals("")) {
                            sRepeat = "[" + sRepeat + "]";
                        }
                        stmpEventAlarm = mEvents.get(i).getEventType() + ": " +  mEvents.get(0).getOccurance() + sRepeat + ", " + convertDBdateStringToString_dateAMPM(mEvents.get(i).getDate());
                    } else {
                        stmpEventAlarm = mEvents.get(i).getEventType() + ": " +  convertDBdateStringToString_dateAMPM(mEvents.get(i).getDate());
                    }
                    txtEventAlarmlbl.setText(stmpEventAlarm);  // convert to convertDBdateStringToString_dateAMPM
                    txtEventAlarmlbl.setTag(mEvents.get(i).getId());
                    txtEventId.setText(Integer.toString(mEvents.get(i).getId()) );
                    txtEventContact.setText(mEvents.get(0).getToContact());
                }
            }
        }

        txtEventAlarmlbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reqCode == EDIT_NOTE_ITEM_REQUEST) {
                    if (txtEventAlarmlbl.getText().toString().contains(EVENT_DELETED)){
                        Toast.makeText(mContext,"Must SAVE note first before Adding another event",Toast.LENGTH_LONG).show();
                    } else {
                        String sCalendarLabel = txtEventAlarmlbl.getText().toString();
                        if (sCalendarLabel.equals("") || sCalendarLabel.equals(getResources().getString(R.string.txtEventAlarmlbl))){
                            sCalendarLabel = CALENDAR_LONGCLICKTOADD;
                            Intent intent1 = new Intent(mContext, EventAddActivity.class);
                            if (tmpNote != null){ //coming from NoteAddActivity
                                intent1.putExtra("requestCode", ADD_EVENT_TONOTE_REQUEST);
                                intent1.putExtra("Note", tmpNote);
                            }
                            startActivityForResult(intent1, ADD_EVENT_TONOTE_REQUEST);
                        } else {
                            Intent intent1 = new Intent(mContext, EventAddActivity.class);
                            if (tmpNote != null){ //coming from NoteAddActivity
                                intent1.putExtra("requestCode", EDIT_EVENT_INNOTE_REQUEST);
                                intent1.putExtra("Note", tmpNote);
                                Event_Object eo = (Event_Object) mEvents.get(0);
                                intent1.putExtra("event_object", eo);
                            }
                            startActivityForResult(intent1, EDIT_EVENT_INNOTE_REQUEST);
                        }


//                        Intent intent1 = new Intent(mContext, CustomCalendarActivity2.class);
//                        if (tmpNote != null){
//                            intent1.putExtra("Note", tmpNote);
//                            intent1.putExtra("calendar_lbl", sCalendarLabel  + ": " + edtNoteItem.getText());
//                            intent1.putExtra("contact_lbl", txtEventContact.getText().toString());
//                            if (txtEventAlarmlbl.getText().toString().contains(getResources().getString(R.string.txtEventDatelbl))){  //original Event Time - click to add/modify
//                                intent1.putExtra("requestCode", ADD_EVENT_TONOTE_REQUEST);
//                                startActivityForResult(intent1, ADD_EVENT_TONOTE_REQUEST);  //requestCode – If >= 0, this code will be returned in onActivityResult() when the activity exits.
//                            } else {
//                                intent1.putExtra("requestCode", EDIT_EVENT_INNOTE_REQUEST);
//                                startActivityForResult(intent1, EDIT_EVENT_INNOTE_REQUEST);
//                            }
//                        } else if (txtEventId.getText()!=null && !txtEventId.getText().equals("eventid")) {
//                            Integer iEventId = Integer.parseInt( txtEventId.getText().toString());
//                            intent1.putExtra("eventid" , iEventId);
//                            intent1.putExtra("requestCode", reqCode);
//                        }
                    }
                } else {
                    Toast.makeText(mContext,"Must SAVE note first before Adding event",Toast.LENGTH_LONG).show();
                }
            }
        });

        edtNoteItem = findViewById(R.id.edtcontentnote_NoteName);
        edtNoteItem.setSelectAllOnFocus(true);
        edtNoteItem.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        edtNoteItem.setRawInputType(InputType.TYPE_CLASS_TEXT);

        edtNotetag = (EditText) findViewById(R.id.edtcontentnote_Tag);
        edtNotetag.setSelectAllOnFocus(true);
        edtNotetag.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edtNotetag.setRawInputType(InputType.TYPE_CLASS_TEXT);

        edtNoteCost = findViewById(R.id.edtcontentnote_Cost);

        edtNoteCost.addTextChangedListener(new TextWatcher() {
            boolean mEditing;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//              wvs http://www.java2s.com/Code/Android/UI/ConvertinputvaluetoCurrencyinTextWatcher.htm
                if(!mEditing) {
                    mEditing = true;
                    String digits = s.toString().replaceAll("\\D", "");
                    Log.i(TAG, " WVS AFTER TEXT CHANGE: " + digits);
                    NumberFormat nf = NumberFormat.getCurrencyInstance();
                    try{
                        String formatted = nf.format(Double.parseDouble(digits)/100);
                        s.replace(0, s.length(), formatted);
                    } catch (NumberFormatException nfe) {
                        s.clear();
                    }
                    mEditing = false;
                }
            }
        });
        txtNoteId = findViewById(R.id.txtcontentnote_NoteId);
        txtNotesId = findViewById(R.id.txtcontentnote_NotesId);

        txtImage = findViewById(R.id.txtImage);
        txtImageDelete = findViewById(R.id.txtImageDelete);

        myGallery = findViewById(R.id.myGalleryNoteItem);
        myHorScrollView = findViewById(R.id.horscrollv);
        myScrollView = findViewById(R.id.scrollvNoteItem);

        btnSave = findViewById(R.id.btnNoteSave);
        btnCamera = findViewById(R.id.btnCamera);
        btnCancel = findViewById(R.id.btnNoteCancel);
        btnNext = findViewById(R.id.btnNext);
        btnAddPic = findViewById(R.id.btnAddPic);


        if (reqCode == ADD_NOTE_ITEM_REQUEST){
            txtNotesId.setText(tmpNotes.getNotesId().toString());
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            Log.i(TAG, " WVS onCreate SHOW KEYBOARD");

        } else if (reqCode == EDIT_NOTE_ITEM_REQUEST){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//            Log.i(TAG, " WVS onCreate #1 HIDING KEYBOARD");

            txtNotesId.setText(tmpNote.getNotesId().toString());
            txtNoteId.setText(tmpNote.getNoteId().toString());

            edtNoteItem.setText(tmpNote.getNoteItem());
            edtNotetag.setText(tmpNote.getTag());
            edtNoteCost.setText(tmpNote.getCost().toString());
            txtImage.setText(tmpNote.getImage());
            btnNext.setVisibility(View.GONE);
//          get height dimension
            ViewGroup.LayoutParams params = myScrollView.getLayoutParams();

            if (tmpNote.getImage().length()>0) {
                myHorScrollView.setVisibility(View.VISIBLE);
//                params.height = 656;
//                myScrollView.setLayoutParams(params);
//                myScrollView.setMinimumHeight(250);
                PopulateLinearLayoutWithImages(getApplication(), myGallery, tmpNote.getImage(), new MyViewOnClickListener());
            } else {
                myHorScrollView.setVisibility(View.GONE);
                params.height = params.height * 2;
                myScrollView.setLayoutParams(params);

//                myScrollView.setMinimumHeight(500);

            }
        }
//      adjust height of myHorScrollView
//      https://stackoverflow.com/questions/10159372/android-view-layout-width-how-to-change-programmatically
//        if (tmpNote.getImage().length()>0){
////            WindowManager.LayoutParams params = (WindowManager.LayoutParams) myHorScrollView.getLayoutParams();
////            HorizontalScrollView.LayoutParams params = (HorizontalScrollView.LayoutParams) myHorScrollView.getLayoutParams();
////            HorizontalScrollView.LayoutParams params = (HorizontalScrollView.LayoutParams) new HorizontalScrollView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT );
////            params.height = (int) 170;
////            myHorScrollView.setLayoutParams(params);
//            myHorScrollView.setMinimumHeight(170);
//            myGallery.setMinimumHeight(170);
//
//        }else {
////            HorizontalScrollView.LayoutParams params = (HorizontalScrollView.LayoutParams) new HorizontalScrollView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,10 );
////            HorizontalScrollView.LayoutParams params = (HorizontalScrollView.LayoutParams) myHorScrollView.getLayoutParams();
////            params.height = (int) 170;
////            myHorScrollView.setLayoutParams(params);
//            myHorScrollView.setMinimumHeight(10);
//            myGallery.setMinimumHeight(10);
//        }

//        btnSave.setOnClickListener(this);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //wvs: hint insert note, update adapter (this is done via NoteActivityFragment (shouldRefreshOnResume) onCreateView, onResume, onStop
                Integer iNotesId;
                long lNoteId;
                String sNoteItem, sDate;
                NoteRepo nr;
                Intent returnIntent = getIntent();
                int requestCode = returnIntent.getExtras().getInt("requestCode");
                nr = new NoteRepo(mContext);

                Log.i(TAG, " WVS btnSave - else");
                if (requestCode == ADD_NOTE_ITEM_REQUEST){
                    Log.i(TAG, " WVS btnSave onClick requestCode == ADD_NOTE_ITEM_REQUEST");
                } else if (requestCode == EDIT_NOTE_ITEM_REQUEST) {
                    Log.i(TAG, " WVS btnSave onClick requestCode == EDIT_NOTE_ITEM_REQUEST");
                }

                if (Integer.valueOf(requestCode) == Integer.valueOf(ADD_NOTE_ITEM_REQUEST)) {
                    tmpNote = new Note(0, edtNoteItem.getText().toString(),"", edtNotetag.getText().toString(), Integer.valueOf(txtNotesId.getText().toString()),0,txtImage.getText().toString() , edtNoteCost.getText().toString(), null);
                    lNoteId = nr.insert(tmpNote);
                    tmpNote.setNoteId((int) lNoteId);
                }
                if (Integer.valueOf(requestCode) == Integer.valueOf(EDIT_NOTE_ITEM_REQUEST)) {
                    tmpNote.setNoteItem(edtNoteItem.getText().toString());
                    tmpNote.setTag(edtNotetag.getText().toString());
                    tmpNote.setCost(edtNoteCost.getText().toString());

//                  delete any physical files that were "removed"
                    String tmpImages = txtImage.getText().toString();
                    String tmpDeletesImages = txtImageDelete.getText().toString();
                    ArrayList<String> tmpAImages = DeleteFiles_misc(tmpNote.getNotesId(), tmpImages,tmpDeletesImages);
                    txtImage.setText(tmpAImages.get(0));
                    tmpNote.setImage(tmpAImages.get(0));
                    txtImageDelete.setText(tmpAImages.get(1));

                    nr.update(tmpNote,true);
                    // wvs todo 9/1/20 move eor logic to EventAddActivity.class
                }

                Log.i(TAG,"Saving item: " + tmpNote.getNoteItem());
//                Intent returnIntent = getIntent();
                if ( returnIntent.getExtras().containsKey("Notes")){
                    Log.i(TAG,"SAVE: navigate to NotaActivity.onActivityResult... returnIntent.getExtras().containsKey(\"Notes\")");
                } else {
                    returnIntent.putExtra("Notes",tmpNotes);
                    Log.i(TAG,"SAVE: navigate to NotaActivity.onActivityResult... putExtras().containsKey(\"Notes\")");
                }
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(mContext, AndroidCameraApi.class);
                startActivityForResult(intent1,START_CAMERA);
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = getIntent();
//            returnIntent.getSerializableExtra("Notes");
                setResult(RESULT_CANCELED,returnIntent);
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long lNoteId;
                NoteRepo nr;
                //          Next is only available for NEW items, save currently entered note...
                tmpNote = new Note(0, edtNoteItem.getText().toString(),"", edtNotetag.getText().toString(), Integer.valueOf(txtNotesId.getText().toString()),0 ,txtImage.getText().toString(),edtNoteCost.getText().toString(),null );
                nr = new NoteRepo(mContext);
                lNoteId = nr.insert(tmpNote);
                tmpNote.setNoteId((int) lNoteId);
//          reset all fields and focus
                edtNoteItem.setText("");
                edtNotetag.setText("");
                edtNoteItem.requestFocus();
            }
        });
        btnAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        Intent mediaIntent;
//     * <em>Note: don't be confused by the word "external" here. This directory
//     * can better be thought as media/shared storage. It is a filesystem that
//     * can hold a relatively large amount of data and that is shared across all
//     * applications (does not enforce permissions). Traditionally this is an SD
//     * card, but it may also be implemented as built-in storage in a device that
//     * is distinct from the protected internal storage and can be mounted as a
//     * filesystem on a computer.</em>
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());  //+ "/bluetooth/"  OR DIRECTORY_PICTURES
                if( WVS_Utils.isAboveKitKat() ){
                    mediaIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    mediaIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    mediaIntent.setType("image/*");
                    mediaIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(mediaIntent, IMPORT_IMAGE_ABOVEKITKAT);
                } else {
                    mediaIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                mediaIntent.setDataAndType(uri, "image/jpeg");
                    mediaIntent.setDataAndType(uri, "image/*");
                    mediaIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true); //to request that the launched content chooser only returns results representing data that is locally available on the device
                    startActivityForResult(mediaIntent, IMPORT_IMAGE);
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            MainActivity.requestPermission_SendSMS(mContext,this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, " WVS requestCode " + requestCode);
        Log.i(TAG, " WVS resultCode " + resultCode);
        String sCaption="";

        String tmpLabel="";

        if (requestCode == START_CAMERA & resultCode == RESULT_OK) {
//            add picture to gallery...
            Bundle extras = data.getExtras();
            if (extras.containsKey("imagefile")) {
                File tmpfile = (File) extras.get("imagefile");
                if (tmpfile.exists()) {
//                    Bitmap myBitmap = BitmapFactory.decodeFile(tmpfile.getAbsolutePath());
//                    imgVProduct.setImageBitmap(myBitmap);
//                    imgVProduct.setRotation(90);
                    String newimagefilename = tmpfile.getAbsolutePath().substring(tmpfile.getAbsolutePath().lastIndexOf("/") + 1);
                    String origimagefilenames = txtImage.getText().toString();
                    if (origimagefilenames.length() > 0) {
                        txtImage.setText(origimagefilenames + "," + newimagefilename);
                        if (tmpNote != null) { // may have not created/saved note
                            tmpNote.setImage(origimagefilenames + "," + newimagefilename);
                        }
                    } else {
                        txtImage.setText(newimagefilename);
                        if (tmpNote != null) { // may have not created/saved note
                            tmpNote.setImage(newimagefilename);
                        }
                    }
//                    wvs hint todo: load gallery with new image just appended... then figure out how to delete image..
//                    wvs hint in content_product.xml <LinearLayout update from android:orientation="center" to android:orientation="horizontal" to show full images correctly
//                    https://stackoverflow.com/questions/15480399/horizontal-scroll-view-inside-linearlayout-android

//                    PopulateLinearLayoutWithImages(this,myGallery,txtImage.getText().toString() , viewonclicklistener);
                    myHorScrollView.setVisibility(View.VISIBLE);
                    PopulateLinearLayoutWithImages(this, myGallery, txtImage.getText().toString(), new MyViewOnClickListener());

                    Log.i(TAG, " WVS BACK from onActivityResult... PopulateLinearLayoutWithImages");
                }
            }
        } else if (requestCode == IMPORT_IMAGE) {
            File tmpfile;
            int iFileStart = 0;
            String tmpfilename = "";
            String tmpfilepath = "";
            String tmpNoteItem, tmpTag, tmpImage;
            tmpNoteItem = tmpTag = tmpImage = "";

            int iPathStart = 0;
//        wvs pixel3a
//            https://stackoverflow.com/questions/20260416/retrieve-absolute-path-when-select-image-from-gallery-kitkat-android
//          Because from android KitKat ( sdk version 19 ), the system returned uri is not real local file path uri, it is a content provider style uri,
//          so we should parse the uri and get the real file local path by query related content provider ( image provider, audio provider, video provider and document provider ).
            Uri uri = data.getData();  //content://com.android.providers.media.documents/document/image%3A7395
            String pathFile;
            Cursor cursor = mContext.getContentResolver().query(uri, null,
                    null, null, null);

            if (cursor == null) {
                tmpfilename = uri.getPath();  ///document/image:7395
            } else {
                cursor.moveToFirst();
                try {
//                  https://developer.android.com/training/secure-file-sharing/retrieve-info
                    tmpfilename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));  // IMG_20200202_152800.jpg

//                    int idx = cursor
//                            .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//                    pathFile = cursor.getString(idx);
                } catch (Exception e) {
                    Log.i(TAG, e.getMessage());
                    tmpfilename = "";
                }
                cursor.close();
            }

            if (tmpfilename.equals("")) {
                if (data.getData().getLastPathSegment().lastIndexOf("/") == -1) {
                    iFileStart = data.getData().getLastPathSegment().lastIndexOf(":") + 1;
                    tmpfilepath = "/";
                } else {
                    iFileStart = data.getData().getLastPathSegment().lastIndexOf("/") + 1;
                    iPathStart = data.getData().getLastPathSegment().lastIndexOf(":") + 1;
                    tmpfilepath = "/" + data.getData().getLastPathSegment().substring(iPathStart, iFileStart);
                }
                tmpfilename = data.getData().getLastPathSegment().substring(iFileStart);
            }

            if (tmpfilepath.equals("/" + DIRECTORY_PICTURES + "/")) {
                tmpfile = new File(Environment.getExternalStorageDirectory().toString() + tmpfilepath + tmpfilename);
            } else {
//                copy file to DIRECTORY_PICTURES (so it can be potentially deleted later)
//                File orgFile = new File(data.getData().getPath());
//                File orgFile = new File(data.getData().toString());
//                File orgFile = new File(Environment.getExternalStorageDirectory().toString() + tmpfilepath + tmpfilename);

//                File orgFile = new File(WVS_Utils.getUriRealPath(mContext,uri) + tmpfilename);
                File orgFile = new File(uri.getPath());
                orgFile.exists();
//                WVS_Utils.getUriRealPath(mContext,uri);

                File toFile = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)) + "/" + tmpfilename);
                try {
                    WVS_Utils.copyFile(new FileInputStream(orgFile), new FileOutputStream(toFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Log.i(TAG, " WVS copy file:" + orgFile + " to " + toFile);
                }
            }
            String newimagefilename = tmpfilename;

//            String newimagefilename = tmpfile.getAbsolutePath().substring(tmpfile.getAbsolutePath().lastIndexOf("/")+1);

            String origimagefilenames = txtImage.getText().toString();
            if (origimagefilenames.length() > 0) {
                txtImage.setText(origimagefilenames + "," + newimagefilename);
                tmpNote.setImage(origimagefilenames + "," + newimagefilename);
            } else {
                txtImage.setText(newimagefilename);
                tmpNote.setImage(newimagefilename);
            }
//                    wvs hint todo: load gallery with new image just appended... then figure out how to delete image..
//                    wvs hint in content_product.xml <LinearLayout update from android:orientation="center" to android:orientation="horizontal" to show full images correctly
//                    https://stackoverflow.com/questions/15480399/horizontal-scroll-view-inside-linearlayout-android

//                    PopulateLinearLayoutWithImages(this,myGallery,txtImage.getText().toString() , viewonclicklistener);
            myHorScrollView.setVisibility(View.VISIBLE);
            PopulateLinearLayoutWithImages(this, myGallery, txtImage.getText().toString(), new MyViewOnClickListener());

            Log.i(TAG, " WVS BACK from onActivityResult... PopulateLinearLayoutWithImages");
        } else if (requestCode == IMPORT_IMAGE_ABOVEKITKAT) {
            Uri uri = data.getData();  //content://com.android.providers.media.documents/document/image%3A7395
//**************************************
//          https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];
            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }
            final String selection = "_id=?";
            final String[] selectionArgs = new String[] {
                    split[1]
            };
//          getDataColumn(context, contentUri, selection, selectionArgs);
//          getDataColumn(Context context, Uri uri, String selection,String[] selectionArgs)
            final String column = "_data";
            final String[] projection = {column};
            Cursor cursor = null;
            String OrigFilePath="";
            String tmpfilename="";
            try {
                cursor = mContext.getContentResolver().query(contentUri, projection, selection, selectionArgs,
                        null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int column_index = cursor.getColumnIndexOrThrow(column);
                    OrigFilePath = cursor.getString(column_index);  ///storage/emulated/0/DCIM/Camera/IMG_20200202_152815.jpg
                    tmpfilename = OrigFilePath.substring(OrigFilePath.lastIndexOf("/")+1);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            File orgFile = new File(OrigFilePath);
//            orgFile.exists();
//                WVS_Utils.getUriRealPath(mContext,uri);
            File toFile = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)) + "/" + tmpfilename);
            try {
                WVS_Utils.copyFile(new FileInputStream(orgFile), new FileOutputStream(toFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Log.i(TAG, " WVS copy file:" + orgFile + " to " + toFile);
            }
            String newimagefilename = tmpfilename;
            String origimagefilenames = txtImage.getText().toString();
            if (origimagefilenames.length() > 0) {
                txtImage.setText(origimagefilenames + "," + newimagefilename);
                tmpNote.setImage(origimagefilenames + "," + newimagefilename);
            } else {
                txtImage.setText(newimagefilename);
                tmpNote.setImage(newimagefilename);
            }
            myHorScrollView.setVisibility(View.VISIBLE);
            PopulateLinearLayoutWithImages(this, myGallery, txtImage.getText().toString(), new MyViewOnClickListener());
            Log.i(TAG, " WVS BACK from onActivityResult... PopulateLinearLayoutWithImages");
//            ***************************
        } else if (requestCode == ADD_EVENT_TONOTE_REQUEST || requestCode == EDIT_EVENT_INNOTE_REQUEST) {
            if (resultCode == RESULT_DELETE) {
//              check to see if delete event is required
                sCaption = mEvents.get(0).getEventType();
                txtEventAlarmlbl.setText(sCaption + ":" + EVENT_DELETED);
            } else if (resultCode == RESULT_CANCELED) {
//              keep label as is
            } else if (resultCode == RESULT_OK) {
                if (data != null) { // return from CustomCalendar  old:  obscelenceEventAddActivity.class without calling timepicker?
                    Bundle extras = data.getExtras();
                    if (extras.containsKey("event_object")) {
                        tmpEOfromTimePicker = (Event_Object) extras.getSerializable("event_object");
                        String sRepeat =  translateWeekdayNumbers(tmpEOfromTimePicker.getRepeat());
                        if (!sRepeat.equals("")) {
                            sRepeat = "[" + sRepeat + "]";
                        }
                        tmpLabel = tmpEOfromTimePicker.getEventType() + ":" + tmpEOfromTimePicker.getOccurance() + sRepeat + "," + convertDBdateStringToString_dateAMPM(tmpEOfromTimePicker.getDate());
                        txtEventAlarmlbl.setText(tmpLabel);
                        txtEventContact.setText(tmpEOfromTimePicker.getToContact());

//                        String stagextra ="";
//                        stagextra = tmpLabel.substring(tmpLabel.indexOf(",") + 1 + "2020-08-25 ".length());
//  no longer modifying tag, keep title info in eventmsgtag only
////                        edtNotetag.setText((edtNotetag.getText().toString() + " " + stagextra + " " + translateWeekdayNumbers(tmpEOfromTimePicker.getRepeat())).trim());
//                        edtNotetag.setText(tmpEOfromTimePicker.getMessageTag().toString());
                    }

                }
            }
        }
    }
    // By using this method get the Uri of Internal/External Storage for Media
//    https://stackoverflow.com/questions/20260416/retrieve-absolute-path-when-select-image-from-gallery-kitkat-android
    private Uri getUri() {
        String state = Environment.getExternalStorageState();
        if(!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Log.i(TAG, " WVS #2 onStart");
    }
    @Override
    protected void onResume() {
        super.onResume();
//        Log.i(TAG, " WVS #3 onResume");
    }
    @Override
    protected void onPause() {
        super.onPause();
//        Log.i(TAG, " WVS #5 onPause");
    }
    @Override
    protected void onStop() {
        super.onStop();
//        Log.i(TAG, " WVS #6 onStop");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
//        Log.i(TAG, " WVS #6a onRestart");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Log.i(TAG, " WVS #7 onDestroy");
    }

    class MyViewOnClickListener implements View.OnClickListener  {
        // wvs hint:      https://stackoverflow.com/questions/8722595/how-to-add-onclicklistener-to-dynamically-generated-text-view
        // WVS: hint: Instead of declaring above in onCreate and then in onActivityResult, declare as inner class

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(v != null){
                //all images
                String tmpFileArray[] = txtImage.getText().toString().split(",");
                if (tmpFileArray.length != 0) {

                    final String removeFile = tmpFileArray[v.getId()];
                    //images chosen to be deleted
                    if (txtImageDelete.getText().toString().contains(removeFile)){
//                            do nothing, already selected to be removed
                        Log.i(TAG, " WVS ALREADY chosen to be removed");
                    }
                    else {
                        Log.i(TAG, " WVS onCreate click image " + v.getId());
                        //wvs hint: https://www.mkyong.com/android/android-prompt-user-input-dialog-example/
                        LayoutInflater li = LayoutInflater.from(mContext);
                        View promptsView = li.inflate(R.layout.deletefileprompt, null);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                mContext);

                        // set prompts.xml to alertdialog builder
                        alertDialogBuilder.setView(promptsView);

                        final TextView fileName = (TextView) promptsView.findViewById(R.id.txtInfo);
                        fileName.setText(removeFile);

                        // set dialog message
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
//                                                etxtImage.setText(updatedFile.toString());
                                                if (txtImageDelete.getText().toString().length() == 0) {
                                                    txtImageDelete.setText(removeFile.toString());
                                                } else {
                                                    txtImageDelete.setText(txtImageDelete.getText().toString() + "," + removeFile.toString());
                                                }
                                            }
                                        })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
                    }
                }
            }
        }
//        };
    }

    public ArrayList<String> DeleteFiles_misc(Integer notesid, String ExistingList, String DeleteList){
        String tmpDeleteList;
        ArrayList<String> retResult = new ArrayList<String>();

        retResult.add(ExistingList);  // 0 position
        retResult.add(DeleteList);    // 1 position

        tmpDeleteList = DeleteList;
        NoteRepo nr = new NoteRepo(this);

        //                  delete any physical files that were "removed"
        if (DeleteList.length() > 0) {
            String tmpDeletedFilesArray[] = DeleteList.split(",");
            String ImagesBasedOnDeletes = "";

            int iCount = tmpDeletedFilesArray.length;
            for (String tmpDeletedName : tmpDeletedFilesArray) {
                File tmpfilepath = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)) + "/" + tmpDeletedName);
                //                        <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
                if (tmpfilepath.exists()) {
                    Log.i(TAG, " WVS DELETING FILE:" + tmpDeletedName);
                    if (nr.DoesAnotherImageExist(notesid,tmpDeletedName) == 0) {
                        tmpfilepath.delete();
                    }
                    ImagesBasedOnDeletes = StripString(ExistingList, tmpDeletedName);
                    //                    txtImage.setText(ImagesBasedOnDeletes);
                    retResult.set(0,ImagesBasedOnDeletes);
                    tmpDeleteList = StripString(tmpDeleteList,tmpDeletedName);
                    retResult.set(1,tmpDeleteList);
                }
            }
        } else if (ExistingList.length() > 0) {
            String tmpExistingFilesArray[] = ExistingList.split(",");
            String ImagesFinal = "";

            int iCount = tmpExistingFilesArray.length;
            for (String tmpDoesnotExist : tmpExistingFilesArray) {
                File tmpfilepath = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)) + "/" + tmpDoesnotExist);
                //                        <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
                if (! tmpfilepath.exists()) {
                    ImagesFinal = StripString(ExistingList, tmpDoesnotExist);
                    ExistingList = ImagesFinal;
                }
            }
            retResult.set(0,ExistingList);
        }

        return retResult;
    }

    public ArrayList<String> ParseMWDefinition(String lookup,  String definition){
//      String[] dimension must be defined
//      ArrayList allows one not to define dimension
//      ArrayList allows one not to define dimension
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        ArrayList <String> defs= new ArrayList<String>();
        String sNodeDefinition="";
        String sDef,sWord,sPart,sSentence;
        sDef=sWord=sPart=sSentence="";
        Boolean foundDef, foundSimilar;
        foundDef=foundSimilar=false;
        try {
            db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(definition));
            try {
                Document docDefinition = db.parse(is);
                int cnt_entry,cnt_entry_child,cnt3,cnt4,cnt_ure,cnt_uro,cnt_sentence;
                cnt_entry=cnt_entry_child=cnt3=cnt4=cnt_ure=cnt_uro=cnt_sentence=0;
                Node tmpNode_entry, tmpNode_ure_similarword;
                NodeList tmpNodeList, tmpNodeListSentence;
//                String message = doc.getDocumentElement().getTextContent();
//                cnt = doc.getDocumentElement().getElementsByTagName("dt").getLength();
//                cnt = docDefinition.getDocumentElement().getElementsByTagName("entry").getLength();
                //doc.getDocumentElement().getElementsByTagName("entry").item(0).getAttributes().getNamedItem("id").getTextContent()

                // Create a XPathFactory
                XPathFactory xFactory = XPathFactory.newInstance();
                // Create a XPath object
                XPath xpath = xFactory.newXPath();
                XPathExpression expr = null;
                XPathExpression expr2 = null;
                String xpathPattern = null;
                String xpathPattern2 = null;
                List<String> items = new ArrayList<String>();
///*
//              <ure> pronunciation
                cnt_ure=docDefinition.getDocumentElement().getElementsByTagName("ure").getLength();
                if (cnt_ure > 0) {
                    for (int k = 0; k < cnt_ure; k++){
                        tmpNode_ure_similarword = docDefinition.getDocumentElement().getElementsByTagName("ure").item(k);
                        sDef = tmpNode_ure_similarword.getTextContent();
                        if( sDef.replace("*","").contains(lookup)){
                            foundSimilar=true;
                            cnt_uro = tmpNode_ure_similarword.getParentNode().getChildNodes().getLength();
                            for (int j = 0; j < cnt_uro; j++) {
                                if (tmpNode_ure_similarword.getParentNode().getChildNodes().item(j).getNodeName().equals("fl")) {
                                    defs.add("[" + tmpNode_ure_similarword.getParentNode().getChildNodes().item(j).getTextContent() + "] " + sDef);
                                }
                            }
                        }
                    }
                }
                cnt_entry = docDefinition.getDocumentElement().getElementsByTagName("entry").getLength();
                for (int i=0; i < cnt_entry; i++){
                    foundDef=foundSimilar=false;
                    if (docDefinition.getDocumentElement().getElementsByTagName("entry").item(i).getAttributes().getNamedItem("id").getTextContent().contains(lookup)){
                        //found definition
                        foundDef=true;
                    }
//                        Node tmpNode = doc.getDocumentElement().getElementsByTagName("entry").item(i);
                    tmpNode_entry = docDefinition.getDocumentElement().getElementsByTagName("entry").item(i);
                    cnt_entry_child = tmpNode_entry.getChildNodes().getLength();
                    for (int j=0; j < cnt_entry_child; j ++) {
//                            <hw> found word
                        if (tmpNode_entry.getChildNodes().item(j).getNodeName().equals("hw")){
                            sWord = tmpNode_entry.getChildNodes().item(j).getTextContent();
                        }
//                          <fl> part of speech: noun, adjective, adverb
                        if (tmpNode_entry.getChildNodes().item(j).getNodeName().equals("fl")){
                            sPart = "[" + tmpNode_entry.getChildNodes().item(j).getTextContent() + "] ";
                        }
//                          <dt> definition
                        if (tmpNode_entry.getChildNodes().item(j).getNodeName().equals("def")){
                            defs.add(sPart + sWord);
                            cnt3 = tmpNode_entry.getChildNodes().item(j).getChildNodes().getLength();
                            tmpNodeList = tmpNode_entry.getChildNodes().item(j).getChildNodes();
                            cnt4=0;
                            for (int k=0; k < cnt3; k++){
                                if (tmpNodeList.item(k).getNodeName().equals("dt")) {
//                                        sDef = tmpNodeList.item(k).getNodeValue();
//                                        sDef = tmpNodeList.item(k).getTextContent();
                                    sDef =  tmpNodeList.item(k).getChildNodes().item(0).getTextContent();
                                    if (!sDef.trim().equals(":")) {
                                        if (cnt4 < DefinitionLimit) {
                                            cnt4++;
                                            defs.add(cnt4 + ") " + sDef);
                                        }
//                                          get <vi> sentence example
                                        cnt_sentence = tmpNodeList.item(k).getChildNodes().getLength();
                                        for (int l = 0; l < cnt_sentence; l++) {
//                                                tmpNodeList.item(k).getChildNodes().item(0).getNodeValue()=e.g.":something stated :"   .getNodeName()=e.g. #text
                                            if (tmpNodeList.item(k).getChildNodes().item(l).getNodeName().equals("vi")) {
                                                sSentence = "{" + tmpNodeList.item(k).getChildNodes().item(l).getTextContent() + "}";
                                                defs.add(sSentence);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
//*/

//                xpathPattern = "//entry/def";
//                //entry/def/../fl/text()
//                //entry/fl/text()
//                //entry/def/dt/text()
//                count(//entry)
//                //entry[1]/def/dt/text()
/*
                try {
                    // Compile the XPath expression
                    expr = xpath.compile(xpathPattern);
                    // Run the query and get a node set
                    Object result = expr.evaluate(docDefinition, XPathConstants.NODESET);
                    // Cast the result to a DOM NodeList
                    NodeList nodesDefinition = (NodeList) result;
                    for (int i = 0; i < nodesDefinition.getLength(); i++) {
//                        nodes.item(i).
//                        xpathPattern2="/fl";
                        defs.add( "[" + docDefinition.getDocumentElement().getElementsByTagName("fl").item(i).getTextContent()+ "] "  );
//                        defs.add(nodes.item(i).getNodeValue());
                        sNodeDefinition = nodesDefinition.item(i).getNodeValue();
//                        sDef = nodes.item(i).getTextContent();
//nodesDefinition.item(0).getChildNodes().getLength() = 20
//nodesDefinition.item(0).getChildNodes().item(0).getNodeName() "sn"

                        is.setCharacterStream(new StringReader(sNodeDefinition));
                        try {
                            Document docNode = db.parse(is);
                            xpathPattern2 = "//dt/text()";
                            expr2 = xpath.compile(xpathPattern2);
                            result = expr2.evaluate(docDefinition, XPathConstants.NODESET);
                            // Cast the result to a DOM NodeList
                            NodeList nodeDefinition = (NodeList) result;
                            for (int j = 0; j < nodeDefinition.getLength(); j++) {
                                sDef =  nodeDefinition.item(j).getNodeValue();
                                if (sDef.trim() != ":") {
                                    defs.add(sDef);
                                }
                            }
                        } catch (SAXException e) {
                            // handle SAXException
                            Log.i(TAG, e.getMessage());
                        }
                        catch (IOException e) {
                            // handle IOException
                            Log.i(TAG, e.getMessage());
                        }
                    }
                } catch (XPathExpressionException xee) {
                    Log.d(TAG, "used xpathPattern: " + xpathPattern);
                }

*/

            } catch (SAXException e) {
                // handle SAXException
                Log.i(TAG, e.getMessage());
            }
            catch (IOException e) {
                // handle IOException
                Log.i(TAG, e.getMessage());
            }
//            catch (java.lang.NullPointerException e){
//
//            }
        } catch (ParserConfigurationException e1) {
            // handle ParserConfigurationException
            Log.i(TAG, e1.getMessage());
        }
        return defs;
    }

    public String getEventFromEventAlarmlbl(){
        //          label -> Event type: Time info [<Weekly>,time]: Event info OR "long click to add date/time"
        String label = txtEventAlarmlbl.getText().toString();
        String sEventType = "";
        if (label.contains(":")) {
            sEventType = label.substring(0, label.indexOf(":")).trim();
        }
        return  sEventType;
    }

    public String getTimeInfoFromEventAlarmlbl(){
        //          label -> Event type: Time info [<Weekly>,time]: Event info OR "long click to add date/time"
        String label = txtEventAlarmlbl.getText().toString();
        String sTimeInfo = "";
        if (label.contains(":")) {
            int pos1 = label.indexOf(":") + 1;
            int pos2 = label.lastIndexOf(":");
            if (pos1 > pos2) {
                // long click to add date/time: advisory
            }else {
                //long click to add date/time: 2020-08-19 09:20 AM: advisory   long click to add date/time: OneTime, : advisory
                sTimeInfo = label.substring(pos1, pos2).trim();
            }

        }
        return  sTimeInfo;
    }

    public String getTimeFromEventAlarmlbl(){
        //          label -> Event type: Time info [<Weekly>,time]: Event info OR "long click to add date/time"
        String sTimeInfo = getTimeInfoFromEventAlarmlbl();
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

    public String getInfoFromEventAlarmlbl(){
        //          label -> Event type: Time info [<Weekly>,time]: Event info OR "long click to add date/time"
        String label = txtEventAlarmlbl.getText().toString();
        String sEventInfo = label.substring(label.lastIndexOf(":") + 1).trim();
        return  sEventInfo;
    }
}
