package com.linxy.gradeorganizer.activities;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperCalendar;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Linxy on 8/8/2015 at 22:40
 * Working on Grade Organizer in com.linxy.gradeorganizer
 */
public class MyApp extends Application {

    DatabaseHelperCalendar dbc;

    public MyApp(){
        /* Open Database, Remove Outdated Calendar Entries, ... */
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Parse.initialize(getBaseContext(), "xxx", "xxx");

//        // register to be informed of activities starting up
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

                                               @Override
                                               public void onActivityCreated(Activity activity,
                                                                             Bundle savedInstanceState) {

                                                   // new activity created; force its orientation to portrait
                                                   activity.setRequestedOrientation(
                                                           ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                                               }

                                               @Override
                                               public void onActivityStarted(Activity activity) {

                                               }

                                               @Override
                                               public void onActivityResumed(Activity activity) {

                                               }

                                               @Override
                                               public void onActivityPaused(Activity activity) {

                                               }

                                               @Override
                                               public void onActivityStopped(Activity activity) {

                                               }

                                               @Override
                                               public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                                               }

                                               @Override
                                               public void onActivityDestroyed(Activity activity) {

                                               }
                                           });
    }
}
