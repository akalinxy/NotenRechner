package com.linxy.gradeorganizer.adapters;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.fragments.CalendarFragment;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Linxy on 7/8/2015 at 10:38
 * Working on Grade Organizer in com.linxy.gradeorganizer.com.linxy.adapters
 */
public class CRVAdapter extends RecyclerView.Adapter<CRVAdapter.CalendarViewHolder> {
    public static MyCalClickListener myCalClickListener;

    public static class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cvMain;
        CardView cvIcon;
        CardView cvDelete;
        TextView tvSubjectName;
        TextView tvExamName;
        TextView tvDateDay;
        TextView tvDateMonth;
        TextView tvWeekDay;
        ImageButton imgbtnAlarm;
        ImageButton imgbtnDelete;

        public CalendarViewHolder(View v) {
            super(v);

            cvMain = (CardView) v.findViewById(R.id.cvMain);
            cvIcon = (CardView) v.findViewById(R.id.cvAlarm);
            cvDelete = (CardView) v.findViewById(R.id.cvDelete);
            tvSubjectName = (TextView) v.findViewById(R.id.tvSubjectname);
            tvExamName = (TextView) v.findViewById(R.id.tvGradename);
            tvDateDay = (TextView) v.findViewById(R.id.tvDateDay);
            tvDateMonth = (TextView) v.findViewById(R.id.tvDateMonth);
            tvWeekDay = (TextView) v.findViewById(R.id.tvWeekday);
            imgbtnAlarm = (ImageButton) v.findViewById(R.id.imgbtnAddNotification);
            imgbtnDelete = (ImageButton) v.findViewById(R.id.imgbtnDeleteSceduledExam);
            imgbtnAlarm.setOnClickListener(this);
            imgbtnDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myCalClickListener.onItemClick(getPosition(), v);
        }
    }

    List<CalendarFragment.Date> dates;

    public CRVAdapter(List<CalendarFragment.Date> dates) {
        this.dates = dates;
    }

    public void setOnItemClickListener(MyCalClickListener myCalClickListener) {
        Log.i("DEBUG", "setOnItemClickListener"); // Happens
        this.myCalClickListener = myCalClickListener;
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }


    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.i("DEBUG", "onCreateViewholder"); // Happens
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.calendar_entry_card, viewGroup, false);
        CalendarViewHolder cvh = new CalendarViewHolder(v);
        return cvh;

    }


    @Override
    public void onViewAttachedToWindow(CalendarViewHolder calendarViewHolder) {
        Log.i("DEBUG", "onViewAttached"); // Doesnt happen
        super.onViewAttachedToWindow(calendarViewHolder);

    }

    @Override
    public void onBindViewHolder(CalendarViewHolder calendarViewHolder, int i) {

        calendarViewHolder.tvSubjectName.setText(dates.get(i).dateSubjectName);
        calendarViewHolder.tvExamName.setText(dates.get(i).dateGradeName);


        String dateString = dates.get(i).dateDate;
        Log.i("DATESTRING", dateString);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date convertedDate = new Date();
        try{
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e){
            e.printStackTrace();
        }
        Log.i("DATE", convertedDate.toString());




        calendarViewHolder.tvDateDay.setText(String.valueOf(convertedDate.getDate()));
        int monthInt = convertedDate.getMonth();
        calendarViewHolder.tvDateMonth.setText(String.valueOf(new DateFormatSymbols().getShortMonths()[monthInt]));
        calendarViewHolder.tvWeekDay.setText(String.valueOf(new SimpleDateFormat("EE").format(convertedDate)));



        String color = monthBackground(convertedDate.getMonth());
        calendarViewHolder.cvIcon.setCardBackgroundColor(Color.parseColor(color));
        calendarViewHolder.cvMain.setCardBackgroundColor(Color.parseColor(color));
        calendarViewHolder.cvDelete.setCardBackgroundColor(Color.parseColor(color));

    }

    private String monthBackground(int month) {
        Log.i("DEBUG", "MonthBackground"); // Doesnt Happen

        String colors[] =  {
                    "#2196F3", // January
                    "#00BCD4", // Febuary
                    "#009688", // March
                    "#8BC34A", // April
                    "#4CAF50", // May
                    "#FFEB3B", // June
                    "#FFA000", // July
                    "#FF5722", // August
                    "#FBC02D", // September
                    "#FFE0B2", // October
                    "#607D8B", // November
                    "#673AB7" // December
        };

        return colors[month];
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        Log.i("DEBUG", "onAttachedToRecyclerview"); // Happens

        super.onAttachedToRecyclerView(recyclerView);
    }

    // functional interface..
    public interface MyCalClickListener {

        public void onItemClick(int position, View v);
    }


}