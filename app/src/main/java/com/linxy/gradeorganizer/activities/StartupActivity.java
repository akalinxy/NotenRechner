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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
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
                        FragmentTransaction transaction = mFragmentManager.beginTransaction();
                        transaction.replace(R.id.fragment_container, new AverageFragment(), AverageFragment.TAG).commit();
                        break;
                    case R.id.navitem_gradecalendar:
                        getSupportActionBar().setTitle(getString(R.string.fragmentGradeCalendar));

                        Log.i(TAG, "navItem greadecalendar");
                        FragmentTransaction xtransaction = mFragmentManager.beginTransaction();
                        xtransaction.replace(R.id.fragment_container, new CalendarFragment(), CalendarFragment.TAG).commit();
                        break;
                    case R.id.navitem_subjects:
                        getSupportActionBar().setTitle(getString(R.string.fragmentSubjects));

                        FragmentTransaction xxtransaction = mFragmentManager.beginTransaction();
                        xxtransaction.replace(R.id.fragment_container, new SubjectsFragment()).commit();
                        break;

                    case R.id.navitem_settings:
                        getSupportActionBar().setTitle(getString(R.string.fragmentSettings));

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


//    @Override
//    public boolean handleMessage(Message msg) {
//        // Process incomming messages here
//        switch (msg.what) {
//            case DATABASE_UPLOAD_GRADE:
//                Log.i(TAG, "Uploading Grade on Seperate Thread");
//                uploadGradeToDatabase((Grade) msg.obj);
//                break;
//        }
//        return true;
//    }
//
//    private void uploadGradeToDatabase(Grade grade) {
//        db.insertData(grade.getSubject(), grade.getName(), String.valueOf(grade.getGrade()), String.valueOf(grade.getFactor()), grade.getDate());
//        ParseObject gradeObject = new ParseObject("Grades");
//        gradeObject.put("deviceid", deviceId);
//        gradeObject.put("gradesubject", grade.getSubject());
//        gradeObject.put("gradename", grade.getName());
//        gradeObject.put("grade", String.valueOf(grade.getGrade()));
//        gradeObject.put("gradefactor", String.valueOf(grade.getFactor()));
//        gradeObject.put("gradedate", grade.getDate());
//        gradeObject.saveInBackground();
//
//        db.close();
//    }
//
//    public void sendMessage(Object data, int where) {
//        // Create new Message with data as parameter
//        // and send it for execution on the handler immediateely;
//        Message.obtain(mHandler, where, data).sendToTarget();
//    }


//    @Override
//    public void OnPremiumClick() {
//        bp.purchase(this, ITEM_SKU);
//    }
//
//
//    @Override
//    public void onDestroy() {
//        if (bp != null)
//            bp.release();
//
//        super.onDestroy();
//    }
//
//
//    @Override
//    public void onBillingInitialized() {
//        /*
//         * Called when BillingProcessor was initialized and it's ready to purchase
//         */
//    }
//
//    @Override
//    public void onProductPurchased(String productId, TransactionDetails details) {
//        /*
//         * Called when requested PRODUCT ID was successfully purchased
//         */
//        Toast.makeText(this, R.string.billing_success, Toast.LENGTH_SHORT).show();
//        PREMIUM = true;
//    }
//
//    @Override
//    public void onBillingError(int errorCode, Throwable error) {
//        /*
//         * Called when some error occurred. See Constants class for more details
//         */
//        Toast.makeText(this, R.string.billing_fail, Toast.LENGTH_SHORT).show();
//
//    }
//
//    @Override
//    public void onPurchaseHistoryRestored() {
//        /*
//         * Called when purchase history was restored and the list of all owned PRODUCT ID's
//         * was loaded from Google Play
//         */
//        if (bp.isPurchased(ITEM_SKU)) {
//            PREMIUM = true;
//        }
//    }
//
//    private void initBilling() {
//        bp = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsijTNgztiarHDP9P1B42JqJQnnTeGxmAOSb7uE98thZk814I7VYJDwSqFlFIBMcdAZfmNXfQEXINLXgAARON4NB7qVwBh3FM/5RW0Xz1ptPkr9JWeb70pIfg3urJ6aWZtj826y8ebZ2AJSVtbD1m+5lfeGeOw03+NJYqLscKDkXJEYVTvDIByipgobgMdiHP9JNJdGLiP+9xxKxssXPLBuVjMYSOeLlda0/1mPkiXsG5RgJyhJJ/dTGqFSyErHs9+z6MJEQfU7JxxIvgRiKn5gArOdsqMJRczLewfI8HtXx68yGqp6qE9CxPVti0fBFXuk+kRhmwjWyelRBNKJnnuwIDAQAB", this);
//        bp.loadOwnedPurchasesFromGoogle();
//        if (bp.isPurchased(ITEM_SKU)) {
//            PREMIUM = true;
//        }
//    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            super.onActivityResult(requestCode, resultCode, intent);
//            if (requestCode == 2) {
//                if (resultCode == Activity.RESULT_OK) {
//                    String subjectname = intent.getStringExtra("subjectname");
//                    String gradename = intent.getStringExtra("gradename");
//                    String grade = intent.getStringExtra("grade");
//                    String gradefactor = intent.getStringExtra("gradefactor");
//                    String gradedate = intent.getStringExtra("gradedate");
//
//                    //sendMessage(new Grade(subjectname, gradename, Double.valueOf(grade), Integer.valueOf(gradefactor), gradedate), DATABASE_UPLOAD_GRADE);
//
////                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, TabbedFragment.getInstance(), TabbedFragment.TAG).commit();
//
//
//                    if (resultCode == Activity.RESULT_CANCELED) {
//                /* No Result */
//                    }
//                }
//            }
//        }
    }


    @Override
    public void onStop() {
        super.onStop();


    }

}
