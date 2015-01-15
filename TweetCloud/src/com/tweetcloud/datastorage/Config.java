package com.tweetcloud.datastorage;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;

/**
 * This class is used to extract the developer's ouath credentials.
 * 
 * @author riyanat
 *
 */
public class Config {

	public final static String accessToken;
	
	public final static String ConsumerKey;
	
	public final static String ConsumerSecret;
	
	public final static String tokenSecret;

	public final static String appId;
	
	static {

		// Get credentials from datastore.
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Key key = KeyFactory.createKey("ouath_credentials", "riyanat");
		Filter filterById = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
				Query.FilterOperator.EQUAL, key);
		
		Query q = new Query("ouath_credentials").setFilter(filterById);
		PreparedQuery pq = datastore.prepare(q);
		Entity entity = pq.asSingleEntity();
		accessToken = (String) entity.getProperty("accessToken");
		ConsumerKey = (String) entity.getProperty("Consumer key");
		ConsumerSecret = (String) entity.getProperty("Consumer secret");
		tokenSecret = (String) entity.getProperty("Token secret");
		
		appId = "mytweetcloud.appspot.com";

	}
}
