package com.linxy.gradeorganizer.utility;

import android.content.Context;
import android.content.res.TypedArray;

import com.linxy.gradeorganizer.R;

/**
 * Created by Linxy on 16/8/2015 at 09:45
 * Working on Grade Organizer in com.linxy.gradeorganizer.utility
 */
public class Utils {

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static int getTabsHeight(Context context) {
        return (int) context.getResources().getDimension(R.dimen.tab_max_width);
    }
}