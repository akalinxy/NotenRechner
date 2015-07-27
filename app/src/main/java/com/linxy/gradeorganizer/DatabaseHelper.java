package com.linxy.gradeorganizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



/**
 * Created by linxy on 7/27/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "grades.db";
    public static final String TABLE_NAME = "grades_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "SUBJECT_NAME";
    public static final String COL_3 = "GRADE_NAME";
    public static final String COL_4 = "GRADE";
    public static final String COL_5 = "GRADE_ROUNDED";
    public static final String COL_6 = "FACTOR";
    public static final String COL_7 = "DATE";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        System.out.print("NJNA");

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

    public boolean insertData(String subjectname, String gradename, String grade, String graderounded, String factor, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, subjectname);
        contentValues.put(COL_3, gradename);
        contentValues.put(COL_4, grade);
        contentValues.put(COL_5, graderounded);
        contentValues.put(COL_6, factor);
        contentValues.put(COL_7, date);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;

    }
}
