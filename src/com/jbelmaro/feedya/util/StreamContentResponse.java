package com.jbelmaro.feedya.util;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown=true)
public class StreamContentResponse
{
  

  public String     direction;
  public String     id;
  public String     continuation;
  public Long       updated;
  public String title;
  public List<Item> items;
  public List<Alternate>  alternate;
}
