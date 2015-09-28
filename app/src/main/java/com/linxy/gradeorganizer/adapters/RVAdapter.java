package com.linxy.gradeorganizer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

    public interface MyLongClickListener {
        public void onLongItemClick(int position, View v);
    }

    public static MyClickListener myClickListener;
//    public static MyLongClickListener myLongClickListener;
    private Context mContext;

    ArrayList<Subject> subjects;
    private SparseBooleanArray selectedItems;

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public Subject getItem(int position) {
        return subjects.get(position);
    }

    public void removeData(int position) {
        subjects.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, position);
    }

    public RVAdapter(Context context, ArrayList<Subject> subjects) {
        this.subjects = subjects;
        mContext = context;
        selectedItems = new SparseBooleanArray();
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

//    public void setOnLongItemClickListener(MyLongClickListener myLongClickListener) {
//        this.myLongClickListener = myLongClickListener;
//    }

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
        subjectViewHolder.mSubjecButton.setActivated(selectedItems.get(i, false));

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class SubjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mSubjectName;
        ImageButton mSubjectInfo;
        Button mSubjecButton;

        SubjectViewHolder(View v) {
            super(v);
            mSubjectName = (TextView) v.findViewById(R.id.textview_subject_name);
            mSubjectInfo = (ImageButton) v.findViewById(R.id.button_subject_info);
            mSubjecButton = (Button) v.findViewById(R.id.subjectlist_button);
//            mSubjecButton.setOnLongClickListener(this);
            mSubjectInfo.setOnClickListener(this);
        }

//        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }
//
//        @Override
//        public boolean onLongClick(View v) {
//            myLongClickListener.onLongItemClick(getPosition(), v);
//            return true;
//        }
    }
}
