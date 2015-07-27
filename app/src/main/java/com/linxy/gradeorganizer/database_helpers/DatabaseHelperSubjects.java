package com.linxy.gradeorganizer.database_helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by minel_000 on 27/7/2015.
 */
public class DatabaseHelperSubjects extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "subjects.db";
    public static final String TABLE_NAME = "subjects_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "SUBJECT";

    public DatabaseHelperSubjects(Context context) {
        super(context, DATABASE_NAME, null, 1);
        // System.out.print("NJNA");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, SUBJECT_NAME TEXT, GRADE_NAME TEXT, GRADE INTEGER, GRADE_ROUNDED INTEGER, FACTOR INTEGER, DATE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String subjectname){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, subjectname);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;

    }
}
