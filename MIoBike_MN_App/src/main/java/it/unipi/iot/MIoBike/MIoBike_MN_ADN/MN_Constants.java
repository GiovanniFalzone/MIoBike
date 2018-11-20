package it.unipi.iot.MIoBike.MIoBike_MN_ADN;

import it.unipi.iot.MIoBike.MIoBike_Utils.Constants;

public class MN_Constants extends Constants{	
	public static final int DEFAULT_POLLING_PERIOD = 60000;

//	public static final String	NFC_URI = "coap://[aaaa::c30c:0:0:2]:"+BIKE_SIMULATOR_COOJA_PORT+"/"+NFC_READER_NAME;
//	public static final String	AIRQUALITY_URI = "coap://127.0.0.1:"+BIKE_SIMULATOR_COOJA_PORT+"/"+AIRQUALITY_NAME;
//	public static final String	GPS_URI = "coap://127.0.0.1:"+BIKE_SIMULATOR_COOJA_PORT+"/"+GPS_NAME;
//	public static final String	LOCK_URI = "coap://[aaaa::c30c:0:0:2]:"+BIKE_SIMULATOR_COOJA_PORT+"/"+LOCK_NAME;
//	public static final String	TEMPERATURE_URI = "coap://[aaaa::c30c:0:0:4]:"+BIKE_SIMULATOR_COOJA_PORT+"/"+TEMPERATURE_NAME;
//	public static final String	HUMIDITY_URI = "coap://[aaaa::c30c:0:0:5]:"+BIKE_SIMULATOR_COOJA_PORT+"/"+HUMIDITY_NAME;
//	public static final String	TYRE_PRESSURE_URI = "coap://[aaaa::c30c:0:0:6]:"+BIKE_SIMULATOR_COOJA_PORT+"/"+TYRE_PRESSURE_NAME;
//	public static final String	ODOMETER_URI = "coap://[aaaa::c30c:0:0:7]:"+BIKE_SIMULATOR_COOJA_PORT+"/"+ODOMETER_NAME;
//	public static final String	SPEEDOMETER_URI = "coap://[aaaa::c30c:0:0:8]:"+BIKE_SIMULATOR_COOJA_PORT+"/"+SPEEDOMETER_NAME;
	
	public static final String	NFC_URI = "coap://127.0.0.1:"+BIKE_SIMULATOR_PORT+"/"+NFC_READER_NAME;
	public static final String	AIRQUALITY_URI = "coap://127.0.0.1:"+BIKE_SIMULATOR_PORT+"/"+AIRQUALITY_NAME;
	public static final String	GPS_URI = "coap://127.0.0.1:"+BIKE_SIMULATOR_PORT+"/"+GPS_NAME;
	public static final String	LOCK_URI = "coap://127.0.0.1:"+BIKE_SIMULATOR_PORT+"/"+LOCK_NAME;
	public static final String	TEMPERATURE_URI = "coap://127.0.0.1:"+BIKE_SIMULATOR_PORT+"/"+TEMPERATURE_NAME;
	public static final String	HUMIDITY_URI = "coap://127.0.0.1:"+BIKE_SIMULATOR_PORT+"/"+HUMIDITY_NAME;
	public static final String	TYRE_PRESSURE_URI = "coap://127.0.0.1:"+BIKE_SIMULATOR_PORT+"/"+TYRE_PRESSURE_NAME;
	public static final String	ODOMETER_URI = "coap://127.0.0.1:"+BIKE_SIMULATOR_PORT+"/"+ODOMETER_NAME;
	public static final String	SPEEDOMETER_URI = "coap://127.0.0.1:"+BIKE_SIMULATOR_PORT+"/"+SPEEDOMETER_NAME;
}
