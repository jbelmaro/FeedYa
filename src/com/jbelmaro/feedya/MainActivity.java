package com.jbelmaro.feedya;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.SearchView;

import com.espian.showcaseview.OnShowcaseEventListener;
import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.ShowcaseViews;
import com.espian.showcaseview.ShowcaseViews.ItemViewProperties;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends FragmentActivity implements SearchView.OnQueryTextListener, OnShowcaseEventListener {

    private SearchView mSearchView;
    private AdView adView;
    private SectionPagerAdapter mSectionPagerAdapter;
    private ViewPager mViewPager;
    private ShowcaseViews mViews;

    public static final float SHOWCASE_SPINNER_SCALE = 1f;
    public static final float SHOWCASE_OVERFLOW_ITEM_SCALE = 0.5f;
    private static final float SHOWCASE_LIKE_SCALE = 0.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        setContentView(R.layout.activity_main);
        // Showcase

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        SharedPreferences settings = getSharedPreferences("FeedYa!Settings", MODE_PRIVATE);
        Log.i("MainActivity", "UNREAD: " + settings.getInt("loadValue", 0));
        select_tab(actionBar, settings.getInt("loadValue", 0));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.valores_spinner,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionBar.setListNavigationCallbacks(adapter, new OnNavigationListener() {

            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                SharedPreferences settings = getSharedPreferences("FeedYa!Settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();

                switch (itemPosition) {
                case 0:
                    editor.putInt("loadValue", 0);
                    editor.commit();

                    break;
                case 1:
                    editor.putInt("loadValue", 1);
                    editor.commit();

                    break;

                default:
                    break;
                }
                return true;
            }
        });
        Tracker tracker = GoogleAnalytics.getInstance(this).getTracker("UA-49378682-1");

        tracker.sendView("/MainActivity");

        adView = (AdView) findViewById(R.id.adView);

        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager(), getApplicationContext());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionPagerAdapter);
        // mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(1);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) searchItem.getActionView();
        setupSearchView(searchItem);
        MenuItem share = menu.findItem(R.id.share);
        share.setVisible(false);
        SharedPreferences settings = getSharedPreferences("FeedYa!Settings", MODE_PRIVATE);
        if (settings.getBoolean("tutorial", true)) {
            ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
            co.block = false;
            mViews = new ShowcaseViews(this, new ShowcaseViews.OnShowcaseAcknowledged() {
                @Override
                public void onShowCaseAcknowledged(ShowcaseView showcaseView) {
                }
            });

            co.showcaseId = 1234;
            co.block = true;
            mViews.addView(new ItemViewProperties(R.id.search, R.string.showcase_title, R.string.showcase_msg,
                    ShowcaseView.ITEM_ACTION_ITEM, SHOWCASE_OVERFLOW_ITEM_SCALE, co));
            mViews.addView(new ShowcaseViews.ItemViewProperties(R.id.pager, R.string.showcase_title,
                    R.string.showcase_pager, SHOWCASE_LIKE_SCALE, co));
            mViews.addView(new ShowcaseViews.ItemViewProperties(R.id.pager, R.string.showcase_title,
                    R.string.showcase_pager, SHOWCASE_LIKE_SCALE, co));
            mViews.addAnimatedGestureToView(1, -500, 0, 500, 0);
            mViews.addAnimatedGestureToView(2, 500, 0, -500, 0);
            mViews.addView(new ShowcaseViews.ItemViewProperties(R.id.pager, R.string.showcase_title,
                    R.string.showcase_push, SHOWCASE_LIKE_SCALE, co));
            mViews.addAnimatedGestureToView(3, 0, 0, 0, 0);
            mViews.show();
            SharedPreferences.Editor editor = settings.edit();

            editor.putBoolean("tutorial", false);
            editor.commit();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case R.id.action_settings:

            startActivity(new Intent(this, SettingsActivity.class));
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void setupSearchView(MenuItem searchItem) {

        if (isAlwaysExpanded()) {
            mSearchView.setIconifiedByDefault(false);
        } else {
            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }
        mSearchView.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        mSearchView.setOnQueryTextListener(this);

    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.i("ActionBar", "Nuevo!");
        Intent intent = new Intent();
        intent = new Intent(this, SearchActivity.class);
        intent.putExtra("busqueda", query);
        SharedPreferences settings = getSharedPreferences("FeedYa!Settings", MODE_PRIVATE);
        intent.putExtra("authCode", settings.getString("authCode", "0"));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        return false;
    }

    public boolean onClose() {
        return false;
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.anim_close, R.anim.anim_in);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }

        SharedPreferences settings = getSharedPreferences("FeedYa!Settings", 0);
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        select_tab(actionBar, settings.getInt("loadValue", 0));

    }

    @Override
    public void onPause() {
        super.onPause();
        if (adView != null) {
            adView.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adView != null) {
            adView.destroy();
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
    public void onShowcaseViewHide(ShowcaseView showcaseView) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {
        // TODO Auto-generated method stub

    }

    private void select_tab(ActionBar b, int pos) {
        try {
            // do the normal tab selection in case all tabs are visible
            b.setSelectedNavigationItem(pos);

            // now use reflection to select the correct Spinner if
            // the bar's tabs have been reduced to a Spinner

            View action_bar_view = findViewById(getResources().getIdentifier("action_bar", "id", "android"));
            Class<?> action_bar_class = action_bar_view.getClass();
            Field tab_scroll_view_prop = action_bar_class.getDeclaredField("mTabScrollView");
            tab_scroll_view_prop.setAccessible(true);
            // get the value of mTabScrollView in our action bar
            Object tab_scroll_view = tab_scroll_view_prop.get(action_bar_view);
            if (tab_scroll_view == null)
                return;
            Field spinner_prop = tab_scroll_view.getClass().getDeclaredField("mTabSpinner");
            spinner_prop.setAccessible(true);
            // get the value of mTabSpinner in our scroll view
            Object tab_spinner = spinner_prop.get(tab_scroll_view);
            if (tab_spinner == null)
                return;
            Method set_selection_method = tab_spinner.getClass().getSuperclass()
                    .getDeclaredMethod("setSelection", Integer.TYPE, Boolean.TYPE);
            set_selection_method.invoke(tab_spinner, pos, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
