package com.tweetcloud.datastorage;

public class User{
	//TODO : Change id from String to long object
	private String name;
	private String id;
	private String token;
	private String secretToken;
	private String summary;
	private String imageURL;
	private String fullname;
	private Long lastCollectedTweet;
	

	
	public Long getLastCollectedTweet() {
		return lastCollectedTweet;
	}

	public void setLastCollectedTweet(Long lastCollectedTweet) {
		this.lastCollectedTweet = lastCollectedTweet;
	}

	public User(String id, String name, String fullname, String token, String secretToken, String summary, String imageURL){
		this.id = id;
		this.name = name;
		this.fullname = fullname;
		this.token = token;
		this.secretToken=secretToken;
		this.summary = summary;
		this.imageURL = imageURL;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		this.id = Integer.toString(name.hashCode());
	}
	
	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getSecretToken() {
		return secretToken;
	}
	
	public void setSecretToken(String secretToken) {
		this.secretToken = secretToken;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

}
