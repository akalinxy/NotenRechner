package com.linxy.gradeorganizer.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.kobakei.ratethisapp.RateThisApp;
import com.linxy.gradeorganizer.BuildConfig;
import com.linxy.gradeorganizer.fragments.AverageFragment;
import com.linxy.gradeorganizer.fragments.CalendarFragment;
import com.linxy.gradeorganizer.fragments.SettingsFragment;
import com.linxy.gradeorganizer.fragments.SubjectsFragment;
import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;
import com.linxy.gradeorganizer.objects.Grade;
import com.parse.ParseObject;

// ShopFragment.BuyPremiumButtonClick, BillingProcessor.IBillingHandler
public class StartupActivity extends AppCompatActivity  {

    /* Constants */
    private static final String TAG = StartupActivity.class.getSimpleName();
    public static String mCurrentPageName = "Noten Rechner";
//    private static final String ITEM_SKU = "purchase_premium";
//    public static final String PREFS = "PrefFile";

    /* Instance Variables*/
    public static  String deviceId;
//    public static boolean PREMIUM;



//    /* Database Helpers*/
//    private DatabaseHelperSubjects dbs;
//    private DatabaseHelper db;

//    /* Ads & Billing */
//    BillingProcessor bp;

//    /* Multithreading */
//    private Handler mHandler;


    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        // Custom criteria: 3 days and 5 launches
        RateThisApp.Config config = new RateThisApp.Config(2, 5);
// Custom title and message
        config.setTitle(R.string.rate_title);
        config.setMessage(R.string.rate_message);
        RateThisApp.init(config);


        // Setup the DrawerLayout and NavigationView
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mNavigationView = (NavigationView) findViewById(R.id.navigationview);

        // Inflate the first fragment
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fragment_container, new AverageFragment()).commit();

        // Setup click events on the Navigation View items
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Fragment fragment = null;
                mDrawerLayout.closeDrawers();

//                if(menuItem.getItemId() == R.id.navitem_settings) {
//                    Intent intent = new Intent(StartupActivity.this, PreferenceActivity.class);
//                    StartupActivity.this.startActivity(intent);
//                    return false;
//                }

                if(menuItem.isChecked())
                    menuItem.setChecked(false);
                else
                    menuItem.setChecked(true);

                switch (menuItem.getItemId()){
                    case R.id.navitem_gradeaverage:
                        Log.i(TAG, "navItem gradeaverage");

                        getSupportActionBar().setTitle(getString(R.string.fragmentGradeAverage));
                        mCurrentPageName = getString(R.string.fragmentGradeAverage);
                        FragmentTransaction transaction = mFragmentManager.beginTransaction();
                        transaction.replace(R.id.fragment_container, new AverageFragment(), AverageFragment.TAG).commit();
                        break;
                    case R.id.navitem_gradecalendar:
                        getSupportActionBar().setTitle(getString(R.string.fragmentGradeCalendar));
                        mCurrentPageName = getString(R.string.fragmentGradeCalendar);
                        Log.i(TAG, "navItem greadecalendar");
                        FragmentTransaction xtransaction = mFragmentManager.beginTransaction();
                        xtransaction.replace(R.id.fragment_container, new CalendarFragment(), CalendarFragment.TAG).commit();
                        break;
                    case R.id.navitem_subjects:
                        getSupportActionBar().setTitle(getString(R.string.fragmentSubjects));
                        mCurrentPageName = getString(R.string.fragmentSubjects);
                        FragmentTransaction xxtransaction = mFragmentManager.beginTransaction();
                        xxtransaction.replace(R.id.fragment_container, new SubjectsFragment()).commit();
                        break;

                    case R.id.navitem_settings:
                        getSupportActionBar().setTitle(getString(R.string.fragmentSettings));
                        mCurrentPageName = getString(R.string.fragmentSettings);
                        FragmentTransaction xxxxtransaction = mFragmentManager.beginTransaction();
                        xxxxtransaction.replace(R.id.fragment_container, new SettingsFragment()).commit();
                        break;

                }

                return false;
            }
        });

        // Setup drawer toggle of the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // We can setup different strings for when the drawer is open or closed here.
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.cancel, R.string.cancel){
            @Override
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mCurrentPageName);
            }

            @Override
            public void onDrawerOpened(View view) {
                getSupportActionBar().setTitle("Navigation");
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        deviceId = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);

//        HandlerThread handlerThread = new HandlerThread("Background Thread");
//        handlerThread.start();

        mNavigationView.getMenu().getItem(0).setChecked(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        RateThisApp.onStart(this);
        if(BuildConfig.DEBUG){
            RateThisApp.showRateDialog(this);
        } else {
            RateThisApp.showRateDialogIfNeeded(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            super.onActivityResult(requestCode, resultCode, intent);
    }


    @Override
    public void onStop() {
        super.onStop();


    }

}
