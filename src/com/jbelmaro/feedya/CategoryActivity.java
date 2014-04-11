package com.jbelmaro.feedya;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.jbelmaro.feedya.util.ArticleItemBean;
import com.jbelmaro.feedya.util.Item;
import com.jbelmaro.feedya.util.SaveForLaterItem;
import com.jbelmaro.feedya.util.StreamContentResponse;
import com.jbelmaro.feedya.util.Utils;

public class CategoryActivity extends ListActivity {

    private TextView titleV;
    private StreamContentResponse load;
    private String titulo;
    private ArrayAdapter<ArticleItemBean> adapter;
    private List<ArticleItemBean> listA;
    private Bitmap articleIcon;
    private ListView lv;
    private boolean endlist = false;
    private String continuation;
    private List<Item> listaArticles;
    private String dateFormatted = "";
    private InterstitialAd interstitial;

    public CategoryActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        // Create the interstitial.
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId("ca-app-pub-9633189420266305/2465547473");
        // Create ad request.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Begin loading your interstitial.
        interstitial.loadAd(adRequest);

        Bundle extras = getIntent().getExtras();
        SharedPreferences settings = getSharedPreferences("FeedYa!Settings", MODE_PRIVATE);
        String authCode = settings.getString("authCode", "0");
        String user = settings.getString("profileId", "0");
        LoadFeedsTask tarea = new LoadFeedsTask(this, authCode, this.getResources(), extras.getString("categoryId"),
                user);
        tarea.execute(new String[] {extras.getString("categoryId")});
        titleV = (TextView) findViewById(R.id.title_list_activity_news);
        Log.i("CategoryActivity", "nombre: " + extras.getString("titulo"));
        titulo = extras.getString("titulo").toUpperCase(Locale.getDefault());
        titleV.setText(Html.fromHtml(titulo));

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("CategoryActivity", "Item clicked: " + id);
        Intent intent = new Intent(this.getApplicationContext(), ArticleActivity.class);
        intent.putExtra("titulo", listaArticles.get(position).origin.title);
        if (listaArticles.get(position).content != null)
            intent.putExtra("noticia", listaArticles.get(position).content.content);
        else
            intent.putExtra("noticia", listaArticles.get(position).summary.content);
        intent.putExtra("noticiaURL", listaArticles.get(position).originId);
        intent.putExtra("noticiaTitulo", listaArticles.get(position).title);
        intent.putExtra("noticiaLINK", listaArticles.get(position).originId);
        intent.putExtra("autorNoticia", listaArticles.get(position).author);
        intent.putExtra("fechaNoticia", dateFormatted);
        intent.putExtra("idNoticia", listaArticles.get(position).id);
        listaArticles.get(position).unread = false;
        ((TextView) v.findViewById(R.id.article_title)).setTextColor(Color.parseColor("#AAAAAA"));
        adapter.notifyDataSetChanged();

        startActivity(intent);
        this.overridePendingTransition(R.anim.anim_open, R.anim.anim_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.share);
        item.setVisible(false);
        item = menu.findItem(R.id.search);
        item.setVisible(false);
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

    private class LoadFeedsTask extends AsyncTask<String, Integer, Boolean> {

        private CategoryActivity activity;
        private ProgressBar dialog;
        private String authCode;
        private Resources resources;
        private String user;
        private String source;
        private String categoryId;

