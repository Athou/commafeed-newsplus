package com.commafeed.newsplus.model.request;


public class ProfileModificationRequest {

	private String email;
	private String password;
	private boolean newApiKey;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isNewApiKey() {
		return newApiKey;
	}

	public void setNewApiKey(boolean newApiKey) {
		this.newApiKey = newApiKey;
	}

}
