package com.endeavor.walter.getout9;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
//import android.support.annotation.Nullable;
//import android.support.annotation.NonNull;
//import android.support.v4.app.Fragment;
//import android.arch.lifecycle.Observer;
//import android.arch.lifecycle.ViewModelProviders;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.endeavor.walter.getout9.ui.main.PageViewModel;

import static androidx.lifecycle.ViewModelProviders.*;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    //        setHasOptionsMenu(true);
//    private PageViewModel pageViewModel = of(this).get(PageViewModel.class);
    private PageViewModel pageViewModel;
    //  enable disable menu...
    private Menu mMenu;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        final TextView textView = root.findViewById(R.id.section_label);
        textView.setText((CharSequence) pageViewModel.getText().getValue());

//        pageViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
//        setHasOptionsMenu(true);
        return root;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
////        inflater.inflate(R.menu.menu_main,menu);
////        this.mMenu = menu;
////        Log.i(TAG, " WVS onCreateOptionsMenu mMenu = " + ((mMenu == null) ? "NULL" : "not NULL"));
////        setMenuState(Utils.getRequestingLocationUpdates(getActivity()));
//        super.onCreateOptionsMenu(menu, inflater);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void setMenuState(boolean breqLocationUpdates) {
//
////        Boolean breqLocationUpdates = Utils.getRequestingLocationUpdates(this);
//        if (mMenu != null) {
//            MenuItem item = mMenu.findItem(R.id.activatelocationupdates);
//            item.setEnabled(!breqLocationUpdates);
//
//            MenuItem item2 = mMenu.findItem(R.id.deactivatelocationupdates);
//            item2.setEnabled(breqLocationUpdates);
//
////            MenuItem item3 = mMenu.findItem(R.id.getPreviousRoutes);
////            item3.setEnabled(breqLocationUpdates);
//        }
//    }
}