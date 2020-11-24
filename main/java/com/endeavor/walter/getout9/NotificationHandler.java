package com.endeavor.walter.getout9;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
//https://inducesmile.com/android/schedule-onetime-notification-with-android-workmanager/

public class NotificationHandler extends Worker {
    public NotificationHandler(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
//        return null;
//        int id = (int) getInputData().getLong("id", 77);
        int id = (int) getInputData().getInt("id", 72);
        String sTitle = getInputData().getString("title");
        String sText = getInputData().getString("text");
        String sTag_Notification = getInputData().getString("notificationtag");

        sendNotification(sTitle, sText, id, sTag_Notification);
        return Result.success();
    }

    private void sendNotification(String title, String text, int id, String tag) {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//      https://stackoverflow.com/questions/1198558/how-to-send-parameters-from-a-notification-click-to-an-activity
//      FLAG_ACTIVITY_SINGLE_TOP  If set, the activity will not be launched if it is already running at the top of the history stack.
//      FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK  // Sets the Activity to start in a new, empty task
        intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("id", id);
        intent.putExtra("notificationtag", tag);  // so in MainActivity we can delete event and navigate to notification's noteitem...
//      PendingIntent.FLAG_CANCEL_CURRENT  https://stackoverflow.com/questions/18049352/notification-created-by-intentservice-uses-always-a-wrong-intent/18049676#18049676
//      PendingIntent.FLAG_UPDATE_CURRENT  Flag indicating that if the described PendingIntent already exists, then keep it but replace its extra data with what is in this new Intent.
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 99, intent, PendingIntent.FLAG_UPDATE_CURRENT);  // original requestCode=0 flags=0

        NotificationManager notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);

        Objects.requireNonNull(notificationManager).notify(id, notification.build());
    }

    public static void scheduleReminder(long duration, Data data, String tag) {
        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotificationHandler.class)
                .setInitialDelay(duration, TimeUnit.MILLISECONDS).addTag(tag)
                .setInputData(data).build();

        WorkManager instance = WorkManager.getInstance();
        instance.enqueue(notificationWork);
    }


    public static void cancelReminder(String tag) {
        WorkManager instance = WorkManager.getInstance();
        instance.cancelAllWorkByTag(tag);
    }

    public static void cancelALLReminders() {
        WorkManager instance = WorkManager.getInstance();
        instance.cancelAllWork();
    }


}
