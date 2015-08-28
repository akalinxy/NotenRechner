package com.linxy.gradeorganizer.utility;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Linxy on 20/8/2015 at 17:27
 * Working on Grade Organizer in com.linxy.gradeorganizer.utility
 */
public class AppBarLayoutSnapBehavior extends ControllableAppBarLayout.Behavior {

    private boolean mNestedScrollStarted = false;
    private ValueAnimator mValueAnimator;

    public AppBarLayoutSnapBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        Log.i("AppBarLayout", "OnStartNestedScroll");
        mNestedScrollStarted = super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
        if(mNestedScrollStarted && mValueAnimator != null){ // Condition: (if) we scroll &and& our ValueAnimator object is NOT null, aka it exists, then and only then we cancel the animation.
            mValueAnimator.cancel();
        }
        return  mNestedScrollStarted; /* Factory return */
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target) {
        Log.i("AppBarLayout", "OnStopNestedScroll");
        super.onStopNestedScroll(coordinatorLayout, child, target);
        if(!mNestedScrollStarted){
            return;
        }

        mNestedScrollStarted = false;

        int scrollRange = child.getTotalScrollRange();
        int topOffset = getTopAndBottomOffset();

        if(topOffset <= - scrollRange || topOffset >= 0){
            // Already fully visible and or invisible
            return;
        }

        if(topOffset < - (scrollRange / 2f)){
            // Snap up.
            animateOffsetTo(-scrollRange);
        } else {
            // Snap down.
            animateOffsetTo(0);
        }
    }

    private void animateOffsetTo(int offset){
        if(mValueAnimator == null){
            mValueAnimator = new ValueAnimator();
            mValueAnimator.setInterpolator(new DecelerateInterpolator());
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setTopAndBottomOffset((int) animation.getAnimatedValue());
                }
            });
        } else {
            mValueAnimator.cancel();
        }

        mValueAnimator.setIntValues(getTopAndBottomOffset(), offset);
        mValueAnimator.start();
    }
}
