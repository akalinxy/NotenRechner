package com.linxy.gradeorganizer;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;

/**
 * Created by linxy on 7/26/15.
 */

// This is the history of grades.

public class Tab2 extends Fragment{

    ListView lvRecentGrades;
    DatabaseHelper db;
    DatabaseHelperSubjects dbs;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.tab_2, container, false);

        lvRecentGrades = (ListView) v.findViewById(R.id.list_view_recent_grades);


        db = new DatabaseHelper(getActivity().getBaseContext());
        dbs = new DatabaseHelperSubjects(getActivity().getBaseContext());

        fillListView(v);




        return  v;
    }

    @Override
    public void onResume(){
        super.onResume();
        fillListView(getView());
    }


    private void fillListView(View v){

        String listArr[];

        Cursor cursor = db.getAllData();
        listArr = new String[cursor.getCount()];
        int i = 0;
        while(cursor.moveToNext()){
            listArr[i] = cursor.getString(1) +"\n"+ cursor.getString(2)+"\n"+cursor.getString(3)+"\n"+cursor.getString(5)+"\n"+ cursor.getString(6);
            i++;
        }

        cursor.close();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, listArr);
        lvRecentGrades.setAdapter(arrayAdapter);
    }


    @Override
    public void onStop(){
        super.onStop();
        db.close();
        dbs.close();
    }



}
