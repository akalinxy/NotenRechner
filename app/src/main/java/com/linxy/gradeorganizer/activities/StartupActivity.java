package com.linxy.gradeorganizer.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.linxy.gradeorganizer.fragments.Tab1;
import com.linxy.gradeorganizer.utility.ControllableAppBarLayout;
import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;
import com.linxy.gradeorganizer.fragments.CalendarFragment;
import com.linxy.gradeorganizer.fragments.PreferenceFragment;
import com.linxy.gradeorganizer.fragments.ShopFragment;
import com.linxy.gradeorganizer.fragments.SubjectsFragment;
import com.linxy.gradeorganizer.fragments.TabbedFragment;
import com.linxy.gradeorganizer.objects.Grade;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;


public class StartupActivity extends ActionBarActivity implements ShopFragment.BuyPremiumButtonClick,TabbedFragment.OnTabChange, BillingProcessor.IBillingHandler, Handler.Callback {

    /* Constants */
    public static final int DATABASE_UPLOAD_GRADE = 1;
    public static final int DATABASE_CREATE_CHART = 2;
    public static final int UI_DRAW_CHART = 3;
    private static final String TAG = "StartupActivity";
    private static final String ITEM_SKU = "purchase_premium";
    public static final String PREFS = "PrefFile";

    /* Instance Variables*/
    private boolean toolbarScroll;
    private String deviceId;
    public static boolean PREMIUM;

    /* View Components */
    private ControllableAppBarLayout appBarLayout;
    private Toolbar toolbar;
    private FloatingActionButton fabAddSubject;
    private TextView navTitle;
    private TabLayout tabLayout;

    /* Database Helpers*/
    private DatabaseHelperSubjects dbs;
    private DatabaseHelper db;

    /* Navigation Drawer */
    private NavigationView mNavigationView;
    private DrawerLayout Drawer;
    private ActionBarDrawerToggle mDrawerToggle;

    /* Ads & Billing */
    BillingProcessor bp;
    InterstitialAd mIntersitialAd;

    /* Navigation Drawer */
    private LineChartView chart;
    private LineSet dataset;

    /* Multithreading */
    private Handler mHandler;
    private Handler mHandlerMain;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        Parse.initialize(getBaseContext(), "tDb5Zw7Tvh7QxU5cU5AjulmP8Uk9NBgREYDaP41W", "KRVun9zyKtDOFaCLUsyEJ3Qofrg0t0OmsyebuCNi");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        deviceId = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        toolbarScroll = true;
        db = new DatabaseHelper(this);
        dbs = new DatabaseHelperSubjects(this);
        appBarLayout = (ControllableAppBarLayout) findViewById(R.id.appBarLayout);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        createFAB();
        createDrawer();
        initBilling();
        initIntersitial();
        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        mNavigationView.setNavigationItemSelectedListener(navigationListener);


        navTitle = (TextView) findViewById(R.id.navTitle);
        if (PREMIUM) {
            navTitle.setText("Notenrechner Premium");
        } else {
            navTitle.setText("Notenrechner Basic");
        }

        mHandlerMain = new Handler(getMainLooper(), this);

