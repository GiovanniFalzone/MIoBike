<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="it">
<head>
	<c:set var="path" value="${pageContext.request.contextPath}" />

	<!-- jsp:useBean id="current_sess" type="it.unipi.iot.MIoBike.web.servlets.UserSession" / -->
	
	<title> MIo Bike - dashboard </title>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

	<link href="${path}/web-UI/css/style.css" rel="stylesheet">
	<link rel="stylesheet" href="${path}/web-UI/ol/ol.css" type="text/css">
	<link rel="shortcut icon" href="${path}/web-UI/img/icons/favicon.png" />
	<link href="${path}/web-UI/css/bootstrap.min.css" rel="stylesheet">
	<!-- The line below is only needed for old environments like Internet Explorer and Android 4.x -->
	<!-- >script type="text/javascript" src="${path}/web-UI/js/plugins/polyfill.min.js"></script-->
	<script src="https://cdn.polyfill.io/v2/polyfill.min.js?features=requestAnimationFrame,Element.prototype.classList,URL"></script>
	<script src="${path}/web-UI/js/plugins/jquery-3.3.1.min.js" ></script>
	<script src="${path}/web-UI/js/plugins/bootstrap.min.js"></script>
	<script src="${path}/web-UI/ol/ol.js"></script>

	<script type="text/javascript" src="${path}/web-UI/js/map.js"></script>
	<script type="text/javascript" src="${path}/web-UI/js/utility.js"></script>
	<script type="text/javascript" src="${path}/web-UI/js/bike.js" ></script>
	<script type="text/javascript" src="${path}/web-UI/js/request-handler.js" ></script>

	<script type="text/javascript" src="${path}/web-UI/js/user-section-manager.js" ></script>


