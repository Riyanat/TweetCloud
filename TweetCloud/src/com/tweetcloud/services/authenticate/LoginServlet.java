/**
 * The Login server navigates the user to the twitter authentication site.
 * 
 * @author Riyanat Shittu
 */

package com.tweetcloud.services.authenticate;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.tweetcloud.datastorage.Attributes;
import com.tweetcloud.datastorage.Config;
import com.tweetcloud.services.datahandling.CrawlerBatchJobServlet;

public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = -6205814293093350242L;
	private static final Logger logger = Logger.getLogger(LoginServlet.class
			.getName());
	private static final String urlPattern = "/callback";
	private StringBuffer callbackURL;
	private String consumerKey;
	private String consumerSecret;

	public LoginServlet() {
		this.consumerKey = Config.ConsumerKey;
		this.consumerSecret = Config.ConsumerSecret;
	}

	public LoginServlet(String consumerKey, String consumerSecret) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
	}

	protected static String getUrlpattern() {
		return urlPattern;
	}

	protected StringBuffer getCallbackURL() {
		return callbackURL;
	}

	protected Twitter getTwitterInstance(Object twitter) {
		if (twitter instanceof Twitter) {
			return (Twitter) twitter;
		} else {
			return new TwitterFactory().getInstance();
		}
	}

	/**
	 * Attempts to authenticate a user.
	 * 
	 * @param request
	 *            The current session's Http request object.
	 * @param resposne
	 *            The current session's Http response object.
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {

		try {
			Twitter twitter = getTwitterInstance(request.getSession()
					.getAttribute(Attributes.TWITTER));
			twitter.setOAuthConsumer(consumerKey, consumerSecret);

			// Constructs callback URL.
			callbackURL = request.getRequestURL();
			callbackURL.replace(callbackURL.lastIndexOf("/"),
					callbackURL.length(), "").append(urlPattern);

			// Sets requestToken.
			RequestToken requestToken = twitter
					.getOAuthRequestToken(callbackURL.toString());
			request.getSession().setAttribute(Attributes.REQUEST_TOKEN,
					requestToken);

			// Sets/Updates current sessions request attributes.
			request.getSession().setAttribute(Attributes.TWITTER, twitter);

			// Directs user to twitter authentication url.
			response.sendRedirect(requestToken.getAuthenticationURL());

		} catch (TwitterException e) {
			logger.log(Level.SEVERE, "An error occurred in authenticating"
					+ Config.appId + "'s OAuth credentials.");
		} catch (IOException e) {
			logger.log(Level.SEVERE, "The authentication url" + callbackURL
					+ "could not be reached");
		} catch (IllegalStateException e) {
			logger.log(
					Level.SEVERE,
					"The supplied twitter object already has consumer key" +
					"and secret set and cannot be reused.");
			// TODO Redirect to Error page.
		}
	}
}
