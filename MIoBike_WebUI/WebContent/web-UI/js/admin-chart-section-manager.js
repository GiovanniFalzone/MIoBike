/* ADMIN-CHART-SECTION-MANAGER
 * This file is used to manage admin dashboard section dedicated to charts. 
 */

/* 
 * Update an existing chart with a new dataset, used for the chart of bikes' sensors
 */
function updateChart(chartName, uom, labels, data) {
	var newDataset = {
		label: chartName+uom,
		borderColor: GREEN,
		data: data
	}

	sensorChart.data.datasets.push(newDataset);
	sensorChart.update();
}

function averageSensorsValues() {
	var temp = 0;
	var hum = 0;
	var aq = 0;
	var t_count = 0;
	var h_count = 0;
	var aq_count = 0;
	
	BIKESARRAY.forEach(function(bike, index) {
		var bike_temp = getResourceValue(getTemperature(bike));
		var bike_hum = getResourceValue(getHumidity(bike));
		var bike_aq = getResourceValue(getAirQuality(bike));

		if(isDefined(bike_temp)) {
			temp += parseFloat(bike_temp.value);
			t_count = t_count+1;
		}

		if(isDefined(bike_hum)) {
			hum += parseFloat(bike_hum.value);
			h_count = h_count+1;
		}

		if(isDefined(bike_aq)) {
			aq += parseFloat(bike_aq.value);
			aq_count = aq_count+1;
		}
	});

	temp = (temp/t_count).toFixed(2);
	hum = (hum/h_count).toFixed(2);
	aq = (aq/aq_count).toFixed(2);

	var resp = '{"temperature" : "'+temp+'", "humidity" : "'+hum+'", "airQuality" : "'+aq+'"}';
	return resp;

}

/*
 * Create a chart in the canvas "divName" with a given name, unit of measure, data, labels, type and colors
 */
function createChartForSingleBike(divName, chartName, uom, data, labels, type, bg_color, border_color) {

	var context = $("#"+divName).get(0).getContext("2d");
	
	var chartData = {
	labels: labels,
	datasets: [
		{
			label: chartName+uom,
			backgroundColor: bg_color,
			borderColor: border_color,
			data: data
		}
	]
	};
	if(isDefined(sensorChart))
		sensorChart.destroy();
	sensorChart = new Chart(context , {
        type: type,
        data: chartData, 
        options:  {
	        scales: {
	            yAxes: [{
	                ticks: {
	                    beginAtZero:true
	                }
	            }]
	        },
	        pan: {
	            enabled: true,
	            mode: 'xy'
	        },
	        zoom: {
	            enabled: true,
	            mode: 'xy',
	            rangeMin: {
	    			// Format of min pan range depends on scale type
	    			x: null,
	    			y: null
	    		},
	    		rangeMax: {
	    			// Format of max pan range depends on scale type
	    			x: null,
	    			y: null
	    		},
	    		// Function called once panning is completed
	    		// Useful for dynamic data loading
	    		onPan: function() { console.log('I was panned!!!'); }
	        }
	    }
    });
}

/*
 * Create the chart for a specific sensor
 */
function createSensorChart(divName, chartName, uom, sensor, type, bg_color, border_color, maxValue, data, labels) {
	var context = $("#"+divName).get(0).getContext("2d");

	if(sensor == "FrontTyre" || sensor == "RearTyre") {
		var messDiv = $('.tyrepressure-'+sensor.toLowerCase()+'-messages');
		messDiv.find('.warning').empty();
		messDiv.find('.ok').empty();
		sensorName = "TyrePressure";	
		var json = getAllResource(sensorName);

		if(!isJSON(json))
			json = JSON.parse(json);

		data = [];
		labels = [];
		json[sensorName].forEach( function(sensorJSON) {
			var d;
			if(sensor == "FrontTyre")
				d = parseFloat(sensorJSON["TyrePressure_value"].front);
			else if(sensor == "RearTyre")
				d = parseFloat(sensorJSON["TyrePressure_value"].rear);
			else
				d = parseFloat(sensorJSON[sensorName+"_value"]);
			var b = sensorJSON.bikeName;
			labels.push(b);
			data.push(d);
			if(sensorName == "TyrePressure") {
				var messDiv = $('.tyrepressure-'+sensor.toLowerCase()+'-messages');
				var warningDiv = messDiv.find('.warning');
				var okDiv = messDiv.find('.ok');
				if(parseFloat(d) < 1.5)
					warningDiv.append("<div>"+b+" : Low Pressure</div>");
				else if(parseFloat(d) > 3) 
					warningDiv.append("<div>"+b+" : High Pressure</div>");
				else
					okDiv.append("<div>"+b+"</div>");
			}
		});
		if(DEV_MODE)
			console.log(json);
	}


	var chartData = {
	labels: labels,
	datasets: [
		{
			label: chartName+" ("+uom+")",
			backgroundColor: bg_color,
			borderColor: border_color,
			data: data
		}
	]
	};
	
	
	if(maxValue > 0) {
		var testChart = new Chart(context , {
	        type: type,
	        data: chartData, 
	        options: {
	        	scales: {
	        		yAxes: [{
	        			display: true,
	        			ticks: {
	        				beginAtZero: true,
	        				max: maxValue
	        			}
	        		}]
	        	}
	        }
	    });
	} else {
		var testChart = new Chart(context , {
	        type: type,
	        data: chartData
	    });
	}

}

/*
 * Function called by handler to create the chart for the resource "resName" with a json that gives the value read from database
 */
function createChart(resName, json) {
	var avg = 0;
	var data = [];
	var labels = [];
	// fill data and labels array with all the values retrieved for that specific resource
	json[resName].forEach( function(array, index){
		data.push(array.value);
		labels.push(array.BikeName);
		var num = parseFloat(""+array.value);
		avg += num;
	});
	if(!isDefined(data))
		return;
	
	avg = avg/json[resName].length;
	$('.admin-sensor-status').find('#actual-'+resName.toLowerCase()).text(avg.toFixed(2));
	var uom;
	var type;
	var colors = [];
	var max;
	// each resource has its own unit of measure and some predefined chart parameter
	switch(resName) {
	case "AirQuality":
		uom = UOM_AQ;
		type = "bar";
		colors = [GREEN, BLUE];
		max = 25;
		break;
	case "Temperature":
		uom = UOM_TEMP;
		type = "bar";
		colors = [RED, BLUE];
		max = 40;
		break;
	case "Odometer":
		uom = UOM_ODO;
		type = "pie";
		colors = [NAVY, ORANGE];
		max = 0;
		break;
	case "Humidity":
		uom = UOM_HUM;
		type = "bar";
		colors = [YELLOW, NAVY];
		max = 100;
		break;
	default:
		uom = UOM_TEMP;
		type = "bar";
	}
	createSensorChart(""+resName.toLowerCase()+"-chart", resName, uom, resName, type, colors, "", max, data, labels);
}

/*
 * Initializes chart: it calls getChartData that is the request function that retrieve data from DB 
 */
function chartInit() {
	BIKESARRAY.forEach(function(bike, index){
		$("#bike-name").append(new Option(bike.BikeName, bike.BikeName));
	});
	getChartData();
}