package com.linxy.gradeorganizer.objects;

import java.io.Serializable;

/**
 * Created by Linxy on 14/8/2015 at 15:02
 * Working on Grade Organizer in com.linxy.gradeorganizer.objects
 */
public class Grade  { // TODO make this implement parcelable for better performance
    private String subject;
    private String name;
    private double grade;
    private int factor;
    private String date;

    public Grade(String subject, String name, double grade, int factor, String date){
        this.subject = subject;
        this.name = name;
        this.grade = grade;
        this.factor =factor;
        this.date = date;
    }

    public String getSubject(){ return this.subject; }
    public String getName(){ return this.name; }
    public double getGrade(){ return this.grade; }
    public int getFactor(){ return this.factor; }
    public String getDate(){ return this.date; }

}