        public LoadFeedsTask(CategoryActivity a, String authCode, Resources resources, String categoryId, String user) {
            activity = a;
            dialog = (ProgressBar) activity.findViewById(R.id.marker_progress);
            this.authCode = authCode;
            this.resources = resources;
            this.user = user;
            this.categoryId = categoryId;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            for (String url : params) {

                load = Utils.LoadCategory(authCode, categoryId, resources);
                source = url;
                Log.i("CategoryActivity", url);
                Log.i("CategoryActivity", "tamaño descarga: " + load.items.size());
            }

            listA = new ArrayList<ArticleItemBean>();
            if (load != null) {
                listaArticles = load.items;

                if (load.continuation != null)
                    continuation = load.continuation;
                for (int i = 0; i < load.items.size(); i++) {

                    Log.i("CategoryActivity", "INFONOTICIA: " + load.items.get(i).originId);
                    Date date = new Date(load.items.get(i).published);
                    long diff = (new Date()).getTime() - date.getTime();
                    // DateFormat formatter = new SimpleDateFormat("HH:mm");
                    // String dateFormatted = formatter.format(date);
                    String dateFormatted = "";
                    if ((diff / 1000) < 60)
                        dateFormatted = "hace " + Integer.toString((int) (diff / 1000)) + " seg.";
                    else if ((diff / 60000) < 60)
                        dateFormatted = "hace " + Integer.toString((int) (diff / (1000 * 60))) + " min.";
                    else if ((diff / (60000 * 60)) < 24)
                        dateFormatted = "hace " + Integer.toString((int) (diff / (1000 * 60 * 60))) + " horas";
                    else
                        dateFormatted = "hace " + Integer.toString((int) (diff / (1000 * 60 * 60 * 24))) + " dias";
                    if ((load.items.get(i).visual != null) && !load.items.get(i).visual.getUrl().equals("none")) {

                        listA.add(new ArticleItemBean(load.items.get(i).title, articleIcon, load.items.get(i).originId,
                                load.items.get(i).visual.getUrl(),
                                load.items.get(i).origin.title + "/" + dateFormatted, load.items.get(i).id, load.items
                                        .get(i).unread));
                        articleIcon = null;
                    } else {
                        articleIcon = null;
                        listA.add(new ArticleItemBean(load.items.get(i).title, articleIcon, load.items.get(i).originId,
                                "", load.items.get(i).origin.title + "/" + dateFormatted, load.items.get(i).id,
                                load.items.get(i).unread));
                    }
                }
            }
            adapter = new ArticleListItemAdapter(activity, listA);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.setVisibility(View.GONE);

            setListAdapter(adapter);
            displayInterstitial();
            adapter.notifyDataSetChanged();
            lv = getListView();
            lv.setOnItemLongClickListener(new OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int arg2, long arg3) {

                    SaveForLaterItem item = new SaveForLaterItem();
                    item.setEntryId(((ArticleItemBean) getListView().getItemAtPosition(arg2)).getId());
                    HttpResponse response = Utils.saveForLater(authCode, user, resources, item);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        SharedPreferences settings = activity.getSharedPreferences("FeedYa!Settings", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("AddedToReadList", true);
                        editor.commit();
                        Log.v("CategoryActivity", "Añadido a Lista de Lectura");
                        Toast.makeText(getApplicationContext(), R.string.added_to_list, Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), R.string.added_to_list_error, Toast.LENGTH_SHORT)
                                .show();
                    }
                    return true;
                }

            });
            lv.setOnScrollListener(new OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    int lastInScreen = firstVisibleItem + visibleItemCount;
                    if ((lastInScreen == getListView().getCount()) && !endlist) {
                        Log.v("CategoryActivity", "Ha bajado hasta abajo");
                        endlist = true;
                        LoadMoreFeedsTask tarea = new LoadMoreFeedsTask(activity, authCode, resources);
                        tarea.execute(new String[] {source});
                        // lv.setSelection(getListAdapter().getCount() - 1);

                    }
                }
            });

        }

        @Override
        protected void onPreExecute() {
            dialog.setVisibility(View.VISIBLE);
        }
    }

    private class LoadMoreFeedsTask extends AsyncTask<String, Integer, Boolean> {

        private CategoryActivity activity;
        private String authCode;
        private Resources resources;
        final int positionToSave = lv.getFirstVisiblePosition();

        public LoadMoreFeedsTask(CategoryActivity a, String authCode, Resources resources) {
            activity = a;
            this.authCode = authCode;
            this.resources = resources;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            for (String url : params) {
                load = Utils.LoadCategoryMore(authCode, url, resources, continuation);
                Log.i("CategoryActivity", url);
            }
            if (load != null) {
                for (int i = 0; i < load.items.size(); i++)
                    listaArticles.add(load.items.get(i));
                if (load.continuation != null) {
                    continuation = load.continuation;
                    endlist = false;
                } else
                    endlist = true;
                for (int i = 0; i < load.items.size(); i++) {

                    Log.i("CategoryActivity", "INFONOTICIA: " + load.items.get(i).originId);
                    Date date = new Date(load.items.get(i).published);
                    long diff = (new Date()).getTime() - date.getTime();
                    // DateFormat formatter = new SimpleDateFormat("HH:mm");
                    // String dateFormatted = formatter.format(date);

                    if ((diff / 1000) < 60)
                        dateFormatted = "hace " + Integer.toString((int) (diff / 1000)) + " seg.";
                    else if ((diff / 60000) < 60)
                        dateFormatted = "hace " + Integer.toString((int) (diff / (1000 * 60))) + " min.";
                    else if ((diff / (60000 * 60)) < 24)
                        dateFormatted = "hace " + Integer.toString((int) (diff / (1000 * 60 * 60))) + " horas";
                    else
                        dateFormatted = "hace " + Integer.toString((int) (diff / (1000 * 60 * 60 * 24))) + " dias";
                    if ((load.items.get(i).visual != null) && !load.items.get(i).visual.getUrl().equals("none")) {

                        listA.add(new ArticleItemBean(load.items.get(i).title, articleIcon, load.items.get(i).originId,
                                load.items.get(i).visual.getUrl(),
                                load.items.get(i).origin.title + "/" + dateFormatted, load.items.get(i).id, load.items
                                        .get(i).unread));
                        articleIcon = null;
                    } else {
                        articleIcon = null;
                        listA.add(new ArticleItemBean(load.items.get(i).title, articleIcon, load.items.get(i).originId,
                                "", load.items.get(i).origin.title + "/" + dateFormatted, load.items.get(i).id,
                                load.items.get(i).unread));
                    }
                }
                adapter = new ArticleListItemAdapter(activity, listA);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            setListAdapter(adapter);
            lv.post(new Runnable() {

                @Override
                public void run() {
                    lv.setSelection(positionToSave);
                }
            });

            lv.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    if (lv.getFirstVisiblePosition() == positionToSave) {
                        lv.getViewTreeObserver().removeOnPreDrawListener(this);
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }

        @Override
        protected void onPreExecute() {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_close, R.anim.anim_in);
    }

    // Invoke displayInterstitial() when you are ready to display an
    // interstitial.
    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
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