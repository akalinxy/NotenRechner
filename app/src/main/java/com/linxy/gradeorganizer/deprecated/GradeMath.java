//package com.linxy.gradeorganizer.utility;
//
//import android.content.Context;
//import android.database.Cursor;
//
//import com.linxy.gradeorganizer.R;
//import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;
//import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;
//import com.linxy.gradeorganizer.objects.SubAvg;
//import com.linxy.gradeorganizer.objects.Subject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
// * Created by Linxy on 15/8/2015 at 17:14
// * Working on Grade Organizer in com.linxy.gradeorganizer.utility
// */
//public class GradeMath {
//    /* Constants */
//    public static final int NO_AVERAGE = -1;
//    public static final int SUFFICIENT = -1;
//
//    /* Member Variables */
//    private DatabaseHelperSubjects dbs;
//    private Context context;
//
//    /* Instance Variables */
//    private boolean roundGrades;
//    private boolean insufficient;
//    private double insufficientGrade = 0;
//    private double numerator = 0;
//    private double denominator = 0;
//
//    public GradeMath (Context context, boolean roundGrades, boolean insufficient){
//        this.context = context;
//        this.roundGrades = roundGrades;
//        this.insufficient = insufficient;
//        dbs = new DatabaseHelperSubjects(context);
//    }
//
//    public ArrayList<SubAvg> getAdapter(){
//        ArrayList<SubAvg> averages = new ArrayList<>();
//        /* Fill averages with data of subject average, rounded inclusive, color, and name */
//        Cursor cursorSubjects = dbs.getAllData();
//        Subject subjectOb;
//        int i = 0;
//        while(cursorSubjects.moveToNext()){
//            subjectOb = new Subject(cursorSubjects.getString(1), Integer.parseInt(cursorSubjects.getString(2)), roundGrades, context);
//
//            if(subjectOb.getAverage() == NO_AVERAGE) {
//                /* If there is no average we make a simple entry for the adapter and leave it at that */
//                averages.add(i, new SubAvg(subjectOb.getName(), context.getResources().getString(R.string.hyphen), context.getResources().getColor(R.color.color_accent_yellow)));
//                i++;
//                continue;
//            } else {
//                /* If there is a average, we make a entry for the adapter and we also calculate possible insufficient marks, and calculate the global average */
//                averages.add(i, new SubAvg(subjectOb.getName(), String.format("%.2f",subjectOb.getAverage()), subjectOb.getColor()));
//
//                /* Calculate a total insufficient markvalue */
//                if(insufficient){
//                    double gi = subjectOb.getInsufficient();
//                    if(!(gi==SUFFICIENT))
//                        insufficientGrade += subjectOb.getInsufficient();
//                }
//                /* Calculate the global average */
//                numerator += subjectOb.getAverage() * (subjectOb.getFactor() / 100);
//                denominator += (subjectOb.getFactor() / 100);
//                i++;
//            }
//        }
//        cursorSubjects.close();
//        dbs.close();
//        return averages;
//    }
//
//    public void Reset(){
//        numerator = 0;
//        denominator = 0;
//        insufficientGrade = 0;
//    }
//
//    public double getInsufficientGrade(){
//        return insufficientGrade;
//    }
//
//    public double getGlobalAverage(){
//        /* The reason for the line commented out below is because apparently, the total average never gets rounded. */
//        // if(roundGrades) return roundToHalf(numerator / denominator);
//        return numerator / denominator;
//    }
//
//    public static double roundToHalf(double d){
//        return Math.round(d * 2) / 2.0;
//    }
//}
