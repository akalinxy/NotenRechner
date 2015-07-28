package com.linxy.gradeorganizer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by linxy on 7/26/15.
 */
public class Tab3 extends Fragment implements View.OnClickListener{

    Spinner spinnerRoundTo;
    String arraySpinner[] = {"Nicht Runden", "Halbe Note", "Ganze Note" };

    // Add Subject Components
    EditText etNewSubjectName;
    EditText etNewSubjectFactor;
    Button btnAddNewSubject;

    // Edit Subjects
    Button btnEditSubjects;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.tab_3, container, false);

        // Add Subject Init Components
        etNewSubjectName = (EditText) v.findViewById(R.id.add_new_grade_name);
        etNewSubjectFactor = (EditText) v.findViewById(R.id.factor_new_grade);
        btnAddNewSubject = (Button) v.findViewById(R.id.add_new_subject);
        btnEditSubjects = (Button) v.findViewById(R.id.edit_subjects);

        btnEditSubjects.setOnClickListener(this);
        btnAddNewSubject.setOnClickListener(this);



        spinnerRoundTo = (Spinner) v.findViewById(R.id.round_to_grade);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), R.layout.centered_spinner, arraySpinner);
        adapter.setDropDownViewResource(R.layout.centered_spinner);
        spinnerRoundTo.setAdapter(adapter);



        return  v;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.add_new_subject:
                if(testFieldsNewSubject()) {
                    ((StartupActivity) getActivity()).AddSubjectToDatabase(etNewSubjectName.getText().toString(), Integer.parseInt(etNewSubjectFactor.getText().toString()));
                    Toast.makeText((StartupActivity)getActivity(), "Added Subject to DB", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText((StartupActivity)getActivity(), "Fill in ALL fields!", Toast.LENGTH_SHORT).show();

                }

                break;
            case R.id.edit_subjects:
                Intent intent = new Intent((StartupActivity)getActivity(), EditSubjectsActivity.class);
                startActivity(intent);
                break;
        }

    }

    private boolean testFieldsNewSubject(){
        if(etNewSubjectName.getText().toString().equals("") | etNewSubjectName.getText().toString() == null) return false;
        if(etNewSubjectFactor.getText().toString().equals("") | etNewSubjectFactor.getText().toString() == null) return false;


        return true;

    }
}
