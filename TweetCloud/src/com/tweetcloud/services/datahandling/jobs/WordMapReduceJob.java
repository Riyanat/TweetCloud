package com.tweetcloud.services.datahandling.jobs;

import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.mapreduce.KeyValue;
import com.google.appengine.tools.mapreduce.MapReduceJob;
import com.google.appengine.tools.mapreduce.MapReduceResult;
import com.google.appengine.tools.mapreduce.MapReduceSettings;
import com.google.appengine.tools.mapreduce.MapReduceSpecification;
import com.google.appengine.tools.mapreduce.Marshallers;
import com.google.appengine.tools.mapreduce.inputs.DatastoreInput;
import com.google.appengine.tools.mapreduce.outputs.InMemoryOutput;
import com.google.appengine.tools.pipeline.Job0;
import com.google.appengine.tools.pipeline.Value;
import com.tweetcloud.services.datahandling.CrawlerBatchJobServlet;

/**
 * Each shard reads {@link #shardCount} tweet entries from the datastore
 * Mapper and Reducer.
 * @author riyanat
 *
 */
public class WordMapReduceJob extends
		Job0<MapReduceResult<List<List<KeyValue<String, Long>>>>> {

	private final static Logger LOGGER = Logger.getLogger(WordMapReduceJob.class.getName()); 
	
	private static final long serialVersionUID = 4050840482428163434L;
	
	/**
	 * TODO: What is this bucket?
	 * 
	 */
	private String bucket;
	
	/**
	 * TODO: What is this id?
	 */
	private String id;

	
	/**
	 * The number of shards to use for the job.
	 */
	private int shardCount;

	
	/**
	 * Constructor.
	 */
	public WordMapReduceJob(String bucket, String id) {
		this.bucket = bucket;
		this.id = id;
		this.shardCount = 10;
	}

	@Override
	public Value<MapReduceResult<List<List<KeyValue<String, Long>>>>> run()
			throws Exception {
LOGGER.info("mapper started");
		MapReduceSettings settings = getSettings(bucket);
		MapReduceSpecification<Entity, String, Long, KeyValue<String, Long>, List<List<KeyValue<String, Long>>>> specification = getSpecification();
		MapReduceJob<Entity, String, Long, KeyValue<String, Long>, List<List<KeyValue<String, Long>>>> mapReduceJob = new MapReduceJob<Entity, String, Long, KeyValue<String, Long>, List<List<KeyValue<String, Long>>>>();

		// MapReduceJob.start(specification, settings);//required??
		// begins the job process.
		return futureCall(mapReduceJob, immediate(specification),
				immediate(settings));
	}

	/**
	 * The map reduce specification. This specifies the datastore entries to
	 * read, the map and reduce jobs to run,
	 * 
	 * @return
	 */
	private MapReduceSpecification<Entity, String, Long, KeyValue<String, Long>, List<List<KeyValue<String, Long>>>> getSpecification() {

		InMemoryOutput<KeyValue<String, Long>> output = new InMemoryOutput<KeyValue<String, Long>>(
				10);
		DatastoreInput input = new DatastoreInput("Tweet", 10);

		return MapReduceSpecification.of("Tweet Mapping", input,
				new WordMap(id), Marshallers.getStringMarshaller(),
				Marshallers.getLongMarshaller(), new WordReduce(), output);

	}

	/**
	 * Returns a new MapReduce setting object. This is required for initialising the mapReduce Job.
	 */
	private MapReduceSettings getSettings(String bucket) {
		return new MapReduceSettings().setBucketName(bucket);// .setModule("mapreduce");
	}
}
