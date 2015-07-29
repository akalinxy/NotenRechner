package com.linxy.gradeorganizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;

import java.util.ArrayList;

/**
 * Created by linxy on 7/26/15.
 */

public class Tab1 extends Fragment {

    ListView lvSubjectAverages;
    TextView allSubjectAverages;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    DatabaseHelper myDB;
    DatabaseHelperSubjects myDBS;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.tab_1, container, false);

        myDB = new DatabaseHelper(getActivity().getBaseContext());
        myDBS = new DatabaseHelperSubjects(getActivity().getBaseContext());

        allSubjectAverages = (TextView) v.findViewById(R.id.average_grade);

        lvSubjectAverages = (ListView) v.findViewById(R.id.list_subject_average);
        prefs = getActivity().getSharedPreferences(StartupActivity.PREFS, 0);
        editor = prefs.edit();


        populareView(v);





        return  v;


    }

    private double getAverage(String subjectName){
        Cursor gradesCur = myDB.getAllData();
        double avg = 0;
        ArrayList<Double> grades = new ArrayList<Double>();
        double total = 0;

        int i = 0;
        while(gradesCur.moveToNext()){
            if(gradesCur.getString(1).equals(subjectName)){
                grades.add(i, Double.parseDouble(gradesCur.getString(3)));
                i++;
            }
        }

        for(int s = 0; s < grades.size(); s++){
            total += grades.get(s);
        }

        if(grades.size() == 0) return 0;

       avg = total / grades.size();


        gradesCur.close();
        return  avg;
    }

    private void populareView(View view){


        boolean showInsufficient = prefs.getBoolean("insufficient", true);
        Cursor gradesCur = myDB.getAllData();
        String listArray[];
        double insufficientMarks = 0;

        double averageAll;
        double totalAll = 0;
        int amountAll;

        Cursor subjectCur = myDBS.getAllData();

        amountAll = 0;
        while(subjectCur.moveToNext()){
            totalAll += getAverage(subjectCur.getString(1));
            if(!(getAverage(subjectCur.getString(1)) == 0.0)){
                amountAll += 1;
            }
        }

        averageAll = totalAll / amountAll;
        allSubjectAverages.setText(String.format("%.3f",averageAll ));


        if(showInsufficient) {
            listArray = new String [subjectCur.getCount() + 1];
            listArray[0] = "Mangelpunkte: " + String.valueOf(insufficientMarks);
            int i = 1;
            subjectCur.close();
            Cursor sc = myDBS.getAllData();

            while(sc.moveToNext()){
                listArray[i] = sc.getString(1) + "  " + getAverage(sc.getString(1));
                i++;
            }

            sc.close();
        } else {
            listArray = new String[subjectCur.getCount()];
            int i = 0;
            while(subjectCur.moveToNext()){
                listArray[i] = subjectCur.getString(1) + "  " + getAverage(subjectCur.getString(1));
                i++;
            }

        }




        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, listArray);
        lvSubjectAverages.setAdapter(adapter);


    }



    private void updateTotalAverage(){

    }

    @Override
    public void onStop(){
        super.onStop();
        myDBS.close();
        myDB.close();

    }


}
