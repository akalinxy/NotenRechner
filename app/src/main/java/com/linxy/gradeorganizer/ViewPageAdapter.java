package com.linxy.gradeorganizer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.View;

import com.linxy.gradeorganizer.fragments.Tab1;
import com.linxy.gradeorganizer.fragments.Tab2;

/**
 * Created by linxy on 7/26/15.
 */
public class ViewPageAdapter extends FragmentStatePagerAdapter{

    CharSequence Titles[];
    int NumbOfTabs;
    SparseArray<View> views = new SparseArray<>();

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



    @Override
    public Object instantiateItem(View container, int position){
        container.setTag(position);
        return container;
    }

}
