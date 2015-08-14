package com.linxy.gradeorganizer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;
import com.linxy.gradeorganizer.fragments.CalendarFragment;
import com.linxy.gradeorganizer.fragments.PreferenceFragment;
import com.linxy.gradeorganizer.fragments.ShopFragment;
import com.linxy.gradeorganizer.fragments.SubjectsFragment;
import com.linxy.gradeorganizer.fragments.ViewPagerContainer;

import util.IabHelper;
import util.IabResult;
import util.Inventory;
import util.Purchase;


public class StartupActivity extends ActionBarActivity implements ViewPagerContainer.OnDataPass, ShopFragment.BuyPremiumButtonClick, BillingProcessor.IBillingHandler {

    /* Instance Variables*/
    public static final String PREFS = "PrefFile";
    private boolean toolbarScroll;
    private boolean runOnce = true;
    public static boolean PREMIUM;

    /* View Components */
    private ControllableAppBarLayout appBarLayout;
    private Toolbar toolbar;
    private FloatingActionButton fabAddSubject;
    private FrameLayout container;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView navTitle;
    /* Database Helpers*/
    private DatabaseHelperSubjects dbs;


    private DatabaseHelper db;
    private NavigationView mNavigationView;

    private DrawerLayout Drawer;
    private ActionBarDrawerToggle mDrawerToggle;
    /* Billing */
//    IInAppBillingService mService;
//    IabHelper mHelper;
    static final String ITEM_SKU = "purchase_premium";

    BillingProcessor bp;
    /* Ads */
    InterstitialAd mIntersitialAd;

    /* Debug */
    private static final String TAG = "StartupActivity";

    /* Navigation Drawer */
    private LineChartView chart;
    private LineSet dataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        Log.i(TAG, "OnCreate Called");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // Create the Toolbar and set it as the toolbar



        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        toolbarScroll = true;
        container = (FrameLayout) findViewById(R.id.fragment_container);
        db = new DatabaseHelper(this);
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

        bp = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsijTNgztiarHDP9P1B42JqJQnnTeGxmAOSb7uE98thZk814I7VYJDwSqFlFIBMcdAZfmNXfQEXINLXgAARON4NB7qVwBh3FM/5RW0Xz1ptPkr9JWeb70pIfg3urJ6aWZtj826y8ebZ2AJSVtbD1m+5lfeGeOw03+NJYqLscKDkXJEYVTvDIByipgobgMdiHP9JNJdGLiP+9xxKxssXPLBuVjMYSOeLlda0/1mPkiXsG5RgJyhJJ/dTGqFSyErHs9+z6MJEQfU7JxxIvgRiKn5gArOdsqMJRczLewfI8HtXx68yGqp6qE9CxPVti0fBFXuk+kRhmwjWyelRBNKJnnuwIDAQAB", this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewPagerContainer()).commit();
        bp.loadOwnedPurchasesFromGoogle();
        if (bp.isPurchased(ITEM_SKU)) {
            PREMIUM = true;
        }

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

        navTitle = (TextView) findViewById(R.id.navTitle);
        if(PREMIUM){
            navTitle.setText("Notenrechner Premium");
        } else {
            navTitle.setText("Notenrechner Basic");
        }

      //  dataset = new LineSet();
      //  chart = (LineChartView) findViewById(R.id.linechart);

        initDataset();
        createChart();

        chart.addData(dataset);

        chart.show();

    }


    @Override
    public void OnPremiumClick() {
        bp.purchase(this, ITEM_SKU);
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

    public void onDestroy() {
        if (bp != null)
            bp.release();

        super.onDestroy();
    }

    private void blurChart() {
        navTitle = (TextView) findViewById(R.id.navTitle);

        RelativeLayout view = (RelativeLayout) findViewById(R.id.header_view);
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bm = view.getDrawingCache();
        Bitmap blurredBm = fastblur(bm, 50);
        ImageView overlayview = (ImageView) findViewById(R.id.overlay_view);
        overlayview.setImageBitmap(blurredBm);

        if (PREMIUM) {
            navTitle.setText("Notenrechner Premium");
        } else {
            navTitle.setText("Notenrechner Basic");
        }
        Log.i(TAG, "After BlurChart");
    }

    private void initDataset(){
        dataset = new LineSet();
        Cursor c = db.getAllData();
        while(c.moveToNext()){
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
                    db.insertData(subjectname, gradename, grade, gradefactor, gradedate);
                    db.close();

                    initDataset();
                    createChart();
                    chart.addData(dataset);
                    chart.show();



                    if (resultCode == Activity.RESULT_CANCELED) {
                /* No Result */
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewPagerContainer()).commit();

                }
            }
        }
    }


    final NavigationView.OnNavigationItemSelectedListener navigationListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            Fragment f = null;
            switch (menuItem.getItemId()) {
                case R.id.navitem_gradeaverage:

                    f = getSupportFragmentManager().findFragmentByTag("viewpagerfrag");
                    if (f != null && f instanceof ViewPagerContainer) {
                    } else {
                        Drawer.closeDrawers();
                        toolbarScroll = true;
                        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
                        toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                        toolbar.setLayoutParams(toolbarLayoutParams);
                        tabLayout.setVisibility(View.VISIBLE);
                        setToolbarTitle(getResources().getString(R.string.fragmentGradeAverage));
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewPagerContainer(), "viewpagerfrag").commit();
                        fabAddSubject.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.navitem_gradecalendar:

                    if (PREMIUM) {
                        f = getSupportFragmentManager().findFragmentByTag("calefrag");
                        if (f != null && f instanceof CalendarFragment) {
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
                            setToolbarTitle(getResources().getString(R.string.fragmentGradeCalendar));
                            fabAddSubject.setVisibility(View.GONE);
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CalendarFragment(), "calefrag").commit();
                        }
                    } else {
                        Toast.makeText(StartupActivity.this, getResources().getString(R.string.premiumForCalendar), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.navitem_subjects:
                    f = getSupportFragmentManager().findFragmentByTag("subjfrag");
                    if (f != null && f instanceof SubjectsFragment) {
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
                        fabAddSubject.setVisibility(View.GONE);
                        setToolbarTitle(getResources().getString(R.string.fragmentSubjects));
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SubjectsFragment(), "subjfrag").commit();
                    }
                    break;
                case R.id.navitem_shop:

                    f = getSupportFragmentManager().findFragmentByTag("shopfrag");
                    if (f != null && f instanceof ShopFragment) {
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
                        fabAddSubject.setVisibility(View.GONE);
                        setToolbarTitle(getResources().getString(R.string.fragmentShop));
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ShopFragment(), "shopfrag").commit();
                    }
                    break;
                case R.id.navitem_settings:

                    f = getSupportFragmentManager().findFragmentByTag("preffrag");
                    if (f != null && f instanceof PreferenceFragment) {

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
                        fabAddSubject.setVisibility(View.GONE);
                        setToolbarTitle(getResources().getString(R.string.fragmentSettings));
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



    @Override
    public void onDataPass(ViewPager vp, TabLayout tl) {
        this.viewPager = vp;

        this.tabLayout = tl;
//        tabLayout.setupWithViewPager(viewPager);
        this.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    appBarLayout.expandToolbar(true);
                    fabAddSubject.setVisibility(View.GONE);
                } else {
                    appBarLayout.expandToolbar(true);
                    fabAddSubject.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public Bitmap fastblur(Bitmap sentBitmap, int radius) {

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e(TAG, w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e(TAG, w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }
}
