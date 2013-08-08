package com.commafeed.newsplus.model.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class MultipleMarkRequest implements Serializable {

	private List<MarkRequest> requests = new ArrayList<MarkRequest>();

	public List<MarkRequest> getRequests() {
		return requests;
	}

	public void setRequests(List<MarkRequest> requests) {
		this.requests = requests;
	}

}
