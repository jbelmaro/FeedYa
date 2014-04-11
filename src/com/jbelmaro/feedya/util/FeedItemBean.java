package com.jbelmaro.feedya.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FeedItemBean {

    private String title;
    private Bitmap icon;
    private Drawable favorite;
    private Drawable settings;
    private String feedURL;
    private String imageURL;
    private int count;

    public FeedItemBean(String t, Bitmap i, Drawable f, String feedURL, String imageURL, Drawable s, int count) {
        title = t;
        icon = i;
        favorite = f;
        this.feedURL = feedURL;
        this.imageURL = imageURL;
        settings = s;
        this.setCount(count);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public Drawable getFavorite() {
        return favorite;
    }

    public void setFavorite(Drawable favorite) {
        this.favorite = favorite;
    }

    public String getFeedURL() {
        return feedURL;
    }

    public void setFeedURL(String feedURL) {
        this.feedURL = feedURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Drawable getSettings() {
        return settings;
    }

    public void setSettings(Drawable settings) {
        this.settings = settings;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count2) {
        this.count = count2;
    }

}
