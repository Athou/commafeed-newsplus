package com.commafeed.newsplus.model.request;

import java.io.Serializable;

@SuppressWarnings("serial")
public class FeedModificationRequest implements Serializable {

	private Long id;
	private String name;
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

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

}
