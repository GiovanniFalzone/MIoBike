/* UTILITIES
 * Manage global variables and some utility functions
 */

//GLOBAL VARIABLES

// map objects
var map;
var movingBikeLayer;
var lockedBikeLayer;
var activeBikeLayer;
var disabledBikeLayer;
var feat;

var icons_dir = '/MIoBike_WebUI/web-UI/img/icons';
var icon_scale = 0.3; 
// some names
var LOCKED = 1;
var UNLOCKED = 0;
var ADMIN = 0;
var USER = 1;
var DEV_MODE = false;
var CONNECTED = true;
var init = false;
var servlet_dir = '../'; //with respect to js directory

//units of measurement
var UOM_SPEED = "km/h";
var UOM_HUM = "%";
var UOM_TP = "bar";
var UOM_ODO = "km";
var UOM_TEMP = "C";
var UOM_AQ = "microg/m^3";
var UOM_TEMP_HTML = "&deg;C";
var UOM_AQ_HTML= "&micro;g/m<sup>3</sup>";
var UOM_CAL= "kcal";
var UOM_GPS = "";
//user messages
var MSG_PLANET_FRIEND = 'You are a Planet Friend!';
var MSG_GOOD = 'You are helping our planet, ride more kilometers to become its savior!';
var MSG_NOT_GOOD = 'You can do better!';
var MSG_ZERO = "You've not used our service, start to ride today!";
//calories utilities
var MIN_CALORIES = 4; //<16km/h
var AVG_CALORIES = 6; //16-20km/h
var MED_CALORIES = 8; //20-26km/h
var MAX_CALORIES = 10; //>26km/h
//periods
var LARGE_PERIOD = 60000;
var MEDIUM_PERIOD = 30000;
var SMALL_PERIOD = 1000;
var GPS_PERIOD = 100;

//coordinates bound (just to know)
var min_lat = 43.7000;
var max_lat = 43.7300;
var min_lon = 10.3850;
var max_lon = 10.4200;

//chart object
var sensorChart;
//colors
var BLUE = 'rgba(99, 240, 220,1)';
var BLUE_T= 'rgba(99, 240, 220,0.2)';
var GREEN = 'rgba(99, 255, 132, 1)';
var GREEN_T = 'rgba(99, 255, 132, 0.2)';
var RED = 'rgba(255, 0, 0, 1)';
var RED_T = 'rgba(255,0,0,0.2)';
var ORANGE = 'rgba(255,69,0,1)';
var YELLOW = 'rgba(255,215,0,1)';
var NAVY = "navy";
var GRAY = "rgba(220,220,220,1)";
var GRAY_T = "rgba(220,220,220,0.2)";

function isDefined(obj) {
	return !(obj == null);
}

function isJSON(obj) {
	return (obj && typeof obj === "object");
}

/*
 * Toggle a the section on btn click
 */
function toggleSection(name, btn) {
	if(name == '#map-section')
		$('.bike-info').show();
	else
		$('.bike-info').hide();
	
	$(".navigation").find(".nav-btn").removeClass("active");
	$(btn).addClass("active");
	$("#main-section").find(".dashboard-section").hide();
	$(name).show();
}

//Parse a date in format yyyy.mm.dd_hh:mm:ss
function getDate(datetime) {
	var res = datetime.split("_");
	return res[0];
}

function getTime(datetime) {
	var res = datetime.split("_");
	return res[1];
}

function getDay(d) {
	var res = d.split(".");
	return res[2];
}

function getMonth(d) {
	var res = d.split(".");
	return res[1];
}

function getYear(d) {
	var res = d.split(".");
	return res[0];
}

function getHours(t) {
	var res = t.split(":");
	return res[0];
}

function getMinutes(t) {
	var res = t.split(":");
	return res[1];
}

function getSeconds(t) {
	var res = t.split(":");
	return res[2];
}