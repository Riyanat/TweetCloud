package com.tweetcloud.services.datahandling;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.tweetcloud.datastorage.Attributes;
import com.tweetcloud.datastorage.Tweet;
import com.tweetcloud.datastorage.TweetDatabase;
import com.tweetcloud.datastorage.TweetDatabaseImpl;
import com.tweetcloud.datastorage.User;
import com.tweetcloud.datastorage.UserDatabase;
import com.tweetcloud.datastorage.UserDatabaseImpl;

public class DataRetrieverServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TweetDatabase tweetDatabase;
	private UserDatabase userDatabase;

	public DataRetrieverServlet() {
		tweetDatabase = TweetDatabaseImpl.getInstance();
		userDatabase = UserDatabaseImpl.getInstance();
	}

	public DataRetrieverServlet(TweetDatabase tweetDatabase,
			UserDatabase userDatabase) {
		this.tweetDatabase = tweetDatabase;
		this.userDatabase = userDatabase;
	}

	/**
	 * Gets the tweet feeds and summaries of a single user. The request should
	 * contain the user object.
	 * 
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {

		List<Tweet> tweets = new ArrayList<Tweet>();
		String summary = "";

		// The user id needs to be included in the session's request.
		if (request.getSession().getAttribute(Attributes.USER_ID) != null) {

			String userId = (String) request.getSession().getAttribute(
					Attributes.USER_ID);

			// Retrieves tweets from datastore.
			tweets = tweetDatabase.getTweetsByUserId(userId);

			// Retrieves tweet summaries information from database.
			User user = userDatabase.get(userId);
			summary = (user == null) ? summary : user.getSummary();
		}
		// Write data to Http Response.
		Map<String, Object> results = new HashMap<String, Object>();
		results.put(Attributes.USER_SUMMARY, summary);
		results.put(Attributes.USER_TWEETS, tweets);

		Gson g = new Gson();
		String tweetsToJSON = g.toJson(results);
		PrintWriter out = null;
		try {

			response.setContentType("text/html");
			out = response.getWriter();
			out.write(tweetsToJSON);
		} catch (IOException e) {
			// TODO log info
		} finally {
			out.close();
		}
	}

}
