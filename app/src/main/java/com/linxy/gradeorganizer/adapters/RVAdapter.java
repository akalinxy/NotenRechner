package com.linxy.gradeorganizer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.linxy.gradeorganizer.fragments.SubjectsFragment;
import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.objects.Subject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Linxy on 30/7/2015 at 15:34
 * Working on Grade Organizer in com.linxy.gradeorganizer.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.SubjectViewHolder> {

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    public static MyClickListener myClickListener;
    ArrayList<Subject> subjects;
    private Context mContext;

    public RVAdapter(Context context, ArrayList<Subject> subjects) {
        this.subjects = subjects;
        mContext = context;
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }


    @Override
    public SubjectViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        SubjectViewHolder svh = new SubjectViewHolder(v);
        return svh;
    }

    @Override
    public void onBindViewHolder(SubjectViewHolder subjectViewHolder, int i) {
        subjectViewHolder.mSubjectName.setText(subjects.get(i).getName());
        Drawable iconInfo = mContext.getResources().getDrawable(R.drawable.icon_info);
        iconInfo.setColorFilter(mContext.getResources().getColor(R.color.color_gray_verymedium), PorterDuff.Mode.SRC_ATOP);
        subjectViewHolder.mSubjectInfo.setImageDrawable(iconInfo);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
}


public static class SubjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView mSubjectName;
    ImageButton mSubjectInfo;

    SubjectViewHolder(View v) {
        super(v);
        mSubjectName = (TextView) v.findViewById(R.id.textview_subject_name);
        mSubjectInfo = (ImageButton) v.findViewById(R.id.button_subject_info);
        mSubjectInfo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        myClickListener.onItemClick(getPosition(), v);
    }
}
}
