/* MAP
 * This file initializes and manages the map that are drawn in admin and user dashboards with openlayer v4.
 * 		https://openlayers.org
 */

/*
 * Check if the marker of bike "bikeId" is in the layer "layer"
 */
function isInLayer(bikeId, layer) {
	var present = false;
	layer.getSource().getFeatures().forEach( function(feature, index){
		if(feature.getId() == bikeId)
			present = true;
	});
	return present;
}

/*
 * Conversion function for coordinates
 */
function toPseudoMarcator(coord) {
	return ol.proj.transform(coord, 'EPSG:4326', 'EPSG:3857')
}

function toLatLong(coord) {
	coord[0] = parseFloat(""+coord[0]);
	coord[1] = parseFloat(""+coord[1]);
	return ol.proj.transform(coord, 'EPSG:3857', 'EPSG:4326');
}

/*
 * Get coordinates of a marker
 */
function getCoordinates(marker) {
	var pos = marker.getGeometry().getCoordinates();
	var longLat = toLatLong(pos);
	longLat[0] = parseFloat(""+longLat[0]).toFixed(6);
	longLat[1] = parseFloat(""+longLat[1]).toFixed(6);
	return longLat;
}

/*
 * Set new coordinates for a marker
 */
function setCoordinates(marker, new_lat, new_lon) {
	new_lat = parseFloat(""+new_lat);
	new_lon = parseFloat(""+new_lon);
	var new_pos = ol.proj.fromLonLat([new_lon, new_lat]);
	marker.getGeometry().setCoordinates(new_pos);
}

/*
 * Get the marker object of the bike "bikeId" from the layer "layer"
 */
function getMarker(bikeId, layer) {
	var marker = null;
	layer.getSource().getFeatures().forEach( function(feature, index){
		if(feature.getId() == bikeId)
			marker = feature;
	});
	return marker;
}

/*
 * Remove the marker of the bike "bikeId" from the layer "layer"
 */
function removeBike(bikeId, layer) {
	layer.getSource().removeFeature(getMarker(bikeId, layer));
}

/*
 * Add the marker for the bike "bikeId" in the given position of the layer "layer"
 */
function addBike(bikeId, lat, lon, layer) {
	lat = parseFloat(""+lat);
	lon = parseFloat(""+lon);
	
	var icon;
	switch(layer){
		case movingBikeLayer:
			icon = "green_marker.png";
			break;
		case lockedBikeLayer:
			icon = "red_marker.png";
			break;
		case activeBikeLayer:
			icon = "yellow_marker.png";
			break;
		case disabledBikeLayer:
			icon = "gray_marker.png";
			break;
		default: 
			icon = "red_marker.png";
	}

	// create a figure for a marker
	var marker = new ol.Feature({
		geometry: new ol.geom.Point(
			ol.proj.fromLonLat([lon, lat])
		),  
	});

	marker.setStyle(new ol.style.Style({
		image: new ol.style.Icon(({
			crossOrigin: 'anonymous',
			src: icons_dir+'/'+icon,
			scale: 0.8
		}))
	}));

	//each marker has a bikename as ID
	marker.setId(bikeId);
	layer.getSource().addFeature(marker);
	return marker;
}

/*
 * Set the bike "bikeId" as active and show it
 */
function showActiveBike(bikeId) {
	var bike = getBike(bikeId);
	var coord = getGPSValue(bike).value;
	clearLayer(activeBikeLayer);
	addBike(bikeId, coord.lat, coord.long, activeBikeLayer);
	toggleLayer(activeBikeLayer);
	//showLayer(activeBikeLayer);
}

/*
 * Functions to show and hide the layers
 */
function toggleLayer(layer) {
	if(layer.getVisible())
		layer.setVisible(false);
	else
		layer.setVisible(true);
}

function showLayer(layer) {
	layer.setVisible(true);
}

function hideLayer(layer) {
	layer.setVisible(false);
}

/*
 * Get the marker object of a bike from a layer and move to another
 */
function changeLayer(bike, from, to) {
	var marker = getMarker(bike, from);
	var coord = getCoordinates(marker);
	removeBike(bike, from);
	addBike(bike, coord[1], coord[0], to);
}

/*
 * Remove all the markers from a layer
 */
function clearLayer(layer) {
	var features = layer.getSource().getFeatures();
    features.forEach((feature) => {
        layer.getSource().removeFeature(feature);
    });
}

/*
 * Initialize map object as seen in openlayer
 */
function mapInit(){
	if(!init)
		init = true;
	else 
		return;
	
	var pisa_lat = 43.7151;
	var pisa_lon = 10.4025;

	// Map object, centered on Pisa
	map = new ol.Map({
		target: 'map',
		layers: [
			new ol.layer.Tile({
				source: new ol.source.OSM()
			})
		],
		view: new ol.View({
			center: ol.proj.fromLonLat([pisa_lon, pisa_lat]), //coordinates of Pisa
			zoom: 15
		})
	});

	// Four vectors that will contains the feature (bikes markers)
	var lockedBikeVector = new ol.source.Vector({});
	var movingBikeVector = new ol.source.Vector({});
	var activeBikeVector = new ol.source.Vector({});
	var disabledBikeVector = new ol.source.Vector({});

	// Four layers, linked to the vectors, to show the markers
	lockedBikeLayer = new ol.layer.Vector({
		visible: false,
		source: lockedBikeVector,
	});
	movingBikeLayer = new ol.layer.Vector({
		visible: false,
		source: movingBikeVector,
	});
	activeBikeLayer = new ol.layer.Vector({
		visible: false,
		source: activeBikeVector,
	});
	disabledBikeLayer = new ol.layer.Vector({
		visible: false,
		source: disabledBikeVector,
	});

	map.addLayer(lockedBikeLayer);
	map.addLayer(movingBikeLayer);
	map.addLayer(activeBikeLayer);
	map.addLayer(disabledBikeLayer);

	// add a click handler to the map
	map.on('click', clickOnMarker);
};