package com.tweetcloud.services.authenticate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.mockito.Mockito;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

import com.tweetcloud.datastorage.Attributes;

// @RunWith(MockitoJUnitRunner.class)
public class LogInServletTest {

	/**
	 * Tests that the doGet method behaves as expected if authentication is
	 * successful.
	 */
	@Test
	public void testDoGetSuccessful() throws TwitterException, IOException {
		String key = "key";
		String secret = "secret";

		LoginServlet loginServlet = new LoginServlet(key, secret);

		// Mock Http Request and Response Objects and properties.
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		HttpServletResponse mockResponse = mock(HttpServletResponse.class);
		HttpSession mockSession = mock(HttpSession.class);
		Twitter mockTwitter = mock(Twitter.class);

		// Dummy Request Token. Cannot be mocked by mockito.
		RequestToken mockRequestToken = new RequestToken(key, secret);

		// Return a dummy URL of the current request.
		String currentUrl = "http://localhost:8888/login";
		String expectedCallbackUrl = "http://localhost:8888/callback";
		String expectedAuthUrl = "https://api.twitter.com/oauth/authenticate?oauth_token=key";

		// Mock Request and Response behaviour.
		Mockito.when(mockRequest.getSession()).thenReturn(mockSession);
		Mockito.when(mockRequest.getSession().getAttribute(Attributes.TWITTER))
				.thenReturn(mockTwitter);
		Mockito.when(mockRequest.getRequestURL()).thenReturn(
				new StringBuffer(currentUrl));
		Mockito.when(mockTwitter.getOAuthRequestToken(expectedCallbackUrl))
				.thenReturn(mockRequestToken);

		loginServlet.doGet(mockRequest, mockResponse);

		// Test that the callback url is correct.
		assertTrue(expectedCallbackUrl.equals(loginServlet.getCallbackURL()
				.toString()));

		// Test that the twitter and requestToken objects are added to the Http
		// request.
		Mockito.verify(mockSession).setAttribute(Attributes.TWITTER,
				mockTwitter);
		Mockito.verify(mockSession).setAttribute(Attributes.REQUEST_TOKEN,
				mockRequestToken);

		// Check that a twitter auth page re-direct is called given
		// authentication is successful.
		Mockito.verify(mockResponse).sendRedirect(expectedAuthUrl);

	}

	/**
	 * Test that Twitter instance returns a valid twitter instance.
	 */
	@Test
	public void getTwitterInstanceTest(){
		LoginServlet loginServlet = new LoginServlet("", "");
		Object mockTwitter = mock(Twitter.class);
		Twitter expected = (Twitter) mockTwitter;
		
		Twitter result = loginServlet.getTwitterInstance(mockTwitter);		
		assertEquals(expected, result);
	}
	
	@Test
	public void getTwitterInstanceTestInvalid(){
		LoginServlet loginServlet = new LoginServlet("", "");
		Twitter expected = new TwitterFactory().getInstance();;
		
		Twitter result = loginServlet.getTwitterInstance("invalid object");		
		assertEquals(expected, result);
	}
	
	@Test
	public void getTwitterInstanceTestNull(){
		LoginServlet loginServlet = new LoginServlet("", "");
		Twitter expected = new TwitterFactory().getInstance();;
		
		Twitter result = loginServlet.getTwitterInstance(null);		
		assertEquals(expected, result);		
	}
}
