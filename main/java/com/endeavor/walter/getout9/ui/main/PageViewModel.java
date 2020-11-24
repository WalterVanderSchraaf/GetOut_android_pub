package com.endeavor.walter.getout9.ui.main;

//import android.arch.core.util.Function;
//import android.arch.lifecycle.LiveData;
//import android.arch.lifecycle.MutableLiveData;
//import android.arch.lifecycle.Transformations;
//import android.arch.lifecycle.ViewModel;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
//    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
//        @Override
//        public String apply(Integer input) {
//            return "Hello world from section: " + input;
//        }
//    });
    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
    @Override
    public String apply(Integer input) {
//        return null;
        return "Hello world from section: " + input;
    }
}) ;

    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public LiveData<String> getText() {
        return mText;
    }
}