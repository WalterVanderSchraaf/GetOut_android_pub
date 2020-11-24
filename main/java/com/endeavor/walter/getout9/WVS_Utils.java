package com.endeavor.walter.getout9;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
//import android.media.ExifInterface;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.work.Data;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.DIRECTORY_PICTURES;
import static com.endeavor.walter.getout9.MainActivity.appEventOccurrance;
import static com.endeavor.walter.getout9.Utils.appLog;
import static com.endeavor.walter.getout9.WVSInfo.CALENDAR_LONGCLICKTOADD;
import static com.endeavor.walter.getout9.WVSInfo.EVENTOCCURRANCE_ONETIME;
import static com.endeavor.walter.getout9.WVSInfo.EVENTOCCURRANCE_WEEKLY;
import static com.endeavor.walter.getout9.WVSInfo.EVENTTYPE_EMAIL;
import static com.endeavor.walter.getout9.WVSInfo.EVENTTYPE_NOTIFICATION;
import static com.endeavor.walter.getout9.WVSInfo.EVENTTYPE_SCHEDULE_NOTIFICATION;
import static com.endeavor.walter.getout9.WVSInfo.EVENTTYPE_TEXTMSG;

// sha256 hashing  https://stackoverflow.com/questions/51444782/sha256-encryption-in-java-and-python-produce-different-results
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Walte on 12/20/2017.
 */

public class WVS_Utils {

