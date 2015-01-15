package com.tweetcloud.services.datahandling;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.tweetcloud.datastorage.Attributes;
import com.tweetcloud.datastorage.Config;
import com.tweetcloud.datastorage.Tweet;
import com.tweetcloud.datastorage.TweetDatabase;
import com.tweetcloud.datastorage.TweetDatabaseImpl;
import com.tweetcloud.datastorage.User;
import com.tweetcloud.datastorage.UserDatabase;
import com.tweetcloud.datastorage.UserDatabaseImpl;
import com.tweetcloud.services.datahandling.jobs.WordAnalyserJob;

/**
 * This servlet is accessible as a cron job which periodically crawls twitter
 * for recent tweets uploaded by a mytweetcloud user's followings.
 * 
 * @author Riyanat Shittu
 * 
 */
public class CrawlerBatchJobServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger
			.getLogger(CrawlerBatchJobServlet.class.getName());
	private UserDatabase userDatabase;
	private TweetDatabase tweetDatabase;
	
	public CrawlerBatchJobServlet(){
		userDatabase = UserDatabaseImpl.getInstance();
		tweetDatabase = TweetDatabaseImpl.getInstance();
	}
	
	public CrawlerBatchJobServlet(UserDatabase userDatabase, TweetDatabase tweetDatabase){
		this.userDatabase = userDatabase;
		this.tweetDatabase = tweetDatabase;
	}
	
	/**
	 * Returns a new instance of the twitter object. If the input param twitter
	 * is not null, the same object will be returned as a re-usable twitter instance.
	 * 
	 * @param twitter A twitter object.
	 * @return
	 */
	protected Twitter getTwitterInstance(Object twitter) {
		if (twitter == null) {
			return new TwitterFactory().getInstance();
		} else {
			return (Twitter) twitter;
		}
	}

	/**
	 * Initialises the crawl process for all users in the mytweetcloud database.
	 * 
	 * @param request The Http request used to call the crawl batch job.
	 * @param response The Http response for the session.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) { 

		// Periodically crawl for new statuses from all users
		Twitter twitter = getTwitterInstance(request.getSession().getAttribute(
				Attributes.TWITTER));
		twitter.setOAuthConsumer(Config.ConsumerKey, Config.ConsumerSecret);

		logger.log(Level.INFO, "Starting batch crawl job for all users");

		for (User user : userDatabase.get()) {
			AccessToken accessToken = new AccessToken(user.getToken(),
					user.getSecretToken(),
					Long.parseLong(user.getId()));

			twitter.setOAuthAccessToken(accessToken);

			run(user.getId(), Config.appId, twitter);

		}

	}

	/**
	 * Collects all new tweets, summarises and persists summaries of all users.
	 * 
	 * @param id The current user's id.
	 * @param bucket The Google Cloud System bucket name for this job.
	 * @param twitter The twitter instance for the current user.
	 */
	public  void run(String id, String bucket, Twitter twitter) {
		// Collects tweets.
		crawl(twitter);

		// Analyses tweets.
		PipelineService service = PipelineServiceFactory.newPipelineService();
		service.startNewPipeline(new WordAnalyserJob(bucket, id));
	}

	/**
	 * Collects all new feeds from a users profile.
	 * 
	 * @param twitter
	 */
	private void crawl(Twitter twitter) {
		try {
			// Gets the recent updates
			List<Status> statuses = twitter.getHomeTimeline();
			String userId = Long.toString(twitter.showUser(
					twitter.getScreenName()).getId());

			// Add each new tweet to the database.
			for (Status status : statuses) {

				Tweet tweet = null;
				String id = Long.toString(status.getId());

				if (tweetDatabase.contains(id)) {
					tweet = tweetDatabase.get(id);

				} else {
					tweet = Tweet.toTweet(status);
				}

				tweet.addOwner(userId);
				tweetDatabase.add(tweet);
			}

		} catch (TwitterException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
