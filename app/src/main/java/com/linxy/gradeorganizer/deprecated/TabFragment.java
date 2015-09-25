//package com.linxy.gradeorganizer.fragments;
//
//import android.os.Bundle;
//import android.support.design.widget.TabLayout;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.linxy.gradeorganizer.R;
//
///**
// * Created by Linxy on 13/9/2015 at 17:18
// * Working on Grade Organizer in com.linxy.gradeorganizer.fragments
// */
//public class TabFragment extends Fragment {
//    public static final String TAG = TabFragment.class.getSimpleName();
//
//    public static TabLayout tabLayout;
//    public static ViewPager viewPager;
//
//    public static int item_count = 2;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        // Inflate tab_layout and setup views
//        View rootView = inflater.inflate(R.layout.tab_layout, null);
//        tabLayout = (TabLayout) rootView.findViewById(R.id.tablayout);
//        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
//
//        // Set an adapter for the ViewPager
//        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
//
//        // Setup the tablayout with the viewpager
//        tabLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                tabLayout.setupWithViewPager(viewPager);
//            }
//        });
//
//        return rootView;
//    }
//
//    public class MyAdapter extends FragmentPagerAdapter {
//
//        public MyAdapter(FragmentManager fm){
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            switch (position) {
//                case 0:
//                    return Tab1.getInstance();
//                case 1:
//                    return Tab2.getInstance();
//            }
//            return null;
//        }
//
//        @Override
//        public int getCount(){
//            return item_count;
//        }
//
//        // TODO: Make these string resources with context.
//        @Override
//        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return "Durschnitt";
//                case 1:
//                    return "Verlauf";
//            }
//            return null;
//        }
//    }
//}
//
