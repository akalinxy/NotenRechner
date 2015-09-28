package com.linxy.gradeorganizer.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Build;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;
import com.linxy.gradeorganizer.utility.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class NewGradeActivity extends ActionBarActivity {

    // Organize

    DatabaseHelper myDb;
    DatabaseHelperSubjects mySDb;

    EditText inGradeName;
    Spinner inSubjectName;
    EditText inFactor;
    Button btnCancel;

    String grade = "";


    Toolbar mToolbar;

    Calendar cal = Calendar.getInstance();
    private int lastIndex;

    String outDate;
    int pos;

    TextView inGrade;
    boolean canVib = true;
    Button btnAddGrade;
    Button btnSetGrade;
    private boolean showTwoDigit;

    private String mSubjectName = "";
    private CardView mSubjectSelectCardView;



    // Handle Date
    Button btnPickDate;
    TextView tvCurrentDate;
    int year_x, month_x, day_x;
    static final int DIALOG_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_grade);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mSubjectSelectCardView = (CardView) findViewById(R.id.newgrade_cardview_subjectselect);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if(intent.getStringExtra(Constants.NEWGRADE_SUBJECTSELECT).equals(Constants.ALL_SUBJECTS)){
            // In this case we want the user to have a selecton of which subject he wants a
            // grade added.
            mSubjectSelectCardView.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle(getResources().getString(R.string.registerExam));
        } else {
            mSubjectName = intent.getStringExtra(Constants.NEWGRADE_SUBJECTSELECT);
            getSupportActionBar().setTitle(mSubjectName + " " + getString(R.string.registerExam));
            mSubjectSelectCardView.setVisibility(View.GONE);
        }




        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.color_primary));
        }



        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        showTwoDigit  = sharedPreferences.getBoolean(getString(R.string.pref_key_twodigitgrades), false);

        myDb = new DatabaseHelper(this);
        btnCancel = (Button) findViewById(R.id.ang_cancel);
        mySDb = new DatabaseHelperSubjects(this);

        inGrade = (TextView) findViewById(R.id.ang_show_grade);

        inGradeName = (EditText) findViewById(R.id.newgrade_name);
        inSubjectName = (Spinner) findViewById(R.id.subject_names);
        inFactor = (EditText) findViewById(R.id.factor);
        btnAddGrade = (Button) findViewById(R.id.button_save);
        btnSetGrade = (Button) findViewById(R.id.set_grade);

        tvCurrentDate = (TextView) findViewById(R.id.current_date_display);

        btnAddGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check that all fields are filled, if they all are, then input the data into the database and close the activity.

                if (isValid()) {
                    Intent returnIntent = new Intent();
                    if(mSubjectSelectCardView.getVisibility() == View.VISIBLE) {
                        returnIntent.putExtra("subjectname", inSubjectName.getSelectedItem().toString());
                    } else {
                        returnIntent.putExtra("subjectname", mSubjectName);
                    }
                    returnIntent.putExtra("gradename",  inGradeName.getText().toString());
                    returnIntent.putExtra("grade", inGrade.getText().toString());
                    returnIntent.putExtra("gradefactor", inFactor.getText().toString());
                    returnIntent.putExtra("gradedate", outDate);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(NewGradeActivity.this, getResources().getString(R.string.fillAllFields), Toast.LENGTH_SHORT).show();
                }
            }
        });
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        String formattedDate = df.format(cal.getTime());
        outDate = formattedDate;
        tvCurrentDate.setText(outDate);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        showDialogOnButtonClick();
        // Populate the Spinner
        String items[];
        Cursor cursor = mySDb.getAllData();
        if (cursor.getCount() > 0) {
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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.centered_spinner, items);
        inSubjectName.setAdapter(arrayAdapter);
        showGradePicker();


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

    public void showGradePicker() {
        btnSetGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(showTwoDigit)
                    pos = 1;
                else
                    pos = 2;


                LayoutInflater layoutInflater = LayoutInflater.from(v.getContext());
                View dialogView = layoutInflater.inflate(R.layout.my_grade_picker, null);

                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                final AlertDialog dialog;

                builder.setView(dialogView);
                builder.setCancelable(false);

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

                for (int i = 0; i < buttons.length; i++) {
                    buttons[i].setClickable(false);
                }




                final TextView tvBeforeBeforeDecimal = (TextView) dialogView.findViewById(R.id.mgp_textview_beforebeforedecimal);



                final TextView tvBeforeDecimal = (TextView) dialogView.findViewById(R.id.mgp_textview_beforedecimal);
                final TextView tvAfteDecimal = (TextView) dialogView.findViewById(R.id.mgp_textview_afterdecimal);
                final TextView tvAfterAfterDecimal = (TextView) dialogView.findViewById(R.id.mgp_textview_afterafterdecimal);
                final Vibrator vib = (Vibrator) NewGradeActivity.this.getSystemService(Context.VIBRATOR_SERVICE);

                if(!showTwoDigit) {
                    tvBeforeBeforeDecimal.setVisibility(View.GONE);
                    tvBeforeDecimal.setTextColor(getResources().getColor(R.color.color_white));
                }
                else {
                    tvBeforeBeforeDecimal.setVisibility(View.VISIBLE);
                    tvBeforeBeforeDecimal.setTextColor(getResources().getColor(R.color.color_white));

                }

                tvBeforeBeforeDecimal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pos  = 1;
                        tvBeforeBeforeDecimal.setTextColor(getResources().getColor(R.color.color_white));
                        tvBeforeDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                        tvAfteDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                        tvAfterAfterDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));

                    }
                });

                tvBeforeDecimal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pos = 2;
                        tvBeforeBeforeDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                        tvBeforeDecimal.setTextColor(getResources().getColor(R.color.color_white));
                        tvAfteDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                        tvAfterAfterDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));

                    }
                });

                tvAfteDecimal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pos = 3;
                        tvBeforeBeforeDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                        tvBeforeDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                        tvAfteDecimal.setTextColor(getResources().getColor(R.color.color_white));
                        tvAfterAfterDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                    }
                });

                tvAfterAfterDecimal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pos = 4;
                        tvBeforeBeforeDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                        tvBeforeDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                        tvAfteDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                        tvAfterAfterDecimal.setTextColor(getResources().getColor(R.color.color_white));
                    }
                });

                builder.setCancelable(true);
                dialog = builder.create();

                touchView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int action = event.getAction();
                        if (action == MotionEvent.ACTION_UP) {
                            switch (pos) {

                                case 1:
                                    tvBeforeBeforeDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                                    tvBeforeDecimal.setTextColor(getResources().getColor(R.color.color_white));
                                    tvAfteDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                                    tvAfterAfterDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                                    break;

                                case 2:
                                    tvBeforeBeforeDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                                    tvBeforeDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                                    tvAfteDecimal.setTextColor(getResources().getColor(R.color.color_white));
                                    tvAfterAfterDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));

                                    break;
                                case 3:
                                    tvBeforeBeforeDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                                    tvBeforeDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                                    tvAfteDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                                    tvAfterAfterDecimal.setTextColor(getResources().getColor(R.color.color_white));
                                    break;
                                case 4:
                                    tvBeforeBeforeDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                                    tvBeforeDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                                    tvAfteDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));
                                    tvAfterAfterDecimal.setTextColor(getResources().getColor(R.color.WhiteCyan));

                                    if(showTwoDigit)
                                     grade = tvBeforeBeforeDecimal.getText().toString() + tvBeforeDecimal.getText().toString() + "." + tvAfteDecimal.getText().toString() + tvAfterAfterDecimal.getText().toString();
                                    else
                                     grade = tvBeforeDecimal.getText().toString() + "." + tvAfteDecimal.getText().toString() + tvAfterAfterDecimal.getText().toString();

                                    inGrade.setText(grade);
                                    double d = Double.parseDouble(inGrade.getText().toString());
                                    if(d<4)
                                        inGrade.setTextColor(getResources().getColor(R.color.color_red));
                                    else
                                        inGrade.setTextColor(getResources().getColor(R.color.color_green));
                                    dialog.cancel();


                                    break;
                            }

                            pos++;

                        }
                        if (action != MotionEvent.ACTION_DOWN
                                && action != MotionEvent.ACTION_MOVE
                                && action != MotionEvent.ACTION_UP){
                            return false;
                        }


                        Rect hitRect = new Rect();
                        Button button;
                       // canVib = true;
                        int s = -1;
                        for (int i = 0; i < buttons.length; i++) {

                            button = buttons[i];
                            button.getBackground().clearColorFilter();

                            button.getHitRect(hitRect);
                            if (hitRect.contains((int) event.getX(), (int) event.getY())) {
                                if(!(i == lastIndex)) canVib = true;
                                else canVib = false;
                                lastIndex = i;



                                switch (pos) {
                                    case 1:

                                        tvBeforeBeforeDecimal.setText(button.getText().toString());
                                        button.getBackground().setColorFilter(getResources().getColor(R.color.WhiteCyan), PorterDuff.Mode.SRC_ATOP);
                                        if(canVib) {
                                            vib.vibrate(50);
                                            canVib = false;
                                        }


                                        break;

                                    case 2:
                                        tvBeforeDecimal.setText(button.getText().toString());
                                        button.getBackground().setColorFilter(getResources().getColor(R.color.WhiteCyan), PorterDuff.Mode.SRC_ATOP);
                                        if(canVib) {
                                            vib.vibrate(50);
                                            canVib = false;
                                        }
                                        break;
                                    case 3:
                                        tvAfteDecimal.setText(button.getText().toString());
                                        button.getBackground().setColorFilter(getResources().getColor(R.color.WhiteCyan), PorterDuff.Mode.SRC_ATOP);
                                        if(canVib) {
                                            vib.vibrate(50);
                                            canVib = false;
                                        }
                                        break;
                                    case 4:
                                        tvAfterAfterDecimal.setText(button.getText().toString());
                                        button.getBackground().setColorFilter(getResources().getColor(R.color.WhiteCyan), PorterDuff.Mode.SRC_ATOP);
                                        if(canVib) {
                                            vib.vibrate(50);
                                            canVib = false;
                                        }
                                        break;
                                }



                            }
                        }
                        return true;
                    }
                });

                dialog.show();

            }
        });
    }

    public void showDialogOnButtonClick() {
        btnPickDate = (Button) findViewById(R.id.choose_date);
        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        myDb.close();
        mySDb.close();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID) {
            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            int month = Calendar.getInstance().get(Calendar.MONTH);
            int year = Calendar.getInstance().get(Calendar.YEAR);

            DatePickerDialog dpicker = new DatePickerDialog(this, dpickerListner, year_x, month_x, day_x);
            dpicker.updateDate(year, month+1, day);
            return dpicker;
        }

        return null;


    }

    private boolean isValid(){

        double zero;

        if(inGrade.getText().toString().isEmpty() || inGrade.getText().toString().equals("") || inGrade.getText().toString() == null) return false;

        try {
            zero = Double.parseDouble(inGrade.getText().toString());
            if(zero == 0) return false;
        } catch (NumberFormatException e){
            e.printStackTrace();
        }


        if (inGradeName.getText().toString() == null || inGradeName.getText().toString().isEmpty())
            return false;
        if(mSubjectSelectCardView.getVisibility() == View.VISIBLE) {
            if (inSubjectName.getSelectedItem().toString() == null || inSubjectName.getSelectedItem().toString().isEmpty())
                return false;
        }

        if (inGrade.getText().toString() == null || inGrade.getText().toString().isEmpty())
            return false;
        if (inFactor.getText().toString() == null || inFactor.getText().toString().isEmpty())
            return false;
        if (outDate == null || outDate.isEmpty()) return false;

        return true;
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