package com.jbelmaro.feedya.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown=true)
public class SearchFeedsResponse
{
  public String        hint;
  public String[]  related;
  public Results[] results;
  public String queryType;
  public String scheme;
  
}
