package com.linxy.gradeorganizer.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.linxy.gradeorganizer.fragments.Tab1;
import com.linxy.gradeorganizer.fragments.Tab2;

import java.util.Objects;

/**
 * Created by linxy on 7/26/15.
 */
public class ViewPageAdapter extends FragmentPagerAdapter{

    public static int ITEM_COUNT = 2;
    SparseArray<Fragment> registeredFragments = new SparseArray<>();
    private Fragment mCurrentFragment;

    public ViewPageAdapter (FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @Override
    public int getCount(){
        return ITEM_COUNT;
    }

    @Override
    public Fragment getItem(int position){
        switch (position) {
            case 0: // Tab1
                return Tab1.getInstance()   ;
            case 1:
                return Tab2.getInstance();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position){
        switch (position) {
            case 0:
                return "Durchschnitt";
            case 1:
                return "Verlauf";
        }
        return null;
    }

    public Fragment getCurrentFragment(){
        return mCurrentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if(getCurrentFragment() != object){
            mCurrentFragment = (Fragment) object;
        }
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position){
        return registeredFragments.get(position);
    }
}
