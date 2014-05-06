package com.jbelmaro.feedya;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.jbelmaro.feedya.util.ArticleItemBean;
import com.jbelmaro.feedya.util.Item;
import com.jbelmaro.feedya.util.SaveForLaterItem;
import com.jbelmaro.feedya.util.StreamContentResponse;
import com.jbelmaro.feedya.util.Utils;

public class NewsActivity extends ListActivity {

    private TextView titleV;
    private StreamContentResponse load;
    private String titulo;
    private String fuente;
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
        fuente = extras.getString("fuente");
        SharedPreferences settings = getSharedPreferences("FeedYa!Settings", MODE_PRIVATE);
        String authCode = settings.getString("authCode", "0");
        String user = settings.getString("profileId", "0");
        LoadFeedsTask tarea = new LoadFeedsTask(this, authCode, this.getResources(), user);
        tarea.execute(new String[] {fuente});
        titleV = (TextView) findViewById(R.id.title_list_activity_news);
        Log.i("NewsActivity", "nombre: " + extras.getString("titulo"));
        titulo = extras.getString("titulo").toUpperCase(Locale.getDefault());
        titleV.setText(Html.fromHtml(extras.getString("titulo").toUpperCase(Locale.getDefault())));
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
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
                executeTask();
                return true;
            }
        });
    }

    public void executeTask() {
        ConnectivityManager connMgr = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            SharedPreferences settings = getSharedPreferences("FeedYa!Settings", MODE_PRIVATE);
            String authCode = settings.getString("authCode", "0");
            String user = settings.getString("profileId", "0");
            if (lv != null) {
                lv.setVisibility(View.INVISIBLE);
                LoadFeedsTask tarea = new LoadFeedsTask(this, authCode, getApplicationContext().getResources(), user);
                tarea.execute(new String[] {fuente});
            }
        } else {
            Toast.makeText(this, "No hay conexión disponible en este momento", Toast.LENGTH_LONG).show();
        }
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
        ((ArticleItemBean) lv.getItemAtPosition(position)).setUnread(false);
        SharedPreferences settings = getSharedPreferences("FeedYa!Settings", 0);
        String authCode = settings.getString("authCode", "0");
        Utils.markAsReadEntry(authCode, getResources(), listaArticles.get(position).id);
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
        item = menu.findItem(R.id.refresh);
        item.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case R.id.action_settings:
            startActivity(new Intent(this, SettingsActivity.class));
        case R.id.refresh:
            SharedPreferences settings = getSharedPreferences("FeedYa!Settings", MODE_PRIVATE);
            String authCode = settings.getString("authCode", "0");
            String user = settings.getString("profileId", "0");
            if (lv != null) {
                lv.setVisibility(View.INVISIBLE);
                LoadFeedsTask tarea = new LoadFeedsTask(this, authCode, this.getResources(), user);
                tarea.execute(new String[] {fuente});
            }
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
            try {
                for (String url : params) {
                    SharedPreferences settings = getSharedPreferences("FeedYa!Settings", MODE_PRIVATE);
                    int loadValue = settings.getInt("loadValue", 0);
                    if (loadValue == 0)
                        load = Utils.LoadFeeds(url, authCode, resources, "&unreadOnly=false");
                    else
                        load = Utils.LoadFeeds(url, authCode, resources, "&unreadOnly=true");

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
                        String content = "";
                        if ((diff / 1000) < 60)
                            dateFormatted = "hace " + Integer.toString((int) (diff / 1000)) + "s";
                        else if ((diff / 60000) < 60)
                            dateFormatted = "hace " + Integer.toString((int) (diff / (1000 * 60))) + "m";
                        else if ((diff / (60000 * 60)) < 24)
                            dateFormatted = "hace " + Integer.toString((int) (diff / (1000 * 60 * 60))) + "h";
                        else
                            dateFormatted = "hace " + Integer.toString((int) (diff / (1000 * 60 * 60 * 24))) + "d";
                        if (load.items.get(i).content != null)
                            content = load.items.get(i).content.content;
                        else
                            content = load.items.get(i).summary.content;
                        if ((load.items.get(i).visual != null) && !load.items.get(i).visual.getUrl().equals("none")) {

                            listA.add(new ArticleItemBean(load.items.get(i).title, articleIcon,
                                    load.items.get(i).originId, load.items.get(i).visual.getUrl(), dateFormatted,
                                    load.items.get(i).id, load.items.get(i).unread, content, load.items.get(i).author));
                            articleIcon = null;
                        } else {
                            articleIcon = null;
                            listA.add(new ArticleItemBean(load.items.get(i).title, articleIcon,
                                    load.items.get(i).originId, "", dateFormatted, load.items.get(i).id, load.items
                                            .get(i).unread, content, load.items.get(i).author));
                        }
                    }
                }
                adapter = new ArticleListItemAdapter(activity, listA);
                return true;

            } catch (NullPointerException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.setVisibility(View.GONE);
            if (result) {
                setListAdapter(adapter);
                adapter.notifyDataSetChanged();
                if (lv != null)
                    lv.setVisibility(View.VISIBLE);
                lv = getListView();
                lv.setOnItemLongClickListener(new OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int arg2, long arg3) {
                        try {
                            SaveForLaterItem item = new SaveForLaterItem();
                            item.setEntryId(((ArticleItemBean) getListView().getItemAtPosition(arg2)).getId());
                            HttpResponse response = Utils.saveForLater(authCode, user, resources, item);
                            if (response.getStatusLine().getStatusCode() == 200) {
                                SharedPreferences settings = activity.getSharedPreferences("FeedYa!Settings", 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean("AddedToReadList", true);
                                editor.commit();
                                Log.v("NewsActivity", "Añadido a Lista de Lectura");
                                Toast.makeText(getApplicationContext(), R.string.added_to_list, Toast.LENGTH_LONG)
                                        .show();

                            } else {
                                Toast.makeText(getApplicationContext(), R.string.added_to_list_error, Toast.LENGTH_LONG)
                                        .show();
                            }
                        } catch (NullPointerException e) {
                            //
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
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                            int totalItemCount) {
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
        final int positionToSave = lv.getFirstVisiblePosition();

        public LoadMoreFeedsTask(NewsActivity a, String authCode, Resources resources) {
            activity = a;
            this.authCode = authCode;
            this.resources = resources;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                for (String url : params) {
                    SharedPreferences settings = getSharedPreferences("FeedYa!Settings", MODE_PRIVATE);
                    int loadValue = settings.getInt("loadValue", 0);
                    if (loadValue == 0)
                        load = Utils.LoadMoreFeeds(url, authCode, resources, continuation, "&unreadOnly=false");
                    else
                        load = Utils.LoadMoreFeeds(url, authCode, resources, continuation, "&unreadOnly=true");

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
                        String content = "";
                        if ((diff / 1000) < 60)
                            dateFormatted = "hace " + Integer.toString((int) (diff / 1000)) + "s";
                        else if ((diff / 60000) < 60)
                            dateFormatted = "hace " + Integer.toString((int) (diff / (1000 * 60))) + "m";
                        else if ((diff / (60000 * 60)) < 24)
                            dateFormatted = "hace " + Integer.toString((int) (diff / (1000 * 60 * 60))) + "h";
                        else
                            dateFormatted = "hace " + Integer.toString((int) (diff / (1000 * 60 * 60 * 24))) + "d";
                        if (load.items.get(i).content != null)
                            content = load.items.get(i).content.content;
                        else
                            content = load.items.get(i).summary.content;
                        if ((load.items.get(i).visual != null) && !load.items.get(i).visual.getUrl().equals("none")) {
                            // articleIcon =
                            // Utils.downloadArticleImage(load.items.get(i).visual.getUrl());

                            listA.add(new ArticleItemBean(load.items.get(i).title, articleIcon,
                                    load.items.get(i).originId, load.items.get(i).visual.getUrl(), dateFormatted,
                                    load.items.get(i).id, load.items.get(i).unread, content, load.items.get(i).author));
                            articleIcon = null;
                        } else {
                            articleIcon = null;
                            listA.add(new ArticleItemBean(load.items.get(i).title, articleIcon,
                                    load.items.get(i).originId, "", dateFormatted, load.items.get(i).id, load.items
                                            .get(i).unread, content, load.items.get(i).author));
                        }
                    }
                    adapter = new ArticleListItemAdapter(activity, listA);
                }
                return true;
            } catch (NullPointerException e) {
                //
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
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

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = getSharedPreferences("FeedYa!Settings", 0);
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        select_tab(actionBar, settings.getInt("loadValue", 0));
    }
}