package com.jbelmaro.feedya.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Origin {
    public String streamId;
    public String title;
    public String htmlUrl;
}