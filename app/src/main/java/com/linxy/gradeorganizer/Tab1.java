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
import com.linxy.gradeorganizer.tabs.UpdatableFragment;

import java.util.ArrayList;

/**
 * Created by linxy on 7/26/15.
 */

public class Tab1 extends Fragment{

    ListView lvSubjectAverages;
    TextView allSubjectAverages;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    DatabaseHelper myDB;
    DatabaseHelperSubjects myDBS;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tab_1, container, false);

        myDB = new DatabaseHelper(getActivity().getBaseContext());
        myDBS = new DatabaseHelperSubjects(getActivity().getBaseContext());

        allSubjectAverages = (TextView) v.findViewById(R.id.average_grade);

        lvSubjectAverages = (ListView) v.findViewById(R.id.list_subject_average);
        prefs = getActivity().getSharedPreferences(StartupActivity.PREFS, 0);
        editor = prefs.edit();


        populareView(v);
        return v;


    }

    @Override
    public void onResume(){
        super.onResume();
        populareView(getView());
    }



    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (getActivity() != null )
        {
            if (visible) {
                populareView(getView());
            }
        }
    }

    //TODO Disallow Grade or Factor of 0!
    private double getAverage(String subjectName) {
        /* Returns -1 if the subject has no registered Grades */

        Cursor gradesCur = myDB.getAllData();

        double numerator = 0;
        double denominator = 0;

        while (gradesCur.moveToNext()) { /* This Loop will cycle through all grade entries */

            if (gradesCur.getString(1).equals(subjectName)) { /* This will Trigger IF and only IF a registered grade is found with subjectName */

                numerator += (Double.parseDouble(gradesCur.getString(4)) / 100) * Double.parseDouble(gradesCur.getString(3));
                denominator += Double.parseDouble(gradesCur.getString(4)) / 100 ;

            }
        }
        gradesCur.close();

        if(numerator == 0) return -1;
        return (numerator / denominator);
    }

    private void populareView(View view) {

        boolean showInsufficient = prefs.getBoolean("insufficient", true);
        String listArray[];
        double insufficientMarks = 0;

        double averageAll = 0;
        double subjectTop = 0;
        double subjectBottom = 0;

        Cursor subjectCur = myDBS.getAllData();

        while (subjectCur.moveToNext()) {
            if(getAverage(subjectCur.getString(1)) == -1){ /* Should this statement execute, then said subject has no registered grades. */

            } else { /* Average of given subject was calculated. */

                subjectTop += getAverage(subjectCur.getString(1)) * (Double.parseDouble(subjectCur.getString(2)) * 100.0);
                subjectBottom += Double.parseDouble(subjectCur.getString(2)) * 100.0;
            }
        }

        subjectCur.close();

        averageAll = subjectTop / subjectBottom;

        allSubjectAverages.setText(String.format("%.1f", averageAll));


        if (showInsufficient) {
            listArray = new String[subjectCur.getCount() + 1];
            listArray[0] = "Mangelpunkte: " + String.valueOf(insufficientMarks);
            int i = 1;
            subjectCur.close();
            Cursor sc = myDBS.getAllData();

            while (sc.moveToNext()) {
                listArray[i] = sc.getString(1) + "  " + String.format("%.2f", getAverage(sc.getString(1)));
                i++;
            }

            sc.close();
        } else {
            listArray = new String[subjectCur.getCount()];
            int i = 0;
            subjectCur.close();
            Cursor sc = myDBS.getAllData();
            while (sc.moveToNext()) {
                listArray[i] = sc.getString(1) + "  " + String.format("%.2f", getAverage(sc.getString(1)));
                i++;
            }

            sc.close();

        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, listArray);
        lvSubjectAverages.setAdapter(adapter);


    }


    private void updateTotalAverage() {

    }

    @Override
    public void onStop() {
        super.onStop();
        myDBS.close();
        myDB.close();

    }


}


