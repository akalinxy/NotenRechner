package com.linxy.gradeorganizer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;
import com.linxy.gradeorganizer.fragments.CalendarFragment;
import com.linxy.gradeorganizer.fragments.PreferenceFragment;
import com.linxy.gradeorganizer.fragments.ShopFragment;
import com.linxy.gradeorganizer.fragments.SubjectsFragment;
import com.linxy.gradeorganizer.tabs.SlidingTabLayout;

import java.util.List;

enum SearchBarVisible {
    SEARCH_BAR_VISIBLE, SEARCH_BAR_INVISIBLE;
}


public class StartupActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {

    /* Instance Variables*/
    private SearchBarVisible SEARCH_VISIBILITY = SearchBarVisible.SEARCH_BAR_INVISIBLE;
    public static final String PREFS = "PrefFile";
    private CharSequence Titles[] = {"Übersicht", "Verlauf"};
    private int Numboftabs = 2;

    /* View Components */
    private SearchView searchView;
    private ViewPager pager;
    private MenuItem searchItem;
    private ControllableAppBarLayout appBarLayout;
    private ViewPageAdapter adapter;
    private SlidingTabLayout tabs;
    private Toolbar toolbar;
    private FloatingActionButton fabAddSubject;
    private FrameLayout container;

    /* Database Helpers*/
    private DatabaseHelperSubjects dbs;

    /* Navigation Drawer */
    String TITLES[] = new String[5];
    int ICONS[] = {
            R.drawable.icon_home,
            R.drawable.icon_calendar,
            R.drawable.icon_librarybooks,
            R.drawable.icon_cart,
            R.drawable.icon_setting
    };

    private String NAME = "3F";
    private String EMAIL = "Semester 1";
    int PROFILE = R.drawable.testshot;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DrawerLayout Drawer;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Create the Toolbar and set it as the toolbar
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        container = (FrameLayout) findViewById(R.id.fragment_container);
        container.setVisibility(View.GONE);


        TITLES[0] = getResources().getString(R.string.iconHome);
        TITLES[1] = getResources().getString(R.string.iconCalendar);
        TITLES[2] = getResources().getString(R.string.iconSubjects);
        TITLES[3] = getResources().getString(R.string.iconShoppingcart);
        TITLES[4] = getResources().getString(R.string.iconSettings);

        dbs = new DatabaseHelperSubjects(this);
        appBarLayout = (ControllableAppBarLayout) findViewById(R.id.appBarLayout);
        fabAddSubject = (FloatingActionButton) findViewById(R.id.fab);
        fabAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Cursor subjectCursor = dbs.getAllData();
                if (subjectCursor.getCount() == 0) {
                    Toast.makeText(StartupActivity.this, getResources().getString(R.string.needSubjects), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(StartupActivity.this, NewGradeActivity.class);
                    startActivity(intent);
                }

                dbs.close();
                subjectCursor.close();

            }
        });


        adapter = new ViewPageAdapter(getSupportFragmentManager(), Titles, Numboftabs);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(adapter);

        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);

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
                        fabAddSubject.setVisibility(View.VISIBLE);
                        SEARCH_VISIBILITY = SearchBarVisible.SEARCH_BAR_INVISIBLE;
                        invalidateOptionsMenu();
                        break;
                    case 1: /* History */
                        appBarLayout.expandToolbar(true);
                        fabAddSubject.setVisibility(View.GONE);
                        searchItem.setVisible(true);
                        SEARCH_VISIBILITY = SearchBarVisible.SEARCH_BAR_VISIBLE;
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


        mRecyclerView = (RecyclerView) findViewById(R.id.drawer_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new MyAdapter(TITLES, ICONS, NAME, EMAIL, PROFILE, this);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        final GestureDetector mGestureDetector = new GestureDetector(StartupActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mGestureDetector.onTouchEvent(e)) {
                    Drawer.closeDrawers();
                    switch (rv.getChildPosition(child)) {
                        case 1:
                            /* Do Nothing */

                            if(pager.getVisibility() == View.GONE) {
                                comeHome();
                                setToolbarTitle(getResources().getString(R.string.gradecalculator));
                            } else {
                                Toast.makeText(StartupActivity.this, "In Fragment Already!", Toast.LENGTH_SHORT).show();
                            }

                            break;
                        case 2:



                            if(!(pager.getVisibility() == View.GONE)) {
                                leaveHome();
                            }

                            if(getSupportFragmentManager().findFragmentByTag("calefrag") instanceof CalendarFragment)
                            {
                                Toast.makeText(StartupActivity.this, "In Fragment Already!", Toast.LENGTH_SHORT).show();

                            } else  {
                                setToolbarTitle(getResources().getString(R.string.grade_calendar));
                                Fragment frag = new CalendarFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag, "calefrag").commit();
                            }



                            break;
                        case 3:



                            if(!(pager.getVisibility() == View.GONE)) {
                                leaveHome();
                            }

                            if(getSupportFragmentManager().findFragmentByTag("subjfrag") instanceof SubjectsFragment)
                            {
                                Toast.makeText(StartupActivity.this, "In Fragment Already!", Toast.LENGTH_SHORT).show();

                            } else  {
                                setToolbarTitle(getResources().getString(R.string.subjects));
                                Fragment frag = new SubjectsFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag, "subjfrag").commit();
                            }


                            break;
                        case 4:



                            if(!(pager.getVisibility() == View.GONE)) {
                                leaveHome();
                            }

                            if(getSupportFragmentManager().findFragmentByTag("shopfrag") instanceof ShopFragment)
                            {
                                Toast.makeText(StartupActivity.this, "In Fragment Already!", Toast.LENGTH_SHORT).show();

                            } else  {
                                setToolbarTitle(getResources().getString(R.string.shop));
                                Fragment frag = new ShopFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag, "shopfrag").commit();
                            }

                            break;
                        case 5:

                            if(!(pager.getVisibility() == View.GONE)) {
                                leaveHome();
                            }

                            if(getSupportFragmentManager().findFragmentByTag("preffrag") instanceof PreferenceFragment)
                            {
                                Toast.makeText(StartupActivity.this, "In Fragment Already!", Toast.LENGTH_SHORT).show();

                            } else  {
                                setToolbarTitle(getResources().getString(R.string.settings));
                                Fragment frag = new PreferenceFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag, "preffrag").commit();
                            }



                            break;
                    }


                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

        Drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        Drawer.setStatusBarBackgroundColor(getResources().getColor(R.color.ColorPrimaryDark));
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.yes, R.string.no) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        Drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }

    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = StartupActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for(Fragment fragment : fragments){
            if(fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
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
        }

        searchView.setSearchableInfo(searchManger.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    private void leaveHome() {
        fabAddSubject.setVisibility(View.GONE);
        tabs.setVisibility(View.GONE);
        pager.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
        appBarLayout.expandToolbar(false);
    }

    private void comeHome() {
        fabAddSubject.setVisibility(View.VISIBLE);
        tabs.setVisibility(View.VISIBLE);
        pager.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);
    }

    private void setToolbarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onStop() {
        super.onStop();
        dbs.close();
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
