package com.codejune.sutaekhighschool.fragment;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codejune.sutaekhighschool.activity.MealActivity;
import com.codejune.sutaekhighschool.activity.Schedule;
import com.codejune.sutaekhighschool.util.MealLoadHelper;
import com.codejune.sutaekhighschool.R;
import com.codejune.sutaekhighschool.activity.NoticesActivity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class Favorites extends Fragment {
    private String URL = "http://www.sutaek.hs.kr/main.php?menugrp=020102&master=" +
            "diary&act=list&master_sid=1";
    String MealString;
    String ScheduleString;
    String NoticesParentString1;
    String NoticesParentString2;
    String NoticesParentDate1;
    String NoticesParentDate2;
    String NoticesString1;
    String NoticesString2;
    String NoticesDate1;
    String NoticesDate2;
    int AMorPM;
    int MONTH;
    int DAYofWEEK;
    int DAYofMONTH;
    String[] lunchstring = new String[7];
    String[] dinnerstring = new String[7];
    private ArrayList<String> titlearray_np;
    private ArrayList<String> titlearray_n;
    private ArrayList<String> datearray1;
    private ArrayList<String> datearray2;
    private ArrayList<String> dayarray;
    private ArrayList<String> schedulearray;
    private TextView MEAL;
    private TextView MEALTIME;
    private TextView SCHEDULE;
    private TextView NOTIPARNTS1;
    private TextView NOTIPARNTSDATE1;
    private TextView NOTIPARNTS2;
    private TextView NOTIPARNTSDATE2;
    private TextView NOTICES1;
    private TextView NOTICESDATE1;
    private TextView NOTICES2;
    private TextView NOTICESDATE2;
    private TextView DAY;
    private SwipeRefreshLayout SRL;
    SharedPreferences sp;
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_favorites, null);
        Calendar Cal = Calendar.getInstance();
        AMorPM = Cal.get(Calendar.AM_PM);
        MONTH = Cal.get(Calendar.MONTH);
        DAYofWEEK = Cal.get(Calendar.DAY_OF_WEEK);
        DAYofMONTH = Cal.get(Calendar.DAY_OF_MONTH);
        MEAL = (TextView)view.findViewById(R.id.mealdata);
        MEALTIME = (TextView)view.findViewById(R.id.mealtime);
        SCHEDULE = (TextView)view.findViewById(R.id.scheduledata);
        NOTIPARNTS1 = (TextView)view.findViewById(R.id.notiparentdata1);
        NOTIPARNTSDATE1  = (TextView)view.findViewById(R.id.notiparentdate1);
        NOTIPARNTS2 = (TextView)view.findViewById(R.id.notiparentdata2);
        NOTIPARNTSDATE2  = (TextView)view.findViewById(R.id.notiparentdate2);
        NOTICES1 = (TextView)view.findViewById(R.id.noticedata1);
        NOTICESDATE1  = (TextView)view.findViewById(R.id.noticedate1);
        NOTICES2 = (TextView)view.findViewById(R.id.noticedata2);
        NOTICESDATE2  = (TextView)view.findViewById(R.id.noticedate2);
        DAY = (TextView)view.findViewById(R.id.day);
        View meal = view.findViewById(R.id.meal);
        View notices_parents = view.findViewById(R.id.notices_parents);
        View notices = view.findViewById(R.id.notices);
        View schedule = view.findViewById(R.id.schedule);
        SRL = (SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);
        SRL.setColorSchemeColors(Color.rgb(231, 76, 60),
                Color.rgb(46, 204, 113), Color.rgb(41, 128, 185),
                Color.rgb(241, 196, 15));
        SRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isNetworkConnected(getActivity())) {
                    Crouton.makeText(getActivity(), R.string.network_connection_warning, Style.INFO).show();
                    SRL.setRefreshing(false);
                }
                else{
                    if (sp.getBoolean("meal", true) && isNetworkConnected(getActivity())) getMeal();
                    if (sp.getBoolean("schedule", true) && isNetworkConnected(getActivity())) getSchedule();
                    if (sp.getBoolean("notices", true) && isNetworkConnected(getActivity())) getNotices();
                    if (sp.getBoolean("notices_parents", true) && isNetworkConnected(getActivity())) getNParents();
                }
            }
        });
        sp = getActivity().getSharedPreferences("sutaek", Context.MODE_PRIVATE);
        if (sp.getBoolean("meal", true)) {
            meal.setVisibility(View.VISIBLE);
            if (isNetworkConnected(getActivity())) {
            } else {
                getMeal();
            }
        } else {
            meal.setVisibility(View.GONE);
        }
        if (sp.getBoolean("schedule", true)) {
            schedule.setVisibility(View.VISIBLE);
            if (!isNetworkConnected(getActivity())) {
            } else {
                getSchedule();
            }
        } else {
            schedule.setVisibility(View.GONE);
        }
        if (sp.getBoolean("notices", true)) {
            notices.setVisibility(View.VISIBLE);
            if (!isNetworkConnected(getActivity())) {
            } else {
                getNotices();
            }
        } else {
            notices.setVisibility(View.GONE);
        }
        if (sp.getBoolean("notices_parents", true)) {
            notices_parents.setVisibility(View.VISIBLE);
            if (!isNetworkConnected(getActivity())) {
            } else {
                getNParents();
            }
        } else {
            notices_parents.setVisibility(View.GONE);
        }

        meal.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MealActivity.class);
                startActivity(intent);
            }
        });

        notices_parents.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NoticesActivity.class);
                startActivity(intent);
            }
        });

        notices.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NoticesActivity.class);
                startActivity(intent);
            }
        });

        schedule.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Schedule.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        View notices = view.findViewById(R.id.notices);
        View notices_parents = view.findViewById(R.id.notices_parents);
        View meal = view.findViewById(R.id.meal);
        View schedule = view.findViewById(R.id.schedule);
        if (sp.getBoolean("meal", true)) {
            meal.setVisibility(View.VISIBLE);
            if (isNetworkConnected(getActivity())) {
                getMeal();
            }
        } else {
            meal.setVisibility(View.GONE);
        }
        if (sp.getBoolean("notices_parents", true)) {
            notices_parents.setVisibility(View.VISIBLE);
            if (isNetworkConnected(getActivity())) {
                getNParents();
            }
        } else {
            notices_parents.setVisibility(View.GONE);
        }
        if (sp.getBoolean("notices", true)) {
            notices.setVisibility(View.VISIBLE);
            if (isNetworkConnected(getActivity())) {
                getNParents();
            }
        } else {
            notices.setVisibility(View.GONE);
        }
        if (sp.getBoolean("schedule", true)) {
            schedule.setVisibility(View.VISIBLE);
            if (isNetworkConnected(getActivity())) {
                getSchedule();
            }
        } else {
            schedule.setVisibility(View.GONE);
        }
    }

    public void getMeal() {
        SRL.setRefreshing(true);
        final Handler mHandler = new Handler();
        new Thread() {

            public void run() {
                try {
                    lunchstring = MealLoadHelper.getMeal("goe.go.kr", "J100000656", "4", "04", "2"); //Get Lunch Menu Date
                    dinnerstring = MealLoadHelper.getMeal("goe.go.kr", "J100000656", "4", "04", "3"); //Get Dinner Menu Date
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mHandler.post(new Runnable() {
                    public void run() {
                        if (AMorPM == Calendar.AM) {
                            MealString = lunchstring[DAYofWEEK - 1];
                            MEALTIME.setText(R.string.lunch);
                        } else {
                            MealString = dinnerstring[DAYofWEEK - 1];
                            MEALTIME.setText(R.string.dinner);
                        }
                        SRL.setRefreshing(false);
                        mHandler.sendEmptyMessage(0);
                        TimeZone kst = TimeZone.getTimeZone("KST");
                        Calendar cal = Calendar.getInstance ( kst );
                        DAY.setText(( cal.get ( Calendar.MONTH ) + 1 ) + "월 "
                                + cal.get ( Calendar.DATE ) + "일 ");
                        if (MealString == null || MealString.equals("") || " ".equals(MealString)) {
                            MealString = getResources().getString(R.string.mealnone);
                        }
                        MEAL.setText(MealString);
                    }
                });
            }
        }.start();
    }

    void getSchedule() {
        SRL.setRefreshing(true);
        schedulearray = new ArrayList<String>();
        dayarray = new ArrayList<String>();
        final Handler mHandler = new Handler();
        new Thread() {
            public void run() {
                try {
                    int skipcount = 0;
                    boolean skipped = false;

                    Document doc = Jsoup.connect(URL).get();
                    Elements rawdaydata = doc.select(".listDay");
                    for (Element el : rawdaydata) {
                        String daydata = el.text();
                        if (daydata.equals("") | daydata == null) {
                            if (skipped) {
                            } else {
                                skipcount++;
                            }
                        } else {
                            dayarray.add(daydata);
                            skipped = true;
                        }
                    }

                    Elements rawscheduledata = doc.select(".listData");
                    for (Element el : rawscheduledata) {
                        String scheduledata = el.text();
                        if (skipcount > 0) {
                            skipcount--;
                        } else {
                            schedulearray.add(scheduledata);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mHandler.post(new Runnable() {
                    public void run() {
                        try {
                            ScheduleString = schedulearray.get(DAYofMONTH - 1);
                        } catch (Exception e) {
                            ScheduleString = getResources().getString(R.string.error);
                        }
                        if (ScheduleString == null || ScheduleString.equals("")) {
                            ScheduleString = getResources().getString(R.string.schedulenone);
                        }
                        mHandler.sendEmptyMessage(0);
                        SRL.setRefreshing(false);
                        SCHEDULE.setText(ScheduleString);
                    }
                });
            }
        }.start();
    }

    void getNotices() {
        final View subNoti = view.findViewById(R.id.subnotice);
        SRL.setRefreshing(true);
        titlearray_n = new ArrayList<String>();
        datearray1 = new ArrayList<String>();
        final Handler mHandler = new Handler();
        new Thread() {
            public void run() {
                try {

                    Document doc1 = Jsoup.connect("http://www.sutaek.hs.kr/main.php?menugrp=020500&master=bbs&act=list&master_sid=1").get();
                    Elements rawdata1 = doc1.select(".listbody a");
                    Elements rawdatedata1 = doc1.select("td:eq(4)");

                    for (Element el : rawdata1) {
                        String titledata = el.attr("title");
                        titlearray_n.add(titledata);
                    }

                    for (Element el : rawdatedata1) {
                        String datedata = el.text();
                        datearray1.add(datedata);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mHandler.post(new Runnable() {
                    public void run() {
                        try {
                            NoticesString1 = titlearray_n.get(0);
                            NoticesDate1 = datearray1.get(0);
                            NoticesString2 = titlearray_n.get(1);
                            NoticesDate2 = datearray1.get(1);
                            subNoti.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            NoticesString1 = getResources().getString(R.string.error);
                            subNoti.setVisibility(View.GONE);
                        }

                        mHandler.sendEmptyMessage(0);
                        SRL.setRefreshing(false);
                        NOTICES1.setText(NoticesString1);
                        NOTICESDATE1.setText("등록일 : " + NoticesDate1);
                        NOTICES2.setText(NoticesString2);
                        NOTICESDATE2.setText("등록일 : " + NoticesDate2);
                    }
                });
            }
        }.start();
    }

    void getNParents() {
        final View subNotiparent = view.findViewById(R.id.subnotipatent);
        SRL.setRefreshing(true);
        titlearray_np = new ArrayList<String>();
        datearray2 = new ArrayList<String>();
        final Handler mHandler = new Handler();
        new Thread() {
            public void run() {
                try {

                    Document doc2 = Jsoup.connect("http://www.sutaek.hs.kr/main.php?menugrp=020500&master=bbs&act=list&master_sid=4").get();
                    Elements rawdata2 = doc2.select(".listbody a");
                    Elements rawdatedata2 = doc2.select("td:eq(4)");

                    for (Element el : rawdata2) {
                        String titledata = el.attr("title");
                        titlearray_np.add(titledata);
                    }

                    for (Element el : rawdatedata2) {
                        String datedata = el.text();
                        datearray2.add(datedata);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mHandler.post(new Runnable() {
                    public void run() {
                        try {
                            NoticesParentString1 = titlearray_np.get(0);
                            NoticesParentDate1 = datearray2.get(0);
                            NoticesParentString2 = titlearray_np.get(1);
                            NoticesParentDate2 = datearray2.get(1);
                            subNotiparent.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            NoticesParentString1 = getResources().getString(R.string.error);
                            subNotiparent.setVisibility(View.GONE);
                        }

                        mHandler.sendEmptyMessage(0);
                        SRL.setRefreshing(false);
                        NOTIPARNTS1.setText(NoticesParentString1);
                        NOTIPARNTSDATE1.setText("등록일 : " + NoticesParentDate1);
                        NOTIPARNTS2.setText(NoticesParentString2);
                        NOTIPARNTSDATE2.setText("등록일 : " + NoticesParentDate2);
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
}