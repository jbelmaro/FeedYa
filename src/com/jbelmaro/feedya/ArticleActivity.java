package com.jbelmaro.feedya;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.jbelmaro.feedya.util.SaveForLaterItem;
import com.jbelmaro.feedya.util.Utils;

public class ArticleActivity extends FragmentActivity {

    ArticlePagerAdapter mArticlePagerAdapter;
    ViewPager mViewPager;
    TextView titleV;
    WebView content;
    String url;
    String idArticle;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article);

        Bundle extras = getIntent().getExtras();
        titleV = (TextView) findViewById(R.id.title_list_activity_news);
        content = (WebView) findViewById(R.id.content_news);
        titleV.setText(Html.fromHtml(extras.getString("titulo").toUpperCase(Locale.getDefault())));

        StringBuilder sb = new StringBuilder();
        sb.append("<html><head>"
                + "<link href='http://fonts.googleapis.com/css?family=Droid+Serif:700' rel='stylesheet' type='text/css'>"
                + "<style>*{background-color:#eeeeee;}"
                + "body{font-family: 'Calibri';}"
                + ".cuerpo{text-align:justify;}img {max-width: 100%;}"
                + "h1{font-family: 'Droid Serif', serif;}a:link {color:#316CE2;}h5{color:#316CE2;}hr{display: block; height: 1px;border: 0; border-top: 1px solid #316CE2;margin: 1em 0; padding: 0;} "
                + "</style>"
                + "<meta name=\"viewport\" content=\"width = device-width,initial-scale=1, maximum-scale=1\"/>"
                + "</head><body>");
        sb.append("<h1>" + extras.getString("noticiaTitulo") + "</h1>");
        sb.append("<h5>" + extras.getString("autorNoticia") + "<br></h5>");
        sb.append("<hr /><div class=\"cuerpo\">" + extras.getString("noticia") + "</div>");

        url = extras.getString("noticiaURL");
        idArticle = extras.getString("idNoticia");
        SharedPreferences settings = getSharedPreferences("FeedYa!Settings", 0);
        String authCode = settings.getString("authCode", "0");
        Utils.markAsReadEntry(authCode, getResources(), idArticle);
        content.getSettings().setJavaScriptEnabled(true);
        content.getSettings().setLoadWithOverviewMode(true);
        content.getSettings().setUseWideViewPort(true);
        content.getSettings().setBuiltInZoomControls(true);
        content.getSettings().setDisplayZoomControls(false);
        content.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        content.loadDataWithBaseURL("file:///android_asset/", sb.toString(), "text/html", "utf-8", null);

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.share);
        item.setVisible(true);
        ShareActionProvider myShareActionProvider = (ShareActionProvider) item.getActionProvider();
        Intent myIntent = new Intent();
        Bundle extras = getIntent().getExtras();
        myIntent.setAction(Intent.ACTION_SEND);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        myIntent.putExtra(
                Intent.EXTRA_TEXT,
                Html.fromHtml(getResources().getString(R.string.share_text) + "\n" + extras.getString("noticiaTitulo")
                        + "\n" + extras.getString("noticiaLINK")));

        myIntent.setType("text/plain");
        myShareActionProvider.setShareIntent(myIntent);

        item = menu.findItem(R.id.search);
        item.setVisible(false);
        item = menu.findItem(R.id.save);
        item.setVisible(true);
        item = menu.findItem(R.id.open);
        item.setVisible(true);
        Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        item.setIntent(web);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case R.id.action_settings:
            startActivity(new Intent(this, SettingsActivity.class));
        case R.id.save:
            Toast.makeText(getApplicationContext(), R.string.added_to_list, Toast.LENGTH_SHORT).show();
            SharedPreferences settings = getSharedPreferences("FeedYa!Settings", 0);
            String authCode = settings.getString("authCode", "0");
            String user = settings.getString("profileId", "0");
            SaveForLaterItem saveItem = new SaveForLaterItem();
            saveItem.setEntryId(idArticle);
            Utils.saveForLater(authCode, user, getResources(), saveItem);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("AddedToReadList", true);
            editor.commit();

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        content.stopLoading();
        finish();
        overridePendingTransition(R.anim.anim_close, R.anim.anim_in);
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            Class.forName("android.webkit.WebView").getMethod("onPause", (Class[]) null)
                    .invoke(content, (Object[]) null);

        } catch (ClassNotFoundException cnfe) {
        } catch (NoSuchMethodException nsme) {
        } catch (InvocationTargetException ite) {
        } catch (IllegalAccessException iae) {
        }
    }

}
