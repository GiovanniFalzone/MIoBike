/* ADMIN-BIKE-SECTION-MANAGER
 * This file is used to manage admin dashboard section dedicated to bikes. It shows the bikes on the map,
 * and the status of their sensor, updating the page.
 */

/*
 * Receives data periodically from PeriodicMovingBikesServlet (every 1 seconds)
 * 	data is the array of unlocked bikes that must be moved on the map
 */
var moving_source = new EventSource("../PeriodicMovingBikesServlet");

moving_source.onmessage = function (event) {
	var data;
	if(event.data && typeof event.data === "object")
		data = event.data;
	else
		data = JSON.parse(event.data);
	
	if(DEV_MODE)
		console.log("Moving...");
	// moves the bike
	data.forEach(function(bike) {
		moveBike(bike);
	});

};

/*
 * Receives data periodically from PeriodicRequestBikeServlet (every 10 seconds)
 * 	data is the array of all bikes and contains the last read values from each sensor of each bike
 */
var bike_source = new EventSource("../PeriodicRequestBikesServlet");

bike_source.onmessage = function (event) {
	var data;
	if(event.data && typeof event.data === "object")
		data = event.data;
	else
		data = JSON.parse(event.data);
	
	if(DEV_MODE)
		console.log("Dati: "+JSON.stringify(data));
	
	// Update bike section for each bike (with sensors value)
	BIKESARRAY = data;
	BIKESARRAY.forEach(function(bike) {
		if($('.other-bikes').find(bike.BikeName+"-data-section"))
			updateBikeSection(bike, $('#'+bike.BikeName+'-data-section'));
		else
			createBikeSection(bike);
	})
	
	
};

/*
 * Dynamically creates the non-existing section for the sensor "name" giving the id sensor-"name"
 */
function createSensorDiv(name, value, format) {
	var className = name.toLowerCase();
	className = className.replace(" ", "-");
	if(!isDefined(value)) 
		return '<div class="sensor-'+className+'"> '+name+': <span class="sensor-value">No value for this sensor</span></div>';
	return '<div class="sensor-'+className+'""> '+name+': <span class="sensor-value">'+value.value+' '+format+'</span></div>';
}

/*
 * Dynamically updates the value of a resource in its section
 */
function updateSensorDiv(div, resource, format, type) {

	if(!isDefined(resource))
		div.text('No value for this sensor');
	else {
		if(type == 't')
			div.text('('+resource.value.front+', '+resource.value.rear+')');
		else if(type == 'g')
			div.text('('+resource.value.lat+', '+resource.value.long+')');
		else
			div.text(resource.value+" ");
		div.append(format);
	}
}

/*
 * Dynamically creates the section for a non-existing bike "bike", with bike-name, status and sensors
 * 	each section has an id BikeName"-data-section" where its name and the status of its sensor is shown
 */ 
function createBikeSection(bike) {

	name = bike.BikeName;

	var speed = getSpeedValue(bike);
	var temp = getTemperatureValue(bike);
	var tp = getTyrePressureValue(bike);
	var aq = getAirQualityValue(bike);
	var hum = getHumidityValue(bike);
	var odo = getOdometerValue(bike);
	var gps = getGPSValue(bike);
	var lock = getLockValue(bike);
	
	var user = getUserValue(bike);
		
	var bikeName = '<div class="bike-name"><b>'+name+'</b></div>';
	var bikeUser = "";
	if(isDefined(user) && lock.value == 'false')
		bikeUser = '<div class="bike-user"><i>'+user.value+'</i></div>';
	var lockStatus;
	if(!isDefined(lock) || lock.value == 'true')
		lockStatus = '<div class="bike-status"> Status: <i>Locked</i></div>';
	else if(isDefined(lock) && lock.value == 'false')
		lockStatus = '<div class="bike-status"> Status: <i>Unlocked</i></div>';
	else
		lockStatus = '<div class="bike-status"> Status: <i>Cant get informations</i></div>';
	var buttons = '<div class="bike-btn-section"><button class="sensors-toggler bike-btn" id="'+name+'-toggler">Look at Sensors Status</button><button class="show-btn bike-btn" id="'+name+'-show">Show on Map</button></div>';
		
	var sensors = '<div class="bike-sensors" id="'+name+'-sensors">';
	
	//create the sensor section
	if(isDefined(lock) && lock.value == 'false')
		sensors += (createSensorDiv("Speed", speed, UOM_SPEED));
	sensors += (createSensorDiv("Temperature", temp, UOM_TEMP_HTML));
	sensors += (createSensorDiv("Air Quality", aq, UOM_AQ_HTML));
	sensors += (createSensorDiv("Humidity", hum, UOM_HUM));
	sensors += (createSensorDiv("Odometer", odo, UOM_ODO));
	
	//tyre pressure and gps section have a different format
	if(isDefined(tp) && isDefined(tp.value))
		sensors += '<div class="sensor-tyre-pressure""> Tyre Pressure(front, rear): <span class="sensor-value">('+parseFloat(tp.value.front).toFixed(2)+', '+parseFloat(tp.value.rear).toFixed(2)+') '+UOM_TP+'</span></div>';
	if(isDefined(gps) && isDefined(gps.value))
		sensors+='<div class="sensor-gps""> GPS(lat, lon): <span class="sensor-value">('+parseFloat(gps.value.lat).toFixed(2)+', '+parseFloat(gps.value.long).toFixed(2)+')</span></div>';
	sensors += '</div>';
		
	//add a marker on the map for the bike
	if(isDefined(gps)) {
		var lat = parseFloat(gps.value.lat);
		var long = parseFloat(gps.value.long);
		if(isDefined(lock) && lock.value == 'false')
			addBike(name, lat, long, movingBikeLayer);
		else
			addBike(name, lat, long, lockedBikeLayer); 
	}
	
	//id="nomebici-data-section"
	var newBikeDiv = '<div class="bike-data" id="'+name+'-data-section">'+bikeName+lockStatus+bikeUser+buttons+sensors+'</div>';
	$('.other-bikes').append(newBikeDiv);
}

