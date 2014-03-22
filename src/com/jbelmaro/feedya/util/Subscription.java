package com.jbelmaro.feedya.util;

import java.util.List;


public class Subscription
{
  private String           id;
  private String           title;
  private String           sortid;
  private Long             updated;
  private String           website;
  private String velocity;
  private List<String> topics;
  private Category[] categories;
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}
public String getSortid() {
	return sortid;
}
public void setSortid(String sortid) {
	this.sortid = sortid;
}
public Long getUpdated() {
	return updated;
}
public void setUpdated(Long updated) {
	this.updated = updated;
}
public String getWebsite() {
	return website;
}
public void setWebsite(String website) {
	this.website = website;
}
public Category[] getCategories() {
	return categories;
}
public void setCategories(Category[] categories) {
	this.categories = categories;
}
public String getVelocity() {
	return velocity;
}
public void setVelocity(String velocity) {
	this.velocity = velocity;
}
public List<String> getTopics() {
	return topics;
}
public void setTopics(List<String> topics) {
	this.topics = topics;
}
}
