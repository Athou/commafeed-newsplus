package com.commafeed.newsplus.model.request;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class TagRequest implements Serializable {

	private Long entryId;
	private List<String> tags;

	public Long getEntryId() {
		return entryId;
	}

	public void setEntryId(Long entryId) {
		this.entryId = entryId;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

}
