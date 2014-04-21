package com.jbelmaro.feedya;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jbelmaro.feedya.util.ArticleItemBean;
import com.jbelmaro.feedya.util.SaveForLaterItem;
import com.jbelmaro.feedya.util.StreamContentResponse;
import com.jbelmaro.feedya.util.Utils;

public class UltimaHoraFragment extends ListFragment {

    private StreamContentResponse load;
    private ArrayAdapter<ArticleItemBean> adapter;
    private List<ArticleItemBean> listA = null;
    private Bitmap articleIcon;
    private AddFeedTask addToList;
    private LoadFeedsTask loadFeeds;
    private LinearLayout layout;
    private ProgressBar dialog;
    private ListView listUltima;
    private String dateFormatted = "";

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("UltimaHoraFragment", "Item clicked: " + id);
        SharedPreferences settings = getActivity().getSharedPreferences("FeedYa!Settings", 0);
        String authCode = settings.getString("authCode", "0");
        Utils.markAsReadEntry(authCode, getResources(), load.items.get(position).id);
        Intent intent = new Intent(this.getActivity(), ArticleActivity.class);
        intent.putExtra("titulo", "Últimas Noticias");
        if (load.items.get(position).content != null)
            intent.putExtra("noticia", load.items.get(position).content.content);
        else
            intent.putExtra("noticia", load.items.get(position).summary.content);
        intent.putExtra("noticiaURL", load.items.get(position).originId);
        intent.putExtra("noticiaTitulo", load.items.get(position).title);
        intent.putExtra("noticiaLINK", load.items.get(position).originId);
        intent.putExtra("autorNoticia", load.items.get(position).author);
        intent.putExtra("idNoticia", load.items.get(position).id);
        intent.putExtra("fechaNoticia", dateFormatted);
        load.items.get(position).unread = false;
        ((ArticleItemBean) listUltima.getItemAtPosition(position)).setUnread(false);
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

        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            loadFeeds = new LoadFeedsTask(authCode, this.getResources(), user);
            loadFeeds.execute(new String[] {});
        } else {
            Toast.makeText(getActivity(), "No hay conexión disponible en este momento", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(false);
        Log.i("LecturaFragment", "tamaño: " + listA.size());

        layout = (LinearLayout) inflater.inflate(R.layout.fragment_ultimahora, container, false);
        listUltima = (ListView) layout.findViewById(android.R.id.list);
        final SharedPreferences settings = getActivity().getSharedPreferences("FeedYa!Settings", 0);
        final String authCode = settings.getString("authCode", "0");
        final String user = settings.getString("profileId", "0");
        listUltima.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int arg2, long arg3) {

                Toast.makeText(getActivity().getApplicationContext(), "Añadido a Lista de Lectura", Toast.LENGTH_LONG)
                        .show();
                SaveForLaterItem item = new SaveForLaterItem();
                item.setEntryId(((ArticleItemBean) getListView().getItemAtPosition(arg2)).getId());
                Utils.saveForLater(authCode, user, getResources(), item);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("AddedToReadList", true);
                editor.commit();

                return true;
            }

        });
        dialog = (ProgressBar) layout.findViewById(R.id.marker_progress);

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private class AddFeedTask extends AsyncTask<String, Integer, Boolean> {

        private UltimaHoraFragment activity;
        private String authCode;
        private Resources resources;
        private String user;

        public AddFeedTask(UltimaHoraFragment a, String authCode, Resources resources, String user) {
            activity = a;
            this.authCode = authCode;
            this.resources = resources;
            this.user = user;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Log.i("UltimaHoraFragment", "tamañoLoad: " + load.items.size());

            if (load.items.size() > listA.size()) {

                Log.i("UltimaHoraFragment", "INFONOTICIA: " + load.title);
                listA = new ArrayList<ArticleItemBean>();
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

                    Log.i("UltimaHoraFragment", "INFONOTICIA: " + load.items.get(i).originId);
                    articleIcon = null;

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
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.setVisibility(View.GONE);
            listUltima.setVisibility(View.VISIBLE);

            if (activity.getActivity() != null) {
                Log.i("UltimaHoraFragment", "Actualizar Adapter");

                adapter = new ArticleListItemAdapter(activity.getActivity(), listA);
                adapter.notifyDataSetChanged();
                listUltima.invalidateViews();
                setListAdapter(adapter);
            }

        }

        @Override
        protected void onPreExecute() {
            if (listA != null) {
                dialog.setVisibility(View.VISIBLE);
                listUltima.setVisibility(View.GONE);
            }
        }

        @Override
        protected void onCancelled(Boolean result) {
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
            try{
                load = Utils.LoadLatest(user, authCode, resources);
            }catch(NullPointerException e){
                //
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
            super.onCancelled(result);
        }

    }

    @Override
    public void onPause() {
        Log.v("UltimaHoraFragment", "Se cancela la asynctask");

        super.onPause();
    }

    @Override
    public void onStop() {
        Log.v("UltimaHoraFragment", "Se cancela la asynctask");
        super.onStop();
    }

    @Override
    public void onResume() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            SharedPreferences settings = getActivity().getSharedPreferences("FeedYa!Settings", 0);
            boolean add = settings.getBoolean("AddedToReadList", false);
            Log.v("UltimaHoraFragment", "AÑADIDO: " + add);
            String authCode = settings.getString("authCode", "0");
            String user = settings.getString("profileId", "0");
            addToList = new AddFeedTask(this, authCode, this.getResources(), user);
            addToList.execute(new String[] {});

        } else {
            Toast.makeText(getActivity(), "No hay conexión disponible en este momento", Toast.LENGTH_LONG).show();
        }
        super.onResume();
    }

}
