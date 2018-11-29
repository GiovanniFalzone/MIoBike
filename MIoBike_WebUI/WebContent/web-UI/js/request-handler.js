/* REQUEST-HANDLER
 * This file manages ajax requests to servlets
 */

/*
 * Retrieve the last read values of some sensors (Humidity, Temperature, AirQuality and Odometer) to plot them
 * 		GET to ChartDataServlet
 * 		Response: array of sensors with format: {'sensorname' : [{ BikeName: 'bikename', value: 'value'}, ..]}
 */
function getChartData() {
	$.get(servlet_dir+"ChartDataServlet", function(response) {
		if(DEV_MODE)
			console.log(response);
		var data;
		if(response && typeof response === "object")
			data = response;
		else
			data = JSON.parse(response);
		
		// Create the chart for each sensor
		createChart('AirQuality', response);
		createChart('Odometer', response);
		createChart('Temperature', response);
		createChart('Humidity', response);
		createSensorChart("tyrepressure-front-chart", "Tyre Pressure Front Tyre", UOM_TP, "FrontTyre", "bar", GRAY, "", 5, [], []);
		createSensorChart("tyrepressure-rear-chart", "Tyre Pressure Rear Tyre", UOM_TP, "RearTyre", "bar", GRAY, "", 5, [], []);
	});
}

/*
 * Retrieve the last read value of the selected sensor of the selected bike
 * 		GET to RequestSensorsServlet with the parameters BikeName and SensorName
 * 		Response: JSON object with format:  {date: "data", resource: "nome risorsa", value: "valore", bike: "nome bici"}
 */

function getAndPlotSensor(btn) {
	var chartDiv = "bike-sensor-chart";
	var bikeName = $('#bike-name').val();
	var sensorName = $('#sensor-name').val();

	$.get(servlet_dir+"RequestSensorsServlet", {BikeName: bikeName, SensorName: sensorName}, function(response) {
		if(DEV_MODE)
			console.log(response);
		
		// Data and labels array for chart
		var data = [];
		var labels = [];
		for(var i = response.length-1; i >= 0; i--) {
			var d = response[i].date;
			d = d.replace(".", "-");
			d = d.replace(".", "-");
			d = d.replace("_", " ");
			
			labels.push(d);
			data.push(parseFloat(response[i].value));
		}
		
		sensorName = sensorName;
		var uom;
		// Unite Of Measure
		switch(sensorName) {
		case "Temperature":
			uom = "("+UOM_TEMP+")";
			break;
		case "Speed":
			uom = "("+UOM_SPEED+")";
			break;
		case "Odometer":
			uom = "("+UOM_ODO+")";
			break;
		case "AirQuality":
			uom = "("+UOM_AQ+")";
			break;
		case "Humidity":
			uom = "("+UOM_HUM+")";
			break;
		case "TyrePressure":
		default:
			uom = "("+UOM_TP+")";
		break;
		}
		if(!isDefined(sensorChart) || btn.value == "show")
			createChartForSingleBike(chartDiv, bikeName+"-"+sensorName, uom, data, labels, "line", GRAY_T, RED);
		else
			updateChart(chartDiv, bikeName+"-"+sensorName, uom, data, labels, "line", GRAY_T, BLUE);
	});


}

/*
 * Retrieve all the bikes registered on IN
 * 		GET to RequestBikeServlet with parameter userType
 * 			if userType = 1 (admin) it asks for all the bike (locked and/or unlocked) 
 * 			if userType = 0 (normal user) it asks only for locked bike (privacy: user can't see bikes of other users positions)
 * 		Response: JSON array of bikes with last value of their sensors, it is saved in global array BIKESARRAY
 */