</head>
<body>
	
	<div class="sidenav bg-color4">
		<div class="usr-details">
			<img class="usr-img" src="${path}/web-UI/img/profile/<%=session.getAttribute("avatar")%>" height="150" width="150"> 
			<div class="usr-info" id="username"><%=session.getAttribute("username")%> </div>
			<div class="usr-data">
				<div class="usr-info d-none d-md-block label">User ID:</div><div id="user_ID" class="usr-info data"><%=session.getAttribute("userId")%></div>
				<div class="usr-info d-none label">Weight:</div><div id="weight" class="d-none usr-info data"><%=session.getAttribute("weight")%></div>
				<div class="usr-info d-none d-md-block label">Subscription ID:</div><div id="subs_ID" class="usr-info data"><%=session.getAttribute("subscriptionId")%></div>
				<div class="usr-info d-none d-md-block label">In use Bike:</div><div id="used-bike" class="usr-info data">/</div>
			</div>

		</div>

		<div class="bike-info user-bike-data-section" id="active-bike-data-section">
			<div class="red error-message"></div>
			Click on a locked bike to get its data and unlock it!
			<div class="bike-name"> Bike ID: <span id="bike-id"><i>Select a bike</i></span></div>
			<div class="bike-coordinates"> Coordinates: <span id="bike-coord"><i>Select a bike</i></span></div>
			<button id="unlock-btn" onclick="unlockBike($('#bike-id').text(), '<%=session.getAttribute("username")%>')">Unlock</button>
			<button id="lock-btn" onclick="lockBike($('#used-bike').text(), '<%=session.getAttribute("username")%>')">Release</button>
		</div>

		<a id="logout-btn" href="${path}/LogoutServlet"> Logout </a>
	</div>
	<div id="main-section" class="main bg-color7" data-spy="scroll" data-target="#spy-navbar" data-offset="0">
		<!--div class="parallax-hdr">
		</div-->

		
		<div class="dashboard-header">
			<div class="title"> USER DASHBOARD </div>
			<div class="navigation">
				<button class="active nav-btn" onclick="toggleSection('#overview', this); getUserInfo(<%=session.getAttribute("userId")%>, '<%=session.getAttribute("username")%>', <%=session.getAttribute("weight")%>)">Overview</button>
				<button class="nav-btn" onclick="toggleSection('#map-section', this); mapInit(); lockedBikesInit();">Unlock Bicycle</button>
				<button class="nav-btn" onclick="toggleSection('#settings', this);">Settings</button>
			</div>
		</div>
		<div class="container">
			<div class="dashboard-section row" id="overview">
				<div class="col-lg-12 p-0">
					<div> Subscription data </div>
				</div>
				<div class="col-lg-6 p-0">
					<div class="subsection">
						<div class="title"> Balance </div>
						<div class="data big"> <span id="balance"><%=session.getAttribute("balance")%></span>&euro;</div>
						<img class="big-icon" src="img/icons/balance.png">
					</div>
				</div>
				<div class="col-lg-6 p-0">
					<div class="subsection"> 
						<div class="head-img"><img class="small-icon" src="img/icons/calendar.png"> </div>
						<div class="title">	Expiring date  <span id="exp-date" class="red"></span></div>
						<div class="data"><span id="remaining-time"></span><br> Renew Subscription</div>
						<form action="../SubscriptionDataServlet" method="POST" >
							<input class="hide" name="userId" value="<%=session.getAttribute("userId")%>">
							<input class="hide" name="balance" value="<%=session.getAttribute("balance")%>">
							<select class="user-form-select" id="subscription-type" name="subscription-type" required>
								<option id="default" value="">Select a subscription</option>
								<option id="weekly" value="5">Weekly subs. (5&euro;)</option>
								<option id="monthly" value="10">Montlhy subs. (10&euro;)</option>
								<option id="annual" value="100">Annual subs. (100&euro;)</option>
							</select>
							<button class="user-form-button" id="renew-sub" name="update" type="submit">Renew Subscription</button>
						</form>
					</div>
				</div>
				<!-- div class="col-12 p-0">
					<div>Daily Statistics</div>
				</div>
				<div class="col-sm-6 col-lg-3 p-0"><div class="subsection"><img class="small-icon" src="img/icons/trips.png"> # trips: <span id="daily-trips"></span></div></div>
				<div class="col-sm-6 col-lg-3 p-0"><div class="subsection"><img class="small-icon" src="img/icons/km.png">  Kilometers: <span id="daily-km"></span></div></div>
				<div class="col-sm-6 col-lg-3 p-0"><div class="subsection"><img class="small-icon" src="img/icons/speed.png"> Avg Speed: <span id="daily-speed"></span></div></div>
				<div class="col-sm-6 col-lg-3 p-0"><div class="subsection"><img class="small-icon" src="img/icons/cal.png"> Calories: <span id="daily-cal"></span></div></div>
				<div class="col-6 p-0">
					<div class="subsection notes-for-user" id="aq-daily-notes">
						The environment is grateful towards you: the average co2 emissions for kilometers in a bycicle trip is <i>21g</i> versus the <i>115,4g/km</i> for a car trip.
						<br> So today you've saved approximately <span class="emissions">0 </span>g. <span class="message">You can do better!</span>
					</div>
				</div>
				<div class="col-6 p-0">
					<div class="subsection notes-for-user" id="cal-daily-notes">
						The average calories spent by cycling for 60 minutes is calculated as <i>(your weight) * 4</i>, but it depends also on your speed.
					</div>
				</div-->
				<div class="col-12 p-0">
					<div>Total Statistics</div>
				</div>
				<div class="col-sm-6 col-lg-3 p-0"><div class="subsection"><img class="small-icon" src="img/icons/trips.png">  # trips: <b><span id="trips"></span></b></div></div>
				<div class="col-sm-6 col-lg-3 p-0"><div class="subsection"><img class="small-icon" src="img/icons/km.png"> Kilometers: <b><span id="km"></span></b></div></div>
				<div class="col-sm-6 col-lg-3 p-0"><div class="subsection"><img class="small-icon" src="img/icons/speed.png"> Avg Speed: <b><span id="speed"></span></b></div></div>
				<div class="col-sm-6 col-lg-3 p-0"><div class="subsection"><img class="small-icon" src="img/icons/cal.png"> Calories: <b><span id="cal"></span></b></div></div>
				<div class="col-6 p-0">
					<div class="subsection notes-for-user" id="aq-total-notes">
						<div class="head-img"><img class="small-icon" src="img/icons/env.png"></div>
						The environment is grateful to you: the average co2 emissions for kilometers in a bycicle trip is <i>21g</i> versus the <i>115,4g/km</i> for a car trip.
						So you've saved approximately <b><span class="emissions">0 </span></b>g. <br><span class="message">You can do better!</span>
					</div>
				</div>
				<div class="col-6 p-0">
					<div class="subsection notes-for-user" id="cal-total-notes">
						<div class="head-img"><img class="small-icon" src="img/icons/avg_cal.png"></div>
						The average calories spent by cycling for 60 minutes, at low speed (less than 16 km/h), is calculated as <i>(your weight) * 4</i>, but it depends also on your speed.
					</div>
				</div>
			</div>
			<div class="dashboard-section row" id="map-section">
				<!-- OpenStreetMap -->
				<div class="col-lg-12 p-0">
					<div class="subsection" id="map"></div>
				</div>
			</div>
			<div class="dashboard-section row" id="settings">
				<div class="col-12 p-0">
					<div> Your data </div>
				</div>
				<div class="col-6 p-0 setting-col-l">
					<div class="subsection">
						<div class="title"> Anagraphical data </div>
						<div>Username: <%=session.getAttribute("username") %></div>
						<div>Weight: <%=session.getAttribute("weight") %></div>
						<div>Email: <%=session.getAttribute("email") %></div>
						
						<div class="form-title"><b>Update your info:</b></div>
						<form action="../UserInfoServlet" method="POST">
							<input class="hide" name="userId" value="<%=session.getAttribute("userId")%>">
							Insert your new weight:<br> <input class="user-form-input user-form-select" type="text" name="weight"><br>
							Insert your new email:<br> <input class="user-form-input user-form-select" type="email" name="email"><br>
							<button class="user-form-button" id="update-info" name="update-anagraphic" type="submit">Update info</button>
						</form>
					</div>
				</div>
				<div class="col-6 p-0 setting-col-r">
					<div class="subsection">
						<div class="title"> Subscription data </div>
							
						<div>Balance: <%=session.getAttribute("balance") %></div>
						
						<div class="form-title"><b>Update your balance:</b></div>
						<form action="../SubscriptionDataServlet" method="POST">
							<input class="hide" name="userId" value="<%=session.getAttribute("userId")%>">
							<input class="hide" name="subscriptionId" value="<%=session.getAttribute("subscriptionId")%>">
							<input class="hide" name="balance" value="<%=session.getAttribute("balance")%>">
							Insert your new credit: <input class="user-form-select" type="text" name="recharge"><br>
							<button class="user-form-button" id="update-balance" name="recharge" type="submit">Recharge</button>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>

</body>
</html>