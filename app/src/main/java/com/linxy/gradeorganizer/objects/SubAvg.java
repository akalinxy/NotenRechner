package com.linxy.gradeorganizer.objects;

/**
 * Created by Linxy on 15/8/2015 at 16:38
 * Working on Grade Organizer in com.linxy.gradeorganizer.objects
 */

/* This class has little functionality, it serves as a data holder for
 * the recyclerview adapter.
 */
public class SubAvg {
    public String subject;
    public String average;
    public int color;

    public SubAvg (String subject, String average, int color){
        this.subject = subject;
        this.average = average;
        this.color = color;
    }
}