function getAllBikes(admin) {
	
	$.get(servlet_dir+"RequestBikeServlet",{userType : admin}, function(response) {
		if(admin == 1) {
			if(response && typeof response === "object")
				BIKESARRAY = response;
			else
				BIKESARRAY = JSON.parse(response);

			if(DEV_MODE)
				console.log("All the bikes:" +JSON.stringify(BIKESARRAY));
			
			// Add the handler for the toggler for active-bike
			$('#active-bike-toggler').click(function () {
				$('#active-bike-sensors').toggle();
			});

			$('#active-bike-show').click(function() {
				var bikeId = $('#active-bike-data-section').find('.bike-name').text();
				var marker = null
				if(isInLayer(bikeId, movingBikeLayer))
					marker = getMarker(bikeId, movingBikeLayer);
				else if(isInLayer(bikeId, lockedBikeLayer))
					marker = getMarker(bikeId, lockedBikeLayer);
				
				if(marker != null) {
					var coord = getCoordinates(marker);
					if(isInLayer(bikeId, activeBikeLayer)) {
						clearLayer(activeBikeLayer);
						hideLayer(activeBikeLayer);
						return;
					}
					clearLayer(activeBikeLayer);
					addBike(bikeId, coord[1], coord[0], activeBikeLayer);
					showLayer(activeBikeLayer);
				}
			});

			$('#active-bike-sensors').hide();
			
			
			// Cycle on bikes to create bike section with sensors values
			BIKESARRAY.forEach(function(bike, index) {
				var name = bike.BikeName;
				if($('#'+name+'-data-section').length == 0) 
					createBikeSection(bike);
				else
					updateBikeSection(bike, $('#'+name+'-data-section'));

				// add the handler for the toggler
				$('#'+name+'-toggler').click(function () {
					$('#'+name+'-sensors').toggle();
				});
				
				//highlight handler bicycle on map
				$('#'+name+'-show').click(function () {
					var marker = null
					if(isInLayer(name, movingBikeLayer))
						marker = getMarker(name, movingBikeLayer);
					else if(isInLayer(name, lockedBikeLayer))
						marker = getMarker(name, lockedBikeLayer);
					if(marker != null) {
						var coord = getCoordinates(marker);
						if(isInLayer(name, activeBikeLayer)) {
							clearLayer(activeBikeLayer);
							hideLayer(activeBikeLayer);
							return;
						}
						clearLayer(activeBikeLayer);
						addBike(name, coord[1], coord[0], activeBikeLayer);
						showLayer(activeBikeLayer);
					}
				});
				
				//hide the sensors
				$('#'+name+'-sensors').hide();
			});
		} else {
			//user retrieve only locked bike
			if(response && typeof response === "object")
				BIKESARRAY = response;
			else
				BIKESARRAY = JSON.parse(response);
			
			//check if user is actually on a bike 
			var usedBike = $('#used-bike').text().replace(" ", "");
			var using = false;
			if(usedBike != "/" && usedBike != " / " && usedBike != " /" && usedBike != "/ ") 
				using = true;
			
			if(DEV_MODE)
				console.log("Locked Bikes: "+JSON.stringify(BIKESARRAY));
			
			// Cycles on locked bikes to update map 
			BIKESARRAY.forEach(function(bike, index) {
				var name = bike.BikeName;
				var coord;
				//here the bikes are add to the map as locked or disabled
				if(isDefined(getGPSValue(bike)))
					coord = getGPSValue(bike).value;
				if(isDefined(coord)) {
					if(using) {
						if(bike.BikeName == usedBike)
							addBike(name, coord.lat, coord.long, lockedBikeLayer);
						else
							addBike(name, coord.lat, coord.long, disabledBikeLayer);
					} else
						addBike(name, coord.lat, coord.long, lockedBikeLayer);
				}
			});
			// layers are set to visible on map
			4
			showLayer(lockedBikeLayer);
			showLayer(disabledBikeLayer);
			
		}
	});
}

/*
 * Retrieve user informations to update user dashboard overview and show its data and stats
 * 		GET to UserInfoServlet
 * 		Response: json with format: {"subscription_info":{"activation_date":date,"expiration_date":date}, "trips_info": {"distance":kilometers,"trips": total trips,"time":milliseconds,"speed":avg_spped}, "using_bike": bikeId}}
 */
