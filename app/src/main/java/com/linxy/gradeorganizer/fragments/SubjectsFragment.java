package com.linxy.gradeorganizer.fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.linxy.gradeorganizer.R;


/**
 * A simple {@link Fragment} subclass.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import com.linxy.gradeorganizer.activities.StartupActivity;
import com.linxy.gradeorganizer.activities.SubjectDetailActivity;
import com.linxy.gradeorganizer.adapters.RVAdapter;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;
import com.linxy.gradeorganizer.objects.Subject;
import com.linxy.gradeorganizer.utility.MyRecyclerScroll;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;


public class SubjectsFragment extends Fragment {

    public static final String GRADE_OBJECT_TAG = "gradeobj";
    public static final String TAG = SubjectsFragment.class.getSimpleName();
    RecyclerView mRecyclerView;
    private FloatingActionButton mNewSubject;
    private String deviceId;
    private DatabaseHelperSubjects mDatabaseHelperSubjects;
    RVAdapter mAdapter;

    private ArrayList<Subject> mSubjects;

    public static SubjectsFragment getInstance() {
        return new SubjectsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_subjects, container, false);
        deviceId = Settings.Secure.getString(getActivity().getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        mNewSubject = (FloatingActionButton) v.findViewById(R.id.fabaddsubject);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view2);
        mRecyclerView.setHasFixedSize(true);

        mSubjects = new ArrayList<>();

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        mAdapter = new RVAdapter(getActivity(), mSubjects);
        mRecyclerView.setAdapter(mAdapter);

        mDatabaseHelperSubjects = new DatabaseHelperSubjects(getActivity());

        mRecyclerView.setOnScrollListener(new MyRecyclerScroll() {
            @Override
            public void show() {
                mNewSubject.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();

            }

            @Override
            public void hide() {
                mNewSubject.animate().translationY(mNewSubject.getHeight() + 48).setInterpolator(new AccelerateInterpolator(2)).start();

            }
        });
        mNewSubject.setOnClickListener(FabClickListener);
        mAdapter.setOnItemClickListener(mClickListener);

        LoadSubjectsTask lst = new LoadSubjectsTask();
        lst.execute();

        return v;
    }

    final View.OnClickListener FabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            LayoutInflater layoutInflater = LayoutInflater.from(v.getContext());
            View dialogView = layoutInflater.inflate(R.layout.dialog_newsubject, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            AlertDialog dialog;
            builder.setView(dialogView);

            final EditText etSubjectName = (EditText) dialogView.findViewById(R.id.add_new_grade_name);
            final EditText etSubjectFactor = (EditText) dialogView.findViewById(R.id.factor_new_grade);



            builder.setTitle(R.string.dlg_new_subject); // TODO MAKE STRING REFERENCE
            builder.setPositiveButton(R.string.dlg_add_subject, new DialogInterface.OnClickListener() { // TODO MAKE STRING REFERENCE
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    /* First make sure that both fields have been filled in */
                    if (testFieldsNewSubject()) { /* Subject is not blank */
                        if (mDatabaseHelperSubjects.hasObject(etSubjectName.getText().toString())) { /* SubjectName already exists in Database */
                            Toast.makeText((StartupActivity) getActivity(), getResources().getString(R.string.subjectExists), Toast.LENGTH_SHORT).show();
                        } else { /* SubjectName is unique. */

                            ParseObject subjectObject = new ParseObject("Subjects");
                            subjectObject.put("deviceId", deviceId);
                            subjectObject.put("subjectname", etSubjectName.getText().toString());
                            subjectObject.put("subjectfactor", etSubjectFactor.getText().toString());
                            subjectObject.saveInBackground();

                            Subject subject = new Subject(etSubjectName.getText().toString(), Integer.valueOf(etSubjectFactor.getText().toString()), false, getActivity());

                            InsertSubjectTask insertSubjectTask = new InsertSubjectTask();
                            insertSubjectTask.execute(subject); // This inserts the subject into the local device database

                            mSubjects.add(subject); // This inserts it directly into the adapter
                            mAdapter.notifyItemInserted(mSubjects.size());

                        }
                    } else {
                        Toast.makeText((StartupActivity) getActivity(), getResources().getString(R.string.fillAllFields), Toast.LENGTH_SHORT).show();
                    }
                    mDatabaseHelperSubjects.close();
                }

                private boolean testFieldsNewSubject() {
                    if (etSubjectName.getText().toString().equals("") | etSubjectName.getText().toString() == null)
                        return false;
                    if (etSubjectFactor.getText().toString().equals("") | etSubjectFactor.getText().toString() == null)
                        return false;
                    return true;
                }

            });

            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // TODO MAKE STRING REFERENCE
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            dialog = builder.create();
            dialog.show();

        }


    };




    final RVAdapter.MyClickListener mClickListener = new RVAdapter.MyClickListener() {
        @Override
        public void onItemClick(int position, View v) {
            Log.i(TAG, "ONCLICK");
            // Launch the Detail Subject Activity
            Intent intent = new Intent(getActivity(), SubjectDetailActivity.class);
            intent.putExtra(GRADE_OBJECT_TAG, mSubjects.get(position));
            startActivity(intent);
        }
    };


    private class LoadSubjectsTask extends AsyncTask<Void, Void, ArrayList<Subject>> {

        @Override
        protected ArrayList<Subject> doInBackground(Void... params) {
            ArrayList<Subject> subjects = new ArrayList<>();

            DatabaseHelperSubjects dbHelperSubjects = new DatabaseHelperSubjects(getActivity());
            Cursor subjectCursor = dbHelperSubjects.getAllData();

            int i = 0;
            while (subjectCursor.moveToNext()) {
                subjects.add(i, new Subject(subjectCursor.getString(1), Integer.parseInt(subjectCursor.getString(2)), false, getActivity()));
                i++;
            }

            dbHelperSubjects.close();
            subjectCursor.close();

            return subjects;
        }

        @Override
        protected void onPostExecute(ArrayList<Subject> subjects) {
            // We have the list of subjects, now we need to update our adapter.
            mSubjects.clear();
            for (Subject subject : subjects) {
                mSubjects.add(subject);
            }
            mAdapter.notifyDataSetChanged();

        }
    }


    private class InsertSubjectTask extends AsyncTask<Subject, Void, Void> {
        @Override
        protected Void doInBackground(Subject... params) {
            DatabaseHelperSubjects dbHelperSubjects = new DatabaseHelperSubjects(getActivity());
            dbHelperSubjects.insertData(params[0].getName(), String.valueOf(params[0].getFactor()));
            dbHelperSubjects.close();
            return null;
        }
    }


}
