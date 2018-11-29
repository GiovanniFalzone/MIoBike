/* BIKE
 * This is an utility file to work on bikes json, as retrieved from om2m platform
 *
 * Bike json format: {"BikeName":"bikename","Resources":{"resourcename":{"ResourceData":{"format":"m/s","value": readvalue},"ResourceName":"resourcename","BikeName":"bikename","Date":"YYYY.MM.DD_hh:mm:ss"},
 */

// resources: Speed, Temperature, TyrePressure, User, AirQuality, Humidity, Odometer, NFC, Sampling_Period, GPS, Lock

var BIKESARRAY;		//global json array for bikes

/*
 * Get the json object of an existing bike in BIKESARRAY
 */
function getBike(name) {
	var val = null;
	BIKESARRAY.forEach(function(bike, index) {
		if(bike.BikeName == name)
			val = bike;
	});
	return val;
}

/*
 * Get the json for a resource with all the value retrieved from BIKESARRAY (last value read on a bike)
 */
function getAllResource(resource) {
	var res = '{ "'+resource+'": []}';
	res = JSON.parse(res);
	BIKESARRAY.forEach(function(bike,index) {
		var json = null;
		if(isDefined(bike.Resources[resource]))
			json = getResourceValue(bike.Resources[resource]);
		if(isDefined(json)) {
			var text = "{}";
			var obj = JSON.parse(text);
			obj.bikeName = bike.BikeName;
			obj[resource+'_value'] = json.value;
			res[resource].push(obj);
		}
	});
	return res;
}

/*
 * Get the json of the resource "resourceName", for the bicycle "bike" (can be the json or the bikeID(name))
 */
function getResource(bike, resourceName) {
	var val = null;
	if(isDefined(bike)) {
		if(isJSON(bike)) {
			if(isDefined(bike.Resources[resourceName]))
				val = bike.Resources[resourceName];
		} else {
			//I have to look for the bike in bikeArray
			BIKESARRAY.forEach(function(bike, index) {
				if(bike.BikeName == name){
					if(isDefined(bike.Resources[resourceName]))
						val = bike.Resources[resourceName];
				}
			});
		}
	}
	return val;
}

function getSpeed(bike) {
	return getResource(bike, 'Speed');
}
function getTemperature(bike) {
	return getResource(bike, 'Temperature');
}
function getTyrePressure(bike) {
	return getResource(bike, 'TyrePressure');
}
function getUser(bike) {
	return getResource(bike, 'User');
}
function getAirQuality(bike) {
	return getResource(bike, 'AirQuality');
}
function getHumidity(bike) {
	return getResource(bike, 'Humidity');
}
function getOdometer(bike) {
	return getResource(bike, 'Odometer');
}
function getNFC(bike) {
	return getResource(bike, 'NFC');
}
function getLock(bike) {
	return getResource(bike, 'Lock');
}
function getGPS(bike) {
	return getResource(bike, 'GPS');
}
function getSampling(bike) {
	return getResource(bike, 'Sampling_Period');
}

/*
 * Get resource data field for a resource
 */
function getResourceValue(resource) {
	var val = null;
	if(isDefined(resource)) {
		if(isDefined(resource.ResourceData))
			val = resource.ResourceData;
	}
	return val;
}

function getSpeedValue(bike) {
	return getResourceValue(getSpeed(bike));
}
function getTemperatureValue(bike) {
	return getResourceValue(getTemperature(bike));
}
function getUserValue(bike) {
	return getResourceValue(getUser(bike));
}
function getTyrePressureValue(bike) {
	return getResourceValue(getTyrePressure(bike));
}
function getAirQualityValue(bike) {
	return getResourceValue(getAirQuality(bike));
}
function getHumidityValue(bike) {
	return getResourceValue(getHumidity(bike));
}
function getOdometerValue(bike) {
	return getResourceValue(getOdometer(bike));
}
function getNFCValue(bike) {
	return getResourceValue(getNFC(bike));
}
function getSamplingPeriodValue(bike) {
	return getResourceValue(getSampling(bike));
}
function getLockValue(bike) {
	return getResourceValue(getLock(bike));
}
function getGPSValue(bike) {
	return getResourceValue(getGPS(bike));
}