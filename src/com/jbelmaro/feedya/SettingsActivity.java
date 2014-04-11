package com.jbelmaro.feedya;

import com.google.analytics.tracking.android.EasyTracker;
import com.jbelmaro.feedya.util.ExchangeCodeResponse;
import com.jbelmaro.feedya.util.Utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        findViewById(android.R.id.list).setFitsSystemWindows(true);
        Preference share = findPreference("share");
        share.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        getResources().getString(R.string.share_app));
                shareIntent.setType("text/plain");
                startActivity(shareIntent);
                return false;
            }
        });
        Preference logout = findPreference("logout");
        logout.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                // Set the dialog title
                builder.setMessage("Cerrar Sesión")
                // Specify the list array, the items to be selected by default
                // (null for none),
                // and the listener through which to receive callbacks when
                // items are selected
                        .setPositiveButton("Aceptar", new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences settings = getSharedPreferences("FeedYa!Settings", MODE_PRIVATE);

                                ExchangeCodeResponse exchangeCodeResponse;
                                Log.v("SplashScreenActivity",
                                        "refresh token: " + settings.getString("authCodeRefresh", "0"));
                                exchangeCodeResponse = Utils.getAuthTokenWithRefreshToken(
                                        settings.getString("authCodeRefresh", "0"), getResources(), "revoke_token");
                                settings.edit().clear().commit();

                                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(
                                        getBaseContext().getPackageName());
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                Utils.clearApplicationData(getApplicationContext());
                                finish();
                            }
                        }).setNegativeButton("Cancelar", new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
        });
        Preference vote = findPreference("vote");
        vote.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String appPackageName = getPackageName();
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
                marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
                        | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                startActivity(marketIntent);
                return false;
            }
        });

        Preference contact = findPreference("contact");
        contact.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent contactIntent = new Intent();
                contactIntent.setAction(Intent.ACTION_SENDTO);
                contactIntent.setData(Uri.parse("mailto:jbelmaro@gmail.com?subject=FeedYa!"));
                startActivity(contactIntent);
                return false;
            }
        });

        Preference about = findPreference("about");
        about.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
                return false;
            }
        });
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
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_close, R.anim.anim_in);
    }
}
