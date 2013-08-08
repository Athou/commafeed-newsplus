package com.commafeed.newsplus.model.request;

import java.io.Serializable;

@SuppressWarnings("serial")
public class FeedInfoRequest implements Serializable {

	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
