package com.linxy.gradeorganizer.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.StartupActivity;
import com.linxy.gradeorganizer.com.linxy.adapters.SRVAdapter;
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
    NestedScrollView nestedScrollView;
    SharedPreferences.Editor editor;
    DatabaseHelper myDB;
    DatabaseHelperSubjects myDBS;
    CardView myCv;
    TextView tvInsufficient;
    RecyclerView recyclerView;


    ViewGroup linearLayout;

    private double insuff = 0;
    private boolean showInsufficient;

    private boolean roundMarks;

    public class SAverage {
        public String sAverageName;
        public String sAverageAverage;
        public int sAverageColor;

        SAverage(String sAverageName, String sAverageAverage, int sAverageColor){
            this.sAverageName = sAverageName;
            this.sAverageAverage = sAverageAverage;
            this.sAverageColor = sAverageColor;
        }


    }

    ArrayList<SAverage> averages;
    SRVAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tab_1, container, false);
        myCv = (CardView) v.findViewById(R.id.cardview_insufficient);
        myDB = new DatabaseHelper(getActivity().getBaseContext());
        myDBS = new DatabaseHelperSubjects(getActivity().getBaseContext());
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

        recyclerView = (RecyclerView) v.findViewById(R.id.avg_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(llm);

        averages = new ArrayList<>();
        adapter = new SRVAdapter(averages);
        fillList();
        recyclerView.setAdapter(adapter);

        return v;


    }

    @Override
    public void onResume() {
        Log.i("OnResume", " ISCALLED ");
        super.onResume();
        fillList();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initPrefs(){
        showInsufficient = prefs.getBoolean("insufficient", true);
        if (prefs.getInt("roundGrade", 0) == 0)
            roundMarks = false;
        else
            roundMarks = true;
    }

    private void calculateTotalAverage(){
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

        if(Double.isNaN(exactAverage)){
            allSubjectAverages.setText(getResources().getString(R.string.hyphen));
        } else {
            allSubjectAverages.setText(String.format("%.2f", exactAverage));
        }
        if (exactAverage < 4.0) {
            allSubjectAverages.setTextColor(getResources().getColor(R.color.ColorFlatRed));
        } else {
            allSubjectAverages.setTextColor(getResources().getColor(R.color.ColorFlatGreen));
        }
        subjectCur.close();
    }

    public void fillList(){

        averages.clear();
        initPrefs();
        calculateTotalAverage();

        //linearLayout.removeAllViews();

        double insufficientMarks = 0;



        // Open Database of Subjects
        Cursor sc = myDBS.getAllData();
       // String listArray[];
        //listArray = new String[sc.getCount()];
        int i = 0;
        while (sc.moveToNext()) {

            String gradeSubjectName = "";
            String gradeSubjectAverage = "";
            int gradeSubjectColor = 0;


           // listArray[i] = sc.getString(1) + "  " + String.format("%.2f", getAverage(sc.getString(1), roundMarks));

            if (getAverage(sc.getString(1), roundMarks) < 4.0 && getAverage(sc.getString(1), roundMarks) != -1) {
                insufficientMarks += (4.0 - getAverage(sc.getString(1), roundMarks));
            }


//            TextView tv = new TextView(getActivity().getBaseContext());
//            tv.setText(listArray[i]);
//            tv.setTextColor(getResources().getColor(R.color.ColorPrimary));
//            tv.setTextSize(10);

//            View layoutC = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.grades_list, linearLayout, false);
//            TextView cName = (TextView) layoutC.findViewById(R.id.gl_subname);
//            TextView cGrade = (TextView) layoutC.findViewById(R.id.gl_subGrade);
//            ImageView cImage = (ImageView) layoutC.findViewById(R.id.gl_image);

            if (getAverage(sc.getString(1), false) >= 3.75 && getAverage(sc.getString(1), false) < 4) {
                //cImage.setColorFilter(getResources().getColor(R.color.FlatOrange), PorterDuff.Mode.SRC_ATOP);
//                gradeSubjectColor = Integer.toHexString(getResources().getColor(R.color.FlatOrange));
                gradeSubjectColor = getResources().getColor(R.color.FlatOrange);
            }

            if (getAverage(sc.getString(1), false) < 3.75) {
                //cImage.setColorFilter(getResources().getColor(R.color.ColorFlatRed), PorterDuff.Mode.SRC_ATOP);
//                gradeSubjectColor = Integer.toHexString(getResources().getColor(R.color.ColorFlatRed));
                gradeSubjectColor = getResources().getColor(R.color.ColorFlatRed);

            }
            if (getAverage(sc.getString(1), false) >= 4.0) {
                //cImage.setColorFilter(getResources().getColor(R.color.ColorFlatGreen), PorterDuff.Mode.SRC_ATOP);
//                gradeSubjectColor = Integer.toHexString(getResources().getColor(R.color.ColorFlatGreen));
                gradeSubjectColor = getResources().getColor(R.color.ColorFlatGreen);


            }

          //  cName.setText(sc.getString(1).toString());
            gradeSubjectName = sc.getString(1).toString();

            if(getAverage(sc.getString(1), roundMarks) == -1){
//                cGrade.setText(getResources().getString(R.string.triplehyphen));
                gradeSubjectAverage = getResources().getString(R.string.triplehyphen);
            } else {
//                cGrade.setText(String.format("%.2f", getAverage(sc.getString(1), roundMarks)));
                gradeSubjectAverage = String.format("%.2f", getAverage(sc.getString(1), roundMarks));
            }
            //linearLayout.addView(layoutC);

            averages.add(i, new SAverage(gradeSubjectName, gradeSubjectAverage, gradeSubjectColor));
            i++;
        }

        sc.close();


        if (showInsufficient) {

            if (roundMarks) {
                tvInsufficient.setText(String.valueOf(roundToHalf(insufficientMarks)));
            } else {
                tvInsufficient.setText(String.format("%.2f", insufficientMarks));
            }
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });




    }


    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);

        if (getActivity() != null) {
            if (visible) {
                fillList();

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

        if (roundSubjectGrades) {
            return roundToHalf(average);
        } else {
            return average;
        }
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


