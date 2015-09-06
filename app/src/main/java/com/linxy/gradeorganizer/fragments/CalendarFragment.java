package com.linxy.gradeorganizer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.activities.RegisterExamActivity;
import com.linxy.gradeorganizer.activities.StartupActivity;
import com.linxy.gradeorganizer.adapters.CRVAdapter;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperCalendar;
import com.linxy.gradeorganizer.utility.MyRecyclerScroll;
import com.parse.ParseObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class CalendarFragment extends Fragment implements View.OnClickListener, CRVAdapter.MyCalClickListener {


    public class Date implements Comparable<Date> {

        public String dateId;
        public String dateSubjectName;
        public String dateGradeName;
        public String dateGradeFactor;
        public String dateDate;
        public java.util.Date date;

        Date(String dateId, String dateSubjectName, String dateGradeName, String dateGradeFactor, String dateDate) {
            this.dateId = dateId;
            this.dateSubjectName = dateSubjectName;
            this.dateGradeName = dateGradeName;
            this.dateGradeFactor = dateGradeFactor;
            this.dateDate = dateDate;

            String dateString = dateDate;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            java.util.Date convertedDate = new java.util.Date();
            try {
                convertedDate = dateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            this.date = convertedDate;
        }

        public java.util.Date getDateTime() {
            return date;
        }

        public void setDateTime(java.util.Date d) {
            this.date = d;
        }

        @Override
        public int compareTo(Date o) {
            return getDateTime().compareTo(o.getDateTime());
        }
    }

    public static final String TAG = CalendarFragment.class.getSimpleName();

    private List<Date> dates;
    CRVAdapter adapter;

    private boolean dateSelected;
    private int selectedDay;
    private int selectedMonth;
    private int selectedYear;


    RecyclerView rvCalDates;
    FloatingActionButton fabRegisterExam;
    DatabaseHelperCalendar dbc;


    public static CalendarFragment getInstance(){
        return new CalendarFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);
        fabRegisterExam = (FloatingActionButton) v.findViewById(R.id.fab_newtest);
        fabRegisterExam.setOnClickListener(this);
        dbc = new DatabaseHelperCalendar(getActivity().getBaseContext());
        rvCalDates = (RecyclerView) v.findViewById(R.id.rvCalendarDates);
        rvCalDates.setHasFixedSize(true);


        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(v.getContext());
        rvCalDates.setLayoutManager(linearLayoutManager);

        dates = new ArrayList<>();
        adapter = new CRVAdapter(dates, getActivity().getBaseContext());
        adapter.setOnItemClickListener(this);
        fillDates();
        Collections.sort(dates);
        rvCalDates.setAdapter(adapter);
        rvCalDates.setOnScrollListener(new MyRecyclerScroll() {
            @Override
            public void show() {
                fabRegisterExam.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void hide() {
                fabRegisterExam.animate().translationY(fabRegisterExam.getHeight() + 48).setInterpolator(new AccelerateInterpolator(2)).start();
            }
        });


        return v;
    }


    private void fillDates() {
        Cursor c = dbc.getAllData();
        while (c.moveToNext()) {
            dates.add(new Date(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4)));
        }
        c.close();
    }


    @Override
    public void onItemClick(final int position, View v) {
        switch (v.getId()) {

            case R.id.imgbtnDeleteSceduledExam:

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                final TextView tvTest = new TextView(v.getContext());
                tvTest.setText(dates.get(position).dateGradeName + dates.get(position).dateId);
                builder.setView(tvTest);

                builder.setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbc.deleteData(dates.get(position).dateId);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dates.remove(position);
                                adapter.notifyItemRemoved(position);
                            }
                        });

                        dialog.cancel();
                    }
                });

                builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                break;
            default:
                break;
        }
    }

    public static long daysBetween(Calendar startDate, Calendar endDate) {
        Calendar date = (Calendar) startDate.clone();
        long daysBetween = 0;
        while (date.before(endDate)) {
            date.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {

                String subjectname = data.getStringExtra("subjectname");
                String examname = data.getStringExtra("examname");
                String examfactor = data.getStringExtra("examfactor");
                String examdate = data.getStringExtra("examdate");
                Log.i("DebugResult", "Data inserted :" + subjectname + examname + examfactor + examdate);

                ParseObject calOb = new ParseObject("TestCalendar");
                calOb.put("deviceId", StartupActivity.deviceId);
                calOb.put("subjectName", subjectname);
                calOb.put("examName", examname);
                calOb.put("examfactor", examfactor);
                calOb.put("ExamDate", examdate);
                calOb.saveInBackground();

                dbc.insertData(subjectname, examname, examfactor, examdate);
                dbc.close();

                Cursor c = dbc.getAllData();
                c.moveToPosition(c.getCount() - 1);


                dates.add(new Date(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4)));
                Log.i("DebugResult", "Date Added" + dates.get(dates.size() - 1).toString());
                c.close();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        Collections.sort(dates);
                    }
                });
                dbc.close();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                /* No Result */
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("ONRESUME", "RESUME");
    }


    @Override
    public void onPause() {
        super.onPause();
        dbc.close();
        Log.i("ONPAUSE", "PAUSE");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_newtest:
                Intent intent = new Intent(getActivity().getBaseContext(), RegisterExamActivity.class);
                startActivityForResult(intent, 1);


                break;


        }
    }
}
