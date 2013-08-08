package com.commafeed.newsplus.model.request;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AddCategoryRequest implements Serializable {

	private String name;
	private String parentId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

}
