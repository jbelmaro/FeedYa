package com.jbelmaro.feedya;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jbelmaro.feedya.util.Category;
import com.jbelmaro.feedya.util.CategoryItem;
import com.jbelmaro.feedya.util.Count;
import com.jbelmaro.feedya.util.Counts;
import com.jbelmaro.feedya.util.FeedItemBean;
import com.jbelmaro.feedya.util.Subscription;
import com.jbelmaro.feedya.util.Utils;

public class FavoriteExpandableFragment extends Fragment {

    private CategoryAdapter listAdapter;
    private ExpandableListView expListView;
    private List<CategoryItem> categoriesHeader;
    private HashMap<String, List<FeedItemBean>> listFeed;
    private LoadCategoriesTask tarea;
    private TextView noFavsTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = this.getActivity().getSharedPreferences("FeedYa!Settings", 0);
        String authCode = settings.getString("authCode", "0");

        tarea = new LoadCategoriesTask(this, authCode, this.getResources());
        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            tarea.execute(new String[] {});
        } else {
            Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(false);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_favoritesexpandable, container, false);
        noFavsTextView = (TextView) layout.findViewById(R.id.no_results_text_view);
        expListView = (ExpandableListView) layout.findViewById(R.id.favoritesExpandable);

        expListView.setAdapter(listAdapter);

        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.i("FavoritesFragment", "Item clicked: " + id);
                Intent intent = new Intent(getActivity().getApplicationContext(), NewsActivity.class);
                intent.putExtra("titulo",
                        ((FeedItemBean) listAdapter.getChild(groupPosition, childPosition)).getTitle());
                intent.putExtra("fuente",
                        ((FeedItemBean) listAdapter.getChild(groupPosition, childPosition)).getFeedURL());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_open, R.anim.anim_out);

                return false;
            }
        });
        expListView.setFocusable(false);
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void onBackPressed() {

        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("FavoriteExpandableFragment", "ON RESUME");

        SharedPreferences settings = this.getActivity().getSharedPreferences("FeedYa!Settings", 0);
        String authCode = settings.getString("authCode", "0");
        LoadCategoriesTask tareaCat = new LoadCategoriesTask(this, authCode, this.getResources());
        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            tareaCat.execute(new String[] {});

        } else {
            Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_LONG).show();
        }

    }

    private class LoadCategoriesTask extends AsyncTask<String, Integer, Boolean> {

        private FavoriteExpandableFragment activity;
        private String authCode;
        private Resources resources;
        private List<Category> categories;
        private List<Subscription> subscriptions;
        private Bitmap feedIcon;
        private Counts counters;
        private int count;
        private LoadCountTask tareaCount;

        public LoadCategoriesTask(FavoriteExpandableFragment a, String authCode, Resources resources) {
            activity = a;
            this.authCode = authCode;
            this.resources = resources;

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                counters = Utils.LoadUnreadCounts(authCode, resources);
                categories = Utils.getCategories(authCode, resources);
                tareaCount = new LoadCountTask(activity, authCode, resources);

                if (categories != null) {
                    categoriesHeader = new ArrayList<CategoryItem>();
                    listFeed = new HashMap<String, List<FeedItemBean>>();
                    Locale l = Locale.getDefault();
                    for (Category category : categories) {
                        Iterator<Count> iterator = counters.getUnreadcounts().iterator();
                        while (iterator.hasNext()) {
                            Count c = iterator.next();
                            if (category.getId().equals(c.getId()))
                                count = c.getCount();
                        }
                        categoriesHeader.add(new CategoryItem(category.getLabel().toUpperCase(l), category.getId(),
                                count));
                    }

                    subscriptions = Utils.getSubscriptions(authCode, resources);
                    if (subscriptions != null) {
                        for (int i = 0; i < categoriesHeader.size(); i++) {
                            String categoryGetted = categoriesHeader.get(i).getTitle();
                            Log.v("FavoriteExpandableFragment", "Category: " + categoryGetted);
                            List<FeedItemBean> listFeedReceive = new ArrayList<FeedItemBean>();
                            for (Subscription subscription : subscriptions) {

                                for (Category c : subscription.getCategories()) {

                                    Log.v("FavoriteExpandableFragment", "Category from Subs: " + c.getLabel());

                                    if (c.getLabel().toUpperCase(l).equals(categoryGetted)) {
                                        Class<?> clz = subscription.getClass();
                                        Bitmap circleBitmap = null;

                                        try {
                                            feedIcon = Utils.downloadBitmap(subscription.getVisualUrl(), true);
                                            Field f = clz.getField("visualUrl");
                                            Log.v("FavoriteExpandableFragment",
                                                    "visualURL: " + subscription.getVisualUrl());

                                        } catch (NoSuchFieldException ex) {
                                            // feedIcon =
                                            // Utils.downloadBitmap(subscription.getWebsite(),
                                            // false);
                                        } catch (SecurityException ex) {
                                            // no access to field
                                        } catch (NullPointerException ex) {
                                            try {
                                                feedIcon = Utils.downloadBitmap(subscription.getWebsite(), false);
                                            } catch (NullPointerException ez) {
                                            }
                                        }/*
                                          * circleBitmap =
                                          * Bitmap.createBitmap(feedIcon
                                          * .getWidth(), feedIcon.getHeight(),
                                          * Bitmap.Config.ARGB_8888);
                                          * BitmapShader shader = new
                                          * BitmapShader(feedIcon,
                                          * TileMode.CLAMP, TileMode.CLAMP);
                                          * Paint paint = new Paint();
                                          * paint.setShader(shader);
                                          * paint.setColor(0xFFfffff0);
                                          * paint.setMaskFilter(new
                                          * BlurMaskFilter(5.0f,
                                          * BlurMaskFilter.Blur.INNER)); Canvas
                                          * canvas = new Canvas(circleBitmap);
                                          * canvas
                                          * .drawCircle(feedIcon.getWidth() / 2,
                                          * feedIcon.getHeight() / 2, (float)
                                          * (feedIcon.getWidth() / 2 - 0.1),
                                          * paint);
                                          */
                                        Iterator<Count> iterator = counters.getUnreadcounts().iterator();
                                        int countFeed = 0;
                                        while (iterator.hasNext()) {
                                            Count countItem = iterator.next();
                                            if (subscription.getId().equals(countItem.getId()))
                                                countFeed = countItem.getCount();
                                        }
                                        FeedItemBean feed = new FeedItemBean(subscription.getTitle(), feedIcon, null,
                                                subscription.getId(), subscription.getWebsite(), null, countFeed);
                                        listFeedReceive.add(feed);
                                    }
                                }
                            }
                            listFeed.put(categoryGetted, listFeedReceive);
                        }
                    }
                    listAdapter = new CategoryAdapter(activity.getActivity(), activity.getActivity(), categoriesHeader,
                            listFeed);
                }
                return true;
            } catch (NullPointerException e) {
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                expListView.setAdapter(listAdapter);
                registerForContextMenu(expListView);
                if (listAdapter.isEmpty())
                    noFavsTextView.setVisibility(View.VISIBLE);
                tareaCount.execute(new String[] {});
            }
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

    private class LoadCountTask extends AsyncTask<String, Integer, Boolean> {

        private FavoriteExpandableFragment activity;
        private String authCode;
        private Resources resources;
        private Counts counters;
        private int count;

        public LoadCountTask(FavoriteExpandableFragment a, String authCode, Resources resources) {
            activity = a;
            this.authCode = authCode;
            this.resources = resources;

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                counters = Utils.LoadUnreadCounts(authCode, resources);
                if (categoriesHeader != null) {
                    for (int i = 0; i < categoriesHeader.size(); i++) {
                        List<FeedItemBean> list = listFeed.get(categoriesHeader.get(i).getTitle());
                        if (counters != null) {
                            Iterator<Count> iterator = counters.getUnreadcounts().iterator();

                            while (iterator.hasNext()) {
                                Count c = iterator.next();
                                if (categoriesHeader.get(i).getCategoryId().equals(c.getId())) {
                                    count = c.getCount();
                                    categoriesHeader.get(i).setItemCount(count);
                                }
                            }

                            for (int j = 0; j < list.size(); j++) {
                                iterator = counters.getUnreadcounts().iterator();
                                while (iterator.hasNext()) {
                                    Count c = iterator.next();
                                    if (list.get(j).getFeedURL().equals(c.getId())) {
                                        count = c.getCount();
                                        list.get(j).setCount(count);
                                    }
                                }
                            }
                        }
                    }
                } else {

                }
                return true;
            } catch (NullPointerException e) {
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result)
                listAdapter.notifyDataSetChanged();
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
}
