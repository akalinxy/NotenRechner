package com.linxy.gradeorganizer.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.github.machinarius.preferencefragment.PreferenceManagerCompat;
import com.linxy.gradeorganizer.R;

/**
 * Created by Linxy on 22/9/2015 at 20:39
 * Working on Grade Organizer in com.linxy.gradeorganizer.fragments
 */
public class SettingsFragment extends com.github.machinarius.preferencefragment.PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }


}
