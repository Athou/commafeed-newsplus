package com.commafeed.newsplus.model.request;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class FeedMergeRequest implements Serializable {

	private Long intoFeedId;
	private List<Long> feedIds;

	public Long getIntoFeedId() {
		return intoFeedId;
	}

	public void setIntoFeedId(Long intoFeedId) {
		this.intoFeedId = intoFeedId;
	}

	public List<Long> getFeedIds() {
		return feedIds;
	}

	public void setFeedIds(List<Long> feedIds) {
		this.feedIds = feedIds;
	}

}
