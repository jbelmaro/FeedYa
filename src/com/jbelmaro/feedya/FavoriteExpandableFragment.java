package com.jbelmaro.feedya;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.Toast;

import com.jbelmaro.feedya.util.Category;
import com.jbelmaro.feedya.util.FeedItemBean;
import com.jbelmaro.feedya.util.Subscription;
import com.jbelmaro.feedya.util.Utils;

public class FavoriteExpandableFragment extends Fragment {

    private CategoryAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> categoriesHeader;
    private List<String> categoriesIds;
    private HashMap<String, List<FeedItemBean>> listFeed;
    private Asincrono tarea;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = this.getActivity().getSharedPreferences("FeedYa!Settings", 0);
        String authCode = settings.getString("authCode", "0");

        tarea = new Asincrono(this, authCode, this.getResources());
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            tarea.execute(new String[] {});
        } else {
            Toast.makeText(getActivity(), "No hay conexión disponible en este momento", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(false);

        expListView = (ExpandableListView) inflater.inflate(R.layout.fragment_favoritesexpandable, container, false);

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
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                return false;
            }
        });
        expListView.setOnGroupClickListener(new OnGroupClickListener() {
            
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Log.i("FavoritesFragment", "Item clicked: " + id);
                Intent intent = new Intent(getActivity().getApplicationContext(), CategoryActivity.class);
                intent.putExtra("titulo",categoriesHeader.get(groupPosition));
                intent.putExtra("categoryId", categoriesIds.get(groupPosition));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                return false;
            }
        });
        return expListView;
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

    }

    private class Asincrono extends AsyncTask<String, Integer, Boolean> {

        private FavoriteExpandableFragment activity;
        private String authCode;
        private Resources resources;
        private List<Category> categories;
        private List<Subscription> subscriptions;
        private Bitmap feedIcon;

        public Asincrono(FavoriteExpandableFragment a, String authCode, Resources resources) {
            activity = a;
            this.authCode = authCode;
            this.resources = resources;

        }

        @Override
        protected Boolean doInBackground(String... params) {
            categories = Utils.getCategories(authCode, resources);
            if (categories != null) {
                categoriesHeader = new ArrayList<String>();
                categoriesIds = new ArrayList<String>();
                listFeed = new HashMap<String, List<FeedItemBean>>();
                Locale l = Locale.getDefault();
                for (Category category : categories) {
                    categoriesHeader.add(category.getLabel().toUpperCase(l));
                    categoriesIds.add(category.getId());
                }

                subscriptions = Utils.getSubscriptions(authCode, resources);
                if (subscriptions != null) {
                    for (String categoryGetted : categoriesHeader) {
                        Log.v("FavoriteExpandableFragment", "Category: " + categoryGetted);
                        List<FeedItemBean> listFeedReceive = new ArrayList<FeedItemBean>();
                        for (Subscription subscription : subscriptions) {

                            for (Category c : subscription.getCategories()) {

                                Log.v("FavoriteExpandableFragment", "Category from Subs: " + c.getLabel());

                                if (c.getLabel().toUpperCase(l).equals(categoryGetted)) {
                                    feedIcon = Utils.downloadBitmap(subscription.getWebsite());
                                    Bitmap circleBitmap = Bitmap.createBitmap(feedIcon.getWidth(),
                                            feedIcon.getHeight(), Bitmap.Config.ARGB_8888);

                                    BitmapShader shader = new BitmapShader(feedIcon, TileMode.CLAMP, TileMode.CLAMP);
                                    Paint paint = new Paint();
                                    paint.setShader(shader);
                                    paint.setColor(0xFFfffff0);
                                    paint.setMaskFilter(new BlurMaskFilter(5.0f, BlurMaskFilter.Blur.INNER));
                                    Canvas canvas = new Canvas(circleBitmap);
                                    canvas.drawCircle(feedIcon.getWidth() / 2, feedIcon.getHeight() / 2,
                                            (float) (feedIcon.getWidth() / 2 - 0.1), paint);

                                    FeedItemBean feed = new FeedItemBean(subscription.getTitle(), circleBitmap, null,
                                            subscription.getId(), subscription.getWebsite(), null);
                                    listFeedReceive.add(feed);
                                }
                            }
                        }
                        listFeed.put(categoryGetted, listFeedReceive);
                    }
                }
                listAdapter = new CategoryAdapter(activity.getActivity(), categoriesHeader, listFeed);
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            expListView.setAdapter(listAdapter);
            registerForContextMenu(expListView);

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
