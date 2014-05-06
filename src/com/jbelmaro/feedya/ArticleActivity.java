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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.jbelmaro.feedya.util.SaveForLaterItem;
import com.jbelmaro.feedya.util.Utils;
import com.jbelmaro.feedya.util.VideoEnabledWebChromeClient;
import com.jbelmaro.feedya.util.VideoEnabledWebView;

public class ArticleActivity extends FragmentActivity {

    private final String TAPPX_KEY = "/120940746/Pub-1333-Android-4029";

    ArticlePagerAdapter mArticlePagerAdapter;
    ViewPager mViewPager;
    TextView titleV;
    VideoEnabledWebView content;
    VideoEnabledWebChromeClient webChromeClient;
    String url;
    String idArticle;
    String shortenURL;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        try {
            Bundle extras = getIntent().getExtras();
            SharedPreferences settings = getSharedPreferences("FeedYa!Settings", 0);
            String authCode = settings.getString("authCode", "0");
            SharedPreferences.Editor editor = settings.edit();
            int tappxCount = settings.getInt("tappxAd", 0);
            if (tappxCount == 5) {
                com.tappx.ads.exchange.Utils.InterstitialConfigureAndShow(this, TAPPX_KEY);
                editor.putInt("tappxAd", 0);
            } else {
                tappxCount++;
                editor.putInt("tappxAd", tappxCount);
            }
            editor.commit();

            shortenURL = Utils.getShortenUrl(authCode, getResources(), extras.getString("idNoticia")).getShortUrl();
            titleV = (TextView) findViewById(R.id.title_list_activity_news);
            content = (VideoEnabledWebView) findViewById(R.id.webView);
            // Initialize the VideoEnabledWebChromeClient and set event handlers
            View nonVideoLayout = findViewById(R.id.nonVideoLayout); // Your own
                                                                     // view,
                                                                     // read
                                                                     // class
                                                                     // comments
            ViewGroup videoLayout = (ViewGroup) findViewById(R.id.videoLayout); // Your
                                                                                // own
                                                                                // view,
                                                                                // read
                                                                                // class
                                                                                // comments
            View loadingView = getLayoutInflater().inflate(R.layout.loading_video, null); // Your
            // own
            // view,
            // read
            // class
            // comments
            webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, content) // See
                                                                                                                 // all
                                                                                                                 // available
                                                                                                                 // constructors...
            {
                // Subscribe to standard events, such as onProgressChanged()...
                @Override
                public void onProgressChanged(WebView view, int progress) {
                    // Your code...
                }
            };
            webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
                @Override
                public void toggledFullscreen(boolean fullscreen) {
                    // Your code to handle the full-screen change, for example
                    // showing and hiding the title bar. Example:
                    if (fullscreen) {
                        WindowManager.LayoutParams attrs = getWindow().getAttributes();
                        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                        attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                        getWindow().setAttributes(attrs);
                        if (android.os.Build.VERSION.SDK_INT >= 14) {
                            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                        }
                    } else {
                        WindowManager.LayoutParams attrs = getWindow().getAttributes();
                        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                        attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                        getWindow().setAttributes(attrs);
                        if (android.os.Build.VERSION.SDK_INT >= 14) {
                            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                        }
                    }

                }
            });
            content.setWebChromeClient(webChromeClient);
            titleV.setText(Html.fromHtml(extras.getString("titulo").toUpperCase(Locale.getDefault())));

            StringBuilder sb = new StringBuilder();
            sb.append("<html><head>"
                    + "<link href='http://fonts.googleapis.com/css?family=Droid+Serif:700' rel='stylesheet' type='text/css'>"
                    + "<style>*{background-color:#eeeeee;}"
                    + "body{font-family: 'Calibri';}"
                    + ".cuerpo{text-align:justify;}img {max-width: 100%;}"
                    + "h1{font-family: 'Droid Serif', serif;}a:link {color:#316CE2;}h5{color:#316CE2;}hr{display: block; height: 1px;border: 0; border-top: 1px solid #316CE2;margin: 1em 0; padding: 0;} "
                    + "</style>"
                    + "<meta name=\"viewport\" content=\"width = device-width,initial-scale=1, user-scale=yes \"/>"
                    + "</head><body>");
            sb.append("<h1>" + extras.getString("noticiaTitulo") + "</h1>");
            sb.append("<h5>" + extras.getString("autorNoticia") + "<br></h5>");
            sb.append("<hr /><div class=\"cuerpo\">" + extras.getString("noticia") + "</div>");

            url = extras.getString("noticiaURL");
            idArticle = extras.getString("idNoticia");

            // Utils.markAsReadEntry(authCode, getResources(), idArticle);
            content.getSettings().setJavaScriptEnabled(true);
            content.getSettings().setLoadWithOverviewMode(true);
            content.getSettings().setUseWideViewPort(true);
            content.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
            content.loadDataWithBaseURL("file:///android_asset/", sb.toString(), "text/html", "utf-8", null);
        } catch (NullPointerException e) {
            //
        }
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
        myIntent.putExtra(Intent.EXTRA_TEXT,
                getResources().getString(R.string.share_text) + "\n" + extras.getString("noticiaTitulo") + "\n"
                        + shortenURL);

        myIntent.setType("text/plain");
        myShareActionProvider.setShareIntent(myIntent);

        item = menu.findItem(R.id.search);
        item.setVisible(false);
        item = menu.findItem(R.id.save);
        item.setVisible(true);
        item = menu.findItem(R.id.open);
        item.setVisible(true);
        Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse(shortenURL));
        item.setIntent(web);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case R.id.action_settings:
            startActivity(new Intent(this, SettingsActivity.class));
            break;
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
            break;
        default:
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Notify the VideoEnabledWebChromeClient, and handle it ourselves if it
        // doesn't handle it
        if (!webChromeClient.onBackPressed()) {
            if (content.canGoBack()) {
                content.goBack();
            } else {
                content.stopLoading();
                finish();
                overridePendingTransition(R.anim.anim_close, R.anim.anim_in);
                super.onBackPressed();
            }
        }

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