function getUserInfo(userId, user, weight) {
	if(!isDefined(userId) || !isDefined(user))
		return;

	// Some info are initialized at login, retrieved from SQL db
	var bal = $("#balance").text();
	if(bal>5) {
		var select = $('#subscription-type');
		if(bal<100)
			select.find($('#annual')).prop('disabled', true);
		if(bal<10)
			select.find($('#monthly')).prop('disabled', true);
		$("#balance").addClass("green");
	} else {
		var select = $('#subscription-type');
		select.find($('#default')).text("You can't renew your subscription, you don't have money!");
		select.find($('#weekly')).prop('disabled', true);
		select.find($('#monthly')).prop('disabled', true);
		select.find($('#annual')).prop('disabled', true);
		$('#renew-sub').prop('disabled', true);
		
		$("#balance").addClass("red");
	}
	
	// get to userInfoServlet to retrieve info from IN
	$.get(servlet_dir+"UserInfoServlet", {user_ID:userId, username: user}, function(response) {
		var user_info = null;
		if(response && typeof response === "object")
			user_info = response;
		else
			user_info = JSON.parse(response);
		
		if(DEV_MODE)
			console.log("User Info: "+JSON.stringify(user_info));

		var today = new Date();
		var exp_date = null;
		if(isDefined(user_info.subscription_info))
			exp_date = user_info.subscription_info.expiration_date;
		exp_date = stringToDatetime(exp_date);
		
		var remaining_days = diffDays(today, exp_date);
		
		$("#exp-date").text(datetimeToString(exp_date, true));
		
		if(remaining_days>0)
			$("#remaining-time").text("You have "+remaining_days+" days remaining");
		else
			$("#remaining-time").text("Your subscription expired "+Math.abs(remaining_days)+" days ago");
		
		// Calculation of user stats: kilometers, average speed, using bike, number of trips, calories, emissions
		if(isDefined(user_info.using_bike))
			$('#used-bike').text(user_info.using_bike);
		else
			$('#used-bike').text("/");

		var dist = 0;
		var trips = 0;
		var avg_speed = 0;
		if(isDefined(user_info.trips_info)) {
			dist = user_info.trips_info.distance;
			trips = user_info.trips_info.trips;
			avg_speed = user_info.trips_info.speed;
		}
	
		$('#km').text(dist+" "+UOM_ODO);
		$('#speed').text(avg_speed+" "+UOM_SPEED);
		$('#trips').text(trips);

		var aq_div = $("#aq-total-notes");
		var co2 = (115.4 - 21)*dist;
		aq_div.find(".emissions").text(co2.toFixed(2));
		if(co2 == 0) 
			aq_div.find(".message").text(MSG_ZERO);
		else if(co2 < 1000)
			aq_div.find(".message").text(MSG_NOT_GOOD);
		else if(co2 < 50000)
			aq_div.find(".message").text(MSG_GOOD);
		else
			aq_div.find(".message").text(MSG_PLANET_FRIEND);
		
		var minutes = 0; //calculates cycling minutes
		if(isDefined(user_info.trips_info)) {
			minutes = parseInt(user_info.trips_info.time);
			minutes = minutes/60000;
		}
		var calories = 0;
		if(avg_speed < 16)
			calories = MIN_CALORIES * weight *(minutes/60);
		else if(avg_speed < 20)
			calories = AVG_CALORIES * weight *(minutes/60);
		else if(avg_speed < 26)
			calories = MED_CALORIES * weight *(minutes/60);
		else
			calories = MAX_CALORIES * weight *(minutes/60);
				
		$('#cal').text(calories.toFixed(2)+" "+UOM_CAL);

	});
}

/*
 * Forward an unlock request for the bicycle "bikeName" by the user "userName" to IN. This function is called by user through interface button
 * 		POST to LockBikeServlet with parameters bike, user, unlock = true (unlock)
 * 		Response: true if success
 * It updates interface with user's bike info and updating the map (disabling the other bikes)
 */
