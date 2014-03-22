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

    public CategoryActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Bundle extras = getIntent().getExtras();
        SharedPreferences settings = getSharedPreferences("FeedYa!Settings", MODE_PRIVATE);
        String authCode = settings.getString("authCode", "0");
        String user = settings.getString("profileId", "0");
        LoadFeedsTask tarea = new LoadFeedsTask(this, authCode, this.getResources(), extras.getString("categoryId"), user);
        tarea.execute(new String[] {extras.getString("categoryId")});
        titleV = (TextView) findViewById(R.id.title_list_activity_news);
        Log.i("CategoryActivity", "nombre: " + extras.getString("titulo"));
        titulo = extras.getString("titulo").toUpperCase(Locale.getDefault());
        titleV.setText(Html.fromHtml(extras.getString("titulo").toUpperCase(Locale.getDefault())));

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("CategoryActivity", "Item clicked: " + id);
        Intent intent = new Intent(this.getApplicationContext(), ArticleActivity.class);
        intent.putExtra("titulo", listaArticles.get(position).origin.title);
        intent.putExtra("noticia", listaArticles.get(position).summary.content);
        intent.putExtra("noticiaURL", listaArticles.get(position).originId);
        intent.putExtra("noticiaTitulo", listaArticles.get(position).title);
        intent.putExtra("noticiaLINK", listaArticles.get(position).originId);
        intent.putExtra("autorNoticia", listaArticles.get(position).author);
        intent.putExtra("fechaNoticia", dateFormatted);
        intent.putExtra("idNoticia", listaArticles.get(position).id);

        startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
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

        public LoadFeedsTask(CategoryActivity a, String authCode, Resources resources,String categoryId, String user) {
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
                Log.i("CategoryActivity", "tamaño descarga: "+ load.items.size());
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
                                load.items.get(i).visual.getUrl(), dateFormatted, load.items.get(i).id));
                        articleIcon = null;
                    } else {
                        articleIcon = null;
                        listA.add(new ArticleItemBean(load.items.get(i).title, articleIcon, load.items.get(i).originId,
                                "", dateFormatted, load.items.get(i).id));
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
                        Log.v("CategoryActivity", "Añadido a Lista de Lectura");
                        Toast.makeText(getApplicationContext(), "Añadido a Lista de Lectura", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "Se ha producido un error al añadir", Toast.LENGTH_LONG)
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
                        lv.setSelection(getListAdapter().getCount() - 1);

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
                        articleIcon = Utils.downloadArticleImage(load.items.get(i).visual.getUrl());

                        listA.add(new ArticleItemBean(load.items.get(i).title, articleIcon, load.items.get(i).originId,
                                load.items.get(i).visual.getUrl(), dateFormatted, load.items.get(i).id));
                        articleIcon = null;
                    } else {
                        articleIcon = null;
                        listA.add(new ArticleItemBean(load.items.get(i).title, articleIcon, load.items.get(i).originId,
                                "", dateFormatted, load.items.get(i).id));
                    }
                }
                adapter = new ArticleListItemAdapter(activity, listA);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            setListAdapter(adapter);

        }

        @Override
        protected void onPreExecute() {
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
}