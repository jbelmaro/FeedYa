package com.jbelmaro.feedya.util;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MarkAsReadFeed {
    private String type;
    private String action;
    private List<String> feedIds;

    public MarkAsReadFeed(String type, String action, String feedId) {
        this.action = action;
        this.type = type;
        feedIds = new ArrayList<String>();
        feedIds.add(feedId);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<String> getFeedIds() {
        return feedIds;
    }

    public void setFeedIds(List<String> feedIds) {
        this.feedIds = feedIds;
    }
}