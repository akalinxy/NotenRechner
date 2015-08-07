package com.linxy.gradeorganizer.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.RegisterExamActivity;
import com.linxy.gradeorganizer.com.linxy.adapters.CRVAdapter;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperCalendar;

import java.util.ArrayList;
import java.util.List;


public class CalendarFragment extends Fragment implements View.OnClickListener, CRVAdapter.MyCalClickListener{


    public class Date {

        public String dateId;
        public String dateSubjectName;
        public String dateGradeName;
        public String dateGradeFactor;
        public String dateDate;

        Date(String dateId, String dateSubjectName, String dateGradeName, String dateGradeFactor, String dateDate) {
            this.dateId = dateId;
            this.dateSubjectName = dateSubjectName;
            this.dateGradeName = dateGradeName;
            this.dateGradeFactor = dateGradeFactor;
            this.dateDate = dateDate;
        }
    }

    private List<Date> dates;
    CRVAdapter adapter;


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
        rvCalDates.setAdapter(adapter);

        return v;
    }

    private void fillDates(){
        Cursor c = dbc.getAllData();
        while(c.moveToNext()) {
            dates.add(new Date(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4)));
        }
        c.close();
    }

    @Override
    public void onItemClick(int position, View v) {
        Toast.makeText(getActivity().getBaseContext(), "Clicky!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onPause(){
        super.onPause();
        dbc.close();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_newtest:
                Intent intent = new Intent(getActivity().getBaseContext(), RegisterExamActivity.class);
                startActivity(intent);
                break;

        }
    }
}
