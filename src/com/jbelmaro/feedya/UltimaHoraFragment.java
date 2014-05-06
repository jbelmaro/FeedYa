package com.jbelmaro.feedya;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
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
import android.widget.Toast;

import com.jbelmaro.feedya.util.ArticleItemBean;
import com.jbelmaro.feedya.util.SaveForLaterItem;
import com.jbelmaro.feedya.util.StreamContentResponse;
import com.jbelmaro.feedya.util.Utils;

public class UltimaHoraFragment extends ListFragment implements OnRefreshListener {

    private StreamContentResponse load;
    private ArrayAdapter<ArticleItemBean> adapter;
    private List<ArticleItemBean> listA = null;
    private Bitmap articleIcon;
    private AddFeedTask addToList;
    private LoadFeedsTask loadFeeds;
    private PullToRefreshLayout layout;
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

            loadFeeds = new LoadFeedsTask(authCode, this.getResources(), user, this);
            loadFeeds.execute(new String[] {});
        } else {
            Toast.makeText(getActivity(), "No hay conexión disponible en este momento", Toast.LENGTH_LONG).show();
        }
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        Log.i("MainActivity", "UNREAD: " + settings.getInt("loadValue", 0));
        select_tab(actionBar, settings.getInt("loadValue", 0));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.valores_spinner,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionBar.setListNavigationCallbacks(adapter, new OnNavigationListener() {

            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                SharedPreferences settings = getActivity().getSharedPreferences("FeedYa!Settings", 0);
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
        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            SharedPreferences settings = getActivity().getSharedPreferences("FeedYa!Settings", 0);
            String authCode = settings.getString("authCode", "0");
            String user = settings.getString("profileId", "0");
            loadFeeds = new LoadFeedsTask(authCode, this.getResources(), user, this);
            loadFeeds.execute(new String[] {});
        } else {
            Toast.makeText(getActivity(), "No hay conexión disponible en este momento", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(false);
        Log.i("LecturaFragment", "tamaño: " + listA.size());

        layout = (PullToRefreshLayout) inflater.inflate(R.layout.fragment_ultimahora, container, false);
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
        // We can now setup the PullToRefreshLayout
        ActionBarPullToRefresh
                .from(getActivity())
                // We need to insert the PullToRefreshLayout into the Fragment's
                // ViewGroup
                .insertLayoutInto((ViewGroup) getView()).theseChildrenArePullable(listUltima).listener(this)
                .setup(layout);

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onRefreshStarted(View view) {
        // TODO Auto-generated method stub

        // setListShown(false); // This will hide the listview and visible a
        // round progress bar
        SharedPreferences settings = getActivity().getSharedPreferences("FeedYa!Settings", 0);
        String authCode = settings.getString("authCode", "0");
        String user = settings.getString("profileId", "0");

        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            loadFeeds = new LoadFeedsTask(authCode, this.getResources(), user, this);
            loadFeeds.execute(new String[] {});
        } else {
            Toast.makeText(getActivity(), "No hay conexión disponible en este momento", Toast.LENGTH_LONG).show();
        }

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
            try {
                Log.i("UltimaHoraFragment", "tamañoLoad: " + load.items.size());

                Log.i("UltimaHoraFragment", "INFONOTICIA: " + load.title);
                listA = new ArrayList<ArticleItemBean>();
                for (int i = 0; i < load.items.size(); i++) {
                    Date date = new Date(load.items.get(i).published);
                    // DateFormat formatter = new SimpleDateFormat("HH:mm");
                    // String dateFormatted = formatter.format(date);
                    long diff = (new Date()).getTime() - date.getTime();
                    String content = "";
                    if ((diff / 1000) < 60)
                        dateFormatted = "hace " + Integer.toString((int) (diff / 1000)) + "s";
                    else if ((diff / 60000) < 60)
                        dateFormatted = "hace " + Integer.toString((int) (diff / (1000 * 60))) + "m";
                    else if ((diff / (60000 * 60)) < 24)
                        dateFormatted = "hace " + Integer.toString((int) (diff / (1000 * 60 * 60))) + "h";
                    else
                        dateFormatted = "hace " + Integer.toString((int) (diff / (1000 * 60 * 60 * 24))) + "d";

                    Log.i("UltimaHoraFragment", "INFONOTICIA: " + load.items.get(i).originId);
                    articleIcon = null;
                    if (load.items.get(i).content != null)
                        content = load.items.get(i).content.content;
                    else
                        content = load.items.get(i).summary.content;

                    if ((load.items.get(i).visual != null) && !load.items.get(i).visual.getUrl().equals("none")) {
                        listA.add(new ArticleItemBean(load.items.get(i).title, articleIcon, load.items.get(i).originId,
                                load.items.get(i).visual.getUrl(),
                                load.items.get(i).origin.title + "/" + dateFormatted, load.items.get(i).id, load.items
                                        .get(i).unread, content, load.items.get(i).author));
                        articleIcon = null;
                    } else {
                        articleIcon = null;
                        listA.add(new ArticleItemBean(load.items.get(i).title, articleIcon, load.items.get(i).originId,
                                "", load.items.get(i).origin.title + "/" + dateFormatted, load.items.get(i).id,
                                load.items.get(i).unread, content, load.items.get(i).author));
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

                listUltima.setVisibility(View.VISIBLE);

                if (activity.getActivity() != null) {
                    Log.i("UltimaHoraFragment", "Actualizar Adapter");

                    adapter = new ArticleListItemAdapter(activity.getActivity(), listA);
                    adapter.notifyDataSetChanged();
                    listUltima.invalidateViews();
                    setListAdapter(adapter);

                }
            } else {

                Toast.makeText(getActivity().getApplicationContext(), "Se ha producido un error al cargar los feeds",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {
            if (listA.size() == 0) {
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
        private UltimaHoraFragment fragment;

        public LoadFeedsTask(String authCode, Resources resources, String user, UltimaHoraFragment fragment) {
            this.authCode = authCode;
            this.resources = resources;
            this.user = user;
            this.fragment = fragment;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                SharedPreferences settings = getActivity().getSharedPreferences("FeedYa!Settings", 0);
                int loadValue = settings.getInt("loadValue", 0);
                if (loadValue == 0)
                    load = Utils.LoadLatest(user, authCode, resources, "&unreadOnly=false");
                else
                    load = Utils.LoadLatest(user, authCode, resources, "&unreadOnly=true");

            } catch (NullPointerException e) {
                //
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            layout.setRefreshComplete();
            SharedPreferences settings = getActivity().getSharedPreferences("FeedYa!Settings", 0);
            boolean add = settings.getBoolean("AddedToReadList", false);
            Log.v("UltimaHoraFragment", "AÑADIDO: " + add);
            String authCode = settings.getString("authCode", "0");
            String user = settings.getString("profileId", "0");
            addToList = new AddFeedTask(fragment, authCode, resources, user);
            addToList.execute(new String[] {});
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

    private void select_tab(ActionBar b, int pos) {
        try {
            // do the normal tab selection in case all tabs are visible
            b.setSelectedNavigationItem(pos);

            // now use reflection to select the correct Spinner if
            // the bar's tabs have been reduced to a Spinner

            View action_bar_view = getActivity().findViewById(
                    getResources().getIdentifier("action_bar", "id", "android"));
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
