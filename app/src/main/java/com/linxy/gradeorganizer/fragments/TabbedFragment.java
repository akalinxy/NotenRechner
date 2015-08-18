package com.linxy.gradeorganizer.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.adapters.ViewPageAdapter;

/**
 * Created by Linxy on 14/8/2015 at 21:55
 * Working on Grade Organizer in com.linxy.gradeorganizer.fragments
 */
public class TabbedFragment extends Fragment {

    /* Constants */
    public static final String TAG = TabbedFragment.class.getSimpleName();

    /* View Components */
    private ViewPager mViewPager;

    /* Member Variables */
    private Activity mActivity;
    private ViewPageAdapter mPagerAdapter;
    private TabLayout tabLayout;

    public static TabbedFragment getInstance(){
        return new TabbedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View v = layoutInflater.inflate(R.layout.fragment_tabbed, viewGroup, false);

        mPagerAdapter = new ViewPageAdapter(getChildFragmentManager());
        mViewPager = (ViewPager) v.findViewById(R.id.viewpager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(onPageChangeListener);
        mViewPager.setOffscreenPageLimit(2);
        tabLayout = (TabLayout) getActivity().findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);

        /* Debug */

        return v;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mActivity = activity;
    }

//    public void refreshFragment(){
//        Fragment testFrag = (Tab1) mPagerAdapter.getCurrentFragment();
//        Log.i(TAG, "TestFrag Assigned" + testFrag.getTag());
//        ((Tab1) testFrag).refreshData();
//    }

    public void refreshFragment(){
        Fragment frag = (Tab1) mPagerAdapter.getRegisteredFragment(0);
        ((Tab1) frag).refreshData();
    }

    final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            ((OnTabChange) mActivity).onTabChange();
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    };

    public interface OnTabChange {
         void onTabChange();
    }

}