    private static final String TAG = WVS_Utils.class.getSimpleName();//" WVS: WVS_Utils";
    private static MessageDigest sha256;
//    WVS_Utils mContext = this;
    /**
     https://stackoverflow.com/questions/6540906/simple-export-and-import-of-a-sqlite-database-on-android#6542214
     Utils class, is a static class that perform small and repetitive operations on a kind of instance
     vs e.g. Helper class, is a class that can be instantiate and do some business work

     * Creates the specified <code>toFile</code> as a byte for byte copy of the
     * <code>fromFile</code>. If <code>toFile</code> already exists, then it
     * will be replaced with a copy of <code>fromFile</code>. The name and path
     * of <code>toFile</code> will be that of <code>toFile</code>.<br/>
     * <br/>
     * <i> Note: <code>fromFile</code> and <code>toFile</code> will be closed by
     * this function.</i>
     *
     * @param fromFile
     *            - FileInputStream for the file to copy from.
     * @param toFile
     *            - FileInputStream for the file to copy to.
     */
    public static void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }


    public static ImageView RotateBitmapForImageView(File imagefilepath, Context context){
        ImageView imageView = new ImageView(context);
        try {
            if (imagefilepath.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imagefilepath.getAbsolutePath());
                Bitmap myrotatedBitmap;
                Bitmap myscaledBitmap;

                int rotateImage = getCameraPhotoOrientation(imagefilepath.getAbsolutePath());
//              wvs 3/23/20 removed below for pixel 3a selfie _front
//                if (rotateImage == 180){
//                    Log.e(TAG, "landscape mode? flip it...");
//                    if (!imagefilepath.getName().toString().contains("_front.jpg")) {
//                        rotateImage = 0;
//                    }
//                }
                Matrix matrix = new Matrix();
                matrix.postRotate(rotateImage);
                myrotatedBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
//                myrotatedBitmap = Bitmap.createBitmap(myBitmap, 0, 0, width, height, matrix, true);

//                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setScaleType(ImageView.ScaleType.MATRIX);
//WVS TODO: portrait 500 landscape 300?
                int orientation = context.getResources().getConfiguration().orientation;
                int width=0;
                int height=0;
                int heightLimit=0;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    // In landscape
                    heightLimit = 300;
                } else {
//                    In Portrait
                    heightLimit =500;
                }

                if (myrotatedBitmap.getHeight() != heightLimit){
                    width = (myrotatedBitmap.getWidth()*heightLimit)/myrotatedBitmap.getHeight();
                    height = heightLimit;
                    myscaledBitmap = Bitmap.createScaledBitmap(myrotatedBitmap,width,height,false);
                    imageView.setImageBitmap(myscaledBitmap);
                } else  {
                    imageView.setImageBitmap(myrotatedBitmap);
                }
            }
        }catch (Exception e) {

        }
        finally {
//            return imageView;
        }
        return imageView;
    }

    public static Bitmap RotateImage_toBitmap(File imagefilepath, float degrees ){
        Bitmap myrotatedBitmap=null;
        try {
            if (imagefilepath.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imagefilepath.getAbsolutePath());

//                https://stackoverflow.com/questions/2577221/android-how-to-create-runtime-thumbnail
//                int THUMBNAIL_SIZE = 64;
//                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(myBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE);

                Matrix matrix = new Matrix();
                matrix.postRotate(degrees);
                myrotatedBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
//                myrotatedBitmap = Bitmap.createBitmap(ThumbImage, 0, 0, ThumbImage.getWidth(), ThumbImage.getHeight(), matrix, true);

            }
        }catch (Exception e) {

        }
        finally {

        }
        return myrotatedBitmap;
    }

    public static void PopulateLinearLayoutWithImages(Context context, LinearLayout gallery, String filenames, View.OnClickListener onclicklistener){
//        ImageView imageView = new ImageView(mContext);
//        Bitmap myrotatedBitmap;
//        float flDegrees=90;
        String tmpFileArray[] = filenames.split(",");
        int iCount = tmpFileArray.length;
        int iIndx = 0;
        for (String tmpName:tmpFileArray){
            File tmpfilepath = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES))+"/" + tmpName );
            if (tmpfilepath.exists()) {
                if (iIndx == 0){
                    if (gallery.getChildCount() > 0) {
                        gallery.removeAllViews();
                    }
                }
                ImageView imageView = RotateBitmapForImageView(tmpfilepath,context);
//                myrotatedBitmap = RotateImage_toBitmap(tmpfilepath,flDegrees);
//                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//                imageView.setImageBitmap(myrotatedBitmap);
//                int genid = View.generateViewId();
                imageView.setId(iIndx);
                iIndx = iIndx + 1;
                imageView.setOnClickListener(onclicklistener);

                gallery.addView(imageView);
            }
        }
    }

    public static void EditText_disable(View v, boolean bDisable){
//wvs hint: add static so it can be called from outside
        if (bDisable) {
            v.setEnabled(false);
            v.setClickable(false);
            v.setFocusable(false);
        } else {
            v.setEnabled(true);
            v.setClickable(true);
            v.setFocusable(true);
        }
    }


    // capture image orientation
    //wvs hint: https://stackoverflow.com/questions/36995146/why-images-are-rotated-90-degree-in-imageview#36995847
    public static int getCameraPhotoOrientation(String imagePath) {

        int rotate = 0;
        int returnRotate = 0;
        try {
//            mContext.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            // Constants used for the Orientation Exif tag.
//            ORIENTATION_UNDEFINED = 0;
//            ORIENTATION_NORMAL = 1;
//            ORIENTATION_FLIP_HORIZONTAL = 2;  // left right reversed mirror
//            ORIENTATION_ROTATE_180 = 3;
//            ORIENTATION_FLIP_VERTICAL = 4;  // upside down mirror
//            // flipped about top-left <--> bottom-right axis
//            ORIENTATION_TRANSPOSE = 5;
//            ORIENTATION_ROTATE_90 = 6;  // rotate 90 cw to right it
//            // flipped about top-right <--> bottom-left axis
//            ORIENTATION_TRANSVERSE = 7;
//            ORIENTATION_ROTATE_270 = 8;  // rotate 270 to right it

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:  //8
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:  //3
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:  //6
                    rotate = 90;
                    break;
            }
            String deviceName = android.os.Build.MODEL;
            String deviceMan = android.os.Build.MANUFACTURER;

            Log.i(TAG, " WVS getCameraPhotoOrientation() Brand:"+deviceMan+",Model:"+deviceName);
            Log.i(TAG," WVS imagePath:" + imagePath);
            Log.i(TAG, " WVS Exif orientation returned: " + orientation);
            Log.i(TAG, " WVS new Rotate value: " + rotate);


            if (deviceMan.toUpperCase().contains("SAMSUNG")) {
                if (imagePath.toString().contains("_front.jpg")) {
                    returnRotate = -rotate;
                }else if (imagePath.toString().contains("_back.jpg")) {
                    returnRotate = rotate;
                }else
                    returnRotate = rotate;

                if (returnRotate==0) {
                    Log.i(TAG, " WVS returnRotate value 0 RESETTING to -90 " );
                    returnRotate = -90;
                }
            } else if (deviceMan.toUpperCase().contains("GOOGLE")) {
                if (imagePath.toString().contains("_front.jpg")) {
                    returnRotate = rotate;
//                    wvs 3/23/20
                    if (rotate==90) {
                        returnRotate = -rotate;
                        Log.i(TAG, " WVS returnRotate value 90 RESETTING to " + returnRotate);
                    }
//                    if (returnRotate==0) {
//                        returnRotate = 180;
//                        Log.i(TAG, " WVS returnRotate value 0 RESETTING to " + returnRotate);
//                    }
                } else if (imagePath.toString().contains("_back.jpg")) {
                    returnRotate = rotate;    // wvs 3/23/20
//                    if (returnRotate==0) {
//                        returnRotate = 180;
//                        Log.i(TAG, "returnRotate value 0 RESETTING to " + returnRotate);
//                    }
                } else {
                    returnRotate = rotate;
//                  wvs 3/23/20
//                    if (returnRotate == 0) {
//                        returnRotate = 180;
//                        Log.i(TAG, " WVS returnRotate value 0 RESETTING to " + returnRotate);
//                    }
                }
            } else if (deviceMan.toUpperCase().contains("BLU")) {
                if (imagePath.toString().contains("_front.jpg")) {
                    returnRotate = rotate;
                    if (returnRotate==0) {
                        returnRotate = 180;
                        Log.i(TAG, " WVS returnRotate value 0 RESETTING to " + returnRotate);
                    }
                } else if (imagePath.toString().contains("_back.jpg")) {
                    returnRotate = rotate;  // wvs 3/21/20

//                    if (returnRotate==0) {
//                        returnRotate = 180;
//                        Log.i(TAG, "returnRotate value 0 RESETTING to " + returnRotate);
//                    }
                } else {
                    returnRotate = rotate;
                    if (returnRotate == 0) {
                        returnRotate = 180;
                        Log.i(TAG, " WVS returnRotate value 0 RESETTING to " + returnRotate);
                    }
                }
            } else {
                returnRotate = rotate;
                if (returnRotate == 0) {
                    returnRotate = 180;
                    Log.i(TAG, " WVS returnRotate value 0 RESETTING to " + returnRotate);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, " WVS returnRotate value: " + returnRotate);

        return returnRotate;
    }

    public static String getCameraPhotoLatLng(File imageFile) {
//    http://android-coding.blogspot.com/2011/10/get-latitude-and-longitude-of-exif.html
        String csvLatLng ="";
        try {
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            float[] LatLong = new float[2];
            if(exif.getLatLong()!=null){
                csvLatLng = LatLong[0] + "," + LatLong[1];
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return csvLatLng;
    }
    public static String StripString (String strCSV, String stripThis){
        String strStripped = strCSV;
        if (strCSV.contains(stripThis)){
            String eachItem[] = strCSV.split(",");
            Integer cnt = eachItem.length;
            if (cnt == 1) {
                strStripped = strStripped.replace(stripThis,"");
            } else if (eachItem[0].equals(stripThis)) {
                strStripped = strStripped.replace(stripThis+",","");
            } else {
                strStripped = strStripped.replace("," + stripThis,"");
            }
        }
        return strStripped;
    }

//    private WebView mWebView;
    private PrintJob mPrintJobs;

    public static void doWebViewPrint(final Context context, File printFile) throws IOException {
//        https://developer.android.com/training/printing/html-docs
        // Create a WebView object specifically for printing
        WebView webView = new WebView(context);
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, " WVS page finished loading " + url);
                createWebPrintJob(view, context);
//                mWebView = null;
            }
        });

        // Generate an HTML document on the fly:
        String htmlDocument = "<html><body><h1>Test Content</h1><p>Testing, " +
                "testing, testing...</p></body></html>";
