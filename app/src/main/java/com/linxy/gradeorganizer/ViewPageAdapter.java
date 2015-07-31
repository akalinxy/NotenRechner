package com.linxy.gradeorganizer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by linxy on 7/26/15.
 */
public class ViewPageAdapter extends FragmentStatePagerAdapter{

    CharSequence Titles[];
    int NumbOfTabs;

    public ViewPageAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb){
        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;



    }


    @Override
    public Fragment getItem(int position){

        switch (position) {
            case 0:
                Tab1 tab1 = new Tab1();
                return tab1;
            case 1:
                Tab2 tab2 = new Tab2();
                return tab2;
            case 2:
                Tab3 tab3 = new Tab3();
                return tab3;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position){
        return Titles[position];
    }

    @Override
    public int getCount(){
        return NumbOfTabs;
    }

}
