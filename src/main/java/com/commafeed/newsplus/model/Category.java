package com.commafeed.newsplus.model;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class Category implements Serializable {

	private String id;
	private String parentId;
	private String name;
	private List<Category> children;
	private List<Subscription> feeds;
	private boolean expanded;
	private Integer position;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Category> getChildren() {
		return children;
	}

	public void setChildren(List<Category> children) {
		this.children = children;
	}

	public List<Subscription> getFeeds() {
		return feeds;
	}

	public void setFeeds(List<Subscription> feeds) {
		this.feeds = feeds;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

}