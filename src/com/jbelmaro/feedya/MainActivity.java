package com.jbelmaro.feedya;

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
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;

import com.jbelmaro.feedya.util.ExchangeCodeResponse;

public class MainActivity extends FragmentActivity implements SearchView.OnQueryTextListener {

    private SearchView mSearchView;
    private TextView mStatusView;
    SectionPagerAdapter mSectionPagerAdapter;
    ViewPager mViewPager;
    ExchangeCodeResponse exchangeCodeResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.

        mStatusView = (TextView) findViewById(R.id.status_text);
        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionPagerAdapter);
        // mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) searchItem.getActionView();
        setupSearchView(searchItem);
        MenuItem share = menu.findItem(R.id.share);
        share.setVisible(false);

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
        mStatusView.setText("Query = " + newText);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mStatusView.setText("Query = " + query + " : submitted");
        Log.i("ActionBar", "Nuevo!");
        finish();
        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(mStatusView.getWindowToken(), 0);
        startActivity(getIntent());
        Intent intent = new Intent();
        intent = new Intent(this, SearchActivity.class);
        intent.putExtra("busqueda", query);
        SharedPreferences settings = getSharedPreferences("FeedYa!Settings", MODE_PRIVATE);
        intent.putExtra("authCode", settings.getString("authCode", "0"));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        finish();
        return false;
    }

    public boolean onClose() {
        mStatusView.setText("Closed!");
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
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

}
