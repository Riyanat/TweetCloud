package com.tweetcloud.services.authenticate;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.mockito.Mockito;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import com.google.gson.Gson;
import com.tweetcloud.datastorage.Attributes;
import com.tweetcloud.datastorage.UserDatabase;

public class LoginCallbackServletTest {

	@Test
	public void testdoGet() throws TwitterException, IOException {

		String key = "key";
		String secret = "secret";
		String url = "http://localhost:8888";

		// Mock Http Request and Response Objects and properties.
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		HttpServletResponse mockResponse = mock(HttpServletResponse.class);
		HttpSession mockSession = mock(HttpSession.class);

		// Mock twitter dependencies.
		Twitter mockTwitter = mock(Twitter.class);
		User mockTwitterUser = mock(User.class);
		RequestToken mockRequestToken = new RequestToken(key, secret);
		AccessToken mockAccessToken = new AccessToken(key, secret);

		UserDatabase mockDb = mock(UserDatabase.class);
		Gson mockGson = new Gson();
		
		// Mock expected behaviour of dependencies.
		Mockito.when(mockRequest.getSession()).thenReturn(mockSession);
		Mockito.when(mockRequest.getContextPath()).thenReturn(url);
		Mockito.when(mockSession.getAttribute(Attributes.TWITTER)).thenReturn(
				mockTwitter);
		Mockito.when(mockSession.getAttribute(Attributes.REQUEST_TOKEN))
				.thenReturn(mockRequestToken);
		Mockito.when(mockRequest.getParameter(Attributes.OAUTH_VERIFIER))
				.thenReturn("");
		Mockito.when(mockTwitter.showUser(anyString())).thenReturn(
				mockTwitterUser);
		Mockito.when(mockTwitter.getOAuthAccessToken(mockRequestToken, ""))
				.thenReturn(mockAccessToken);

		LoginCallbackServlet loginCallbackServlet = new LoginCallbackServlet(
				mockDb);
		loginCallbackServlet.doGet(mockRequest, mockResponse);

		// Test that the user is added to the database.
		Mockito.verify(mockDb).add(loginCallbackServlet.user);

		// Test that the user is also added to the session object.
		Mockito.verify(mockSession).setAttribute(Attributes.USER,
				mockGson.toJson(loginCallbackServlet.user));
		Mockito.verify(mockSession).setAttribute(Attributes.USER_ID,
				loginCallbackServlet.user.getId());

		// Test that re-direct url is correct and is invoked.
		String expectedUrl = "http://localhost:8888/";
		Mockito.verify(mockResponse).sendRedirect(expectedUrl);
	}

	public void doGetFailed() {
		// if access is not granted test that the user is not added to database.
		// Test that the user is not added to the session.

		// Test that the callback url is correct.
	}

}