        HandlerThread handlerThread = new HandlerThread("Background Thread");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper(), this);


        sendMessage(null, DATABASE_CREATE_CHART);
        Message.obtain(mHandlerMain, UI_DRAW_CHART).sendToTarget();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, TabbedFragment.getInstance(), TabbedFragment.TAG).commit();

    }


    @Override
    public boolean handleMessage(Message msg) {
        // Process incomming messages here
        switch (msg.what) {
            case DATABASE_UPLOAD_GRADE:
                Log.i(TAG, "Uploading Grade on Seperate Thread");
                uploadGradeToDatabase((Grade) msg.obj);

                break;
            case DATABASE_CREATE_CHART:
                Log.i(TAG, "Creating Chart on Seperate Thread");
                initDataset();
                break;
            case UI_DRAW_CHART:
                createChart();
                chart.addData(dataset);
                chart.show();
                break;
        }

        // Recycle the object
//        msg.recycle();
        return true;
    }

    private void uploadGradeToDatabase(Grade grade) {
        db.insertData(grade.getSubject(), grade.getName(), String.valueOf(grade.getGrade()), String.valueOf(grade.getFactor()), grade.getDate());

        ParseObject gradeObject = new ParseObject("Grades");
        gradeObject.put("deviceid", deviceId);
        gradeObject.put("gradesubject", grade.getSubject());
        gradeObject.put("gradename", grade.getName());
        gradeObject.put("grade", String.valueOf(grade.getGrade()));
        gradeObject.put("gradefactor", String.valueOf(grade.getFactor()));
        gradeObject.put("gradedate", grade.getDate());
        gradeObject.saveInBackground();

        db.close();
    }

    public void sendMessage(Object data, int where) {
        // Create new Message with data as parameter
        // and send it for execution on the handler immediateely;
        Message.obtain(mHandler, where, data).sendToTarget();
    }


    @Override
    public void OnPremiumClick() {
        bp.purchase(this, ITEM_SKU);
    }

    @Override
    public void onTabChange() {
        appBarLayout.expandToolbar(true);
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();

        super.onDestroy();
        mHandler.getLooper().quit();
        mHandlerMain.getLooper().quit();
    }


    @Override
    public void onBillingInitialized() {
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        /*
         * Called when requested PRODUCT ID was successfully purchased
         */
        Toast.makeText(this, "Purchase Successful", Toast.LENGTH_SHORT).show();
        PREMIUM = true;
        navTitle.setText("Notenrechner Premium");
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        /*
         * Called when some error occurred. See Constants class for more details
         */
        Toast.makeText(this, "Purchase Fail", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
        if (bp.isPurchased(ITEM_SKU)) {
            PREMIUM = true;
        }
    }


    private void requestNewIntersitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mIntersitialAd.loadAd(adRequest);
    }

    private void createDrawer() {

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

    private void createFAB() {
        fabAddSubject = (FloatingActionButton) findViewById(R.id.fab);
        fabAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor subjectCursor = dbs.getAllData();
                if (subjectCursor.getCount() == 0) {
                    Toast.makeText(StartupActivity.this, getResources().getString(R.string.needSubjects), Toast.LENGTH_SHORT).show();
                } else {
                    if (PREMIUM) {
                        Intent intent = new Intent(StartupActivity.this, NewGradeActivity.class);
                        startActivityForResult(intent, 2);
                    } else {
                        if (mIntersitialAd.isLoaded()) {
                            Intent intent = new Intent(StartupActivity.this, NewGradeActivity.class);
                            startActivityForResult(intent, 2);
                            mIntersitialAd.show();
                        }
                    }
                }
                dbs.close();
                subjectCursor.close();
            }
        });
    }

    private void initBilling() {
        bp = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsijTNgztiarHDP9P1B42JqJQnnTeGxmAOSb7uE98thZk814I7VYJDwSqFlFIBMcdAZfmNXfQEXINLXgAARON4NB7qVwBh3FM/5RW0Xz1ptPkr9JWeb70pIfg3urJ6aWZtj826y8ebZ2AJSVtbD1m+5lfeGeOw03+NJYqLscKDkXJEYVTvDIByipgobgMdiHP9JNJdGLiP+9xxKxssXPLBuVjMYSOeLlda0/1mPkiXsG5RgJyhJJ/dTGqFSyErHs9+z6MJEQfU7JxxIvgRiKn5gArOdsqMJRczLewfI8HtXx68yGqp6qE9CxPVti0fBFXuk+kRhmwjWyelRBNKJnnuwIDAQAB", this);
        bp.loadOwnedPurchasesFromGoogle();
        if (bp.isPurchased(ITEM_SKU)) {
            PREMIUM = true;
        }
    }

    private void initIntersitial() {
        if (!PREMIUM) {
            mIntersitialAd = new InterstitialAd(this);
            mIntersitialAd.setAdUnitId(getResources().getString(R.string.intersitial_ad_unit_id));
            mIntersitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    requestNewIntersitial();
                }
            });
            requestNewIntersitial();
        }
    }


    private void initDataset() {
        dataset = new LineSet();
        Cursor c = db.getAllData();
        while (c.moveToNext()) {
            dataset.addPoint(new Point("gradepoint", Float.valueOf(c.getString(3))));
        }
        c.close();
        dataset.setColor(getResources().getColor(R.color.ColorYellow));
        dataset.setThickness(20);
    }

    private void createChart() {
        chart = (LineChartView) findViewById(R.id.linechart);
        chart.dismiss();
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.ColorFlatRed));
        paint.setStrokeWidth(15);
        chart.setThresholdLine(4, paint);

        chart.setYLabels(AxisController.LabelPosition.NONE);
        chart.setXLabels(AxisController.LabelPosition.NONE);
        chart.setXAxis(false);
        chart.setYAxis(false);
        Log.i(TAG, "FIX");

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (!bp.handleActivityResult(requestCode, resultCode, intent)) {
            super.onActivityResult(requestCode, resultCode, intent);
            if (requestCode == 2) {
                Log.i(TAG, "Inside RequestCode == 2");
                if (resultCode == Activity.RESULT_OK) {
                    String subjectname = intent.getStringExtra("subjectname");
                    String gradename = intent.getStringExtra("gradename");
                    String grade = intent.getStringExtra("grade");
                    String gradefactor = intent.getStringExtra("gradefactor");
                    String gradedate = intent.getStringExtra("gradedate");

                    sendMessage(new Grade(subjectname, gradename, Double.valueOf(grade), Integer.valueOf(gradefactor), gradedate), DATABASE_UPLOAD_GRADE);
                    sendMessage(null, DATABASE_CREATE_CHART);
                    Message.obtain(mHandlerMain, UI_DRAW_CHART).sendToTarget();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Fragment fragmentTabbed = (TabbedFragment) getSupportFragmentManager().findFragmentByTag(TabbedFragment.TAG);
                            Log.i(TAG, "Fragment F assigned: " + fragmentTabbed.getTag());
                            ((TabbedFragment) fragmentTabbed).refreshFragment();
                        }
                    }, 1000);



                    if (resultCode == Activity.RESULT_CANCELED) {
                /* No Result */
                    }
                }
            }
        }
    }


    final NavigationView.OnNavigationItemSelectedListener navigationListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            Fragment f;
            switch (menuItem.getItemId()) {
                case R.id.navitem_gradeaverage:
                    f = getSupportFragmentManager().findFragmentByTag(TabbedFragment.TAG);
                    if (!(f != null && f instanceof TabbedFragment)) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, TabbedFragment.getInstance(), TabbedFragment.TAG).commit();
                        setToolbarTitle(getResources().getString(R.string.fragmentGradeAverage));
                        fabAddSubject.setVisibility(View.VISIBLE);
                        tabLayout.setVisibility(View.VISIBLE);
                        toolbarScroll = true;
                        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
                        toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                        toolbar.setLayoutParams(toolbarLayoutParams);
                    }
                    Drawer.closeDrawers();
                    break;
                case R.id.navitem_gradecalendar:
                    if (PREMIUM) {
                        f = getSupportFragmentManager().findFragmentByTag(CalendarFragment.TAG);
                        if (!(f != null && f instanceof CalendarFragment)) {
                            if (toolbarScroll) {
                                ControllableAppBarLayout.LayoutParams toolbarLayoutParams = (ControllableAppBarLayout.LayoutParams) toolbar.getLayoutParams();
                                toolbarLayoutParams.setScrollFlags(0);
                                toolbar.setLayoutParams(toolbarLayoutParams);
                                toolbarScroll = false;
                            }
                            tabLayout.setVisibility(View.GONE);
                            setToolbarTitle(getResources().getString(R.string.fragmentGradeCalendar));
                            fabAddSubject.setVisibility(View.GONE);
                            appBarLayout.expandToolbar();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, CalendarFragment.getInstance(), CalendarFragment.TAG).commit();
                        }
                        Drawer.closeDrawers();
                    } else {
                        Toast.makeText(StartupActivity.this, getResources().getString(R.string.premiumForCalendar), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.navitem_subjects:
                    f = getSupportFragmentManager().findFragmentByTag(SubjectsFragment.TAG);
                    if (!(f != null && f instanceof SubjectsFragment)) {
                        if (toolbarScroll) {
                            ControllableAppBarLayout.LayoutParams toolbarLayoutParams = (ControllableAppBarLayout.LayoutParams) toolbar.getLayoutParams();
                            toolbarLayoutParams.setScrollFlags(0);
                            toolbar.setLayoutParams(toolbarLayoutParams);
                            toolbarScroll = false;
                        }
                        tabLayout.setVisibility(View.GONE);
                        appBarLayout.expandToolbar();
                        fabAddSubject.setVisibility(View.GONE);
                        setToolbarTitle(getResources().getString(R.string.fragmentSubjects));
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, SubjectsFragment.getInstance(), SubjectsFragment.TAG).commit();
                    }
                    Drawer.closeDrawers();
                    break;
                case R.id.navitem_shop:
                    f = getSupportFragmentManager().findFragmentByTag(ShopFragment.TAG);
                    if (!(f != null && f instanceof ShopFragment)) {
                        if (toolbarScroll) {
                            ControllableAppBarLayout.LayoutParams toolbarLayoutParams = (ControllableAppBarLayout.LayoutParams) toolbar.getLayoutParams();
                            toolbarLayoutParams.setScrollFlags(0);
                            toolbar.setLayoutParams(toolbarLayoutParams);
                            toolbarScroll = false;
                        }
                        tabLayout.setVisibility(View.GONE);
                        setToolbarTitle(getResources().getString(R.string.fragmentShop));
                        appBarLayout.expandToolbar();
                        fabAddSubject.setVisibility(View.GONE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ShopFragment.getInstance(), ShopFragment.TAG).commit();
                    }
                    Drawer.closeDrawers();
                    break;
                case R.id.navitem_settings:
                    f = getSupportFragmentManager().findFragmentByTag(PreferenceFragment.TAG);
                    if (!(f != null && f instanceof PreferenceFragment)) {
                        if (toolbarScroll) {
                            ControllableAppBarLayout.LayoutParams toolbarLayoutParams = (ControllableAppBarLayout.LayoutParams) toolbar.getLayoutParams();
                            toolbarLayoutParams.setScrollFlags(0);
                            toolbar.setLayoutParams(toolbarLayoutParams);
                            toolbarScroll = false;
                        }
                        tabLayout.setVisibility(View.GONE);
                        appBarLayout.expandToolbar();
                        fabAddSubject.setVisibility(View.GONE);
                        setToolbarTitle(getResources().getString(R.string.fragmentSettings));
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, PreferenceFragment.getInstance(), PreferenceFragment.TAG).commit();
                    }
                    Drawer.closeDrawers();
                    break;
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_startup, menu);
        return true;
    }

    private void setToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onStop() {
        super.onStop();
        dbs.close();
        db.close();

    }

}
