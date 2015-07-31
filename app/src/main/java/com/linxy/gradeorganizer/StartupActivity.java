package com.linxy.gradeorganizer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;
import com.linxy.gradeorganizer.tabs.SlidingTabLayout;


public class StartupActivity extends ActionBarActivity {

    private Toolbar toolbar;
    ViewPager pager;
    ViewPageAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Ubersicht", "Verlauf", "Bearbeiten"};
    int Numboftabs = 3;

    DatabaseHelperSubjects myDBSubjects;
    DatabaseHelper myDBGrades;
    public static final String PREFS = "PrefFile";
    public static int tHeigt;
    FloatingActionButton test;


    // Components


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        test = (FloatingActionButton) findViewById(R.id.fab);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartupActivity.this, NewGradeActivity.class);
                startActivity(intent);

            }
        });
        // Create the Toolbar and set it as the toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        tHeigt = getSupportActionBar().getHeight();

        // Create the ViewPageAdapter and passing it Fragment manager
        adapter = new ViewPageAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        // Assigning Viewpager view and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);


        // Assigning the Sliding tab layout view
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);

        // Setting custom Color for Scroll bar indicator of the tabs
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrolColor);
            }
        });

        // Setting the ViewPager for the slidingTabsLayout
        tabs.setViewPager(pager);



        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                if (position == 2) {
                    test.setVisibility(View.INVISIBLE);

                } else {
                    test.setVisibility(View.VISIBLE);

                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        myDBGrades = new DatabaseHelper(this);
        myDBSubjects = new DatabaseHelperSubjects(this);
    }

    // Remove this method #TODO
    public void AddSubjectToDatabase(String subjectName, int factor) {
        if (!myDBSubjects.hasObject(subjectName))
            myDBSubjects.insertData(subjectName, String.valueOf(factor));
        else Toast.makeText(this, "EXISTS!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_startup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        myDBSubjects.close();
        myDBGrades.close();
    }


}
