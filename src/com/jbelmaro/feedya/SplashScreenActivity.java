package com.jbelmaro.feedya;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Window;

import com.crashlytics.android.Crashlytics;
import com.jbelmaro.feedya.util.ExchangeCodeResponse;
import com.jbelmaro.feedya.util.Profile;
import com.jbelmaro.feedya.util.Utils;

public class SplashScreenActivity extends Activity {

    // Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Crashlytics.start(this);
        // Set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_screen);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                // Start the next activity
                SharedPreferences settings = getSharedPreferences("FeedYa!Settings", MODE_PRIVATE);

                if (settings.contains("authCode")) {
                    long expiration = settings.getLong("authCodeExpiration", 0);
                    Date date = new Date(expiration);
                    Date now = new Date();
                    boolean before = now.before(date);
                    if (before == true) {
                        Intent mainIntent = new Intent().setClass(SplashScreenActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                    } else {
                        if (settings.getString("authCodeRefresh", "0").equals("0") == false) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    SharedPreferences settings = getSharedPreferences("FeedYa!Settings", MODE_PRIVATE);
                                    ExchangeCodeResponse exchangeCodeResponse;
                                    Log.v("SplashScreenActivity",
                                            "refresh token: " + settings.getString("authCodeRefresh", "0"));
                                    exchangeCodeResponse = Utils.getAuthTokenWithRefreshToken(
                                            settings.getString("authCodeRefresh", "0"), getResources(), "refresh_token");// revoke_token
                                    // y
                                    // deberia
                                    // mandar
                                    // a
                                    // LoginFeedlyActivity
                                    // y
                                    // borrar
                                    // valores
                                    // de
                                    // shared
                                    // preferences
                                    if (exchangeCodeResponse != null) {
                                        Log.v("SplashScreenActivity", "errorId: " + exchangeCodeResponse.errorId);
                                        Log.v("SplashScreenActivity", "errorMessage: "
                                                + exchangeCodeResponse.errorMessage);
                                        Log.v("SplashScreenActivity", "errorCode: " + exchangeCodeResponse.errorCode);
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putString("authCode", exchangeCodeResponse.access_token);
                                        editor.putLong("authCodeExpiration", exchangeCodeResponse.expires_in);
                                        editor.putString("authCodeRefresh", exchangeCodeResponse.refresh_token);
                                        editor.commit();
                                        Profile profile = Utils.getProfile(settings.getString("authCode", "0"),
                                                getResources());
                                        Log.v("SplashScreenActivity", "Profile: " + profile.getFullName());
                                        editor.putString("profileName", profile.getFullName());
                                        editor.putString("profileId", profile.getId());
                                        editor.commit();
                                    }
                                }
                            }).start();
                        }
                        Intent mainIntent = new Intent().setClass(SplashScreenActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                    }
                } else {
                    Intent loginIntent = new Intent().setClass(getApplicationContext(), LoginFeedlyActivity.class);
                    startActivity(loginIntent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                }

                // Close the activity so the user won't able to go back this
                // activity pressing Back button
                finish();
            }
        };

        // Simulate a long loading process on application startup.
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }

}