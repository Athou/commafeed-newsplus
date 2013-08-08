package com.commafeed.newsplus.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UnreadCount implements Serializable {

	private long feedId;
	private long unreadCount;

	public long getFeedId() {
		return feedId;
	}

	public void setFeedId(long feedId) {
		this.feedId = feedId;
	}

	public long getUnreadCount() {
		return unreadCount;
	}

	public void setUnreadCount(long unreadCount) {
		this.unreadCount = unreadCount;
	}

}
