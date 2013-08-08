package com.commafeed.newsplus.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Settings implements Serializable {

	private String language;
	private String readingMode;
	private String readingOrder;
	private String viewMode;
	private boolean showRead;
	private boolean socialButtons;
	private boolean scrollMarks;
	private String theme;
	private String customCss;

	public String getReadingMode() {
		return readingMode;
	}

	public void setReadingMode(String readingMode) {
		this.readingMode = readingMode;
	}

	public String getCustomCss() {
		return customCss;
	}

	public void setCustomCss(String customCss) {
		this.customCss = customCss;
	}

	public String getReadingOrder() {
		return readingOrder;
	}

	public void setReadingOrder(String readingOrder) {
		this.readingOrder = readingOrder;
	}

	public boolean isShowRead() {
		return showRead;
	}

	public void setShowRead(boolean showRead) {
		this.showRead = showRead;
	}

	public boolean isSocialButtons() {
		return socialButtons;
	}

	public void setSocialButtons(boolean socialButtons) {
		this.socialButtons = socialButtons;
	}

	public String getViewMode() {
		return viewMode;
	}

	public void setViewMode(String viewMode) {
		this.viewMode = viewMode;
	}

	public boolean isScrollMarks() {
		return scrollMarks;
	}

	public void setScrollMarks(boolean scrollMarks) {
		this.scrollMarks = scrollMarks;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

}