/*
 * Dynamically update the existing bike section with sensors' values
 */
function updateBikeSection(bike, bikeDataDiv) {
	
	var name = bike.BikeName;
	if(isDefined(name))
		bikeDataDiv.find('.bike-name').text(name);

	// get resource values in the format (value, format)
	var speed = getSpeedValue(bike);
	var temp = getTemperatureValue(bike);
	var tp = getTyrePressureValue(bike);
	var aq = getAirQualityValue(bike);
	var hum = getHumidityValue(bike);
	var odo = getOdometerValue(bike);
	var gps = getGPSValue(bike);
	var lock = getLockValue(bike);
	
	var user = getUserValue(bike);
	if(isDefined(user) && lock.value == 'false') 
		bikeDataDiv.find('.bike-user').find('i').text(user.value);
	else
		bikeDataDiv.find('.bike-user').find('i').text("Not in use");
	
	if(isDefined(lock) && lock.value == 'false') {
		var lat = gps.value.lat;
		var lon = gps.value.long;
		bikeDataDiv.find('.bike-status').find('i').text('Unlocked');
		if(!isInLayer(name, movingBikeLayer)) {
			if(isInLayer(name, lockedBikeLayer))
				removeBike(name, lockedBikeLayer);
			addBike(name, lat, lon, movingBikeLayer);
		}
	}
	else {
		bikeDataDiv.find('.bike-status').find('i').text('Locked');
		if(!isInLayer(name, lockedBikeLayer)) {
			if(isInLayer(name, movingBikeLayer))
				removeBike(name, movingBikeLayer);
			if(isDefined(gps)) {
				addBike(name, gps.value.lat, gps.value.long, lockedBikeLayer);
			}
		}
	}
	
	//updating
	var sensors = bikeDataDiv.find('.bike-sensors');
	if(isDefined(lock) && lock.value == 'false')	
		updateSensorDiv(sensors.find('.sensor-speed').find('.sensor-value'), speed, UOM_SPEED, 'n');
	updateSensorDiv(sensors.find('.sensor-temperature').find('.sensor-value'), temp, UOM_TEMP_HTML, 'n');
	updateSensorDiv(sensors.find('.sensor-tyre-pressure').find('.sensor-value'), tp, UOM_TP, 't');
	updateSensorDiv(sensors.find('.sensor-air-quality').find('.sensor-value'), aq, UOM_AQ_HTML, 'n');
	updateSensorDiv(sensors.find('.sensor-humidity').find('.sensor-value'), hum, UOM_HUM, 'n');
	updateSensorDiv(sensors.find('.sensor-odometer').find('.sensor-value'), odo, UOM_ODO, 'n');
	updateSensorDiv(sensors.find('.sensor-gps').find('.sensor-value'), gps, UOM_GPS, 'g');
	
}

/*
 * This function moves the bike retrieving its actual position and updating its marker on the map
 */
function moveBike(bike) {
	var bikeId = bike.BikeName;
	//moves only the bikes that are on the "movingbikelayer"
	movingBikeLayer.getSource().getFeatures().forEach( function(feature, index){
		if(feature.getId() == bikeId) {
			//actual coordinates
			var longLat = getCoordinates(feature);
			longLat[0] = parseFloat(""+longLat[0]);
			longLat[1] = parseFloat(""+longLat[1]);
			
			//new coordinates
			//var newCoord = [];
			var GPS = getGPSValue(bike);
			var newCoord = [];
			newCoord[0] = GPS.value.long;
			newCoord[1] = GPS.value.lat;

			if(DEV_MODE){
				console.log("Old coords:("+longLat[1]+","+longLat[0]+")");
				console.log("New coords:("+newCoord[1]+","+newCoord[0]+")");
			}
			
			setCoordinates(feature, newCoord[1], newCoord[0]);
			
			if(isInLayer(bikeId, activeBikeLayer)) {
				clearLayer(activeBikeLayer);
				addBike(bikeId, newCoord[1], newCoord[0], activeBikeLayer);
				//toggleLayer(activeBikeLayer);
			}
			
			return true;
		} 

	});
	return false;

}

/*
 * Click handler for map, if a marker is clicked it retrieves the bike name and update its section
 */
function clickOnMarker(evt) {
	//var coordinates = ol.proj.transform(evt.coordinate, 'EPSG:3857', 'EPSG:4326');
	var feature = map.forEachFeatureAtPixel(evt.pixel, function(feature, layer) {
		/* TODO
		 * clicking on a feature you retrieve its sensors status
		 */
		var id = feature.getId();
		var bike = getBike(id);
		if(bike == null){
			$('.bike-name').text('No bike selected');
			return [feature, layer];
		}var bikeSection = $('#active-bike-data-section');
		updateBikeSection(bike, bikeSection);
		return [feature, layer];								  
	});				 
}

/*
 * Initialization function to get bikes data
 */
function bikesInit() {
	// Get all bikes data and dynamically create each section (from request-handler.js)
	getAllBikes(1);
}