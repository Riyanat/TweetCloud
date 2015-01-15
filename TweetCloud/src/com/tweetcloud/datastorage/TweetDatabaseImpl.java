package com.tweetcloud.datastorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class TweetDatabaseImpl implements TweetDatabase {

	private static DatastoreService datastore;
	private static TweetDatabaseImpl instance;

	private TweetDatabaseImpl() {
		datastore = DatastoreServiceFactory.getDatastoreService();
	}

	public static TweetDatabaseImpl getInstance() {
		if (instance == null) {
			instance = new TweetDatabaseImpl();
		}
		return instance;
	}

	@Override
	public List<Tweet> get() {
		List<Tweet> tweets = new ArrayList<Tweet>();
		PreparedQuery pq = datastore.prepare(new Query("Tweet"));
		for (Entity dsEntity : pq.asIterable()) {		
			tweets.add(Tweet.toTweet(dsEntity));
		}
		return tweets;
	}

	public void refresh(){
		PreparedQuery pq = datastore.prepare(new Query("Tweet"));
		for (Entity dsEntity : pq.asIterable()) {		
			datastore.delete(dsEntity.getKey());
		}
			
	}
	public Tweet get(String id) {
		Key key = KeyFactory.createKey("Tweet", id);
		Filter filterById = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
				Query.FilterOperator.EQUAL, key);
		Query q = new Query("Tweet").setFilter(filterById);
		PreparedQuery pq = datastore.prepare(q);
		Entity entity = pq.asSingleEntity();
		return Tweet.toTweet(entity);
	}

	/***
	 * Returns a set of tweets tweeted by a users followings given the user's
	 * Id.
	 * 
	 * @param userId
	 *            The unique id of user.
	 * @return A list of tweets tweeted by a users followings given the user's
	 *         Id. Returns an emtpy list if no user is found.
	 */
	public List<Tweet> getTweetsByUserId(String userId) {
		List<Tweet> tweets = new ArrayList<Tweet>();

		if (userId != null && !userId.equals("")) {

			Query query = new Query("Tweet");
			PreparedQuery preparedQuery = datastore.prepare(query);

			System.out.println(preparedQuery.countEntities(FetchOptions.Builder.withDefaults()));
			for (Entity entity : preparedQuery.asIterable()) {
				Tweet tweet = Tweet.toTweet(entity);
				if (tweet.getOwners().contains(userId)) {
					tweets.add(tweet);
				}
			}
		}
		return tweets;
	}

	@Override
	public void add(Tweet tweet) {
		Entity entity = toEntity(tweet);

		// First check if it already contains entity.
		Key key = entity.getKey();
		datastore.delete(key);
		datastore.put(entity);

	}

	@Override
	public boolean contains(Tweet tweet) {
		Entity entity = toEntity(tweet);
		return contains(entity.getKey());
	}

	public boolean contains(String key) {
		Key entityKey = KeyFactory.createKey("Tweet", key);
		return contains(entityKey);
	}

	private boolean contains(Key key) {

		try {
			datastore.get(key);
			return true;
		} catch (EntityNotFoundException e) {
			return false;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
	
	
	public Entity toEntity(Tweet tweet){
		Key key = KeyFactory.createKey("Tweet", tweet.getId());				
		Entity entity = new Entity(key);
		entity.setProperty("id", tweet.getId());
		entity.setProperty("text", tweet.getText());
		entity.setProperty("owners", tweet.getOwners());
		entity.setProperty("creatorName", tweet.getCreatorName());
		entity.setProperty("creatorImgUrl", tweet.getCreatorImgUrl());
		return entity;
	}
	

}
