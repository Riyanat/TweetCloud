package com.tweetcloud.datastorage;

import java.util.List;

public interface UserDatabase {
	
	/*
	 * This method returns a list of all user entities
	 */
	public List<User> get();
	
	
	/*
	 * This method a single user given the users unique id/key.
	 */
	public User get(String id);
	
	
	/*
	 * This method adds a user to the database
	 */
	public void add(User entity);
	
	
	/*
	 * This method checks if the arg entity is in the database;
	 */
	public boolean contains(User entity);
}
