package com.linxy.gradeorganizer.database_helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by linxy on 7/27/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "grades.db";
    public static final String TABLE_NAME = "grades_table";
    public static final String COL_1 = "_id";
    public static final String COL_2 = "SUBJECT_NAME";
    public static final String COL_3 = "GRADE_NAME";
    public static final String COL_4 = "GRADE";
    public static final String COL_5 = "FACTOR";
    public static final String COL_6 = "DATE";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        // System.out.print("NJNA");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, SUBJECT_NAME TEXT, GRADE_NAME TEXT, GRADE INTEGER, FACTOR INTEGER, DATE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String subjectname, String gradename, String grade, String factor, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, subjectname);
        contentValues.put(COL_3, gradename);
        contentValues.put(COL_4, grade);
        contentValues.put(COL_5, factor);
        contentValues.put(COL_6, date);
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

    public boolean hasObject(String id) {
        SQLiteDatabase db = getWritableDatabase();
        String selectString = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_2 + " =?";

        // Add the String you are searching by here.
        // Put it in an array to avoid an unrecognized token error
        Cursor cursor = db.rawQuery(selectString, new String[] {id});

        boolean hasObject = false;
        if(cursor.moveToFirst()){
            hasObject = true;

            //region if you had multiple records to check for, use this region.

            int count= 0;
            while(cursor.moveToNext()){
                count++;
            }
            //here, count is records found
            Log.d("t", String.format("%d records found", count));

            //endregion

        }

        cursor.close();          // Dont forget to close your cursor
        db.close();              //AND your Database!
        return hasObject;
    }

    public boolean updateData(String id, String subjectname, String gradename, String grade, String gradefactor, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, subjectname);
        contentValues.put(COL_3, gradename);
        contentValues.put(COL_4, grade);
        contentValues.put(COL_5, gradefactor);
        contentValues.put(COL_6, date);

        db.update(TABLE_NAME, contentValues, "_id = ?", new String[]{id});
        db.close();
        return true;
    }

    public Integer deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "_id = ?", new String[] {id});
    }
}
