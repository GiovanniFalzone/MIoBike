/*
 * Receives locked bikes periodically from PeriodicLockedBikesServlet (every 10 seconds)
 * 	data is the array of locked bikes that must be shown to the user on the map
 */
if(CONNECTED)
	var source = new EventSource("../PeriodicLockedBikesServlet");

source.onmessage = function (event) {
	if(!$('#map-section').is(':visible'))
		console.log("nothing to do");
	else {
		//retrieve locked bikes
		var data;
		if(event.data && typeof event.data === "object")
			data = event.data;
		else
			data = JSON.parse(event.data);
		
		if(DEV_MODE)
			console.log("Retrieved data");
		//check if there is a bike used by user
		var usedBike = $('#used-bike').text().replace(" ", "");
		
		var using = false;
		if(usedBike != "/" && usedBike != " / " && usedBike != " /" && usedBike != "/ ") 
			using = true;
		
		//if user is using a bike it must be added (or remain) in lockedBikeLayer
		if(using) {
			JSONUsedBike = getBike(usedBike);
			var coord = getGPSValue(JSONUsedBike);
			if(isDefined(coord) && !isInLayer(usedBike, lockedBikeLayer))
				addBike(usedBike, coord.lat, coord.long, lockedBikeLayer);
		}
		//update BIKESARRAY
		BIKESARRAY = data;

		//add all bikes to locked or disabled bike layers
		BIKESARRAY.forEach(function(bike, index) {
			var name = bike.BikeName;
			var coord;
			if(isDefined(getGPSValue(bike)))
				coord = getGPSValue(bike).value;
			else{
				if(DEV_MODE)
					console.log("no GPS value");
				coord = null;
			}
			
			//if user is using a bike the other bikes are disabled (must be added if not already there)
			if(isDefined(coord)) {
				if(using) {
					if(!isInLayer(name, lockedBikeLayer) && !isInLayer(bike.BikeName, disabledBikeLayer))
						addBike(name, coord.lat, coord.long, disabledBikeLayer);
					else if(!isInLayer(name, disabledBikeLayer))
						changeLayer(name, lockedBikeLayer, disabledBikeLayer);
				} else {
					if(!isInLayer(name, lockedBikeLayer))
						addBike(name, coord.lat, coord.long, lockedBikeLayer);
				}
			}
		});
	};
};

/*
 * Call to getUserInfo on load of the document, in order to retrieve user info from DB
 */
$(document).ready(function() {
	getUserInfo($("#user_ID").text(), $("#username").text(), $("#weight").text());
});

/*
 * Datetime parsing
 */
function datetimeToString(date, notime) {
	if(notime)
		return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
	else
		return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate()+" "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds()+".0";
}

function stringToDatetime(date) {
	return new Date(date.split(' ')[0])
}

function diffDays(date1, date2) {
	var millis = date2-date1;
	var secs = millis/1000;
	var min = secs/60;
	var hours = min/60;
	var days = hours/24;
	return Math.floor(days);
	
}

/*
 * Initialization of locked bikes, to show them on user map
 */
function lockedBikesInit() {
	getAllBikes(0);
}
 
/*
 * Click handler for map, if a marker is clicked it retrieves the bike name and update its section
 */
function clickOnMarker(evt) {
	//var coordinates = ol.proj.transform(evt.coordinate, 'EPSG:3857', 'EPSG:4326');
	updateBikeSection(null, $('#active-bike-data-section'));
	var feature = map.forEachFeatureAtPixel(evt.pixel, function(feature, layer) {
		var id = feature.getId();
		var bike = getBike(id);
		var bikeSection = $('#active-bike-data-section');
		if(bike == null){
			updateBikeSection(null, bikeSection);
			return [feature, layer];
		}
		
		updateBikeSection(bike, bikeSection);
		//var coord = getCoordinates(feature);
		return [feature, layer];								  
	});				 
}

/*
 * Update the bikesection, where the info for a chosen bike are shown
 */
function updateBikeSection(bike, bikeDataDiv) {
	var name;

	if(isDefined(bike))
		name = bike.BikeName;
	else {
		var msg = "No bike selected";
		bikeDataDiv.find('.bike-name').find('#bike-id').find('i').text(msg);
		bikeDataDiv.find('.bike-status').find('#bike-status').find('i').text(msg);
		bikeDataDiv.find('.bike-coordinates').find('#bike-coord').find('i').text(msg);
		return;
	}

	if(isDefined(name))
		bikeDataDiv.find('.bike-name').find('#bike-id').find('i').text(name);

	var gps = getGPSValue(bike);
	if(isDefined(gps)) {
		var lat = parseFloat(gps.value.lat).toFixed(4);
		var long = parseFloat(gps.value.long).toFixed(4);
		bikeDataDiv.find('.bike-coordinates').find('#bike-coord').find('i').text('('+lat+', '+long+')')
	}
}