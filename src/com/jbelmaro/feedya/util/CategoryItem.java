package com.jbelmaro.feedya.util;

public class CategoryItem {

    private String title;
    private String categoryId;
    private int itemCount;

    public CategoryItem(String title, String id, int count) {
        this.title = title;
        categoryId = id;
        itemCount = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
