package it.unipi.iot.MIoBike.MIoBike_common;

public class Constants extends Constants_om2m{

	public static final String [] TESTERS = {"Johnny", "Robert", "Alexander", "Alice", "Bob"};

	public static final boolean DEV_MODE_0	= false;
	public static final boolean DEV_MODE_1	= false;
	public static final boolean DEV_MODE_IN	= false;
	public static final boolean DEV_MODE_MN = false;
	public static final boolean DEV_MODE_BM = false;
	public static final boolean DEV_MODE 	= true;
	
	public static final boolean SENSORTAG = 		false;
	public static final boolean COOJA = 			true;
	
	public static final int VERY_HIGH_FREQ_PERIOD =	1000;
	public static final int HIGH_FREQ_PERIOD =		10000;
	public static final int LOW_FREQ_PERIOD =		60000;
	public static final int DISABLED_FREQ_PERIOD =	-1;

	public static final int DEFAULT_POLLING_PERIOD = 10000;

	public static final int PHY_SIMULATORS_PERIOD = 1000;

	public static final int	NFC_TASK_PORT =		5693;
	public static final int	LOCKER_TASK_PORT =	5694;
	
	public static final int	BIKE_SIMULATOR_COOJA_PORT =	5683;
	public static final int	BIKE_JAVA_SIMULATOR_PORT =	5690;

	public static final int BIKE_SIMULATOR_PORT = BIKE_JAVA_SIMULATOR_PORT;

	public static final String WEBSERVICE_URI = "http://192.168.0.3:8080/MIoBike_WebUI/DbManagerRequestServlet";
	
	public static final String LOCAL_URI = "coap://127.0.0.1:";
	
	public static final String SENSORTAG_URI =	"coap://[aaaa::212:4b00:1280:7e80]";
	public static final String SENSORTAG_PORT = "5683";
	public static final String SENSORTAG_TEMPERATURE_PATH = "sensor/hdc/temp";
	public static final String SENSORTAG_HUMIDITY_PATH =	"sensor/hdc/hum";
	
	public static final String SAMPLING_PERIOD =		"Sampling_Period";
	public static final String USER_RESOURCE_NAME =		"User";

	public static final String LOCKER_NAME =			"Locker";
	public static final String NFC_READER_NAME =		"NFC";
	public static final String GPS_NAME =				"GPS";
	public static final String ODOMETER_NAME =			"Odometer";
	public static final String SPEEDOMETER_NAME =		"Speed";
	public static final String AIRQUALITY_NAME =		"AirQuality";
	public static final String LOCK_NAME =				"Lock";
	public static final String TEMPERATURE_NAME =		"Temperature";
	public static final String HUMIDITY_NAME =			"Humidity";
	public static final String TYRE_PRESSURE_NAME =		"TyrePressure";

//	public static final String NFC_URI_COOJA = 				"coap://[aaaa::c30c:0:0:2]"+":"+BIKE_SIMULATOR_COOJA_PORT+"/"+NFC_READER_NAME;
//	public static final String LOCKER_URI_COOJA = 			"coap://[aaaa::c30c:0:0:8]"+":"+BIKE_SIMULATOR_COOJA_PORT+"/"+LOCKER_NAME;

	public static final String LOCK_URI_COOJA = 			"coap://[aaaa::c30c:0:0:2]"+":"+BIKE_SIMULATOR_COOJA_PORT+"/"+LOCK_NAME;
	public static final String GPS_URI_COOJA = 				"coap://[aaaa::c30c:0:0:3]"+":"+BIKE_SIMULATOR_COOJA_PORT+"/"+GPS_NAME;
	public static final String ODOMETER_URI_COOJA = 		"coap://[aaaa::c30c:0:0:3]"+":"+BIKE_SIMULATOR_COOJA_PORT+"/"+ODOMETER_NAME;
	public static final String SPEEDOMETER_URI_COOJA = 		"coap://[aaaa::c30c:0:0:3]"+":"+BIKE_SIMULATOR_COOJA_PORT+"/"+SPEEDOMETER_NAME;
	public static final String AIRQUALITY_URI_COOJA = 		"coap://[aaaa::c30c:0:0:4]"+":"+BIKE_SIMULATOR_COOJA_PORT+"/"+AIRQUALITY_NAME;
	public static final String TEMPERATURE_URI_COOJA =		"coap://[aaaa::c30c:0:0:5]"+":"+BIKE_SIMULATOR_COOJA_PORT+"/"+TEMPERATURE_NAME;
	public static final String HUMIDITY_URI_COOJA = 		"coap://[aaaa::c30c:0:0:5]"+":"+BIKE_SIMULATOR_COOJA_PORT+"/"+HUMIDITY_NAME;
	public static final String TYRE_PRESSURE_URI_COOJA = 	"coap://[aaaa::c30c:0:0:6]"+":"+BIKE_SIMULATOR_COOJA_PORT+"/"+TYRE_PRESSURE_NAME;
	
