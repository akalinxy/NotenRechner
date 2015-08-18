package com.linxy.gradeorganizer.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.activities.StartupActivity;
import com.linxy.gradeorganizer.adapters.SRVAdapter;
import com.linxy.gradeorganizer.objects.SubAvg;
import com.linxy.gradeorganizer.objects.Subject;
import com.linxy.gradeorganizer.utility.GradeMath;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by linxy on 7/26/15.
 * Updated revision: 16:8:2015
 */

public class Tab1 extends Fragment implements Handler.Callback {

    /* Constants */
    public static final String TAG = Tab1.class.getSimpleName();
    public static final int INITIALIZE_DATA = 10; /* Not on main thread */
    public static final int UPDATE_GUI = 20; /* On main thread */
    public static final int UPDATE_DATA = 30; /* On seperate thread */





    /* View Components */
    private TextView tvGlobalAverage;
    private TextView tvInsufficient;
    private CardView cvInsufficientHolder;
    private RecyclerView recyclerView;

    /* Multithreading */
    Handler mHandler;
    Handler mHandlerMain;

    /* Instance Variables */
    private boolean showInsufficient;
    private boolean roundGrades;

    private double globalAverage;
    private double globalInsufficient;

    /* Adapter Components */
    SRVAdapter mAdapter;
    ArrayList<SubAvg> averages; /* Guidline: Create list and declare it globally */

    /* Objects */
    GradeMath gradeMath;  /* We are allowed to create a instance grademath component, because it relies on
                          * prefrences which cannot be changed directly without having to change tabs and
                          * thus recreate this view.
                          */


    public static Tab1 getInstance(){
        return new Tab1();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_1, container, false);


        mHandlerMain = new Handler(getActivity().getMainLooper(), this);
        HandlerThread handlerThread = new HandlerThread("Background Thread");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper(), this);

        SetupPrefs(); /* Creates a preference object, and assigns their values to our instance preference values */

        /* We must create mAdapter before SetupGui(v); */
        /* INITIALIZE_DATA creates mAdapter */
        sendMessage(mHandler, INITIALIZE_DATA, null);

       // SetupGui(v);


        return v;
    }

    /* This method will be called after the dataset has changed after adding a new grade. */
    public void refreshData(){
        Log.i(TAG, "refreshData()");
        /* UPDATE_DATA will call UPDATE_GUI */
        sendMessage(mHandler, UPDATE_DATA, null);
    }

    @Override
    public boolean handleMessage(Message msg) {
        GradeMath gradeMath = null;
        switch (msg.what){

            case INITIALIZE_DATA:
                Log.i(TAG, "INITIALIZE_DATA");
                /* We must get the preferences before this is executed! */
                /* This will 1. Create our data 2. Initialize our Adapter */
                gradeMath = new GradeMath(getActivity().getBaseContext(), roundGrades, showInsufficient);
                averages = new ArrayList<>(); /*  We create an ArrayList of SubAvg objects, it is empty. */
                averages = gradeMath.getAdapter();
                mAdapter = new SRVAdapter(averages); /* mAdapter is Initialized */

                /* We get our values, and assign them to our instance variables */
                globalAverage = gradeMath.getGlobalAverage();
                globalInsufficient = gradeMath.getInsufficientGrade();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SetupGui(getView());
                    }
                });

                break;

            case UPDATE_GUI:
                Log.i(TAG, "UPDATE_GUI");
                /* Update the TextView's with the current values */
                /* This gets called AFTER we updated the data on a seperate thread */
                if(!(Double.isNaN(globalAverage))) {
                    tvGlobalAverage.setText(String.format("%.2f", globalAverage));
                    tvGlobalAverage.setTextColor(Subject.getColor(globalAverage, getActivity().getBaseContext()));
                } else {
                    tvGlobalAverage.setText(getResources().getString(R.string.triplehyphen));
                }
                if(showInsufficient) {
                    tvInsufficient.setText(String.format("%.2f", globalInsufficient));
                }

                break;

            case UPDATE_DATA:
                Log.i(TAG, "UPDATE_DATA");
                /* This Thread will 1. Update our values 2. Update the recycler 3. Call UPDATE_GUI */
                gradeMath = new GradeMath(getActivity().getBaseContext(), roundGrades, showInsufficient);
                /* We reassign a new ArrayList to averages */
                averages.clear();
                ArrayList<SubAvg> tempList = gradeMath.getAdapter();
                for(int i = 0; i < tempList.size(); i++){
                    averages.add(tempList.get(i));
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
                globalAverage = gradeMath.getGlobalAverage();
                globalInsufficient = gradeMath.getInsufficientGrade();
                sendMessage(mHandlerMain, UPDATE_GUI, null);
                break;

        }
        return true;
    }

    private void sendMessage(Handler handler, int where, Object data){
        Message.obtain(handler, where, data).sendToTarget();

    }

    private void SetupPrefs(){
        /* Initialize our Prefrences */
        SharedPreferences preferences = getActivity().getSharedPreferences(StartupActivity.PREFS, 0);
        showInsufficient = preferences.getBoolean("insufficient", true);
        //roundGrades = preferences.getInt("roundGrade", 0) == 0 ? roundGrades = false : roundGrades == true;
        if(preferences.getInt("roundGrade", 0) == 0)
            roundGrades = false;
        else
            roundGrades = true;
    }

    private void SetupGui(View v){
        tvGlobalAverage = (TextView) v.findViewById(R.id.average_grade);
        tvInsufficient = (TextView) v.findViewById(R.id.insuff_marks);
        cvInsufficientHolder = (CardView) v.findViewById(R.id.cardview_insufficient);
        recyclerView = (RecyclerView) v.findViewById(R.id.avg_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mAdapter);

        if(showInsufficient) {
            cvInsufficientHolder.setVisibility(View.VISIBLE);
        } else {
            cvInsufficientHolder.setVisibility(View.GONE);
        }

        sendMessage(mHandlerMain, UPDATE_GUI, null);
    }



    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mHandler.getLooper().quit();
    }


}


