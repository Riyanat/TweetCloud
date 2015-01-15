package com.tweetcloud.services.datahandling.jobs;

import java.util.List;
import com.google.appengine.tools.mapreduce.KeyValue;
import com.google.appengine.tools.mapreduce.MapReduceResult;
import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job0;
import com.google.appengine.tools.pipeline.Value;

/**
 * This job is responsible for collecting a user's tweet feed from the
 * tweetcloud database, summarising the tweet feed, and storing the summaries
 * back into the tweetcloud database. The job is split into two sub-routines.
 * 
 * @author Riyanat Shittu
 * 
 */
public class WordAnalyserJob extends Job0<Void> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -380369711890508235L;

	/**
	 * This subroutine job is responsible for reading the tweets from the db and
	 * summarising the tweets.
	 */
	private WordMapReduceJob job;

	/**
	 * This subroutine job is responsible for aggregating the summaries and
	 * storing them into the database.
	 */
	private WordCacheJob cache;

	/**
	 * Constructor initialises both subroutine jobs.
	 * 
	 * @param bucket
	 * @param userId
	 */
	public WordAnalyserJob(String bucket, String userId) {
		job = new WordMapReduceJob(bucket, userId);
		cache = new WordCacheJob(userId);
	}

	/**
	 * Runs both subroutines sequentially. The output of 'job' is the input of
	 * 'cache'.
	 */
	public Value<Void> run() throws Exception {

		FutureValue<MapReduceResult<List<List<KeyValue<String, Long>>>>> keyValues = futureCall(job);
		FutureValue<Void> noResults = futureCall(cache, keyValues);
		return noResults;
	}

}
