package com.tweetcloud.services.datahandling.jobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.mapreduce.Mapper;

/*
 * This class maps the tweets to words
 */
public class WordMap extends Mapper<Entity, String, Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -89259943132225582L;
	private String id;
	public WordMap(String id){
		this.id = id;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void map(Entity entity) {
		
		// Check if this entity belongs to this user firstto user.
		List<String> entityIds = (List<String>)entity.getProperty("owners");
		if (!entityIds.contains(id)){
			return;
		}
		
		String[] words = ((String)entity.getProperty("text")).split("\\s+");
		
		
		
		HashMap<String, Integer> wordCount = new HashMap<String, Integer>();
		
		for (String word : words){
			// TODO : Check if this word should be filtered... 
			
			 Integer count = wordCount.get(word);
		      if (count == null) {
		        wordCount.put(word, 1);
		      } else {
		        wordCount.put(word, count + 1);
		      }
		}
		
		for (Entry<String, Integer> kv : wordCount.entrySet()) {
		      emit(String.valueOf(kv.getKey()), Long.valueOf(kv.getValue()));
		 }	
	}
}
