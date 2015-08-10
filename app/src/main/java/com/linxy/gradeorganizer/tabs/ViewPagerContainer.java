package com.linxy.gradeorganizer.tabs;

import android.app.Activity;
import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.ViewPageAdapter;


public class ViewPagerContainer extends Fragment {

    private ViewPager mViewPager;
    private ViewPageAdapter adapter;
    private CharSequence Titles[] = {"Übersicht", "Verlauf"};
    private int Numboftabs = 2;
    private SlidingTabLayout tabs;
    OnDataPass dataPasser;

    TabLayout tabLayout;

    public ViewPagerContainer(){}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_pager_container, container, false);

        tabLayout = (TabLayout) getActivity().findViewById(R.id.tabLayout);
        //tabLayout.addTab(tabLayout.newTab().setText("Overview"));
      //  tabLayout.addTab(tabLayout.newTab().setText("History"));
        mViewPager = (ViewPager) v.findViewById(R.id.viewpager);
        adapter = new ViewPageAdapter(getActivity().getSupportFragmentManager(), Titles, Numboftabs);
        mViewPager.setAdapter(adapter);
        passData(mViewPager);
       tabLayout.setupWithViewPager(mViewPager);



        return v;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dataPasser = (OnDataPass) activity;

    }

    public void passData(ViewPager viewPager){
        dataPasser.onDataPass(viewPager, tabLayout);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnDataPass{
        public void onDataPass(ViewPager viewPager, TabLayout tablayout);
    }

}
