package com.linxy.gradeorganizer.tabs;

import android.content.Context;
import android.database.DatabaseUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.linxy.gradeorganizer.EditSubjectsActivity;
import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.StartupActivity;
import com.linxy.gradeorganizer.Utils;

/**
 * Created by Linxy on 30/7/2015 at 19:46
 * Working on Grade Organizer in com.linxy.gradeorganizer.tabs
 */
public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {

    public static final float HIDE_THRESHOLD = 10;
    public static final float SHOW_THRESHOLD = 70;

    private int mToolbarOffset = 0;
    private int mToolbarHeight;
    private boolean mControlsVisible = true;
    private int mTotalScrolledDistance;

    public HidingScrollListener(Context context) {
        mToolbarHeight = Utils.getToolbarHeight(context);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if(newState == RecyclerView.SCROLL_STATE_IDLE) {
            if(mTotalScrolledDistance < mToolbarHeight) {
                setVisible();
            } else {
                if (mControlsVisible) {
                    if (mToolbarOffset > HIDE_THRESHOLD) {
                        setInvisible();
                    } else {
                        setVisible();
                    }
                } else {
                    if ((mToolbarHeight - mToolbarOffset) > SHOW_THRESHOLD) {
                        setVisible();
                    } else {
                        setInvisible();
                    }
                }
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        clipToolbarOffset();
        onMoved(mToolbarOffset);

        if((mToolbarOffset <mToolbarHeight && dy>0) || (mToolbarOffset >0 && dy<0)) {
            mToolbarOffset += dy;
        }

        mTotalScrolledDistance += dy;

    }

    private void clipToolbarOffset() {
        if(mToolbarOffset > mToolbarHeight) {
            mToolbarOffset = mToolbarHeight;
        } else if(mToolbarOffset < 0) {
            mToolbarOffset = 0;
        }
    }

    private void setVisible(){
        if(mToolbarOffset > 0){
            onShow();
            mToolbarOffset = 0;
        }
        mControlsVisible = true;
    }

    private void setInvisible(){
        if(mToolbarOffset < mToolbarHeight){
            onHide();
            mToolbarOffset = mToolbarHeight;
        }
        mControlsVisible = false;
    }

    public abstract void onShow();
    public abstract void onHide();

    public abstract void onMoved(int distance);
}