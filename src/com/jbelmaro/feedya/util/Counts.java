package com.jbelmaro.feedya.util;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Counts {

    private List<Count> unreadcounts;

    public List<Count> getUnreadcounts() {
        return unreadcounts;
    }

    public void setUnreadcounts(List<Count> unreadcounts) {
        this.unreadcounts = unreadcounts;
    }
}
