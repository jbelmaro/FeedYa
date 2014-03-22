package com.jbelmaro.feedya.util;

import java.util.List;

public class Item
{

  public String           id;
    public String fingerprint;
    public String originId;
  public boolean          unread;
  public List<Category> categories;
  public List<Category> tags;
  public String           title;
  public Long             published;
  public Long             updated;
  public Long             crawled;
  public Long             actionTimestamp;
  public Content          summary;
  public Content          content;
  public String           author;
  public String           engagement;
  public Origin           origin;
  public List<Alternate>  alternate;
    public List<Canonical>  canonical;
    public Visual          visual;
    public String sid;
    public Long recrawled;
    public List<String> keywords;
    public Enclosure[] enclosure;
    public Thumbnail[] thumbnail;
}