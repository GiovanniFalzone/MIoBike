<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
		pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<c:set var="path" value="${pageContext.request.contextPath}" />

	<!-- jsp:useBean id="current_sess" type="it.unipi.iot.MIoBike.web.servlets.UserSession" / -->
	<title> MIo Bike - dashboard </title>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	
	<link rel="shortcut icon" href="${path}/web-UI/img/icons/favicon.png" />
	<link href="${path}/web-UI/css/style.css" rel="stylesheet">
	<link rel="stylesheet" href="${path}/web-UI/ol/ol.css" type="text/css">
	<link href="${path}/web-UI/css/bootstrap.min.css" rel="stylesheet">

	<!-- The line below is only needed for old environments like Internet Explorer and Android 4.x -->
	<!-- script src="https://cdn.polyfill.io/v2/polyfill.min.js?features=requestAnimationFrame,Element.prototype.classList,URL"></script-->
	
	<script type="text/javascript" src="${path}/web-UI/js/plugins/polyfill.min.js"></script>
	<script src="${path}/web-UI/js/plugins/jquery-3.3.1.min.js" ></script>
	<script src="${path}/web-UI/js/plugins/bootstrap.min.js"></script>
	<script type="text/javascript" src="${path}/web-UI/ol/ol.js"></script>
	<script type="text/javascript" src="${path}/web-UI/js/plugins/chart.bundle.min.js"></script>
	<script type="text/javascript" src="${path}/web-UI/js/plugins/hammer.min.js"></script>
	<script type="text/javascript" src="${path}/web-UI/js/plugins/chartjs-plugin-zoom.min.js"></script>
	
	<script type="text/javascript" src="${path}/web-UI/js/utility.js"></script>
	<script type="text/javascript" src="${path}/web-UI/js/bike.js"></script>
	<script type="text/javascript" src="${path}/web-UI/js/map.js"></script>
	<script type="text/javascript" src="${path}/web-UI/js/request-handler.js"></script>

	<script type="text/javascript" src="${path}/web-UI/js/admin-bike-section-manager.js"></script>
	<script type="text/javascript" src="${path}/web-UI/js/admin-chart-section-manager.js"></script>

</head>

