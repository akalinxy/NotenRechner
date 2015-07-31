package com.linxy.gradeorganizer.tabs;

import android.content.Context;
import android.database.DatabaseUtils;
import android.support.v7.widget.RecyclerView;

import com.linxy.gradeorganizer.EditSubjectsActivity;
import com.linxy.gradeorganizer.StartupActivity;

/**
 * Created by Linxy on 30/7/2015 at 19:46
 * Working on Grade Organizer in com.linxy.gradeorganizer.tabs
 */
public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {
    private int mToolbarOffset = 0;
    private int mToolbarHeight;

    public HidingScrollListener(Context context){
        mToolbarHeight = StartupActivity.tHeigt;
    }


}
