package com.jbelmaro.feedya.util;

import android.graphics.Bitmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleItemBean {

    private String title;
    private Bitmap icon;
    private String articleURL;
    private String iconURL;
    private String time;
    private String id;
    private boolean unread;
    private String content;
    private String author;

    public ArticleItemBean(String t, Bitmap i, String articleURL, String iconURL, String time, String id,
            boolean unread, String content, String author) {
        // TODO Auto-generated constructor stub
        title = t;
        icon = i;
        this.setIconURL(iconURL);
        this.articleURL = articleURL;
        this.setTime(time);
        this.setId(id);
        this.unread = unread;
        this.setContent(content);
        this.setAuthor(author);
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

    public String getArticleURL() {
        return articleURL;
    }

    public void setArticleURL(String articleURL) {
        this.articleURL = articleURL;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

}
