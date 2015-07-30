package com.linxy.gradeorganizer;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import javax.security.auth.Subject;

/**
 * Created by Linxy on 30/7/2015 at 15:34
 * Working on Grade Organizer in com.linxy.gradeorganizer.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.SubjectViewHolder>{
    public static MyClickListener myClickListener;

    public static class SubjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        TextView TVsubjectName;
        Button BTNsubjectFactor;
        ImageButton BTNsubjectDelete;

        SubjectViewHolder(View v) {
            super(v);
            cv = (CardView) v.findViewById(R.id.cv_cardview);
            TVsubjectName = (TextView) v.findViewById(R.id.cv_subject_name);
            BTNsubjectFactor = (Button) v.findViewById(R.id.cv_subject_factor);
            BTNsubjectDelete = (ImageButton) v.findViewById(R.id.cv_subject_delete);
            BTNsubjectFactor.setOnClickListener(this);
            BTNsubjectDelete.setOnClickListener(this);


           // v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            myClickListener.onItemClick(getPosition(), v);
        }


    }

    List<EditSubjectsActivity.Subject> subjects;

    RVAdapter(List<EditSubjectsActivity.Subject> subjects){
        this.subjects = subjects;
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public int getItemCount(){
        return subjects.size();
    }


    @Override
    public SubjectViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        SubjectViewHolder svh  = new SubjectViewHolder(v);
        return svh;
    }

    @Override
    public void onBindViewHolder(SubjectViewHolder subjectViewHolder, int i){
        subjectViewHolder.TVsubjectName.setText(subjects.get(i).subjectname);
        subjectViewHolder.BTNsubjectFactor.setText(subjects.get(i).subjectfactor);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

        // functional interface..
    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }




}