function unlockBike(bikeName, userName) {
	if(DEV_MODE)
		console.log("Unlocking bike "+bikeName+" for user "+userName);
	$.post(servlet_dir+"LockBikeServlet", {bike: bikeName, user: userName, unlock: "true"}, function(resp) {
		
		// Each other bike become disabled
		BIKESARRAY.forEach(function(bike,index) {
			if(bike.BikeName != bikeName)
				changeLayer(bike.BikeName, lockedBikeLayer, disabledBikeLayer);
		});
		showLayer(disabledBikeLayer);
		
		if(resp.res == true) {
			// User info are updated
			$('#used-bike').text(bikeName);
			$('#unlock-btn').prop("disabled", true);
			$('#lock-btn').prop("disabled", false);
			$('#active-bike-data-section').find('.error-message').text("SUCCESS: you are using "+bikeName);
			$('#active-bike-data-section').find('.error-message').removeClass('red');
		} else {
			$('#active-bike-data-section').find('.error-message').text("It was impossible to unlock the bike");
			$('#active-bike-data-section').find('.error-message').addClass('red');
		}
	});
}

/*
 *	Forward a release request for the bicycle "bikeName" by user "userName" to IN. Called by interaction with user dashboard button
 *		POST to LockBikeServlet with parameter bike, user, unlock = false (release)
 * It updates interface with user's bike info and updating the map (enabling the other bikes)
 */
function lockBike(bikeName, userName) {
	if(DEV_MODE)
		console.log("Locking bike "+bikeName+" for user "+userName);
	console.log(typeof bikeName);
	console.log(bikeName);
	$.post(servlet_dir+"LockBikeServlet", {bike: bikeName, user: userName, unlock: "false"}, function(resp){
			
		var gps = getGPSValue(bikeName);
		if(isDefined(gps)) {
			lat = parseFloat(gps.value.lat);
			long = parseFloat(gps.value.long);
			addBike(bikeName, lat, long, lockedBikeLayer);
		}
		// Disabled bike become locked (so unlockable)
		BIKESARRAY.forEach(function(bike, index) {
			if(bike.BikeName != bikeName)
				changeLayer(bike.BikeName, disabledBikeLayer, lockedBikeLayer);
		});

		hideLayer(disabledBikeLayer);
		
		$('#used-bike').text(' / ');
		$('#lock-btn').prop("disabled", true);
		$('#unlock-btn').prop("disabled", false);
		$('#active-bike-data-section').find('.error-message').text("SUCCESS: you've released "+bikeName);
		$('#active-bike-data-section').find('.error-message').removeClass('red');
			
	});
		
}

/*
 * Retrieve all the users info for the admin
 * 		GET to UsersServlet
 * 		Response: a JSON array with format: {users: [{avatar: "avatar_uri", ]}
 */
function getUsers() {
	$.get("../UsersServlet", function(response) {
		var users;
		if(response && typeof response === "object")
			users = response;
		else
			users = JSON.parse(response);

		users.forEach(function(user, index) {
			//Create the subsection with user informations
			var userInfo = '<img class="usr-img" src="img/profile/'+user.avatar+'" width="100" height="100"><div class="user-name">Username: '+user.name+'</div><div class="user-subscription"><div class="subs-id">Subscription ID: '+user.subscription_ID+'</div>';

			if(isDefined(user.subscription_info))
				userInfo += '<div class="expiration-date">Expiration Date: '+user.subscription_info.expiration_date+'</div>';
				userInfo += '<div class="user-balance"> Balance: '+user.balance+'</div></div>'
				userInfo += '<div class="user-mail"><a href="mailto:'+user.email+'">Send an email</a></div>';
			var userSection = '<div class="col-sm-6 p-0"><div class="subsection user-card" id="'+user.name+'-info">'+userInfo+'</div></div>';
			$('#users').find('.row').append(userSection);
		});
	});
}
