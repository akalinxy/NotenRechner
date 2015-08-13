package com.linxy.gradeorganizer.database_helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Linxy on 12/8/2015 at 20:58
 * Working on Grade Organizer in com.linxy.gradeorganizer.database_helpers
 */
public class DatabaseHelperScheduled extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "scheduled.db";
    public static final String TABLE_NAME = "scheduled_dates";
    public static final String COL_1 = "_id";
    public static final String COL_2 = "DATE";
    public static final String COL_3 = "SUBJECTNAME";
    public static final String COL_4 = "GRADENAME";



    public DatabaseHelperScheduled(Context context) {
        super(context, DATABASE_NAME, null, 1);
        // System.out.print("NJNA");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, DATE TEXT, SUBJECTNAME TEXT, GRADENAME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String date, String subject, String grade){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, date);
        contentValues.put(COL_3, subject);
        contentValues.put(COL_4, grade);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;

    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }




}