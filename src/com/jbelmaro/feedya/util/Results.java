package com.jbelmaro.feedya.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Results {
    public String[] deliciousTags;
    public String title;
    public String website;
    public String feedId;
    public String velocity;
    public String language;
    public String description;
    public String lastUpdated;
    public String score;
    public Integer subscribers;
    public boolean curated;
    public boolean featured;
    public boolean partial;
    public String hint;
    public int estimatedEngagement;
    public int facebookLikes;
    public int twitterFollowers;
    public String visualUrl;
    public String twitterScreenName;
    public String facebookUsername;
    public String tileIcon;
}
