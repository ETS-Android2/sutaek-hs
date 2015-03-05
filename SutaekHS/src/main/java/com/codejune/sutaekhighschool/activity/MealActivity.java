package com.codejune.sutaekhighschool.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.codejune.sutaekhighschool.R;
import com.codejune.sutaekhighschool.ui.FloatingActionButton;
import com.codejune.sutaekhighschool.ui.SlidingTabLayout;
import com.codejune.sutaekhighschool.util.MealCacheManager;
import com.codejune.sutaekhighschool.util.MealLoadHelper;
import java.util.Calendar;
import java.util.Locale;

import me.drakeet.materialdialog.MaterialDialog;

public class MealActivity extends ActionBarActivity {

    String LOG_TAG = "MealActivity";
    private ViewPager mViewPager;
    SwipeRefreshLayout SRL;
    ShareActionProvider mShareActionProvider;

    //ToolBar
    private Toolbar toolbar;
    private SlidingTabLayout tabs;

    static String[] LunchArray;
    static String[] LunchKcalArray;
    static String[] DinnerArray;
    static String[] DinnerKcalArray;

    MaterialDialog networkConnection;
    MaterialDialog mealInfo;
    FloatingActionButton button;

    private final Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        networkConnection = new MaterialDialog(this);
        networkConnection.setTitle("네트워크 연결");
        networkConnection.setMessage("네트워크 연결 상태 확인 후 다시 시도해 주세요.");
        networkConnection.setPositiveButton("확인", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mealInfo = new MaterialDialog(this);
        mealInfo.setTitle("알레르기 정보");
        mealInfo.setMessage("①난류\n②우유\n③메밀\n④땅콩\n⑤대두\n⑥밀\n⑦고등어\n⑧게\n⑨새우\n⑩돼지고기\n⑪복숭아\n⑫토마토\n⑬아황산염");
        mealInfo.setPositiveButton("확인", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mealInfo.dismiss();
            }
        });

        if (!isNetworkConnected(this)) {
            networkConnection.show();
        } else {

            //Setup Status Bar and Toolbar
            toolbarStatusBar();

            // Fix issues for each version and modes (check method at end of this file)
            navigationBarStatusBar();

            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(new SamplePagerAdapter());

            tabs = (SlidingTabLayout) findViewById(R.id.tabs);
            // Setting Custom Color for the Scroll bar indicator of the Tab View
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.md_yellow_500);
                }
            });
            // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width\
            tabs.setDistributeEvenly(true);
            tabs.setViewPager(mViewPager);

            button = (FloatingActionButton) findViewById(R.id.mealinfo);
            button.setSize(FloatingActionButton.SIZE_NORMAL);
            button.setColorNormalResId(R.color.md_yellow_500);
            button.setColorPressedResId(R.color.md_yellow_200);
            button.setIcon(R.drawable.info);
            button.setStrokeVisible(false);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mealInfo.show();
                }
            });

            SRL = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
            loadMealTask();
            SRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadMealTask();
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fragment_meal, menu);
        // 공유 버튼 찾기
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // ShareActionProvider 얻기
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // 공유 버튼에 사용할 Intent 를 만들어 주는 메서드를 호출합니다.
        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareIntent(1));
        } else {
        }
        return true;
    }

    class SamplePagerAdapter extends PagerAdapter {

        /**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            return 5;
        }

        /**
         * @return true if the value returned from
         *         {@link #instantiateItem(android.view.ViewGroup, int)} is the same object
         *         as the {@link android.view.View} added to the {@link android.support.v4.view.ViewPager}.
         */
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        // BEGIN_INCLUDE (pageradapter_getpagetitle)
        /**
         * Return the title of the item at {@code position}. This is important
         * as what this method returns is what is displayed in the
         * {@link com.codejune.sutaekhighschool.ui.SlidingTabLayout}.
         * <p>
         * Here we construct one using the position value, but for real
         * application the title should refer to the item's contents.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.monday).toUpperCase(l);
                case 1:
                    return getString(R.string.tuesday).toUpperCase(l);
                case 2:
                    return getString(R.string.wednsday).toUpperCase(l);
                case 3:
                    return getString(R.string.thursday).toUpperCase(l);
                case 4:
                    return getString(R.string.friday).toUpperCase(l);
            }
            return null;
        }

        // END_INCLUDE (pageradapter_getpagetitle)

        /**
         * Instantiate the {@link android.view.View} which should be displayed at
         * {@code position}. Here we inflate a layout from the apps resources
         * and then change the text view to signify the position.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            try{
                if (mShareActionProvider != null ) {
                    mShareActionProvider.setShareIntent(createShareIntent(mViewPager.getCurrentItem()+1));
                } else {
                }
            }catch(Exception e){}
            // Inflate a new layout from our resources

            View view = getLayoutInflater().inflate(R.layout.fragment_day_meal,
                    container, false);
            TextView LunchTxt = (TextView)view.findViewById(R.id.lunchtxt);
            TextView DinnerTxt = (TextView)view.findViewById(R.id.dinnertxt);

            if(LunchArray==null){
                LunchTxt.setText(getResources().getString(R.string.loading));
            }else if(LunchArray[position+1]==null){
                LunchTxt.setText(getResources().getString(R.string.mealnone));
            }else{
                String LunchData = LunchArray[position+1]+"\n\n"+LunchKcalArray[position+1];
                LunchTxt.setText(LunchData);
            }

            if(DinnerArray==null){
             DinnerTxt.setText(getResources().getString(R.string.loading));
            }else if(DinnerArray[position+1]==null){
                DinnerTxt.setText(getResources().getString(R.string.mealnone));
            }else{
                String DinnerData = DinnerArray[position+1]+"\n\n"+DinnerKcalArray[position+1];
                DinnerTxt.setText(DinnerData);
            }
            container.addView(view);

            Log.i(LOG_TAG, "instantiateItem() [position: " + position + "]");

            // Return the View
            return view;
        }

        /**
         * Destroy the item from the {@link android.support.v4.view.ViewPager}. In our case this is
         * simply removing the {@link android.view.View}.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            Log.i(LOG_TAG, "destroyItem() [position: " + position + "]");
        }

    }

    private void loadMealTask() {
        final MealCacheManager manager = new MealCacheManager(MealActivity.this);
        SRL.setRefreshing(false);
        SRL.setRefreshing(true);
        final Handler mHandler = new Handler();
        new Thread() {

            public void run() {
                mHandler.post(new Runnable() {

                    public void run() {
                        // SRL.setRefreshing(true);
                    }
                });
                try {
                    LunchArray = MealLoadHelper.getMeal("goe.go.kr", "J100000656", "4", "04", "2"); //Get Lunch Menu Date
                    LunchKcalArray = MealLoadHelper.getKcal("goe.go.kr", "J100000656", "4", "04", "2"); //Get Lunch Menu Kcal Value
                    DinnerArray = MealLoadHelper.getMeal("goe.go.kr", "J100000656", "4", "04", "3"); //Get Dinner Menu Date
                    DinnerKcalArray = MealLoadHelper.getKcal("goe.go.kr", "J100000656", "4", "04", "3"); //Get Dinner Menu Kcal Value
                } catch (Exception e) {
                }

                mHandler.post(new Runnable() {
                    public void run() {
                        mViewPager.setAdapter(new SamplePagerAdapter());
                        tabs.setViewPager(mViewPager);
                        manager.updateCache(LunchArray, LunchKcalArray, DinnerArray, DinnerKcalArray);
                        Log.d("MealLoadTadk", "Data Loading Done");
                        if (mShareActionProvider != null) {
                            mShareActionProvider.setShareIntent(createShareIntent(1));
                        } else {
                        }
                        SRL.setRefreshing(false);
                        handler.sendEmptyMessage(0);
                    }
                });

            }
        }.start();
        Log.d("MealLoadTask", "Loading from Cache");
        LunchArray = manager.loadLunchCache();
        LunchKcalArray = manager.loadLunchKcalcache();
        DinnerArray = manager.loadDinnerCache();
        DinnerKcalArray = manager.loadDinnerKcalCache();
        mViewPager.setAdapter(new SamplePagerAdapter());
        tabs.setViewPager(mViewPager);
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent(1));
        } else {
        }
        SRL.setRefreshing(false);

    }

    public int getDefaultPageNumber(){
        Calendar c = Calendar.getInstance();
        int DAY = c.get(Calendar.DAY_OF_WEEK);
        if(DAY==0){
            return 0;
        }else if(DAY==6){
            return 6;
        }else{
            return DAY-1;
        }
    }

    private Intent createShareIntent(int pos) {
        //액션은 ACTION_SEND 로 합니다.
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        //Flag 를 설정해 줍니다. 공유하기 위해 공유에 사용할 다른 앱의 하나의 Activity 만 열고,
        //다시 돌아오면 열었던 Activity 는 꺼야 하기 때문에
        //FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET 로 해줍니다.
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        //공유할 것의 형태입니다. 우리는 텍스트를 공유합니다.
        shareIntent.setType("text/plain");
        String mealData;
        try {
            mealData =
                    getResources().getString(R.string.lunch) + "\n"
                            + LunchArray[mViewPager.getCurrentItem()+1] + "\n\n"
                            + getResources().getString(R.string.dinner) + "\n"
                            + DinnerArray[mViewPager.getCurrentItem()+1] + "\n\n";
        }catch (Exception e){
            mealData = getResources().getString(R.string.mealnone);
        }
        //보낼 데이터를 Extra 로 넣어줍니다.
        shareIntent.putExtra(Intent.EXTRA_TEXT,mealData);
        Log.d(LOG_TAG,"Creating Share Intent:"+mealData);
        return shareIntent;
    }

    public void toolbarStatusBar() {

        // Cast toolbar and status bar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Get support to the toolbar and change its title
        setSupportActionBar(toolbar);

        // Fix ArrowUp color
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.md_white_1000), PorterDuff.Mode.SRC_ATOP);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(upArrow);
    }

    public void navigationBarStatusBar() {

        // Fix portrait issues
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Fix issues for KitKat setting Status Bar color primary
            if (Build.VERSION.SDK_INT >= 19) {
                TypedValue typedValue19 = new TypedValue();
                MealActivity.this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue19, true);
                final int color = typedValue19.data;
                FrameLayout statusBar = (FrameLayout) findViewById(R.id.statusBar);
                statusBar.setBackgroundColor(color);
            }

            // Fix issues for Lollipop, setting Status Bar color primary dark
            if (Build.VERSION.SDK_INT >= 21) {
                TypedValue typedValue21 = new TypedValue();
                MealActivity.this.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue21, true);
                final int color = typedValue21.data;
                FrameLayout statusBar = (FrameLayout) findViewById(R.id.statusBar);
                statusBar.setBackgroundColor(color);
            }
        }
        // Fix landscape issues (only Lollipop)
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (Build.VERSION.SDK_INT >= 19){
                TypedValue typedValue19 = new TypedValue();
                MealActivity.this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue19, true);
                final int color = typedValue19.data;
                FrameLayout statusBar = (FrameLayout) findViewById(R.id.statusBar);
                statusBar.setBackgroundColor(color);
            }
            if (Build.VERSION.SDK_INT >= 21){
                TypedValue typedValue = new TypedValue();
                MealActivity.this.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
            }
        }
    }

    // 인터넷 연결 상태 체크
    public boolean isNetworkConnected(Context context) {

        boolean isConnected = false;

        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mobile.isConnected() || wifi.isConnected()) {
            isConnected = true;
        } else {
            isConnected = false;
        }
        return isConnected;
    }

}