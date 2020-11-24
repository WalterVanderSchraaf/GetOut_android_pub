package com.endeavor.walter.getout9;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import static com.endeavor.walter.getout9.WVSInfo.ADD_NOTE_ITEM_REQUEST;
import static com.endeavor.walter.getout9.WVSInfo.EDIT_NOTE_ITEM_REQUEST;
import static com.endeavor.walter.getout9.WVSInfo.SORTBYCHECK;
import static com.endeavor.walter.getout9.WVSInfo.SORTBYDATE;
import static com.endeavor.walter.getout9.WVSInfo.SORTBYNAME;
import static com.endeavor.walter.getout9.WVSInfo.SORTBYTAG;
import static com.endeavor.walter.getout9.WVS_Utils.doWebViewPrint;
import static com.endeavor.walter.getout9.WVS_Utils.getStringFromFile;

public class NoteActivity extends AppCompatActivity {
    //LIST OF ITEMS IN A NOTE
    public final static String TAG = "NoteActivity";
    private NoteActivityFragment frag;
    EditText edtNoteName, edtSearch;
    Notes tmpNotes;
    Context mContext;
    Menu mMenu;
    MenuItem ToggleTags;
    MenuItem ToggleChecks;
    boolean bHiddenTags;
    boolean bHiddenChecks;
    String sSearchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_note);

        edtSearch = (EditText) findViewById(R.id.edtSearch);
        edtSearch.setVisibility(View.GONE);
//WVS: HINT:      http://www.androidbegin.com/tutorial/android-search-listview-using-filter/
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = edtSearch.getText().toString().toLowerCase(Locale.getDefault());
                Log.i(TAG, " WVS s = " + s.toString()  + ", edtSearch = " + text + ", sSearch = " + sSearchText);
                sSearchText = text;
//                adapter.filter_note(text);
//WVS: HINT portrait to landscape etc, frag is null thus throws exception
                if (frag != null) {
                    Log.i(TAG," WVS edtSearch.afterTextChanged frag != null");
                    frag.Filter_the_Adapter_lstV(text);
                    Log.i(TAG," WVS edtSearch.afterTextChanged returning from frag.Filter_the_Adapter_lstV(text)");
                } else {
                    Log.i(TAG," WVS edtSearch.afterTextChanged frag == NULL");
//WVS: HINT: solution... reset frag
                    frag = (NoteActivityFragment) getSupportFragmentManager().findFragmentById(R.id.note_detail_container);
                    frag.Filter_the_Adapter_lstV(text);
//                  wvs foobar fix this?
                    frag.ToggleChecksTags(bHiddenChecks,bHiddenTags);
                    //                    frag.Filter_the_Adapter_lstV(text);
//                    Log.i(TAG,"edtSearch.afterTextChanged calling frag.Filter_the_Adapter_lstV(text)");
                }
            }
        });
//        toolbar.setTitle("");

//      WVS: HINT: Prevent the keyboard from displaying on activity start
//      https://stackoverflow.com/questions/9732761/prevent-the-keyboard-from-displaying-on-activity-start
//      Also can declare in your manifest file's activity - android:windowSoftInputMode="stateHidden"
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//        Log.i(TAG, " WVS onCreate #1 HIDING KEYBOARD");

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
//            Log.i(TAG, " WVS onCreate #1b onCreate savedInstanceState == NULL");
            Bundle arguments = new Bundle();
//          when adding Notes, first time in, tmpNotes is null
            tmpNotes = (Notes) getIntent().getSerializableExtra("Notes");
