package it.unipi.iot.MIoBike.MIoBike_MN_ADN;

import it.unipi.iot.MIoBike.MIoBike_Utils.Constants;

public class MN_Constants extends Constants{
	public static final boolean DEV_MODE = true;

	public static final int		MN_om2m_port = 5683;
	public static final String	MN_address = "coap://127.0.0.1";	
	public static final String	MN_uri = MN_address+ ":" + MN_om2m_port;

	public static final String	Bike_Name = "Bike1";
	public static final String	MN_Id = "/" + Bike_Name + "-mn-cse";
	public static final String	MN_Name = "/" + Bike_Name + "-mn-name";
	
	public static final int 	MN_NM_port = 5686;
	public static final String	MN_NM_NAME = Bike_Name + "_NM";
	public static final String	MN_NM_url = MN_address + ":" + MN_NM_port + "/" + MN_NM_NAME;
	
	public static final String	GPS_uri = "coap://[aaaa::c30c:0:0:2]:5683/GPS";
	public static final String	Lock_uri = "coap://[aaaa::c30c:0:0:3]:5683/Lock";
	public static final String	Temperature_uri = "coap://[aaaa::c30c:0:0:4]:5683/Temperature";
	public static final String	Humidity_uri = "coap://[aaaa::c30c:0:0:5]:5683/Humidity";
	public static final String	Tire_pressure = "coap://[aaaa::c30c:0:0:6]:5683/Tire_pressure";
	public static final String	Odometer = "coap://[aaaa::c30c:0:0:7]:5683/Odometer";
	public static final String	Speedometer = "coap://[aaaa::c30c:0:0:8]:5683/Speedometer";
}
