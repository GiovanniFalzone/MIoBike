<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="it">
<head>
	<c:set var="path" value="${pageContext.request.contextPath}" />

	<!-- jsp:useBean id="current_sess" type="it.unipi.iot.MIoBike.web.servlets.UserSession" / -->
	<title> MIo Bike - dashboard </title>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta charset="utf-8">

	<link href="${path}/web-UI/css/style.css" rel="stylesheet">
	<link rel="stylesheet" href="${path}/web-UI/ol/ol.css" type="text/css">
	<!-- The line below is only needed for old environments like Internet Explorer and Android 4.x -->
	<script src="https://cdn.polyfill.io/v2/polyfill.min.js?features=requestAnimationFrame,Element.prototype.classList,URL"></script>

	
	
	<link rel="shortcut icon" href="${path}/web-UI/img/icons/favicon.png" />
	<link href="${path}/web-UI/css/bootstrap.min.css" rel="stylesheet">
	<script src="${path}/web-UI/js/jquery-3.3.1.min.js" ></script>
	<script src="${path}/web-UI/js/bootstrap.min.js"></script>
	<script src="${path}/web-UI/ol/ol.js"></script>
	
	<script src="${path}/web-UI/js/ajax_script.js" ></script>

	<!-- OpenLayers Modules -->
	<!--script type="module" src="/js/ol/Map.js"></script>
	<script type="module" src="/js/ol/View.js"></script>
	<script type="module" src="/js/ol/layer/Tile.js"></script>
	<script type="module" src="/js/ol/Source/XYZ.js"></script>
	<script type="module" src="/js/ol/Source/OSM.js"></script-->

	<script src="${path}/web-UI/js/map.js"></script>
	<script type="text/javascript" src="${path}/web-UI/js/dashboard.js"></script>

</head>
<body data-spy="scroll" data-target=".sidenav" data-offset="50">
	
	<div id="spy-navbar" class="sidenav bg-color4">
		<div class="usr-details">
			<img class="usr-img" src="${path}/web-UI/img/icons/<%=session.getAttribute("avatar")%>" width="100"> 
			<div class="usr-info"><%=session.getAttribute("username")%> </div>
			<div class="usr-data">
				<div class="usr-info d-none d-md-block label">User ID:</div><div id="user_ID" class="usr-info data"><%=session.getAttribute("userId")%></div>
				<div class="usr-info d-none d-md-block label">Subscription ID:</div><div id="subs_ID" class="usr-info data"><%=session.getAttribute("subscriptionId")%></div>
				<div class="usr-info d-none d-md-block label">Key ID:</div><div id="key_ID" class="usr-info data"><%=session.getAttribute("keyId")%></div>
			</div>
		</div>

		<a id="logout-btn" href="${path}/LogoutServlet"> Logout </a>
		<!--a href="#map"> Map </a-->
	</div>
	<div class="main bg-color7" data-spy="scroll" data-target="#spy-navbar" data-offset="0">
		<!--div class="parallax-hdr">
		</div-->

		
		<div class="dashboard-header">
			<div class="title"> USER DASHBOARD </div>
			<div class="navigation">
				<button class="active nav-btn" onclick="toggleSection('#overview', this);">Overview</button>
				<button class="nav-btn" onclick="toggleSection('#map-section', this); mapInit();">Unlock Bicycle</button>
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
						<div id="balance"class="data big"> 50</div>
						<img class="icon" src="img/icons/balance.png">
					</div>
				</div>
				<div class="col-lg-6 p-0">
					<div class="subsection"> 
						<div class="title">	Expiring date  <span id="exp-date" class="data red"></span></div>
						<div class="data"><span id="remaining-time"></span></div>
						<form>Renew Subscription</form>
					</div>
				</div>
				<div class="col-12 p-0">
					<div>Daily Statistics</div>
				</div>
				<div class="col-lg-4 p-0"><div class="subsection"> # trips: <span id="daily-trips"></span></div></div>
				<div class="col-lg-4 p-0"><div class="subsection"> Kilometers: <span id="daily-km"></span></div></div>
				<div class="col-lg-4 p-0"><div class="subsection"> Calories: <span id="daily-cal"></span></div></div>
				<div class="col-12 p-0">
					<div>Total Statistics</div>
				</div>
				<div class="col-lg-4 p-0"><div class="subsection"> # trips: <span id="trips"></span></div></div>
				<div class="col-lg-4 p-0"><div class="subsection"> Kilometers: <span id="km"></span></div></div>
				<div class="col-lg-4 p-0"><div class="subsection"> Calories: <span id="cal"></span></div></div>
			</div>
			<div class="dashboard-section row" id="map-section">
				<div class="col-4 p-0">
					<div class="subsection">
						<button onclick="toggleLayer(movingBikeLayer);"> Moving Bikes </button>
						<button onclick="toggleLayer(lockedBikeLayer);"> Locked Bikes </button>
						<button onclick="toggleLayer(lockerLayer);"> Lockers </button>
					</div>
				</div>
				<!-- OpenStreetMap -->
				<div class="col-8 p-0">
					<div class="subsection" id="map"></div>
				</div>
			</div>
			<div class="dashboard-section row" id="settings">
				<div class="col-12 p-0">
					<div> Update your anagraphical data </div>
				</div>
				<div class="col-6 p-0">
					<div class="subsection">
						<div class="title"> Anagraphical data </div>
						<form>Weight
						Email
						<input type="button" value="update">
						</form>
					</div>
				</div>
			</div>
		</div>

</body>
</html>