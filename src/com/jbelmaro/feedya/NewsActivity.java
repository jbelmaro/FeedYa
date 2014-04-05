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
import com.jbelmaro.feedya.util.ArticleItemBean;
import com.jbelmaro.feedya.util.Item;
import com.jbelmaro.feedya.util.SaveForLaterItem;
import com.jbelmaro.feedya.util.StreamContentResponse;
import com.jbelmaro.feedya.util.Utils;

public class NewsActivity extends ListActivity {

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

    public NewsActivity() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Bundle extras = getIntent().getExtras();
        SharedPreferences settings = getSharedPreferences("FeedYa!Settings", MODE_PRIVATE);
        String authCode = settings.getString("authCode", "0");
        String user = settings.getString("profileId", "0");
        LoadFeedsTask tarea = new LoadFeedsTask(this, authCode, this.getResources(), user);
        tarea.execute(new String[] {extras.getString("fuente")});
        titleV = (TextView) findViewById(R.id.title_list_activity_news);
        Log.i("NewsActivity", "nombre: " + extras.getString("titulo"));
        titulo = extras.getString("titulo").toUpperCase(Locale.getDefault());
        titleV.setText(Html.fromHtml(extras.getString("titulo").toUpperCase(Locale.getDefault())));

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("NewsActivity", "Item clicked: " + id);
        Intent intent = new Intent(this.getApplicationContext(), ArticleActivity.class);
        intent.putExtra("titulo", titulo);
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

        private NewsActivity activity;
        private ProgressBar dialog;
        private String authCode;
        private Resources resources;
        private String user;
        private String source;

        public LoadFeedsTask(NewsActivity a, String authCode, Resources resources, String user) {
            activity = a;
            dialog = (ProgressBar) activity.findViewById(R.id.marker_progress);
            this.authCode = authCode;
            this.resources = resources;
            this.user = user;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            for (String url : params) {

                load = Utils.LoadFeeds(url, authCode, resources);
                source = url;
                Log.i("NewsActivity", url);
            }

            listA = new ArrayList<ArticleItemBean>();
            if (load != null) {
                listaArticles = load.items;

                if (load.continuation != null)
                    continuation = load.continuation;
                for (int i = 0; i < load.items.size(); i++) {

                    Log.i("NewsActivity", "INFONOTICIA: " + load.items.get(i).originId);
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
                                load.items.get(i).visual.getUrl(), dateFormatted, load.items.get(i).id, load.items
                                        .get(i).unread));
                        articleIcon = null;
                    } else {
                        articleIcon = null;
                        listA.add(new ArticleItemBean(load.items.get(i).title, articleIcon, load.items.get(i).originId,
                                "", dateFormatted, load.items.get(i).id, load.items.get(i).unread));
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
                        Log.v("NewsActivity", "Añadido a Lista de Lectura");
                        Toast.makeText(getApplicationContext(), R.string.added_to_list, Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(), R.string.added_to_list_error, Toast.LENGTH_LONG)
                                .show();
                    }
                    return true;
                }

            });
            lv.setOnScrollListener(new OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    int lastInScreen = firstVisibleItem + visibleItemCount;
                    if ((lastInScreen == getListView().getCount()) && !endlist) {
                        Log.v("NewsActivity", "Ha bajado hasta abajo");
                        endlist = true;
                        LoadMoreFeedsTask tarea = new LoadMoreFeedsTask(activity, authCode, resources);
                        tarea.execute(new String[] {source});

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

        private NewsActivity activity;
        private String authCode;
        private Resources resources;

        public LoadMoreFeedsTask(NewsActivity a, String authCode, Resources resources) {
            activity = a;
            this.authCode = authCode;
            this.resources = resources;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            for (String url : params) {
                load = Utils.LoadMoreFeeds(url, authCode, resources, continuation);
                Log.i("NewsActivity", url);
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

                    Log.i("NewsActivity", "INFONOTICIA: " + load.items.get(i).originId);
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
                        //articleIcon = Utils.downloadArticleImage(load.items.get(i).visual.getUrl());

                        listA.add(new ArticleItemBean(load.items.get(i).title, articleIcon, load.items.get(i).originId,
                                load.items.get(i).visual.getUrl(), dateFormatted, load.items.get(i).id, load.items
                                        .get(i).unread));
                        articleIcon = null;
                    } else {
                        articleIcon = null;
                        listA.add(new ArticleItemBean(load.items.get(i).title, articleIcon, load.items.get(i).originId,
                                "", dateFormatted, load.items.get(i).id, load.items.get(i).unread));
                    }
                }
                adapter = new ArticleListItemAdapter(activity, listA);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            setListAdapter(adapter);
            lv.setSelection(getListAdapter().getCount() - 20);

        }

        @Override
        protected void onPreExecute() {
        }
    }

    @Override
    public void onBackPressed() {
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
}