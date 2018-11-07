package it.unipi.iot.MIoBike.MIoBike_MN_ADN;

import java.util.HashMap;
import java.util.Map;

import it.unipi.iot.MIoBike.MIoBike_Utils.*;

public class Bike_Manager {
	static Manager_om2m my_om2m_MN_manager;
	static String Bike_Name;
	static String GPS_uri = "coap://[aaaa::c30c:0:0:2]:5683/GPS";
	static String Lock_uri = "coap://[aaaa::c30c:0:0:3]:5683/Lock";
	static String Temperature_uri = "coap://[aaaa::c30c:0:0:4]:5683/Temperature";
	static String Humidity_uri = "coap://[aaaa::c30c:0:0:5]:5683/Humidity";
	static String Tire_pressure = "coap://[aaaa::c30c:0:0:6]:5683/Tire_pressure";
	static String Odometer = "coap://[aaaa::c30c:0:0:7]:5683/Odometer";
	static String Speedometer = "coap://[aaaa::c30c:0:0:8]:5683/Speedometer";

	
	static Map<String, Resource_Manager> Resources = new HashMap<String, Resource_Manager>();
	static String NotificationManager_name = "NotificationManager";
	
	private static void add_resource(String uri, String res_name, int period) {
		Resource_Manager res = new Resource_Manager(uri, my_om2m_MN_manager, period, Bike_Name, res_name, NotificationManager_name);
		Resources.put(res_name, res);
		res.start();
	}
		
	private static void create_resources(){
		MonitorThread NotificationManager = new MonitorThread(NotificationManager_name);
		NotificationManager.start();
		System.out.println("Creating Resources for " + Bike_Name);
		add_resource(GPS_uri, "GPS", 2000);
		add_resource(Lock_uri, "Lock", 2000);
		add_resource(Temperature_uri, "Temperature_uri", 2000);
		add_resource(Humidity_uri, "Humidity_uri", 2000);
		add_resource(Tire_pressure, "Tire_pressure", 2000);
		add_resource(Odometer, "Odometer", 2000);
		add_resource(Speedometer, "Speedometer", 2000);
	}		

	private static void create_bike_MN() {
		System.out.println("Creating MN components for " + Bike_Name);

		int MN_port = 5683;
		String MN_uri = "coap://127.0.0.1:"+ MN_port;
		String MN_Id = Bike_Name + "-mn-cse";
		String MN_Name = Bike_Name + "-mn-name";
		my_om2m_MN_manager = new Manager_om2m(MN_uri, MN_Id, MN_Name);
		
		String Res_Name = Bike_Name;
		String Res_App_id = Bike_Name + "_id";
		String Res_rr = "true";
		my_om2m_MN_manager.create_AE(Res_App_id, Res_Name, Res_rr);
	}

	public static synchronized void handle_Notification(String content) {
		System.out.println("---------------------------------------------------------------");
		System.out.println("received notification");
		System.out.println("---------------------------------------------------------------");
	}	
	
	public static void main( String[] args ) {
		Bike_Name = "Bike1";
		System.out.println("-------------- Inizializing " + Bike_Name + "------------------");
		create_bike_MN();
		create_resources();
		System.out.println("-------------- End of Inizialization --------------------------");
	}
}
