package com.jbelmaro.feedya.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleBean {

    private MediaGroups[] mediaGroups;
    private String title;
    private String link;
    private String author;
    private String publishedDate;
    private String contentSnippet;
    private String content;
    private String[] categories;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getContentSnippet() {
        return contentSnippet;
    }

    public void setContentSnippet(String contentSnippet) {
        this.contentSnippet = contentSnippet;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MediaGroups[] getMediaGroups() {
        return mediaGroups;
    }

    public void setMediaGroups(MediaGroups[] mediaGroups) {
        this.mediaGroups = mediaGroups;
    }

    static class MediaGroups {
        private Content[] contents;

        public Content[] getContents() {
            return contents;
        }

        public void setContents(Content[] contents) {
            this.contents = contents;
        }

    }

    static class Content {
        private String url;
        private String type;
        private String height;
        private String width;
        private String title;
        private String description;
        private Thumbnail[] thumbnails;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Thumbnail[] getThumbnails() {
            return thumbnails;
        }

        public void setThumbnails(Thumbnail[] thumbnails) {
            this.thumbnails = thumbnails;
        }

    }

    static class Thumbnail {
        private String height;
        private String width;
        private String url;

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
