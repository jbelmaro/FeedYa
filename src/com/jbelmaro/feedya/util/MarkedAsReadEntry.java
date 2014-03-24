package com.jbelmaro.feedya.util;

import java.util.ArrayList;
import java.util.List;

public class MarkedAsReadEntry {
    private String type;
    private String action;
    private List<String> entryIds;

    public MarkedAsReadEntry(String type, String action, String entryId) {
        this.action = action;
        this.type = type;
        entryIds = new ArrayList<String>();
        entryIds.add(entryId);
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

    public List<String> getEntryIds() {
        return entryIds;
    }

    public void setEntryIds(List<String> entryIds) {
        this.entryIds = entryIds;
    }
}
