package com.linxy.gradeorganizer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Layout;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class NewGradeActivity extends ActionBarActivity   {

    // Organize

    DatabaseHelper myDb;
    DatabaseHelperSubjects mySDb;

    EditText inGradeName;
    Spinner inSubjectName;
    EditText inGrade;
    EditText inFactor;

    Button createGrade;


    Calendar cal = Calendar.getInstance();

    String outDate;
    int pos = 0;

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
        mySDb = new DatabaseHelperSubjects(this);

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
                  //  finish();
                  //  onBackPressed();
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
        // Populate the Spinner
        String items[];
        Cursor cursor = mySDb.getAllData();
        if(cursor.getCount() > 0) {
            items = new String[cursor.getCount()];
            int i = 0;
            while (cursor.moveToNext()) {
                items[i] = cursor.getString(1);
                i++;
            }
        } else {
            items = new String[0];
        }
        cursor.close();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, items);
        inSubjectName.setAdapter(arrayAdapter);
        showGradePicker();
    }

    public void showGradePicker()  {
        btnAddGrade = (Button) findViewById(R.id.set_grade);
        btnAddGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LayoutInflater layoutInflater = LayoutInflater.from(v.getContext());
                View dialogView = layoutInflater.inflate(R.layout.my_grade_picker, null);

                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                final AlertDialog dialog;

                builder.setView(dialogView);

                final Button buttons[] = new Button[10];

                  buttons[0] = (Button) dialogView.findViewById(R.id.mgp_0);
                  buttons[1] = (Button) dialogView.findViewById(R.id.mgp_1);
                  buttons[2] = (Button) dialogView.findViewById(R.id.mgp_2);
                  buttons[3] = (Button) dialogView.findViewById(R.id.mgp_3);
                  buttons[4] = (Button) dialogView.findViewById(R.id.mgp_4);
                  buttons[5] = (Button) dialogView.findViewById(R.id.mgp_5);
                  buttons[6] = (Button) dialogView.findViewById(R.id.mgp_6);
                  buttons[7] = (Button) dialogView.findViewById(R.id.mgp_7);
                  buttons[8] = (Button) dialogView.findViewById(R.id.mgp_8);
                  buttons[9] = (Button) dialogView.findViewById(R.id.mgp_9);


                final View touchView = dialogView.findViewById(R.id.mgp_relview);
                touchView.setClickable(true);

                for(int i = 0; i < buttons.length; i++){
                    buttons[i].setClickable(false);
                }

                final TextView tvBeforeDecimal = (TextView) dialogView.findViewById(R.id.mgp_textview_beforedecimal);
                final TextView tvAfteDecimal = (TextView) dialogView.findViewById(R.id.mgp_textview_afterdecimal);
                final TextView tvAfterAfterDecimal = (TextView) dialogView.findViewById(R.id.mgp_textview_afterafterdecimal);
                final Vibrator vib = (Vibrator) NewGradeActivity.this.getSystemService(Context.VIBRATOR_SERVICE);

                tvBeforeDecimal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pos = 0;
                        tvBeforeDecimal.setTextColor(getResources().getColor(R.color.WhiteColor));
                        tvAfteDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                        tvAfterAfterDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));

                    }
                });

                tvAfteDecimal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pos = 1;
                        tvBeforeDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                        tvAfteDecimal.setTextColor(getResources().getColor(R.color.WhiteColor));
                        tvAfterAfterDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                    }
                });

                tvAfterAfterDecimal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pos = 2;
                        tvBeforeDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                        tvAfteDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                        tvAfterAfterDecimal.setTextColor(getResources().getColor(R.color.WhiteColor));
                    }
                });

                touchView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        int action = event.getAction();
                        if(action==MotionEvent.ACTION_UP){

                            switch (pos){

                                case 0:
                                    tvBeforeDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                                    tvAfteDecimal.setTextColor(getResources().getColor(R.color.WhiteColor));

                                    break;
                                case 1:
                                    tvAfteDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                                    tvAfterAfterDecimal.setTextColor(getResources().getColor(R.color.WhiteColor));
                                    break;
                                case 2:
                                    tvAfteDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                                    tvAfterAfterDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                                    break;
                            }

                            pos++;

                        }
                        if (action != MotionEvent.ACTION_DOWN
                                && action != MotionEvent.ACTION_MOVE
                                && action != MotionEvent.ACTION_UP)
                            return false;





                        Rect hitRect = new Rect();
                        Button button;
                        int s = -1;

                        for (int i = 0; i < buttons.length; i++) {

                            button = buttons[i];
                                button.getBackground().clearColorFilter();


                            button.getHitRect(hitRect);
                            if (hitRect.contains((int) event.getX(), (int) event.getY())) {

                                switch (pos){
                                    case 0:
                                        tvBeforeDecimal.setText(button.getText().toString());
                                        button.getBackground().setColorFilter(getResources().getColor(R.color.WhiteCyan), PorterDuff.Mode.SRC_ATOP);
                                        vib.vibrate(10);
                                        break;
                                    case 1:
                                        tvAfteDecimal.setText(button.getText().toString());
                                        button.getBackground().setColorFilter(getResources().getColor(R.color.WhiteCyan), PorterDuff.Mode.SRC_ATOP);
                                        vib.vibrate(10);
                                        break;
                                    case 2:
                                        tvAfterAfterDecimal.setText(button.getText().toString());
                                        button.getBackground().setColorFilter(getResources().getColor(R.color.WhiteCyan), PorterDuff.Mode.SRC_ATOP);
                                        vib.vibrate(10);
                                        break;
                                }
                            }
                        }
                        return true;
                    }
                });





                dialog = builder.create();
                dialog.show();

















            }
        });
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
    public void onStop(){
        super.onStop();
        myDb.close();
        mySDb.close();
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if(id == DIALOG_ID){
                int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                int month = Calendar.getInstance().get(Calendar.MONTH);
                int year = Calendar.getInstance().get(Calendar.YEAR);

           DatePickerDialog dpicker = new DatePickerDialog(this, dpickerListner, year_x, month_x, day_x);
            dpicker.updateDate(year,month,day);
            return dpicker;
        }


        return null;


    }

    private boolean fillDatabase(){
        double r = 0;
        double s = 0;
        double t = 0;

        boolean b;

        if(inGradeName.getText().toString() == null || inGradeName.toString().isEmpty()) return false;
        if(inSubjectName.getSelectedItem().toString() == null || inSubjectName.getSelectedItem().toString().isEmpty()) return false;
        if(inGrade.getText().toString() == null || inGrade.getText().toString().isEmpty()) return false;
        if(inFactor.getText().toString() == null || inFactor.getText().toString().isEmpty()) return false;
        if(outDate == null || outDate.isEmpty()) return false;


//        t = Double.parseDouble(inGrade.getText().toString());
//        r = 2 * t;
//        s = Math.round(r);
//        outRounded = s / 2;

        b = myDb.insertData(
                inSubjectName.getSelectedItem().toString(),
                inGradeName.getText().toString(),
                inGrade.getText().toString(),
                inFactor.getText().toString(),
                outDate);
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