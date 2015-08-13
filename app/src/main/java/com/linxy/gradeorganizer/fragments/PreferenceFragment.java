package com.linxy.gradeorganizer.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.linxy.gradeorganizer.fragments.SubjectsFragment;
import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.StartupActivity;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
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
import com.parse.ParseObject;

import org.w3c.dom.Text;

/**
 * Created by linxy on 7/26/15.
 */
public class PreferenceFragment extends Fragment implements View.OnClickListener{

    Spinner spinnerRoundTo;
    String arraySpinner[] = {"Nicht Runden", "Halbe Note" };

    // Add Subject Components

    Switch swtShowInsufficient;

    Button deleteAllGrades;

    ScrollView scrollView;


    // Edit Subjects

    private boolean showinsufficient;
    private boolean showTwoDigit;
    private int roundSubjectGradesPosition;
    private String deviceId;

    DatabaseHelperSubjects dbs;
    DatabaseHelper db;


    Switch swtTwoDigit;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_preference, container, false);

        dbs = new DatabaseHelperSubjects(getActivity());
        deviceId = Settings.Secure.getString(getActivity().getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Add Subject Init Components

        db = new DatabaseHelper(getActivity());
        swtTwoDigit = (Switch) v.findViewById(R.id.switch_doubledigitgrade);
        deleteAllGrades = (Button) v.findViewById(R.id.new_semester);
        swtShowInsufficient = (Switch) v.findViewById(R.id.show_unsufficient);

        spinnerRoundTo = (Spinner) v.findViewById(R.id.round_to_grade);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), R.layout.centered_spinner, arraySpinner);
        adapter.setDropDownViewResource(R.layout.centered_spinner);
        spinnerRoundTo.setAdapter(adapter);




        final SharedPreferences pref = getActivity().getSharedPreferences(StartupActivity.PREFS, 0);
        final SharedPreferences.Editor editor = pref.edit();

        roundSubjectGradesPosition = pref.getInt("roundGrade", 0);
        spinnerRoundTo.setSelection(roundSubjectGradesPosition);
        deleteAllGrades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final AlertDialog dialog;
                final TextView textView = new TextView(getActivity());
                textView.setPadding(30, 30, textView.getPaddingRight(), textView.getPaddingBottom());
                textView.setTextSize(20f);
                textView.setText(getResources().getString(R.string.deleteSure));
                builder.setView(textView);
                builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteAll();
                        dialog.cancel();
                    }
                });

                builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                dialog = builder.create();
                dialog.show();

            }
        });

        spinnerRoundTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    editor.putInt("roundGrade", position);
                    editor.commit();
                } else {
                    editor.putInt("roundGrade", position);
                    editor.commit();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

        showTwoDigit = pref.getBoolean("twodigit", false);
        swtTwoDigit.setChecked(showTwoDigit);
        swtTwoDigit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showTwoDigit = isChecked;
                editor.putBoolean("twodigit", isChecked);
                editor.commit();
            }
        });


        AdView mAdView = (AdView) v.findViewById(R.id.AdView);
        if(StartupActivity.PREMIUM){
            mAdView.setVisibility(View.GONE);

        } else {
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("B2CAF611A47219282C0590A0804E1BEF").build();
            mAdView.loadAd(adRequest);
        }

        return  v;
    }

    @Override /* TODO Create String References in strings.xml for these Strings! */
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_subjects:
                Intent intent = new Intent((StartupActivity)getActivity(), SubjectsFragment.class);
                startActivity(intent);
                break;
        }
    }


}
