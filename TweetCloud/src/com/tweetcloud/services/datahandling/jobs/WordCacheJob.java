package com.tweetcloud.services.datahandling.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.tools.mapreduce.KeyValue;
import com.google.appengine.tools.mapreduce.MapReduceResult;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.google.gson.Gson;
import com.tweetcloud.datastorage.User;
import com.tweetcloud.datastorage.UserDatabase;
import com.tweetcloud.datastorage.UserDatabaseImpl;

public class WordCacheJob extends Job1<Void, MapReduceResult<List<List<KeyValue<String, Long>>>>>{

	/**
	 * This job receives the output of the wordMapReduceJob and is responsible for 
	 */
	private static final long serialVersionUID = 5473725763743982718L;
	
	/**
	 * TODO: The aggregated results from the wordMapReduce Job in the following format.
	 * [
	 * 	{text:"aaa", size: 10},
	 * 	{text:"aab", size: 4}
	 * ]
	 */
	private List<Map<String, String>> results; 
	
	/**
	 * The id of the user whose tweet's are to be cached.
	 */
	private String userId;
	
	public WordCacheJob(String userId) {
		//TODO, get old summary, add to new summary
		results = new ArrayList<Map<String, String>>();
		this.userId = userId;
	}
	
	public Value<Void>run(MapReduceResult<List<List<KeyValue<String, Long>>>> keyValues) {
		
		//TODO: Find out why result cannot be initialised here.
		List<List<KeyValue<String, Long>>> values = keyValues.getOutputResult();
		
		for (List<KeyValue<String, Long>> setOfValues: values) {
			for (KeyValue<String, Long> value : setOfValues){
				
				// Create a new database entry
				Map<String, String> result = new HashMap<String, String>();
				result.put("size", Long.toString(value.getValue() * 5));
				result.put("text", value.getKey());
				results.add(result);

			}
		}
		

		Gson gson = new Gson();
		String result = gson.toJson(results);
		UserDatabaseImpl userDb = UserDatabaseImpl.getInstance();
		User user = userDb.get(userId);
		

		Collections.sort(results, new Comparator<Map<String, String>>(){
			@Override
			public int compare(Map<String, String> word1, Map<String, String> word2) {		
				return Integer.parseInt(word2.get("size")) - Integer.parseInt(word1.get("size"));
			}
		});
		
		// Update database.
		user.setSummary(result);
		userDb.add(user);
		
		return null;
		
	}
	
	
	
}
