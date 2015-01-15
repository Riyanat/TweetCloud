
goog.provide('tweetcloud.main.visualization');

goog.require('goog.json');
goog.require('goog.string.format');

tweetcloud.main.visualization = function() {
	/**
	 * The Object containing the current users information.
	 * 
	 * @private
	 */
	this._user = null;

	/**
	 * The DOM Element containing the tweet summaries i.e. tagcloud.
	 * 
	 * @private
	 */
	this._summary = null;

	/**
	 * The DOM Element containing the list of raw tweets.
	 * 
	 * @private
	 */
	this._list = null;

	/**
	 * The width of the tag cloud.
	 * 
	 * @private
	 */
	this._summaryWidth = 500;

	/**
	 * The height of the tag cloud.
	 * 
	 * @private
	 */
	this._summaryHeight = 400;

	/**
	 * The scale used to determine the size of words in the tag cloud.
	 * @private
	 */
	this._fontScale = d3.scale.linear().range([10, 40]);
	
	this.summariesData = [];
	
	this.tweetData = [];
	
};
goog.addSingletonGetter(tweetcloud.main.visualization);

/**
 * Initialises private variables.
 * 
 * @param{Element} container The DOM Element that contains the homepage.
 * @param{Object.<string,string>} The user object. 
 */
tweetcloud.main.visualization.prototype.init = function(container, user) {

	this._user = user;
	this._summary = goog.dom.getElement('summary');
	this._list = goog.dom.getElement('list');
	
	//update the profile page.
	var pictureElement = goog.dom.getElement("picture", container);
	pictureElement.setAttribute('src', user.imageURL);
	
	var usernameElement = goog.dom.getElement("user-id", container);
	goog.dom.setTextContent(usernameElement, 'Hi ' + user.name);
	
	this._summaryWidth = this._summary.offsetWidth - 5;
	this._summaryHeight = this._summary.offsetHeight - 5;

};

/**
 * Updates the UI. Serves as a callback function when new data is received by the controller.
 * 
 * @param{string} data The data(tweets and tweet summaries) received from the controller.
 */
tweetcloud.main.visualization.prototype.update = function(data) {

	var dataJSON = goog.json.parse(data);
	
	this.updateList(dataJSON['user_tweets']);
	this.updateSummary(dataJSON['user_summary']);

};

/**
 * Currently this is more like re-render list rather than update.
 * 
 */
tweetcloud.main.visualization.prototype.updateList = function(tweets) {
	if (!goog.isDefAndNotNull(tweets) || tweets == '' || tweets == this.tweetData) {
		return;
	}
	// Reset the list element.
	goog.dom.removeChildren(this._list);
	var entries = goog.dom.createDom('div', {'class':'entries'}, null);
	goog.dom.appendChild(list, entries);

	// Parse data from string to json list.
	for (var i = 0; i < tweets.length; i++) {
		var tweet = tweets[i];
		var entry = goog.dom.createDom('div', {'class':'entry'}, null);
		var entryImg = goog.dom.createDom('img', {'class':'float-left',
			'src':tweet.creatorImgUrl}, null);
		var entryOwner = goog.dom.createDom('span', {'class':'float-left name'},null);
		var entryText = goog.dom.createDom('span', {'class':'float-left'}, null);
		var br = goog.dom.createDom('br', {}, null);
		
		goog.dom.setTextContent(entryText, tweet.text);
		goog.dom.setTextContent(entryOwner, tweet.creatorName);
		
		goog.dom.appendChild(entry, entryImg);	
		goog.dom.appendChild(entry, entryOwner);
		goog.dom.appendChild(entry, br);
		goog.dom.appendChild(entry, entryText);	
		goog.dom.appendChild(entries, entry);
	}
	
	this.tweetData = tweets;
};

/**
 * 
 */
tweetcloud.main.visualization.prototype.updateSummary = function(summaries) {
	if (!goog.isDefAndNotNull(summaries) || summaries == '' || summaries == this.summariesData) {
		return;
	}
	goog.dom.removeChildren(this._summary);
	
	var words = goog.json.parse(summaries);
	
	//words = words.splice(0,200);

	// update this font scale.
	var max = d3.max(words, function(d) {return d.size});
	
	var scale = this._fontScale;
	scale.domain([1,max]);

	var tagcloud = d3.layout.cloud()
		.size([this._summaryWidth, this._summaryHeight])
		.words(words)
		.font("Impact")
		.fontSize(function (d) {
			return scale(Number(d.size));
		})
		.on("end", goog.bind(this._drawSummary, this))
		.start();
	
	this.summariesData = summaries;
};

/**
 * 
 */
tweetcloud.main.visualization.prototype._drawSummary = function(words) {
	
	var fill = d3.scale.category20b();
	var svg = d3.select("#" + this._summary.id).append("svg")
		.attr("width", this._summaryWidth)
		.attr("height", this._summaryHeight)
		.append("g")
		.attr("transform", 
				"translate(" + ((this._summary.offsetWidth/2)+5) + "," + 
				((this._summary.offsetHeight/2) + 5) + ")")
		.selectAll("text")
		.data(words)
		.enter().append("text")
		.style("font-size", function (d) {
			return d.size + "px";
		})
		.style("font-family", "Impact")
		.style('cursor', 'pointer')
		.style("fill", function (d, i) {
			return fill(i);
		})
		.attr("text-anchor", "middle")
		.attr("transform", function (d) {
			return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
		})
		.text(function (d) {
			return d.text;
		})
		.append('title')
		.text(function(d){
			var title =  goog.string.format('People you follow said "%s" %d times', d.text, d.size);
			return title;
		});
}

//TODO: Map to viewport.