package com.linxy.gradeorganizer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.linxy.gradeorganizer.database_helpers.DatabaseHelperCalendar;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;
import com.linxy.gradeorganizer.fragments.CalendarFragment;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateChangedListener;

import java.util.ArrayList;
import java.util.Calendar;

public class RegisterExamActivity extends ActionBarActivity implements  OnDateChangedListener {

    Toolbar mToolbar;

    /* Private Instance Variables */
    private boolean calendarSet = false;
    private boolean factorSet = false;
    private boolean testSet = false;
    Snackbar snackbar;


    /* Components */
    Spinner spnrSubjectList;
    EditText etTestName;
    EditText etExamFactor;
    MaterialCalendarView mcvCalendar;

    /* Database Helpers */
    DatabaseHelperSubjects dbs;
    DatabaseHelperCalendar dbc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_exam);

        dbs = new DatabaseHelperSubjects(this);
        dbc = new DatabaseHelperCalendar(this);

        spnrSubjectList = (Spinner) findViewById(R.id.spinner_subjects);
        etTestName = (EditText) findViewById(R.id.edit_text_test_name);
        etExamFactor = (EditText) findViewById(R.id.et_testfactor);
        mcvCalendar = (MaterialCalendarView) findViewById(R.id.calendarView);

        etTestName.addTextChangedListener(twTest);
        etExamFactor.addTextChangedListener(twFactor);

        Calendar cal = Calendar.getInstance();

        //

        mcvCalendar.setMinimumDate(cal);
        mcvCalendar.setOnDateChangedListener(this);


        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.registerExam));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }

        ArrayList<String> arr = new ArrayList<>();
        Cursor c = dbs.getAllData();
        int i = 0;
        while(c.moveToNext()){
            arr.add(i++,c.getString(1));
        }
        c.close();
        dbs.close();
        ArrayAdapter<String> spnrAdapter = new ArrayAdapter<String>(this, R.layout.centered_spinner, arr);
        spnrSubjectList.setAdapter(spnrAdapter);

        snackbar = Snackbar.make(findViewById(android.R.id.content), "Had a Snackbar", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Submit Exam", clickListener);
        snackbar.setActionTextColor(getResources().getColor(R.color.FlatOrange));


    }

    private void createSnackbar(){
        snackbar = Snackbar.make(findViewById(android.R.id.content), "Had a Snackbar", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Submit Exam", clickListener);
        snackbar.setActionTextColor(getResources().getColor(R.color.FlatOrange));
    }

    private TextWatcher twTest = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if(s.length() == 0){
                testSet = false;
                snackbar.dismiss();
            } else {
                testSet = true;
            }

            if(calendarSet && factorSet && testSet){
                createSnackbar();
                snackbar.show();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher twFactor = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if(s.length() == 0){
                factorSet = false;
                snackbar.dismiss();
            } else {
                factorSet = true;
            }

            if(calendarSet && testSet && factorSet){
                createSnackbar();
                snackbar.show();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_exam, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDateChanged(MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {
        int cDay = Calendar.DAY_OF_MONTH;
        int cMonth = Calendar.MONTH;
        int cYear = Calendar.YEAR;
        calendarSet = true;
        if(testSet && factorSet){
            createSnackbar();
            snackbar.show();
        }


    }

    final View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {

                //Toast.makeText(RegisterExamActivity.this, "Clicky", Toast.LENGTH_SHORT).show();
                String subjectname = spnrSubjectList.getSelectedItem().toString();
                String examname = etTestName.getText().toString();
                String examfactor = etExamFactor.getText().toString();

                CalendarDay calDay = mcvCalendar.getSelectedDate();
                int day = calDay.getDay();
                int month = calDay.getMonth() + 1;
                int year = calDay.getYear();
                String examdate = day + "." + month + "." + year;

                Intent returnIntent = new Intent();
                returnIntent.putExtra("subjectname", subjectname);
                returnIntent.putExtra("examname", examname);
                returnIntent.putExtra("examfactor", examfactor);
                returnIntent.putExtra("examdate", examdate);
                setResult(RESULT_OK, returnIntent);
                finish();

        }
    };


    @Override
    public void onPause(){
        super.onPause();
        dbc.close();
        dbs.close();
    }



}
