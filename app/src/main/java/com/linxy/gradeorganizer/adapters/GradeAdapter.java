package com.linxy.gradeorganizer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.objects.Grade;

import java.util.ArrayList;

/**
 * Created by Linxy on 24/9/2015 at 21:15
 * Working on Grade Organizer in com.linxy.gradeorganizer.adapters
 */
public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.GradeViewHolder> {

    public interface MyGradeClickListener {
        public void onGradeItemClick(int position, View view);
    }

    public Context mContext;
    public static MyGradeClickListener mGradeClickListener;



    private ArrayList<Grade> mGrades;
    public GradeAdapter(Context context, ArrayList<Grade> mAdapter) {
        this.mGrades = mAdapter;
        this.mContext = context;
    }

    public void setOnGradeClickListener(MyGradeClickListener mGradeClickListener) {
        this.mGradeClickListener= mGradeClickListener;
    }

    @Override
    public GradeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_subject_grade, parent, false);
        GradeViewHolder gradeViewHolder = new GradeViewHolder(view);
        return gradeViewHolder;
    }

    @Override
    public void onBindViewHolder(GradeViewHolder holder, int position) {
        holder.bindGrade(mGrades.get(position));
    }

    @Override
    public int getItemCount() {
        return mGrades.size();
    }


    public class GradeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mGradeName;
        public TextView mGradeDate;
        public TextView mGradeMark;
        public TextView mGradeFactor;

        public Button mEditGrade;
        public Button mDeleteGrade;

        public GradeViewHolder(View view) {
            super(view);
            mGradeName = (TextView) view.findViewById(R.id.subjectdetail_name);
            mGradeDate = (TextView) view.findViewById(R.id.subjectdetail_date);
            mGradeMark = (TextView) view.findViewById(R.id.subjectdetail_grade);
            mGradeFactor = (TextView) view.findViewById(R.id.subjectdetail_factor);

            mEditGrade = (Button) view.findViewById(R.id.subjectdetail_editgrade);
            mDeleteGrade = (Button) view.findViewById(R.id.subjectdetail_deletegrade);

        }

        public void bindGrade(Grade grade) {
            mGradeName.setText(grade.getName());
            mGradeDate.setText(mContext.getResources().getString(R.string.date_from) + grade.getDate());
            mGradeMark.setText(String.valueOf(grade.getGrade()));
            mGradeFactor.setText(String.valueOf(grade.getFactor()));

            mEditGrade.setOnClickListener(this);
            mDeleteGrade.setOnClickListener(this);
        }





        @Override
        public void onClick(View v) {
            mGradeClickListener.onGradeItemClick(getPosition(), v);
        }
    }


}
