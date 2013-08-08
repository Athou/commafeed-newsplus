package com.commafeed.newsplus.model;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Subscription implements Serializable {

	private Long id;
	private String name;
	private String message;
	private int errorCount;
	private Date lastRefresh;
	private Date nextRefresh;
	private String feedUrl;
	private String feedLink;
	private String iconUrl;
	private long unread;
	private String categoryId;
	private Integer position;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getUnread() {
		return unread;
	}

	public void setUnread(long unread) {
		this.unread = unread;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFeedUrl() {
		return feedUrl;
	}

	public void setFeedUrl(String feedUrl) {
		this.feedUrl = feedUrl;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}

	public String getFeedLink() {
		return feedLink;
	}

	public void setFeedLink(String feedLink) {
		this.feedLink = feedLink;
	}

	public Date getLastRefresh() {
		return lastRefresh;
	}

	public void setLastRefresh(Date lastRefresh) {
		this.lastRefresh = lastRefresh;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public Date getNextRefresh() {
		return nextRefresh;
	}

	public void setNextRefresh(Date nextRefresh) {
		this.nextRefresh = nextRefresh;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

}