package com.endeavor.walter.getout9;
//https://medium.com/@ssaurel/taking-a-screenshot-programmatically-in-android-apps-67619cb80bf8

import android.graphics.Bitmap;
import android.view.View;

public class ScreenShot {

    public static Bitmap takescreenshot(View v) {
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return b;
    }

    public static Bitmap takescreenshotOfRootView(View v) {
        return takescreenshot(v.getRootView());
    }

}
