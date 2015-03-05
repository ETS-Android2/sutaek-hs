package com.codejune.sutaekhighschool.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.codejune.sutaekhighschool.util.ListCalendarAdapter;
import com.codejune.sutaekhighschool.R;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import me.drakeet.materialdialog.MaterialDialog;

public class Schedule extends ActionBarActivity {

    //ToolBar
    private Toolbar toolbar;
    private ArrayList<String> dayarray;
    private ArrayList<String> schedulearray;
    private ListCalendarAdapter adapter;
    private SwipeRefreshLayout SRL;
    private String URL = "http://www.sutaek.hs.kr/main.php?menugrp=020500&master="
            + "diary&act=list&master_sid=1";
    private String NewURL;
    private String NextMonthURL;
    ListView listview;
    private int month;
    private int year;
    int movement = 0;
    MaterialDialog mMaterialDialog;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        //Setup Status Bar and Toolbar
        toolbarStatusBar();

        // Fix issues for each version and modes (check method at end of this file)
        navigationBarStatusBar();

        mMaterialDialog = new MaterialDialog(this);
        mMaterialDialog.setTitle("네트워크 연결");
        mMaterialDialog.setMessage("네트워크 연결 상태 확인 후 다시 시도해 주세요.");
        mMaterialDialog.setPositiveButton("확인", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (!isNetworkConnected(this)) {
            mMaterialDialog.show();
        } else {
            final TextView MonthTxt = (TextView) findViewById(R.id.month);
            Button Minus = (Button) findViewById(R.id.minus);
            Button Plus = (Button) findViewById(R.id.plus);
            Calendar Cal = Calendar.getInstance();
            month = Cal.get(Calendar.MONTH) + 1;
            year = Cal.get(Calendar.YEAR);

            final int MONTH = month;
            final int YEAR = year;

            MonthTxt.setText(String.valueOf(year) + "." + String.valueOf(month));
            Minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    movement--;
                    NewURL = "http://www.sutaek.hs.kr/main.php?menugrp=020500&master=diary&act=list&master_sid=1&"
                            + "SearchYear="
                            + YEAR
                            + "&SearchMonth="
                            + MONTH
                            + "&SearchCategory=&SearchMoveMonth=" + movement;
                    URL = NewURL;
                    networkTask();
                    if (month == 1) {
                        month = 12;
                        year--;
                    } else {
                        month--;
                    }
                    MonthTxt.setText(String.valueOf(year) + "."
                            + String.valueOf(month));
                }
            });
            Plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    movement++;
                    NewURL = "http://www.sutaek.hs.kr/main.php?menugrp=020500&master=diary&act=list&master_sid=1&"
                            + "SearchYear="
                            + YEAR
                            + "&SearchMonth="
                            + MONTH
                            + "&SearchCategory=&SearchMoveMonth=" + movement;
                    URL = NewURL;
                    networkTask();
                    if (month == 12) {
                        month = 1;
                        year++;
                    } else {
                        month++;
                    }
                    MonthTxt.setText(String.valueOf(year) + "."
                            + String.valueOf(month));
                }
            });

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            upArrow.setColorFilter(getResources().getColor(R.color.md_white_1000), PorterDuff.Mode.SRC_ATOP);
            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(upArrow);
            listview = (ListView) findViewById(R.id.listView);
            SRL = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
            SRL.setColorSchemeColors(Color.rgb(231, 76, 60),
                    Color.rgb(46, 204, 113), Color.rgb(41, 128, 185),
                    Color.rgb(241, 196, 15));
            SRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    networkTask();
                }
            });
            networkTask();
        }
    }

    private void networkTask() {
        final Handler mHandler = new Handler();
        new Thread() {

            public void run() {

                mHandler.post(new Runnable() {

                    public void run() {
                        SRL.setRefreshing(true);
                    }
                });

                // Task

                // Notices URL
                try {
                    int skip = 0;
                    boolean skipedboolean = false;
                    schedulearray = new ArrayList<String>();
                    dayarray = new ArrayList<String>();

                    // 학사일정 데이터 학교 홈페이지에서 파싱해 가져오기
                    Document doc = Jsoup.connect(URL).get();

                    Elements rawdaydata = doc.select(".listDay"); // Get
                    // contents
                    // from the
                    // class,"listDay"
                    for (Element el : rawdaydata) {
                        String daydata = el.text();
                        if (daydata.equals("") | daydata == null) {
                            if (skipedboolean) {
                            } else {
                                skip++;
                            }
                        } else {
                            dayarray.add(daydata); // add value to ArrayList
                            skipedboolean = true;
                        }
                    }
                    Log.d("Schedule", "Parsed Day Array" + dayarray);

                    Elements rawscheduledata = doc.select(".listData"); // Get
                    // contents
                    // from
                    // tags,"a"
                    // which
                    // are
                    // in
                    // the
                    // class,"ellipsis"
                    for (Element el : rawscheduledata) {
                        String scheduledata = el.text();
                        if (skip > 0) {
                            skip--;
                        } else {
                            schedulearray.add(scheduledata); // add value to
                            // ArrayList
                        }
                    }
                    Log.d("Schedule", "Parsed Schedule Array" + schedulearray);

                    // SRL.setRefreshing(false);
                } catch (IOException e) {
                    e.printStackTrace();
                    // SRL.setRefreshing(false);

                }

                mHandler.post(new Runnable() {
                    public void run() {
                        // UI Task
                        adapter = new ListCalendarAdapter(Schedule.this,
                                dayarray, schedulearray);
                        listview.setAdapter(adapter);
                        SRL.setRefreshing(false);
                        handler.sendEmptyMessage(0);

                    }
                });

            }
        }.start();
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
                Schedule.this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue19, true);
                final int color = typedValue19.data;
                FrameLayout statusBar = (FrameLayout) findViewById(R.id.statusBar);
                statusBar.setBackgroundColor(color);
            }

            // Fix issues for Lollipop, setting Status Bar color primary dark
            if (Build.VERSION.SDK_INT >= 21) {
                TypedValue typedValue21 = new TypedValue();
                Schedule.this.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue21, true);
                final int color = typedValue21.data;
                FrameLayout statusBar = (FrameLayout) findViewById(R.id.statusBar);
                statusBar.setBackgroundColor(color);
            }
        }
        // Fix landscape issues (only Lollipop)
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (Build.VERSION.SDK_INT >= 19){
                TypedValue typedValue19 = new TypedValue();
                Schedule.this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue19, true);
                final int color = typedValue19.data;
                FrameLayout statusBar = (FrameLayout) findViewById(R.id.statusBar);
                statusBar.setBackgroundColor(color);
            }
            if (Build.VERSION.SDK_INT >= 21){
                TypedValue typedValue = new TypedValue();
                Schedule.this.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
            }
        }
    }
}