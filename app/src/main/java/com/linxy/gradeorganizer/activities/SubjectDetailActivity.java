package com.linxy.gradeorganizer.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.adapters.GradeAdapter;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;
import com.linxy.gradeorganizer.fragments.SubjectsFragment;
import com.linxy.gradeorganizer.objects.Grade;
import com.linxy.gradeorganizer.objects.Subject;
import com.linxy.gradeorganizer.utility.Constants;
import com.parse.ParseObject;

import java.util.ArrayList;

public class SubjectDetailActivity extends AppCompatActivity implements GradeAdapter.MyGradeClickListener {

    public static final String TAG = SubjectDetailActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private ArrayList<Grade> mGrades;
    private GradeAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private FloatingActionButton mNewGradeFab;

    private TextView mSubjectAverage;
    private TextView mSubjectInsufficient;
    private TextView mSubjectFactor;

    private Subject mSubject;

    public Context mContext;
    public ArrayList<Integer> mIds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail);
        mContext = this;


        mGrades = new ArrayList<>();
        mIds = new ArrayList<>();
        mNewGradeFab = (FloatingActionButton) findViewById(R.id.fab_new_subject);
        mRecyclerView = (RecyclerView) findViewById(R.id.scrollable_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new GradeAdapter(this, mGrades);
        mAdapter.setOnGradeClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mSubjectAverage = (TextView) findViewById(R.id.subjectdetail_average);
        mSubjectInsufficient = (TextView) findViewById(R.id.subjectdetail_insufficient);
        mSubjectFactor = (TextView) findViewById(R.id.subjectdetail_subjectfactor);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);


        Intent intent = getIntent();

        mSubject = (Subject) intent.getSerializableExtra(SubjectsFragment.GRADE_OBJECT_TAG);

        LoadSubjectGradesTask lsgTask = new LoadSubjectGradesTask();
        lsgTask.execute(mSubject.getName());

        mCollapsingToolbar.setTitle(mSubject.getName());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Log.i(TAG, "inside oncreatea with subject " + mSubject.getName());

        // We dont initialize with LoadHeaderTask as the subject is given to us pre loaded
        if (mSubject.getInsufficient() != -1.0) {
            mSubjectInsufficient.setText(String.format("%.2f", mSubject.getInsufficient()));
        } else {
            mSubjectInsufficient.setText("0");
        }

        if (mSubject.getAverage() == Subject.NO_AVERAGE) {
            mSubjectAverage.setText(R.string.subject_no_grades_yet);
        } else {
            mSubjectAverage.setText(String.valueOf(String.format("%.2f", mSubject.getAverage())));
        }

        mSubjectFactor.setText(mSubject.getFactor() + "%");

        mNewGradeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubjectDetailActivity.this, NewGradeActivity.class);
                intent.putExtra(Constants.NEWGRADE_SUBJECTSELECT, mSubject.getName());
                startActivityForResult(intent, 3);
            }
        });

        Log.i(TAG, "Started With Subject Values:" + mSubject.getAverage() + " " + mSubject.getInsufficient());

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "Pringing IDS");
        for (Integer s : mIds) {
            Log.i(TAG, String.valueOf(s));
        }
    }

    @Override
    public void onGradeItemClick(final int position, View view) {
        Toast.makeText(SubjectDetailActivity.this, "Clicked" + position, Toast.LENGTH_SHORT).show();

        switch (view.getId()) {
            case R.id.subjectdetail_deletegrade:
                // Delete the subject from the database, then update the recycler!
                DeleteGradeTask dst = new DeleteGradeTask();
                dst.execute(position);

                break;
            case R.id.subjectdetail_editgrade:
                // Here we update a subjects values and reflect the changes
                // So we need an alertdialog
                final LayoutInflater inflater = SubjectDetailActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.gradeinfo_dialog, null);

                final AlertDialog.Builder builder = new AlertDialog.Builder(SubjectDetailActivity.this);

                builder.setView(dialogView);

                final ToggleButton editGradeTButton = (ToggleButton) dialogView.findViewById(R.id.dialog_btn_edit);
                final EditText gradeNameEditText = (EditText) dialogView.findViewById(R.id.dialog_et_gradename);
                final EditText gradeEditText = (EditText) dialogView.findViewById(R.id.dialog_et_grade);
                final EditText gradeFactorEditText = (EditText) dialogView.findViewById(R.id.dialog_et_factor);
                final EditText gradeDateEditText = (EditText) dialogView.findViewById(R.id.dialog_et_date);
                final TextView gradeSubjectNameTextView = (TextView) dialogView.findViewById(R.id.dialog_tv_subject);

                gradeNameEditText.setFocusable(false);
                gradeEditText.setFocusable(false);
                gradeDateEditText.setFocusable(false);
                gradeFactorEditText.setFocusable(false);
                editGradeTButton.setChecked(false);

                gradeSubjectNameTextView.setText(mSubject.getName());
                editGradeTButton.setText(getString(R.string.edit));

                gradeNameEditText.setText(mGrades.get(position).getName());
                gradeFactorEditText.setText(String.valueOf(mGrades.get(position).getFactor()));
                gradeEditText.setText(String.valueOf(mGrades.get(position).getGrade()));
                gradeDateEditText.setText(mGrades.get(position).getDate());

                editGradeTButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            // We in Edit Mode
                            // Enable editing of the fields
                            editGradeTButton.setText(getString(R.string.save));
                            gradeNameEditText.setFocusableInTouchMode(true);
                            gradeEditText.setFocusableInTouchMode(true);
                            gradeDateEditText.setFocusableInTouchMode(true);
                            gradeFactorEditText.setFocusableInTouchMode(true);
                        } else {
                            // We not in Edit Mode
                            editGradeTButton.setText(getString(R.string.edit));

                            gradeNameEditText.setFocusable(false);
                            gradeEditText.setFocusable(false);
                            gradeDateEditText.setFocusable(false);
                            gradeFactorEditText.setFocusable(false);

                            // Here we have to save the changes, update the adapter
                            // We have to disable editing of the fields
                            // Have to make sure that the data entered is valid
                            if (validateInput()) {
                                Toast.makeText(SubjectDetailActivity.this, getString(R.string.input_saved), Toast.LENGTH_SHORT).show();
                                // Input is valid. Must save!
                                new AsyncTask<Void, Void, Void>() {

                                    private Grade insertGrade;

                                    @Override
                                    public void onPreExecute() {
                                        insertGrade = new Grade(
                                                mSubject.getName(),
                                                gradeNameEditText.getText().toString(),
                                                Double.parseDouble(gradeEditText.getText().toString()),
                                                Integer.parseInt(gradeFactorEditText.getText().toString()),
                                                gradeDateEditText.getText().toString());
                                    }

                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        DatabaseHelper dbHelperGrades = new DatabaseHelper(SubjectDetailActivity.this);
                                        dbHelperGrades.updateData(
                                                String.valueOf(mIds.get(position)),
                                                mSubject.getName(),
                                                insertGrade.getName(),
                                                String.valueOf(insertGrade.getGrade()),
                                                String.valueOf(insertGrade.getFactor()),
                                                insertGrade.getDate());

                                        dbHelperGrades.close();
                                        return null;
                                    }

                                    @Override
                                    public void onPostExecute(Void resutl) {
                                        // Okay, we update the adapter to reflect the changes.
                                        mGrades.set(position, insertGrade);
                                        mAdapter.notifyItemChanged(position);

                                        UpdateHeaderTask updateHeaderTask = new UpdateHeaderTask();
                                        updateHeaderTask.execute();
                                    }

                                }.execute();


                            } else {
                                Toast.makeText(SubjectDetailActivity.this, getString(R.string.input_error), Toast.LENGTH_SHORT).show();
                            }


                        }
                    }

                    private boolean validateInput() {

                        if (gradeNameEditText.getText().toString().isEmpty() || gradeEditText.getText().toString() == null || gradeNameEditText.getText().toString().equals(""))
                            return false;
                        if (gradeDateEditText.getText().toString().isEmpty() || gradeDateEditText.getText().toString() == null || gradeDateEditText.getText().toString().equals(""))
                            return false;
                        if (gradeFactorEditText.getText().toString().isEmpty() || gradeFactorEditText.getText().toString() == null || gradeFactorEditText.getText().toString().equals(""))
                            return false;
                        if (gradeEditText.getText().toString().isEmpty() || gradeEditText.getText().toString() == null || gradeEditText.getText().toString().equals(""))
                            return false;

                        if (Double.valueOf(gradeEditText.getText().toString()) <= 0.0)
                            return false;
                        if (Integer.valueOf(gradeFactorEditText.getText().toString()) <= 0)
                            return false;

                        return true;
                    }
                });

                builder.setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();

                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.show();

                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            // This is what we called the register grade with
            if (resultCode == Activity.RESULT_OK) {
                // The user has a successful result, we can insert the data into the database
                // and then update our recyclerview and header
                Grade grade = new Grade(
                        mSubject.getName(),
                        data.getStringExtra("gradename"),
                        Double.parseDouble(data.getStringExtra("grade")),
                        Integer.parseInt(data.getStringExtra("gradefactor")),
                        data.getStringExtra("gradedate"));

                ParseObject gradeob = new ParseObject("GRADE");
                gradeob.put("SUBJECT", grade.getSubject());
                gradeob.put("NAME", grade.getName());
                gradeob.put("GRADE", grade.getGrade());
                gradeob.put("FACTOR", grade.getFactor());
                gradeob.put("DATE", grade.getDate());
                gradeob.saveInBackground();

                UploadGradeTask ugt = new UploadGradeTask();
                ugt.execute(grade);


            } else {
                // The user canceled
            }
        }
    }


    private class UpdateHeaderTask extends AsyncTask<Void, Void, Void> {

        private String calcAverage;
        private String calcInsufficient;

        @Override
        protected Void doInBackground(Void... params) {
            // We gotta get calculate the average and insufficent mark of the subject.
            Subject subject = new Subject(mSubject.getName(), mSubject.getFactor(), false, SubjectDetailActivity.this);

            calcAverage = String.valueOf(subject.getAverage());
            calcInsufficient = String.valueOf(subject.getInsufficient());

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (Double.parseDouble(calcInsufficient) != -1.0) {
                mSubjectInsufficient.setText(String.format("%.2f", Double.parseDouble(calcInsufficient)));
            } else {
                mSubjectInsufficient.setText("0");
            }

            if (Double.parseDouble(calcAverage) == Subject.NO_AVERAGE) {
                mSubjectAverage.setText(R.string.subject_no_grades_yet);
            } else {
                mSubjectAverage.setText(String.format("%.2f", Double.parseDouble(calcAverage)));
            }
        }
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
                    mIds.add(Integer.parseInt(cursor.getString(0)));
                    Log.i(TAG, "ID ADDED" + Integer.parseInt(cursor.getString(0)));
                }
            }

            dbHelperGrades.close();
            cursor.close();
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

    private class UploadGradeTask extends AsyncTask<Grade, Void, Void> {

        private Grade uploadGrade;

        @Override
        protected Void doInBackground(Grade... params) {
            uploadGrade = params[0];
            DatabaseHelper dbHelperGrades = new DatabaseHelper(SubjectDetailActivity.this);
            dbHelperGrades.insertData(
                    mSubject.getName(),
                    params[0].getName(),
                    String.valueOf(params[0].getGrade()),
                    String.valueOf(params[0].getFactor()),
                    params[0].getDate());
            Cursor c = dbHelperGrades.getAllData();
            c.moveToLast();
            String id = c.getString(0);
            mIds.add(Integer.parseInt(id));

            dbHelperGrades.close();
            c.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Here we update the header to reflect the changes.

            // And we update our recyclerview
            mGrades.add(uploadGrade);
            mAdapter.notifyItemInserted(mGrades.size() - 1);

            UpdateHeaderTask uht = new UpdateHeaderTask();
            uht.execute();
        }
    }


    private class DeleteGradeTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            DatabaseHelper dbHelperGrades = new DatabaseHelper(SubjectDetailActivity.this);

            final int removePosition = params[0];
            // So what we have to do is delete the Grade from the Subject.
            dbHelperGrades.deleteData(String.valueOf(mIds.get(params[0])));
            // Then delete it from the adapter and delete the ID too.
            mIds.remove(removePosition);
            mGrades.remove(removePosition);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyItemRemoved(removePosition);
                    mAdapter.notifyItemRangeChanged(removePosition, mGrades.size());
                }
            });

            dbHelperGrades.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            UpdateHeaderTask uht = new UpdateHeaderTask();
            uht.execute();
        }

    }


}
