package com.tweetcloud.services.authenticate;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import com.google.gson.Gson;
import com.tweetcloud.datastorage.Attributes;
import com.tweetcloud.datastorage.Config;
import com.tweetcloud.datastorage.User;
import com.tweetcloud.datastorage.UserDatabase;
import com.tweetcloud.datastorage.UserDatabaseImpl;
import com.tweetcloud.services.datahandling.CrawlerBatchJobServlet;

/**
 * The login callback server adds the user to the database and navigates to the
 * homepage.
 * 
 * @author Riyanat Shittu
 * 
 */
public class LoginCallbackServlet extends HttpServlet {

	private static final long serialVersionUID = 8027966440437017431L;
	private static final Logger logger = Logger
			.getLogger(LoginCallbackServlet.class.getName());
	protected UserDatabase userDatabase;
	protected User user;

	public LoginCallbackServlet() {
		this.userDatabase = UserDatabaseImpl.getInstance();
	}

	public LoginCallbackServlet(UserDatabase userDatabase) {
		this.userDatabase = userDatabase;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {

		try {

			// Gets user's access token for extracting information from user's
			// profile.
			Twitter twitter = (Twitter) request.getSession().getAttribute(
					Attributes.TWITTER);
			RequestToken requestToken = (RequestToken) request.getSession()
					.getAttribute(Attributes.REQUEST_TOKEN);
			String verifier = request.getParameter(Attributes.OAUTH_VERIFIER);

			AccessToken accessToken = twitter.getOAuthAccessToken(requestToken,
					verifier);
			twitter.setOAuthAccessToken(accessToken);

			// Extracts information from user's profile.
			String userId = Long.toString(twitter.showUser(
					twitter.getScreenName()).getId());
			String userName = twitter.getScreenName();
			String fullname = twitter.showUser(twitter.getScreenName())
					.getName();
			String imageURL = twitter.showUser(twitter.getScreenName())
					.getProfileImageURL();

			// Adds User to database.
			user = new User(userId, userName, fullname, accessToken.getToken(),
					accessToken.getTokenSecret(), "", imageURL);

			userDatabase.add(user);

			// Adds User information to session.
			Gson gson = new Gson();
			request.getSession().setAttribute(Attributes.USER,
					gson.toJson(user));
			request.getSession().setAttribute(Attributes.USER_ID, userId);
			response.sendRedirect(request.getContextPath() + "/");

			// Run the crawler.
			CrawlerBatchJobServlet crawler = new CrawlerBatchJobServlet();
			crawler.run(userId, Config.appId, twitter);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

}