//wvs: todo: convert file to string and print
        try {
            htmlDocument = getStringFromFile(printFile.getAbsolutePath());
//          https://stackoverflow.com/questions/5027084/android-webview-incorrectly-handling-newlines-in-preformatted-text
            htmlDocument =  htmlDocument.replaceAll("\\n", "<br/>");

            //            htmlDocument = URLEncoder.encode(htmlDocument, "utf-8").replaceAll("\\+", "%20");
            //            webview.loadData(URLEncoder.encode("<html><body><pre>line 1\nline 2</pre></body></html>", "utf-8").replaceAll("\\+", "%20"), "text/html", "utf-8");
        } catch (Exception e){
            e.printStackTrace();
        } finally {
//            webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);
            webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);

        }


//        try using /loca/
//        byte[] encoded = Files.readAllLines(printFile.toPath() , Charset.defaultCharset()));

//printFile.toString();
//webView.loadUrl();
//        webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);
        // Print an existing web page (remember to request INTERNET permission!):
//        webView.loadUrl("http://developer.android.com/about/index.html");
//        webView.loadUrl("file:///android_asset/html/Home.html");
//        webView.loadUrl("file:///" + printFile.getAbsoluteFile());
//        webView.loadUrl("file://" + printFile.getAbsoluteFile());
//        webView.loadUrl("file://sdcard/" + printFile.getName());
//        webView.loadUrl("file://local/" + printFile.getName());
//                              /storage/emulated/0/to do_2018-08-29 13-05-16.csv
//        webView.loadUrl(printFile.toURI().toString());
//        webView.loadUrl( Paths.get(printFile.getAbsolutePath()).toUri().toURL().toString());

//        printFile.toURL()
//        File file = new File(Environment.getExternalStorageDirectory()+"/"+printFile.getName());
//        if (file.exists()) {
//            webView.loadUrl("file://" + Environment.getExternalStorageDirectory() + "/"+printFile.getName());
//        }
//        /data/local/tmp
//      file:/// simply denotes that you wish to access the local filesystem, and points to the root directory. Anything mentioned after this is the relative path to the resource we would like to load in the WebView. Hence, when the URL is of the form file:///android_asset, we are specifying the base URL for the path to the asset folder for the application package.

        // Keep a reference to WebView object until you pass the PrintDocumentAdapter
        // to the PrintManager
//        mWebView = webView;
    }

//    static String readFile(String path, Charset encoding)
//            throws IOException
//    {
//        byte[] encoded = Files.readAllBytes(Paths.get(path));
//        return new String(encoded, encoding);
//    }
    private static void createWebPrintJob(WebView webView, Context context) {

        // Get a PrintManager instance
//        PrintManager printManager = (PrintManager) getActivityId().getSystemService(Context.PRINT_SERVICE);
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

        // Create a print job with name and adapter instance
        String jobName = context.getString(R.string.app_name) + " Document";
//        String jobName = getReso
        PrintJob printJob = printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());

        // Save the job object for later status checking
