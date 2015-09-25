package com.linxy.gradeorganizer.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.adapters.GradeAdapter;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;
import com.linxy.gradeorganizer.fragments.SubjectsFragment;
import com.linxy.gradeorganizer.objects.Grade;
import com.linxy.gradeorganizer.objects.Subject;

import java.util.ArrayList;

public class SubjectDetailActivity extends AppCompatActivity {

    public static final String TAG = SubjectDetailActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private ArrayList<Grade> mGrades;
    private GradeAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private Subject mSubject;
    
    public Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail);
        mContext = this;
        
        
        mGrades = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.scrollable_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new GradeAdapter(this, mGrades);
        mRecyclerView.setAdapter(mAdapter);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbar.setTitle("Fachname");

        ImageView header = (ImageView) findViewById(R.id.header);

        Intent intent = getIntent();

        mSubject = (Subject) intent.getSerializableExtra(SubjectsFragment.GRADE_OBJECT_TAG);

        LoadSubjectGradesTask lsgTask =  new LoadSubjectGradesTask();
        lsgTask.execute(mSubject.getName());

        getSupportActionBar().setTitle(mSubject.getName());

        Log.i(TAG, "inside oncreatea with subject " + mSubject.getName());



    }

    private class LoadSubjectGradesTask extends AsyncTask<String, Void, ArrayList<Grade>> {
        
        @Override
        protected ArrayList<Grade> doInBackground(String... params) {

            ArrayList<Grade> grades = new ArrayList<>();

            DatabaseHelper dbHelperGrades = new DatabaseHelper(mContext);
            Cursor cursor = dbHelperGrades.getAllData(); // TODO this can be optimized with the SEQUEL LIKE STATEMENT

            while (cursor.moveToNext()) {
                if (params[0].equals(cursor.getString(1))) { // The grade has same string in subject name as passed to this task
                    grades.add(new Grade(cursor.getString(1), cursor.getString(2), Double.valueOf(cursor.getString(3)), Integer.valueOf(cursor.getString(4)), cursor.getString(5)));
                }
            }
            return grades;
        }

        @Override
        protected void onPostExecute(ArrayList<Grade> grades) {
            Log.i(TAG, "onpostexecute with grades size" + grades.size());
            mGrades.clear();
            for (Grade grade : grades) {
                mGrades.add(grade);
            }

            mAdapter.notifyDataSetChanged();
        }
    }

}
