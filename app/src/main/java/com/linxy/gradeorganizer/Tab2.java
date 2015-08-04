package com.linxy.gradeorganizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.linxy.gradeorganizer.com.linxy.adapters.HRVAdapter;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;
import com.linxy.gradeorganizer.tabs.HidingScrollListener;
import com.linxy.gradeorganizer.tabs.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linxy on 7/26/15.
 */

// This is the history of grades.

public class Tab2 extends Fragment implements HRVAdapter.MyHisClickListener {

    ListView lvRecentGrades;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    DatabaseHelper db;
    DatabaseHelperSubjects dbs;

    private List<Grade> grades;
    private boolean inEdit = false;
    static boolean scroll_down;

    SearchView searchView;


    HRVAdapter adapter;

    int selGradeIds[];
    String selGradeSubName[];
    String selGradeName[];
    double selGrade[];
    int selGradeFactor[];
    String selGradeDate[];
    TextView noGrades;


    public class Grade {

        public String gradeId;
        public String gradeSubject;
        public String gradeName;
        public String grade;
        public String gradeFactor;
        public String gradeDate;

        Grade(String gradeId, String gradeSubject, String gradeName, String grade, String gradeFactor, String gradeDate) {
            this.gradeId = gradeId;
            this.gradeSubject = gradeSubject;
            this.gradeName = gradeName;
            this.grade = grade;
            this.gradeFactor = gradeFactor;
            this.gradeDate = gradeDate;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tab_2, container, false);
        db = new DatabaseHelper(getActivity().getBaseContext());
        dbs = new DatabaseHelperSubjects(getActivity().getBaseContext());

        //lvRecentGrades = (ListView) v.findViewById(R.id.list_view_recent_grades);
        Cursor c = db.getAllData();
        int size = c.getCount();
        selGradeIds = new int[size];
        selGradeSubName = new String[size];
        selGradeName = new String[size];
        selGrade = new double[size];
        selGradeFactor = new int[size];
        selGradeDate = new String[size];

        c.close();

        recyclerView = (RecyclerView) v.findViewById(R.id.gh_recycler_view);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(llm);
        noGrades = (TextView) v.findViewById(R.id.no_grade_text);
        fillListView(v);
        return v;
    }

    @Override
    public void onItemClick(final int position, final View v) {
        LayoutInflater li = LayoutInflater.from(v.getContext());
        View dialogView = li.inflate(R.layout.gradeinfo_dialog, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        final AlertDialog dialog;

        builder.setView(dialogView);
        builder.setTitle(R.string.info);


        final TextView subjectName = (TextView) dialogView.findViewById(R.id.dialog_tv_subject);
        final EditText examName = (EditText) dialogView.findViewById(R.id.dialog_et_gradename);
        final EditText grade = (EditText) dialogView.findViewById(R.id.dialog_et_grade);
        final EditText factor = (EditText) dialogView.findViewById(R.id.dialog_et_factor);
        final EditText date = (EditText) dialogView.findViewById(R.id.dialog_et_date);
        final ToggleButton edit = (ToggleButton) dialogView.findViewById(R.id.dialog_btn_edit);


        subjectName.setText(selGradeSubName[position]);
        examName.setText(selGradeName[position]);
        grade.setText(String.valueOf(selGrade[position]));
        factor.setText(String.valueOf(selGradeFactor[position]));
        date.setText(selGradeDate[position]);

        examName.setFocusable(false);
        grade.setFocusable(false);
        factor.setFocusable(false);
        date.setFocusable(false);
        edit.setChecked(false);

        edit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    // Clicking edit
                    examName.setFocusableInTouchMode(true);
                    grade.setFocusableInTouchMode(true);
                    factor.setFocusableInTouchMode(true);
                    date.setFocusableInTouchMode(true);


                } else {
                    // Clicking save
                    db.updateData(String.valueOf(
                                    selGradeIds[position]),
                            selGradeSubName[position],
                            examName.getText().toString(),
                            grade.getText().toString(),
                            factor.getText().toString(),
                            date.getText().toString()
                    );


                    examName.setFocusable(false);
                    grade.setFocusable(false);
                    factor.setFocusable(false);
                    date.setFocusable(false);
                    edit.setChecked(false);


                }


            }
        });

        builder.setCancelable(true);
        builder.setPositiveButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                fillListView(v);


            }
        });

        builder.setNegativeButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteData(String.valueOf(selGradeIds[position]));
                dialog.cancel();
                fillListView(v);

            }
        });


        dialog = builder.create();

        dialog.show();

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        // final TextView tvSubject = new TextView("Fach: ");
    }

    @Override
    public void onResume() {
        super.onResume();

         fillListView(getView());
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);

        if (getActivity() != null) {
          fillListView(getView());
        }


    }


    private void fillListView(View v) {


        Cursor cdb = db.getAllData();
        grades = new ArrayList<>();

        int size = cdb.getCount();


        int i = 0;
        while (cdb.moveToNext()) {
            grades.add(new Grade(cdb.getString(0), cdb.getString(1), cdb.getString(2), cdb.getString(3), cdb.getString(4), cdb.getString(5)));
            selGradeIds[i] = Integer.parseInt(cdb.getString(0));
            selGradeSubName[i] = cdb.getString(1);
            selGradeName[i] = cdb.getString(2);
            selGrade[i] = Double.parseDouble(cdb.getString(3));
            selGradeFactor[i] = Integer.parseInt(cdb.getString(4));
            selGradeDate[i] = cdb.getString(5);

            i++;
        }

        adapter = new HRVAdapter(grades);
        adapter.setOnItemClickListener(this);


        if(adapter.getItemCount() == 0){
            noGrades.setVisibility(View.VISIBLE);
        } else {
            noGrades.setVisibility(View.GONE);
            recyclerView.setAdapter(adapter);
        }


        cdb.close();
    }


    @Override
    public void onStop() {
        super.onStop();
        db.close();
        dbs.close();
    }


}
