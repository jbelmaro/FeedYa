package com.jbelmaro.feedya;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jbelmaro.feedya.util.ArticleItemBean;
import com.jbelmaro.feedya.util.StreamContentResponse;
import com.jbelmaro.feedya.util.Utils;

public class LecturaFragment extends ListFragment {
    private StreamContentResponse load;
    private ArrayAdapter<ArticleItemBean> adapter;
    private List<ArticleItemBean> listA = null;
    private Bitmap articleIcon;
    private AddFeedTask addToList;
    private LoadFeedsTask loadFeeds;
    private PullToRefreshLayout layout;
    private ProgressBar dialog;
    private ListView listLectura;
    private TextView mNoResultsView;
    private boolean add;
    private String dateFormatted = "";

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("LecturaFragment", "Item clicked: " + id);
        Intent intent = new Intent(this.getActivity(), ArticleActivity.class);
        intent.putExtra("titulo", "Últimas Noticias");
        intent.putExtra("noticia", listA.get(position).getContent());

        intent.putExtra("noticiaURL", listA.get(position).getArticleURL());
        intent.putExtra("noticiaTitulo", listA.get(position).getTitle());
        intent.putExtra("noticiaLINK", listA.get(position).getArticleURL());
        intent.putExtra("autorNoticia", listA.get(position).getAuthor());
        intent.putExtra("fechaNoticia", dateFormatted);
        intent.putExtra("idNoticia", listA.get(position).getId());
        load.items.get(position).unread = false;
        ((ArticleItemBean) listLectura.getItemAtPosition(position)).setUnread(false);
        SharedPreferences settings = getActivity().getSharedPreferences("FeedYa!Settings", 0);
        String authCode = settings.getString("authCode", "0");
        Utils.markAsReadEntry(authCode, getResources(), listA.get(position).getId());
        adapter.notifyDataSetChanged();

        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_open, R.anim.anim_out);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listA = new ArrayList<ArticleItemBean>();
        SharedPreferences settings = getActivity().getSharedPreferences("FeedYa!Settings", 0);
        String authCode = settings.getString("authCode", "0");
        String user = settings.getString("profileId", "0");
        add = settings.getBoolean("AddedToReadList", false);
        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            loadFeeds = new LoadFeedsTask(authCode, this.getResources(), user);
            loadFeeds.execute(new String[] {});
        } else {
            Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(false);
        Log.i("LecturaFragment", "tamaño: " + listA.size());

        layout = (PullToRefreshLayout) inflater.inflate(R.layout.fragment_ultimahora, container, false);
        mNoResultsView = (TextView) layout.findViewById(R.id.no_results_text_view);
        listLectura = (ListView) layout.findViewById(android.R.id.list);
        SharedPreferences settings = getActivity().getSharedPreferences("FeedYa!Settings", 0);
        final String authCode = settings.getString("authCode", "0");
        final String user = settings.getString("profileId", "0");
        listLectura.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int arg2, long arg3) {
                try {
                    HttpResponse response = Utils.deleteFromSaveLater(authCode, getResources(), user,
                            ((ArticleItemBean) getListView().getItemAtPosition(arg2)).getId());
                    if (response.getStatusLine().getStatusCode() == 200) {
                        Toast.makeText(getActivity().getApplicationContext(), "Borrado de Lista de Lectura",
                                Toast.LENGTH_LONG).show();
                        listA.remove(arg2);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "No se ha borrado correctamente",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (NullPointerException e) {

                }
                return true;
            }

        });
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private class AddFeedTask extends AsyncTask<String, Integer, Boolean> {

        private LecturaFragment activity;
        private String authCode;
        private Resources resources;
        private String user;

        public AddFeedTask(LecturaFragment activity, String authCode, Resources resources, String user) {
            this.activity = activity;
            this.authCode = authCode;
            this.resources = resources;
            this.user = user;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                Log.i("LecturaFragment", "tamañoLoad: " + load.items.size());

                if (load.items.size() > listA.size()) {

                    Log.i("LecturaFragment", "INFONOTICIA: " + load.title);
                    listA = new ArrayList<ArticleItemBean>();
                    String content = "";
                    for (int i = 0; i < load.items.size(); i++) {
                        Date date = new Date(load.items.get(i).published);
                        // DateFormat formatter = new SimpleDateFormat("HH:mm");
                        // String dateFormatted = formatter.format(date);
                        long diff = (new Date()).getTime() - date.getTime();
                        if ((diff / 1000) < 60)
                            dateFormatted = "hace " + Integer.toString((int) (diff / 1000)) + " seg.";
                        else if ((diff / 60000) < 60)
                            dateFormatted = "hace " + Integer.toString((int) (diff / (1000 * 60))) + " min.";
                        else if ((diff / (60000 * 60)) < 24)
                            dateFormatted = "hace " + Integer.toString((int) (diff / (1000 * 60 * 60))) + " horas";
                        else
                            dateFormatted = "hace " + Integer.toString((int) (diff / (1000 * 60 * 60 * 24))) + " dias";

                        Log.i("LecturaFragment", "INFONOTICIA: " + load.items.get(i).originId);
                        articleIcon = null;
                        if (load.items.get(i).content != null)
                            content = load.items.get(i).content.content;
                        else
                            content = load.items.get(i).summary.content;

                        if ((load.items.get(i).visual != null) && !load.items.get(i).visual.getUrl().equals("none")) {

                            listA.add(new ArticleItemBean(load.items.get(i).title, articleIcon,
                                    load.items.get(i).originId, load.items.get(i).visual.getUrl(),
                                    load.items.get(i).origin.title + "/" + dateFormatted, load.items.get(i).id,
                                    load.items.get(i).unread, content, load.items.get(i).author));

                        } else {
                            articleIcon = null;
                            listA.add(new ArticleItemBean(load.items.get(i).title, articleIcon,
                                    load.items.get(i).originId, "", load.items.get(i).origin.title + "/"
                                            + dateFormatted, load.items.get(i).id, load.items.get(i).unread, content,
                                    load.items.get(i).author));
                        }
                    }

                }
                return true;
            } catch (NullPointerException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                dialog.setVisibility(View.GONE);
                listLectura.setVisibility(View.VISIBLE);

                if (activity.getActivity() != null) {
                    Log.i("LecturaFragment", "Actualizar Adapter");

                    adapter = new ArticleListItemAdapter(activity.getActivity(), listA);
                    adapter.notifyDataSetChanged();
                    listLectura.invalidateViews();
                    setListAdapter(adapter);

                }
            } else {
                dialog.setVisibility(View.GONE);
                Toast.makeText(getActivity().getApplicationContext(), "Se ha producido un error al cargar los feeds",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {
            if (listA != null) {
                dialog.setVisibility(View.VISIBLE);
                listLectura.setVisibility(View.GONE);
            }

        }

        @Override
        protected void onCancelled(Boolean result) {
            // TODO Auto-generated method stub
            super.onCancelled(result);
        }

    }

    private class LoadFeedsTask extends AsyncTask<String, Integer, Boolean> {

        private String authCode;
        private Resources resources;
        private String user;

        public LoadFeedsTask(String authCode, Resources resources, String user) {
            this.authCode = authCode;
            this.resources = resources;
            this.user = user;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                load = Utils.LoadSavedForLater(user, authCode, resources);
            } catch (NullPointerException e) {

            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onCancelled(Boolean result) {
            // TODO Auto-generated method stub
            super.onCancelled(result);
        }

    }

    @Override
    public void onPause() {
        Log.v("LecturaFragment", "ON PAUSE");
        super.onPause();
    }

    @Override
    public void onStop() {

        Log.v("LecturaFragment", "ON STOP");
        super.onStop();
    }

    @Override
    public void onResume() {
        Log.v("LecturaFragment", "ON RESUME");

        SharedPreferences settings = getActivity().getSharedPreferences("FeedYa!Settings", 0);
        add = settings.getBoolean("AddedToReadList", false);

        String authCode = settings.getString("authCode", "0");
        String user = settings.getString("profileId", "0");

        listLectura = (ListView) layout.findViewById(android.R.id.list);
        dialog = (ProgressBar) layout.findViewById(R.id.marker_progress);
        Log.v("LecturaFragment", "AÑADIDO: " + add);
        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            if (listA.size() == 0) {
                loadFeeds = new LoadFeedsTask(authCode, this.getResources(), user);
                loadFeeds.execute(new String[] {});
                listA = new ArrayList<ArticleItemBean>();
                addToList = new AddFeedTask(this, authCode, getResources(), user);
                addToList.execute(new String[] {});

            } else if (add == true) {
                loadFeeds = new LoadFeedsTask(authCode, this.getResources(), user);
                loadFeeds.execute(new String[] {});
                listA = new ArrayList<ArticleItemBean>();
                addToList = new AddFeedTask(this, authCode, getResources(), user);
                addToList.execute(new String[] {});
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("AddedToReadList", false);
                editor.commit();
            }
        } else {
            Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_LONG).show();
        }
        super.onResume();
    }

}
