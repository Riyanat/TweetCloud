package com.tweetcloud.services.datahandling;

import static org.mockito.Mockito.mock;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.tweetcloud.datastorage.Attributes;
import com.tweetcloud.datastorage.Tweet;
import com.tweetcloud.datastorage.TweetDatabase;
import com.tweetcloud.datastorage.TweetDatabaseImpl;
import com.tweetcloud.datastorage.User;
import com.tweetcloud.datastorage.UserDatabase;
import com.tweetcloud.datastorage.UserDatabaseImpl;

public class DataRetrieverServletTest {
	private UserDatabase mockUserDb;
	private TweetDatabase mockTweetDb;

	private HttpServletRequest mockRequest;
	private HttpServletResponse mockResponse;
	private PrintWriter mockResponsePW;
	private HttpSession mockSession;

	public DataRetrieverServletTest() {
		// Mock databases.
		mockUserDb = Mockito.mock(UserDatabaseImpl.class);
		mockTweetDb = Mockito.mock(TweetDatabaseImpl.class);

		// Mock Http Request and Response Objects and properties.
		mockRequest = mock(HttpServletRequest.class);
		mockResponse = mock(HttpServletResponse.class);
		mockResponsePW = mock(PrintWriter.class);
		mockSession = mock(HttpSession.class);
	}

	/**
	 * Tests that data retriever works given the correct input.
	 */
	@Test
	public void testDoGet() throws Exception {
		// Mock tweets in database.	
		List<Tweet> mockTweets = new ArrayList<Tweet>();
		Tweet tweet = new Tweet(
				"Tweet001", "sample tweet", new Date(), new ArrayList<String>(), "", "");
		mockTweets.add(tweet);

		User mockUser = new User("User0001", "", "", "", "", "summary0001", "");

		// Mocks expected behaviour.
		Mockito.when(mockRequest.getSession()).thenReturn(mockSession);
		Mockito.when(mockResponse.getWriter()).thenReturn(mockResponsePW);
		Mockito.when(mockSession.getAttribute(Attributes.USER_ID)).thenReturn(
				"User0001");
		Mockito.when(mockTweetDb.getTweetsByUserId("User0001")).thenReturn(
				mockTweets);
		Mockito.when(mockUserDb.get("User0001")).thenReturn(mockUser);

		// Expected Result.
		Gson gson = new Gson();
		Map<String, Object> expectedResults = new HashMap<String, Object>();
		expectedResults.put(Attributes.USER_SUMMARY, "summary0001");
		expectedResults.put(Attributes.USER_TWEETS, mockTweets);

		// Call the method under test.
		DataRetrieverServlet dataRetriever = new DataRetrieverServlet(
				mockTweetDb, mockUserDb);
		dataRetriever.doGet(mockRequest, mockResponse);

		// Verify that the response contains the expected REsults.
		Mockito.verify(mockResponsePW).write(gson.toJson(expectedResults));
	}

	/**
	 * Tests that the data retriever works even when the user id is invalid
	 * (i.e. unspecified or unknown) in the http request.
	 */
	
	@Test
	public void testDoGetInvalidUser() throws Exception {
		// Mocks expected behaviour.
		Mockito.when(mockRequest.getSession()).thenReturn(mockSession);
		Mockito.when(mockResponse.getWriter()).thenReturn(mockResponsePW);

		// Expected Result.
		List<Tweet> mockTweets = new ArrayList<Tweet>();
		Gson gson = new Gson();
		Map<String, Object> expectedResults = new HashMap<String, Object>();
		expectedResults.put(Attributes.USER_SUMMARY, "");
		expectedResults.put(Attributes.USER_TWEETS, mockTweets);

		// Call the method under test.
		DataRetrieverServlet dataRetriever = new DataRetrieverServlet(
				mockTweetDb, mockUserDb);
		dataRetriever.doGet(mockRequest, mockResponse);

		// Verify that the response contains the expected REsults.
		Mockito.verify(mockResponsePW).write(gson.toJson(expectedResults));
	}
}
