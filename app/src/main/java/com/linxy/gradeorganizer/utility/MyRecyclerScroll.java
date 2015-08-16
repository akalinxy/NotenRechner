package com.linxy.gradeorganizer.utility;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Linxy on 11/8/2015 at 13:34
 * Working on Grade Organizer in com.linxy.gradeorganizer.utils
 */
public abstract  class MyRecyclerScroll extends RecyclerView.OnScrollListener {

    int scrollDist = 0;
    boolean isVisible = true;
    static final float MINIMUM = 5;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (isVisible && scrollDist > MINIMUM) {
            hide();
            scrollDist = 0;
            isVisible = false;
        }
        else if (!isVisible && scrollDist < -MINIMUM) {
            show();
            scrollDist = 0;
            isVisible = true;
        }
        if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
            scrollDist += dy;
        }
    }

    public abstract void show();
    public abstract void hide();
}
