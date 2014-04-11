package com.jbelmaro.feedya;

import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
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
import com.jbelmaro.feedya.util.CategoryItem;
import com.jbelmaro.feedya.util.FeedItemBean;
import com.jbelmaro.feedya.util.Utils;

public class CategoryAdapter extends BaseExpandableListAdapter {

    private Context context;
    private FragmentActivity fragment;
    private List<CategoryItem> categoriesHeader;
    private HashMap<String, List<FeedItemBean>> listFeed;

    public CategoryAdapter(FragmentActivity f, Context context, List<CategoryItem> categoriesHeader,
            HashMap<String, List<FeedItemBean>> listFeed) {
        fragment = f;
        this.context = context;
        this.categoriesHeader = categoriesHeader;
        this.listFeed = listFeed;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listFeed.get(this.categoriesHeader.get(groupPosition).getTitle()).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
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
            viewHolder.count = (TextView) convertView.findViewById(R.id.feed_count);
            convertView.setTag(viewHolder);
        }
        final ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.text.setText(Html.fromHtml(child.getTitle()));
        try {
            holder.icon.setImageBitmap(child.getIcon());
            holder.icon.getLayoutParams().height = 120;
            holder.icon.getLayoutParams().width = 120;
        } catch (NullPointerException e) {

        }
        holder.count.setText("" + child.getCount());
        holder.favorite.setFocusable(false);

        holder.favorite.setImageResource(R.drawable.ic_menu_moreoverflow_normal_holo_light);

        View.OnClickListener imgButtonHandler = new View.OnClickListener() {

            PopupWindow popupWindow;
            String[] popUpContents = {"Borrar Subscripcion", "Marcar Como Leído"};

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
                    public void onItemClick(AdapterView<?> arg0, View arg1, int item, long arg3) {
                        SharedPreferences settings = v.getContext().getSharedPreferences("FeedYa!Settings", 0);
                        if (item == 0) {
                            popupWindow.dismiss();

                            HttpResponse response = Utils.deleteSubscription(settings.getString("authCode", "0"),
                                    v.getResources(), child.getFeedURL());
                            if (response.getStatusLine().getStatusCode() == 200) {
                                Toast.makeText(v.getContext(), R.string.subs_del, Toast.LENGTH_SHORT).show();
                                List<FeedItemBean> listABorrar = listFeed.get(categoriesHeader.get(groupPosition)
                                        .getTitle());

                                int total = categoriesHeader.get(groupPosition).getItemCount()
                                        - listABorrar.get(childPosition).getCount();
                                categoriesHeader.get(groupPosition).setItemCount(total);
                                listABorrar.remove(childPosition);
                                if (listABorrar.size() == 0) {
                                    Log.v("CategoryAdapter", "ESTA VACIA: " + listFeed.size() + " "
                                            + categoriesHeader.get(groupPosition).getTitle());
                                    listFeed.remove(categoriesHeader.get(groupPosition).getTitle());
                                    categoriesHeader.remove(groupPosition);
                                }
                                notifyDataSetChanged();
                            } else {
                                Toast.makeText(v.getContext(), R.string.subs_del_error, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            popupWindow.dismiss();
                            String authCode = settings.getString("authCode", "0");
                            Utils.markAsReadFeed(authCode, arg1.getResources(), child.getFeedURL());

                            categoriesHeader.get(groupPosition).setItemCount(
                                    categoriesHeader.get(groupPosition).getItemCount() - child.getCount());
                            child.setCount(0);
                            notifyDataSetChanged();
                        }
                    }
                });
                popupWindow.setFocusable(true);
                popupWindow.setWidth(400);
                popupWindow.setHeight(LayoutParams.WRAP_CONTENT);

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
        return this.listFeed.get(this.categoriesHeader.get(groupPosition).getTitle()).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.categoriesHeader.get(groupPosition).getTitle();
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
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.category_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.category);
        TextView countView = (TextView) convertView.findViewById(R.id.category_count);
        countView.setText(" " + categoriesHeader.get(groupPosition).getItemCount());
        lblListHeader.setTypeface(null, Typeface.BOLD_ITALIC);
        lblListHeader.setText(headerTitle);
        lblListHeader.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("FavoritesFragment", "Item clicked: " + groupPosition);
                Intent intent = new Intent(context, CategoryActivity.class);
                intent.putExtra("titulo", categoriesHeader.get(groupPosition).getTitle());
                intent.putExtra("categoryId", categoriesHeader.get(groupPosition).getCategoryId());
                context.startActivity(intent);
                fragment.overridePendingTransition(R.anim.anim_open, R.anim.anim_out);
            }
        });
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }
}
