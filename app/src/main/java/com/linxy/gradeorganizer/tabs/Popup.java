package com.linxy.gradeorganizer.tabs;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.linxy.gradeorganizer.R;

/**
 * Created by minel_000 on 28/7/2015.
 */
public class Popup extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_edit_subject);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.6));
    }
}
