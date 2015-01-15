package com.tweetcloud.datastorage;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class UserDatabaseImpl implements UserDatabase{
	
	private static DatastoreService datastore;
	private static UserDatabaseImpl instance;
	private static String kind;
	
	private UserDatabaseImpl(){
		datastore = DatastoreServiceFactory.getDatastoreService();	
		kind = "User";
	}

	public static UserDatabaseImpl getInstance() {
		if (instance == null) {
			instance = new UserDatabaseImpl();
		}
		return instance;
	}
	
	public List<User> get(){
		List<User> users = new ArrayList<User>();
		
		PreparedQuery pq = datastore.prepare(new Query(kind));
		for (Entity dsEntity : pq.asIterable()){
			users.add(toUser(dsEntity));
		}
		return users;
	}
	
	
	public User get(String id){
		Key key = KeyFactory.createKey(kind, id);
		Filter filterById	= new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.EQUAL, key);
		Query q = new Query(kind).setFilter(filterById);
		PreparedQuery pq = datastore.prepare(q);
		Entity userEntity = pq.asSingleEntity();
		return toUser(userEntity);
	}

	@Override
	public void add(User user) {		
		Entity entity = toEntity(user);
		
		// first check is object exists in datastore
		Key key = entity.getKey();
		datastore.delete(key);
		datastore.put(entity);
	}

	@Override
	public boolean contains(User user) {
		Entity entity = toEntity(user);
		try {
			datastore.get(entity.getKey());
			return true;
		}catch (EntityNotFoundException e){
			return false;
		}catch (IllegalArgumentException e){
			return false;
		}
	}
	
	public boolean contains(String key) {
		Key entityKey = KeyFactory.createKey(kind, key);
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
	
	
	public User toUser(Entity entity){
		String id = (String)entity.getProperty("id");
		String name = (String)entity.getProperty("name");
		String fullname = (String)entity.getProperty("fullname");
		String token = (String)entity.getProperty("token");
		String secret = (String)entity.getProperty("secretToken");
		String summary = ((Text)entity.getProperty("summary")).getValue();
		String image = ((String)entity.getProperty("imageURL"));
		User user = new User(id, name, fullname, token, secret, summary, image);
		return user;
	}
	
	public Entity toEntity(User user){	
		Key key = KeyFactory.createKey(kind, user.getId());
		Entity entity = new Entity(key);
		Text textSummary = new Text(user.getSummary());
		entity.setProperty("name", user.getName());
		entity.setProperty("fullname", user.getFullname());
		entity.setProperty("id", user.getId());
		entity.setProperty("token", user.getToken());
		entity.setProperty("secretToken", user.getSecretToken());
		entity.setProperty("summary", textSummary);
		entity.setProperty("imageURL", user.getImageURL());
		return entity;
	}
	
	
}
