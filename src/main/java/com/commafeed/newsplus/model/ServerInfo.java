package com.commafeed.newsplus.model;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("serial")
public class ServerInfo implements Serializable {

	private String announcement;
	private String version;
	private String gitCommit;
	private Map<String, String> supportedLanguages;

	public String getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(String announcement) {
		this.announcement = announcement;
	}

	public Map<String, String> getSupportedLanguages() {
		return supportedLanguages;
	}

	public void setSupportedLanguages(Map<String, String> supportedLanguages) {
		this.supportedLanguages = supportedLanguages;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getGitCommit() {
		return gitCommit;
	}

	public void setGitCommit(String gitCommit) {
		this.gitCommit = gitCommit;
	}

}
