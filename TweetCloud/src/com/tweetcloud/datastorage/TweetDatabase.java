package com.tweetcloud.datastorage;

import java.util.List;

public interface TweetDatabase {
	/**
	 *  Gets the list of all tweets in the database.
	 * @return
	 */
	public List<Tweet> get();
	
	
	/**
	 *  Gets the list of all tweets in the database related to a single user.
	 * @return
	 */
	public List<Tweet> getTweetsByUserId(String id);
	
	
	/**
	 *  Adds a single tweet to the database.
	 *  
	 * @param tweet The tweet to be added to the database.
	 */
	public void add(Tweet tweet);
	
	/**
	 * Gets a single tweet from the database given its unique key.
	 * @param id
	 * @return
	 */
	public Tweet get(String key);
	
	/**
	 * Checks if a tweet is in the database given the tweet object.
	 * @param tweet
	 * @return
	 */
	public boolean contains(Tweet tweet);
	
	/**
	 * Checks if a tweet is in the database given its key.
	 * @param key
	 * @return
	 */
	public boolean contains(String key);	
}
