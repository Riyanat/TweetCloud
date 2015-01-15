goog.provide('tweetcloud.login.controller');


tweetcloud.login.controller.init = function(container) {
	 goog.net.XhrIo.send("templates/login.html", function(e) {
		 
		  // loads login page
		  var xhr = e.target;
	      var contentText = xhr.getResponseText();
	      var contentNode = goog.dom.htmlToDocumentFragment(contentText);
	  	  goog.dom.removeChildren(container);
	      goog.dom.appendChild(container, contentNode);
	  });
};