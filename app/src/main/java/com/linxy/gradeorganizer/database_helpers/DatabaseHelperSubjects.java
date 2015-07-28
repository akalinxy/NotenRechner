package com.linxy.gradeorganizer.database_helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by minel_000 on 27/7/2015.
 */
public class DatabaseHelperSubjects extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "subjects.db";
    public static final String TABLE_NAME = "subjects_table";
    public static final String COL_1 = "_id";
    public static final String COL_2 = "SUBJECT";
    public static final String COL_3 = "SUBJECT_FACTOR";

    public DatabaseHelperSubjects(Context context) {
        super(context, DATABASE_NAME, null, 1);
        // System.out.print("NJNA");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, SUBJECT TEXT, SUBJECT_FACTOR INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String subjectname, String subjectfactor){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, subjectname);
        contentValues.put(COL_3, subjectfactor);
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

    public boolean updateData(String id, String subjectname, String subjectfactor){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, subjectname);
        contentValues.put(COL_3, subjectfactor);

        db.update(TABLE_NAME, contentValues, "_id = ?", new String[]{id});
        db.close();
        return true;
    }

    public Integer deleteData(String id){
       SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "_id = ?", new String[] {id});
    }
}
