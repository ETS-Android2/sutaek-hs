package com.codejune.sutaekhighschool.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.codejune.sutaekhighschool.fragment.Favorites;
import com.codejune.sutaekhighschool.R;
import com.codejune.sutaekhighschool.fragment.Schoolinfo;
import com.codejune.sutaekhighschool.ui.SlidingTabLayout;
import com.codejune.sutaekhighschool.util.Utils;

import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends ActionBarActivity {
    //View Pager
    ViewPager mViewPager;
    MainPagerAdapter mMainPagerAdapter;
    int Numboftabs = 2;
    CharSequence Titles[] = {"즐겨찾기", "학교정보"};
    SlidingTabLayout tabs;
    Toolbar toolbar;
    FrameLayout statusBar;
    DrawerLayout mDrawerLayout;
    RelativeLayout relativeLayoutSettings;
    ToggleButton toggleButtonDrawer;
    LinearLayout linearLayoutMain;
    ActionBarDrawerToggle mDrawerToggle;
    Intent intent;
    SharedPreferences sp;
    SharedPreferences.Editor edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isNetworkConnected(this)) {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_error)
                    .setTitle("네트워크 연결")
                    .setMessage("\n네트워크 연결 상태 확인 후 다시 시도해 주십시요\n")
                    .setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                }
                            }).show();
        } else {
            Utils utils = new Utils(getApplicationContext());
            utils.registerAlarm();

            //Setup Status Bar and Toolbar
            toolbarStatusBar();

            //Setup Navigation Drawer
            navigationDrawer();

            // Fix issues for each version and modes (check method at end of this file)
            navigationBarStatusBar();

            // Setup drawer accounts toggle.
            toogleButtonDrawer();

            // Open settings method
            openMain();
            openNotices();
            openParents();
            openEvents();
            openMeal();
            openSchedule();
            openSchoolIntro();
            openSetting();

            // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
            mMainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

            // Assigning ViewPager View and setting the adapter
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(mMainPagerAdapter);

            // Assiging the Sliding Tab Layout View
            tabs = (SlidingTabLayout) findViewById(R.id.tabs);
            tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

            // Setting Custom Color for the Scroll bar indicator of the Tab View
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.md_amber_500);
                }
            });

            // Setting the ViewPager For the SlidingTabsLayout
            tabs.setViewPager(mViewPager);

            sp = getSharedPreferences("sutaek", Context.MODE_PRIVATE);
            edit = sp.edit();
            final CheckBox mMealChkBox = (CheckBox) findViewById(R.id.meal_chkbox);
            mMealChkBox.setChecked(sp.getBoolean("meal", true));
            mMealChkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMealChkBox.isChecked()) {
                        edit.putBoolean("meal", true).apply();
                        findViewById(R.id.meal).setVisibility(View.VISIBLE);
                        mMainPagerAdapter.notifyDataSetChanged();
                    } else {
                        edit.putBoolean("meal", false).apply();
                        findViewById(R.id.meal).setVisibility(View.GONE);
                        mMainPagerAdapter.notifyDataSetChanged();
                    }
                }
            });
            final CheckBox NoticesChkBox = (CheckBox) findViewById(R.id.notices_chkbox);
            NoticesChkBox.setChecked(sp.getBoolean("notices", true));
            NoticesChkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NoticesChkBox.isChecked()) {
                        edit.putBoolean("notices", true).apply();
                        findViewById(R.id.notices).setVisibility(View.VISIBLE);
                        mMainPagerAdapter.notifyDataSetChanged();
                    } else {
                        edit.putBoolean("notices", false).apply();
                        findViewById(R.id.notices).setVisibility(View.GONE);
                        mMainPagerAdapter.notifyDataSetChanged();
                    }
                }
            });
            final CheckBox mNParentsChkBox = (CheckBox) findViewById(R.id.notices_parents_chkbox);
            mNParentsChkBox.setChecked(sp.getBoolean("notices_parents", true));
            mNParentsChkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mNParentsChkBox.isChecked()) {
                        edit.putBoolean("notices_parents", true).apply();
                        findViewById(R.id.notices_parents).setVisibility(View.VISIBLE);
                        mMainPagerAdapter.notifyDataSetChanged();
                    } else {
                        edit.putBoolean("notices_parents", false).apply();
                        findViewById(R.id.notices_parents).setVisibility(View.GONE);
                        mMainPagerAdapter.notifyDataSetChanged();
                    }
                }
            });

            final CheckBox ScheduleChkBox = (CheckBox) findViewById(R.id.schedule_chkbox);
            ScheduleChkBox.setChecked(sp.getBoolean("schedule", true));
            ScheduleChkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ScheduleChkBox.isChecked()) {
                        edit.putBoolean("schedule", true).apply();
                        findViewById(R.id.schedule).setVisibility(View.VISIBLE);
                        mMainPagerAdapter.notifyDataSetChanged();
                    } else {
                        edit.putBoolean("schedule", false).apply();
                        findViewById(R.id.schedule).setVisibility(View.GONE);
                        mMainPagerAdapter.notifyDataSetChanged();
                    }
                }
            });

        }

    }

    @Override
    public void onBackPressed() {
        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // 하드웨어 뒤로가기버튼 이벤트 설정.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            // 하드웨어 뒤로가기 버튼에 따른 이벤트 설정
            case KeyEvent.KEYCODE_BACK:

                final MaterialDialog mMaterialDialog = new MaterialDialog(this);
                mMaterialDialog.setTitle("종료");
                mMaterialDialog.setMessage("어플리케이션을 종료하시겠습니까?");
                mMaterialDialog.setPositiveButton("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
                mMaterialDialog.setNegativeButton("취소", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });

                mMaterialDialog.show();

            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void toolbarStatusBar() {

        // Cast toolbar and status bar
        statusBar = (FrameLayout) findViewById(R.id.statusBar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Get support to the toolbar and change its title
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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

    private void openMain() {
        relativeLayoutSettings = (RelativeLayout) findViewById(R.id.main);
        relativeLayoutSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after some time
                        mDrawerLayout.closeDrawers();
                    }
                }, 500);
            }
        });
    }

    private void openParents() {
        relativeLayoutSettings = (RelativeLayout) findViewById(R.id.parents);
        relativeLayoutSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, NoticesActivity.class);
                startActivity(intent);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after some time
                        mDrawerLayout.closeDrawers();
                    }
                }, 500);
            }
        });
    }

    private void openNotices() {
        relativeLayoutSettings = (RelativeLayout) findViewById(R.id.notices);
        relativeLayoutSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, NoticesActivity.class);
                startActivity(intent);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after some time
                        mDrawerLayout.closeDrawers();
                    }
                }, 500);
            }
        });
    }

    private void openMeal() {
        relativeLayoutSettings = (RelativeLayout) findViewById(R.id.meal);
        relativeLayoutSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, MealActivity.class);
                startActivity(intent);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after some time
                        mDrawerLayout.closeDrawers();
                    }
                }, 500);
            }
        });
    }

    private void openSchedule() {
        relativeLayoutSettings = (RelativeLayout) findViewById(R.id.schedule);
        relativeLayoutSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, Schedule.class);
                startActivity(intent);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after some time
                        mDrawerLayout.closeDrawers();
                    }
                }, 500);
            }
        });
    }

    private void openEvents() {
        relativeLayoutSettings = (RelativeLayout) findViewById(R.id.events);
        relativeLayoutSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, SchoolEvent.class);
                startActivity(intent);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after some time
                        mDrawerLayout.closeDrawers();
                    }
                }, 500);
            }
        });
    }

    private void openSchoolIntro() {
        relativeLayoutSettings = (RelativeLayout) findViewById(R.id.schoolintro);
        relativeLayoutSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, Schoolintro.class);
                startActivity(intent);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after some time
                        mDrawerLayout.closeDrawers();
                    }
                }, 500);
            }
        });
    }

    private void openSetting() {
        relativeLayoutSettings = (RelativeLayout) findViewById(R.id.setting);
        relativeLayoutSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, Appinfo.class);
                startActivity(intent);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after some time
                        mDrawerLayout.closeDrawers();
                    }
                }, 500);
            }
        });
    }

    public void toogleButtonDrawer() {
        toggleButtonDrawer = (ToggleButton) findViewById(R.id.toggleButtonDrawer);
        linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);
    }


    public void navigationDrawer() {

        // Cast drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Fix right margin to 56dp (portrait)
        View drawer = findViewById(R.id.scrimInsetsFrameLayout);
        ViewGroup.LayoutParams layoutParams = drawer.getLayoutParams();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutParams.width = displayMetrics.widthPixels - (56 * Math.round(displayMetrics.density));
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutParams.width = displayMetrics.widthPixels + (20 * Math.round(displayMetrics.density)) - displayMetrics.widthPixels / 2;
        }

        // Setup Drawer Icon
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
    }

    public void navigationBarStatusBar() {

        // Fix portrait issues
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Fix issues for KitKat setting Status Bar color primary
            if (Build.VERSION.SDK_INT >= 19) {
                TypedValue typedValue19 = new TypedValue();
                MainActivity.this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue19, true);
                final int color = typedValue19.data;
                FrameLayout statusBar = (FrameLayout) findViewById(R.id.statusBar);
                statusBar.setBackgroundColor(color);
            }

            // Fix issues for Lollipop, setting Status Bar color primary dark
            if (Build.VERSION.SDK_INT >= 21) {
                TypedValue typedValue21 = new TypedValue();
                MainActivity.this.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue21, true);
                final int color = typedValue21.data;
                FrameLayout statusBar = (FrameLayout) findViewById(R.id.statusBar);
                statusBar.setBackgroundColor(color);
            }
        }
        // Fix landscape issues (only Lollipop)
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (Build.VERSION.SDK_INT >= 19) {
                TypedValue typedValue19 = new TypedValue();
                MainActivity.this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue19, true);
                final int color = typedValue19.data;
                FrameLayout statusBar = (FrameLayout) findViewById(R.id.statusBar);
                statusBar.setBackgroundColor(color);
            }
            if (Build.VERSION.SDK_INT >= 21) {
                TypedValue typedValue = new TypedValue();
                MainActivity.this.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
                final int color = typedValue.data;
                mDrawerLayout.setStatusBarBackgroundColor(color);
            }
        }
    }
}

class MainPagerAdapter extends FragmentStatePagerAdapter {
    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public MainPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {

        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if (position == 0) // if the position is 0 we are returning the First tab
        {
            Favorites favorites = new Favorites();
            return favorites;
        } else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {

            Schoolinfo schoolinfo = new Schoolinfo();
            return schoolinfo;
        }


    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return NumbOfTabs;
    }

}