//        mPrintJobs.add(printJob);
    }

    public static String convertStreamToString(InputStream is) throws Exception {
//        https://stackoverflow.com/questions/12910503/read-file-as-string#13357785
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
//            sb.append(line).append(" \n ");
            sb.append(line).append(System.getProperty("line.separator"));
//            System.getProperty("line.separator");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }
////https://stackoverflow.com/questions/5568874/how-to-extract-the-file-name-from-uri-returned-from-intent-action-get-content#25005243
//public String getFileName(Uri uri) {
//    String result = null;
//    if (uri.getScheme().equals("content")) {
////        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//
//        Cursor cursor = mContext.getContentResolver().query(contentUri, proj, null, null, null);
//
//        try {
//            if (cursor != null && cursor.moveToFirst()) {
//                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//            }
//        } finally {
//            cursor.close();
//        }
//    }
//    if (result == null) {
//        result = uri.getPath();
//        int cut = result.lastIndexOf('/');
//        if (cut != -1) {
//            result = result.substring(cut + 1);
//        }
//    }
//    return result;
//}
    public static Date convertDBdateStringToDate_date(String dateInString){
//        DateFormat format = new SimpleDateFormat("d-MM-yyyy", Locale.ENGLISH);
//        yyyy-MM-dd HH-mm-ss
//        Notes.setDate() ... DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = null;
        if (!dateInString.trim().equals("")) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            try {
                date = format.parse(dateInString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }
//String datetime = new SimpleDateFormat("MM-dd-yy hh:mm a").format(WVS_Utils.convertDBdateStringToDate_datetime(objALocations.get(position).getDatetime()) );
//    DateFormat format = new SimpleDateFormat("MM-dd-yy hh:mm a", Locale.ENGLISH);
public static String convertDBdateStringToString_dateAMPM(String dateInString){
//        DateFormat format = new SimpleDateFormat("d-MM-yyyy", Locale.ENGLISH);
//        yyyy-MM-dd HH-mm-ss
//        Notes.setDate() ... DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    String datetimeAMPM = new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(WVS_Utils.convertDBdateStringToDate_datetime(dateInString) );

    return datetimeAMPM;
}

    public static String convertDBdateStringToString_startofDay(String dateInString){
//      DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String startofDay = dateInString.substring(0,11) + "00:00:00";
        return startofDay;
    }

    public static String convertDBdateStringToString_endofDay(String dateInString){
//      DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String endofDay = dateInString.substring(0,11) + "23:59:59";
        return endofDay;
    }
    public static String convertTimePicker_formatAMPM_To_DBdateString(String dateInString){
//        yyyy-MM-dd HH:mm AMPM    input 2019-07-09 03:19 PM  ????07-09-19 12:45 PM, 07-09-19 01:20 PM
//        0123456789 12345 7
//        Notes.setDate() ... DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String sAMPM = dateInString.substring(dateInString.length()-2);
        int iHrEnd = dateInString.indexOf(":");
        int iDateEnd = dateInString.indexOf(" ");

        int itmpHr = Integer.parseInt(  dateInString.substring (iHrEnd-2,iHrEnd) );
        int iHr = (sAMPM.equals("PM")? (itmpHr == 12 ? itmpHr:  itmpHr + 12): (itmpHr == 12) ? 0: itmpHr );

//        String sYear = dateInString.substring(0,iDateEnd).trim() ;
//        if (sYear.length() == 2){
//            sYear = "20" + sYear;
//        }
        //                            yyyy-MM-dd HH:mm:ss
        String datetimeAMPM = dateInString.substring(0,iDateEnd).trim() + " " + String.format("%02d",iHr) + ":" + dateInString.substring(iHrEnd + 1,iHrEnd + 3) + ":00" ;

        return datetimeAMPM;
    }

    public static Long convertDBdateStringToMilliSeconds(String dateInString){
        //e.g. 2019-05-23 18:30:03

        Long lDate_Millisecs=0l;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,Integer.valueOf(dateInString.substring(0,4)) );
        cal.set(Calendar.MONTH,Integer.valueOf(dateInString.substring(5,7))-1 ); //The first month of the year in the Gregorian and Julian calendars is <code>JANUARY</code> which is 0
        cal.set(Calendar.DAY_OF_MONTH,Integer.valueOf(dateInString.substring(8,10)) );
        cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(dateInString.substring(11,13)) );
        cal.set(Calendar.MINUTE, Integer.valueOf(dateInString.substring(14,16)) );
        cal.set(Calendar.SECOND, Integer.valueOf(dateInString.substring(17,19)));
        lDate_Millisecs = cal.getTimeInMillis();

        return  lDate_Millisecs;
    }

public static String TimePicker_formatAMPM(int itmpHr, int iMin) {

//https://docs.oracle.com/javase/6/docs/api/java/util/Formatter.html#syntax wvs hint
//                 %[argument_index$][flags][width][.precision]conversion

    Integer iHr = (itmpHr > 12)? itmpHr - 12: (itmpHr == 0) ? 12: itmpHr;  // 0 is 12 am, 12 is 12 pm
    String sAMPM = (itmpHr >= 12)? " PM": " AM";
    String sTime = String.format("%02d",iHr) + ":" + String.format("%02d",iMin) + sAMPM;

    return sTime;
}
    public static Date convertDBdateStringToDate_datetime(String dateInString){
//        DateFormat format = new SimpleDateFormat("d-MM-yyyy", Locale.ENGLISH);
//        yyyy-MM-dd HH-mm-ss
//        Notes.setDate() ... DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        Date date = null;
        if (!dateInString.trim().equals("")) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            try {
                date = format.parse(dateInString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    public static String convertDBdateStringToDate_dateAMPM(String dateInString){

        Date dteDate = WVS_Utils.convertDBdateStringToDate_datetime(dateInString);
        String datetime = new SimpleDateFormat("MM-dd-yy hh:mm a").format(dteDate );
        String retdateAMPM = "";
        retdateAMPM = ((datetime.substring(0,1).equals("0")) ? " " : datetime.substring(0,1)) + datetime.substring(1,9) +
                                   ((datetime.substring(9,10).equals("0")) ? "  " : datetime.substring(9,10))  + datetime.substring(10);

        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        String dayOfTheWeek = sdf.format(dteDate);
        retdateAMPM = dayOfTheWeek + " " + retdateAMPM;

        return retdateAMPM;

    }

    public static String convertDBdateStringToDate_timeAMPM(String dateInString){
        String rettimeAMPM = "";
        String tmpdateAMPM = convertDBdateStringToDate_dateAMPM(dateInString);
//      Fri 10-16-20 11:51 AM
        rettimeAMPM = tmpdateAMPM.substring(12).trim();

        return rettimeAMPM;
    }

    public static String convertDateToString_datepart(Date tmpDate){
        String retDatepart;

        Calendar DBcal = Calendar.getInstance();
        DBcal.setTime(tmpDate);
//      "yyyy-MM-dd"
        retDatepart = String.format("%04d",DBcal.get(Calendar.YEAR)) + "-" + String.format("%02d",DBcal.get(Calendar.MONTH)+1) + "-" + String.format("%02d",DBcal.get(Calendar.DAY_OF_MONTH));

        return retDatepart;
    }
    public static boolean IsDBStringDateEqualtoDate(String DBStringDate, Date date){
        boolean bRet = false;
//wvs todo: add Event_Object.getEventsByDate()

        Calendar DBcal = Calendar.getInstance();
        Date DBdate = convertDBdateStringToDate_date(DBStringDate);
        DBcal.setTime(DBdate);

        Calendar Datecal = Calendar.getInstance();
        Datecal.setTime(date);

        if (DBcal.get(Calendar.DAY_OF_MONTH) == Datecal.get(Calendar.DAY_OF_MONTH) &&
                DBcal.get(Calendar.MONTH) == Datecal.get(Calendar.MONTH) &&
                DBcal.get(Calendar.YEAR) == Datecal.get(Calendar.YEAR)) {
            bRet = true;
        }

        return  bRet;
    }

    public static void DisplayExtras(Intent intent) {

        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Object value = bundle.get(key);
                    Log.i(TAG, " WVS DisplayExtras() getExtras " + String.format("%s %s (%s)", key,
                            value.toString(), value.getClass().getName()));
                    }
                } else {
                Log.i(TAG, " WVS DisplayExtras() no Extras");
            }
        } else {
            Log.i(TAG, " WVS DisplayExtras() intent is NULL, no Extras");
        }
    }

    public static String ReformatTimeString(String totaltime){
//      totaltime HH:MM:SS
//                01 34 67
        String formattedtotaltime = "";
        if (totaltime != null && totaltime.length() > 0) {
            formattedtotaltime = ((totaltime.substring(0, 3).equals("00:")) ? "" : ((totaltime.substring(0, 1).equals("0")) ? totaltime.substring(1, 3) : totaltime.substring(0, 3))) +
                    ((!(totaltime.substring(0, 3).equals("00:"))) ? totaltime.substring(3,5) :
                    (totaltime.substring(3, 6).equals("00:")) ? "" : (totaltime.substring(3, 4).equals("0") ? totaltime.substring(4, 5) : totaltime.substring(3, 5))) +
                    (totaltime.substring(5));
        }
        return formattedtotaltime;
    }

    public static String ReformatTimeStringForTextView(String totaltime){
//      totaltime HH:MM:SS
//                01 34 67
        String formattedtotaltime = "";
        String htmlText = "";

        if (totaltime != null && totaltime.length() > 0) {
            htmlText = ((totaltime.substring(0, 3).equals("00:")) ? totaltime.substring(0, 3).replace("00:","<font color='#ffffff'>"+"00:"+"</font>") :
                    ((totaltime.substring(0,1).equals("0")) ? totaltime.substring(0,1).replace("0","<font color='#ffffff'>"+"0"+"</font>")+totaltime.substring(1,3) : totaltime.substring(0, 3))) +
                    ((!(totaltime.substring(0, 3).equals("00:"))) ? totaltime.substring(3,5) :
                    (totaltime.substring(3, 5).equals("00")) ? totaltime.substring(3, 5).replace("00","<font color='#ffffff'>"+"00"+"</font>") :
                            ((totaltime.substring(3, 4).equals("0")) ? totaltime.substring(3, 4).replace("0","<font color='#ffffff'>"+"0"+"</font>")+totaltime.substring(4,5) : totaltime.substring(3, 5))) +
                    (totaltime.substring(5));
//	#ffffff white font
//  String textToBeColored = "a";
//String formattedtotasltime = totaltime.substring(0, 3).replace(textToBeColored,"<font color='#ffffff'>"+textToBeColored +"</font>");
//txtView.setText(Html.fromHtml(htmlText);
        }
        return htmlText;
    }

//https://stackoverflow.com/questions/38202109/how-to-create-direction-arrows-for-my-polylines-in-android
//     * Return a BitmapDescriptor of an arrow endcap icon for the passed color.
// *
//         * @param mContext - a valid mContext object
// * @param color - the color to make the arrow icon
// * @return BitmapDescriptor - the new endcap icon
// */
    public static BitmapDescriptor getEndCapIcon(Context context, int color, int drawableid) {

        // mipmap icon - white arrow, pointing up, with point at center of image
        // you will want to create:  mdpi=24x24, hdpi=36x36, xhdpi=48x48, xxhdpi=72x72, xxxhdpi=96x96
        Drawable drawable = ContextCompat.getDrawable(context, drawableid); //R.mipmap.endcap

        // set the bounds to the whole image (may not be necessary ...)
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        // overlay (multiply) your color over the white icon
        drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

        // create a bitmap from the drawable
        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // render the bitmap on a blank canvas
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);

        // create a BitmapDescriptor from the new bitmap
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    public static String getDateTimeString_standard() {
        String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return datetime;
    }

    public static String getDateTimeString_Now_mod(Integer unitTime, int quantity) {
        Date today = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        if (unitTime.equals(Calendar.DAY_OF_YEAR)){
            cal.add(Calendar.DAY_OF_YEAR, quantity);
        } else if (unitTime.equals(Calendar.MONTH)){
            cal.add(Calendar.MONTH, quantity);
        }

        String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
        return datetime;
    }

    public static Integer getDayOfWeek_Now_mod() {
        Date today = new Date();
        Integer iDay = Calendar.MONDAY;

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        iDay = cal.get(Calendar.DAY_OF_WEEK);

        return iDay;
    }

    public static int getEventId_NotificationTag(String tag){
//      notification tag NoteId + " " + EventId + " " + EventDate
        int posFirstSpace = tag.indexOf(" ");
        int posSecondSpace = tag.indexOf(" ", tag.indexOf(" ") + 1);
        int iEventId = Integer.parseInt(tag.substring(posFirstSpace, posSecondSpace).trim());
        return iEventId;
    }

    public static int getNoteId_NotificationTag(String tag){
//      notification tag NoteId + " " + EventId + " " + EventDate
        int posFirstSpace = tag.indexOf(" ");
        int iNoteId = Integer.parseInt(tag.substring(0,posFirstSpace));

        return iNoteId;
    }

    public static String getPathNameForSQLiteDB(Context context, Intent data){
        if( isAboveKitKat() ) {

            Uri uri = data.getData();
            String OrigFilePath = "";
//            OrigFilePath = getUriRealPathAboveKitkat(context,uri);
            //File Commander - content://com.android.externalstorage.documents/document/primary%3A2020-01-31%2012-12-41_Maps05.db
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
            else if ("primary".equals(type)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1];
            }
            final String selection = "_id=?";
            final String[] selectionArgs = new String[]{
                    split[1]
            };
            final String column = "_data";
            final String[] projection = {column};
            Cursor cursor = null;
//            String OrigFilePath = "";
            String tmpfilename = "";
            try {
                cursor = context.getContentResolver().query(contentUri, projection, selection, selectionArgs,
                        null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int column_index = cursor.getColumnIndexOrThrow(column);
                    OrigFilePath = cursor.getString(column_index);  ///storage/emulated/0/DCIM/Camera/IMG_20200202_152815.jpg
                    tmpfilename = OrigFilePath.substring(OrigFilePath.lastIndexOf("/") + 1);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return OrigFilePath;
        } else {
            return "";
        }
    }

    // https://stackoverflow.com/questions/51444782/sha256-encryption-in-java-and-python-produce-different-results
    // generated password is stored encrypted (using also user name for hashing)
    public synchronized static String encrypt(String hash) {
        try {

            StringBuilder builder = new StringBuilder();
            builder.append(hash);

            // first time , encrypt user name , password and static key
            String encryptedCredentials = encryptionIterator(builder.toString());
            return encryptedCredentials;
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private static String encryptionIterator(String content) {
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
            // append the static key to each iteration
            byte[] passBytes = (content).getBytes();
            sha256.reset();
            byte[] digested = sha256.digest(passBytes);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < digested.length; i++) {
//                sb.append(Integer.toHexString(0xff & digested[i])); // this drops the leading zero if the (unsigned) byte value is 15 or less.
                sb.append(String.format("%02x", 0xff & digested[i]));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }

        return "";
    }


//  NOT USED- REMOVE BELOW  https://www.dev2qa.com/how-to-get-real-file-path-from-android-uri/
    /* Get uri related content real local file path. */
    public static String getUriRealPath(Context ctx, Uri uri)
    {
        String ret = "";

        if( isAboveKitKat() )
        {
            // Android OS above sdk version 19.
            ret = getUriRealPathAboveKitkat(ctx, uri);
        }else
        {
            // Android OS below sdk version 19
            ret = getImageRealPath(ctx.getContentResolver(), uri, null);
        }

        return ret;
    }

    private static String getUriRealPathAboveKitkat(Context ctx, Uri uri)
    {
        String ret = "";

        if(ctx != null && uri != null) {

            if(isContentUri(uri))
            {
                if(isGooglePhotoDoc(uri.getAuthority()))
                {
                    ret = uri.getLastPathSegment();
                }else {
                    ret = getImageRealPath(ctx.getContentResolver(), uri, null);
                }
            }else if(isFileUri(uri)) {
                ret = uri.getPath();
            }else if(isDocumentUri(ctx, uri)){

                // Get uri related document id.
                String documentId = DocumentsContract.getDocumentId(uri);

                // Get uri authority.
                String uriAuthority = uri.getAuthority();

                if(isMediaDoc(uriAuthority))
                {
                    String idArr[] = documentId.split(":");
                    if(idArr.length == 2)
                    {
                        // First item is document type.
                        String docType = idArr[0];

                        // Second item is document real id.
                        String realDocId = idArr[1];

                        // Get content uri by document type.
                        Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        if("image".equals(docType))
                        {
                            mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        }else if("video".equals(docType))
                        {
                            mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        }else if("audio".equals(docType))
                        {
                            mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }

                        // Get where clause with real document id.
                        String whereClause = MediaStore.Images.Media._ID + " = " + realDocId;

                        ret = getImageRealPath(ctx.getContentResolver(), mediaContentUri, whereClause);
                    }

                }else if(isDownloadDoc(uriAuthority))
                {
                    // Build download uri.
                    Uri downloadUri = Uri.parse("content://downloads/public_downloads");

                    // Append download document id at uri end.
                    Uri downloadUriAppendId = ContentUris.withAppendedId(downloadUri, Long.valueOf(documentId));

                    ret = getImageRealPath(ctx.getContentResolver(), downloadUriAppendId, null);

                }else if(isExternalStoreDoc(uriAuthority))
                {
                    String idArr[] = documentId.split(":");
                    if(idArr.length == 2)
                    {
                        String type = idArr[0];
                        String realDocId = idArr[1];

                        if("primary".equalsIgnoreCase(type))
                        {
                            ret = Environment.getExternalStorageDirectory() + "/" + realDocId;
                        }
                    }
                }
            }
        }

        return ret;
    }

    /* Check whether current android os version is bigger than kitkat or not. */
    public static boolean isAboveKitKat()
    {
        boolean ret = false;
        ret = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        return ret;
    }

    /* Check whether this uri represent a document or not. */
    private static boolean isDocumentUri(Context ctx, Uri uri)
    {
        boolean ret = false;
        if(ctx != null && uri != null) {
            ret = DocumentsContract.isDocumentUri(ctx, uri);
        }
        return ret;
    }

    /* Check whether this uri is a content uri or not.
     *  content uri like content://media/external/images/media/1302716
     *  */
    private static boolean isContentUri(Uri uri)
    {
        boolean ret = false;
        if(uri != null) {
            String uriSchema = uri.getScheme();
            if("content".equalsIgnoreCase(uriSchema))
            {
                ret = true;
            }
        }
        return ret;
    }

    /* Check whether this uri is a file uri or not.
     *  file uri like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
     * */
    private static boolean isFileUri(Uri uri)
    {
        boolean ret = false;
        if(uri != null) {
            String uriSchema = uri.getScheme();
            if("file".equalsIgnoreCase(uriSchema))
            {
                ret = true;
            }
        }
        return ret;
    }


    /* Check whether this document is provided by ExternalStorageProvider. */
    private static boolean isExternalStoreDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.externalstorage.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by DownloadsProvider. */
    private static boolean isDownloadDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.providers.downloads.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by MediaProvider. */
    private static boolean isMediaDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.providers.media.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by google photos. */
    private static boolean isGooglePhotoDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.google.android.apps.photos.content".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Return uri represented document file real local path.*/
    private static String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause)
    {
        String ret = "";

        // Query the uri with condition.
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);

        if(cursor!=null)
        {
            boolean moveToFirst = cursor.moveToFirst();
            if(moveToFirst)
            {

                // Get columns name by uri type.
                String columnName = MediaStore.Images.Media.DATA;

                if( uri==MediaStore.Images.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Images.Media.DATA;
                }else if( uri==MediaStore.Audio.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Audio.Media.DATA;
                }else if( uri==MediaStore.Video.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Video.Media.DATA;
                }

                // Get column index.
                int imageColumnIndex = cursor.getColumnIndex(columnName);

                // Get column value which is the uri related file local path.
                ret = cursor.getString(imageColumnIndex);
            }
        }

        return ret;
    }

    public static File getWritableFile(String sfilename){
        File retFile=null;
        String state = Environment.getExternalStorageState();
        String pathToExtStorage = Environment.getExternalStorageDirectory().toString();
        File sd = new File(pathToExtStorage);

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            if (Environment.MEDIA_MOUNTED.equals(state) ||
                    Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                if (sd.canWrite()) {
//                    retFile = new File(sd, sfilename);
                    retFile = sd;
                }
            }
        }

        return retFile;
    }

    public static void CreateNotification(Context context, Event_Object tmpEO, String strEventAlarmlbl, Note tmpNote){
//      wvs todo 9/1/20 remove tmpNote
//      CREATE NOTIFICATIONS
        Long lAlertTime=0l;
        String sDateTimePM = "";
        String sOrigSchedDateTime = "";
        String sOccurance = "";
        String sSchedDateTime = "";
        String sDatepart = "";
        String sTimepart = "";
//        Integer iOddEven = 0;
        String sEventRepeat ="";
        Boolean bEven = false;

        if (tmpEO.getDate().toString().equals("")){
//          from label: eventtype: datetime "2020-08-20 12:45 PM" [from AddEditNote: Event info]
            sDateTimePM = strEventAlarmlbl.substring(strEventAlarmlbl.indexOf(":")+1).trim();
            sOrigSchedDateTime = convertTimePicker_formatAMPM_To_DBdateString(sDateTimePM);
//          "yyyy-MM-dd HH:mm:ss"
        } else {
            sOrigSchedDateTime = tmpEO.getDate();
        }

        if (tmpEO.getOccurance().equals("")) {
            sOccurance = strEventAlarmlbl.substring(0, strEventAlarmlbl.indexOf(":")).trim();
        } else {
            sOccurance = tmpEO.getOccurance();
        }

//      Event type  strEventAlarmlbl "Eventtype:date"
        String sEventType = "";
        if (!tmpEO.getEventType().equals("")){
            sEventType = tmpEO.getEventType();
        } else {
            sEventType = strEventAlarmlbl.substring(0, strEventAlarmlbl.indexOf(":"));
        }

//          Weekly: convert to weekday today's time  Monthly: convert to next month time etc
//          {"OneTime", "Weekly", "Monthly", "Yearly"}
        if (sOccurance.equals(EVENTOCCURRANCE_ONETIME)){
            lAlertTime = WVS_Utils.convertDBdateStringToMilliSeconds(sOrigSchedDateTime) - System.currentTimeMillis();
        } else if (sOccurance.equals(EVENTOCCURRANCE_WEEKLY) && sEventType.equals(EVENTTYPE_SCHEDULE_NOTIFICATION)){
//              "yyyy-MM-dd HH:mm:ss"
            int i = 0;
            int iDayOffset = 0;
            Integer iDay = Calendar.MONDAY;  //1Sun, 2Mon, 3Tue, 4Wed, 5Thu, 6Fri, Sat7
            do {
//              tmpEOfromTimePicker.getMessage() is zero - everyday, is even Wed, Fri, is odd Tues, Thur
                sEventRepeat = tmpEO.getRepeat().toString();
                iDay = getDayOfWeek_Now_mod();
                String tmpRepeatArray[] = sEventRepeat.split(",");
                for (String RepDay: tmpRepeatArray){
                    if (iDay <= Integer.valueOf(RepDay)){
                        iDayOffset = i + Integer.valueOf(RepDay) - iDay;
                        sDatepart =  getDateTimeString_Now_mod(Calendar.DAY_OF_YEAR, iDayOffset).substring(0,"yyyy-MM-dd ".length());
                        sTimepart = sOrigSchedDateTime.substring("yyyy-MM-dd ".length());
                        sSchedDateTime = sDatepart + sTimepart;
                        lAlertTime = WVS_Utils.convertDBdateStringToMilliSeconds(sSchedDateTime) - System.currentTimeMillis();
                        if (lAlertTime > 0){
                            break;
                        }
                    }
                }
                if (lAlertTime <=0 ) {
//                 past all repeat days, get first next week repeat
                    iDayOffset = Calendar.SATURDAY - iDay + Integer.valueOf(tmpRepeatArray[0]);
                    sDatepart =  getDateTimeString_Now_mod(Calendar.DAY_OF_YEAR, iDayOffset).substring(0,"yyyy-MM-dd ".length());
                    sTimepart = sOrigSchedDateTime.substring("yyyy-MM-dd ".length());
                    sSchedDateTime = sDatepart + sTimepart;
                    lAlertTime = WVS_Utils.convertDBdateStringToMilliSeconds(sSchedDateTime) - System.currentTimeMillis();
                }
            } while (lAlertTime <= 0);
            tmpEO.setDate(sSchedDateTime);
            EventObjectRepo eor = new EventObjectRepo(context);
            eor.update(tmpEO);
        } else {
//              add logic for monthly, yearly
            lAlertTime = WVS_Utils.convertDBdateStringToMilliSeconds(sOrigSchedDateTime) - System.currentTimeMillis();
        }

        int random = (int) (Math.random() * 50 + 1);
//        Log.i(TAG, " WVS random generated NOTIFICATION ID = " + random);
        String sTag_Notification = "";
//        sTag_Notification = tmpNote.getNoteId().toString() + " " + tmpEO.getId() + " " + tmpEO.getDate();
        sTag_Notification = tmpEO.getmNoteId().toString() + " " + tmpEO.getId() + " " + tmpEO.getDate();

        if (sEventType.contains(EVENTTYPE_NOTIFICATION) || sEventType.contains(EVENTTYPE_SCHEDULE_NOTIFICATION)) {
            appLog(TAG, " WVS generated notification tag = " + sTag_Notification);
            Data data = new Data.Builder()
                    .putString("title", tmpEO.getMessage())  //tmpNote.getNoteItem()
                    .putString("text", tmpEO.getMessageTag()) //tmpNote.getTag()
                    .putInt("id", random)
                    .putString("notificationtag", sTag_Notification)
                    .build();
            NotificationHandler.scheduleReminder(lAlertTime, data, sTag_Notification);
            appLog(TAG, " WVS Scheduled new Notification " + sTag_Notification);
        } else if (sEventType.contains(EVENTTYPE_TEXTMSG)) {
            appLog(TAG, " WVS generated SMS Text tag = " + sTag_Notification);
            String sContact = tmpEO.getToContact();
            Data data = new Data.Builder()
                    .putString("title", tmpEO.getMessage()) //tmpNote.getNoteItem()
                    .putString("text", tmpEO.getMessage() + " \n " + tmpEO.getMessageTag()) //tmpNote.getNoteItem() + " \n " + tmpNote.getTag()
                    .putString("phonenumber", sContact)
                    .putString("smstexttag", sTag_Notification)
                    .putInt("id", random)
                    .putString("notificationtag", sTag_Notification)
                    .build();
            SMSTextHandler.scheduleReminder(lAlertTime, data, sTag_Notification);
            appLog(TAG, " WVS Scheduled new SMS Text Tag " + sTag_Notification);
        } else if (sEventType.contains(EVENTTYPE_EMAIL)) {
//                                        TODO SCHEDULE EMAILS
        }

    }
    public static String translateWeekdayNumbers(String sCalendarWeekDayNumbers){
        String sRet = "";

        if (sCalendarWeekDayNumbers.contains(String.valueOf(Calendar.SUNDAY))){
         sRet = "Su,";
        }
        if (sCalendarWeekDayNumbers.contains(String.valueOf(Calendar.MONDAY))){
            sRet = sRet + "Mo,";
        }
        if (sCalendarWeekDayNumbers.contains(String.valueOf(Calendar.TUESDAY))){
            sRet = sRet + "Tu,";
        }
        if (sCalendarWeekDayNumbers.contains(String.valueOf(Calendar.WEDNESDAY))){
            sRet = sRet + "We,";
        }
        if (sCalendarWeekDayNumbers.contains(String.valueOf(Calendar.THURSDAY))){
            sRet = sRet + "Th,";
        }
        if (sCalendarWeekDayNumbers.contains(String.valueOf(Calendar.FRIDAY))){
            sRet = sRet + "Fr,";
        }
        if (sCalendarWeekDayNumbers.contains(String.valueOf(Calendar.SATURDAY))){
            sRet = sRet + "Sa,";
        }
        return sRet;
    }

    public static int getCountOfChar(String phrase, char sCharacter){
        int cnt = 0;
        for (int i = 0; i < phrase.length(); i++ ){
            if (phrase.charAt(i) == sCharacter){
                cnt = cnt + 1;
            }
        }
        return cnt;
    }


    public String getEventTypeFromCalendarLabel(String label){
        //          label -> Event type: Time info [<Weekly>,time]: Event info OR "long click to add date/time"

        String sEventType = "";
        if (label.contains(":")) {
            sEventType = label.substring(0, label.indexOf(":")).trim();
        }
        return  sEventType;
    }

    public String getTimeInfoFromCalendarLabel(String label){
        //          label -> Event type: Time info [<occurrance>,time] OR "long click to add date/time"

        String sTimeInfo = "";
        if (label.contains(":")) {
            int pos1 = label.indexOf(":") + 1;
//            int pos2 = label.indexOf(",") + 1;
//            if (label.contains(",")) {
//                sTimeInfo = label.substring(pos2).trim();
//            } else
            if (label.contains(CALENDAR_LONGCLICKTOADD)) {
                // long click to add date/time: advisory
            } else {
                //long click to add date/time: <Weekly>,2020-08-19 09:20 AM
                sTimeInfo = label.substring(pos1).trim();
            }

        }
        return  sTimeInfo;
    }

    public String getTimeFromCalendarLabel(String label){
        //          label -> Event type: Time info [<occurrance>,time]: Event info OR "long click to add date/time"
        String sTimeInfo = getTimeInfoFromCalendarLabel(label);
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

    public String getEventInfoFromCalendarLabel(String label){
        //          label -> Event type: Time info [<Weekly>,time]: Event info OR "long click to add date/time"

        int cnt = getCountOfChar(label,':');
        String sEventInfo = "";
        if (cnt == 3) {
            sEventInfo = label.substring(label.lastIndexOf(":") + 1).trim();
        }
        return  sEventInfo;
    }

    public String updateCalendarlblEventTime(String label, String snewEventTime){
        //          label -> Event type: Time info [<occurrance>,time]: Event info
        String sEventType = getEventTypeFromCalendarLabel(label);
        String sEventTime =  getTimeInfoFromCalendarLabel(label);
        String retVal = "";

        if (sEventTime.contains(",")){
            String sOccurance = "";
            sOccurance = sEventTime.substring(0, sEventTime.indexOf(",")).trim();
            sEventTime = sOccurance + ", " + snewEventTime;
        } else {
            sEventTime = snewEventTime;
        }
        retVal = sEventType + ": " + sEventTime;
        return retVal;
    }

    public String updateCalendarlblEventOccurance(String label, String snewEventOccuranc){
        //          label -> Event type: Time info [<occurrance>,time]: Event info
        String sEventType = getEventTypeFromCalendarLabel(label);  //long click to add date/time: 2020-08-19 09:20 AM: advisory
        String sEventTime =  getTimeInfoFromCalendarLabel(label);
        String sTime = "";
        String retVal = "";

        if (sEventTime.contains(",")){
            sTime = sEventTime.substring(sEventTime.indexOf(",") + 1).trim();
            sEventTime = snewEventOccuranc + ", " + sTime;
        } else {
            sEventTime = snewEventOccuranc + ", " + sEventTime;
        }
        retVal = sEventType + ": " + sEventTime;
        return retVal;
    }

    public static boolean isDateValid(String sdate, String spattern){
        boolean bRet;
        try {
            DateFormat df = new SimpleDateFormat(spattern);
            df.setLenient(false);
            df.parse(sdate);
            bRet = true;
        } catch (ParseException e) {
            bRet = false;
        }

        return bRet;
    }

    public static String getstrCurrentYear(){
        String strCurrentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        return strCurrentYear;
    }
}


