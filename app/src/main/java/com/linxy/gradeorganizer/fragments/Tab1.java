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
    public static final int REFRESH_DATA = 5;
    public static final int CREATE_DATA = 10;
    public static final int UI_UPDATE_GLOBAL = 15;
    public static final int UI_REFRESH = 20;
    public static final int INIT_PREFS = 30;



    /* View Components */
    private TextView tvGlobalAverage;
    private TextView tvInsufficient;
    private CardView cvInsufficientHolder;
    private RecyclerView rvSubjectAverages;

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
    ArrayList<SubAvg> averages;

    /* Objects GradeMath gradeMath; */
     /* We are allowed to create a instance grademath component, because it relies on
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
        tvGlobalAverage = (TextView) v.findViewById(R.id.average_grade);
        tvInsufficient = (TextView) v.findViewById(R.id.insuff_marks);
        cvInsufficientHolder = (CardView) v.findViewById(R.id.cardview_insufficient);

        rvSubjectAverages = (RecyclerView) v.findViewById(R.id.avg_recycler);
        rvSubjectAverages.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        rvSubjectAverages.setLayoutManager(linearLayoutManager);


        mHandlerMain = new Handler(getActivity().getMainLooper(), this);
        HandlerThread handlerThread = new HandlerThread("Background Thread");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper(), this);

        sendMessage(mHandler, INIT_PREFS, null); /* We must initialize the preferences before we can create a GradeMath object */

        Log.i(TAG, "Prefs Initialized. RoundGrades : ShowInsufficient " + roundGrades + " : " + showInsufficient);
        sendMessage(mHandler, CREATE_DATA, null);
        sendMessage(mHandlerMain, UI_UPDATE_GLOBAL, null);

        rvSubjectAverages.setAdapter(mAdapter);




        return v;
    }

    /* This method will be called after the dataset has changed after adding a new grade. */
    public void refreshData(){
        Log.i(TAG, "inside RefreshData");
        sendMessage(mHandler, REFRESH_DATA, null);
        //sendMessage(mHandlerMain, UI_UPDATE_GLOBAL, null);
    }

    @Override
    public boolean handleMessage(Message msg) {
        GradeMath gradeMath = null;
        switch (msg.what){

            case REFRESH_DATA:
                /* Recalculate the adapter and global values, then notify datasetchanged on the adapter.*/
                /* We are on a seperate thread */
                gradeMath = new GradeMath(getActivity().getBaseContext(), roundGrades, showInsufficient);
                averages.clear();
                averages = gradeMath.getAdapter();
                mAdapter = new SRVAdapter(averages);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rvSubjectAverages.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                Log.i(TAG, "REFRESH_DATA");
                globalAverage = gradeMath.getGlobalAverage();
                if(showInsufficient) globalInsufficient = gradeMath.getInsufficientGrade();
                Log.i(TAG, "Inside REFRESH DATA WITH " + globalAverage + "  " + globalInsufficient);

                HashMap<String, Double> obj = new HashMap<>();
                obj.put("average", gradeMath.getGlobalAverage());
                obj.put("insufficient", gradeMath.getInsufficientGrade());

                sendMessage(mHandlerMain, UI_REFRESH, obj);


                break;
            case CREATE_DATA:
                /* Calculate our Data on seperate thread */ /* This gets Called when the user reigsters a new Grade */
                gradeMath = new GradeMath(getActivity().getBaseContext(), roundGrades, showInsufficient);
                averages = gradeMath.getAdapter();
                mAdapter = new SRVAdapter(averages);
                globalAverage = gradeMath.getGlobalAverage();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rvSubjectAverages.setAdapter(mAdapter);
                    }
                });
                if(showInsufficient) globalInsufficient = gradeMath.getInsufficientGrade();
                Log.i(TAG, "Inside CREATEDATA with Values: " + globalAverage + " " + globalInsufficient);


                break;
            case UI_UPDATE_GLOBAL:

                Log.i(TAG, "Inside UIUPDATEGLOBAL with Values: " + globalAverage + " " + globalInsufficient);

                tvGlobalAverage.setText(String.format("%.2f", globalAverage));
                if(showInsufficient){
                    cvInsufficientHolder.setVisibility(View.VISIBLE);
                    tvInsufficient.setText(String.format("%.2f", globalInsufficient));
                } else {
                    cvInsufficientHolder.setVisibility(View.GONE);
                }

                break;

            case UI_REFRESH:
                HashMap<String, Double> data = (HashMap<String, Double>) msg.obj;
                tvGlobalAverage.setText(String.format("%.2f", data.get("average")));
                if(showInsufficient){
                    cvInsufficientHolder.setVisibility(View.VISIBLE);
                    tvInsufficient.setText(String.format("%.2f", data.get("insufficient")));
                } else {
                    cvInsufficientHolder.setVisibility(View.GONE);
                }



                break;
            case INIT_PREFS:
                /* Initialize our Prefrences */
                SharedPreferences preferences = getActivity().getSharedPreferences(StartupActivity.PREFS, 0);
                showInsufficient = preferences.getBoolean("insufficient", true);

                //roundGrades = preferences.getInt("roundGrade", 0) == 0 ? roundGrades = false : roundGrades == true;
                if(preferences.getInt("roundGrade", 0) == 0)
                    roundGrades = false;
                else
                    roundGrades = true;

                Log.i(TAG, "Inside INIT_PREFS : showInsufficient : roundGrades -> " + showInsufficient + ":" + roundGrades);
                gradeMath = new GradeMath(getActivity().getBaseContext(), roundGrades, showInsufficient);

                break;
        }
        return true;
    }

    private void sendMessage(Handler handler, int where, Object data){
        Message.obtain(handler, where, data).sendToTarget();

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


