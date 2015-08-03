package com.linxy.gradeorganizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;
import com.linxy.gradeorganizer.tabs.UpdatableFragment;

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
    CardView myCv;
    TextView tvInsufficient;

    ViewGroup linearLayout;

    private double insuff = 0;
    private boolean showInsufficient;

    private boolean roundMarks;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tab_1, container, false);
        myCv = (CardView) v.findViewById(R.id.cardview_insufficient);
        myDB = new DatabaseHelper(getActivity().getBaseContext());
        myDBS = new DatabaseHelperSubjects(getActivity().getBaseContext());
        linearLayout = (LinearLayout) v.findViewById(R.id.tabone_linearlayout);
        allSubjectAverages = (TextView) v.findViewById(R.id.average_grade);


        prefs = getActivity().getSharedPreferences(StartupActivity.PREFS, 0);
        editor = prefs.edit();
        showInsufficient = prefs.getBoolean("insufficient", true);

        tvInsufficient = (TextView) v.findViewById(R.id.insuff_marks);


        if (showInsufficient) {
            myCv.setVisibility(View.VISIBLE);

        } else {
            myCv.setVisibility(View.GONE);
        }

        populareView(v);
        return v;


    }

    @Override
    public void onResume() {
        super.onResume();
        populareView(getView());
    }


    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);

        if (getActivity() != null) {
            if (visible) {
                populareView(getView());

                if (showInsufficient) {
                    myCv.setVisibility(View.VISIBLE);

                } else {
                    myCv.setVisibility(View.GONE);
                }
            }
        }


    }

    //TODO Disallow Grade or Factor of 0!
    private double getAverage(String subjectName, boolean roundSubjectGrades) {
        /* Returns -1 if the subject has no registered Grades */

        Cursor gradesCur = myDB.getAllData();

        double numerator = 0;
        double denominator = 0;
        double average = 0;

        while (gradesCur.moveToNext()) { /* This Loop will cycle through all grade entries */

            if (gradesCur.getString(1).equals(subjectName)) { /* This will Trigger IF and only IF a registered grade is found with subjectName */

                numerator += (Double.parseDouble(gradesCur.getString(4)) / 100) * Double.parseDouble(gradesCur.getString(3));
                denominator += Double.parseDouble(gradesCur.getString(4)) / 100;

            }
        }
        gradesCur.close();

        if (numerator == 0) return -1;

        average = numerator / denominator;

        if(roundSubjectGrades){
            return roundToHalf(average);
        } else {
            return  average;
        }

    }


    private void populareView(View view) {



        showInsufficient = prefs.getBoolean("insufficient", true);

        if (prefs.getInt("roundGrade", 0) == 0)
            roundMarks = false;
        else
            roundMarks = true;


        linearLayout.removeAllViews();

        double insufficientMarks = 0;

        double averageAll = 0;
        double subjectTop = 0;
        double subjectBottom = 0;

        Cursor subjectCur = myDBS.getAllData();

        while (subjectCur.moveToNext()) {
            if (getAverage(subjectCur.getString(1), roundMarks) == -1) { /* Should this statement execute, then said subject has no registered grades. */

            } else { /* Average of given subject was calculated. */

                subjectTop += getAverage(subjectCur.getString(1), roundMarks) * (Double.parseDouble(subjectCur.getString(2)) * 100.0);
                subjectBottom += Double.parseDouble(subjectCur.getString(2)) * 100.0;
            }
        }

        subjectCur.close();

        double exactAverage = subjectTop / subjectBottom;

        if (roundMarks) {

            allSubjectAverages.setText(String.valueOf(roundToHalf(exactAverage)));
            if (roundToHalf(exactAverage) < 4.0) {
                allSubjectAverages.setTextColor(getResources().getColor(R.color.ColorFlatRed));
            } else {
                allSubjectAverages.setTextColor(getResources().getColor(R.color.ColorFlatGreen));
            }
        } else {
            allSubjectAverages.setText(String.format("%.2f", exactAverage));
            if (exactAverage < 4.0) {
                allSubjectAverages.setTextColor(getResources().getColor(R.color.ColorFlatRed));
            } else {
                allSubjectAverages.setTextColor(getResources().getColor(R.color.ColorFlatGreen));
            }

        }

        subjectCur.close();

        // Open Database of Subjects
        Cursor sc = myDBS.getAllData();
        String listArray[];
        listArray = new String[subjectCur.getCount()];
        int i = 0;
        while (sc.moveToNext()) {
            listArray[i] = sc.getString(1) + "  " + String.format("%.2f",getAverage(sc.getString(1), roundMarks));

            if(getAverage(sc.getString(1), roundMarks) < 4.0 && getAverage(sc.getString(1), roundMarks) != -1){
                insufficientMarks += (4.0 - getAverage(sc.getString(1), roundMarks));
            }


//            TextView tv = new TextView(getActivity().getBaseContext());
//            tv.setText(listArray[i]);
//            tv.setTextColor(getResources().getColor(R.color.ColorPrimary));
//            tv.setTextSize(10);

            View layoutC = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.grades_list, linearLayout, false);
            TextView cName = (TextView) layoutC.findViewById(R.id.gl_subname);
            TextView cGrade = (TextView) layoutC.findViewById(R.id.gl_subGrade);
            ImageView cImage = (ImageView) layoutC.findViewById(R.id.gl_image);

            if(getAverage(sc.getString(1), false) >= 3.75 && getAverage(sc.getString(1), false) < 4){
                cImage.setColorFilter(getResources().getColor(R.color.FlatOrange), PorterDuff.Mode.SRC_ATOP);
            }

            if(getAverage(sc.getString(1), false) < 3.75){
                cImage.setColorFilter(getResources().getColor(R.color.ColorFlatRed), PorterDuff.Mode.SRC_ATOP);
            }
            if(getAverage(sc.getString(1), false) >= 4.0){
                cImage.setColorFilter(getResources().getColor(R.color.ColorFlatGreen), PorterDuff.Mode.SRC_ATOP);
            }

            cName.setText(sc.getString(1).toString());
            cGrade.setText(String.format("%.2f",getAverage(sc.getString(1), roundMarks)));
            linearLayout.addView(layoutC);
            i++;
        }

        sc.close();


        if(showInsufficient){
            if(roundMarks){
                tvInsufficient.setText(String.valueOf(roundToHalf(insufficientMarks)));
            } else {
                tvInsufficient.setText(String.format("%.2f",insufficientMarks));
            }
        }


//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, listArray);
//
//        int adapterCount = adapter.getCount();
//
//        for (int s = 0; s < adapterCount; s++) {
//            TextView tv = (TextView) adapter.getView(s, null, null);
//            linearLayout.addView(tv);
//        }


        //   lvSubjectAverages.setAdapter(adapter);


    }

    @Override
    public void onStop() {
        super.onStop();
        myDBS.close();
        myDB.close();

    }

    public static double roundToHalf(double d) {
        return Math.round(d * 2) / 2.0;
    }



}


