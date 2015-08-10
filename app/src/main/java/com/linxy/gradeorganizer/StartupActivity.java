package com.linxy.gradeorganizer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
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
import com.linxy.gradeorganizer.fragments.Tab2;
import com.linxy.gradeorganizer.tabs.SlidingTabLayout;
import com.linxy.gradeorganizer.tabs.ViewPagerContainer;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import java.util.List;

enum SearchBarVisible {
    SEARCH_BAR_VISIBLE, SEARCH_BAR_INVISIBLE;
}


public class StartupActivity extends ActionBarActivity implements SearchView.OnQueryTextListener, ViewPagerContainer.OnDataPass {

    /* Instance Variables*/
    private SearchBarVisible SEARCH_VISIBILITY = SearchBarVisible.SEARCH_BAR_INVISIBLE;
    public static final String PREFS = "PrefFile";
    private boolean toolbarScroll;

    /* View Components */
    private SearchView searchView;
    private MenuItem searchItem;
    private ControllableAppBarLayout appBarLayout;
    private Toolbar toolbar;
    private FloatingActionButton fabAddSubject;
    private FrameLayout container;
    private ViewPager viewPager;
    private TabLayout tabLayout;


    /* Database Helpers*/
    private DatabaseHelperSubjects dbs;

    /* Navigation Drawer */


    private String NAME = "Notenrechner Basic";
    private String EMAIL = "Deine Leistung";


    private NavigationView mNavigationView;
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
        toolbarScroll = true;


        container = (FrameLayout) findViewById(R.id.fragment_container);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewPagerContainer()).commit();


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

        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        mNavigationView.setNavigationItemSelectedListener(navigationListener);


        mDrawerToggle.syncState();

    }

