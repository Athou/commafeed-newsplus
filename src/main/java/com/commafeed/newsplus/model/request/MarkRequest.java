package com.commafeed.newsplus.model.request;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MarkRequest implements Serializable {

	private String id;
	private Long feedId;
	private boolean read;
	private Long olderThan;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public Long getOlderThan() {
		return olderThan;
	}

	public void setOlderThan(Long olderThan) {
		this.olderThan = olderThan;
	}

	public Long getFeedId() {
		return feedId;
	}

	public void setFeedId(Long feedId) {
		this.feedId = feedId;
	}

}
