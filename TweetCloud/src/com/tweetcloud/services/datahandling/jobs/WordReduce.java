package com.tweetcloud.services.datahandling.jobs;

import com.google.appengine.tools.mapreduce.KeyValue;
import com.google.appengine.tools.mapreduce.Reducer;
import com.google.appengine.tools.mapreduce.ReducerInput;

public class WordReduce extends Reducer <String, Long, KeyValue<String, Long>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6785053705224430097L;

	@Override
	  public void reduce(String key, ReducerInput<Long> values) {
	    long total = 0;
	    while (values.hasNext()) {
	      total += values.next();
	    }
	    emit(KeyValue.of(key, total));
	  }
}
