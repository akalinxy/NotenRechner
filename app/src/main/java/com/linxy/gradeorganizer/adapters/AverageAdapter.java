package com.linxy.gradeorganizer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.objects.Grade;
import com.linxy.gradeorganizer.objects.SubjectGrade;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Linxy on 22/9/2015 at 19:36
 * Working on Grade Organizer in com.linxy.gradeorganizer.adapters
 */
public class AverageAdapter extends RecyclerView.Adapter<AverageAdapter.AverageViewHolder> {
    private List<SubjectGrade> mGrades;

    public interface MySubjectDetailClickListener {
        public abstract void onClick(int position, View view);
    }
    public MySubjectDetailClickListener mClickListener;

    public void setOnClickListener(MySubjectDetailClickListener listener) {
        mClickListener = listener;
    }

    public AverageAdapter(List<SubjectGrade> grades) {
        mGrades = grades;
    }

    @Override
    public AverageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // This gets called whenever a new viewholder is needed, because views get recycled.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_subject_average, parent, false);
        AverageViewHolder viewHolder = new AverageViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AverageViewHolder holder, int position) {
        holder.bindGrade(mGrades.get(position));
    }

    @Override
    public int getItemCount() {
        return mGrades.size();
    }

    public class AverageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public RelativeLayout mLayout;
        public TextView mSubjectName;
        public TextView mSubjectAverage;
        public ImageView mAverageIcon;

        public AverageViewHolder(View itemView) {
            super(itemView);

            mLayout = (RelativeLayout) itemView.findViewById(R.id.relativelayout_subjectaverage_layout);
            mSubjectName = (TextView) itemView.findViewById(R.id.textview_subjectaverage_name);
            mSubjectAverage = (TextView) itemView.findViewById(R.id.textview_subjectaverage_grade);
            mAverageIcon = (ImageView) itemView.findViewById(R.id.imageview_subjectaverage_icon);
            mLayout.setOnClickListener(this);
        }

        public void bindGrade(SubjectGrade grade) {
            mSubjectName.setText(grade.getName());
            if(grade.getGrade() == -1) {
                mSubjectAverage.setText("");
            } else {
                mSubjectAverage.setText(String.format("%.2f",grade.getGrade())); // TODO Format This
            }
            mAverageIcon.setImageResource(grade.getIconId());
        }

        @Override
        public void onClick(View v) {
            mClickListener.onClick(getPosition(), v);
        }
    }
}
