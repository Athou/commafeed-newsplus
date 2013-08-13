package com.commafeed.newsplus.model.request;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class MarkRequest implements Serializable {

	private String id;
	private boolean read;
	private Long olderThan;
	private List<Long> excludedSubscriptions;

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

	public List<Long> getExcludedSubscriptions() {
		return excludedSubscriptions;
	}

	public void setExcludedSubscriptions(List<Long> excludedSubscriptions) {
		this.excludedSubscriptions = excludedSubscriptions;
	}

}
