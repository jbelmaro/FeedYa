package com.jbelmaro.feedya;

import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jbelmaro.feedya.FeedListItemAdapter.ViewHolder;
import com.jbelmaro.feedya.util.FeedItemBean;
import com.jbelmaro.feedya.util.Utils;

public class CategoryAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> categoriesHeader;
    private HashMap<String, List<FeedItemBean>> listFeed;

    public CategoryAdapter(Context context, List<String> categoriesHeader, HashMap<String, List<FeedItemBean>> listFeed) {
        this.context = context;
        this.categoriesHeader = categoriesHeader;
        this.listFeed = listFeed;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listFeed.get(this.categoriesHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
            ViewGroup parent) {
        final FeedItemBean child = (FeedItemBean) getChild(groupPosition, childPosition);
        Log.v("CategoryAdapter", "Feed: " + child.getTitle());

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.feed_row, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.feed_title);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.feed_icon);
            viewHolder.favorite = (ImageButton) convertView.findViewById(R.id.buttonFav);
            convertView.setTag(viewHolder);
        }
        final ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.text.setText(Html.fromHtml(child.getTitle()));
        holder.icon.setImageBitmap(child.getIcon());
        holder.icon.getLayoutParams().height = 120;
        holder.icon.getLayoutParams().width = 120;
        holder.favorite.setFocusable(false);

        holder.favorite.setImageResource(R.drawable.ic_menu_moreoverflow_normal_holo_light);

        View.OnClickListener imgButtonHandler = new View.OnClickListener() {

            PopupWindow popupWindow;
            String[] popUpContents = {"Borrar Subscripcion"};

            @Override
            public void onClick(final View v) {
                Log.v("CategoryAdapter", "Feed Pulsado: " + child.getTitle());
                popupWindow = new PopupWindow(v.getContext());
                ListView listPopup = new ListView(v.getContext());
                listPopup.setBackgroundColor(Color.WHITE);
                listPopup.setAdapter(new ArrayAdapter<String>(v.getContext(), R.layout.popup_list, popUpContents) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {

                        // setting the ID and text for every items in the list

                        String text = getItem(position);

                        // visual settings for the list item
                        TextView listItem = new TextView(this.getContext());

                        listItem.setText(text);
                        listItem.setTag(position);
                        listItem.setTextSize(18);
                        listItem.setTextColor(Color.BLACK);
                        listItem.setBackgroundColor(Color.WHITE);
                        listItem.setHeight(150);

                        return listItem;
                    }
                });
                listPopup.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        popupWindow.dismiss();
                        SharedPreferences settings = v.getContext().getSharedPreferences("FeedYa!Settings", 0);
                        HttpResponse response = Utils.deleteSubscription(settings.getString("authCode", "0"),
                                v.getResources(), child.getFeedURL());
                        if (response.getStatusLine().getStatusCode() == 200) {
                            Toast.makeText(v.getContext(), "Subscripcion borrada", Toast.LENGTH_LONG).show();
                            List<FeedItemBean> listABorrar = listFeed.get(categoriesHeader.get(groupPosition));
                            listABorrar.remove(childPosition);
                            if (listABorrar.size() == 0) {
                                Log.v("CategoryAdapter",
                                        "ESTA VACIA: " + listFeed.size() + " " + categoriesHeader.get(groupPosition));
                                listFeed.remove(categoriesHeader.get(groupPosition));
                                categoriesHeader.remove(groupPosition);
                            }
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(v.getContext(), "Se ha producido un error al borrar", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
                popupWindow.setFocusable(true);
                popupWindow.setWidth(400);
                popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

                // set the list view as pop up window content
                popupWindow.setContentView(listPopup);
                popupWindow.showAsDropDown(v);

            }

        };
        holder.favorite.setOnClickListener(imgButtonHandler);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listFeed.get(this.categoriesHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.categoriesHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.categoriesHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.category_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.category);
        lblListHeader.setTypeface(null, Typeface.BOLD_ITALIC);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void notifyDataSetChanged() {
        // TODO Auto-generated method stub
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        // TODO Auto-generated method stub
        super.notifyDataSetInvalidated();
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return super.isEmpty();
    }
}
