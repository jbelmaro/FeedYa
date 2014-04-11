package com.jbelmaro.feedya.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public interface FeedlyKey {
    public static final String CLIENT_SECRET = "YOURKEYHERE";
}
