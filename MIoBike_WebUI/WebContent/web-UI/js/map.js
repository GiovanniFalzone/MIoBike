//import '../ol/ol.css';
//import Map from '/ol/Map.js';
//import View from 'ol/View';
//import TileLayer from 'ol/layer/Tile';
//import XYZSource from 'ol/source/XYZ';


/*
bottom-left	43.7077, 10.391
top-left	43.7245, 10.3902
bottom-right43.7208, 10.4216
top-right	43.7043, 10.415
*/
var LOCKED = 1;
var UNLOCKED = 0;

var min_lat = 43.7043;
var max_lat = 43.7245;
var min_lon = 10.3902;
var max_lon = 10.4216;

//Layers
var movingBikeLayer;
var lockedBikeLayer;
var lockerLayer;
var feat;
//vector of bikes
var lockedBikeVector;
var movingBikeVector;

var icons_dir = '/MIoBike_WebUI/web-UI/img/icons';
var icon_scale = 0.3;

/* Function to add a marker with given longitude and latitude, to a vector source and with a precise icon
*	0 for green: "active";
*	1 for red: "locked";
*	2 for yellow: "locker";
*/
function addMarker(lat, lon, source, status) {
	var icon;
	switch(status){
		case 0:
			icon = "green_marker.png";
			break;
		case 1:
			icon = "red_marker.png";
			break;
		case 2:
			icon = "yellow_marker.png";
			break;
		default: 
			icon = "red_marker.png";
	}

	// create a figure for a marker
	var marker = new ol.Feature({
		geometry: new ol.geom.Point(
			ol.proj.fromLonLat([lon, lat])
		),  // Cordinates of pisa
	});

	marker.setStyle(new ol.style.Style({
		image: new ol.style.Icon(({
			crossOrigin: 'anonymous',
			src: icons_dir+'/'+icon,
			scale: 0.8
		}))
	}));

	source.addFeature(marker);
	return marker;
}

/* Function that adds n random positioned markers to the vector source "source", of the type "type" */
function addRandomMarkers(source, n, type) {
	if(n == undefined)
		n = 10;
	var old_t = type;

	for(var i = 0; i < n; i++) {
		type = old_t;
		var lat = Math.random()*(max_lat - min_lat) + min_lat;
		var lon = Math.random()*(max_lon - min_lon) + min_lon;
		if(type == undefined) {
			old_t = undefined;
			type = Math.round(Math.random()*2);
		}
		addMarker(lat, lon, source, type);
	}
}

function toggleLayer(layer) {
	//addRandomMarkers(lockedBikeVector, 10, 1);
	if(layer.getVisible())
		layer.setVisible(false);
	else
		layer.setVisible(true);
}

function moveMarker(marker, dir, offset) {
	//actual position
	var pos = marker.getGeometry().getCoordinates();
	var longLat = ol.proj.transform(pos, 'EPSG:3857', 'EPSG:4326');
	
	//calculate new position
	var new_pos = ol.proj.fromLonLat([10.3025, 43.7140]);

	marker.getGeometry().setCoordinates(new_pos);
}

function getBikeGPS() {

	var lat = 43.7164;
	var lon = 10.4020;
	
	addMarker(lat, lon, lockedBikeVector, LOCKED); 
	/*$.get("../RequestHandlerServlet", function(resp) {
		
		console.log(resp);
	});*/
}

function mapInit(){
	var pisa_lat = 43.7151;
	var pisa_lon = 10.4025;

	var map = new ol.Map({
		target: 'map',
		layers: [
			new ol.layer.Tile({
				source: new ol.source.OSM()
			})
		],
		controls: ol.control.defaults({
          attributionOptions: {
            collapsible: false
          }
        }),
		view: new ol.View({
			center: ol.proj.fromLonLat([pisa_lon, pisa_lat]), //coordinates of Pisa
			zoom: 14
		})
	});

	// create a vector to add the marker feature
	lockedBikeVector = new ol.source.Vector({});
	//var lockerVector = new ol.source.Vector({});
	movingBikeVector = new ol.source.Vector({});
	
	getBikeGPS();
	//var coord = getBikeGPS();
	//var GPS_lat = coord.lat;
	//var GPS_lon = coord.long;
	
	//feat = addmarker(GPS_lat,GPS_lon, lockedBikeVector, 1);
	//addRandomMarkers(lockedBikeVector, 10, 1);
	//addRandomMarkers(lockerVector, 4, 2);
	feat = addMarker(43.7151, 10.4025, movingBikeVector, UNLOCKED);

	/*addMarker(43.7151, 10.4025, vectorSource, 0);
	addMarker(min_lat, min_lon, vectorSource, 1);
	addMarker(max_lat, max_lon, vectorSource, 2);	
	addMarker(43.688, 10.3984, vectorSource, 2);*/	

	//vectorSource.addFeature(marker);

	// layers da aggiungere alla mappa
	lockedBikeLayer = new ol.layer.Vector({
		visible: true,
		source: lockedBikeVector,
	});

	/*lockerLayer = new ol.layer.Vector({
		visible: false,
		source: lockerVector,
	});*/

	movingBikeLayer = new ol.layer.Vector({
		visible: false,
		source: movingBikeVector,
	});
	
	map.addLayer(lockedBikeLayer);
	//map.addLayer(lockerLayer);
	map.addLayer(movingBikeLayer);

};


/*
function addRandomFeature() {
var x = Math.random() * 360 - 180;
var y = Math.random() * 180 - 90;


var duration = 3000;
function flash(feature) {
var start = new Date().getTime();
var listenerKey;

function animate(event) {
var vectorContext = event.vectorContext;
var frameState = event.frameState;
var flashGeom = feature.getGeometry().clone();
var elapsed = frameState.time - start;
var elapsedRatio = elapsed / duration;
// radius will be 5 at start and 30 at end.
var radius = ol.easing.easeOut(elapsedRatio) * 25 + 5;
var opacity = ol.easing.easeOut(1 - elapsedRatio);

var style = new ol.style.Style({
image: new ol.style.Circle({
radius: radius,
snapToPixel: false,
stroke: new ol.style.Stroke({
color: 'rgba(255, 0, 0, ' + opacity + ')',
width: 0.25 + opacity
})
})
});

vectorContext.setStyle(style);
vectorContext.drawGeometry(flashGeom);
if (elapsed > duration) {
ol.Observable.unByKey(listenerKey);
return;
}
// tell OpenLayers to continue postcompose animation
map.render();
}
listenerKey = map.on('postcompose', animate);
}

source.on('addfeature', function(e) {
flash(e.feature);
});

window.setInterval(addRandomFeature, 1000);*/