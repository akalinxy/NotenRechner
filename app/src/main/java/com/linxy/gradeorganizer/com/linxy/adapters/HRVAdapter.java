package com.linxy.gradeorganizer.com.linxy.adapters;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.linxy.gradeorganizer.EditSubjectsActivity;
import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.StartupActivity;
import com.linxy.gradeorganizer.Tab2;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Linxy on 31/7/2015 at 10:10
 * Working on Grade Organizer in com.linxy.gradeorganizer.com.linxy.adapters
 */
public class HRVAdapter extends RecyclerView.Adapter<HRVAdapter.GradeViewHolder>{
    public static MyHisClickListener myHisClickListener;

    public static class GradeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        TextView tvSubName;
        TextView tvGrade;
        ImageButton btnMoreInfo;

        public GradeViewHolder(View v) {
            super(v);
            cv = (CardView) v.findViewById(R.id.cv_gh_cardview);
            tvSubName = (TextView) v.findViewById(R.id.cvgh_subjectname);
            tvGrade = (TextView) v.findViewById(R.id.cvghtv_grade);
            btnMoreInfo = (ImageButton) v.findViewById(R.id.cvgh_moreinfo);

            btnMoreInfo.setOnClickListener(this);


            // v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            myHisClickListener.onItemClick(getPosition(), v);
        }


    }

    List<Tab2.Grade> grades;

    public HRVAdapter(List<Tab2.Grade> grades){
        this.grades = grades;
    }

    public void setOnItemClickListener(MyHisClickListener myHisClickListener) {
        this.myHisClickListener = myHisClickListener;
    }

    @Override
    public int getItemCount(){
        return grades.size();
    }


    @Override
    public GradeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_past_grades, viewGroup, false);
        GradeViewHolder gvh  = new GradeViewHolder(v);
        return gvh;
    }

    @Override
    public void onViewAttachedToWindow(GradeViewHolder gradeViewHolder){
        super.onViewAttachedToWindow(gradeViewHolder);
                if(Double.parseDouble(gradeViewHolder.tvGrade.getText().toString()) < 4.0) {
            gradeViewHolder.tvGrade.setTextColor(Color.parseColor("#F44336"));
        } else {
            gradeViewHolder.tvGrade.setTextColor(Color.parseColor("#4CAF50"));
        }
    }

    @Override
    public void onBindViewHolder(GradeViewHolder gradeViewHolder, int i){
        gradeViewHolder.tvSubName.setText(grades.get(i).gradeName);

        gradeViewHolder.tvGrade.setText(grades.get(i).grade);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
     //   grades

        super.onAttachedToRecyclerView(recyclerView);
    }

    // functional interface..
    public interface MyHisClickListener {
        public void onItemClick(int position, View v);
    }




}