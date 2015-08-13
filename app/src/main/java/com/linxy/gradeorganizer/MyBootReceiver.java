package com.linxy.gradeorganizer;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.SystemClock;
import android.util.Log;

import com.linxy.gradeorganizer.database_helpers.DatabaseHelperScheduled;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Linxy on 12/8/2015 at 20:52
 * Working on Grade Organizer in com.linxy.gradeorganizer
 */
public class MyBootReceiver extends BroadcastReceiver
{
    private static final String TAG = "MyBootReceiver";
    private DatabaseHelperScheduled dbsc;
    private Context mContext;

    public void onReceive(Context context, Intent intent) {
        mContext = context;
        Log.d(TAG, "onReceive");
        dbsc = new DatabaseHelperScheduled(context);
        Cursor c = dbsc.getAllData();
        Calendar startDate = Calendar.getInstance();

        while(c.moveToNext()){
            String dateString = c.getString(1);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            java.util.Date convertedDate = new java.util.Date();
            try {
                convertedDate = dateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar endDate = Calendar.getInstance();
            endDate.setTime(convertedDate);

            /* Insert all dates into new scheduled dates */
            long days = daysBetween(startDate, endDate);
            long millis = TimeUnit.DAYS.toMillis(days);
            scheduleNotification(getNotification(c.getString(2) + "\n" + c.getString(3)), millis);  /* TODO MAKE THIS WORK */
        }

    }


    public static long daysBetween(Calendar startDate, Calendar endDate) {
        Calendar date = (Calendar) startDate.clone();
        long daysBetween = 0;
        while (date.before(endDate)) {
            date.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }

    private void scheduleNotification(Notification notification, long delay) {
        Intent notificationIntent = new Intent(mContext, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);

    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(mContext);
        builder.setContentTitle("Prufing Errinerung"); /* TODO MAKE STRING REFERENCE FOR REMEMBER TEST */
        builder.setContentTitle(content);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        return builder.build();
    }

}