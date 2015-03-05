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
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.codejune.sutaekhighschool.R;
import com.codejune.sutaekhighschool.ui.FloatingActionButton;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import me.drakeet.materialdialog.MaterialDialog;

public class NoticesContents extends ActionBarActivity {
    //ToolBar
    private Toolbar toolbar;
    String URL;
    String Title;
    String Author;
    String Date;
    TextView tvContents, tvFile1 ,tvFile2, tvFile3, tvFile4, tvFile5;
    CardView cvFile;
    String cons = "", filename1 = "", filename2 = "", filename3 = "", filename4 = "", filename5 = "";
    private NoticeOpenTask noticeTask;
    MaterialDialog mMaterialDialog;
    FloatingActionButton button;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_contents);
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
            tvFile1 = (TextView) findViewById(R.id.tv_file1);
            tvFile2 = (TextView) findViewById(R.id.tv_file2);
            tvFile3 = (TextView) findViewById(R.id.tv_file3);
            tvFile4 = (TextView) findViewById(R.id.tv_file4);
            tvFile5 = (TextView) findViewById(R.id.tv_file5);
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

    private class NoticeOpenTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {

            if (!urls[0].equals("")) {

                try {

                    Document doc = Jsoup.connect(urls[0]).get();
                    Elements rawcontents = doc.select("#bbsWrap div table tbody tr:eq(4) td p");
                    Elements rawfile1 = doc.select("#bbsWrap div table tbody tr:eq(5) td a");
                    Elements rawfile2 = doc.select("#bbsWrap div table tbody tr:eq(6) td a");
                    Elements rawfile3 = doc.select("#bbsWrap div table tbody tr:eq(7) td a");
                    Elements rawfile4 = doc.select("#bbsWrap div table tbody tr:eq(8) td a");
                    Elements rawfile5 = doc.select("#bbsWrap div table tbody tr:eq(9) td a");


                    for (Element el : rawcontents) {
                        String con = el.text();
                        Log.d("rawcontents", con);
                        con = con.trim();
                        Log.d("CONS", con);
                        cons = cons + con + "\n";
                    }

                    for (Element el : rawfile1) {
                        String filedata1 = "http://www.sutaek.hs.kr/" + el.attr("href") + "<br>";
                        String filetitle1 = el.text();
                        filename1 = filename1 + "<a href=\""
                                + filedata1.replace("/common/", "/m/") //모바일 다운로드시 인코딩문제 해결
                                + "\">" + filetitle1 + "</a>"  +"<br>";
                        Log.d("CONS2", filedata1);
                        Log.d("CONS2", filename1);
                    }
                    if (!rawfile2.equals("") || !rawfile2.equals(null)) {

                        for (Element el : rawfile2) {
                            String filedata2 = "http://www.sutaek.hs.kr/" + el.attr("href") + "<br>";
                            String filetitle2 = el.text();
                            filename2 = filename2 + "<a href=\""
                                    + filedata2.replace("/common/", "/m/") //모바일 다운로드시 인코딩문제 해결
                                    + "\">" + filetitle2 + "</a>"  +"<br>";
                            Log.d("CONS2", filedata2);
                            Log.d("CONS2", filename2);
                        }
                    }

                    if (!rawfile3.equals("") || !rawfile3.equals(null)) {
                        for (Element el : rawfile3) {
                            String filedata3 = "http://www.sutaek.hs.kr/" + el.attr("href") + "<br>";
                            String filetitle3 = el.text();
                            filename3 = filename3 + "<a href=\""
                                    + filedata3.replace("/common/", "/m/") //모바일 다운로드시 인코딩문제 해결
                                    + "\">" + filetitle3 + "</a>"  +"<br>";
                            Log.d("CONS2", filedata3);
                            Log.d("CONS2", filename3);
                        }
                    }

                    if (!rawfile4.equals("") || !rawfile4.equals(null)) {
                        for (Element el : rawfile4) {
                            String filedata4 = "http://www.sutaek.hs.kr/" + el.attr("href") + "<br>";
                            String filetitle4 = el.text();
                            filename4 = filename4 + "<a href=\""
                                    + filedata4.replace("/common/", "/m/") //모바일 다운로드시 인코딩문제 해결
                                    + "\">" + filetitle4 + "</a>"  +"<br>";
                            Log.d("CONS2", filedata4);
                            Log.d("CONS2", filename4);
                        }
                    }

                    if (!rawfile5.equals("") || !rawfile5.equals(null)) {
                        for (Element el : rawfile5) {
                            String filedata5 = "http://www.sutaek.hs.kr/" + el.attr("href") + "<br>";
                            String filetitle5 = el.text();
                            filename5 = filename5 + "<a href=\""
                                    + filedata5.replace("/common/", "/m/") //모바일 다운로드시 인코딩문제 해결
                                    + "\">" + filetitle5 + "</a>"  +"<br>";
                            Log.d("CONS2", filedata5);
                            Log.d("CONS2", filename5);
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
                tvContents.setText(getString(R.string.attachment));
            } else {
                tvContents.setText(cons);
            }
            if (!filename1.equals("")) {
                cvFile.setVisibility(View.VISIBLE);
                tvFile1.setText(Html.fromHtml(filename1));
                tvFile1.setMovementMethod(LinkMovementMethod.getInstance());
            }
            if (!filename2.equals("")) {
                tvFile2.setVisibility(View.VISIBLE);
                tvFile2.setText(Html.fromHtml(filename2));
                tvFile2.setMovementMethod(LinkMovementMethod.getInstance());
            }
            if (!filename3.equals("")) {
                tvFile3.setVisibility(View.VISIBLE);
                tvFile3.setText(Html.fromHtml(filename3));
                tvFile3.setMovementMethod(LinkMovementMethod.getInstance());
            }
            if (!filename4.equals("")) {
                tvFile4.setVisibility(View.VISIBLE);
                tvFile4.setText(Html.fromHtml(filename4));
                tvFile4.setMovementMethod(LinkMovementMethod.getInstance());
            }
            if (!filename5.equals("")) {
                tvFile5.setVisibility(View.VISIBLE);
                tvFile5.setText(Html.fromHtml(filename5));
                tvFile5.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
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
                NoticesContents.this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue19, true);
                final int color = typedValue19.data;
                FrameLayout statusBar = (FrameLayout) findViewById(R.id.statusBar);
                statusBar.setBackgroundColor(color);
            }

            // Fix issues for Lollipop, setting Status Bar color primary dark
            if (Build.VERSION.SDK_INT >= 21) {
                TypedValue typedValue21 = new TypedValue();
                NoticesContents.this.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue21, true);
                final int color = typedValue21.data;
                FrameLayout statusBar = (FrameLayout) findViewById(R.id.statusBar);
                statusBar.setBackgroundColor(color);
            }
        }
        // Fix landscape issues (only Lollipop)
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (Build.VERSION.SDK_INT >= 19){
                TypedValue typedValue19 = new TypedValue();
                NoticesContents.this.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue19, true);
                final int color = typedValue19.data;
                FrameLayout statusBar = (FrameLayout) findViewById(R.id.statusBar);
                statusBar.setBackgroundColor(color);
            }
            if (Build.VERSION.SDK_INT >= 21){
                TypedValue typedValue = new TypedValue();
                NoticesContents.this.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
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