package com.linxy.gradeorganizer;

import android.content.Context;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;

import org.w3c.dom.Text;

/**
 * Created by linxy on 7/26/15.
 */
public class Tab3 extends Fragment implements View.OnClickListener{

    Spinner spinnerRoundTo;
    String arraySpinner[] = {"Nicht Runden", "Halbe Note" };

    // Add Subject Components
    EditText etNewSubjectName;
    EditText etNewSubjectFactor;
    Button btnAddNewSubject;
    Switch swtShowInsufficient;

    ScrollView scrollView;

    // Edit Subjects
    Button btnEditSubjects;

    private boolean showinsufficient;

    DatabaseHelperSubjects dbs;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.tab_3, container, false);

        dbs = new DatabaseHelperSubjects(getActivity());


        // Add Subject Init Components
        etNewSubjectName = (EditText) v.findViewById(R.id.add_new_grade_name);
        etNewSubjectFactor = (EditText) v.findViewById(R.id.factor_new_grade);
        btnAddNewSubject = (Button) v.findViewById(R.id.add_new_subject);
        btnEditSubjects = (Button) v.findViewById(R.id.edit_subjects);

        btnEditSubjects.setOnClickListener(this);
        btnAddNewSubject.setOnClickListener(this);

        swtShowInsufficient = (Switch) v.findViewById(R.id.show_unsufficient);

        spinnerRoundTo = (Spinner) v.findViewById(R.id.round_to_grade);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), R.layout.centered_spinner, arraySpinner);
        adapter.setDropDownViewResource(R.layout.centered_spinner);
        spinnerRoundTo.setAdapter(adapter);

        final SharedPreferences pref = getActivity().getSharedPreferences(StartupActivity.PREFS, 0);
        final SharedPreferences.Editor editor = pref.edit();




        showinsufficient = pref.getBoolean("insufficient", true);
        swtShowInsufficient.setChecked(showinsufficient);
        swtShowInsufficient.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showinsufficient = isChecked;
                editor.putBoolean("insufficient", showinsufficient);
                editor.commit();
            }
        });




        return  v;
    }

    @Override /* TODO Create String References in strings.xml for these Strings! */
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_new_subject:
                if(testFieldsNewSubject()) { /* Subject is not blank */
                    if(dbs.hasObject(etNewSubjectName.getText().toString())){ /* SubjectName already exists in Database */
                        Toast.makeText((StartupActivity)getActivity(), "Subject Exists!", Toast.LENGTH_SHORT).show();
                    } else { /* SubjectName is unique. */
                        dbs.insertData(etNewSubjectName.getText().toString(), etNewSubjectFactor.getText().toString());
                        Toast.makeText((StartupActivity)getActivity(), "Subject inserted @ " + dbs.getDatabaseName(), Toast.LENGTH_SHORT).show();
                        dbs.close();
                    }
                } else {
                    Toast.makeText((StartupActivity)getActivity(), "Fill all Fields!", Toast.LENGTH_SHORT).show();
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
