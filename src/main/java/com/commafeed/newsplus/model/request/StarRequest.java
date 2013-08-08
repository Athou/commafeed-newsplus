package com.commafeed.newsplus.model.request;

import java.io.Serializable;

@SuppressWarnings("serial")
public class StarRequest implements Serializable {

	private String id;
	private Long feedId;
	private boolean starred;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isStarred() {
		return starred;
	}

	public void setStarred(boolean starred) {
		this.starred = starred;
	}

	public Long getFeedId() {
		return feedId;
	}

	public void setFeedId(Long feedId) {
		this.feedId = feedId;
	}

}
