goog.provide('tweetcloud');
goog.provide('tweetcloud.main.controller');

goog.require('goog.dom'); 
goog.require('goog.net.XhrIo');
//goog.require('tweetcloud.main.visualization');


tweetcloud.main.controller.init = function(container, user) {
	// Refresh page
	this._loadContent(container, user);
	
	// Periodically fetch data every 10 seconds.
	setInterval(this._fetchData, 5000);
	this._fetchData();
};

/**
 * Loads the content of the main page.
 * 
 * @private
 * @param 
 */
tweetcloud.main.controller._loadContent = function(container, user) {
	 goog.net.XhrIo.send("templates/home.html", function(e) {
		 
		 // loads html content
		  var xhr = e.target;
	      var contentText = xhr.getResponseText();
	      var contentNode = goog.dom.htmlToDocumentFragment(contentText);
	  	  goog.dom.removeChildren(container);
	      goog.dom.appendChild(container, contentNode);
	  	  
	      // initialises visualization
	      tweetcloud.main.visualization.getInstance().init(container, user);
	  });

};


tweetcloud.main.controller._fetchData = function() {
	 // do i need to send the user information?
	 goog.net.XhrIo.send("get_data", function(e) {
		 var xhr = e.target;
	      var contentText = xhr.getResponseText();
	      tweetcloud.main.visualization.getInstance().update(contentText);
	 });
};

