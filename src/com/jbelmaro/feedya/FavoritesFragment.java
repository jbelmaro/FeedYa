package com.jbelmaro.feedya;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jbelmaro.feedya.util.FeedItemBean;
import com.jbelmaro.feedya.util.SQLiteUtils;
import com.jbelmaro.feedya.util.Utils;

public class FavoritesFragment extends ListFragment {

    private ArrayAdapter<FeedItemBean> adapter;
    private List<FeedItemBean> listA;
    private Bitmap feedIcon;

    public FavoritesFragment() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SQLiteUtils sqlite = new SQLiteUtils(getActivity(), "DBFeedYa", null, 1);
        SQLiteDatabase db = sqlite.getReadableDatabase();
        Cursor c = db.rawQuery(" SELECT * FROM Feeds", null);
        listA = new ArrayList<FeedItemBean>();

        if (c.moveToFirst()) {
            // Recorremos el cursor hasta que no haya m??s registros
            do {
                Log.i("FavoritesFragment",
                        "database feed:\n" + "\nTitle: " + c.getString(0) + "\nIMAGE: " + c.getString(1)
                                + "\nfeedURL: " + c.getString(2) + "\nimageURL: " + c.getString(3));

                feedIcon = Utils.downloadBitmap(c.getString(3),false);
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
                listA.add(new FeedItemBean(c.getString(0), circleBitmap, null, c.getString(2), c.getString(3), null, 0));
            } while (c.moveToNext());
        }
        Log.i("FavoritesFragment", "DB STATUS: " + db.getPath());
        db.close();

        // busqueda de feeds
        adapter = new FeedListItemAdapter(getActivity(), listA);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("FavoritesFragment", "Item clicked: " + id);
        Intent intent = new Intent(getActivity().getApplicationContext(), NewsActivity.class);
        intent.putExtra("titulo", adapter.getItem(position).getTitle());
        intent.putExtra("fuente", ((FeedItemBean) l.getItemAtPosition(position)).getFeedURL());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

    }

    public void onBackPressed() {
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
}
