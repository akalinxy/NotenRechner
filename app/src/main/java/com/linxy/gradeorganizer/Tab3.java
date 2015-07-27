package com.linxy.gradeorganizer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;

/**
 * Created by linxy on 7/26/15.
 */
public class Tab3 extends Fragment{

    Spinner spinnerRoundTo;
    String arraySpinner[] = {"Ganze Note", "Halbe Note" };

    Spinner spinnerBestGrade;
    String arrayBestGrade[] = new String[10];

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.tab_3, container, false);


        for(int i = 0; i < 10; i++){
            arrayBestGrade[i] = String.valueOf(i);
        }
        spinnerBestGrade = (Spinner) v.findViewById(R.id.best_grade);
        ArrayAdapter<String> nadapter = new ArrayAdapter<String>(getActivity().getBaseContext(), R.layout.centered_spinner, arrayBestGrade);
        nadapter.setDropDownViewResource(R.layout.centered_spinner);
        spinnerBestGrade.setAdapter(nadapter);

        spinnerRoundTo = (Spinner) v.findViewById(R.id.round_to_grade);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), R.layout.centered_spinner, arraySpinner);
        adapter.setDropDownViewResource(R.layout.centered_spinner);
        spinnerRoundTo.setAdapter(adapter);


        return  v;
    }
}
