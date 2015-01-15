package com.tweetcloud.datastorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import twitter4j.Status;

import com.google.appengine.api.datastore.Entity;

/**
 * The tweet object used by tweetcloud.
 * @author riyanat
 *
 */
public class Tweet  {
	
	/***
	 *  The unique id of the tweet assigned by twitter.
	 */
	private String id;
	
	/**
	 * The content of the tweet.
	 */
	private String text;
	
	/**
	 * The date the tweet was created.
	 */
	private Date createdAt;
	
	/**
	 * The list of all tweetcloud users (id) who have this tweet on their timeline.
	 */
	private List<String> owners;
	
	/**
	 * The user who created this tweet.
	 */
	private String creatorName;
	
	/**
	 * The image url of the user who created this tweet.
	 */
	private String creatorImgUrl;
	
	public Tweet(String id, String text, Date dateCreated, List<String> owners, String creatorName, String creatorImgUrl){
		this.id = id;
		this.createdAt = dateCreated;
		this.text = text;
		this.owners = owners;
		this.creatorName = creatorName;
		this.creatorImgUrl = creatorImgUrl;		
	}
	
	public Date getDateCreated() {
		return createdAt;
	}

	public void setDateCreated(Date dateCreated) {
		this.createdAt = dateCreated;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public List<String> getOwners() {
		return owners;
	}
	
	public void addOwner(String owner){
		if (owners.contains(owner))return;
		owners.add(owner);
	}
	
	public void setOwners(List<String> owners) {
		this.owners = owners;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getCreatorImgUrl() {
		return creatorImgUrl;
	}

	public void setCreatorImgUrl(String creatorImgUrl) {
		this.creatorImgUrl = creatorImgUrl;
	}
	
	
	public static Tweet toTweet(Entity entity){
		
		String id = (String)entity.getProperty("id");
		String text = (String)entity.getProperty("text");
		List<String> owners = (List<String>)entity.getProperty("owners");
		Date date = (Date)entity.getProperty("dateCreated");
		String creatorName = (String)entity.getProperty("creatorName");
		String creatorImgUrl = (String)entity.getProperty("creatorImgUrl");
		Tweet tweet = new Tweet(id,text,date,owners, creatorName, creatorImgUrl);
		
		return tweet;
	}
	
	public static Tweet toTweet(Status status) {
		String id = Long.toString(status.getId());
		String text = status.getText(); 
		List<String> owners = new ArrayList<String>();
		Date date = status.getCreatedAt();
		String name = status.getUser().getName();
		String img = status.getUser().getProfileImageURL();
				
		return new Tweet(id, text, date, owners, name, img);

	}
	
}
