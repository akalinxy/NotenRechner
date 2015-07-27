package com.linxy.gradeorganizer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class NewGradeActivity extends ActionBarActivity  {

    // Organize

    DatabaseHelper myDb;

    EditText inGradeName;
    Spinner inSubjectName;
    EditText inGrade;
    EditText inFactor;

    Calendar cal = Calendar.getInstance();

    double outRounded;
    String outDate;

    Button btnAddGrade;

    // Handle Date
    Button btnPickDate;
    TextView tvCurrentDate;
    int year_x, month_x,day_x;
    static final int DIALOG_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_grade);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        myDb = new DatabaseHelper(this);


        inGradeName = (EditText) findViewById(R.id.newgrade_name);
        inSubjectName = (Spinner) findViewById(R.id.subject_names);
        inGrade = (EditText) findViewById(R.id.grade_achieved);
        inFactor = (EditText) findViewById(R.id.factor);
        btnAddGrade = (Button) findViewById(R.id.button_save);
        tvCurrentDate = (TextView) findViewById(R.id.current_date_display);




        btnAddGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check that all fields are filled, if they all are, then input the data into the database and close the activity.
                boolean isInserted = fillDatabase();
                if (isInserted) {
                    Toast.makeText(NewGradeActivity.this, "Data Inserted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NewGradeActivity.this, StartupActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(NewGradeActivity.this, "Fill All Fields!", Toast.LENGTH_SHORT).show();

                }

            }
        });

        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        String formattedDate = df.format(cal.getTime());

        outDate = formattedDate;
        tvCurrentDate.setText(outDate);

        showDialogOnButtonClick();
    }

    public void showDialogOnButtonClick(){
        btnPickDate = (Button) findViewById(R.id.choose_date);

        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
            }
        });
    }



    @Override
    protected Dialog onCreateDialog(int id){
        if(id == DIALOG_ID){
            return new DatePickerDialog(this, dpickerListner, year_x, month_x, day_x);
        } return null;
    }

    private boolean fillDatabase(){
        double r = 0;
        double s = 0;
        double t = 0;

        boolean b;

        if(inGradeName.getText().toString() == null || inGradeName.toString().toString().isEmpty()) return false;
        if(inSubjectName.getSelectedItem().toString() == null || inSubjectName.getSelectedItem().toString().isEmpty()) return false;
        if(inGrade.getText().toString() == null || inGrade.getText().toString().isEmpty()) return false;
        if(inFactor.getText().toString() == null || inFactor.getText().toString().isEmpty()) return false;
        if(outDate == null || outDate.isEmpty()) return false;


        t = Double.parseDouble(inGrade.getText().toString());
        r = 2 * t;
        s = Math.round(r);
        outRounded = s / 2;

        b = myDb.insertData(inSubjectName.getSelectedItem().toString(),inGradeName.getText().toString(),inGrade.getText().toString(),
                String.valueOf(outRounded),inFactor.getText().toString(),outDate);
        return  b;
    }

    private DatePickerDialog.OnDateSetListener dpickerListner =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    year_x = year;
                    month_x = monthOfYear;
                    day_x = dayOfMonth;
                    tvCurrentDate.setText(year_x + " / " + month_x + " / " + day_x);
                    outDate = year_x + "." + month_x + "." + day_x;
                }
            };



}
