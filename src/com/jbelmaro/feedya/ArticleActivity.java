package com.jbelmaro.feedya;

import java.util.Locale;
import java.util.regex.Pattern;

import com.jbelmaro.feedya.util.SaveForLaterItem;
import com.jbelmaro.feedya.util.Utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
                + ".button {display: block;height: 70px;width: 100px;background: #dddddd;border: 2px;"
                + "text-align: center;}" + "</style>"
                + "<meta name=\"viewport\" content=\"width = device-width,initial-scale=1, maximum-scale=1\"/>"
                + "</head><body>");
        sb.append("<h1>" + extras.getString("noticiaTitulo") + "</h1>");
        sb.append("<h5>" + extras.getString("autorNoticia") + "<br></h5>");
        sb.append("<hr /><div class=\"cuerpo\">" + extras.getString("noticia") + "</div>");
        sb.append("" + "<br><br>" + "<p><a class=\"button\" value=\"WEB\" href=\"" + extras.getString("noticiaURL")
                + "\">Ver en la WEB</a><p></body></html>");
        url = extras.getString("noticiaURL");
        idArticle = extras.getString("idNoticia");
        content.getSettings().setJavaScriptEnabled(true);
        content.getSettings().setLoadWithOverviewMode(true);
        content.getSettings().setUseWideViewPort(true);
        content.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        content.loadDataWithBaseURL("file:///android_asset/", sb.toString(), "text/html", "utf-8", null);

    }

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
        String[] splits = extras.getString("noticiaLINK").split(Pattern.quote("?"));

        myIntent.putExtra(
                Intent.EXTRA_TEXT,
                Html.fromHtml(
                        "Te comparto esta noticia a través de FeedYa!\n" + extras.getString("noticiaTitulo") + "\n"
                                + splits[0]).toString());

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
            Toast.makeText(getApplicationContext(), "Añadido a Lista de Lectura",
                    Toast.LENGTH_LONG).show();
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
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
}
