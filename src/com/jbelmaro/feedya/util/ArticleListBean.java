package com.jbelmaro.feedya.util;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleListBean {

    private ResponseDataBean responseData;
    private String responseDetails;
    private String responseStatus;

    public static class ResponseDataBean {

        private Feed feed;

        public Feed getFeed() {
            return feed;
        }

        public void setFeed(Feed feed) {
            this.feed = feed;
        }

    }

    public static class Feed {

        private String feedUrl;
        private String title;
        private String link;
        private String author;
        private String description;
        private String type;
        private List<ArticleBean> entries;

        public String getFeedUrl() {
            return feedUrl;
        }

        public void setFeedUrl(String feedUrl) {
            this.feedUrl = feedUrl;
        }

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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<ArticleBean> getEntries() {
            return entries;
        }

        public void setEntries(List<ArticleBean> entries) {
            this.entries = entries;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

    }

    public ResponseDataBean getResponseData() {
        return responseData;
    }

    public void setResponseData(ResponseDataBean responseData) {
        this.responseData = responseData;
    }

    public String getResponseDetails() {
        return responseDetails;
    }

    public void setResponseDetails(String responseDetails) {
        this.responseDetails = responseDetails;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

}
