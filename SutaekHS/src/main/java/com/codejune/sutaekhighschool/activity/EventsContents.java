package com.codejune.sutaekhighschool.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.codejune.sutaekhighschool.R;
import com.codejune.sutaekhighschool.ui.FloatingActionButton;
import com.codejune.sutaekhighschool.util.ImageDownloader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import me.drakeet.materialdialog.MaterialDialog;

public class EventsContents extends ActionBarActivity {
    //ToolBar
    private Toolbar toolbar;
    String URL;
    String Title;
    String Author;
    String Date;
    TextView tvContents;
    ImageView tvImg1, tvImg2, tvImg3, tvImg4, tvImg5;
    CardView cvFile;
    String cons = "", filename1 = "", filename2 = "", filename3 = "", filename4 = "", filename5 = "";
    private NoticeOpenTask noticeTask;
    MaterialDialog mMaterialDialog;
    FloatingActionButton button;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_contents);
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
            //Setup Status Bar and Toolbar
            toolbarStatusBar();

            // Fix issues for each version and modes (check method at end of this file)
            navigationBarStatusBar();
            URL = getIntent().getStringExtra("URL");
            tvContents = (TextView) findViewById(R.id.tv_contents);
            tvImg1 = (ImageView) findViewById(R.id.tvimg1);
            tvImg2 = (ImageView) findViewById(R.id.tvimg2);
            tvImg3 = (ImageView) findViewById(R.id.tvimg3);
            tvImg4 = (ImageView) findViewById(R.id.tvimg4);
            tvImg5 = (ImageView) findViewById(R.id.tvimg5);
            cvFile = (CardView) findViewById(R.id.card_file);
            Intent in = getIntent();
            Log.d("CONTENT", in.getStringExtra("URL"));
            noticeTask = new NoticeOpenTask();
            noticeTask.execute(in.getStringExtra("URL"), "", "");
            button = (FloatingActionButton) findViewById(R.id.gotoweb);
            button.setSize(FloatingActionButton.SIZE_NORMAL);
            button.setColorNormalResId(R.color.md_yellow_500);
            button.setColorPressedResId(R.color.md_yellow_200);
            button.setIcon(R.drawable.ic_open_in_new_white_48dp);
            button.setStrokeVisible(false);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(URL));
                    startActivity(intent);
                }
            });
        }
    }

    private class NoticeOpenTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {

            if (!urls[0].equals("")) {

                try {

                    Document doc = Jsoup.connect(urls[0]).get();
                    Elements rawcontents = doc.select("#bbsWrap div table tbody tr:eq(4) td p");
                    Elements rawimg1 = doc.select("#bbsWrap div table tbody tr:eq(5) td img");
                    Elements rawimg2 = doc.select("#bbsWrap div table tbody tr:eq(6) td img");
                    Elements rawimg3 = doc.select("#bbsWrap div table tbody tr:eq(7) td img");
                    Elements rawimg4 = doc.select("#bbsWrap div table tbody tr:eq(8) td img");
                    Elements rawimg5 = doc.select("#bbsWrap div table tbody tr:eq(9) td img");


                    for (Element el : rawcontents) {
                        String con = el.text();
                        Log.d("rawcontents", con);
                        con = con.trim();
                        Log.d("CONS", con);
                        cons = cons + con + "\n";
                    }

                    for (Element el : rawimg1) {
                        String imgdata1 = "http://www.sutaek.hs.kr/" + el.attr("src");
                        new ImageDownloader(tvImg1).execute(imgdata1);
                        filename1 = filename1 + "<img src=\"" + imgdata1 + "\">";
                        Log.d("ImgLink", imgdata1);
                    }
                    if (!rawimg2.equals("") || !rawimg2.equals(null)) {

                        for (Element el : rawimg2) {
                            String imgdata2 = "http://www.sutaek.hs.kr/" + el.attr("src");
                            new ImageDownloader(tvImg2).execute(imgdata2);
                            filename2 = filename2 + "<img src=\"" + imgdata2 + "\">";
                            Log.d("ImgLink", imgdata2);
                        }
                    }

                    if (!rawimg3.equals("") || !rawimg3.equals(null)) {
                        for (Element el : rawimg3) {
                            String imgdata3 = "http://www.sutaek.hs.kr/" + el.attr("src");
                            new ImageDownloader(tvImg3).execute(imgdata3);
                            filename3 = filename3 + "<img src=\"" + imgdata3 + "\">";

                            Log.d("ImgLink", imgdata3);
                        }
                    }

                    if (!rawimg4.equals("") || !rawimg4.equals(null)) {
                        for (Element el : rawimg4) {
                            String imgdata4 = "http://www.sutaek.hs.kr/" + el.attr("src");
                            new ImageDownloader(tvImg4).execute(imgdata4);
                            filename4 = filename4 + "<img src=\"" + imgdata4 + "\">";

                            Log.d("ImgLink", imgdata4);
                        }
                    }

                    if (!rawimg5.equals("") || !rawimg5.equals(null)) {
                        for (Element el : rawimg5) {
                            String imgdata5 = "http://www.sutaek.hs.kr/" + el.attr("src");
                            new ImageDownloader(tvImg5).execute(imgdata5);
                            filename5 = filename5 + "<img src=\"" + imgdata5 + "\">";
                            Log.d("ImgLink", imgdata5);
                        }
                    }

                    Log.d("CON", cons);
                    return cons;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return cons;
        }

        @Override
        protected void onPostExecute(String cons) {
            super.onPostExecute(cons);
            if (cons.equals("") || cons == null || cons.equals(" ")) {
                tvContents.setText("<첨부파일 참조>");
            } else {
                tvContents.setText(cons);
            }
            if (!filename1.equals("")) {
                cvFile.setVisibility(View.VISIBLE);
            }
            if (!filename2.equals("")) {
                tvImg2.setVisibility(View.VISIBLE);
            }
            if (!filename3.equals("")) {
                tvImg3.setVisibility(View.VISIBLE);
            }
            if (!filename4.equals("")) {
                tvImg4.setVisibility(View.VISIBLE);
            }
            if (!filename5.equals("")) {
                tvImg5.setVisibility(View.VISIBLE);
            }
        }
    }

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
        Title =  getIntent().getStringExtra("title");
        Date = getIntent().getStringExtra("date");
        Author = getIntent().getStringExtra("author");
        toolbar.setTitle(Title);
        toolbar.setSubtitle(Author+" - "+Date);

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
                EventsContents.this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue19, true);
                final int color = typedValue19.data;
                FrameLayout statusBar = (FrameLayout) findViewById(R.id.statusBar);
                statusBar.setBackgroundColor(color);
            }

            // Fix issues for Lollipop, setting Status Bar color primary dark
            if (Build.VERSION.SDK_INT >= 21) {
                TypedValue typedValue21 = new TypedValue();
                EventsContents.this.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue21, true);
                final int color = typedValue21.data;
                FrameLayout statusBar = (FrameLayout) findViewById(R.id.statusBar);
                statusBar.setBackgroundColor(color);
            }
        }
        // Fix landscape issues (only Lollipop)
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (Build.VERSION.SDK_INT >= 19){
                TypedValue typedValue19 = new TypedValue();
                EventsContents.this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue19, true);
                final int color = typedValue19.data;
                FrameLayout statusBar = (FrameLayout) findViewById(R.id.statusBar);
                statusBar.setBackgroundColor(color);
            }
            if (Build.VERSION.SDK_INT >= 21){
                TypedValue typedValue = new TypedValue();
                EventsContents.this.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_post_view, menu);
        // 공유 버튼 찾기
        MenuItem menuItem = menu.findItem(R.id.action_share);
        // ShareActionProvider 얻기
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // 공유 버튼에 사용할 Intent 를 만들어 주는 메서드를 호출합니다.
        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
        return true;
    }

    private Intent createShareIntent() {
        //액션은 ACTION_SEND 로 합니다.
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        //Flag 를 설정해 줍니다. 공유하기 위해 공유에 사용할 다른 앱의 하나의 Activity 만 열고,
        //다시 돌아오면 열었던 Activity 는 꺼야 하기 때문에
        //FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET 로 해줍니다.
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        //공유할 것의 형태입니다. 우리는 텍스트를 공유합니다.
        shareIntent.setType("text/plain");
        //보낼 데이터를 Extra 로 넣어줍니다.
        String ShareData;
        try {
            ShareData =
                    getIntent().getStringExtra("title") + "\n\n"
                            + getIntent().getStringExtra("URL");
        }catch (Exception e){
            ShareData = getResources().getString(R.string.error);
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT,ShareData);
        return shareIntent;
    }

}