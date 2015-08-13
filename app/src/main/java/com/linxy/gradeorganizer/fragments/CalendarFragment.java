package com.linxy.gradeorganizer.fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.linxy.gradeorganizer.NotificationPublisher;
import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.RegisterExamActivity;
import com.linxy.gradeorganizer.StartupActivity;
import com.linxy.gradeorganizer.com.linxy.adapters.CRVAdapter;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperCalendar;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperScheduled;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;
import com.linxy.gradeorganizer.utils.MyRecyclerScroll;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateChangedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


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

    private List<Date> dates;
    CRVAdapter adapter;

    private boolean dateSelected;
    private int selectedDay;
    private int selectedMonth;
    private int selectedYear;


    RecyclerView rvCalDates;
    FloatingActionButton fabRegisterExam;
    DatabaseHelperCalendar dbc;

    public CalendarFragment() {
        // Required empty public constructor
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
        adapter = new CRVAdapter(dates);
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
            case R.id.imgbtnAddNotification:
                dateSelected = false;
                LayoutInflater li = LayoutInflater.from(v.getContext());
                View dialogView = li.inflate(R.layout.popup_schedulenotification, null);

                AlertDialog.Builder builder1 = new AlertDialog.Builder(v.getContext());
                AlertDialog dialog1;
                builder1.setView(dialogView);
                builder1.setTitle("Errinerung Setzern"); /* TODO MAKE STRING REFERENCE FOR THIS */

                final MaterialCalendarView mcvScheduledNotification = (MaterialCalendarView) dialogView.findViewById(R.id.scheduleCalendar);


                final java.util.Date today = new java.util.Date();
                today.setDate(today.getDate());
                today.setHours(0);

                java.util.Date dayofexam = dates.get(position).date;


                mcvScheduledNotification.setMinimumDate(today);
                mcvScheduledNotification.setMaximumDate(dayofexam);
                mcvScheduledNotification.setOnDateChangedListener(new OnDateChangedListener() {
                    @Override
                    public void onDateChanged(MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {
                        dateSelected = true;
                    }
                });

                builder1.setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /* Create the Notification */
                        if (dateSelected) {
                            Calendar startDate = Calendar.getInstance();
                            Calendar endDate = mcvScheduledNotification.getSelectedDate().getCalendar();
                            long days = daysBetween(startDate, endDate);
                            Log.i("DAYS", String.valueOf(days));

                            long millis = TimeUnit.DAYS.toMillis(days);

                            scheduleNotification(getNotification(dates.get(position).dateSubjectName + "\n" + dates.get(position).dateGradeName), millis);  /* TODO MAKE THIS WORK */
                            Log.i("TIMEDATE: ", String.valueOf(millis));
                            DatabaseHelperScheduled db = new DatabaseHelperScheduled(getActivity().getBaseContext());
                            db.insertData(endDate.get(Calendar.DAY_OF_WEEK)+"."+endDate.get(Calendar.MONTH) + 1+"."+endDate.get(Calendar.YEAR), dates.get(position).dateSubjectName, dates.get(position).dateGradeName);
                            db.close();


                        } else {
                            Toast.makeText(getActivity().getBaseContext(), "Error, must select date!", Toast.LENGTH_SHORT).show(); /* TODO MAKE STRING REFERENCE FOR ERROR MUST SELECT DATE */

                        }
                    }
                });

                builder1.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                dialog1 = builder1.create();
                dialog1.show();


                break;
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

    private void scheduleNotification(Notification notification, long delay) {
        Intent notificationIntent = new Intent(getActivity().getBaseContext(), NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getBaseContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);

    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(getActivity().getBaseContext());
        builder.setContentTitle("Prufing Errinerung"); /* TODO MAKE STRING REFERENCE FOR REMEMBER TEST */
        builder.setContentTitle(content);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        return builder.build();
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