//    @Override
//    public void onOptionItemsSelected(MenuItem item){
//        switch (item.getItemId()){
//        }
//    }


    final NavigationView.OnNavigationItemSelectedListener navigationListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            Fragment f = null;
            switch (menuItem.getItemId()) {
                case R.id.navitem_gradeaverage:

                    f = getSupportFragmentManager().findFragmentByTag("viewpagerfrag");
                    if (f != null && f instanceof ViewPagerContainer) {
                        Toast.makeText(StartupActivity.this, "Fragment NOT Changed", Toast.LENGTH_SHORT).show();
                    } else {
                        Drawer.closeDrawers();
                        toolbarScroll = true;
                        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
                        toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                        toolbar.setLayoutParams(toolbarLayoutParams);
                        Toast.makeText(StartupActivity.this, "Fragment CHANGED", Toast.LENGTH_SHORT).show();
                        tabLayout.setVisibility(View.VISIBLE);
                        setToolbarTitle(getResources().getString(R.string.app_name));
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewPagerContainer(), "viewpagerfrag").commit();
                        fabAddSubject.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.navitem_gradecalendar:


                    f = getSupportFragmentManager().findFragmentByTag("calefrag");
                    if (f != null && f instanceof CalendarFragment) {
                        Toast.makeText(StartupActivity.this, "Fragment NOT Changed", Toast.LENGTH_SHORT).show();
                    } else {
                        Drawer.closeDrawers();
                        if (toolbarScroll) {
                            ControllableAppBarLayout.LayoutParams toolbarLayoutParams = (ControllableAppBarLayout.LayoutParams) toolbar.getLayoutParams();
                            toolbarLayoutParams.setScrollFlags(0);
                            toolbar.setLayoutParams(toolbarLayoutParams);
                            toolbarScroll = false;
                        }
                        appBarLayout.expandToolbar();
                        Toast.makeText(StartupActivity.this, "Fragment CHANGED", Toast.LENGTH_SHORT).show();
                        tabLayout.setVisibility(View.GONE);
                        setToolbarTitle(getResources().getString(R.string.grade_calendar));
                        fabAddSubject.setVisibility(View.GONE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CalendarFragment(), "calefrag").commit();
                    }
                    break;
                case R.id.navitem_subjects:
                    f = getSupportFragmentManager().findFragmentByTag("subjfrag");
                    if (f != null && f instanceof SubjectsFragment) {
                        Toast.makeText(StartupActivity.this, "Fragment NOT Changed", Toast.LENGTH_SHORT).show();
                    } else {
                        Drawer.closeDrawers();
                        if (toolbarScroll) {
                            ControllableAppBarLayout.LayoutParams toolbarLayoutParams = (ControllableAppBarLayout.LayoutParams) toolbar.getLayoutParams();
                            toolbarLayoutParams.setScrollFlags(0);
                            toolbar.setLayoutParams(toolbarLayoutParams);
                            toolbarScroll = false;
                        }
                        appBarLayout.expandToolbar();
                        tabLayout.setVisibility(View.GONE);
                        Toast.makeText(StartupActivity.this, "Fragment CHANGED", Toast.LENGTH_SHORT).show();
                        fabAddSubject.setVisibility(View.GONE);
                        setToolbarTitle(getResources().getString(R.string.subjects));
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SubjectsFragment(), "subjfrag").commit();
                    }
                    break;
                case R.id.navitem_shop:

                    f = getSupportFragmentManager().findFragmentByTag("shopfrag");
                    if (f != null && f instanceof ShopFragment) {
                        Toast.makeText(StartupActivity.this, "Fragment NOT Changed", Toast.LENGTH_SHORT).show();
                    } else {
                        Drawer.closeDrawers();
                        if (toolbarScroll) {
                            ControllableAppBarLayout.LayoutParams toolbarLayoutParams = (ControllableAppBarLayout.LayoutParams) toolbar.getLayoutParams();
                            toolbarLayoutParams.setScrollFlags(0);
                            toolbar.setLayoutParams(toolbarLayoutParams);
                            toolbarScroll = false;
                        }
                        appBarLayout.expandToolbar();
                        tabLayout.setVisibility(View.GONE);
                        Toast.makeText(StartupActivity.this, "Fragment CHANGED", Toast.LENGTH_SHORT).show();
                        fabAddSubject.setVisibility(View.GONE);
                        setToolbarTitle(getResources().getString(R.string.shop));
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ShopFragment(), "shopfrag").commit();
                    }
                    break;
                case R.id.navitem_settings:

                    f = getSupportFragmentManager().findFragmentByTag("preffrag");
                    if (f != null && f instanceof PreferenceFragment) {
                        Toast.makeText(StartupActivity.this, "Fragment NOT Changed", Toast.LENGTH_SHORT).show();
                    } else {
                        Drawer.closeDrawers();
                        if (toolbarScroll) {
                            ControllableAppBarLayout.LayoutParams toolbarLayoutParams = (ControllableAppBarLayout.LayoutParams) toolbar.getLayoutParams();
                            toolbarLayoutParams.setScrollFlags(0);
                            toolbar.setLayoutParams(toolbarLayoutParams);
                            toolbarScroll = false;
                        }
                        appBarLayout.expandToolbar();
                        tabLayout.setVisibility(View.GONE);
                        Toast.makeText(StartupActivity.this, "Fragment CHANGED", Toast.LENGTH_SHORT).show();
                        fabAddSubject.setVisibility(View.GONE);
                        setToolbarTitle(getResources().getString(R.string.settings));
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PreferenceFragment(), "preffrag").commit();
                    }
                    break;
            }
            return false;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_startup, menu);
        searchItem = menu.findItem(R.id.toolbar_search);
        SearchManager searchManger = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        switch (SEARCH_VISIBILITY) {
            case SEARCH_BAR_VISIBLE:
                searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
                searchItem.setVisible(true);
                searchView.setSearchableInfo(searchManger.getSearchableInfo(getComponentName()));
                searchView.setIconifiedByDefault(true);
                searchView.setOnQueryTextListener(this);
                break;
            case SEARCH_BAR_INVISIBLE:
                searchItem.setVisible(false);
                break;
        }


        return true;
    }

    private void setToolbarTitle(String title) {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onDataPass(ViewPager viewPager, TabLayout tabLayout) {
        this.viewPager = viewPager;
        this.tabLayout = tabLayout;
//        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getText().toString().equals("Verlauf")) {
                    appBarLayout.expandToolbar(true);
                    fabAddSubject.setVisibility(View.GONE);
                    searchItem.setVisible(true);
                    SEARCH_VISIBILITY = SearchBarVisible.SEARCH_BAR_VISIBLE;
                    invalidateOptionsMenu();
                } else {
                    appBarLayout.expandToolbar(true);
                    fabAddSubject.setVisibility(View.VISIBLE);
                    SEARCH_VISIBILITY = SearchBarVisible.SEARCH_BAR_INVISIBLE;
                    invalidateOptionsMenu();


                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }
}
