package com.codejune.sutaekhighschool.fragment;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.codejune.sutaekhighschool.R;
import com.codejune.sutaekhighschool.activity.NParentsContents;
import com.codejune.sutaekhighschool.util.PostListAdapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Notices_Parents extends Fragment {
    private ArrayList<String> titlearray;
    private ArrayList<String> titleherfarray;
    private ArrayList<String> authorarray;
    private ArrayList<String> datearray;
    private PostListAdapter adapter;
    private SwipeRefreshLayout SRL;
    ListView listview;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        View view = inflater.inflate(R.layout.activity_notices_parents, null);
        listview = (ListView) view.findViewById(R.id.listView);
        SRL = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
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

        return view;
    }



    private AdapterView.OnItemClickListener GoToWebPage = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View clickedView,
                                int pos, long id) {
            String herfitem = titleherfarray.get(pos);
            String title = titlearray.get(pos);
            String date = datearray.get(pos);
            String author = authorarray.get(pos);

            Intent intent = new Intent(getActivity(),
                    NParentsContents.class);
            intent.putExtra("URL", herfitem);
            intent.putExtra("title", title);
            intent.putExtra("date", date);
            intent.putExtra("author", author);
            startActivity(intent);
        }
    };

    private void networkTask() {
        final Handler mHandler = new Handler();
        new Thread() {

            public void run() {
                mHandler.post(new Runnable() {

                    public void run() {
                        SRL.setRefreshing(true);
                    }
                });
                try {
                    titlearray = new ArrayList<String>();
                    titleherfarray = new ArrayList<String>();
                    authorarray = new ArrayList<String>();
                    datearray = new ArrayList<String>();
                    Document doc = Jsoup
                            .connect(
                                    "http://www.sutaek.hs.kr/main.php?menugrp=020500&master=bbs&act=list&master_sid=4")
                            .get();
                    Elements rawdata = doc.select(".listbody a");
                    Elements rawauthordata = doc.select("td:eq(3)");
                    Elements rawdatedata = doc.select("td:eq(4)");
                    String titlestring = rawdata.toString();
                    Log.i("Notices", "Parsed Strings" + titlestring);

                    for (Element el : rawdata) {
                        String titlherfedata = el.attr("href");
                        String titledata = el.attr("title");
                        titleherfarray.add("http://www.sutaek.hs.kr/"
                                + titlherfedata);
                        titlearray.add(titledata);
                    }
                    Log.i("Notices", "Parsed Link Array Strings"
                            + titleherfarray);
                    Log.i("Notices", "Parsed Array Strings" + titlearray);

                    for (Element el : rawauthordata) {
                        String authordata = el.text();
                        Log.d("Author", el.text());
                        authorarray.add(authordata);
                    }
                    for (Element el : rawdatedata) {
                        String datedata = el.text();
                        Log.d("Date", el.text());
                        datearray.add(datedata);
                    }

                } catch (IOException e) {
                    e.printStackTrace();

                }

                mHandler.post(new Runnable() {
                    public void run() {
                        adapter = new PostListAdapter(getActivity(),
                                titlearray, datearray, authorarray);
                        listview.setAdapter(adapter);
                        listview.setOnItemClickListener(GoToWebPage);
                        handler.sendEmptyMessage(0);
                        SRL.setRefreshing(false);

                    }
                });

            }
        }.start();

    }

}
