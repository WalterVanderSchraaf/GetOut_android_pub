package com.endeavor.walter.getout9;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.io.File;

import static android.os.Environment.DIRECTORY_PICTURES;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
//    private Integer [] images = {R.drawable.slide1,R.drawable.slide2,R.drawable.slide3};
//    private Integer [] images = {1,2,3};
    private String sImages,sCaption;
    private String [] sArrayImages;
    private static final String TAG = "ViewPagerAdapter";

    public ViewPagerAdapter(Context context, String images, String caption) {
        this.context = context;
        this.sImages = images;
        this.sArrayImages = images.split(",");
        this.sCaption = caption;
    }

    @Override
    public int getCount() {
//        return images.length;
        return sArrayImages.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        https://www.ryadel.com/en/android-proportionally-stretch-imageview-fit-whole-screen-width-maintaining-aspect-ratio/
        View view = layoutInflater.inflate(R.layout.custom_layout, null);
//        https://stackoverflow.com/questions/41689686/zoom-the-viewpager-imageview
//        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        TouchViewPagerIMageView imageView = (TouchViewPagerIMageView) view.findViewById(R.id.imageView);

        TextView txtCaption = (TextView) view.findViewById(R.id.txtCaption);

//        imageView.setImageResource(images[position]);
        String tmpFile = sArrayImages[position];
        txtCaption.setText(tmpFile);
        File tmpfilepath = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)) + "/" + tmpFile);
        if (tmpfilepath.exists() && tmpFile.length() != 0) {
            Drawable drawable  = Drawable.createFromPath(tmpfilepath.getAbsolutePath());
//drawable.getIntrinsicHeight();
//drawable.setBounds(0,0,width, height);  int rotateImage = getCameraPhotoOrientation(imagefilepath.getAbsolutePath()); todo:

//            int rotateImage = WVS_Utils.getCameraPhotoOrientation(tmpfilepath.getAbsolutePath());
//            if (rotateImage == 180){
//                Log.e(TAG, "landscape mode");
////                    rotateImage = 0;
//            }
//            Matrix matrix = new Matrix();
//            matrix.postRotate(rotateImage);
//            myrotatedBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);

//wvs: HINT:    <com.endeavor.walter.notepad.TouchViewPagerIMageView
//              android:layout_width="match_parent"
//              android:layout_height="match_parent"

//            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//WVS: HINT: MATRIX allow ZOOM
            imageView.setScaleType(ImageView.ScaleType.MATRIX);
//          wvs selfies are being draw upsidedown... correction below

            if (tmpFile.contains("_front.jpg")){
                imageView.setRotation(180f);
            }

            imageView.setImageDrawable(drawable);
            int orientation = context.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // In landscape
                Log.i(TAG, " WVS landscape mode");

            } else {
                // In portrait
                Log.i(TAG, " WVS portrait mode");
            }
//            int orientation = context.getResources().getConfiguration().orientation;
//            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                // In landscape
//                if (drawable.getIntrinsicHeight() != 320) {
//                    if (drawable.getIntrinsicWidth()/drawable.getIntrinsicHeight() < 1) {
////                  portrait
//                        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
//                        int width = (drawable.getIntrinsicWidth() * 320) / drawable.getIntrinsicHeight();
//                        int height = 320;
//                        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, width, height, false);
//                        Drawable resizeddrawable;
//                        resizeddrawable = new BitmapDrawable(context.getResources(), bitmapResized);
//                        imageView.setImageDrawable(resizeddrawable);
//                    } else {
////                  landscape
//                        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
//                        int width = 320;
//                        int height = (drawable.getIntrinsicHeight() * 320) / drawable.getIntrinsicWidth(); ;
//                        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, width, height, false);
//                        Drawable resizeddrawable;
//                        resizeddrawable = new BitmapDrawable(context.getResources(), bitmapResized);
//                        imageView.setImageDrawable(resizeddrawable);
//                    }
//                } else {
//                    imageView.setImageDrawable(drawable);
//                }
//
//            } else {
//                // In portrait mode
//                if (drawable.getIntrinsicHeight() != 320) {
//                    if (drawable.getIntrinsicWidth()/drawable.getIntrinsicHeight() < 1) {
////                  portrait dimensions
//                        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
//                        int width = (drawable.getIntrinsicWidth() * 320) / drawable.getIntrinsicHeight();
//                        int height = 320;
//                        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, width, height, false);
//                        Drawable resizeddrawable;
//                        resizeddrawable = new BitmapDrawable(context.getResources(), bitmapResized);
//                        imageView.setImageDrawable(resizeddrawable);
//                    } else {
////                  landscape dimensions
//                        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
//                        int width = 240;
//                        int height = (drawable.getIntrinsicHeight() * 240) / drawable.getIntrinsicWidth(); ;
//                        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, width, height, false);
//                        Drawable resizeddrawable;
//                        resizeddrawable = new BitmapDrawable(context.getResources(), bitmapResized);
//                        imageView.setImageDrawable(resizeddrawable);
//                    }
//                } else {
//                    imageView.setImageDrawable(drawable);
//                }
//            }

        } else {
            Log.i(TAG, " WVS IMAGE DOES NOT EXIST " + tmpFile);
        }

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}
