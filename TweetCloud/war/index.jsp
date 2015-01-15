<!-----notes----->
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html>
<head>
<title>TweetCloud</title>
<!-- External Library Files -->
<script src="/js/lib/closure-library/closure/goog/base.js"></script>
<script src="/js/lib/d3.min.js"></script>
<script src="/js/lib/d3.layout.cloud.js"></script>
<link rel="stylesheet" type="text/css" href="/css/layout.css">


<!-- Internal Sources -->
<script src="/js/tweetcloud/login/controller.js"></script>
<script src="/js/tweetcloud/home/controller.js"></script>
<script src="/js/tweetcloud/home/view.js"></script>
</head>
<body>
	<div id = "page-container" class="page-container">
		<script>
			function main() {
				var user = <%=session.getAttribute("user")%>;
				var userIsLoggedIn = (user == null || user == "") ? false : true;
				var container = document.getElementById("page-container");

				if (userIsLoggedIn) {
					// Load home page.					
					tweetcloud.main.controller.init(container, user);
				} else {
					// Load login page. 
					tweetcloud.login.controller.init(container);
				}
			}
			main();
		</script>
	</div>
</body>
</html>