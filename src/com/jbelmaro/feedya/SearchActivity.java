package com.jbelmaro.feedya;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.jbelmaro.feedya.util.FeedItemBean;
import com.jbelmaro.feedya.util.SearchFeedsResponse;
import com.jbelmaro.feedya.util.Utils;

public class SearchActivity extends ListActivity implements SearchView.OnQueryTextListener {

    private SearchView mSearchView;
    private TextView mStatusView;
    private TextView mNoResultsView;
    private ArrayAdapter<FeedItemBean> adapter;
    private List<FeedItemBean> listA;
    private SearchFeedsResponse load;
    private String authCode;

    public SearchActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        setContentView(R.layout.activity_search);

        mStatusView = (TextView) findViewById(R.id.status_text);
        mNoResultsView = (TextView) findViewById(R.id.no_results_text_view);

        String busqueda = extras.getString("busqueda");
        authCode = extras.getString("authCode");
        busqueda = busqueda.replace(" ", "%20");
        mStatusView.setText(Uri.decode(busqueda));
        SharedPreferences settings = getSharedPreferences("FeedYa!Settings", MODE_PRIVATE);
        authCode = settings.getString("authCode", "0");
        Asincrono tarea = new Asincrono(this, authCode, this.getResources());
        tarea.execute(new String[] {busqueda.toString()});

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.share);
        item.setVisible(false);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchItem.setVisible(true);
        mSearchView = (SearchView) searchItem.getActionView();
        setupSearchView(searchItem);

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
        mStatusView.setText(newText);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.i("ActionBar", "Nuevo!");
        Asincrono tarea = new Asincrono(this, authCode, this.getResources());
        tarea.execute(new String[] {query});

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
    public void onListItemClick(ListView l, View v, int position, long id) {

        Log.i("SearchActivity", "Item clicked: " + id);
        Intent intent = new Intent(this.getApplicationContext(), NewsActivity.class);
        intent.putExtra("titulo", load.results[position].title);
        Log.i("SearchActivity", "TITULO: " + load.results[position].title + "\nFUENTE: "
                + load.results[position].website);
        intent.putExtra("fuente", load.results[position].feedId);
        intent.putExtra("authCode", authCode);
        startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    private class Asincrono extends AsyncTask<String, Integer, Boolean> {

        private SearchActivity activity;
        private ProgressBar dialog;
        private String authCode;
        private Resources resources;

        public Asincrono(SearchActivity a, String authCode, Resources resources) {
            activity = a;
            dialog = (ProgressBar) activity.findViewById(R.id.marker_progress);
            this.authCode = authCode;
            this.resources = resources;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            for (String url : params) {
                load = Utils.FindFeeds(Uri.encode(url), authCode, resources);
                Log.i("SearchActivity", url);
            }

            Bitmap feedIcon = null;
            Drawable favorite = null;

            try {
                for (int i = 0; i < load.results.length; i++) {
                    feedIcon = Utils.downloadBitmap(load.results[i].website, false);
                    Bitmap circleBitmap = Bitmap.createBitmap(feedIcon.getWidth(), feedIcon.getHeight(),
                            Bitmap.Config.ARGB_8888);

                    BitmapShader shader = new BitmapShader(feedIcon, TileMode.CLAMP, TileMode.CLAMP);
                    Paint paint = new Paint();
                    paint.setShader(shader);
                    paint.setColor(0xFFfffff0);
                    paint.setMaskFilter(new BlurMaskFilter(5.0f, BlurMaskFilter.Blur.INNER));
                    Canvas canvas = new Canvas(circleBitmap);
                    canvas.drawCircle(feedIcon.getWidth() / 2, feedIcon.getHeight() / 2,
                            (float) (feedIcon.getWidth() / 2 - 0.1), paint);
                    listA.add(new FeedItemBean(load.results[i].title, circleBitmap, favorite, load.results[i].feedId,
                            load.results[i].website, null, 0));
                }
            } catch (NullPointerException e) {
                Log.e("SearchActivity", "hay un error porque devuelve la lista vacía");
            }
            adapter = new FeedListItemAdapter(activity, listA);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            dialog.setVisibility(View.GONE);
            if (listA.isEmpty())
                mNoResultsView.setVisibility(View.VISIBLE);
            setListAdapter(adapter);
        }

        @Override
        protected void onPreExecute() {

            listA = new ArrayList<FeedItemBean>();
            adapter = new FeedListItemAdapter(activity, listA);
            dialog.setVisibility(View.VISIBLE);
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
}
