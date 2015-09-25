package com.linxy.gradeorganizer.objects;

import com.linxy.gradeorganizer.R;

/**
 * Created by Linxy on 22/9/2015 at 19:46
 * Working on Grade Organizer in com.linxy.gradeorganizer.objects
 */
public class SubjectGrade {

    private double mGrade;
    private String mName;
    private int mIconId;

    public SubjectGrade (String name, double grade){
        mGrade = grade;
        mName = name;
    }

    public void setGrade(double grade) {
        mGrade = grade;
    }

    public void setName(String name) {
        mName = name;
    }

    public double getGrade() {
        return mGrade;
    }

    public String getName() {
        return mName;
    }

    public int getIconId() { // TODO make Average customizable
        if(mGrade >= 4){
            return R.drawable.icon_average_up;
        } else if(mGrade == -1) {
            return R.drawable.icon_no_average;
        } else {
            return R.drawable.icon_average_down;
        }
    }
}
