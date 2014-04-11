package com.jbelmaro.feedya.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SearchFeedsList {

	public SearchFeedsResponse[] results;

}