	public static final String NFC_URI_LOCAL =				LOCAL_URI+BIKE_SIMULATOR_PORT+"/"+NFC_READER_NAME;
	public static final String LOCKER_URI_LOCAL = 			LOCAL_URI+BIKE_SIMULATOR_PORT+"/"+LOCKER_NAME;
	public static final String LOCK_URI_LOCAL = 			LOCAL_URI+BIKE_SIMULATOR_PORT+"/"+LOCK_NAME;
	public static final String GPS_URI_LOCAL = 				LOCAL_URI+BIKE_SIMULATOR_PORT+"/"+GPS_NAME;
	public static final String ODOMETER_URI_LOCAL = 		LOCAL_URI+BIKE_SIMULATOR_PORT+"/"+ODOMETER_NAME;
	public static final String SPEEDOMETER_URI_LOCAL = 		LOCAL_URI+BIKE_SIMULATOR_PORT+"/"+SPEEDOMETER_NAME;
	public static final String AIRQUALITY_URI_LOCAL = 		LOCAL_URI+BIKE_SIMULATOR_PORT+"/"+AIRQUALITY_NAME;
	public static final String TEMPERATURE_URI_LOCAL = 		LOCAL_URI+BIKE_SIMULATOR_PORT+"/"+TEMPERATURE_NAME;
	public static final String HUMIDITY_URI_LOCAL = 		LOCAL_URI+BIKE_SIMULATOR_PORT+"/"+HUMIDITY_NAME;
	public static final String TYRE_PRESSURE_URI_LOCAL = 	LOCAL_URI+BIKE_SIMULATOR_PORT+"/"+TYRE_PRESSURE_NAME;

	public static final String TEMPERATURE_URI_SENSORTAG = SENSORTAG_URI+":"+SENSORTAG_PORT+"/"+SENSORTAG_TEMPERATURE_PATH;
	public static final String HUMIDITY_URI_SENSORTAG = 	SENSORTAG_URI+":"+SENSORTAG_PORT+"/"+SENSORTAG_HUMIDITY_PATH;
	
//----------------------------------------------------------------------------------
	public static final String NFC_URI = 			NFC_URI_LOCAL;
	public static final String LOCKER_URI = 		LOCKER_URI_LOCAL;
	public static final String LOCK_URI =			LOCK_URI_LOCAL;

	public static final String AIRQUALITY_URI = 	AIRQUALITY_URI_COOJA;
	public static final String GPS_URI = 			GPS_URI_COOJA;
	public static final String ODOMETER_URI = 		ODOMETER_URI_COOJA;
	public static final String SPEEDOMETER_URI = 	SPEEDOMETER_URI_COOJA;
	public static final String TEMPERATURE_URI = 	TEMPERATURE_URI_COOJA;
	public static final String HUMIDITY_URI = 		HUMIDITY_URI_COOJA;
	public static final String TYRE_PRESSURE_URI = 	TYRE_PRESSURE_URI_COOJA;
	
	public static final boolean PHY_SIMULATOR_MOTION		= true;
	public static boolean 		PHY_SIMULATOR_LOCKER		= true;
	public static boolean 		PHY_SIMULATOR_NFC			= true;

	public static boolean 		PHY_SIMULATOR_TEMPERATURE	= false;
	public static boolean 		PHY_SIMULATOR_HUMIDITY		= false;
	public static final boolean PHY_SIMULATOR_AIRQUALITY	= false;
	public static final boolean PHY_SIMULATOR_TYRE_PRESSURE	= false;
	//----------------------------------------------------------------------------------

	public static final String START_TRIP_LABEL =		"Start_Trip";
	public static final String END_TRIP_LABEL =			"End_Trip";
	
	public static final String JSON_KEY_BIKENAME = 		"BikeName";
	public static final String JSON_KEY_RESOURCENAME = 	"ResourceName";
	public static final String JSON_KEY_RESOURCEDATA = 	"ResourceData";
	public static final String JSON_KEY_DATE = 			"Date";
	public static final String JSON_KEY_VALUE = 		"value";
	public static final String JSON_KEY_FORMAT = 		"format";
	public static final String JSON_KEY_PERIOD = 		"period";
	public static final String JSON_KEY_NFC_REQUEST = 	"request";
	
	public static final String LABEL_SENSOR =	"sensor";
	public static final String LABEL_ACTUATOR =	"actuator";
	public static final String LABEL_VIRTUAL =	"virtual";
	public static final String LABEL_LOCKED =	"locked";
	public static final String LABEL_UNLOCKED =	"unlocked";

	public static final String LABEL_PREFIX_USERID =		"UserId:";
	public static final String LABEL_PREFIX_BIKENAME =		"BikeName:";
	public static final String LABEL_PREFIX_BIKESTATUS =	"BikeStatus:";
	public static final String LABEL_PREFIX_RESOURCENAME =	"ResourceName:";
	public static final String LABEL_PREFIX_TYPE =			"Type:";
//	{
//		JSON_KEY_DATE : String, //"2018.11.16_12:25:04"	GMT+0
//		JSON_KEY_RESOURCENAME : LOCK_NAME,
//		JSON_KEY_RESOURCEDATA : {
//			JSON_KEY_FORMAT : String, //"boolean",
//			JSON_KEY_VALUE : String //"true"
//				},
//		JSON_KEY_BIKENAME : String //"Bike1"
//	}	
	
}
