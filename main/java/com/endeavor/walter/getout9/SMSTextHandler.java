package com.endeavor.walter.getout9;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SMSTextHandler extends Worker {
    public SMSTextHandler(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    final String TAG = SMSTextHandler.class.getSimpleName();

    @NonNull
    @Override
    public Result doWork() {
//        return null;
        String message = getInputData().getString("text");
        String phone = getInputData().getString("phonenumber");
        sendMySMS(phone, message);
        return Result.success();
    }

    public void sendMySMS(String phone, String message ) {
//https://www.androidtutorialpoint.com/basics/send-sms-programmatically-android-tutorial/
//        String phone = phoneEditText.getText().toString();
//        String message = messageEditText.getText().toString();

        //Check if the phoneNumber is empty
        if (phone.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Enter a Valid Phone Number", Toast.LENGTH_SHORT).show();
        } else {
//            Log.i(TAG, " WVS sms text phone# " + phone);
//            Log.i(TAG, " WVS sms text msg " + message);
            SmsManager sms = SmsManager.getDefault();
            // if message length is too long messages are divided
            List<String> messages = sms.divideMessage(message);
            for (String msg : messages) {

                PendingIntent sentIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_SENT"), 0);
                PendingIntent deliveredIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_DELIVERED"), 0);
                sms.sendTextMessage(phone, null, msg, sentIntent, deliveredIntent);
            }
//          delete event...
            String sTag_SMSText = getInputData().getString("smstexttag");
            int iEventId = WVS_Utils.getEventId_NotificationTag(sTag_SMSText);
            EventObjectRepo eor = new EventObjectRepo(getApplicationContext());
            eor.deletebyEventId(iEventId);

        }
    }

    public static void scheduleReminder(long duration, Data data, String tag) {
        OneTimeWorkRequest SMSTextWork = new OneTimeWorkRequest.Builder(SMSTextHandler.class)
                .setInitialDelay(duration, TimeUnit.MILLISECONDS).addTag(tag)
                .setInputData(data).build();

        WorkManager instance = WorkManager.getInstance();
        instance.enqueue(SMSTextWork);

    }


    public static void cancelReminder(String tag) {
        WorkManager instance = WorkManager.getInstance();
        instance.cancelAllWorkByTag(tag);
    }


}
