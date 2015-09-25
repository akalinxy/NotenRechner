package com.linxy.gradeorganizer.fragments;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.activities.NewGradeActivity;
import com.linxy.gradeorganizer.adapters.AverageAdapter;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;
import com.linxy.gradeorganizer.objects.Grade;
import com.linxy.gradeorganizer.objects.Subject;
import com.linxy.gradeorganizer.objects.SubjectGrade;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class AverageFragment extends Fragment {

    public static final String TAG = AverageFragment.class.getSimpleName();

    private static final String GLOBAL_AVERAGE = "global_average";
    private static final String TOTAL_INSUFFICIENT = "total_insufficient";

    private RecyclerView mRecyclerView;
    private ArrayList<SubjectGrade> mGrades;
    private AverageAdapter mAdapter;

    private TextView mSubtitle;
    private TextView mGlobalAverage;
    private TextView mInsufficient;

    private LinearLayout mInsufficientLayout;

    private boolean mShowInsufficient;
    private boolean mRoundGrades;
    private boolean mTwoDigitGrades;

    private Button mNewGrade;


    public AverageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_average, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_subject_averages);
        mGrades = new ArrayList<>();
        mAdapter = new AverageAdapter(mGrades);

        mSubtitle = (TextView) rootView.findViewById(R.id.textview_average_subtitle);
        mGlobalAverage = (TextView) rootView.findViewById(R.id.textview_average_global_grade);
        mInsufficient = (TextView) rootView.findViewById(R.id.textview_insufficient_grade);
        mInsufficientLayout = (LinearLayout) rootView.findViewById(R.id.linearlayout_insufficient);
        mNewGrade = (Button) rootView.findViewById(R.id.button_new_grade);
        mNewGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewGradeActivity.class);

                startActivityForResult(intent, 0);
            }
        });

        setupPreferences();
        AverageGradesTask task = new AverageGradesTask();
        task.execute();

        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                // yes grade



                String subjectname = data.getStringExtra("subjectname");
                String gradename = data.getStringExtra("gradename");
                String grade = data.getStringExtra("grade");
                String gradefactor = data.getStringExtra("gradefactor");
                String gradedate = data.getStringExtra("gradedate");

                UploadGradeTask task = new UploadGradeTask();
                task.execute(new Grade(subjectname, gradename, Double.parseDouble(grade), Integer.parseInt(gradefactor), gradedate));

            } else {

                // no grade
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupPreferences();
    }

    private void setupPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mShowInsufficient = sharedPreferences.getBoolean(getString(R.string.pref_key_showinsufficient), false);
        mRoundGrades = sharedPreferences.getBoolean(getString(R.string.pref_key_roundgrades), false);
        mTwoDigitGrades = sharedPreferences.getBoolean(getString(R.string.pref_key_twodigitgrades), false);

        if (mRoundGrades) {
            mSubtitle.setText(R.string.text_roundsubjects_true);
        } else {
            mSubtitle.setText(R.string.text_roundsubjects_false);
        }

        if (mShowInsufficient) {
            mInsufficientLayout.setVisibility(View.VISIBLE);
        } else {
            mInsufficientLayout.setVisibility(View.GONE);
        }
    }

    private class UploadGradeTask extends AsyncTask<Grade, Void, Void> {

        @Override
        protected Void doInBackground(Grade... params) {

            DatabaseHelper dbGradeHelper = new DatabaseHelper(getActivity());
            Log.i(TAG, "Inserting grade into dbGradeHelper with " + params[0].getGrade());
            dbGradeHelper.insertData(params[0].getSubject(), params[0].getName(), String.valueOf(params[0].getGrade()), String.valueOf(params[0].getFactor()), params[0].getDate());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "OnPostExecute of InsertGrade");

            AverageGradesTask task = new AverageGradesTask();
            task.execute();
        }
    }

    private class AverageGradesTask extends AsyncTask<Void, SubjectGrade, HashMap<String, Double>> {

        public static final int NO_AVERAGE = -1;
        public static final int SUFFICIENT = -1;

        double numerator = 0;
        double denominator = 0;

        double insufficientGrade = 0;
        double averageGrade;

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            // code to run before executing the task
            mGrades.clear();
            mAdapter.notifyDataSetChanged();


        }


        @Override
        protected HashMap<String, Double> doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");

            // Code to run in the background thread
            // Open connection to the database helpers // TODO Make better database helpers

            DatabaseHelper dbGradeHelper = new DatabaseHelper(getActivity());
            DatabaseHelperSubjects dbSubjectsHelper = new DatabaseHelperSubjects(getActivity());

            HashMap<String, Double> result = new HashMap<>();


            //ArrayList<SubAvg> averages = new ArrayList<>();
        /* Fill averages with data of subject average, rounded inclusive, color, and name */
            Cursor cursorSubjects = dbSubjectsHelper.getAllData();
            Subject subjectOb;
            int i = 0;
            while (cursorSubjects.moveToNext()) {
                subjectOb = new Subject(cursorSubjects.getString(1), Integer.parseInt(cursorSubjects.getString(2)), mRoundGrades, getActivity());

                if (subjectOb.getAverage() == NO_AVERAGE) {
                /* If there is no average we make a simple entry for the adapter and leave it at that */
                    publishProgress(new SubjectGrade(subjectOb.getName(), -1));
                    //averages.add(i, new SubAvg(subjectOb.getName(), getActivity().getResources().getString(R.string.hyphen), getActivity().getResources().getColor(R.color.ColorYellow)));
                    i++;
                    continue;
                } else {
                /* If there is a average, we make a entry for the adapter and we also calculate possible insufficient marks, and calculate the global average */
                    publishProgress(new SubjectGrade(subjectOb.getName(), subjectOb.getAverage()));
                    //averages.add(i, new SubAvg(subjectOb.getName(), String.format("%.2f", subjectOb.getAverage()), subjectOb.getColor()));

                /* Calculate a total insufficient markvalue */
                    if (mShowInsufficient) {
                        double gi = subjectOb.getInsufficient();
                        if (!(gi == SUFFICIENT))
                            insufficientGrade += subjectOb.getInsufficient();
                    }
                /* Calculate the global average */
                    numerator += subjectOb.getAverage() * (subjectOb.getFactor() / 100);
                    denominator += (subjectOb.getFactor() / 100);
                    i++;
                }
            }

            cursorSubjects.close();
            dbSubjectsHelper.close();
            dbGradeHelper.close();

            averageGrade = numerator / denominator;

            result.put(TOTAL_INSUFFICIENT, insufficientGrade);
            result.put(GLOBAL_AVERAGE, averageGrade);

            return result;
        }

        @Override
        protected void onProgressUpdate(SubjectGrade... grade) {
            Log.i(TAG, "onProgressUpdate" + grade[0].getName() + " " + grade[0].getGrade());

            // Code to run to publish progress of the task
            mGrades.add(grade[0]);
            mAdapter.notifyDataSetChanged();
        }


        @Override
        protected void onPostExecute(HashMap<String, Double> info) {
            Log.i(TAG, "onPostExecute");

            // Code to run when the task is complete
            Double global_average = info.get(GLOBAL_AVERAGE);
            double total_insufficient = info.get(TOTAL_INSUFFICIENT);

            if (global_average.isNaN()) {
                mGlobalAverage.setText("Leer");
            } else {
                mGlobalAverage.setText(String.format("%.2f", global_average)); // TODO FORMATTING
            }

            mInsufficient.setText(String.format("%.2f",total_insufficient));
        }
    }


}
