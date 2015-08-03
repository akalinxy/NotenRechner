package com.linxy.gradeorganizer;

import android.app.SearchManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;
import com.linxy.gradeorganizer.tabs.SlidingTabLayout;

enum SearchBarVisible {
    SEARCH_BAR_VISIBLE, SEARCH_BAR_INVISIBLE;
}


public class StartupActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {

    public Toolbar toolbar;
    private SearchBarVisible SEARCH_VISIBILITY = SearchBarVisible.SEARCH_BAR_INVISIBLE;

    MenuItem searchItem;

    ViewPager pager;
    ViewPageAdapter adapter;
    SlidingTabLayout tabs;


    CharSequence Titles[] = {"Ubersicht", "Verlauf", "Bearbeiten"};
    int Numboftabs = 3;

    public static final String PREFS = "PrefFile";
    public static int tHeigt;
    FloatingActionButton test;

    DatabaseHelperSubjects dbs;


    private SearchView searchView;
    private ListView listView;
    private ArrayAdapter arrayAdapter;


    // Components


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        dbs = new DatabaseHelperSubjects(this);
        setContentView(R.layout.activity_startup);
        test = (FloatingActionButton) findViewById(R.id.fab);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Cursor subjectCursor = dbs.getAllData();
                if(subjectCursor.getCount()  == 0){
                    Toast.makeText(StartupActivity.this, getResources().getString(R.string.needSubjects), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(StartupActivity.this, NewGradeActivity.class);
                    startActivity(intent);
                }

                dbs.close();
                subjectCursor.close();

            }
        });


        // Create the Toolbar and set it as the toolbar
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        tHeigt = getSupportActionBar().getHeight();


        // Create the ViewPageAdapter and passing it Fragment manager
        adapter = new ViewPageAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        // Assigning Viewpager view and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(adapter);


        // Assigning the Sliding tab layout view
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);

        // Setting custom Color for Scroll bar indicator of the tabs
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.ColorYellow);
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

                switch (position) {
                    case 0: /* Overview */
                        test.setVisibility(View.VISIBLE);
                        SEARCH_VISIBILITY = SearchBarVisible.SEARCH_BAR_INVISIBLE;
                        invalidateOptionsMenu();
//                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);


                        break;
                    case 1: /* History */
                        test.setVisibility(View.VISIBLE);
                        searchItem.setVisible(true);
                        SEARCH_VISIBILITY = SearchBarVisible.SEARCH_BAR_VISIBLE;
                        invalidateOptionsMenu();


                        break;
                    case 2: /* Settings */
                        test.setVisibility(View.INVISIBLE);

                        searchItem.setVisible(false);
                        SEARCH_VISIBILITY = SearchBarVisible.SEARCH_BAR_INVISIBLE;
                        invalidateOptionsMenu();


                        break;
                    default: /* This doesnt Happen*/
                        break;

                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        String testArr[] = getResources().getStringArray(R.array.test_subjects);
        //   listView = new ListView(getBaseContext());


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_startup, menu);


        searchItem = menu.findItem(R.id.toolbar_search);
        SearchManager searchManger = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        switch (SEARCH_VISIBILITY) {
            case SEARCH_BAR_VISIBLE:
                searchItem.setVisible(true);
                break;
            case SEARCH_BAR_INVISIBLE:
                searchItem.setVisible(false);
                break;
            default:
        }

        searchView.setSearchableInfo(searchManger.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(this);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.toolbar_settings) {

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