//wvs hint: removed from xml android:selectAllOnFocus="true"
            edtNoteName = findViewById(R.id.edtNoteName);
            edtNoteName.setSelectAllOnFocus(true);

            if (tmpNotes !=null) {
                arguments.putSerializable("Notes", tmpNotes);
//                Log.i(TAG," WVS onCreate #1b tmpNotes != null");
            } else {
//                Log.i(TAG," WVS onCreate #1b tmpNotes == NULL");
                //create new note, temporarily.... first time with new notes
                tmpNotes = new Notes(0, edtNoteName.getText().toString(), "",DBInfo.TABLE_TBLNOTE_KEY_Date ,0);
                NotesRepo nr = new NotesRepo(this);
                long lNotesId = nr.insert(tmpNotes);
                tmpNotes.setNotesId((int) lNotesId);
                arguments.putSerializable("Notes", tmpNotes);
            }

            if (tmpNotes != null){
                edtNoteName.setText(tmpNotes.getName());
//              note  EditText_disable(edtNoteName,true);
            } else {
                Log.i(TAG,"onCreate #1b tmpNotes == NULL, 1st time in from adding Notes");
            }

//            Log.i(TAG, " WVS onCreate #1b getSupportFragmentManager().beginTransaction()");
            frag = new NoteActivityFragment();
            frag.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.note_detail_container, frag)
                    // wvs hint add fragment to widget.NestedScrollView
                    .commit();
        } else {
//            Log.i(TAG, " WVS onCreate #1c savedInstanceState != null");
            if (tmpNotes == null) {
//                Log.i(TAG, " WVS onCreate #1c tmpNotes == NULL");
                tmpNotes = (Notes) getIntent().getSerializableExtra("Notes");

            } else {
//                Log.i(TAG, " WVS onCreate #1c tmpNotes != null");
            }

            if (savedInstanceState.containsKey("HiddenChecks")) {
                bHiddenChecks = savedInstanceState.getBoolean("HiddenChecks");
            }
            if (savedInstanceState.containsKey("HiddenTags")) {
                bHiddenTags = savedInstanceState.getBoolean("HiddenTags");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        mMenu = menu;
//menu.findItem(R.id.mi_note_toggletags).getTitle()

        if (bHiddenTags){
            menu.findItem(R.id.mi_note_toggletags).setTitle(getString(R.string.menu_note_ToggleTags_Show));
        } else {
            menu.findItem(R.id.mi_note_toggletags).setTitle(getString(R.string.menu_note_ToggleTags_Hide));
        }
        if (bHiddenChecks){
            menu.findItem(R.id.mi_note_togglechecks).setTitle(getString(R.string.menu_note_ToggleChecks_Show));
        } else {
            menu.findItem(R.id.mi_note_togglechecks).setTitle(getString(R.string.menu_note_ToggleChecks_Hide));
        }

        String sOrderBy = tmpNotes.getOrderby();
        if (sOrderBy!=null) {
            switch (sOrderBy) {
                case DBInfo.TABLE_TBLNOTE_KEY_Checked:
                    Menu_prepareChecks(menu.findItem(R.id.mi_note_sortby_check));
                    break;
                case DBInfo.TABLE_TBLNOTE_KEY_NoteItem:
                    Menu_prepareChecks(menu.findItem(R.id.mi_note_sortby_notes));
                    break;
                case DBInfo.TABLE_TBLNOTE_KEY_Date:
                    Menu_prepareChecks(menu.findItem(R.id.mi_note_sortby_dates));
                    break;
                case DBInfo.TABLE_TBLNOTE_KEY_Tag:
                    Menu_prepareChecks(menu.findItem(R.id.mi_note_sortby_tag));
                    break;
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.i(TAG, " WVS onOptionsItemSelected " + id);
        NotesRepo nsr = new NotesRepo(this);
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
//WVS: HINT: https://developer.android.com/training/appbar/up-action
            //You do not need to catch the up action in the activity's onOptionsItemSelected() method. Instead, that method should call its superclass, as shown in Respond to Actions. The superclass method responds to the Up selection by navigating to the parent activity, as specified in the app manifest.
//            Intent intent2 = new Intent(NoteActivity.this, MainActivity.class);
//            navigateUpTo( intent2);
//            return true;
        } else if (id == R.id.mi_delete_Notesid) {
//            save the note
//            frag.WriteNotebyNotedId(false, true);
//          delete from db
            frag.DeleteNoteOrItems(true,false);
            navigateUpTo(new Intent(this, MainActivity.class)); //instead just refresh
            return true;
        } else if (id == R.id.mi_delete_check_items) {
//            frag.DeleteCheckedItems();
            frag.DeleteNoteOrItems(false,true);
            frag.RefreshList();
//            navigateUpTo(new Intent(this, MainActivity.class)); //instead just refresh
            return true;
        } else if (id == R.id.mi_delete_all_items) {
            frag.DeleteNoteOrItems(false,false);
//            navigateUpTo(new Intent(this, MainActivity.class)); //instead just refresh
            return true;
        } else if (id == R.id.mi_delete_linenumber){

            nsr.RemoveLineNumbers(tmpNotes.getNotesId());
//          figure out how to refresh list? for now navigate up
            navigateUpTo(new Intent(this, NoteActivity.class));
            return true;
        } else if (id == R.id.mi_note_sortby_check) {
            frag.sortList(SORTBYCHECK, -1);
            Menu_prepareChecks(item);
//wvs: todo: update tmpNotes Orderby
//            nr = new NotesRepo(this);
            if (tmpNotes != null){
                tmpNotes.setOrderby(DBInfo.TABLE_TBLNOTE_KEY_Checked);
                nsr.update(tmpNotes);
            }
            return true;
        } else if (id == R.id.mi_note_sortby_notes) {
            Menu_prepareChecks(item);
            frag.sortList(SORTBYNAME,-1);
//            nr = new NotesRepo(this);
            if (tmpNotes != null){
                tmpNotes.setOrderby(DBInfo.TABLE_TBLNOTE_KEY_NoteItem);
                nsr.update(tmpNotes);
            }
            return true;
        } else if (id == R.id.mi_note_sortby_dates) {
            Menu_prepareChecks(item);
            frag.sortList(SORTBYDATE,1);
//            nr = new NotesRepo(this);
            if (tmpNotes != null){
//              tblNotes.SortBy = tblNote.NoteDate
                tmpNotes.setOrderby(DBInfo.TABLE_TBLNOTE_KEY_Date);
                nsr.update(tmpNotes);
            }
            return true;
        }else if (id == R.id.mi_note_sortby_tag) {
            Menu_prepareChecks(item);
            frag.sortList(SORTBYTAG,1);
//            nr = new NotesRepo(this);
            if (tmpNotes != null){
                tmpNotes.setOrderby(DBInfo.TABLE_TBLNOTE_KEY_Tag);
                nsr.update(tmpNotes);
            }
            return true;
        } else if (id == R.id.mi_note_Add){
            //wvs: hint: todo: check if tmpNotes is null...
//            nr = new NotesRepo(this);
            if (tmpNotes != null){
                if (!tmpNotes.getName().equals(edtNoteName.getText().toString())) {
                    tmpNotes.setName(edtNoteName.getText().toString());
                    nsr.update(tmpNotes);
                }
            }else {
//              new notes, so create one...
                tmpNotes = new Notes(0, edtNoteName.getText().toString(), "",DBInfo.TABLE_TBLNOTE_KEY_Date,0);
                long lNotesId = nsr.insert(tmpNotes);
                tmpNotes.setNotesId((int)lNotesId );
            }

            Intent intent1 = new Intent(NoteActivity.this, NoteAddActivity.class);
            intent1.putExtra("Notes", tmpNotes);
            intent1.putExtra("requestCode", ADD_NOTE_ITEM_REQUEST);
//          startActivity(intent1); previoulsy
            startActivityForResult(intent1,ADD_NOTE_ITEM_REQUEST);
            return true;
        } else if (id == R.id.mi_note_sumcosts) {
            if (tmpNotes != null){
                String strTotalCost="";
                strTotalCost=  nsr.SumCheckedCosts(tmpNotes.getNotesId());
                Toast.makeText(mContext, "Total Cost = $"+ strTotalCost,Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.mi_note_toggletags) {

            if (!bHiddenTags){  // default menu "HideTags", bHiddenTags = false   item.getTitle().equals(getString(R.string.menu_note_ToggleTags_Hide))
//                change menu to Show Tags
                bHiddenTags = true;
//                frag.ToggleTags(bHiddenTags);
                frag.ToggleChecksTags(bHiddenChecks,bHiddenTags);
                item.setTitle(getString(R.string.menu_note_ToggleTags_Show));
            } else if (bHiddenTags){ //item.getTitle().equals(getString(R.string.menu_note_ToggleTags_Show))
//                change menu to Show Tags
                bHiddenTags = false;
//                frag.ToggleTags(bHiddenTags);
                frag.ToggleChecksTags(bHiddenChecks,bHiddenTags);
                item.setTitle(getString(R.string.menu_note_ToggleTags_Hide));
            }
        } else if (id == R.id.mi_note_togglechecks){
            if (!bHiddenChecks){ //item.getTitle().equals(getString(R.string.menu_note_ToggleChecks_Hide))
//                change menu to Show Tags
                bHiddenChecks = true;
//                frag.ToggleChecks(bHiddenChecks);
                frag.ToggleChecksTags(bHiddenChecks,bHiddenTags);
                item.setTitle(getString(R.string.menu_note_ToggleChecks_Show));
            } else if (bHiddenChecks){  //item.getTitle().equals(getString(R.string.menu_note_ToggleChecks_Show))
//                change menu to Show Tags
                bHiddenChecks = false;
//                frag.ToggleChecks(bHiddenChecks);
                frag.ToggleChecksTags(bHiddenChecks,bHiddenTags);
                item.setTitle(getString(R.string.menu_note_ToggleChecks_Hide));
            }
        }
        else if (id == R.id.mi_note_Search){
//          wvs hint  https://stackoverflow.com/questions/8991522/how-can-i-set-the-focus-and-display-the-keyboard-on-my-edittext-programmatical
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (edtSearch.getVisibility() == View.VISIBLE) {
                edtSearch.setVisibility(View.GONE);
                edtNoteName.setVisibility(View.VISIBLE);
                edtSearch.setText("");
                sSearchText="";
                if (frag != null) {
                    frag.Filter_the_Adapter_lstV("");
                }
//                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
            } else if (edtSearch.getVisibility() == View.GONE) {
                edtSearch.setVisibility(View.VISIBLE);
                if (edtNoteName!=null) {
//                    wvs foobar fix this?
                    edtNoteName.setVisibility(View.GONE);
                }
//                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE );
                edtSearch.requestFocus();
                imm.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT);
            }
        } else if (id == R.id.miSaveNoteName){
//            nr = new NotesRepo(this);
            if (tmpNotes != null){
                tmpNotes.setName(edtNoteName.getText().toString());
                nsr.update(tmpNotes);
            }else {
                tmpNotes = new Notes(0, edtNoteName.getText().toString(), "",DBInfo.TABLE_TBLNOTE_KEY_Date,0);
//            objA_Notes.add(tmpNotes);
                long lNotesId = nsr.insert(tmpNotes);
                tmpNotes.setNotesId((int) lNotesId);
            }

        } else if (id == R.id.mi_note_save_note) {
//          saving for the next import, so no line numbers
            frag.WriteNotebyNotedId(false, false);
        } else if (id == R.id.mi_note_share_note) {
            String sBody = "";
            File sFileName = frag.WriteNotebyNotedId(false, true);
            try {
                sBody = getStringFromFile(sFileName.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
//            call email...
//                Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
//                intent.setType("message/rfc822");
//                intent.putExtra(Intent.EXTRA_SUBJECT,  tmpNotes.getName());
//                intent.putExtra(Intent.EXTRA_TEXT, sBody);
//                intent.setData(Uri.parse("mailto:")); // or just "mailto:" for blank
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
//                startActivity(intent);

//              SHARE  ******************
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//                below only if we want to attach a file along with the body
//                Uri uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".fileprovider", sFileName);
//                intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
//                intent.setDataAndType(uri, "text/plain");
//                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, sBody);
                intent.putExtra(Intent.EXTRA_SUBJECT,  tmpNotes.getName());
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
//                ******************
            }

        }
        else if (id == R.id.mi_note_print_checked_items | id == R.id.mi_note_print_all_items) {
            File sFileName=null;
            if (id == R.id.mi_note_print_checked_items) {
                sFileName = frag.WriteNotebyNotedId(true, true);
            } else {
                sFileName = frag.WriteNotebyNotedId(false, true);
            }
            try {
                doWebViewPrint(this,sFileName);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (sFileName.exists()){
                    //wvs: hint: this occurs independently of print manager window being displayed.
                    sFileName.delete();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
////    https://stackoverflow.com/questions/32790497/android-checkable-menu-item
////        return super.onPrepareOptionsMenu(menu);
//        MenuItem Bycheck = menu.findItem(R.id.mi_note_sortby_check);
//        Bycheck.setChecked(false);
//        MenuItem Bynotes = menu.findItem(R.id.mi_note_sortby_notes);
//        Bynotes.setChecked(false);
//        MenuItem Bydates = menu.findItem(R.id.mi_note_sortby_dates);
//        Bydates.setChecked(false);
//        MenuItem Bytag = menu.findItem(R.id.mi_note_sortby_tag);
//        Bytag.setChecked(false);
//
//        return true;
//    }

    private void Menu_prepareChecks(MenuItem item){

        int id = item.getItemId();

        MenuItem Bycheck = mMenu.findItem(R.id.mi_note_sortby_check);
        Bycheck.setCheckable(true);
        Bycheck.setChecked(false);
        MenuItem Bynotes = mMenu.findItem(R.id.mi_note_sortby_notes);
        Bynotes.setCheckable(true);
        Bynotes.setChecked(false);
        MenuItem Bydates = mMenu.findItem(R.id.mi_note_sortby_dates);
        Bydates.setCheckable(true);
        Bydates.setChecked(false);
        MenuItem Bytag = mMenu.findItem(R.id.mi_note_sortby_tag);
        Bytag.setCheckable(true);
        Bytag.setChecked(false);

        if (id == R.id.mi_note_sortby_check){
            Bycheck.setChecked(true);
            return;
        } else if (id == R.id.mi_note_sortby_notes){
            Bynotes.setChecked(true);
            return;
        } else if (id == R.id.mi_note_sortby_dates){
            Bydates.setChecked(true);
            return;
        } else if (id == R.id.mi_note_sortby_tag){
            Bytag.setChecked(true);
            return;
        }


    }
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
//        Bundle bundle = data.getExtras();
//        if (bundle != null) {
//            for (String key : bundle.keySet()) {
//                Object value = bundle.get(key);
//                Log.d(TAG, String.format("%s %s (%s)", key,
//                        value.toString(), value.getClass().getName()));
//            }
//        }
        //wvs: hint: todo: must get Note and place as ExtraData...
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if (edtSearch != null) {
            edtSearch.setVisibility(View.GONE);
        }
        if (edtNoteName != null) {
            edtNoteName.setVisibility(View.VISIBLE);
        }
        Log.i(TAG, " WVS onActivityResult sSearchText = " + sSearchText);
        if (sSearchText != null) {
            edtSearch.setText(sSearchText);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Log.i(TAG, " WVS onActivityResult() HIDING KEYBOARD");
        if (requestCode == ADD_NOTE_ITEM_REQUEST){
            Log.i(TAG," WVS returning from NoteAddActivity");
            if (tmpNotes == null){
//wvs: hint:    Samsung it's null, Pixel it's not null
                if (Objects.requireNonNull(data.getExtras()).containsKey("Notes")) {
                    tmpNotes = (Notes) data.getSerializableExtra("Notes");
//                tmpNote = (Note) data.getSerializableExtra("Note");
                }
            }
        } else if (requestCode == EDIT_NOTE_ITEM_REQUEST){
            Log.i(TAG," WVS returning from NoteActivityFragment");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("HiddenTags", bHiddenTags);
        outState.putBoolean("HiddenChecks", bHiddenChecks);
        outState.putString("textSearch", sSearchText);

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


}