<body onload="mapInit(); bikesInit();">	
	<div class="bg-color4">
		<div class="container dashboard-header admin-dashboard-header"> 
			<img width="40px" src="${path}/web-UI/img/icons/bike-home.png">
			<div class="title">ADMIN DASHBOARD </div>
			<div class="navigation">
				<button class="active nav-btn" onclick="toggleSection('#overview', this);">Overview</button>
				<button class="nav-btn" onclick="toggleSection('#data', this); chartInit();">Data</button>
				<button class="nav-btn" onclick="toggleSection('#users', this); getUsers();">Users</button>
				<a class="nav-btn d-block d-lg-none" href="${path}/LogoutServlet"> LOGOUT</a>
			</div>
			<a class="nav-btn logout d-sm-none d-lg-block" href="${path}/LogoutServlet"> LOGOUT</a>
		</div>
	</div>
	<div id="main-section">
		<div class="admin-dashboard-container">			
			<div class="dashboard-section admin-bike row" id="overview">
				<div class="col-sm-12 p-0"><div class="title"> Bikes Overview</div></div>
				<div class="col-sm-4 p-0 col-bike">
					<div class="subsection active-bike">
					<!-- active bike data -->
						Click on a bike to get its data
						<div class="bike-data" id="active-bike-data-section">
							<div class="bike-name"><i>null</i></div>
							<div class="bike-status"> Status: <i></i></div>
							<div class="bike-user"> User: <i></i></div>
							<div class="bike-btn-section">
								<button class="sensors-toggler bike-btn" id="active-bike-toggler">Look at Sensors Status</button>
								<button class="show-btn bike-btn" id="active-bike-show">Show on Map</button>
							</div>
							<div class="bike-sensors" id="active-bike-sensors">
								<div class="sensor-speed"> speed: <span class="sensor-value">No value retrieved</span></div>
								<div class="sensor-temperature"> temperature: <span class="sensor-value">No value retrieved</span></div>
								<div class="sensor-tyre-pressure"> tyre-pressure: <span class="sensor-value">No value retrieved</span></div>
								<div class="sensor-air-quality"> air-quality: <span class="sensor-value">No value retrieved</span></div>
								<div class="sensor-humidity"> humidity: <span class="sensor-value">No value retrieved</span></div>
								<div class="sensor-odometer"> odometer: <span class="sensor-value">No value retrieved</span></div>
							</div>
						</div>
					</div>
					<div class="subsection other-bikes">
					</div>
				</div>
				<!-- OpenStreetMap -->
				<div class="col-sm-8 p-0 col-map">
					<div class="col-sm-12 subsection p-1">
						<button class="bike-btn" onclick="toggleLayer(lockedBikeLayer);"> See locked bikes</button>
						<button class="bike-btn" onclick="toggleLayer(movingBikeLayer);"> See unlocked bikes</button>
					</div>
					<div class="col-sm-12 subsection" id="map"></div>
				</div>
			</div>
		</div>
		<div class="container">
		<div class="dashboard-section" id="data">
			<div class="row">
				<div class="col-sm-12 p-0"><div class="title"> Mean Values</div></div>
			</div>
			<div class="row">
				<div class="subsection admin-sensor-status col-sm-3">
					<div class="title"> Temperature (&deg;C) </div>
					<div class="data"><img class="small-icon" src="img/icons/temp.png"><span id="actual-temperature"></span></div>
				</div>
				<div class="subsection admin-sensor-status col-sm-3">
					<div class="title"> Humidity (%)</div>
					<div class="data"><img class="small-icon" src="img/icons/hum.png"><span id="actual-humidity"></span></div>
				</div>
				<div class="subsection admin-sensor-status col-sm-3">
					<div class="title"> Air Quality (&micro;g/m<sup>3</</sup>)</div>
					<div class="data"><img class="small-icon" src="img/icons/aq.png"><span id="actual-airquality"></span></div>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-12 p-0"><div class="title"> Charts</div></div>
			</div>
			<div class="row">
				<div class="col-sm-6 p-0">
					<div class="subsection">
						<canvas id="temperature-chart" width="200" height="200"></canvas>
						
					</div>
				</div>
				<div class="col-sm-6 p-0">
					<div class="subsection">
						<canvas id="humidity-chart" width="200" height="200"></canvas>
						
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-6 p-0">
					<div class="subsection">
						<canvas id="airquality-chart" width="200" height="200"></canvas>
						
					</div>
				</div>
				<div class="col-sm-6 p-0">
					<div class="subsection">
						<canvas id="odometer-chart" width="200" height="200"></canvas>
						
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-6 p-0">
					<div class="subsection">
						<canvas id="tyrepressure-front-chart" width="200" height="200"></canvas>
						<div class="tyrepressure-messages tyrepressure-fronttyre-messages">
						These bikes don't have problems:
							<div class="ok"></div>
						These bikes need maintenance: <br> Pressure should be between (1.5,3)
							<div class="warning"></div>
						</div>
						
					</div>
				</div>
				<div class="col-sm-6 p-0">
					<div class="subsection">
						<canvas id="tyrepressure-rear-chart" width="200" height="200"></canvas>
						<div class="tyrepressure-messages tyrepressure-reartyre-messages">
						These bikes don't have problems:
							<div class="ok"></div>
						These bikes need maintenance: 
						<br> Pressure should be between (1.5,3)
							<div class="warning"></div>
						</div>
						
					</div>
				</div>
				
			</div>
			<div class="row">
				<div class="col-sm-12 p-0">
					<div class="subsection">
						<select class="user-form-select" id="bike-name">
							<!-- option value="Bike1">Bike1 </option>
							<option value="Bike2">Bike2 </option-->
						</select>
						<select class="user-form-select" id="sensor-name">
							<option value="Temperature"> Temperature </option>
							<option value="Humidity"> Humidity </option>
							<option value="AirQuality"> AirQuality </option>
							<option value="Speed"> Speed </option>
						</select>
						<button class="bike-btn" value="show" onclick="getAndPlotSensor(this);"> Show data</button>
						<button class="bike-btn" value="update" onclick="getAndPlotSensor(this);"> Add data</button>
						<canvas id="bike-sensor-chart" width="600" height="400"></canvas>
						
					</div>
				</div>
			</div>
		</div>
		<div class="dashboard-section" id="users">
			<div class="row"></div>
			
		</div>
	</div>

</body>
</html>
