package com.jbelmaro.feedya;

import java.util.regex.Pattern;

import com.jbelmaro.feedya.util.ExchangeCodeResponse;
import com.jbelmaro.feedya.util.Profile;
import com.jbelmaro.feedya.util.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class LoginFeedlyActivity extends Activity {

    private WebView webView;
    private String code;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_feedly);
        webView = (WebView) findViewById(R.id.viewFeedly);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setAllowFileAccess(true);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Log.v("TEST", url);
                    String[] urlPartes = url.split(Pattern.quote("?"));
                    if (urlPartes[0].equals("http://localhost/")) {
                        code = urlPartes[1];
                        Log.v("TEST", code);
                        SharedPreferences settings = getSharedPreferences("FeedYa!Settings", MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("code", code);
                        editor.commit();

                        ExchangeCodeResponse exchangeCodeResponse;
                        exchangeCodeResponse = Utils.getAuthToken(settings.getString("code", "0"), getResources());
                        if (exchangeCodeResponse != null) {
                            editor.putString("authCode", exchangeCodeResponse.access_token);
                            editor.putLong("authCodeExpiration", exchangeCodeResponse.expires_in);
                            editor.putString("authCodeRefresh", exchangeCodeResponse.refresh_token);
                            editor.commit();
                            Profile profile = Utils.getProfile(settings.getString("authCode", "0"), getResources());
                            if (profile != null) {
                                Log.v("MainActivity", "Profile: " + profile.getFullName());

                                editor.putString("profileName", profile.getFullName());
                                editor.putString("profileId", profile.getId());
                                editor.commit();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                                startActivity(intent);
                                view.destroy();
                                return true;
                            } else {
                                view.loadUrl(url);
                            }
                        } else {
                            view.loadUrl(url);
                        }

                    } else {
                        view.loadUrl(url);
                    }
                    return false;
                }
            });
            webView.loadUrl("http://sandbox.feedly.com/v3/auth/auth?response_type=code&redirect_uri=http://localhost&client_id=sandbox&scope=https://cloud.feedly.com/subscriptions");

        } else {

            Toast.makeText(this, "No hay conexión disponible en este momento", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_feedly, menu);
        return true;
    }

    @Override
    public void onBackPressed() {

        finish();
    }
}
