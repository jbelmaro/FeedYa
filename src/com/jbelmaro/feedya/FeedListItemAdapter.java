package com.jbelmaro.feedya;

import java.util.List;

import org.apache.http.HttpResponse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jbelmaro.feedya.util.Category;
import com.jbelmaro.feedya.util.FeedItemBean;
import com.jbelmaro.feedya.util.Subscription;
import com.jbelmaro.feedya.util.Utils;

public class FeedListItemAdapter extends ArrayAdapter<FeedItemBean> {
    private final List<FeedItemBean> list;
    private final Activity context;
    private CharSequence[] categories;
    private String authCode;
    private Subscription subs;
    private List<Category> categoriesStored;
    private FeedAsincrono tarea;

    public FeedListItemAdapter(Activity context, List<FeedItemBean> list) {
        super(context, R.layout.feed_row, list);
        this.context = context;
        this.list = list;
        SharedPreferences settings = getContext().getSharedPreferences("FeedYa!Settings", 0);
        authCode = settings.getString("authCode", "0");
        categoriesStored = Utils.getCategories(authCode, getContext().getResources());
        if (categoriesStored != null) {
            categories = new String[categoriesStored.size()];
            for (int i = 0; i < categoriesStored.size(); i++) {
                categories[i] = Utils.capitalize(categoriesStored.get(i).getLabel());

            }
        }
    }

    static class ViewHolder {
        protected TextView text;
        protected ImageView icon;
        protected ImageButton favorite;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.feed_row, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.feed_title);
            viewHolder.icon = (ImageView) view.findViewById(R.id.feed_icon);
            viewHolder.favorite = (ImageButton) view.findViewById(R.id.buttonFav);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(Html.fromHtml(list.get(position).getTitle()));
        holder.icon.setImageBitmap(list.get(position).getIcon());
        holder.icon.getLayoutParams().height = 120;
        holder.icon.getLayoutParams().width = 120;
        holder.favorite.setImageResource(android.R.drawable.star_off);

        View.OnClickListener imgButtonHandler = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // holder.favorite.setImageResource(R.drawable.ic_action_important);

                categoriesStored = Utils.getCategories(authCode, getContext().getResources());
                categories = new String[categoriesStored.size()];
                for (int i = 0; i < categoriesStored.size(); i++) {
                    categories[i] = Utils.capitalize(categoriesStored.get(i).getLabel());

                }
                subs = new Subscription();
                subs.setId(list.get(position).getFeedURL());
                subs.setTitle(list.get(position).getTitle());

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                // Set the dialog title
                builder.setTitle("Categorias")
                // Specify the list array, the items to be selected by default
                // (null for none),
                // and the listener through which to receive callbacks when
                // items are selected
                        .setItems(categories, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Category[] cats = {categoriesStored.get(which)};
                                subs.setCategories(cats);
                                SharedPreferences settings = getContext().getSharedPreferences("FeedYa!Settings", 0);
                                String authCode = settings.getString("authCode", "0");
                                tarea = new FeedAsincrono(authCode, getContext().getResources());
                                tarea.execute(new String[] {});

                            }
                        })
                        // Set the action buttons
                        .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }).setNeutralButton("AÑADIR CATEGORIA", new AlertDialog.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                final SharedPreferences settings = getContext().getSharedPreferences("FeedYa!Settings",
                                        0);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                final EditText input = new EditText(getContext());
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                                        LayoutParams.MATCH_PARENT);
                                input.setLayoutParams(lp);
                                builder.setPositiveButton("CREAR", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Category category = new Category();
                                        category.setLabel(input.getText().toString());
                                        Log.v("FeedListItemAdapter",
                                                "Categoria creada: " + "user/" + settings.getString("profileId", "0")
                                                        + "/category/" + category.getLabel());
                                        category.setId("user/" + settings.getString("profileId", "0") + "/category/"
                                                + category.getLabel());
                                        Category[] cats = {category};
                                        subs.setCategories(cats);
                                        ConnectivityManager connMgr = (ConnectivityManager) getContext()
                                                .getSystemService(Context.CONNECTIVITY_SERVICE);
                                        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                                        if (networkInfo != null && networkInfo.isConnected()) {
                                            SharedPreferences settings = getContext().getSharedPreferences(
                                                    "FeedYa!Settings", 0);
                                            String authCode = settings.getString("authCode", "0");

                                            tarea = new FeedAsincrono(authCode, getContext().getResources());
                                            tarea.execute(new String[] {});

                                        } else {
                                            Toast.makeText(getContext(), "No hay conexión disponible en este momento",
                                                    Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.setView(input);
                                alert.setTitle("Añade nueva categoria");

                                alert.show();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

                ((Button) alert.findViewById(android.R.id.button3)).setBackgroundColor(Color.LTGRAY);

                // se hace el envío

            }
        };
        holder.favorite.setOnClickListener(imgButtonHandler);
        holder.favorite.setFocusable(false);
        return view;
    }

    private class FeedAsincrono extends AsyncTask<String, Integer, Boolean> {

        private String authCode;
        private Resources resources;
        private HttpResponse response;
        public FeedAsincrono(String authCode, Resources resources) {
            this.authCode = authCode;
            this.resources = resources;

        }

        @Override
        protected Boolean doInBackground(String... params) {

            response = Utils.addSubscription(authCode, resources, subs);
            
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (response.getStatusLine().getStatusCode() == 200) {
                Toast.makeText(getContext(), "Se ha añadido una nueva subscripcion", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "No se ha añadido correctamente", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {

        }
    }
}
