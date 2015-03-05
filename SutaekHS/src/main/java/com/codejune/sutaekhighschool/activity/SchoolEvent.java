package com.codejune.sutaekhighschool.activity;

import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.codejune.sutaekhighschool.util.PostListAdapter;
import com.codejune.sutaekhighschool.R;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import me.drakeet.materialdialog.MaterialDialog;

public class SchoolEvent extends ActionBarActivity {
    //ToolBar
    private Toolbar toolbar;
    private ArrayList<String> titlearray;
    private ArrayList<String> titleherfarray;
    private ArrayList<String> authorarray;
    private ArrayList<String> datearray;
    private PostListAdapter adapter;
    private SwipeRefreshLayout SRL;
    ListView listview;
    MaterialDialog mMaterialDialog;

    private final Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {

        }
    };
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schoolevent);
        //Setup Status Bar and Toolbar
        toolbarStatusBar();

        // Fix issues for each version and modes (check method at end of this file)
        navigationBarStatusBar();
        listview = (ListView) findViewById(R.id.listView);
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
            SRL = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
            SRL.setColorSchemeColors(Color.rgb(231, 76, 60),
                    Color.rgb(46, 204, 113),
                    Color.rgb(41, 128, 185),
                    Color.rgb(241, 196, 15));
            SRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    networkTask();
                }
            });
            networkTask();
            Crouton.makeText(this, R.string.schoolevent_info, Style.INFO).show();
        }

    }
    private AdapterView.OnItemClickListener GoToWebPage = new AdapterView.OnItemClickListener()
    {
        public void onItemClick(AdapterView<?> adapterView, View clickedView, int pos, long id)
        {
            String herfitem = titleherfarray.get(pos);
            String title = titlearray.get(pos);
            String date = datearray.get(pos);
            String author = authorarray.get(pos);

            Intent intent = new Intent(SchoolEvent.this,
                    EventsContents.class);
            intent.putExtra("URL", herfitem);
            intent.putExtra("title", title);
            intent.putExtra("date", date);
            intent.putExtra("author", author);
            startActivity(intent);
        }
    };

    //Method for get list of notices
    private void networkTask(){
        final Handler mHandler = new Handler();
        new Thread()
        {

            public void run()
            {

                mHandler.post(new Runnable(){

                    public void run()
                    {
                        SRL.setRefreshing(true);
                    }
                });

                //Task

                //Notices URL
                try {
                    titlearray = new ArrayList<String>();
                    titleherfarray = new ArrayList<String>();
                    authorarray = new ArrayList<String>();
                    datearray = new ArrayList<String>();
                    //파싱할 페이지 URL
                    Document doc = Jsoup.connect("http://www.sutaek.hs.kr/main.php?menugrp=020400&master=bbs&act=list&master_sid=3").get();
                    Elements rawmaindata = doc.select(".listbody a"); //Get contents from tags,"a" which are in the class,"listbody"
                    Elements rawauthordata = doc.select("td:eq(3)"); //작성자 이름 얻기 - 3번째 td셀 에서 얻기
                    Elements rawdatedata = doc.select("td:eq(4)"); //작성 날자 얻기 - 4번째 td셀 에서 얻기
                    String titlestring = rawmaindata.toString();
                    Log.i("Notices","Parsed Strings" + titlestring);


                    //파싱할 데이터로 배열 생성
                    for (Element el : rawmaindata) {
                        String titlherfedata = el.attr("href");
                        String titledata = el.attr("title");
                        titleherfarray.add("http://www.sutaek.hs.kr/" + titlherfedata); // add value to ArrayList
                        titlearray.add(titledata); // add value to ArrayList
                    }
                    Log.i("Notices","Parsed Link Array Strings" + titleherfarray);
                    Log.i("Notices","Parsed Array Strings" + titlearray);

                    for (Element el : rawauthordata){
                        String authordata = el.text();
                        Log.d("Author",el.text());
                        authorarray.add(authordata);
                    }
                    for (Element el : rawdatedata){
                        String datedata = el.text();
                        Log.d("Date",el.text());
                        datearray.add(datedata);
                    }


                } catch (IOException e) {
                    e.printStackTrace();

                }


                mHandler.post(new Runnable()
                {
                    public void run()
                    {
                        //UI Task
                        //배열로 어뎁터 설정
                        adapter = new PostListAdapter(SchoolEvent.this, titlearray,datearray,authorarray);
                        listview.setAdapter(adapter);
                        listview.setOnItemClickListener(GoToWebPage);
                        handler.sendEmptyMessage(0);
                        SRL.setRefreshing(false);

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
                SchoolEvent.this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue19, true);
                final int color = typedValue19.data;
                FrameLayout statusBar = (FrameLayout) findViewById(R.id.statusBar);
                statusBar.setBackgroundColor(color);
            }

            // Fix issues for Lollipop, setting Status Bar color primary dark
            if (Build.VERSION.SDK_INT >= 21) {
                TypedValue typedValue21 = new TypedValue();
                SchoolEvent.this.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue21, true);
                final int color = typedValue21.data;
                FrameLayout statusBar = (FrameLayout) findViewById(R.id.statusBar);
                statusBar.setBackgroundColor(color);
            }
        }
        // Fix landscape issues (only Lollipop)
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (Build.VERSION.SDK_INT >= 19){
                TypedValue typedValue19 = new TypedValue();
                SchoolEvent.this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue19, true);
                final int color = typedValue19.data;
                FrameLayout statusBar = (FrameLayout) findViewById(R.id.statusBar);
                statusBar.setBackgroundColor(color);
            }
            if (Build.VERSION.SDK_INT >= 21){
                TypedValue typedValue = new TypedValue();
                SchoolEvent.this.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
            }
        }
    }
}